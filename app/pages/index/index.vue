<template>
	<view id="home" :data-theme="theme" :style="[pageStyle]">
		<tui-skeleton v-if="showSkeleton"></tui-skeleton>
		<!-- 有网内容 -->
		<view v-if="!errorNetwork">
			<view class="page-index tui-skeleton page_count" :class="{'bgf':navIndex >0}"
				:style="{visibility: showSkeleton ? 'hidden' : 'visible'}">
				<!-- 组合组件 -->
				<homeComb v-if="showHomeComb" :bgInfo="bgInfo" :dataConfig="homeCombData" @changeTab="changeTab" :isScrolled="isScrolled"
					:navIndex="navIndex"></homeComb>
				<!-- 顶部搜索框 -->
				<headerSearch :isScrolled="isScrolled" v-if="showHeaderSerch" :dataConfig="headerSerchCombData"></headerSearch>
				<!-- 分类 -->
				<cateNav v-if="showCateNav" :dataConfig="cateNavData" @changeTab="changeTab"></cateNav>
				<view class="page_content skeleton">
					<view v-if="navIndex === 0">
						<view v-for="(item, index) in styleConfig" :key="index">
							<!-- 新闻简报 -->
							<news v-if="item.name == 'news'&&!item.isHide" :dataConfig="item"></news>
							<!-- 导航组 -->
							<menus v-if="item.name == 'menus'&&!item.isHide" :dataConfig="item"></menus>
							<!-- 文章列表 -->
							<articleList v-if="item.name == 'homeArticle'&&!item.isHide" :dataConfig="item"></articleList>
							<!-- 秒杀 -->
							<seckill-data v-if="item.name == 'seckill'&&!item.isHide" :dataConfig="item"></seckill-data>
							<!-- 优惠券 -->
							<coupon v-if="item.name == 'homeCoupons'&&!item.isHide" :dataConfig="item"></coupon>
							<!-- 图片魔方 -->
							<pictureCube v-if="item.name == 'pictureCube'&&!item.isHide" :dataConfig="item"></pictureCube>
							<!-- 热区 -->
							<hotSpot v-if="item.name == 'homeHotspot'&&!item.isHide" :dataConfig="item"></hotSpot>
							<!-- 轮播图 -->
							<swiperBg v-if="item.name == 'swiperBg'&&!item.isHide" :dataConfig="item"></swiperBg>
							<!-- 视频 -->
							<shortVideo v-if="item.name == 'video'&&!item.isHide" :dataConfig="item"></shortVideo>
							<!-- 拼团 -->
							<group v-if="item.name == 'group'&&!item.isHide" :dataConfig="item"></group>
							<!-- 砍价 -->
							<bargain v-if="item.name == 'bargain'&&!item.isHide" :dataConfig="item"></bargain>
							<!-- 辅助线 -->
							<guide v-if="item.name == 'guide'&&!item.isHide" :dataConfig="item"></guide>
							<!-- 富文本-->
							<rich-text-editor v-if="item.name == 'richTextEditor'&&!item.isHide"
								:dataConfig="item"></rich-text-editor>
							<!-- 辅助空白-->
							<blank-page v-if="item.name == 'blankPage'&&!item.isHide" :dataConfig="item"></blank-page>
							<!-- 标题 -->
							<home-title v-if="item.name == 'titles'&&!item.isHide" :dataConfig="item"></home-title>
							<!-- 商品列表 -->
							<goodList v-if="item.name == 'goodList'&&!item.isHide" :dataConfig="item" @detail="goDetail"></goodList>
							<!-- 选项卡商品列表-->
							<homeTab v-if="item.name == 'homeTab'&&!item.isHide" :dataConfig="item" @detail="goDetail"></homeTab>
						</view>
					</view>

					<!-- 分类页-->
					<view class="productList" v-if="navIndex === 1 && sortList.length>0">
						<view class="sort acea-row" :class="sortList.length ? '' : 'no_pad'"
							:style="{ marginTop: sortMarTop + 'px' }">
							<navigator hover-class="none"
								:url="'/pages/goods/goods_list/index?cid=' + item.id + '&title=' + item.name"
								class="item" v-for="(item, index) in sortList" :key="index" v-if="index<9">
								<view class="pictrue">
									<image :src="item.extra" class='slide-image tui-skeleton-rect'></image>
								</view>
								<view class="text">{{ item.name }}</view>
							</navigator>
							<view class="item" @click="bindMore()" v-if="sortList.length >= 9">
								<view class="pictrues acea-row row-center-wrapper">
									<text class="iconfont icon-gengduo2"></text>
								</view>
								<view class="text">更多</view>
							</view>
						</view>
					</view>
					<!-- 推荐商品，分类商品列表-->
					<recommend v-if="categoryId>0" ref="recommendIndex" :categoryId='categoryId'
						:isShowTitle="isShowTitle" @getRecommendLength="getRecommendLength"></recommend>
					<view class='noCommodity' v-if="isNoCommodity&& navIndex > 0">
						<view class='pictrue'>
							<image :src="urlDomain+'crmebimage/perset/staticImg/noShopper.png'"></image>
						</view>
						<text class="text-ccc">暂无商品</text>
					</view>
					<!-- 备案设置 -->
					<!-- #ifdef H5 -->
					<copyRight></copyRight>
					<!-- #endif -->
					<!-- 底部导航距离，做兼容处理的-->
					<view class="footerBottom-h10"></view>
					<view class="footerBottom"></view>
				</view>

				<!-- #ifdef MP -->
				<aTip :isCustom="true" :text="wxText" :borderR="5"></aTip>
				<!-- #endif -->
			</view>
		</view>
		<!-- 断网内容 -->
		<view v-else>
			<view class="error-network">
				<image class="img" src="./error-network.png"></image>
				<view class="title">网络连接断开</view>
				<view class="con">
					<view class="label">请检查情况：</view>
					<view class="item">· 在设置中是否已开启网络权限</view>
					<view class="item">· 当前是否处于弱网环境</view>
					<view class="item">· 版本是否过低，升级试试吧</view>
				</view>
				<view class="btn" @click="reconnect">重新连接</view>
			</view>
		</view>
		<!-- 底部 -->
		<pageFooter></pageFooter>
	</view>
