<template>
	<view>
		<view class='order-details'>
			<!-- 给header上与data上加on为退款订单-->
			<view class='header bg-color acea-row row-middle' :class='isGoodsReturn ? "on":""'>
				<view class='pictrue' v-if="isGoodsReturn==false">
					<image :src="orderInfo.statusPic"></image>
				</view>
				<view class='data' :class='isGoodsReturn ? "on":""'>
					<view class='state'>{{orderInfo.pstatus.msg}}</view>
					<view>{{orderInfo.createTime}}</view>
				</view>
			</view>
			<view v-if="isGoodsReturn==false">
				<view class='nav'>
					<view class='navCon acea-row row-between-wrapper'>
						<view :class="status.type == 0 || status.type == -9 ? 'on':''">待付款</view>
						<view :class="status.type == 1 ? 'on':''">{{orderInfo.shippingType==1 ? '待发货':'待核销'}}</view>
						<view :class="status.type == 2 ? 'on':''" v-if="orderInfo.shippingType == 1">待收货</view>
						<view :class="status.type == 3 ? 'on':''">待评价</view>
						<view :class="status.type == 4 ? 'on':''">已完成</view>
					</view>
					<view class='progress acea-row row-between-wrapper'>
						<view class='iconfont' :class='(status.type == 0 || status.type == -9  ? "icon-webicon318":"icon-yuandianxiao") + " " + (status.type >= 0 ? "font-color":"")'></view>
						<view class='line' :class='status.type > 0 ? "bg-color":""'></view>
						<view class='iconfont' :class='(status.type == 1 ? "icon-webicon318":"icon-yuandianxiao") + " " + (status.type >= 1 ? "font-color":"")'></view>
						<view class='line' :class='status.type > 1 ? "bg-color":""' v-if="orderInfo.shippingType == 1"></view>
						<view class='iconfont' :class='(status.type == 2 ? "icon-webicon318":"icon-yuandianxiao") + " " +(status.type >= 2 ? "font-color":"")'
						 v-if="orderInfo.shippingType == 1"></view>
						<view class='line' :class='status.type > 2 ? "bg-color":""'></view>
						<view class='iconfont' :class='(status.type == 3 ? "icon-webicon318":"icon-yuandianxiao") + " " + (status.type >= 3 ? "font-color":"")'></view>
						<view class='line' :class='status.type > 3 ? "bg-color":""'></view>
						<view class='iconfont' :class='(status.type == 4 ? "icon-webicon318":"icon-yuandianxiao") + " " + (status.type >= 4 ? "font-color":"")'></view>
					</view>
				</view>
				<!-- 拒绝退款 -->
				<view class="refund" v-if="orderInfo.refundReason">
					<view class="title">
						<image src="/static/images/shuoming.png" mode=""></image>
						商家拒绝退款
					</view>
					<view class="con">拒绝原因：{{orderInfo.refundReason}}</view>
				</view>
				<view class="writeOff" v-if="orderInfo.shippingType == 2 && orderInfo.paid">
					<view class="title">核销信息</view>
					<view class="grayBg">
						<view class="pictrue">
							<!-- <div class="qrcode" ref="qrcode"></div> -->
							<!-- <canvas canvas-id="qrcode" :style="{width: `${qrcodeSize}100%`, height: `${qrcodeSize}100%`}"/> -->
							<image :src="codeImg"></image>
						</view>
					</view>
					<view class="gear">
						<image src="../../static/images/writeOff.jpg"></image>
					</view>
					<view class="num">{{orderInfo.verifyCode}}</view>
					<view class="rules" v-if='orderInfo.systemStore'>
						<view class="item">
							<view class="rulesTitle acea-row row-middle">
								<text class="iconfont icon-shijian"></text>核销时间
							</view>
							<view class="info">
								每日：<text class="time">{{orderInfo.systemStore.dayTime}}</text>
							</view>
						</view>
						<view class="item">
							<view class="rulesTitle acea-row row-middle">
								<text class="iconfont icon-shuoming1"></text>使用说明
							</view>
							<view class="info">可将二维码出示给店员扫描或提供数字核销码</view>
						</view>
					</view>
				</view>
				<view class="map acea-row row-between-wrapper" v-if="orderInfo.shippingType == 2">
					<view>自提地址信息</view>
					<view class="place cart-color acea-row row-center-wrapper" @tap="showMaoLocation">
						<text class="iconfont icon-weizhi"></text>查看位置
					</view>
				</view>
				<view class='address' v-if="orderInfo.shippingType === 1">
					<view class='name'>{{orderInfo.realName}}<text class='phone'>{{orderInfo.userPhone}}</text></view>
					<view>{{orderInfo.userAddress}}</view>
				</view>
				<view class='address' v-else style="margin-top:0;">
					<view class='name' @tap="makePhone">{{orderInfo.systemStore?orderInfo.systemStore.name:''}}<text class='phone'>{{orderInfo.systemStore?orderInfo.systemStore.phone:''}}</text><text
						 class="iconfont icon-tonghua font-color"></text></view>
					<view>{{orderInfo.systemStore?orderInfo.systemStore.detailedAddress:''}}</view>
				</view>
				<view class='line' v-if="orderInfo.shippingType === 1">
					<image src='../../static/images/line.jpg'></image>
				</view>
			</view>
			<orderGoods :evaluate='evaluate' :orderId="order_id" :ids="id" :uniId="uniId" :cartInfo="cartInfo" :jump="true"></orderGoods>
			<!-- <div class="goodCall" @click="goGoodCall"> -->
			<!-- #ifdef H5 -->
			<div class="goodCall" @click="kefuClick">
				<span class="iconfont icon-kefu"></span><span>联系客服</span>
			</div>
			<!-- #endif -->
			<!-- #ifdef MP -->
			<div class="goodCall">
				<button open-type='contact' hover-class='none'>
					<span class="iconfont icon-kefu"></span><span>联系客服</span>
				</button>
			</div>
			<!-- #endif -->
			<view class='wrapper'>
				<view class='item acea-row row-between'>
					<view>订单编号：</view>
					<view class='conter acea-row row-middle row-right'>{{orderInfo.orderId}}
						<!-- #ifndef H5 -->
						<text class='copy' @tap='copy'>复制</text>
						<!-- #endif -->
						<!-- #ifdef H5 -->
						<text class='copy copy-data' :data-clipboard-text="orderInfo.orderId">复制</text>
						<!-- #endif -->
					</view>
				</view>
				<view class='item acea-row row-between'>
					<view>下单时间：</view>
					<view class='conter'>{{(orderInfo.createTime || 0)}}</view>
				</view>
				<view class='item acea-row row-between'>
					<view>支付状态：</view>
					<view class='conter' v-if="orderInfo.paid">已支付</view>
					<view class='conter' v-else>未支付</view>
				</view>
				<view class='item acea-row row-between'>
					<view>支付方式：</view>
					<view class='conter'>{{orderInfo.pstatus.payType}}</view>
				</view>
				<view class='item acea-row row-between' v-if="orderInfo.mark">
					<view>买家留言：</view>
					<view class='conter'>{{orderInfo.mark}}</view>
				</view>
			</view>
			<!-- 退款订单详情 -->
			<view class='wrapper' v-if="isGoodsReturn">
				<view class='item acea-row row-between'>
					<view>收货人：</view>
					<view class='conter'>{{orderInfo.realName}}</view>
				</view>
				<view class='item acea-row row-between'>
					<view>联系电话：</view>
					<view class='conter'>{{orderInfo.userPhone}}</view>
				</view>
				<view class='item acea-row row-between'>
					<view>收货地址：</view>
					<view class='conter'>{{orderInfo.userAddress}}</view>
				</view>
			</view>
			<view v-if="orderInfo.status!=0">
				<view class='wrapper' v-if='orderInfo.deliveryType=="express"'>
					<view class='item acea-row row-between'>
						<view>配送方式：</view>
						<view class='conter'>发货</view>
					</view>
					<view class='item acea-row row-between'>
						<view>快递公司：</view>
						<view class='conter'>{{orderInfo.deliveryName || ''}}</view>
					</view>
					<view class='item acea-row row-between'>
						<view>快递号：</view>
						<view class='conter'>{{orderInfo.deliveryId || ''}}</view>
					</view>
				</view>
				<view class='wrapper' v-else-if='orderInfo.deliveryType=="send"'>
					<view class='item acea-row row-between'>
						<view>配送方式：</view>
						<view class='conter'>送货</view>
					</view>
					<view class='item acea-row row-between'>
						<view>配送人姓名：</view>
						<view class='conter'>{{orderInfo.deliveryName || ''}}</view>
					</view>
					<view class='item acea-row row-between'>
						<view>联系电话：</view>
						<view class='conter acea-row row-middle row-right'>{{orderInfo.deliveryId || ''}}<text class='copy' @tap='goTel'>拨打</text></view>
					</view>
				</view>
				<view class='wrapper' v-else-if='orderInfo.deliveryType=="fictitious"'>
					<view class='item acea-row row-between'>
						<view>虚拟发货：</view>
						<view class='conter'>已发货，请注意查收</view>
					</view>
				</view>
			</view>
			<view class='wrapper'>
				<view class='item acea-row row-between'>
					<view>支付金额：</view>
					<view class='conter'>￥{{orderInfo.totalPrice}}</view>
				</view>
				<view class='item acea-row row-between' v-if='orderInfo.couponId'>
					<view>优惠券抵扣：</view>
					<view class='conter'>-￥{{orderInfo.couponPrice}}</view>
				</view>
				<view class='item acea-row row-between' v-if="orderInfo.useIntegral > 0">
					<view>积分抵扣：</view>
					<view class='conter'>-￥{{orderInfo.deductionPrice}}</view>
				</view>
				<view class='item acea-row row-between' v-if="orderInfo.payPostage > 0">
					<view>运费：</view>
					<view class='conter'>￥{{orderInfo.payPostage}}</view>
				</view>
				<view class='actualPay acea-row row-right'>实付款：<text class='money font-color'>￥{{orderInfo.payPrice}}</text></view>
			</view>
			<view style='height:120rpx;'></view>
			<view class='footer acea-row row-right row-middle' v-if="isGoodsReturn==false || status.type == 9">
				<view class="qs-btn" v-if="status.type == 0 || status.type == -9" @click.stop="cancelOrder">取消订单</view>
				<view class='bnt bg-color' v-if="status.type==0" @tap='pay_open(orderInfo.orderId)'>立即付款</view>
				<!-- #ifdef MP -->
				<view @tap="openSubcribe('/pages/users/goods_return/index?orderId='+orderInfo.orderId)" class='bnt cancel'
				 v-else-if="orderInfo.paid === true && orderInfo.refundStatus === 0">申请退款</view>
				<!-- #endif -->
				<!-- #ifndef MP -->
				<navigator hover-class="none" :url="'/pages/users/goods_return/index?orderId='+orderInfo.orderId" class='bnt cancel'
				 v-else-if="orderInfo.paid === true && orderInfo.refundStatus === 0">申请退款</navigator>
				<!-- #endif -->
				<view class='bnt bg-color' v-if="status.class_status==1" @tap='goJoinPink'>查看拼团</view>
				<navigator class='bnt cancel' v-if="orderInfo.deliveryType == 'express' && status.class_status==3 && status.type==2"
				 hover-class='none' :url="'/pages/users/goods_logistics/index?orderId='+ orderInfo.orderId">查看物流</navigator>
				<view class='bnt bg-color' v-if="status.class_status==3" @tap='confirmOrder'>确认收货</view>
				<view class='bnt cancel' v-if="status.type==4" @tap='delOrder'>删除订单</view>
				<view class='bnt bg-color' v-if="status.class_status==5" @tap='goOrderConfirm'>再次购买</view>
			</view>
		</view>
		<home></home>
		<!-- #ifdef MP -->
		<authorize @onLoadFun="onLoadFun" :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize>
		<!-- #endif -->
		<payment :payMode='payMode' :pay_close="pay_close" @onChangeFun='onChangeFun' :order_id="pay_order_id" :totalPrice='totalPrice'></payment>
	</view>
