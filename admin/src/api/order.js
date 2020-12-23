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
export function orderSendApi(data) {
  return request({
    url: '/admin/store/order/send',
    method: 'post',
    data
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

/**
 * 订单 核销订单
 * @param pram
 */
export function writeUpdateApi(vCode) {
  return request({
    url: `/admin/store/order/writeUpdate/${vCode}`,
    method: 'get'
  })
}

/**
 * 订单 核销码查询待核销订单
 * @param pram
 */
export function writeConfirmApi(vCode) {
  return request({
    url: `/admin/store/order/writeConfirm/${vCode}`,
    method: 'get'
  })
}

/**
 * 订单 统计 头部数据
 */
export function orderStatisticsApi() {
  return request({
    url: `/admin/store/order/statistics`,
    method: 'get'
  })
}

/**
 * 核销订单 月列表数据
 */
export function statisticsDataApi(params) {
  return request({
    url: `/admin/store/order/statisticsData`,
    method: 'get',
    params
  })
}

/**
 * 一键改价
 */
export function editPriceApi(params) {
  return request({
    url: `/admin/store/order/editPrice`,
    method: 'get',
    params
  })
}

/**
 *订单统计详情
 */
export function orderTimeApi(params) {
  return request({
    url: `/admin/store/order/time`,
    method: 'get',
    params
  })
}

/**
 *面单默认配置信息
 */
export function sheetInfoApi() {
  return request({
    url: `/admin/store/order/sheet/info`,
    method: 'get'
  })
}

/**
 *面单默认配置信息
 */
export function getLogisticsInfoApi(params) {
  return request({
    url: `/admin/store/order/getLogisticsInfo`,
    method: 'get',
    params
  })
}
