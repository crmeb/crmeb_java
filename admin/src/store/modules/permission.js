// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { defineStore } from 'pinia';
import { ref } from 'vue';
import { asyncRoutes, constantRoutes } from '@/router';
import * as roleApi from '@/api/roleApi.js';
import { formatRoutes } from '@/utils/parsing';
import { useSettingsStore } from './settings';

/**
 * Filter asynchronous routing tables by recursion
 * @param routes asyncRoutes
 * @param roles
 */
export function filterAsyncRoutes(routes, roles) {
  const res = [];

  routes.forEach((route) => {
    const tmp = { ...route };
    if (tmp.child) {
      tmp.child = filterAsyncRoutes(tmp.child, roles);
    }
    res.push(tmp);
  });

  return res;
}

function comRouter(menus, asyncRouter, hasLeft) {
  const res = [];
  asyncRouter.forEach((router) => {
    const _leftUrl = hasLeft ? hasLeft + '/' + router.path : router.path;
    const _hasPromise = menus.filter((item) => item.url.startsWith(_leftUrl));
    if (_hasPromise.length > 0) {
      res.push(router);
    }
    if (router.children) {
      comRouter(menus, router.children, router.path);
    }
  });
  return res;
}

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref([]);
  const addRoutes = ref([]);
  const topbarRouters = ref([]);
  const sidebarRouters = ref([]);

  function SET_ROUTES(list) {
    addRoutes.value = list;
    routes.value = list;
  }

  function SET_TOPBAR_ROUTES(list) {
    topbarRouters.value = list;
  }

  function SET_SIDEBAR_ROUTERS(list) {
    sidebarRouters.value = list;
  }

  function generateRoutes(roleid) {
    return new Promise(async (resolve) => {
      let accessedRoutes = [];
      let menus = [];
      let menusAll = await roleApi.menuListApi();
      menusAll = formatRoutes(menusAll);

      menus = menusAll;
      const _routerResult = comRouter(menus, asyncRoutes);
      accessedRoutes = filterAsyncRoutes(_routerResult);
      // todo 这里控制是否过滤路由，经测试有些菜单不能予以设置，比如系统设置等等
      useSettingsStore().showSettings = false;
      SET_ROUTES(menus);
      SET_TOPBAR_ROUTES(menus);
      if (useSettingsStore().topNav) {
        SET_SIDEBAR_ROUTERS(sidebarRouters.value.length ? sidebarRouters.value : menus[0].child);
      } else {
        SET_SIDEBAR_ROUTERS(menus);
      }
      resolve(accessedRoutes);
    });
  }

  return {
    routes,
    addRoutes,
    topbarRouters,
    sidebarRouters,
    SET_ROUTES,
    SET_TOPBAR_ROUTES,
    SET_SIDEBAR_ROUTERS,
    generateRoutes,
  };
});
