<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class="acea-row row-around nav">
			<template v-for="item in navList" :key="item.type">
				<view :class="['acea-row', 'row-middle', type === item.type ? 'on' : '']" >
					<text @click="setType(item.type)">{{ item.name }}</text>
				</view>
			</template>
		</view>
		<view style="height: 106rpx;"></view>
		<view class='coupon-list' v-if="couponsList.length">
			<view class='item acea-row row-center-wrapper' v-for="(item,index) in couponsList" :key="index">
				<view class='money' :class='item.isUse ? "moneyGray" : "main_bg" '>
					<view >￥<text class='num' :style="[{'font-size':item.money.length>=7?'42rpx':'60rpx'}]" >{{item.money?Number(item.money):''}}</text></view>
					<view class="pic-num">满{{item.minPrice?Number(item.minPrice):''}}元可用</view>
				</view>
				<view class='text'>
					<view class='condition line2'>
					    <span class='line-title' :class='(item.isUse==true || item.isUse==2)?"gray":"select"' v-if='item.useType===1'>通用</span>
					    <span class='line-title' :class='(item.isUse==true || item.isUse==2)?"gray":"select"' v-else-if='item.useType===3'>品类</span>
					    <span class='line-title' :class='(item.isUse==true || item.isUse==2)?"gray":"select"' v-else>商品</span>
					    <span>{{item.name}}</span>
					</view>
					<view class='data acea-row row-between-wrapper'>
						<view v-if="item.day>0">领取后{{item.day}}天内可用</view>
						<view v-else>{{ item.useStartTimeStr&& item.useEndTimeStr ? item.useStartTimeStr + " - " + item.useEndTimeStr : ""}}</view>
						<view class='bnt gray' v-if="item.isUse==true">已领取</view>
						<view class='bnt main_bg' v-else @click='getCoupon(item.id,index)'>立即领取</view>
					</view>
				</view>
			</view>
		</view>
		<view class='loadingicon acea-row row-center-wrapper'>
		     <text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{couponsList.length?loadTitle:''}}
		  </view>
		<view class='noCommodity' v-if="!couponsList.length && isShow && !loading">
			<view class='pictrue'>
				<image src="/static/images/noCoupon.png"></image>
			</view>
		</view>
	</view>	
</template>

<script setup>
	import {
		getCoupons,
		setCouponReceive
	} from '@/api/api.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
	import { ref, watch, getCurrentInstance } from 'vue';
	import { onLoad, onReachBottom } from '@dcloudio/uni-app';
import { useColor } from '@/composables/useColor.js';
	let app = getApp();
	const { proxy } = getCurrentInstance();
	const appStore = useAppStore();
	const { isLogin } = storeToRefs(appStore);

	const couponsList = ref([]);
	const loading = ref(false);
	const loadend = ref(false);
	const loadTitle = ref('加载更多');//提示语
	const page = ref(1);
	const limit = ref(20);
	const type = ref(1);
	const isShow = ref(false);
	const navList = ref([{
			type: 1,
			name: '通用券',
			count: 0
		},
		{
			type: 2,
			name: '商品券',
			count: 0
		},
		{
			type: 3,
			name: '品类券',
			count: 0
		},
	]);
	const count = ref(0);
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

	 /**
	   * 页面上拉触底事件的处理函数
	   */
	onReachBottom(() => {
		getUseCoupons();
	});

	function getCoupon(id, index) {
		let list = couponsList.value;
		let ids = [];
		ids.push(id);
		//领取优惠券
		setCouponReceive(id).then(function (res) {
			list[index].isUse = true;
			couponsList.value = list;
			proxy.$util.Tips({ title: '领取成功' });
		}, function (res) {
			return proxy.$util.Tips({ title: res });
		})
	}
	 /**
	   * 获取领取优惠券列表
	  */
	function getUseCoupons() {
		if (loadend.value) return false;
		if (loading.value) return false;
		loading.value = true;
		getCoupons({ page: page.value, limit: limit.value, type: type.value }).then(res => {
			let list = res.data.list, loadendVal = list.length < limit.value;
			let list2 = proxy.$util.SplitArray(list, couponsList.value);
			couponsList.value = list2;
			loadend.value = loadendVal;
			loadTitle.value = loadendVal ? '我也是有底线的~' : '加载更多';
			page.value = page.value + 1;
			loading.value = false;
			isShow.value = true;
		}).catch(err => {
			loading.value = false;
			loadTitle.value = '加载更多';
		});
	}
	function setType(t) {
		if (type.value !== t) {
			type.value = t;
			couponsList.value = [];
			page.value = 1;
			loadend.value = false;
			getUseCoupons();
		}
	}
</script>

<style scoped lang="scss">
	.nav {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 106rpx;
		background-color: #FFFFFF;
		font-size: 30rpx;
		color: #999999;
		z-index: 9;
	}
	
	.nav .acea-row {
		border-top: 5rpx solid transparent;
		border-bottom: 5rpx solid transparent;
		cursor: pointer;
	}
	
	.nav .acea-row.on {
		@include tab_border_bottom(theme);
		@include main_color(theme);
	}
	
	.condition .line-title{
	  width:90rpx;
	  padding: 0 10rpx;
	  box-sizing: border-box;
	  background:#fff;
	  opacity:1;
	  border-radius:20rpx;
	  font-size:20rpx;
	  margin-right: 12rpx;
	}
	.condition .line-title.gray{
		border:1px solid #BBB;
	  color:#bbb;
	  background-color:#F5F5F5;
	}
	.coupon-list .pic-num{
	  color: #FFFFFF;
	  font-size: 24rpx;
	}
	.main_bg{
		@include main_bg_color(theme);
	}
	.select{
		@include main_color(theme);
		@include coupons_border_color(theme);
	}
</style>
