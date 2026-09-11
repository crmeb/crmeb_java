<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class='collectionGoods' v-if="collectProductList.length">
			<!-- #ifdef  H5 || MP-->
			<view class='nav acea-row row-between-wrapper'>
				<view>当前共<text class='num font_color'>{{ totals }}</text>件商品</view>
				<view class='administrate acea-row row-center-wrapper' @click='manage'>{{ footerswitch ? '管理' : '取消'}}
				</view>
			</view>
			<!-- #endif -->
			<view class="list">
				<checkbox-group @change="checkboxChange" class="centent">
					<!-- #ifndef APP-PLUS-->
					<view v-for="(item,index) in collectProductList" :key="index" class='item acea-row row-middle'>
						<!-- #ifndef MP -->
						<checkbox :value="item.id.toString()" :checked="item.checked" v-if="!footerswitch"
							color="#ffffff"
							backgroundColor="#ffffff"
							activeBackgroundColor="var(--view-theme, #E93323)"
							activeBorderColor="var(--view-theme, #E93323)"
							iconColor="#ffffff"
							style="margin-right: 10rpx;" />
						<!-- #endif -->
						<!-- #ifdef MP -->
						<checkbox :value="item.id.toString()" :checked="item.checked" v-if="!footerswitch"
							style="margin-right: 10rpx;" />
						<!-- #endif -->
						<navigator :render-link="false" :url='"/pages/goods/goods_details/index?id="+item.productId' hover-class='none'
							class="acea-row">
							<view class='pictrue'>
								<image :src="item.image"></image>
							</view>
							<view>
								<view class='name line1'>{{item.storeName}}</view>
								<view class='money'>￥{{item.price}}</view>
							</view>
						</navigator>
					</view>
					<!-- #endif -->
					<!-- #ifdef APP-PLUS -->
					<view v-for="(item,index) in collectProductList" :key="index" :data-index="index"
						class='item acea-row row-middle order-item'>
						<navigator :render-link="false" :url='"/pages/goods/goods_details/index?id="+item.productId' hover-class='none' class="acea-row">
							<view class='pictrue'>
								<image :src="item.image"></image>
							</view>
							<view>
								<view class='name line1'>{{item.storeName}}</view>
								<view class='money'>￥{{item.price}}</view>
							</view>
						</navigator>
						<view class="remove borRadius14" @tap="delCollection(item.id)">删除</view>
					</view>
					<!-- #endif -->
				</checkbox-group>
			</view>
			<view class='loadingicon acea-row row-center-wrapper'>
				<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
			</view>
			<view v-if="!footerswitch" class='footer acea-row row-between-wrapper'>
				<view>
					<checkbox-group @change="checkboxAllChange" class="acea-row row-middle">
						<!-- #ifndef MP -->
						<checkbox
							value="all"
							:checked="!!isAllSelect"
							color="#ffffff"
							backgroundColor="#ffffff"
							activeBackgroundColor="var(--view-theme, #E93323)"
							activeBorderColor="var(--view-theme, #E93323)"
							iconColor="#ffffff"
						/>
						<!-- #endif -->
						<!-- #ifdef MP -->
						<checkbox value="all" :checked="!!isAllSelect" />
						<!-- #endif -->
						<text @click="isAllSelectChange" class='checkAll'>{{isAllSelect?'取消':'全选'}}</text>
					</checkbox-group>
				</view>
				<view class='button acea-row row-middle'>
					<form @submit="delCollectionAll" report-submit='true'>
						<button class='bnt cart-color' formType="submit">取消收藏</button>
					</form>
				</view>
			</view>
		</view>
		<view class='noCommodity' v-else-if="!collectProductList.length && page > 1">
			<view class='pictrue'>
				<image :src="urlDomain+'/crmebimage/perset/usersImg/noCollection.png'"></image>
			</view>
			<recommend ref="recommendIndex"></recommend>
		</view>
	</view>
</template>

<script setup>
	import {
		getCollectUserList,
		getProductHot,
		collectDelete
	} from '@/api/store.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
	import {
		toLogin
	} from '@/libs/login.js';
	import recommend from '@/components/recommend/index.vue';
	import { ref, getCurrentInstance } from 'vue';
	import { onLoad, onShow, onReachBottom } from '@dcloudio/uni-app';
