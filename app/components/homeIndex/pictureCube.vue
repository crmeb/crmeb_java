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
		name: 'pictureCube',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
		},
		data() {
			return {
				picList: this.dataConfig.picStyle.picList,
				style: this.dataConfig.tabConfig.tabVal,
				prConfig: this.dataConfig.lrConfig.val,
				widthC: '',
				imageH: 0,
			};
		},
		computed: {
			//最外层盒子的样式
			boxStyle() {
				return {
					borderRadius: this.dataConfig.bgStyle.val * 2 + 'rpx',
					background: `linear-gradient(${this.dataConfig.bgColor.color[0].item}, ${this.dataConfig.bgColor.color[1].item})`,
					margin: this.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + this.dataConfig.lrConfig.val * 2 + 'rpx' +
						' ' + 0,
					padding: this.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + '0' + ' ' + this.dataConfig.downConfig.val *
						2 + 'rpx'
				}
			},
			imageStyle() {
				return {
					height: this.imageH + 'rpx',
					'border-radius': this.dataConfig.contantStyle.val ? this.dataConfig.contantStyle.val + 'px' :
						'0'
				}
			},
			widthStyle(){
				return {width :750-this.dataConfig.lrConfig.val * 4 + 'rpx',gap: 2 * this.dataConfig.spaceConfig.val + 'rpx'}
			},
			radiusStyle(){
				return {'border-radius': this.dataConfig.contantStyle.val ? this.dataConfig.contantStyle.val + 'px' :
						'0'}
			},
			gapStyle(){
				return{gap: 2 * this.dataConfig.spaceConfig.val + 'rpx',}
			},
			//图片魔方排版
			gridColumns() {
				if ([1, 5].includes(this.dataConfig.tabConfig.tabVal)) {
					return {
						gridTemplateColumns: 'repeat(2, 1fr)',
						gap: 2 * this.dataConfig.spaceConfig.val + 'rpx',
					}
				} else if (this.dataConfig.tabConfig.tabVal == 0) {
					return {
						gridTemplateColumns: 'repeat(1, 1fr)',
						gap: 2 * this.dataConfig.spaceConfig.val + 'rpx',
					}
				} else if (this.dataConfig.tabConfig.tabVal == 2) {
					return {
						gridTemplateColumns: 'repeat(3, 1fr)',
						gap: 2 * this.dataConfig.spaceConfig.val + 'rpx',
					}
				} else if (this.dataConfig.tabConfig.tabVal == 4) {
					return {
						gridTemplateColumns: 'repeat(4, 1fr)',
						gap: 2 * this.dataConfig.spaceConfig.val + 'rpx',
					}
				}
			},
		},
		mounted() {
			if (this.picList.length) {
				let that = this;
				this.$nextTick((e) => {
					// 宽度
					if (this.style == 0) {
						this.widthC = 750
					} else if (this.style == 1) {
						this.widthC = 375
					} else if (this.style == 2) {
						this.widthC = 250
					} else if (this.style == 4) {
						this.widthC = 187.5
					}
					//高度计算
					if (this.style == 5) {
						that.$set(that, 'imageH', 187.5);
					} else {
						let maxHeight = 0
						this.picList.forEach((val, index) => {
							let height = val.height * ((that.widthC - that.prConfig *
									2) / val
								.width)
							if (height > maxHeight) {
								maxHeight = height
							}
						})
						that.$set(that, 'imageH', maxHeight);
					}
				})
			}
		},
		methods: {
			goDetail(item) {
				this.$util.navigateTo(item.link);
			}
		}
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