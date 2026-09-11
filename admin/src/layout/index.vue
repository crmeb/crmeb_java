<template>
  <!-- 根据头部菜单是否显示来判断显示哪个组件 -->
  <Mains v-if="headMenuNoShow" />
  <!-- 根据主题配置中的布局类型来判断显示哪个组件 -->
  <Classic v-else-if="getThemeConfig.layout === 'classic'" />
  <Defaults v-else-if="getThemeConfig.layout === 'defaults'" />
  <Transverse v-else-if="getThemeConfig.layout === 'transverse'" />
  <Columns v-else-if="getThemeConfig.layout === 'columns'" />
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, defineAsyncComponent } from 'vue';
import { useRoute } from 'vue-router';
import { Local } from '@/utils/storage.js';
import { getNewTagList } from '@/utils/util';
import { useThemeConfigStore } from '@/store/modules/themeConfig';
import { useMenuStore } from '@/store/modules/menu';
import { useUserStore } from '@/store/modules/user';
import bus from '@/utils/bus';

defineOptions({ name: 'layout' });

const route = useRoute();
const themeConfigStore = useThemeConfigStore();
const menuStore = useMenuStore();
const userStore = useUserStore();

const Defaults = defineAsyncComponent(() => import('@/layout/main/defaults.vue'));
const Classic = defineAsyncComponent(() => import('@/layout/main/classic.vue'));
const Transverse = defineAsyncComponent(() => import('@/layout/main/transverse.vue'));
const Columns = defineAsyncComponent(() => import('@/layout/main/columns.vue'));
const Mains = defineAsyncComponent(() => import('@/layout/component/main.vue'));

const headMenuNoShow = ref(false);

// 获取布局配置信息
const getThemeConfig = computed(() => themeConfigStore.themeConfig);
const tagNavList = computed(() => menuStore.tagNavList);
const routesList = computed(() => userStore.menuList);

watch(route, (newRoute) => {
  headMenuNoShow.value = route.meta.fullScreen;
  const { name, query, params, meta, path } = newRoute;
  menuStore.addTag({
    route: { name, query, params, meta, path },
    type: 'push',
  });
  menuStore.setBreadCrumb(newRoute);
  menuStore.setTagNavList(getNewTagList(tagNavList.value, newRoute));
});

// created 时机
headMenuNoShow.value = route.meta.fullScreen;
onLayoutResize();
window.addEventListener('resize', onLayoutResize, { passive: true });

onUnmounted(() => {
  window.removeEventListener('resize', onLayoutResize);
});

// 窗口大小改变时(适配移动端)
function onLayoutResize() {
  if (!Local.get('oldLayout')) Local.set('oldLayout', themeConfigStore.themeConfig.layout);
  const clientWidth = document.body.clientWidth;
  if (clientWidth < 1000) {
    themeConfigStore.themeConfig.isCollapse = false;
    bus.emit('layoutMobileResize', {
      layout: 'defaults',
      clientWidth,
    });
  } else {
    bus.emit('layoutMobileResize', {
      layout: Local.get('oldLayout') ? Local.get('oldLayout') : themeConfigStore.themeConfig.layout,
      clientWidth,
    });
  }
}
</script>
