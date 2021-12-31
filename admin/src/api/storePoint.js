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
 * 提货点分页列表
 * @param pram
 */
export function storeListApi(data) {
  return request({
    url: '/admin/system/store/list',
    method: 'get',
    params: data
  })
}

/**
 * 提货点数量
 * @param pram
 */
export function storeGetCountApi() {
  return request({
    url: '/admin/system/store/getCount',
    method: 'get'
  })
}

/**
 * 提货点门店状态
 * @param pram
 */
export function storeUpdateStatusApi(data) {
  return request({
    url: '/admin/system/store/update/status',
    method: 'get',
    params: data
  })
}

/**
 * 提货点门店刪除
 * @param pram
 */
export function storeDeleteApi(data) {
  return request({
    url: '/admin/system/store/delete',
    method: 'get',
    params: data
  })
}

/**
 * 提货点门店回收站刪除
 * @param pram
 */
export function allDeleteApi(params) {
  return request({
    url: '/admin/system/store/completely/delete',
    method: 'get',
    params
  })
}

/**
 * 提货点添加
 * @param pram
 */
export function storeSaveApi(data) {
  return request({
    url: '/admin/system/store/save',
    method: 'post',
    data
  })
}

/**
 * 提货点详情
 * @param pram
 */
export function storeInfoApi(data) {
  return request({
    url: '/admin/system/store/info',
    method: 'get',
    params: data
  })
}

/**
 * 提货点修改
 * @param pram
 */
export function storeUpdateApi(data,id) {
  // const param = ;
  return request({
    url: '/admin/system/store/update',
    method: 'post',
    params: {id: id},
    data
  })
}

/**
 * 提货点恢复
 * @param pram
 */
export function storeRecoveryApi(params) {
  return request({
    url: '/admin/system/store/recovery',
    method: 'get',
    params
  })
}

/**
 * 核销员分页列表
 * @param pram
 */
export function storeStaffListApi(data) {
  return request({
    url: '/admin/system/store/staff/list',
    method: 'get',
    params: data
  })
}

/**
 * 核销员添加
 * @param pram
 */
export function storeStaffSaveApi(data) {
  return request({
    url: '/admin/system/store/staff/save',
    method: 'POST',
    params: data
  })
}

/**
 * 核销员删除
 * @param pram
 */
export function storeStaffDeleteApi(data) {
  return request({
    url: '/admin/system/store/staff/delete',
    method: 'get',
    params: data
  })
}


/**
 * 核销员编辑
 * @param pram
 */
export function storeStaffUpdateApi(data) {
  return request({
    url: '/admin/system/store/staff/update',
    method: 'POST',
    params: data
  })
}

/**
 * 核销员编辑
 * @param pram
 */
export function storeStaffInfoApi(id) {
  return request({
    url: '/admin/system/store/staff/info',
    method: 'get',
    params: id
  })
}

/**
 * 核销员编辑
 * @param pram
 */
export function storeStaffUpdateStatusApi(data) {
  return request({
    url: '/admin/system/store/staff/update/status',
    method: 'get',
    params: data
  })
}

/**
 * 核销员编辑
 * @param pram
 */
export function userListApi(data) {
  return request({
    url: '/admin/wechat/user/list',
    method: 'get',
    params: data
  })
}

/**
 * 核销订单
 * @param pram
 */
export function orderListApi(params) {
  return request({
    url: '/admin/system/store/order/list',
    method: 'post',
    params
  })
}










