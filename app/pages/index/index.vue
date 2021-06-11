<template>
	<view>
		<view class="page-index" :class="{'bgf':navIndex >0}">
			<!-- #ifdef H5 -->
			<view class="header">
				<view class="serch-wrapper flex">
					<view class="logo">
						<image :src="logoUrl" mode=""></image>
					</view>
					<navigator url="/pages/goods_search/index" class="input" hover-class="none"><text
							class="iconfont icon-xiazai5"></text>
						搜索商品</navigator>
				</view>
			</view>
			<!-- #endif -->
			<!-- #ifdef MP -->
			<view class="mp-header">
				<view class="sys-head" :style="{ height: statusBarHeight }"></view>
				<view class="serch-box" style="height: 40px;">
					<view class="serch-wrapper flex">
						<view class="logo">
							<image :src="logoUrl" mode=""></image>
						</view>
						<navigator url="/pages/goods_search/index" class="input" hover-class="none"><text
								class="iconfont icon-xiazai5"></text>
							搜索商品</navigator>
					</view>
				</view>
			</view>
			<!-- #endif -->
			<!-- 首页展示 -->
			<view class="page_content" :style="'margin-top:'+(marTop)+'px;'" v-if="navIndex == 0">
				<view class="mp-bg"></view>
				<!-- banner -->
				<view class="swiper" v-if="imgUrls.length">
					<swiper indicator-dots="true" :autoplay="true" :circular="circular" :interval="interval"
						:duration="duration" indicator-color="rgba(255,255,255,0.6)" indicator-active-color="#fff">
						<block v-for="(item,index) in imgUrls" :key="index">
							<swiper-item>
								<navigator :url='item.url' class='slide-navigator acea-row row-between-wrapper'
									hover-class='none'>
									<image :src="item.pic" class="slide-image" lazy-load></image>
								</navigator>
							</swiper-item>
						</block>
					</swiper>
				</view>
				<!-- 新闻简报 -->
				<view class='notice acea-row row-middle row-between' v-if="roll.length">
					<view class="pic">
						<image src="/static/images/xinjian.png"></image>
					</view>
					<text class='line'>|</text>
					<view class='swipers'>
						<swiper :indicator-dots="indicatorDots" :autoplay="autoplay" interval="2500" duration="500" vertical="true"
						 circular="true">
							<block v-for="(item,index) in roll" :key='index'>
								<swiper-item>
									<navigator class='item' :url='item.url' hover-class='none'>
										<view class='line1'>{{item.info}}</view>
									</navigator>
								</swiper-item>
							</block>
						</swiper>
					</view>
					<view class="iconfont icon-xiangyou"></view>
				</view>
				<!-- menu -->
				<view class='nav acea-row' v-if="menus.length">
					<block v-for="(item,index) in menus" :key="index">
						<navigator class='item' v-if="item.show == '1'" :url='item.url' open-type='switchTab'
							hover-class='none'>
							<view class='pictrue'>
								<image :src='item.pic'></image>
							</view>
							<view class="menu-txt">{{item.name}}</view>
						</navigator>
						<navigator class='item' v-else :url='item.url' hover-class='none'>
							<view class='pictrue'>
								<image :src='item.pic'></image>
							</view>
							<view class="menu-txt">{{item.name}}</view>
						</navigator>
					</block>
				</view>
				<!-- 优惠券 -->
				<view class="couponIndex" v-if="couponList.length>0">
					<view class="acea-row" style="height: 100%;">
						<view class="titBox">
							<view class="tit1">领取优惠券</view>
							<view class="tit2">福利大礼包，省了又省</view>
							<navigator class='item' url='/pages/users/user_get_coupon/index' hover-class='none'>
								<view class="tit3">查看全部 <text class="iconfont icon-xiangyou"></text></view>
							</navigator>
						</view>
						<view class="listBox acea-row">
							<view class="list" :class='item.isGet ? "listHui" : "listActive" ' v-for="(item, index) in couponList" :key="index">
								<view class="tit line1" :class='item.isGet ? "pricehui" : "titActive" '>{{item.name}}</view>
								<view class="price" :class='item.isGet ? "pricehui" : "icon-color" '>{{item.money?Number(item.money):''}}<text class="yuan">元</text></view>
								<view class="ling" v-if="!item.isGet" :class='item.isGet ? "pricehui" : "icon-color" '  @click="getCoupon(item.id,index)">领取</view>
								<view class="ling" v-else :class='item.isGet ? "pricehui fonthui" : "icon-color" '>已领取</view>
								<view class="priceM">满{{item.minPrice?Number(item.minPrice):''}}元可用</view>
							</view>
						</view>

					</view>
				</view>
				<!-- 活动-->
				<a_seckill></a_seckill>
				<b_combination></b_combination>
				<c_bargain></c_bargain>

				<!-- 精品推荐 -->
				<view class='boutique'>
					<swiper autoplay="true" indicator-dots="true" :circular="circular" :interval="interval"
						:duration="duration" indicator-color="rgba(0,0,0,.4)" indicator-active-color="#fff">
						<block v-for="(item,index) in bastBanner" :key="index">
							<swiper-item>
								<navigator :url='item.url' style='width:100%;height:100%;' hover-class='none'>
									<image :src="item.pic" class="slide-image" />
								</navigator>
							</swiper-item>
						</block>
					</swiper>
				</view>

				<!-- 首页推荐 -->
				<!-- :class="iSshowH?'on':''" -->
				<view class="sticky-box" :style="'top:'+(marTop)+'px;'">
					<scroll-view class="scroll-view_H" style="width: 100%;" scroll-x="true" scroll-with-animation
						:scroll-left="tabsScrollLeft" @scroll="scroll">
						<view class="tab nav-bd" id="tab_list">
							<view id="tab_item" :class="{ 'active': listActive == index}" class="item"
								v-for="(item, index) in explosiveMoney" :key="index" @click="ProductNavTab(item,index)">
								<view class="txt">{{item.title}}</view>
								<view class="label">{{item.info}}</view>
							</view>
						</view>
					</scroll-view>
				</view>

				<!-- 首发新品 -->
				<view class="index-product-wrapper" :class="iSshowH?'on':''">
					<view class="list-box animated" :class='tempArr.length > 0?"fadeIn on":""'>
						<view class="item" v-for="(item,index) in tempArr" :key="index" @click="goDetail(item)">
							<view class="pictrue">
								<span class="pictrue_log pictrue_log_class"
									v-if="item.activityH5 && item.activityH5.type === '1'">秒杀</span>
								<span class="pictrue_log pictrue_log_class"
									v-if="item.activityH5 && item.activityH5.type === '2'">砍价</span>
								<span class="pictrue_log pictrue_log_class"
									v-if="item.activityH5 && item.activityH5.type === '3'">拼团</span>
								<image :src="item.image" mode=""></image>
							</view>
							<view class="text-info">
								<view class="title line1">{{item.storeName}}</view>
								<view class="old-price"><text>¥{{item.otPrice}}</text></view>
								<view class="price">
									<text>￥</text>{{item.price}}
									<view class="txt" v-if="item.checkCoupon">券</view>
								</view>
							</view>
						</view>
					</view>
					<view class='loadingicon acea-row row-center-wrapper' v-if="goodScroll">
						<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>
					</view>
					<view class="mores-txt flex" v-if="!goodScroll">
						<text>我是有底线的</text>
					</view>
				</view>
			</view>
			<!-- 分类页 -->
			<view class="productList" v-if="navIndex>0" :style="'margin-top:'+prodeuctTop+'px'">
				<block v-if="sortProduct.length>0">
					<view class='list acea-row row-between-wrapper' :class='is_switch==true?"":"on"'>
						<view class='item' :class='is_switch==true?"":"on"' hover-class='none'
							v-for="(item,index) in sortProduct" :key="index" @click="godDetail(item)">
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
								<view class='money font-color' :class='is_switch==true?"":"on"'>￥<text
										class='num'>{{item.price}}</text></view>
								<view class='vip acea-row row-between-wrapper' :class='is_switch==true?"":"on"'>
									<view class='vip-money' v-if="item.vipPrice && item.vipPrice > 0">￥{{item.vipPrice}}
										<image src='../../static/images/vip.png'></image>
									</view>
									<view>已售{{Number(item.sales) + Number(item.ficti) || 0 }}{{item.unitName}}</view>
								</view>
							</view>
						</view>
						<view class='loadingicon acea-row row-center-wrapper' v-if='sortProduct.length > 0'>
							<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
						</view>
					</view>
				</block>
				<Loading :loaded="loadend" :loading="loading"></Loading>
				<block v-if="sortProduct.length == 0">
					<view class="noCommodity">
						<view class='pictrue'>
							<image src='../../static/images/noShopper.png'></image>
						</view>
						<recommend :hostProduct="hostProduct"></recommend>
					</view>
				</block>

			</view>
			<!-- <coupon-window :window='window' :couponList="couponList" @onColse="onColse"></coupon-window> -->
			<!-- #ifdef MP -->
			<!-- <authorize @onLoadFun="onLoadFun" :isAuto="isAuto" :isShowAuth="isShowAuth" @authColse="authColse" :isGoIndex="false"></authorize> -->
			<!-- #endif -->
		</view>
	</view>
