<template>
	<view class="productSort">
		<view class='nav acea-row row-middle' :style="{top: iStatusBarHeight + 'px'}">
			<scroll-view class="scroll-view_x" scroll-x scroll-with-animation :scroll-left="scrollLeft" style="width:auto;overflow:hidden;">
				<view class='item' v-for="(item,index) in navLists" :key='index' :class='active==index?"on":""' @click='tabSelect(index,item.id)'
				 :id="'id'+index">
					<view>{{item.name}}</view>
					<view class='line' v-if="active==index"></view>
				</view>
			</scroll-view>
		</view>
		<view class='list acea-row row-between-wrapper' :style="{top: iStatusBarHeight + 'px'}">
			<view class='item' v-for="(item,index) in productList" :key="index" @click="godDetail(item)">
				<view class='pictrue'>
					<image :src='item.image'></image>
				</view>
				<view class='text'>
					<view class='name line1'>{{item.storeName}}</view>
					<view class='money'>￥<text class='num'>{{item.price}}</text></view>
					<view class='sales acea-row row-between-wrapper'>
						<view>已售{{item.sales }}{{item.unitName}}</view>
					</view>
				</view>
			</view>
			<view class='loadingicon acea-row row-center-wrapper mb-100' v-if="productList.length">
				<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
			</view>
		</view>
		<view class='noCommodity' :style="{top: iStatusBarHeight + 'px'}" v-if="productList.length== 0 && page > 1">
			<view class='pictrue'>
				<image :src="urlDomain+'/crmebimage/perset/staticImg/noShopper.png'"></image>
			</view>
			<recommend ref="recommendIndex"></recommend>
		</view>
	</view>
</template>

<script setup>
	import { ref, getCurrentInstance } from 'vue';
	import { onReachBottom } from '@dcloudio/uni-app';
	import {
		getCategoryList,
		getProductslist
	} from '@/api/store.js';
	import {
		goShopDetail
	} from '@/libs/order.js'
	import recommend from '@/components/recommend/index.vue';
	import Cache from '@/utils/cache.js';
	import util from '@/utils/util.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
	import animationType from '@/utils/animationType.js'

	const { proxy } = getCurrentInstance();
	const appStore = useAppStore();
	const { uid } = storeToRefs(appStore);

	const recommendIndex = ref(null);

	const urlDomain = ref(Cache.get("imgHost"));
	const navLists = ref([]);
	const productList = ref([]);
	const scrollLeft = ref(0);
	const active = ref(0);
	const loading = ref(false);
	const loadend = ref(false);
	const loadTitle = ref('加载更多');
	const page = ref(1);
	const limit = ref(10);
	const cid = ref(0);
	const hostProduct = ref([]);
	const iStatusBarHeight = ref(0); // 状态栏高度

	// created
	// #ifdef APP-PLUS
	iStatusBarHeight.value = uni.getSystemInfoSync().statusBarHeight;
	// #endif
	getAllCategory();

	// 去详情页
	function godDetail(item) {
		goShopDetail(item, uid.value).then(res => {
			uni.navigateTo({
				animationType: animationType.type,
				animationDuration: animationType.duration,
				url: `/pages/goods/goods_details/index?id=${item.id}`
			})
		})
	}
	function tabSelect(index, id) {
		active.value = index;
		const query = uni.createSelectorQuery().in(proxy);
		query.select('#id' + index).boundingClientRect(data => {
			scrollLeft.value = (index - 1) * data.width;
		}).exec();
		cid.value = id;
		loadend.value = false;
		page.value = 1;
		productList.value = [];
		getProductList();
	}
	async function getAllCategory() {
		try {
			const res = await getCategoryList();
			const lists = Array.isArray(res && res.data) ? res.data : [];
			navLists.value = lists;
			const pid = lists[0] && lists[0].id ? lists[0].id : 0;
			tabSelect(0, pid);
		} catch (e) {
			navLists.value = [];
			tabSelect(0, 0);
		}
	}
	function getProductList() {
		if (loadend.value) return;
		if (loading.value) return;
		loading.value = true;
		loadTitle.value = '';
		getProductslist({
			page: page.value,
			limit: limit.value,
			cid: cid.value
		}).then(res => {
			let list = res.data.list,
				isEnd = list.length < limit.value;
			productList.value = util.SplitArray(list, productList.value);
			loading.value = false;
			loadend.value = isEnd;
			loadTitle.value = isEnd ? "我也是有底线的~" : "加载更多";
			page.value = page.value + 1;
		}).catch(err => {
			loading.value = false,
				loadTitle.value = '加载更多'
		});
	}

	onReachBottom(() => {
		recommendIndex.value.get_host_product();
	});

	defineExpose({ getProductList, tabSelect });
</script>

<style scoped lang="scss">
	.productSort {
		.nav {
			padding: 0 30rpx;
			width: 100%;
			white-space: nowrap;
			box-sizing: border-box;
			height: 86rpx;
			background-color: #fff;
			position: fixed;
			top: 0;
			left: 0;
			z-index: 9;

			.item {
				display: inline-block;
				font-size: 30rpx;
				color: #282828;
				padding-right: 46rpx;

				&.on {
					color: #4B56AA;
					font-weight: bold;
				}

				.line {
					width: 40rpx;
					height: 4rpx;
					background-color: #4B56AA;
					margin: 10rpx auto 0 auto;
				}
			}
		}

		.list {
			position: relative;
			margin-top: 86rpx;
			padding: 0 20rpx;

			.item {
				width: 345rpx;
				margin-top: 20rpx;
				background-color: #fff;
				border-radius: 20rpx;

				.pictrue {
					position: relative;
					width: 100%;
					height: 345rpx;

					image {
						width: 100%;
						height: 100%;
						border-radius: 20rpx 20rpx 0 0;
					}
				}

				.text {
					padding: 20rpx 17rpx 26rpx 17rpx;
					font-size: 30rpx;
					color: #222;

					.money {
						font-size: 26rpx;
						font-weight: bold;
						margin-top: 8rpx;
						@include price_color(theme); 
						.num {
							font-size: 34rpx;
						}
					}
				}
			}
		}
	}
	.noCommodity {
		position: relative;
	}
	.scroll-Y{
		height: 100vh;
	}
	.mb-100 {
		margin-bottom: 100rpx;
	}
</style>