</template>
<style scoped lang="scss">
	.goodCall {
		color: #e93323;
		text-align: center;
		width: 100%;
		height: 86rpx;
		padding: 0 30rpx;
		border-bottom: 1rpx solid #eee;
		font-size: 30rpx;
		line-height: 86rpx;
		background: #fff;

		.icon-kefu {
			font-size: 36rpx;
			margin-right: 15rpx;
		}

		/* #ifdef MP */
		button {
			display: flex;
			align-items: center;
			justify-content: center;
			height: 86rpx;
			font-size: 30rpx;
			color: #e93323;
		}

		/* #endif */
	}

	.order-details .header {
		padding: 0 30rpx;
		height: 150rpx;
	}

	.order-details .header.on {
		background-color: #666 !important;
	}

	.order-details .header .pictrue {
		width: 110rpx;
		height: 110rpx;
	}

	.order-details .header .pictrue image {
		width: 100%;
		height: 100%;
	}

	.order-details .header .data {
		color: rgba(255, 255, 255, 0.8);
		font-size: 24rpx;
		margin-left: 27rpx;
	}

	.order-details .header .data.on {
		margin-left: 0;
	}

	.order-details .header .data .state {
		font-size: 30rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 7rpx;
	}

	.order-details .header .data .time {
		margin-left: 20rpx;
	}

	.order-details .nav {
		background-color: #fff;
		font-size: 26rpx;
		color: #282828;
		padding: 25rpx 0;
	}

	.order-details .nav .navCon {
		padding: 0 40rpx;
	}

	.order-details .nav .on {
		color: #e93323;
	}

	.order-details .nav .progress {
		padding: 0 65rpx;
		margin-top: 10rpx;
	}

	.order-details .nav .progress .line {
		width: 100rpx;
		height: 2rpx;
		background-color: #939390;
	}

	.order-details .nav .progress .iconfont {
		font-size: 25rpx;
		color: #939390;
		margin-top: -2rpx;
	}

	.order-details .address {
		font-size: 26rpx;
		color: #868686;
		background-color: #fff;
		margin-top: 13rpx;
		padding: 35rpx 30rpx;
	}

	.order-details .address .name {
		font-size: 30rpx;
		color: #282828;
		margin-bottom: 15rpx;
	}

	.order-details .address .name .phone {
		margin-left: 40rpx;
	}

	.order-details .line {
		width: 100%;
		height: 3rpx;
	}

	.order-details .line image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.order-details .wrapper {
		background-color: #fff;
		margin-top: 12rpx;
		padding: 30rpx;
	}

	.order-details .wrapper .item {
		font-size: 28rpx;
		color: #282828;
	}

	.order-details .wrapper .item~.item {
		margin-top: 20rpx;
	}

	.order-details .wrapper .item .conter {
		color: #868686;
		width: 460rpx;
		text-align: right;
	}

	.order-details .wrapper .item .conter .copy {
		font-size: 20rpx;
		color: #333;
		border-radius: 3rpx;
		border: 1rpx solid #666;
		padding: 3rpx 15rpx;
		margin-left: 24rpx;
	}

	.order-details .wrapper .actualPay {
		border-top: 1rpx solid #eee;
		margin-top: 30rpx;
		padding-top: 30rpx;
	}

	.order-details .wrapper .actualPay .money {
		font-weight: bold;
		font-size: 30rpx;
	}

	.order-details .footer {
		width: 100%;
		height: 100rpx;
		position: fixed;
		bottom: 0;
		left: 0;
		background-color: #fff;
		padding: 0 30rpx;
		box-sizing: border-box;
	}

	.order-details .footer .bnt {
		width: 176rpx;
		height: 60rpx;
		text-align: center;
		line-height: 60rpx;
		border-radius: 50rpx;
		color: #fff;
		font-size: 27rpx;
	}

	.order-details .footer .bnt.cancel {
		color: #aaa;
		border: 1rpx solid #ddd;
	}

	.order-details .footer .bnt~.bnt {
		margin-left: 18rpx;
	}

	.order-details .writeOff {
		background-color: #fff;
		margin-top: 13rpx;
		padding-bottom: 30rpx;
	}

	.order-details .writeOff .title {
		font-size: 30rpx;
		color: #282828;
		height: 87rpx;
		border-bottom: 1px solid #f0f0f0;
		padding: 0 30rpx;
		line-height: 87rpx;
	}

	.order-details .writeOff .grayBg {
		background-color: #f2f5f7;
		width: 590rpx;
		height: 384rpx;
		border-radius: 20rpx 20rpx 0 0;
		margin: 50rpx auto 0 auto;
		padding-top: 55rpx;
	}

	.order-details .writeOff .grayBg .pictrue {
		width: 290rpx;
		height: 290rpx;
		margin: 0 auto;
	}

	.order-details .writeOff .grayBg .pictrue image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.order-details .writeOff .gear {
		width: 590rpx;
		height: 30rpx;
		margin: 0 auto;
	}

	.order-details .writeOff .gear image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.order-details .writeOff .num {
		background-color: #f0c34c;
		width: 590rpx;
		height: 84rpx;
		color: #282828;
		font-size: 48rpx;
		margin: 0 auto;
		border-radius: 0 0 20rpx 20rpx;
		text-align: center;
		padding-top: 4rpx;
	}

	.order-details .writeOff .rules {
		margin: 46rpx 30rpx 0 30rpx;
		border-top: 1px solid #f0f0f0;
		padding-top: 10rpx;
	}

	.order-details .writeOff .rules .item {
		margin-top: 20rpx;
	}

	.order-details .writeOff .rules .item .rulesTitle {
		font-size: 28rpx;
		color: #282828;
	}

	.order-details .writeOff .rules .item .rulesTitle .iconfont {
		font-size: 30rpx;
		color: #333;
		margin-right: 8rpx;
		margin-top: 5rpx;
	}

	.order-details .writeOff .rules .item .info {
		font-size: 28rpx;
		color: #999;
		margin-top: 7rpx;
	}

	.order-details .writeOff .rules .item .info .time {
		margin-left: 20rpx;
	}

	.order-details .map {
		height: 86rpx;
		font-size: 30rpx;
		color: #282828;
		line-height: 86rpx;
		border-bottom: 1px solid #f0f0f0;
		margin-top: 13rpx;
		background-color: #fff;
		padding: 0 30rpx;
	}

	.order-details .map .place {
		font-size: 26rpx;
		width: 176rpx;
		height: 50rpx;
		border-radius: 25rpx;
		line-height: 50rpx;
		text-align: center;
	}

	.order-details .map .place .iconfont {
		font-size: 27rpx;
		height: 27rpx;
		line-height: 27rpx;
		margin: 2rpx 3rpx 0 0;
	}

	.order-details .address .name .iconfont {
		font-size: 34rpx;
		margin-left: 10rpx;
	}

	.refund {
		padding: 0 30rpx 30rpx;
		margin-top: 24rpx;
		background-color: #fff;

		.title {
			display: flex;
			align-items: center;
			font-size: 30rpx;
			color: #333;
			height: 86rpx;
			border-bottom: 1px solid #f5f5f5;

			image {
				width: 32rpx;
				height: 32rpx;
				margin-right: 10rpx;
			}
		}

		.con {
			padding-top: 25rpx;
			font-size: 28rpx;
			color: #868686;
		}
	}
