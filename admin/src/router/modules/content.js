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
      path: 'articleCreat/:id?',
      name: 'articleCreat',
      component: () => import('@/views/content/article/edit'),
      meta: {
        title: '添加文章',
       noCache: true, activeMenu: `/content/articleManager`
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
