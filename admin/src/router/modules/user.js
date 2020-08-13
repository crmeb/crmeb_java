import Layout from '@/layout'

const userRouter = {
  path: '/user',
  component: Layout,
  redirect: '/user/index',
  name: 'User',
  meta: {
    title: '用户',
    icon: 'clipboard'
  },
  children: [
    {
      path: 'index',
      component: () => import('@/views/user/list/index'),
      name: 'UserIndex',
      meta: { title: '用户管理', icon: '' }
    },
    {
      path: 'grade',
      component: () => import('@/views/user/grade/index'),
      name: 'Grade',
      meta: { title: '用户等级', icon: '' }
    },
    {
      path: 'label',
      component: () => import('@/views/user/group/index'),
      name: 'Label',
      meta: { title: '用户标签', icon: '' }
    },
    {
      path: 'group',
      component: () => import('@/views/user/group/index'),
      name: 'Group',
      meta: { title: '用户分组', icon: '' }
    }
  ]
}

export default userRouter
