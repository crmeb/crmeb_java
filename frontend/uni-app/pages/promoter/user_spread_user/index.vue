<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class='my-promotion'>
			<view class="header">
				<image class="head_img" :src="urlDomain+'/crmebimage/maintain/2021/07/13/48e81e3e2e374d48820b7a9a56905365k2qa9yj8n5.png'"></image>
				<navigator :render-link="false" :url="'/pages/promoter/user_spread_money/index?type=1&extractCount='+spreadInfo.extractCount"  hover-class="none" class='record'>提现记录<text class='iconfont icon-xiangyou'></text></navigator>
				<view class="head_box">
					<view class='name acea-row row-center-wrapper'>
						<view>当前佣金</view>
					</view>
					<view class='num'>{{spreadInfo.commissionCount}}</view>
					<view class='profit acea-row row-between-wrapper'>
						<view class='item'>
							<view>昨日收益</view>
							<view class='money'>{{spreadInfo.lastDayCount ? Number(spreadInfo.lastDayCount).toFixed(2) : 0}}</view>
						</view>
						<view class='item'>
							<view>累积已提</view>
							<view class='money'>{{spreadInfo.extractCount ? Number(spreadInfo.extractCount).toFixed(2) : 0}}</view>
						</view>
					</view>
				</view>
			</view>
			<!-- #ifdef APP-PLUS || H5 -->
			<navigator :render-link="false" url="/pages/users/user_cash/index" hover-class="none" class='bnt bg_color'>立即提现</navigator>
			<!-- #endif -->
			<!-- #ifdef MP -->
			<view @click="openSubscribe('/pages/users/user_cash/index')" class='bnt bg_color'>立即提现</view>
			<!-- #endif -->
			<view class='list acea-row row-between-wrapper'>
				<navigator :render-link="false" url='/pages/promoter/user_spread_code/index' hover-class="none" class='item acea-row row-center-wrapper row-column'>
					<text class='iconfont icon-erweima'></text>
					<view>推广名片</view>
				</navigator>
				<navigator :render-link="false" url='/pages/promoter/promoter-list/index' hover-class="none" class='item acea-row row-center-wrapper row-column'>
					<text class='iconfont icon-tongji'></text>
					<view>推广人统计</view>
				</navigator>
				<navigator :render-link="false" :url="'/pages/promoter/user_spread_money/index?type=2&commissionCount='+spreadInfo.commissionCount" hover-class="none" class='item acea-row row-center-wrapper row-column'>
					<text class='iconfont icon-qiandai'></text>
					<view>佣金明细</view>
				</navigator>
				<navigator :render-link="false" url='/pages/promoter/promoter-order/index' hover-class="none" class='item acea-row row-center-wrapper row-column'>
					<text class='iconfont icon-dingdan'></text>
					<view>推广人订单</view>
				</navigator>
				<navigator :render-link="false" url='/pages/promoter/promoter_rank/index' hover-class="none" class='item acea-row row-center-wrapper row-column'>
					<text class='iconfont icon-paihang1'></text>
					<view>推广人排行</view>
				</navigator>
				<navigator :render-link="false" url='/pages/promoter/commission_rank/index' hover-class="none" class='item acea-row row-center-wrapper row-column'>
					<text class='iconfont icon-paihang'></text>
					<view>佣金排行</view>
				</navigator>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, watch, getCurrentInstance } from 'vue';
	import { onShow } from '@dcloudio/uni-app';
	import { getSpreadInfo as getSpreadInfoApi } from '@/api/user.js';
	import {toLogin} from '@/libs/login.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
	import {setThemeColor} from '@/utils/setTheme.js'
import { useColor } from '@/composables/useColor.js';
	const app = getApp();
	const { proxy } = getCurrentInstance();

	const appStore = useAppStore();
	const { isLogin } = storeToRefs(appStore);

	const urlDomain = ref(proxy.$Cache.get("imgHost"));
	const spreadInfo = ref({});
	const { colorStyle } = useColor();
	const theme = ref(app.globalData.theme);
	const bgColor = ref('#e93323');

	watch(isLogin, (newV) => {
		if (newV) {
			getSpreadInfo();
		}
	}, { deep: true });

	onShow(() => {
		bgColor.value = setThemeColor();
		uni.setNavigationBarColor({
			frontColor: '#ffffff',
			backgroundColor: bgColor.value,
		});

		if (isLogin.value) {
			getSpreadInfo();
		} else {
			toLogin();
		}
	});

	function openSubscribe(page) {
		uni.navigateTo({
			url: page,
		});
	}

	/**
	 * 获取个人用户信息
	 */
	function getSpreadInfo() {
		getSpreadInfoApi().then(res => {
			spreadInfo.value = res.data;
		});
	}
</script>

<style scoped lang="scss">
	.my-promotion .header {
		width: 100%;
		height: 375rpx;
		position: relative;
	}
	.head_img{
		width: 100%;
		height: 375rpx;
		position: absolute;
		top: 0;
		z-index: 2;
	}
	.head_box{
		width: 100%;
		height: 375rpx;
		position: absolute;
		top: 0;
		z-index: 0;
		@include main_bg_color(theme);
	}
	.my-promotion .header .name {
		font-size: 30rpx;
		color: #fff;
		padding-top: 57rpx;
		position: relative;
	}

	.my-promotion .header  .record {
		font-size: 26rpx;
		color: rgba(255, 255, 255, 0.8);
		position: absolute;
		right: 20rpx;
		top:60rpx;
		z-index:10;
	}

	.my-promotion .header  .record .iconfont {
		font-size: 25rpx;
		margin-left: 10rpx;
		vertical-align: 2rpx;
	}

	.my-promotion .header .num {
		text-align: center;
		color: #fff;
		margin-top: 28rpx;
		font-size: 90rpx;
		font-family: 'Guildford Pro';
	}

	.my-promotion .header .profit {
		padding: 0 20rpx;
		margin-top: 35rpx;
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.8);
	}

	.my-promotion .header .profit .item {
		min-width: 200rpx;
		text-align: center;
	}

	.my-promotion .header .profit .item .money {
		font-size: 34rpx;
		color: #fff;
		margin-top: 5rpx;
	}

	.my-promotion .bnt {
		font-size: 28rpx;
		color: #fff;
		width: 278rpx;
		height: 108rpx;
		box-sizing: border-box;
		border: 20rpx solid #f5f5f5;
		border-radius: 50rpx;
		text-align: center;
		line-height: 68rpx;
		margin: -52rpx auto 0 auto;
		box-sizing: border-box;
		position: absolute;
		left: 0;
		right: 0;
		z-index: 3;
	}
	.bg_color{
		@include main_bg_color(theme);
	}
	.my-promotion .list {
		padding: 0 30rpx 50rpx 30rpx;
		margin-top: 60rpx;
	}

	.my-promotion .list .item {
		width: 335rpx;
		height: 240rpx;
		border-radius: 14rpx;
		background-color: #fff;
		margin-top: 20rpx;
		font-size: 30rpx;
		color: #666;
	}

	.my-promotion .list .item .iconfont {
		font-size: 70rpx;
		// background: linear-gradient(to right, #fc4d3d 0%, #e93323 100%);
		@include main_bg_color(theme);
		-webkit-background-clip: text;
		-webkit-text-fill-color: transparent;
		margin-bottom: 20rpx;
	}
</style>
