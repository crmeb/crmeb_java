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
		import { filterTheme } from '@/filters';
		const { proxy } = getCurrentInstance();
		let app = getApp();

		const props = defineProps({
			dataConfig: {
				type: Object,
				default: () => {}
			},
			//是否固定
			isFixed: {
				type: Boolean,
				default: false
			}
		});

		const emit = defineEmits(['changeTab']);

		const tabClick = ref(0); //导航栏被点击
		const isLeft = ref(0); //导航栏下划线位置
		const isWidth = ref(0); //每个导航栏占位
		const mainWidth = ref(0);
		const tabLeft = ref(0);
		const isTop = ref(0);
		const navHeight = ref(35);
		const tabItem = ref(null); //tab选中的对象
		const themeColor = ref(filterTheme(app.globalData.theme));

		//外部盒子
		const boxStyle = computed(() => {
			return {
				borderRadius: props.dataConfig.bgStyle.val * 2 + 'rpx',
				background: `linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
				 margin: 0 + ' ' + 2*props.dataConfig.lrConfig.val + 'rpx' + ' ' + 0 ,
			}
		});
		//标签文字颜色
		const fontColor = computed(() => {
			return props.dataConfig.fontColor.color[0].item
		});
		//选中颜色
		const checkColor = computed(() => {
			return props.dataConfig.themeStyleConfig.tabVal?props.dataConfig.checkColor.color[0].item:themeColor.value
		});
		const tabList = computed(() => {
			//type=0微页面，1分类，2首页
			let tabList = props.dataConfig.listConfig.list;
			tabList.unshift({
				title: '首页',
				type: 2,
				val: 0
			})
			return tabList
		});

		watch(tabClick, (newValue, oldValue) => {
			if (tabItem.value) emit('changeTab', newValue, tabItem.value);
		}, { immediate: true });

		// 导航栏点击
		function longClick(index, item) {
			tabItem.value = item;
			tabClick.value = index; //设置导航点击了哪一个
			nextTick(() => {
				let id = 'id' + index;
				tabLeft.value = (index - 2) * isWidth.value //设置下划线位置
			})
		}

		// created
		// 获取设备宽度
		uni.getSystemInfo({
			success(e) {
				isWidth.value = (e.windowWidth) / 5
			}
		})
		setTimeout((e) => {
			let statusHeight = uni.getSystemInfoSync().statusBarHeight;
			const query = uni.createSelectorQuery().in(proxy);
			query.select('.navTabBox').boundingClientRect(data => {
				navHeight.value = (data.height + statusHeight) * 2;
			}).exec();
		}, 300)
		let windowHeight = 0;
		nextTick(function() {
			uni.getSystemInfo({
				success: function(res) {
					windowHeight = res.windowHeight;
				}
			});
		})
		// #ifdef MP || APP-PLUS
		isTop.value = (uni.getSystemInfoSync().statusBarHeight + 43) + 'px'
		// #endif
		// #ifdef H5
		isTop.value = 0
		// #endif
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