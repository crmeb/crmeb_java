<template>
	<view class='promotionGood'>
		<block v-for="(item,index) in benefit.slice(0,6)" :key="index">
			<view class='item' @tap="goDetail(item)" hover-class="none">
				<view class='pictrue'>
					<image :src='item.image'></image>
				</view>
				<view class='money'>
					<text class="rmb">¥</text>
					<text class="price">{{item.price}}</text>
					<text class="ot-price">{{item.otPrice}}</text>
				</view>
			</view>
		</block>
	</view>
</template>
<script>
	import animationType from '@/utils/animationType.js'
	export default {
		props: {
			benefit: {
				type: Array,
				default: function() {
					return [];
				}
			}
		},
		data() {
			return {

			};
		},
		methods: {
			goDetail(item) {
				uni.navigateTo({
					animationType: animationType.type,					animationDuration: animationType.duration,
					url: `/pages/goods/goods_details/index?id=${item.id}`
				})
			}
		}
	}
</script>

<style scoped lang='scss'>
	.promotionGood {
		padding: 0 30rpx;
		display: flex;
		flex-wrap: wrap;
		padding: 15rpx 24rpx;

		.item {
			width: 215rpx;
			display: flex;
			flex-direction: column;
			justify-content: center;
			padding: 15rpx 9rpx;

			.pictrue {
				height: 198rpx;
				border-radius: 12rpx;

				image {
					width: 100%;
					height: 100%;
					border-radius: 12rpx;
				}

			}

			.money {
				font-size: 30rpx;
				margin-top: 10rpx;
				overflow:hidden; 
				text-overflow:ellipsis; 
				white-space:nowrap; 
				.rmb {
					font-weight: bold;
					@include price_color(theme);
					font-size: 20rpx;
				}
				.price{
					@include price_color(theme);
					font-weight: bold;
				}
				.ot-price {
					color: #999;
					text-decoration: line-through;
					font-size: 20rpx;
					margin-left: 4rpx;
				}
			}
		}
	}
</style>