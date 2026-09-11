<template>
	<!-- 辅助空白-->
	<view>
		<view :style="[boxStyle]"></view>
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
		}
	});

	//最外层盒子的样式
	const boxStyle = computed(() => {
		return {
			height: props.dataConfig.heightConfig.val + 'px',
			background: `linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
			margin: props.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + props.dataConfig.lrConfig.val * 2 + 'rpx' +
				' ' + 0,
		}
	});
</script>

<style lang="scss" scoped>
	.blankPage {
		.bankCon {
			width: 100%;
		}
	}
</style>