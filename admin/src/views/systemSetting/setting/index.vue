<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-tabs
        v-model="activeNamel1"
        @tab-change="handleTabClick"
        v-loading="loading"
        v-if="checkPermi(['admin:system:config:info'])"
      >
        <el-tab-pane v-for="tab in treeList" :key="getNodeKey(tab)" :label="tab.name" :name="getNodeTabName(tab)">
          <div v-if="activeNamel1 === getNodeTabName(tab)">
            <el-tabs
              v-if="hasChildren(tab)"
              v-model="activeNamel2"
              type="border-card"
              @tab-change="handleItemTabClick"
              class="tab-content"
            >
              <el-tab-pane
                v-for="tabItem in tab.child"
                :key="getNodeKey(tabItem)"
                :label="tabItem.name"
                :name="getNodeTabName(tabItem)"
              >
                <parser
                  v-if="activeNamel2 === getNodeTabName(tabItem) && formConfChild.render"
                  :key="formConfChild.id"
                  :is-edit="formConfChild.isEdit"
                  :form-conf="formConfChild.content"
                  :form-edit-data="currentEditData"
                  @submit="handlerSubmit"
                />
              </el-tab-pane>
            </el-tabs>
            <div v-else>
              <parser
                v-if="formConf.render"
                :key="formConf.id"
                :is-edit="formConf.isEdit"
                :form-conf="formConf.content"
                :form-edit-data="currentEditData"
                @submit="handlerSubmit"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import parser from '@/components/FormGenerator/components/parser/Parser';
import * as categoryApi from '@/api/categoryApi.js';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';
import * as systemSettingApi from '@/api/systemSetting.js';
import * as systemConfigApi from '@/api/systemConfig.js';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';

// name: "index"
defineOptions({ name: 'systemSetting' });

const CONFIG_CATEGORY_TYPE = 6;
const loading = ref(false);
const formConf = reactive({ content: { fields: [] }, id: null, render: false, isEdit: false });
const formConfChild = reactive({ content: { fields: [] }, id: null, render: false, isEdit: false });
const activeNamel1 = ref('0');
const activeNamel2 = ref(''); //针对文件特殊处理
const treeList = ref([]);
const currentEditId = ref(null);
const currentEditData = ref({});
const currentSelectedUploadFlag = ref(null);

function toTabName(value) {
  return value != null ? value.toString() : '';
}

function getNodeKey(node) {
  return node?.id ?? node?.extra ?? node?.name;
}

function getNodeTabName(node) {
  return toTabName(getNodeKey(node));
}

function hasChildren(node) {
  return Array.isArray(node?.child) && node.child.length > 0;
}

function getArrayPayload(payload) {
  if (Array.isArray(payload)) return payload;
  if (!payload || typeof payload !== 'object') return [];
  const arrayKeys = ['tabs', 'data', 'list', 'records', 'rows', 'children', 'child'];
  for (const key of arrayKeys) {
    if (Array.isArray(payload[key])) return payload[key];
  }
  if (payload.data && typeof payload.data === 'object') return getArrayPayload(payload.data);
  return [];
}

function getChildPayload(node) {
  const childKeys = ['child', 'children', 'tabs', 'list', 'data'];
  for (const key of childKeys) {
    if (Array.isArray(node?.[key]) && node[key].length > 0) return node[key];
  }
  for (const key of childKeys) {
    if (Array.isArray(node?.[key])) return node[key];
  }
  return [];
}

function normalizeCategoryNode(node) {
  const child = getChildPayload(node);
  const name = node?.name ?? node?.title ?? node?.label ?? '';
  const extra = node?.extra ?? node?.formId ?? node?.form_id ?? '';
  const id = node?.id ?? node?.categoryId ?? node?.value ?? extra ?? name;
  return {
    ...node,
    id,
    name,
    label: name,
    extra,
    child: child.map(normalizeCategoryNode),
  };
}

function normalizeTreeList(payload) {
  return getArrayPayload(payload)
    .map(normalizeCategoryNode)
    .filter((item) => item.id !== undefined && item.id !== null && item.name);
}

function findNodeByTabName(nodes, tabName) {
  const currentName = toTabName(tabName);
  for (const node of nodes || []) {
    if (getNodeTabName(node) === currentName || toTabName(node.extra) === currentName) return node;
    const child = findNodeByTabName(node.child, currentName);
    if (child) return child;
  }
  return null;
}

function findFirstFormNode(node) {
  if (!node) return null;
  if (hasChildren(node)) {
    for (const child of node.child) {
      const formNode = findFirstFormNode(child);
      if (formNode) return formNode;
    }
  }
  return node.extra ? node : null;
}

function getFirstAvailableChild(children) {
  return (children || []).find((item) => findFirstFormNode(item)) || children?.[0] || null;
}

function getFormIdByNode(node) {
  return findFirstFormNode(node)?.extra || '';
}

function resetFormState() {
  formConf.content = { fields: [] };
  formConf.render = false;
  formConf.isEdit = false;
  formConfChild.content = { fields: [] };
  formConfChild.render = false;
  formConfChild.isEdit = false;
  currentEditData.value = {};
}

// tab-change 回调直接传入当前 tab 的 name，此时 v-model(activeNamel1) 已更新
function handleTabClick(currentName) {
  const currentTab = findNodeByTabName(treeList.value, currentName);
  if (!currentTab) return;
  loadCategoryForm(currentTab);
}