import { useColor } from '@/composables/useColor.js';
	let app = getApp();
	const { proxy } = getCurrentInstance();
	const appStore = useAppStore();
	const { isLogin } = storeToRefs(appStore);

	const recommendIndex = ref(null);
	const urlDomain = ref(proxy.$Cache.get("imgHost"));
	const footerswitch = ref(true);
	const loadTitle = ref('加载更多');
	const loading = ref(false);
	const loadend = ref(false);
	const collectProductList = ref([]);
	const limit = ref(8);
	const page = ref(1);
	const isAllSelect = ref(false); //全选
	const selectValue = ref([]); //选中的数据
	const delBtnWidth = ref(80); //左滑默认宽度
	const totals = ref(0);
	const theme = ref(app.globalData.theme);
	const { colorStyle } = useColor();

	onLoad(() => {
		if (isLogin.value) {
			loadend.value = false;
			page.value = 1;
			collectProductList.value = [];
		} else {
			toLogin();
		}
	});
	onShow(() => {
		loadend.value = false;
		page.value = 1;
		collectProductList.value = [];
		get_user_collect_product();
	});

	function manage() {
		footerswitch.value = !footerswitch.value;
	}

	function checkboxChange(event) {
		var items = collectProductList.value,
			values = event.detail.value;
		for (var i = 0, lenI = items.length; i < lenI; ++i) {
			const item = items[i]
			if (values.includes(item.id.toString())) {
				item.checked = true
			} else {
				item.checked = false
			}
		}
		selectValue.value = values.toString();
		isAllSelect.value = items.length === values.length;
	}
	function checkboxAllChange(event) {
		let value = event.detail.value;
		if (value.length > 0) {
			setAllSelectValue(1)
		} else {
			setAllSelectValue(0)
		}
	}
	function isAllSelectChange() {
		isAllSelect.value = !isAllSelect.value
		if (isAllSelect.value) {
			setAllSelectValue(1)
		} else {
			setAllSelectValue(0)
		}
	}
	function setAllSelectValue(status) {
		let selectValueArr = [];
		if (collectProductList.value.length > 0) {
			collectProductList.value.map(item => {
				if (status) {
					item.checked = true
					selectValueArr.push(item.id);
					isAllSelect.value = true;
				} else {
					item.checked = false
					isAllSelect.value = false;
				}
			});
			selectValue.value = selectValueArr.toString();
		}
	}
	/**
	 * 获取收藏产品
	 */
	function get_user_collect_product() {
		if (loading.value) return;
		if (loadend.value) return;
		loading.value = true;
		loadTitle.value = "";
		getCollectUserList({
			page: page.value,
			limit: limit.value
		}).then(res => {
			res.data.list.map(item => {
				item.right = 0;
			});
			totals.value = res.data.total;
			let list = res.data.list;
			let loadendVal = list.length < limit.value;
			collectProductList.value = proxy.$util.SplitArray(list, collectProductList.value);
			loadend.value = loadendVal;
			loadTitle.value = loadendVal ? '我也是有底线的~' : '加载更多';
			page.value = page.value + 1;
			loading.value = false;
		}).catch(err => {
			loading.value = false;
			loadTitle.value = "加载更多";
		});
	}
	/**
	 * 取消收藏
	 */
	function delCollection(id, index) {
		selectValue.value = id;
		del({
			ids: selectValue.value.toString()
		});
	}
	function delCollectionAll() {
		if (!selectValue.value || selectValue.value.length == 0) return proxy.$util.Tips({
			title: '请选择商品'
		});
		del({
			ids: selectValue.value
		});
	}
	function del(data) {
		collectDelete(data).then(res => {
			proxy.$util.Tips({
				title: '取消收藏成功',
				icon: 'success'
			});
			selectValue.value = [];
			collectProductList.value = [];
			loadend.value = false;
			page.value = 1;
			get_user_collect_product();
		}).catch(err => {
			return proxy.$util.Tips({
				title: err
			})
		});
	}

	/**
	 * 页面上拉触底事件的处理函数
	 */
	onReachBottom(() => {
		get_user_collect_product();
		recommendIndex.value.get_host_product();
	});
