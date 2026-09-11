<template>
  <div class="components-container">
    <config-list :prent-data="prentData" />
  </div>
</template>

<script setup>
import configList from '@/components/FormGenerator/index/Home.vue';

defineOptions({});

const props = defineProps({
  prentData: {
    type: Object,
    default: () => ({}),
  },
});
</script>

<style scoped></style>