function loadCategoryForm(currentTab) {
  resetFormState();
  if (hasChildren(currentTab)) {
    const currentChild = getFirstAvailableChild(currentTab.child);
    const formId = getFormIdByNode(currentChild);
    activeNamel2.value = getNodeTabName(currentChild);
    if (!formId) return ElMessage.error('表单配置不正确，请关联正确表单后使用');
    handlerGetLevel2FormConfig(formId);
    return;
  }
  const formId = getFormIdByNode(currentTab);
  activeNamel2.value = '';
  if (!formId) return ElMessage.error('表单配置不正确，请关联正确表单后使用');
  handlerGetLevel1FormConfig(formId);
}

function handlerGetLevel1FormConfig(id) {
  handlerGetFormConfig(id, 1, formConf);
}

// tab-change 回调直接传入当前子 tab 的 name，父级用 activeNamel1（v-model 已更新）
function handleItemTabClick(currentName) {
  const currentTab = findNodeByTabName(treeList.value, activeNamel1.value);
  const currentChild = findNodeByTabName(currentTab?.child, currentName);
  const formId = getFormIdByNode(currentChild);
  if (!formId) return ElMessage.error('表单配置不正确，请关联正确表单后使用');
  handlerGetLevel2FormConfig(formId);
}

function handlerGetLevel2FormConfig(id) {
  handlerGetFormConfig(id, 2, formConfChild);
}

function parseFormContent(content) {
  if (!content) return { fields: [] };
  if (typeof content === 'object') return content;
  try {
    return JSON.parse(content);
  } catch (error) {
    ElMessage.error('表单配置解析失败');
    return { fields: [] };
  }
}

function normalizeFormContent(content) {
  const formContent = parseFormContent(content);
  return {
    formRef: 'elForm',
    formModel: 'formData',
    formRules: 'rules',
    size: 'default',
    labelPosition: 'right',
    labelWidth: 100,
    gutter: 15,
    disabled: false,
    formBtns: true,
    ...formContent,
    fields: Array.isArray(formContent.fields) ? formContent.fields : [],
  };
}

function handlerGetFormConfig(id, level, targetConf) {
  currentEditId.value = id;
  targetConf.content = { fields: [] };
  targetConf.render = false;
  targetConf.isEdit = false;
  currentEditData.value = {};
  loading.value = true;
  systemFormConfigApi
    .getFormConfigInfo({ id })
    .then((data) => {
      const formId = data?.id ?? id;
      targetConf.content = normalizeFormContent(data?.content);
      targetConf.id = formId;
      return handlerGetSettingInfo(formId, level);
    })
    .catch(() => {
      loading.value = false;
    })
    .finally(() => {
      loading.value = false;
    });
}

// 表单详情
function handlerGetSettingInfo(id, level) {
  return systemSettingApi
    .systemConfigInfo({ id: id })
    .then((data) => {
      const editData = data && typeof data === 'object' ? { ...data } : {};
      // 小程序登录配置，此处返回字符串，表单组件是数组，需要字符串转为数组
      if (typeof editData.routine_phone_verification === 'string') {
        editData.routine_phone_verification = editData.routine_phone_verification.split(',').map(Number);
      }
      currentEditData.value = editData;
      setFormRender(level, Object.keys(editData).length > 0);
    })
    .catch(() => {
      currentEditData.value = {};
      setFormRender(level, false);
    });
}

function setFormRender(level, isEdit) {
  if (level === 1) {
    formConf.isEdit = isEdit;
    formConf.render = true;
  } else {
    formConfChild.isEdit = isEdit;
    formConfChild.render = true;
  }
}

const handlerSubmit = Debounce(function (formValue) {
  handlerSave(formValue);
});

function handlerSave(formValue) {
  const _pram = buildFormPram(formValue);
  systemSettingApi.systemConfigSave(_pram).then((data) => {
    ElMessage.success('添加数据成功');
  });
}

function handlerGetTreeList() {
  const _pram = { type: CONFIG_CATEGORY_TYPE, status: 1 };
  loading.value = true;
  categoryApi
    .treeCategroy(_pram)
    .then((data) => {
      treeList.value = normalizeTreeList(data);
      if (treeList.value.length > 0) {
        const firstTab = treeList.value[0];
        activeNamel1.value = getNodeTabName(firstTab);
        loading.value = false;
        loadCategoryForm(firstTab);
        return;
      }
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
}

function buildFormPram(formValue) {
  const _pram = {
    fields: [],
    id: currentEditId.value,
    sort: 0, // 参数暂时无用
    status: true, // 参数暂时无用
  };
  const _fields = [];
  Object.keys(formValue).forEach((key) => {
    _fields.push({
      name: key,
      title: key,
      // 提交数据要求是字符串，如果是数组，转为字符串
      value: formValue[key] instanceof Array ? formValue[key].join(',') : formValue[key],
    });
  });
  _pram.fields = _fields;
  return _pram;
}
//获取文件存储类型
function getCurrentUploadSelectedFlag() {
  systemConfigApi.getUploadTypeApi().then((data) => {
    currentSelectedUploadFlag.value = parseInt(data);
  });
}

onMounted(() => {
  handlerGetTreeList();
  getCurrentUploadSelectedFlag();
});
</script>

<style scoped lang="scss">
:deep(.tab-content .el-tabs__header) {
  margin-bottom: 20px !important;
}
:deep(.dialog-footer-inner) {
  float: left !important;
}
:deep(.closeBtn) {
  display: none;
}
:deep(.saveBtn) {
  margin-left: 0 !important;
}
:deep(.el-tabs__nav .el-tabs__item:nth-of-type(1)) {
  padding-left: 20px !important;
}
</style>
