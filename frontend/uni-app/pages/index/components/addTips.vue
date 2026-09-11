<template>
	<!-- 小程序顶部提示 -->
	<view>
		<view class="tip_box" :class="{ anScale: isAm }" v-if="showTip"
			:style="{ top: isCustom ? boxTop + 'px' : '0px' }">
			<view class="arrow" :style="{ 'margin-right': arrowMargin + 'px', borderBottomColor: bgColor }"></view>
			<view class="container" :style="{'margin-right': cotainerMargin + 'px',backgroundColor: bgColor,borderRadius: borderR + 'px',}">
				<!-- 提示文字 -->
				<view class="tips" :style="{ color: fontObj.color, fontSize: fontObj.fontSize, fontWeight: fontObj.fontWeight }">
					{{ text }}</view>
				<view class="close" @tap="tipHidden">
					<text class="iconfont icon-cha3" v-if="closeColor"></text>
					<text class="iconfont icon-cha3" style="color:#fff;" v-else></text>
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
	import { ref } from 'vue'
	import { onReady } from '@dcloudio/uni-app'

	const props = defineProps({
		/* 是否是自定义头部 */
		isCustom: {
			type: Boolean,
			default: false,
		},
		/* 背景颜色 */
		bgColor: {
			type: String,
			default: "#ffffff",
		},
		/* 提示文字 */
		text: {
			type: String,
			default: "添加到我的小程序",
		},
		/* 提示文字样式 */
		fontObj: {
			type: Object,
			default: function() {
				return {
					color: "#202020",
					fontSize: "12px",
					fontWeight: "0",
				};
			},
		},
		/* 圆角大小  px*/
		borderR: {
			type: Number,
			default: 5,
		},
		/* 延时出现 */
		delay: {
			type: Number,
			default: 2000,
		},
		/* 关闭btn黑白两色 或者自行添加 */
		closeColor: {
			type: Boolean,
			default: true,
		},
		/* 动画效果 */
		isAm: {
			type: Boolean,
			default: true,
		},
	})

	const showTip = ref(false);
	const boxTop = ref(0);
	const arrowMargin = ref(0);
	const cotainerMargin = ref(0);
	const screenWidth = ref(0);

	function tipHidden() {
		uni.setStorageSync("my_tips_2020", "true");
		showTip.value = false;
	}

	function timeOut() {
		tipHidden();
		showTip.value = true;
	}

	function init() {
		if (uni.getStorageSync("my_tips_2020")) return;
		let rect = uni.getMenuButtonBoundingClientRect();
		let screenWidthVal = uni.getSystemInfoSync().screenWidth;
		boxTop.value = rect.bottom;
		arrowMargin.value = rect.width * 0.75 + 4;
		cotainerMargin.value = screenWidthVal - rect.right;
		timeOut();
	}

	onReady(() => {
		init();
	})
</script>

<style lang="scss" scoped>
	@keyframes anScale {
		from {
			-webkit-transform: scale3d(0.96, 0.96, 0.96);
			transform: scale3d(0.96, 0.96, 0.96);
		}

		50% {
			-webkit-transform: scale3d(1, 1, 1);
			transform: scale3d(1, 1, 1);
		}

		to {
			-webkit-transform: scale3d(0.96, 0.96, 0.96);
			transform: scale3d(0.96, 0.96, 0.96);
		}
	}

	.anScale {
		animation: anScale 1s linear infinite;
	}

	.tip_box {
		width: 70%;
		position: fixed;
		top: 0;
		right: 0;
		z-index: 100;
		display: flex;
		justify-content: flex-end;
		align-items: flex-end;
		flex-direction: column;

		.arrow {
			width: 0;
			height: 0;
			border: 10rpx solid;
			border-color: transparent;
		}

		.container {
			display: flex;
			align-items: center;
			justify-content: center;
			padding: 16rpx 24rpx;

			.tips {
				flex: 1;
				padding-right: 12rpx;
			}

			.close {
				height: 30rpx;
				width: 30rpx;
				font-size: 20rpx;
				line-height: 30rpx;
				color: #999;
				.closeImg {
					height: 100%;
					width: 100%;
				}
			}
		}
	}
</style>
