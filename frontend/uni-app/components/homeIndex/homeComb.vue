<template>
	<!-- 组合组件 -->
	<view class="page_count tui-skeleton" :data-theme="theme" :style="colorStyle">
		<!--logo-->
		<!-- 组合组件背景图轮换 -->
		<view class="bg-img" :style="{'background-image': bgColor}">
			<block v-for="(item,index) in banner" :key="index">
				<img class="bg-image" :src="item.img" alt="" v-show="index == swiperCur">
			</block>
			<view class="maskBg" :style="[maskBgStyle]"></view>
		</view>
		<!--头部-->
		<view :class="{scrolled:isScrolled, 'my-main': true}" :style="{ height: myMainHeight+'px' }">
			<!--搜索-->
			<!-- #ifdef H5 -->
			<view class="header">
				<view class="serch-wrapper acea-row">
					<view v-if="logoConfig" class="logo skeleton-rect">
						<image :src="logoUrl" mode="scaleToFill"></image>
					</view>
					<navigator :render-link="false" v-if="hotWords.length > 0" :url="'/pages/goods/goods_search/index?searchVal='+searchVal"
						:style="[searchBoxStyle]" :class="logoConfig ? 'input' : 'uninput'" hover-class="none"
						class="input">
						<view class='swiperTxt'>
							<swiper :indicator-dots="indicatorDots" :autoplay="true" :interval="interval"
								:duration="duration" vertical="true" circular="true" @change="textChange">
								<block v-for="(item,index) in hotWords" :key='index'>
									<swiper-item catchtouchmove='catchTouchMove'>
										<view class=''>
											<view class='text'>
												<view class='newsTitle line1'><text
														class="iconfont icon-sousuo"></text><text>{{item.val}}</text>
												</view>
											</view>
										</view>
									</swiper-item>
								</block>
							</swiper>
						</view>
					</navigator>
					<navigator :render-link="false" v-else :style="[searchBoxStyle]" :class="logoConfig ? 'input' : 'uninput'"
						url="/pages/goods/goods_search/index" class="input" hover-class="none">
						<text class="iconfont icon-sousuo8"></text>
						<text class="line1">{{placeWords}}</text>
					</navigator>
				</view>
			</view>
			<!-- #endif -->
			<!-- #ifdef MP || APP-PLUS-->
			<view class="mp-header">
				<view class="sys-head" :style="{ height: `${isSmallPage?0:statusBarHeight}px`}"></view>
				<!-- #ifdef MP -->
				<view class="serch-box" :style="{ 'margin-top': `${searchTop}px`,'height': `${searchHeight}px`}">
				<!-- #endif -->
				<!-- #ifdef APP-PLUS -->
				<view class="serch-box" style="margin-top: 9px;margin-right: 2px;">
				<!-- #endif -->
					<view class="serch-wrapper acea-row">
						<view v-if="logoConfig" class="logo tui-skeleton-rect">
							<image :src="logoUrl" mode="scaleToFill"></image>
						</view>
						<navigator :render-link="false" v-if="hotWords.length > 0"
							:url="'/pages/goods/goods_search/index?searchVal='+searchVal" :style="[searchBoxStyle]"
							hover-class="none" class="input" :class="logoConfig&&!isSmallPage ? 'input' : 'uninput'">
							<view class='swiperTxt'>
								<swiper :indicator-dots="indicatorDots" :autoplay="true" :interval="interval"
									:duration="duration" vertical="true" circular="true" @change="textChange">
									<block v-for="(item,index) in hotWords" :key='index'>
										<swiper-item catchtouchmove='catchTouchMove'>
											<view class='acea-row row-between-wrapper text-box'>
												<view class='text'>
													<view class='newsTitle line1'><text
															class="iconfont icon-sousuo"></text><text>{{item.val}}</text>
													</view>
												</view>
											</view>
										</swiper-item>
									</block>
								</swiper>
							</view>
						</navigator>
						<navigator :render-link="false" v-else :style="[searchBoxStyle]" :class="logoConfig ? 'input' : 'uninput'"
							url="/pages/goods/goods_search/index" class="input" hover-class="none">
							<text class="iconfont icon-sousuo8"></text>
							<text class="line1">{{placeWords}}</text>
						</navigator>
					</view>
				</view>
			</view>
			<!-- #endif -->
			<view v-if="tabShowConfig" class="navTabBox tabNav tui-skeletonpictrue acea-row" :style="'top:'+isTop">
				<view class="longTab">
					<scroll-view scroll-x="true" style="white-space: nowrap; display: flex;" scroll-with-animation
						:scroll-left="tabLeft" show-scrollbar="true">
						<view class="longItem" :data-index="index" v-for="(item,index) in tabList" :key="index"
							:id="'id'+index" @click="longClick(index,item)"
							:class="tabClick === index? 'navChecked':''">
							<view class="acea-row row-middle">
								<view class="name tui-skeleton-rect">{{item.title}}</view>
								<view class="underlineBox" v-if="index===tabClick">
									<!-- <view class="underline"></view> -->
								</view>
							</view>
						</view>

					</scroll-view>
				</view>
				<view class="category">
					<text v-if="isShow" class="iconfont icon-xiangshang" @click="isShow=false"></text>
					<text v-if="!isShow" class="iconfont icon-xiangxia" @click="isShow=true"></text>
				</view>
			</view>
			<view v-if="isShow" class="navChangeBox" catchtouchmove="true" :style="'top:'+isTop">
				<view class="navChange">
					<block v-for="(item,index) in tabList" :key="index">
						<view class="titleBox">
							<text :class="tabClick === index ? 'checkColor' : 'textColor' " class="title line1"
								:id="'id'+index" @click="longClick(index,item)">{{item.title}}</text>
						</view>
					</block>
				</view>
				<view class="mask" @touchmove.prevent :hidden="!isShow" @click="isShow=false"></view>
			</view>
		</view>

		<!-- banner -->
		<view class="swiperBg" :style="{ marginTop: swiperTop+'px'}">
			<view class="swiper page_swiper" v-if="navIndex === 0">
				<swiper :autoplay="true" :circular="circular" :interval="intervalBanner" :duration="duration"
					:previous-margin="swiperType==0?'30rpx':''" :next-margin="swiperType==0?'30rpx':''"
					:current="swiperCur" @change="swiperChange">
					<block v-for="(item,index) in banner" :key="index">
						<swiper-item :style="[contentStyleBanner]" :class="{ active: index == swiperCur }"
							class="scalex">
							<view @click="menusTap(item.info[1].value)"
								class='slide-navigator acea-row row-between-wrapper tui-skeleton-rect'
								:class="swiperType==0?'row-between-wrapper-1':'row-between-wrapper-2'">
								<image mode="aspectFill" :style="[contentStyleBanner]" :src="item.img"
									class="slide-image aa"></image>
							</view>
						</swiper-item>
					</block>
				</swiper>
				<view v-if="docType === 0" class="dots" :style="[dotStyle]">
					<block v-for="(item,index) in banner" :key="index">
						<view class="dot-item"
							:style="{'background-color': swiperCur === index ? (dataConfig.themeStyleConfig.tabVal?dataConfig.docColor.color[0].item:themeColor)  : ''}">
						</view>
					</block>
				</view>
				<view v-if="docType === 1" class="dots" :style="[dotStyle]">
					<block v-for="(item,index) in banner" :key="index">
						<view class="dot"
							:style="{'background-color': swiperCur === index ? (dataConfig.themeStyleConfig.tabVal?dataConfig.docColor.color[0].item:themeColor)  : ''}">
						</view>
					</block>
				</view>
			</view>
		</view>

	</view>