</script>

<style scoped lang="scss">
	.money{
		font-size: 26rpx;
		@include price_color(theme);
	}
	.order-item {
		width: 100%;
		display: flex;
		position: relative;
		align-items: right;
		flex-direction: row;
	}

	.remove {
		width: 120rpx;
		height: 40rpx;
		@include main_bg_color(theme);
		color: #fff;
		position: absolute;
		bottom: 30rpx;
		right: 60rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 24rpx;
	}

	.collectionGoods {

		.nav {
			width: 92%;
			height: 90rpx;
			background-color: #fff;
			padding: 0 24rpx;
			-webkit-box-sizing: border-box;
			box-sizing: border-box;
			font-size: 28rpx;
			color: #282828;
			position: fixed;
			left: 30rpx;
			z-index: 5;
			top: 30rpx;
			border-bottom: 1px solid #EEEEEE;
			border-top-left-radius: 14rpx;
			border-top-right-radius: 14rpx;
		}

		.list {
			padding: 30rpx;
			/* #ifndef APP-PLUS*/
			margin-top: 90rpx;
			/* #endif */
			/* #ifdef MP  */
			//margin-top: 0rpx;
			/* #endif */

			.name {
				width: 434rpx;
				/* #ifdef APP-PLUS */
				width: 486rpx;
				/* #endif */
				margin-bottom: 56rpx;
			}
		}

		.centent {
			/* #ifdef H5 || MP */
			background-color: #fff;
			/* #endif */
			border-bottom-left-radius: 14rpx;
			border-bottom-right-radius: 14rpx;
		}
	}

	.collectionGoods .item {
		background-color: #fff;
		padding-left: 24rpx;
		height: 180rpx;
		margin-bottom: 15rpx;
		border-radius: 14rpx;

	}

	.collectionGoods .item .pictrue {
		width: 130rpx;
		height: 130rpx;
		margin-right: 20rpx;
	}

	.collectionGoods .item .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 14rpx;
	}

	.collectionGoods .item .text {
		width: 535rpx;
		height: 130rpx;
		font-size: 28rpx;
		color: #282828;
	}

	.collectionGoods .item .text .name {
		width: 100%;
	}

	.collectionGoods .item .text .delete {
		font-size: 26rpx;
		color: #282828;
		width: 144rpx;
		height: 46rpx;
		border: 1px solid #bbb;
		border-radius: 4rpx;
		text-align: center;
		line-height: 46rpx;
	}

	.noCommodity {
		background-color: #fff;
		padding-top: 1rpx;
		border-top: 0;
	}

	.footer {
		z-index: 9;
		width: 100%;
		height: 96rpx;
		background-color: #fff;
		position: fixed;
		padding: 0 30rpx;
		box-sizing: border-box;
		border-top: 1rpx solid #eee;
		border-bottom: 1px solid #EEEEEE;
		/* #ifdef H5 || MP */
		bottom: 0rpx;
		/* #endif */
		/* #ifdef APP-PLUS */
		bottom: 0;

		/* #endif */
		/* #ifndef MP || APP-PLUS */
		// bottom: 98rpx;
		// bottom: calc(98rpx + constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		// bottom: calc(98rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
		/* #endif */
		.checkAll {
			font-size: 28rpx;
			color: #282828;
			margin-left: 16rpx;
		}

		.button .bnt {
			font-size: 28rpx;
			color: #999;
			border-radius: 30rpx;
			border: 1px solid #999;
			height: 60rpx;
			text-align: center;
			line-height: 60rpx;
		}
	}
	.font_color{
		@include main_color(theme);
	}
	::v-deep  uni-checkbox .uni-checkbox-input.uni-checkbox-input-checked,
	::v-deep  checkbox .uni-checkbox-input.uni-checkbox-input-checked {
		@include main_bg_color(theme);
		@include coupons_border_color(theme);
		color: #fff!important
	}
	
	::v-deep  checkbox .wx-checkbox-input.wx-checkbox-input-checked {
		@include main_bg_color(theme);
		@include coupons_border_color(theme);
		color: #fff!important;
		margin-right: 0 !important;
	}
</style>
