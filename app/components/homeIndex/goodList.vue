<template>
	<!-- 商品列表 -->
	<view>
		<view v-if="tempArr.length" :style="[boxStyle]">
			<!-- 单列 -->
			<block v-if="itemStyle == 0">
				<view class="listA" :style="[gridGap]">
					<view class="item" v-for="(item, index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="pictrue">
							<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val">
							</easy-loadimage>
						</view>
						<view class="text-info text-add">
							<view>
								<view class="title line2" :style="[titleColor]" v-if="titleShow">
									<span>{{ item.storeName }}</span>
								</view>
							</view>
							<view v-if="item.productTags && item.productTags.locationUnderTitle.length">
								<text
									v-for="items in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
									:key="items.id" class="mr10 tagSolid">{{items.tagName}}</text>
							</view>
							<view class="price acea-row row-middle" :style="[priceColor]">
								<view v-if="priceShow">
										￥<span class="num semiBold">{{item.price}}</span>
								</view>
							</view>
							<view class="old-price" :style="[soldColor]" v-if="soldShow">已售
								{{ item.sales|| 0 }} {{item.unitName}}
							</view>
						</view>
					</view>
				</view>
			</block>
			<!-- 两列 -->
			<block v-if="itemStyle == 1">
				<view class="listC" :style="[gridGap]">
					<view class="item" :style="[contentStyle]" v-for="(item, index) in tempArr" :key="index"
						@click="goDetail(item)">
						<view class="pictrue">
							<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val">
							</easy-loadimage>
						</view>
						<view class="text-info">
							<view class="title line2" :style="[titleColor]" v-if="titleShow">
								<span>{{ item.storeName }}</span>
							</view>
							<view v-if="item.productTags && item.productTags.locationUnderTitle.length">
								<text
									v-for="items in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
									:key="items.id" class="mr10 tagSolid">{{items.tagName}}</text>
							</view>
							<view class="acea-row row-middle price" :style="[priceColor]">
								<view v-if="priceShow">
									￥<span class="num semiBold">{{item.price}}</span>
								</view>
							</view>
							<view class="old-price" :style="[soldColor]" v-if="soldShow">已售
								{{ item.sales|| 0 }} {{item.unitName}}
							</view>
						</view>
					</view>
				</view>
			</block>
			<!-- 三列 -->
			<block v-if="itemStyle == 2">
				<view class="listB" :style="[gridGap]">
					<view class="item" v-for="(item, index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="pictrue">
							<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val">
							</easy-loadimage>
						</view>
						<view class="text-info">
							<view class="title line2" :style="[titleColor]" v-if="titleShow">
								<span>{{ item.storeName }}</span>
							</view>
							<view v-if="item.productTags && item.productTags.locationUnderTitle.length">
								<text
									v-for="items in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
									:key="items.id" class="mr10 tagSolid">{{items.tagName}}</text>
							</view>
							<view class="price" :style="[priceColor]">
								<view v-if="priceShow">
										￥<span class="num semiBold">{{item.price}}</span>
								</view>
							</view>
							<view class="old-price" v-if="soldShow" :style="[soldColor]">
								已售 {{ item.sales|| 0 }} {{ item.unitName }}
							</view>
						</view>
					</view>
				</view>
			</block>
			<!-- 大图 -->
			<block v-if="itemStyle == 3 && tempArr.length">
				<view class="listBig" :style="[gridGap]">
					<view class="itemBig" v-for="(item,index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="img-box">
							<easy-loadimage :image-src="item.image" :radius="dataConfig.contentStyle.val">
							</easy-loadimage>
						</view>
						<view class="name line2" :style="[titleColor]" v-if="titleShow">
							<span>{{item.storeName}}</span>
						</view>
						<view style="padding: 0 8px;"
							v-if="item.productTags && item.productTags.locationUnderTitle.length">
							<text
								v-for="items in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
								:key="items.id" class="mr10 tagSolid">{{items.tagName}}</text>
						</view>
						<slot name="center"></slot>
						<view class="acea-row row-middle price" :style="[priceColor]">
							<view v-if="priceShow">
								￥<span class="num semiBold">{{item.price}}</span>
							</view>
						</view>
						<view class="old-price  mt20" :style="[soldColor]" v-if="soldShow">已售
							{{ item.sales || 0 }} {{item.unitName}}
						</view>
					</view>
				</view>
			</block>
			<view class='loadingicon acea-row row-center-wrapper' :hidden='loading==false'>
				<text class='loading iconfont icon-jiazai'></text>
			</view>
			<!-- <view class="mores-txt" v-if="goodScroll">
				<text>我是有底线的</text>
			</view> -->
		</view>
	</view>
