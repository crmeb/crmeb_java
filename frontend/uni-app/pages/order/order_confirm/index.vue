<template>
	<view :data-theme="theme" :style="colorStyle">
		<!-- #ifdef MP -->
		<view class="mp-nav-bar" :style="{ height: mpNavHeight + 'rpx' }">
			<view class="mp-nav-bar__content">
				<view class="mp-nav-bar__back iconfont icon-fanhui2" @tap="goBack"></view>
				<view class="mp-nav-bar__title">{{ navTitle }}</view>
				<view class="mp-nav-bar__placeholder"></view>
			</view>
		</view>
		<view class="mp-nav-placeholder" :style="{ height: mpNavHeight + 'rpx' }"></view>
		<!-- #endif -->

		<view class='order-submission'>
			<view class="allAddress" :style="store_self_mention ? '':'padding-top:0;'">
				<view class="nav acea-row">
					<view class="item font_color" :class="shippingType == 0 ? 'on' : 'on2'" @tap="addressType(0)"
						v-if='store_self_mention'></view>
					<view class="item font_color" :class="shippingType == 1 ? 'on' : 'on2'" @tap="addressType(1)"
						v-if='store_self_mention'></view>
				</view>
				<view class='address acea-row row-between-wrapper' @tap='onAddress' v-if='shippingType == 0'
					:style="store_self_mention ? '':'border-top-left-radius: 14rpx;border-top-right-radius: 14rpx;'">
					<view class='addressCon' v-if="addressInfo.realName">
						<view class='name'>{{addressInfo.realName}}
							<text class='phone'>{{addressInfo.phone}}</text>
						</view>
						<view class="acea-row">
							<text class='default font_color' v-if="addressInfo.isDefault">[默认]</text>
							<text
								class="line2">{{addressInfo.province}}{{addressInfo.city}}{{addressInfo.district}}{{addressInfo.detail}}</text>
						</view>
					</view>
					<view class='addressCon' v-else>
						<view class='setaddress'>设置收货地址</view>
					</view>
					<view class='iconfont icon-jiantou'></view>
				</view>
				<view class='address acea-row row-between-wrapper' v-else @tap="showStoreList">
					<block v-if="storeList.length>0">
						<view class='addressCon'>
							<view class='name'>{{system_store.name}}
								<text class='phone'>{{system_store.phone}}</text>
							</view>
							<view class="line1"> {{system_store.address}}{{", " + system_store.detailedAddress}}
							</view>
						</view>
						<view class='iconfont icon-jiantou'></view>
					</block>
					<block v-else>
						<view>暂无门店信息</view>
					</block>
				</view>
				<view class='line'>
					<image :src="urlDomain+'/crmebimage/perset/staticImg/line.jpg'"></image>
				</view>
			</view>
			<view class="pad30">
				<orderGoods :cartInfo="cartInfo" :orderProNum="orderProNum"></orderGoods>
				<view class='wrapper borRadius14'>
					<view class='item acea-row row-between-wrapper' @tap='couponTap'
						v-if="canUseDiscount">
						<view>优惠券</view>
						<view class='discount'>{{couponTitle}}
							<text class='iconfont icon-jiantou'></text>
						</view>
					</view>
					<view class='item acea-row row-between-wrapper' v-if='shippingType==0'>
						<view>快递费用</view>
						<view class='discount' v-if='parseFloat(orderInfoVo.freightFee) > 0'>
							+￥{{orderInfoVo.freightFee}}
						</view>
						<view class='discount' v-else>免运费</view>
					</view>
					<view v-else>
						<view class="item acea-row row-between-wrapper">
							<view>联系人</view>
							<view class="discount textR">
								<input type="text" placeholder="请填写您的联系姓名" placeholder-style="color:#ccc;"
									placeholder-class="placeholder" @blur='realName' maxlength="20"></input>
							</view>
						</view>
						<view class="item acea-row row-between-wrapper">
							<view>联系电话</view>
							<view class="discount textR">
								<input type="number" placeholder="请填写您的联系电话" placeholder-style="color:#ccc;"
									placeholder-class="placeholder" @blur='phone' maxlength="11"></input>
							</view>
						</view>
					</view>
					<!-- <view class='item acea-row row-between-wrapper' wx:else>
					  <view>自提门店</view>
					  <view class='discount'>{{system_store.name}}</view>
					</view> -->
					<view class='item' v-if="textareaStatus">
						<view class="flex justify-between">
							<view>备注信息</view>
							<view>
								<text style="color:#666;">{{markNum ? markNum : 0}}</text>/<text>150</text>
							</view>
						</view>
						<textarea v-if="coupon.coupon===false" placeholder-class='placeholder' @input='bindHideKeyboard'
							:maxlength="150" value="" name="mark" placeholder='请添加备注（150字以内）'></textarea>
					</view>
				</view>
				<view class='moneyList borRadius14'>
					<view class='item acea-row row-between-wrapper'>
						<view>商品总价：</view>
						<view class='money'>￥{{orderInfoVo.proTotalFee || 0}}</view>
					</view>
					<view class='item acea-row row-between-wrapper' v-if="orderInfoVo.couponFee > 0">
						<view>优惠券抵扣：</view>
						<view class='money'>-￥{{orderInfoVo.couponFee}}</view>
					</view>
					<view class='item acea-row row-between-wrapper' v-if="orderInfoVo.freightFee > 0">
						<view>运费：</view>
						<view class='money'>+￥{{orderInfoVo.freightFee}}</view>
					</view>
				</view>
				<view style='height:120rpx;'></view>
			</view>
			<view class='footer acea-row row-between-wrapper'>
				<view>合计:
					<text class='price_color'>￥{{orderInfoVo.payFee || 0}}</text>
				</view>
				<view class='settlement' style='z-index:100' @tap="SubOrder">提交订单</view>
			</view>
		</view>
		<view class="alipaysubmit" v-html="formContent"></view>
		<couponListWindow :coupon='coupon' @ChangCouponsClone="ChangCouponsClone" :openType='openType'
			@ChangCoupons="ChangCoupons" :orderShow="orderShow"></couponListWindow>
	</view>
