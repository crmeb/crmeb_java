<template>
	<!-- 热区+图片魔方 -->
	<view class="mobile-page" v-if="dataConfig" :style="[boxStyle]">
		<view class="advert">
			<!-- 单图/热区布局 -->
			<template v-if="style === 0">
				<view class="advertItem01 acea-row hotspot" v-for="(item, index) in picList" :key="index">
					<image :src="item.image" mode="widthFix" v-if="item.image" :style="[contentStyle]"
						@click="dataConfig.checkoutConfig.hotspot.length?'':goDetail(item)"></image>
					<view v-for="(item, index) in dataConfig.checkoutConfig.hotspot" :key="index" :style="{
						top: `${Number(spotIndex*item.starY)}rpx`,
						left: `${Number(spotIndex*item.starX)}rpx`,
						width: `${Number(spotIndex*item.areaWidth)}rpx`,
						height: `${Number(spotIndex*item.areaHeight)}rpx`,
					}" class="area" @click="goDetail(item)"></view>
				</view>
			</template>
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
	import { ref, computed, getCurrentInstance } from 'vue';
	const { proxy } = getCurrentInstance();

	const props = defineProps({
		dataConfig: {
			type: Object,
			default: () => {}
		},
	});

	const picList = ref(props.dataConfig.picStyle.picList);
	const style = ref(props.dataConfig.tabConfig.tabVal);
	const prConfig = ref(props.dataConfig.lrConfig.val);
	const igConfig = ref(props.dataConfig.igConfig.val);

	//最外层盒子的样式
	const boxStyle = computed(() => {
		return {
			borderRadius: props.dataConfig.bgStyle.val * 2 + 'rpx',
			backgroundImage: `linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
			margin: props.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + props.dataConfig.lrConfig.val * 2 + 'rpx' +
				' ' + 0,
			padding: props.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + '0' + ' ' + props.dataConfig.downConfig.val *
				2 + 'rpx'
		}
	});
	const contentStyle = computed(() => {
		return{
			borderRadius: props.dataConfig.contentStyle.val * 2 + 'rpx',
		}
	});
	//伸缩系数
	const spotIndex = computed(() => {
		return  (750-4*props.dataConfig.lrConfig.val)/456
	});
	// 两张图片 图片间距样式
	const twoImgStyle = computed(() => {
		return {
			width: (750 - 2 * igConfig.value - 4 * prConfig.value) / 2 + 'rpx',
			height: (750 - 2 * igConfig.value - 4 * prConfig.value) / 2 + 'rpx',
		}
	});
	// 第一种 三张图片间距样式
	const thrOneImgStyle = computed(() => {
		return {
			width: (750 - 4 * igConfig.value - 4 * prConfig.value) / 3 + 'rpx',
			height: (750 - 4 * igConfig.value - 4 * prConfig.value) / 3 + 'rpx',
		}
	});
	// 第一种四张图片布局
	const forOneImgStyle = computed(() => {
		return {
			width: (750 - 6 * igConfig.value - 4 * prConfig.value) / 4 + 'rpx',
		}
	});
	// 第二种四张图片布局
	const forTwoImgStyle = computed(() => {
		return {
			width: (750 - 2 * igConfig.value - 4 * prConfig.value) / 2 + 'rpx',
			height: (750 - 2 * igConfig.value - 4 * prConfig.value) / 2 + 'rpx',
		}
	});
	const imgBoxStyle = computed(() => {
		return {
			display: igConfig.value ? 'flex' : '',
			'justify-content': igConfig.value ? 'space-between' : ''
		}
	});

	//替换安全域名
	function setDomain(url) {
		url = url ? url.toString() : '';
		//本地调试打开,生产请注销
		if (url.indexOf("https://") > -1) return url;
		else return url.replace('http://', 'https://');
	}
	function goDetail(item) {
		proxy.$util.navigateTo(item.link);
	}
</script>

<style lang="scss" scoped>
	.mobile-page {
		.advert {
			.advertItem01 {
				width: 100%;
				height: 100%;

				.empty-box {
					width: 100%;
					height: auto;
				}

				image {
					width: 100%;
					height: 100%;
				}
			}

			.advertItem02 {
				width: 100%;

				.item {
					width: 50%;
					height: auto;

					image {
						width: 100%;
						height: 100%;
					}

					.empty-box {
						width: 100%;
						height: auto;
					}
				}
			}

			.advertItem03 {
				.item {
					width: 33.3333%;
					height: auto;

					.empty-box {
						width: 100%;
						height: auto;
					}
				}
			}

			.advertItem04 {
				.item {
					width: 50%;
					height: auto;

					.empty-box {
						width: 100%;
						height: 100%;
					}

					image {
						width: 100%;
						height: 100%;
					}

					.pic {
						width: 100%;
						height: auto;
					}
				}
			}

			.advertItem05 {
				.item {
					width: 25%;

					.empty-box {
						width: 100%;
						height: auto;
					}
				}
			}

			.advertItem06 {
				.item {
					width: 50%;

					image {
						width: 100%;
						height: 100%;
					}

					.empty-box {
						width: 100%;
						height: 100%;
					}
				}
			}
		}
	}

	.hotspot {
		position: relative;

		.area {
			position: absolute;
		}
	}
</style>