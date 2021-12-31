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

export function getMenu() {
  return request({
    url: '/admin/system/role/testMenu',
    method: 'GET'
  })
}

export function adminDel(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/system/admin/delete',
    method: 'GET',
    params: data
  })
}

export function adminInfo(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/system/admin/info',
    method: 'GET',
    params: data
  })
}

export function adminList(params) {
  return request({
    url: '/admin/system/admin/list',
    method: 'GET',
    params
  })
}

export function adminAdd(pram) {
  const data = {
    account: pram.account,
    level: pram.level,
    pwd: pram.pwd,
    realName: pram.realName,
    roles: pram.roles.join(','),
    status: pram.status,
    phone: pram.phone
  }
  return request({
    url: '/admin/system/admin/save',
    method: 'POST',
    data: data
  })
}

export function adminUpdate(pram) {
  const data = {
    account: pram.account,
    level: pram.level,
    pwd: pram.pwd,
    roles:pram.roles,
    realName: pram.realName,
    status: pram.status,
    id: pram.id,
    isDel: pram.isDel
  }
  return request({
    url: '/admin/system/admin/update',
    method: 'POST',
    data
  })
}

/**
 * 修改状态
 * @param pram
 */
export function updateStatusApi(params) {
  return request({
    url: `/admin/system/admin/updateStatus`,
    method: 'get',
    params
  })
}

/**
 * 修改后台管理员是否接收状态
 * @param pram
 */
export function updateIsSmsApi(params) {
  return request({
    url: `/admin/system/admin/update/isSms`,
    method: 'get',
    params
  })
}

/**
 * 权限规则菜单列表
 * @param pram
 */
 export function menuListApi(params) {
  const data = {
    menuType: params.menuType, //菜单类型:M-目录，C-菜单，A-按钮
    name: params.name, //菜单名称
  }
  return request({
    url: `/admin/system/menu/list`,
    method: 'get',
    params:data
  })
}

/**
 * 权限规则新增菜单
 * @param data
 */
 export function menuAdd(data) {
  let systemMenuRequest = data;
  return request({
    url: `/admin/system/menu/add`,
    method: 'post',
    data:systemMenuRequest
  })
}

/**
 * 权限规则删除菜单
 * @param data
 */
 export function menuDelete(id) {
  return request({
    url: `/admin/system/menu/delete/${id}`,
    method: 'post',
  })
}

/**
 * 权限规则菜单详情
 * @param data
 */
 export function menuInfo(id) {
  return request({
    url: `/admin/system/menu/info/${id}`,
    method: 'get',
  })
}

/**
 * 权限规则菜单修改
 * @param data
 */
 export function menuUpdate(data) {
  let systemMenuRequest = data;
  return request({
    url: `/admin/system/menu/update`,
    method: 'post',
    data:systemMenuRequest
  })
}

/**
 * 权限规则修改菜单显示状态
 * @param data
 */
 export function menuUpdateShowStatus(params) {
  return request({
    url: `/admin/system/menu/updateShowStatus`,
    method: 'post',
    params
  })
}

//
/**
 * 权限规则菜单详情
 * @param data
 */
 export function sensitiveListApi(params) {
  return request({
    url: `/admin/log/sensitive/list`,
    method: 'get',
    params
  })
}