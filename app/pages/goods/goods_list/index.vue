<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class='productList'>
			<view class='search bg_color acea-row row-between-wrapper'>
				<!-- #ifdef H5 -->
				<view class="iconfont icon-xiangzuo" @click="goback()"></view>
				<!-- #endif -->
				<view class='input acea-row row-between-wrapper'><text class='iconfont icon-sousuo'></text>
					<input placeholder='搜索商品名称' placeholder-class='placeholder' confirm-type='search' name="search"
						:value='where.keyword' @confirm="searchSubmit" maxlength="20"></input>
				</view>
				<view class='iconfont' :class='is_switch==true?"icon-pailie":"icon-tupianpailie"' @click='Changswitch'>
				</view>
			</view>
			<view class='nav acea-row row-middle'>
				<view class='item' :class='title ? "font_color":""' @click='set_where(1)'>{{title ? title:'默认'}}</view>
				<view class='item' @click='set_where(2)'>
					价格
					<image v-if="price==1" :src="urlDomain+'/crmebimage/perset/staticImg/up.png'"></image>
					<image v-else-if="price==2" :src="urlDomain+'/crmebimage/perset/staticImg/down.png'"></image>
					<image v-else :src="urlDomain+'/crmebimage/perset/staticImg/horn.png'"></image>
				</view>
				<view class='item' @click='set_where(3)'>
					销量
					<image v-if="stock==1" :src="urlDomain+'/crmebimage/perset/staticImg/up.png'"></image>
					<image v-else-if="stock==2" :src="urlDomain+'/crmebimage/perset/staticImg/down.png'"></image>
					<image v-else :src="urlDomain+'/crmebimage/perset/staticImg/horn.png'"></image>
				</view>
				<!-- down -->
				<view class='item' :class='nows ? "font_color":""' @click='set_where(4)'>新品</view>
			</view>
			<view :class='is_switch==true?"":"listBox"' v-if="productList.length>0">
				<view class='list acea-row row-between-wrapper' :class='is_switch==true?"":"on"'>
					<view class='item' :class='is_switch==true?"":"on"' hover-class='none'
						v-for="(item,index) in productList" :key="index" @click="godDetail(item)">
						<view class='pictrue' :class='is_switch==true?"":"on"'>
							<image :src='item.image' :class='is_switch==true?"":"on"'></image>
							<span class="pictrue_log_class"
								:class="is_switch === true ? 'pictrue_log_big' : 'pictrue_log'"
								v-if="item.activityH5 && item.activityH5.type === '1'">秒杀</span>
							<span class="pictrue_log_class"
								:class="is_switch === true ? 'pictrue_log_big' : 'pictrue_log'"
								v-if="item.activityH5 && item.activityH5.type === '2'">砍价</span>
							<span class="pictrue_log_class"
								:class="is_switch === true ? 'pictrue_log_big' : 'pictrue_log'"
								v-if="item.activityH5 && item.activityH5.type === '3'">拼团</span>
						</view>
						<view class='text' :class='is_switch==true?"":"on"'>
							<view class='name line1'>{{item.storeName}}</view>
							<view class='x-money' :class='is_switch==true?"":"on"'>￥<text
									class='num'>{{item.price}}</text></view>
							<view class='sales acea-row row-between-wrapper' :class='is_switch==true?"":"on"'>
								<view>已售{{Number(item.sales)}}{{item.unitName}}</view>
							</view>
						</view>
					</view>
				</view>
				<view class='loadingicon acea-row row-center-wrapper' v-if='productList.length > 0'>
					<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
				</view>
			</view>
		</view>
		<view class='noCommodity' v-if="productList.length==0 && where.page > 1">
			<view class='pictrue'>
				<image :src="urlDomain+'/crmebimage/perset/staticImg/noShopper.png'"></image>
			</view>
			<recommend ref="recommendIndex"></recommend>
		</view>
	</view>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from "vue";
import { onLoad, onReachBottom } from "@dcloudio/uni-app";
import { getProductslist, getProductHot } from "@/api/store.js";
import recommend from "@/components/recommend/index.vue";
import { goShopDetail } from "@/libs/order.js";
import animationType from "@/utils/animationType.js";
import util from "@/utils/util.js";
import Cache from "@/utils/cache.js";
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from "pinia";
import { useColor } from '@/composables/useColor.js';

const { proxy } = getCurrentInstance();
const app = getApp();
const appStore = useAppStore();
const { uid } = storeToRefs(appStore);

const recommendIndex = ref(null);
const urlDomain = ref(Cache.get("imgHost"));
const productList = ref([]);
const is_switch = ref(true);
const where = reactive({
	keyword: "", priceOrder: "", salesOrder: "",
	news: 0, page: 1, limit: 20, cid: "",
});
const price = ref(0);
const stock = ref(0);
const nows = ref(false);
const loadend = ref(false);
const loading = ref(false);
const loadTitle = ref("加载更多");
const title = ref("");
const theme = ref(app.globalData.theme);
const { colorStyle } = useColor();

onLoad((options) => {
	where.cid = options.cid || "";
	title.value = options.title || "";
	where.keyword = options.searchValue || "";
	get_product_list();
});

onReachBottom(() => {
	if (productList.value.length > 0) get_product_list();
	else recommendIndex.value.get_host_product();
});

function goback() {
	// #ifdef H5
	return history.back();
	// #endif
	// #ifndef H5
	return uni.navigateBack({ delta: 1 });
	// #endif
}

function godDetail(item) {
	goShopDetail(item, uid.value).then(() => {
		uni.navigateTo({
			animationType: animationType.type, animationDuration: animationType.duration,
			url: `/pages/goods/goods_details/index?id=${item.id}`
		});
	});
}

