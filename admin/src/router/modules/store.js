import Layout from '@/layout'

const storeRouter = {
    path: '/store',
    component: Layout,
    redirect: '/store/index',
    name: 'Store',
    meta: {
      title: '商品',
      icon: 'clipboard'
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/store/index'),
        name: 'StoreIndex',
        meta: { title: '商品管理', icon: '' }
      },
      {
        path: 'sort',
        component: () => import('@/views/store/sort/index'),
        name: 'Sort',
        meta: { title: '商品分类', icon: '' }
      },
      {
        path: 'attr',
        component: () => import('@/views/store/storeAttr/index'),
        name: 'SortAttr',
        meta: { title: '商品规格', icon: '' }
      },
      {
        path: 'comment',
        component: () => import('@/views/store/storeComment/index'),
        name: 'StoreComment',
        meta: { title: '商品评论', icon: '' }
      },
      {
        path: 'list/creatProduct/:id?/:isDisabled?',
        component: () => import('@/views/store/creatStore/index'),
        name: 'SortCreat',
        meta: { title: '商品添加', noCache: true, activeMenu: `/store/index` },
        hidden: true
      }
    ]
  }

export default storeRouter
