<template>
  <div>
    <iframe
      ref="iframesRef"
      src="https://api.crmeb.com/"
      width="100%"
      :height="iframeHeight"
      style="border: none"
    ></iframe>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, getCurrentInstance, nextTick } from 'vue';

defineOptions({ name: 'SmsConfig' });

const { proxy } = getCurrentInstance();

const iframeHeight = ref(0);

function handleResize() {
  iframeHeight.value = proxy.$selfUtil.getTableHeight(0);
}

window.addEventListener('resize', handleResize);

onMounted(() => {
  nextTick(() => {
    iframeHeight.value = proxy.$selfUtil.getTableHeight(0);
  });
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
});
</script>
