<template>
  <div class="container-FromGen">
    <div class="left-board">
      <div class="logo-wrapper">
        <div class="logo">
          <span>CRMEB</span>
        </div>
      </div>
      <el-scrollbar class="left-scrollbar">
        <div class="components-list">
          <div v-for="(item, listIndex) in leftComponents" :key="listIndex">
            <div class="components-title">
              <svg-icon icon-class="component" />
              {{ item.title }}
            </div>
            <draggable
              class="components-draggable"
              :list="item.list"
              :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
              :clone="cloneComponent"
              :item-key="getDraggableItemKey"
              draggable=".components-item"
              :sort="false"
              @end="onEnd"
            >
              <template #item="{ element }">
                <div class="components-item" @click="addComponent(element)">
                  <div class="components-body">
                    <svg-icon :icon-class="element.__config__.tagIcon" />
                    {{ element.__config__.label }}
                  </div>
                </div>
              </template>
            </draggable>
          </div>
        </div>
      </el-scrollbar>
    </div>

    <div class="center-board">
      <div class="action-bar">
        <!--                <el-button icon="el-icon-video-play" link @click="run">-->
        <!--                  运行-->
        <!--                </el-button>-->
        <!--                <el-button icon="el-icon-view" link @click="showJson">-->
        <!--                  查看json-->
        <!--                </el-button>-->
        <!-- <el-button icon="el-icon-download" link @click="download"> -->
        <!-- 导出vue文件 -->
        <!-- </el-button> -->
        <!--                <el-button class="copy-btn-main" icon="el-icon-document-copy" link @click="copy">-->
        <!--                  复制代码-->
        <!--                </el-button>-->
        <!--                <el-button class="delete-btn" icon="el-icon-delete" link @click="empty">-->
        <!--                  清空-->
        <!--                </el-button>-->
        <el-form ref="selfForm" inline :model="selfFormData">
          <el-form-item
            label="名称"
            prop="name"
            :rules="[{ required: true, message: '请填写名称', trigger: ['blur', 'change'] }]"
          >
            <el-input v-model="selfFormData.name" placeholder="名称" />
          </el-form-item>
          <el-form-item
            label="描述"
            prop="info"
            :rules="[{ required: true, message: '请填写描述', trigger: ['blur', 'change'] }]"
          >
            <el-input v-model="selfFormData.info" placeholder="描述" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handlerSaveJSON('selfForm')" v-hasPermi="['admin:system:form:update']"
              >保存</el-button
            >
          </el-form-item>
        </el-form>
      </div>
      <el-scrollbar class="center-scrollbar">
        <el-row class="center-board-row" :gutter="formConfRef.gutter">
          <el-form
            :size="normalizeElementSize(formConfRef.size)"
            :label-position="formConfRef.labelPosition"
            :disabled="formConfRef.disabled"
            :label-width="formConfRef.labelWidth + 'px'"
          >
            <draggable
              class="drawing-board"
              :list="drawingList"
              :item-key="getDraggableItemKey"
              :animation="340"
              group="componentsGroup"
            >
              <template #item="{ element, index }">
                <draggable-item
                  :key="element.renderKey"
                  :drawing-list="drawingList"
                  :element="element"
                  :index="index"
                  :active-id="activeId"
                  :form-conf="formConfRef"
                  @activeItem="activeFormItem"
                  @copyItem="drawingItemCopy"
                  @deleteItem="drawingItemDelete"
                />
              </template>
            </draggable>
            <div v-show="!drawingList.length" class="empty-info">从左侧拖入或点选组件进行表单设计</div>
          </el-form>
        </el-row>
      </el-scrollbar>
    </div>

    <right-panel
      :active-data="activeData"
      :form-conf="formConfRef"
      :show-field="!!drawingList.length"
      @tag-change="tagChange"
    />

    <code-type-dialog
      v-model="dialogVisible"
      title="选择生成类型"
      :show-file-name="showFileName"
      @confirm="generate"
    />
    <input id="copyNode" type="hidden" />
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted, getCurrentInstance } from 'vue';
import { ElMessage, ElNotification, ElMessageBox } from '@/utils/elementPlusFeedback';
import draggable from 'vuedraggable';
import { debounce } from 'throttle-debounce';
import { saveAs } from 'file-saver';
import ClipboardJS from 'clipboard';
import render from '@/components/FormGenerator/components/render/render';
import RightPanel from './RightPanel';
import {
  inputComponents,
  selectComponents,
  layoutComponents,
  formConf,
} from '@/components/FormGenerator/components/generator/config';
import { exportDefault, beautifierConf, isNumberStr, titleCase } from '../utils/index';
import { makeUpHtml, vueTemplate, vueScript, cssStyle } from '@/components/FormGenerator/components/generator/html';
import { makeUpJs } from '@/components/FormGenerator/components/generator/js';
import { makeUpCss } from '@/components/FormGenerator/components/generator/css';
import drawingDefalut from '@/components/FormGenerator/components/generator/drawingDefalut';
// import logo from '@/assets/logo.png'
import CodeTypeDialog from './CodeTypeDialog';
import DraggableItem from './DraggableItem';
import { getDrawingList, saveDrawingList, getIdGlobal, saveIdGlobal, getFormConf, getFormConfSelf } from '../utils/db';
import loadBeautifier from '../utils/loadBeautifier';
import { Debounce } from '@/utils/validate';
import { getDraggableItemKey } from '@/utils/draggableKey';
let beautifier;
const emptyActiveData = { style: {}, autosize: {} };
let oldActiveId;
let tempActiveData;
const drawingListInDB = getDrawingList();
const formConfInDB = getFormConf();
const idGlobal = ref(getIdGlobal());