</template>

<script>
	// +----------------------------------------------------------------------
	// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
	// +----------------------------------------------------------------------
	// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
	// +----------------------------------------------------------------------
	// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
	// +----------------------------------------------------------------------
	// | Author: CRMEB Team <admin@crmeb.com>
	// +----------------------------------------------------------------------
	import tuiSkeleton from '@/components/base/tui-skeleton.vue';
	import Cache from '../../utils/cache';
	import homeComb from '@/components/homeIndex/homeComb';
	import recommend from "@/components/base/recommend.vue";
	import seckillData from "@/components/homeIndex/seckill.vue";
	import aTip from './components/addTips.vue';
	import coupon from "@/components/homeIndex/coupon.vue";
	import menus from "@/components/homeIndex/menus.vue";
	import pictureCube from '@/components/homeIndex/pictureCube'
	import news from '@/components/homeIndex/news'
	import goodList from '@/components/homeIndex/goodList'
	import guide from '@/components/homeIndex/guide';
	import articleList from '@/components/homeIndex/articleList'
	import swiperBg from '@/components/homeIndex/swiperBg'
	import headerSearch from '@/components/homeIndex/headerSearch';
	import cateNav from '@/components/homeIndex/cateNav';
	import richTextEditor from '@/components/homeIndex/richTextEditor';
	import shortVideo from '@/components/homeIndex/video';
	import homeTab from '@/components/homeIndex/homeTab';
	import blankPage from '@/components/homeIndex/blankPage';
	import homeTitle from '@/components/homeIndex/title';
	import hotSpot from '@/components/homeIndex/hotSpot.vue';
	import group from "@/components/homeIndex/group.vue";
	import bargain from "@/components/homeIndex/bargain.vue";
	import pageFooter from "@/components/pageFooter/index.vue";
	import copyRight from './components/copyRight.vue';
	import {
		getIndexData,
		getTheme,
		getAppVersion,
		getCategoryTwo,
		pagediyInfoApi
	} from '@/api/api.js';
	// #ifdef MP-WEIXIN || APP-PLUS
	import {
		getTemlIds
	} from '@/api/api.js';
	// #endif
	import {
		getShare
	} from '@/api/public.js';
	import {
		mapGetters
	} from "vuex";
	import {
		silenceBindingSpread,
	} from '@/utils/index.js';
	import animationType from '@/utils/animationType.js'
	import {
		goProductDetail
	} from "../../libs/order";

	const arrTemp = ["beforePay", "afterPay", "createBargain", "pink"];
	var statusBarHeight = uni.getSystemInfoSync().statusBarHeight + 'px';
	let app = getApp();
	export default {
		computed: mapGetters(['isLogin', 'uid', 'bottomNavigationIsCustom']),
		components: {
			tuiSkeleton,
			aTip,
			homeComb,
			recommend,
			seckillData,
			pageFooter,
			coupon,
			menus,
			pictureCube,
			news,
			goodList,
			articleList,
			swiperBg,
			headerSearch,
			cateNav,
			guide,
			richTextEditor,
			shortVideo,
			homeTab,
			blankPage,
			homeTitle,
			hotSpot,
			group,
			bargain,
			copyRight
		},
		data() {
			return {
				urlDomain: this.$Cache.get("imgHost"),
				isNoCommodity: false,
				isScrolled: false, //是否开始滚动
				categoryId: 0,
				showSkeleton: true, //骨架屏显示隐藏
				isNodes: 0, //控制什么时候开始抓取元素节点,只要数值改变就重新抓取
				statusBarHeight: statusBarHeight,
				navIndex: 0, //判断首页显示内容，1显示分类页和商品，0首页
				ProductNavindex: 0,
				sortProduct: [],
				site_name: '', //首页title
				configApi: {}, //分享类容配置
				listActive: 0, // 当前选中项
				theme: app.globalData.theme,
				imgHost: '', //图片域名地址
				appUpdate: {},
				wxText: "点击添加到我的小程序，微信首页下拉即可访问商城。",
				locationContent: '授权位置信息，提供完整服务',
				sortMpTop: 0,
				// #ifdef APP-PLUS || MP
				isFixed: true,
				// #endif
				// #ifdef H5
				isFixed: false,
				// #endif
				domOffsetTop: 50,
				prodeuctTop: 30,
				sortList: [],
				sortMarTop: 0,
				navHeight: 38,
				domHeight: 0,
				cateNavActive: 0,
				couponModal: false,
				styleConfig: [], //DIY数据
				diyId: 0, //DIYID
				smallPage: false, //是否微页面
				isHeaderSerch: false,
				homeCombData: {}, //组合组件数据
				showCateNav: false, //是否显示分类导航组件
				cateNavData: {}, //分类导航组件数据
				showHomeComb: false, //是否显示组合
				showHeaderSerch: false, //是否显示搜索框
				headerSerchCombData: {}, //搜索框对象
				isShowTitle: false, //是否显示头部标题，同时也判断是否展示分类下的商品列表
				bgColor: '', //背景颜色
				bgPic: '', //背景图片
				bgTabVal: '', //背景图片样式
				windowHeight: 0,
				pageStyle: {},
				isDefault: 1, //是否首页，1是，0不是
				errorNetwork: false, //是否有网络
				bgInfo:{
					colorPicker:'#f5f5f5',
					isBgColor:1,
				},
			}
		},
		//下拉刷新
		onPullDownRefresh() {
			// #ifdef APP-PLUS
			setTimeout(() => {
				uni.reLaunch({
					url: "/pages/index/index"
				});
				uni.stopPullDownRefresh();
			}, 1000)
			// #endif
		},
		onLoad(options) {
			// #ifdef APP-PLUS
			//app刚进入检测有无网络
			this.snycNetWork();
			this.getInitTheme();
			//APP版本检测
			this.appVersionConfig();
			// #endif

			if (options.spread) this.$Cache.set('spread',options.spread);
			if (options.scene) {
				let qrCodeValue = this.$util.getUrlParams(decodeURIComponent(options.scene));
				let mapeMpQrCodeValue = this.$util.formatMpQrCodeData(qrCodeValue);
				app.globalData.spread = mapeMpQrCodeValue.spread;
			}

			//获取浏览器id
			let diyid = 0;
			//check
			diyid = options.id ? options.id : 0;
			//diy数据加载
			this.diyData(diyid, false);

			//首页数据加载
			this.getIndexConfig();

			let that = this;
			this.$nextTick(function() {
				uni.getSystemInfo({
					success: function(res) {
						that.windowHeight = res.windowHeight;
					}
				});
			})
		},
		onShow() {
			// 分类样式3、4跳回首页tabbar处理
			!this.bottomNavigationIsCustom&&uni.showTabBar()
			let self = this;
			// #ifdef APP-PLUS
			setTimeout(() => {
				if (self.appUpdate.openUpgrade == 'true') {
					self.appVersionConfig();
				}
			}, 1000)
			// #endif
			//分销关系绑定，分享需要开启分销员开关，才能绑定成功
			this.getTokenIsExist();
		},
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
			if (e.scrollTop > this.domOffsetTop) {
				this.isScrolled = true;
			}
			if (e.scrollTop < this.domOffsetTop) {
				this.$nextTick(() => {
					this.isScrolled = false;
				});
			}
		},
		methods: {
			//校验token是否有效,true为有效，false为无效
			getTokenIsExist() {
				this.$LoginAuth.getTokenIsExist().then(data => {
					if (data) {
						//绑定关系
						silenceBindingSpread();
					}
				});
			},
			getInitTheme() {
				let that = this;
				// 主题变色
				getTheme().then(resP => {
					this.$Cache.set('theme', `theme${Number(resP.data.value)}`);
				})
			},
			//app刚进入检测有无网络
			snycNetWork() {
				uni.getNetworkType({
					success: res => {
						//res.networkType === 'none'无网络
						this.errorNetwork = res.networkType === 'none';
						//如果没有网络，清除骨架屏
						if (this.errorNetwork) this.reloadData();
					}
				});
			},
			// 断网后重新链接
			reconnect() {
				uni.getNetworkType({
					success: res => {
						this.errorNetwork = res.networkType === 'none';
						if (!this.errorNetwork) {
							setTimeout(() => {
								uni.reLaunch({
									url: "/pages/index/index"
								});
							}, 1000)
						}
					}
				});
			},
			/**
			 * 获取DIY
			 * @param {number} id
			 * @param {boolean} type 区分是否是微页面
			 */
			diyData(id, type) {
				let that = this;
				that.styleConfig = []
				uni.showLoading({
					title: '加载中...'
				});
				pagediyInfoApi(id).then(res => {
					that.errorNetwork = false;
					uni.setNavigationBarTitle({
						title: res.data.title
					});
					uni.setNavigationBarColor({
						//必须要16进制小写
						frontColor: res.data.titleColor,
						backgroundColor: res.data.titleBgColor.toString().toLowerCase(),
					})
					let data = res.data;
					that.diyId = res.data.id;
					that.isDefault = data.isDefault; //是否是首页，1是，0不是
					that.styleConfig = that.$util.objToArr(res.data.value);
					this.bgInfo.isBgColor=data.isBgColor
					this.bgInfo.colorPicker=data.colorPicker
					that.pageStyle = {
						'background-color': data.isBgColor === 1 ? res.data.colorPicker : '',
						'background-image': data.isBgPic === 1 ? `url(${res.data.bgPic})` : '',
						'background-repeat': res.data.bgTabVal === 1 ? 'repeat-y' : 'no-repeat',
						'background-size': res.data.bgTabVal === 2 ? 'cover' : 'contain'
					};
					uni.hideLoading();
					if (type) {
						that.styleConfig.forEach((item) => {
							if (item.name == 'headerSerch' || item.name == 'homeComb' || item.name ==
								'tabNav') {
								that.styleConfig.splice(index, 1);
							}
						});
					} else {
						that.styleConfig.forEach((item) => {
							if (item.name === 'tabNav'&&!item.isHide) {
								that.showCateNav = true;
								that.cateNavData = item;
							}
							if (item.name === 'homeComb'&&!item.isHide) {
								that.showHomeComb = true
								that.homeCombData = item;
							}
							if (item.name === 'headerSerch'&&!item.isHide) {
								that.showHeaderSerch = true
								that.headerSerchCombData = item;
							}
						});
					}
					setTimeout(() => {
						that.isNodes++;
					}, 100);
				}).catch(err => {
					return that.$util.Tips({
						title: err
					});
					uni.hideLoading();
				});
			},
			bindMore() {
				uni.setStorageSync('categoryId', this.categoryId);
				uni.switchTab({
					url: `/pages/goods_cate/goods_cate`
				})
			},
			getRecommendLength(e) {
				this.isNoCommodity = e == 0 ? true : false;
			},
			// 导航分类切换
			changeTab(index, item) {
				//type=0微页面，1分类，2首页
				this.cateNavActive = index;
				if (item.type == 1) {
					this.navIndex = 1;
					if (!item.val) {
						this.sortList = [];
						this.categoryId = 0;
						this.$util.Tips({
							title: "请在平台端选择商品分类！确保加载商品分类数据。"
						});
						return;
					} else {
						getCategoryTwo(item.val).then(res => {
							this.sortList = res.data;
							// #ifdef H5
							self.sortMarTop = 10;
							// #endif
						});
						this.categoryId = item.val;
						this.isShowTitle = false;
					}
				} else if (item.type == 0) {
					this.navIndex = 0;
					this.isShowTitle = true;
					this.categoryId = 0;
					if (!item.val) {
						return this.$util.Tips({
							title: "请在平台端选择微页面链接！确保加载微页面数据。"
						});
					} else {
						this.styleConfig = [];
						this.diyData(item.val, true);
					}
				} else {
					this.categoryId = 0;
					this.navIndex = 0;
					this.styleConfig = [];
					this.diyData(item.val, false);
				}
			},
			toNewsList() {
				uni.navigateTo({
					animationType: animationType.type,
					animationDuration: animationType.duration,
					url: '/pages/goods/news_list/index'
				})
			},
			//清除骨架屏
			reloadData() {
				this.showSkeleton = false;
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
			// #ifdef APP-PLUS
			xieyiApp() {
				uni.navigateTo({
					animationType: animationType.type,
					animationDuration: animationType.duration,
					url: '/pages/users/web_page/index?webUel=https://admin.java.crmeb.net/useragreement/xieyi.html&title=协议内容'
				})
			},
			// #endif
			// #ifdef MP || APP-PLUS
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
				});
			},
			// #endif
			// 首页数据
			getIndexConfig: function() {
				let that = this;
				getIndexData().then(res => {
					let imgHost = res.data.logoUrl.split('crmebimage')[0];
					that.imgHost = imgHost;
					that.$Cache.set('imgHost', imgHost );
					// #ifdef H5 || APP-PLUS
					that.$store.commit("SET_CHATURL", res.data.yzfUrl);
					Cache.set('chatUrl', res.data.yzfUrl);
					// #endif
					that.$Cache.setItem({
						name: 'categoryConfig',
						value: {
							categoryConfig: res.data.categoryPageConfig,
							isShowCategory: res.data.isShowCategory
						}
					});
					Cache.setItem({
						name: 'chatConfig',
						value: {
							consumer_hotline: res.data.consumerHotline,
							telephone_service_switch: res.data.telephoneServiceSwitch,
							wx_chant_independent:res.data.wxChatIndependent
						}
					});
					this.reloadData();
				}).catch(err => {
					return this.$util.Tips({
						title: err
					});
				});
			},
			appVersionConfig() {
				var that = this;
				//app升级
				// 获取本地应用资源版本号
				getAppVersion().then(res => {
					that.$set(that.appUpdate, 'androidAddress', res.data.androidAddress);
					that.$set(that.appUpdate, 'appVersion', res.data.appVersion);
					that.$set(that.appUpdate, 'iosAddress', res.data.iosAddress);
					that.$set(that.appUpdate, 'openUpgrade', res.data.openUpgrade);
					plus.runtime.getProperty(plus.runtime.appid, function(inf) {
						let nowVersion = (inf.version).split('.').join('');
						let appVersion = (res.data.appVersion).split('.').join('');
						uni.getSystemInfo({
							success: (res) => {
								if (appVersion > nowVersion) {
									uni.showModal({
										title: '更新提示',
										content: '发现新版本，是否前去下载?',
										showCancel: that.appUpdate.openUpgrade == '1' ?
											true : false,
										cancelColor: '#eeeeee',
										confirmColor: '#FF0000',
										success(response) {
											if (response.confirm) {
												switch (res.platform) {
													case "android":
														plus.runtime.openURL(that
															.appUpdate
															.androidAddress);
														break;
													case "ios":
														plus.runtime.openURL(encodeURI(
															that.appUpdate
															.iosAddress));
														break;
												}

											}
										}
									});
								}
							}
						})
					});
				})
			},
			shareApi: function() {
				getShare().then(res => {
					this.$set(this, 'configApi', res.data);
					this.$set(this, "site_name", res.data.title);
					uni.setNavigationBarTitle({
						title: this.site_name
					})
					// #ifdef H5
					this.setOpenShare(res.data);
					// #endif
				})
			},
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
			stopTouchMove() {
				return true //禁止新闻swiper手动滑动
			},
			closeDialog() {
				this.couponModal = false;
				this.$Cache.clear('newGift');
			},
			goDetail(item) {
				goProductDetail(item.id, 0, '')
			},
		},
		mounted() {
			let query = uni.createSelectorQuery().in(this);
			query.select("#home").boundingClientRect();
			query.exec(res => {
				this.domHeight = res[0].height;
			})
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
		}
		// #endif
	}
