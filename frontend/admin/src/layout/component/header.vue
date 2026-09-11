<template>
  <el-header class="layout-header" :height="setHeaderHeight">
    <NavBarsIndex />
  </el-header>
</template>

<script setup>
import { computed } from 'vue';
import NavBarsIndex from '@/layout/navBars/index.vue';
import { useThemeConfigStore } from '@/store/modules/themeConfig';

defineOptions({ name: 'layoutHeader' });

const themeConfigStore = useThemeConfigStore();

// 设置顶部 header 的具体高度
const setHeaderHeight = computed(() => {
  let { isTagsview, layout } = themeConfigStore.themeConfig;
  if (isTagsview && layout !== 'classic') return '84px';
  else return '50px';
});
</script>
