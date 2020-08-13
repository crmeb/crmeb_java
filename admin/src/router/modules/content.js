import Layout from '@/layout'

const contentRouter = {
  path: '/content',
  component: Layout,
  redirect: '/content/articleManager',
  name: 'content',
  meta: {
    title: '内容',
    icon: 'clipboard'
  },
  children: [
    {
      path: 'articleManager',
      name: 'articleManager',
      component: () => import('@/views/content/article/list'),
      meta: {
        title: '文章管理',
        icon: 'clipboard'
      }
    },
    {
      path: 'classifManager',
      name: 'classifManager',
      component: () => import('@/views/content/articleclass/list'),
      meta: {
        title: '文章分类',
        icon: 'clipboard'
      }
    }
  ]
}

export default contentRouter
