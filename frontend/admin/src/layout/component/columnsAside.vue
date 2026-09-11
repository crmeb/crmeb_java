<template>
  <div class="layout-columns-aside">
    <el-scrollbar>
      <Logo />
      <ul>
        <li
          v-for="(v, k) in columnsAsideList"
          :key="k"
          @click="onColumnsAsideMenuClick(v)"
          ref="columnsAsideOffsetTopRefs"
          class="layout-columns"
          :class="{ 'layout-columns-active': v.k === liIndex }"
          :title="v.title"
        >
          <div :class="setColumnsAsidelayout">
            <i :class="'el-icon-' + v.icon"></i>
            <div class="font14">
              {{
                v.title && v.title.length >= 4
                  ? v.title.substr(0, setColumnsAsidelayout === 'columns-vertical' ? 4 : 3)
                    : v.title
              }}
            </div>
          </div>
        </li>
        <div ref="columnsAsideActiveRef" :class="setColumnsAsideStyle"></div>
      </ul>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getMenuSider, getHeaderName, findFirstNonNullChildren } from '@/utils/system.js';
import Logo from '@/layout/logo/index.vue';
import bus from '@/utils/bus';
import { useMenuStore } from '@/store/modules/menu';
import { useUserStore } from '@/store/modules/user';
import { useThemeConfigStore } from '@/store/modules/themeConfig';

defineOptions({ name: 'layoutColumnsAside' });

const route = useRoute();
const router = useRouter();
const menuStore = useMenuStore();
const userStore = useUserStore();
const themeConfigStore = useThemeConfigStore();

const columnsAsideList = ref([]);
const liIndex = ref(0);
const difference = ref(0);
const routeSplit = ref([]);

const columnsAsideOffsetTopRefs = ref(null);
const columnsAsideActiveRef = ref(null);

// 设置分栏高亮风格
const setColumnsAsideStyle = computed(() => themeConfigStore.themeConfig.columnsAsideStyle);
// 设置分栏布局风格
const setColumnsAsidelayout = computed(() => themeConfigStore.themeConfig.columnsAsideLayout);
const Layout = computed(() => themeConfigStore.themeConfig.Layout);

const routesList = computed(() => userStore.menuList);

const activePath = computed(() => menuStore.activePath);

// 设置菜单高亮位置移动
function setColumnsAsideMove(k) {
  if (k === undefined) return false;
  const els = columnsAsideOffsetTopRefs.value;
  liIndex.value = k;
  columnsAsideActiveRef.value.style.top = `${els[k].offsetTop + difference.value}px`;
}

// 菜单高亮点击事件
function onColumnsAsideMenuClick(v) {
  let { path } = v;
  if (Array.isArray(v.children) && v.children.length) {
    const firstRoute = findFirstNonNullChildren(v.children, path);
    if (firstRoute && firstRoute.path) router.push(firstRoute.path);
  } else {
    router.push(path);
  }
  // 一个路由设置自动收起菜单
  if (!v.children || v.children.length <= 1) themeConfigStore.themeConfig.isCollapse = true;
  else if (v.children.length > 1) themeConfigStore.themeConfig.isCollapse = false;
}

// 设置高亮动态位置
function onColumnsAsideDown(k) {
  nextTick(() => {
    setColumnsAsideMove(k);
  });
}

// 设置/过滤路由（非静态路由/是否显示在菜单中）
function setFilterRoutes() {
  if (userStore.menuList.length <= 0) return false;
  columnsAsideList.value = filterRoutesFun(userStore.menuList);
  const resData = setSendChildren(getHeaderName(route, columnsAsideList.value));
  if (!resData.children) {
    bus.emit('setSendColumnsChildren', []);
    userStore.setChildMenuList([]);

    themeConfigStore.themeConfig.isCollapse = true;
    return false;
  }
  bus.emit('oneCatName', resData.item[0].title);
  onColumnsAsideDown(resData.item[0].k);
  // 刷新时，初始化一个路由设置自动收起菜单
  resData.children.length > 0
    ? (themeConfigStore.themeConfig.isCollapse = false)
    : (themeConfigStore.themeConfig.isCollapse = true);
  bus.emit('setSendColumnsChildren', resData.children || []);
  userStore.setChildMenuList(resData.children || []);
}

