// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import defaultSettings from '@/settings'
import Cache from '@/plugins/cache'
const { showSettings, tagsView, fixedHeader, sidebarLogo,topNav,sideTheme,navIcon } = defaultSettings
const storageSetting = Cache.local.has('layout-setting') ? Cache.local.getJSON('layout-setting') : '';
const state = {
  theme: storageSetting.theme || '#409EFF',
  sideTheme:storageSetting.sideTheme || sideTheme,
  topNav:storageSetting.topNav === undefined ? topNav : storageSetting.topNav,
  showSettings: showSettings,
  tagsView: storageSetting.tagsView === undefined ? tagsView : storageSetting.tagsView,
  fixedHeader: storageSetting.fixedHeader === undefined ? fixedHeader : storageSetting.fixedHeader,
  sidebarLogo: storageSetting.sidebarLogo === undefined ? sidebarLogo : storageSetting.sidebarLogo,
  navIcon: storageSetting.navIcon === undefined ? navIcon : storageSetting.navIcon
}
const mutations = {
  CHANGE_SETTING: (state, { key, value }) => {
    if (state.hasOwnProperty(key)) {
      state[key] = value
    }
  },
}

const actions = {
  changeSetting({ commit }, data) {
    commit('CHANGE_SETTING', data)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

