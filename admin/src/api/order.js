import request from '@/utils/request'

/**
 * 订单 列表
 * @param pram
 */
export function orderListApi(params) {
  return request({
    url: '/admin/store/order/list',
    method: 'get',
    params
  })
}


/**
 * 订单 删除
 * @param pram
 */
export function orderDeleteApi(params) {
  return request({
    url: '/admin/store/order/delete',
    method: 'get',
    params
  })
}

/**
 * 订单 编辑
 * @param pram
 */
export function orderUpdateApi(data, params) {
  return request({
    url: '/admin/store/order/update',
    method: 'post',
    data,
    params
  })
}

/**
 * 订单 记录
 * @param pram
 */
export function orderLogApi(params) {
  return request({
    url: '/admin/store/order/status/list',
    method: 'get',
    params
  })
}

/**
 * 订单 详情
 * @param pram
 */
export function orderDetailApi(params) {
  return request({
    url: '/admin/store/order/info',
    method: 'get',
    params
  })
}

/**
 * 订单 备注
 * @param pram
 */
export function orderMarkApi(params) {
  return request({
    url: '/admin/store/order/mark',
    method: 'post',
    params
  })
}

/**
 * 订单 发货
 * @param pram
 */
export function orderSendApi(params) {
  return request({
    url: '/admin/store/order/send',
    method: 'get',
    params
  })
}

/**
 * 订单 拒绝退款
 * @param pram
 */
export function orderRefuseApi(params) {
  return request({
    url: '/admin/store/order/refund/refuse',
    method: 'get',
    params
  })
}

/**
 * 订单 立即退款
 * @param pram
 */
export function orderRefundApi(params) {
  return request({
    url: '/admin/store/order/refund',
    method: 'get',
    params
  })
}
