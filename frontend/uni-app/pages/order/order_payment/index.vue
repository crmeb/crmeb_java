<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class='wrapper'>
			<view class='item borRadius14'>
				<view class="title">￥<text>{{payPrice}}</text></view>
				<view class='list'>
					<block v-for="(item,index) in cartArr" :key='index'>
						<view v-if="item.payStatus === 1" class='payItem acea-row row-middle'
							:class='active==index ?"on":""' @tap='payItem(index,item)'>
							<view class='name acea-row row-center-wrapper'>
								<view class='iconfont animated'
									:class='(item.icon) + " " + (animated==true&&active==index ?"bounceIn":"")'>
								</view>
								{{item.name}}
							</view>
							<view class="acea-row">
								<view class='tip'>
									{{item.title}}
									<block v-if="item.value === 'yue'">
										{{item.userBalance}}
									</block>
								</view>
								<view class="radio">
									<block v-if="active==index">
										<view class="iconfont icon-xuanzhong1 font-color"></view>
									</block>
									<block v-else>
										<view class="iconfont icon-weixuanzhong"></view>
									</block>
								</view>
							</view>
						</view>
					</block>
				</view>
			</view>
		</view>
		<view v-if="isShow" class="titleNo">暂无支付方式！</view>
		<view class="btn-box">
			<button class='Bnt bg-color' @tap='toOrderPay' :disabled="isBuy">立即支付</button>
		</view>
		<view class="alipaysubmit" v-html="formContent"></view>
	</view>
</template>

<script setup>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	import { ref, nextTick, getCurrentInstance } from 'vue'
	import { onLoad, onUnload } from '@dcloudio/uni-app'
	import { storeToRefs } from 'pinia'
	import {openOrderSubscribe} from '@/utils/SubscribeMessage.js';
	import {
		orderPay,
		wechatQueryPayResult
	} from '@/api/order.js';
	import {
		Debounce
	} from '@/utils/validate.js'
	import { useAppStore } from "@/store/app.js";
