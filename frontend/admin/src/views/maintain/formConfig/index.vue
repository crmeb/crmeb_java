<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add padding-24">
        <el-form inline @submit.prevent>
          <el-form-item label="关键字：">
            <el-input
              v-model="listPram.keywords"
              placeholder="请输入id，名称，描述"
              clearable
              class="selWidth"

            >
            </el-input>
          </el-form-item>
          <el-button type="primary" @click="handlerSearch">搜索</el-button>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card">
      <template #header>
        <div class="clearfix">
          <el-button
            type="primary"
            @click="handlerEditData({}, 0)"
            v-if="!selectModel"
            v-hasPermi="['admin:system:form:save']"
            >创建表单</el-button
          >
          <el-button v-if="selectModel" type="primary" :disabled="!selectedConfigData.id" @click="handlerConfimSelect"
            >确定选择</el-button
          >
        </div>
      </template>
      <el-table
        :data="dataList.list"
        :highlight-current-row="selectModel"

        class="table"
        @current-change="handleCurrentRowChange"
      >
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column label="名称" prop="name" min-width="180" />
        <el-table-column label="描述" prop="info" min-width="220" />
        <el-table-column label="更新时间" prop="updateTime" min-width="200" />
        <el-table-column v-if="!selectModel" label="操作" width="80" fixed="right">
          <template #default="scope">
            <a @click="handlerEditData(scope.row, 1)" v-hasPermi="['admin:system:form:info']">编辑</a>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="listPram.page"
        :page-size="listPram.limit"
        :page-sizes="constants.page.limit"
        :layout="constants.page.layout"
        :total="dataList.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        background
      />
    </el-card>
    <el-dialog
      v-model="editDialogConfig.visible"
      fullscreen
      :title="editDialogConfig.isCreate === 0 ? '创建表单' : '编辑表单'"
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

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';
import edit from './edit';

defineOptions({});

const props = defineProps({
  selectModel: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['selectedRowData']);

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const listPram = reactive({
  keywords: null,
  page: 1,
  limit: constants.page.limit[0],
});
const editDialogConfig = reactive({
  visible: false,
  editData: {},
  isCreate: 0,
});
const dataList = reactive({ list: [], total: 0 });
const selectedConfigData = ref({});

function handlerSearch() {
  listPram.page = 1;
  handlerGetList(listPram);
}
function handlerGetList(pram) {
  systemFormConfigApi.getFormConfigList(pram).then((data) => {
    Object.assign(dataList, data);
  });
}
function handlerEditData(rowData, isCreate) {
  if (isCreate === 0) {
    editDialogConfig.editData = {};
  } else {
    editDialogConfig.editData = rowData;
  }
  editDialogConfig.isCreate = isCreate;
  editDialogConfig.visible = true;
}
function handlerHide() {
  editDialogConfig.editData = {};
  editDialogConfig.isCreate = 0;
  editDialogConfig.visible = false;
  handlerGetList(listPram);
}
function handleSizeChange(val) {
  listPram.limit = val;
  handlerGetList(listPram);
}
function handleCurrentChange(val) {
  listPram.page = val;
  handlerGetList(listPram);
}
function handleCurrentRowChange(rowData) {
  selectedConfigData.value = rowData;
}
function handlerConfimSelect() {
  emit('selectedRowData', selectedConfigData.value);
}

onMounted(() => {
  handlerGetList(listPram);
});
</script>

<style scoped lang="scss">
:deep(.el-card.is-always-shadow) {
  -webkit-box-shadow: none;
  box-shadow: none;
  padding: 0 4px;
}
.padding-24 {
  padding: 20px 24px 0;
}
</style>