</template>

<script setup>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	import { ref, computed, watch, nextTick, getCurrentInstance } from 'vue';
	import {
		goPage
	} from '@/libs/iframe.js'
	import animationType from '@/utils/animationType.js'
	import util from '@/utils/util.js';
	import { filterTheme } from '@/filters';
import { useColor } from '@/composables/useColor.js';
	let app = getApp();
	const { proxy } = getCurrentInstance();

	const props = defineProps({
		dataConfig: {
			type: Object,
			default: () => {}
		},
		//判断首页显示内容，1显示分类页和商品，0首页
		navIndex: {
			type: Number,
			default: 0
		},
		//是否开始滚动
		isScrolled: {
			type: Boolean,
			default: false
		},
		//是否为微页面
		isSmallPage: {
			type: Boolean,
			default: false
		},
		//页面设置信息
		bgInfo: {
			type: Object,
			default: () => {}
		},
	});
	const emit = defineEmits(['changeTab']);

	const myMainHeight = ref(0); //头部tab切换页和搜索按钮的高度和
	const indicatorDots = ref(false);
	const circular = ref(true);
	const autoplay = ref(false);
	const duration = ref(500);
	const searchH = ref(0);
	const swiperTop = ref(0);
	const statusBarHeight = ref(app.globalData.statusBarHeight); //手机端头部显示时间位置的高度
	const swiperCur = ref(0);
	const showSkeleton = ref(true);
	const tabClick = ref(0); //导航栏被点击
	const isLeft = ref(0); //导航栏下划线位置
	const isWidth = ref(0); //每个导航栏占位
	const mainWidth = ref(app.globalData.mainWidth);
	const theme = ref(app.globalData.theme);
	const { colorStyle } = useColor();
	const tabLeft = ref(0);
	const bgColor = ref('');
	const isTop = ref(0);
	const navHeight = ref(0);
	const isShow = ref(false);
	const marTop = ref(0);
	const searchVal = ref('');
	const intervalBanner = ref(2500);
	const themeColor = ref(filterTheme(app.globalData.theme));
	const searchTop = ref(0);
	const searchRight = ref(0);
	const searchHeight = ref(0);
	const statusWidth = ref(0);

	//分类是否展示，0展示，1不展示
	const tabShowConfig = computed(() => props.dataConfig.tabShowConfig.tabVal == 0);
	//搜索提示语
	const placeWords = computed(() => props.dataConfig.placeWords.val);
	//轮播切换时间
	const interval = computed(() => props.dataConfig.titleConfig.val * 1000);
	//指示器类型，0圆，1直，2无
	const docType = computed(() => props.dataConfig.docConfig.tabVal);
	//轮播图样式
	const swiperType = computed(() => props.dataConfig.swiperStyleConfig.tabVal);
	//搜索热词列表
	const hotWords = computed(() => props.dataConfig.hotWords.list);
	//分类选中颜色
	const lineColor = computed(() => ({
		backgroundColor: props.dataConfig.checkColor.color[0].item
	}));
	const maskBgStyle = computed(() => ({
		background: props.bgInfo.isBgColor == '1' ?
			`linear-gradient(180deg, rgba(245, 245, 245, 0) 0%, ${props.bgInfo.colorPicker} 100%)` :
			`linear-gradient(180deg, rgba(245, 245, 245, 0) 0%, #f5f5f5 100%)`,
	}));
	//判断logo图是否展示
	const logoConfig = computed(() => props.dataConfig.logoConfig.url && props.dataConfig.searConfig.tabVal === 1);
	//logo图
	const logoUrl = computed(() => {
		if (props.isScrolled && props.dataConfig.logoFixConfig.url) {
			return props.dataConfig.logoFixConfig.url
		} else {
			return props.dataConfig.logoConfig.url
		}
	});
	//标签文字颜色
	const textColor = computed(() => props.dataConfig.fontColor.color[0].item);
	//分类列表
	const tabList = computed(() => {
		//type=0微页面，1分类，2首页
		let list = props.dataConfig.listConfig.list;
		list.unshift({
			title: '首页',
			type: 2,
			val: 0
		})
		return list
	});
	//轮播列表
	const banner = computed(() => props.dataConfig.swiperConfig.list);
	//搜索框样式
	const searchBoxStyle = computed(() => ({
		borderRadius: props.dataConfig.contentStyle.val ? props.dataConfig.contentStyle.val + 'px' : '0',
		backgroundColor: props.dataConfig.borderColor.color[0].item,
		color: props.dataConfig.textColor.color[0].item,
		textAlign: props.dataConfig.textPosition.list[props.dataConfig.textPosition.tabVal].style,
		// #ifdef MP
		height: searchHeight.value + 'px',
		flex: !props.isSmallPage ? 1 : '',
		marginRight: !props.isSmallPage ? (statusWidth.value + searchRight.value + 'px') : '',
		// #endif
	}));
	//指示器样式
	const dotStyle = computed(() => ({
		padding: '0 40rpx',
		justifyContent: props.dataConfig.txtStyle.tabVal === 1 ? 'center' : props.dataConfig.txtStyle
			.tabVal === 2 ? 'flex-end' : 'flex-start'
	}));
	//轮播图圆角
	const contentStyleBanner = computed(() => ({
		'borderRadius': props.dataConfig.contentStyleBanner.val ? props.dataConfig.contentStyleBanner
			.val + 'px' : '0'
	}));

	watch(banner, (val) => {
		bgColor.value = val[0].img;
	}, { immediate: true });

	// #ifdef MP || APP-PLUS
	isTop.value = (props.isSmallPage ? 0 : statusBarHeight.value) + 48 + 'px' //分类的top值
	// tabShowConfig，true有分类,false无分类
	if (!tabShowConfig.value) {
		myMainHeight.value = (props.isSmallPage ? 0 : statusBarHeight.value) + 40 + 10; //头部tab切换页和搜索按钮的高度和，10是下边距
	} else {
		myMainHeight.value = (props.isSmallPage ? 0 : statusBarHeight.value) + 40 + 42; //头部tab切换页和搜索按钮的高度和
	}
	// #endif

	// #ifdef MP
	const res = uni.getMenuButtonBoundingClientRect()
	const statusHeight = res.top //胶囊距离顶部
	const statusRight = res.right //胶囊右边界坐标
	const jnHeight = res.height //胶囊高度
	statusWidth.value = res.width
	searchTop.value = statusHeight - statusBarHeight.value
	searchHeight.value = jnHeight
	//搜索框宽度计算
	uni.getSystemInfo({
		success: res => {
			searchRight.value = res.windowWidth - statusRight
		}
	})
	// #endif

	// #ifdef H5
	isTop.value = 0
	myMainHeight.value = 'auto';
	// #endif

	isWidth.value = (mainWidth.value - 65) / 4;
	setTimeout((e) => {
		const query = uni.createSelectorQuery().in(proxy);
		query.select('.header').boundingClientRect(res => {
			if (res) marTop.value = res.height //头部的高度
		}).exec();

		//展示与不展示分类的距离值判断
		if (!tabShowConfig.value) {
			// 不展示分类
			query.select('.swiperBg').boundingClientRect(res => {
				// #ifdef H5
				swiperTop.value = navHeight.value + marTop.value + statusBarHeight.value +
					4; //轮播图的top值
				//#endif
				// #ifndef H5
				if (props.isSmallPage) {
					swiperTop.value = statusBarHeight.value; //轮播图的top值
				} else {
					swiperTop.value = statusBarHeight.value + 48; //轮播图的top值
				}
				//#endif
			}).exec();
		} else {
			//展示分类
			query.select('.navTabBox').boundingClientRect(data => {
				navHeight.value = data.height //元素navHeight的高度
				// #ifdef H5
				swiperTop.value = navHeight.value + marTop.value + statusBarHeight.value +
					4; //轮播图的top值
				//#endif
				// #ifndef H5
				if (props.isSmallPage) {
					swiperTop.value = 85; //轮播图的top值
				} else {
					swiperTop.value = statusBarHeight.value + 85; //轮播图的top值
				}
				//#endif
			}).exec();
		}

	}, 200)

	//轮播图跳转
	function menusTap(url) {
		util.navigateTo(url);
	}
	function swiperChange(e) {
		let {
			current,
			source
		} = e.detail;
		if (source === 'autoplay' || source === 'touch') {
			swiperCur.value = e.detail.current;
			bgColor.value = banner.value[e.detail.current]['img']
		}
	}
	function textChange(e) {
		let {
			current,
			source
		} = e.detail;
		if (source === 'autoplay' || source === 'touch') {
			searchVal.value = hotWords.value[e.detail.current]['val']
		}
	}
	// 导航栏点击
	function longClick(index, item) {
		tabClick.value = index; //设置导航点击了哪一个
		nextTick(() => {
			let id = 'id' + index;
			tabLeft.value = (index - 2) * isWidth.value //设置下划线位置
			emit('changeTab', index, item);
		})
	}
	function parentEmit(id, index) {
		emit('changeTab', id, index);
	}