import { useColor } from '@/composables/useColor.js';
	let app = getApp();

	const { proxy } = getCurrentInstance();
	const appStore = useAppStore();
	const { productType, systemPlatform } = storeToRefs(appStore);

	const active = ref(null); //支付方式切换
	const theme = ref(app.globalData.theme);
	const { colorStyle } = useColor();
	//支付方式
	//支付方式
	const cartArr = ref([]);
	const payPrice = ref('');
	const orderNo = ref('');
	const animated = ref(false);
	const payType = ref(''); //支付方式
	const payChannel = ref('');
	const formContent = ref('');
	const isShow = ref(false);
	const userBalance = ref(''); //余额
	const isBuy = ref(false); //是否可以点击购买
	const isPaid = ref(false); // 是否点击了立即支付

	onLoad((options) => {
		payPrice.value = options.payPrice;
		orderNo.value = options.orderNo;
		payConfig();
	})

	onUnload(() => {
		if (!isPaid.value) {
			unPayBack()
		}
	})

	// 支付配置
	function payConfig() {
		uni.hideLoading();
		// 支付方式
		appStore.getPayConfig().then((res) => {
			cartArr.value = res.payConfig;
			userBalance.value = res.userBalance;
			if (cartArr.value.length) {
				active.value = 0;
				payType.value = cartArr.value[0].value;
				isShow.value = false;
			} else {
				isShow.value = true;
				return proxy.$util.Tips({
					title: '暂无支付方式！'
				})
			}
		});
	}

	// 支付方式切换（即时响应，不用防抖，避免切换延迟）
	function payItem(e, item) {
		if (item.userBalance) userBalance.value = item.userBalance
		let activeIndex = e;
		active.value = activeIndex;
		animated.value = true;
		payType.value = cartArr.value[activeIndex].value;
		setTimeout(function() {
			car();
		}, 500);
	}

	function car() {
		animated.value = false;
	}

	//选择支付方式的判断，传参
	function getPayCheck() {
		if (!payType.value) return proxy.$util.Tips({
			title: '请选择支付方式'
		});
		if (payType.value === 'yue') {
			payChannel.value = 'yue'
		} else if (payType.value == 'alipay') {
			// #ifdef H5
			payChannel.value = 'alipay';
			// #endif
			// #ifdef APP-PLUS
			payChannel.value = 'appAliPay';
			// #endif
		} else {
			// #ifdef H5
			payChannel.value = proxy.$wechat.isWeixin() ? 'public' : 'weixinh5';
			// #endif
			// #ifdef APP-PLUS
			payChannel.value = systemPlatform.value === 'ios' ? 'weixinAppIos' : 'weixinAppAndroid';
			// #endif
			// #ifdef MP
			payChannel.value = "routine";
			if (productType.value == 'video') {
				payChannel.value = "video";
			} else {
				payChannel.value = "routine";
			}
			// #endif
		}
	}

	function getOrderPay(orderNo, message) {
		let goPages = '/pages/order/order_pay_status/index?order_id=' + orderNo;
		orderPay({
			orderNo: orderNo,
			payChannel: payChannel.value,
			payType: payType.value,
			scene: productType.value === 'normal' ? 0 : 1177 //下单时小程序的场景值
		}).then(res => {
			let jsConfig = res.data.jsConfig;
			switch (res.data.payType) {
				case 'weixin':
					weixinPay(jsConfig, orderNo, goPages);
					break;
				case 'yue':
					return proxy.$util.Tips({
						title: message
					}, {
						tab: 5,
						url: goPages + '&status=1'
					});
					uni.hideLoading();
					break;
				case 'weixinh5':
					setTimeout(() => {
						location.href = jsConfig.mwebUrl + '&redirect_url=' +
							window.location
							.protocol + '//' + window.location.host + goPages +
							'&status=1';
					}, 100)
					uni.hideLoading();
					break;
				case 'alipay':
					//#ifdef H5
					if (proxy.$wechat.isWeixin()) {
						uni.redirectTo({
							url: `/pages/users/alipay_invoke/index?id=${orderNo}&type=order`
						});
					} else {
						//h5支付
						uni.hideLoading();
						formContent.value = res.data.alipayRequest;
						uni.setStorage({
							key: 'orderNo',
							data: orderNo
						});
						nextTick(() => {
							document.forms['punchout_form'].submit();
						})
					}
					//#endif
					// #ifdef APP-PLUS
					let alipayRequest = res.data.alipayRequest;
					uni.requestPayment({
						provider: 'alipay',
						orderInfo: alipayRequest,
						success: (e) => {
							uni.showToast({
								title: "支付成功"
							})
							setTimeout(res => {
								uni.navigateTo({
									url: '/pages/users/alipay_return/alipay_return?out_trade_no=' +
										orderNo +
										'&payChannel=' +
										'appAlipay'
								})
							}, 1000)
						},
						fail: (e) => {
							uni.showModal({
								content: "支付失败",
								showCancel: false,
								success: function(res) {
									if (res.confirm) {
										//点击确认的操作
										uni.navigateTo({
											url: '/pages/users/alipay_return/alipay_return?out_trade_no=' +
												orderNo +
												'&payChannel=' +
												'appAlipay'
										})
									}
								}
							})
						},
						complete: () => {
							uni.hideLoading();
						},
					});
					// #endif
					break;
			}
		}).catch(err => {
			uni.hideLoading();
			return proxy.$util.Tips({
				title: err
			});
		});
	}

	function weixinPay(jsConfig, orderNo, goPages) {
		// #ifdef MP
		if (productType.value === 'video') {
			uni.requestOrderPayment({
				timeStamp: jsConfig.timeStamp,
				nonceStr: jsConfig.nonceStr,
				package: jsConfig.packages,
				signType: jsConfig.signType,
				paySign: jsConfig.paySign,
				ticket: jsConfig.ticket,
				success: function(ress) {
					uni.hideLoading();
					openOrderSubscribe().then(() => {
						return proxy.$util.Tips({
							title: '支付成功',
							icon: 'success'
						}, {
							tab: 5,
							url: goPages
						}, );
					})
				},
				fail: function(e) {
					uni.hideLoading();
					return proxy.$util.Tips({
						title: '取消支付'
					}, {
						tab: 5,
						url: goPages + '&status=2'
					});
				},
				complete: function(e) {
					uni.hideLoading();
					//关闭当前页面跳转至订单状态
					if (e.errMsg == 'requestPayment:cancel') return proxy.$util.Tips({
						title: '取消支付'
					}, {
						tab: 5,
						url: goPages + '&status=2'
					});
				},
			})
		} else {
			uni.requestPayment({
				timeStamp: jsConfig.timeStamp,
				nonceStr: jsConfig.nonceStr,
				package: jsConfig.packages,
				signType: jsConfig.signType,
				paySign: jsConfig.paySign,
				//ticket: jsConfig.ticket,
				success: function(ress) {
					uni.hideLoading();
					openOrderSubscribe().then(() => {
						return proxy.$util.Tips({
							title: '支付成功',
							icon: 'success'
						}, {
							tab: 5,
							url: goPages
						}, );
					})
				},
				fail: function(e) {
					uni.hideLoading();
					return proxy.$util.Tips({
						title: '取消支付'
					}, {
						tab: 5,
						url: goPages + '&status=2'
					});
				},
				complete: function(e) {
					uni.hideLoading();
					//关闭当前页面跳转至订单状态
					if (e.errMsg == 'requestPayment:cancel') return proxy.$util.Tips({
						title: '取消支付'
					}, {
						tab: 5,
						url: goPages + '&status=2'
					});
				},
			})
		}
	
		// #endif
		// #ifdef H5
		let data = {
			timestamp: jsConfig.timeStamp,
			nonceStr: jsConfig.nonceStr,
			package: jsConfig.packages,
			signType: jsConfig.signType,
			paySign: jsConfig.paySign
		};
		proxy.$wechat.pay(data).then(res => {
			if (res.errMsg == 'chooseWXPay:cancel') {
				uni.hideLoading();
				return proxy.$util.Tips({
					title: '取消支付'
				}, {
					tab: 5,
					url: goPages + '&status=2'
				});
			} else {
				wechatQueryPayResult(orderNo).then(res => {
					uni.hideLoading();
					return proxy.$util.Tips({
						title: '支付成功',
						icon: 'success'
					}, {
						tab: 5,
						url: goPages
					});
				}).catch(err => {
					uni.hideLoading();
					return proxy.$util.Tips({
						title: err
					});
				})
			}
		}).catch(res => {
			uni.hideLoading();
			return proxy.$util.Tips({
				title: '取消支付'
			}, {
				tab: 5,
				url: goPages + '&status=2'
			});
		});
		// #endif
		// #ifdef APP-PLUS
		uni.requestPayment({
			provider: 'wxpay',
			orderInfo: {
				"appid": jsConfig.appId, // 微信开放平台 - 应用 - AppId，注意和微信小程序、公众号 AppId 可能不一致
				"noncestr": jsConfig.nonceStr, // 随机字符串
				"package": "Sign=WXPay", // 固定值
				"partnerid": jsConfig.partnerid, // 微信支付商户号
				"prepayid": jsConfig.packages, // 统一下单订单号
				"timestamp": Number(jsConfig.timeStamp), // 时间戳（单位：秒）
				"sign": systemPlatform.value === 'ios' ? 'MD5' : jsConfig
					.paySign // 签名，这里用的 MD5 签名
			}, //微信、支付宝订单数据 【注意微信的订单信息，键值应该全部是小写，不能采用驼峰命名】
			success: function(res) {
				wechatQueryPayResult(orderNo).then(res => {
					uni.hideLoading();
					let url = '/pages/order/order_pay_status/index?order_id=' + orderNo +
						'&msg=支付成功';
					uni.showToast({
						title: "支付成功"
					})
					setTimeout(res => {
						uni.redirectTo({
							url: url
						})
					}, 2000)
				}).catch(err => {
					uni.hideLoading();
					return proxy.$util.Tips({
						title: err
					});
				})
			},
			fail: function(err) {
				uni.hideLoading();
				let url = '/pages/order/order_pay_status/index?order_id=' + orderNo +
					'&msg=支付失败';
				uni.showModal({
					content: "支付失败",
					showCancel: false,
					success: function(res) {
						if (res.confirm) {
							uni.redirectTo({
								url: url
							})
						}
					}
				})
			},
			complete: (err) => {
				uni.hideLoading();
			}
		});
		// #endif
	}

	//立即支付
	const toOrderPay = Debounce(function() {
		getPayCheck();
		isPaid.value = true
		if (Number(payPrice.value) > Number(userBalance.value) && payType.value === 'yue') return proxy.$util
			.Tips({
				title: '余额的金额不够，请切换支付方式'
			});
		uni.showLoading({
			title: '加载中...'
		});
		isBuy.value = true;
		getOrderPay(orderNo.value, '支付成功')
	});

	// 未支付多级返回
	function unPayBack() {
		uni.navigateBack({
			delta: 1
		});
	}
