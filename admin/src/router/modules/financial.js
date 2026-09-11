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

const financialRouter = {
  path: '/financial',
  component: Layout,
  redirect: '/financial/commission/template',
  name: 'Financial',
  meta: {
    title: '财务',
    icon: 'clipboard',
  },
  children: [
    {
      path: 'commission/template',
      component: () => import('@/views/financial/commission/withdrawal/index'),
      name: 'commissionTemplate',
      meta: { title: '提现记录', icon: '' },
    },
    {
      path: 'record/charge',
      component: () => import('@/views/financial/record/charge/index'),
      name: 'Charge',
      meta: { title: '充值记录', icon: '' },
    },
    {
      path: 'brokerage',
      component: () => import('@/views/financial/brokerage/index'),
      name: 'Brokerage',
      meta: { title: '佣金记录', icon: '' },
    },
    {
      path: 'record/monitor',
      component: () => import('@/views/financial/record/monitor/index'),
      name: 'Monitor',
      meta: { title: '资金监控', icon: '' },
    },
  ],
};

export default financialRouter;
