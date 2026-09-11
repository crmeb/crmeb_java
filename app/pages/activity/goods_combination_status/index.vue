<template>
	<div :data-theme="theme" :style="colorStyle">
	<div class="group-con">
		<div v-if="storeCombination" class="header acea-row row-between-wrapper">
			<div class="pictrue"><image :src="storeCombination.image" /></div>
			<div class="text">
				<div class="line1" v-text="storeCombination.title"></div>
				<div class="money font_price">
					￥
					<span class="num" v-text="storeCombination.price || 0"></span>
					<span class="team cart-color">{{storeCombination.people?storeCombination.people:0 }}人拼</span>
				</div>
			</div>
			<div v-if="pinkBool === -1" class="iconfont icon-pintuanshibai"></div>
			<div v-else-if="pinkBool === 1" class="iconfont icon-pintuanchenggong font-color-red"></div>
		</div>
		<div class="wrapper">
			<div class="title acea-row row-center-wrapper" v-if="pinkBool === 0">
				<div class="line"></div>
				<div class="name acea-row row-center-wrapper">
					剩余
					<CountDown :bgColor="bgColor" :is-day="false" :tip-text="' '" :day-text="' '" :hour-text="' : '"
						:minute-text="' : '" :second-text="' '" :datatime="pinkT.stopTime/1000"></CountDown>
					<span class="end">结束</span>
				</div>
				<div class="line"></div>
			</div>
			<div class="tips font-color-red" v-if="pinkBool === 1">恭喜您拼团成功</div>
			<div class="tips" v-else-if="pinkBool === -1">还差{{ count }}人，拼团失败</div>
			<div class="tips font-color-red" v-else-if="pinkBool === 0">拼团中，还差{{ count }}人拼团成功</div>
			<div class="list acea-row row-middle"
				:class="[pinkBool === 1 || pinkBool === -1 ? 'result' : '', iShidden ? 'on' : '']">
				<div class="pinkT">
					<div class="pictrue"><image :src="pinkT.avatar" /></div>
					<div class="chief">团长</div>
				</div>
				<block v-if="pinkAll.length > 0">
					<div class="pictrue" v-for="(item, index) in pinkAll" :key="index"><image :src="item.avatar" /></div>
				</block>
				<div class="pictrue" v-for="index in count" :key="index">
					<image class="img-none" :src="urlDomain+'/crmebimage/perset/activityImg/vacancy.png'" />
				</div>
			</div>
			<div v-if="(pinkBool === 1 || pinkBool === -1) && count > 9" class="lookAll acea-row row-center-wrapper"
				@click="lookAll">
				{{ iShidden ? '收起' : '查看全部' }}
				<span class="iconfont" :class="iShidden ? 'icon-xiangshang' : 'icon-xiangxia'"></span>
			</div>
			<!-- #ifndef MP -->
			<div v-if="userBool === 1 && isOk == 0 && pinkBool === 0">
				<div class="teamBnt bg-color-red" v-if='pinkT.stopTime>timestamp' @click="goPoster">邀请好友参团</div>
			</div>
			<!-- #endif -->
			<!-- #ifdef MP -->
			<button open-type="share" class="teamBnt bg-color-red"
				v-if="userBool === 1 && isOk == 0 && pinkBool === 0 && pinkT.stopTime>timestamp">邀请好友参团</button>
			<!-- #endif -->
			<div class="teamBnt bg-color-hui" v-if="pinkT.stopTime<timestamp && isOk == 0 && pinkBool === 0">拼团已过期</div>
			<div class="teamBnt bg-color-red"
				v-else-if="userBool === 0 && pinkBool === 0 && count > 0 && pinkT.stopTime>timestamp" @click="pay">我要参团
			</div>
			<div class="teamBnt bg-color-red" v-if="pinkBool === 1 || pinkBool === -1"
				@click="goDetail(storeCombination.id)">再次开团</div>
			<div class="cancel" @click="getCombinationRemove" v-if="pinkBool === 0 && userBool === 1">
				<span class="iconfont icon-guanbi3"></span>
				取消开团
			</div>

			<div class="lookOrder" v-if="pinkBool === 1" @click="goOrder">
				查看订单信息
				<span class="iconfont icon-xiangyou"></span>
			</div>
		</div>
		<div class="group-recommend">
			<div class="title acea-row row-between-wrapper">
				<div>大家都在拼</div>
				<div class="more" @click="goList">
					更多拼团
					<span class="iconfont icon-jiantou"></span>
				</div>
			</div>
			<div class="list acea-row row-middle">
				<div class="item" v-for="(item, index) in storeCombinationHost" :key="index" @click="goDetail(item.id)">
					<div class="pictrue">
						<image :src="item.image" />
						<div class="team" v-text="item.people + '人团'"></div>
					</div>
					<div class="name line1" v-text="item.title"></div>
					<div class="money font_price" v-text="'￥' + item.price"></div>
				</div>
			</div>
		</div>
		<product-window :attr="attr" :onceNum="onceNum" :limitNum="1" :iSbnt="1" @myevent="onMyEvent" @ChangeAttr="ChangeAttr"
			@ChangeCartNum="ChangeCartNum" @iptCartNum="iptCartNum" @attrVal="attrVal" @goCat="goPay"></product-window>
		<!-- 分享按钮 -->
		<view class="generate-posters acea-row row-middle" :class="posters ? 'on' : ''"> 
			<!-- #ifdef APP-PLUS -->
			<view class="item" @click="appShare('WXSceneSession')">
				<view class="iconfont icon-weixin3"></view>
				<view class="">微信好友</view>
			</view>
			<view class="item" @click="appShare('WXSenceTimeline')">
				<view class="iconfont icon-pengyouquan"></view>
				<view class="">微信朋友圈</view>
			</view>
			<!-- #endif -->
		</view>
		<view class="mask" v-if="posters || canvasStatus" @click="listenerActionClose"></view>
		<!-- 发送给朋友图片 -->
		<view class="share-box" v-if="H5ShareBox">
			<image :src="urlDomain+'/crmebimage/perset/staticImg/share-info.png'" @click="H5ShareBox = false"></image>
		</view>
		<!-- 海报展示 -->
		<view class='poster-pop' v-if="canvasStatus">
			<image :src='imagePath'></image>
			<!-- #ifndef H5  -->
			<view class='save-poster' @click="savePosterPath">保存到手机</view>
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<view class="keep">长按图片保存至相册</view>
			<view class='iconfont icon-cha2 close' @tap='listenerActionClose'></view>
			<!-- #endif -->
		</view>
		<view class="canvas">
			<canvas style="width:597px;height:850px;" canvas-id="activityCanvas"></canvas>
			<canvas canvas-id="qrcode" :style="{width: `${qrcodeSize}px`, height: `${qrcodeSize}px`}"
				style="opacity: 0;" />
		</view>
		</div>
	</div>
