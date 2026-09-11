// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { createRouter, createWebHistory } from 'vue-router';

/* Layout */
import Layout from '@/layout';

/* Router Modules */
// import componentsRouter from './modules/components'
import storeRouter from './modules/store';
import orderRouter from './modules/order';
import userRouter from './modules/user';
import distributionRouter from './modules/distribution';
import marketingRouter from './modules/marketing';
import financialRouter from './modules/financial';
import contentRouter from './modules/content';
import operationRouter from './modules/operation';
import appSettingRouter from './modules/appSetting';
import maintainRouter from './modules/maintain';
import designRouter from './modules/design';

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 *
 */

export const constantRoutes = [
  // 商品
  storeRouter,
  // 订单
  orderRouter,
  // 会员
  userRouter,
  // 分销
  distributionRouter,
  // 营销
  marketingRouter,
  // 财务
  financialRouter,
  // 内容
  contentRouter,
  // 设置
  operationRouter,
  // 应用
  appSettingRouter,
  // 维护
  maintainRouter,
  //装修
  designRouter,
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true,
  },
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index'),
      },
    ],
  },
  {
    path: '/auth-send',
    component: () => import('@/views/mobile/auth-send'),
    hidden: true,
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true,
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/auth-redirect'),
    hidden: true,
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true,
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '主页', icon: 'dashboard', isAffix: true },
      },
    ],
  },
  {
    path: '/setting/uploadPicture',
    component: () => import('@/components/uploadPicture/index.vue'),
    name: 'uploadPicture',
  },
  // 404 page must be placed at the end !!!
  // Vue Router 4 中通配符 * 不再支持，需用 :pathMatch(.*)*
  { path: '/:pathMatch(.*)*', redirect: '/404', hidden: true },
];

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [];

const createAppRouter = () =>
  createRouter({
    // mode: 'history', // require service support
    history: createWebHistory(),
    // Vue Router 4 scrollBehavior 使用 left/top 而非 x/y
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes: constantRoutes,
  });

const router = createAppRouter();

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
// Vue Router 4 中 router.matcher 已移除，resetRouter 改为移除动态添加的路由
export function resetRouter() {
  const newRouter = createAppRouter();
  // 移除所有动态添加的路由（保留初始 constantRoutes）
  router.getRoutes().forEach((r) => {
    if (!constantRoutes.some((cr) => cr.name === r.name) && r.name) {
      router.removeRoute(r.name);
    }
  });
  // 简化处理：用新路由的 matcher 思路不可用，这里通过 removeRoute 清理动态路由
  // 若仍有残留，可手动调用各动态路由 name 移除
  void newRouter;
}

export default router;
