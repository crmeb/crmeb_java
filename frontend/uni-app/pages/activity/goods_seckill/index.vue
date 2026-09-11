<template>
	<view :data-theme="theme" :style="colorStyle">
		<skeleton :show="showSkeleton" :isNodes="isNodes" ref="skeleton" loading="chiaroscuro" selector="skeleton"
			bgcolor="#FFF"></skeleton>
		<view class="skeleton" :style="{visibility: showSkeleton ? 'hidden' : 'visible'}">
			<view class="combinationBj"></view>
			<view class='flash-sale'>
				<!-- #ifdef H5 -->
				<view class='iconfont icon-xiangzuo' @tap='goBack' :style="'top:'+ (navH/2) +'rpx'" v-if="returnShow"></view>
				<!-- #endif -->
				<view class="saleBox"></view>
				<view class="header skeleton-rect" v-if="dataList.length">
					<swiper indicator-dots="true" autoplay="true" :circular="circular" interval="3000" duration="1500"
						indicator-color="rgba(255,255,255,0.6)" indicator-active-color="#fff">
						<block v-for="(items,index) in dataList[active].slide" :key="index">
							<swiper-item class="borRadius14">
								<image :src="items.sattDir" class="slide-image borRadius14" lazy-load></image>
							</swiper-item>
						</block>
					</swiper>
			
				</view>
				<view class="seckillList acea-row row-between-wrapper">
					<view class="priceTag skeleton-rect">
						<image :src="urlDomain+'/crmebimage/perset/staticImg/priceTag.png'"></image>
					</view>
					<view class='timeLsit'>
						<scroll-view class="scroll-view_x" scroll-x scroll-with-animation :scroll-left="scrollLeft"
							style="width:auto;overflow:hidden;">
							<block v-for="(item,index) in dataList" :key='index'>
								<view @tap='settimeList(item,index)' class='item' :class="active == index?'on':''">
									<view class='time'>{{item.time.split(',')[0]}}</view>
									<view class="state">{{item.statusName}}</view>
								</view>
							</block>
						</scroll-view>
					</view>
				</view>
				<view class='list pad30' v-if='seckillList.length>0'>
					<block v-for="(item,index) in seckillList" :key='index'>
						<view class='item acea-row row-between-wrapper' @tap='goDetails(item)'>
							<view class='pictrue skeleton-rect'>
								<image :src='item.image'></image>
							</view>
							<view class='text acea-row row-column-around'>
								<view class='name line1 skeleton-rect'>{{item.title}}</view>
								<view class='money skeleton-rect'><text class="font_color">￥</text>
									<text class='num font_color'>{{item.price}}</text>
									<text class="y_money">￥{{item.otPrice}}</text>
								</view>
								<view class="limit skeleton-rect">限量 <text class="limitPrice">{{item.quota}} {{item.unitName}}</text>
								</view>
								<view class="progress skeleton-rect">
									<view class='bg-reds' :style="'width:'+item.percent+'%;'"></view>
									<view class='piece'>已抢{{item.percent}}%</view>
								</view>
							</view>
							<view class='grab bg_color' v-if="status == 2">马上抢</view>
							<view class='grab bg_color' v-else-if="status == 1">未开始</view>
							<view class='grab bg-color-hui' v-else>已结束</view>
						</view>
					</block>
				</view>
			</view>
			<view class='noCommodity' v-if="seckillList.length == 0 && (page != 1 || active== 0)">
				<view class='pictrue'>
					<image :src="urlDomain+'/crmebimage/perset/staticImg/noShopper.png'"></image>
				</view>
			</view>
		</view>	
	</view>
</template>

<script setup>
	import { ref, getCurrentInstance } from 'vue';
	import { onLoad, onReachBottom } from '@dcloudio/uni-app';
	import {
		getSeckillHeaderApi,
		getSeckillList as getSeckillListApi
	} from '../../../api/activity.js';
	import animationType from '@/utils/animationType.js'
