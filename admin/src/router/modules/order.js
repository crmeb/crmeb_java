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
      meta: { title: '订单管理' }
    },
    {
      path: 'statistics',
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
      path: 'orderStatistics/:type/:time?',
      component: () => import('@/views/mobile/orderStatistics/Statistics.vue'),
      name: 'OrderStatistics',
      meta: { title: '订单数据统计' }
    }
  ]
}

export default orderRouter
