<template>
	<!-- 辅助线 -->
	<view>
		<view class="lines" :style="[boxStyle]">
			<view class="item" :style="[lineStyle]"></view>
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
		import { computed } from 'vue';

		const props = defineProps({
			dataConfig: {
				type: Object,
				default: () => {}
			},
		});

		//最外层盒子的样式
		const boxStyle = computed(() => {
			return {
				background: `linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
				margin: props.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + props.dataConfig.lrConfig.val * 2 + 'rpx' +
					' ' + 0,
			}
		});
		//线条样式
		const lineStyle = computed(() => {
			return {
				borderBottomWidth: 2*props.dataConfig.heightConfig.val + 'rpx',
				borderBottomColor: props.dataConfig.lineColor.color[0].item,
				borderBottomStyle: props.dataConfig.lineStyle.list[props.dataConfig.lineStyle.tabVal].style
			}
		});
	</script>

<style lang="scss" scoped>
	.lines {
		padding: 0 20rpx;

		.item {
			width: 100%;
			box-sizing: border-box;
			border-bottom-color: #666;
			border-bottom-width: 1px;
			border-bottom-style: dotted;
		}
	}
</style>