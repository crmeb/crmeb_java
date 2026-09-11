<template>
	<view :data-theme="theme" :style="colorStyle">
		<skeleton :show="showSkeleton" :isNodes="isNodes" ref="skeleton" loading="chiaroscuro" selector="skeleton"
			bgcolor="#FFF"></skeleton>
		<view class='newsList skeleton' :style="{visibility: showSkeleton ? 'hidden' : 'visible'}">
			<view class='swiper skeleton-rect' v-if="imgUrls.length > 0">
				<swiper indicator-dots="true" :autoplay="autoplay" :circular="circular" :interval="interval" :duration="duration"
				 indicator-color="rgba(102,102,102,0.3)" indicator-active-color="#666">
					<block v-for="(item,index) in imgUrls" :key="index">
						<swiper-item>
							<navigator :render-link="false" :url="'/pages/news/news_details/index?id='+item.id">
								<image :src="item.imageInput" class="slide-image" mode="aspectFill" />
							</navigator>
						</swiper-item>
					</block>
				</swiper>
			</view>
			<view class='nav'>
				<scroll-view class="scroll-view_x" scroll-x scroll-with-animation :scroll-left="scrollLeft" style="width:auto;overflow:hidden;">
					<block v-for="(item,index) in navList" :key="index">
						<view class='item borRadius14 skeleton-rect' :class='active==item.id?"on":""' @click='tabSelect(item.id, index)'>
							<view>{{item.name}}</view>
							<view class='line bg_color' v-if="active==item.id"></view>
						</view>
					</block>
				</scroll-view>
			</view>
			<view class='list'>
				<block v-for="(item,index) in articleList" :key="index">
					<view class='item acea-row row-between-wrapper' @click="toNewDetail(item.id)">
						<view class='text acea-row row-column-between'>
							<view class='name line2 skeleton-rect'>{{item.title}}</view>
							<view class="skeleton-rect">{{item.createTime}}</view>
						</view>
						<view class='pictrue skeleton-rect'>
							<image :src='item.imageInput'></image>
						</view>
					</view>
				</block>
			</view>
		</view>
		<view class='noCommodity' v-if="articleList.length == 0 && (page != 1 || active== 0) && isShow">
			<view class='pictrue'>
				<image :src="urlDomain+'/crmebimage/perset/staticImg/noNews.png'"></image>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, getCurrentInstance } from 'vue';
	import { onLoad, onShow, onReachBottom } from '@dcloudio/uni-app';
	import {
		getArticleCategoryList,
		getArticleList,
		getArticleHotList,
		getArticleBannerList,
	} from '@/api/api.js';
	import animationType from '@/utils/animationType.js'
	import Cache from '@/utils/cache.js';
import { useColor } from '@/composables/useColor.js';
	let app = getApp();

	const { proxy } = getCurrentInstance();

	// data
	const urlDomain = ref(Cache.get("imgHost"));
	const showSkeleton = ref(true); //骨架屏显示隐藏
	const isNodes = ref(0); //控制什么时候开始抓取元素节点,只要数值改变就重新抓取
	const imgUrls = ref([{imageInput:''}]);
	const articleList = ref([{imageInput:'',title: '占位占位',createTime:'占位'}]);
	const indicatorDots = ref(false);
	const circular = ref(true);
	const autoplay = ref(true);
	const interval = ref(3000);
	const duration = ref(500);
	const navList = ref([{id:0,name:'占位'},{id:0,name:'占位'},{id:0,name:'占位'}]);
	const active = ref(0);
	const page = ref(1);
	const limit = ref(8);
	const status = ref(false);
	const scrollLeft = ref(0);
	const isShow = ref(false);
	const theme = ref(app.globalData.theme);
	const { colorStyle } = useColor();

	onLoad(() => {
		setTimeout(() => {
			//couponsList.value =  [{name:''}]
			isNodes.value++;
			// #ifdef H5
			setShare();
			// #endif
		}, 500);
		getArticleHot();
		getArticleBanner();
		getArticleCate();
		status.value = false;
		page.value = 1;
		//articleList.value = [];
		getCidArticle();
	});
	/**
	 * 生命周期函数--监听页面显示
	 */
	onShow(() => {
		// getArticleHot();
		// getArticleBanner();
		// getArticleCate();
		// status.value = false;
		// page.value = 1;
		// //articleList.value = [];
		// getCidArticle();
	});
	/**
	   * 页面上拉触底事件的处理函数
	*/
	onReachBottom(() => {
	    getCidArticle();
	});

	function getArticleHot() {
		getArticleHotList().then(res => {
			articleList.value = res.data.list;
		});
	}
	function getArticleBanner() {
		getArticleBannerList().then(res => {
			imgUrls.value = res.data.list;
			setTimeout(() => {
				showSkeleton.value = false
			}, 1000)
		});
	}
	function getCidArticle() {
		if (active.value == 0) return;
		let lim = limit.value;
		let pg = page.value;
		let list = articleList.value;
		if (status.value) return;
		getArticleList(active.value, {
			page: pg,
			limit: lim
		}).then(res => {
			let articleListNew = [];
			let len = res.data.list.length;
			articleListNew = list.concat(res.data.list);
			page.value++;
			articleList.value = articleListNew;
			status.value = lim > len;
			isShow.value = true;
		});
	}
	function getArticleCate() {
		getArticleCategoryList().then(res => {
			let list = res.data.list;
			list.unshift({id:0,name:'热门'});
			navList.value = list;
			setTimeout(() => {
				showSkeleton.value = false
			}, 1000)
		});
	}
	function tabSelect(activeVal,e) {
		active.value = activeVal;
		scrollLeft.value = e * 60;
		// scrollLeft.value = (active.value - 1) * 50;
		if (active.value == 0) getArticleHot();
		else {
			articleList.value = [];
			page.value = 1;
			status.value = false;
			getCidArticle();
		}
	}
	// '"/pages/news_details/index?id="+item.id'
	function toNewDetail(id){
		uni.navigateTo({
			animationType: animationType.type,					animationDuration: animationType.duration,
			url:"/pages/news/news_details/index?id="+id
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
				desc: articleList.value[0].title,
				title: articleList.value[0].title,
				link: location.href,
				imgUrl:articleList.value[0].imageInput 
			}).then(res => {
			}).catch(err => {
			});
	}
	// #endif
