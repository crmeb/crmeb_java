<template>
	<view :data-theme="theme">
		<view class='my-order' :style="'margin-top:'+(marTop)+'rpx;'">
			<view class="mp-bg">
				<view class="searchCon acea-row">
					<!-- #ifndef APP-PLUS||MP -->
					<view @click="toBack">
						<text class="iconfont icon-fanhui"></text>
					</view>
					<!-- #endif -->
					<view class="search acea-row row-middle">
						<text class="iconfont icon-ic_search"></text>
						<input class="inputs" placeholder='输入商品或订单号搜索' placeholder-class='placeholder'
							placeholder-style="font-size:28rpx" v-model="keywords" @confirm="search"></input>
					</view>
					<view class="btn" @click="search">
						<text>搜索</text>
					</view>
				</view>
				<scroll-view class="scroll-view_x" scroll-x style="white-space: nowrap; vertical-align: middle;"
					show-scrollbar="false">
					<view class='nav borRadius14 acea-row'>
						<text class='item' :class='orderStatus==0 ? "on": ""' @click="statusClick(0)">
							<text>待付款</text>
						</text>
						<text class='item' :class='orderStatus==1 ? "on": ""' @click="statusClick(1)">
							<text>待发货</text>
						</text>
						<text class='item' :class='orderStatus==2 ? "on": ""' @click="statusClick(2)">
							<text>待收货</text>
						</text>
						<text class='item' :class='orderStatus==3 ? "on": ""' @click="statusClick(3)">
							<text>待评价</text>
						</text>
						<text class='item' :class='orderStatus==4 ? "on": ""' @click="statusClick(4)">
							<text>已完成</text>
						</text>
					</view>
				</scroll-view>
			</view>
			<view class='list'>
				<view class='item' v-for="(item,index) in orderList" :key="index">
					<view @click='goOrderDetails(item.orderId)'>
						<view class='title acea-row row-between-wrapper'>
							<view class="acea-row row-middle">
								<text class="sign cart-color acea-row row-center-wrapper" v-if="item.activityType !== '普通' && item.activityType !== '核销'">{{item.activityType}}</text>
						        <view>{{item.createTime}}</view>
							</view>
							<view class='font_color'>{{item.orderStatus}}</view>
						</view>
						<view class='item-info acea-row row-between row-top' v-for="(items,index) in item.orderInfoList" :key="index">
							<view class='pictrue'>
								<image :src='items.image'></image>
							</view>
							<view class='text acea-row row-between'>
								<view class='name line2'>{{items.storeName}}</view>
								<view class='money'>
									<view :style="[{'font-size':items.price.length>8?'24rpx':'28rpx'}]" >￥{{items.price}}</view>
									<view>x{{items.cartNum}}</view>
								</view>
							</view>
						</view>
						<view class='totalPrice'>共{{item.totalNum}}件商品，总金额
							<text class='money'>￥{{item.payPrice}}</text>
						</view>
					</view>
					<view class='bottom acea-row row-right row-middle'>
						<view class='bnt cancelBnt' v-if="!item.paid" @click='cancelOrder(index,item.id)'>取消订单</view>
						<view class='bnt bg_color' v-if="!item.paid" @click='goPay(item.payPrice,item.orderId)'>立即付款</view>
						<view class='bnt bg_color' v-else-if="item.status== 0 || item.status== 1 || item.status== 3" @click='goOrderDetails(item.orderId)'>查看详情</view>
						<view class='bnt bg_color' v-else-if="item.status==2" @click='goComment(item)'>去评价</view>
						<view class='bnt cancelBnt' v-if="item.status == 3" @click='delOrder(item.id,index)'>删除订单</view>
					</view>
				</view>
			</view>
			
			<view class='loadingicon acea-row row-center-wrapper'>
				<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{orderList.length>0?loadTitle:''}}
			</view>
			<view class='noCart' v-if="orderList.length == 0 && isShow && !loading">
				<view class='pictrue'>
					<image :src="urlDomain+'crmebimage/perset/staticImg/noOrder.png'"></image>
				</view>
			</view>
		</view>
		<payment :pay_close="pay_close" @onChangeFun='onChangeFun' :order_id="pay_order_id" :totalPrice='totalPrice'></payment>
	</view>
