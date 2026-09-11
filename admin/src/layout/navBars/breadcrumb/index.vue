<template>
  <div class="layout-navbars-breadcrumb-index">
    <Logo v-if="setIsShowLogo" />
    <Breadcrumb />
    <Horizontal :menuList="menuList" v-if="isLayoutTransverse" />
    <transverseAside v-if="isLayoutClassic" />
    <User />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue';
import Breadcrumb from '@/layout/navBars/breadcrumb/breadcrumb.vue';
import User from '@/layout/navBars/breadcrumb/user.vue';
import Logo from '@/layout/logo/index.vue';
import Horizontal from '@/layout/navMenu/horizontal.vue';
import transverseAside from '@/layout/component/transverseAside.vue';
import { useThemeConfigStore } from '@/store/modules/themeConfig';
import { useUserStore } from '@/store/modules/user';
import bus from '@/utils/bus';

defineOptions({ name: 'layoutNavBars' });

const themeConfigStore = useThemeConfigStore();
const userStore = useUserStore();

const menuList = ref([]);

// 设置 logo 是否显示
const setIsShowLogo = computed(() => {
  let { isShowLogo, layout } = themeConfigStore.themeConfig;
  return (isShowLogo && layout === 'classic') || (isShowLogo && layout === 'transverse');
});
// 设置是否显示横向菜单
const isLayoutTransverse = computed(() => {
  let { layout, isClassicSplitMenu } = themeConfigStore.themeConfig;
  return layout === 'transverse' || (isClassicSplitMenu && layout === 'classic');
});
const isLayoutClassic = computed(() => {
  let { layout } = themeConfigStore.themeConfig;
  return layout === 'classic';
});

// 设置路由的过滤
function setFilterRoutes() {
  menuList.value = filterRoutesFun(userStore.menuList);
}
// 设置路由的过滤递归函数
function filterRoutesFun(arr) {
  return arr
    .filter((item) => item.path)
    .map((item) => {
      item = Object.assign({}, item);
      if (item.children.length) item.children = filterRoutesFun(item.children);
      return item;
    });
}

onMounted(() => {
  setFilterRoutes();
  bus.on('routesListChange', onRoutesListChange);
});

onBeforeUnmount(() => {
  bus.off('routesListChange', onRoutesListChange);
});

function onRoutesListChange() {
  setFilterRoutes();
}

// 监听 store 数据变化
watch(
  () => themeConfigStore.themeConfig,
  (val) => {
    if (userStore.menuList.length === menuList.value.length) return false;
    setFilterRoutes();
  },
  { deep: true },
);
</script>

<style scoped lang="scss">
.layout-navbars-breadcrumb-index {
  height: 50px;
  display: flex;
  align-items: center;
  background: var(--prev-bg-topBar);
  border-bottom: 1px solid var(--prev-border-color-lighter);
  padding-left: 15px;
}
</style>
