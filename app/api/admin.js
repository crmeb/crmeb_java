import request from "@/utils/request.js";

/**
 * 统计数据
 */
export function getStatisticsInfo() {
  return request.get("/admin/order/statistics", {}, { login: true });
}
/**
 * 订单月统计
 */
export function getStatisticsMonth(where) {
  return request.get("/admin/order/data", where, { login: true });
}
/**
 * 订单月统计
 */
export function getAdminOrderList(where) {
  return request.get("/admin/order/list", where, { login: true });
}
/**
 * 订单改价
 */
export function setAdminOrderPrice(data) {
  return request.post("/admin/order/price", data, { login: true });
}
/**
 * 订单备注
 */
export function setAdminOrderRemark(data) {
  return request.post("/admin/order/remark", data, { login: true });
}
/**
 * 订单详情
 */
export function getAdminOrderDetail(orderId) {
  return request.get("/admin/order/detail/" + orderId, {}, { login: true });
}
/**
 * 订单发货信息获取
 */
export function getAdminOrderDelivery(orderId) {
  return request.get(
    "/admin/order/delivery/gain/" + orderId,
    {},
    { login: true }
  );
}

/**
 * 订单发货保存
 */
export function setAdminOrderDelivery(data) {
  return request.post("/admin/order/delivery/keep", data, { login: true });
}
/**
 * 订单统计图
 */
export function getStatisticsTime(data) {
  return request.get("/admin/order/time", data, { login: true });
}
/**
 * 线下付款订单确认付款
 */
export function setOfflinePay(data) {
  return request.post("/admin/order/offline", data, { login: true });
}
/**
 * 订单确认退款
 */
export function setOrderRefund(data) {
  return request.post("/admin/order/refund", data, { login: true });
}

/**
 * 获取快递公司
 * @returns {*}
 */
export function getLogistics() {
  return request.get("/logistics", {}, { login: false });
}

/**
 * 订单核销
 * @returns {*}
 */
export function orderVerific(verify_code, is_confirm) {
  return request.post("order/order_verific", { verify_code, is_confirm });
}
