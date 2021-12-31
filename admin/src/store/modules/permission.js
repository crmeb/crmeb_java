// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { asyncRoutes, constantRoutes } from '@/router'
import * as roleApi from '@/api/roleApi.js'
import * as Auth from '@/libs/wechat';
import {formatRoutes} from '@/utils/parsing'

/**
 * Filter asynchronous routing tables by recursion
 * @param routes asyncRoutes
 * @param roles
 */
export function filterAsyncRoutes(routes, roles) {
  const res = []

  routes.forEach(route => {
    const tmp = { ...route }
    if (tmp.child) {
      tmp.child = filterAsyncRoutes(tmp.child, roles)
    }
    res.push(tmp)
  })

  return res
}

const state = {
  routes: [],
  addRoutes: [],
  topbarRouters: [],
  sidebarRouters: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    // state.routes = constantRoutes.concat(routes)
    state.routes = routes
  },
  SET_TOPBAR_ROUTES: (state, routes) => {
    state.topbarRouters = routes
  },
  SET_SIDEBAR_ROUTERS: (state, routes) => {
    state.sidebarRouters = routes
  },
}

const actions = {
  generateRoutes({ commit }, roleid) {
    return new Promise(async resolve => {
      let accessedRoutes = []
      let menus= []
      // const { rules } = await roleApi.getRoleById(roleid)
      let menusAll = await roleApi.menuListApi()
      menusAll = formatRoutes(menusAll)
      
      !Auth.isPhone() ? menus = menusAll.filter(item => item.url !== '/javaMobile') : menus = menusAll.filter(item => item.url === '/javaMobile')
      const _routerResult = comRouter(menus, asyncRoutes)
      accessedRoutes = filterAsyncRoutes(_routerResult)
      // todo 这里控制是否过滤路由，经测试有些菜单不能予以设置，比如系统设置等等
      this.state.settings.showSettings = false
      commit('SET_ROUTES', menus)
      commit('SET_TOPBAR_ROUTES', menus)
      if(this.state.settings.topNav){
        commit('SET_SIDEBAR_ROUTERS', state.sidebarRouters.length ? state.sidebarRouters : menus[0].child)
      }else{
        commit('SET_SIDEBAR_ROUTERS', menus)
      }
      // resolve(menus)
      // commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes)

      // commit('SET_ROUTES', asyncRoutes)
      // resolve(asyncRoutes)
    })
  }
}

function comRouter(menus, asyncRouter, hasLeft) {
  const res = []
  asyncRouter.forEach(router => {
    const _leftUrl = hasLeft ? (hasLeft + '/' + router.path) : router.path
    const _hasPromise = menus.filter(item => item.url.startsWith(_leftUrl))
    if (_hasPromise.length > 0) {
      res.push(router)
    }
    if (router.children) {
      comRouter(menus, router.children, router.path)
    }
  })
  return res
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
