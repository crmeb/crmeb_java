// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

// 兼容 Vue2 时代 store.getters.xxx 的访问习惯。
// Vue3 + Pinia 无全局 getters，改为各 store 内的 computed。
// 此处聚合常用 getters，供过渡期使用；新代码建议直接 useXxxStore()。
import { computed } from 'vue';
import { useAppStore } from './modules/app';
import { useTagsViewStore } from './modules/tagsView';
import { useUserStore } from './modules/user';
import { usePermissionStore } from './modules/permission';
import { useErrorLogStore } from './modules/errorLog';
import { useProductStore } from './modules/product';
import { useSettingsStore } from './modules/settings';

export function useGetters() {
  const appStore = useAppStore();
  const tagsViewStore = useTagsViewStore();
  const userStore = useUserStore();
  const permissionStore = usePermissionStore();
  const errorLogStore = useErrorLogStore();
  const productStore = useProductStore();
  const settingsStore = useSettingsStore();

  return {
    sidebar: computed(() => appStore.sidebar),
    size: computed(() => appStore.size),
    device: computed(() => appStore.device),
    visitedViews: computed(() => tagsViewStore.visitedViews),
    cachedViews: computed(() => tagsViewStore.cachedViews),
    token: computed(() => userStore.token),
    avatar: computed(() => userStore.avatar),
    name: computed(() => userStore.name),
    introduction: computed(() => userStore.introduction),
    roles: computed(() => userStore.roles),
    permission_routes: computed(() => permissionStore.routes),
    permissions: computed(() => userStore.permissions),
    sidebarRouters: computed(() => permissionStore.sidebarRouters),
    errorLogs: computed(() => errorLogStore.logs),
    isLogin: computed(() => userStore.isLogin),
    adminProductClassify: computed(() => productStore.adminProductClassify),
    frontDomain: computed(() => settingsStore.frontDomain),
    mediaDomain: computed(() => settingsStore.mediaDomain),
    mobileTheme: computed(() => settingsStore.mobileTheme),
  };
}
