<template>
	<!-- 底部导航 -->
	<view :data-theme="theme">
		<view v-if="obj.isCustom==1">
			<view class="page-footer" id="target">
				<view class="foot-item" v-for="(item,index) in obj.bottomNavigationList" :key="index"
					@click="goRouter(item)">
					<block v-if="item.link.split('?')[0] == activeRouter">
						<image :src="item.checked"></image>
						<view class="txt">{{item.name}}</view>
					</block>
					<block v-else>
						<image :src="item.unchecked"></image>
						<view class="unchecked">{{item.name}}</view>
					</block>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	let app = getApp();
	import {
		getBottomNavigationApi
	} from '@/api/api.js'
	export default {
		props: {
			arr: {
				type: Array,
				default: () => []
			},
		},
		data() {
			return {
				theme: app.globalData.theme,
				obj: {},
				activeRouter: ''
			}
		},
		created() {
			let routes = getCurrentPages(); //获取当前打开过的页面路由数组
			let curRoute = routes[routes.length - 1].route //获取当前页面路由
			this.activeRouter = '/' + curRoute;
		},
		mounted() {
			this.getInit()
		},
		methods: {
			getInit() {
				getBottomNavigationApi().then((res) => {
					this.obj = res.data
					this.$store.commit('BottomNavigationIsCustom', this.obj.isCustom == 1 ? true : false);
					if (this.obj.isCustom == 1) {
						uni.hideTabBar()
					} else {
						uni.showTabBar()
					}
				})
			},
			goRouter(item) {
				var pages = getCurrentPages();
				var page = (pages[pages.length - 1]).$page.fullPath;
				if (item.link == page) return
				if (['/pages/index/index', '/pages/goods_cate/goods_cate',
						'/pages/order_addcart/order_addcart', '/pages/user/index'
					].indexOf(item.link) > -1) {
					uni.switchTab({
						url: item.link,
						fail(err) {
							uni.redirectTo({
								url: item.link
							})
						}
					})
				} else {
					uni.navigateTo({
						url: item.link
					})
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.unchecked {
		color: #333;
		font-size: 24rpx;
	}

	.page-footer {
		position: fixed;
		bottom: 0;
		z-index: 666;
		display: flex;
		align-items: center;
		justify-content: space-around;
		width: 100%;
		height: calc(98rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(98rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
		box-sizing: border-box;
		border-top: solid 1rpx #F3F3F3;
		background-color: #fff;
		box-shadow: 0px 0px 17rpx 1rpx rgba(206, 206, 206, 0.32);
		padding-bottom: constant(safe-area-inset-bottom); ///兼容 IOS<11.2/
		padding-bottom: env(safe-area-inset-bottom); ///兼容 IOS>11.2/

		.foot-item {
			display: flex;
			width: max-content;
			align-items: center;
			justify-content: center;
			flex-direction: column;
			position: relative;

			.count-num {
				position: absolute;
				display: flex;
				justify-content: center;
				align-items: center;
				width: 40rpx;
				height: 40rpx;
				top: 0rpx;
				right: -15rpx;
				color: #fff;
				font-size: 20rpx;
				background-color: #FD502F;
				border-radius: 50%;
				padding: 4rpx;
			}
		}

		.foot-item image {
			height: 50rpx;
			width: 50rpx;
			text-align: center;
			margin: 0 auto;
		}

		.txtchecked {
			font-size: 24rpx;
		}

		.foot-item .txt {
			font-size: 24rpx;
			@include main-color(theme);
		}
	}
</style>