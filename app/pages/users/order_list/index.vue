<template>
	<view>
		<view class='my-order'>
			<view class='header bg-color'>
				<view class='picTxt acea-row row-between-wrapper'>
					<view class='text'>
						<view class='name'>订单信息</view>
						<view>消费订单：{{orderData.orderCount || 0}} 总消费：￥{{Number(orderData.sumPrice).toFixed(2) || 0}}</view>
					</view>
					<view class='pictrue'>
						<image src='../../../static/images/orderTime.png'></image>
					</view>
				</view>
			</view>
			<view class='nav acea-row row-around'>
				<view class='item' :class='orderStatus==0 ? "on": ""' @click="statusClick(0)">
					<view>待付款</view>
					<view class='num'>{{orderData.unPaidCount || 0}}</view>
				</view>
				<view class='item' :class='orderStatus==1 ? "on": ""' @click="statusClick(1)">
					<view>待发货</view>
					<view class='num'>{{orderData.unShippedCount || 0}}</view>
				</view>
				<view class='item' :class='orderStatus==2 ? "on": ""' @click="statusClick(2)">
					<view>待收货</view>
					<view class='num '>{{orderData.receivedCount || 0}}</view>
				</view>
				<view class='item' :class='orderStatus==3 ? "on": ""' @click="statusClick(3)">
					<view>待评价</view>
					<view class='num'>{{orderData.evaluatedCount || 0}}</view>
				</view>
				<view class='item' :class='orderStatus==4 ? "on": ""' @click="statusClick(4)">
					<view>已完成</view>
					<view class='num'>{{orderData.completeCount || 0}}</view>
				</view>
			</view>
			<view class='list'>
				<view class='item' v-for="(item,index) in orderList" :key="index">
					<view @click='goOrderDetails(item.orderId)'>
						<view class='title acea-row row-between-wrapper'>
							<view class="acea-row row-middle">
								<text class="sign cart-color acea-row row-center-wrapper" v-if="item.activityType !== '普通' && item.activityType !== '核销'">{{item.activityType}}</text>
								<!-- <text class="sign cart-color acea-row row-center-wrapper" v-if="item.bargainId != 0">砍价</text>
								<text class="sign cart-color acea-row row-center-wrapper" v-else-if="item.storeOrder.combinationId != 0">拼团</text>
								<text class="sign cart-color acea-row row-center-wrapper" v-else-if="item.storeOrder.seckillId != 0">秒杀</text> -->
								<view>{{item.createTime}}</view>
							</view>
							<view class='font-color'>{{item.orderStatus}}</view>
							<!-- <view v-if="item.status?item.status.type == 0:0" class='font-color'>待付款</view>
							<view v-else-if="item.status?item.status.type == 1:0 && item.storeOrder.shippingType==1" class='font-color'>待发货</view>
							<view v-else-if="item.status?item.status.type == 2:0 && item.storeOrder.shippingType==1" class='font-color'>待收货</view>
							<view v-else-if="item.status?item.status.type == 3:0 && item.storeOrder.shippingType==1" class='font-color'>待评价</view>
							<view v-else-if="item.status?item.status.type == 4:0 && item.storeOrder.shippingType==1" class='font-color'>已完成</view>
							<view v-else-if="item.storeOrder.shippingType==2" class='font-color'>待核销</view> -->
						</view>
						<view class='item-info acea-row row-between row-top' v-for="(items,index) in item.orderInfoList" :key="index">
							<view class='pictrue'>
								<image :src='items.image'></image>
							</view>
							<view class='text acea-row row-between'>
								<view class='name line2'>{{items.storeName}}</view>
								<view class='money'>
									<view>￥{{items.price}}</view>
									<view>x{{items.cartNum}}</view>
								</view>
							</view>
						</view>
						<view class='totalPrice'>共{{item.totalNum}}件商品，总金额
							<text class='money font-color'>￥{{item.payPrice}}</text>
						</view>
					</view>
					<view class='bottom acea-row row-right row-middle'>
						<view class='bnt cancelBnt' v-if="!item.paid" @click='cancelOrder(index,item.id)'>取消订单</view>
						<view class='bnt bg-color' v-if="!item.paid" @click='goPay(item.payPrice,item.orderId)'>立即付款</view>
						<view class='bnt bg-color' v-else-if="item.status== 0 || item.status== 1 || item.status== 3" @click='goOrderDetails(item.orderId)'>查看详情</view>
						<view class='bnt bg-color' v-else-if="item.status==2" @click='goOrderDetails(item.orderId)'>去评价</view>
						<view class='bnt cancelBnt' v-if="item.status == 3" @click='delOrder(item.id,index)'>删除订单</view>
					</view>
					<!-- <view class='bottom acea-row row-right row-middle'>
						<view class='bnt cancelBnt' v-if="item.status?item.status.type==0:0 || item.status?item.status.type == 9:0" @click='cancelOrder(index,item.id)'>取消订单</view>
						<view class='bnt bg-color' v-if="item.status?item.status.type == 0:0" @click='goPay(item.payPrice,item.orderId)'>立即付款</view>
						<view class='bnt bg-color' v-else-if="item.status?item.status.type == 1:0 || item.status?item.status.type == 9:0" @click='goOrderDetails(item.orderId)'>查看详情</view>
						<view class='bnt bg-color' v-else-if="item.status?item.status.type == 2:0 && item.status.deliveryType" @click='goOrderDetails(item.orderId)'>查看详情</view>
						<view class='bnt bg-color' v-else-if="item.status?item.status.type == 3:0" @click='goOrderDetails(item.orderId)'>去评价</view>
						<view class='bnt bg-color' v-else-if="item.storeOrder.seckillId < 1 && item.storeOrder.bargainId < 1 && item.storeOrder.combinationId < 1 && item.status?item.status.type == 4:0"
						 @click='goOrderDetails(item.orderId)'>再次购买</view>
						<view class='bnt cancelBnt' v-if="item.status?item.status.type == 4:0" @click='delOrder(item.id,index)'>删除订单</view>
					</view> -->
				</view>
			</view>
			<view class='loadingicon acea-row row-center-wrapper' v-if="orderList.length>0">
				<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
			</view>
			<view v-if="orderList.length == 0">
				<emptyPage title="暂无订单~"></emptyPage>
			</view>
		</view>
		<view class='noCart' v-if="orderList.length == 0 && page > 1">
			<view class='pictrue'>
				<image src='/images/noOrder.png'></image>
			</view>
		</view>
		<!-- #ifdef MP -->
		<!-- <authorize @onLoadFun="onLoadFun" :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize> -->
		<!-- #endif -->
		<home></home>
		<payment :payMode='payMode' :pay_close="pay_close" @onChangeFun='onChangeFun' :order_id="pay_order_id" :totalPrice='totalPrice'></payment>
	</view>
