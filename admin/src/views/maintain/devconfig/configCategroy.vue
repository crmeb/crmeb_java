<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header">
        <el-form inline>
          <el-form-item>
            <el-button size="mini" type="primary" @click="handlerOpenAdd({id:0,name:'顶层目录'})">添加分类</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table
        ref="treeList"
        :data="treeList"
        style="width: 100%;"
        row-key="id"
        size="mini"
        class="table"
        highlight-current-row
        :tree-props="{children: 'child', hasChildren: 'hasChildren'}"
      >
        <el-table-column prop="name" label="分类昵称" min-width="300">
          <template slot-scope="scope">
            {{ scope.row.name }}
          </template>
        </el-table-column>
        <el-table-column label="英文名称" show-overflow-tooltip min-width="180">
          <template slot-scope="scope">
            <span>{{ scope.row.url }}</span>
          </template>
        </el-table-column>
        <el-table-column label="已关联的表单" show-overflow-tooltip min-width="130">
          <template slot-scope="scope">
            <span>{{ scope.row.extra }}</span>
          </template>
        </el-table-column>
        <!--            <el-table-column label="排序" prop="sort" width="150"></el-table-column>-->
        <el-table-column label="启用状态" min-width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.status | filterYesOrNo }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="250" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              :disabled="scope.row.pid > 0"
              @click="handlerOpenAdd(scope.row)"
            >添加子目录</el-button>
            <el-button type="text" size="small" @click="handleEditMenu(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="handlerOpenFormConfig(scope.row)">配置列表</el-button>
            <el-button type="text" size="small" @click="handleDelMenu(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog
      :title="editDialogConfig.isCreate === 0?'添加分类':'编辑分类'"
      :visible.sync="editDialogConfig.visible"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <edit
        v-if="editDialogConfig.visible"
        :prent="editDialogConfig.prent"
        :is-create="editDialogConfig.isCreate"
        :edit-data="editDialogConfig.data"
        :biztype="editDialogConfig.biztype"
        :all-tree-list="treeList"
        @hideEditDialog="hideEditDialog"
      />
    </el-dialog>
    <el-dialog title="选择已配置的表单" :visible.sync="configFormSelectedDialog.visible">
      <span class="color-red">注意：表单不能重复关联</span>
      <form-config-list
        v-if="configFormSelectedDialog.visible"
        select-model
        @selectedRowData="handlerSelectedRowData"
      />
      <el-form>
        <el-form-item>
          <el-button type="primary" style="width:100%;" @click="handlerAddFormExtra">关联</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import * as categoryApi from '@/api/categoryApi.js'
import edit from '@/views/maintain/devconfig/configCategotyEdit.vue'
import * as selfUtil from '@/utils/ZBKJIutil.js'
import configList from './configList'
import formConfigList from '@/views/maintain/formConfig'
export default {
  // name: "configCategroy"
  components: { edit, configList, formConfigList },
  props: {

  },
  data() {
    return {
      constants: this.$constants,
      searchPram: {
        status: null,
        type: null
      },
      editDialogConfig: {
        visible: false,
        isCreate: 0, // 0=创建，1=编辑
        prent: {}, // 父级对象
        data: {}
      },
      treeList: [],
      listPram: {
        pid: 0,
        type: this.$constants.categoryType[5].value,
        status: null,
        name: null,
        page: this.$constants.page.page,
        limit: this.$constants.page.limit[1]
      },
      configFormSelectedDialog: {
        visible: false,
        currentData: {}
      }
    }
  },
  mounted() {
    this.handlerGetTreeList()
  },
  methods: {
    handlerOpenFormConfig(rowData) {
      this.configFormSelectedDialog.currentData = rowData
      this.configFormSelectedDialog.visible = true
    },
    handlerGetList() {
      categoryApi.listCategroy(this.listPram).then(data => {
        // this.dataList = data
        this.treeList = data.list
      })
    },
    handlerOpenAdd(rowData) {
      this.editDialogConfig.isCreate = 0
      this.editDialogConfig.prent = rowData
      this.editDialogConfig.data = {}
      this.editDialogConfig.biztype = this.biztype
      this.editDialogConfig.visible = true
    },
    handleEditMenu(rowData) {
      this.editDialogConfig.isCreate = 1
      this.editDialogConfig.data = rowData
      this.editDialogConfig.prent = rowData
      this.editDialogConfig.visible = true
    },
    handleDelMenu(rowData) {
      this.$confirm('确定删除当前数据?').then(() => {
        categoryApi.deleteCategroy(rowData).then(data => {
          this.handlerGetTreeList()
          this.$message.success('删除成功')
        })
      })
    },
    hideEditDialog() {
      setTimeout(() => {
        this.editDialogConfig.prent = {}
        this.editDialogConfig.type = 0
        this.editDialogConfig.visible = false
        this.handlerGetTreeList()
      }, 200)
    },
    handlerGetTreeList() {
      // status: this.selectModel?1:-1
      const _pram = { type: this.constants.categoryType[5].value, status: -1 }
      categoryApi.treeCategroy(_pram).then(data => {
        this.treeList = this.handleAddArrt(data)
      })
    },
    handleAddArrt(treeData) {
      // let _result = this.addTreeListLabel(treeData)
      const _result = selfUtil.addTreeListLabel(treeData)
      return _result
    },
    handlerSelectedRowData(rowData) {
      this.configFormSelectedDialog.currentData.extra = rowData.id
    },
    handlerAddFormExtra() {
      categoryApi.updateCategroy(this.configFormSelectedDialog.currentData).then(data => {
        this.$message.success('关联表单成功')
        setTimeout(() => {
          this.configFormSelectedDialog.visible = false
          this.handlerGetTreeList()
        }, 800)
      })
    }
  }
}
</script>

<style scoped>

</style>