</template>
<script setup>
import img____static_vacancy_png_src from '../static/vacancy.png';
import img____images_bargain_post1_png_src from '../images/bargain_post1.png';
import img____images_bargain_post2_png_src from '../images/bargain_post2.png';
import img____images_bargain_post3_png_src from '../images/bargain_post3.png';
import img____images_bargain_post4_png_src from '../images/bargain_post4.png';
import img____images_bargain_post5_png_src from '../images/bargain_post5.png';
import { ref, reactive, watch, getCurrentInstance } from "vue";
import { onLoad, onShareAppMessage, onReachBottom } from "@dcloudio/uni-app";
import CountDown from "@/pages/activity/components/countDown/index.vue";
import ProductWindow from "@/components/productWindow/index.vue";
import uQRCode from "@/js_sdk/Sansnn-uQRCode/uqrcode.js";
import { imageBase64 } from "@/api/public.js";
import { toLogin } from "@/libs/login.js";
import { getCombinationPink, postCombinationRemove, getCombinationMore } from "@/api/activity.js";
import { postCartAdd } from "@/api/store.js";
// #ifdef APP-PLUS
import { HTTP_H5_URL } from "@/config/app.js";
// #endif
import { silenceBindingSpread } from "@/utils";
import util from "@/utils/util.js";
import Cache from "@/utils/cache.js";
import * as Order from "@/libs/order.js";
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from "pinia";
import { useColor } from '@/composables/useColor.js';