</script>
<style>
	page {
		height: auto;
		display: flex;
		flex-direction: column;
		height: 100%;
		/* #ifdef H5 */
		background-color: #fff;
		/* #endif */

	}
</style>
<style lang="scss" scoped>
	.error-network {
		position: fixed;
		left: 0;
		top: 0;
		display: flex;
		flex-direction: column;
		align-items: center;
		width: 100%;
		height: 100%;
		padding-top: 40rpx;
		background: #fff;
		padding-top: 30%;

		.img {
			width: 414rpx;
			height: 336rpx;
		}

		.title {
			position: relative;
			top: -40rpx;
			font-size: 32rpx;
			color: #666;
		}

		.con {
			font-size: 24rpx;
			color: #999;

			.label {
				margin-bottom: 20rpx;
			}

			.item {
				margin-bottom: 20rpx;
			}
		}

		.btn {
			display: flex;
			align-items: center;
			justify-content: center;
			width: 508rpx;
			height: 86rpx;
			margin-top: 100rpx;
			border: 1px solid #d74432;
			color: #e93323;
			font-size: 30rpx;
			border-radius: 120rpx;
		}
	}

	.ysize {
		background-size: 100%;
	}

	.fullsize {
		background-size: 100% 100%;
	}

	.repeat {
		background-repeat: repeat;
	}

	.noRepeat {
		background-repeat: no-repeat;
	}

	.noCommodity {
		margin-top: 30%;
	}

	.icon-gengduo1 {
		color: #F8F8F8;
	}

	.pageIndex {
		padding: 0 24rpx;
	}

	.productList {
		background-color: #F5F5F5;
		margin-top: 20rpx;

		// min-height: 70vh;
		.sort {
			width: 710rpx;
			max-height: 380rpx;
			background: rgba(255, 255, 255, 1);
			border-radius: 16rpx;
			padding: 0rpx 0rpx 20rpx 0rpx !important;
			flex-wrap: wrap;
			margin: 25rpx auto 0 auto;

			&.no_pad {
				padding: 0;
			}

			.item {
				width: 20%;
				margin-top: 20rpx;
				text-align: center;

				.pictrues {
					width: 90rpx;
					height: 90rpx;
					background: #F5F5F5;
					border-radius: 50%;
					margin: 0 auto;
				}

				.pictrue {
					width: 90rpx;
					height: 90rpx;
					background: #F5F5F5;
					border-radius: 50%;
					margin: 0 auto;
				}

				.slide-image {
					width: 90rpx;
					height: 90rpx;
					border-radius: 50%;
					overflow: hidden;
				}

				/deep/ .easy-loadimage,
				uni-image,
				.easy-loadimage {
					width: 90rpx;
					height: 90rpx;
					display: inline-block;
				}

				.text {
					color: #272727;
					font-size: 24rpx;
					margin-top: 10rpx;
					// overflow: hidden;
					white-space: nowrap;
					text-overflow: ellipsis;
				}
			}
		}
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

		.name {
			display: flex;
			align-items: center;

			.name_text {
				display: inline-block;
				max-width: 200rpx;
			}
		}
	}

	.page-index {
		display: flex;
		flex-direction: column;
		min-height: 100%;

		.page_content {
			overflow: hidden;

			// background-color: #f5f5f5;
			.swiper {
				position: relative;
				width: 100%;
				height: 246rpx;
				margin: 0 auto;
				border-radius: 10rpx;
				overflow: hidden;
				margin-bottom: 25rpx;
				/* #ifdef MP */
				margin-top: 20rpx;

				/* #endif */
				swiper,
				swiper-item,
				.slide-image,
				image {
					width: 100%;
					height: 246rpx;
					border-radius: 10rpx;
				}
			}
		}
	}

	.fixed {
		z-index: 100;
		position: fixed;
		left: 0;
		top: 0;
		background: linear-gradient(90deg, red 50%, #ff5400 100%);

	}

	.menu-txt {
		font-size: 24rpx;
		color: #454545;
	}

	.footerBottom-h10 {
		height: 20rpx;
	}
</style>