</template>
<script setup>
	import { ref, reactive, computed, watch, getCurrentInstance } from 'vue';
	import { onLoad, onShow } from '@dcloudio/uni-app';
	import {
		getCouponsOrderPrice,
		orderCreate,
		postOrderComputed,
		orderPay,
		wechatQueryPayResult,
		loadPreOrderApi
	} from '@/api/order.js';
	import {
		getAddressDetail,
		getAddressDefault
	} from '@/api/user.js';
	import {
		openPaySubscribe,
		openOrderSubscribe
	} from '@/utils/SubscribeMessage.js';
	import {
		storeListApi
	} from '@/api/store.js';
	import {
		CACHE_LONGITUDE,
		CACHE_LATITUDE
	} from '@/config/cache.js';
	import couponListWindow from '@/components/couponListWindow/index.vue';
	import orderGoods from '@/components/orderGoods/index.vue';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		Debounce
	} from '@/utils/validate.js'
	import util from '@/utils/util.js';
	import Cache from '@/utils/cache.js';
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
import { useColor } from '@/composables/useColor.js';
	let app = getApp();

	const { proxy } = getCurrentInstance();
	const appStore = useAppStore();
	const { isLogin, systemPlatform } = storeToRefs(appStore);

	// data
	const urlDomain = ref(Cache.get("imgHost"));
	const navTitle = ref('提交订单');
	// #ifdef MP
	const mpWindowInfo = uni.getWindowInfo();
	const mpWindowWidth = Number(mpWindowInfo.windowWidth || 375);
	const cachedNavHeight = Number(app.globalData.navHeight);
	const mpNavHeight =
		cachedNavHeight > 0
			? cachedNavHeight
			: Number(mpWindowInfo.statusBarHeight || 0) * (750 / mpWindowWidth) + 91;
	// #endif
	const orderShow = ref('orderShow'); //下单页面使用优惠券组件不展示tab切换页
	const textareaStatus = ref(true);
	//支付方式
	const cartArr = ref([{
			"name": "微信支付",
			"icon": "icon-weixin2",
			value: 'weixin',
			title: '微信快捷支付',
			payStatus: 1,
		},
		{
			"name": "余额支付",
			"icon": "icon-yuezhifu",
			value: 'yue',
			title: '可用余额:',
			payStatus: 1,
		},
		// #ifndef MP
		{
			"name": "支付宝支付",
			"icon": "icon-zhifubao",
			value: 'alipay',
			title: '支付宝快捷支付',
			payStatus: 1,
		}
		// #endif
	]);
	const payType = ref('weixin'); //支付方式
	const openType = ref(1); //优惠券打开方式 1=使用
	const active = ref(0); //支付方式切换
	const coupon = reactive({
		coupon: false,
		list: [],
		statusTile: '立即使用'
	}); //优惠券组件
	const addressInfo = ref({}); //地址信息
	const addressId = ref(0); //地址id
	const couponId = ref(0); //优惠券id
	const cartId = ref(''); //购物车id
	const mark = ref(''); //备注信息
	const couponTitle = ref('请选择'); //优惠券
	const coupon_price = ref(0); //优惠券抵扣金额
	const formIds = ref([]); //收集formid
	const status = ref(0);
	const is_address = ref(false);
	const toPay = ref(false); //修复进入支付时页面隐藏从新刷新页面
	const shippingType = ref(0);
	const system_store = ref({});
	const storePostage = ref(0);
	const contacts = ref('');
	const contactsTel = ref('');
	const mydata = ref({});
	const storeList = ref([]);
	const store_self_mention = ref(false);
	const cartInfo = ref([]);
	const animated = ref(false);
	const totalPrice = ref(0);
	const orderKey = ref("");
	// const usableCoupon = ref({});
	const offlinePostage = ref("");
	const payChannel = ref('');
	const news = ref(true);
	const again = ref(false);
	const addAgain = ref(false);
	const bargain = ref(false); //是否是砍价
	const combination = ref(false); //是否是拼团
	const secKill = ref(false); //是否是秒杀
	const orderInfoVo = ref({});
	const addressList = ref([]); //地址列表数据
	const orderProNum = ref(0);
	const preOrderNo = ref(''); //预下单订单号
	const theme = ref(app.globalData.theme);
	const { colorStyle } = useColor();
	const formContent = ref('');
	const addressChangeId = ref(0);
	const orderNo = ref(''); //下单订单号

	// computed
	const markNum = computed(() => {
		if (mark.value) {
			return mark.value.length
		}
	});
	const canUseDiscount = computed(() => {
		const info = orderInfoVo.value;
		return info.isVideo === false && !info.bargainId && !info.combinationId && !info.seckillId;
	});

	watch(isLogin, (newV, oldV) => {
		if (newV) {
			getloadPreOrder();
		}
	}, { deep: true });

	onLoad((options) => {
		// #ifdef H5
		payChannel.value = proxy.$wechat.isWeixin() ? 'public' : 'weixinh5';
		// #endif
		// #ifdef MP
		payChannel.value = 'routine';
		// #endif
		// #ifdef APP-PLUS
		payChannel.value = systemPlatform.value === 'ios' ? 'weixinAppIos' : 'weixinAppAndroid';
		// #endif
		// if (!options.cartId) return util.Tips({
		// 	title: '请选择要购买的商品'
		// }, {
		// 	tab: 3,
		// 	url: 1
		// });
		preOrderNo.value = options.preOrderNo || 0;
		addressChangeId.value = options.addressId || 0;
		is_address.value = options.is_address ? true : false;
		if (isLogin.value) {
			getloadPreOrder();
		} else {
			toLogin();
		}
	});
	/**
	 * 生命周期函数--监听页面显示
	 */
	onShow(() => {
		// wx.getLaunchOptionsSync 
		textareaStatus.value = true;

		uni.$on("handClick", res => {
			if (res) {
				system_store.value = res.address
			}
			// 清除监听
			uni.$off('handClick');
		})
	});

	// #ifdef MP
	function goBack() {
		if (getCurrentPages().length > 1) {
			uni.navigateBack();
			return;
		}

		uni.switchTab({
			url: '/pages/index/index'
		});
	}
	// #endif

	// 订单详情
	function getloadPreOrder() {
		loadPreOrderApi(preOrderNo.value).then(res => {
			let info = res.data.orderInfoVo;
			orderInfoVo.value = info;
			appStore.PRODUCT_TYPE(info.isVideo ? 'video' : 'normal');
			if (info.addressId && addressChangeId.value === 0) {
				addressId.value = info.addressId;
			} else {
				addressId.value = addressChangeId.value;
				if (info.addressId != addressChangeId.value) computedPrice();
			}
			cartInfo.value = info.orderDetailList;
			orderProNum.value = info.orderProNum;
			cartArr.value[1].title = '可用余额:' + info.userBalance;
			cartArr.value[1].payStatus = parseInt(res.data.yuePayStatus) === 1 ? 1 : 2;
			cartArr.value[0].payStatus = parseInt(res.data.payWeixinOpen) === 1 ? 1 : 0;
			getaddressInfo();
			// #ifdef H5
			if (proxy.$wechat.isWeixin()) cartArr.value.pop();
			// #endif
			store_self_mention.value = res.data.storeSelfMention == '1' && info.isVideo === false;
		}).catch(err => {
			uni.navigateTo({
				url: '/pages/users/order_list/index'
			});
		})
	}
	/**
	 * 获取门店列表数据
	 */
	function getList() {
		let longitude = uni.getStorageSync("user_longitude") ? uni.getStorageSync("user_longitude") : ''; //经度
		let latitude = uni.getStorageSync("user_latitude") ? uni.getStorageSync("user_latitude") : ''; //纬度
		let data = {
			latitude: latitude, //纬度
			longitude: longitude, //经度
			page: 1,
			limit: 10
		}
		storeListApi(data).then(res => {
			let list = res.data.list || [];
			storeList.value = list;
			system_store.value = list[0];
		}).catch(err => {
			return util.Tips({
				title: err
			});
		})
	}
	/*
	 * 跳转门店列表
	 */
	function showStoreList() {
		if (storeList.value.length > 0) {
			uni.navigateTo({
				url: '/pages/goods/goods_details_store/index'
			})
		}
	}
	// 计算订单价格
	function computedPrice() {
		postOrderComputed({
			addressId: addressId.value,
			useIntegral: false, // 开源版不支持积分抵扣，后端要求该字段非空
			couponId: couponId.value,
			shippingType: parseInt(shippingType.value) + 1,
			preOrderNo: preOrderNo.value
		}).then(res => {
			let data = res.data;
			orderInfoVo.value.couponFee = data.couponFee;
			orderInfoVo.value.freightFee = data.freightFee;
			orderInfoVo.value.payFee = data.payFee;
			orderInfoVo.value.proTotalFee = data.proTotalFee;
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	}
	//选择地址还是门店自提
	function addressType(e) {
		let index = e;
		shippingType.value = parseInt(index);
		computedPrice();
		//调起获取定位信息
		if (index == 1) {
			getList();
			util.$L.getLocation();
		}
	}
	function bindPickerChange(e) {
		let value = e.detail.value;
		shippingType.value = value;
		computedPrice();
	}
	function ChangCouponsClone() {
		coupon.coupon = false;
	}
	/**
	 * 处理点击优惠券后的事件
	 * 
	 */
	function ChangCoupons(e) {
		// usableCoupon.value = e
		// coupon.coupon = false
		let index = e,
			list = coupon.list,
			title = '请选择',
			id = 0;
		for (let i = 0, len = list.length; i < len; i++) {
			if (i != index) {
				list[i].use_title = '';
				list[i].isUse = 0;
			}
		}
		if (list[index].isUse) {
			//不使用优惠券
			list[index].use_title = '';
			list[index].isUse = 0;
		} else {
			//使用优惠券
			list[index].use_title = '不使用';
			list[index].isUse = 1;
			title = list[index].name;
			id = list[index].id;
		}
		couponTitle.value = title;
		couponId.value = id;
		coupon.coupon = false;
		coupon.list = list;
		computedPrice();
	}
	function bindHideKeyboard(e) {
		mark.value = e.detail.value;
	}
	/**
	 * 获取当前金额可用优惠券
	 * 
	 */
	function getCouponList() {
		getCouponsOrderPrice(preOrderNo.value).then(res => {
			coupon.list = res.data;
			openType.value = 1;
		});
	}
	/*
	 * 获取默认收货地址或者获取某条地址信息
	 */
	function getaddressInfo() {
		if (addressId.value) {

			getAddressDetail(addressId.value).then(res => {
				if (res.data) {
					res.data.isDefault = res.data.isDefault;
					addressInfo.value = res.data || {};
				}
			})
		}
	}
	function payItem(e) {
		let activeVal = e;
		active.value = activeVal;
		animated.value = true;
		payType.value = cartArr.value[activeVal].value;
		// #ifdef H5
		if (payType.value == 'alipay' && proxy.$wechat.isWeixin() == true) {
			payChannel.value = 'public';
		} else if (payType.value == 'alipay' && proxy.$wechat.isWeixin() == false) {
			payChannel.value = 'weixinh5';
		}
		// #endif
		// #ifdef APP-PLUS
		if (payType.value == 'alipay') {
			payChannel.value = 'appAliPay';
		}
		// #endif
		//computedPrice();
		setTimeout(function() {
			car();
		}, 500);
	}
	function couponTap() {
		coupon.coupon = true;
		if (!coupon.list.length) getCouponList();
	}
	function car() {
		animated.value = false;
	}
	function onAddress() {

		uni.redirectTo({
			url: '/pages/users/user_address_list/index?preOrderNo=' + preOrderNo.value
		});
	}
	function realName(e) {
		contacts.value = e.detail.value;
	}
	function phone(e) {
		contactsTel.value = e.detail.value;
	}
	function payment(data) {
		// #ifdef MP
		uni.checkBeforeAddOrder({
		success(res) {
			const traceId = res.data.traceId;
			data.traceId = traceId;
			onCreate(data);
		},
		fail(res) {
		}
	});
		// #endif
		// #ifndef MP
		onCreate(data);
		// #endif
	}
	function onCreate(data) {
		orderCreate(data).then(res => {
			orderNo.value = res.data.orderNo;
			uni.navigateTo({
				url: `/pages/order/order_payment/index?orderNo=${orderNo.value}&payPrice=${orderInfoVo.value.payFee}`
			})
		}).catch(err => {
			uni.hideLoading();
			return util.Tips({
				title: err
			}, '/pages/users/order_list/index');
		});
	}
	function SubOrder(e) {
		let data = {};
		if (!addressId.value && !shippingType.value) return util.Tips({
			title: '请选择收货地址'
		});
		if (shippingType.value == 1) {
			if (contacts.value == "" || contactsTel.value == "") {
				return util.Tips({
					title: '请填写联系人及联系人电话'
				});
			}
			if (!/^1(3|4|5|7|8|9|6)\d{9}$/.test(contactsTel.value)) {
				return util.Tips({
					title: '请填写正确的手机号'
				});
			}
			if (!/^[\u4e00-\u9fa5\w]{2,16}$/.test(contacts.value)) {
				return util.Tips({
					title: '请填写您的真实姓名'
				});
			}
			if (storeList.value.length == 0) return util.Tips({
				title: '暂无门店,请选择其他方式'
			});
		}
		data = {
			realName: contacts.value,
			phone: contactsTel.value,
			addressId: addressId.value,
			couponId: couponId.value,
			useIntegral: false, // 开源版不支持积分抵扣，后端要求该字段非空
			preOrderNo: preOrderNo.value,
			mark: mark.value,
			storeId: system_store.value.id || 0,
			shippingType: util.$h.Add(shippingType.value, 1),
		};
		// #ifdef MP
		openPaySubscribe().then(() => {
			payment(data);
		});
		// #endif
		// #ifndef MP
		payment(data);
		// #endif
	}
</script>

<style lang="scss" scoped>
	/* #ifdef MP */
	.mp-nav-bar {
		position: fixed;
		top: 0;
		right: 0;
		left: 0;
		z-index: 998;
		box-sizing: border-box;
		@include main_bg_color(theme);
	}

	.mp-nav-bar__content {
		display: flex;
		align-items: center;
		position: absolute;
		right: 0;
		bottom: 16rpx;
		left: 0;
		height: 60rpx;
		padding: 0 14rpx;
		box-sizing: border-box;
	}

	.mp-nav-bar__back,
	.mp-nav-bar__placeholder {
		width: 72rpx;
		flex: 0 0 72rpx;
	}

	.mp-nav-bar__back {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 60rpx;
		border: 1px solid rgba(0, 0, 0, 0.07);
		border-radius: 30rpx;
		background: rgba(255, 255, 255, 0.6);
		color: #000;
		font-size: 36rpx;
	}

	.mp-nav-bar__title {
		flex: 1;
		height: 60rpx;
		overflow: hidden;
		color: #fff;
		font-size: 34rpx;
		font-weight: 500;
		line-height: 60rpx;
		text-align: center;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.mp-nav-placeholder {
		width: 100%;
	}
	/* #endif */

	.font_color {
		@include main_color(theme);
	}

	.price_color {
		@include price_color(theme);
	}

	.line2 {
		width: 504rpx;
	}

	.textR {
		text-align: right;
	}

	.order-submission .line {
		width: 100%;
		height: 3rpx;
	}

	.order-submission .line image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.order-submission .address {
		padding: 28rpx;
		background-color: #fff;
		box-sizing: border-box;
	}

	.order-submission .address .addressCon {
		width: 596rpx;
		font-size: 26rpx;
		color: #666;
	}

	.order-submission .address .addressCon .name {
		font-size: 30rpx;
		color: #282828;
		font-weight: bold;
		margin-bottom: 10rpx;
	}

	.order-submission .address .addressCon .name .phone {
		margin-left: 50rpx;
	}

	.order-submission .address .addressCon .default {
		margin-right: 12rpx;
	}

	.order-submission .address .addressCon .setaddress {
		color: #333;
		font-size: 28rpx;
	}

	.order-submission .address .iconfont {
		font-size: 35rpx;
		color: #707070;
	}

	.order-submission .allAddress {
		width: 100%;
		@include index-gradient(theme);
		padding: 110rpx 30rpx 0 30rpx;
	}

	.order-submission .allAddress .nav {
		width: 690rpx;
		margin: 0 auto;
	}

	.order-submission .allAddress .nav .item {
		width: 334rpx;
	}

	.order-submission .allAddress .nav .item.on {
		position: relative;
		width: 230rpx;
	}

	.order-submission .allAddress .nav .item.on::before {
		position: absolute;
		bottom: 0;
		content: "快递配送";
		font-size: 28rpx;
		display: block;
		height: 0;
		width: 336rpx;
		border-width: 0 20rpx 80rpx 0;
		border-style: none solid solid;
		border-color: transparent transparent #fff;
		z-index: 2;
		border-radius: 14rpx 36rpx 0 0;
		text-align: center;
		line-height: 80rpx;
	}

	.order-submission .allAddress .nav .item:nth-of-type(2).on::before {
		content: "到店自提";
		border-width: 0 0 80rpx 20rpx;
		border-radius: 36rpx 14rpx 0 0;
	}

	.order-submission .allAddress .nav .item.on2 {
		position: relative;
	}

	.order-submission .allAddress .nav .item.on2::before {
		position: absolute;
		bottom: 0;
		content: "到店自提";
		font-size: 28rpx;
		display: block;
		height: 0;
		width: 401rpx;
		border-width: 0 0 60rpx 60rpx;
		border-style: none solid solid;
		border-color: transparent transparent rgba(255, 255, 255, 0.6);
		border-radius: 36rpx 14rpx 0 0;
		text-align: center;
		line-height: 60rpx;
	}

	.order-submission .allAddress .nav .item:nth-of-type(1).on2::before {
		content: "快递配送";
		border-width: 0 60rpx 60rpx 0;
		border-radius: 14rpx 36rpx 0 0;
	}

	.order-submission .allAddress .address {
		width: 690rpx;
		max-height: 180rpx;
		margin: -2rpx auto 0 auto;
	}

	.order-submission .allAddress .line {
		width: 100%;
		margin: 0 auto;
	}

	.order-submission .wrapper .item .discount .placeholder {
		color: #ccc;
	}

	.order-submission .wrapper {
		background-color: #fff;
		margin-top: 15rpx;
	}

	.order-submission .wrapper .item {
		padding: 27rpx 24rpx;
		font-size: 30rpx;
		color: #333333;
		border-bottom: 1px solid #F5F5F5;
	}

	.order-submission .wrapper .item .discount {
		font-size: 30rpx;
		color: #333;
	}

	.order-submission .wrapper .item .discount .iconfont {
		color: #515151;
		font-size: 30rpx;
		margin-left: 15rpx;
	}

	.order-submission .wrapper .item .discount .num {
		font-size: 32rpx;
		margin-right: 20rpx;
	}

	.order-submission .wrapper .item .shipping {
		font-size: 30rpx;
		color: #999;
		position: relative;
		padding-right: 58rpx;
	}

	.order-submission .wrapper .item .shipping .iconfont {
		font-size: 35rpx;
		color: #707070;
		position: absolute;
		right: 0;
		top: 50%;
		transform: translateY(-50%);
		margin-left: 30rpx;
	}

	.order-submission .wrapper .item textarea {
		background-color: #f9f9f9;
		width: auto !important;
		height: 140rpx;
		border-radius: 14rpx;
		margin-top: 30rpx;
		padding: 15rpx;
		box-sizing: border-box;
		font-weight: 400;
	}

	.order-submission .wrapper .item .placeholder {
		color: #ccc;
	}

	.order-submission .wrapper .item .list {
		margin-top: 35rpx;
	}

	.order-submission .wrapper .item .list .payItem {
		border: 1px solid #eee;
		border-radius: 14rpx;
		height: 86rpx;
		width: 100%;
		box-sizing: border-box;
		margin-top: 20rpx;
		font-size: 28rpx;
		color: #282828;
	}

	.order-submission .wrapper .item .list .payItem.on {
		// border-color: #fc5445;
		@include coupons_border_color(theme);
		color: $theme-color;
	}

	.order-submission .wrapper .item .list .payItem .name {
		width: 50%;
		text-align: center;
		border-right: 1px solid #eee;
	}

	.order-submission .wrapper .item .list .payItem .name .iconfont {
		width: 44rpx;
		height: 44rpx;
		border-radius: 50%;
		text-align: center;
		line-height: 44rpx;
		background-color: #fe960f;
		color: #fff;
		font-size: 30rpx;
		margin-right: 15rpx;
	}

	.order-submission .wrapper .item .list .payItem .name .iconfont.icon-weixin2 {
		background-color: #41b035;
	}

	.order-submission .wrapper .item .list .payItem .name .iconfont.icon-zhifubao {
		background-color: #00AAEA;
	}

	.order-submission .wrapper .item .list .payItem .tip {
		width: 49%;
		text-align: center;
		font-size: 26rpx;
		color: #aaa;
	}

	.order-submission .moneyList {
		margin-top: 15rpx;
		background-color: #fff;
		padding: 30rpx;
		margin-bottom: calc(constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		margin-bottom: calc(env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
	}

	.order-submission .moneyList .item {
		font-size: 28rpx;
		color: #282828;
	}

	.order-submission .moneyList .item~.item {
		margin-top: 20rpx;
	}

	.order-submission .moneyList .item .money {
		color: #666666;
	}

	.order-submission .footer {
		width: 100%;
		height: 100rpx;
		background-color: #fff;
		padding: 0 30rpx;
		font-size: 28rpx;
		color: #333;
		box-sizing: border-box;
		position: fixed;
		bottom: 0;
		left: 0;
		height: calc(100rpx + constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(100rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
	}

	.order-submission .footer .settlement {
		font-size: 30rpx;
		color: #fff;
		width: 240rpx;
		height: 70rpx;
		@include main_bg_color(theme);
		border-radius: 50rpx;
		text-align: center;
		line-height: 70rpx;
	}

	.footer .transparent {
		opacity: 0
	}
</style>
