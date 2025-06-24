<template>
	<view :data-theme="theme">
		<skeleton :show="showSkeleton" :isNodes="isNodes" ref="skeleton" loading="chiaroscuro" selector="skeleton"
			bgcolor="#FFF"></skeleton>
		<view class='newsList skeleton' :style="{visibility: showSkeleton ? 'hidden' : 'visible'}">
			<view class='swiper skeleton-rect' v-if="imgUrls.length > 0">
				<swiper indicator-dots="true" :autoplay="autoplay" :circular="circular" :interval="interval" :duration="duration"
				 indicator-color="rgba(102,102,102,0.3)" indicator-active-color="#666">
					<block v-for="(item,index) in imgUrls" :key="index">
						<swiper-item>
							<navigator :url="'/pages/news/news_details/index?id='+item.id">
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
				<image :src="urlDomain+'crmebimage/perset/staticImg/noNews.png'"></image>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getArticleCategoryList,
		getArticleList,
		getArticleHotList,
		getArticleBannerList,
	} from '@/api/api.js';
	import animationType from '@/utils/animationType.js'
	let app = getApp();
	export default {
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				showSkeleton: true, //骨架屏显示隐藏
				isNodes: 0, //控制什么时候开始抓取元素节点,只要数值改变就重新抓取
				imgUrls: [{imageInput:''}],
				articleList: [{imageInput:'',title: '占位占位',createTime:'占位'}],
				indicatorDots: false,
				circular: true,
				autoplay: true,
				interval: 3000,
				duration: 500,
				navList: [{id:0,name:'占位'},{id:0,name:'占位'},{id:0,name:'占位'}],
				active: 0,
				page: 1,
				limit: 8,
				status: false,
				scrollLeft: 0,
				isShow: false,
				theme:app.globalData.theme,
			};
		},
		onLoad(){
			setTimeout(() => {
				//this.couponsList =  [{name:''}]
				this.isNodes++;
				// #ifdef H5
				this.setShare();
				// #endif
			}, 500);
			this.getArticleHot();
			this.getArticleBanner();
			this.getArticleCate();
			this.status = false;
			this.page = 1;
			//this.articleList = [];
			this.getCidArticle();
		},
		/**
		 * 生命周期函数--监听页面显示
		 */
		onShow: function() {
			// this.getArticleHot();
			// this.getArticleBanner();
			// this.getArticleCate();
			// this.status = false;
			// this.page = 1;
			// //this.articleList = [];
			// this.getCidArticle();
		},
		/**
		   * 页面上拉触底事件的处理函数
		*/
		onReachBottom: function () {
		    this.getCidArticle();
		},
		methods: {
			getArticleHot: function() {
				let that = this;
				getArticleHotList().then(res => {
					that.$set(that, 'articleList', res.data.list);
				});
			},
			getArticleBanner: function() {
				let that = this;
				getArticleBannerList().then(res => {
					that.imgUrls = res.data.list;
					setTimeout(() => {
						this.showSkeleton = false
					}, 1000)
				});
			},
			getCidArticle: function() {
				let that = this;
				if (that.active == 0) return;
				let limit = that.limit;
				let page = that.page;
				let articleList = that.articleList;
				if (that.status) return;
				getArticleList(that.active, {
					page: page,
					limit: limit
				}).then(res => {
					let articleListNew = [];
					let len = res.data.list.length;
					articleListNew = articleList.concat(res.data.list);
					that.page++;
					that.$set(that, 'articleList', articleListNew);
					that.status = limit > len;
					that.page = that.page;
					that.isShow = true;
				});
			},
			getArticleCate: function() {
				let that = this;
				getArticleCategoryList().then(res => {
					let list = res.data.list;
					list.unshift({id:0,name:'热门'});
					that.$set(that, 'navList', list);
					setTimeout(() => {
						this.showSkeleton = false
					}, 1000)
				});
			},
			tabSelect(active,e) {
				this.active = active;
				this.scrollLeft =  e * 60;
				// this.scrollLeft = (active - 1) * 50;
				if (this.active == 0) this.getArticleHot();
				else {
					this.$set(this, 'articleList', []);
					this.page = 1;
					this.status = false;
					this.getCidArticle();
				}
			},
			// '"/pages/news_details/index?id="+item.id'
			toNewDetail(id){
				uni.navigateTo({
					animationType: animationType.type,					animationDuration: animationType.duration,
					url:"/pages/news/news_details/index?id="+id
				})
			},
			setShare: function() {
				this.$wechat.isWeixin() &&
					this.$wechat.wechatEvevt([
						"updateAppMessageShareData",
						"updateTimelineShareData",
						"onMenuShareAppMessage",
						"onMenuShareTimeline"
					], {
						desc: this.articleList[0].title,
						title: this.articleList[0].title,
						link: location.href,
						imgUrl:this.articleList[0].imageInput 
					}).then(res => {
					}).catch(err => {
						console.log(err);
					});
			},
		}
	}
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
