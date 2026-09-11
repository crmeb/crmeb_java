<template>
  <div>
    <el-dialog
      title="上传图片"
      v-model="visible"
      width="1024px"
      :modal="true"
      append-to-body
      :show-close="false"
      :before-close="handleClose"
    >
      <el-button
        class="selfDialogClose"
        link
        :icon="Close"
        circle
        @click="handleClose"
        size="default"
      ></el-button>
      <upload-index v-if="visible" :isMore="isMore" :modelName="modelName" @getImage="getImage" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Close } from '@element-plus/icons-vue';
import UploadIndex from '@/components/uploadPicture/index.vue';

defineOptions({ name: 'UploadFroms' });

const visible = ref(false);
const callback = ref(function () {});
const isMore = ref('');
const modelName = ref('');
const ISmodal = ref(false);
const booleanVal = ref(true);

function handleClose() {
  visible.value = false;
}

function getImage(img) {
  callback.value(img);
  visible.value = false;
}

function open(options = {}) {
  callback.value = typeof options.callback === 'function' ? options.callback : function () {};
  isMore.value = options.isMore;
  modelName.value = options.modelName;
  booleanVal.value = options.booleanVal ?? true;
  visible.value = true;
}

defineExpose({
  open,
  visible,
  callback,
  isMore,
  modelName,
  booleanVal,
});
</script>

<style lang="scss" scoped>
/* 统一组件中的特殊组件 */
:deep(.el-dialog__header) {
  display: none !important;
}
.selfDialogClose {
  display: inline-block;
  position: absolute;
  right: 0;
  top: 3px;
  pointer-events: auto;
  z-index: 999;
  font-size: 16px;
  color: #363f4d;
  width: auto;
}
</style>
