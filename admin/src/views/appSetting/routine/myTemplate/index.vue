<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form size="small" :inline="true"  label-width="55px" label-position="left">
            <el-form-item label="状态：">
              <el-select v-model="tableFrom.status" placeholder="请选择状态" clearable class="selWidth">
                <el-option :label="item.label" :value="item.value" v-for="(item, index) in switchData" :key="index"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="名称：">
              <el-input v-model="tableFrom.title" placeholder="请输入模板名称" class="selWidth" size="small" clearable></el-input>
            </el-form-item>
            <el-form-item label="ID：">
              <el-input v-model="tableFrom.tempId" placeholder="请输入模板ID" class="selWidth" size="small" clearable></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="seachList" size="small">查询</el-button>
            </el-form-item>
          </el-form>
          <router-link :to=" { path:'/appSetting/publicRoutine/publicRoutineTemplate' } ">
            <el-button type="primary" size="small" class="mr10">添加</el-button>
          </router-link>
          <el-button type="primary" size="small" class="mr10" @click="checkTemp">一键同步我的模板</el-button>
        </div>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        class="table"
        highlight-current-row
      >
        <el-table-column
          label="ID"
          width="80"
          prop="id"
        />
        <el-table-column
        prop="tempId"
        label="模板ID"
        min-width="320"
      />
        <el-table-column
          prop="title"
          label="模板名"
          min-width="150"
        />
        <el-table-column
          label="模板关键字"
          min-width="250">
          <template slot-scope="scope" v-if="scope.row.extra">
            <span v-for="item in JSON.parse(scope.row.extra)" :key="item.kid" class="mr5">{{item.name}}</span>
          </template>
          <template slot-scope="scope" v-else>
            <span class="mr5">-</span>
          </template>
        </el-table-column>
        <el-table-column
          label="状态"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              class="demo"
              active-text="开启"
              inactive-text="关闭"
              :active-value="true"
              :inactive-value="false"
              @click.native="onchangeIsShow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          label="应用场景"
          min-width="150"
        >
          <template slot-scope="scope">
            <el-select v-model="scope.row.type" placeholder="请选择" clearable @change="onchangeType(scope.row)">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="添加时间"
          min-width="150"
        />
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
          <template slot-scope="scope">
            <router-link :to=" { path:'/appSetting/publicRoutine/creatPublicTemplate/' + scope.row.tid + '/0/' + scope.row.id } ">
              <el-button size="small" type="text" class="mr10">编辑</el-button>
            </router-link>
            <!--<el-button type="text" size="small" @click="handleDelete(scope.row, scope.$index)">删除</el-button>-->
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>
    </el-card>

    <!--编辑-->
    <el-dialog
      title="编辑订单"
      :visible.sync="dialogVisible"
      width="500px"
      :before-close="handleClose">
      <zb-parser
        v-if="dialogVisible"
        :form-id="105"
        :is-create="isCreate"
        :edit-data="editData"
        @submit="handlerSubmit"
        @resetForm="resetForm"
      />
    </el-dialog>
  </div>
</template>

<script>
  import * as constants from '@/utils/constants.js'
  import { tempAsyncApi, myTempTypeApi, myTempListApi, wechatTemplateStatusApi, wechatTemplateSaveApi, wechatTemplateUpdateApi, wechatTemplateDeleteApi, myTempStatusApi} from '@/api/wxApi'
  import zbParser from '@/components/FormGenerator/components/parser/ZBParser'
  export default {
    name: "MyTemplates",
    components: { zbParser },
    data() {
      return {
        value: '',
        options: [{
          value: 'paySubscribe',
          label: '支付成功'
        }, {
          value: 'orderSubscribe',
          label: '订单相关'
        }, {
          value: 'extrctSubscribe',
          label: '提现消息'
        }, {
          value: 'orderRefundSubscribe',
          label: '订单退款'
        }, {
          value: 'rechargeSubscribe',
          label: '充值成功'
        }],
        labelPosition:'right',
        isCreate: 0,
        editData: {},
        dialogVisible: false,
        switchData: constants.switchStatus,
        tableFrom: {
          page: 1,
          limit: 20,
          status: null,
          title: null,
          tempId: null
        },
        tableData: {
          data: [],
          total: 0
        },
        listLoading: true,
        tempId: null
      }
    },
    mounted() {
      this.getList()
    },
    methods: {
      resetForm(formValue) {
        this.dialogVisible = false
      },
      checkTemp() {
        this.$modalSure('同步我的模板到小程序').then(() => {
          tempAsyncApi().then(() => {
            this.$message.success('同步成功')
          })
        })
      },
      seachList() {
        this.tableFrom.page = 1
        this.getList()
      },
      // 订单删除
      handleDelete(row, idx) {
        this.$modalSure().then(() => {
          wechatTemplateDeleteApi( row.id ).then(() => {
            this.$message.success('删除成功')
            this.tableData.data.splice(idx, 1)
          })
        })
      },
      handleClose() {
        this.dialogVisible = false
        this.editData = {}
      },
      handlerSubmit(formValue) {
        this.isCreate === 0 ? wechatTemplateSaveApi(formValue).then(data => {
          this.$message.success('新增成功')
          this.dialogVisible = false
          this.editData = {}
          this.getList()
        }) : wechatTemplateUpdateApi(this.tempId, formValue).then(data => {
          this.$message.success('编辑成功')
          this.dialogVisible = false
          this.getList()
        })
      },
      add() {
        this.dialogVisible = true
      },
      edit(row) {
        this.tempId = row.id
        this.dialogVisible = true
        this.isCreate = 1
        this.editData = JSON.parse(JSON.stringify(row))
      },
      // 列表
      getList() {
        this.listLoading = true
        myTempListApi(this.tableFrom).then(res => {
          this.tableData.data = res.list || []
          this.tableData.total = res.total
          this.listLoading = false
        }).catch(() => {
          this.listLoading = false
        })
      },
      pageChange(page) {
        this.tableFrom.page = page
        this.getList()
      },
      handleSizeChange(val) {
        this.tableFrom.limit = val
        this.getList()
      },
      // 修改状态
      onchangeIsShow(row) {
        myTempStatusApi({status: row.status , id: row.id}).then(() => {
          this.$message.success('修改成功')
          this.getList()
        }).catch(()=>{
          row.status = !row.status
        })
      },
      // 修改场景
      onchangeType(row) {
        myTempTypeApi({type: row.type , id: row.id}).then(() => {
          this.$message.success('修改成功')
          this.getList()
        })
      }
    }
  }
</script>

<style scoped lang="scss">
  .selWidth {
    width: 350px;
  }
</style>
