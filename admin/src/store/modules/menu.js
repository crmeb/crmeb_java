// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
/**
 * 菜单
 * */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { cloneDeep } from 'lodash';
import {
  getBreadCrumbList,
  setTagNavListInLocalstorage,
  // getMenuByRouter,
  getTagNavListFromLocalstorage,
  getHomeRoute,
  getNextRoute,
  routeHasExist,
  routeEqual,
  getRouteTitleHandled,
  localSave,
  localRead,
} from '@/utils/util';
import router from '@/router';
import { useUserStore } from './user';

// import { includeArray } from '@/utils/system.js';
const homeName = 'Dashboard';
// 根据 menu 配置的权限，过滤菜单
function filterMenu(menuList, access, lastList) {
  menuList.forEach((menu) => {
    let menuAccess = menu.auth;

    if (!menuAccess || includeArray(menuAccess, access)) {
      let newMenu = {};
      for (let i in menu) {
        if (i !== 'children') newMenu[i] = cloneDeep(menu[i]);
      }
      if (menu.children && menu.children.length) newMenu.children = [];

      lastList.push(newMenu);
      menu.children && filterMenu(menu.children, access, newMenu.children);
    }
  });
  return lastList;
}
// 递归处理顶部菜单问题
function getChilden(data) {
  if (data.children) {
    return getChilden(data.children[0]);
  }
  return data.path;
}
// 获取表单页面的标题
function getFormTitle(routerItem) {
  let path = routerItem.path;
  var index = path.lastIndexOf('/');
  path = path.substring(index + 1, path.length);
  switch (path) {
    case 'shop_tabs':
      return '商城设置';
      break;
    case 'pay_tabs':
      return '支付设置';
      break;
    case 'smallapp':
      return '小程序配置';
      break;
    case 'wechat':
      return '公众号配置';
      break;
    case 'wechat_open_app':
      return 'app配置';
      break;
    case 'app_version':
      return 'app升级配置';
      break;
    case 'pc_config':
      return 'pc基础配置';
      break;
    default:
      return routerItem.meta.title;
  }
}

// includeArray 原本从 @/utils/system.js 引入但被注释，这里内联一个等价实现以避免运行时未定义
function includeArray(list, target) {
  if (!target || !target.length) return false;
  return list.some((item) => target.includes(item));
}

