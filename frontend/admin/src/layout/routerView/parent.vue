<template>
  <div :class="isTagHistory ? 'h100' : 'h101'">
    <router-view v-slot="{ Component }">
      <keep-alive :include="keepAliveNameList">
        <component :is="Component" :key="refreshRouterViewKey || route.path" />
      </keep-alive>
    </router-view>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, ref } from 'vue';
import { useRoute } from 'vue-router';
import bus from '@/utils/bus';
import { useThemeConfigStore } from '@/store/modules/themeConfig';
import { useMenuStore } from '@/store/modules/menu';

defineOptions({ name: 'parent' });

const route = useRoute();
const themeConfigStore = useThemeConfigStore();
const menuStore = useMenuStore();

const refreshRouterViewKey = ref(null);
const keepAliveNameList = ref([]);
const keepAliveNameNewList = ref([]);

// 设置主界面切换动画
const setTransitionName = computed(() => themeConfigStore.themeConfig.animation);
const isTagHistory = computed(() => themeConfigStore.themeConfig.isTagsview);

// 获取路由缓存列表（name），默认路由全部缓存
function getKeepAliveNames() {
  return menuStore.keepAliveNames;
}

function onTagsViewRefreshRouterView(path) {
  if (route.path !== path) return false;
  keepAliveNameList.value = getKeepAliveNames().filter((name) => route.name !== name);
  refreshRouterViewKey.value = `${route.path}-${Date.now()}`;
  nextTick(() => {
    refreshRouterViewKey.value = null;
    keepAliveNameList.value = getKeepAliveNames();
  });
}

keepAliveNameList.value = getKeepAliveNames();
bus.on('onTagsViewRefreshRouterView', onTagsViewRefreshRouterView);

onBeforeUnmount(() => {
  bus.off('onTagsViewRefreshRouterView', onTagsViewRefreshRouterView);
});
</script>