</style>

<script>
	import {
		getOrderDetail,
		orderAgain,
		orderTake,
		orderDel,
		orderCancel,
		qrcodeApi
	} from '@/api/order.js';
	import {
		openOrderRefundSubscribe
	} from '@/utils/SubscribeMessage.js';
	import {
		getUserInfo
	} from '@/api/user.js';
	import home from '@/components/home';
	import payment from '@/components/payment';
	import orderGoods from "@/components/orderGoods";
	import ClipboardJS from "@/plugin/clipboard/clipboard.js";
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	// #ifdef MP
	import authorize from '@/components/Authorize';
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js'
	// #endif
	export default {
		components: {
			payment,
			home,
			orderGoods,
			// #ifdef MP
			authorize
			// #endif
		},
		data() {
			return {
				codeImg: '',
				qrcodeSize: 100,
				order_id: '',
				evaluate: 0,
				cartInfo: [], //购物车产品
				orderInfo: {
					systemStore: {},
					pstatus: {}
				}, //订单详情
				system_store: {},
				isGoodsReturn: false, //是否为退款订单
				status: {}, //订单底部按钮状态
				isClose: false,
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
					},
				],
				pay_close: false,
				pay_order_id: '',
				totalPrice: '0',
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				id: 0,//订单id
				uniId:''
			};
		},
		computed: mapGetters(['isLogin', 'chatUrl']),
		onLoad: function(options) {
			if (!options.order_id && !options.uniId) return this.$util.Tips({title:'缺少参数'},{tab:3,url:1});
			   this.$set(this, 'order_id', options.order_id);
		},
		onShow() {
			if (this.isLogin) {
				this.getOrderInfo();
				this.getUserInfo();
			} else {
				// #ifdef H5 || APP-PLUS
				toLogin();
				// #endif 
				// #ifdef MP
				this.isAuto = true;
				this.$set(this, 'isShowAuth', true);
				// #endif
			}
		},
		onHide: function() {
			this.isClose = true;
		},
		onReady: function() {
			// #ifdef H5
			this.$nextTick(function() {
				const clipboard = new ClipboardJS(".copy-data");
				clipboard.on("success", () => {
					this.$util.Tips({
						title: '复制成功'
					});
				});
			});
			// #endif
		},
		methods: {
			kefuClick(){
               location.href = this.chatUrl;
			},
			goGoodCall() {
				let self = this
				uni.navigateTo({
					url: `/pages/customer_list/index?orderId=${self.order_id}`
				})
			},
			openSubcribe: function(e) {
				let page = e;
				uni.showLoading({
					title: '正在加载',
				})
				openOrderRefundSubscribe().then(res => {
					uni.hideLoading();
					uni.navigateTo({
						url: page,
					});
				}).catch(() => {
					uni.hideLoading();
				});
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
			 * 拨打电话
			 */
			makePhone: function() {
				uni.makePhoneCall({
					phoneNumber: this.system_store.phone
				})
			},
			/**
			 * 打开地图
			 * 
			 */
			showMaoLocation: function() {
				if (!this.system_store.latitude || !this.system_store.longitude) return this.$util.Tips({
					title: '缺少经纬度信息无法查看地图！'
				});
				uni.openLocation({
					latitude: parseFloat(this.system_store.latitude),
					longitude: parseFloat(this.system_store.longitude),
					scale: 8,
					name: this.system_store.name,
					address: this.system_store.address + this.system_store.detailedAddress,
					success: function() {

					},
				});
			},
			/**
			 * 关闭支付组件
			 * 
			 */
			payClose: function() {
				this.pay_close = false;
			},
			/**
			 * 打开支付组件
			 * 
			 */
			pay_open: function() {
				this.pay_close = true;
				this.pay_order_id = this.orderInfo.orderId;
				this.totalPrice = this.orderInfo.payPrice;
			},
			/**
			 * 支付成功回调
			 * 
			 */
			pay_complete: function() {
				this.pay_close = false;
				this.pay_order_id = '';
				this.getOrderInfo();
			},
			/**
			 * 支付失败回调
			 * 
			 */
			pay_fail: function() {
				this.pay_close = false;
				this.pay_order_id = '';
			},
			/**
			 * 登录授权回调
			 * 
			 */
			onLoadFun: function() {
				this.getOrderInfo();
				this.getUserInfo();
			},
			/**
			 * 获取用户信息
			 * 
			 */
			getUserInfo: function() {
				let that = this;
				getUserInfo().then(res => {
					that.payMode[1].number = res.data.nowMoney;
					that.$set(that, 'payMode', that.payMode);
				})
			},
			/**
			 * 获取订单详细信息
			 * 
			 */
			getOrderInfo: function() {
				let that = this;
				uni.showLoading({
					title: "正在加载中"
				});
				getOrderDetail(that.order_id).then(res => {
					uni.hideLoading();
					let _type = res.data.pstatus.type;
					that.$set(that, 'orderInfo', res.data);
					that.$set(that, 'evaluate', _type == 3 ? 3 : 0);
					that.$set(that, 'system_store', res.data.systemStore);
					that.$set(that, 'id', res.data.id);
					let cartInfo = res.data.cartInfo,newCartInfo = [];
					cartInfo.forEach((item,index)=>{
						newCartInfo.push(item.info);
					});
					that.$set(that, 'cartInfo', newCartInfo);
					console.log(that.cartInfo)
					if (res.data.refundStatus != 0) {
						that.isGoodsReturn = true;
					};
					that.getOrderStatus();
					that.markCode(res.data.verifyCode);
				}).catch(err => {
					uni.hideLoading();
					that.$util.Tips({
						title: err
					}, '/pages/users/order_list/index');
				});
			},
			/**
			 * 
			 * 生成二维码
			 */
			markCode(text) {
				qrcodeApi({ height: '145', text: text, width: '145' }).then(res => {
					this.codeImg = res.data.code
				});
			},
			/**
			 * 
			 * 剪切订单号
			 */
			// #ifndef H5
			copy: function() {
				let that = this;
				uni.setClipboardData({
					data: this.orderInfo.order_id
				});
			},
			// #endif
			/**
			 * 打电话
			 */
			goTel: function() {
				uni.makePhoneCall({
					phoneNumber: this.orderInfo.deliveryId
				})
			},
			/**
			 * 设置底部按钮
			 * 
			 */
			getOrderStatus: function() {
				let orderInfo = this.orderInfo || {},
					_status = orderInfo.pstatus || {
						type: 0
					},
					status = {};
				let type = parseInt(_status.type),
					delivery_type = orderInfo.deliveryType,
					seckill_id = orderInfo.seckillId ? parseInt(orderInfo.seckillId) : 0,
					bargain_id = orderInfo.bargainId ? parseInt(orderInfo.bargainId) : 0,
					combination_id = orderInfo.combinationId ? parseInt(orderInfo.combinationId) : 0;
				status = {
					type: type == 9 ? -9 : type,
					class_status: 0
				};
				if (type == 1 && combination_id > 0) status.class_status = 1; //查看拼团
				if (type == 2 && delivery_type == 'express') status.class_status = 2; //查看物流
				if (type == 2) status.class_status = 3; //确认收货
				if (type == 4 || type == 0) status.class_status = 4; //删除订单
				if (!seckill_id && !bargain_id && !combination_id && (type == 3 || type == 4)) status.class_status = 5; //再次购买
				this.$set(this, 'status', status);
			},
			/**
			 * 去拼团详情
			 * 
			 */
			goJoinPink: function() {
				uni.navigateTo({
					url: '/pages/activity/goods_combination_status/index?id=' + this.orderInfo.pinkId,
				});
			},
			/**
			 * 再此购买
			 * 
			 */
			goOrderConfirm: function() {
				let that = this;
				orderAgain(that.orderInfo.unique).then(res => {
					return uni.navigateTo({
						url: '/pages/users/order_confirm/index?cartId=' + res.data.cateId + '&again=true&new=true&addAgain=true'
					});
				});
			},
			confirmOrder: function() {
				let that = this;
				uni.showModal({
					title: '确认收货',
					content: '为保障权益，请收到货确认无误后，再确认收货',
					success: function(res) {
						if (res.confirm) {
							orderTake(that.id).then(res => {
								return that.$util.Tips({
									title: '操作成功',
									icon: 'success'
								}, function() {
									that.getOrderInfo();
								});
							}).catch(err => {
								return that.$util.Tips({
									title: err
								});
							})
						}
					}
				})
			},
			/**
			 * 
			 * 删除订单
			 */
			delOrder: function() {
				let that = this;
				orderDel(this.order_id).then(res => {
					return that.$util.Tips({
						title: '删除成功',
						icon: 'success'
					}, {
						tab: 3,
						url: 1
					});
				}).catch(err => {
					return that.$util.Tips({
						title: err
					});
				});
			},
			cancelOrder() {
				let self = this
				uni.showModal({
					title: '提示',
					content: '确认取消该订单?',
					success: function(res) {
						if (res.confirm) {
							orderCancel(self.orderInfo.id)
								.then((data) => {
									self.$util.Tips({
										title: '取消成功'
									}, {
										tab: 3
									})
								})
								.catch(() => {
									self.$util.Tips({
										title: err
									})
									self.getDetail();
								});
						} else if (res.cancel) {
							console.log('用户点击取消');
						}
					}
				});
			},
		}
	}
</script>

<style>
	.qs-btn {
		width: auto;
		height: 60rpx;
		text-align: center;
		line-height: 60rpx;
		border-radius: 50rpx;
		color: #fff;
		font-size: 27rpx;
		padding: 0 3%;
		color: #aaa;
		border: 1px solid #ddd;
		margin-right: 20rpx;
	}
</style>
