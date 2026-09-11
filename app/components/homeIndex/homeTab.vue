<template>
	<!-- tab选项卡 -->
	<view class="index-product-wrapper" :style="[mbConfig]">
		<view class="nav-bd longTab" :style="[tabBgColor]">
			<scroll-view scroll-x="true" style="white-space: nowrap; display: flex" :scroll-left="tabLeft">
				<view class="longItem"
					:style="'color:' + (index == ProductNavindex ? checkColor : fontColor)+';--color:'+checkColor"
					:data-index="index" :class="index===ProductNavindex?'click':''" v-for="(item,index) in navList"
					:key="index" :id="'id'+index" @click="ProductNavTab(item, index)">{{ item.val }}
				</view>
			</scroll-view>
		</view>
		<view :style="[boxStyle]">
			<!-- 单列 -->
			<block v-if="itemStyle == 0">
				<view class="listA" :style="[gridGap]">
					<view class="item" v-for="(item, index) in tempArr" :key="index" @click="goDetail(item)">
						<view class="pictrue">
							<easy-loadimage :image-src="item.image"
								:radius="dataConfig.contentStyle.val"></easy-loadimage>
						</view>
						<view class="text-info text-add">
							<view>
								<view class="title line2" :style="[titleColor]" v-if="showArr.includes(0)">
									<span>{{ item.storeName }}</span>
								</view>
							</view>
							<view v-if="item.productTags && item.productTags.locationUnderTitle.length">
								<text
									v-for="items in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
									:key="items.id" class="mr10 tagSolid">{{items.tagName}}</text>
							</view>
							<view class="price acea-row row-middle" :style="[priceColor]">
								<view v-if="showArr.includes(1)">
									￥<span class="num semiBold">{{item.price}}</span>
								</view>
							</view>
							<view class="old-price" v-if="showArr.includes(2)" :style="[soldColor]">
								已售 {{ item.sales || 0 }} {{ item.unitName }}
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
							<easy-loadimage :image-src="item.image"
								:radius="dataConfig.contentStyle.val"></easy-loadimage>
						</view>
						<view class="text-info">
							<view class="title line2" :style="[titleColor]" v-if="showArr.includes(0)">
								<span>{{ item.storeName }}</span>
							</view>
							<view v-if="item.productTags && item.productTags.locationUnderTitle.length">
								<text
									v-for="items in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
									:key="items.id" class="mr10 tagSolid">{{items.tagName}}</text>
							</view>
							<view class="row-middle price" :style="[priceColor]">
								<view v-if="showArr.includes(1)">
									￥<span class="num semiBold">{{item.price}}</span>
								</view>
								<view class="old-price ml10 " v-if="showArr.includes(2)" :style="[soldColor]">
									已售 {{ item.sales || 0 }} {{ item.unitName }}
								</view>
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
							<easy-loadimage :image-src="item.image"
								:radius="dataConfig.contentStyle.val"></easy-loadimage>
						</view>
						<view class="text-info">
							<view class="title line2" :style="[titleColor]" v-if="showArr.includes(0)">
								<span>{{ item.storeName }}</span>
							</view>
							<view v-if="item.productTags && item.productTags.locationUnderTitle.length">
								<text
									v-for="items in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
									:key="items.id" class="mr10 tagSolid">{{items.tagName}}</text>
							</view>
							<view class="price" :style="[priceColor]">
								<view v-if="showArr.includes(1)">
									￥<span class="num semiBold">{{item.price}}</span>
								</view>
							</view>
							<view class="old-price " v-if="showArr.includes(2)" :style="[soldColor]">
								已售 {{ item.sales || 0 }} {{ item.unitName }}
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
							<easy-loadimage :image-src="item.image"
								:radius="dataConfig.contentStyle.val"></easy-loadimage>
						</view>
						<view class="name line2" :style="[titleColor]" v-if="showArr.includes(0)">
							<span>{{item.storeName}}</span>
						</view>
						<view style="padding: 0 8px;"
							v-if="item.productTags && item.productTags.locationUnderTitle.length">
							<text
								v-for="items in item.productTags.locationUnderTitle.length>3?item.productTags.locationUnderTitle.slice(0,3):item.productTags.locationUnderTitle"
								:key="items.id" class="mr10 tagSolid">{{items.tagName}}</text>
						</view>
						<slot name="center"></slot>
						<view class="row-middle price" :style="[priceColor]">
							<span v-if="showArr.includes(1)">
								￥<span class="num semiBold">{{item.price}}</span>
							</span>
							<view class="old-price" v-if="showArr.includes(2)" :style="[soldColor]">
								已售 {{ item.sales || 0 }} {{ item.unitName }}
							</view>
						</view>
					</view>
				</view>
			</block>
		</view>
	</view>
