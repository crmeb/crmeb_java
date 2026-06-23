// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import store from '@/store'
import {
	preOrderApi
} from '@/api/order.js';
import {
	tokenIsExistApi
} from '@/api/api.js';
import {
	toLogin
} from '@/libs/login.js';
import util from 'utils/util'
import animationType from '@/utils/animationType.js'
/**
 * 去商品详情
 */
export function goShopDetail(item, uid) {
	return new Promise(resolve => {
		if (item.activityH5 && item.activityH5.type === "1") {
			uni.navigateTo({
				url: `/pages/activity/goods_seckill_details/index?id=${item.activityH5.id}`
			})
		} else if (item.activityH5 && item.activityH5.type === "2") {
			uni.navigateTo({
				url: `/pages/activity/goods_bargain_details/index?id=${item.activityH5.id}&startBargainUid=${uid}`
			})
		} else if (item.activityH5 && item.activityH5.type === "3") {
			uni.navigateTo({
				url: `/pages/activity/goods_combination_details/index?id=${item.activityH5.id}`
			})
		} else {
			resolve(item);
		}
	});
}

/**
 * 活动商品、普通商品、购物车、再次购买预下单
 */
export function getPreOrder(preOrderType, orderDetails) {
	return new Promise((resolve, reject) => {
		preOrderApi({
			"preOrderType": preOrderType,
			"orderDetails": orderDetails
		}).then(res => {
			uni.navigateTo({
				url: '/pages/order/order_confirm/index?preOrderNo=' + res.data.preOrderNo
			});
		}).catch(err => {
			// 如果token此时失效
			tokenIsExistApi().then(tokenRes => {
				let tokenIsExist = tokenRes.data;
				if (!tokenIsExist && (preOrderType == 'buyNow' || preOrderType == 'shoppingCart')) {
					uni.navigateTo({
						url: '/pages/users/login/index',
						success: () => {
							store.commit("LOGOUT");
							uni.showToast({
								title: 'token已失效',
								icon: 'none',
								duration: 1000
							})
						}
					})
				} else {
					uni.showToast({
						title: err,
						icon: 'none',
						duration: 1000
					})
				}
			})
		})
	});
}
/**
 * 协议富文本
 */
export function goToAgreement(from) {
	return new Promise(resolve => {
		// #ifdef MP
		uni.navigateTo({
			url: `/pages/goods/agreement_info/index?from=${from}`
		})
		// #endif
		// #ifndef MP
		uni.navigateTo({
			animationType: animationType.type,
			animationDuration: animationType.duration,
			url: `/pages/goods/agreement_info/index?from=${from}`
		})
		// #endif
	});
}