// 传送当前子级数据到菜单中
function setSendChildren(path) {
  let currentData = {};
  columnsAsideList.value.map((v, k) => {
    v['k'] = k;
    if (v.path === path) {
      currentData['item'] = [{ ...v }];
      if (v.children) currentData['children'] = v.children;
    }
  });
  return currentData;
}

// 路由过滤递归函数
function filterRoutesFun(arr) {
  return arr
    .filter((item) => item.path)
    .map((item) => {
      item = Object.assign({}, item);
      if (item.children.length) item.children = filterRoutesFun(item.children);
      return item;
    });
}

// tagsView 点击时，根据路由查找下标 columnsAsideList，实现左侧菜单高亮
function setColumnsMenuHighlight(path) {
  const currentSplitRoute = columnsAsideList.value.find((v) => v.path === path);
  if (!currentSplitRoute) {
    // onColumnsAsideDown(0);
    return false;
  }
  // 延迟拿值，防止取不到
  setTimeout(() => {
    onColumnsAsideDown(currentSplitRoute.k);
  }, 0);
}

function onRoutesListChange() {
  setFilterRoutes();
}

// 监听 vuex 数据变化
watch(
  () => [themeConfigStore.themeConfig, userStore.menuList],
  (val) => {
    const [themeConfig, menuList] = val;
    themeConfig.columnsAsideStyle === 'columnsRound' ? (difference.value = 3) : (difference.value = 0);
    if (menuList.length === columnsAsideList.value.length) return false;
  },
  { deep: true }
);

// 监听路由的变化
watch(
  route,
  (to) => {
    setColumnsMenuHighlight(to.path);
    let HeadName = getHeaderName(to, columnsAsideList.value);
    let asideList = getMenuSider(columnsAsideList.value, HeadName)[0].children;
    const resData = setSendChildren(HeadName);
    if (resData.item) {
      onColumnsAsideDown(resData.item[0].k);
      bus.emit('oneCatName', resData.item[0].title);
    } else {
      onColumnsAsideDown(0);
    }
    userStore.setChildMenuList(asideList || []);
  },
  { deep: true }
);

onBeforeUnmount(() => {
  bus.off('routesListChange', onRoutesListChange);
});

onMounted(() => {
  bus.on('routesListChange', onRoutesListChange);
  setFilterRoutes();
});
</script>

<style scoped lang="scss">
.layout-columns-aside {
  width: 80px;
  height: 100%;
  background: var(--prev-bg-columnsMenuBar);
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  border-right: 1px solid var(--prev-border-color-lighter);
  z-index: 20;
  ul {
    position: relative;
    li {
      color: var(--prev-bg-columnsMenuBarColor);
      width: 100%;
      height: 50px;
      text-align: center;
      display: flex;
      cursor: pointer;
      position: relative;
      z-index: 1;
      .columns-vertical {
        margin: auto;
        .columns-vertical-title {
          padding-top: 1px;
        }
      }
      .columns-horizontal {
        display: flex;
        height: 50px;
        width: 100%;
        align-items: center;
        justify-content: center;
        padding: 0 5px;
        i {
          margin-right: 3px;
        }
        a {
          display: flex;
          .columns-horizontal-title {
            padding-top: 1px;
          }
        }
      }
      a {
        text-decoration: none;
        color: var(--prev-bg-columnsMenuBarColor);
      }
    }
    // li:hover {
    //   background: var(--prev-color-primary);
    //   color: var(--prev-bg-columnsMenuBarColor);
    // }
    .layout-columns {
      transition: 0.3s ease-in-out;
    }
    .layout-columns-active,
    .layout-columns-active a {
      color: var(--prev-bg-columnsMenuActiveColor);
      transition: 0.3s ease-in-out;
    }

    .columns-round {
      background: var(--prev-color-primary);
      // color: var(--prev-color-text-white);
      position: absolute;
      left: 50%;
      top: 2px;
      height: 50px;
      width: 65px;
      transform: translateX(-50%);
      z-index: 0;
      transition: 0.3s ease-in-out;
      border-radius: 5px;
    }
    .columns-card {
      @extend .columns-round;
      top: 0;
      height: 50px;
      width: 100%;
      border-radius: 0;
    }
  }
}
</style>
