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
import { reactive, ref } from 'vue';
import Cookies from 'js-cookie';

export const useAppStore = defineStore('app', () => {
  const sidebar = reactive({
    opened: Cookies.get('sidebarStatus') ? !!+Cookies.get('sidebarStatus') : true,
    withoutAnimation: false,
  });
  const device = ref('desktop');
  // 尺寸配置统一归一到 Element Plus default。
  const sizeMap = { medium: 'default', mini: 'default', small: 'default', large: 'default' };
  const normalizeSize = (sizeVal) => sizeMap[sizeVal] || sizeVal || 'default';
  const size = ref(normalizeSize(Cookies.get('size')));

  // TOGGLE_SIDEBAR
  function toggleSideBar() {
    sidebar.opened = !sidebar.opened;
    sidebar.withoutAnimation = false;
    if (sidebar.opened) {
      Cookies.set('sidebarStatus', 1);
    } else {
      Cookies.set('sidebarStatus', 0);
    }
  }

  // CLOSE_SIDEBAR
  function closeSideBar({ withoutAnimation }) {
    Cookies.set('sidebarStatus', 0);
    sidebar.opened = false;
    sidebar.withoutAnimation = withoutAnimation;
  }

  // TOGGLE_DEVICE
  function toggleDevice(deviceVal) {
    device.value = deviceVal;
  }

  // SET_SIZE
  function setSize(sizeVal) {
    const mapped = normalizeSize(sizeVal);
    size.value = mapped;
    Cookies.set('size', mapped);
  }

  return {
    sidebar,
    device,
    size,
    toggleSideBar,
    closeSideBar,
    toggleDevice,
    setSize,
  };
});