import { useColor } from '@/composables/useColor.js';
	let app = getApp();
	const { proxy } = getCurrentInstance();

	const urlDomain = ref(proxy.$Cache.get("imgHost"));
	const showSkeleton = ref(true); //骨架屏显示隐藏
	const isNodes = ref(0); //控制什么时候开始抓取元素节点,只要数值改变就重新抓取
	const circular = ref(true);
	const autoplay = ref(true);
	const topImage = ref('');
	const seckillList = ref([]);
	const timeList = ref([]);
	const active = ref(0);
	const scrollLeft = ref(0);
	const interval = ref(0);
	const status = ref(1);
	const countDownHour = ref("00");
	const countDownMinute = ref("00");
	const countDownSecond = ref("00");
	const page = ref(1);
	const limit = ref(10);
	const loading = ref(false);
	const loadend = ref(false);
	const pageloading = ref(false);
	const dataList = ref([]);
	const returnShow = ref(true);
	const navH = ref('');
	const { colorStyle } = useColor();
	const theme = ref(app.globalData.theme);

	function goBack() {
		uni.navigateBack();
	}
	function getSeckillConfig() {
		//if(showSkeleton.value) dataList.value = [{slide:''}]
		seckillList.value = [{image:'',otPrice:'',percent:'',price:'',title:''}];
		getSeckillHeaderApi().then(res => {
			if(res.data == ''){
				proxy.$util.Tips({
					title: '暂无秒杀活动'
				}, {
					url: '/pages/index/index'
				});
				return;
			}
			seckillList.value = [];
			res.data.map(item => {
				item.slide = JSON.parse(item.slide)
			})
			dataList.value = res.data;
			page.value = 1;
			status.value = dataList.value[active.value].status;
			getSeckillList();

		});
	}
	function getSeckillList() {
		var data = {
			page: page.value,
			limit: limit.value
		};
		if (loadend.value) return;
		if (pageloading.value) return;
		pageloading.value = true
		getSeckillListApi(dataList.value[active.value].id, data).then(res => {
			// seckillList.value = [];
			var list = res.data.list;
			var loadendVal = list.length < limit.value;
			seckillList.value = seckillList.value.concat(list);
			page.value++;
			pageloading.value = false;
			loadend.value = loadendVal;
			// #ifdef H5
			setShare();
			// #endif
			setTimeout(() => {
				showSkeleton.value = false
			}, 1000)
		}).catch(err => {
			pageloading.value = false
		});
	}
	function settimeList(item, index) {
		if(index !== active.value){
			active.value = index
			if (interval.value) {
				clearInterval(interval.value);
				interval.value = null
			}
			interval.value = 0,
				countDownHour.value = "00";
			countDownMinute.value = "00";
			countDownSecond.value = "00";
			status.value = dataList.value[active.value].status;
			loadend.value = false;
			page.value = 1;
			seckillList.value = [];
			// wxh.time(e.currentTarget.dataset.stop, that);
			getSeckillList();
		}
	}
	function goDetails(item) {
		uni.navigateTo({
			animationType: animationType.type,					animationDuration: animationType.duration,
			url: '/pages/activity/goods_seckill_details/index?id=' + item.id
		})
	}
	// #ifdef H5
	function setShare() {
		proxy.$wechat.isWeixin() &&
			proxy.$wechat.wechatEvevt([
				"updateAppMessageShareData",
				"updateTimelineShareData",
				"onMenuShareAppMessage",
				"onMenuShareTimeline"
			], {
				desc: seckillList.value[0].title,
				title: seckillList.value[0].title,
				link: location.href,
				imgUrl:seckillList.value[0].image 
			}).then(res => {
			}).catch(err => {
			});
	}
	// #endif

	onLoad(() => {
		setTimeout(() => {
			isNodes.value++;
		}, 500);
		var pages = getCurrentPages();
		returnShow.value = pages.length===1?false:true;
		// #ifdef H5
		navH.value = app.globalData.navHeight-18;
		// #endif
		getSeckillConfig();
	});

	/**
	 * 页面上拉触底事件的处理函数
	 */
	onReachBottom(() => {
		getSeckillList();
	});
</script>

<style>
	page {
		background-color: #F5F5F5 !important;
	}
