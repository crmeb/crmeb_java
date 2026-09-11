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
  redirect: '/design/mall_theme',
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
      path: 'mall_theme',
      name: 'mallTheme',
      component: () => import('@/views/design/mall_theme/index'),
      meta: {
        title: '商城主题',
      },
    },
    {
      path: 'my_theme',
      name: 'myTheme',
      component: () => import('@/views/design/my_theme/index'),
      meta: {
        title: '我的主题',
      },
    },
    {
      path: 'micro_theme',
      name: 'microTheme',
      component: () => import('@/views/design/micro_theme/index'),
      meta: {
        title: '专题页面',
      },
    },
    {
      path: 'edit_theme',
      name: 'editTheme',
      hidden: true,
      component: () => import('@/views/design/edit_theme/index'),
      meta: {
        title: '主题风格',
        fullScreen: true,
        activeMenu: '/design/mall_theme',
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
      path: 'picture',
      name: 'designPicture',
      component: () => import('@/views/maintain/picture'),
      meta: {
        title: '素材管理',
      },
    },
    {
      path: 'advertisement',
      name: 'advertisement',
      component: () => import('@/views/design/advertisement/index'),
      meta: {
        title: '开屏广告',
      },
    },
  ],
};

export default designRouter;
