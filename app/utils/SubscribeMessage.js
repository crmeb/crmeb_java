const arrTemp =  ["beforePay","afterPay","refundApply", "beforeRecharge", "createBargain","pink"];

// export function auth() {
// 	let tmplIds = {};
// 	let messageTmplIds = uni.getStorageSync(SUBSCRIBE_MESSAGE);
// 	tmplIds = messageTmplIds ? JSON.parse(messageTmplIds) : {};
// 	return tmplIds;
// }

/**
 * 支付成功后订阅消息id
 * 订阅  确认收货通知 订单支付成功  新订单管理员提醒 
 */
export function openPaySubscribe() {
	let tmplIds = uni.getStorageSync('tempID' + arrTemp[0]);
	return subscribe(tmplIds);
}

/**
 * 订单相关订阅消息
 * 送货 发货 取消订单
 */
export function openOrderSubscribe() {
	let tmplIds = uni.getStorageSync('tempID' + arrTemp[1]);
	return subscribe(tmplIds);
}

/**
 * 提现消息订阅
 * 成功 和 失败 消息
 */
// export function openExtrctSubscribe() {
// 	let tmplIds = uni.getStorageSync('tempID' + arrTemp[2]);
// 	return subscribe(tmplIds);
// }

/**
 * 订单退款
 */
export function openOrderRefundSubscribe() {
	let tmplIds = uni.getStorageSync('tempID' + arrTemp[2]);
	return subscribe(tmplIds);
}

/**
 * 充值成功
 */
export function openRechargeSubscribe() {
	let tmplIds = uni.getStorageSync('tempID' + arrTemp[3]);
	return subscribe(tmplIds);
}

/**
 * 砍价成功
 */
export function openBargainSubscribe() {
	let tmplIds = uni.getStorageSync('tempID' + arrTemp[4]);
	return subscribe(tmplIds);
}


/**
 * 拼团成功
 */
export function openPinkSubscribe() {
	let tmplIds = uni.getStorageSync('tempID' + arrTemp[5]);
	return subscribe(tmplIds);
}
// /**
//  * 提现
//  */
// export function openEextractSubscribe() {
// 	let tmplIds = JSON.parse(uni.getStorageSync('tempID' + paySubscribe));
// 	return subscribe(tmplIds);
// }

/**
 * 调起订阅界面
 * array tmplIds 模板id
 */
export function subscribe(tmplIds) {
	 let wecaht = wx;
	return new Promise((reslove, reject) => {
		wecaht.requestSubscribeMessage({
			tmplIds: tmplIds,
			success(res) {
				return reslove(res);
			},
			fail(res) {
				return reslove(res);
			}
		})
	});
}
