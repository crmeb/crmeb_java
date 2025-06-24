<template>
	<view :style="{ backgroundImage: `url(${productInfo.activityStyle})` }" class='nav acea-row row-between-wrapper '>
		<view class='money skeleton-rect'>
			<view class="price-box">
				<view class="price">
					<span class="price-icon">￥</span>{{productInfo.price}}
				</view>
				<view class="vip-price" v-if="productInfo.vipPrice">
					<image :src="urlDomain+'crmebimage/perset/staticImg/vip_badge.png'" class="vip_icon"></image>
					<view
						class='vip_money skeleton-rect'>￥{{productInfo.vipPrice}}</view>
				</view>
			</view>
		</view>
		<view class='acea-row row-middle skeleton-rect'>
		</view>
	</view>
</template>

<script>
	export default {
		props: {
			productInfo: {
				type: Object,
				default: () => {},
			}
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
			}

		}
	}
</script>

<style lang="scss" scoped>
	.nav {
		background-repeat: no-repeat;
		background-size: 100% 100%;
		width: 100%;
		height: 100rpx;
		padding: 0 24px;
		box-sizing: border-box;

		.money {
			font-size: 14px;
			color: #fff;
		}

		.num {
			font-size: 24px;
		}

		.y-money {
			font-size: 26rpx;
			margin-left: 10rpx;
			text-decoration: line-through;
		}
	}
	.price-box{
		display: flex;
		font-size: 48rpx;
		font-weight: 700;
		@include price_color(theme);
		.price-icon{
			font-size: 28rpx;
		}
		.vip-price{
			display: flex;
			align-items: center;
			padding-left: 20rpx;
			margin-top: 8rpx;
			.vip_icon {
				width: 44rpx;
				height: 28rpx;
				vertical-align: middle;
			}
			.vip_money {
				background: #FFE7B9;
				border-radius: 4px;
				font-size: 22rpx;
				color: #333;
				text-align: center;
				padding: 0 4rpx;
				box-sizing: border-box;
				margin-left: -7rpx;
				height: 14px;
			}
		}
	}
</style>