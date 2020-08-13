import request from '@/utils/request'

// 订单量
export function statisticsOrderApi() {
  return request({
    url: '/admin/statistics/home/order',
    method: 'get'
  })
}

// 销售额
export function statisticsSalesApi() {
  return request({
    url: '/admin/statistics/home/sales',
    method: 'get'
  })
}

// 新增用户
export function statisticsUserApi() {
  return request({
    url: '/admin/statistics/home/user',
    method: 'get'
  })
}

// 用户访问量
export function statisticsViewsApi() {
  return request({
    url: '/admin/statistics/home/views',
    method: 'get'
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
