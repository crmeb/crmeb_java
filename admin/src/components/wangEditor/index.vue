<template>
  <div class="wang-editor-shell" :class="{ 'is-disabled': disabled }">
    <div class="wang-editor" :style="editorStyle">
      <Toolbar class="wang-editor-toolbar" :editor="editorRef" :defaultConfig="mergedToolbarConfig" :mode="mode" />
      <Editor
        v-show="!sourceVisible"
        class="wang-editor-content"
        v-model="valueHtml"
        :defaultConfig="mergedEditorConfig"
        :mode="mode"
        @onCreated="handleCreated"
        @onChange="handleChange"
      />
      <div v-show="sourceVisible" class="wang-source">
        <el-input
          v-model="sourceHtml"
          type="textarea"
          :autosize="false"
          class="wang-source-input"
          @update:modelValue="handleSourceChange"
        />
      </div>
    </div>

    <el-dialog v-model="imageDialogVisible" width="1024px" title="选择图片" :close-on-click-modal="false">
      <upload-pictures v-if="imageDialogVisible" isChoice="多选" :isType="1" @getPic="handlePickImages" />
    </el-dialog>
    <el-dialog v-model="videoDialogVisible" width="1024px" title="选择视频" :close-on-click-modal="false">
      <upload-pictures v-if="videoDialogVisible" isChoice="多选" :isType="2" @getPic="handlePickVideos" />
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, shallowRef, watch, onBeforeUnmount } from 'vue';
import '@wangeditor/editor/dist/css/style.css';
import { Boot } from '@wangeditor/editor';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import UploadPictures from '@/views/design/theme_editor/components/uploadPictures';

defineOptions({ name: 'WangEditor' });

const imageMenuKey = 'crmebWangEditorImage';
const videoMenuKey = 'crmebWangEditorVideo';
const htmlMenuKey = 'crmebWangEditorHtml';
const editorActions = new WeakMap();
let menusRegistered = false;

class PickerMenu {
  constructor(title, iconSvg, action) {
    this.title = title;
    this.tag = 'button';
    this.iconSvg = iconSvg;
    this.action = action;
  }
  getValue() {
    return '';
  }
  isActive() {
    return false;
  }
  isDisabled() {
    return false;
  }
  exec(editor) {
    const actions = editorActions.get(editor);
    if (actions && typeof actions[this.action] === 'function') actions[this.action]();
  }
}

function registerMenus() {
  if (menusRegistered) return;
  menusRegistered = true;
  const imageIcon =
    '<svg viewBox="0 0 1024 1024"><path d="M864 160H160c-35.3 0-64 28.7-64 64v576c0 35.3 28.7 64 64 64h704c35.3 0 64-28.7 64-64V224c0-35.3-28.7-64-64-64zM224 288h576v288L653.3 429.3c-12.5-12.5-32.8-12.5-45.3 0L480 557.3l-64-64c-12.5-12.5-32.8-12.5-45.3 0L224 640V288zm0 448 169.3-169.3 64 64c12.5 12.5 32.8 12.5 45.3 0L630.7 502.6 800 672v64H224zm112-288a64 64 0 1 0 0-128 64 64 0 0 0 0 128z" fill="currentColor"/></svg>';
  const videoIcon =
    '<svg viewBox="0 0 1024 1024"><path d="M896 256H128c-35.3 0-64 28.7-64 64v384c0 35.3 28.7 64 64 64h768c35.3 0 64-28.7 64-64V320c0-35.3-28.7-64-64-64zM416 656V368l256 144-256 144z" fill="currentColor"/></svg>';
  const htmlIcon =
    '<svg viewBox="0 0 1024 1024"><path d="M352 672 192 512l160-160 45.3 45.3L282.6 512l114.7 114.7L352 672zm320 0-45.3-45.3L741.4 512 626.7 397.3 672 352l160 160-160 160zM480 736l-61.8-16.6L544 288l61.8 16.6L480 736z" fill="currentColor"/></svg>';

  try {
    Boot.registerModule({
      menus: [
        {
          key: imageMenuKey,
          factory() {
            return new PickerMenu('图片', imageIcon, 'image');
          },
        },
        {
          key: videoMenuKey,
          factory() {
            return new PickerMenu('视频', videoIcon, 'video');
          },
        },
        {
          key: htmlMenuKey,
          factory() {
            return new PickerMenu('HTML', htmlIcon, 'html');
          },
        },
      ],
    });
  } catch (error) {
    // 多个编辑器实例重复加载时，wangEditor 会提示菜单已注册，忽略即可。
  }
}

