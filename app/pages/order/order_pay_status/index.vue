<template>
	<view :data-theme="theme">
		<view class='payment-status'>
			<!--失败时： 用icon-iconfontguanbi fail替换icon-duihao2 bg-color-->
			<view class='iconfont icons icon-duihao2 bg_color'
				v-if="order_pay_info.paid === 1"></view>
			<view v-if="order_pay_info.paid === 2" class='iconfont icons icon-iconfontguanbi'></view>
			<!-- 失败时：订单支付失败 -->
			<view class='status' v-if="order_pay_info.payType != 'offline'">{{status==2 ? '订单取消支付' : errMsg ? '订单支付异常':payResult }}</view>
			<view class='status' v-else>订单创建成功</view>
			<view class='wrapper'>
				<view class='item acea-row row-between-wrapper'>
					<view>订单编号</view>
					<view class='itemCom'>{{order_pay_info.orderId}}</view>
				</view>
				<view class='item acea-row row-between-wrapper'>
					<view>下单时间</view>
					<view class='itemCom'>{{order_pay_info.createTime?order_pay_info.createTime:'-'}}</view>
				</view>
				<view class='item acea-row row-between-wrapper'>
					<view>支付方式</view>
					<view class='itemCom' v-if="order_pay_info.payType=='weixin'">微信支付</view>
					<view class='itemCom' v-else-if="order_pay_info.payType=='yue'">余额支付</view>
					<view class='itemCom' v-else-if="order_pay_info.payType=='offline'">线下支付</view>
				</view>
				<view class='item acea-row row-between-wrapper'>
					<view>支付金额</view>
					<view class='itemCom'>{{order_pay_info.payPrice}}</view>
				</view>
				<!--失败时加上这个  -->
				<view class='item acea-row row-between-wrapper'
					v-if="!order_pay_info.paid && order_pay_info.payType != 'offline'">
					<view>失败原因</view>
					<view class='itemCom'>{{status==2 ? '取消支付':msg}}</view>
				</view>
			</view>
			<!--失败时： 重新购买 -->
			<view @tap="goOrderDetails">
				<button formType="submit" class='returnBnt bg_color' hover-class='none'>查看订单</button>
			</view>
			<button @click="goPink(order_pay_info.pinkId)" class='returnBnt cart_color' formType="submit"
				hover-class='none'
				v-if="order_pay_info.pinkId && order_pay_info.paid!=0 && status!=2 && status!=1">邀请好友参团</button>
			<button @click="goIndex" class='returnBnt cart-color' formType="submit" hover-class='none'
				v-else>返回首页</button>
		</view>
	</view>
</template>

