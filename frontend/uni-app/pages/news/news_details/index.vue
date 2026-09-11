<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class='newsDetail' :style="{backgroundColor:bgColor}">
			<view class='title'>{{articleInfo.title}}</view>
			<view class='list acea-row row-middle'>
				<view class='label'>{{articleInfo.author}}</view>
				<view class='item'>{{articleInfo.createTime}}</view>
				<view class='item'><text class='iconfont icon-liulan'></text>{{articleInfo.visit}}</view>
			</view>
			<view class='conter'>
				<mp-html :content="content" ref="article" :tag-style="tagStyle"></mp-html>
			</view>
			<view class="picTxt acea-row row-between-wrapper" v-if="store_info.id">
				<view class="pictrue">
					<image :src="store_info.image"></image>
				</view>
				<view class="text">
					<view class="name line1">{{store_info.storeName}}</view>
					<view class="money price_color">
						￥<text class="num">{{store_info.price}}</text>
					</view>
					<view class="y_money">￥{{store_info.otPrice}}</view>
				</view>
				<navigator :render-link="false" :url="'/pages/goods/goods_details/index?id='+store_info.id" hover-class="none" class="label"><text
						class="span">查看商品</text></navigator>
			</view>
			<!-- #ifdef H5 -->
			<button class="bnt bg_color" hover-class='none' @click="listenerActionSheet"
				v-if="isWeixin">和好友一起分享</button>
			<!-- #endif -->
			<!-- #ifdef MP -->
			<button class="bnt bg_color" open-type="share" hover-class='none'>和好友一起分享</button>
			<!-- #endif -->
		</view>
		<shareInfo @setShareInfoStatus="setShareInfoStatus" :shareInfoStatus="shareInfoStatus"></shareInfo>
		<view class="article_theme">

		</view>
	</view>
</template>

<script setup>
	import { ref, getCurrentInstance } from 'vue';
	import { onLoad, onShow, onShareAppMessage } from '@dcloudio/uni-app';
	import {
		getArticleDetails 
	} from '@/api/api.js';
	import {
		getProductDetail
	} from '@/api/store.js';
	import shareInfo from '@/components/shareInfo/index.vue';
	import mpHtml from '@/uni_modules/mp-html/components/mp-html/mp-html.vue';
import { useColor } from '@/composables/useColor.js';
	let app = getApp();

	const { proxy } = getCurrentInstance();

	// data
	const id = ref(0);
	const articleInfo = ref([]);
	const store_info = ref({});
	const content = ref('');
	const shareInfoStatus = ref(false);
	const tagStyle = ref({
		img: 'width:100%;display:block;',
		table: 'width:100%',
		video: 'width:100%'
	});
	const productId = ref(0);
	const theme = ref(app.globalData.theme);
	const { colorStyle } = useColor();
	const bgColor = ref('#ffffff');
	const isWeixin = ref(false);

	// #ifdef H5
	function getWechat() {
		return proxy && proxy.$wechat ? proxy.$wechat : null;
	}

	function checkIsWeixin() {
		const wechat = getWechat();
		return !!(wechat && typeof wechat.isWeixin === 'function' && wechat.isWeixin());
	}

	isWeixin.value = checkIsWeixin();
	// #endif

	/**
	 * 生命周期函数--监听页面加载
	 */
	onLoad((options) => {
		if (options.hasOwnProperty('id')) {
			id.value = options.id;
		} else {
			// #ifndef H5
			uni.navigateBack({
				delta: 1
			});
			// #endif
			// #ifdef H5
		 history.back();
			// #endif
		}
	});
	onShow(() => {
		getArticleOne();
	});
	/**
	 * 用户点击右上角分享
	 */
	// #ifdef MP
	onShareAppMessage(() => {
		return {
			title: articleInfo.value.title,
			imageUrl: articleInfo.value.imageInput.length ? articleInfo.value.imageInput : "",
			desc: articleInfo.value.synopsis,
			path: '/pages/news/news_details/index?id=' + id.value
		};
	});
	// #endif

	function getArticleOne() {
		getArticleDetails({
			id: id.value
		}).then(res => {
			uni.setNavigationBarTitle({
				title: res.data.title.substring(0, 7) + "..."
			});
			articleInfo.value = res.data;
			productId.value = res.data.productId;
			if (res.data.productId) {
				goodInfo(res.data.productId);
			}
			content.value = res.data.content;
			// #ifdef H5
			if (isWeixin.value) {
				setShareInfo();
			}
			// #endif
		});
	}
	function goodInfo(id) {
		getProductDetail(id).then(res => {
			store_info.value = res.data.storeInfo ? res.data.storeInfo : {};
		})
	}
	function listenerActionSheet() {
		shareInfoStatus.value = true
	}
	function setShareInfoStatus() {
		shareInfoStatus.value = false
	}
	// #ifdef H5
	function setShareInfo() {
		const wechat = getWechat();
		if (!wechat) return;
		let href = location.href;
		const imageInput = articleInfo.value.imageInput || [];
		let configAppMessage = {
			desc: articleInfo.value.synopsis,
			title: articleInfo.value.title,
			link: href,
			imgUrl: imageInput.length ? imageInput[0] : ""
		};
		wechat.wechatEvevt(["updateAppMessageShareData", "updateTimelineShareData"], configAppMessage);
	}
	// #endif
	function bgTheme(value){
		bgColor.value = value;
	}