</script>

<style lang="scss" scoped>
	.uninput {
		/* #ifdef MP */
		width: 510rpx !important;
		/* #endif */
		/* #ifndef MP */
		width: 100% !important;
		/* #endif */
	}

	.icon-sousuo8 {
		font-size: 15px;

	}

	.bgwhite {
		background-color: #fff !important;
	}

	.mask {
		z-index: 999;
		top: 260rpx;
	}

	.navChangeBox {
		position: relative;
	}

	.navChange {
		background-color: #fff;
		position: absolute;
		z-index: 999999;
		width: 100%;
		border-radius: 0px 0px 16rpx 16rpx;
		padding: 24rpx 20rpx;
		display: grid;
		grid-template-rows: auto;
		grid-template-columns: repeat(5, 1fr);
		grid-column-gap: 10rpx;
		grid-row-gap: 20rpx;

		.nobg {
			background-color: #fff !important;
		}

		.titleBox {
			height: 58rpx;
			background: #F2F2F2;
			border-radius: 8rpx;
			text-align: center;
		}

		.title {
			margin: 0 auto;
			display: inline-block;
			width: 118rpx;
			height: 58rpx;
			line-height: 58rpx;
			text-align: center;

			opacity: 1;
			color: #333333;
			font-size: 24rpx;
		}

		.titleBox:nth-child(5n) {
			margin-right: 0;
		}

		.titleBox:last-child {
			margin-bottom: 0;
		}

		.changed {
			border-radius: 8rpx;
			@include cate-two-btn(theme);
			@include coupons_border_color(theme);

			.title {
				@include main_color(theme);
			}
		}
	}

	.row-middle {
		flex-direction: column;
	}

	.navTabBox {
		width: 100%;
		height: 66rpx;
		color: rgba(255, 255, 255, 1);
		position: relative;
		padding: 0 24rpx 0 24rpx;
		display: flex;
		justify-content: space-between;
		z-index: 9;

		&.isFixed {
			z-index: 10;
			position: fixed;
			left: 0;
			width: 100%;
			/* #ifdef H5 */
			top: 0;
			/* #endif */
		}

		.click {
			color: white;
		}

		.longTab {
			width: 94%;

			.longItem {
				//height: 72rpx;
				display: inline-block;
				// line-height: 52rpx;
				text-align: center;
				font-size: 28rpx;
				color: #fff;
				white-space: nowrap;
				text-overflow: ellipsis;
				margin-right: 42rpx;

				&.click {
					font-weight: bold;
					font-size: 30rpx;
					color: #fff;
					font-weight: bold;
				}

				.name {
					height: 48rpx;
				}
			}

			.underlineBox {
				margin-top: 8rpx;
				height: 3px;
				transition: .5s;

				.underline {
					width: 33rpx;
					height: 4rpx;
				}
			}
		}

		.category {
			height: 66rpx;
			line-height: 46rpx;
			z-index: 3;

			// padding: 0 15rpx 0 25rpx;
			.iconfont {
				font-size: 24rpx;
			}
		}
	}


	.swiperBg {
		z-index: 1;
		margin-top: 10rpx;

		.colorBg {
			position: absolute;
			left: 0;
			top: 0;
			height: 130rpx;
			width: 100%;
		}

		.page_swiper {
			position: relative;
			width: 100%;
			height: auto;
			margin: 0 auto;
			border-radius: 10rpx;
			overflow: hidden;
			z-index: 8;
			padding:  0rpx 20rpx 0rpx;

			image {
				width: 100%;
				height: 310rpx;
				margin: 0 auto;
				border-radius: 10rpx;
			}

			.acea-row.row-between-wrapper {
				height: 310rpx;
				margin: 0 auto;
				border-radius: 10rpx;
			}

			.acea-row.row-between-wrapper-1 {
				width: 100%;
			}

			.acea-row.row-between-wrapper-2 {
				width: 97%;
			}

			swiper {
				width: 100%;
				display: block;
				height: 310rpx;

				&.scalex {
					::v-deep .uni-swiper-slide-frame {
						transform: translate(0, 0) !important;
					}
				}
			}

			image {
				transform: scale(0.96);
				transition: all 0.6s ease;
			}

			::v-deep  swiper-item.active {
				image {
					transform: scale(1);
				}
			}

			/*用来包裹所有的小圆点  */
			.dots {
				display: flex;
				flex-direction: row;
				position: absolute;
				bottom: 40rpx;
				align-items: center;
				width: 100%;
			}

			.dot-item {
				width: 10rpx;
				height: 10rpx;
				background-color: rgba(255, 255, 255, .4);
				border-radius: 50%;
				margin: 0 6rpx;
			}

			/*未选中时的小圆点样式 */
			.dot {
				width: 16rpx;
				height: 6rpx;
				border-radius: 6rpx;
				margin-right: 6rpx;
				background-color: rgba(255, 255, 255, .4);
			}
		}
	}

	.scrolled {
		z-index: 5000;
		position: fixed;
		left: 0;
		top: 0;
		width: 100%;
		background-color: #fff !important;
		color: #000 !important;
		transition: background-color .5s ease;

		.longItem,
		.click,
		.category text {
			color: #000 !important;
		}

		.btn .iconfont {
			color: #333 !important;
		}

		.iconnum {
			background: #333 !important;
		}

		.underline {
			background: #000 !important;
		}

		.click {
			&::after {
				background-color: #fff !important;
			}
		}

		.input,
		.uninput {

			background-color: #eee !important;
		}
	}

	.page_count {
		position: relative;
		overflow: hidden;
		background-repeat: no-repeat;


		.bg-img {
			position: absolute;
			width: 100%;
			height: 100%;
			top: 0;
			/* #ifdef MP || APP-PLUS */
			z-index: -1;
			/* #endif */
			/* #ifdef H5 */
			z-index: 0;
			/* #endif */
			z-index: 0;
			filter: blur(0);
			overflow: hidden;

			.bg-image {
				width: 100%;
				height: 100%;
				filter: blur(30rpx);
				transform: scale(1.5);
			}

			.maskBg {
				position: absolute;
				bottom: 0;
				width: 100%;
				height: 136px;
				background: linear-gradient(180deg, rgba(245, 245, 245, 0) 0%, #f5f5f5 100%);
			}
		}
	}

	.my-main {
		left: 0;
		position: fixed;
		top: 0;
		width: 100%;
		z-index: 30;
		transition: background-color .5s ease;
	}

	.page_count {

		.header {
			width: 100%;
			padding: 24rpx;

			.serch-wrapper {
				align-items: center;

				.logo {
					width: 118rpx;
					margin-right: 24rpx;
				}

				image {
					width: 118rpx;
					height: 42rpx;
				}

				.input {
					display: flex;
					align-items: center;
					width: 546rpx;
					height: 55rpx;
					padding-left: 20rpx;
					font-size: 26rpx;
					padding-right: 4rpx;
					box-sizing: border-box;

					.iconfont {
						margin-right: 4rpx;
						font-size: 26rpx;
						color: #666666;
					}
				}
			}

			.tabNav {
				padding-top: 24rpx;
			}
		}

		/* #ifdef MP || APP-PLUS */
		.mp-header {
			z-index: 999;
			position: fixed;
			left: 0;
			top: 0;
			width: 100%;
			/* #ifdef H5 */
			padding-bottom: 20rpx;
			/* #endif */

			.serch-wrapper {
				height: 100%;
				align-items: center;
				padding: 0 24rpx 0 24rpx;

				image {
					width: 118rpx;
					height: 42rpx;
					margin-right: 30rpx;
				}

				.input {
					display: flex;
					align-items: center;
					/* #ifdef MP */
					width: 365rpx;
					/* #endif */
					/* #ifndef MP */
					width: 546rpx;
					/* #endif */
					/* #ifdef APP-PLUS */
					flex: 1;
					/* #endif */
					height: 50rpx;
					padding-left: 20rpx;
					font-size: 28rpx;
					box-sizing: border-box;

					.iconfont {
						margin-right: 20rpx;
					}
				}
			}
		}

		/* #endif */
		.swiperTxt {
			width: 100%;
			height: 100%;
			line-height: 52rpx;
			overflow: hidden;
		}

		.swiperTxt .text {
			width: 100%;

		}

		.swiperTxt .text .label {
			font-size: 20rpx;
			color: #ff4c48;
			width: 64rpx;
			height: 30rpx;
			border-radius: 40rpx;
			text-align: center;
			line-height: 28rpx;
			border: 2rpx solid #ff4947;
		}

		.swiperTxt .text .newsTitle {
			font-size: 24rpx;
		}

		.swiperTxt swiper {
			height: 100%;
		}
	}

	.navChecked {
		font-size: 32rpx !important;
		font-weight: 500;
	}

	.checkColor {
		@include main_color(theme);
	}
	.text-box{
		height: 100%;
	}
</style>
