<template>
	<!-- 秒杀 -->
	<view v-if="spikeList.length">
		<view class="seckill" :style="[boxStyle]" >
			<view class="bg_box" :style="[boxBgStyle]"></view>
			<view class="title acea-row row-between-wrapper">
				<view class="acea-row row-middle">
					<view class="pictrue skeleton-rect">
						<image :src="logoUrl"></image>
					</view>
					<view class="lines"></view>
					<view class="point skeleton-rect" :style="[titleColor]">{{titleText}}</view>
				</view>
				<view class="more acea-row row-center-wrapper skeleton-rect" @click="toSeckillList()">
					GO<text class="iconfont icon-you"></text>
				</view>
			</view>
			<view class="conter" v-if="listStyle == 0">
				<scroll-view scroll-x="true" style="white-space: nowrap; vertical-align: middle;"
					show-scrollbar="false">
					<view class="itemCon" :style="[{'margin-right':itemStyle}]" v-for="(item, index) in spikeList"
						:key="index" @click="toSeckillDetail(item.id)">
						<view class="item">
							<view class="pictrue skeleton-rect">
								<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val">
								</easy-loadimage>
							</view>
							<view v-show="nameShow" :style="[nameColor]" class="name line1 skeleton-rect">{{item.title}}</view>
							<view v-show="priceShow" :style="[priceColor]" class="x_money semiBold line1 skeleton-rect">¥<text
									class="num semiBold">{{item.price}}</text>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
			<view class="conter_y" :style="[{'grid-gap':itemStyle}]" v-if="listStyle == 1">
				<view class="item" v-for="(item, index) in spikeList" :key="index" @click="toSeckillDetail(item.id)">
					<view class="pictrue">
						<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val">
						</easy-loadimage>
					</view>
					<view class="text-info acea-row row-column row-between">
						<view v-show="nameShow">
							<view :style="[nameColor]" class="title line2">{{ item.title }}</view>
						</view>
						<view v-show="priceShow" :style="[priceColor]" class="price semiBold">
							<view>
								<text class="semiBold">￥</text>
								<text class="semiBold">{{ item.price }}</text>
							</view>
						</view>
					</view>
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
		import { ref, computed } from 'vue';
		import {
			getSeckillIndexApi
		} from '@/api/activity.js';
		import easyLoadimage from '@/components/base/easy-loadimage.vue';
		import util from '@/utils/util.js';
		import Cache from '@/utils/cache.js';
		import { filterTheme } from '@/filters';
		let app = getApp();

		const props = defineProps({
			dataConfig: {
				type: Object,
				default: () => {}
			},
		});

		const urlDomain = ref(Cache.get("imgHost"));
		const spikeList = ref([]); // 秒杀
		const datatime = ref(0);
		const status = ref(0);
		const themeColor = ref(filterTheme(app.globalData.theme));

		//秒杀样式
		const listStyle = computed(() => {
			return props.dataConfig.tabConfig.tabVal
		});
		//最外层盒子的背景图片
		const boxBgStyle = computed(() => {
			return {
				borderRadius: props.dataConfig.bgStyle.val * 2 + 'rpx' + ' ' + props.dataConfig.bgStyle.val * 2 + 'rpx' +
					' ' + 0 + ' ' + 0,
				backgroundImage: `url(${urlDomain.value}/crmebimage/presets/seckill_bg_pic.png),linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`
			}
		});
		//最外层盒子的样式
		const boxStyle = computed(() => {
			return {
				borderRadius: props.dataConfig.bgStyle.val * 2 + 'rpx',
				margin: props.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + props.dataConfig.lrConfig.val * 2 + 'rpx' +
					' ' + 0,
				padding: props.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + '24rpx' + ' ' + props.dataConfig.downConfig
					.val * 2 + 'rpx'
			}
		});
		//图片圆角
		const itemStyle = computed(() => {
			return props.dataConfig.contentConfig.val * 2 + 'rpx'
		});
		//标题图片
		const logoUrl = computed(() => {
			return props.dataConfig.logoConfig.url
		});
		//标题
		const titleText = computed(() => {
			return props.dataConfig.titleConfig.val
		});
		//标题颜色
		const titleColor = computed(() => {
			return {
				color: props.dataConfig.titleColor.color[0].item
			}
		});
		//名称颜色
		const nameColor = computed(() => {
			return {
				color: props.dataConfig.nameColor.color[0].item
			};
		});
		//价格颜色
		const priceColor = computed(() => {
			return {
				color: props.dataConfig.themeStyleConfig.tabVal?props.dataConfig.priceColor.color[0].item:themeColor.value
			};
		});
		//商品名称
		const nameShow = computed(() => {
			if (props.dataConfig.typeConfig.activeValue.indexOf(0) !== -1) {
				return true;
			} else {
				return false;
			}
		});
		//商品价格
		const priceShow = computed(() => {
			if (props.dataConfig.typeConfig.activeValue.indexOf(1) !== -1) {
				return true;
			} else {
				return false;
			}
		});

		// created
		getSeckillIndex();

		function getSeckillIndex() {
			getSeckillIndexApi().then(({
				data
			}) => {
				spikeList.value = [];
				spikeList.value = data ? data.productList : [];
			})
		}
		function toSeckillList() {
			util.navigateTo(props.dataConfig.linkConfig.val)
		}
		function toSeckillDetail(id){
			uni.navigateTo({
				url:`/pages/activity/goods_seckill_details/index?id=${id}`
			})
		}
	</script>