</template>

<script>
	// uniapp 小程序用 deep 重写组件样式不生效，需保留 MP 组件选项
	export default {
		options: {
			styleIsolation: 'shared'
		}
	}
</script>
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
	import { ref, computed, nextTick, onMounted, getCurrentInstance } from 'vue';
	import {
		getProductslist, productByidsApi
	} from '@/api/store.js';
	import easyLoadimage from '@/components/base/easy-loadimage.vue';
	import util from '@/utils/util.js';
	import { filterTheme } from '@/filters';
	let app = getApp();

	const props = defineProps({
		dataConfig: {
			type: Object,
			default: () => {}
		}
	});

	const tempArr = ref([]);
	const iSshowH = ref(false);
	const ProductNavindex = ref(0);
	const itemStyle = ref(0); //样式类型
	const titleConfig = ref(1); //标题位置
	const infoColor = ref('#999');
	const goodType = ref(3);
	const loadend = ref(false);
	const loading = ref(false);
	const page = ref(1);
	const isWidth = ref(0); //每个导航栏占位
	const tabLeft = ref(0);
	const limit = ref(0); //分页条数
	const themeColor = ref(filterTheme(app.globalData.theme));

	//标签文字颜色
	const fontColor = computed(() => props.dataConfig.fontColor.color[0].item);
	//选中颜色
	const checkColor = computed(() => props.dataConfig.checkThemeStyleConfig.tabVal ? props.dataConfig.checkColor.color[0].item : themeColor.value);
	//选项卡背景颜色
	const tabBgColor = computed(() => ({
		background: `linear-gradient(${props.dataConfig.tabBgColor.color[0].item}, ${props.dataConfig.tabBgColor.color[1].item})`,
	}));
	//页面间距
	const mbConfig = computed(() => ({
		marginTop: props.dataConfig.mbConfig.val ? props.dataConfig.mbConfig.val + 'px' : 0
	}));
	//分类列表
	const navList = computed(() => props.dataConfig.tabItemConfig.list);
	//最外层盒子的样式
	const boxStyle = computed(() => ({
		borderRadius: props.dataConfig.bgStyle.val * 2 + 'rpx',
		background: `linear-gradient(${props.dataConfig.bgColor.color[0].item}, ${props.dataConfig.bgColor.color[1].item})`,
		margin: props.dataConfig.topConfig.val * 2 + 'rpx' + ' ' + props.dataConfig.lrConfig.val * 2 + 'rpx' +
			' ' + 0,
		padding: props.dataConfig.upConfig.val * 2 + 'rpx' + ' ' + 0 + ' ' + props.dataConfig.downConfig.val *
			2 + 'rpx'
	}));
	//商品间距
	const gridGap = computed(() => ({
		'grid-gap': props.dataConfig.contentConfig.val * 2 + 'rpx'
	}));
	//图片的圆角和高度
	const imgStyle = computed(() => ({
		'border-radius': props.dataConfig.contentStyle.val * 2 + 'rpx',
	}));
	//价格颜色
	const priceColor = computed(() => ({
		'color': props.dataConfig.priceThemeStyleConfig.tabVal ? props.dataConfig.priceColor.color[0].item : themeColor.value,
	}));
	//商品名称颜色
	const titleColor = computed(() => ({
		'color': props.dataConfig.titleColor.color[0].item,
	}));
	//已售数量
	const soldColor = computed(() => ({
		'color': props.dataConfig.soldColor.color[0].item,
	}));
	const showArr = computed(() => props.dataConfig.tabItemConfig.list[ProductNavindex.value].activeList.showContent);
	//商品名称
	const titleShow = computed(() => props.dataConfig.typeConfig.activeValue.includes(0));
	//价格
	const priceShow = computed(() => props.dataConfig.typeConfig.activeValue.includes(1));
	//销量
	const soldShow = computed(() => props.dataConfig.typeConfig.activeValue.includes(2));
	//内容圆角
	const contentStyle = computed(() => ({
		'border-radius': props.dataConfig.contentStyle.val ? props.dataConfig.contentStyle.val + 'px' : '0'
	}));

	uni.getSystemInfo({
		success(e) {
			isWidth.value = (e.windowWidth) / 5;
		}
	})

	onMounted(() => {
		//默认加载第一项的商品数据
		if (navList.value) {
			itemStyle.value = navList.value[0].activeList ? navList.value[0].activeList.styleType : 0;
			if (navList.value[0].activeList && navList.value[0].activeList.activeProTabIndex == 0) {
				getProductByids(navList.value[0].activeList.goods);
			} else {
				limit.value = navList.value[0].activeList ? navList.value[0].activeList.num : 3;
				if (navList.value[0].activeList) {
					getGroomList(navList.value[0].activeList);
				}
			}
		}
	});

	//根据商品id集合查询对应商品
	function getProductByids(data) {
		if (!data.length) return;
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
	// 选项卡切换点击事件；商品类型选择除第一个指定商品，加载商品从平台端获取数据，其余选项均请求接口加载
	function changeTab(item, index) {
		tempArr.value = [];
		if (item.activeList.activeProTabIndex == 0) {
			getProductByids(item.activeList.goods);
		} else {
			page.value = 1;
			loadend.value = false;
			getGroomList(item.activeList);
		}
	}
	// 商品列表
	function getGroomList(item) {
		let cid = item.activeValue; //分类id
		let goodsSort = item.goodsSort // 商品排序，0综合，1按销量，2按价格
		let priceOrder = '';
		let salesOrder = '';
		if (loadend.value) return false;
		if (loading.value) return false;
		if (goodsSort === 0) {
			priceOrder = '';
			salesOrder = '';
		} else if (goodsSort === 1) {
			priceOrder = '';
			salesOrder = 'desc';
		} else {
			priceOrder = 'desc';
			salesOrder = '';
		}
		getProductslist({
				page: page.value,
				limit: limit.value,
				cid: cid,
				priceOrder: priceOrder,
				salesOrder: salesOrder
			}).then((res) => {
				let list = res.data.list;
				tempArr.value = util.SplitArray(list, tempArr.value);
				let loadendVal = list.length < limit.value;
				loadend.value = loadendVal;
				loading.value = false;
				page.value = page.value + 1;
			})
			.catch(res => {
				loading.value = false;
			});
	}
	// 选项卡切换
	function ProductNavTab(item, index) {
		ProductNavindex.value = index;
		itemStyle.value = navList.value[index].activeList.styleType;
		nextTick(() => {
			let id = 'id' + index;
			tabLeft.value = (index - 2) * isWidth.value //设置下划线位置
		})
		limit.value = item.activeList.num;
		changeTab(item, index);
	}
	function goDetail(item) {
		uni.navigateTo({
			url: `/pages/goods/goods_details/index?id=${item.id}`
		})
	}
</script>

<style lang="scss" scoped>
	.longTab {
		.longItem {
			height: 70rpx;
			display: inline-block;
			line-height: 70rpx;
			text-align: center;
			font-size: 28rpx;
			color: #333333;
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
			margin-right: 46rpx;
			&.click {
				font-weight: bold;
				font-size: 30rpx;
				position: relative;

				&::after {
					content: '';
					width: 40rpx;
					height: 4rpx;
					background: var(--color);
					position: absolute;
					bottom: 0;
					left: 50%;
					transform: translateX(-50%);
				}
			}
		}
	}

	.index-product-wrapper {

		&.on {
			min-height: 1500rpx;
		}

		.nav-bd {
			height: 70rpx;
			line-height: 70rpx;
			padding-left: 20rpx;
			background: #fff;

			.item {
				display: inline-block;
				font-size: 28rpx;
				color: #333;
				font-weight: 400;
				padding-right: 48rpx;

				&.on {
					border-radius: 0;
				}
			}
		}
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
		padding: 0 20rpx;

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
		padding: 0 20rpx;

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
				margin-left: 14rpx;
				flex: 1
			}
		}
	}

	.listB {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		grid-template-rows: auto;
		width: 100%;
		padding: 0 20rpx;

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
				padding: 10rpx 0;
			}
		}
	}

	.listC {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		grid-template-rows: auto;
		width: 100%;
		padding: 0 20rpx;

		::v-deep .origin-img,
		::v-deep .easy-loadimage {
			border-bottom-left-radius: 0 !important;
			border-bottom-right-radius: 0 !important;
		}

		.item {
			background-color: #fff;
			overflow: hidden;

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
				padding: 16rpx 0 16rpx 16rpx;

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
			line-height: 40rpx;
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