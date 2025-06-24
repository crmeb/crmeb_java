<template>
	<!-- 辅助线 -->
	<view>
		<view class="lines" :style="[boxStyle]">
			<view class="item" :style="[lineStyle]"></view>
		</view>
	</view>

</template>

<script>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	export default {
		name: 'guide',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
		},
		computed: {
			//最外层盒子的样式
			boxStyle() {
				return {
					background: `linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					margin: this.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + this.dataConfig.lrConfig.val * 2 + 'rpx' +
						' ' + 0,
				}
			},
			//线条样式
			lineStyle() {
				return {
					borderBottomWidth: 2*this.dataConfig.heightConfig.val + 'rpx',
					borderBottomColor: this.dataConfig.lineColor.color[0].item,
					borderBottomStyle: this.dataConfig.lineStyle.list[this.dataConfig.lineStyle.tabVal].style
				}
			}
		},
	}
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