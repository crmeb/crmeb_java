<template>
  <div class="user-editor">
    <diy-index ref="diy" page-type="user" />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import DiyIndex from '@/views/design/theme_editor/devise/diyIndex.vue';

defineOptions({ name: 'UserEditor' });

const diy = ref(null);

function preview() {
  diy.value && diy.value.preview && diy.value.preview();
}
function saveOnly() {
  diy.value && diy.value.saveConfig && diy.value.saveConfig(1);
}
function saveAndClose() {
  diy.value && diy.value.saveConfig && diy.value.saveConfig(2);
}
function saveTemplate() {
  if (diy.value) diy.value.nameModal = true;
}

defineExpose({ preview, saveOnly, saveAndClose, saveTemplate });
</script>

<style lang="scss" scoped>
.user-editor {
  height: 100%;
}
:deep(.i-layout-page-header.header-title) {
  display: none;
}
</style>
