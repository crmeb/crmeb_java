<template>
	<view>
		<view class='order-submission'>
			<view class="allAddress" :style="store_self_mention ? '':'padding-top:10rpx'">
				<view class="nav acea-row">
					<view class="item font-color" :class="shippingType == 0 ? 'on' : 'on2'" @tap="addressType(0)" v-if='store_self_mention'></view>
					<view class="item font-color" :class="shippingType == 1 ? 'on' : 'on2'" @tap="addressType(1)" v-if='store_self_mention'></view>
				</view>
				<view class='address acea-row row-between-wrapper' @tap='onAddress' v-if='shippingType == 0'>
					<view class='addressCon' v-if="addressInfo.realName">
						<view class='name'>{{addressInfo.realName}}
							<text class='phone'>{{addressInfo.phone}}</text>
						</view>
						<view><text class='default font-color' v-if="addressInfo.isDefault">[默认]</text>{{addressInfo.province}}{{addressInfo.city}}{{addressInfo.district}}{{addressInfo.detail}}</view>
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
							<view class="line1"> {{system_store.address}}{{", " + system_store.detailedAddress}}</view>
						</view>
						<view class='iconfont icon-jiantou'></view>
					</block>
					<block v-else>
						<view>暂无门店信息</view>
					</block>
				</view>
				<view class='line'>
					<image src='/static/images/line.jpg'></image>
				</view>
			</view>
			<orderGoods :cartInfo="cartInfo"></orderGoods>
			<view class='wrapper'>
				<view class='item acea-row row-between-wrapper' @tap='couponTap' v-if="!pinkId && !BargainId && !combinationId && !seckillId">
					<view>优惠券</view>
					<view class='discount'>{{couponTitle}}
						<text class='iconfont icon-jiantou'></text>
					</view>
				</view>
				<view class='item acea-row row-between-wrapper' v-if="!pinkId && !BargainId && !combinationId && !seckillId">
					<view>积分抵扣</view>
					<view class='discount acea-row row-middle'>
						<view> {{useIntegral ? "剩余积分":"当前积分"}}
							<text class='num font-color'>{{integral || 0}}</text>
						</view>
						<checkbox-group @change="ChangeIntegral">
							<checkbox :checked='useIntegral ? true : false' />
						</checkbox-group>
					</view>
				</view>
				<view class='item acea-row row-between-wrapper' v-if="priceGroup.vipPrice > 0 && userInfo.vip && !pinkId && !BargainId && !combinationId && !seckillId">
					<view>会员优惠</view>
					<view class='discount'>-￥{{priceGroup.vipPrice}}</view>
				</view>
				<view class='item acea-row row-between-wrapper' v-if='shippingType==0'>
					<view>快递费用</view>
					<view class='discount' v-if='parseFloat(priceGroup.storePostage) > 0'>+￥{{priceGroup.storePostage}}</view>
					<view class='discount' v-else>免运费</view>
				</view>
				<view v-else>
					<view class="item acea-row row-between-wrapper">
						<view>联系人</view>
						<view class="discount">
							<input type="text" placeholder="请填写您的联系姓名" placeholder-class="placeholder" @blur='realName'></input>
						</view>
					</view>
					<view class="item acea-row row-between-wrapper">
						<view>联系电话</view>
						<view class="discount">
							<input type="text" placeholder="请填写您的联系电话" placeholder-class="placeholder" @blur='phone'></input>
						</view>
					</view>
				</view>
				<!-- <view class='item acea-row row-between-wrapper' wx:else>
		      <view>自提门店</view>
		      <view class='discount'>{{system_store.name}}</view>
		    </view> -->
				<view class='item' v-if="textareaStatus">
					<view>备注信息</view>
					<textarea v-if="coupon.coupon===false" placeholder-class='placeholder' @input='bindHideKeyboard' value="" name="mark"
					 placeholder='请添加备注（150字以内）'></textarea>
				</view>
			</view>
			<view class='wrapper'>
				<view class='item'>
					<view>支付方式</view>
					<view class='list'>
						<view class='payItem acea-row row-middle' :class='active==index ?"on":""' @tap='payItem(index)' v-for="(item,index) in cartArr"
						 :key='index' v-if="item.payStatus==1">
							<view class='name acea-row row-center-wrapper'>
								<view class='iconfont animated' :class='(item.icon) + " " + (animated==true&&active==index ?"bounceIn":"")'></view>{{item.name}}
							</view>
							<view class='tip'>{{item.title}}</view>
						</view>
						<!-- #ifdef MP || APP-PLUS -->
						<!-- <view class='payItem acea-row row-middle' :class='active==index ?"on":""' @tap='payItem(index)' v-for="(item,index) in cartArr"
						 :key='index' v-if="item.payStatus==1">
							<view class='name acea-row row-center-wrapper'>
								<view class='iconfont animated' :class='(item.icon) + " " + (animated==true&&active==index ?"bounceIn":"")'></view>{{item.name}}
							</view>
							<view class='tip'>{{item.title}}</view>
						</view> -->
						<!-- #endif -->
					</view>
				</view>
			</view>
			<view class='moneyList'>
				<view class='item acea-row row-between-wrapper'>
					<view>商品总价：</view>
					<view class='money'>￥{{priceGroup.totalPrice}}</view>
				</view>
				<view class='item acea-row row-between-wrapper' v-if="coupon_price > 0">
					<view>优惠券抵扣：</view>
					<view class='money'>-￥{{coupon_price}}</view>
				</view>
				<view class='item acea-row row-between-wrapper' v-if="integral_price > 0">
					<view>积分抵扣：</view>
					<view class='money'>-￥{{integral_price}}</view>
				</view>
				<view class='item acea-row row-between-wrapper' v-if="priceGroup.storePostage > 0">
					<view>运费：</view>
					<view class='money'>+￥{{priceGroup.storePostage}}</view>
				</view>
			</view>
			<view style='height:120rpx;'></view>
			<view class='footer acea-row row-between-wrapper'>
				<view>合计:
					<text class='font-color'>￥{{totalPrice || 0}}</text>
				</view>
				<view class='settlement' style='z-index:100' @tap="SubOrder">立即结算</view>
			</view>
		</view>
		<couponListWindow :coupon='coupon' @ChangCouponsClone="ChangCouponsClone" :openType='openType' :cartId='cartId'
		 @ChangCoupons="ChangCoupons"></couponListWindow>
		<addressWindow ref="addressWindow" @changeTextareaStatus="changeTextareaStatus" :address='address' :pagesUrl="pagesUrl"
		 @OnChangeAddress="OnChangeAddress" @changeClose="changeClose"></addressWindow>
		<!-- #ifdef MP -->
		<authorize @onLoadFun="onLoadFun" :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse"></authorize>
		<!-- #endif -->
		<home></home>
	</view>
