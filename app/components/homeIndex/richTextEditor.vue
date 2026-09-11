<template>
	<!-- 富文本 -->
	<view v-if="description" :style="[boxStyle]">
		<view class="rich_text">
			<!-- #ifdef MP || APP-PLUS -->
			<mp-html :content="description" :tag-style="tagStyle" selectable="true"  show-img-menu="true"/>  
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<view v-html="description"></view>
			<!-- #endif -->
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
	import mpHtml from "@/uni_modules/mp-html/components/mp-html/mp-html.vue";

	const props = defineProps({
		dataConfig: {
			type: Object,
			default: () => {}
		},
	});

	const tagStyle = ref({
		img: 'width:100%;display:block;',
		table: 'width:100%',
		video: 'width:100%'
	});

	//最外层盒子的样式
	const boxStyle = computed(() => {
		return {
			borderRadius: props.dataConfig.bgStyle.val * 2 + 'rpx',
			background: `linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
			margin: props.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + props.dataConfig.lrConfig.val * 2 + 'rpx' +
				' ' + 0,
		}
	});
	//富文本内容
	const description = computed(() => {
		return props.dataConfig.richText.val.replace(/<video/g, "<video style='width:100%'").replace(/\<img/gi, '<img style="max-width:100%;height:auto" ')
			.replace(/style="text-wrap: wrap;"/gi, '');
	});
</script>

<style lang="scss" scoped>
	.rich_text {
		padding: 10px;
		width: 100%;
		overflow: hidden;
	}
</style>