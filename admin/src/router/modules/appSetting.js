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

const appSettingRouter = {
  path: '/appSetting',
  component: Layout,
  redirect: '/appSetting/publicAccount/wxMenus',
  name: 'appSetting',
  meta: {
    title: '应用',
    icon: 'clipboard'
  },
  children: [
    {
      path: 'publicAccount',
      name: 'publicAccount',
      component: () => import('@/views/appSetting/wxAccount'),
      meta: {
        title: '公众号',
        icon: 'clipboard',
        roles: ['admin']
      },
      children: [
        {
          path: 'wxMenus',
          component: () => import('@/views/appSetting/wxAccount/wxMenus'),
          name: 'wxMenus',
          meta: { title: '微信菜单', icon: '' }
        },
        {
          path: 'wxReply',
          component: () => import('@/views/appSetting/wxAccount/reply/index'),
          name: 'wxReply',
          meta: { title: '自动回复', icon: '' },
          children: [
            {
              path: 'follow',
              component: () => import('@/views/appSetting/wxAccount/reply/follow'),
              name: 'wxFollow',
              meta: { title: '微信关注回复', icon: '' }
            },
            {
              path: 'keyword',
              component: () => import('@/views/appSetting/wxAccount/reply/keyword'),
              name: 'wxKeyword',
              meta: { title: '关键字回复', icon: '' }
            },
            {
              path: 'replyIndex',
              component: () => import('@/views/appSetting/wxAccount/reply/follow'),
              name: 'wxReplyIndex',
              meta: { title: '无效关键词回复', icon: '' }
            },
            {
              path: 'keyword/save/:id?',
              name: 'wechatKeywordAdd',
              meta: {
                title: '关键字添加',
                noCache: true,
                activeMenu: `/appSetting/publicAccount/wxReply/keyword`
              },
              hidden: true,
              component: () => import('@/views/appSetting/wxAccount/reply/follow')
            }
          ]
        },
      ]
    },
  ]
}

export default appSettingRouter