</template>
<script>
	import {
		orderConfirm,
		getCouponsOrderPrice,
		orderCreate,
		postOrderComputed,
		orderPay
	} from '@/api/order.js';
	import {
		getAddressDefault,
		getAddressDetail
	} from '@/api/user.js';
	import {
		openPaySubscribe
	} from '@/utils/SubscribeMessage.js';
	import {
		storeListApi
	} from '@/api/store.js';
	import {
		CACHE_LONGITUDE,
		CACHE_LATITUDE
	} from '@/config/cache.js';
	import couponListWindow from '@/components/couponListWindow';
	import addressWindow from '@/components/addressWindow';
	import orderGoods from '@/components/orderGoods';
	import home from '@/components/home';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	// #ifdef MP
	import authorize from '@/components/Authorize';
	// #endif
	export default {
		components: {
			couponListWindow,
			addressWindow,
			orderGoods,
			home,
			// #ifdef MP
			authorize
			// #endif
		},
		data() {
			return {
				textareaStatus: true,
				//支付方式
				cartArr: [{
						"name": "微信支付",
						"icon": "icon-weixin2",
						value: 'weixin',
						title: '微信快捷支付',
						payStatus: 1,
					},
					{
						"name": "余额支付",
						"icon": "icon-icon-test",
						value: 'yue',
						title: '可用余额:',
						payStatus: 1,
					},
					{
						"name": "线下支付",//offlinePayStatu：1开启线下支付；2关闭；offlinePostage：true有邮费
						"icon": "icon-yinhangqia",
						value: 'offline',
						title: '线下支付',
						payStatus: 2,
					},
				],
				payType: 'weixin', //支付方式
				openType: 1, //优惠券打开方式 1=使用
				active: 0, //支付方式切换
				coupon: {
					coupon: false,
					list: [],
					statusTile: '立即使用'
				}, //优惠券组件
				address: {
					address: false
				}, //地址组件
				addressInfo: {}, //地址信息
				pinkId: 0, //拼团id
				addressId: 0, //地址id
				couponId: 0, //优惠券id
				cartId: '', //购物车id
				BargainId: 0,
				combinationId: 0,
				seckillId: 0,
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
				store_self_mention: 0,
				cartInfo: [],
				priceGroup: {},
				animated: false,
				totalPrice: 0,
				integralRatio: "0",
				pagesUrl: "",
				orderKey: "",
				// usableCoupon: {},
				offlinePostage: "",
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				from: '',
				news: true,
				again: false,
				addAgain: false,
				bargain: false, //是否是砍价
				combination: false, //是否是拼团
				secKill: false, //是否是秒杀
			};
		},
		computed: mapGetters(['isLogin']),
		onLoad: function(options) {
			// #ifdef H5
			this.from = this.$wechat.isWeixin() ? 'public' : 'weixinh5'
			// #endif
			// #ifdef MP
			this.from = 'routine'
			// #endif
			if (!options.cartId) return this.$util.Tips({
				title: '请选择要购买的商品'
			}, {
				tab: 3,
				url: 1
			});
			this.couponId = options.couponId || 0;
			this.pinkId = options.pinkId ? parseInt(options.pinkId) : 0;
			this.addressId = options.addressId || 0;
			this.cartId = options.cartId;
			this.is_address = options.is_address ? true : false;
			this.news = options.new || true;
			this.again = options.again || false;
			this.addAgain = options.addAgain || false;
			this.secKill = options.secKill || false;
			this.combination = options.combination || false;
			this.bargain = options.bargain || false;
			if (this.isLogin) {
				this.getaddressInfo();
				this.getConfirm();
				//调用子页面方法授权后执行获取地址列表
				this.$nextTick(function() {
					this.$refs.addressWindow.getAddressList();
				})
			} else {
				// #ifdef H5 || APP-PLUS
				toLogin();
				// #endif 
				// #ifdef MP
				this.isAuto = true;
				this.$set(this, 'isShowAuth', true);
				// #endif
			}
		},
		/**
		 * 生命周期函数--监听页面显示
		 */
		onShow: function() {
			let _this = this
			this.textareaStatus = true;
			if (this.isLogin && this.toPay == false) {
				this.getaddressInfo();
				this.$nextTick(function() {
					this.$refs.addressWindow.getAddressList();
				})
			}

			uni.$on("handClick", res => {
				if (res) {
					_this.system_store = res.address
				}
				// 清除监听
				uni.$off('handClick');
			})

			// let pages = getCurrentPages();
			// let currPage = pages[pages.length - 1]; //当前页面
			// if (currPage.data.storeItem) {
			// 	let json = currPage.data.storeItem;
			// 	this.$set(this, 'system_store', json);
			// }
		},
		/**
		 * 生命周期函数--监听页面隐藏
		 */
		// onHide: function() {
		// 	console.log(999);
		// 	this.isClose = true
		// },
		methods: {
			/**
			 * 授权回调事件
			 * 
			 */
			onLoadFun: function() {
				this.getaddressInfo();
				this.getConfirm();
				//调用子页面方法授权后执行获取地址列表
				// this.$scope.selectComponent('#address-window').getAddressList();
			},
			/**
			 * 获取门店列表数据
			 */
			getList: function() {
				let longitude = uni.getStorageSync("user_longitude"); //经度
				let latitude = uni.getStorageSync("user_latitude"); //纬度
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
			// 关闭地址弹窗；
			changeClose: function() {
				this.$set(this.address, 'address', false);
			},
			/*
			 * 跳转门店列表
			 */
			showStoreList: function() {

				let _this = this
				if (this.storeList.length > 0) {
					uni.navigateTo({
						url: '/pages/users/goods_details_store/index'
					})
				}
			},
			computedPrice: function() {
				let shippingType = this.shippingType;
				postOrderComputed(this.orderKey, {
					addressId: this.addressId,
					useIntegral: this.useIntegral ? 1 : 0,
					couponId: this.couponId,
					shippingType: parseInt(shippingType) + 1,
					payType: this.payType
				}).then(res => {
					console.log(res)
					let result = res.data.result.result;
					if (result) {
						this.totalPrice = result.orderId.payPrice;
						this.integral_price = result.orderId.deductionPrice;
						this.coupon_price = result.orderId.couponPrice;
						this.integral = this.useIntegral ? result.orderId.surplusIntegral : this.userInfo.integral;
						this.$set(this.priceGroup, 'storePostage', shippingType == 1 ? 0 : result.orderId.payPostage);
					}
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
				});
			},
			addressType: function(e) {
				let index = e;
				this.shippingType = parseInt(index);
				this.computedPrice();
				if (index == 1) this.getList();
			},
			bindPickerChange: function(e) {
				let value = e.detail.value;
				this.shippingType = value;
				this.computedPrice();
			},
			ChangCouponsClone: function() {
				this.$set(this.coupon, 'coupon', false);
			},
			changeTextareaStatus: function() {
				for (let i = 0, len = this.coupon.list.length; i < len; i++) {
					this.coupon.list[i].use_title = '';
					this.coupon.list[i].is_use = 0;
				}
				this.textareaStatus = true;
				this.status = 0;
				this.$set(this.coupon, 'list', this.coupon.list);
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
			/**
			 * 选择地址后改变事件
			 * @param object e
			 */
			OnChangeAddress: function(e) {
				this.textareaStatus = true;
				this.addressId = e;
				this.address.address = false;
				this.getaddressInfo();
				this.computedPrice();
			},
			bindHideKeyboard: function(e) {
				this.mark = e.detail.value;
			},
			/**
			 * 获取当前订单详细信息
			 * 
			 */
			getConfirm: function() {
				let that = this;
				orderConfirm(that.cartId,that.news,this.addAgain,this.secKill,this.combination,this.bargain).then(res => {
					that.$set(that, 'userInfo', res.data.userInfo);
					that.$set(that, 'integral', res.data.userInfo.integral);
					that.$set(that, 'cartInfo', res.data.cartInfo);
					that.$set(that, 'integralRatio', res.data.integralRatio);
					that.$set(that, 'offlinePostage', res.data.offlinePostage);
					that.$set(that, 'orderKey', res.data.orderKey);

					that.$set(that, 'priceGroup', res.data.priceGroup);
					that.$set(that, 'totalPrice', that.$util.$h.Add(parseFloat(res.data.priceGroup.totalPrice), parseFloat(res.data
						.priceGroup.storePostage)));
					that.$set(that, 'seckillId', parseInt(res.data.secKillId));
					that.$set(that, 'store_self_mention', res.data.storeSelfMention == 'true'?true:false);
					that.cartArr[1].title = '可用余额:' + res.data.userInfo.nowMoney;
					that.cartArr[0].payStatus = res.data.payWeixinOpen || 0
					that.cartArr[1].payStatus = res.data.yuePayStatus || 0
					if (res.data.offlinePayStatus == 2) {
						that.cartArr[2].payStatus = 1
					} else {
						that.cartArr[2].payStatus = 0
					}

					// that.$set(that, 'cartArr', that.cartArr);
					that.$set(that, 'ChangePrice', that.totalPrice);
					that.getBargainId();
					if(!that.secKill) that.getCouponList();
				}).catch(err => {
					return this.$util.Tips({
						title: err
					}, {
						tab: 3,
						url: 1
					});
				});
			},
			/*
			 * 提取砍价和拼团id
			 */
			getBargainId: function() {
				let that = this;
				let cartINfo = that.cartInfo;
				let BargainId = 0;
				let combinationId = 0;
				cartINfo.forEach(function(value, index, cartINfo) {
					BargainId = cartINfo[index].bargainId,
						combinationId = cartINfo[index].combinationId
				})
				that.$set(that, 'BargainId', parseInt(BargainId));
				that.$set(that, 'combinationId', parseInt(combinationId));
				if (that.cartArr.length == 3 && (BargainId || combinationId || that.seckillId)) {
					that.cartArr[2].payStatus = 0;
					that.$set(that, 'cartArr', that.cartArr);
				}
			},
			/**
			 * 获取当前金额可用优惠券
			 * 
			 */
			getCouponList: function() {
				let that = this;
				let data = {
					cartId: this.cartId
				}
				getCouponsOrderPrice(data).then(res => {
					that.$set(that.coupon, 'list', res.data);
					that.openType = 1;
				});
			},
			/*
			 * 获取默认收货地址或者获取某条地址信息
			 */
			getaddressInfo: function() {
				let that = this;
				if (that.addressId) {
					getAddressDetail(that.addressId).then(res => {
						if(res.data){
							res.data.isDefault = parseInt(res.data.isDefault);
							that.addressInfo = res.data || {};
							that.addressId = res.data.id || 0;
							that.address.addressId = res.data.id || 0;
						}
					})
				} else {
					getAddressDefault().then(res => {
						if(res.data){
							res.data.isDefault = parseInt(res.data.isDefault);
							that.addressInfo = res.data || {};
							that.addressId = res.data.id || 0;
							that.address.addressId = res.data.id || 0;
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
				that.computedPrice();
				setTimeout(function() {
					that.car();
				}, 500);
			},
			couponTap: function() {
				this.coupon.coupon = true;
			},
			car: function() {
				let that = this;
				that.animated = false;
			},
			onAddress: function() {
				let that = this;
				that.textareaStatus = false;
				that.address.address = true;
				that.pagesUrl = '/pages/users/user_address_list/index?cartId=' + this.cartId + '&pinkId=' + this.pinkId +
					'&couponId=' + this.couponId + '&secKill=' + this.secKill + '&combination=' + this.combination + '&bargain=' + this.bargain;
			},
			realName: function(e) {
				this.contacts = e.detail.value;
			},
			phone: function(e) {
				this.contactsTel = e.detail.value;
			},
			payment: function(data) {
				let that = this;
				orderCreate(that.orderKey, data).then(res => {
					let result = res.data.result,
					    status = res.data.status,
						orderId = result.orderId,
						jsConfig = res.data.jsConfig,
						message = res.data.message;
						if(that.totalPrice===0)return that.$util.Tips({
										title: '支付成功',
										icon: 'success'
									});
						if(that.news == "false"){
							orderPay({
								'paytype': that.payType,
								'uni':res.data.result.key,
								// #ifdef MP 
								'from': 'routine',
								// #endif
								// #ifdef H5 || APP-PLUS
								'from': this.$wechat.isWeixin() ? 'public' : 'weixinh5',
								// #endif
							}).then(res=>{
								result = res.data.result;
								status = res.data.status;
								orderId = result.orderId;
								jsConfig = res.data.jsConfig;
								message = res.data.message;
								that.getPayType(status,orderId,message,jsConfig);
							})
						}else{
							that.getPayType(status,orderId,message,jsConfig);
						}
				}).catch(err => {
					uni.hideLoading();
					return that.$util.Tips({
						title: err
					});
				});
			},
			getPayType: function(status,orderId,message,jsConfig){
				let that = this;
				let goPages = '/pages/order_pay_status/index?order_id=' + orderId + '&msg=' + message;
				switch (status) {
					case 'ORDER_EXIST':
					case 'EXTEND_ORDER':
					case 'PAY_ERROR':
						uni.hideLoading();
						return that.$util.Tips({
							title: message
						}, {
							tab: 5,
							url: goPages
						});
						break;
					case 'SUCCESS':	
						uni.hideLoading();
						if (that.BargainId || that.combinationId || that.pinkId || that.seckillId)
							return that.$util.Tips({
								title: message,
								icon: 'success'
							}, {
								tab: 4,
								url: goPages
							});	
						return that.$util.Tips({
							title: message,
							icon: 'success'
						}, {
							tab: 5,
							url: goPages
						});
						break;
					case 'WECHAT_PAY':
						// #ifdef MP
						that.toPay = true;
						let packages = 'prepay_id=' + jsConfig.prepayId;
						uni.requestPayment({
							timeStamp: jsConfig.timeStamp.toString(),
							nonceStr: jsConfig.nonceStr,
							package: packages,
							signType: jsConfig.signType,
							paySign: jsConfig.paySign,
							success: function(res) {
								uni.hideLoading();
								if (that.BargainId || that.combinationId || that.pinkId || that.seckillId)
									return that.$util.Tips({
										title: '支付成功',
										icon: 'success'
									}, {
										tab: 4,
										url: goPages
									});
								return that.$util.Tips({
									title: '支付成功',
									icon: 'success'
								}, {
									tab: 5,
									url: goPages
								});
							},
							fail: function(e) {
								uni.hideLoading();
								return that.$util.Tips({
									title: '取消支付'
								}, {
									tab: 5,
									url: goPages + '&status=0'
								});
							},
							complete: function(e) {
								uni.hideLoading();
								//关闭当前页面跳转至订单状态
								if (res.errMsg == 'requestPayment:cancel') return that.$util.Tips({
									title: '取消支付'
								}, {
									tab: 5,
									url: goPages + '&status=0'
								});
							},
						})
						// #endif
						// #ifdef H5
						let jsConfigAgain = jsConfig;
						let packages = 'prepay_id=' + jsConfigAgain.prepayId;
						let data = {
							timestamp:jsConfigAgain.timeStamp,
							nonceStr:jsConfigAgain.nonceStr,
							package:packages,
							signType:jsConfigAgain.signType,
							paySign:jsConfigAgain.paySign,
							h5PayUrl:jsConfigAgain.h5PayUrl
						};
						this.$wechat.pay(data).then(res => {
							return that.$util.Tips({
								title: '支付成功',
								icon: 'success'
							}, {
								tab: 5,
								url: goPages
							});
						}).cache(res => {
							if (res.errMsg == 'requestPayment:cancel') return that.$util.Tips({
								title: '取消支付'
							}, {
								tab: 5,
								url: goPages + '&status=0'
							});
						})
						// #endif
						break;
					case 'PAY_DEFICIENCY':
						uni.hideLoading();
						//余额不足
						return that.$util.Tips({
							title: message
						}, {
							tab: 5,
							url: goPages + '&status=1'
						});
						break;
					case "WECHAT_H5_PAY": //网页版公众号支付
						setTimeout(() => {
							let domain = encodeURIComponent(location.href);
							let urls = jsConfigAgain.h5PayUrl + '&redirect_url='+ domain;
							location.href = urls;
							return that.$util.Tips({
								title: '支付成功',
								icon: 'success'
							}, {
								tab: 5,
								url: goPages
							});
						}, 100);
						break;
				}
			},
			SubOrder: function(e) {	

				let that = this,
					data = {};

				if (!that.payType) return that.$util.Tips({
					title: '请选择支付方式'
				});
				if (!that.addressId && !that.shippingType) return that.$util.Tips({
					title: '请选择收货地址'
				});
				if (that.shippingType == 1) {
					if (that.contacts == "" || that.contactsTel == "") {
						return that.$util.Tips({
							title: '请填写联系人或联系人电话'
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
					formId: '',
					couponId: that.couponId,
					payType: that.payType,
					useIntegral: that.useIntegral,
					bargainId: that.BargainId,
					combinationId: that.combinationId,
					pinkId: that.pinkId,
					seckillId: that.seckillId,
					mark: that.mark,
					storeId: that.system_store.id || 0,
					'from': that.from,
					shippingType: that.$util.$h.Add(that.shippingType, 1),
					isNew: that.news
				};
				if (data.payType == 'yue' && parseFloat(that.userInfo.nowMoney) < parseFloat(that.totalPrice)) return that.$util.Tips({
					title: '余额不足！'
				});
				uni.showLoading({
					title: '订单支付中'
				});
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
		padding: 28rpx 30rpx;
		background-color: #fff;
		box-sizing: border-box;
	}

	.order-submission .address .addressCon {
		width: 610rpx;
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
		background: linear-gradient(to bottom, #e93323 0%, #f5f5f5 100%);
		// background-image: linear-gradient(to bottom, #e93323 0%, #f5f5f5 100%);
		// background-image: -webkit-linear-gradient(to bottom, #e93323 0%, #f5f5f5 100%);
		// background-image: -moz-linear-gradient(to bottom, #e93323 0%, #f5f5f5 100%);
		padding-top: 100rpx;
	}

	.order-submission .allAddress .nav {
		width: 710rpx;
		margin: 0 auto;
	}

	.order-submission .allAddress .nav .item {
		width: 355rpx;
	}

	.order-submission .allAddress .nav .item.on {
		position: relative;
		width: 250rpx;
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
		border-radius: 7rpx 30rpx 0 0;
		text-align: center;
		line-height: 80rpx;
	}

	.order-submission .allAddress .nav .item:nth-of-type(2).on::before {
		content: "到店自提";
		border-width: 0 0 80rpx 20rpx;
		border-radius: 30rpx 7rpx 0 0;
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
		width: 400rpx;
		border-width: 0 0 60rpx 60rpx;
		border-style: none solid solid;
		border-color: transparent transparent #f7c1bd;
		border-radius: 40rpx 6rpx 0 0;
		text-align: center;
		line-height: 60rpx;
	}

	.order-submission .allAddress .nav .item:nth-of-type(1).on2::before {
		content: "快递配送";
		border-width: 0 60rpx 60rpx 0;
		border-radius: 6rpx 40rpx 0 0;
	}

	.order-submission .allAddress .address {
		width: 710rpx;
		height: 150rpx;
		margin: 0 auto;
	}

	.order-submission .allAddress .line {
		width: 710rpx;
		margin: 0 auto;
	}

	.order-submission .wrapper .item .discount .placeholder {
		color: #ccc;
	}

	.order-submission .wrapper {
		background-color: #fff;
		margin-top: 13rpx;
	}

	.order-submission .wrapper .item {
		padding: 27rpx 30rpx;
		font-size: 30rpx;
		color: #282828;
		border-bottom: 1px solid #f0f0f0;
	}

	.order-submission .wrapper .item .discount {
		font-size: 30rpx;
		color: #999;
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
		width: 690rpx;
		height: 140rpx;
		border-radius: 3rpx;
		margin-top: 30rpx;
		padding: 25rpx 28rpx;
		box-sizing: border-box;
	}

	.order-submission .wrapper .item .placeholder {
		color: #ccc;
	}

	.order-submission .wrapper .item .list {
		margin-top: 35rpx;
	}

	.order-submission .wrapper .item .list .payItem {
		border: 1px solid #eee;
		border-radius: 6rpx;
		height: 86rpx;
		width: 100%;
		box-sizing: border-box;
		margin-top: 20rpx;
		font-size: 28rpx;
		color: #282828;
	}

	.order-submission .wrapper .item .list .payItem.on {
		border-color: #fc5445;
		color: #e93323;
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

	.order-submission .wrapper .item .list .payItem .tip {
		width: 49%;
		text-align: center;
		font-size: 26rpx;
		color: #aaa;
	}

	.order-submission .moneyList {
		margin-top: 12rpx;
		background-color: #fff;
		padding: 30rpx;
	}

	.order-submission .moneyList .item {
		font-size: 28rpx;
		color: #282828;
	}

	.order-submission .moneyList .item~.item {
		margin-top: 20rpx;
	}

	.order-submission .moneyList .item .money {
		color: #868686;
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
	}

	.order-submission .footer .settlement {
		font-size: 30rpx;
		color: #fff;
		width: 240rpx;
		height: 70rpx;
		background-color: #e93323;
		border-radius: 50rpx;
		text-align: center;
		line-height: 70rpx;
	}

	.footer .transparent {
		opacity: 0
	}
</style>