</script>

<style lang="scss" scoped>
	// page {
	// 	background-color: #fff !important;
	// }
	.newsDetail {
		// background-color: #fff;
		// background-color: #D8EFD2;
		// background-color: #F9F2E2;
		// background-color: #D9EBED;
		// background-color: #131313;
		// color: #fff !important;
		padding: 30rpx 0;
	}

	.newsDetail .title {
		padding: 0 30rpx;
		font-size: 34rpx;
		color: #282828;
		font-weight: bold;
		line-height: 1.5;
	}

	.newsDetail .list {
		margin: 28rpx 30rpx 0 30rpx;
		padding-bottom: 25rpx;
	}

	.newsDetail .list .label {
		font-size: 30rpx;
		color: #B1B2B3;
	}

	.newsDetail .list .item {
		margin-left: 27rpx;
		font-size: 30rpx;
		color: #B1B2B3;
	}

	.newsDetail .list .item .iconfont {
		font-size: 28rpx;
		margin-right: 10rpx;
	}

	.newsDetail .list .item .iconfont.icon-shenhezhong {
		font-size: 26rpx;
	}

	.newsDetail .picTxt {
		width: 690rpx;
		height: 200rpx;
		border-radius: 20rpx;
		border: 1px solid #e1e1e1;
		position: relative;
		margin: 30rpx auto 0 auto;
	}

	.newsDetail .picTxt .pictrue {
		width: 200rpx;
		height: 200rpx;
	}

	.newsDetail .picTxt .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 20rpx 0 0 20rpx;
		display: block;
	}

	.newsDetail .picTxt .text {
		width: 460rpx;
	}

	.newsDetail .picTxt .text .name {
		font-size: 30rpx;
		color: #282828;
	}

	.newsDetail .picTxt .text .money {
		font-size: 24rpx;
		margin-top: 40rpx;
		font-weight: bold;
	}

	.price_color {
		@include price_color(theme);
	}

	.newsDetail .picTxt .text .money .num {
		font-size: 36rpx;
	}

	.newsDetail .picTxt .text .y_money {
		font-size: 26rpx;
		color: #999;
		text-decoration: line-through;
	}

	.newsDetail .picTxt .label {
		position: absolute;
		background-color: #303131;
		width: 160rpx;
		height: 50rpx;
		right: -7rpx;
		border-radius: 25rpx 0 6rpx 25rpx;
		text-align: center;
		line-height: 50rpx;
		bottom: 24rpx;
	}

	.newsDetail .picTxt .label .span {
		background-image: linear-gradient(to right, #fff71e 0%, #f9b513 100%);
		-webkit-background-clip: text;
		-webkit-text-fill-color: transparent;
	}

	.newsDetail .picTxt .label:after {
		content: " ";
		position: absolute;
		width: 0;
		height: 0;
		border-bottom: 8rpx solid #303131;
		border-right: 8rpx solid transparent;
		top: -7rpx;
		right: 0;
	}

	.newsDetail .bnt {
		color: #fff;
		font-size: 30rpx;
		width: 690rpx;
		height: 90rpx;
		border-radius: 45rpx;
		margin: 48rpx auto;
		text-align: center;
		line-height: 90rpx;
	}

	.bg_color {
		@include main-bg_color(theme);
	}
</style>