const { proxy } = getCurrentInstance();
const app = getApp();
const appStore = useAppStore();
const { isLogin, userInfo: userData, uid } = storeToRefs(appStore);

// data
const urlDomain = ref(Cache.get("imgHost"));
const bgColor = ref({ bgColor: '#333333', Color: '#fff', width: '44rpx', timeTxtwidth: '16rpx', isDay: true });
const currentPinkOrder = ref('');
const isOk = ref(0);
const pinkBool = ref(0);
const userBool = ref(0);
const pinkAll = ref([]);
const pinkT = ref([]);
const storeCombination = ref(null);
const storeCombinationHost = ref([]);
const pinkId = ref(0);
const count = ref(0);
const iShidden = ref(false);
const isOpen = ref(false);
const attr = reactive({
	cartAttr: false,
	productSelect: {
		image: '', storeName: '', price: '', quota: 0, unique: '', cart_num: 1, quotaShow: 0, stock: 0, num: 0
	},
	attrValue: '',
	productAttr: []
});
const cart_num = ref('');
const limit = ref(10);
const page = ref(1);
const loading = ref(false);
const loadend = ref(false);
const localUserInfo = ref({});
const posters = ref(false);
const H5ShareBox = ref(false);
const isAuto = ref(false);
const isShowAuth = ref(false);
const onceNum = ref(0);
const timestamp = ref(0);
const qrcodeSize = ref(600);
const posterbackgd = ref('/static/images/canbj.png');
const PromotionCode = ref('');
const canvasStatus = ref(false);
const imgTop = ref('');
const imagePath = ref('');
const theme = ref(app.globalData.theme);
const { colorStyle } = useColor();
const vacancyPic = ref(img____static_vacancy_png_src);
const openPages = ref('');
const productValue = ref({});

watch(isLogin, (newV) => {
	if (newV) {
		getCombinationPinkFn();
		if (parseInt(app.globalData.spread) > 0) silenceBindingSpread();
	}
}, { deep: true });

onLoad((options) => {
	loadend.value = false;
	loading.value = false;
	combinationMore();
	theme.value = Cache.get('theme');
	pinkId.value = options.id;
	if (options.spread) app.globalData.spread = options.spread;
	switch (theme.value) {
		case 'theme1': posterbackgd.value = img____images_bargain_post1_png_src; break;
		case 'theme2': posterbackgd.value = img____images_bargain_post2_png_src; break;
		case 'theme3': posterbackgd.value = img____images_bargain_post3_png_src; break;
		case 'theme4': posterbackgd.value = img____images_bargain_post4_png_src; break;
		case 'theme5': posterbackgd.value = img____images_bargain_post5_png_src; break;
	}
	if (isLogin.value) {
		timestamp.value = (new Date()).getTime();
		getCombinationPinkFn();
		if (parseInt(app.globalData.spread) > 0) silenceBindingSpread();
	} else {
		Cache.set('login_back_url', `/pages/activity/goods_combination_status/index?id=${options.id}&spread=${app.globalData.spread ? app.globalData.spread : 0}`);
		toLogin();
	}
});

//#ifdef MP
onShareAppMessage(() => {
	return {
		title: '您的好友' + userData.value.nickname + '邀请您参团' + storeCombination.value.title,
		path: openPages.value,
		imageUrl: storeCombination.value.image
	};
});
//#endif

