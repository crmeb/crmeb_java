<template>
  <div class="layout-columns-tra-aside el-menu-horizontal-warp">
    <el-scrollbar ref="elMenuHorizontalScrollRef" @wheel.prevent="onElMenuHorizontalScroll">
      <ul>
        <li
          v-for="(v, k) in columnsAsideList"
          :key="k"
          @click="onColumnsAsideMenuClick(v)"
          ref="columnsAsideOffsetLeftRefs"
          class="layout-columns"
          :class="{ 'layout-columns-active': v.k === liIndex }"
          :title="v.title"
        >
          <div :class="setColumnsAsidelayout">
            <div class="font12">
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
import { useUserStore } from '@/store/modules/user';
import { useThemeConfigStore } from '@/store/modules/themeConfig';

defineOptions({ name: 'layoutColumnsAside' });

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const themeConfigStore = useThemeConfigStore();

const columnsAsideList = ref([]);
const liIndex = ref(0);
const difference = ref(0);
const routeSplit = ref([]);
const activePath = ref('');

const elMenuHorizontalScrollRef = ref(null);
const columnsAsideOffsetLeftRefs = ref(null);
const columnsAsideActiveRef = ref(null);

// 设置分栏高亮风格
const setColumnsAsideStyle = computed(() => themeConfigStore.themeConfig.columnsAsideStyle);
// 设置分栏布局风格
const setColumnsAsidelayout = computed(() => themeConfigStore.themeConfig.columnsAsideLayout);
const Layout = computed(() => themeConfigStore.themeConfig.Layout);

const routesList = computed(() => {
  userStore.menuList;
});

// 设置横向滚动条可以鼠标滚轮滚动
function onElMenuHorizontalScroll(e) {
  const eventDelta = e.wheelDelta || -e.deltaY * 40;
  elMenuHorizontalScrollRef.value.wrapRef.scrollLeft =
    elMenuHorizontalScrollRef.value.wrapRef.scrollLeft + eventDelta / 4;
}

// 初始化数据，页面刷新时，滚动条滚动到对应位置
function initElMenuOffsetLeft() {
  nextTick(() => {
    let els = document.querySelector('.layout-columns.layout-columns-active');
    if (!els) return false;
    elMenuHorizontalScrollRef.value.wrapRef.scrollLeft = els.offsetLeft;
  });
}

// 设置菜单高亮位置移动
function setColumnsAsideMove(k) {
  if (k === undefined) return false;
  const els = columnsAsideOffsetLeftRefs.value;
  liIndex.value = k;
  columnsAsideActiveRef.value.style.left = `${els[k].offsetLeft + difference.value}px`;
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
  // bus.emit('setSendColumnsChildren', getMenuSider(columnsAsideList.value, path));
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
  //   const resData = getHeaderName(route.path, columnsAsideList.value);
  const resData = setSendChildren(getHeaderName(route, columnsAsideList.value));
  if (!resData && !resData.item[0].children.length) {
    bus.emit('setSendColumnsChildren', []);
    userStore.setChildMenuList([]);

    themeConfigStore.themeConfig.isCollapse = true;
    return false;
  }
  if (!resData) return;
  bus.emit('oneCatName', resData.item[0].title);
  onColumnsAsideDown(resData.item[0].k);
  // 刷新时，初始化一个路由设置自动收起菜单
  resData.item[0].children.length > 0
    ? (themeConfigStore.themeConfig.isCollapse = false)
    : (themeConfigStore.themeConfig.isCollapse = true);
  bus.emit('setSendColumnsChildren', resData.item[0].children || []);
  userStore.setChildMenuList(resData.item[0].children || []);
}

// 传送当前子级数据到菜单中
function setSendChildren(path) {
  // const currentPathSplit = path.split('/');
  let currentData = {};
  columnsAsideList.value.map((v, k) => {
    v['k'] = k;
    if (v.path === path) {
      currentData['item'] = [{ ...v }];
      //   currentData['children'] = [{ ...v }];
      if (v.children.length) currentData['children'] = v.children;
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
  // routeSplit.value = path.split('/');
  // routeSplit.value.shift();
  // const routeFirst = `/${routeSplit.value[0]}`;
  const currentSplitRoute = columnsAsideList.value.find((v) => v.path === path);
  if (!currentSplitRoute) {
    onColumnsAsideDown(0);
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
    // setColumnsAsideMove();
    let HeadName = getHeaderName(to, columnsAsideList.value);
    let asideList = getMenuSider(columnsAsideList.value, HeadName)[0].children;
    const resData = setSendChildren(HeadName);
    if (resData && resData.item && resData.item.length) {
      onColumnsAsideDown(resData.item[0].k);
      bus.emit('oneCatName', resData.item[0].title);
    }

    bus.emit('setSendColumnsChildren', asideList || []);
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
  nextTick(() => {
    initElMenuOffsetLeft();
  });
});
</script>

<style scoped lang="scss">
:deep(.el-scrollbar__bar.is-horizontal) {
  height: 0;
}
.el-menu-horizontal-warp {
  :deep(.el-scrollbar__bar.is-vertical) {
    display: none;
  }
  :deep(.el-scrollbar__wrap) {
    overflow-y: hidden !important;
    overflow-x: scroll !important;
  }
  :deep(.a) {
    width: 100%;
  }
  .el-menu.el-menu--horizontal {
    display: flex;
    height: 100%;
    width: 100%;
    box-sizing: border-box;
  }
}

.layout-columns-tra-aside {
  height: 100%;
  background: var(--prev-bg-columnsMenuBar);
  // box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  overflow-y: hidden;
  // flex: 1;
  ul {
    position: relative;
    display: flex;
    li {
      color: var(--prev-bg-columnsMenuBarColor);
      width: 70px;
      height: 50px;
      text-align: center;
      display: flex;
      cursor: pointer;
      position: relative;
      z-index: 1;
      .columns-vertical {
        margin: auto;
        // .columns-vertical-title {
        //   padding-top: 1px;
        // }
      }
      .columns-horizontal {
        display: flex;
        height: 50px;
        width: 70px;
        align-items: center;
        justify-content: center;
        padding: 0 5px;
        i {
          margin-right: 5px;
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
    //   background: var(--prev-bg-menu-hover-ba-color);
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
      position: absolute;
      left: 0;
      height: 40px;
      width: 70px;
      margin-top: 5px;
      transform: translatey(0%);
      z-index: 0;
      transition: 0.2s ease-in-out;
      border-radius: 3px;
    }
    .columns-card {
      @extend .columns-round;
      top: 0;
      height: 50px;
      width: 70px;
      border-radius: 0;
      margin-top: 0px;
    }
  }
}
:deep(.el-scrollbar) {
  height: 50px;
}
:deep(.el-scrollbar__bar.is-horizontal) {
  display: none;
}
:deep(.el-scrollbar__thumb) {
  display: none;
}
</style>