</template>

<script>
	import Auth from '@/libs/wechat';
	import Cache from '../../utils/cache';
	var statusBarHeight = uni.getSystemInfoSync().statusBarHeight + 'px';
	let app = getApp();
	import {
		getIndexData,
		getCoupons,
		setCouponReceive
	} from '@/api/api.js';
	// #ifdef MP-WEIXIN
	import {
		getTemlIds
	} from '@/api/api.js';
	// import {
	// 	SUBSCRIBE_MESSAGE,
	// 	TIPS_KEY
	// } from '@/config/cache';
	// #endif
	// #ifdef H5  
	import {
		follow
	} from '@/api/public.js';
	// #endif
	import {
		getShare
	} from '@/api/public.js';
	import a_seckill from './components/a_seckill';
	import b_combination from './components/b_combination';
	import c_bargain from './components/c_bargain';
	import goodList from '@/components/goodList';
	import promotionGood from '@/components/promotionGood';
	import couponWindow from '@/components/couponWindow';
	import ClipboardJS from "@/plugin/clipboard/clipboard.js";
	import {
		goShopDetail
	} from '@/libs/order.js'
	import {
		mapGetters
	} from "vuex";
	import tabNav from '@/components/tabNav.vue'
	import countDown from '@/components/countDown';
	import {
		getCategoryList,
		getProductslist,
		getProductHot,
		getGroomList
	} from '@/api/store.js';
	// import {
	// 	setVisit
	// } from '@/api/user.js'
	import recommend from '@/components/recommend';
	// #ifdef MP
	import authorize from '@/components/Authorize';
	// #endif
	import {
		silenceBindingSpread
	} from '@/utils';
	// #ifndef MP
	import {
		kefuConfig
	} from "@/api/public";
	import {
		getWechatConfig
	} from "@/api/public";
	// #endif
	import Loading from '@/components/Loading/index.vue';
	const arrTemp = ["beforePay", "afterPay", "refundApply", "beforeRecharge", "createBargain", "pink"];
	export default {
		computed: mapGetters(['isLogin', 'uid']),
		components: {
			tabNav,
			goodList,
			promotionGood,
			couponWindow,
			countDown,
			a_seckill,
			b_combination,
			c_bargain,
			recommend,
			// #ifdef MP
			authorize,
			// #endif
			Loading
		},
		data() {
			return {
				loaded: false,
				loading: false,
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				statusBarHeight: statusBarHeight,
				navIndex: 0,
				navTop: [],
				followUrl: "",
				followHid: true,
				followCode: false,
				logoUrl: "",
				imgUrls: [],
				itemNew: [],
				menus: [],
				bastBanner: [],
				bastInfo: '',
				fastInfo: '',
				fastList: [],
				firstInfo: '',
				salesInfo: '',
				indicatorDots: false,
				circular: true,
				autoplay: true,
				interval: 3000,
				duration: 500,
				window: false,
				iShidden: false,
				navH: "",
				newGoodsBananr: '',
				couponList: [],
				liveList: [],
				hotList: [{
					pic: '/static/images/hot_001.png'
				}, {
					pic: '/static/images/hot_002.png'
				}, {
					pic: '/static/images/hot_003.png'
				}],
				ProductNavindex: 0,
				marTop: 0,
				childID: 0,
				loadend: false,
				loadTitle: '加载更多',
				sortProduct: [],
				where: {
					cid: 0,
					page: 1,
					limit: 10,
				},
				is_switch: true,
				hostProduct: [],
				hotPage: 1,
				hotLimit: 10,
				hotScroll: false,
				explosiveMoney: [],
				prodeuctTop: 0,
				searchH: 0,
				isFixed: false,
				goodType: 0, //精品推荐Type
				goodScroll: true, //精品推荐开关
				params: { //精品推荐分页
					page: 1,
					limit: 10,
				},
				tempArr: [], //精品推荐临时数组
				roll: [], // 新闻简报
				site_name: '', //首页title
				iSshowH: false,
				configApi: {}, //分享类容配置
				spikeList: [], // 秒杀
				point: '',
				privacyStatus: false, // 隐私政策是否同意过
				tabsScrollLeft: 0, // tabs当前偏移量
				scrollLeft: 0,
				lineColor: 'red',
				lineStyle: {}, // 下划线位置--动态甲酸
				listActive: 0, // 当前选中项

				duration: 0.2 // 下划线动画时长
			}
		},
		watch: {
			ProductNavindex(newVal) { // 监听当前选中项
				this.setTabList()
			},
			listActive(newVal) { // 监听当前选中项
				this.setTabList()
			}
		},
		mounted() {
			this.setTabList()
		},
		onLoad() {
			var that = this;
			// 获取系统信息
			uni.getSystemInfo({
				success(res) {
					that.$store.commit("SYSTEM_PLATFORM", res.platform);
					console.log(res.platform) //手机牌子
					console.log(res.model) //手机型号
					console.log(res.screenWidth) //屏幕宽度
					console.log(res.screenHeight) //屏幕高度

				}
			});
			uni.getLocation({
				type: 'gcj02',
				altitude: true,
				geocode: true,
				success: function(res) {
					try {
						uni.setStorageSync('user_latitude', res.latitude);
						uni.setStorageSync('user_longitude', res.longitude);
					} catch {}
				}
			});
			let self = this
			// #ifdef MP
			// 获取小程序头部高度
			this.navH = app.globalData.navHeight;
			let info = uni.createSelectorQuery().select(".mp-header");
			info.boundingClientRect(function(data) {
				self.marTop = data.height
				self.poTop = Number(data.height) + 84
			}).exec()
			// #endif
			// #ifndef MP
			this.navH = 0;
			// #endif
			this.isLogin && silenceBindingSpread();
			// Promise.all([this.getAllCategory(), this.getIndexConfig()
			// 	// , this.setVisit()
			// ]);
			this.getIndexConfig();
			// #ifdef MP
			this.getTemlIds()
			// #endif
		},
		onShow() {
			let self = this
			uni.setNavigationBarTitle({
				title: self.site_name
			})
		},
		methods: {
			getCoupon: function(id, index) {
				let that = this;
				//领取优惠券
				setCouponReceive(id).then(function(res) {
					that.$set(that.couponList[index], 'isGet', true);
					that.$util.Tips({
						title: '领取成功'
					});
				}, function(res) {
					return that.$util.Tips({
						title: res
					});
				})
			},
			clickSort(index) {
				this.listActive = index
			},
			// scroll-view滑动事件
			scroll(e) {
				this.scrollLeft = e.detail.scrollLeft;
			},
			setTabList() {
				this.$nextTick(() => {
					//this.setLine()
					this.scrollIntoView()
				})
			},
			// 计算tabs位置
			scrollIntoView() { // item滚动
				let lineLeft = 0;
				this.getElementData('#tab_list', (data) => {
					let list = data[0]
					this.getElementData(`#tab_item`, (data) => {
						let el = data[this.listActive]
						lineLeft = el.width / 2 + (-list.left) + el.left - list.width / 2 - this.scrollLeft
						this.tabsScrollLeft = this.scrollLeft + lineLeft
					})
				})
			},
			//  计算下划线位置
			setLine() {
				let lineWidth = 0,
					lineLeft = 0
				this.getElementData(`#tab_item`, (data) => {
					let el = data[this.listActive]
					lineWidth = el.width / 2
					// lineLeft = el.width * (this.currentIndex + 0.5)  // 此种只能针对每个item长度一致的
					lineLeft = el.width / 2 + (-data[0].left) + el.left
					this.lineStyle = {
						width: `${lineWidth}px`,
						transform: `translateX(${lineLeft}px) translateX(-50%)`,
						transitionDuration: `${this.duration}s`
					};
				})
			},
			getElementData(el, callback) {
				uni.createSelectorQuery().in(this).selectAll(el).boundingClientRect().exec((data) => {
					callback(data[0]);
				});
			},
			xieyiApp() {
				uni.navigateTo({
					url: '/pages/users/web_page/index?webUel=https://admin.java.crmeb.net/useragreement/xieyi.html&title=协议内容'
				})
			},
			// #ifdef MP
			getTemlIds() {
				for (var i in arrTemp) {
					this.getTem(arrTemp[i]);
				}
			},
			getTem(data) {
				getTemlIds({
					type: data
				}).then(res => {
					if (res.data) {
						let arr = res.data.map((item) => {
							return item.tempId
						})
						wx.setStorageSync('tempID' + data, arr);
					}
				})
			},
			// #endif
			// 关闭优惠券弹窗
			onColse() {
				this.$set(this, "window", false);
			},
			//分类产品
			get_product_list: function(isPage) {
				let that = this;
				if (that.loadend) return;
				if (that.loading) return;
				that.loading = true;
				that.loadTitle = '';
				getProductslist(that.where).then(res => {
					if (that.where.page == 1 && res.data.list.length == 0) {
						that.hotScroll = false
						that.hotPage = 1
						that.hostProduct = []
						this.get_host_product()
					}
					let list = res.data.list;
					let productList = that.$util.SplitArray(list, that.sortProduct);
					let loadend = list.length < that.where.limit;
					that.loadend = loadend;
					that.loading = false;
					that.loadTitle = loadend ? '已全部加载' : '加载更多';
					that.$set(that, 'sortProduct', productList);
					that.$set(that.where, 'page', that.where.page + 1);
				}).catch(err => {
					that.loading = false;
					that.loadTitle = '加载更多';
				});
			},
			/**
			 * 获取我的推荐
			 */
			get_host_product: function() {
				let that = this;
				that.loading = true;

				if (that.hotScroll) return
				getProductHot(
					that.hotPage,
					that.hotLimit,
				).then(res => {
					that.hotPage++
					that.hotScroll = res.data.list.length < that.hotLimit
					that.hostProduct = that.hostProduct.concat(res.data.list)
				});
			},

			// 首页数据
			getIndexConfig: function() {
				let that = this;
				getIndexData().then(res => {
					uni.setNavigationBarTitle({
						title: '首页'
					})
					that.$set(that, "logoUrl", res.data.logoUrl);
					that.$set(that, "site_name", '首页');
					that.$set(that, "imgUrls", res.data.banner);
					that.$set(that, "menus", res.data.menus);
					that.$set(that, "roll", res.data.roll ? res.data.roll : []);
					that.$set(that, "bastBanner", res.data.bastBanner || []);
					that.$set(that, "couponList", res.data.couponList || []);
					// #ifdef H5
					that.$store.commit("SET_CHATURL", res.data.yzfUrl);
					Cache.set('chatUrl', res.data.yzfUrl);
					// #endif
					that.$set(that, "explosiveMoney", res.data.explosiveMoney);
					that.goodType = res.data.explosiveMoney[0].type
					this.getGroomList();
					this.shareApi();
					// #ifdef H5
					// that.subscribe = res.data.subscribe;
					// #endif

				})
			},
			getcouponList() {
				let that = this;
				getCoupons({
					page: 1,
					limit: 6
				}).then(res => {
					that.$set(that, "couponList", res.data);
					// 小程序判断用户是否授权；
					// #ifdef MP
					uni.getSetting({
						success(res) {
							if (!res.authSetting['scope.userInfo']) {
								that.window = that.couponList.length ? true : false;
							} else {
								that.window = false;
								that.iShidden = true;
							}
						}
					});
					// #endif
					// #ifndef MP
					if (that.isLogin) {
						that.window = false;
					} else {
						that.window = res.data.length ? true : false;
					}
					// #endif
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
				});
			},
			shareApi: function() {
				getShare().then(res => {
					this.$set(this, 'configApi', res.data);
					// #ifdef H5
					this.setOpenShare(res.data);
					// #endif
				})
			},
			getChatUrL() {
				kefuConfig().then(res => {
					let data = res.data;
					this.$store.commit("SET_CHATURL", data.yzfUrl);
					Cache.set('chatUrl', data.yzfUrl);
				})
			},
			// setOpenShare:function(mss){
			// 	getWechatConfig()
			// 		.then(res => {
			// 			let data = res.data;
			// 			console.log('调试配置');
			// 			console.log(data);
			// 			WechatJSSDK.config({
			// 				  debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
			// 				  appId: data.appId, // 必填，公众号的唯一标识
			// 				  timestamp: data.timestamp, // 必填，生成签名的时间戳
			// 				  nonceStr: data.nonceStr, // 必填，生成签名的随机串
			// 				  signature: data.signature,// 必填，签名
			// 				  jsApiList: data.jsApiList // 必填，需要使用的JS接口列表
			// 			});
			// 			WechatJSSDK.error(function(res){
			// 				console.log('error')
			// 			   console.log(res);
			// 			});
			// 			WechatJSSDK.ready(function () {   //需在用户可能点击分享按钮前就先调用
			// 			  WechatJSSDK.onMenuShareAppMessage({ 
			// 			    title: mss.title, // 分享标题
			// 			    desc: mss.synopsis, // 分享描述
			// 			    link: location.href, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
			// 			    imgUrl: mss.img, // 分享图标
			// 			    success: function (res) {
			// 					console.log('success');
			// 					console.log(res);
			// 			      // 设置成功
			// 			    },
			// 				fail:function(err){
			// 					console.log('err');
			// 					console.log(err);
			// 				}	
			// 			  })
			// 			}); 
			// 		}).catch(err => {
			// 			console.log(err);
			// 			reject(err);
			// 		});
			// },
			// 微信分享；
			setOpenShare: function(data) {
				let that = this;
				if (that.$wechat.isWeixin()) {
					let configAppMessage = {
						desc: data.synopsis,
						title: data.title,
						link: location.href,
						imgUrl: data.img
					};
					that.$wechat.wechatEvevt(["updateAppMessageShareData", "updateTimelineShareData"],
						configAppMessage);
				}
			},
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e
			},
			// 授权回调
			onLoadFun() {

			},
			// 首发新品切换
			ProductNavTab(item, index) {
				this.listActive = index
				this.goodType = item.type
				this.listActive = index
				this.ProductNavindex = index
				this.tempArr = []
				this.params.page = 1
				this.goodScroll = true
				let onloadH = true
				this.getGroomList(onloadH)
			},
			// 首发新品详情
			goDetail(item) {
				if (item.activityH5 && item.activityH5.type === "2" && !this.isLogin) {
					toLogin();
				} else {
					goShopDetail(item, this.uid).then(res => {
						uni.navigateTo({
							url: `/pages/goods_details/index?id=${item.id}`
						})
					})
				}
			},
			// 分类详情
			godDetail(item) {
				goShopDetail(item, this.uid).then(res => {
					uni.navigateTo({
						url: `/pages/goods_details/index?id=${item.id}`
					})
				})
			},
			// 精品推荐
			getGroomList(onloadH) {
				this.loading = true
				let type = this.goodType;
				if (!this.goodScroll) return
				if (onloadH) {
					this.iSshowH = true
				}
				getGroomList(type, this.params).then(({
					data
				}) => {
					this.iSshowH = false
					this.loading = false
					this.goodScroll = data.list.length >= this.params.limit
					this.params.page++
					this.tempArr = this.tempArr.concat(data.list)
				})
			}
		},
		mounted() {
			let self = this
			// #ifdef H5
			//self.getChatUrL();
			// 获取H5 搜索框高度
			let appSearchH = uni.createSelectorQuery().select(".serch-wrapper");
			appSearchH.boundingClientRect(function(data) {
				self.searchH = data.height
			}).exec()
			// #endif
		},
		/**
		 * 用户点击右上角分享
		 */
		// #ifdef MP
		onShareAppMessage: function() {
			return {
				title: this.configApi.title,
				imageUrl: this.configApi.img,
				desc: this.configApi.synopsis,
				path: '/pages/index/index'
			};
		},
		// #endif
		// 滚动到底部
		onReachBottom() {

			if (this.navIndex == 0) {
				// 首页加载更多
				if (this.params.page != 1) {
					this.getGroomList();
				}
			} else {
				// 分类栏目加载更多
				if (this.sortProduct.length > 0) {
					//this.get_product_list();
				} else {
					this.get_host_product();
				}
			}
		},
		// 滚动监听
		// onPageScroll(e) {
		// 	console.log(e)
		// 	let self = this
		// 	if (e.scrollTop == 1276) {
		// 		self.isFixed = true
		// 	}
		// }
	}