<script>
	import {
		getOrderDetail,
		wechatQueryPayResult
	} from '@/api/order.js';
	import {
		openOrderSubscribe
	} from '@/utils/SubscribeMessage.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	let app = getApp();
	export default {
		data() {
			return {
				orderId: '',
				order_pay_info: {
					paid: 0,
					_status: {}
				},
				status: 0,
				msg: '',
				errMsg: false,
				payResult: '订单查询中...',
				theme:app.globalData.theme,
			};
		},
		computed: mapGetters(['isLogin']),
		watch: {
			isLogin: {
				handler: function(newV, oldV) {
					if (newV) {
						this.getOrderPayInfo();
					}
				},
				deep: true
			}
		},
		onLoad: function(options) {
			if (!options.order_id) return this.$util.Tips({
				title: '缺少参数无法查看订单支付状态'
			}, {
				tab: 3,
				url: 1
			});
			this.orderId = options.order_id;
			this.status = options.status || 0;
			if (this.isLogin) {
				this.getOrderPayInfo();
			} else {
				toLogin();
			}
		},
		methods: {
			wechatQueryPay() {
				wechatQueryPayResult(this.orderId).then(res => {
					this.payResult = '支付成功';
					uni.setNavigationBarTitle({
						title: '支付成功'
					});
					this.order_pay_info.paid = 1;
					uni.hideLoading();
				})
				.catch(err => {
					this.order_pay_info.paid = 2;
					this.errMsg = true;
					this.msg = err;
					uni.hideLoading();
					this.$util.Tips({
						title: err
					});
				});
			},
			onLoadFun: function() {
				this.getOrderPayInfo();
			},
			/**
			 * 
			 * 支付完成查询支付状态
			 * 
			 */
			getOrderPayInfo: function() {
				let that = this;
				uni.showLoading({
					title: '正在加载中'
				});
				getOrderDetail(that.orderId).then(res => {
					that.$set(that, 'order_pay_info', res.data);
					if (res.data.payType === 'weixin') {
						setTimeout(()=>{
							that.wechatQueryPay();
						},2000);
					}else {
						uni.setNavigationBarTitle({
							title: res.data.paid ? '支付成功' : '未支付'
						});
						if(res.data.paid){
							this.payResult = '支付成功';
							this.order_pay_info.paid = 1;
						}else{
							this.payResult = '支付失败';
							this.order_pay_info.paid = 2;
						}
						uni.hideLoading();
					} 
					
				}).catch(err => {
					uni.hideLoading();
				});
			},
			/**
			 * 去首页关闭当前所有页面
			 */
			goIndex: function(e) {
				uni.switchTab({
					url: '/pages/index/index'
				});
			},
			// 去参团页面；
			goPink: function(id) {
				uni.navigateTo({
					url: '/pages/activity/goods_combination_status/index?id=' + id
				});
			},
			/**
			 * 
			 * 去订单详情页面
			 */
			goOrderDetails: function(e) {
				let that = this;
				// #ifdef MP
				uni.showLoading({
					title: '正在加载',
				})
				openOrderSubscribe().then(res => {
					uni.hideLoading();
					uni.navigateTo({
						url: '/pages/order/order_details/index?order_id=' + that.orderId
					});
				}).catch(() => {
					uni.hideLoading();
				});
				// #endif
				// #ifndef MP
				uni.navigateTo({
					url: '/pages/order/order_details/index?order_id=' + that.orderId
				})
				// #endif
			}

		}
	}
</script>

<style lang="scss">
	.icon-iconfontguanbi {
		background-color: #999 !important;
		text-shadow: none !important;
	}
	.bg_color{
		@include main_bg_color(theme);
	}
	.cart_color{
		@include main_color(theme);
		@include coupons_border_color(theme);
	}
	.payment-status {
		background-color: #fff;
		margin: 195rpx 30rpx 0 30rpx;
		border-radius: 10rpx;
		padding: 1rpx 0 28rpx 0;
	}

	.payment-status .icons {
		font-size: 70rpx;
		width: 140rpx;
		height: 140rpx;
		border-radius: 50%;
		color: #fff;
		text-align: center;
		line-height: 140rpx;
		text-shadow: 0px 4px 0px rgba(0,0,0,.1);
		border: 6rpx solid #f5f5f5;
		margin: -76rpx auto 0 auto;
		background-color: #999;
	}

	.payment-status .iconfont {
		font-size: 70rpx;
		width: 140rpx;
		height: 140rpx;
		border-radius: 50%;
		color: #fff;
		text-align: center;
		line-height: 140rpx;
		text-shadow: 0px 4px 0px rgba(0,0,0,.1);
		border: 6rpx solid #f5f5f5;
		margin: -76rpx auto 0 auto;
		background-color: #999;
	}

	.payment-status .iconfont.fail {
		text-shadow: 0px 4px 0px #7a7a7a;
	}

	.payment-status .status {
		font-size: 32rpx;
		font-weight: bold;
		text-align: center;
		margin: 25rpx 0 37rpx 0;
	}

	.payment-status .wrapper {
		border: 1rpx solid #eee;
		margin: 0 30rpx 47rpx 30rpx;
		padding: 35rpx 0;
		border-left: 0;
		border-right: 0;
	}

	.payment-status .wrapper .item {
		font-size: 28rpx;
		color: #282828;
	}

	.payment-status .wrapper .item~.item {
		margin-top: 20rpx;
	}

	.payment-status .wrapper .item .itemCom {
		color: #666;
	}

	.payment-status .returnBnt {
		width: 630rpx;
		height: 86rpx;
		border-radius: 50rpx;
		color: #fff;
		font-size: 30rpx;
		text-align: center;
		line-height: 86rpx;
		margin: 0 auto 20rpx auto;
		
	}
	.cart-color {
		@include main_color(theme);
		@include coupons_border_color(theme);
	}
</style>
