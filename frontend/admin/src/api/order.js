// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import request from '@/utils/request';

/**
 * 订单 列表
 * @param prams
 */
export function orderListApi(params) {
  return request({
    url: '/admin/store/order/list',
    method: 'get',
    params,
  });
}

/**
 * 订单 列表 获取各状态数量
 * @param params
 */
export function orderStatusNumApi(params) {
  return request({
    url: '/admin/store/order/status/num',
    method: 'get',
    params,
  });
}

/**
 * 订单 列表 数据统计
 * @param params
 */
export function orderListDataApi(params) {
  return request({
    url: '/admin/store/order/list/data',
    method: 'get',
    params,
  });
}
/**
 * 订单 删除
 * @param params
 */
export function orderDeleteApi(params) {
  return request({
    url: '/admin/store/order/delete',
    method: 'get',
    params,
  });
}

/**
 * 订单 编辑
 * @param prams
 */
export function orderUpdateApi(data, params) {
  return request({
    url: '/admin/store/order/update',
    method: 'post',
    data,
    params,
  });
}

/**
 * 订单 记录
 * @param prams
 */
export function orderLogApi(params) {
  return request({
    url: '/admin/store/order/status/list',
    method: 'get',
    params,
  });
}

/**
 * 订单 详情
 * @param prams
 */
export function orderDetailApi(params) {
  return request({
    url: '/admin/store/order/info',
    method: 'get',
    params,
  });
}

/**
 * 订单 备注
 * @param prams
 */
export function orderMarkApi(params) {
  return request({
    url: '/admin/store/order/mark',
    method: 'post',
    params,
  });
}

/**
 * 订单 发货
 * @param prams
 */
export function orderSendApi(data) {
  return request({
    url: '/admin/store/order/send',
    method: 'post',
    data,
  });
}

/**
 * 订单 拒绝退款
 * @param prams
 */
export function orderRefuseApi(params) {
  return request({
    url: '/admin/store/order/refund/refuse',
    method: 'get',
    params,
  });
}

/**
 * 订单 立即退款
 * @param prams
 */
export function orderRefundApi(params) {
  return request({
    url: '/admin/store/order/refund',
    method: 'get',
    params,
  });
}

/**
 * 一键改价
 */
export function updatePriceApi(data) {
  return request({
    url: `admin/store/order/update/price`,
    method: 'post',
    data,
  });
}

/**
 *面单默认配置信息
 */
export function sheetInfoApi() {
  return request({
    url: `/admin/store/order/sheet/info`,
    method: 'get',
  });
}

/**
 *面单默认配置信息
 */
export function getLogisticsInfoApi(params) {
  return request({
    url: `/admin/store/order/getLogisticsInfo`,
    method: 'get',
    params,
  });
}

/**
 *打印小票
 */
export function orderPrint(id) {
  return request({
    url: `/admin/yly/print/${id}`,
    method: 'get',
  });
}

/**
 *更改订单运单号
 */
export function updateTrackingNumberApi(data) {
  return request({
    url: `/admin/store/order/update/tracking/number`,
    method: 'post',
    data,
  });
}

/**
 * 退款订单详情（原代码引用但未定义，补占位避免 build 失败）
 */
export function refundOrderDetailApi(params) {
  return request({
    url: `/admin/store/order/refund/detail`,
    method: 'get',
    params,
  });
}


export function editPriceApi(params) {
  return request({ url: `/admin/placeholder/editPrice`, method: 'get', params });
}