function Changswitch() { is_switch.value = !is_switch.value; }

function searchSubmit(e) {
	where.keyword = e.detail.value;
	loadend.value = false;
	where.page = 1;
	get_product_list(true);
}

function set_where(e) {
	switch (e) {
		case 1: return;
		case 2:
			if (price.value == 0) price.value = 1;
			else if (price.value == 1) price.value = 2;
			else if (price.value == 2) price.value = 0;
			stock.value = 0;
			break;
		case 3:
			if (stock.value == 0) stock.value = 1;
			else if (stock.value == 1) stock.value = 2;
			else if (stock.value == 2) stock.value = 0;
			price.value = 0;
			break;
		case 4:
			nows.value = !nows.value;
			break;
	}
	loadend.value = false;
	where.page = 1;
	get_product_list(true);
}

function setWhere() {
	if (price.value == 0) where.priceOrder = "";
	else if (price.value == 1) where.priceOrder = "asc";
	else if (price.value == 2) where.priceOrder = "desc";
	if (stock.value == 0) where.salesOrder = "";
	else if (stock.value == 1) where.salesOrder = "asc";
	else if (stock.value == 2) where.salesOrder = "desc";
	where.news = nows.value ? 1 : 0;
}

function get_product_list(isPage) {
	setWhere();
	if (loadend.value) return;
	if (loading.value) return;
	if (isPage === true) productList.value = [];
	loading.value = true;
	loadTitle.value = "";
	getProductslist(where).then(res => {
		let list = res.data.list;
		let pl = util.SplitArray(list, productList.value);
		let isEnd = list.length < where.limit;
		loadend.value = isEnd;
		loading.value = false;
		loadTitle.value = isEnd ? "已全部加载" : "加载更多";
		productList.value = pl;
		where.page++;
		if (productList.value.length === 0) get_host_product();
	}).catch(() => {
		loading.value = false;
		loadTitle.value = "加载更多";
	});
}
</script>

<style scoped lang="scss">
	.bg_color{
		@include main_bg_color(theme);
	}
	.font_color{
		@include main_color(theme);
	}
	.x-money{
		@include price_color(theme);
	}
	.iconfont {
		color: #fff;
	}
    .listBox{
		padding: 20px 15px;
		margin-top: 154rpx;
	}
	.productList .search {
		width: 100%;
		height: 86rpx;
		padding-left: 23rpx;
		box-sizing: border-box;
		position: fixed;
		left: 0;
		top: 0;
		z-index: 9;
	}

	.productList .search .input {
		// width: 640rpx;
		height: 60rpx;
		background-color: #fff;
		border-radius: 50rpx;
		padding: 0 20rpx;
		box-sizing: border-box;
	}

	.productList .search .input input {
		/* #ifdef H5 */
		width: 528rpx;
		/* #endif */
		/* #ifndef H5 */
		width: 548rpx;
		/* #endif */
		height: 100%;
		font-size: 26rpx;
	}

	.productList .search .input .placeholder {
		color: #999;
	}

	.productList .search .input .iconfont {
		font-size: 35rpx;
		color: #555;
	}

	.productList .search .icon-pailie,
	.productList .search .icon-tupianpailie {
		color: #fff;
		width: 62rpx;
		font-size: 40rpx;
		height: 86rpx;
		line-height: 86rpx;
	}

	.productList .nav {
		height: 86rpx;
		color: #454545;
		position: fixed;
		left: 0;
		width: 100%;
		font-size: 28rpx;
		background-color: #fff;
		margin-top: 86rpx;
		top: 0;
		z-index: 9;
	}

	.productList .nav .item {
		width: 25%;
		text-align: center;
	}

	.productList .nav .item.font-color {
		font-weight: bold;
	}

	.productList .nav .item image {
		width: 15rpx;
		height: 19rpx;
		margin-left: 10rpx;
	}

	.productList .list {
		padding: 0 30rpx;
		margin-top: 192rpx;

	}

	.productList .list.on {
		border-radius: 14rpx;
		margin-top: 0 !important;
		background-color: #fff;
		padding: 40rpx 0 0 0;
		// margin: 20rpx 0;
		// background-color: #fff;
	}

	.productList .list .item {
		width: 335rpx;
		background-color: #fff;
		border-radius: 14rpx;
		margin-bottom: 20rpx;
	}

	.productList .list .item.on {
		width: 100%;
		display: flex;
		padding: 0 24rpx 50rpx 24rpx;
		margin: 0;
		border-radius: 14rpx;
	}

	.productList .list .item .pictrue {
		position: relative;
		width: 100%;
		height: 335rpx;
	}

	.productList .list .item .pictrue.on {
		width: 180rpx;
		height: 180rpx;
	}

	.productList .list .item .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 20rpx 20rpx 0 0;
	}

	.productList .list .item .pictrue image.on {
		border-radius: 6rpx;
	}

	.productList .list .item .text {
		padding: 18rpx 20rpx;
		font-size: 30rpx;
		color: #222;
	}

	.productList .list .item .text.on {
		width: 456rpx;
		padding: 0 0 0 20rpx;
	}

	.productList .list .item .text .money {
		font-size: 26rpx;
		font-weight: bold;
		margin-top: 8rpx;
	}

	.productList .list .item .text .money.on {
		margin-top: 50rpx;
	}

	.productList .list .item .text .money .num {
		font-size: 34rpx;
	}

	.productList .list .item .text .sales {
		font-size: 22rpx;
		color: #aaa;
		margin-top: 7rpx;
	}

	.productList .list .item .text .sales.on {
		margin-top: 12rpx;
	}

	.noCommodity {
		background-color: #fff;
		padding-bottom: 30rpx;
		margin-top: 172rpx;
	}
</style>
