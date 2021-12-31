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
 * 新增
 * @param 
 */
export function addRole(pram) {
  const data = {
    level: pram.level,
    roleName: pram.roleName,
    status: pram.status,
    rules: pram.rules
  }
  // data.rules = pram.rules.join(',')
  return request({
    url: '/admin/system/role/save',
    method: 'POST',
    data: data
  })
}

/**
 * 删除
 * @param 
 */
export function delRole(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/system/role/delete',
    method: 'GET',
    params: data
  })
}

/**
 * 详情
 * @param 
 */
export function getInfo(pram) {
  return request({
    url: `/admin/system/role/info/${pram}`,
    method: 'GET',
  })
}

/**
 * 分页列表
 * @param 
 */
export function getRoleList(pram) {
  const data = {
    createTime: pram.createTime,
    updateTime: pram.updateTime,
    level: pram.level,
    page: pram.page,
    limit: pram.limit,
    roleName: pram.roleName,
    rules: pram.rules,
    status: pram.status
  }
  return request({
    url: '/admin/system/role/list',
    method: 'get',
    params: data
  })
}

/**
 * 修改
 * @param 
 */
export function updateRole(pram) {
  const data = {
    id: pram.id,
    roleName: pram.roleName,
    rules: pram.rules,
    status: pram.status
  }
  return request({
    url: '/admin/system/role/update',
    method: 'post',
    params: {id: pram.id},
    data: data
  })
}

/**
 * 修改身份状态
 * @param 
 */
export function updateRoleStatus(pram) {
  return request({
    url: '/admin/system/role/updateStatus',
    method: 'get',
    params: {id: pram.id,status:pram.status},
  })
}

/**
 * 缓存菜单
 * @param 
 */
 export function menuCacheList(pram) {
  return request({
    url: '/admin/system/menu/cache/tree',
    method: 'get',
  })
}