</template>

<script setup>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	import { ref, reactive, computed, onMounted } from 'vue';
	import {
		getProductslist,productByidsApi
	} from '@/api/store.js';
	import { filterTheme } from '@/filters';
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	let app = getApp();

	//uniapp小程序用deep重写组件样式不生效
	defineOptions({
		options: {
			styleIsolation: 'shared'
		}
	});

	const props = defineProps({
		dataConfig: {
			type: Object,
			default: () => {}
		},
	});

	// data
	const tempArr = ref([]);
	const numConfig = ref(props.dataConfig.numConfig.val); //展示多少条
	const itemStyle = ref(props.dataConfig.itemStyle.tabVal); //商品列表展示方式  单列 两列 三列
	const type = ref(props.dataConfig.tabConfig.tabVal || 0); //商品类型 0指定商品，1指定品牌，2指定分类，3指定商户
	const selectId = ref(props.dataConfig.selectConfig ? props.dataConfig.selectConfig.activeValue : []); //分类
	const productIds = ref(props.dataConfig.goodsList.ids || []);
	const params = reactive({ //精品推荐分页
		page: 1,
		limit: 10,
		cid: '',
		priceOrder: '',
		salesOrder: ''
	});
	const goodScroll = ref(false);
	const loading = ref(false);
	const themeColor = ref(filterTheme(app.globalData.theme));

	// computed
	//商品名称颜色
	const titleColor = computed(() => {
		return {
			'color': props.dataConfig.titleColor.color[0].item,
		}
	});
	//最外层盒子的样式
	const boxStyle = computed(() => {
		return {
			borderRadius: props.dataConfig.bgStyle.val * 2 + 'rpx',
			background: `linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
			margin: props.dataConfig.mbConfig.val * 2 + 'rpx' + ' ' + props.dataConfig.lrConfig.val * 2 + 'rpx' +
				' ' + 0,
			padding: props.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + '16rpx' + ' ' + props.dataConfig.downConfig
				.val *
				2 + 'rpx'
		}
	});
	//图片展示样式
	const gridGap = computed(() => {
		return {
			'grid-gap': props.dataConfig.contentConfig.val * 2 + 'rpx'
		}
	});
	//文章图片的圆角和高度
	const imgStyle = computed(() => {
		return {
			'border-radius': props.dataConfig.contentStyle.val * 2 + 'rpx',
		}
	});
	//价格颜色
	const priceColor = computed(() => {
		return {
			'color': props.dataConfig.themeStyleConfig.tabVal?props.dataConfig.priceColor.color[0].item:themeColor.value,
		}
	});
	//已售数量
	const soldColor = computed(() => {
		return {
			'color': props.dataConfig.soldColor.color[0].item,
		}
	});
	//商品名称
	const titleShow = computed(() => {
		if (props.dataConfig.typeConfig.activeValue.includes(0)) {
			return true;
		} else {
			return false;
		}
	});
	//价格
	const priceShow = computed(() => {
		if (props.dataConfig.typeConfig.activeValue.includes(1)) {
			return true;
		} else {
			return false;
		}
	});
	//销量
	const soldShow = computed(() => {
		if (props.dataConfig.typeConfig.activeValue.includes(2)) {
			return true;
		} else {
			return false;
		}
	});
	//排序，0综合，1销量，2价格
	const goodsSort = computed(() => {
		return props.dataConfig.goodsSort.tabVal
	});
	//内容圆角
	const contentStyle = computed(() => {
		return {
			'border-radius': props.dataConfig.contentStyle.val ? 2*props.dataConfig.contentStyle.val + 'rpx' : '0'
		};
	});

	onMounted(() => {
		params.page = 1;
		goodScroll.value = false;
		tempArr.value = [];
		//类型为0时，直接加载选中的商品，不为0时根据条件加载商品列表
		if (type.value > 0) {
			productslist();
		} else {
			getProList();
		}
	});

	//根据商品id集合查询对应商品
	function getProductByids(data) {
		uni.showLoading({
			title: '加载中...'
		});
		let ids = data.map((item) => item.id).join(',');
		productByidsApi(ids).then((res) => {
				tempArr.value = res.data;
				uni.hideLoading();
			})
			.catch(res => {
				uni.hideLoading();
			});
	}
	function getProList() {
		getProductByids(props.dataConfig.goodsList.list);
	}
	function productslist() {
		if (goodScroll.value) return;
		loading.value = true
		params.limit = numConfig.value;
		switch (type.value) {
			case 1:
				params.cid = selectId.value.join(',');
				break;
		}

		if (goodsSort.value === 0) {
			params.priceOrder = '';
			params.salesOrder = '';
		} else if (goodsSort.value === 1) {
			params.priceOrder = '';
			params.salesOrder = 'desc';
		} else {
			params.priceOrder = 'desc';
			params.salesOrder = '';
		}

		getProductslist(params).then(res => {
			params.page = params.page + 1;
			goodScroll.value = params.page > res.data.totalPage;
			tempArr.value = tempArr.value.concat(res.data.list || []);
			loading.value = false
		});
	}
	function goDetail(item) {
		uni.navigateTo({
		    url: `/pages/goods/goods_details/index?id=${item.id}`
		})
	}
</script>

<style lang="scss" scoped>
	.mores-txt {
		text-align: center;
	}

	.text-add {
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.listBig {
		display: grid;
		grid-template-rows: auto;
		grid-template-columns: repeat(1, 1fr);

		.itemBig {
			width: 100%;

			.img-box {
				width: 100%;
				height: 710rpx;
				position: relative;
			}

			.name {
				font-size: 28rpx;
				font-weight: bold;
				margin-top: 16rpx;
				// padding: 0 8px;
			}

			.price {
				font-weight: bold;
				font-size: 12px;
				margin-top: 10rpx;
				// padding: 0 8px;

				.num {
					font-size: 32rpx;
					margin-right: 10rpx;
				}

				.old-price {
					color: #aaa;
					font-weight: normal;
				}
			}
		}
	}

	.listA {
		display: grid;
		grid-template-columns: repeat(1, 1fr);
		grid-template-rows: auto;
		width: 100%;

		.item {
			display: flex;
			width: 100%;

			.pictrue {
				width: 220rpx;
				height: 220rpx;
				position: relative;

				image {
					width: 100%;
					height: 100%;
				}
			}

			.text-info {
				margin-left: 20rpx;
				flex: 1
			}
		}
	}

	.listB {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		grid-template-rows: auto;
		width: 100%;

		.item {
			.pictrue {
				width: 100%;
				height: 220rpx;
				position: relative;

				image {
					width: 100%;
					height: 100%;
				}
			}

			.text-info {
				padding-top: 14rpx;
			}
		}
	}

	.listC {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		grid-template-rows: auto;
		width: 100%;

		::v-deep .origin-img,
		::v-deep .easy-loadimage {
			border-bottom-left-radius: 0 !important;
			border-bottom-right-radius: 0 !important;
		}

		.item {
			background-color: #fff;

			.pictrue {
				width: 100%;
				height: 345rpx;
				overflow: hidden;
				position: relative;

				image {
					width: 100%;
					height: 100%;
				}
			}

			.text-info {
				padding: 14rpx 0 14rpx 14rpx;

				.title {
					width: 300rpx;
				}
			}
		}
	}

	.text-info {
		.title {
			width: 100%;
			height: 80rpx;
			line-height: 42rpx;
			color: #333;
		}

		.old-price {
			font-weight: normal;
			font-size: 24rpx;
			color: #999;
		}

		.price {
			font-size: 36rpx;
			font-weight: 550;

			text {
				padding-bottom: 4rpx;
				font-size: 26rpx;
				font-weight: normal;
			}
		}
	}

	.mer_badge {
		padding: 0 4rpx;
		background-color: theme;
		color: #fff;
		font-size: 20rpx;
		display: inline-block;
		border-radius: 4rpx;
		line-height: 28rpx;
		height: 28rpx;
	}
</style>