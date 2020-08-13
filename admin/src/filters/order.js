//订单过滤器

/**
 * @description 支付状态
 */
export function paidFilter(status) {
  const statusMap = {
    true: '未支付',
    false: '已支付'
  }
  return statusMap[status]
}

/**
 * @description 订单状态
 */
export function orderStatusFilter(status) {
  const statusMap = {
    '0': '待发货',
    '1': '待收货',
    '2': '已收货',
    '3': '待评价',
    '-1': '已退款'
  }
  return statusMap[status]
}
