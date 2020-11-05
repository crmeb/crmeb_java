import Layout from '@/layout'

const mobileRouter = {
  path: '/javaMobile',
  component: Layout,
  redirect: '/javaMobile/index',
  name: 'Mobile',
  alwaysShow: true,
  meta: {
    title: '移动端',
    icon: 'clipboard'
  },
  children: [
    {
      path: 'orderCancellation',
      component: () => import('@/views/mobile/orderCancellation/index.vue'),
      name: 'OrderCancellation',
      meta: { title: '订单核销', icon: '' }
    },
    {
      path: 'orderStatistics',
      component: () => import('@/views/mobile/orderStatistics/index.vue'),
      name: 'OrderStatistics',
      meta: { title: '订单统计' }
    },
    {
      path: 'orderList/:types?',
      component: () => import('@/views/mobile/orderStatistics/orderList.vue'),
      name: 'OrderList',
      meta: { title: '订单列表' }
    },
    {
      path: 'orderDelivery/:oid/:id?',
      component: () => import('@/views/mobile/orderStatistics/orderDelivery.vue'),
      name: 'OrderDelivery',
      meta: { title: '订单发货' }
    },
    {
      path: 'orderDetail/:id?/:goname?',
      component: () => import('@/views/mobile/orderStatistics/orderDetail.vue'),
      name: 'OrderDetail',
      meta: { title: '订单详情' }
    },
    {
      path: 'orderStatisticsDetail/:type/:time?',
      component: () => import('@/views/mobile/orderStatistics/Statistics.vue'),
      name: 'OrderStatisticsDetail',
      meta: { title: '订单数据统计' }
    }
  ]
}

export default mobileRouter