</style>
<style scoped lang="scss">

   .icon-xiangzuo {
		font-size: 40rpx;
		color: #fff;
		position: fixed;
		left: 30rpx;
		z-index: 99;
		transform: translateY(-20%);
	}
	.flash-sale .header {
		width: 710rpx;
		height: 330rpx;
		margin: -276rpx auto 0 auto;
		border-radius: 14rpx;
		overflow: hidden;
		swiper{
			height: 330rpx !important;
			border-radius: 14rpx;
			overflow: hidden;
		}
	}

	.flash-sale .header image {
		width: 100%;
		height: 100%;
		border-radius: 14rpx;
		overflow: hidden;
	}

	.flash-sale .seckillList {
		padding: 25rpx;
	}

	.flash-sale .seckillList .priceTag {
		width: 75rpx;
		height: 70rpx;
	}

	.flash-sale .seckillList .priceTag image {
		width: 100%;
		height: 100%;
	}

	.flash-sale .timeLsit {
		width: 596rpx;
		white-space: nowrap;
	}

	.flash-sale .timeLsit .item {
		display: inline-block;
		font-size: 20rpx;
		color: #666;
		text-align: center;
		box-sizing: border-box;
		margin-right: 30rpx;
		width: 130rpx;
	}

	.flash-sale .timeLsit .item .time {
		font-size: 36rpx;
		font-weight: 600;
		color: #333;
	}

	.flash-sale .timeLsit .item.on .time {
		@include main_color(theme);
	}

	.flash-sale .timeLsit .item.on .state {
		height: 30rpx;
		line-height: 30rpx;
		border-radius: 15rpx;
		width: 128rpx;
		@include main_bg_color(theme);
		color: #fff;
	}

	.flash-sale .countDown {
		height: 92rpx;
		border-bottom: 1rpx solid #f0f0f0;
		margin-top: -14rpx;
		font-size: 28rpx;
		color: #282828;
	}

	.flash-sale .countDown .num {
		font-size: 28rpx;
		font-weight: bold;
		background-color: #ffcfcb;
		padding: 4rpx 7rpx;
		border-radius: 3rpx;
	}

	.flash-sale .countDown .text {
		font-size: 28rpx;
		color: #282828;
		margin-right: 13rpx;
	}

	.flash-sale .list .item {
		height: 230rpx;
		position: relative;
		/* width: 710rpx; */
		margin: 0 auto 20rpx auto;
		background-color: #fff;
		border-radius: 14rpx;
		padding: 25rpx 24rpx;
	}

	.flash-sale .list .item .pictrue {
		width: 180rpx;
		height: 180rpx;
		border-radius: 10rpx;
		background-color: #F5F5F5;
	}

	.flash-sale .list .item .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 10rpx;
	}

	.flash-sale .list .item .text {
		width: 440rpx;
		font-size: 30rpx;
		color: #333;
		height: 166rpx;
	}

	.flash-sale .list .item .text .name {
		width: 100%;
	}

	.flash-sale .list .item .text .money {
		font-size: 30rpx;
		@include price_color(theme);
	}

	.flash-sale .list .item .text .money .num {
		font-size: 40rpx;
		font-weight: 500;
		font-family: 'Guildford Pro';
	}

	.flash-sale .list .item .text .money .y_money {
		font-size: 24rpx;
		color: #999;
		text-decoration-line: line-through;
		margin-left: 15rpx;
	}

	.flash-sale .list .item .text .limit {
		font-size: 22rpx;
		color: #999;
		margin-bottom: 5rpx;
	}

	.flash-sale .list .item .text .limit .limitPrice {
		margin-left: 10rpx;
	}

	.flash-sale .list .item .text .progress {
		overflow: hidden;
		background-color: #EEEEEE;
		width: 260rpx;
		border-radius: 18rpx;
		height: 18rpx;
		position: relative;
	}

	.flash-sale .list .item .text .progress .bg-reds {
		width: 0;
		height: 100%;
		transition: width 0.6s ease;
		@include second-gradient(theme);
	}

	.flash-sale .list .item .text .progress .piece {
		position: absolute;
		left: 8%;
		transform: translate(0%, -50%);
		top: 49%;
		font-size: 16rpx;
		color: #FFB9B9;
	}

	.flash-sale .list .item .grab {
		font-size: 28rpx;
		color: #fff;
		width: 150rpx;
		height: 54rpx;
		border-radius: 27rpx;
		text-align: center;
		line-height: 54rpx;
		position: absolute;
		right: 30rpx;
		bottom: 30rpx;
		background: #bbbbbb;
	}
	.bg_color{
		@include main_bg_color(theme);
	}
	.flash-sale .saleBox {
		width: 100%;
		height: 298rpx;
		/* #ifdef MP */
		height: 300rpx;
		/* #endif */
		@include main_bg_color(theme);
		border-radius: 0 0 50rpx 50rpx;
	}
</style>
