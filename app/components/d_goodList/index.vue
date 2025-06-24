<template>
	<view class="goodsList">
		<view class="item" v-for="(item,index) in tempArr" :key='index' @click="goDetail(item)">
			<view class="pictrue">
				<image :src="item.recommend_image" mode="aspectFill" v-if="item.recommend_image"></image>
				<image :src="item.image" mode="aspectFill" v-else></image>
			</view>
			<view class="text line2">{{item.storeName}}</view>
			<view class="bottom acea-row row-between-wrapper">
				<view class="sales acea-row row-middle">
					<view class="money" :class="item.price.length>8?'lengthStr':''"><text>￥</text>{{item.price}} <text class="item_sales">已售 {{item.sales}}</text> </view>
					<!-- <view></view> -->
				</view>
				<view v-if="item.stock>0">
				    <view class="bnt" v-if="item.activity && (item.activity.type === '1' || item.activity.type === '2' || item.activity.type === '3')">立即购买</view>
					<view v-else>
						<view class="bnt" @click.stop="goCartDuo(item)">
							加入购物车
							<view class="num" v-if="item.cartNum">{{item.cartNum}}</view>
						</view>
					</view>
				</view>
				<view class="end" v-else>已售罄</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: 'd_goodList',
		props: {
			dataConfig: {
				type: Object,
				default: () => {}
			},
			tempArr:{
				type: Array,
				default:[]
			},
			isLogin:{
				type: Boolean,
				default:false
			}
		},
		data() {
			return {
			};
		},
		created() {},
		mounted() {},
		methods: {
			goDetail(item){
				this.$emit('detail',item);
			},
			goCartDuo(item){
				this.$emit('gocartduo',item);
			}
		}
	};
</script>

<style lang="scss">
	.goodsList{
		padding: 0 30rpx;
		.item{
			width: 100%;
			box-sizing: border-box;
			margin-bottom: 63rpx;
			.pictrue{
				width: 100%;
				height: 216rpx;
				border-radius: 16rpx;
				position: relative;
				image{
					width: 100%;
					height: 100%;
					border-radius: 16rpx;
				}
			}
			.text{
				font-size:30rpx;
				font-family:PingFang SC;
				font-weight:bold;
				color: #282828;
				margin: 20rpx 0;
			}
			.bottom{
				.sales{
					font-size: 22rpx;
					color: #8E8E8E;
					.money{
						font-size: 42rpx;
						font-weight: bold;
						margin-right: 18rpx;
						@include price_color(theme);
						.item_sales{
							font-size: 24rpx;
							font-family: PingFang SC;
							font-weight: 400;
							padding-left: 17rpx;
							color: #8e8e8e;
						}
						text{
							font-size: 28rpx;
						}
					}
				}
				.cart{
					height: 56rpx;
					.pictrue{
						color: #E93323;
						font-size:46rpx;
						width: 50rpx;
						height: 50rpx;
						text-align: center;
						line-height: 50rpx;
						&.icon-jiahao{
							 background:linear-gradient(140deg, #FA6514 0%, #E93323 100%);
							-webkit-background-clip:text;
							-webkit-text-fill-color:transparent;
						}
					}
					.num{
						font-size: 30rpx;
						color: #282828;
						font-weight: bold;
						width: 80rpx;
						text-align: center;
					}
				}
				.bnt{
					padding: 0 30rpx;
					height: 56rpx;
					line-height: 56rpx;
					@include main_bg_color(theme);
					border-radius:42rpx;
					font-size: 26rpx;
					color: #fff;
					position: relative;
					.num{
						@include main_color(theme);
						@include coupons_border_color(theme);
						background: #fff;
						min-width: 12rpx;
						border-radius: 15px;
						position: absolute;
						right: -14rpx;
						top: -15rpx;
						font-size: 22rpx;
						padding: 0 10rpx;
						height: 34rpx;
						line-height: 34rpx;
					}
				}
				.end{
					padding: 0 30rpx;
					height: 56rpx;
					line-height: 56rpx;
					border-radius:42rpx;
					font-size: 26rpx;
					color: #fff;
					position: relative;
					background:rgba(203,203,203,1);
				}
			}
		}
	}
	.lengthStr{
		font-size: 36rpx !important;
	}
</style>