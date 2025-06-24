<template>
	<view class="goodsList">
		<view class="item acea-row row-between-wrapper" v-for="(item,index) in tempArr" :key='index' @click="goDetail(item)">
			<view class="pic">
				<image :src="item.image" mode=""></image>
				<view :style="{ backgroundImage: `url(${item.activityStyle})` }" class="border-picture"></view>
			</view>
			<view class="pictxt">
				<view class="text line2">{{item.storeName}}</view>
				<view class="bottom acea-row row-between-wrapper">
					<view class="money">
						<text class="sign">￥</text>{{item.price}} 
					</view>
					<view v-if="item.stock>0">
						<view>
							<!-- 多规格 -->
							<view class="bnt" @click.stop="goCartDuo(item)">
								选规格
								<view class="num" v-if="item.cartNum">{{item.cartNum}}</view>
							</view>
						</view>
					</view>
					<view class="bnt end" v-else>已售罄</view>
				</view>
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
			},
			goCartDan(item,index){
				this.$emit('gocartdan',item,index);
			},
			CartNumDes(index,item){
				this.$emit('ChangeCartNumDan', false,index,item);
			},
			CartNumAdd(index,item){
				this.$emit('ChangeCartNumDan', true,index,item);
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
			.pic{
				width: 140rpx;
				height: 140rpx;
				border-radius: 10rpx;
				position: relative;
				border-radius: 22rpx;
				image{
					width: 100%;
					height: 100%;
					border-radius: 22rpx;
				}
			}
			.pictxt{
				width: 372rpx;
				.text{
					font-size:26rpx;
					font-family:PingFang SC;
					font-weight:500;
					color: $crmeb-font-color;
				}
				.bottom{
					margin-top: 22rpx;
					.money{
						font-size: 34rpx;
						font-weight: 800;
						width: 212rpx;
						@include price_color(theme);
						.sign{
							font-size: 24rpx;
						}
					}
					.otPrice{
						font-size: 20rpx;
						font-family: PingFang SC;
						font-weight: 400;
						line-height: 24rpx;
						padding-left: 14rpx;
						color: #999999;
					}
					.cart{
						height: 46rpx;
						.pictrue{
							color: $crmeb-theme-color;
							font-size:46rpx;
							width: 46rpx;
							height: 46rpx;
							text-align: center;
							line-height: 46rpx;
							&.icon-jiahao{
								 color: $crmeb-theme-color;
							}
						}
						.num{
							font-size: 30rpx;
							color: $crmeb-font-color;
							font-weight: bold;
							width: 60rpx;
							text-align: center;
						}
					}
					.icon-gouwuche6{
						width: 46rpx;
						height: 46rpx;
						background-color: $crmeb-theme-color;
						border-radius: 50%;
						color: $crmeb-font-color-white;
						font-size: 30rpx;
					}
					.bnt{
						padding: 0 20rpx;
						height: 46rpx;
						line-height: 46rpx;
						@include main_bg_color(theme);
						border-radius:23rpx;
						font-size: 22rpx;
						color: $crmeb-font-color-white;
						position: relative;
						&.end{
							background:$crmeb-font-color-disable;
						}
						.num{
							min-width: 12rpx;
							@include main_color(theme);
							@include coupons_border_color(theme);
							background: #fff;
							border-radius: 15px;
							position: absolute;
							right: -13rpx;
							top: -11rpx;
							font-size: 16rpx;
							padding: 0 11rpx;
							height: 32rpx;
							line-height: 32rpx;
						}
					}
				}
			}
		}
	}
</style>
