<template>
  <el-aside class="layout-aside" :class="setCollapseWidth" v-if="clientWidth > 1000">
    <Logo v-if="setShowLogo && menuList.length && getThemeConfig.layout !== 'columns'" />
    <div v-if="menuList.length && getThemeConfig.layout == 'columns'" class="cat-name">
      {{ adminTitle || catName }}
    </div>
    <el-scrollbar class="flex-auto" ref="layoutAsideRef">
      <Vertical :menuList="menuList" :class="setCollapseWidth" />
    </el-scrollbar>
  </el-aside>
  <el-drawer v-model:visible="getThemeConfig.isCollapse" :with-header="false" direction="ltr" size="180px" v-else>
    <el-aside class="layout-aside w100 h100">
      <Logo v-if="setShowLogo && menuList.length" />
      <el-scrollbar class="flex-auto" ref="layoutAsideRef">
        <Vertical :menuList="menuList" />
      </el-scrollbar>
    </el-aside>
  </el-drawer>
</template>

<script setup>
import { ref, computed, onBeforeUnmount, onUnmounted, nextTick } from 'vue';
import Vertical from '@/layout/navMenu/vertical.vue';
import Logo from '@/layout/logo/index.vue';
import bus from '@/utils/bus';
import { useMenuStore } from '@/store/modules/menu';
import { useUserStore } from '@/store/modules/user';
import { useThemeConfigStore } from '@/store/modules/themeConfig';

defineOptions({ name: 'layoutAside' });

const emit = defineEmits(['routesListChange']);

const menuStore = useMenuStore();
const userStore = useUserStore();
const themeConfigStore = useThemeConfigStore();

const clientWidth = ref('');
const catName = ref('');
const layoutAsideRef = ref(null);

const adminTitle = computed(() => menuStore.adminTitle || '');

// 设置左侧菜单的具体宽度
const menuList = computed(() => {
  userStore.childMenuList.length > 0
    ? (themeConfigStore.themeConfig.isCollapse = false)
    : (themeConfigStore.themeConfig.isCollapse = true);
  return userStore.childMenuList;
});

const setCollapseWidth = computed(() => {
  let { layout, isCollapse } = themeConfigStore.themeConfig;
  let asideBrColor = '';
  layout === 'classic' || layout === 'columns' ? (asideBrColor = 'layout-el-aside-br-color') : '';

  if (layout === 'columns') {
    // 分栏布局，菜单收起时宽度给 1px / 暂为0px
    if (isCollapse) {
      return ['layout-aside-width1', asideBrColor];
    } else {
      return ['layout-aside-width-default', asideBrColor];
    }
  } else {
    // 其它布局给 64px
    if (isCollapse) {
      return ['layout-aside-width1', asideBrColor];
    } else {
      return ['layout-aside-width-default', asideBrColor, layout === 'classic' ? 'pt8' : ''];
    }
  }
});

// 设置 logo 是否显示
const setShowLogo = computed(() => {
  let { layout, isShowLogo } = themeConfigStore.themeConfig;
  return (isShowLogo && layout === 'defaults') || (isShowLogo && layout === 'columns');
});

// 获取布局配置信息
const getThemeConfig = computed(() => themeConfigStore.themeConfig);

function getMenus() {
  userStore.getMenus().then(() => {
    setFilterRoutes();
  });
}

// 设置/过滤路由（非静态路由/是否显示在菜单中）
function setFilterRoutes() {
  if (themeConfigStore.themeConfig.layout === 'columns') return false;
  userStore.setChildMenuList(filterRoutesFun(userStore.menuList));
}

// 设置/过滤路由 递归函数
function filterRoutesFun(arr) {
  return arr
    .filter((item) => item.path)
    .map((item) => {
      item = Object.assign({}, item);
      if (item.children) item.children = filterRoutesFun(item.children);

      return item;
    });
}

// 设置菜单导航是否固定（移动端）
function initMenuFixed(clientWidthVal) {
  clientWidth.value = clientWidthVal;
  emit('routesListChange');
}

// 菜单滚动条监听
function onLayoutMobileResize(res) {
  initMenuFixed(res.clientWidth);
}

function onOneCatName(name) {
  catName.value = name;
}

function onUpdateElScrollBar() {
  setTimeout(() => {
    layoutAsideRef.value.update();
  }, 300);
}

function onRoutesListChange() {
  setFilterRoutes();
}

// created
initMenuFixed(document.body.clientWidth);
getMenus();
bus.on('layoutMobileResize', onLayoutMobileResize);
bus.on('oneCatName', onOneCatName);
bus.on('updateElScrollBar', onUpdateElScrollBar);
if (themeConfigStore.themeConfig.layout !== 'columns') {
  bus.on('routesListChange', onRoutesListChange);
}

onBeforeUnmount(() => {
  bus.off('routesListChange', onRoutesListChange);
});

// 页面销毁时，取消菜单滚动条监听
onUnmounted(() => {
  bus.off('updateElScrollBar', onUpdateElScrollBar);
});
</script>
<style lang="scss" scoped>
.cat-name {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  border-bottom: 1px solid var(--prev-border-color-lighter);
  font-weight: 500;
  font-size: 15px;
}
</style>