export const useMenuStore = defineStore('menu', () => {
  // 顶部菜单
  const header = ref([]);
  // 一级菜单名称
  const oneMenuName = ref('');
  // 侧栏菜单
  const sider = ref([]);
  // 当前顶栏菜单的 name
  const headerName = ref('');
  // 当前所在菜单的 path
  const activePath = ref('');
  // 展开的子菜单 name 集合
  const openNames = ref([]);
  //----------------------------------------------------------------
  const breadCrumbList = ref([]);
  const tagNavList = ref(getTagNavListFromLocalstorage() || []);
  const homeRoute = ref({});
  const local = ref(localRead('local'));
  const errorList = ref([]);
  const adminTitle = ref('');
  const hasReadErrorPage = ref(false);
  //----------------------------------------------------------------
  const keepAliveNames = ref([]);

  // getters
  const filterSider = computed(() => {
    const userInfo = useUserStore();
    // @权限（原代码引用 rootState.user.info，user store 实际无 info 字段，保持原行为返回空过滤结果）
    const access = userInfo.info ? userInfo.info.access : [];
    if (access && access.length) {
      return filterMenu(sider.value, access, []);
    } else {
      return filterMenu(sider.value, [], []);
    }
  });

  const filterHeader = computed(() => {
    // 调用递归函数
    header.value.forEach((item) => {
      item.path = getChilden(item);
    });
    // @权限（原代码引用 rootState.admin.user.info，保持原行为）
    const userStore = useUserStore();
    const access = userStore.info ? userStore.info.access : [];
    if (access && access.length) {
      return header.value.filter((item) => {
        let state = true;
        if (item.auth && !includeArray(item.auth, access)) state = false;
        return state;
      });
    } else {
      return header.value.filter((item) => {
        let state = true;
        if (item.auth && item.auth.length) state = false;
        return state;
      });
    }
  });

  const currentHeader = computed(() => header.value.find((item) => item.name === headerName.value));

  const hideSider = computed(() => {
    let visible = false;
    if (currentHeader.value && 'hideSider' in currentHeader.value) visible = currentHeader.value.hideSider;
    return visible;
  });

  // mutations
  function setSider(menu) {
    sider.value = menu;
  }
  function setOpenMenuName(menu) {
    oneMenuName.value = menu;
  }
  function setHeader(menu) {
    header.value = menu;
  }
  function setHeaderName(name) {
    headerName.value = name;
  }
  function setActivePath(path) {
    activePath.value = path;
  }
  function setOpenNames(names) {
    openNames.value = names;
  }
  function getCacheKeepAlive(data) {
    keepAliveNames.value = data;
  }
  function setBreadCrumb(route) {
    breadCrumbList.value = getBreadCrumbList(route, homeRoute.value);
  }
  function setAdminTitle(title) {
    adminTitle.value = title;
  }
  function setHomeRoute(routes) {
    homeRoute.value = getHomeRoute(routes, homeName);
  }
  function setTagNavList(list) {
    let tagList = [];
    if (list.length) {
      tagList = [...list];
    }
    tagNavList.value = tagList;
    setTagNavListInLocalstorage([...tagList]);
  }
  function closeTag(route) {
    let tag = tagNavList.value.filter((item) => routeEqual(item, route));
    route = tag[0] ? tag[0] : null;
    if (!route) return;
    closePage(route);
  }
  function addTag({ route, type = 'unshift' }) {
    let routerItem = getRouteTitleHandled(route);
    routerItem.meta.title = getFormTitle(routerItem);
    let i = tagNavList.value.findIndex((item) => item.path === route.path);

    if (!routeHasExist(tagNavList.value, routerItem)) {
      if (type === 'push')
        if (i < 1) tagNavList.value.push(routerItem);
        else {
          return;
        }
      setTagNavListInLocalstorage([...tagNavList.value]);
    }
  }
  function setLocal(lang) {
    localSave('local', lang);
    local.value = lang;
  }
  function addError(error) {
    errorList.value.push(error);
  }
  function setHasReadErrorLoggerStatus(status = true) {
    hasReadErrorPage.value = status;
  }
  function clearAll() {
    tagNavList.value = [];
  }

  function closePage(route) {
    const nextRoute = getNextRoute(tagNavList.value, route);
    tagNavList.value = tagNavList.value.filter((item) => {
      return !routeEqual(item, route);
    });
    router.push(nextRoute);
  }

  // actions
  function addErrorLog(info) {
    if (!window.location.href.includes('error_logger_page')) setHasReadErrorLoggerStatus(false);
    const userStore = useUserStore();
    const { token, userId, userName } = { token: userStore.token, userId: userStore.userId, userName: userStore.name };
    let data = {
      ...info,
      time: Date.parse(new Date()),
      token,
      userId,
      userName,
    };
    // saveErrorLogger(info).then(() => { addError(data) })
  }

  // 设置路由缓存（name字段）
  async function setCacheKeepAlive(data) {
    getCacheKeepAlive(data);
  }

  return {
    header,
    oneMenuName,
    sider,
    headerName,
    activePath,
    openNames,
    breadCrumbList,
    tagNavList,
    homeRoute,
    local,
    errorList,
    adminTitle,
    hasReadErrorPage,
    keepAliveNames,
    filterSider,
    filterHeader,
    currentHeader,
    hideSider,
    setSider,
    setOpenMenuName,
    setHeader,
    setHeaderName,
    setActivePath,
    setOpenNames,
    getCacheKeepAlive,
    setBreadCrumb,
    setAdminTitle,
    setHomeRoute,
    setTagNavList,
    closeTag,
    addTag,
    setLocal,
    addError,
    setHasReadErrorLoggerStatus,
    clearAll,
    addErrorLog,
    setCacheKeepAlive,
  };
});