const props = defineProps({
  editData: {
    type: Object,
    default: {},
  },
  isCreate: {
    type: Number,
    default: 0, // 0=创建，1=编辑
  },
});
const emit = defineEmits(['getFormConfigDataResult']);
const { proxy } = getCurrentInstance();

const formConfRef = ref(formConf);
const inputComponentsRef = inputComponents;
const selectComponentsRef = selectComponents;
const layoutComponentsRef = layoutComponents;
const labelWidth = ref(100);
const drawingList = ref(drawingDefalut);
const drawingData = ref({});
const activeId = ref(drawingDefalut[0].formId);
const formData = ref({});
const dialogVisible = ref(false);
const generateConf = ref(null);
const showFileName = ref(false);
const activeData = ref(drawingDefalut[0]);
const saveDrawingListDebounce = debounce(340, saveDrawingList);
const saveIdGlobalDebounce = debounce(340, saveIdGlobal);
const leftComponents = [
  {
    title: '输入型组件',
    list: inputComponents,
  },
  {
    title: '选择型组件',
    list: selectComponents,
  },
  {
    title: '布局型组件',
    list: layoutComponents,
  },
];
const selfForm = ref(null);
const selfFormData = reactive({
  name: null,
  info: null,
  id: null,
});
let operationType = '';

function normalizeElementSize(size) {
  const sizeMap = { medium: 'default', mini: 'small' };
  return sizeMap[size] || size;
}

// eslint-disable-next-line func-names
watch(
  () => activeData.value.__config__.label,
  function (val, oldVal) {
    if (
      activeData.value.placeholder === undefined ||
      !activeData.value.__config__.tag ||
      oldActiveId !== activeId.value
    ) {
      return;
    }
    activeData.value.placeholder = activeData.value.placeholder.replace(oldVal, '') + val;
  },
);
watch(
  activeId,
  (val) => {
    oldActiveId = val;
  },
  { immediate: true },
);
watch(
  drawingList,
  (val) => {
    saveDrawingListDebounce(val);
    if (val.length === 0) idGlobal.value = 100;
  },
  { deep: true },
);
watch(
  idGlobal,
  (val) => {
    saveIdGlobalDebounce(val);
  },
  { immediate: true },
);

