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
import { themeProductCategory } from '@/api/theme';

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

export const useProductStore = defineStore('product', () => {
  const adminProductClassify = ref(
    localStorage.getItem('adminProductClassify')
      ? JSON.parse(localStorage.getItem('adminProductClassify'))
      : [],
  );

  function SET_AdminProductClassify(list) {
    adminProductClassify.value = list;
    localStorage.setItem('adminProductClassify', JSON.stringify(changeNodes(list)));
    if (!list.length) localStorage.removeItem('adminProductClassify');
  }

  /** 平台商品分类 **/
  function getAdminProductClassify() {
    return new Promise((resolve, reject) => {
      themeProductCategory({ status: -1 })
        .then(async (res) => {
          const list = res.data || [];
          SET_AdminProductClassify(changeNodes(list));
          resolve(list);
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  return {
    adminProductClassify,
    SET_AdminProductClassify,
    getAdminProductClassify,
    changeNodes,
  };
});

export { changeNodes };
