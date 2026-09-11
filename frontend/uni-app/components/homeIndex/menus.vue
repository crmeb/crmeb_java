<template>
	<!-- 导航组 -->
	<view v-show="menus.length" :style="[boxStyle]">
		<view v-if="isMany === 1">
			<view class="swiper">
				<swiper :interval="interval" :duration="duration" :style="'height:'+ navHigh +'px;'"
					@change='bannerfun'>
					<block>
						<swiper-item v-for="(item,indexw) in menuList" :key="indexw">
							<view class="menu acea-row" :id="'nav' + indexw" :style="[gridColumns]">
								<view :style="[titleColor]" class="item" :class="number===1?'four':number===2?'five':''"
									v-for="(itemn,indexn) in item.list" :key="indexn"
									@click="menusTap(itemn.info[1].value)">
									<view class="pictrue skeleton-radius">
										<easy-loadimage :image-src="itemn.img" :radius="dataConfig.contentStyle.val">
										</easy-loadimage>
									</view>
									<view class="menu-txt">{{ itemn.info[0].value }}</view>
								</view>
							</view>
						</swiper-item>
					</block>
				</swiper>
			</view>
			<view class="dot acea-row row-center-wrapper" v-if="docConfig<2 && menuList.length>1">
				<view class="dot-item line_dot-item" :style="active==index?'background:'+ dotColor:''"
					v-for="(item,index) in menuList"></view>
			</view>
		</view>
		<view class="nav oneNav" v-else>
			<scroll-view scroll-x="true" style="white-space: nowrap; display: flex" show-scrollbar="false">
				<block v-for="(item, index) in menus" :key="index">
					<view class="item" v-show="item.status" :style="[titleColor]" @click="menusTap(item.info[1].value)">
						<view class="pictrue skeleton-radius">
							<easy-loadimage :image-src="item.img" :radius="dataConfig.contentStyle.val">
							</easy-loadimage>
						</view>
						<view class="menu-txt">{{ item.info[0].value }}</view>
					</view>
				</block>
			</scroll-view>
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
	import { ref, computed, onMounted, nextTick, getCurrentInstance } from 'vue';
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	import util from '@/utils/util.js';

	const { proxy } = getCurrentInstance();

	const props = defineProps({
		dataConfig: {
			type: Object,
			default: () => {}
		}
	});

	// data
	const interval = ref(3000);
	const duration = ref(500);
	const menus = ref(props.dataConfig.menuConfig.list || []);
	const rowsNum = ref(props.dataConfig.rowsNum.tabVal);
	const number = ref(props.dataConfig.number.tabVal);
	const isMany = ref(props.dataConfig.tabConfig.tabVal);
	const docConfig = ref(0);
	const dotColor = ref('#E93323');
	const menuList = ref([]);
	const active = ref(0);
	const navHigh = ref(0);

	// computed
	//最外层盒子的样式
	const boxStyle = computed(() => {
		return {
			borderRadius: props.dataConfig.bgStyle.val * 2 + 'rpx',
			background: `linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
			margin: props.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + props.dataConfig.lrConfig.val * 2 + 'rpx' +' ' + 0,
			padding: props.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + 0 + ' ' + props.dataConfig.downConfig.val *2 + 'rpx'
		}
	});
	//分几行展示，一行展示多少个
	const gridColumns = computed(() => {
		if (props.dataConfig.number.tabVal == 0) {
			return {
				gridRowGap: props.dataConfig.contentConfig.val * 2 + 'rpx',
				gridTemplateColumns: 'repeat(3, 1fr)'
			}
		} else if (props.dataConfig.number.tabVal == 1) {
			return {
				gridRowGap: props.dataConfig.contentConfig.val * 2 + 'rpx',
				gridTemplateColumns: 'repeat(4, 1fr)'
			}
		} else {
			return {
				gridRowGap: props.dataConfig.contentConfig.val * 2 + 'rpx',
				gridTemplateColumns: 'repeat(5, 1fr)'
			}
		}
	});
	//标题颜色
	const titleColor = computed(() => {
		return {
			'color': props.dataConfig.titleColor.color[0].item,
		}
	});
	//内容圆角
	const menuStyle = computed(() => {
		return {
			'border-radius': props.dataConfig.contentStyle.val + 'px'
		}
	});

	onMounted(() => {
		if (rowsNum.value === 0) {
			if (number.value === 0) {
				pageNum(6)
			} else if (number.value === 1) {
				pageNum(8)
			} else {
				pageNum(10)
			}
		} else if (rowsNum.value === 1) {
			if (number.value === 0) {
				pageNum(9)
			} else if (number.value === 1) {
				pageNum(12)
			} else {
				pageNum(15)
			}
		} else {
			if (number.value === 0) {
				pageNum(12)
			} else if (number.value === 1) {
				pageNum(16)
			} else {
				pageNum(20)
			}
		}
		nextTick(() => {
			if (menuList.value.length && isMany.value===1) {
				// #ifdef H5
				menuHeight()
				// #endif
				// #ifndef H5
				setTimeout(() => {
					menuHeight()
				}, 150)
				// #endif
			}
		})
	});

	function bannerfun(e) {
		active.value = e.detail.current;
	}
	function menuHeight() {
		const query = uni.createSelectorQuery().in(proxy);
		query.select('#nav0').boundingClientRect(data => {
			navHigh.value = data.height;
		}).exec();
	}
	function pageNum(num) {
		let list = menus.value.filter(item=>item.status);
		let count = Math.ceil(list.length / num);
		let goodArray = new Array();
		for (let i = 0; i < count; i++) {
			let arr = list.slice(i * num, i * num + num);
			if (arr.length)
				goodArray.push({
					list: arr
				});
		}
		menuList.value = goodArray;
	}
	function menusTap(url) {
		util.navigateTo(url);
	}
</script>

<style lang="scss" scoped>
	.dot {
		width: 100%;
		margin-top: 30rpx;

		.instruct {
			width: 50rpx;
			height: 36rpx;
			line-height: 36rpx;
			background-color: rgba(0, 0, 0, 0.8);
			color: #fff;
			border-radius: 16rpx;
			font-size: 24rpx;
			text-align: center;
		}

		.dot-item {
			width: 10rpx;
			height: 10rpx;
			background: rgba(0, 0, 0, .4);
			border-radius: 50%;
			margin: 0 4px;

			&.line_dot-item {
				width: 20rpx;
				height: 5rpx;
				border-radius: 3rpx;
			}
		}
	}

	.nav {
		.item {
			width: 160rpx;
			text-align: center;
			font-size: 24rpx;
			display: inline-block;

			.pictrue {
				width: 90rpx;
				height: 90rpx;
				margin: 0 auto;

				image {
					width: 100%;
					height: 100%;
					border-radius: 50%;
				}

				&.on {
					image {
						border-radius: 0;
					}
				}
			}

			.menu-txt {
				margin-top: 15rpx;
			}
		}
	}

	.swiper {
		z-index: 20;
		position: relative;
		overflow: hidden;

		.menu {
			display: grid;
			grid-template-rows: auto;
			width: 100%;
			padding: 0 20rpx;

			.item {
				.pictrue {
					width: 90rpx;
					height: 90rpx;
					margin: 0 auto;

					image {
						width: 100%;
						height: 100%;
						border-radius: 50%;
					}

					&.on {
						image {
							border-radius: 0;
						}
					}
				}

				.menu-txt {
					text-align: center;
					font-size: 12px;
					margin-top: 14rpx;
				}
			}
		}

		swiper,
		.swiper-item {
			width: 100%;
			display: block;
		}
	}
</style>
