<template>
	<view :class="{borderShow:isBorader}">
		<view class="combination" v-if="combinationList.length">
			<view class="title acea-row row-between-wrapper">
				<view class="acea-row row-middle">
					<view class="sign">
						<image src="../../../static/images/sign02.png"></image>
					</view>
					<view class="name">拼团惠<text>享超值开团价</text></view>
				</view>
				<navigator url="/pages/activity/goods_combination/index" hover-class="none" class="more acea-row row-center-wrapper">超值精选<text class="iconfont icon-xiangyou"></text></navigator>
			</view>
			<view class="conter acea-row">
				<scroll-view scroll-x="true" style="white-space: nowrap; vertical-align: middle;" show-scrollbar="false">
					<view class="itemCon" v-for="(item, index) in combinationList" :key="index" @click="goDetail(item)">
						<view class="item">
							<view class="pictrue">
								<image :src="item.image"></image>
							</view>
							<view class="text lines1">
								<text class="money">¥<text class="num">{{item.price}}</text></text>
								<!-- <text class="y_money">¥{{item.otPrice}}</text> -->
							</view>
						</view>
					</view>
					<!-- <navigator :url="`/pages/activity/goods_combination_details/index?id=${item.id}`" hover-class="none" class="item" v-for="(item, index) in combinationList" :key="index">
						<view class="pictrue">
							<image :src="item.image"></image>
						</view>
						<view class="text lines1">
							<text class="money">¥<text class="num">{{item.price}}</text></text>
							<text class="y_money">¥{{item.otPrice}}</text>
						</view>
					</navigator> -->
				</scroll-view>
			</view>
		</view>
	</view>
</template>

<script>
	let app = getApp();
	import {
		getCombinationList
	} from '@/api/activity.js';
	export default {
		name: 'b_combination',
		data() {
			return {
				combinationList: [],
				isBorader:false,
			};
		},
		created() {
			this.getCombinationList();
		},
		mounted() {},
		methods: {
			// 拼团列表
			getCombinationList: function() {
				let that = this;
				// let limit = that.$config.LIMIT;
				let data = {
					page: 1,
					limit: 4
				};
				getCombinationList(data).then(function(res) {
					that.combinationList = res.data.list;
				}).catch((res) => {
					return that.$util.Tips({
						title: res
					});
				})
			},
			goDetail(item){
				uni.navigateTo({
					url: `/pages/activity/goods_combination_details/index?id=${item.id}`
				})
			}
		}
	}
</script>

<style lang="scss">
	.default{
		width: 690rpx;
		height: 300rpx;
		border-radius: 14rpx;
		margin: 26rpx auto 0 auto;
		background-color: #ccc;
		text-align: center;
		line-height: 300rpx;
		.iconfont{
			font-size: 80rpx;
		}
	}
	.combination{
		width: 690rpx;
		background-color: #fff;
		border-radius: 14rpx;
		margin: 26rpx auto 0 auto;
		padding: 25rpx 20rpx 21rpx 20rpx;
		
		.title {
			.sign {
				width: 40rpx;
				height: 40rpx;
		
				image {
					width: 100%;
					height: 100%;
				}
			}
		
			.name {
				font-size: 32rpx;
				color: #282828;
				margin-left: 12rpx;
				font-weight: bold;
		
				text {
					color: #797979;
					font-size: 24rpx;
					font-weight: 400;
					margin-left: 14rpx;
				}
			}
		
			.more {
				width: 122rpx;
				height: 37rpx;
				background: linear-gradient(270deg, #1DB0FC 0%, #22CAFD 100%);
				border-radius: 21rpx;
				font-size: 22rpx;
				color: #fff;
				padding-left: 2rpx;
		
				.iconfont {
					font-size: 20rpx;
				}
			}
		}
		.conter{
			margin-top: 18rpx;
			.itemCon {
				display: inline-block;
				width: 174rpx;
				margin-right: 24rpx;
			}
			.item{
				width:100%;
				.pictrue{
					width: 100%;
					height: 174rpx;
					border-radius: 6rpx;
					image{
						width: 100%;
						height: 100%;
						border-radius: 6rpx;
					}
				}
				.text{
					margin-top: 4rpx;
					.money{
						font-size: 24rpx;
						color: #FD502F;
						font-weight: bold;
						.num{
							font-size: 28rpx;
						}
					}
					.y_money{
						color: #959595;
						text-decoration: line-through;
						font-size: 20rpx;
						margin-left: 8rpx;
					}
				}
			}
		}
	}
</style>
