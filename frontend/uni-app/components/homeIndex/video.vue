<template>
	<view>
		<view class="diy_video acea-row row-center-wrapper" :style="[boxStyle]">
			<video :style="[contantRadius]" :src="link" :show-mute-btn="pageGesture" :poster="cover" controls :autoplay="false" loop
				objectFit="cover"></video>
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

	const props = defineProps({
		dataConfig: {
			type: Object, 
			default: () => {}
		},
	});

	const pageGesture = ref(true);
	const onloadCode = ref('');

	//视频封面
	const cover = computed(() => {
		return props.dataConfig.cover.url
	});
	//视频地址
	const link = computed(() => {
		if (props.dataConfig.tabConfig.tabVal === 0) {
			return props.dataConfig.uploadVideo.url
		} else {
			return props.dataConfig.link.val
		}

	});
	//最外层盒子的样式
	const boxStyle = computed(() => {
		return {
			borderRadius: props.dataConfig.bgStyle.val * 2 + 'rpx',
			background: `linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
			margin: props.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + props.dataConfig.lrConfig.val * 2 + 'rpx' +
				' ' + 0,
			padding: props.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + 0 + ' ' + props.dataConfig.downConfig.val *
				2 + 'rpx'
		}
	});
	const contantRadius = computed(() => {
	      return { 'border-radius': props.dataConfig.contantStyle.val ? props.dataConfig.contantStyle.val + 'px' : '0' };
	    });

	// created
	// #ifdef APP
	onloadCode.value =
		`this.contentWindow.document.body.innerHTML = '<video style="width: 100%;height: 100%" objectFit="cover" controls="controls"  loop show-mute-btn="${pageGesture.value}" poster="${cover.value}" src="${link.value}"></video>';`
	// #endif
</script>
<style lang="scss" scoped>
	.diy_video {
		iframe {
			border: none;

			body {
				margin: 0;
			}
		}

		video {
			width: 100%;
			height: 340rpx;
			border-radius: 14rpx;
		}
	}
</style>