</template>

<script>
	import {
		getOrderList,
		orderData,
		orderCancel,
		orderDel
	} from '@/api/order.js';
	import {
		openOrderSubscribe
	} from '@/utils/SubscribeMessage.js';
	import home from '@/components/home';
	import payment from '@/components/payment';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	// #ifdef MP
	import authorize from '@/components/Authorize';
	// #endif
	import emptyPage from '@/components/emptyPage.vue'
	export default {
		components: {
			payment,
			home,
			emptyPage,
			// #ifdef MP
			authorize
			// #endif
		},
		data() {
			return {
				loading: false, //是否加载中
				loadend: false, //是否加载完毕
				loadTitle: '加载更多', //提示语
				orderList: [], //订单数组
				orderData: {}, //订单详细统计
				orderStatus: 0, //订单状态
				page: 1,
				limit: 20,
				payMode: [{
						name: "微信支付",
						icon: "icon-weixinzhifu",
						value: 'weixin',
						title: '微信快捷支付'
					},
					{
						name: "余额支付",
						icon: "icon-yuezhifu",
						value: 'yue',
						title: '可用余额:',
						number: 0
					}
				],
				pay_close: false,
				pay_order_id: '',
				totalPrice: '0',
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false //是否隐藏授权
			};
		},
		computed: mapGetters(['isLogin', 'userInfo']),
		onShow() {
			if (this.isLogin) {
				this.loadend = false;
				this.page = 1;
				this.$set(this, 'orderList', []);
				this.getOrderData();
				this.getOrderList();
				this.payMode[1].number = this.userInfo.nowMoney;
				this.$set(this, 'payMode', this.payMode);
			} else {
				toLogin();
			}
		},
		methods: {
			onLoadFun() {
				this.getOrderData();
				this.getOrderList();
			},
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e
			},
			/**
			 * 事件回调
			 * 
			 */
			onChangeFun: function(e) {
				let opt = e;
				let action = opt.action || null;
				let value = opt.value != undefined ? opt.value : null;
				(action && this[action]) && this[action](value);
			},
			/**
			 * 关闭支付组件
			 * 
			 */
			payClose: function() {
				this.pay_close = false;
			},
			/**
			 * 生命周期函数--监听页面加载
			 */
			onLoad: function(options) {
				if (options.status) this.orderStatus = options.status;
			},
			/**
			 * 获取订单统计数据
			 * 
			 */
			getOrderData: function() {
				let that = this;
				orderData().then(res => {
					that.$set(that, 'orderData', res.data);
				})
			},
			/**
			 * 取消订单
			 * 
			 */
			cancelOrder: function(index, order_id) {
				let that = this;
				if (!order_id) return that.$util.Tips({
					title: '缺少订单号无法取消订单'
				});
				uni.showLoading({
				    title: '正在删除中'
				});
				orderCancel(order_id).then(res => {
					uni.hideLoading();
					return that.$util.Tips({
						title: '删除成功',
						icon: 'success'
					}, function() {
						that.orderList.splice(index, 1);
						that.$set(that, 'orderList', that.orderList);
						that.$set(that.orderData, 'unpaid_count', that.orderData.unpaid_count - 1);
						that.getOrderData();
					});
				}).catch(err => {
					return that.$util.Tips({
						title: err
					});
				});
			},
			/**
			 * 打开支付组件
			 * 
			 */
			goPay(pay_price, order_id) {
				this.$set(this, 'pay_close', true);
				this.$set(this, 'pay_order_id', order_id);
				this.$set(this, 'totalPrice', pay_price);
			},
			/**
			 * 支付成功回调
			 * 
			 */
			pay_complete: function() {
				this.loadend = false;
				this.page = 1;
				this.$set(this, 'orderList', []);
				this.$set(this, 'pay_close', false);
				this.getOrderData();
				this.getOrderList();
			},
			/**
			 * 支付失败回调
			 * 
			 */
			pay_fail: function() {
				this.pay_close = false;
			},
			/**
			 * 去订单详情
			 */
			goOrderDetails: function(order_id) {
				if (!order_id) return that.$util.Tips({
					title: '缺少订单号无法查看订单详情'
				});
				// #ifdef MP
				uni.showLoading({
					title: '正在加载',
				})
				openOrderSubscribe().then(() => {
					uni.hideLoading();
					uni.navigateTo({
						url: '/pages/order_details/index?order_id=' + order_id
					})
				}).catch(() => {
					uni.hideLoading();
				})
				// #endif  
				// #ifndef MP
				uni.navigateTo({
					url: '/pages/order_details/index?order_id=' + order_id
				})
				// #endif
			},
			/**
			 * 切换类型
			 */
			statusClick: function(status) {
				if (status == this.orderStatus) return;
				this.orderStatus = status;
				this.loadend = false;
				this.page = 1;
				this.$set(this, 'orderList', []);
				this.getOrderList();
			},
			/**
			 * 获取订单列表
			 */
			getOrderList: function() {
				let that = this;
				if (that.loadend) return;
				if (that.loading) return;
				that.loading = true;
				that.loadTitle = "加载更多";
				getOrderList({
					type: that.orderStatus,
					page: that.page,
					limit: that.limit,
				}).then(res => {
					let list = res.data.list || [];
					let loadend = list.length < that.limit;
					that.orderList = that.$util.SplitArray(list, that.orderList);
					that.$set(that, 'orderList', that.orderList);
					that.loadend = loadend;
					that.loading = false;
					that.loadTitle = loadend ? "我也是有底线的" : '加载更多';
					that.page = that.page + 1;
				}).catch(err => {
					that.loading = false;
					that.loadTitle = "加载更多";
				})
			},

			/**
			 * 删除订单
			 */
			delOrder: function(order_id, index) {
				let that = this;
				orderDel(order_id).then(res => {
					that.orderList.splice(index, 1);
					that.$set(that, 'orderList', that.orderList);
					that.$set(that.orderData, 'unpaid_count', that.orderData.unpaid_count - 1);
					that.getOrderData();
					return that.$util.Tips({
						title: '删除成功',
						icon: 'success'
					});
				}).catch(err => {
					return that.$util.Tips({
						title: err
					});
				})
			},
		},
		onReachBottom: function() {
			this.getOrderList();
		}
	}
