//订单过滤器

/**
 * @description 支付状态
 */
export function paidFilter(status) {
  const statusMap = {
    true: '已支付',
    false: '未支付'
  }
  return statusMap[status]
}

/**
 * @description 订单状态
 * 2,已收货，待评价
 */
export function orderStatusFilter(status) {
  const statusMap = {
    '0': '待发货',
    '1': '待收货',
    '2': '待评价',
    '3': '已完成',
    '-2': '已退款',
    '-1': '退款中'
  }
  return statusMap[status]
}

/**
 * @description 退款状态
 * 2,已收货，待评价
 */
export function refundStatusFilter(status) {
  const statusMap = {
    '0': '未退款',
    '1': '申请中',
    '2': '已退款',
    '3': '退款中'
  }
  return statusMap[status]
}
