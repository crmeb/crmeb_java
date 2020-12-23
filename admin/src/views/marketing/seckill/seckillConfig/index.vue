<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form inline>
            <el-form-item label="是否显示">
              <el-select v-model="tableFrom.status" placeholder="请选择" class="filter-item selWidth mr20" @change="getList(1)" clearable>
                <el-option label="关闭" :value="0" />
                <el-option label="开启" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="秒杀名称：">
              <el-input v-model="tableFrom.name" placeholder="请输入秒杀名称" class="selWidth" clearable>
                <el-button slot="append" icon="el-icon-search" @click="getList(1)"/>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <el-button size="mini" type="primary" @click="add">添加秒杀配置</el-button>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        ref="multipleTable"
      >
        <el-table-column
          prop="id"
          label="ID"
          min-width="50"
        />
        <el-table-column
          label="秒杀名称"
          min-width="100"
        >
          <template slot-scope="scope">
            <router-link :to="{ path:'/marketing/seckill/list/' + scope.row.id}">
              <el-button type="text" size="small">{{scope.row.name}}</el-button>
            </router-link>
          </template>
        </el-table-column>
        <el-table-column
          prop="name"
          label="秒杀时段"
          min-width="100"
        >
          <template slot-scope="scope">
            {{scope.row.time.split(',').join(' - ')}}
          </template>
        </el-table-column>
        <el-table-column label="轮播图" min-width="200">
          <template slot-scope="scope">
            <div class="acea-row" v-if="scope.row.silderImgs">
              <div class="demo-image__preview mr5" v-for="item in JSON.parse(scope.row.silderImgs)" :key="item.attId">
                <el-image
                  style="width: 36px; height: 36px"
                  :src="item.sattDir"
                  :preview-src-list="[item.sattDir]"
                />
              </div>
            </div>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <!--<el-table-column-->
          <!--label="排序"-->
          <!--min-width="100"-->
        <!--&gt;-->
          <!--<template slot-scope="scope">-->
            <!--<div v-if="!scope.row.isEdit" @click="onEditSort(scope.row)" @blur="onBlur(scope.row, scope.row.sort)" style="width: 100%;cursor: pointer;">{{scope.row.sort}}</div>-->
            <!--<el-input v-model="scope.row.sort" placeholder="请输入排序" v-else @blur="onBlur(scope.row)"></el-input>-->
          <!--</template>-->
        <!--</el-table-column>-->
        <el-table-column
          label="状态"
          min-width="150"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              active-text="开启"
              inactive-text="关闭"
              @change="onchangeIsShow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          min-width="130"
        />
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEdit(scope.row.id)">编辑</el-button>
            <el-button type="text" size="small" @click="handleDelete(scope.row.id, scope.$index)"  class="mr10">删除</el-button>
            <router-link :to="{ path:'/marketing/seckill/creatSeckill/creat/' + scope.row.id}">
              <el-button type="text" size="small">添加商品</el-button>
            </router-link>
          </template>
        </el-table-column>
      </el-table>
      <div class="block mb20">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>
    </el-card>

    <el-dialog
      :title="isCreate===0 ? '添加数据' : '编辑数据'"
      :visible.sync="dialogVisible"
      width="700px"
      :before-close="handleClose"
    >
      <div v-loading="loading">
        <zb-parser
          :form-id="formId"
          :is-create="isCreate"
          :edit-data="editData"
          @submit="handlerSubmit"
          @resetForm="resetForm"
          v-if="dialogVisible"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import zbParser from '@/components/FormGenerator/components/parser/ZBParser'
  import { configSaveForm, configInfo } from '@/api/systemConfig.js'
  import { seckillListApi, seckillUpdateApi, seckillInfoApi, seckillSaveApi, seckillDeleteApi, seckillConfigStatusApi } from '@/api/marketing'
  export default {
    name: "SeckillConfig",
    components: { zbParser },
    data() {
      return {
        dialogVisible: false,
        isShow: true,
        isCreate: 0,
        editData: {},
        formId: 123,
        listLoading: true,
        tableData: {
          data: [],
          total: 0
        },
        tableFrom: {
          page: 1,
          limit: 20,
          name: '',
          isDel: false,
          status: ''
        },
        seckillId: '',
        loading: false
      }
    },
    mounted() {
     this.getList()
    },
    methods: {
      resetForm(formValue) {
        this.dialogVisible = false
      },
      // 删除
      handleDelete(id, idx) {
        this.$modalSure().then(() => {
          seckillDeleteApi({ id: id }).then(() => {
            this.$message.success('删除成功')
            this.tableData.data.splice(idx, 1)
          })
        })
      },
      onchangeIsShow(row) {
        seckillConfigStatusApi(row.id, {status: row.status})
          .then(async () => {
            this.$message.success('修改成功');
            this.getList()
          }).catch(()=>{
          row.status = !row.status
        })
      },
      onEditSort(row) {
        this.$set(row, 'isEdit', true)
      },
      onBlur(row) {
        this.$set(row, 'isEdit', false)
        this.onEdit(row.id, row)
      },
      // 获取表单详情
      getFormInfo(id) {
        this.loading = true
        seckillInfoApi({ id: id }).then(res => {
          this.editData = res
          this.dialogVisible = true
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      },
      // 编辑
      handleEdit(id) {
        this.seckillId = id
        this.getFormInfo(id)
        this.isCreate = 1
      },
      // 编辑
      onEdit(id, obj) {
        const data = obj ? obj : this.editData
        seckillUpdateApi({id}, data).then(res => {
          this.isSuccess()
        }).catch((res) => {
          this.listLoading = false
        })
      },
      // 提交
      handlerSubmit(formValue) {
       if(formValue.time.split(',')[0].split(':')[0] > formValue.time.split(',')[1].split(':')[0]) return this.$message.error('请填写正确的时间范围')
        this.isCreate === 0 ? seckillSaveApi(formValue).then(res => {
          this.isSuccess()
        }) : seckillUpdateApi({id: this.seckillId}, formValue).then(res => {
          this.isSuccess()
        })
      },
      isSuccess(){
        this.$message.success('操作成功')
        this.dialogVisible = false
        this.getList()
      },
      // 列表
      getList(num) {
        this.listLoading = true
        this.tableFrom.page = num ? num : this.tableFrom.page;
        seckillListApi(this.tableFrom).then(res => {
          this.tableData.data = res.list
          this.tableData.total = res.total
          this.tableData.data.map(item => this.$set(item, 'isEdit', false))
          this.listLoading = false
        }).catch((res) => {
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
      add() {
        this.isCreate = 0
        this.dialogVisible = true
      },
      handleClose() {
        this.dialogVisible = false
        this.editData = {}
      }
    }
  }
</script>

<style scoped>

</style>