</script>

<style scoped lang="scss">
	.my-order .header {
		height: 250rpx;
		padding: 0 30rpx;
	}

	.my-order .header .picTxt {
		height: 190rpx;
	}

	.my-order .header .picTxt .text {
		color: rgba(255, 255, 255, 0.8);
		font-size: 26rpx;
		font-family: 'Guildford Pro';
	}

	.my-order .header .picTxt .text .name {
		font-size: 34rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 20rpx;
	}

	.my-order .header .picTxt .pictrue {
		width: 122rpx;
		height: 109rpx;
	}

	.my-order .header .picTxt .pictrue image {
		width: 100%;
		height: 100%;
	}

	.my-order .nav {
		background-color: #fff;
		width: 690rpx;
		height: 140rpx;
		border-radius: 14rpx;
		margin: -60rpx auto 0 auto;
	}

	.my-order .nav .item {
		text-align: center;
		font-size: 26rpx;
		color: #282828;
		padding: 26rpx 0;
	}

	.my-order .nav .item.on {
		// font-weight: bold;
		// border-bottom: 5rpx solid #e93323;
		/* #ifdef H5 || MP */
		font-weight: bold;
		/* #endif */
		border-bottom: 5rpx solid $theme-color;
	}

	.my-order .nav .item .num {
		margin-top: 18rpx;
	}

	.my-order .list {
		width: 690rpx;
		margin: 14rpx auto 0 auto;
	}

	.my-order .list .item {
		background-color: #fff;
		border-radius: 14rpx;
		margin-bottom: 14rpx;
	}

	.my-order .list .item .title {
		height: 84rpx;
		padding: 0 24rpx;
		border-bottom: 1rpx solid #eee;
		font-size: 28rpx;
		color: #282828;
	}

	.my-order .list .item .title .sign {
		font-size: 24rpx;
		padding: 0 13rpx;
		height: 36rpx;
		margin-right: 15rpx;
		border-radius: 18rpx;
	}

	.my-order .list .item .item-info {
		padding: 0 24rpx;
		margin-top: 22rpx;
	}

	.my-order .list .item .item-info .pictrue {
		width: 120rpx;
		height: 120rpx;
	}

	.my-order .list .item .item-info .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 14rpx;
	}

	.my-order .list .item .item-info .text {
		width: 500rpx;
		font-size: 28rpx;
		color: #999;
	}

	.my-order .list .item .item-info .text .name {
		width: 350rpx;
		color: #282828;
	}

	.my-order .list .item .item-info .text .money {
		text-align: right;
	}

	.my-order .list .item .totalPrice {
		font-size: 26rpx;
		color: #282828;
		text-align: right;
		margin: 27rpx 0 0 30rpx;
		padding: 0 30rpx 30rpx 0;
		border-bottom: 1rpx solid #eee;
	}

	.my-order .list .item .totalPrice .money {
		font-size: 28rpx;
		font-weight: bold;
	}

	.my-order .list .item .bottom {
		height: 107rpx;
		padding: 0 30rpx;
	}

	.my-order .list .item .bottom .bnt {
		width: 176rpx;
		height: 60rpx;
		text-align: center;
		line-height: 60rpx;
		color: #fff;
		border-radius: 50rpx;
		font-size: 27rpx;
	}

	.my-order .list .item .bottom .bnt.cancelBnt {
		border: 1rpx solid #ddd;
		color: #aaa;
	}

	.my-order .list .item .bottom .bnt~.bnt {
		margin-left: 17rpx;
	}

	.noCart {
		margin-top: 171rpx;
		padding-top: 0.1rpx;
	}

	.noCart .pictrue {
		width: 414rpx;
		height: 336rpx;
		margin: 78rpx auto 56rpx auto;
	}

	.noCart .pictrue image {
		width: 100%;
		height: 100%;
	}
</style>
