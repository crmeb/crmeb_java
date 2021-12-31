<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form inline size="small" @submit.native.prevent>
            <el-form-item label="关键字">
              <el-input v-model="listPram.keywords" placeholder="请输入id，名称，描述" clearable class="selWidth" size="small">
                <el-button slot="append" icon="el-icon-search" size="small" @click="handlerSearch" />
              </el-input>
            </el-form-item>
            <el-form-item v-if="selectModel">
              <el-button type="primary" :disabled="!selectedConfigData.id" @click="handlerConfimSelect">确定选择</el-button>
            </el-form-item>
          </el-form>
        </div>
        <el-button size="mini" type="primary" @click="handlerEditData({},0)" v-if="!selectModel" v-hasPermi="['admin:system:form:save']">创建表单</el-button>
      </div>
      <el-table
        :data="dataList.list"
        :highlight-current-row="selectModel"
        size="mini"
        class="table"
        @current-change="handleCurrentRowChange"
        :header-cell-style=" {fontWeight:'bold'}"
      >
        <el-table-column label="ID" prop="id" width="80"/>
        <el-table-column label="名称" prop="name" min-width="180"/>
        <el-table-column label="描述" prop="info" min-width="220"/>
        <el-table-column label="更新时间" prop="updateTime" min-width="200" />
        <el-table-column v-if="!selectModel" label="操作" min-width="80" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handlerEditData(scope.row,1)" v-hasPermi="['admin:system:form:info']">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="listPram.page"
        :page-sizes="constants.page.limit"
        :layout="constants.page.layout"
        :total="dataList.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    <el-dialog
      :visible.sync="editDialogConfig.visible"
      fullscreen
      :title="editDialogConfig.isCreate === 0? '创建表单':'编辑表单'"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <edit
        v-if="editDialogConfig.visible"
        :is-create="editDialogConfig.isCreate"
        :edit-data="editDialogConfig.editData"
        @hideDialog="handlerHide"
      />
    </el-dialog>
  </div>
</template>

<script>
import * as systemFormConfigApi from '@/api/systemFormConfig.js'
import edit from './edit'
export default {
  // name: "index"
  components: { edit },
  props: {
    selectModel: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      constants: this.$constants,
      listPram: {
        keywords: null,
        page: 1,
        limit: this.$constants.page.limit[0]
      },
      editDialogConfig: {
        visible: false,
        editData: {},
        isCreate: 0
      },
      dataList: { list: [], total: 0 },
      selectedConfigData: {}
    }
  },
  mounted() {
    this.handlerGetList(this.listPram)
  },
  methods: {
    handlerSearch() {
      this.listPram.page = 1
      this.handlerGetList(this.listPram)
    },
    handlerGetList(pram) {
      systemFormConfigApi.getFormConfigList(pram).then(data => {
        this.dataList = data
      })
    },
    handlerEditData(rowData, isCreate) {
      if (isCreate === 0) {
        this.editDialogConfig.editData = {}
      } else {
        this.editDialogConfig.editData = rowData
      }
      this.editDialogConfig.isCreate = isCreate
      this.editDialogConfig.visible = true
    },
    handlerHide() {
      this.editDialogConfig.editData = {}
      this.editDialogConfig.isCreate = 0
      this.editDialogConfig.visible = false
      this.handlerGetList(this.listPram)
    },
    handleSizeChange(val) {
      this.listPram.limit = val
      this.handlerGetList(this.listPram)
    },
    handleCurrentChange(val) {
      this.listPram.page = val
      this.handlerGetList(this.listPram)
    },
    handleCurrentRowChange(rowData) {
      this.selectedConfigData = rowData
    },
    handlerConfimSelect() {
      this.$emit('selectedRowData', this.selectedConfigData)
    }
  }
}
</script>

<style scoped>

</style>
