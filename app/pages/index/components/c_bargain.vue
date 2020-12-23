<template>
	<view :class="{borderShow:isBorader}">
		<view class="combination" v-if="bargList.length">
			<view class="title acea-row row-between-wrapper">
				<view class="acea-row row-middle">
					<view class="sign">
						<image src="../../../static/images/sign03.png"></image>
					</view>
					<view class="name">砍价活动<text>呼朋唤友来砍价</text></view>
				</view>
				<navigator url="/pages/activity/goods_bargain/index" hover-class="none" class="more acea-row row-center-wrapper">品质好货<text class="iconfont icon-xiangyou"></text></navigator>
			</view>
			<view class="conter acea-row">
				<scroll-view scroll-x="true" style="white-space: nowrap; vertical-align: middle;" show-scrollbar="false">
					<view class="itemCon" v-for="(item, index) in bargList" :key="index" @click="bargDetail(item)">
						<view class="item">
							<view class="pictrue">
								<image :src="item.image"></image>
							</view>
							<view class="text lines1">
								<text class="money">¥<text class="num">{{item.minPrice}}</text></text>
								<text class="y_money">¥{{item.price}}</text>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
		</view>
	</view>
</template>

<script>
	let app = getApp();
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		getBargainList
	} from '@/api/activity.js';
	import { mapGetters } from 'vuex';
	export default {
		name: 'c_bargain',
		computed: mapGetters(['uid']),
		data() {
			return {
				bargList: [],
				isBorader:false
			};
		},
		created() {
			this.getBargainList();
		},
		mounted() {
		},
		methods: {
			// 砍价列表
			getBargainList() {
				let limit = this.$config.LIMIT;
				getBargainList({
					page: 1,
					limit: limit
				}).then(res => {
					this.bargList = res.data.list
				})
			},
			bargDetail(item){
			   uni.navigateTo({
			   	url: `/pages/activity/goods_bargain_details/index?id=${item.id}&bargain=${this.uid}`
			   });
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
		// height: 288rpx;
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
				background: linear-gradient(135deg, #FF9F6C 0%, #FD502F 100%);
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
