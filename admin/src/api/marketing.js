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

/**
 * 秒杀配置 列表
 * @param pram
 */
export function seckillListApi(params) {
  return request({
    url: '/admin/store/seckill/manger/list',
    method: 'get',
    params
  })
}

/**
 * 秒杀配置 详情
 * @param pram
 */
export function seckillInfoApi(params) {
  return request({
    url: '/admin/store/seckill/manger/info',
    method: 'get',
    params
  })
}

/**
 * 秒杀配置 新增
 * @param pram
 */
export function seckillSaveApi(data) {
  return request({
    url: '/admin/store/seckill/manger/save',
    method: 'post',
    data
  })
}

/**
 * 秒杀配置 修改
 * @param pram
 */
export function seckillUpdateApi(params, data) {
  return request({
    url: '/admin/store/seckill/manger/update',
    method: 'post',
    params,
    data
  })
}

/**
 * 秒杀配置 删除
 * @param pram
 */
export function seckillDeleteApi(params) {
  return request({
    url: '/admin/store/seckill/manger/delete',
    method: 'get',
    params
  })
}


/**
 * 秒杀商品 列表
 * @param pram
 */
export function seckillStoreListApi(params) {
  return request({
    url: '/admin/store/seckill/list',
    method: 'get',
    params
  })
}

/**
 * 秒杀商品 详情
 * @param pram
 */
export function seckillStoreInfoApi(params) {
  return request({
    url: '/admin/store/seckill/info',
    method: 'get',
    params
  })
}

/**
 * 秒杀商品 新增
 * @param pram
 */
export function seckillStoreSaveApi(data) {
  return request({
    url: '/admin/store/seckill/save',
    method: 'post',
    data
  })
}

/**
 * 秒杀商品 修改
 * @param pram
 */
export function seckillStoreUpdateApi(params, data) {
  return request({
    url: '/admin/store/seckill/update',
    method: 'post',
    params,
    data
  })
}

/**
 * 秒杀商品 删除
 * @param pram
 */
export function seckillStoreDeleteApi(params) {
  return request({
    url: '/admin/store/seckill/delete',
    method: 'get',
    params
  })
}

/**
 * 秒杀商品 修改状态
 */
export function seckillStoreStatusApi(params) {
  return request({
    url: '/admin/store/seckill/update/status',
    method: 'post',
    params
  })
}

/**
 * 秒杀配置 修改状态
 */
export function seckillConfigStatusApi(id,params) {
  return request({
    url: `/admin/store/seckill/manger/update/status/${id}`,
    method: 'post',
    params
  })
}
