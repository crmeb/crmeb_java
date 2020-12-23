<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form size="small" :inline="true"  label-width="100px">
            <el-form-item label="状态：">
              <el-select v-model="tableFrom.status" placeholder="请选择状态" clearable class="selWidth">
                <el-option :label="item.label" :value="item.value" v-for="(item, index) in switchData" :key="index"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="模板名称：">
              <el-input v-model="tableFrom.name" placeholder="请输入模板名称" class="selWidth" size="small" clearable></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="seachList" size="small">查询</el-button>
            </el-form-item>
          </el-form>
          <el-button type="primary" @click="add" size="small">添加模板消息</el-button>
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
          prop="tempKey"
          label="模板编号"
          min-width="150"
        />
        <el-table-column
          prop="tempId"
          label="模板ID"
          min-width="340"
        />
        <el-table-column
          prop="name"
          label="模板名"
          min-width="150"
        />
        <el-table-column
          label="回复内容"
          min-width="200"
        >
          <template slot-scope="scope">
            <span v-for="(item, index) in scope.row.content.split('\n')" :key="index" style="display: block">{{item}}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="状态"
          width="100"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              class="demo"
              active-text="开启"
              inactive-text="关闭"
              :active-value="1"
              :inactive-value="0"
              @click.native="onchangeIsShow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="添加时间"
          min-width="150"
        />
        <el-table-column label="操作" min-width="150" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="edit(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleDelete(scope.row, scope.$index)">删除</el-button>
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
      :title="isCreate===0 ? '添加模板' : '编辑模板'"
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
    import { wechatTemplateListApi, wechatTemplateStatusApi, wechatTemplateSaveApi, wechatTemplateUpdateApi, wechatTemplateDeleteApi} from '@/api/wxApi'
    import zbParser from '@/components/FormGenerator/components/parser/ZBParser'
    export default {
      name: "Templates",
      components: { zbParser },
      data() {
        return {
          labelPosition:'right',
          isCreate: 0,
          editData: {},
          dialogVisible: false,
          switchData: constants.switchStatus,
          tableFrom: {
            page: 1,
            limit: 20,
            status: '',
            name: ''
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
          wechatTemplateListApi(this.tableFrom).then(res => {
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
          wechatTemplateStatusApi(row.id, {status: row.status}).then(() => {
            this.$message.success('修改成功')
            this.getList()
          }).catch(()=>{
            row.status = !row.status
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
