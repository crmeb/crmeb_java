// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import Layout from '@/layout';

const designRouter = {
  path: '/design',
  component: Layout,
  redirect: '/design/devise',
  name: 'design',
  meta: {
    title: '装修',
    icon: 'clipboard',
  },
  children: [
    {
      path: 'theme',
      name: 'theme',
      component: () => import('@/views/design/theme/index'),
      meta: {
        title: '一键换色',
      },
    },
    {
      path: 'viewDesign',
      name: 'viewDesign',
      component: () => import('@/views/design/viewDesign/index'),
      meta: {
        title: '页面设计',
      },
    },
    {
      path: 'devise',
      name: 'devise',
      component: () => import('@/views/design/devise/index'),
      meta: {
        title: '首页装修',
      },
    },
  ],
};

export default designRouter;
