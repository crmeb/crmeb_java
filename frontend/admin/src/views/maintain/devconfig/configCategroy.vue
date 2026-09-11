<template>
  <div class="divBox">
    <el-card class="box-card">
      <template #header>
        <div>
          <el-form inline>
            <el-form-item>
              <el-button type="primary" @click="handlerOpenAdd({ id: 0, name: '顶层目录' })">添加分类</el-button>
            </el-form-item>
          </el-form>
          <el-alert
            title="温馨提示"
            type="success"
            description="添加一级分类以后，务必添加二级分类并配置表单，否则会出现渲染错误"
          >
          </el-alert>
        </div>
      </template>
      <el-table
        ref="treeListRef"
        :data="treeList"
        style="width: 100%"
        row-key="id"

        class="table"
        highlight-current-row
        :tree-props="{ children: 'child', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="name" label="分类昵称" min-width="300">
          <template #default="scope">
            {{ scope.row.name }}
          </template>
        </el-table-column>
        <el-table-column label="英文名称" show-overflow-tooltip min-width="180">
          <template #default="scope">
            <span>{{ scope.row.url }}</span>
          </template>
        </el-table-column>
        <el-table-column label="已关联的表单" show-overflow-tooltip min-width="130">
          <template #default="scope">
            <span>{{ scope.row.extra }}</span>
          </template>
        </el-table-column>
        <!--            <el-table-column label="排序" prop="sort" width="150"></el-table-column>-->
        <el-table-column label="启用状态" min-width="100">
          <template #default="scope">
            <span>{{ $filters.filterYesOrNo(scope.row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <a :disabled="scope.row.pid > 0" @click="handlerOpenAdd(scope.row)">添加子目录</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="handleEditMenu(scope.row)">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="handlerOpenFormConfig(scope.row)">配置列表</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDelMenu(scope.row)">删除</a>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog
      :title="editDialogConfig.isCreate === 0 ? '添加分类' : '编辑分类'"
      v-model="editDialogConfig.visible"
      destroy-on-close
      :close-on-click-modal="false"
      width="540px"
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
    <el-dialog
      title="选择已配置的表单"
      width="720px"
      class="user-dialog"
      v-model="configFormSelectedDialog.visible"
    >
      <form-config-list
        v-if="configFormSelectedDialog.visible"
        select-model
        @selectedRowData="handlerSelectedRowData"
      />
      <el-form>
        <el-form-item class="btn-box">
          <el-button type="primary" style="width: 100%" @click="handlerAddFormExtra">关联</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import * as categoryApi from '@/api/categoryApi.js';
import edit from '@/views/maintain/devconfig/configCategotyEdit.vue';
import * as selfUtil from '@/utils/ZBKJIutil.js';
import configList from './configList';
import formConfigList from '@/views/maintain/formConfig';

defineOptions({});

const props = defineProps({});

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const searchPram = reactive({
  status: null,
  type: null,
});
const editDialogConfig = reactive({
  visible: false,
  isCreate: 0, // 0=创建，1=编辑
  prent: {}, // 父级对象
  data: {},
});
const treeList = ref([]);
const listPram = reactive({
  pid: 0,
  type: constants.categoryType[5].value,
  status: null,
  name: null,
  page: constants.page.page,
  limit: constants.page.limit[1],
});
const configFormSelectedDialog = reactive({
  visible: false,
  currentData: {},
});

const treeListRef = ref(null);

function handlerOpenFormConfig(rowData) {
  configFormSelectedDialog.currentData = rowData;
  configFormSelectedDialog.visible = true;
}
function handlerGetList() {
  categoryApi.listCategroy(listPram).then((data) => {
    // this.dataList = data
    treeList.value = data;
  });
}
function handlerOpenAdd(rowData) {
  editDialogConfig.isCreate = 0;
  editDialogConfig.prent = rowData;
  editDialogConfig.data = {};
  editDialogConfig.biztype = props.biztype;
  editDialogConfig.visible = true;
}
function handleEditMenu(rowData) {
  editDialogConfig.isCreate = 1;
  editDialogConfig.data = rowData;
  editDialogConfig.prent = rowData;
  editDialogConfig.visible = true;
}
function handleDelMenu(rowData) {
  proxy.$modalSure('删除当前数据?').then(() => {
    categoryApi.deleteCategroy(rowData).then((data) => {
      handlerGetTreeList();
      ElMessage.success('删除成功');
    });
  });
}
function hideEditDialog() {
  setTimeout(() => {
    editDialogConfig.prent = {};
    editDialogConfig.type = 0;
    editDialogConfig.visible = false;
    handlerGetTreeList();
  }, 200);
}
function handlerGetTreeList() {
  // status: this.selectModel?1:-1
  const _pram = { type: constants.categoryType[5].value, status: -1 };
  categoryApi.treeCategroy(_pram).then((data) => {
    treeList.value = handleAddArrt(data);
  });
}
function handleAddArrt(treeData) {
  // let _result = this.addTreeListLabel(treeData)
  const _result = selfUtil.addTreeListLabel(treeData);
  return _result;
}
function handlerSelectedRowData(rowData) {
  configFormSelectedDialog.currentData.extra = rowData.id;
}
function handlerAddFormExtra() {
  categoryApi.updateCategroy(configFormSelectedDialog.currentData).then((data) => {
    ElMessage.success('关联表单成功');
    setTimeout(() => {
      configFormSelectedDialog.visible = false;
      handlerGetTreeList();
    }, 800);
  });
}

onMounted(() => {
  handlerGetTreeList();
});
</script>

<style lang="scss" scoped>
:deep(.user-dialog .el-dialog__body) {
  padding: 0 !important;
  height: 600px;
}
.btn-box {
  padding: 0 20px;
}
:deep(.el-alert--success.is-light ){
  background: #fff1e5 !important;
  font-size: 12px;
  color: #ff7d00 !important;
}
:deep(.el-alert--success.is-light .el-alert__description) {
  color: #ff7d00 !important;
}
</style>
