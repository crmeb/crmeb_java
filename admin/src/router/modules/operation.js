import Layout from '@/layout'

const operationRouter = {
  path: '/operation',
  component: Layout,
  redirect: '/operation/setting',
  name: 'Operation',
  meta: {
    title: '设置',
    icon: 'clipboard',
    roles: ['admin']
  },
  children: [
    {
      path: 'setting',
      name: 'setting',
      component: () => import('@/views/systemSetting/setting'),
      meta: {
        title: '系统设置',
        icon: 'clipboard'
      }
    },
    {
      path: 'onePass',
      name: 'onePass',
      component: () => import('@/views/sms/smsConfig'),
      meta: {
        title: '一号通',
        icon: 'clipboard'
      }
    },
    {
      path: 'roleManager',
      name: 'RoleManager',
      component: () => import('@/views/systemSetting/administratorAuthority'),
      meta: {
        title: '管理权限',
        icon: 'clipboard',
        roles: ['admin']
      },
      children: [
        {
          path: 'identityManager',
          component: () => import('@/views/systemSetting/administratorAuthority/identityManager'),
          name: 'identityManager',
          meta: { title: '身份管理', icon: '' }
        },
        {
          path: 'adminList',
          component: () => import('@/views/systemSetting/administratorAuthority/adminList'),
          name: 'adminList',
          meta: { title: '管理员列表', icon: '' }
        },
        {
          path: 'promiseRules',
          component: () => import('@/views/systemSetting/administratorAuthority/permissionRules'),
          name: 'promiseRules',
          meta: { title: '权限规则', icon: '' }
        }
      ]
    },
    {
      path: 'logistics',
      name: 'Logistics',
      alwaysShow: true,
      redirect: '/logistics/cityList',
      component: () => import('@/views/systemSetting/administratorAuthority'),
      meta: {
        title: '物流设置',
        icon: 'clipboard',
        roles: ['admin']
      },
      children: [
        {
          path: 'cityList',
          component: () => import('@/views/systemSetting/logistics/cityList'),
          name: 'cityList',
          meta: { title: '城市数据', icon: '' }
        },
        {
          path: 'companyList',
          component: () => import('@/views/systemSetting/logistics/companyList'),
          name: 'companyList',
          meta: { title: '物流公司', icon: '' }
        },
        {
          path: 'shippingTemplates',
          component: () => import('@/views/systemSetting/logistics/shippingTemplates'),
          name: 'shippingTemplates',
          meta: { title: '运费模板', icon: '' }
        },
        {
          path: 'logisticsConfig',
          component: () => import('@/views/systemSetting/logistics/config'),
          name: 'logisticsConfig',
          meta: { title: '物流配置', icon: '' }
        }
      ]
    },
    {
      path: 'systemStore',
      name: 'SystemStore',
      alwaysShow: true,
      redirect: '/systemStore/point',
      component: () => import('@/views/systemSetting/systemStore'),
      meta: {
        title: '提货点设置',
        icon: 'clipboard',
        roles: ['admin']
      },
      children: [
        {
          path: 'point',
          component: () => import('@/views/systemSetting/systemStore/point'),
          name: 'point',
          meta: { title: '提货点', icon: '' }
        },
        {
          path: 'clerkList',
          component: () => import('@/views/systemSetting/systemStore/clerkList'),
          name: 'clerkList',
          meta: { title: '核销员', icon: '' }
        },
        {
          path: 'order',
          component: () => import('@/views/systemSetting/systemStore/order'),
          name: 'order',
          meta: { title: '核销订单', icon: '' }
        }
      ]
    },
    {
      path: 'systemSms',
      component: () => import('@/views/sms'),
      name: 'systemSms',
      meta: {
        title: '短信设置',
        icon: 'clipboard',
        roles: ['admin']
      },
      children: [
        {
          path: 'config',
          component: () => import('@/views/sms/smsConfig'),
          name: 'SmsConfig',
          meta: { title: '短信账户', noCache: true }
        },
        {
          path: 'template',
          component: () => import('@/views/sms/smsTemplate'),
          name: 'SmsTemplate',
          meta: { title: '短信模板', noCache: true, activeMenu: `/operation/onePass` }
        },
        {
          path: 'pay',
          component: () => import('@/views/sms/smsPay'),
          name: 'SmsPay',
          meta: { title: '短信购买', noCache: true, activeMenu: `/operation/onePass` }
        },
        {
          path: 'message',
          component: () => import('@/views/sms/smsMessage'),
          name: 'SmsMessage',
          meta: { title: '短信开关', noCache: true }
        }
      ]
    },
    {
      path: 'storeService',
      component: () => import('@/views/systemSetting/storeService'),
      name: 'StoreService',
      meta: {
        title: '客服管理',
        icon: 'clipboard',
        roles: ['admin']
      },
      children: [
        {
          path: 'list',
          component: () => import('@/views/systemSetting/storeService/chatRoom'),
          name: 'StoreServiceList',
          meta: { title: '客服列表', noCache: true }
        },
        {
          path: 'chatRoom',
          component: () => import('@/views/systemSetting/storeService/list'),
          name: 'ChatRoom',
          meta: { title: '聊天室', noCache: true }
        }
      ]
    },
  ]
}

export default operationRouter
