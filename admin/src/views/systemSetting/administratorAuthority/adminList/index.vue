<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-form inline size="small" @submit.native.prevent>
        <el-form-item>
          <el-select v-model="listPram.roles" placeholder="身份" clearable class="selWidth">
            <el-option
              v-for="item in roleList.list"
              :key="item.id"
              :label="item.roleName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
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
          <el-input v-model="listPram.realName" placeholder="姓名或者账号" clearable class="selWidth"/>
        </el-form-item>
        <el-form-item>
          <el-button size="mini" type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
      <el-form inline @submit.native.prevent>
        <el-form-item>
          <el-button size="mini" type="primary" @click="handlerOpenEdit(0)" v-hasPermi="['admin:system:admin:save']">添加管理员</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="listData.list" size="mini" :header-cell-style=" {fontWeight:'bold'}">
        <el-table-column
          prop="id"
          label="ID"
          width="50"
        />
        <el-table-column label="姓名" prop="realName" min-width="120"/>
        <el-table-column label="账号" prop="account" min-width="120"/>
        <el-table-column label="手机号" prop="lastTime" min-width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.phone | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column label="身份" prop="realName" min-width="230">
          <template slot-scope="scope" v-if="scope.row.roleNames">
            <el-tag size="small" type="info" v-for="(item, index) in scope.row.roleNames.split(',')" :key="index" class="mr5">{{ item }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后登录时间" prop="lastTime" min-width="180">
          <template slot-scope="scope">
            <span>{{ scope.row.lastTime | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column label="最后登录IP" prop="lastIp" min-width="150">
          <template slot-scope="scope">
            <span>{{ scope.row.lastIp | filterEmpty }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="状态"
          min-width="100"
        >
          <template slot-scope="scope"  v-if="checkPermi(['admin:system:admin:update:status'])">
            <el-switch
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              @change="onchangeIsShow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          label="是否接收短信"
          min-width="100"
        >
          <template slot-scope="scope" v-if="checkPermi(['admin:system:admin:update:sms'])">
            <el-switch
              v-model="scope.row.isSms"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              :disabled="!scope.row.phone"
              @click.native="onchangeIsSms(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="删除标记" prop="status" min-width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.isDel | filterYesOrNo }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="130"  fixed="right">
          <template slot-scope="scope">
            <template v-if="scope.row.isDel">
              <span>-</span>
            </template>
            <template v-else>
              <el-button type="text" size="mini" @click="handlerOpenEdit(1,scope.row)" v-hasPermi="['admin:system:admin:info']">编辑</el-button>
              <el-button type="text" size="mini" @click="handlerOpenDel(scope.row)" v-hasPermi="['admin:system:admin:delete']">删除</el-button>
            </template>
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
      width="700px"
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
import * as systemAdminApi from '@/api/systemadmin.js'
import * as roleApi from '@/api/role.js'
import edit from './edit'
import { checkPermi } from "@/utils/permission"; // 权限判断函数
export default {
  // name: "index"
  components: { edit },
  data() {
    return {
      constants:this.$constants,
      listData: { list: [] },
      listPram: {
        account: null,
        addTime: null,
        lastIp: null,
        lastTime: null,
        level: null,
        loginCount: null,
        realName: null,
        roles: null,
        status: null,
        page: 1,
        limit: this.$constants.page.limit[0]
      },
      roleList: [],
      menuList: [],
      editDialogConfig: {
        visible: false,
        isCreate: 0, // 0=创建，1=编辑
        editData: {}
      }
    }
  },
  mounted() {
    this.handleGetAdminList()
    this.handleGetRoleList()
  },
  methods: {
    checkPermi,
    onchangeIsShow(row) {
      systemAdminApi.updateStatusApi({id: row.id, status: row.status})
        .then(async () => {
          this.$message.success('修改成功');
          this.handleGetAdminList()
        }).catch(()=>{
        row.status = !row.status
      })
    },
    onchangeIsSms(row) {
      // this.$confirm(`此操作将${!row.isSms ? '开启' : '关闭'}验证, 是否继续？`, "提示", {
      //   confirmButtonText: "确定",
      //   cancelButtonText: "取消",
      //   type: "warning"
      // }).then(async () => {
      //   row.isSms = !row.isSms
      // }).catch(() => {
      //   this.$message.error('取消操作')
      // })

      if(!row.phone) return this.$message({
        message: '请先为管理员添加手机号!',
        type: 'warning'
      });
      systemAdminApi.updateIsSmsApi({id: row.id})
        .then(async () => {
          this.$message.success('修改成功');
          this.handleGetAdminList()
        }).catch(()=>{
        row.isSms = !row.isSms
      })
    },
    handleSearch() {
      this.listPram.page = 1
      this.handleGetAdminList()
    },
    handleSizeChange(val) {
      this.listPram.limit = val
      this.handleGetAdminList()
      this.handleGetRoleList(this.listPram)
    },
    handleCurrentChange(val) {
      this.listPram.page = val
      this.handleGetAdminList()
      this.handleGetRoleList(this.listPram)
    },
    handleGetRoleList() {
      const _pram = {
        page: 1,
        limit: this.constants.page.limit[4]
      }
      roleApi.getRoleList(_pram).then(data => {
        this.roleList = data
      })
    },
    handlerOpenDel(rowData) {
      this.$confirm('确认删除当前数据').then(() => {
        const _pram = { id: rowData.id}
        systemAdminApi.adminDel(_pram).then(data => {
          this.$message.success('删除数据成功')
          this.handleGetAdminList()
        })
      })
    },
    handleGetAdminList() {
      systemAdminApi.adminList( this.listPram ).then(data => {
        this.listData = data
        // this.handlerGetMenuList()
      })
    },
    handlerOpenEdit(isCreate, editDate) {
      this.editDialogConfig.editData = editDate
      this.editDialogConfig.isCreate = isCreate
      this.editDialogConfig.visible = true
    },
    handlerGetMenuList() { // 获取菜单全部数据后做menu翻译使用
      systemAdminApi.listCategroy({ page: 1, limit: 999, type: 5 }).then(data => {
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
      this.handleGetAdminList()
    }
  }
}
</script>

<style scoped>

</style>