function activeFormItem(element) {
  activeData.value = element;
  activeId.value = element.__config__.formId;
}
function onEnd(obj) {
  if (obj.from !== obj.to) {
    activeData.value = tempActiveData;
    activeId.value = idGlobal.value;
  }
}
function addComponent(item) {
  const clone = cloneComponent(item);
  drawingList.value.push(clone);
  activeFormItem(clone);
}
function cloneComponent(origin) {
  const clone = JSON.parse(JSON.stringify(origin));
  const config = clone.__config__;
  config.formId = ++idGlobal.value;
  config.span = formConfRef.value.span;
  config.renderKey = +new Date(); // 改变renderKey后可以实现强制更新组件
  if (config.layout === 'colFormItem') {
    clone.__vModel__ = `field${idGlobal.value}`;
    clone.placeholder !== undefined && (clone.placeholder += config.label);
  } else if (config.layout === 'rowFormItem') {
    config.componentName = `row${idGlobal.value}`;
    config.gutter = formConfRef.value.gutter;
  }
  tempActiveData = clone;
  return tempActiveData;
}
function AssembleFormData() {
  formData.value = {
    fields: JSON.parse(JSON.stringify(drawingList.value)),
    ...formConfRef.value,
  };
}
function generate(data) {
  const func = proxy[`exec${titleCase(operationType)}`];
  generateConf.value = data;
  func && func(data);
}
function execDownload(data) {
  const codeStr = generateCode();
  const blob = new Blob([codeStr], { type: 'text/plain;charset=utf-8' });
  saveAs(blob, data.fileName);
}
function execCopy(data) {
  document.getElementById('copyNode').click();
}
function empty() {
  ElMessageBox.confirm('确定要清空所有组件吗？', '提示', { type: 'warning', customClass: 'deleteConfirm' }).then(() => {
    drawingList.value = [];
    idGlobal.value = 100;
  });
}
function drawingItemCopy(item, parent) {
  let clone = JSON.parse(JSON.stringify(item));
  clone = createIdAndKey(clone);
  parent.push(clone);
  activeFormItem(clone);
}
function createIdAndKey(item) {
  const config = item.__config__;
  config.formId = ++idGlobal.value;
  config.renderKey = +new Date();
  if (config.layout === 'colFormItem') {
    item.__vModel__ = `field${idGlobal.value}`;
  } else if (config.layout === 'rowFormItem') {
    config.componentName = `row${idGlobal.value}`;
  }
  if (Array.isArray(config.children)) {
    config.children = config.children.map((childItem) => createIdAndKey(childItem));
  }
  return item;
}
function drawingItemDelete(index, parent) {
  parent.splice(index, 1);
  proxy.$nextTick(() => {
    const len = drawingList.value.length;
    if (len) {
      activeFormItem(drawingList.value[len - 1]);
    }
  });
}
function generateCode() {
  const { type } = generateConf.value;
  AssembleFormData();
  const script = vueScript(makeUpJs(formData.value, type));
  const html = vueTemplate(makeUpHtml(formData.value, type));
  const css = cssStyle(makeUpCss(formData.value));
  return beautifier.html(html + script + css, beautifierConf.html);
}
const handlerSaveJSON = Debounce(function (form) {
  // AssembleFormData()
  // loadBeautifier(btf => {
  //   beautifier = btf
  //   let jsonStr = JSON.stringify(formData)
  //   beautifierJson = beautifier.js(jsonStr, beautifierConf.js)
  //
  // })
  selfForm.value.validate((result) => {
    if (!result) return;
    const formConfig = getFormConfSelf();
    if (formConfig.fields.length === 0) {
      ElMessage.error('表单配置数据不能为空');
      return;
    }
    selfFormData.content = JSON.stringify(formConfig);
    emit('getFormConfigDataResult', selfFormData);
  });
});
function download() {
  dialogVisible.value = true;
  showFileName.value = true;
  operationType = 'download';
}
function run() {
  dialogVisible.value = true;
  showFileName.value = false;
  operationType = 'run';
}
function copy() {
  dialogVisible.value = true;
  showFileName.value = false;
  operationType = 'copy';
}
function tagChange(newTag) {
  newTag = cloneComponent(newTag);
  const config = newTag.__config__;
  newTag.__vModel__ = activeData.value.__vModel__;
  config.formId = activeId.value;
  config.span = activeData.value.__config__.span;
  activeData.value.__config__.tag = config.tag;
  activeData.value.__config__.tagIcon = config.tagIcon;
  activeData.value.__config__.document = config.document;
  if (typeof activeData.value.__config__.defaultValue === typeof config.defaultValue) {
    config.defaultValue = activeData.value.__config__.defaultValue;
  }
  Object.keys(newTag).forEach((key) => {
    if (activeData.value[key] !== undefined) {
      newTag[key] = activeData.value[key];
    }
  });
  activeData.value = newTag;
  updateDrawingList(newTag, drawingList.value);
}
function updateDrawingList(newTag, list) {
  const index = list.findIndex((item) => item.__config__.formId === activeId.value);
  if (index > -1) {
    list.splice(index, 1, newTag);
  } else {
    list.forEach((item) => {
      if (Array.isArray(item.__config__.children)) updateDrawingList(newTag, item.__config__.children);
    });
  }
}

defineExpose({ activeFormItem, cloneComponent, AssembleFormData, generate, execDownload, execCopy, empty, drawingItemCopy, createIdAndKey, drawingItemDelete, generateCode, handlerSaveJSON, download, run, copy, tagChange, updateDrawingList });

onMounted(() => {
  if (props.editData.content) {
    let { id, name, info, content } = props.editData;
    selfFormData.name = name;
    selfFormData.id = id;
    selfFormData.info = info;
    content = JSON.parse(content);
    drawingList.value = content.fields;
    const _content = JSON.parse(JSON.stringify(content));
    delete _content.fields;
    // 原地合并编辑数据到响应式 formConfRef，保证 RightPanel 持有的同一个响应式代理能收到更新
    // （若用 formConfRef.value = _content 替换引用，RightPanel 仍持有旧对象，编辑数据不会生效）
    Object.assign(formConfRef.value, _content);
  }
  // if (Array.isArray(drawingListInDB) && drawingListInDB.length > 0) {
  //   drawingList.value = drawingListInDB
  // } else {
  //   drawingList.value = drawingDefalut
  // }
  activeFormItem(drawingList.value[0]);
  // if (formConfInDB) {
  //   formConfRef.value = formConfInDB
  // }
  loadBeautifier((btf) => {
    beautifier = btf;
  });
  const clipboard = new ClipboardJS('#copyNode', {
    text: (trigger) => {
      const codeStr = generateCode();
      ElNotification({
        title: '成功',
        message: '代码已复制到剪切板，可粘贴。',
        type: 'success',
      });
      return codeStr;
    },
  });
  clipboard.on('error', (e) => {
    ElMessage.error('代码复制失败');
  });
});
</script>

<style lang="scss">
@use '../styles/home' as *;
</style>
