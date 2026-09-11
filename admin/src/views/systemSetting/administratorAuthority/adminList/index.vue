<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline @submit.prevent>
          <el-form-item label="身份搜索：">
            <el-select v-model="listPram.roles" placeholder="身份" clearable class="selWidth">
              <el-option v-for="item in roleList.list" :key="item.id" :label="item.roleName" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态搜索：">
            <el-select v-model="listPram.status" placeholder="状态" clearable class="selWidth">
              <el-option
                v-for="item in constants.roleListStatus"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="姓名账号：">
            <el-input v-model="listPram.realName" placeholder="姓名或者账号" clearable class="selWidth" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <el-form inline @submit.prevent>
        <el-form-item>
          <el-button type="primary" @click="handlerOpenEdit(0)" v-hasPermi="['admin:system:admin:save']"
            >添加管理员</el-button
          >
        </el-form-item>
      </el-form>
      <el-table :data="listData.list">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column label="姓名" prop="realName" min-width="120" />
        <el-table-column label="账号" prop="account" min-width="120" />
        <el-table-column label="手机号" prop="lastTime" min-width="120">
          <template #default="scope">
            <span>{{ $filters.filterEmpty(scope.row.phone) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="身份" prop="realName" min-width="230">
          <template #default="scope">
            <template v-if="scope.row.roleNames">
              <el-tag

                type="info"
                v-for="(item, index) in scope.row.roleNames.split(',')"
                :key="index"
                class="mr5"
                >{{ item }}</el-tag
              >
            </template>
          </template>
        </el-table-column>
        <el-table-column label="最后登录时间" prop="lastTime" min-width="180">
          <template #default="scope">
            <span>{{ $filters.filterEmpty(scope.row.lastTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="最后登录IP" prop="lastIp" min-width="150">
          <template #default="scope">
            <span>{{ $filters.filterEmpty(scope.row.lastIp) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template #default="scope">
            <el-switch
              v-if="checkPermi(['admin:system:admin:update:status'])"
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              @change="onchangeIsShow(scope.row)"
            />
            <span v-else>{{ scope.row.status ? '开启' : '关闭' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="是否接收短信" min-width="100">
          <template #default="scope">
            <el-switch
              v-if="checkPermi(['admin:system:admin:update:sms'])"
              v-model="scope.row.isSms"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              :disabled="!scope.row.phone"
              @click="onchangeIsSms(scope.row)"
            />
            <span v-else>{{ scope.row.isSms ? '开启' : '关闭' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="删除标记" prop="status" min-width="100">
          <template #default="scope">
            <span>{{ $filters.filterYesOrNo(scope.row.isDel) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="scope">
            <template v-if="scope.row.isDel">
              <span>-</span>
            </template>
            <template v-else>
              <a @click="handlerOpenEdit(1, scope.row)" v-hasPermi="['admin:system:admin:info']">编辑</a>
              <el-divider direction="vertical"></el-divider>
              <a @click="handlerOpenDel(scope.row)" v-hasPermi="['admin:system:admin:delete']">删除</a>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="listPram.page"
        :page-size="listPram.limit"
        :page-sizes="constants.page.limit"
        :layout="constants.page.layout"
        :total="listData.total"
        @size-change="handleSizeChange"
        background
        @current-change="handleCurrentChange"
      />
    </el-card>
    <el-dialog
      v-model="editDialogConfig.visible"
      :title="editDialogConfig.isCreate === 0 ? '添加管理员' : '编辑管理员'"
      destroy-on-close
      :close-on-click-modal="false"
      width="540px"
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

<script setup>
import { reactive, ref, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import * as systemAdminApi from '@/api/systemadmin.js';
import * as roleApi from '@/api/role.js';
import edit from './edit';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

// name: "index"
defineOptions({ name: 'adminList' });

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const listData = ref({ list: [], total: 0 });
const listPram = reactive({
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
  limit: constants.page.limit[0],
});
const roleList = ref([]);
const menuList = ref([]);
const editDialogConfig = reactive({
  visible: false,
  isCreate: 0, // 0=创建，1=编辑
  editData: {},
});

//重置
function handleReset() {
  listPram.roles = null;
  listPram.status = null;
  listPram.realName = null;
  handleGetAdminList();
}
function onchangeIsShow(row) {
  systemAdminApi
    .updateStatusApi({ id: row.id, status: row.status })
    .then(async () => {
      ElMessage.success('修改成功');
      handleGetAdminList();
    })
    .catch(() => {
      row.status = !row.status;
    });
}
function onchangeIsSms(row) {
  // this.$confirm(`此操作将${!row.isSms ? '开启' : '关闭'}验证, 是否继续？`, "提示", {
  //   confirmButtonText: "确定",
  //   cancelButtonText: "取消",
  //   type: "warning"
  // }).then(async () => {
  //   row.isSms = !row.isSms
  // }).catch(() => {
  //   this.$message.error('取消操作')
  // })

  if (!row.phone)
    return ElMessage({
      message: '请先为管理员添加手机号!',
      type: 'warning',
    });
  systemAdminApi
    .updateIsSmsApi({ id: row.id })
    .then(async () => {
      ElMessage.success('修改成功');
      handleGetAdminList();
    })
    .catch(() => {
      row.isSms = !row.isSms;
    });
}
function handleSearch() {
  listPram.page = 1;
  handleGetAdminList();
}
function handleSizeChange(val) {
  listPram.limit = val;
  handleGetAdminList();
  handleGetRoleList(listPram);
}
function handleCurrentChange(val) {
  listPram.page = val;
  handleGetAdminList();
  handleGetRoleList(listPram);
}
function handleGetRoleList() {
  const _pram = {
    page: 1,
    limit: constants.page.limit[4],
  };
  roleApi.getRoleList(_pram).then((data) => {
    roleList.value = data;
  });
}
function handlerOpenDel(rowData) {
  proxy.$modalSure('删除当前数据').then(() => {
    const _pram = { id: rowData.id };
    systemAdminApi.adminDel(_pram).then((data) => {
      ElMessage.success('删除数据成功');
      handleGetAdminList();
    });
  });
}
function handleGetAdminList() {
  systemAdminApi.adminList(listPram).then((data) => {
    listData.value = data;
    // this.handlerGetMenuList()
  });
}
function handlerOpenEdit(isCreate, editDate) {
  editDialogConfig.editData = editDate;
  editDialogConfig.isCreate = isCreate;
  editDialogConfig.visible = true;
}
function handlerGetMenuList() {
  // 获取菜单全部数据后做menu翻译使用
  systemAdminApi.listCategroy({ page: 1, limit: 999, type: 5 }).then((data) => {
    menuList.value = data.list;
    listData.value.list.forEach((item) => {
      const _muneText = [];
      const menuids = item.rules.split(',');
      menuids.map((muid) => {
        menuList.value.filter((menu) => {
          if (menu.id == muid) {
            _muneText.push(menu.name);
          }
        });
      });
      item.rulesView = _muneText.join(',');
      item.rulesViews = item.rulesView;
    });
  });
}
function hideEditDialog() {
  editDialogConfig.visible = false;
  handleGetAdminList();
}

onMounted(() => {
  handleGetAdminList();
  handleGetRoleList();
});
</script>

<style scoped></style>
