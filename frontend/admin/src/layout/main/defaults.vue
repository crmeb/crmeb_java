<template>
  <el-container class="layout-container">
    <Asides />
    <el-container class="flex-center layout-backtop">
      <Headers v-if="isFixedHeader" />
      <el-scrollbar ref="layoutDefaultsScrollbarRef">
        <Headers v-if="!isFixedHeader" />
        <Mains />
      </el-scrollbar>
    </el-container>
    <el-backtop target=".layout-backtop .el-scrollbar__wrap"></el-backtop>
  </el-container>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import Asides from '@/layout/component/aside.vue';
import Headers from '@/layout/component/header.vue';
import Mains from '@/layout/component/main.vue';
import { useThemeConfigStore } from '@/store/modules/themeConfig';

defineOptions({ name: 'layoutDefaults' });

const route = useRoute();
const themeConfigStore = useThemeConfigStore();

const layoutDefaultsScrollbarRef = ref(null);

// 是否开启固定 header
const isFixedHeader = computed(() => themeConfigStore.themeConfig.isFixedHeader);

// 监听路由的变化
watch(
  () => route,
  () => {
    if (layoutDefaultsScrollbarRef.value && layoutDefaultsScrollbarRef.value.wrapRef) {
      layoutDefaultsScrollbarRef.value.wrapRef.scrollTop = 0;
    }
  },
  { deep: true }
);
</script>
