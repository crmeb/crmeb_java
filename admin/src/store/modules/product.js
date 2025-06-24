// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import * as product from '@/api/store';

const state = {
  adminProductClassify: localStorage.getItem('adminProductClassify')
    ? JSON.parse(localStorage.getItem('adminProductClassify'))
    : [] /** 平台商品分类 **/,
};

const mutations = {
  SET_AdminProductClassify: (state, adminProductClassify) => {
    state.adminProductClassify = adminProductClassify;
    localStorage.setItem('adminProductClassify', JSON.stringify(changeNodes(adminProductClassify)));
    if (!adminProductClassify.length) localStorage.removeItem('adminProductClassify');
  },
};

const actions = {
  /** 平台商品分类 **/
  getAdminProductClassify({ commit, dispatch }) {
    return new Promise((resolve, reject) => {
      product
        .categoryApi({ status: -1, type: 1 })
        .then(async (res) => {
          commit('SET_AdminProductClassify', changeNodes(res));
          resolve(res);
        })
        .catch((error) => {
          reject(error);
        });
    });
  },
};

/** tree去除 childList=[] 的结构**/
const changeNodes = function (data) {
  if (data.length > 0) {
    for (var i = 0; i < data.length; i++) {
      if (data[i].isShow === false) {
        data[i].disabled = true;
      }
      if (!data[i].child || data[i].child.length < 1) {
        data[i].child = undefined;
      } else {
        changeNodes(data[i].child);
      }
    }
  }
  return data;
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  changeNodes,
};
