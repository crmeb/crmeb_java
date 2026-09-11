<template>
	<view>
		<view class="payment" :class="pay_close ? 'on' : ''">
			<view class="title acea-row row-center-wrapper">
				选择付款方式<text class="iconfont icon-guanbi" @click='close'></text>
			</view>
			<template v-for="(item,index) in payMode" :key="index">
				<view class="item acea-row row-between-wrapper" @click='goPay(item.number || 0 , item.value)'
					v-if="item.payStatus==1">
					<view class="left acea-row row-between-wrapper">
						<view class="iconfont" :class="item.icon"></view>
						<view class="text">
							<view class="name">{{item.name}}</view>
							<view class="info" v-if="item.number">
								{{item.title}} <span class="money">￥{{ item.number }}</span>
							</view>
							<view class="info" v-else>{{item.title}}</view>
						</view>
					</view>
					<view class="iconfont icon-xiangyou"></view>
				</view>
			</template>
		</view>
		<view class="mask" @click='close' v-if="pay_close"></view>
		<view class="alipaysubmit" v-html="formContent"></view>
	</view>
</template>

<script setup>
import { ref, nextTick, getCurrentInstance } from 'vue';
import util from '@/utils/util.js';
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from 'pinia';
import {
	orderPay,
	wechatQueryPayResult,
	getPayConfig
} from '@/api/order.js';

const { proxy } = getCurrentInstance();
const appStore = useAppStore();
const { systemPlatform, userInfo, productType } = storeToRefs(appStore);

const props = defineProps({
	pay_close: { type: Boolean, default: false },
	order_id: { type: String, default: '' },
	totalPrice: { type: String, default: '0' }
});

const emit = defineEmits(['onChangeFun']);

const formContent = ref('');
const payChannel = ref('');
// 原组件直接修改 prop order_id（Vue2 反模式），Vue3 中 props 只读，改用本地 ref
const currentOrderId = ref(props.order_id);
const payMode = ref([
	{ "name": "微信支付", "icon": "icon-weixin2", value: 'weixin', title: '微信快捷支付', payStatus: 1 },
	{ "name": "余额支付", "icon": "icon-yuezhifu", value: 'yue', title: '可用余额:', payStatus: 1, number: 0 },
	// #ifndef MP
	{ "name": "支付宝支付", "icon": "icon-zhifubao", value: 'alipay', title: '支付宝快捷支付', payStatus: 1 }
	// #endif
]);

// created
payConfig();
payMode.value[1].number = userInfo.value && userInfo.value.nowMoney || 0;

function close() {
	emit('onChangeFun', { action: 'payClose' });
}

function payConfig() {
	getPayConfig().then(res => {
		if (payMode.value[1]) payMode.value[1].payStatus = parseInt(res.data.yuePayStatus) === 1 ? 1 : 2;
		if (payMode.value[0]) payMode.value[0].payStatus = parseInt(res.data.payWeixinOpen) === 1 ? 1 : 0;
	})
}

function goPay(number, paytype) {
	let goPages = '/pages/order/order_pay_status/index?order_id=' + props.order_id;
	if (!props.order_id) return util.Tips({ title: '请选择要支付的订单' });
	if (paytype == 'yue' && parseFloat(number) < parseFloat(props.totalPrice)) return util.Tips({ title: '余额不足！' });
	uni.showLoading({ title: '支付中' });

	// #ifdef H5
	if (paytype == 'alipay') {
		payChannel.value = 'alipay';
	} else if (paytype == 'weixin' && proxy.$wechat.isWeixin()) {
		payChannel.value = 'public';
	} else {
		payChannel.value = 'weixinh5';
	}
	// #endif
	// #ifdef APP-PLUS
	if (paytype == 'alipay') {
		payChannel.value = 'appAliPay';
	} else if (paytype == 'weixin') {
		payChannel.value = systemPlatform.value === 'ios' ? 'weixinAppIos' : 'weixinAppAndroid';
	}
	// #endif

	orderPay({
		orderNo: props.order_id,
		// #ifdef MP
		payChannel: 'routine',
		// #endif
		// #ifndef MP
		payChannel: payChannel.value,
		// #endif
		payType: paytype,
		scene: productType.value === 'normal' ? 0 : 1177
	}).then(res => {
		let jsConfig = res.data.jsConfig;
		currentOrderId.value = res.data.orderNo;
		switch (res.data.payType) {
			case 'weixin':
				weixinPay(jsConfig);
				break;
			case 'yue':
				uni.hideLoading();
				return util.Tips({ title: '余额支付成功', icon: 'success' }, () => {
					emit('onChangeFun', { action: 'pay_complete' });
				});
			case 'weixinh5':
				uni.hideLoading();
				location.replace(jsConfig.mwebUrl + '&redirect_url=' + window.location.protocol +
					'//' + window.location.host + goPages + '&status=1');
				return util.Tips({ title: "支付中", icon: 'success' }, () => {
					emit('onChangeFun', { action: 'pay_complete' });
				});
			case 'alipay':
				//#ifdef H5
				uni.hideLoading();
				formContent.value = res.data.alipayRequest;
				uni.setStorage({ key: 'orderNo', data: res.data.orderNo });
				nextTick(() => {
					document.forms['punchout_form'].submit();
				})
				//#endif
				// #ifdef APP-PLUS
				uni.requestPayment({
					provider: 'alipay',
					orderInfo: res.data.alipayRequest,
					success: (e) => {
						uni.showToast({ title: "支付成功" })
						setTimeout(() => {
							emit('onChangeFun', { action: 'pay_complete' });
						}, 2000)
					},
					fail: () => {
						uni.showModal({
							content: "支付失败",
							showCancel: false,
							success: function(res) {
								if (res.confirm) {
									emit('onChangeFun', { action: 'pay_fail' });
								}
							}
						})
					},
					complete: () => { uni.hideLoading(); },
				});
				// #endif
				break;
		}
	}).catch(err => {
		uni.hideLoading();
		return util.Tips({ title: err }, () => {
			emit('onChangeFun', { action: 'pay_fail' });
		});
	})
}

