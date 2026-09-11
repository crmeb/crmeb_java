<template>
  <el-menu :default-active="activeMenu" mode="horizontal" @select="handleSelect">
    <template v-for="(item, index) in topMenus" :key="index">
      <el-menu-item :style="{ '--theme': theme }" :index="item.path" v-if="index < visibleNumber">
        <i :class="'el-icon-' + item.extra" v-if="navIcon" />{{ item.name }}
      </el-menu-item>
    </template>
    <!-- 顶部菜单超出数量折叠 -->
    <el-sub-menu :style="{ '--theme': theme }" index="more" v-if="topMenus.length > visibleNumber">
      <template #title>更多菜单</template>
      <template v-for="(item, index) in topMenus" :key="index">
        <el-menu-item :index="item.path" v-if="index >= visibleNumber">
          <i :class="'el-icon-' + item.extra" v-if="navIcon" />{{ item.name }}
        </el-menu-item>
      </template>
    </el-sub-menu>
  </el-menu>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useSettingsStore } from '@/store/modules/settings';
import { usePermissionStore } from '@/store/modules/permission';

const route = useRoute();
const router = useRouter();
const settingsStore = useSettingsStore();
const permissionStore = usePermissionStore();

// 顶部栏初始数
const visibleNumber = ref(9);
// 当前激活菜单的 index
const currentIndex = ref(undefined);

const theme = computed(() => settingsStore.theme);
const navIcon = computed(() => settingsStore.navIcon);

// 所有的路由信息
const routers = computed(() => {
  let routers = permissionStore.topbarRouters;
  let arr = [],
    obj = {};
  routers.forEach((item) => {
    obj = item;
    obj.path = item.url;
    arr.push(obj);
  });
  return arr;
});

// 顶部显示菜单
const topMenus = computed(() => {
  let topMenus = [];
  routers.value.map((menu) => {
    if (menu.hidden !== true) {
      // 兼容顶部栏一级菜单内部跳转
      if (menu.path === '/') {
        topMenus.push(menu.child[0]);
      } else {
        topMenus.push(menu);
      }
    }
  });
  return topMenus;
});

// 默认激活的菜单
const activeMenu = computed(() => {
  //简单粗暴的办法，获取当前路由截取一级路由赋值给默认选中菜单
  let key = route.path.split('/')[1];
  key = '/' + key;
  return key;
});

// 根据宽度计算设置显示栏数
function setVisibleNumber() {
  const width = document.body.getBoundingClientRect().width / 1.8;
  visibleNumber.value = parseInt(width / 85);
}

// 菜单选择事件
function handleSelect(key, keyPath) {
  currentIndex.value = key;
  if (ishttp(key)) {
    // http(s):// 路径新窗口打开
    window.open(key, '_blank');
  } else if (key.indexOf('/redirect') !== -1) {
    // /redirect 路径内部打开
    router.push({ path: key.replace('/redirect', '') });
  } else {
    // 显示左侧联动菜单
    activeRoutes(key);
  }
}

// 当前激活的路由
function activeRoutes(key) {
  var routes = [];
  routers.value.map((item) => {
    if (key == item.url && item.child) {
      //如果选中导航的key值与遍历项的url匹配并且有子级，那么就将该项的子级数组放在routes中
      routes = item.child;
    } else if (key == item.url && !item.child) {
      //只满足选中导航的key值与遍历项的url匹配但是没有子级的情况下，就把这一项赋值给vuex中
      //这一项其实针对控制台，控制台没有子级
      permissionStore.SET_SIDEBAR_ROUTERS([item]);
    }
  });
  if (routes.length > 0) {
    //routes数组有长度就将它放在vuex中，左侧导航就能读取到，展示的也是选中项的子级
    permissionStore.SET_SIDEBAR_ROUTERS(routes);
  }
  return routes;
}

function ishttp(url) {
  return url.indexOf('http://') !== -1 || url.indexOf('https://') !== -1;
}

onMounted(() => {
  setVisibleNumber();
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', setVisibleNumber);
});

// 原 beforeMount 注册监听，迁移到 setup 顶层立即注册（onBeforeMount 等价）
window.addEventListener('resize', setVisibleNumber);
</script>

<style lang="scss">
.topmenu-container.el-menu--horizontal > .el-menu-item {
  float: left;
  height: 50px !important;
  line-height: 50px !important;
  color: #999093;
  padding: 0 5px !important;
  margin: 0 10px !important;
}

.topmenu-container.el-menu--horizontal > .el-menu-item.is-active,
.el-menu--horizontal > .el-submenu.is-active .el-submenu__title {
  border-bottom: 2px solid #{'var(--theme)'} !important;
  color: var(--theme);
}

/* submenu item */
.topmenu-container.el-menu--horizontal > .el-submenu .el-submenu__title {
  float: left;
  height: 50px !important;
  line-height: 50px !important;
  color: #999093 !important;
  padding: 0 5px !important;
  margin: 0 10px !important;
}
</style>