registerMenus();

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: undefined,
  },
  value: {
    type: [String, Number],
    default: undefined,
  },
  content: {
    type: [String, Number],
    default: undefined,
  },
  height: {
    type: [Number, String],
    default: 400,
  },
  placeholder: {
    type: String,
    default: '',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  mode: {
    type: String,
    default: 'default',
  },
  toolbarConfig: {
    type: Object,
    default: () => ({}),
  },
  editorConfig: {
    type: Object,
    default: () => ({}),
  },
});

const emit = defineEmits(['update:modelValue', 'update:value', 'input', 'editorContent', 'change']);

const editorRef = shallowRef(null);
const valueHtml = ref(normalizeHtml(getExternalValue()));
const sourceHtml = ref(valueHtml.value);
const imageDialogVisible = ref(false);
const videoDialogVisible = ref(false);
const sourceVisible = ref(false);
let syncingFromOutside = false;

const editorStyle = computed(() => ({
  height: normalizeHeight(props.height),
}));

const mergedToolbarConfig = computed(() => {
  const insertKeys = props.toolbarConfig.insertKeys || {};
  const customKeys = uniqueKeys([htmlMenuKey, imageMenuKey, videoMenuKey, ...(insertKeys.keys || [])]);
  return {
    ...props.toolbarConfig,
    excludeKeys: uniqueKeys([
      'uploadImage',
      'insertImage',
      'group-image',
      'uploadVideo',
      'insertVideo',
      'group-video',
      ...(props.toolbarConfig.excludeKeys || []),
    ]),
    insertKeys: {
      ...insertKeys,
      index: insertKeys.index ?? 0,
      keys: customKeys,
    },
  };
});

const mergedEditorConfig = computed(() => {
  const menuConf = props.editorConfig.MENU_CONF || {};
  return {
    placeholder: props.placeholder,
    autoFocus: false,
    ...props.editorConfig,
    readOnly: props.disabled,
    MENU_CONF: {
      ...menuConf,
      uploadImage: {
        showLinkImg: false,
        ...(menuConf.uploadImage || {}),
        customUpload() {
          openImagePicker();
        },
      },
      uploadVideo: {
        ...(menuConf.uploadVideo || {}),
        customUpload() {
          openVideoPicker();
        },
      },
    },
  };
});

function uniqueKeys(keys) {
  return [...new Set(keys.filter(Boolean))];
}

function normalizeHeight(height) {
  if (height === undefined || height === null || height === '') return '400px';
  return typeof height === 'number' ? `${height}px` : height;
}

function normalizeHtml(value) {
  if (value === undefined || value === null) return '';
  return String(value);
}

function getExternalValue() {
  if (props.modelValue !== undefined) return props.modelValue;
  if (props.content !== undefined) return props.content;
  return props.value;
}

function emitContent(html) {
  const nextHtml = normalizeHtml(html);
  emit('update:modelValue', nextHtml);
  emit('update:value', nextHtml);
  emit('input', nextHtml);
  emit('editorContent', nextHtml);
  emit('change', nextHtml);
}

function setEditorHtml(html, shouldEmit = false) {
  const nextHtml = normalizeHtml(html);
  valueHtml.value = nextHtml;
  sourceHtml.value = nextHtml;
  if (editorRef.value && editorRef.value.getHtml() !== nextHtml) {
    syncingFromOutside = true;
    editorRef.value.setHtml(nextHtml);
    syncingFromOutside = false;
  }
  if (shouldEmit) emitContent(nextHtml);
}

function handleCreated(editor) {
  editorRef.value = editor;
  editorActions.set(editor, {
    image: openImagePicker,
    video: openVideoPicker,
    html: toggleSource,
  });
  setEditorHtml(getExternalValue());
  updateDisabledState();
}

function handleChange(editor) {
  if (syncingFromOutside) return;
  const html = editor.getHtml();
  valueHtml.value = html;
  sourceHtml.value = html;
  emitContent(html);
}

function handleSourceChange(html) {
  sourceHtml.value = normalizeHtml(html);
  emitContent(sourceHtml.value);
}

function toggleSource() {
  if (!sourceVisible.value && editorRef.value) {
    sourceHtml.value = editorRef.value.getHtml();
  }
  if (sourceVisible.value) {
    setEditorHtml(sourceHtml.value, true);
  }
  sourceVisible.value = !sourceVisible.value;
}

function openImagePicker() {
  imageDialogVisible.value = true;
}