function weixinPay(jsConfig) {
	// #ifdef MP
	uni.requestOrderPayment({
		timeStamp: jsConfig.timeStamp,
		nonceStr: jsConfig.nonceStr,
		package: jsConfig.packages,
		signType: jsConfig.signType,
		paySign: jsConfig.paySign,
		ticket: jsConfig.ticket,
		success: function() {
			uni.hideLoading();
			wechatQueryPayResult(currentOrderId.value).then(res => {
				uni.hideLoading();
				return util.Tips({ title: "支付成功", icon: 'success' }, () => {
					emit('onChangeFun', { action: 'pay_complete' });
				});
			}).catch(err => {
				uni.hideLoading();
				return util.Tips({ title: err });
			})
		},
		fail: function() {
			uni.hideLoading();
			return util.Tips({ title: '取消支付' }, () => {
				emit('onChangeFun', { action: 'pay_fail' });
			});
		},
		complete: function(e) {
			uni.hideLoading();
			if (e.errMsg == 'requestPayment:cancel') return util.Tips({ title: '取消支付' }, () => {
				emit('onChangeFun', { action: 'pay_fail' });
			});
		},
	})
	// #endif
	// #ifdef H5
	let datas = {
		timestamp: jsConfig.timeStamp,
		nonceStr: jsConfig.nonceStr,
		package: jsConfig.packages,
		signType: jsConfig.signType,
		paySign: jsConfig.paySign
	};
	proxy.$wechat.pay(datas).then(res => {
		if (res.errMsg == 'chooseWXPay:cancel') {
			uni.hideLoading();
			return util.Tips({ title: '支付失败' });
		} else {
			wechatQueryPayResult(currentOrderId.value).then(res => {
				uni.hideLoading();
				return util.Tips({ title: "支付成功", icon: 'success' }, () => {
					emit('onChangeFun', { action: 'pay_complete' });
				});
			}).catch(err => {
				uni.hideLoading();
				return util.Tips({ title: err });
			})
		}
	}).catch(errW => {
		uni.hideLoading();
		return util.Tips({ title: errW });
	})
	// #endif
	// #ifdef APP-PLUS
	uni.requestPayment({
		provider: 'wxpay',
		orderInfo: {
			"appid": jsConfig.appId,
			"noncestr": jsConfig.nonceStr,
			"package": "Sign=WXPay",
			"partnerid": jsConfig.partnerid,
			"prepayid": jsConfig.packages,
			"timestamp": Number(jsConfig.timeStamp),
			"sign": systemPlatform.value === 'ios' ? 'MD5' : jsConfig.paySign
		},
		success: () => {
			uni.hideLoading();
			uni.showToast({ title: "支付成功" })
			setTimeout(() => {
				emit('onChangeFun', { action: 'pay_complete' });
			}, 2000)
		},
		fail: () => {
			uni.hideLoading();
			uni.showModal({
				content: "支付失败",
				showCancel: false,
				success: function(res) {
					if (res.confirm) {
						emit('onChangeFun', { action: 'pay_fail' });
					}
				}
			})
		},
		complete: () => { uni.hideLoading(); },
	});
	// #endif
}
</script>

<style scoped lang="scss">
	.payment {
		position: fixed;
		bottom: 0;
		left: 0;
		width: 100%;
		border-radius: 16rpx 16rpx 0 0;
		background-color: #fff;
		padding-bottom: 60rpx;
		z-index: 99;
		transition: all 0.3s cubic-bezier(0.25, 0.5, 0.5, 0.9);
		transform: translate3d(0, 100%, 0);
	}

	.payment.on {
		transform: translate3d(0, 0, 0);
	}

	.payment .title {
		text-align: center;
		height: 123rpx;
		font-size: 32rpx;
		color: #282828;
		font-weight: bold;
		padding-right: 30rpx;
		margin-left: 30rpx;
		position: relative;
		border-bottom: 1rpx solid #eee;
	}

	.payment .title .iconfont {
		position: absolute;
		right: 30rpx;
		top: 50%;
		transform: translateY(-50%);
		font-size: 43rpx;
		color: #8a8a8a;
		font-weight: normal;
	}

	.payment .item {
		border-bottom: 1rpx solid #eee;
		height: 130rpx;
		margin-left: 30rpx;
		padding-right: 30rpx;
	}

	.payment .item .left {
		width: 610rpx;
	}

	.payment .item .left .text {
		width: 540rpx;
	}

	.payment .item .left .text .name {
		font-size: 32rpx;
		color: #282828;
	}

	.payment .item .left .text .info {
		font-size: 24rpx;
		color: #999;
	}

	.payment .item .left .text .info .money {
		color: #ff9900;
	}

	.payment .item .left .iconfont {
		font-size: 45rpx;
		color: #09bb07;
	}

	.payment .item .left .iconfont.icon-zhifubao {
		color: #00aaea;
	}

	.payment .item .left .iconfont.icon-yuezhifu {
		color: #ff9900;
	}

	.payment .item .left .iconfont.icon-yuezhifu1 {
		color: #eb6623;
	}

	.payment .item .iconfont {
		font-size: 0.3rpx;
		color: #999;
	}
</style>
