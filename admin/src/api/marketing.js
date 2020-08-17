import request from '@/utils/request'

/**
 * 优惠券 列表
 * @param pram
 */
export function marketingListApi(params) {
  return request({
    url: '/admin/marketing/coupon/list',
    method: 'get',
    params
  })
}


/**
 * 优惠券 详情
 * @param pram
 */
export function couponInfoApi(params) {
  return request({
    url: '/admin/marketing/coupon/info',
    method: 'post',
    params
  })
}

/**
 * 优惠券 发送
 * @param pram
 */
export function couponUserApi(params) {
  return request({
    url: '/admin/marketing/coupon/user/receive',
    method: 'post',
    params
  })
}

/**
 * 优惠券 发送
 * @param pram
 */
export function couponSaveApi(data) {
  return request({
    url: '/admin/marketing/coupon/save',
    method: 'post',
    data
  })
}

/**
 * 优惠券 修改状态
 * @param pram
 */
export function couponIssueStatusApi(params) {
  return request({
    url: '/admin/marketing/coupon/update/status',
    method: 'post',
    params
  })
}

/**
 * 会员领取记录 列表
 * @param pram
 */
export function couponUserListApi(params) {
  return request({
    url: '/admin/marketing/coupon/user/list',
    method: 'get',
    params
  })
}

/**
 * 积分日志 列表
 * @param pram
 */
export function integralListApi(params, data) {
  return request({
    url: '/admin/user/bill/list',
    method: 'post',
    params,
    data
  })
}
