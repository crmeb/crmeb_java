<template>
	<view :class="{borderShow:isBorader}">
		<view class="combination" v-if="bargList.length">
			<view class="title acea-row row-between">
				<view class="acea-row row-column">
					<image src="../../../static/images/kanjia.png" class="pic"></image>
				</view>
				<navigator url="/pages/activity/goods_bargain/index" hover-class="none" class="more acea-row row-center-wrapper">GO<text class="iconfont icon-xiangyou"></text></navigator>
			</view>
			<view class="conter acea-row">
				<scroll-view scroll-x="true" style="white-space: nowrap; vertical-align: middle;" show-scrollbar="false">
					<view class="itemCon" v-for="(item, index) in bargList" :key="index" @click="bargDetail(item)">
						<view class="item">
							<view class="pictrue">
								<image :src="item.image"></image>
							</view>
							<view class="text lines1">
								<view class="name line1">{{item.title}}</view>
								<view class="money">¥<text class="num">{{item.minPrice}}</text></view>
								<view class="btn">参与砍价</view>
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
		getBargainIndexApi
	} from '@/api/activity.js';
	import { mapGetters } from 'vuex';
	export default {
		name: 'c_bargain',
		computed: mapGetters({
			'userData': 'userInfo',
			'uid': 'uid'
		}),
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
				getBargainIndexApi().then(res => {
					this.bargList = res.data ? res.data.productList : [];
				})
			},
			bargDetail(item){
			   uni.navigateTo({
			   	url: `/pages/activity/goods_bargain_details/index?id=${item.id}&startBargainUid=${this.uid}`
			   });
			}
		}
	}
</script>

<style lang="scss" scoped>
	.pic{
		width: 130rpx;
		height: 30rpx;
	}
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
		width: auto;
		background-image: url(../../../static/images/kjbj.png);
		background-repeat: no-repeat;
		background-size: 100%;
		background-color: #fff;
		border-radius: 14rpx;
		margin: 30rpx auto 0 auto;
		padding: 25rpx 20rpx 25rpx 20rpx;
		
		.title {
			.sign {
				font-size: 32rpx;
				color: $theme-color;
				margin-bottom: 2rpx;
				font-weight: bold;
				margin-bottom: 10rpx;
			}
		
			.name {
				
				text {
					color: #333333;
					font-size: 26rpx;
					font-weight: 400;
				}
			}
		
			.more {
				width: 86rpx;
				height: 40rpx;
				background: linear-gradient(142deg, #FFE9CE 0%, #FFD6A7 100%);
				opacity: 1;
				border-radius: 18px;
				font-size: 22rpx;
				color: #FE960F;
				padding-left: 8rpx;
				font-weight: 800;
				.iconfont {
					font-size: 21rpx;
				}
			}
		}
		.conter{
			margin-top: 28rpx;
			.itemCon {
				display: inline-block;
				width: 220rpx;
				margin-right: 24rpx;
			}
			.item{
				width:100%;
				.pictrue{
					width: 100%;
					height: 220rpx;
					border-radius: 6rpx;
					image{
						width: 100%;
						height: 100%;
						border-radius: 6rpx;
					}
				}
				.text{
					margin-top: 4rpx;
					.y_money {
						font-size: 24rpx;
						color: #999999;
						text-decoration: line-through;
					}
					.name {
						font-size: 24rpx;
						color: #000;
						margin-top: 14rpx;
					}
					.money {
						color: #FD502F;
						font-size: 28rpx;
						height: 100%;
						font-weight: bold;
					    margin: 10rpx 0;
						.num {
							font-size: 28rpx;
						}
					}
					.btn{
						width: 220rpx;
						height: 48rpx;
						line-height: 48rpx;
						text-align: center;
						background: linear-gradient(129deg, #FF5555 0%, #FF0000 100%);
						opacity: 1;
						border-radius: 0px 0px 14rpx 14rpx;
						color: #FFFFFF;
						font-size: 26rpx;
						margin-top: 6rpx;
					}
				}
			}
		}
	}
</style>
