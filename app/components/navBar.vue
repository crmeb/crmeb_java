<template>
	<view>
		<view class="cart_nav" :class="isBackgroundColor?'cart_nav_color':''"  :style='"height:"+navH+"rpx;"'>
			<view class='navbarCon acea-row'>
				<!-- #ifdef MP -->
				<view class="select_nav flex justify-center align-center" id="home" :style="{ top: homeTop + 'rpx' }">
					<text class="iconfont icon-fanhui2 px-20" @tap="returns"></text>
					<text v-if="productType!=='video'" class="iconfont icon-gengduo5 px-20" @tap.stop="showNav"></text>
					<text v-if="productType!=='video'" class="nav_line"></text>
				</view>
				<!-- #endif -->
				<!-- #ifdef H5 -->
				<view id="home" class="home acea-row row-center-wrapper iconfont icon-shouye4 h5_back"
					:style="{ top: homeTop + 'rpx' }" @tap="goToHome">
				</view>
				<!-- #endif -->
				<!-- #ifndef APP-PLUS -->
				<view class="nav_title" :style="{ top: homeTop + 'rpx' }">{{navTitle}}</view>
				<!-- #endif -->
				<!-- #ifdef H5 -->
				<view class="right_select" :style="{ top: homeTop + 'rpx' }" @tap="showNav">
					<text class="iconfont icon-gengduo2"></text>
				</view>
				<!-- #endif -->
			</view>
		</view>
		<view class="dialog_nav" :style='"top:"+navH+"rpx;"' v-show="currentPage">
			<view class="dialog_nav_item" v-for="(item,index) in selectNavList" :key="index" @click="linkPage(item.url)">
				<text class="iconfont" :class="item.icon"></text>
				<text class="pl-20">{{item.name}}</text>
			</view>
		</view>
	</view>
</template>

<script>
	import animationType from '@/utils/animationType.js'
	import {
		mapGetters
	} from "vuex";
	let app = getApp();
	export default {
		data() {
			return {
				homeTop: 20,
				navH:"",
				currentPage:false,
				selectNavList:[
					{name:'首页',icon:'icon-shouye8',url:'/pages/index/index'},
					{name:'搜索',icon:'icon-sousuo6',url:'/pages/goods/goods_search/index'},
					{name:'我的收藏',icon:'icon-shoucang3',url:'/pages/users/user_goods_collection/index'},
					{name:'个人中心',icon:'icon-gerenzhongxin1',url:'/pages/user/index'},
				]
			}
		},
		props:{
			navTitle:{
				type: String,
				default:''
			},
			isBackgroundColor: {
				type: Boolean,
				default: true
			},
		},
		computed: {
			...mapGetters(['productType'])
		},
		created(){
		       // #ifdef MP
			   // 获取导航高度；
			   uni.getSystemInfo({
			   	success: function(res) {
			   		app.globalData.navHeight = res.statusBarHeight * (750 / res.windowWidth) + 91;
			   	}
			   });
		       this.navH = app.globalData.navHeight;
		       // #endif
		       // #ifndef MP
		       this.navH = 96;
		       // #endif
			   this.$emit('getNavH', this.navH)
		},
		onReady() {
			this.$nextTick(function() {
				// #ifdef MP
				const menuButton = uni.getMenuButtonBoundingClientRect();
				const query = uni.createSelectorQuery().in(this);
				query
					.select('#home')
					.boundingClientRect(data => {
						if(this.productType!=='video'){
							this.homeTop = menuButton.top * 2 + menuButton.height - data.height;
						}else{
							this.homeTop = 18;
						}
						
					})
					.exec();
				// #endif
			});
		},
		methods: {
			//返回首页
			goToHome(){
				uni.switchTab({
					url:'/pages/index/index'
				})
			},
			returns: function() {
				uni.navigateBack();
			},
			showNav(){
				this.currentPage = !this.currentPage;
			},
			//下拉导航页面跳转
			linkPage(url){
				if(url == '/pages/index/index' || url == '/pages/user/index'){
					uni.switchTab({
						url
					})
				}else{
					uni.navigateTo({
						animationType: animationType.type,
						animationDuration: animationType.duration,
						url
					})
				}
				this.currentPage = false
			},
			touchStart(){
				this.currentPage = false;
			}
		}
	}