</script>

<style lang="scss" scoped>
	.titleNo {
		width: 100%;
		font-size: 28rpx;
		text-align: center;
	}

	.btn-box {
		padding: 0 30rpx;
		position: fixed;
		bottom: 43rpx;
	}

	.Bnt {
		font-size: 30rpx;
		font-weight: bold;
		color: #fff;
		width: 690rpx;
		height: 86rpx;
		border-radius: 43rpx;
		text-align: center;
		line-height: 86rpx;
	}

	.wrapper {

		padding: 30rpx;

		.list {
			margin-top: 50rpx;
		}

		.item {
			padding: 50rpx 30rpx;
			font-size: 30rpx;
			color: #333333;
			background-color: #fff;

			.title {
				text-align: center;
				@include main_color(theme);
				font-size: 34rpx;

				text {
					font-weight: 800;
					font-size: 50rpx;
				}
			}
		}

		.payItem {
			border-bottom: 1px solid #eee;
			justify-content: space-between;
			height: 138rpx;
			line-height: 138rpx;
			width: 100%;
			box-sizing: border-box;
			font-size: 32pxrpx;
			color: #333333;

			.on {
				// border-color: #fc5445;
				@include coupons_border_color(theme);
				color: $theme-color;
			}

			.name {

				.iconfont {
					width: 48rpx;
					height: 48rpx;
					border-radius: 50%;
					text-align: center;
					line-height: 48rpx;
					background-color: #fe960f;
					color: #fff;
					font-size: 30rpx;
					margin-right: 28rpx;
				}
			}

			.iconfont.icon-weixinzhifu1 {
				background-color: #41b035;
			}

			.iconfont.icon-zhifubao {
				background-color: #00AAEA;
			}

			.tip {
				text-align: center;
				font-size: 26rpx;
				color: #aaa;
				margin-right: 20rpx;
			}

			.radio {
				.iconfont {
					font-size: 46rpx;
				}
			}
		}
	}
</style>
