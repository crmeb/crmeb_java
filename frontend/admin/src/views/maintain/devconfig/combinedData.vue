<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline>
          <el-form-item label="数据搜索：">
            <el-input
              v-model="listPram.keywords"
              placeholder="请输入ID，KEY，组合数据名称，简介"
              class="selWidth"

              clearable
            >
            </el-input>
          </el-form-item>
          <el-button type="primary" @click="handlerSearch">搜索</el-button>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <template #header>
        <div class="clearfix">
          <el-button type="primary" @click="handlerOpenEdit({}, 0)" v-hasPermi="['admin:system:group:save']"
            >添加数据组</el-button
          >
        </div>
      </template>
      <el-table :data="dataList.list" style="width: 100%; margin-bottom: 20px" highlight-current-row>
        <el-table-column label="数据组名称" prop="name" min-width="150" />
        <el-table-column label="简介" prop="info" min-width="150" />
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="scope">
            <a @click="handleDataList(scope.row)" v-hasPermi="['admin:system:group:data:list']">数据列表</a>
            <el-divider direction="vertical"></el-divider>
            <a
              @click="handlerOpenEdit(scope.row, 1)"
              v-hasPermi="['admin:system:group:info', 'admin:system:group:update']"
              >编辑</a
            >
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDelete(scope.row)" v-hasPermi="['admin:system:group:delete']">删除</a>
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
        background
      />
    </el-card>

    <el-dialog
      :title="editDialogConfig.isCreate === 0 ? '创建数据组' : '编辑数据组'"
      v-model="editDialogConfig.visible"
      width="540px"
    >
      <edit
        v-if="editDialogConfig.visible"
        :is-create="editDialogConfig.isCreate"
        :edit-data="editDialogConfig.editData"
        @hideDialog="handlerHideDialog"
        @closeDialog="closeDialog"
      />
    </el-dialog>
    <el-dialog title="组合数据列表" v-model="comDataListConfig.visible">
      <cm-data-list v-if="comDataListConfig.visible" :form-data="comDataListConfig.formData" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import edit from '@/views/maintain/devconfig/combinedDataEdit';
import * as systemGroupApi from '@/api/systemGroup';
import cmDataList from './combineDataList';

defineOptions({});

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const dataList = reactive({
  list: [],
  total: 0,
});
const listPram = reactive({
  keywords: null,
  page: 1,
  pageSize: constants.page.limit[0],
});
const editDialogConfig = reactive({
  visible: false,
  isCreate: 0, // 0=创建 1=编辑
  editData: {},
});
const comDataListConfig = reactive({
  visible: false,
  formData: {},
});

function closeDialog() {
  editDialogConfig.visible = false;
}
function handlerSearch() {
  listPram.page = 1;
  handlerGetList(listPram);
}
function handlerOpenEdit(editData, isCreate) {
  isCreate === 0 ? (editDialogConfig.editData = {}) : (editDialogConfig.editData = editData);
  editDialogConfig.isCreate = isCreate;
  editDialogConfig.visible = true;
}
function handlerGetList(pram) {
  systemGroupApi.groupList(pram).then((data) => {
    Object.assign(dataList, data);
  });
}
function handleDataList(rowData) {
  if (rowData.formId <= 0) return ElMessage.error('请先关联表单');
  comDataListConfig.formData = rowData;
  comDataListConfig.visible = true;
}
function handleDelete(rowData) {
  proxy.$modalSure('删除当前数据', '提示').then(() => {
    systemGroupApi.groupDelete(rowData).then((data) => {
      ElMessage.success('删除数据成功');
      setTimeout(() => {
        handlerGetList(listPram);
      }, 800);
    });
  });
}
function handleSizeChange(val) {
  listPram.limit = val;
  handlerGetList(listPram);
}
function handleCurrentChange(val) {
  listPram.page = val;
  handlerGetList(listPram);
}
function handlerHideDialog() {
  setTimeout(() => {
    editDialogConfig.visible = false;
    handlerGetList(listPram);
  }, 800);
}

onMounted(() => {
  handlerGetList(listPram);
});
</script>

<style scoped></style>
