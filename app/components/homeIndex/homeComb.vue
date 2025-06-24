<template>
	<!-- 组合组件 -->
	<view class="page_count tui-skeleton" :data-theme="theme">
		<!--logo-->
		<view class="bg-img" :style="{'background-image': bgColor}">
			<img :src="bgColor" alt="">
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
					<navigator v-if="hotWords.length > 0" :url="'/pages/goods/goods_search/index?searchVal='+searchVal"
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
					<navigator v-else :style="[searchBoxStyle]" :class="logoConfig ? 'input' : 'uninput'"
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
						<navigator v-if="hotWords.length > 0"
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
						<navigator v-else :style="[searchBoxStyle]" :class="logoConfig ? 'input' : 'uninput'"
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

<script>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	let app = getApp();
	import {
		goPage
	} from '@/libs/iframe.js'
	import animationType from '@/utils/animationType.js'
	export default {
		name: 'homeComb',
		props: {
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
		},
		data() {
			return {
				myMainHeight: 0, //头部tab切换页和搜索按钮的高度和
				indicatorDots: false,
				circular: true,
				autoplay: false,
				duration: 500,
				searchH: 0,
				swiperTop: 0,
				statusBarHeight: app.globalData.statusBarHeight, //手机端头部显示时间位置的高度
				swiperCur: 0,
				showSkeleton: true,
				tabClick: 0, //导航栏被点击
				isLeft: 0, //导航栏下划线位置
				isWidth: 0, //每个导航栏占位
				mainWidth: app.globalData.mainWidth,
				theme: app.globalData.theme,
				tabLeft: 0,
				bgColor: '',
				isTop: 0,
				navHeight: 0,
				isShow: false,
				marTop: 0,
				searchVal: '',
				intervalBanner: 2500,
				themeColor:this.$options.filters.filterTheme(app.globalData.theme),
				searchTop:0,
				searchRight:0,
				searchHeight:0,
				statusWidth:0,
			};
		},
		watch: {
			banner: {
				handler(val) {
					this.bgColor = val[0].img;
				},
				immediate: true
			},
		},
		computed: {
			//分类是否展示，0展示，1不展示
			tabShowConfig() {
				return this.dataConfig.tabShowConfig.tabVal == 0;
			},
			//搜索提示语
			placeWords() {
				return this.dataConfig.placeWords.val;
			},
			//轮播切换时间
			interval() {
				return this.dataConfig.titleConfig.val * 1000
			},
			//指示器类型，0圆，1直，2无
			docType() {
				return this.dataConfig.docConfig.tabVal
			},
			//轮播图样式
			swiperType() {
				return this.dataConfig.swiperStyleConfig.tabVal
			},
			//搜索热词列表
			hotWords() {
				return this.dataConfig.hotWords.list
			},
			//分类选中颜色
			lineColor() {
				return {
					backgroundColor: this.dataConfig.checkColor.color[0].item
				}
			},
			maskBgStyle() {
				return {
					background: this.bgInfo.isBgColor=='1' ?
						`linear-gradient(180deg, rgba(245, 245, 245, 0) 0%, ${this.bgInfo.colorPicker} 100%)` :
						`linear-gradient(180deg, rgba(245, 245, 245, 0) 0%, #f5f5f5 100%)`,
				}
			},
			//判断logo图是否展示
			logoConfig() {
				return this.dataConfig.logoConfig.url && this.dataConfig.searConfig.tabVal === 1
			},
			//logo图
			logoUrl() {
				if (this.isScrolled && this.dataConfig.logoFixConfig.url) {
					return this.dataConfig.logoFixConfig.url
				} else {
					return this.dataConfig.logoConfig.url
				}
			},
			//标签文字颜色
			textColor() {
				return this.dataConfig.fontColor.color[0].item;
			},
			//分类列表
			tabList() {
				//type=0微页面，1分类，2首页
				let tabList = this.dataConfig.listConfig.list;
				tabList.unshift({
					title: '首页',
					type: 2,
					val: 0
				})
				return tabList
			},
			//轮播列表
			banner() {
				return this.dataConfig.swiperConfig.list
			},
			//搜索框样式
			searchBoxStyle() {
				return {
					borderRadius: this.dataConfig.contentStyle.val ? this.dataConfig.contentStyle.val + 'px' : '0',
					backgroundColor: this.dataConfig.borderColor.color[0].item,
					color: this.dataConfig.textColor.color[0].item,
					textAlign: this.dataConfig.textPosition.list[this.dataConfig.textPosition.tabVal].style,
					// #ifdef MP
					height:this.searchHeight + 'px',
					flex:!this.isSmallPage?1:'',
					marginRight:!this.isSmallPage?(this.statusWidth + this.searchRight+'px'):'',
					// #endif
				}
			},
			//指示器样式
			dotStyle() {
				return {
					padding: '0 40rpx',
					justifyContent: this.dataConfig.txtStyle.tabVal === 1 ? 'center' : this.dataConfig.txtStyle
						.tabVal === 2 ? 'flex-end' : 'flex-start'
				}
			},
			//轮播图圆角
			contentStyleBanner() {
				return {
					'borderRadius': this.dataConfig.contentStyleBanner.val ? this.dataConfig.contentStyleBanner
						.val + 'px' : '0'
				}
			},
		},
		created() {
			// #ifdef MP || APP-PLUS
			this.isTop = (this.isSmallPage ? 0 : this.statusBarHeight) + 48 + 'px' //分类的top值
			// this.tabShowConfig，true有分类,false无分类
			if (!this.tabShowConfig) {
				this.myMainHeight = (this.isSmallPage ? 0 : this.statusBarHeight) + 40 + 10; //头部tab切换页和搜索按钮的高度和，10是下边距
			} else {
				this.myMainHeight = (this.isSmallPage ? 0 : this.statusBarHeight) + 40 + 42; //头部tab切换页和搜索按钮的高度和
			}
			// #endif
			
			// #ifdef MP
			const res = uni.getMenuButtonBoundingClientRect()
			const statusHeight = res.top //胶囊距离顶部
			const statusRight = res.right //胶囊右边界坐标
			const jnHeight = res.height //胶囊高度
			this.statusWidth= res.width
			this.searchTop=statusHeight-this.statusBarHeight
			this.searchHeight=jnHeight
			//搜索框宽度计算
			uni.getSystemInfo({
				success:res=>{
					this.searchRight=res.windowWidth-statusRight
				}
			})
			// #endif

			// #ifdef H5
			this.isTop = 0
			this.myMainHeight = 'auto';

			// #endif

			this.isWidth = (this.mainWidth - 65) / 4;
			setTimeout((e) => {
				const query = uni.createSelectorQuery().in(this);
				query.select('.header').boundingClientRect(res => {
					if (res) this.marTop = res.height //头部的高度
				}).exec();

				//展示与不展示分类的距离值判断
				if (!this.tabShowConfig) {
					// 不展示分类
					query.select('.swiperBg').boundingClientRect(res => {
						// #ifdef H5
						this.swiperTop = this.navHeight + this.marTop + this.statusBarHeight +
							4; //轮播图的top值
						//#endif
						// #ifndef H5
						if (this.isSmallPage) {
							this.swiperTop = this.statusBarHeight; //轮播图的top值
						} else {
							this.swiperTop = this.statusBarHeight + 48; //轮播图的top值
						}
						//#endif
					}).exec();
				} else {
					//展示分类
					query.select('.navTabBox').boundingClientRect(data => {
						this.navHeight = data.height //元素navHeight的高度
						// #ifdef H5
						this.swiperTop = this.navHeight + this.marTop + this.statusBarHeight +
							4; //轮播图的top值
						//#endif
						// #ifndef H5
						if (this.isSmallPage) {
							this.swiperTop = 85; //轮播图的top值
						} else {
							this.swiperTop = this.statusBarHeight + 85; //轮播图的top值
						}
						//#endif
					}).exec();
				}

			}, 200)
		},
		methods: {
			//轮播图跳转
			menusTap(url) {
				this.$util.navigateTo(url);
			},
			swiperChange(e) {
				let {
					current,
					source
				} = e.detail;
				if (source === 'autoplay' || source === 'touch') {
					this.swiperCur = e.detail.current;
					this.bgColor = this.banner[e.detail.current]['img']
				}
			},
			textChange(e) {
				let {
					current,
					source
				} = e.detail;
				if (source === 'autoplay' || source === 'touch') {
					this.searchVal = this.hotWords[e.detail.current]['val']
				}
			},
			// 导航栏点击
			longClick(index, item) {
				this.tabClick = index; //设置导航点击了哪一个
				this.$nextTick(() => {
					let id = 'id' + index;
					this.tabLeft = (index - 2) * this.isWidth //设置下划线位置
					this.$emit('changeTab', index, item);
				})
			},
			parentEmit(id, index) {
				this.$emit('changeTab', id, index);
			}
		}
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
					/deep/.uni-swiper-slide-frame {
						transform: translate(0, 0) !important;
					}
				}
			}

			image {
				transform: scale(0.96);
				transition: all 0.6s ease;
			}

			/deep/ swiper-item.active {
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

			img {
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