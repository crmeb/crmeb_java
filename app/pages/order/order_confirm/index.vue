<template>
	<view :data-theme="theme">
		<!-- #ifndef APP-PLUS-->
		<view v-if="productType==='video'" class='cart_nav'>
			<nav-bar :navTitle='navTitle' @getNavH='getNavH'></nav-bar>
		</view>

		<!-- #endif -->
		<view class='order-submission' :style="'margin-top:'+(marTop)+'rpx;'">
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
					<image :src="urlDomain+'crmebimage/perset/staticImg/line.jpg'"></image>
				</view>
			</view>
			<view class="pad30">
				<orderGoods :cartInfo="cartInfo" :orderProNum="orderProNum"></orderGoods>
				<view class='wrapper borRadius14'>
					<view class='item acea-row row-between-wrapper' @tap='couponTap'
						v-if="!orderInfoVo.bargainId && !orderInfoVo.combinationId && !orderInfoVo.seckillId && productType==='normal'">
						<view>优惠券</view>
						<view class='discount'>{{couponTitle}}
							<text class='iconfont icon-jiantou'></text>
						</view>
					</view>

					<view class='item acea-row row-between-wrapper'
						v-if="!orderInfoVo.bargainId && !orderInfoVo.combinationId && !orderInfoVo.seckillId && productType==='normal'">
						<view>积分抵扣</view>
						<view class='discount acea-row row-middle'>
							<view> {{useIntegral ? "剩余积分":"当前积分"}}
								<text
									class='num font_color'>{{useIntegral ? orderInfoVo.surplusIntegral : orderInfoVo.userIntegral || 0}}</text>
							</view>
							<checkbox-group @change="ChangeIntegral">
								<checkbox :checked='useIntegral ? true : false'
									:disabled="orderInfoVo.userIntegral==0 && !useIntegral" />
							</checkbox-group>
						</view>
					</view>
					<!-- <view class='item acea-row row-between-wrapper'
						v-if="priceGroup.vipPrice > 0 && userInfo.vip && !pinkId && !BargainId && !combinationId && !seckillId">
						<view>会员优惠</view>
						<view class='discount'>-￥{{priceGroup.vipPrice}}</view>
					</view> -->
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
						 :maxlength="150"	value="" name="mark" placeholder='请添加备注（150字以内）'></textarea>
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
					<view class='item acea-row row-between-wrapper' v-if="orderInfoVo.deductionPrice > 0">
						<view>积分抵扣：</view>
						<view class='money'>-￥{{orderInfoVo.deductionPrice}}</view>
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
<script>
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
	import couponListWindow from '@/components/couponListWindow';
	import orderGoods from '@/components/orderGoods';
	import navBar from '@/components/navBar';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import {
		Debounce
	} from '@/utils/validate.js'
	let app = getApp();
	export default {
		components: {
			navBar,
			couponListWindow,
			orderGoods,
		},
		onReady() {
			this.$nextTick(function() {
				// #ifdef MP
				const menuButton = uni.getMenuButtonBoundingClientRect();
				const query = uni.createSelectorQuery().in(this);
				query
					.select('#home')
					.boundingClientRect(data => {
						this.homeTop = menuButton.top * 2 + menuButton.height - data.height;
					})
					.exec();
				// #endif
			});
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				marTop: 0,
				navTitle: '提交订单',
				homeTop: 20,
				orderShow: 'orderShow', //下单页面使用优惠券组件不展示tab切换页
				textareaStatus: true,
				//支付方式
				cartArr: [{
						"name": "微信支付",
						"icon": "icon-weixin2",
						value: 'weixin',
						title: '微信快捷支付',
						// #ifdef APP
						payStatus: 0,
						// #endif
						// #ifndef APP
						payStatus: 1,
						// #endif
					},
					{
						"name": "余额支付",
						"icon": "icon-yuezhifu",
						value: 'yue',
						title: '可用余额:',
						payStatus: 1,
					}
				],
				payType: 'weixin', //支付方式
				openType: 1, //优惠券打开方式 1=使用
				active: 0, //支付方式切换
				coupon: {
					coupon: false,
					list: [],
					statusTile: '立即使用'
				}, //优惠券组件
				addressInfo: {}, //地址信息
				addressId: 0, //地址id
				couponId: 0, //优惠券id
				cartId: '', //购物车id
				userInfo: {}, //用户信息
				mark: '', //备注信息
				couponTitle: '请选择', //优惠券
				coupon_price: 0, //优惠券抵扣金额
				useIntegral: false, //是否使用积分
				integral_price: 0, //积分抵扣金额
				integral: 0,
				ChangePrice: 0, //使用积分抵扣变动后的金额
				formIds: [], //收集formid
				status: 0,
				is_address: false,
				toPay: false, //修复进入支付时页面隐藏从新刷新页面
				shippingType: 0,
				system_store: {},
				storePostage: 0,
				contacts: '',
				contactsTel: '',
				mydata: {},
				storeList: [],
				store_self_mention: false,
				cartInfo: [],
				priceGroup: {},
				animated: false,
				totalPrice: 0,
				integralRatio: "0",
				orderKey: "",
				// usableCoupon: {},
				offlinePostage: "",
				payChannel: '',
				news: true,
				again: false,
				addAgain: false,
				bargain: false, //是否是砍价
				combination: false, //是否是拼团
				secKill: false, //是否是秒杀
				orderInfoVo: {},
				addressList: [], //地址列表数据
				orderProNum: 0,
				preOrderNo: '', //预下单订单号
				theme: app.globalData.theme,
				formContent: '',
				addressChangeId: 0,
				orderNo: '' //下单订单号
			};
		},
		computed: {
			...mapGetters(['isLogin', 'systemPlatform', 'productType']),
			markNum() {
				if (this.mark) {
					return this.mark.length
				}
			}
		},
		watch: {
			isLogin: {
				handler: function(newV, oldV) {
					if (newV) {
						this.getloadPreOrder();
					}
				},
				deep: true
			}
		},
		onLoad(options) {
			// #ifdef H5
			this.payChannel = this.$wechat.isWeixin() ? 'public' : 'weixinh5';
			// #endif
			// #ifdef MP
			this.payChannel = 'routine';
			// #endif
			// #ifdef APP-PLUS
			this.payChannel = this.systemPlatform === 'ios' ? 'weixinAppIos' : 'weixinAppAndroid';
			// #endif
			// if (!options.cartId) return this.$util.Tips({
			// 	title: '请选择要购买的商品'
			// }, {
			// 	tab: 3,
			// 	url: 1
			// });
			this.preOrderNo = options.preOrderNo || 0;
			this.addressChangeId = options.addressId || 0;
			this.is_address = options.is_address ? true : false;
			if (this.isLogin) {
				this.getloadPreOrder();
			} else {
				toLogin();
			}
		},
		/**
		 * 生命周期函数--监听页面显示
		 */
		onShow: function() {
			let _this = this
			// wx.getLaunchOptionsSync 
			this.textareaStatus = true;

			uni.$on("handClick", res => {
				if (res) {
					_this.system_store = res.address
				}
				// 清除监听
				uni.$off('handClick');
			})
		},
		methods: {
			getNavH(marTop) {
				this.marTop = marTop;
			},
			// 订单详情
			getloadPreOrder: function() {
				loadPreOrderApi(this.preOrderNo).then(res => {
					let orderInfoVo = res.data.orderInfoVo;
					this.orderInfoVo = orderInfoVo;
					if (orderInfoVo.addressId && this.addressChangeId === 0) {
						this.addressId = orderInfoVo.addressId;
					} else {
						this.addressId = this.addressChangeId;
						if (orderInfoVo.addressId != this.addressChangeId) this.computedPrice();
					}
					this.cartInfo = orderInfoVo.orderDetailList;
					this.orderProNum = orderInfoVo.orderProNum;
					this.cartArr[1].title = '可用余额:' + orderInfoVo.userBalance;
					this.cartArr[1].payStatus = parseInt(res.data.yuePayStatus) === 1 ? 1 : 2;
					this.cartArr[0].payStatus = parseInt(res.data.payWeixinOpen) === 1 ? 1 : 0;
					this.getaddressInfo();
					// #ifdef H5
					if (this.$wechat.isWeixin()) this.cartArr.pop();
					// #endif
					this.store_self_mention = res.data.storeSelfMention == '1' && this
						.productType ===
						'normal' ? true : false;
				}).catch(err => {
					uni.navigateTo({
						url: '/pages/users/order_list/index'
					});
				})
			},
			/**
			 * 获取门店列表数据
			 */
			getList: function() {
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
					this.$set(this, 'storeList', list);
					this.$set(this, 'system_store', list[0]);
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
				})
			},
			/*
			 * 跳转门店列表
			 */
			showStoreList: function() {
				if (this.storeList.length > 0) {
					uni.navigateTo({
						url: '/pages/goods/goods_details_store/index'
					})
				}
			},
			// 计算订单价格
			computedPrice: function() {
				let shippingType = this.shippingType;
				postOrderComputed({
					addressId: this.addressId,
					useIntegral: this.useIntegral ? true : false,
					couponId: this.couponId,
					shippingType: parseInt(shippingType) + 1,
					preOrderNo: this.preOrderNo
				}).then(res => {
					let data = res.data;
					this.orderInfoVo.couponFee = data.couponFee;
					//赋值操作，userIntegral 当前积分，surplusIntegral 剩余积分
					this.orderInfoVo.userIntegral = data.surplusIntegral;
					this.orderInfoVo.deductionPrice = data.deductionPrice;
					this.orderInfoVo.freightFee = data.freightFee;
					this.orderInfoVo.payFee = data.payFee;
					this.orderInfoVo.proTotalFee = data.proTotalFee;
					this.orderInfoVo.useIntegral = data.useIntegral;
					this.orderInfoVo.usedIntegral = data.usedIntegral;
					this.orderInfoVo.surplusIntegral = data.surplusIntegral;
					//this.orderInfoVo.userIntegral = data.userIntegral;
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
				});
			},
			//选择地址还是门店自提
			addressType: function(e) {
				let index = e;
				this.shippingType = parseInt(index);
				this.computedPrice();
				//调起获取定位信息
				if (index == 1) {
					this.getList();
					this.$util.$L.getLocation();
				}
			},
			bindPickerChange: function(e) {
				let value = e.detail.value;
				this.shippingType = value;
				this.computedPrice();
			},
			ChangCouponsClone: function() {
				this.$set(this.coupon, 'coupon', false);
			},
			/**
			 * 处理点击优惠券后的事件
			 * 
			 */
			ChangCoupons: function(e) {
				// this.usableCoupon = e
				// this.coupon.coupon = false
				let index = e,
					list = this.coupon.list,
					couponTitle = '请选择',
					couponId = 0;
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
					couponTitle = list[index].name;
					couponId = list[index].id;
				}
				this.couponTitle = couponTitle;
				this.couponId = couponId;
				this.$set(this.coupon, 'coupon', false);
				this.$set(this.coupon, 'list', list);
				this.computedPrice();
			},
			/**
			 * 使用积分抵扣
			 */
			ChangeIntegral: function() {
				this.useIntegral = !this.useIntegral;
				this.computedPrice();
			},
			bindHideKeyboard: function(e) {
				this.mark = e.detail.value;
			},
			/**
			 * 获取当前金额可用优惠券
			 * 
			 */
			getCouponList: function() {
				getCouponsOrderPrice(this.preOrderNo).then(res => {
					this.$set(this.coupon, 'list', res.data);
					this.openType = 1;
				});
			},
			/*
			 * 获取默认收货地址或者获取某条地址信息
			 */
			getaddressInfo: function() {
				if (this.addressId) {

					getAddressDetail(this.addressId).then(res => {
						if (res.data) {
							res.data.isDefault = res.data.isDefault;
							this.addressInfo = res.data || {};
						}
					})
				}
			},
			payItem: function(e) {
				let that = this;
				let active = e;
				that.active = active;
				that.animated = true;
				that.payType = that.cartArr[active].value;
				// #ifdef H5
				if (that.payType == 'alipay' && this.$wechat.isWeixin() == true) {
					that.payChannel = 'public';
				} else if (that.payType == 'alipay' && this.$wechat.isWeixin() == false) {
					that.payChannel = 'weixinh5';
				}
				// #endif
				// #ifdef APP-PLUS
				if (that.payType == 'alipay') {
					that.payChannel = 'appAliPay';
				}
				// #endif
				//that.computedPrice();
				setTimeout(function() {
					that.car();
				}, 500);
			},
			couponTap: function() {
				this.coupon.coupon = true;
				if (!this.coupon.list.length) this.getCouponList();
			},
			car: function() {
				let that = this;
				that.animated = false;
			},
			onAddress: function() {

				uni.redirectTo({
					url: '/pages/users/user_address_list/index?preOrderNo=' + this.preOrderNo
				});
			},
			realName: function(e) {
				this.contacts = e.detail.value;
			},
			phone: function(e) {
				this.contactsTel = e.detail.value;
			},
			payment: function(data) {
				let that = this;
				// #ifdef MP
				uni.checkBeforeAddOrder({
					success(res) {
						console.log("下单前置检查 成功：", JSON.stringify(res));
						const traceId = res.data.traceId;
						data.traceId = traceId;
						that.onCreate(data);
					},
					fail(res) {
						console.log("下单前置检查 失败：", JSON.stringify(res));
					}
				});
				// #endif
				// #ifndef MP
				that.onCreate(data);
				// #endif
			},
			onCreate(data) {
				let that = this
				orderCreate(data).then(res => {
					this.orderNo = res.data.orderNo;
					console.log(res.data.orderNo);
					uni.navigateTo({
						url:`/pages/order/order_payment/index?orderNo=${this.orderNo}&payPrice=${this.orderInfoVo.payFee}`
					})
				}).catch(err => {
					uni.hideLoading();
					return that.$util.Tips({
						title: err
					}, '/pages/users/order_list/index');
				});
			},
			SubOrder(e) {
				let that = this,
					data = {};
				if (!that.addressId && !that.shippingType) return that.$util.Tips({
					title: '请选择收货地址'
				});
				if (that.shippingType == 1) {
					if (that.contacts == "" || that.contactsTel == "") {
						return that.$util.Tips({
							title: '请填写联系人及联系人电话'
						});
					}
					if (!/^1(3|4|5|7|8|9|6)\d{9}$/.test(that.contactsTel)) {
						return that.$util.Tips({
							title: '请填写正确的手机号'
						});
					}
					if (!/^[\u4e00-\u9fa5\w]{2,16}$/.test(that.contacts)) {
						return that.$util.Tips({
							title: '请填写您的真实姓名'
						});
					}
					if (that.storeList.length == 0) return that.$util.Tips({
						title: '暂无门店,请选择其他方式'
					});
				}
				data = {
					realName: that.contacts,
					phone: that.contactsTel,
					addressId: that.addressId,
					couponId: that.couponId,
					useIntegral: that.useIntegral,
					preOrderNo: that.preOrderNo,
					mark: that.mark,
					storeId: that.system_store.id || 0,
					shippingType: that.$util.$h.Add(that.shippingType, 1),
				};
				// #ifdef MP
				openPaySubscribe().then(() => {
					that.payment(data);
				});
				// #endif
				// #ifndef MP
				that.payment(data);
				// #endif
			}
		}
	}
</script>

<style lang="scss" scoped>
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
		height: calc(100rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
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

	/deep/ checkbox .uni-checkbox-input.uni-checkbox-input-checked {
		@include main_bg_color(theme);
		border: none !important;
		color: #fff !important
	}

	/deep/ checkbox .wx-checkbox-input.wx-checkbox-input-checked {
		@include main_bg_color(theme);
		border: none !important;
		color: #fff !important;
		margin-right: 0 !important;
	}
</style>