</template>

<script>
	import {
		getOrderList,
		orderData,
		orderCancel,
		orderDel
	} from '@/api/order.js';
	import {openOrderSubscribe} from '@/utils/SubscribeMessage.js';
	import payment from '@/components/payment';
	import navBar from '@/components/navBar';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import emptyPage from '@/components/emptyPage.vue'
	import {setThemeColor} from '@/utils/setTheme.js'
	import animationType from '@/utils/animationType.js'
	const app = getApp();
	export default {
		components: {
			payment,
			emptyPage,
			navBar
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				navTitle: '我的订单',
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
						title: '微信快捷支付',
						payStatus: 1,
					},
					{
						name: "余额支付",
						icon: "icon-yuezhifu",
						value: 'yue',
						title: '可用余额:',
						number: 0,
						payStatus: 1,
					},
					// #ifndef MP
					{
						"name": "支付宝支付",
						"icon": "icon-zhifubao",
						value: 'alipay',
						title: '支付宝快捷支付',
						payStatus: 1,
					}
					// #endif
				],
				pay_close: false,
				pay_order_id: '',
				totalPrice: '0',
				isShow: false,
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				theme:app.globalData.theme,
				marTop: 0,
				keywords:''
			};
		},
		computed: mapGetters(['isLogin', 'userInfo']),
		
		onShow() {
			let that = this;
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
		mounted:function(){
			// #ifdef H5
			if(this.$wechat.isWeixin()) this.payMode.pop();
			// #endif
		},
		methods: {
			//搜索
			search() {
				this.statusClick(this.orderStatus)
			},
			// 返回上一页
			toBack() {
				uni.navigateBack({
					delta: 1
				})
			},
			getNavH(marTop){
				this.marTop = marTop;
			},
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
				uni.showModal({
					content: '确定取消该订单',
					cancelText: "取消", 
					confirmText: "确定", 
					showCancel: true, 
					confirmColor: '#f55850',
					success: (res) => {
						if(res.confirm) {  
							uni.showLoading({
							    title: '正在取消中'
							});
							orderCancel(order_id).then(res => {
								uni.hideLoading();
								return that.$util.Tips({
									title: '取消成功',
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
						} else {  
							
						}  
					} ,
				})
			},
			/**
			 * 打开支付组件
			 * 
			 */
			goPay(pay_price, order_id) {
				uni.navigateTo({
					url:`/pages/order/order_payment/index?orderNo=${order_id}&payPrice=${pay_price}`
				})
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
			//去评价
			goComment(item){
				const noReplyItem = item.orderInfoList.find(ele => ele.isReply===0);
				if(noReplyItem){
					uni.navigateTo({
						url:`/pages/goods/goods_comment_con/index?unique=${noReplyItem.attrId}&orderId=${item.orderId}&id=${item.id}`
					})
				}else{
					that.$util.Tips({
						title: '已全部评价'
					});
				}
			},
			/**
			 * 去订单详情
			 */
			goOrderDetails: function(order_id,status) {
				if (!order_id) return that.$util.Tips({
					title: '缺少订单号无法查看订单详情'
				});
				// #ifdef MP
				uni.navigateTo({
					url: '/pages/order/order_details/index?order_id=' + order_id
				})
				// #endif  
				// #ifndef MP
				uni.navigateTo({
					animationType: animationType.type,					animationDuration: animationType.duration,
					url: '/pages/order/order_details/index?order_id=' + order_id
				})
				// #endif
			},
			/**
			 * 切换类型
			 */
			statusClick: function(status) {
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
					keywords:encodeURIComponent(that.keywords)
				}).then(res => {
					let list = res.data.list || [];
					let loadend = list.length < that.limit;
					that.orderList = that.$util.SplitArray(list, that.orderList);
					that.$set(that, 'orderList', that.orderList);
					that.loadend = loadend;
					that.loading = false;
					that.loadTitle = loadend ? "我也是有底线的~" : '加载更多';
					that.page = that.page + 1;
					that.isShow = true;
				}).catch(err => {
					that.loading = false;
					that.loadTitle = "加载更多";
				})
			},

			/**
			 * 删除订单
			 */
			delOrder: function(order_id, index) {
				uni.showModal({
					content: '确定删除该订单',
					cancelText: "取消", 
					confirmText: "确定", 
					showCancel: true, 
					confirmColor: '#f55850',
					success: (res) => {
						if(res.confirm) {  
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
						} else {  
							
						}  
					} ,
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
		// padding: 0 30rpx;
	}
	.bg_color{
		@include main_bg_color(theme);
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
		// background-color: #fff;
		// width: 690rpx;
		// height: 140rpx;
		// border-radius: 14rpx;
		// margin: -60rpx auto 0 auto;
	}

	.my-order .nav .item {
		text-align: center;
		font-size: 26rpx;
		color: #282828;
		padding: 26rpx 0;
	}

	.my-order .nav .item.on {
		/* #ifdef H5 || MP */
		font-weight: bold;
		/* #endif */
		/* #ifdef APP-PLUS */
		color: #000;
		/* #endif */
		position: relative;
	}
	.my-order .nav .item .num {
		margin-top: 18rpx;
	}

	.my-order .list {
		width: 690rpx;
		margin: 14rpx auto 0 auto;
		/* #ifdef H5 */
		margin-top: 220rpx !important;
		/* #endif */
		/* #ifdef MP || APP-PLUS */
		margin-top: 190rpx !important;
		
		/* #endif */
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
		@include coupons_border_color(theme);
		@include main_color(theme);
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
	.font_color{
		@include main_color(theme);
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
		@include price_color(theme);
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
	.nav {
		background-color: #fff;
		justify-content: space-between;
		align-items: center;
	}
	
	.nav .item {
		font-size: 26rpx;
		color: #282828;
		padding: 25rpx 0;
		display: inline-block;
		font-size: 26rpx;
	}
	
	.nav .item.on {
		/* #ifdef H5 || MP */
		font-weight: bold;
		/* #endif */
		/* #ifdef APP-PLUS */
		color: #000;
		/* #endif */
		position: relative;
		@include main_color(theme);
		font-size: 30rpx;
	}
	
	.nav .item .num {
		margin-top: 18rpx;
	}
	.mp-bg {
		top: 0;
		position: fixed;
		left: 0;
		width: 100%;
		// height: 120rpx;
		// @include index-gradient(theme);
		background: #fff;
		/* #ifdef H5 */
		padding: 30rpx 30rpx 0 30rpx;
		/* #endif */
		/* #ifdef MP || APP-PLUS */
		padding: 0 30rpx;
		/* #endif */
		z-index: 99;
	}
	.searchCon {
		padding: 10rpx 0rpx;
		align-items: center;
	
		.search {
			flex: 1;
			height: 60rpx;
			padding: 0 32rpx;
			border-radius: 36rpx;
			background: #F5F5F5;
			margin-left: 5px;
	
			.iconfont {
				margin-right: 16rpx;
				font-size: 30rpx;
				color: #999999;
			}
	
			.inputs {
				flex: 1;
				font-size: 28rpx;
				color: #333;
			}
	
			.placeholder {
				font-size: 26rpx;
				color: #ccc;
				background-color: j62f7f8;
			}
		}
	
		.btn {
			width: 72rpx;
			height: 60rpx;
			line-height: 60rpx;
			margin-left: 20rpx;
			background: #FFFFFF;
			text-align: center;
			font-size: 30rpx;
			color: #282828;
	
			.iconfont {
				font-size: 32rpx;
				line-height: 72rpx;
				color: #666666;
			}
		}
	}
</style>