// #ifdef APP-PLUS
function appShare(scene) {
	let routes = getCurrentPages();
	let curRoute = routes[routes.length - 1].$page.fullPath;
	uni.share({
		provider: "weixin", scene, type: 0, href: `${HTTP_H5_URL}${curRoute}`,
		title: '您的好友' + userData.value.nickname + '邀请您参团' + storeCombination.value.title,
		imageUrl: storeCombination.value.image,
		success: function() { posters.value = false; },
		fail: function() { uni.showToast({ title: '分享失败', icon: 'none', duration: 2000 }); posters.value = false; }
	});
}
// #endif

function listenerActionClose() { posters.value = false; canvasStatus.value = false; }

function combinationMore() {
	if (loadend.value) return;
	if (loading.value) return;
	var data = { page: page.value, limit: limit.value, comId: pinkId.value };
	loading.value = true;
	getCombinationMore(data).then(res => {
		var sch = storeCombinationHost.value;
		var lim = limit.value;
		page.value++;
		loadend.value = lim > res.data.length;
		storeCombinationHost.value = sch.concat(res.data.list);
		page.value = data.page;
		loading.value = false;
	}).catch(res => {
		loading.value = false;
		util.Tips({ title: res });
	});
}

function iptCartNum(e) {
	if (e > onceNum.value) {
		util.Tips({ title: `该商品每次限购${onceNum.value}${storeCombination.value.unitName}` });
		attr.productSelect.cart_num = onceNum.value;
		cart_num.value = onceNum.value;
	} else {
		attr.productSelect.cart_num = e;
		cart_num.value = e;
	}
}

function attrVal(val) {
	attr.productAttr[val.indexw].index = attr.productAttr[val.indexw].attrValues[val.indexn];
}

function onMyEvent() {
	attr.cartAttr = false;
	isOpen.value = false;
}

function ChangeAttr(res) {
	cart_num.value = 1;
	let productSelect = productValue.value[res];
	if (productSelect) {
		attr.productSelect.image = productSelect.image;
		attr.productSelect.price = productSelect.price;
		attr.productSelect.quota = productSelect.quota;
		attr.productSelect.unique = productSelect.id;
		attr.productSelect.cart_num = 1;
		attr.productSelect.stock = productSelect.stock;
		attr.productSelect.quotaShow = productSelect.quotaShow;
		attr.attrValue = res;
	} else {
		attr.productSelect.image = storeCombination.value.image;
		attr.productSelect.price = storeCombination.value.price;
		attr.productSelect.quota = 0;
		attr.productSelect.unique = '';
		attr.productSelect.cart_num = 0;
		attr.productSelect.quotaShow = 0;
		attr.productSelect.stock = 0;
		attr.attrValue = '';
	}
}

function ChangeCartNum(res) {
	let productSelect = productValue.value[attr.attrValue];
	if (onceNum.value === productSelect.cart_num) {
		return util.Tips({ title: `该商品每次限购${onceNum.value}${storeCombination.value.unitName}` });
	}
	if (cart_num.value) {
		productSelect.cart_num = cart_num.value;
		attr.productSelect.cart_num = cart_num.value;
	}
	if (productSelect === undefined && !attr.productAttr.length) productSelect = attr.productSelect;
	if (productSelect === undefined) return;
	let stock = productSelect.stock || 0;
	let quotaShow = productSelect.quotaShow || 0;
	let quota = productSelect.quota || 0;
	let num = attr.productSelect;
	let nums = storeCombination.value.num || 0;
	if (productSelect.cart_num == undefined) productSelect.cart_num = 1;
	if (res) {
		num.cart_num++;
		let arrMin = [nums, quota, stock];
		let minN = Math.min.apply(null, arrMin);
		if (num.cart_num >= minN) {
			attr.productSelect.cart_num = minN ? minN : 1;
			cart_num.value = minN ? minN : 1;
		}
		cart_num.value = num.cart_num;
		attr.productSelect.cart_num = num.cart_num;
	} else {
		num.cart_num--;
		if (num.cart_num < 1) {
			attr.productSelect.cart_num = 1;
			cart_num.value = 1;
		}
		cart_num.value = num.cart_num;
		attr.productSelect.cart_num = num.cart_num;
	}
}

