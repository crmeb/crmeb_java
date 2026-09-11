<template>
	<!-- 标题 -->
	<view class="title-box acea-row row-between row-middle" :style="[...boxStyle]" @click="goPage">
		<view class="acea-row row-middle" >
			<view :style="[...titleStyle]">{{ titleTxt }}</view>
			<view class="ml6" :style="[...titleFuStyle]">{{ titleFuTxt }}</view>
		</view>
		<view v-if="!selectShow" :style="[...titleRightStyle]">{{ titleRightTxt }}<text :style="[...titleRightStyle]"  class="iconfont icon-xiangyou"></text></view>
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
		import util from '@/utils/util.js';

		const props = defineProps({
			dataConfig: {
				type: Object,
				default: () => {}
			},
		});

		//最外层盒子的样式
		const boxStyle = computed(() => {
			return [{
					'border-radius': 2*props.dataConfig.bgTopStyle.val +
						'rpx' +
						' ' +
						2*props.dataConfig.bgTopStyle.val +
						'rpx' +
						' ' +
						2*props.dataConfig.bgDownStyle.val +
						'rpx' +
						' ' +
						2*props.dataConfig.bgDownStyle.val +
						'rpx',
				},
				{
					'background-image': selectStyle.value ?
						`linear-gradient(to right,${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})` :
						`url(${bgImgUrl.value})`,
				},
				{
					margin: 2*props.dataConfig.mbConfig.val + 'rpx' + ' ' + 2*props.dataConfig.lrConfig.val + 'rpx' + ' ' +
						0
				},
				{
					padding: 2*props.dataConfig.upConfig.val + 'rpx' + ' ' + '20rpx' + ' ' + 2*props.dataConfig.downConfig
						.val + 'rpx'
				},
			];
		});
		const titleStyle = computed(() => {
			return [{
					'font-weight': props.dataConfig.textStyle.list[props.dataConfig.textStyle.tabVal].style,
				},
				{
					'font-style': props.dataConfig.textStyle.list[props.dataConfig.textStyle.tabVal].style
				},
				{
					fontSize: 2*props.dataConfig.fontSize.val + 'rpx',
				},
				{
					color: props.dataConfig.fontColor.color[0].item
				},
			];
		});
		const titleFuStyle = computed(() => {
			return [{
					fontSize: 2*props.dataConfig.fontFuSize.val + 'rpx',
				},
				{
					color: props.dataConfig.fontFuColor.color[0].item
				},
			];
		});
		const titleRightStyle = computed(() => {
			return [{
					fontSize: 2*props.dataConfig.fontRightSize.val + 'rpx',
				},
				{
					color: props.dataConfig.fontRightColor.color[0].item
				},
			];
		});

		const configObj = ref(null);
		const titleTxt = ref('');
		const titleFuTxt = ref('');
		const titleRightTxt = ref('');
		const link = ref('');
		const txtPosition = ref('');
		const txtStyle = ref('');
		const fontSize = ref(0);
		const mTOP = ref(0);
		const titleColor = ref('');
		const themeColor = ref('');
		const prConfig = ref(0);
		const bgStyle = ref(0);
		const pageData = ref({});
		const selectShow = ref('');
		const selectStyle = ref('');
		const bgImgUrl = ref('');

		// created
		setConfig()

		function setConfig(data) {
			configObj.value = data;
			titleTxt.value = props.dataConfig.titleConfig.val;
			titleFuTxt.value = props.dataConfig.titleFuConfig.val;
			titleRightTxt.value = props.dataConfig.titleRightConfig.val;
			link.value = props.dataConfig.linkConfig.val;
			bgImgUrl.value = props.dataConfig.bgImg.url;
			selectShow.value = props.dataConfig.selectShow.tabVal;
			selectStyle.value = props.dataConfig.selectStyle.tabVal;
		}
		function goPage() {
			util.navigateTo(props.dataConfig.linkConfig.val)
		}
	</script>

<style scoped lang="scss">
	.title-box{
		background-repeat: no-repeat;
		object-fit: contain;
	}
	.title {
		font-size: 32rpx;
		color: #000;
		text-align: center;

		&.left {
			text-align: left;
		}

		&.right {
			text-align: right;
		}

		&.blod {
			font-weight: bold;
		}

		&.italics {
			font-style: italic;
		}
	}
	.ml6 {
	  margin-left: 12rpx;
	}
</style>