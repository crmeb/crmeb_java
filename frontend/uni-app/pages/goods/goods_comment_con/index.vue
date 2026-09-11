<template>
	<view :data-theme="theme" :style="colorStyle">
		<form @submit="formSubmit" report-submit='true'>
			<view class='evaluate-con pad30'>
				<view class='goodsStyle acea-row row-between borRadius14'>
					<view class='pictrue'>
						<image :src='productInfo.image'></image>
					</view>
					<view class='text acea-row row-between'>
						<view>
							<view class='name line2'>{{productInfo.storeName}}</view>
							<view class='attr line1 font_sm' v-if="productInfo.sku">{{productInfo.sku}}</view>
						</view>
						<view class='money'>
							<view>￥{{productInfo.truePrice}}</view>
							<view class='num'>x{{productInfo.cartNum}}</view>
						</view>
					</view>
				</view>
				<view class='score borRadius14'>
					<view class='item acea-row row-middle' v-for="(item,indexw) in scoreList" :key="indexw">
						<view>{{item.name}}</view>
						<view class='starsList'>
							<text @click="stars(indexn, indexw)" v-for="(itemn, indexn) in item.stars" :key="indexn" class='iconfont' :class="item.index >= indexn? 'icon-shitixing':'icon-kongxinxing'"></text>
						</view>
						<text class='evaluate'>{{item.index === -1 ? "" : item.index + 1 + "分"}}</text>
					</view>
					<view class='textarea'>
						<textarea placeholder='商品满足你的期待么？说说你的想法，分享给想买的他们吧~' name="comment" placeholder-class='placeholder'></textarea>
						<view class='list acea-row row-middle'>
							<view class='pictrue' v-for="(item,index) in picsPath" :key="index">
								<image :src='item'></image>
								<text class='iconfont icon-guanbi1' @click='DelPic(index)'></text>
							</view>
							<view class='pictrue acea-row row-center-wrapper row-column' @click='uploadpic' v-if="picsPath.length < 8">
								<text class='iconfont icon-icon25201'></text>
								<view>上传图片</view>
							</view>
						</view>
					</view>
					<button class='evaluateBnt bg_color' formType="submit">立即评价</button>
				</view>
			</view>
		</form>
	</view>
</template>

<script setup>
import { ref, watch } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { orderProduct, orderComment } from "@/api/order.js";
import { toLogin } from "@/libs/login.js";
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from "pinia";
import util from "@/utils/util.js";
import { useColor } from '@/composables/useColor.js';

const app = getApp();
const appStore = useAppStore();
const { isLogin } = storeToRefs(appStore);

const pics = ref([]);
const picsPath = ref([]);
const scoreList = ref([
	{ name: "商品质量", stars: ["", "", "", "", ""], index: -1 },
	{ name: "服务态度", stars: ["", "", "", "", ""], index: -1 }
]);
const orderId = ref("");
const productId = ref(0);
const evaluateId = ref(0);
const unique = ref("");
const productInfo = ref({ storeName: '', sku: '', truePrice: '', cartNum: '', image: '' });
const cart_num = ref(0);
const id = ref(0);
const theme = ref(app.globalData.theme);
const { colorStyle } = useColor();

watch(isLogin, (newV) => { if (newV) getOrderProduct(); }, { deep: true });

onLoad((options) => {
	if (!options.unique || !options.orderId) return util.Tips({ title: '缺少参数' }, { tab: 3, url: 1 });
	unique.value = Number(options.unique) || 0;
	orderId.value = options.orderId || 0;
	evaluateId.value = Number(options.id) || 0;
	if (isLogin.value) getOrderProduct();
	else toLogin();
});

