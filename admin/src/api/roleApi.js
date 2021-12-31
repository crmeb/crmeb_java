// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import request from '@/utils/request'

/**
 * 角色详情
 */
export function getRoleById(pram) {
  return request({
    url: `/admin/system/role/info/${pram.roles}`,
    method: 'GET',
  })
}

/**
 * 菜单
 * @param pram
 */
export function menuListApi() {
  return request({
    url: '/admin/getMenus',
    method: 'GET'
  })
}
