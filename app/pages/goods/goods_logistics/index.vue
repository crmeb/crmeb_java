<template>
	<view>
		<view class='logistics'>
			<view class='header acea-row row-between row-top'>
				<view class='pictrue'>
					<image :src='product.productImg'></image>
				</view>
				<view class='text acea-row row-between'>
					<view class='name line2'>{{product.productName}}</view>
					<view class='money'>
						<view>￥{{product.price}}</view>
						<view>x{{product.payNum}}</view>
					</view>
				</view>
			</view>
			<view class='logisticsCon'>
				<view class='company acea-row row-between-wrapper'>
					<view class='picTxt acea-row row-between-wrapper'>
						<view class='iconfont icon-wuliu'></view>
						<view class='text'>
							<view><text class='name line1'>物流公司：</text> {{orderInfo.deliveryName}}</view>
							<view class='express line1'><text class='name'>快递单号：</text> {{orderInfo.deliveryId}}</view>
						</view>
					</view>
					<!-- #ifndef H5 -->
					<view class='copy' @tap='copyOrderId'>复制单号</view>
					<!-- #endif -->
					<!-- #ifdef H5 -->
					<view class='copy copy-data' :data-clipboard-text="orderInfo.deliveryId">复制单号</view>
					<!-- #endif -->
				</view>
				<view class='item' v-for="(item,index) in expressList" :key="index">
					<view class='circular' :class='index === 0 ? "on":""'></view>
					<view class='text' :class='index===0 ? "on-font on":""'>
						<view>{{item.status}}</view>
						<view class='data' :class='index===0 ? "on-font on":""'>{{item.time}}</view>
					</view>
				</view>
			</view>
			<recommend ref="recommendIndex" v-if="expressList.length== 0 && isloading"></recommend>
		</view>
	</view>
</template>

<script setup>
import { ref, watch, nextTick, getCurrentInstance } from "vue";
import { onLoad, onReady, onReachBottom } from "@dcloudio/uni-app";
import { express } from "@/api/order.js";
import ClipboardJS from "@/plugin/clipboard/clipboard.js";
import { toLogin } from "@/libs/login.js";
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from "pinia";
import util from "@/utils/util.js";
import recommend from "@/components/recommend/index.vue";

const { proxy } = getCurrentInstance();
const appStore = useAppStore();
const { isLogin } = storeToRefs(appStore);

const orderId = ref("");
const product = ref({ productInfo: {} });
const orderInfo = ref({});
const expressList = ref([]);
const loading = ref(false);
const isloading = ref(false);
const recommendIndex = ref(null);
const params = ref({ page: 1 });

watch(isLogin, (newV) => { if (newV) getExpress(); }, { deep: true });

onLoad((options) => {
	if (!options.orderId) return util.Tips({ title: '缺少订单号' });
	orderId.value = options.orderId;
	if (isLogin.value) getExpress();
	else toLogin();
});

onReady(() => {
	// #ifdef H5
	nextTick(() => {
		const clipboard = new ClipboardJS(".copy-data");
		clipboard.on("success", () => { util.Tips({ title: '复制成功' }); });
	});
	// #endif
});

onReachBottom(() => {
	if (params.value.page != 1) recommendIndex.value.get_host_product();
});

function copyOrderId() {
	uni.setClipboardData({ data: orderInfo.value.deliveryId });
}

function getExpress() {
	isloading.value = false;
	express(orderId.value).then(function(res) {
		let result = res.data.express || {};
		product.value = res.data.order.info[0] || {};
		orderInfo.value = res.data.order;
		expressList.value = result.list || [];
		isloading.value = true;
	}).catch(e => {
		isloading.value = false;
		util.Tips({ title: e });
	});
}
</script>

<style scoped lang="scss">
	.logistics .header {
		padding: 23rpx 30rpx;
		background-color: #fff;
		height: 166rpx;
		box-sizing: border-box;
	}

	.logistics .header .pictrue {
		width: 120rpx;
		height: 120rpx;
	}

	.logistics .header .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 6rpx;
	}

	.logistics .header .text {
		width: 540rpx;
		font-size: 28rpx;
		color: #999;
		margin-top: 6rpx;
	}

	.logistics .header .text .name {
		width: 365rpx;
		color: #282828;
	}

	.logistics .header .text .money {
		text-align: right;
	}

	.logistics .logisticsCon {
		background-color: #fff;
		margin: 12rpx 0;
	}

	.logistics .logisticsCon .company {
		height: 120rpx;
		margin: 0 0 45rpx 30rpx;
		padding-right: 30rpx;
		border-bottom: 1rpx solid #f5f5f5;
	}

	.logistics .logisticsCon .company .picTxt {
		width: 520rpx;
	}

	.logistics .logisticsCon .company .picTxt .iconfont {
		width: 50rpx;
		height: 50rpx;
		background-color: #666;
		text-align: center;
		line-height: 50rpx;
		color: #fff;
		font-size: 35rpx;
	}

	.logistics .logisticsCon .company .picTxt .text {
		width: 450rpx;
		font-size: 26rpx;
		color: #282828;
	}

	.logistics .logisticsCon .company .picTxt .text .name {
		color: #999;
	}

	.logistics .logisticsCon .company .picTxt .text .express {
		margin-top: 5rpx;
	}

	.logistics .logisticsCon .company .copy {
		font-size: 20rpx;
		width: 106rpx;
		height: 40rpx;
		text-align: center;
		line-height: 40rpx;
		border-radius: 20rpx;
		border: 1rpx solid #999;
	}

	.logistics .logisticsCon .item {
		padding: 0 40rpx;
		position: relative;
	}

	.logistics .logisticsCon .item .circular {
		width: 20rpx;
		height: 20rpx;
		border-radius: 50%;
		position: absolute;
		top: -1rpx;
		left: 31.5rpx;
		background-color: #ddd;
	}

	.logistics .logisticsCon .item .circular.on {
		background-color: $theme-color;
	}

	.logistics .logisticsCon .item .text.on-font {
		color: $theme-color;
	}

	.logistics .logisticsCon .item .text .data.on-font {
		color: $theme-color;
	}

	.logistics .logisticsCon .item .text {
		font-size: 26rpx;
		color: #666;
		width: 615rpx;
		border-left: 1rpx solid #e6e6e6;
		padding: 0 0 60rpx 38rpx;
	}

	.logistics .logisticsCon .item .text.on {
		border-left-color: #f8c1bd;
	}

	.logistics .logisticsCon .item .text .data {
		font-size: 24rpx;
		color: #999;
		margin-top: 10rpx;
	}

	.logistics .logisticsCon .item .text .data .time {
		margin-left: 15rpx;
	}
</style>
