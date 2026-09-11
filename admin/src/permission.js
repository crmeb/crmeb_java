// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import router from './router';
import { ElMessage as Message } from '@/utils/elementPlusFeedback';
import NProgress from 'nprogress'; // progress bar
import 'nprogress/nprogress.css'; // progress bar style
import { getToken } from '@/utils/auth'; // get token from cookie
import getPageTitle from '@/utils/get-page-title';
import { useUserStore } from '@/store/modules/user';
import { usePermissionStore } from '@/store/modules/permission';
import { useMenuStore } from '@/store/modules/menu';

NProgress.configure({ showSpinner: false }); // NProgress Configuration

const whiteList = ['/login', '/auth-redirect']; // no redirect whitelist

router.beforeEach(async (to, from, next) => {
  // start progress bar
  NProgress.start();

  // set page title
  document.title = getPageTitle(to.meta.title);

  // determine whether the user has logged in
  const hasToken = getToken();

  if (hasToken) {
    if (to.path === '/login') {
      // if is logged in, redirect to the home page
      next({ path: '/' });
      NProgress.done();
    } else {
      const userStore = useUserStore();
      const hasRoles = userStore.roles && userStore.roles.length > 0;
      if (hasRoles) {
        next();
      } else {
        try {
          const roles = await userStore.getInfo();
          const permissionStore = usePermissionStore();
          const accessRoutes = await permissionStore.generateRoutes(roles);
          // Vue Router 4 中 addRoutes 改为 addRoute（单数），需逐个添加
          accessRoutes.forEach((route) => {
            router.addRoute(route);
          });
          next({ ...to, replace: true });
        } catch (error) {
          // remove token and go to login page to re-login
          await useUserStore().resetToken();
          Message.error((error && (error.message || error.msg)) || error || 'Has Error');
          next(`/login?redirect=${to.path}`);
          NProgress.done();
        }
      }
    }
  } else {
    /* has no token*/
    if (whiteList.indexOf(to.path) !== -1) {
      // in the free login whitelist, go directly
      next();
    } else {
      // other pages that do not have permission to access are redirected to the login page.
      next(`/login?redirect=${to.path}`);
      NProgress.done();
    }
  }
});

router.afterEach(() => {
  // finish progress bar
  NProgress.done();
  // 填充 keep-alive 缓存列表：取已打开标签中未标记 noCache 的路由 name
  // keep-alive :include 按组件 name 匹配，路由 name 与组件 name 大部分一致
  const menuStore = useMenuStore();
  const names = menuStore.tagNavList
    .filter((item) => item.meta && !item.meta.noCache && item.name)
    .map((item) => item.name);
  menuStore.setCacheKeepAlive(names);
});
