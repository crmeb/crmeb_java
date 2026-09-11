<template>
	<div class="quality-recommend" :data-theme="theme" :style="colorStyle">
		<view class="saleBox" v-if="typeInfo.pic"></view>
		<view class="header skeleton-rect" v-if="typeInfo.pic">
			<view class="borRadius14">
				<image :src="typeInfo.pic" class="slide-image borRadius14" lazy-load></image>
			</view>
		</view>
		<div class="title acea-row row-center-wrapper">
			<div class="line"></div>
			<div class="name">
				<span class="iconfont icon-jingpintuijian" ></span> {{typeInfo.name}}
			</div>
			<div class="line"></div>
		</div>
		<view class="wrapper">
			<view class="list">
				<view class="item acea-row row-middle" v-for="(item,index) in tempArr" :key="index" @click="toDetail(item.id)">
					<view class="img_box">
						<image class="pictrue" :src="item.image"></image>
					</view>
					<view class="ml_11 flex-column justify-between">
						<view class="goods_name">{{item.storeName}}</view>
						<view class="price flex justify-between">
							<view>
								<text class="price_bdg">￥</text>{{item.price}}
								<text class="otPrice">￥{{item.otPrice}}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
			<view class='loadingicon acea-row row-center-wrapper' v-if="goodScroll">
				<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>
			</view>
			<view class="txt-bar"  v-if="tempArr.length > 0">我也是有底线的~</view>
			<emptyPage title="暂无数据~" v-if="tempArr.length == 0"></emptyPage>
		</view>
	</div>
</template>
<script setup>
	import { ref } from 'vue';
	import { onLoad, onReachBottom } from '@dcloudio/uni-app';
	import emptyPage from '@/components/emptyPage.vue';
	import GoodList from '@/components/goodList/index.vue';
	import {getGroomList as getGroomListApi} from '@/api/store.js';
	import {productRank} from '@/api/api.js'
	import Loading from '@/components/Loading/index.vue';
	import animationType from '@/utils/animationType.js'
import { useColor } from '@/composables/useColor.js';
	let app = getApp()

	const circular = ref(true);
	const theme = ref(app.globalData.theme);
	const { colorStyle } = useColor();
	const typeInfo = ref({});
	const loading = ref(false);
	const params = ref({ //精品推荐分页
		page: 1,
		limit: 10,
	});
	const goodScroll = ref(true); //精品推荐开关
	const tempArr = ref([]);

	function getGroomList() {
		loading.value = true
		if (!goodScroll.value) return
		getGroomListApi(typeInfo.value.type, params.value).then(({data}) => {
			goodScroll.value = data.list.length >= params.value.limit
			loading.value = false
			params.value.page++
			tempArr.value = tempArr.value.concat(data.list)
		})
	}
	function getProductRank(){
		productRank().then(res=>{
			tempArr.value = res.data;
		})
	}
	function toDetail(id){
		uni.navigateTo({
			animationType: animationType.type,
			animationDuration: animationType.duration,
			url:'/pages/goods/goods_details/index?id=' + id
		})
	}

	onLoad((e) => {
		typeInfo.value = e;
		uni.setNavigationBarTitle({
			title: typeInfo.value.name
		});
		if(typeInfo.value.name == '商品排行'){
			getProductRank();
		}else{
			getGroomList();
		}
	});

	onReachBottom(() => {
		if (params.value.page != 1) {
			getGroomList();
		}
	});
</script>
<style lang="scss">
	::v-deep  .quality-recommend {
		background-color: #f5f5f5;
	}
	.saleBox{
		width: 100%;
		height: 298rpx;
		/* #ifdef MP */
		height: 300rpx;
		/* #endif */
		@include main_bg_color(theme);
		border-radius: 0 0 50rpx 50rpx;
	}
	.quality-recommend .header {
		width: 710rpx;
		height: 330rpx;
		margin: -276rpx auto 0 auto;
		border-radius: 14rpx;
		overflow: hidden;
		.slide-image{
			height: 330rpx !important;
			border-radius: 14rpx;
			overflow: hidden;
		}
	}
	
	.quality-recommend .header image {
		width: 100%;
		height: 100%;
		border-radius: 14rpx;
		overflow: hidden;
	}
	.quality-recommend {
		.wrapper {
			// background: #fff;
			.list {
				width: 690rpx;
				border-radius: 20rpx;
				background-color: #fff;
				margin: 0rpx auto 0 auto;
				padding: 20rpx 20rpx 30rpx;
				box-sizing: border-box;
				.item {
					background: #fff;
					margin-top: 26rpx;
					.img_box{
						width: 180rpx;
						height: 180rpx;
						background: #F3F3F3;
						position: relative;
						.pictrue{
							width:100%;
							height:100%;
							border-radius: 10rpx;
						}
						.rank_bdg{
							width: 100%;
							height: 46rpx;
							position: absolute;
							bottom: 0;
							left: 0;
							right: 0;
							margin: auto;
							text-align: center;
							color: #fff;
							font-size: 24rpx;
							line-height: 46rpx;
						}
					}
					.ml_11{
						margin-left: 22rpx;
						border-bottom: 1px solid #eee;
						padding-bottom: 20rpx;
					}
					.goods_name{
						width: 420rpx;
						height: 80rpx;
						font-size: 30rpx;
						font-weight: 400;
						color: #333333;
						line-height: 40rpx;
						overflow: hidden;
						text-overflow:ellipsis;
						white-space: wrap;
					}
				}
			}
		}
		
		.title {
			height: 120rpx;
			font-size: 32rpx;
			color: #282828;
			background-color: #f5f5f5;

			.name {
				margin: 0 20rpx;

				.iconfont {
					margin-right: 10rpx;
				}
			}

			.line {
				width: 230rpx;
				height: 2rpx;
				background-color: #e9e9e9;
			}
		}
	}
	.price{
		margin-top: 60rpx;
		font-size: 34rpx;
		font-weight: 600;
		@include price_color(theme);
		.price_bdg{
			font-size: 26rpx;
		}
		.otPrice{
			font-size: 24rpx;
			color: #999999;
			font-weight: 400;
			padding-left: 12rpx;
			text-decoration: line-through;
		}
		.cart_icon{
			width: 48rpx;
			height: 48rpx;
			border-radius: 50%;
			@include main_bg_color(theme);
			text-align: center;
			line-height: 40rpx;
			.iconfont{
				font-size: 28rpx;
				font-weight: 400;
				color: #fff;
			}
		}
	}
	.txt-bar {
		padding: 20rpx 0;
		text-align: center;
		font-size: 26rpx;
		color: #666;
		background-color: #f5f5f5;
	}
</style>