function getOrderProduct() {
	orderProduct({ orderId: evaluateId.value, uni: unique.value }).then(res => {
		productInfo.value = res.data;
	});
}
function stars(indexn, indexw) { scoreList.value[indexw].index = indexn; }
function DelPic(index) { picsPath.value.splice(index, 1); pics.value.splice(index, 1); }
function uploadpic() {
	util.uploadImageOne({ url: 'upload/image', name: 'multipart', model: "product", pid: 1 }, function(res) {
		pics.value.push(res.data.url);
		picsPath.value.push(res.data.localPath);
	});
}
function formSubmit(e) {
	let value = e.detail.value;
	let product_score = scoreList.value[0].index + 1 === 0 ? "" : scoreList.value[0].index + 1;
	let service_score = scoreList.value[1].index + 1 === 0 ? "" : scoreList.value[1].index + 1;
	if (!value.comment) return util.Tips({ title: '请填写你对宝贝的心得！' });
	value.productScore = product_score;
	value.serviceScore = service_score;
	value.pics = pics.value.length > 0 ? JSON.stringify(pics.value) : '';
	value.productId = productInfo.value.productId;
	value.orderNo = orderId.value;
	value.unique = unique.value;
	value.sku = productInfo.value.sku;
	uni.showLoading({ title: "正在发布评论……" });
	orderComment(value).then(() => {
		uni.hideLoading();
		return util.Tips({ title: '感谢您的评价!', icon: 'success' }, '/pages/order/order_details/index?order_id=' + orderId.value);
	}).catch(err => {
		uni.hideLoading();
		return util.Tips({ title: err });
	});
}
</script>

<style lang="scss" scoped>
	.goodsStyle .text .name, .attr{
		//width: 496rpx;
	}
	.font_sm{
		width: 360rpx;
		font-size: 24rpx;
		color: #999;
		padding-top: 10rpx;
	}
	.icon-shitixing{
		color: #FFBB00 !important;
	}
	.evaluate-con .score {
		background-color: #fff;
		// border-top: 1rpx solid #f5f5f5;
		margin-top: 20rpx;
		font-size: 28rpx;
		color: #282828;
		padding: 46rpx 24rpx;
		color: fffdca63e;
	}

	.evaluate-con .score .item~.item {
		margin-top: 36rpx;
	}

	.evaluate-con .score .item .starsList {
		padding: 0 35rpx 0 40rpx;
	}

	.evaluate-con .score .item .starsList .iconfont {
		font-size: 40rpx;
		color: #aaa;
	}

	.evaluate-con .score .item .starsList .iconfont~.iconfont {
		margin-left: 20rpx;
	}

	.evaluate-con .score .item .evaluate {
		color: #aaa;
		font-size: 24rpx;
	}

	.evaluate-con .score .textarea {
		width: 100%;
		background-color: #F5F5F5;
		border-radius: 14rpx;
		margin-top: 55rpx;
	}

	.evaluate-con .score .textarea textarea {
		font-size: 28rpx;
		padding: 38rpx 30rpx 0 30rpx;
		width: 100%;
		box-sizing: border-box;
		height: 160rpx;
		width: auto !important;
	}

	.evaluate-con .score .textarea .placeholder {
		color: #bbb;
	}

	.evaluate-con .score .textarea .list {
		margin-top: 25rpx;
		padding-left: 5rpx;
	}

	.evaluate-con .score .textarea .list .pictrue {
		width: 140rpx;
		height: 140rpx;
		margin: 0 0 35rpx 25rpx;
		position: relative;
		font-size: 22rpx;
		color: #bbb;
		border-radius: 14rpx;
	}

	.evaluate-con .score .textarea .list .pictrue:nth-last-child(1) {
		border: 1rpx solid #ddd;
		box-sizing: border-box;
	}

	.evaluate-con .score .textarea .list .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 14rpx;
	}

	.evaluate-con .score .textarea .list .pictrue .icon-guanbi1 {
		font-size: 45rpx;
		position: absolute;
		top: -20rpx;
		right: -20rpx;
	}

	.evaluate-con .score .textarea .list .pictrue .icon-icon25201 {
		color: #bfbfbf;
		font-size: 50rpx;
	}

	.evaluate-con .score .evaluateBnt {
		font-size: 30rpx;
		color: #fff;
		width: 100%;
		height: 86rpx;
		border-radius: 43rpx;
		text-align: center;
		line-height: 86rpx;
		margin-top: 45rpx;
	}
	.bg_color{
		@include main_bg_color(theme);
	}
</style>
