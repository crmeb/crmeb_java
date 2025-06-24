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
function getFormTitle(router) {
  let path = router.path;
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
    case 'pc_config':
      return 'pc基础配置';
      break;
    default:
      return router.meta.title;
  }
}
const closePage = (state, route) => {
  const nextRoute = getNextRoute(state.tagNavList, route);
  state.tagNavList = state.tagNavList.filter((item) => {
    return !routeEqual(item, route);
  });
  router.push(nextRoute);
};

export default {
  namespaced: true,
  state: {
    // 顶部菜单
    header: [],
    // 一级菜单名称
    oneMenuName: '',
    // 侧栏菜单
    sider: [],
    // 当前顶栏菜单的 name
    headerName: '',
    // 当前所在菜单的 path
    activePath: '',
    // 展开的子菜单 name 集合
    openNames: [],
    //----------------------------------------------------------------
    breadCrumbList: [],
    tagNavList: getTagNavListFromLocalstorage() || [],
    homeRoute: {},
    local: localRead('local'),
    errorList: [],
    adminTitle: '',
    hasReadErrorPage: false,
    //----------------------------------------------------------------
    keepAliveNames: [],
  },
  getters: {
    /**
     * @description 根据 user 里登录用户权限，对侧边菜单进行鉴权过滤
     * */
    filterSider(state, getters, rootState) {
      const userInfo = rootState.user.info;
      // @权限
      const access = userInfo.access;
      if (access && access.length) {
        return filterMenu(state.sider, access, []);
      } else {
        return filterMenu(state.sider, [], []);
      }
    },
    // 处理顶部路由递归

    /**
     * @description 根据 user 里登录用户权限，对顶栏菜单进行鉴权过滤
     * */
    filterHeader(state, getters, rootState) {
      //  调用递归函数
      state.header.forEach((item) => {
        item.path = getChilden(item);
      });

      // @权限
      const userInfo = rootState.admin.user.info;
      const access = userInfo.access;
      if (access && access.length) {
        return state.header.filter((item) => {
          let state = true;
          if (item.auth && !includeArray(item.auth, access)) state = false;
          return state;
        });
      } else {
        return state.header.filter((item) => {
          let state = true;
          if (item.auth && item.auth.length) state = false;
          return state;
        });
      }
    },
    /**
     * @description 当前 header 的全部信息
     * */
    currentHeader(state) {
      return state.header.find((item) => item.name === state.headerName);
    },
    /**
     * @description 在当前 header 下，是否隐藏 sider（及折叠按钮）
     * */
    hideSider(state, getters) {
      let visible = false;
      if (getters.currentHeader && 'hideSider' in getters.currentHeader) visible = getters.currentHeader.hideSider;
      return visible;
    },
  },
  mutations: {
    /**
     * @description 设置侧边栏菜单
     * @param {Object} state vuex state
     * @param {Array} menu menu
     */
    setSider(state, menu) {
      state.sider = menu;
    },
    /**
     * @description 设置侧边栏菜单
     * @param {Object} state vuex state
     * @param {Array} menu menu
     */
    setOpenMenuName(state, menu) {
      state.oneMenuName = menu;
    },
    /**
     * @description 设置顶栏菜单
     * @param {Object} state vuex state
     * @param {Array} menu menu
     */
    setHeader(state, menu) {
      state.header = menu;
    },
    /**
     * @description 设置当前顶栏菜单 name
     * @param {Object} state vuex state
     * @param {Array} name headerName
     */
    setHeaderName(state, name) {
      state.headerName = name;
    },
    /**
     * @description 设置当前所在菜单的 path，用于侧栏菜单高亮当前项
     * @param {Object} state vuex state
     * @param {Array} path fullPath
     */
    setActivePath(state, path) {
      state.activePath = path;
    },
    /**
     * @description 设置当前所在菜单的全部展开父菜单的 names 集合
     * @param {Object} state vuex state
     * @param {Array} names openNames
     */
    setOpenNames(state, names) {
      state.openNames = names;
    },
    getCacheKeepAlive(state, data) {
      state.keepAliveNames = data;
    },
    // ----------------------------------------------------------------
    setBreadCrumb(state, route) {
      state.breadCrumbList = getBreadCrumbList(route, state.homeRoute);
    },
    setAdminTitle(state, title) {
      state.adminTitle = title;
    },
    setHomeRoute(state, routes) {
      state.homeRoute = getHomeRoute(routes, homeName);
    },
    setTagNavList(state, list) {
      let tagList = [];
      if (list.length) {
        tagList = [...list];
      }
      state.tagNavList = tagList;
      setTagNavListInLocalstorage([...tagList]);
    },
    closeTag(state, route) {
      let tag = state.tagNavList.filter((item) => routeEqual(item, route));
      route = tag[0] ? tag[0] : null;
      if (!route) return;
      closePage(state, route);
    },
    addTag(state, { route, type = 'unshift' }) {
      let router = getRouteTitleHandled(route);
      router.meta.title = getFormTitle(router);
      let i = state.tagNavList.findIndex((item) => item.path === route.path);

      if (!routeHasExist(state.tagNavList, router)) {
        if (type === 'push')
          if (i < 1) state.tagNavList.push(router);
          else {
            return;
            // if (router.name === homeName) state.tagNavList.unshift(router);
            // else state.tagNavList.splice(1, 0, router);
          }
        setTagNavListInLocalstorage([...state.tagNavList]);
      }
    },
    setLocal(state, lang) {
      localSave('local', lang);
      state.local = lang;
    },
    addError(state, error) {
      state.errorList.push(error);
    },
    setHasReadErrorLoggerStatus(state, status = true) {
      state.hasReadErrorPage = status;
    },
    clearAll(state) {
      state.tagNavList = [];
    },
  },
  actions: {
    addErrorLog({ commit, rootState }, info) {
      if (!window.location.href.includes('error_logger_page')) commit('setHasReadErrorLoggerStatus', false);
      const {
        user: { token, userId, userName },
      } = rootState;
      let data = {
        ...info,
        time: Date.parse(new Date()),
        token,
        userId,
        userName,
      };
      // saveErrorLogger(info).then(() => {
      //   commit('addError', data)
      // })
    },
    // 设置路由缓存（name字段）
    async setCacheKeepAlive({ commit }, data) {
      commit('getCacheKeepAlive', data);
    },
  },
};
