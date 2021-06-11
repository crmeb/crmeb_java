import request from "@/utils/request.js";

/**
 * 获取购物车列表
 * @param numType boolean true 购物车数量,false=购物车产品数量
 */
export function getCartCounts(numType,type) {
	return request.get("cart/count?numType=" + numType + "&type=" + type);
}
/**
 * 获取购物车列表
 * 
 */
export function getCartList(data) {
	return request.get("cart/list", data);
}

/**
 * 修改购物车数量
 * @param int cartId  购物车id
 * @param int number 修改数量
 */
export function changeCartNum(cartId, number) {
	return request.post("cart/num", {
		id: cartId,
		number: number
	}, {}, 1);
}
/**
 * 清除购物车
 * @param object ids join(',') 切割成字符串
 */
export function cartDel(ids) {
	if (typeof ids === 'object')
		ids = ids.join(',');
	return request.post('cart/delete', {
		ids: ids
	}, {}, 1);
}

/**
 * 购物车重选提交
 * 
 */
export function getResetCart(data) {
	return request.post('cart/resetcart', data);
}

/**
 * 订单列表
 * @param object data
 */
export function getOrderList(data) {
	return request.get('order/list', data);
}

/**
 * 订单产品信息
 * @param string unique 
 */
export function orderProduct(data) {
	return request.post('order/product', data);
}

/**
 * 订单评价
 * @param object data
 * 
 */
export function orderComment(data) {
	return request.post('order/comment', data);
}

/**
 * 订单支付
 * @param object data
 */
export function orderPay(data) {
	return request.post('order/pay', data);
}

/**
 * 订单统计数据
 */
export function orderData() {
	return request.get('order/data')
}

/**
 * 订单取消
 * @param string id
 * 
 */
export function orderCancel(id) {
	return request.post('order/cancel', {
		id: id
	}, {}, 1);
}

/**
 * 删除已完成订单
 * @param string uni
 * 
 */
export function orderDel(uni) {
	return request.post('order/del', {
		id: uni
	}, {}, 1);
}

/**
 * 订单详情
 * @param string uni 
 */
export function getOrderDetail(uni) {
	return request.get('order/detail/' + uni);
}

/**
 * 再次下单
 * @param string uni
 * 
 */
export function orderAgain(uni) {
	return request.post('order/again', {
		orderNo: uni
	});
}

/**
 * 订单收货
 * @param string uni
 * 
 */
export function orderTake(uni) {
	return request.post('order/take', {
		id: uni
	}, {}, 1);
}

/**
 * 订单查询物流信息
 * @returns {*}
 */
export function express(uni) {
	return request.get("order/express/" + uni);
}

/**
 * 获取退款理由
 * 
 */
export function ordeRefundReason() {
	return request.get('order/refund/reason');
}

/**
 * 订单退款审核
 * @param object data
 */
export function orderRefundVerify(data) {
	return request.post('order/refund', data);
}

/**
 * 订单确认获取订单详细信息
 * @param string cartId
 */
export function orderConfirm(cartId, isNew, addAgain,secKill,combination,bargain) {
	return request.post('order/confirm', {
		cartIds: cartId,
		isNew: isNew,
		addAgain: addAgain,
		secKill: secKill,
		combination:combination,
		bargain:bargain
	});
}

/**
 * 获取当前金额能使用的优惠卷
 * @param string price
 * 
 */
export function getCouponsOrderPrice(preOrderNo) {
	return request.get(`coupons/order/${preOrderNo}`)
}

/**
 * 订单创建
 * @param string key
 * @param object data
 * 
 */
export function orderCreate(data) {
	return request.post('order/create', data);
}

/**
 * 计算订单金额
 * @param key
 * @param data
 * @returns {*}
 */
export function postOrderComputed(data) {
	return request.post("order/computed/price", data);
}

/**
 * 将字符串 转base64
 * @param object data
 */
export function qrcodeApi(data) {
	return request.post('qrcode/str2base64', data, {}, 1);
}

/**
 * 微信订单支付
 * @param object data
 */
export function wechatOrderPay(data) {
	return request.post('pay/payment', data);
}

/**
 * 微信查询支付结果
 * @param object data
 */
export function wechatQueryPayResult(data) {
	return request.get('pay/queryPayResult?orderNo=' + data);
}

/**
 * 申请退款商品详情
 * @param object data
 */
export function applyRefund(orderId) {
	return request.get(`order/apply/refund/${orderId}`);
}

/**
 * 预下单
 * @param object data
 */
export function preOrderApi(data) {
	return request.post(`order/pre/order`, data);
}

/**
 * 加载预下单
 * @param object preOrderNo
 */
export function loadPreOrderApi(preOrderNo) {
	return request.get(`order/load/pre/${preOrderNo}`);
}