<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline @submit.prevent>
          <el-form-item label="昵称搜索：">
            <el-input v-model="listPram.roleName" placeholder="请输入角色昵称" clearable class="selWidth" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleGetRoleList">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <el-form inline @submit.prevent>
        <el-form-item>
          <el-button
            type="primary"
            @click="handlerOpenEdit(0)"
            v-hasPermi="['admin:system:role:save', 'admin:system:menu:cache:tree']"
            >添加角色</el-button
          >
        </el-form-item>
      </el-form>
      <el-table :data="listData.list" :header-cell-style="{ background: '#f8f8f9', color: '#515a6e' }">
        <el-table-column label="角色编号" prop="id" width="120"></el-table-column>
        <el-table-column label="角色昵称" prop="roleName" min-width="130" />
        <el-table-column label="状态" prop="status">
          <template #default="scope">
            <el-switch
              v-if="checkPermi(['admin:system:role:update:status'])"
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              @change="handleStatusChange(scope.row)"
            ></el-switch>
            <span v-else>{{ scope.row.status ? '开启' : '关闭' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" min-width="150" />
        <el-table-column label="更新时间" prop="updateTime" min-width="150" />
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="scope">
            <a @click="handlerOpenEdit(1, scope.row)" v-hasPermi="['admin:system:role:info']">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="handlerOpenDel(scope.row)" v-hasPermi="['admin:system:role:delete']">删除</a>
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
        background
      />
    </el-card>
    <el-dialog
      v-model="editDialogConfig.visible"
      :title="editDialogConfig.isCreate === 0 ? '创建身份' : '编辑身份'"
      destroy-on-close
      :close-on-click-modal="false"
      width="500px"
    >
      <edit
        v-if="editDialogConfig.visible"
        :is-create="editDialogConfig.isCreate"
        :edit-data="editDialogConfig.editData"
        @hideEditDialog="hideEditDialog"
        ref="editFormRef"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import * as roleApi from '@/api/role.js';
import edit from './edit';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

// name: "index"
defineOptions({ name: 'identityManager' });

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const listData = ref({ list: [], total: 0 });
const listPram = reactive({
  createTime: null,
  updateTime: null,
  level: null,
  page: 1,
  limit: constants.page.limit[0],
  roleName: null,
  rules: null,
  status: null,
});
const menuList = ref([]);
const editDialogConfig = reactive({
  visible: false,
  isCreate: 0, // 0=创建，1=编辑
  editData: {},
});

const editFormRef = ref(null);

function handlerOpenDel(rowData) {
  proxy.$modalSure('删除当前数据').then(() => {
    roleApi.delRole(rowData).then((data) => {
      ElMessage.success('删除数据成功');
      handleGetRoleList();
    });
  });
}
function handleGetRoleList() {
  roleApi.getRoleList(listPram).then((data) => {
    listData.value = data;
  });
}
function handlerOpenEdit(isCreate, editDate) {
  isCreate === 1 ? (editDialogConfig.editData = editDate) : (editDialogConfig.editData = {});
  editDialogConfig.isCreate = isCreate;
  editDialogConfig.visible = true;
}
function hideEditDialog() {
  editDialogConfig.visible = false;
  handleGetRoleList();
}
function handleSizeChange(val) {
  listPram.limit = val;
  handleGetRoleList(listPram);
}
function handleCurrentChange(val) {
  listPram.page = val;
  handleGetRoleList(listPram);
}
//修改状态
function handleStatusChange(row) {
  roleApi.updateRoleStatus(row).then((res) => {
    ElMessage.success('更新状态成功');
    handleGetRoleList();
  });
}

onMounted(() => {
  handleGetRoleList();
});
</script>

<style scoped lang="scss"></style>