function DefaultSelect() {
	let productAttr = attr.productAttr;
	let value = [];
	const sortedArray = Object.entries(productValue.value)
		.sort(([, a], [, b]) => a.id - b.id)
		.map(([key, val]) => ({ key, ...val }));
	for (let i = 0; i < sortedArray.length; i++) {
		const attrItem = sortedArray[i];
		if (attrItem.stock > 0 && attrItem.isShow) {
			if (value.length == 0) value = productAttr.length ? attrItem.key.split(",") : [];
			if (attrItem.isDefault) { value = productAttr.length ? attrItem.key.split(",") : []; break; }
		}
	}
	for (let i = 0; i < productAttr.length; i++) productAttr[i].index = value[i];
	let productSelect = productValue.value[value.join(',')];
	if (productSelect && productAttr.length) {
		attr.productSelect.storeName = storeCombination.value.title;
		attr.productSelect.image = productSelect.image;
		attr.productSelect.price = productSelect.price;
		attr.productSelect.quota = productSelect.quota;
		attr.productSelect.unique = productSelect.id;
		attr.productSelect.cart_num = 1;
		attr.productSelect.stock = productSelect.stock;
		attr.productSelect.quotaShow = productSelect.quotaShow;
		attr.attrValue = value.join(',');
	} else if (!productSelect && productAttr.length) {
		attr.productSelect.storeName = storeCombination.value.title;
		attr.productSelect.image = storeCombination.value.image;
		attr.productSelect.price = storeCombination.value.price;
		attr.productSelect.quota = 0;
		attr.productSelect.unique = '';
		attr.productSelect.cart_num = 0;
		attr.productSelect.stock = 0;
		attr.productSelect.quotaShow = 0;
		attr.attrValue = '';
	} else if (!productSelect && !productAttr.length) {
		attr.productSelect.storeName = storeCombination.value.title;
		attr.productSelect.image = storeCombination.value.image;
		attr.productSelect.price = storeCombination.value.price;
		attr.productSelect.quota = 0;
		attr.productSelect.unique = storeCombination.value.id || '';
		attr.productSelect.cart_num = 1;
		attr.productSelect.quotaShow = 0;
		attr.productSelect.stock = 0;
		attr.attrValue = '';
	}
}

function setProductSelect() {
	attr.productSelect.image = storeCombination.value.image;
	attr.productSelect.storeName = storeCombination.value.title;
	attr.productSelect.price = storeCombination.value.price;
	attr.productSelect.quota = 0;
	attr.productSelect.quotaShow = 0;
	attr.productSelect.stock = 0;
	attr.cartAttr = false;
}

function pay() { attr.cartAttr = true; isOpen.value = true; }

function goPay() {
	Order.getPreOrder("buyNow", [{
		"attrValueId": parseFloat(attr.productSelect.unique),
		"combinationId": parseFloat(storeCombination.value.id),
		"productNum": parseFloat(attr.productSelect.cart_num),
		"productId": parseFloat(storeCombination.value.productId),
		"pinkId": parseFloat(pinkId.value)
	}]);
}

function goPoster() {
	//#ifdef H5
	if (proxy.$wechat.isWeixin()) {
		H5ShareBox.value = true;
	} else {
		uni.showLoading({ title: '海报生成中', mask: true });
		posters.value = false;
		if (!PromotionCode.value) {
			uni.hideLoading();
			util.Tips({ title: '海报二维码生成失败' });
			return;
		}
		setTimeout(() => {
			if (!imgTop.value) {
				uni.hideLoading();
				util.Tips({ title: '无法生成商品海报！' });
				return;
			}
		}, 1000);
		uni.downloadFile({
			url: imgTop.value,
			success: (res) => {
				let arrImagesUrlTop = res.tempFilePath;
				let arrImages = [posterbackgd.value, arrImagesUrlTop, PromotionCode.value];
				setTimeout(() => {
					util.activityCanvas(arrImages, storeCombination.value.title, storeCombination.value.price,
						storeCombination.value.people + '人团', '还差' + count.value + '人拼团成功', 9,
						(tempFilePath) => { imagePath.value = tempFilePath; canvasStatus.value = true; uni.hideLoading(); });
				}, 500);
			}
		});
	}
	//#endif
	//#ifdef APP-PLUS
	posters.value = true;
	//#endif
}

