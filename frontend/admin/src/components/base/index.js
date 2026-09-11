// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { defineAsyncComponent } from 'vue';

const components = {
  pagesHeader: () => import('./pagesHeader.vue'),
  optionDatePicker: () => import('./optionDatePicker.vue'),
  ZBParser: () => import('./ZBParser.vue'),
  UserSearchInput: () => import('./UserSearchInput.vue'),
};

export default function (app) {
  Object.keys(components).forEach((name) => {
    app.component(name, defineAsyncComponent(components[name]));
  });
};
