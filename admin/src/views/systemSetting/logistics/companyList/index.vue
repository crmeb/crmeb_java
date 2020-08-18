<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form ref="form" inline :model="form">
            <el-form-item label="状态">
              <el-select v-model="form.isShow" placeholder="状态" clearable @change="handlerSearch" class="selWidth">
                <el-option
                  v-for="item in constants.switchStatus"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="关键字：">
              <el-input v-model="form.keywords" placeholder="请输入关键字" class="selWidth"  size="small">
                <el-button slot="append" size="small" icon="el-icon-search" @click="handlerSearch" />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <el-button type="primary"  size="small" @click="addExpress">添加物流公司</el-button>
      </div>
      <el-table
        v-loading="loading"
        :data="tableData.list"
        style="width: 100%"
      >
        <el-table-column
          prop="id"
          label="ID"
          min-width="180"
        />
        <el-table-column
          label="物流公司名称"
          min-width="150"
          prop="name"
        />
        <el-table-column
          min-width="200"
          label="编码"
          prop="code"
        />
        <el-table-column
          min-width="100"
          label="排序"
          prop="sort"
          sortable
        />
        <el-table-column
          label="状态"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.isShow"
              class="demo"
              :active-value="1" :inactive-value="0"
              active-text="开启"
              inactive-text="关闭"
              @change="bindStatus(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="address"
          fixed="right"
          min-width="120"
          label="操作"
        >
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="bindEdit(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="bindDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="block-pagination">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableData.limit"
          :current-page="tableData.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @current-change="pageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
    <el-dialog
      title="添加物流公司"
      :visible.sync="dialogVisible"
      width="700px"
      :before-close="handleClose"
    >
      <parser v-if="formShow" ref="formBox" class="formBox" :form-conf="formConf" :form-edit-data="formData" :is-edit="isCreate === 1" @submit="submit" />
    </el-dialog>
  </div>
</template>

<script>
import parser from '@/components/FormGenerator/components/parser/Parser'
import * as systemFormConfigApi from '@/api/systemFormConfig.js'
import * as logistics from '@/api/logistics.js'
import * as constants from '@/utils/constants.js'
export default {
  name: 'CompanyList',
  components: { parser },
  data() {
    return {
      constants,
      // 表单
      formConf: { fields: [] },
      form: {
        keywords: '',
        isShow: null
      },
      tableData: {},
      page: 1,
      limit: 20,
      loading: false,
      dialogVisible: false,
      fromType: 'add',
      formData: {},
      isCreate: 0,
      formShow: false,
      editId: 0
    }
  },
  created() {
    this.getExpressList()
  },
  methods: {
    handlerSearch() {
      this.page = 1
      this.getExpressList()
    },
    //  获取物流公司列表
    getExpressList() {
      this.loading = true
      logistics.expressList({
        page: this.page,
        limit: this.limit,
        keywords: this.form.keywords,
        isShow: this.form.isShow
      }).then(res => {
        this.loading = false
        this.tableData = res
      })
    },
    // 物流开关
    bindStatus(item) {
      logistics.expressUpdate({
        code: item.code,
        id: item.id,
        isShow: item.isShow,
        name: item.name,
        sort: item.sort
      }).then(res => {
        this.$message.success('操作成功')
        this.getExpressList()
      }).catch(() => {
        item.isShow = !item.isShow
      })
    },
    // 分页
    pageChange(e) {
      this.page = e
      this.getExpressList()
    },
    handleSizeChange(e) {
      this.limit = e
      this.getExpressList()
    },
    // 添加物流公司
    addExpress() {
      this.fromType = 'add'
      const _pram = { id: 71 }
      systemFormConfigApi.getFormConfigInfo(_pram).then(data => {
        this.dialogVisible = true
        this.formConf = JSON.parse(data.content)
        this.formShow = true
      })
    },
    // 删除物流公司
    bindDelete(item) {
      this.$modalSure().then(() => {
        logistics.expressDelete({ id: item.id }).then(res => {
          this.$message.success('删除成功')
          this.getExpressList()
        })
      })
    },
    // 表单提交
    submit(data) {
      if (this.fromType === 'add') {
        logistics.expressSave(data).then(res => {
          this.handleClose()
          this.getExpressList()
          this.$message.success('操作成功')
        })
      } else {
        data.id = this.editId
        logistics.expressUpdate(data).then(res => {
          this.handleClose()
          this.getExpressList()
          this.$message.success('操作成功')
        })
      }
    },
    //  关闭模态框
    handleClose(done) {
      this.formShow = false
      this.formData = {}
      this.formConf.fields = []
      this.dialogVisible = false
      this.isCreate = 0
    },
    // 编辑
    bindEdit(item) {
      this.editId = item.id
      this.fromType = 'edit'
      const _pram = { id: 71 }
      systemFormConfigApi.getFormConfigInfo(_pram).then(data => {
        this.formShow = false
        this.isCreate = 0
        this.dialogVisible = true
        this.formConf = JSON.parse(data.content)
        this.getInfo(item)
      })
    },
    getInfo(item) {
      logistics.expressInfo({ id: item.id }).then(res => {
        this.formData = res
        this.isCreate = 1
        setTimeout(() => { // 让表单重复渲染待编辑数据
          this.formShow = true
        }, 80)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
  .selWidth{
    width: 350px;
  }
  .el-icon-plus {
    margin-right: 5px;
  }

  .demo .el-switch__label {
    position: absolute;
    display: none;
    color: #fff;
  }

  /*打开时文字位置设置*/
  .demo .el-switch__label--right {
    z-index: 1;
  }

  /*关闭时文字位置设置*/
  .demo .el-switch__label--left {
    z-index: 1;
    left: 19px;
  }

  /*显示文字*/
  .demo .el-switch__label.is-active {
    display: block;
  }

  .demo.el-switch .el-switch__core,
  .el-switch .el-switch__label {
    width: 60px !important;
  }
  .formBox{
    .el-input-number--medium{
      width: 100px;
    }
  }
</style>
