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
        {
          path: 'template/:type?',
          component: () => import('@/views/appSetting/wxAccount/wxTemplate'),
          name: 'wxTemplate',
          meta: { title: '微信模板消息', icon: '' }
        }
      ]
    },
    {
      path: 'publicRoutine',
      name: 'PublicRoutine',
      component: () => import('@/views/appSetting/routine'),
      meta: {
        title: '小程序',
        icon: 'clipboard',
        roles: ['admin'],
        hidden: true
      },
      children: [
        {
          path: 'template/:type?',
          component: () => import('@/views/appSetting/wxAccount/wxTemplate'),
          name: 'RoutineTemplate',
          meta: { title: '小程序订阅消息', icon: '' }
        },
        {
          path: 'routineTemplate',
          component: () => import('@/views/appSetting/routine/myTemplate'),
          name: 'RoutineTemplate',
          meta: { title: '我的模板', icon: '' }
        },
        {
          path: 'publicRoutineTemplate',
          component: () => import('@/views/appSetting/routine/publicTemplate/index.vue'),
          name: 'PublicRoutineTemplate',
          meta: { title: '公共模板', icon: '' }
        },
        {
          path: 'creatPublicTemplate/:tid/:id/:myId?',
          component: () => import('@/views/appSetting/routine/publicTemplate/creatPublicTemplate.vue'),
          name: 'CreatPublicTemplate',
          meta: { title: '添加公共模板', icon: '', activeMenu: `/appSetting/publicRoutine/publicRoutineTemplate` },
          hidden: true
        }
      ]
    }
  ]
}

export default appSettingRouter