<style lang="scss" scoped>
	.seckill {
		width: auto;
		background: #fff;
		border-radius: 14rpx;
		box-sizing: border-box;
		position: relative;

		.bg_box {
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 256rpx;
			background-repeat: no-repeat;
			background-size: cover;
			border-radius: 14rpx 14rpx 0 0;
		}

		.title {
			.pictrue {
				width: 124rpx;
				height: 32rpx;
				z-index: 9;

				image {
					width: 100%;
					height: 100%;
				}
			}

			.lines {
				width: 1rpx;
				height: 24rpx;
				background-color: #fff;
				opacity: 0.6;
				margin-left: 16rpx;
			}

			.point {
				font-size: 26rpx;
				color: #fff;
				margin-left: 21rpx;
				margin-right: 4rpx;
				z-index: 9;
			}

			.styleAll {
				width: 35rpx;
				height: 35rpx;
				background-color: #2F2F2F;
				border-radius: 6rpx;
				color: #fff;
				text-align: center;
			}

			.more {
				width: 86rpx;
				height: 40rpx;
				background: linear-gradient(142deg, #FFE9CE 0%, #FFD6A7 100%);
				opacity: 1;
				border-radius: 18px;
				font-size: 22rpx;
				color: #FE960F;
				padding-left: 8rpx;
				font-weight: 800;
				z-index: 9;

				.iconfont {
					font-size: 21rpx;
				}
			}
		}

		.conter {
			border-radius: 12px;
			margin-top: 30rpx;

			.itemCon {
				display: inline-block;
				width: 186rpx;

				.item {
					width: 100%;

					.pictrue {
						width: 100%;
						height: 186rpx;
						overflow: hidden;

						image {
							width: 100%;
							height: 100%;
							border-radius: 6rpx;
						}
					}

					.name {
						font-size: 26rpx;
						color: #333;
						margin-top: 8rpx;
					}

					.y_money {
						font-size: 24rpx;
						color: #999999;
						text-decoration: line-through;
					}

					.x_money {
						font-size: 28rpx;
						height: 100%;
						font-weight: bold;
						margin-top: 4rpx;

						.num {
							font-size: 28rpx;
						}
					}

					.money {
						margin-top: 14rpx;
					}
				}
			}
		}
	}

	.conter_y {
		display: grid;
		grid-template-columns: repeat(1, 1fr);
		grid-template-rows: auto;
		width: 100%;
		margin-top: 30rpx;

		.item {
			display: flex;
			width: 100%;
			z-index: 9;

			.pictrue {
				width: 108px;
				height: 108px;

				image {
					width: 100%;
					height: 100%;
				}
			}

			.text-info {
				z-index: 9;
				margin-left: 20rpx;
				flex: 1;

				.title {
					width: 100%;
					height: 80rpx;
					line-height: 40rpx;
					color: #333;
				}

				.old-price {
					font-weight: normal;
					font-size: 24rpx;
					color: #999;
				}

				.price {
					font-size: 36rpx;
					font-weight: 550;

					text {
						padding-bottom: 4rpx;
						font-size: 26rpx;
						font-weight: normal;
					}
				}
			}
		}
	}
	.semiBold{
		font-size: 36rpx !important;
	}
</style>