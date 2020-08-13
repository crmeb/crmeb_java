import Layout from '@/layout'

const distributionRouter = {
  path: '/distribution',
  component: Layout,
  redirect: '/distribution/index',
  name: 'Distribution',
  meta: {
    title: '分销',
    icon: 'clipboard'
  },
  children: [
    {
      path: 'index',
      component: () => import('@/views/distribution/index'),
      name: 'distributionIndex',
      meta: { title: '分销员管理', icon: '' }
    },
    {
      path: 'distributionconfig',
      component: () => import('@/views/distribution/config/index'),
      name: 'distributionConfig',
      meta: { title: '分销配置', icon: '' }
    }
  ]
}

export default distributionRouter
