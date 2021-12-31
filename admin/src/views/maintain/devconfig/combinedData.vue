<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form inline>
            <el-form-item label="数据搜索">
              <el-input v-model="listPram.keywords" placeholder="请输入ID，KEY，组合数据名称，简介" class="selWidth" size="small" clearable>
                <el-button slot="append" icon="el-icon-search" size="small" @click="handlerSearch" />
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <el-button size="mini" type="primary" @click="handlerOpenEdit({},0)" v-hasPermi="['admin:system:group:save']">添加数据组</el-button>
      </div>
      <el-table
        :data="dataList.list"
        style="width: 100%;margin-bottom: 20px;"
        size="mini"
        highlight-current-row
        :header-cell-style=" {fontWeight:'bold'}"
      >
        <el-table-column label="数据组名称" prop="name"  min-width="150"/>
        <el-table-column label="简介" prop="info" min-width="150"/>
        <el-table-column label="操作" fixed="right" min-width="180">
          <template slot-scope="scope">
            <el-button size="small" type="text" @click="handleDataList(scope.row)" v-hasPermi="['admin:system:group:data:list']">数据列表</el-button>
            <el-button size="small" type="text" @click="handlerOpenEdit(scope.row, 1)" v-hasPermi="['admin:system:group:info','admin:system:group:update']">编辑</el-button>
            <el-button size="small" type="text" @click="handleDelete(scope.row)"  v-hasPermi="['admin:system:group:delete']">删除</el-button>
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
      :title="editDialogConfig.isCreate === 0?'创建数据组':'编辑数据组'"
      :visible.sync="editDialogConfig.visible"
    >
      <edit
        v-if="editDialogConfig.visible"
        :is-create="editDialogConfig.isCreate"
        :edit-data="editDialogConfig.editData"
        @hideDialog="handlerHideDialog"
      />
    </el-dialog>
    <el-dialog title="组合数据列表" :visible.sync="comDataListConfig.visible" fullscreen>
      <cm-data-list v-if="comDataListConfig.visible" :form-data="comDataListConfig.formData" />
    </el-dialog>
  </div>
</template>

<script>
import edit from '@/views/maintain/devconfig/combinedDataEdit'
import * as systemGroupApi from '@/api/systemGroup'
import cmDataList from './combineDataList'
export default {
  // name: "combinedData"
  components: { edit, cmDataList },
  data() {
    return {
      constants: this.$constants,
      dataList: {
        list: [],
        total: 0
      },
      listPram: {
        keywords: null,
        page: 1,
        pageSize: this.$constants.page.limit[0]
      },
      editDialogConfig: {
        visible: false,
        isCreate: 0, // 0=创建 1=编辑
        editData: {}
      },
      comDataListConfig: {
        visible: false,
        formData: {}
      }
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
    handlerOpenEdit(editData, isCreate) {
      isCreate === 0 ? this.editDialogConfig.editData = {} : this.editDialogConfig.editData = editData
      this.editDialogConfig.isCreate = isCreate
      this.editDialogConfig.visible = true
    },
    handlerGetList(pram) {
      systemGroupApi.groupList(pram).then(data => {
        this.dataList = data
      })
    },
    handleDataList(rowData) {
      if (rowData.formId <= 0) return this.$message.error('请先关联表单')
      this.comDataListConfig.formData = rowData
      this.comDataListConfig.visible = true
    },
    handleDelete(rowData) {
      this.$confirm('确定删除当前数据', '提示').then(() => {
        systemGroupApi.groupDelete(rowData).then(data => {
          this.$message.success('删除数据成功')
          setTimeout(() => {
            this.handlerGetList(this.listPram)
          }, 800)
        })
      })
    },
    handleSizeChange(val) {
      this.listPram.limit = val
      this.handlerGetList(this.listPram)
    },
    handleCurrentChange(val) {
      this.listPram.page = val
      this.handlerGetList(this.listPram)
    },
    handlerHideDialog() {
      setTimeout(() => {
        this.editDialogConfig.visible = false
        this.handlerGetList(this.listPram)
      }, 800)
    }
  }
}
</script>

<style scoped>

</style>