function goOrder() { uni.navigateTo({ url: '/pages/order/order_details/index?order_id=' + currentPinkOrder.value }); }
function goList() { uni.navigateTo({ url: '/pages/activity/goods_combination/index' }); }
function goDetail(id) { pinkId.value = id; uni.navigateTo({ url: '/pages/activity/goods_combination_details/index?id=' + id }); }
function getImageBase64Fn(images) { imageBase64({ url: images }).then(res => { imgTop.value = res.data.code; }); }

function make() {
	let href = location.protocol + '//' + location.host + '/pages/activity/goods_combination_status/index?id=' + pinkId.value + "&spread=" + uid.value;
	uQRCode.make({
		canvasId: 'qrcode', text: href, size: qrcodeSize.value, margin: 10,
		success: res => { PromotionCode.value = res; },
		complete: () => {},
		fail: () => { util.Tips({ title: '海报二维码生成失败！' }); }
	});
}

function getCombinationPinkFn() {
	getCombinationPink(pinkId.value).then(res => {
		openPages.value = '/pages/activity/goods_combination_status/index?id=' + pinkId.value + "&spread=" + uid.value;
		let sc = res.data.storeCombination;
		res.data.pinkT.stop_time = parseInt(res.data.pinkT.stopTime);
		storeCombination.value = sc;
		attr.productSelect.num = sc.totalNum;
		pinkT.value = res.data.pinkT;
		pinkAll.value = res.data.pinkAll;
		count.value = res.data.count;
		userBool.value = res.data.userBool;
		pinkBool.value = res.data.pinkBool;
		isOk.value = res.data.isOk;
		currentPinkOrder.value = res.data.currentPinkOrder;
		localUserInfo.value = res.data.userInfo;
		onceNum.value = sc.onceNum;
		attr.productAttr = sc.productAttr;
		attr.productAttr.forEach(item => {
			if (!item.optionList) { item.optionList = []; item.isShowImage = false; }
		});
		productValue.value = sc.productValue;
		//#ifdef H5
		getImageBase64Fn(sc.image);
		make();
		setOpenShare();
		//#endif
		setProductSelect();
		if (attr.productAttr != 0) DefaultSelect();
	}).catch(err => {
		if (isLogin.value) util.Tips({ title: err }, { url: '/pages/index/index' });
	});
}

//#ifdef H5
function setOpenShare() {
	let configTimeline = {
		title: '您的好友' + userData.value.nickname + '邀请您参团' + storeCombination.value.title,
		desc: storeCombination.value.title,
		link: window.location.protocol + '//' + window.location.host + '/pages/activity/goods_combination_status/index?id=' + pinkId.value + "&spread=" + uid.value,
		imgUrl: storeCombination.value.image
	};
	if (proxy.$wechat.isWeixin()) {
		proxy.$wechat.wechatEvevt(['updateAppMessageShareData', 'updateTimelineShareData', 'onMenuShareAppMessage', 'onMenuShareTimeline'], configTimeline)
			.then(() => {})
			.catch(res => {
				if (res.is_ready) {
					res.wx.updateAppMessageShareData(configTimeline);
					res.wx.updateTimelineShareData(configTimeline);
					res.wx.onMenuShareAppMessage(configTimeline);
					res.wx.onMenuShareTimeline(configTimeline);
				}
			});
	}
}
//#endif

function getCombinationRemove() {
	postCombinationRemove({ id: pinkId.value, cid: storeCombination.value.id })
		.then(res => { util.Tips({ title: res.msg }, { tab: 3 }); })
		.catch(res => { util.Tips({ title: res }); });
}

