// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

/** When your routing table is too long, you can split it into small modules **/

import Layout from '@/layout';

const maintainRouter = {
  path: '/maintain',
  component: Layout,
  redirect: '/maintain/devconfiguration/configCategory',
  name: 'maintain',
  meta: {
    title: '维护',
    icon: 'clipboard',
  },
  children: [
    {
      path: 'devconfiguration',
      name: 'devconfiguration',
      component: () => import('@/views/maintain'),
      meta: {
        title: '开发配置',
        icon: 'clipboard',
      },
      children: [
        {
          path: 'configCategory',
          name: 'configCategory',
          component: () => import('@/views/maintain/devconfig/configCategroy'),
          meta: {
            title: '配置分类',
            icon: 'clipboard',
          },
        },
        {
          path: 'combineddata',
          name: 'combineddata',
          component: () => import('@/views/maintain/devconfig/combinedData'),
          meta: {
            title: '组合数据',
            icon: 'clipboard',
          },
        },
        {
          path: 'formConfig',
          name: 'formConfig',
          component: () => import('@/views/maintain/formConfig/index'),
          meta: {
            title: '表单配置',
            icon: 'clipboard',
          },
        },
      ],
    },
    {
      path: 'user',
      name: 'MaintainUser',
      component: () => import('@/views/maintain/user'),
      meta: {
        title: '个人中心',
        icon: 'clipboard',
      },
      hidden: true,
    },
    {
      path: 'update',
      name: 'MaintainUpdate',
      component: () => import('@/views/maintain/user/update'),
      meta: {
        title: '修改密码',
        icon: 'clipboard',
      },
      hidden: true,
    },
    {
      path: 'picture',
      name: 'picture',
      component: () => import('@/views/maintain/picture'),
      meta: {
        title: '素材管理',
        icon: 'clipboard',
      },
      hidden: false,
    },
    {
      path: 'logistics',
      name: 'Logistics',
      alwaysShow: true,
      redirect: '/logistics/cityList',
      component: () => import('@/views/maintain'),
      meta: {
        title: '物流设置',
        icon: 'clipboard',
        roles: ['admin'],
      },
      children: [
        {
          path: 'cityList',
          component: () => import('@/views/maintain/logistics/cityList'),
          name: 'cityList',
          meta: { title: '城市数据', icon: '' },
        },
        {
          path: 'companyList',
          component: () => import('@/views/maintain/logistics/companyList'),
          name: 'companyList',
          meta: { title: '物流公司', icon: '' },
        },
      ],
    },
    {
      path: 'clearCache',
      name: 'clearCache',
      component: () => import('@/views/maintain/clearCache'),
      meta: {
        title: '缓存清除',
        icon: 'clipboard',
      },
      hidden: false,
    },
    {
      path: 'schedule',
      name: 'schedule',
      component: () => import('@/views/maintain/schedule'),
      meta: {
        title: '定时任务管理',
        icon: 'clipboard',
        roles: ['admin'],
      },
      children: [
        {
          path: 'list',
          component: () => import('@/views/maintain/schedule/list'),
          name: 'list',
          meta: { title: '定时任务', icon: '' },
        },
        {
          path: 'logList',
          component: () => import('@/views/maintain/schedule/logList'),
          name: 'logList',
          meta: { title: '定时任务日志', icon: '' },
        },
      ],
    },
  ],
};

export default maintainRouter;
