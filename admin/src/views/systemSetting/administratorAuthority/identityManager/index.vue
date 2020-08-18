<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-form inline size="small" @submit.native.prevent>
        <el-form-item>
          <el-select v-model="listPram.status" placeholder="状态" clearable class="selWidth">
            <el-option
              v-for="item in constants.roleListStatus"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="listPram.roleName" placeholder="请输入身份昵称" clearable class="selWidth"/>
        </el-form-item>
        <el-form-item>
          <el-button size="mini" type="primary" @click.native="handleGetRoleList">查询</el-button>
        </el-form-item>
      </el-form>
      <el-form inline @submit.native.prevent>
        <el-form-item>
          <el-button size="mini" type="primary" @click="handlerOpenEdit(0)">添加身份</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="listData.list" size="mini">
        <el-table-column label="身份昵称" prop="roleName" min-width="130"/>
        <el-table-column label="权限" show-overflow-tooltip min-width="350">
          <template slot-scope="scope">
            {{ scope.row.rulesView | filterEmpty }}
          </template>
        </el-table-column>
        <!--<el-table-column label="level" prop="level" />-->
        <el-table-column label="状态" prop="status">
          <template slot-scope="scope">
            <span>{{ scope.row.status | filterShowOrHide }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" min-width="150"/>
        <el-table-column label="更新时间" prop="updateTime" min-width="150"/>
        <el-table-column label="操作" min-width="130" fixed="right">
          <template slot-scope="scope">
            <el-button size="small" type="text" @click="handlerOpenEdit(1,scope.row)">编辑</el-button>
            <el-button size="small" type="text" @click="handlerOpenDel(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="listPram.page"
        :page-sizes="constants.page.limit"
        :layout="constants.page.layout"
        :total="listData.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    <el-dialog
      :visible.sync="editDialogConfig.visible"
      :title="editDialogConfig.isCreate === 0? '创建身份':'编辑身份'"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <edit
        v-if="editDialogConfig.visible"
        :is-create="editDialogConfig.isCreate"
        :edit-data="editDialogConfig.editData"
        @hideEditDialog="hideEditDialog"
      />
    </el-dialog>
  </div>
</template>

<script>
import * as roleApi from '@/api/role.js'
import * as categroyApi from '@/api/categoryApi.js'
import * as constants from '@/utils/constants.js'
import edit from './edit'
export default {
  // name: "index"
  components: { edit },
  data() {
    return {
      constants,
      listData: { list: [] },
      listPram: {
        createTime: null,
        updateTime: null,
        level: null,
        page: 1,
        limit: constants.page.limit[0],
        roleName: null,
        rules: null,
        status: null
      },
      menuList: [],
      editDialogConfig: {
        visible: false,
        isCreate: 0, // 0=创建，1=编辑
        editData: {}
      }
    }
  },
  mounted() {
    this.handleGetRoleList()
  },
  methods: {
    handlerOpenDel(rowData) {
      this.$confirm('确认删除当前数据').then(() => {
        roleApi.delRole(rowData).then(data => {
          this.$message.success('删除数据成功')
          this.handleGetRoleList()
        })
      })
    },
    handleGetRoleList() {
      roleApi.getRoleList(this.listPram).then(data => {
        this.listData = data
        this.handlerGetMenuList()
      })
    },
    handlerOpenEdit(isCreate, editDate) {
      if (isCreate === 1) { editDate.rules = editDate.rules.split(',') }

      this.editDialogConfig.editData = editDate
      this.editDialogConfig.isCreate = isCreate
      this.editDialogConfig.visible = true
    },
    handlerGetMenuList() { // 获取菜单全部数据后做menu翻译使用
      categroyApi.listCategroy({ page: 1, limit: 999, type: 5 }).then(data => {
        this.menuList = data.list
        this.listData.list.forEach(item => {
          const _muneText = []
          const menuids = item.rules.split(',')
          menuids.map(muid => {
            this.menuList.filter(menu => {
              if (menu.id == muid) {
                _muneText.push(menu.name)
              }
            })
          })
          item.rulesView = _muneText.join(',')
          this.$set(item, 'rulesViews', item.rulesView)
        })
      })
    },
    hideEditDialog() {
      this.editDialogConfig.visible = false
      this.handleGetRoleList()
    },
    handleSizeChange(val) {
      this.listPram.limit = val
      this.handleGetRoleList(this.listPram)
    },
    handleCurrentChange(val) {
      this.listPram.page = val
      this.handleGetRoleList(this.listPram)
    }
  }
}
</script>

<style scoped>

</style>