function lookAll() { iShidden.value = !iShidden.value; }
</script>
<style lang="scss" scoped>
	.generate-posters {
		width: 100%;
		height: 170rpx;
		background-color: #fff;
		position: fixed;
		left: 0;
		bottom: 0;
		z-index: 300;
		transform: translate3d(0, 100%, 0);
		transition: all 0.3s cubic-bezier(0.25, 0.5, 0.5, 0.9);
		border-top: 1rpx solid #eee;
	}
	
	.generate-posters.on {
		transform: translate3d(0, 0, 0);
	}
	
	.generate-posters .item {
		flex: 1;
		text-align: center;
		font-size: 30rpx;
	}
	
	.generate-posters .item .iconfont {
		font-size: 80rpx;
		color: #5eae72;
	}
	
	.generate-posters .item .iconfont.icon-haibao {
		color: #5391f1;
	}
	.pinkT {
		position: relative;

		.chief {
			position: absolute;
			width: 72rpx;
			height: 30rpx;
			@include main_bg_color(theme);
			border-radius: 15rpx;
			font-size: 20rpx;
			line-height: 30rpx;
			text-align: center;
			right: -24rpx;
			top: -16rpx;
			color: #fff;
		}
	}
	.bg-color-red{
		@include main_bg_color(theme);
	}
	.canvas {
		position: fixed;
		opacity: 0;
	}
	.poster-pop {
		width: 594rpx;
		height: 850rpx;
		position: fixed;
		left: 50%;
		transform: translateX(-50%);
		z-index: 999;
		top: 50%;
		margin-top: -466rpx;
	
		image {
			width: 100%;
			height: 100%;
			display: block;
			border-radius: 10rpx;
		}
	
		.close {
			text-align: center;
			margin-top: 55rpx;
			color: #fff;
			font-size: 52rpx;
		}
	
		.save-poster {
			background-color: #df2d0a;
			font-size: ：22rpx;
			color: #fff;
			text-align: center;
			height: 76rpx;
			line-height: 76rpx;
			width: 100%;
		}
	
		.keep {
			color: #fff;
			text-align: center;
			font-size: 25rpx;
			margin-top: 25rpx;
		}
	}
	
	/*开团*/
	.group-con .header {
		height: 186rpx;
		background-color: #fff;
		border-top: 1px solid #f5f5f5;
		margin: 20rpx 30rpx 0;
		border-radius: 14rpx;
		position: relative;
	}

	.group-con .header .iconfont {
		font-size: 100rpx;
		position: absolute;
		color: #ccc;
		right: 33rpx;
		bottom: 20rpx;
	}

	.group-con .header .pictrue {
		width: 140rpx;
		height: 140rpx;
	}

	.group-con .header .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 6rpx;
	}

	.group-con .header .text {
		width: 540rpx;
		font-size: 30rpx;
		color: #222;
	}

	.group-con .header .text .money {
		font-size: 24rpx;
		font-weight: bold;
		margin-top: 15rpx;
	}

	.group-con .header .text .money .num {
		font-size: 32rpx;
	}

	.group-con .header .text .money .team {
		padding: 1rpx 10rpx;
		font-weight: normal;
		border-radius: 50rpx;
		font-size: 20rpx;
		vertical-align: 4rpx;
		margin-left: 15rpx;
		@include main_color(theme);
		@include coupons_border_color(theme);
	}

	.group-con .wrapper {
		background-color: #fff;
		margin: 20rpx 30rpx 0;
		border-radius: 14rpx;
		padding-bottom: 20rpx;
	}

	.group-con .wrapper .title {
		margin-top: 30rpx;
		padding-top: 30rpx;
	}

	.group-con .wrapper .title .line {
		width: 136rpx;
		height: 1px;
		background-color: #ddd;
	}

	.group-con .wrapper .title .name {
		margin: 0 45rpx;
		font-size: 28rpx;
		color: #282828;
	}

	.group-con .wrapper .title .name .time {
		margin: 0 14rpx;
	}

	.group-con .wrapper .title .name .timeTxt {
		color: #fc4141;
	}

	.group-con .wrapper .title .name .time .styleAll {
		background-color: #ffcfcb;
		text-align: center;
		border-radius: 3rpx;
		font-size: 28rpx;
		font-weight: bold;
		display: inline-block;
		vertical-align: middle;
		color: #fc4141;
		padding: 2rpx 5rpx;
	}

	.group-con .wrapper .tips {
		font-size: 30rpx;
		font-weight: bold;
		text-align: center;
		margin-top: 30rpx;
		color: #999;
	}

	.group-con .wrapper .list {
		padding: 0 30rpx;
		margin-top: 45rpx;
	}

	.group-con .wrapper .list.result {
		max-height: 240rpx;
	}

	.group-con .wrapper .list.result.on {
		max-height: 2000rpx;
	}

	.group-con .wrapper .list .pictrue {
		width: 94rpx;
		height: 94rpx;
		margin: 0 0 29rpx 35rpx;
	}

	.group-con .wrapper .list .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		@include coupons_border_color(theme);
	}

	.group-con .wrapper .list .pictrue image.img-none {
		border: none;
	}

	.group-con .wrapper .lookAll {
		font-size: 24rpx;
		color: #282828;
		padding-top: 10rpx;
	}

	.group-con .wrapper .lookAll .iconfont {
		font-size: 25rpx;
		margin: 2rpx 0 0 10rpx;
	}

	.group-con .wrapper .teamBnt {
		font-size: 30rpx;
		width: 620rpx;
		height: 86rpx;
		border-radius: 50rpx;
		text-align: center;
		line-height: 86rpx;
		color: #fff;
		margin: 21rpx auto 0 auto;
		
	}

	.group-con .wrapper .cancel,
	.group-con .wrapper .lookOrder {
		text-align: center;
		font-size: 24rpx;
		color: #282828;
		padding-top: 30rpx;
		padding-bottom:30rpx;
	}

	.group-con .wrapper .cancel .iconfont {
		font-size: 35rpx;
		color: #2c2c2c;
		vertical-align: -4rpx;
		margin-right: 9rpx;
	}

	.group-con .wrapper .lookOrder .iconfont {
		font-size: 25rpx;
		color: #2c2c2c;
		margin-left: 10rpx;
	}

	.group-con .group-recommend {
		background-color: #fff;
		margin: 20rpx 30rpx 0;
		border-radius: 14rpx;
	}

	.group-con .group-recommend .title {
		padding-right: 30rpx;
		margin-left: 30rpx;
		height: 85rpx;
		border-bottom: 1px solid #eee;
		font-size: 28rpx;
		color: #282828;
	}

	.group-con .group-recommend .title .more {
		color: #808080;
	}

	.group-con .group-recommend .title .more .iconfont {
		margin-left: 13rpx;
		font-size: 28rpx;
	}

	.group-con .group-recommend .list {
		margin-top: 30rpx;
	}

	.group-con .group-recommend .list .item {
		width: 190rpx;
		margin: 0 0 25rpx 30rpx;
	}

	.group-con .group-recommend .list .item .pictrue {
		width: 100%;
		height: 190rpx;
		position: relative;
	}

	.group-con .group-recommend .list .item .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 10rpx;
	}

	.group-con .group-recommend .list .item .pictrue .team {
		position: absolute;
		top: 28rpx;
		left: -5rpx;
		min-width: 100rpx;
		height: 36rpx;
		line-height: 36rpx;
		text-align: center;
		border-radius: 0 18rpx 18rpx 0;
		font-size: 20rpx;
		color: #fff;
		@include main_bg_color(theme);
	}

	.group-con .group-recommend .list .item .name {
		font-size: 28rpx;
		color: #333;
		margin-top: 0.18rem;
	}

	.group-con .group-recommend .list .item .money {
		font-weight: bold;
		font-size: 28rpx;
	}

	.share-box {
		z-index: 1000;
		position: fixed;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;

		image {
			width: 100%;
			height: 100%;
		}
	}
	.font_price{
		@include price_color(theme);
	}
</style>
