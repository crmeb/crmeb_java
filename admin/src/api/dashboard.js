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

// 首页数据概览
export function viewModelApi() {
  return request({
    url: '/admin/statistics/home/index',
    method: 'GET',
  })
}

// 用户曲线图
export function chartUserApi() {
  return request({
    url: '/admin/statistics/home/chart/user',
    method: 'get'
  })
}

// 用户购买统计
export function chartBuyApi() {
  return request({
    url: '/admin/statistics/home/chart/user/buy',
    method: 'get'
  })
}

// 订单量趋势 30天
export function chartOrder30Api() {
  return request({
    url: '/admin/statistics/home/chart/order',
    method: 'get'
  })
}

// 订单量趋势 月
export function chartOrderMonthApi() {
  return request({
    url: '/admin/statistics/home/chart/order/month',
    method: 'get'
  })
}

// 订单量趋势 周
export function chartOrderWeekApi() {
  return request({
    url: '/admin/statistics/home/chart/order/week',
    method: 'get'
  })
}

// 订单量趋势 年
export function chartOrderYearApi() {
  return request({
    url: '/admin/statistics/home/chart/order/year',
    method: 'get'
  })
}

// 首页经营数据
export function businessData() {
  return request({
    url: '/admin/statistics/home/operating/data',
    method: 'get',
  })
}
