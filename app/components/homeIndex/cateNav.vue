<template>
	<!-- 商品分类 -->
	<view>
		<!-- #ifdef MP || APP-PLUS -->
		<!-- <view style="visibility: hidden;" :style="{ height: navHeight + 'rpx' }"></view> -->
		<!-- #endif -->
		<view class="navTabBox" :class="{isFixed:isFixed}" :style="[boxStyle]">
			<view class="longTab">
				<scroll-view scroll-x="true" style="white-space: nowrap; display: flex;" scroll-with-animation
					:scroll-left="tabLeft" show-scrollbar="true">
					<view class="longItem"
						:style="'color:' + (index == tabClick ? checkColor : fontColor)+';--color:'+checkColor"
						:data-index="index" :class="index===tabClick?'click':''" v-for="(item,index) in tabList"
						:key="index" :id="'id'+index" @click="longClick(index,item)">{{ item.title }}
					</view>
				</scroll-view>
			</view>
		</view>
		<view style="height: 70rpx"></view>
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
	export default {
		name: 'tabNav',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
			//是否固定
			isFixed: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				tabClick: 0, //导航栏被点击
				isLeft: 0, //导航栏下划线位置
				isWidth: 0, //每个导航栏占位
				mainWidth: 0,
				tabLeft: 0,
				isTop: 0,
				navHeight: 35,
				tabItem: null ,//tab选中的对象
				themeColor:this.$options.filters.filterTheme(app.globalData.theme)
			};
		},
		computed: {
			//外部盒子
			boxStyle() {
				return {
					borderRadius: this.dataConfig.bgStyle.val * 2 + 'rpx',
					background: `linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					 margin: 0 + ' ' + 2*this.dataConfig.lrConfig.val + 'rpx' + ' ' + 0 ,
					// padding: 2*this.dataConfig.upConfig.val + 'rpx' + ' ' + '20rpx' + ' ' + 2*this.dataConfig.downConfig.val + 'rpx' ,
				}
			},
			//标签文字颜色
			fontColor() {
				return this.dataConfig.fontColor.color[0].item
			},
			//选中颜色
			checkColor() {
				return this.dataConfig.themeStyleConfig.tabVal?this.dataConfig.checkColor.color[0].item:this.themeColor
			},
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
		},
		created() {
			let that = this
			// 获取设备宽度
			uni.getSystemInfo({
				success(e) {
					//that.mainWidth = e.windowWidth
					that.isWidth = (e.windowWidth) / 5
				}
			})
			setTimeout((e) => {
				let statusHeight = uni.getSystemInfoSync().statusBarHeight;
				const query = uni.createSelectorQuery().in(this);
				query.select('.navTabBox').boundingClientRect(data => {
					that.navHeight = (data.height + statusHeight) * 2;
				}).exec();
			}, 300)
			that.$nextTick(function() {
				uni.getSystemInfo({
					success: function(res) {
						that.windowHeight = res.windowHeight;
					}
				});
			})
			// #ifdef MP || APP-PLUS
			this.isTop = (uni.getSystemInfoSync().statusBarHeight + 43) + 'px'
			// #endif
			// #ifdef H5
			this.isTop = 0
			// #endif
		},
		watch: {
			tabClick: {
				handler(newValue, oldValue) {
					if (this.tabItem) this.$emit('changeTab', newValue, this.tabItem);
				},
				immediate: true
			}
		},
		methods: {
			// 导航栏点击
			longClick(index, item) {
				this.tabItem = item;
				this.tabClick = index; //设置导航点击了哪一个
				this.$nextTick(() => {
					let id = 'id' + index;
					this.tabLeft = (index - 2) * this.isWidth //设置下划线位置
					//this.$emit('changeTab', index,item);
				})
			},
		}
	}
</script>

<style lang="scss" scoped>
	.navTabBox {
		width: 100%;
		height: 70rpx;
		color: rgba(255, 255, 255, 1);
		position: fixed;
		z-index: 99;
		padding: 0 20rpx;
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
			.longItem {
				height: 70rpx;
				display: inline-block;
				line-height: 70rpx;
				text-align: center;
				font-size: 28rpx;
				color: #333333;
				white-space: nowrap;
				overflow: hidden;
				text-overflow: ellipsis;
				margin-right: 30rpx;
				&.click {
					font-weight: bold;
					font-size: 30rpx;
					position: relative;

					&::after {
						content: '';
						width: 40rpx;
						height: 4rpx;
						background: var(--color);
						// background-color: #E93323;
						position: absolute;
						bottom: 0;
						left: 50%;
						transform: translateX(-50%);
					}
				}
			}
		}
	}
</style>