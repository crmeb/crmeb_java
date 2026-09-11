<template>
	<view :style="colorStyle">
		<view class='return-list pad30' v-if="orderList.length">
			<view class='goodWrapper borRadius14' v-for="(item,index) in orderList" :key="index" @click='goOrderDetails(item.orderId)'>
				<view class='iconfont icon-tuikuanzhong powder' v-if="item.refundStatus==1 || item.refundStatus==3"></view>
				<view class='iconfont icon-yituikuan' v-if="item.refundStatus==2"></view>
				<view class='orderNum'>订单号：{{item.orderId}}</view>
				<view class='item acea-row row-between-wrapper' v-for="(items,index) in item.orderInfoList" :key="index">
					<view class='pictrue'>
						<image :src='items.image'></image>
					</view>
					<view class='text'>
						<view class='acea-row row-between-wrapper'>
							<view class='name line1'>{{items.storeName}}</view>
							<view class='num'>x {{items.cartNum}}</view>
						</view>
						<view class='attr line1' v-if="items.sku">{{items.sku}}</view>
						<view class='money'>￥{{items.price}}</view>
					</view>
				</view>
				<view class='totalSum'>共{{item.totalNum || 0}}件商品，总金额 <text class=' price'>￥{{item.payPrice}}</text></view>
			</view>
		</view>
		<view class='loadingicon acea-row row-center-wrapper' v-if="orderList.length">
			<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
		</view>
		<view v-if="orderList.length == 0 && !loading">
			<emptyPage title="暂无订单~"></emptyPage>
		</view>
	</view>
</template>

<script setup>
	import { ref, watch, getCurrentInstance } from 'vue';
	import { onLoad, onReachBottom } from '@dcloudio/uni-app';
	import emptyPage from '@/components/emptyPage.vue'
	import {
		getOrderList as getOrderListApi
	} from '@/api/order.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
import { useColor } from '@/composables/useColor.js';
const { colorStyle } = useColor();
	const { proxy } = getCurrentInstance();
	const appStore = useAppStore();
	const { isLogin } = storeToRefs(appStore);

	const loading = ref(false);
	const loadend = ref(false);
	const loadTitle = ref('加载更多'); //提示语
	const orderList = ref([]); //订单数组
	const orderStatus = ref(-3); //订单状态
	const page = ref(1);
	const limit = ref(20);

	watch(isLogin, (newV, oldV) => {
		if (newV) {
			getOrderList();
		}
	}, { deep: true });

	onLoad(() => {
		if (isLogin.value) {
			getOrderList();
		} else {
			toLogin();
		}
	});

	/**
	 * 页面上拉触底事件的处理函数
	 */
	onReachBottom(() => {
		getOrderList();
	});

	/**
	 * 去订单详情
	 */
	function goOrderDetails(order_id) {
		if (!order_id) return proxy.$util.Tips({
			title: '缺少订单号无法查看订单详情'
		});
		uni.navigateTo({
			url: '/pages/order/order_details/index?order_id=' + order_id + '&isReturen=1'
		})
	}

	/**
	 * 获取订单列表
	 */
	function getOrderList() {
		if (loadend.value) return;
		if (loading.value) return;
		loading.value = true;
		loadTitle.value = "";
		getOrderListApi({
			type: orderStatus.value,
			page: page.value,
			limit: limit.value,
		}).then(res => {
			let list = res.data.list || [];
			let loadendVal = list.length < limit.value;
			orderList.value = proxy.$util.SplitArray(list, orderList.value);
			orderList.value = orderList.value;
			loadend.value = loadendVal;
			loading.value = false;
			loadTitle.value = loadendVal ? "我也是有底线的~" : '加载更多';
			page.value = page.value + 1;
		}).catch(err => {
			loading.value = false;
			loadTitle.value = "加载更多";
		});
	}
</script>

<style lang="scss" scoped>
	.return-list .goodWrapper {
		background-color: #fff;
		margin-top: 20rpx;
		position: relative;
		padding: 0rpx 24rpx;
	}

	.return-list .goodWrapper .orderNum {
		border-bottom: 1px solid #eee;
		height: 87rpx;
		line-height: 87rpx;
		font-size: 30rpx;
		color: #333333;
	}

	.return-list .goodWrapper .item {
		border-bottom: 0;
	}

	.return-list .goodWrapper .totalSum {
		padding: 0 30rpx 32rpx 30rpx;
		text-align: right;
		font-size: 26rpx;
		color: #282828;
	}

	.return-list .goodWrapper .totalSum .price {
		font-size: 28rpx;
		font-weight: bold;
		@include price_color(theme);
	}

	.return-list .goodWrapper .iconfont {
		position: absolute;
		font-size: 109rpx;
		top: 7rpx;
		right: 22rpx;
		color: #ccc;
	}

	.return-list .goodWrapper .iconfont.powder {
		color: #f8c1bd;
	}
</style>