</script>

<style scoped lang="scss">
	.pl-20{
		padding-left: 20rpx;
	}
	.cart_nav{
		position: fixed;
		top: 0;
		left: 0;
		z-index: 99;
		width: 100%;
	}
	.cart_nav_color{
		@include main_bg_color(theme);
	}
	.navbarCon {
		position: absolute;
		bottom: 0;
		height: 100rpx;
		width: 100%;
	}
	.h5_back {
		color: #fff;
		position: fixed;
		left:20rpx;
		font-size: 32rpx;
		text-align: center;
		line-height: 58rpx;
	}
	.select_nav{
		border: 1px solid rgba(0, 0, 0, 0.07);
		width: 170rpx !important;
		height: 60rpx !important;
		border-radius: 33rpx;
		background: rgba(255, 255, 255, 0.6);
		color: #000;
		position: fixed;
		font-size: 18px;
		line-height: 58rpx;
		z-index: 1000;
		left: 14rpx;
	}
	.px-20{
		padding: 0 20rpx 0;
	}
	.nav_line{
		content: '';
		display: inline-block;
		width: 1px;
		height: 34rpx;
		background: #ddd;
		position: absolute;
		left: 0;
		right: 0;
		margin: auto;
	}
	.container_detail{
		/* #ifdef MP */
		margin-top:32rpx;
		/* #endif */
	}
	.tab_nav{
		width: 100%;
		height: 48px;
		padding:0 30rpx 0;
	}
	.nav_title{
		width: 200rpx;
		height: 58rpx;
		line-height: 58rpx;
		color: #fff;
		font-size: 36rpx;
		position: fixed;
		text-align: center;
		left: 0;
		right: 0;
		margin: auto;
	}
	.right_select{
		position: fixed;
		right: 20rpx;
		color: #fff;
		text-align: center;
		line-height: 58rpx;
	}
	.select_nav{
		width: 170rpx !important;
		height: 60rpx !important;
		border-radius: 33rpx;
		background: rgba(255, 255, 255, 0.6);
		color: #000;
		position: fixed;
		font-size: 18px;
		line-height: 58rpx;
		z-index: 1000;
		left: 14rpx;
	}
	.px-20{
		padding: 0 20rpx 0;
	}
	.justify-center{
		justify-content: center;
	}
	.align-center {
		align-items: center;
	}
	.dialog_nav{
		position: fixed;
		/* #ifdef MP */
		left: 14rpx;
		/* #endif */
		/* #ifdef H5 || APP-PLUS*/
		right: 14rpx;
		/* #endif */
		width: 240rpx;
		background: #FFFFFF;
		box-shadow: 0px 0px 16rpx rgba(0, 0, 0, 0.08);
		z-index: 999;
		border-radius: 14rpx;
		&::before{
			content: '';
			width: 0;
			height: 0;
			position: absolute;
			/* #ifdef MP */
			left: 0;
			right: 0;
			margin:auto;
			/* #endif */
			/* #ifdef H5 || APP-PLUS */
			right: 8px;
			/* #endif */
			top:-9px;
			border-bottom: 10px solid #fff;
			border-left: 10px solid transparent;    /*transparent 表示透明*/
			border-right: 10px solid transparent;
		}
	}
	.dialog_nav_item{
		width: 100%;
		height: 84rpx;
		line-height: 84rpx;
		padding: 0 20rpx 0;
		box-sizing: border-box;
		border-bottom: #eee;
		font-size: 28rpx;
		color: #333;
		position: relative;
		.iconfont{
			font-size: 32rpx;
		}
		&::after{
			content: '';
			position: absolute;
			width:86px;
			height: 1px;
			background-color: #EEEEEE;
			bottom: 0;
			right: 0;
		}
	}
</style>