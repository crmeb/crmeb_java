<template>
  <div>
    <i class="iconfont iconios-qr-scanner" style="font-size: 20px" @click="click"></i>
    <!--<svg-icon :icon-class="isFullscreen?'exit-fullscreen':'fullscreen'" @click="click" />-->
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import screenfull from 'screenfull';

defineOptions({ name: 'Screenfull' });

const isFullscreen = ref(false);

function click() {
  if (!screenfull.enabled) {
    ElMessage({
      message: 'you browser can not work',
      type: 'warning',
    });
    return false;
  }
  screenfull.toggle();
}

function change() {
  isFullscreen.value = screenfull.isFullscreen;
}

function init() {
  if (screenfull.enabled) {
    screenfull.on('change', change);
  }
}

function destroy() {
  if (screenfull.enabled) {
    screenfull.off('change', change);
  }
}

onMounted(() => {
  init();
});

onBeforeUnmount(() => {
  destroy();
});
</script>

<style scoped>
.screenfull-svg {
  display: inline-block;
  cursor: pointer;
  fill: #5a5e66;
  width: 20px;
  height: 20px;
  vertical-align: 10px;
}
</style>
