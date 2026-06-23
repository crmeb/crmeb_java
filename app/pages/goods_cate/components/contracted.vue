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
					<view :style="{ backgroundImage: `url(${item.activityStyle})` }" class="border-picture"></view>
				</view>
				<view class='text'>
					<view class='name line1'>{{item.storeName}}</view>
					<view class='money'>￥<text class='num'>{{item.price}}</text></view>
					<view class='vip acea-row row-between-wrapper'>
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
				<image :src="urlDomain+'crmebimage/perset/staticImg/noShopper.png'"></image>
			</view>
			<recommend ref="recommendIndex"></recommend>
		</view>
	</view>
</template>

<script>
	import {
		getCategoryList,
		getProductslist
	} from '@/api/store.js';
	import {
		goShopDetail
	} from '@/libs/order.js'
	import recommend from '@/components/recommend';
	import {
		mapGetters
	} from "vuex";
	import animationType from '@/utils/animationType.js'
	export default {
		computed: mapGetters(['uid']),
		components: {
			recommend,
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				navLists: [],
				productList: [],
				scrollLeft: 0,
				active: 0,
				loading: false,
				loadend: false,
				loadTitle: '加载更多',
				page: 1,
				limit: 10,
				cid: 0,
				hostProduct: [],
				iStatusBarHeight: 0, // 状态栏高度
			}
		},
		created(){
			// #ifdef APP-PLUS
			this.iStatusBarHeight = uni.getSystemInfoSync().statusBarHeight;
			// #endif
			this.getAllCategory();
		},
		methods: {
			// 去详情页
			godDetail(item) {
				goShopDetail(item, this.uid).then(res => {
					uni.navigateTo({
						animationType: animationType.type,
            animationDuration: animationType.duration,
						url: `/pages/goods/goods_details/index?id=${item.id}`
					})
				})
			},
			tabSelect(index, id) {
				this.active = index;
				const query = uni.createSelectorQuery().in(this);
				query.select('#id' + index).boundingClientRect(data => {
					this.scrollLeft = (index - 1) * data.width;
				}).exec();
				this.cid = id;
				this.loadend = false;
				this.page = 1;
				this.$set(this, 'productList', [])
				this.getProductList();
			},
			async getAllCategory() {
        try {
          const res = await getCategoryList();
          const lists = Array.isArray(res && res.data) ? res.data : [];
          this.navLists = lists;
          const pid = lists[0] && lists[0].id ? lists[0].id : 0;
          this.tabSelect(0, pid);
        } catch (e) {
          this.navLists = [];
          this.tabSelect(0, 0);
        }
			},
			getProductList: function() {
				let that = this;
				if (that.loadend) return;
				if (that.loading) return;
				that.loading = true;
				that.loadTitle = '';
				getProductslist({
					page: that.page,
					limit: that.limit,
					cid: that.cid
				}).then(res => {
					let list = res.data.list,
						loadend = list.length < that.limit;
					that.productList = that.$util.SplitArray(list, that.productList);
					that.$set(that, 'productList', that.productList);
					that.loading = false;
					that.loadend = loadend;
					that.loadTitle = loadend ? "我也是有底线的~" : "加载更多";
					that.page = that.page + 1;
				}).catch(err => {
					that.loading = false,
						that.loadTitle = '加载更多'
				});
			},
		},
		onReachBottom() {
			this.$refs.recommendIndex.get_host_product();
		}
	}
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