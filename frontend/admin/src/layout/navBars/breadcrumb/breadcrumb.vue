<template>
  <div class="layout-navbars-breadcrumb">
    <i
      v-if="collapseShow"
      class="layout-navbars-breadcrumb-icon"
      :class="getThemeConfig.isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"
      @click="onThemeConfigChange"
    ></i>
    <el-breadcrumb class="layout-navbars-breadcrumb-hide" v-if="isShowcrumb" :style="{ display: isShowBreadcrumb }">
      <transition-group name="breadcrumb">
        <el-breadcrumb-item v-for="(v, k) in [...breadCrumbList, ...crumbPast]" :key="v.id">
          <span v-if="k == 1" class="layout-navbars-breadcrumb-span">
            <i
              :class="`el-icon-${v.icon}`"
              class="ivu-icon layout-navbars-breadcrumb-iconfont"
              v-if="getThemeConfig.isBreadcrumbIcon"
            />{{ v.title }}
          </span>
          <a v-else @click.prevent="onBreadcrumbClick(v)">
            <i
              :class="`el-icon-${v.icon}`"
              class="ivu-icon layout-navbars-breadcrumb-iconfont"
              v-if="getThemeConfig.isBreadcrumbIcon"
            />{{ v.title }}
          </a>
        </el-breadcrumb-item>
      </transition-group>
    </el-breadcrumb>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Local } from '@/utils/storage.js';
import { R, getMenuopen } from '@/utils/util';
import { useThemeConfigStore } from '@/store/modules/themeConfig';
import { useUserStore } from '@/store/modules/user';

defineOptions({ name: 'layoutBreadcrumb' });

const route = useRoute();
const router = useRouter();
const themeConfigStore = useThemeConfigStore();
const userStore = useUserStore();

const breadcrumbList = ref([]);
const routeSplit = ref([]);
const routeSplitFirst = ref('');
const routeSplitIndex = ref(1);

const breadCrumbList = computed(() => {
  let menuList = userStore.menuList;
  let openMenus = getMenuopen(route, menuList);
  let allMenuList = R(menuList, []);
  let selectMenu = [];
  if (allMenuList.length > 0) {
    openMenus.forEach((i) => {
      allMenuList.forEach((a) => {
        if (i === a.path) {
          selectMenu.push(a);
        }
      });
    });
  }
  return selectMenu;
});

const crumbPast = computed(() => {
  let menuList = userStore.menuList;
  let allMenuList = R(menuList, []);
  let selectMenu = [];
  if (allMenuList.length > 0) {
    allMenuList.forEach((a) => {
      if (route.path === a.path) {
        selectMenu.push(a);
      }
    });
  }
  return selectMenu;
});

// 获取布局配置信息
const getThemeConfig = computed(() => {
  return themeConfigStore.themeConfig;
});

// 动态设置经典、横向布局不显示
const isShowBreadcrumb = computed(() => {
  const { layout, isBreadcrumb } = themeConfigStore.themeConfig;
  if (layout === 'transverse' || layout === 'classic') {
    return 'none';
  } else {
    return isBreadcrumb ? '' : 'none';
  }
});

const isShowcrumb = computed(() => {
  const { layout } = themeConfigStore.themeConfig;
  if (layout === 'transverse' || layout === 'classic') {
    return false;
  } else {
    return true;
  }
});

const collapseShow = computed(() => {
  return ['defaults', 'columns'].includes(themeConfigStore.themeConfig.layout);
});

onMounted(() => {
  initRouteSplit(route.path);
});

// breadcrumb 当前项点击时
function onBreadcrumbClick(v) {
  console.log(v);

  const { redirect, path } = v;
  router.push(path);
}

// breadcrumb icon 点击菜单展开与收起
function onThemeConfigChange() {
  if (
    themeConfigStore.themeConfig.layout == 'columns' &&
    !userStore.childMenuList.length &&
    themeConfigStore.themeConfig.isCollapse
  ) {
    return;
  }
  themeConfigStore.themeConfig.isCollapse = !themeConfigStore.themeConfig.isCollapse;
  setLocalThemeConfig();
}

// 存储布局配置
function setLocalThemeConfig() {
  Local.remove('JavaPlatThemeConfigPrev');
  Local.set('JavaPlatThemeConfigPrev', themeConfigStore.themeConfig);
}

// 递归设置 breadcrumb
function getBreadcrumbList(arr) {
  if (!Array.isArray(arr) || arr.length === 0) return;
  arr.map((item) => {
    routeSplit.value.map((v, k, arrs) => {
      if (routeSplitFirst.value === item.path) {
        routeSplitFirst.value += `/${arrs[routeSplitIndex.value]}`;
        breadcrumbList.value.push(item);
        routeSplitIndex.value++;
        if (item.children) getBreadcrumbList(item.children);
      }
    });
  });
}

// 当前路由分割处理
function initRouteSplit(path) {
  const firstMenu = userStore.menuList[0];

  if (!firstMenu) {
    breadcrumbList.value = [];
    return;
  }

  breadcrumbList.value = [
    {
      path: '/',
      meta: {
        title: firstMenu.title,
        icon: firstMenu.icon,
      },
    },
  ];
  //   routeSplit.value = path.split('/');
  //   routeSplit.value.shift();
  routeSplitFirst.value = path;
  routeSplitIndex.value = 1;
  getBreadcrumbList(userStore.menuList);
}

// 监听路由的变化
watch(
  () => [route.path, userStore.menuList],
  ([path]) => {
    initRouteSplit(path);
    const newVal = route;
    let menuList = userStore.menuList;
    let openMenus = getMenuopen(newVal, menuList);
    let allMenuList = R(menuList, []);
    let selectMenu = [];
    if (allMenuList.length > 0) {
      openMenus.forEach((i) => {
        allMenuList.forEach((a) => {
          if (i === a.path) {
            selectMenu.push(a);
          }
        });
      });
    }
  },
  { deep: true },
);
</script>

<style scoped lang="scss">
.layout-navbars-breadcrumb {
  // flex: 1;
  height: inherit;
  display: flex;
  align-items: center;
  padding-left: 15px;
  .layout-navbars-breadcrumb-icon {
    cursor: pointer;
    font-size: 18px;
    margin-right: 15px;
    color: var(--prev-bg-topBarColor);
    opacity: 0.8;
    &:hover {
      opacity: 1;
    }
  }
  .layout-navbars-breadcrumb-span {
    opacity: 0.7;
    color: var(--prev-bg-topBarColor);
  }
  .layout-navbars-breadcrumb-iconfont {
    font-size: 14px;
    margin-right: 5px;
  }
}
</style>
