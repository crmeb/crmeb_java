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

const storeRouter = {
    path: '/codegen',
    component: Layout,
    redirect: '/codegen/codegenList',
    name: 'Store',
    meta: {
      title: '代码生成',
      icon: 'clipboard'
    },
    children: [
      {
        path: 'codegenList',
        component: () => import('@/views/codegen/codegenList'),
        name: 'StoreIndex',
        meta: { title: '待生成列表', icon: '' }
      }
    ]
  }

export default storeRouter
