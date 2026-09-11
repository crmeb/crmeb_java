<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class='payment-status'>
			<!--失败时： 用icon-iconfontguanbi fail替换icon-duihao2 bg-color-->
			<view class='iconfont icons icon-duihao2 bg_color'
				v-if="order_pay_info.paid === 1"></view>
			<view v-if="order_pay_info.paid === 2" class='iconfont icons icon-iconfontguanbi'></view>
			<!-- 失败时：订单支付失败 -->
			<view class='status'>{{payResult}}</view>
			<view class='wrapper'>
				<view class='item acea-row row-between-wrapper'>
					<view>订单编号</view>
					<view class='itemCom'>{{orderId}}</view>
				</view>
				<view class='item acea-row row-between-wrapper'>
					<view>下单时间</view>
					<view class='itemCom'>{{order_pay_info.createTime?order_pay_info.createTime:'-'}}</view>
				</view>
				<view class='item acea-row row-between-wrapper'>
					<view>支付方式</view>
					<view class='itemCom'>支付宝支付</view>
				</view>
				<view class='item acea-row row-between-wrapper'>
					<view>支付金额</view>
					<view class='itemCom'>{{order_pay_info.payPrice}}</view>
				</view>
				<!--失败时加上这个  -->
				<view class='item acea-row row-between-wrapper'
					v-if="order_pay_info.paid === 2">
					<view>失败原因</view>
					<view class='itemCom'>{{msg}}</view>
				</view>
			</view>
			<!--失败时： 重新购买 -->
			<view @tap="goOrderDetails">
				<button formType="submit" class='returnBnt bg_color' hover-class='none'>查看订单</button>
			</view>
			<button @click="goIndex" class='returnBnt cart-color' formType="submit" hover-class='none'>返回首页</button>
		</view>
	</view>
</template>

<script setup>
	import { ref } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import {getOrderDetail,alipayQueryPayResult} from '@/api/order.js';
	import util from '@/utils/util.js';
import { useColor } from '@/composables/useColor.js';
	let app = getApp();

	// data
	const orderId = ref('');
	const payPrice = ref('');
	const order_pay_info = ref({
		paid: 0,
		_status: {}
	});
	const isAuto = ref(false); //没有授权的不会自动授权
	const isShowAuth = ref(false); //是否隐藏授权
	const status = ref(0);
	const msg = ref('');
	const payResult = ref('订单查询中...');
	const payTime = ref('');
	const theme = ref(app.globalData.theme);
	const { colorStyle } = useColor();

	onLoad((e) => {
		// #ifdef H5
		var url = window.location.search;
		if(url){
			var theRequest = new Object();
			if (url.indexOf("?") != -1) {
			    var str = url.substr(1);
			    var strs = str.split("&");
			    for (var i = 0; i < strs.length; i++) {
					theRequest[strs[i].split('=')[0]] = decodeURI(strs[i].split('=')[1]);
			    }
			}
			orderId.value = theRequest.out_trade_no; //返回的订单号
			getOrderPayInfo();
		}else{
			uni.getStorage({
			    key: 'orderNo',
			    success: function (res) {
			        orderId.value = res.data; //如果是支付宝中途放弃支付跳转到这个页面，就从缓存读取订单号查询订单详情和支付结果
					setTimeout(()=>{
						getOrderPayInfo();
					},200)
			    }
			});
		}
		// #endif
		
		// #ifdef APP-PLUS
		orderId.value = e.out_trade_no;
		getOrderPayInfo();
		// #endif
	});

	function getOrderPayInfo() { 
		uni.showLoading({
			title: '正在加载中'
		});
		getOrderDetail(orderId.value).then(res => {
			order_pay_info.value = res.data;
			alipayQueryPay();
			uni.hideLoading();
		}).catch(err => {
			uni.hideLoading();
		});
	}
	function alipayQueryPay() {
		alipayQueryPayResult(orderId.value).then(res => {
			payResult.value = '支付成功';
			uni.setNavigationBarTitle({
				title: '支付成功'
			});
			order_pay_info.value.paid = 1;
			uni.hideLoading();
		}).catch(err => {
			order_pay_info.value.paid = 2;
			payResult.value = err;
			msg.value = err;
			uni.hideLoading();
			return util.Tips({
				title: err
			});
		})
	}
	function goOrderDetails(){
		uni.navigateTo({
			url: '/pages/order/order_details/index?order_id=' + orderId.value
		})
	}
	function goIndex(){
		uni.switchTab({
			url: '/pages/index/index'
		});
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
		text-shadow: 0px 4px 0px #df1e14;
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
		text-shadow: 0px 4px 0px #df1e14;
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