</script>

<style lang="scss">
	page {
		background-color: #fff !important;
	}

	.newsList .swiper {
		width: 100%;
		position: relative;
		box-sizing: border-box;
		padding: 0 30rpx;
	}

	.newsList .swiper swiper {
		width: 100%;
		height: 365rpx;
		position: relative;
	}

	.newsList .swiper .slide-image {
		width: 100%;
		height: 335rpx;
		border-radius: 14rpx;
	}
	// #ifdef MP-WEIXIN
	.newsList .swiper .wx-swiper-dot {
		width: 12rpx !important;
		height: 12rpx !important;
		border-radius: 0;
		transform: rotate(-45deg);
		transform-origin: 0 100%;
	}
	
	.newsList .swiper .wx-swiper-dot~.wx-swiper-dot {
		margin-left: 5rpx;
	}

	.newsList .swiper .wx-swiper-dots.wx-swiper-dots-horizontal {
		margin-bottom: -15rpx;
	}
	// #endif
	// #ifdef APP-PLUS || H5
	.newsList .swiper .uni-swiper-dot {
			width: 12rpx !important;
			height: 12rpx !important;
			border-radius: 0;
			transform: rotate(-45deg);
			transform-origin: 0 100%;
	}
	
	.newsList .swiper .uni-swiper-dot~.uni-swiper-dot {
		margin-left: 5rpx;
	}
	
	.newsList .swiper .uni-swiper-dots.uni-swiper-dots-horizontal {
		margin-bottom: -15rpx;
	}
	// #endif
	.newsList .nav {
		padding: 0 24rpx;
		width: 100%;
		white-space: nowrap;
		box-sizing: border-box;
		margin-top: 43rpx;
	}

	.newsList .nav .item {
		display: inline-block;
		font-size: 32rpx;
		color: #999;
	}

	.newsList .nav .item.on {
		color: #282828;
	}

	.newsList .nav .item~.item {
		margin-left: 46rpx;
	}

	.newsList .nav .item .line {
		width: 24rpx;
		height: 4rpx;
		border-radius: 2rpx;
		margin: 10rpx auto 0 auto;
		@include main_bg_color(theme);
	}

	.newsList .list .item {
		margin: 0 24rpx;
		border-bottom: 1rpx solid #f0f0f0;
		padding: 35rpx 0;
	}

	.newsList .list .item .pictrue {
		width: 250rpx;
		height: 156rpx;
	}

	.newsList .list .item .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 14rpx;
	}

	.newsList .list .item .text {
		width: 420rpx;
		height: 156rpx;
		font-size: 24rpx;
		color: #999;
	}

	.newsList .list .item .text .name {
		font-size: 30rpx;
		color: #282828;
	}

	.newsList .list .item .picList .pictrue {
		width: 335rpx;
		height: 210rpx;
		margin-top: 30rpx;
	}

	.newsList .list .item .picList.on .pictrue {
		width: 217rpx;
		height: 136rpx;
	}

	.newsList .list .item .picList .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 6rpx;
	}

	.newsList .list .item .time {
		text-align: right;
		font-size: 24rpx;
		color: #999;
		margin-top: 22rpx;
	}
</style>
