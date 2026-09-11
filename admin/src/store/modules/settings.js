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
import defaultSettings from '@/settings';
import Cache from '@/plugins/cache';

const { showSettings, tagsView, fixedHeader, sidebarLogo, topNav, sideTheme, navIcon } = defaultSettings;
const storageSetting = Cache.local.has('layout-setting') ? Cache.local.getJSON('layout-setting') : '';

export const useSettingsStore = defineStore('settings', () => {
  const theme = ref(storageSetting.theme || '#409EFF');
  const sideThemeVal = ref(storageSetting.sideTheme || sideTheme);
  const topNavVal = ref(storageSetting.topNav === undefined ? topNav : storageSetting.topNav);
  const showSettingsVal = ref(showSettings);
  const tagsViewVal = ref(storageSetting.tagsView === undefined ? tagsView : storageSetting.tagsView);
  const fixedHeaderVal = ref(storageSetting.fixedHeader === undefined ? fixedHeader : storageSetting.fixedHeader);
  const sidebarLogoVal = ref(storageSetting.sidebarLogo === undefined ? sidebarLogo : storageSetting.sidebarLogo);
  const navIconVal = ref(storageSetting.navIcon === undefined ? navIcon : storageSetting.navIcon);
  const frontDomain = ref(localStorage.getItem('frontDomain') || ''); //移动端域名
  const mediaDomain = ref(localStorage.getItem('mediaDomain') || ''); //图片域名
  const mobileTheme = ref(localStorage.getItem('mobileTheme') || '');

  // 暴露给外部的属性名保持与原 state 一致（showSettings/tagsView/fixedHeader/sidebarLogo/topNav/sideTheme/navIcon）
  function changeSetting({ key, value }) {
    const map = {
      theme,
      sideTheme: sideThemeVal,
      topNav: topNavVal,
      showSettings: showSettingsVal,
      tagsView: tagsViewVal,
      fixedHeader: fixedHeaderVal,
      sidebarLogo: sidebarLogoVal,
      navIcon: navIconVal,
      frontDomain,
      mediaDomain,
      mobileTheme,
    };
    if (map[key]) {
      map[key].value = value;
    }
  }

  function SET_FrontDomain(val) {
    frontDomain.value = val;
    localStorage.setItem('frontDomain', val);
    if (!val) localStorage.removeItem('frontDomain');
  }

  function SET_mobileThemeColor(val) {
    mobileTheme.value = val;
    localStorage.setItem('mobileTheme', val);
    if (!val) localStorage.removeItem('mobileTheme');
  }

  function SET_mediaDomain(val) {
    mediaDomain.value = val;
    localStorage.setItem('mediaDomain', val);
    if (!val) localStorage.removeItem('mediaDomain');
  }

  return {
    theme,
    sideTheme: sideThemeVal,
    topNav: topNavVal,
    showSettings: showSettingsVal,
    tagsView: tagsViewVal,
    fixedHeader: fixedHeaderVal,
    sidebarLogo: sidebarLogoVal,
    navIcon: navIconVal,
    frontDomain,
    mediaDomain,
    mobileTheme,
    changeSetting,
    SET_FrontDomain,
    SET_mobileThemeColor,
    SET_mediaDomain,
  };
});
