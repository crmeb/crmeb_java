// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import Layout from '@/layout';

const operationRouter = {
  path: '/operation',
  component: Layout,
  redirect: '/operation/setting',
  name: 'Operation',
  meta: {
    title: '设置',
    icon: 'clipboard',
    roles: ['admin'],
  },
  children: [
    {
      path: 'setting',
      name: 'setting',
      component: () => import('@/views/systemSetting/setting'),
      meta: {
        title: '系统设置',
        icon: 'clipboard',
      },
    },
    {
      path: 'guide',
      name: 'guide',
      component: () => import('@/views/systemSetting/guide'),
      meta: {
        title: '配置引导',
        icon: 'clipboard',
      },
    },
    {
      path: 'notification',
      name: 'notification',
      component: () => import('@/views/systemSetting/notification'),
      meta: {
        title: '消息通知',
        icon: 'clipboard',
      },
    },
    {
      path: 'onePassConfig',
      name: 'onePassConfig',
      component: () => import('@/views/sms/smsConfig/config'),
      meta: {
        title: '一号通配置',
        icon: 'clipboard',
      },
    },
    {
      path: 'roleManager',
      name: 'RoleManager',
      component: () => import('@/views/systemSetting/administratorAuthority'),
      meta: {
        title: '管理权限',
        icon: 'clipboard',
        roles: ['admin'],
      },
      children: [
        {
          path: 'adminList',
          component: () => import('@/views/systemSetting/administratorAuthority/adminList'),
          name: 'adminList',
          meta: { title: '管理员列表', icon: '' },
        },
        {
          path: 'identityManager',
          component: () => import('@/views/systemSetting/administratorAuthority/identityManager'),
          name: 'identityManager',
          meta: { title: '角色管理', icon: '' },
        },
        {
          path: 'promiseRules',
          component: () => import('@/views/systemSetting/administratorAuthority/permissionRules'),
          name: 'promiseRules',
          meta: { title: '权限规则', icon: '' },
        },
      ],
    },
    {
      path: 'logistics',
      name: 'LogisticsSettings',
      alwaysShow: true,
      redirect: '/operation/logistics/freightSet',
      component: () => import('@/views/systemSetting/deliverGoods'),
      meta: {
        title: '物流设置',
        roles: ['admin'],
      },
      children: [
        {
          path: 'freightSet',
          component: () => import('@/views/systemSetting/deliverGoods/freightSet'),
          name: 'freightSet',
          meta: { title: '运费模板', noCache: true },
        },
        {
          path: 'companyList',
          component: () => import('@/views/maintain/logistics/companyList'),
          name: 'companyList',
          meta: { title: '物流公司', icon: '' },
        },
        {
          path: 'cityList',
          component: () => import('@/views/maintain/logistics/cityList'),
          name: 'cityList',
          meta: { title: '城市数据', icon: '' },
        },
      ],
    },
    {
      path: 'systemSms',
      component: () => import('@/views/sms'),
      name: 'systemSms',
      meta: {
        title: '短信设置',
        icon: 'clipboard',
        roles: ['admin'],
      },
      children: [
        {
          path: 'config',
          redirect: '/operation/setting',
          name: 'SmsConfig',
          meta: { title: '短信账户', noCache: true },
        },
        {
          path: 'template',
          component: () => import('@/views/sms/smsTemplate'),
          name: 'SmsTemplate',
          meta: { title: '短信模板', noCache: true, activeMenu: `/operation/onePassConfig` },
        },
        {
          path: 'pay',
          component: () => import('@/views/sms/smsPay'),
          name: 'SmsPay',
          meta: { title: '短信购买', noCache: true, activeMenu: `/operation/onePassConfig` },
        },
        {
          path: 'message',
          component: () => import('@/views/sms/smsMessage'),
          name: 'SmsMessage',
          meta: { title: '短信开关', noCache: true },
        },
      ],
    },
    {
      path: 'deliverGoods',
      name: 'LegacyDeliverGoods',
      alwaysShow: true,
      component: () => import('@/views/systemSetting/deliverGoods'),
      meta: {
        title: '发货设置',
        roles: ['admin'],
      },
      hidden: true,
      children: [
        {
          path: 'freightSet',
          component: () => import('@/views/systemSetting/deliverGoods/freightSet'),
          name: 'LegacyFreightSet',
          meta: { title: '运费模板', noCache: true },
        },
      ],
    },
    {
      path: 'agreement',
      name: 'agreement',
      component: () => import('@/views/systemSetting/agreement'),
      meta: {
        title: '协议管理',
      },
    },
  ],
};

export default operationRouter; //collate