</script>
<style>
	page {
		display: flex;
		flex-direction: column;
		height: 100%;
		/* #ifdef H5 */
		background-color: #fff;
		/* #endif */

	}
</style>
<style lang="scss">
	.notice{
		width: 100%;
		height: 70rpx;
		border-radius: 10rpx;
		background-color: #fff;
		margin-bottom: 25rpx;
		line-height: 70rpx;
		padding: 0 14rpx;
		.line {
			color: #CCCCCC;
		}
		.pic{
			width: 130rpx;
			height: 36rpx;
			image{
				width: 100%;
				height: 100%;
				display: block !important;
			}
		}
		.swipers {
			height: 100%;
			width: 444rpx;
			overflow: hidden;
			swiper {
				height: 100%;
				width: 100%;
				overflow: hidden;
				font-size: 22rpx;
				color: #333333;
			}
		}
		.iconfont {
			color: #999999;
			font-size: 20rpx;
		} 
	}
	.couponIndex {
		width: auto;
		height: 238rpx;
		background-image: url('~@/static/images/yhjsy.png');
		background-size: 100% 100%;
		padding-left: 42rpx;
		margin-bottom: 30rpx;
		
		.titBox {
			padding: 47rpx 0;
			text-align: center;
			height: 100%;

			.tit1 {
				color: #FFEBD2;
				font-size: 34rpx;
				font-weight: 600;
			}

			.tit2 {
				color: #FFEBD2;
				font-size: 22rpx;
				margin:10rpx 0 26rpx 0;
			}

			.tit3 {
				color: #FFDAAF;
				font-size: 24rpx;
				.iconfont {
					font-size: 20rpx;
				}
			}
		}

		.listBox {
			padding: 14rpx 0;

			.listActive {
				background-image: url('~@/static/images/lingyhj.png');
				background-size: 100% 100%;
			}

			.listHui {
				background-image: url('~@/static/images/weiling.png');
				background-size: 100% 100%;
			}

			.list {
				width: 170rpx;
				height: 210rpx;
				padding: 16rpx 0;
				text-align: center;
				margin-left: 24rpx;

				.tit {
					font-size: 18rpx;
					padding: 0 26rpx;
				}

				.titActive {
					color: #C99959;
				}

				.price {
					font-size: 46rpx;
					font-weight: 900;
					margin-top: 4rpx;
				}

				.pricehui {
					color: #B2B2B2;
				}
                .fonthui{
					background-color: #F5F5F5 !important;
				}
				.yuan {
					font-size: 24rpx;
				}

				.ling {
					font-size: 24rpx;
					margin-top: 9.5rpx;
					width: 102rpx;
					height: 36rpx;
					line-height: 36rpx;
					background-color: #FFE5C7;
					border-radius: 28rpx;
					margin: auto;
				}

				.priceM {
					color: #FFDAAF;
					font-size: 22rpx;
					margin-top: 14rpx;
				}
			}
		}
	}

	.sticky-box {
		/* #ifndef APP-PLUS-NVUE */
		display: flex;
		position: -webkit-sticky;
		/* #endif */
		position: sticky;
		/* #ifdef H5*/
		top: var(--window-top);
		/* #endif */
		
		z-index: 99;
		flex-direction: row;
		margin: 0px;
		background: #f5f5f5;
		padding: 30rpx 0;
	}

	.listAll {
		width: 20%;
		text-indent: 62rpx;
		font-size: 30rpx;
		border-left: 1px #eee solid;
		margin: 1% 0;
		padding: 5rpx;
		position: relative;

		image {
			position: absolute;
			left: 20rpx;
			top: 8rpx;
		}
	}

	.tab {
		position: relative;
		display: flex;
		font-size: 28rpx;
		white-space: nowrap;

		&__item {
			flex: 1;
			padding: 0 20rpx;
			text-align: center;
			height: 60rpx;
			line-height: 60rpx;
			color: #666;

			&.active {
				color: #09C2C9;
			}
		}
	}

	.tab__line {
		display: block;
		height: 6rpx;
		position: absolute;
		bottom: 0;
		left: 0;
		z-index: 1;
		border-radius: 3rpx;
		position: relative;
		background: #2FC6CD;
	}

	.scroll-view_H {
		/* 文本不会换行，文本会在在同一行上继续，直到遇到 <br> 标签为止。 */
		white-space: nowrap;
		width: 100%;
	}


	.privacy-wrapper {
		z-index: 999;
		position: fixed;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
		background: #7F7F7F;

		.privacy-box {
			position: absolute;
			left: 50%;
			top: 50%;
			transform: translate(-50%, -50%);
			width: 560rpx;
			padding: 50rpx 45rpx 0;
			background: #fff;
			border-radius: 20rpx;

			.title {
				text-align: center;
				font-size: 32rpx;
				text-align: center;
				color: #333;
				font-weight: 700;
			}

			.content {
				margin-top: 20rpx;
				line-height: 1.5;
				font-size: 26rpx;
				color: #666;
				text-indent: 54rpx;

				i {
					font-style: normal;
					color: $theme-color;
				}
			}

			.btn-box {
				margin-top: 40rpx;
				text-align: center;
				font-size: 30rpx;

				.btn-item {
					height: 82rpx;
					line-height: 82rpx;
					background: linear-gradient(90deg, #F67A38 0%, #F11B09 100%);
					color: #fff;
					border-radius: 41rpx;
				}

				.btn {
					padding: 30rpx 0;
				}
			}
		}
	}

	.page-index {
		display: flex;
		flex-direction: column;
		min-height: 100%;
		background: linear-gradient(180deg, #fff 0%, #f5f5f5 100%);

		.header {
			width: 100%;
			background-color: $theme-color;
			padding: 28rpx 30rpx;

			.serch-wrapper {
				align-items: center;


				.logo {
					width: 118rpx;
					height: 42rpx;
					margin-right: 24rpx;
				}

				image {
					width: 118rpx;
					height: 42rpx;
				}

				.input {
					display: flex;
					align-items: center;
					width: 546rpx;
					height: 58rpx;
					padding: 0 0 0 30rpx;
					background: rgba(247, 247, 247, 1);
					border: 1px solid rgba(241, 241, 241, 1);
					border-radius: 29rpx;
					color: #BBBBBB;
					font-size: 26rpx;

					.iconfont {
						margin-right: 20rpx;
						font-size: 26rpx;
						color: #666666;
					}
				}
			}

			.tabNav {
				padding-top: 24rpx;
			}
		}

		/* #ifdef MP */
		.mp-header {
			z-index: 999;
			position: fixed;
			left: 0;
			top: 0;
			width: 100%;
			/* #ifdef H5 */
			padding-bottom: 20rpx;
			/* #endif */
			background-color: $theme-color;

			.serch-wrapper {
				height: 100%;
				align-items: center;
				padding: 0 50rpx 0 53rpx;

				image {
					width: 118rpx;
					height: 42rpx;
					margin-right: 30rpx;
				}

				.input {
					display: flex;
					align-items: center;
					/* #ifdef MP */
					width: 305rpx;
					/* #endif */
					height: 50rpx;
					padding: 0 0 0 30rpx;
					background: rgba(247, 247, 247, 1);
					border: 1px solid rgba(241, 241, 241, 1);
					border-radius: 29rpx;
					color: #BBBBBB;
					font-size: 28rpx;

					.iconfont {
						margin-right: 20rpx;
					}
				}
			}
		}

		/* #endif */

		.page_content {
			background-color: #f5f5f5;
			/* #ifdef H5 */
			// margin-top: 20rpx !important;
			/* #endif */
			padding: 0 30rpx;

			.swiper {
				position: relative;
				width: 100%;
				height: 280rpx;
				margin: 0 auto;
				border-radius: 10rpx;
				overflow: hidden;
				margin-bottom: 25rpx;
				/* #ifdef MP */
				z-index: 10;
				margin-top: 20rpx;

				/* #endif */
				swiper,
				.swiper-item,
				image {
					width: 100%;
					height: 280rpx;
					border-radius: 10rpx;
				}
			}

			.nav {
				padding-bottom: 26rpx;
				background: #fff;
				opacity: 1;
				border-radius: 14rpx;
				width: 100%;
				margin-bottom: 30rpx;

				.item {
					display: flex;
					flex-direction: column;
					align-items: center;
					justify-content: center;
					width: 20%;
					margin-top: 30rpx;

					image {
						width: 82rpx;
						height: 82rpx;
					}
				}
			}

			.live-wrapper {
				position: relative;
				width: 100%;
				overflow: hidden;
				border-radius: 16rpx;

				image {
					width: 100%;
					height: 400rpx;
				}

				.live-top {
					z-index: 20;
					position: absolute;
					left: 0;
					top: 0;
					display: flex;
					align-items: center;
					justify-content: center;
					color: #fff;
					width: 180rpx;
					height: 54rpx;
					border-radius: 0rpx 0px 18rpx 0px;

					image {
						width: 30rpx;
						height: 30rpx;
						margin-right: 10rpx;
						/* #ifdef H5 */
						display: block;
						/* #endif */
					}
				}

				.live-title {
					position: absolute;
					left: 0;
					bottom: 6rpx;
					width: 100%;
					height: 70rpx;
					line-height: 70rpx;
					text-align: center;
					font-size: 30rpx;
					color: #fff;
					background: rgba(0, 0, 0, .35);
				}

				&.mores {
					width: 100%;

					.item {
						position: relative;
						width: 320rpx;
						display: inline-block;
						border-radius: 16rpx;
						overflow: hidden;
						margin-right: 20rpx;

						image {
							width: 320rpx;
							height: 180rpx;
							border-radius: 16rpx;
						}

						.live-title {
							height: 40rpx;
							line-height: 40rpx;
							text-align: center;
							font-size: 22rpx;
						}

						.live-top {
							width: 120rpx;
							height: 36rpx;
							font-size: 22rpx;

							image {
								width: 20rpx;
								height: 20rpx;
							}
						}
					}
				}
			}

			.hot-img {
				/* #ifndef H5 */
				margin-top: 40rpx;

				/* #endif */
				swiper,
				swiper-item image {
					width: 100%;
					height: 225rpx;
				}
			}

			.explosion {
				width: 100%;
				height: 500rpx;
				margin-top: 20rpx;
				padding: 30rpx 20rpx;
				background-image: url('~@/static/images/explosion.png');
				background-size: 100% 100%;
				border-radius: 13px;

				.hd {
					display: flex;
					align-items: center;

					image {
						width: 147rpx;
						height: 35rpx;
						margin-right: 20rpx;
					}

					.txt {
						width: 220rpx;
						height: 36rpx;
						background: linear-gradient(90deg, rgba(255, 168, 0, 1) 0%, rgba(255, 34, 15, 1) 100%);
						border-radius: 26rpx 0px 26rpx 0px;
						color: #fff;
						text-align: center;
						font-size: 22rpx;
						box-shadow: 3px 1px 1px 1px rgba(255, 203, 199, .8);
					}
				}

				.bd {
					display: flex;
					flex-wrap: wrap;
					margin-top: 28rpx;

					.item {
						display: flex;
						align-items: center;
						justify-content: space-between;
						position: relative;
						width: 325rpx;
						height: 180rpx;
						margin-bottom: 20rpx;
						margin-right: 20rpx;
						background-color: #fff;
						border-radius: 16rpx;
						padding: 0 20rpx;
						box-sizing: border-box;

						image {
							width: 160rpx;
							height: 160rpx;
						}

						.con-box {
							display: flex;
							flex-direction: column;
							justify-content: center;
							width: 120rpx;
							height: 100%;

							.title {
								color: #282828;
								font-size: 30rpx;
							}

							.con {
								color: #999999;
								font-size: 20rpx;
							}

							.go {
								display: flex;
								align-items: center;
								justify-content: center;
								margin-top: 10rpx;
								width: 112rpx;
								height: 36rpx;
								border-radius: 18rpx;
								color: #fff;
								font-size: 26rpx;
								font-weight: bold;
								font-style: italic;

								image {
									width: 26rpx;
									height: 26rpx;
								}
							}
						}

						&:first-child .go {
							background: linear-gradient(90deg, rgba(75, 196, 255, 1) 0%, rgba(32, 126, 255, 1) 100%);
						}

						&:nth-child(2) .go {
							background: linear-gradient(90deg, rgba(255, 144, 67, 1) 0%, rgba(255, 83, 29, 1) 100%);
						}

						&:nth-child(3) .go {
							background: linear-gradient(90deg, rgba(150, 225, 135, 1) 0%, rgba(72, 206, 44, 1) 100%);
						}

						&:nth-child(4) .go {
							background: linear-gradient(90deg, rgba(255, 197, 96, 1) 0%, rgba(255, 156, 0, 1) 100%);
						}

						&:nth-child(2n) {
							margin-right: 0;
						}
					}
				}
			}

			.spike-box {
				margin-top: 20rpx;
				padding: 23rpx 20rpx;
				border-radius: 24rpx;
				background-color: #fff;
				overflow: hidden;
				box-shadow: 0px 0px 16px 3px rgba(0, 0, 0, 0.04);

				.hd {
					display: flex;
					align-items: center;
					justify-content: space-between;

					.left {
						display: flex;
						align-items: center;
						width: 500rpx;

						.icon {
							width: 38rpx;
							height: 38rpx;
							margin-right: 8rpx;
						}

						.title {
							width: 134rpx;
							height: 33rpx;
						}
					}

					.more {
						font-size: 26rpx;
						color: #999;

						.iconfont {
							margin-left: 6rpx;
							font-size: 25rpx;
						}
					}
				}

				.spike-wrapper {
					width: 100%;
					margin-top: 27rpx;

					.spike-item {
						display: inline-block;
						width: 222rpx;
						margin-right: 20rpx;

						.img-box {
							position: relative;
							height: 222rpx;

							image {
								width: 100%;
								height: 222rpx;
								border-radius: 16rpx;
							}

							.msg {
								position: absolute;
								left: 10rpx;
								bottom: 16rpx;
								width: 86rpx;
								height: 30rpx;
								background: rgba(255, 255, 255, 1);
								border: 1px solid rgba(255, 109, 96, 1);
								border-radius: 6rpx;
								font-size: 20rpx;
								color: $theme-color;
							}
						}


						.info {
							margin-top: 8rpx;
							padding: 0 10rpx;

							.name {
								font-size: 28rpx;
							}

							.price-box {
								display: flex;
								align-items: center;
								justify-content: center;

								.tips {
									display: flex;
									align-items: center;
									justify-content: center;
									width: 28rpx;
									height: 28rpx;
									background-color: $theme-color;
									color: #fff;
									font-size: 20rpx;
									border-radius: 2px;
								}

								.price {
									display: flex;
									margin-left: 10rpx;
									color: $theme-color;
									font-size: 28rpx;
									font-weight: bold;

									text {
										font-size: 18rpx;
									}
								}
							}
						}
					}
				}
			}

			.barg {
				width: 100%;
				height: 478rpx;
				margin-top: 20rpx;
				padding-left: 20rpx;
				background-image: url('~@/static/images/barg002.png');
				background-size: 100% 100%;

				.title {
					display: flex;
					align-items: center;
					justify-content: center;
					padding-top: 42rpx;

					image {
						width: 463rpx;
						height: 39rpx;
					}
				}

				.barg-swiper {
					margin-top: 43rpx;
					padding-right: 20rpx;

					.wrapper {
						display: flex;
					}

					.list-box {
						flex-shrink: 0;
						width: 210rpx;
						background-color: #fff;
						border-radius: 16rpx;
						overflow: hidden;
						padding-bottom: 18rpx;
						margin-right: 20rpx;

						image {
							width: 100%;
							height: 210rpx;
						}

						.info-txt {
							width: 100%;
							display: flex;
							flex-direction: column;
							align-items: center;
							justify-content: center;
							padding-top: 15rpx;

							.price {
								font-weight: 700;
								color: $theme-color;
							}

							.txt {
								display: flex;
								align-items: center;
								justify-content: center;
								width: 136rpx;
								height: 33rpx;
								margin-top: 10rpx;
								background: linear-gradient(90deg, $bg-star 0%, $bg-end 100%);
								border-radius: 17px;
								font-size: 22rpx;
								color: #fff;

							}
						}
					}

					.more-box {
						flex-shrink: 0;
						display: flex;
						flex-direction: column;
						align-items: center;
						justify-content: center;
						width: 80rpx;
						background-color: #fff;
						border-radius: 16rpx;

						image {
							width: 24rpx;
							height: 24rpx;
							margin-top: 10rpx;
						}

						.txt {
							display: block;
							writing-mode: vertical-lr;
							font-size: 26rpx;
							line-height: 1.2;
						}
					}
				}
			}

			.group-wrapper {
				padding: 26rpx 20rpx;
				margin-top: 20rpx;
				background: #fff;
				border-radius: 24rpx;

				.hd {
					display: flex;
					align-items: center;
					justify-content: space-between;

					.left {
						display: flex;
						align-items: center;

						.icon {
							width: 38rpx;
							height: 38rpx;
							margin-right: 8rpx;
						}

						.title {
							width: 134rpx;
							height: 33rpx;
						}

						.person {
							display: flex;
							align-items: center;
							margin-left: 40rpx;

							.avatar-box {
								display: flex;
								align-items: center;

								image {
									width: 30rpx;
									height: 30rpx;
									border-radius: 50%;
									margin-right: -10rpx;
								}
							}

							.num {
								margin-left: 18rpx;
								font-size: 26rpx;
								color: #999999;
							}
						}
					}

					.more {
						font-size: 26rpx;
						color: #999;

						.iconfont {
							margin-left: 6rpx;
							font-size: 25rpx;
						}
					}
				}

				.group-scroll {
					width: 100%;
					margin-top: 25rpx;

					.group-item {
						display: inline-block;
						width: 222rpx;
						margin-right: 20rpx;
						box-shadow: 0px 2px 6px 2px rgba(0, 0, 0, 0.03);
						border-radius: 16rpx;

						image {
							width: 100%;
							height: 222rpx;
							border-radius: 16rpx 16rpx 0 0;
						}

						.info {
							padding: 8rpx 13rpx;

							.name {
								font-size: 24rpx;
							}

							.price-box {
								display: flex;
								align-items: center;
								margin-top: 10rpx;

								.tips {
									display: flex;
									align-items: center;
									justify-content: center;
									width: 76rpx;
									height: 30rpx;
									margin-right: 6rpx;
									background: linear-gradient(90deg, rgba(255, 0, 0, .1) 0%, rgba(255, 84, 0, .1) 100%);
									border-radius: 2px;
									font-size: 20rpx;
									color: $theme-color;
								}

								.price {
									font-size: 26rpx;
									color: $theme-color;
									font-weight: 700;

									text {
										font-size: 20rpx;
									}
								}
							}

						}

						.bom-btn {
							display: flex;
							align-items: center;
							justify-content: center;
							width: 100%;
							height: 48rpx;
							background: linear-gradient(90deg, $bg-star 0%, $bg-end 100%);
							border-radius: 0px 0px 16rpx 16rpx;
							color: #fff;

						}
					}
				}
			}

			.boutique {
				margin-top: 30rpx;
				height: 140rpx;
				width: 100%;

				swiper,
				swiper-item,
				.slide-image {
					width: 100%;
					height: 140rpx;
					border-radius: 12rpx;
				}
			}


			.nav-bd {
				display: flex;
				align-items: center;
				justify-content: space-between;

				.item {
					display: flex;
					flex-direction: column;
					align-items: center;
					justify-content: center;

					.txt {
						font-size: 32rpx;
						color: #282828;
					}

					.label {
						display: flex;
						align-items: center;
						justify-content: center;
						width: 124rpx;
						height: 32rpx;
						margin-top: 5rpx;
						font-size: 24rpx;
						color: #999;
					}

					&.active {
						color: $theme-color;

						.txt {
							color: $theme-color;
						}

						.label {
							background: linear-gradient(90deg, $bg-star 0%, $bg-end 100%);
							border-radius: 16rpx;
							color: #fff;
						}
					}
				}
			}

			.index-product-wrapper {
				margin-bottom: 110rpx;

				&.on {
					min-height: 1500rpx;
				}

				.list-box {
					display: flex;
					flex-wrap: wrap;
					justify-content: space-between;

					.item {
						width: 335rpx;
						margin-bottom: 20rpx;
						background-color: #fff;
						border-radius: 10rpx;
						overflow: hidden;

						image {
							width: 100%;
							height: 330rpx;
						}

						.text-info {
							padding: 10rpx 20rpx 15rpx;

							.title {
								color: #222222;
							}

							.old-price {
								margin-top: 8rpx;
								font-size: 26rpx;
								color: #AAAAAA;
								text-decoration: line-through;

								text {
									margin-right: 2px;
									font-size: 20rpx;
								}
							}

							.price {
								display: flex;
								align-items: flex-end;
								color: $theme-color;
								font-size: 34rpx;
								font-weight: 800;

								text {
									padding-bottom: 4rpx;
									font-size: 24rpx;
									font-weight: 800;
								}

								.txt {
									display: flex;
									align-items: center;
									justify-content: center;
									width: 28rpx;
									height: 28rpx;
									margin-left: 15rpx;
									margin-bottom: 10rpx;
									border: 1px solid $theme-color;
									border-radius: 4rpx;
									font-size: 22rpx;
									font-weight: normal;
								}
							}
						}
					}

					&.on {
						display: flex;
					}
				}
			}
		}
	}

	.productList {
		/* #ifdef H5 */
		padding-bottom: 140rpx;
		/* #endif */
	}

	.productList .list {
		padding: 0 20rpx;
	}

	.productList .list.on {
		background-color: #fff;
		border-top: 1px solid #f6f6f6;
	}

	.productList .list .item {
		width: 345rpx;
		margin-top: 20rpx;
		background-color: #fff;
		border-radius: 10rpx;
	}

	.productList .list .item.on {
		width: 100%;
		display: flex;
		border-bottom: 1rpx solid #f6f6f6;
		padding: 30rpx 0;
		margin: 0;
	}

	.productList .list .item .pictrue {
		position: relative;
		width: 100%;
		height: 345rpx;
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
		padding: 20rpx 17rpx 26rpx 17rpx;
		font-size: 30rpx;
		color: #222;
	}

	.productList .list .item .text.on {
		width: 508rpx;
		padding: 0 0 0 22rpx;
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

	.productList .list .item .text .vip {
		font-size: 22rpx;
		color: #aaa;
		margin-top: 7rpx;
	}

	.productList .list .item .text .vip.on {
		margin-top: 12rpx;
	}

	.productList .list .item .text .vip .vip-money {
		font-size: 24rpx;
		color: #282828;
		font-weight: bold;
	}

	.productList .list .item .text .vip .vip-money image {
		width: 46rpx;
		height: 21rpx;
		margin-left: 4rpx;
	}

	.pictrue {
		position: relative;
	}

	.fixed {
		z-index: 100;
		position: fixed;
		left: 0;
		top: 0;
		background: linear-gradient(90deg, red 50%, #ff5400 100%);

	}

	.mores-txt {
		width: 100%;
		align-items: center;
		justify-content: center;
		height: 70rpx;
		color: #999;
		font-size: 24rpx;

		.iconfont {
			margin-top: 2rpx;
			font-size: 20rpx;
		}
	}

	.menu-txt {
		font-size: 24rpx;
		color: #454545;
	}

	.mp-bg {
		position: absolute;
		left: 0;
		/* #ifdef H5 */
		top: 98rpx;
		/* #endif */
		width: 100%;
		height: 304rpx;
		background: linear-gradient(180deg, #E93323 0%, #F5F5F5 100%, #751A12 100%);
		// border-radius: 0 0 30rpx 30rpx;
	}
</style>
