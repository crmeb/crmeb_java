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

const orderRouter = {
  path: '/statistic',
  component: Layout,
  redirect: '/statistic/product',
  name: 'statistic',
  alwaysShow: true,
  meta: {
    title: '统计管理',
    icon: 'clipboard',
  },
  children: [
    {
      path: 'product',
      name: `product`,
      meta: { title: '商品统计' },
      component: () => import('@/views/statistic/product/index'),
      // children: [
      //   {
      //     path: 'visualization',
      //     component: () => import('@/views/statistic/product/visualization'),
      //     name: 'visualization',
      //     meta: { title: '统计', icon: '' }
      //   },
      //   {
      //     path: 'tableData',
      //     component: () => import('@/views/statistic/product/tableData'),
      //     name: 'tableData',
      //     meta: { title: '商品排行', icon: '' }
      //   },
      // ]
    },
    {
      path: 'statuser',
      name: `statuser`,
      meta: { title: '用户统计' },
      component: () => import('@/views/statistic/user/index'),
    },
    {
      path: 'transaction',
      name: `transaction`,
      meta: { title: '交易统计' },
      component: () => import('@/views/statistic/transaction/index'),
    },
  ],
};

export default orderRouter;