function openVideoPicker() {
  videoDialogVisible.value = true;
}

function handlePickImages(payload) {
  imageDialogVisible.value = false;
  normalizeMediaList(payload).forEach((item) => {
    const src = getMediaUrl(item);
    if (src) insertHtml(`<img src="${escapeAttr(src)}" style="max-width:100%;"/>`);
  });
}

function handlePickVideos(payload) {
  videoDialogVisible.value = false;
  normalizeMediaList(payload).forEach((item) => {
    const src = getMediaUrl(item);
    if (src) insertVideo(src);
  });
}

function normalizeMediaList(payload) {
  if (!payload) return [];
  return Array.isArray(payload) ? payload : [payload];
}

function getMediaUrl(item = {}) {
  return item.sattDir || item.att_dir || item.satt_dir || item.url || item.src || '';
}

function escapeAttr(value) {
  return String(value).replace(/"/g, '&quot;');
}

function insertHtml(html) {
  if (sourceVisible.value) {
    sourceHtml.value = `${sourceHtml.value || ''}${html}`;
    emitContent(sourceHtml.value);
    return;
  }
  if (!editorRef.value) return;
  restoreEditorSelection();
  editorRef.value.dangerouslyInsertHtml(html);
  syncEditorValue();
}

function insertVideo(src) {
  const videoHtml = `<video src="${escapeAttr(src)}" controls style="max-width:100%;"></video><p><br></p>`;
  if (sourceVisible.value) {
    insertHtml(videoHtml);
    return;
  }
  const editor = editorRef.value;
  if (!editor) return;
  restoreEditorSelection();
  editor.insertNode({
    type: 'video',
    src,
    children: [{ text: '' }],
  });
  syncEditorValue();
}

function restoreEditorSelection() {
  const editor = editorRef.value;
  if (!editor) return;
  if (!editor.selection && typeof editor.restoreSelection === 'function') editor.restoreSelection();
  if (!editor.selection && typeof editor.focus === 'function') editor.focus(true);
}

function syncEditorValue() {
  if (!editorRef.value) return;
  const currentHtml = editorRef.value.getHtml();
  valueHtml.value = currentHtml;
  sourceHtml.value = currentHtml;
  emitContent(currentHtml);
}

function updateDisabledState() {
  if (!editorRef.value) return;
  if (props.disabled && typeof editorRef.value.disable === 'function') {
    editorRef.value.disable();
  } else if (!props.disabled && typeof editorRef.value.enable === 'function') {
    editorRef.value.enable();
  }
}

function setContent(value) {
  setEditorHtml(value, true);
}

function getContent() {
  if (sourceVisible.value) return sourceHtml.value;
  if (editorRef.value) return editorRef.value.getHtml();
  return valueHtml.value;
}

function focus() {
  if (editorRef.value && typeof editorRef.value.focus === 'function') editorRef.value.focus();
}

function blur() {
  if (editorRef.value && typeof editorRef.value.blur === 'function') editorRef.value.blur();
}

watch(
  () => [props.modelValue, props.value, props.content],
  () => {
    const nextHtml = normalizeHtml(getExternalValue());
    if (nextHtml !== valueHtml.value || nextHtml !== sourceHtml.value) {
      setEditorHtml(nextHtml);
    }
  },
);

watch(
  () => props.disabled,
  () => {
    updateDisabledState();
  },
);

defineExpose({ setContent, getContent, focus, blur });

onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor) {
    editorActions.delete(editor);
    editor.destroy();
  }
  editorRef.value = null;
});
</script>

<style scoped lang="scss">
.wang-editor-shell {
  width: 100%;
  line-height: normal;
}

.wang-editor {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  background: #fff;
  display: flex;
  flex-direction: column;
}

.wang-editor-toolbar {
  border-bottom: 1px solid #dcdfe6;
}

.wang-editor-content {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.wang-editor-content :deep(.w-e-text-container) {
  min-height: 100%;
}

.wang-editor-content :deep(video) {
  max-width: 100%;
}

.wang-source {
  flex: 1;
  min-height: 0;
  padding: 0;
  background: #fff;
}

.wang-source-input {
  height: 100%;
}

.wang-source-input :deep(.el-textarea__inner) {
  height: 100% !important;
  min-height: 100% !important;
  resize: none;
  border: 0;
  border-radius: 0;
  font-family: Menlo, Consolas, monospace;
  line-height: 1.6;
}

.is-disabled {
  opacity: 0.75;
}
</style>
