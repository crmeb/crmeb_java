// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import Layout from '@/layout'

const orderRouter = {
  path: '/order',
  component: Layout,
  redirect: '/order/index',
  name: 'Order',
  alwaysShow: true,
  meta: {
    title: '订单',
    icon: 'clipboard'
  },
  children: [
    {
      path: 'index',
      component: () => import('@/views/order/index'),
      name: 'OrderIndex',
      meta: { title: '订单' }
    }
  ]
}

export default orderRouter
