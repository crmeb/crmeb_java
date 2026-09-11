<template>
	<!-- 图片魔方 -->
	<view class="pictureCube skeleton-rect"  :style="[boxStyle]" v-if="picList.length&&(imageH>0||style==3)">
		<view class="grid_box"  :style="[gridColumns]" v-if="[0,1,2,4,5].includes(style)">
			<image class="center" v-for="(item,index) in picList" :key="index" @click="goDetail(item)" :src="item.image"
				:mode="item.radioVal === '0' ? 'scaleToFill' : item.radioVal === '1' ? 'aspectFit' : 'aspectFill'"
				:style="[imageStyle]"></image>
		</view>
		<view class="advertItem04" v-if="style==3" :style="[widthStyle]">
			<view class="item" @click="goDetail(picList[0])">
				<image class="img-left center" :src="picList[0].image" :style="[radiusStyle]"
					:mode="picList[0].radioVal === '0' ? 'scaleToFill' : picList[0].radioVal === '1' ? 'aspectFit' : 'aspectFill'">
				</image>
			</view>
			<view class="item item-right" :style="[gapStyle]">
				<view class="pic" @click="goDetail(picList[1])">
					<image class="img-right center" :src="picList[1].image" :style="[radiusStyle]"
						:mode="picList[1].radioVal === '0' ? 'scaleToFill' : picList[1].radioVal === '1' ? 'aspectFit' : 'aspectFill'">
					</image>
				</view>
				<view class="pic" @click="goDetail(picList[2])">
					<image class="img-right center" :src="picList[2].image" :style="[radiusStyle]"
						:mode="picList[2].radioVal === '0' ? 'scaleToFill' : picList[2].radioVal === '1' ? 'aspectFit' : 'aspectFill'">
					</image>
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
	import { ref, computed, onMounted, nextTick } from 'vue';
	import util from '@/utils/util.js';

	const props = defineProps({
		dataConfig: {
			type: Object,
			default: () => {}
		},
	});

	// data
	const picList = ref(props.dataConfig.picStyle.picList);
	const style = ref(props.dataConfig.tabConfig.tabVal);
	const prConfig = ref(props.dataConfig.lrConfig.val);
	const widthC = ref('');
	const imageH = ref(0);

	// computed
	//最外层盒子的样式
	const boxStyle = computed(() => {
		return {
			borderRadius: props.dataConfig.bgStyle.val * 2 + 'rpx',
			background: `linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
			margin: props.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + props.dataConfig.lrConfig.val * 2 + 'rpx' +
				' ' + 0,
			padding: props.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + '0' + ' ' + props.dataConfig.downConfig.val *
				2 + 'rpx'
		}
	});
	const imageStyle = computed(() => {
		return {
			height: imageH.value + 'rpx',
			'border-radius': props.dataConfig.contantStyle.val ? props.dataConfig.contantStyle.val + 'px' :
				'0'
		}
	});
	const widthStyle = computed(() => {
		return {width :750-props.dataConfig.lrConfig.val * 4 + 'rpx',gap: 2 * props.dataConfig.spaceConfig.val + 'rpx'}
	});
	const radiusStyle = computed(() => {
		return {'border-radius': props.dataConfig.contantStyle.val ? props.dataConfig.contantStyle.val + 'px' :
				'0'}
	});
	const gapStyle = computed(() => {
		return{gap: 2 * props.dataConfig.spaceConfig.val + 'rpx',}
	});
	//图片魔方排版
	const gridColumns = computed(() => {
		if ([1, 5].includes(props.dataConfig.tabConfig.tabVal)) {
			return {
				gridTemplateColumns: 'repeat(2, 1fr)',
				gap: 2 * props.dataConfig.spaceConfig.val + 'rpx',
			}
		} else if (props.dataConfig.tabConfig.tabVal == 0) {
			return {
				gridTemplateColumns: 'repeat(1, 1fr)',
				gap: 2 * props.dataConfig.spaceConfig.val + 'rpx',
			}
		} else if (props.dataConfig.tabConfig.tabVal == 2) {
			return {
				gridTemplateColumns: 'repeat(3, 1fr)',
				gap: 2 * props.dataConfig.spaceConfig.val + 'rpx',
			}
		} else if (props.dataConfig.tabConfig.tabVal == 4) {
			return {
				gridTemplateColumns: 'repeat(4, 1fr)',
				gap: 2 * props.dataConfig.spaceConfig.val + 'rpx',
			}
		}
	});

	onMounted(() => {
		if (picList.value.length) {
			nextTick((e) => {
				// 宽度
				if (style.value == 0) {
					widthC.value = 750
				} else if (style.value == 1) {
					widthC.value = 375
				} else if (style.value == 2) {
					widthC.value = 250
				} else if (style.value == 4) {
					widthC.value = 187.5
				}
				//高度计算
				if (style.value == 5) {
					imageH.value = 187.5;
				} else {
					let maxHeight = 0
					picList.value.forEach((val, index) => {
						let height = val.height * ((widthC.value - prConfig.value *
								2) / val
							.width)
						if (height > maxHeight) {
							maxHeight = height
						}
					})
					imageH.value = maxHeight;
				}
			})
		}
	});

	function goDetail(item) {
		util.navigateTo(item.link);
	}
</script>

<style lang="scss">
	.grid_box {
		display: grid;
		grid-template-rows: auto;

		image {
			width: 100%;
		}
	}

	.pictureCube {
		uni-image {
			width: 100%;
			max-width: 100%;
			height: 100%;
		}

		.advertItem04 {
			display: grid;
			grid-template-columns: repeat(2, 1fr);
			.item-right{
				display: grid;
				grid-template-columns: repeat(1, 1fr);
				height: 375rpx;
			}
			.item{
				height: 375rpx;
			}
			.img-left{
				width: 100%;
				height: 100%;
			}
			.img-right{
				width: 100%;
				height: 100%;
			}
		}
	}
	.center {
		justify-content: center;
		align-items: center;
	}
</style>