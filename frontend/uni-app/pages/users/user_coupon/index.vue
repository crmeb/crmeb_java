<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class="navbar acea-row row-around">
			<view class="item acea-row row-center-wrapper" :class="{ on: navOn === 'usable' }" @click="onNav('usable')">未使用</view>
			<view class="item acea-row row-center-wrapper" :class="{ on: navOn === 'unusable' }" @click="onNav('unusable')">已使用/过期</view>
		</view>
		<view class='coupon-list' v-if="couponsList.length">
			<view class='item acea-row row-center-wrapper' v-for='(item,index) in couponsList' :key="index">
				<view class='money' :class="item.validStr==='unusable'||item.validStr==='overdue'||item.validStr==='notStart' ? 'moneyGray' : 'main_bg'">
					<view>￥<text class='num':style="[{'font-size':item.money.length>=7?'42rpx':'60rpx'}]" >{{item.money?Number(item.money):''}}</text></view>
					<view class="pic-num">满{{ item.minPrice?Number(item.minPrice):'' }}元可用</view>
				</view>
				<view class='text'>
					<view class='condition line2'>
						<span class="line-title" :class="item.validStr==='unusable'||item.validStr==='overdue'||item.validStr==='notStart' ? 'bg-color-huic' : 'bg-color-check'" v-if="item.useType === 1">通用</span>
						<span class="line-title" :class="item.validStr==='unusable'||item.validStr==='overdue'||item.validStr==='notStart' ? 'bg-color-huic' : 'bg-color-check'"  v-else-if="item.useType === 2">商品</span>
						<span class="line-title" :class="item.validStr==='unusable'||item.validStr==='overdue'||item.validStr==='notStart' ? 'bg-color-huic' : 'bg-color-check'" v-else-if="item.useType === 3">品类</span>
						<span>{{item.name}}</span>
					</view>
					<view class='data acea-row row-between-wrapper'>
						<view>{{item.useStartTimeStr}}~{{item.useEndTimeStr}}</view>
						<view class='bnt' :class="item.validStr==='unusable'||item.validStr==='overdue'||item.validStr==='notStart'?'gray':'bg_color'">{{ validStrFilter(item.validStr) }}</view>
					</view>
				</view>
			</view>
		</view>
		<view class='loadingicon acea-row row-center-wrapper' v-if="couponsList.length">
		     <text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
		  </view>
		<view class='noCommodity' v-if="!couponsList.length">
			<view class='pictrue'>
				<image src="/static/images/noCoupon.png"></image>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, watch } from 'vue';
	import { getCurrentInstance } from 'vue';
	import {
		getUserCoupons
	} from '@/api/api.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
	import { onLoad, onReachBottom } from '@dcloudio/uni-app';
import { useColor } from '@/composables/useColor.js';
	let app = getApp();
	const { proxy } = getCurrentInstance();
	const appStore = useAppStore();
	const { isLogin } = storeToRefs(appStore);

	function validStrFilter(status) {
		const statusMap = {
			'usable': '可用',
			'unusable': '已用',
			'overdue': '过期',
			'notStart': '未开始'
		}
		return statusMap[status]
	}

	const couponsList = ref([]);
	const loading = ref(false);
	const loadend = ref(false);
	const loadTitle = ref('加载更多');//提示语
	const page = ref(1);
	const limit = ref(20);
	const navOn = ref('usable');
	const { colorStyle } = useColor();
	const theme = ref(app.globalData.theme);

	watch(isLogin, (newV, oldV) => {
		if (newV) {
			getUseCoupons();
		}
	}, { deep: true });

	onLoad(() => {
		if (isLogin.value) {
			getUseCoupons();
		} else {
			toLogin();
		}
	});

	function onNav(type) {
		navOn.value = type;
		couponsList.value = [];
		page.value = 1;
		loadend.value = false;
		getUseCoupons();
	}
	/**
	 * 获取领取优惠券列表
	 */
	function getUseCoupons() {
		if (loadend.value) return false;
		if (loading.value) return false;
		getUserCoupons({ page: page.value, limit: limit.value, type: navOn.value }).then(res => {
			let list = res.data ? res.data.list : [], loadendVal = list.length < limit.value;
			let list2 = proxy.$util.SplitArray(list, couponsList.value);
			couponsList.value = list2;
			loadend.value = loadendVal;
			loadTitle.value = loadendVal ? '我也是有底线的~' : '加载更多';
			page.value = page.value + 1;
			loading.value = false;
		}).catch(err => {
			loading.value = false;
			loadTitle.value = '加载更多';
		});
	}

	/**
	  * 页面上拉触底事件的处理函数
	  */
	onReachBottom(() => {
		getUseCoupons();
	});
</script>

<style lang="scss" scoped>
	.navbar {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 106rpx;
		background-color: #FFFFFF;
		z-index: 9;
	
		.item {
			border-top: 5rpx solid transparent;
			border-bottom: 5rpx solid transparent;
			font-size: 30rpx;
			color: #999999;
			&.on{
				@include tab_border_bottom(theme);
				@include main_color(theme);
			}
		}
	}
	
	
	.money {
		display: flex;
		flex-direction: column;
		justify-content: center;
	}
	.bg_color{
		@include main_bg_color(theme);
	}
	.pic-num {
		color: #ffffff;
		font-size: 24rpx;
	}
	.coupon-list {
		margin-top: 122rpx;
	}
	.coupon-list .item .text{
		height: 100%;
	}
	.coupon-list .item .text .condition{
		/* display: flex;
		align-items: center; */
	}
	.condition .line-title {
		width: 90rpx;
		height: 40rpx !important;
		line-height: 40rpx !important;
		padding: 2rpx 10rpx;
		-webkit-box-sizing: border-box;
		box-sizing: border-box;
		@include coupons_border_color(theme);
		opacity: 1;
		border-radius: 20rpx;
		font-size: 18rpx !important;
		@include main_color(theme);
		margin-right: 12rpx;
	}
	.noCommodity {
		margin-top: 300rpx;
	}
	.main_bg{
		@include main_bg_color(theme);
	}
</style>
