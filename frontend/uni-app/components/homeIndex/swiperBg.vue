<template>
	<!-- 轮播图 -->
	<view class="swiperBg" :style="[boxStyle]">
		<view class="swiper page_swiper" :class="docConfig" v-if="imgUrls.length">
			<swiper :autoplay="true" :circular="true" :interval="3000" :duration="500"
				:indicator-active-color="docColor" :current="swiperCur" :previous-margin="swiperType==0?'40rpx':''" :next-margin="swiperType==0?'40rpx':''"
				@change="swiperChange">
				<block v-for="(item,index) in imgUrls" :key="index">
					<swiper-item :class="{ active: index == swiperCur }">
						<view @click="goDetail(item)" class='slide-navigator acea-row row-between-wrapper tui-skeleton-rect'>
							<image :src="item.img" mode="aspectFill" :style="[imgStyle]" class="slide-image aa"></image>
						</view>
					</swiper-item>
				</block>
			</swiper>
			<view v-if="docType === 0" class="dots" :style="[dotStyle]">
				<block v-for="(item,index) in imgUrls" :key="index">
					<view class="dot-item"
						:style="{'background-color': swiperCur === index ? (dataConfig.themeStyleConfig.tabVal?dataConfig.docColor.color[0].item:themeColor)  : ''}">
					</view>
				</block>
			</view>
			<view v-if="docType === 1" class="dots" :style="[dotStyle]">
				<block v-for="(item,index) in imgUrls" :key="index">
					<view class="dot"
						:style="{'background-color': swiperCur === index ? (dataConfig.themeStyleConfig.tabVal?dataConfig.docColor.color[0].item:themeColor)  : ''}">
					</view>
				</block>
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
	import { ref, computed, watch, onMounted, nextTick } from 'vue';
	import {
		navigatoPage
	} from "@/utils/index.js"
	import { filterTheme } from '@/filters';
	import util from '@/utils/util.js';
	let app = getApp();

	const props = defineProps({
		dataConfig: {
			type: Object,
			default: () => {}
		},
		merId: {}
	});

	// data
	const indicatorDots = ref(false);
	const imgUrls = ref([]); //图片轮播数据
	const txtStyle = ref(props.dataConfig.txtStyle.type); //指示器位置
	const imageH = ref(310);
	const swiperCur = ref(0);
	const themeColor = ref(filterTheme(app.globalData.theme));

	watch(imageH, (nVal, oVal) => {
		imageH.value = nVal
	});

	// computed
	//指示器样式
	const dotStyle = computed(() => {
		return {
			padding: '0 50rpx',
			justifyContent: props.dataConfig.txtStyle.tabVal === 1 ? 'center' : props.dataConfig.txtStyle
				.tabVal === 2 ? 'flex-end' : 'flex-start'
		}
	});
	//指示器类型，0圆，1直，2无
	const docType = computed(() => {
		return props.dataConfig.docConfig.tabVal
	});
	//轮播图样式
	const swiperType = computed(() => {
		return props.dataConfig.swiperStyleConfig.tabVal
	});
	//最外层盒子的样式
	const boxStyle = computed(() => {
		return {
			borderRadius: props.dataConfig.bgStyle.val * 2 + 'rpx',
			background: `linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
			margin: props.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + props.dataConfig.lrConfig.val * 2 + 'rpx' +
				' ' + 0,
			padding: props.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + '20rpx' + ' ' + props.dataConfig.downConfig.val *
				2 + 'rpx'
		}
	});
	//指示器颜色
	const docColor = computed(() => {
		return props.dataConfig.docColor.color[0].item + '!important'
	});
	//指示器样式
	const docConfig = computed(() => {
		if (props.dataConfig.docConfig.tabVal == 1) {
			return 'square'
		} else if (props.dataConfig.docConfig.tabVal == 2) {
			return 'nodoc'
		} else {
			return 'circular'
		}
	});
	//内容圆角
	const imgStyle = computed(() => {
		return {
			"border-radius": props.dataConfig.contentStyle.val * 2 + 'rpx'
		}
	});

	// created
	imgUrls.value = props.dataConfig.swiperConfig.list

	onMounted(() => {
		nextTick(function() {
			uni.getImageInfo({
				src: setDomain(imgUrls.value[0].img),
				success: function(res) {
					imageH.value = res.height;
				},
				fail: function(error) {
					imageH.value = 310;
				}
			})
		})
	});

	//替换安全域名
	function setDomain(url) {
		url = url ? url.toString() : '';
		//本地调试打开,生产请注销
		if (url.indexOf("https://") > -1) return url;
		else return url.replace('http://', 'https://');
	}
	function swiperChange(e) {
		let {
			current,
			source
		} = e.detail;
		if (source === 'autoplay' || source === 'touch') {
			swiperCur.value = e.detail.current;
		}
	}
	function goDetail(url) {
		let path = url.info[1].value
		util.navigateTo(path);
	}
</script>

<style lang="scss" scoped>
	/*用来包裹所有的小圆点  */
	.dots {
		display: flex;
		flex-direction: row;
		position: absolute;
		bottom: 24rpx;
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
	.swiperBg {
		position: relative;
		background: #fff;
		z-index: 1;

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
			overflow-x: hidden;
			/* #ifdef MP */
			z-index: 20;

			/* #endif */
			/* 设置圆角 */
			&.fillet {

				.swiper-item,
				image,
				.acea-row.row-between-wrapper {
					border-radius: 10rpx;
				}
			}

			.swiper-item,
			image,
			.acea-row.row-between-wrapper {
				width: 100%;
				height: 100%;
				margin: 0 auto;
			}

			swiper {
				width: 100%;
				display: block;
			}

			image {
				transform: scale(0.93);
				transition: all 0.6s ease;
			}

			swiper-item.active {
				image {
					transform: scale(1);
				}
			}

			// 圆形指示点
			&.circular {
				::v-deep .uni-swiper-dot {
					width: 10rpx;
					height: 10rpx;
				}
			}

			// 方形指示点
			&.square {
				::v-deep .uni-swiper-dot {
					width: 20rpx;
					height: 5rpx;
					border-radius: 3rpx;
				}
			}

			&.nodoc {
				::v-deep .uni-swiper-dot {
					display: none;
				}
			}
		}
	}

	::v-deep .dot0 .uni-swiper-dots-horizontal {
		left: 10%;
	}

	::v-deep .dot1 .uni-swiper-dots-horizontal {
		left: 50%;
	}

	::v-deep .dot2 .uni-swiper-dots-horizontal {
		left: 90%;
	}

	.item-img image {
		display: block;
		width: 100%;
	}
</style>
