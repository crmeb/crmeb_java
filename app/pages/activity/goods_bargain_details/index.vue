<template>
	<view>
		<view class='bargain'>
			<view class="header" :class="startBargainUid != userInfo.uid ? 'on' : ''">
				<navigator url="/pages/activity/goods_bargain/index" hover-class="none">
					<view class="back">
						<text class="iconfont icon-xiangzuo"></text> 返回砍价列表
					</view>
				</navigator>
			</view>
			<view class="pad30" style="padding-bottom: 30rpx;">
				<view class='wrapper'>
					<view class='pictxt acea-row row-between' @tap="goProduct">
						<view class='pictrue'>
							<image :src='bargainInfo.image'></image>
						</view>
						<view class='text acea-row row-column-around'>
							<view class='line1'>{{bargainInfo.title}}</view>
							<view class="surplus">最低价：￥{{bargainInfo.minPrice}}</view>
							<view class="surplus">剩余：{{bargainInfo.quota}}{{bargainInfo.unitName}}</view>
							<view class='money font-color-red'>
								当前: ￥
								<text class='num'>{{buyPrice}}</text>
							</view>
							<view class="acea-row"
								v-if="new Date().getTime()- bargainInfo.stopTime < 0 && bargainInfo.quota>0">
								<countDown :tipText="' '" :bgColor="bgColor" :dayText="':'" :hourText="':'"
									:minuteText="':'" :secondText="' '" :datatime="datatime" :isDay="true"></countDown>
								<text style="font-size: 22rpx;margin-left: 4rpx;line-height: 36rpx;">后结束</text>
							</view>
							<view v-if="new Date().getTime()- bargainInfo.stopTime >=0">
								<view style="font-size: 22rpx;" @tap='currentBargainUser'>已结束</view>
							</view>
							<view v-if="bargainInfo.quota==0">
								<view style="font-size: 22rpx;" @tap='currentBargainUser'>已售罄</view>
							</view>
						</view>
						<text class="iconfont icon-jiantou iconfonts"></text>
					</view>
				</view>
				<view class="content">
					<block
						v-if="(startBargainUid == uid && (bargainStatus==1 || bargainStatus==3)) || (startBargainUid != uid && bargainStatus==5)">
						<view class='money acea-row row-center'
							:class="new Date().getTime()- bargainInfo.stopTime >=0 ? 'font_hui': ''">
							<view style="margin-right: 40rpx;">已砍<text class="font-color-red"
									:class="new Date().getTime()- bargainInfo.stopTime >=0 ? 'font_hui': ''">￥{{bargainUserInfo.alreadyPrice}}</text>
							</view>
							<view>还剩<text class="font-color-red"
									:class="new Date().getTime()- bargainInfo.stopTime >=0 ? 'font_hui': ''">￥{{bargainUserInfo.surplusPrice}}</text>
							</view>
						</view>
						<view class="cu-progress acea-row row-middle round margin-top"
							:class="new Date().getTime()- bargainInfo.stopTime >=0 ? 'bg_qian': ''">
							<view class='acea-row row-middle bg-red'
								:class="new Date().getTime()- bargainInfo.stopTime >=0 ? 'bg-color-hui': ''"
								:style="'width:'+ bargainUserInfo.bargainPercent +'%;'"></view>
						</view>
						<view class='tip'>
							一 已有{{bargainInfo.sales}}位好友砍价成功 一
						</view>
					</block>

					<!-- 自己砍价 -->
					<view
						v-if="startBargainUid == uid && parseFloat(bargainUserInfo.surplusPrice) > 0 && bargainStatus==1">
						<view class='bargainBnt' @tap='userBargain' v-if="quota>0">
							立即参与砍价
						</view>
						<view class='bargainBnt grey' v-if="quota<=0">商品暂无库存</view>
					</view>
					<view
						v-if="startBargainUid == uid && parseFloat(bargainUserInfo.surplusPrice) > 0 && bargainStatus==3">
						<!-- #ifdef H5 -->
						<view class='bargainBnt' v-if="$wechat.isWeixin()" @click="H5ShareBox = true">邀请好友帮砍价</view>
						<view class='bargainBnt' v-else @tap='getBargainUserBargainPricePoster'>邀请好友帮砍价</view>
						<!-- #endif -->
						<!-- #ifdef MP -->
						<button open-type='share' class='bargainBnt'>邀请好友帮砍价</button>
						<!-- #endif -->
					</view>

					<view
						v-if="startBargainUid != uid && bargainStatus==5 && parseFloat(bargainUserInfo.surplusPrice) > 0">
						<view class='bargainBnt' @tap='setBargainHelp'>帮好友砍一刀</view>
					</view>
					<view v-if="startBargainUid != uid && bargainStatus==4 && parseFloat(bargainUserInfo.surplusPrice) == 0" >
						<view class='bargainSuccess'>
							<image src="../static/cheng.png"></image>
							好友已砍成功
						</view>
						<view class='bargainBnt' @tap='currentBargainUser'>我也要参与</view>
					</view>
					<view v-if="startBargainUid != uid && bargainStatus==7">
						<view class='bargainSuccess'>
							<image src="../static/cheng.png"></image>
							您已帮其他好友砍过此商品
						</view>
						<view class='bargainBnt' @tap='currentBargainUser'>我也要参与</view>
					</view>
					<view v-if="startBargainUid != uid && bargainStatus==6">
						<view class='bargainSuccess'>
							<image src="../static/chengh.png"></image>
							已成功帮助好友砍价
						</view>
						<view class='bargainBnt' @tap='currentBargainUser'>我也要参与</view>
					</view>
					<view
						v-if="parseFloat(bargainUserInfo.surplusPrice) == 0 &&  startBargainUid == uid && (bargainStatus==4 || bargainStatus==8)">
						<view class='bargainSuccess'>
							<image src="../static/chengh.png"></image>
							恭喜您砍价成功，快去支付吧~
						</view>
						<view class="acea-row row-between buyBox pad30">
							<view class='buyMore on' @tap='goBargainList'>继续选购</view>
							<view v-if="bargainStatus==4" class='buyNow on' @tap='goPay'>立即支付</view>
							<view v-if="bargainStatus==8" class='buyNow on' @tap='goConfirm'>去支付</view>
						</view>
					</view>
					<view
						v-if="parseFloat(bargainUserInfo.surplusPrice) == 0 &&  startBargainUid == uid && bargainStatus==9">
						<view class='bargainSuccess'>
							<image src="../static/chengh.png"></image>
							恭喜您砍价成功，去看看别的商品吧~
						</view>
						<view class="buyBox pad30">
							<view class='buyMore on' @tap='goBargainList' style="margin: 40rpx auto 0 auto;">继续选购</view>
						</view>
					</view>
					<view v-if="new Date().getTime()- bargainInfo.stopTime >=0">
						<view class='huiBtn' @tap='currentBargainUser'>活动已结束</view>
					</view>
					<view v-else-if="bargainInfo.quota==0">
						<view class='huiBtn' @tap='currentBargainUser'>商品已售罄</view>
					</view>
					<navigator v-if="new Date().getTime()- bargainInfo.stopTime >=0 || bargainInfo.quota==0"
						url="/pages/activity/goods_bargain/index" hover-class="none">
						<view class="go">再去逛逛<text class="iconfont icon-jiantou"></text></view>
					</navigator>
					<view v-if="bargainStatus==2" class="contentNo" style="padding: 0;">
						<text class="iconfont icon-xiaolian mr8"></text>
						您购买的商品数量已达上限
					</view>
				</view>

				<!-- 砍价记录 -->
				<view class='title font-color acea-row row-center-wrapper'>
					<view class='pictrue'>
						<image src='../static/zuo2.png'></image>
					</view>
					<view class="tits">
						<view class='titleCon'>砍价记录</view>
						<view class="line"></view>
					</view>
					<view class='pictrue on'>
						<image src='../static/you2.png'></image>
					</view>
				</view>
				<view class='bargainGang borRadius14'>
					<view class='list' v-if="bargainUserHelpList.length>0">
						<block v-for="(item,index) in bargainUserHelpList" :key='index'
							v-if="index<3 || !couponsHidden">
							<view class='item acea-row row-between-wrapper'>
								<view class='pictxt acea-row row-between-wrapper'>
									<view class='pictrue'>
										<image :src='item.avatar'></image>
									</view>
									<view class='text'>
										<view class='name line1'>{{item.nickname}}</view>
										<view class='line1'>{{item.addTimeStr }}</view>
									</view>
								</view>
								<view class='money'>
									已砍 <text class="font-color-red">{{item.price}}</text>元
								</view>
							</view>

						</block>
						<view class="open acea-row row-center-wrapper" @click="openTap"
							v-if="bargainUserHelpList.length>3">{{couponsHidden?'展开更多':'关闭展开'}}<text class="iconfont"
								:class='couponsHidden==true?"icon-xiangxia":"icon-xiangshang"'></text></view>
					</view>
					<view v-if="bargainUserHelpList.length===0" class="contentNo">
						<text class="iconfont icon-xiaolian mr8"></text>
						暂无助力记录
					</view>
					<!-- <view class='load font-color' v-if="!limitStatus" @tap='getBargainUser'>点击加载更多</view> -->
				</view>

				<!-- 商品详情 -->
				<view class='title font-color acea-row row-center-wrapper'>
					<view class='pictrue'>
						<image src='../static/zuo2.png'></image>
					</view>
					<view class="tits">
						<view class='titleCon'>商品详情</view>
						<view class="line"></view>
					</view>
					<view class='pictrue on'>
						<image src='../static/you2.png'></image>
					</view>
				</view>
				<view class='goodsDetails borRadius14'>
					<view class='conter borRadius14'>
						<jyf-parser v-if="bargainInfo.content" :html="bargainInfo.content" ref="article"
							:tag-style="tagStyle"></jyf-parser>
						<view v-else class="contentNo">
							<text class="iconfont icon-xiaolian mr8"></text>
							暂无商品详情
						</view>
					</view>
				</view>

				<view class='bargainTip' :class='active==true?"on":""'>
					<view class='pictrue'>
						<image src="../../../static/images/bargainBg.png"></image>
					</view>
					<view v-if="startBargainUid == uid">
						<view class='cutOff'>
							您已砍掉<text class='font-color'>{{bargainUserBargainPrice}}元</text>
						</view>
						<view class="bubbleBox">
							<view class="bubble"
								:style="'left:'+ (bargainUserInfo.bargainPercent>0?bargainUserInfo.bargainPercent-9:0) +'%;'">
								<text>{{bargainUserInfo.bargainPercent}}%</text>
							</view>
						</view>
						<view class="cu-progress acea-row row-middle round margin-top">
							<view class='acea-row row-middle bg-red'
								:style="'width:'+ bargainUserInfo.bargainPercent +'%;'"></view>
						</view>
						<view class="t1">分享次数越多，成功的机会越大哦！</view>
						<!-- #ifdef MP -->
						<button open-type='share' class='tipBnt'>邀请好友帮砍价</button>
						<!-- #endif -->
						<!-- #ifdef H5 -->
						<view class='tipBnt' @tap='getBargainUserBargainPricePoster'>邀请好友帮砍价</view>
						<!-- #endif -->
					</view>
					<view v-else>
						<view class='cutOff'>
							帮好友砍掉<text class='font-color'>{{bargainUserBargainPrice}}元</text>
						</view>
						<view class="bubbleBox">
							<view class="bubble"
								:style="'left:'+ (bargainUserInfo.bargainPercent>0?bargainUserInfo.bargainPercent-9:0) +'%;'">
								<text>{{bargainUserInfo.bargainPercent}}%</text>
							</view>
						</view>
						<view class="cu-progress acea-row row-middle round margin-top">
							<view class='acea-row row-middle bg-red'
								:style="'width:'+ bargainUserInfo.bargainPercent +'%;'"></view>
						</view>
						<view class="t1">您也可以砍价低价拿哦，快去挑选吧~</view>
						<!-- <view class='help font-color'>成功帮砍{{bargainUserBargainPrice}}元</view> -->
						<!-- 						<view class='cutOff on'>您也可以砍价低价拿哦，快去挑选心仪的商品吧~</view> -->
						<view @tap='currentBargainUser' class='tipBnt'>我也要参与</view>
					</view>
				</view>
			</view>
			<view class='mask' catchtouchmove="true" v-show='active==true || posters==true || canvasStatus'
				@tap='close'></view>
		</view>

		<!-- 发送给朋友图片 -->
		<view class="share-box" v-if="H5ShareBox">
			<image src="/static/images/share-info.png" @click="H5ShareBox = false"></image>
		</view>

		<!-- 海报展示 -->
		<view class='poster-pop' v-if="canvasStatus">
			<image :src='imagePath'></image>
			<!-- #ifndef H5  -->
			<view class='save-poster' @click="savePosterPath">保存到手机</view>
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<view class="keep">长按图片保存至相册</view>
			<view class='iconfont icon-cha2 close' @tap='close'></view>
			<!-- #endif -->
		</view>
		<view class="canvas">
			<canvas style="width:597px;height:850px;" canvas-id="activityCanvas"></canvas>
			<canvas canvas-id="qrcode" :style="{width: `${qrcodeSize}px`, height: `${qrcodeSize}px`}"
				style="opacity: 0;" />
		</view>
		<home></home>
	</view>
</template>

<script>
	import {
		getBargainDetail,
		postBargainStartUser,
		postBargainStart,
		postBargainHelp,
		getBargainUser
	} from '../../../api/activity.js';
	import {
		imageBase64
	} from "@/api/public";
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js';
	import {
		postCartAdd
	} from '../../../api/store.js';
	import util from '../../../utils/util.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	// #ifdef MP
	import authorize from '@/components/Authorize';
	// #endif
	import countDown from '@/components/countDown';
	import home from '@/components/home';
	import parser from "@/components/jyf-parser/jyf-parser";
	import {
		silenceBindingSpread
	} from "@/utils";
	const app = getApp();

	export default {
		components: {
			countDown,
			// #ifdef MP
			authorize,
			// #endif
			home,
			"jyf-parser": parser
		},
		filters: {
			picFilter(status) {
				const statusMap = {
					'0': 'num1',
					'1': 'num2',
					'2': 'num3'
				}
				return statusMap[status]
			}
		},
		/**
		 * 页面的初始数据
		 */
		
		data() {
			return {
				bgColor: {
					'bgColor': '#333333',
					'Color': '#fff',
					'width': '44rpx',
					'timeTxtwidth': '16rpx',
					'isDay': true
				},
				countDownDay: '00',
				countDownHour: '00',
				countDownMinute: '00',
				countDownSecond: '00',
				active: false,
				id: 0, //砍价商品id
				bargainStatus: 0, //当前用户砍价状态：1-可以参与砍价,2-参与次数已满，3-砍价中,4-已完成，5-可以帮砍，6-已帮砍,7-帮砍次数已满,8-已生成订单未支付，9-已支付
				startBargainUid: 0, //开启砍价用户uid
				bargainUserInfo: {}, //开启砍价用户信息
				//bargainUserId: 0, //开启砍价编号
				storeBargainId: 0, // 砍价活动id
				bargainInfo: {}, //砍价产品
				page: 1,
				limit: 5,
				limitStatus: false,
				bargainUserHelpList: [],
				bargainUserHelpInfo: [],
				bargainUserBargainPrice: 0, //砍了多少钱
				bargainCount: [], //分享人数  浏览人数 参与人数
				retunTop: true,
				bargainPartake: 0,
				isHelp: false,
				interval: null,
				productStock: 0, //判断是否售罄；
				quota: 0, //判断是否已限量；
				userBargainStatusHelp: true,
				navH: '',
				bargainPrice: 0,
				datatime: 0,
				offest: '',
				tagStyle: {
					img: 'width:100%;'
				},
				H5ShareBox: false, //公众号分享图片
				systemH: 0,
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				pages: '',
				couponsHidden: true,
				loading: false,
				loadend: false,
				posters: false,
				buyPrice: '', //最后砍价购买价格
				qrcodeSize: 600,
				posterbackgd: '/static/images/canbj.png',
				PromotionCode: '', //二维码
				canvasStatus: false,
				imgTop: '', //商品图base64位
				imagePath: '' // 海报图片
			}

		},
		computed: mapGetters(['isLogin', 'userInfo', 'uid']),
		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			var that = this;
			that.$store.commit("PRODUCT_TYPE", 'normal');
			// #ifdef MP
			uni.getSystemInfo({
				success: function(res) {
					that.systemH = res.statusBarHeight
					that.navH = that.systemH + 10
				}
			})
			// #endif


			var pages = getCurrentPages();
			if (pages.length <= 1) {
				that.retunTop = false
			}
			//扫码携带参数处理
			// #ifdef MP
			if (options.scene) {
				var value = util.getUrlParams(decodeURIComponent(options.scene));
				if (typeof value === 'object') {
					value.id ? options.id = value.id : options.id = ''
					if (value.startBargainUid) options.startBargainUid = value.startBargainUid;
					//记录推广人uid
					if (value.pid) app.globalData.spid = value.pid;
				} else {
					app.globalData.spid = value;
				}
			}
			//记录推广人uid
			if (options.spid) app.globalData.spid = options.spid;
			// #endif
			if (options.hasOwnProperty('id')) {
				options.id ? that.id = options.id : that.id = ''
			}
			options.startBargainUid == 'undefined' ? that.startBargainUid = 0 : that.startBargainUid = Number(options
				.startBargainUid);
			if (this.isLogin) {
				if (that.startBargainUid == 0) {
					that.startBargainUid = Number(that.$store.state.app.uid)
				}
				this.storeBargainId = options.storeBargainId ? Number(options.storeBargainId) : 0;
				this.page = 1;
				//this.getBargainDetails();
				// app.globalData.openPages = '/pages/activity/goods_bargain_details/index?id=' + this.id + '&bargain=' + this.bargainUid +
				// 	'&spid=' + e.detail.uid;
				// this.$set(that, 'bargainPartake', e.detail.uid);
			} else {
				this.$Cache.set('login_back_url',
					'/pages/activity/goods_bargain_details/index?id=' + options.id +
					'&startBargainUid=' + this
					.uid + '&spid=' + this.uid + '&storeBargainId=' + this.storeBargainId
				);
				toLogin();
			}

			this.isLogin && silenceBindingSpread();
			uni.setNavigationBarTitle({
				title: '砍价详情'
			})
		},
		methods: {
			//去支付
			goConfirm() {
				uni.navigateTo({
					url: `/pages/activity/bargain/index`
				})
			},
			//#ifdef H5
			setOpenShare() {
				let that = this;
				let configTimeline = {
					title: "您的好友" +
						that.userInfo.nickname +
						"邀请您砍价" +
						that.bargainInfo.title,
					desc: that.bargainInfo.info,
					link: window.location.protocol +
						"//" +
						window.location.host +
						'/pages/activity/goods_bargain_details/index?id=' + this.id + '&startBargainUid=' + this
						.uid + '&spid=' + this.uid + '&storeBargainId=' + this.storeBargainId,
					imgUrl: that.bargainInfo.image
				};
				if (this.$wechat.isWeixin()) {
					this.$wechat.wechatEvevt([
								"updateAppMessageShareData",
								"updateTimelineShareData",
								"onMenuShareAppMessage",
								"onMenuShareTimeline"
							],
							configTimeline
						)
						.then(res => {})
						.catch(res => {
							if (res.is_ready) {
								res.wx.updateAppMessageShareData(configTimeline);
								res.wx.updateTimelineShareData(configTimeline);
								res.wx.onMenuShareAppMessage(configTimeline);
								res.wx.onMenuShareTimeline(configTimeline);
							}
						});
				}
			},
			//#endif
			openTap() {
				this.$set(this, 'couponsHidden', !this.couponsHidden);
			},
			// 去商品页
			goProduct() {
				uni.navigateTo({
					url: `/pages/goods_details/index?id=${this.bargainInfo.productId}`
				})
			},
			// 自己砍价；
			userBargain: function() {
				if (this.uid == this.startBargainUid) {
					this.setBargain();
				}
			},
			goBack: function() {
				uni.navigateBack({

					delta: 1
				})
			},
			// 生成二维码；
			make() {
				let href = window.location.protocol +
					"//" +
					window.location.host +
					'/pages/activity/goods_bargain_details/index?id=' + this.id + '&startBargainUid=' + this
					.uid + '&spid=' + this.uid + '&storeBargainId=' + this.storeBargainId;
				uQRCode.make({
					canvasId: 'qrcode',
					text: href,
					size: this.qrcodeSize,
					margin: 10,
					success: res => {
						this.PromotionCode = res;
					},
					complete: () => {},
					fail: res => {
						this.$util.Tips({
							title: '海报二维码生成失败！'
						});
					}
				})
			},
			// 商品图片转base64
			getImageBase64: function(images) {
				let that = this;
				imageBase64({
					url: images
				}).then(res => {
					that.imgTop = res.data.code;
				})
			},
			//获取砍价用户信息
			gobargainUserInfo: function() {
				getBargainUser({
					bargainId: this.id,
					bargainUserId: this.storeBargainId || 0
				}).then(res => {
					let bargainUserInfo = res.data;
					this.bargainUserInfo = bargainUserInfo;
					this.bargainStatus = bargainUserInfo.bargainStatus;
					this.storeBargainId = bargainUserInfo.storeBargainUserId;
					this.buyPrice = this.$util.$h.Sub(parseFloat(this.bargainPrice), parseFloat(bargainUserInfo
						.alreadyPrice))
					this.bargainUserHelpList = bargainUserInfo.userHelpList || [];
					//#ifdef H5
					if (bargainUserInfo.storeBargainUserId) this.make();
					this.setOpenShare();
					//#endif
				}).catch(err => {
					//状态异常返回上级页面
					return this.$util.Tips({
						title: err.toString()
					}, {
						tab: 3,
						url: 1
					});
				});
			},
			goPay: function() { //立即支付
				if (this.isLogin === false) {
					toLogin();
				} else {
					// 预下单
					this.$Order.getPreOrder("buyNow", [{
						"attrValueId": parseFloat(this.bargainInfo.attrValueId),
						"bargainId": parseFloat(this.id),
						"productNum": 1,
						"productId": parseFloat(this.bargainInfo.productId),
						"bargainUserId": parseFloat(this.storeBargainId)
					}]);
				}
			},
			//获取砍价产品详情
			getBargainDetails: function() {
				var that = this;
				getBargainDetail(that.id).then(function(res) {
					let bargainInfo = res.data;
					that.bargainInfo = bargainInfo;
					that.bargainPrice = bargainInfo.price;
					that.quota = bargainInfo.quota;
					that.datatime = bargainInfo.stopTime / 1000;
					that.pages = '/pages/activity/goods_bargain_details/index?id=' + that.id +
						'&startBargainUid=' + that
						.uid + '&scene=' + that.uid + '&storeBargainId=' + that.storeBargainId;
					uni.setNavigationBarTitle({
						title: bargainInfo.title.substring(0, 13) + '...'
					})
					that.bargainUserHelpList = []
					if (that.isLogin && that.quota > 0 && new Date().getTime() - bargainInfo.stopTime < 0) {
						that.gobargainUserInfo();
					} else {
						that.buyPrice = that.bargainPrice;
					}
					//#ifdef H5
					that.getImageBase64(bargainInfo.image);
					//#endif
				}).catch(function(err) {
					that.$util.Tips({
						title: err
					}, {
						tab: 2,
						url: '/pages/activity/goods_bargain/index'
					});
				})
			},
			currentBargainUser: function() { //当前用户砍价
				this.page = 1;
				uni.navigateTo({
					url: '/pages/activity/goods_bargain_details/index?id=' + this.id + '&startBargainUid=' +
						this.uid
				});
			},
			setBargain: function() { //参与砍价
				var that = this;
				postBargainStart(that.id).then(res => {
					if (res.code === 'subscribe') {
						return;
					}
					this.storeBargainId = res.data.storeBargainUserId;
					that.setBargainHelp();
					that.userBargainStatus = 1;
					//#ifdef H5
					that.make();
					//#endif
				}, error => {
					this.startBargainUid = 0;
					that.$util.Tips({
						title: error
					})
				})
			},
			//帮好友砍价
			setBargainHelp: function() {
				var data = {
					bargainId: this.id,
					bargainUserId: this.storeBargainId,
					bargainUserUid: this.startBargainUid
				};
				postBargainHelp(data).then(res => {
					this.$set(this, 'bargainUserHelpList', []);
					this.$set(this, 'bargainUserBargainPrice', res.data.bargainPrice);
					this.$set(this, 'active', true);
					this.gobargainUserInfo();
				}).catch(err => {
					this.$util.Tips({
						title: err
					})
					this.$set(this, 'bargainUserHelpList', []);
				})
			},
			getBargainUserBargainPricePoster: function() {
				var that = this;
				this.active = false
				uni.showLoading({
					title: '海报生成中',
					mask: true
				});
				this.posters = false;
				let arrImagesUrl = '';
				let arrImagesUrlTop = '';
				if (!this.PromotionCode) {
					uni.hideLoading();
					this.$util.Tips({
						title: this.errT
					});
					return
				}
				setTimeout(() => {
					if (!this.imgTop) {
						uni.hideLoading();
						this.$util.Tips({
							title: '无法生成商品海报！'
						});
						return
					}
				}, 1000);
				uni.downloadFile({
					url: this.imgTop,
					success: (res) => {
						arrImagesUrlTop = res.tempFilePath;
						let arrImages = [this.posterbackgd, arrImagesUrlTop, this.PromotionCode];
						setTimeout(() => {
							this.$util.activityCanvas(arrImages, this.bargainInfo.title, this
								.buyPrice, '已砍至', '还剩' + this.bargainUserInfo
								.surplusPrice + '元砍价成功', 0,
								(tempFilePath) => {
									this.imagePath = tempFilePath;
									this.canvasStatus = true;
									uni.hideLoading();
								});
						}, 500);
					}
				});
			},
			goBargainList: function() {
				uni.navigateTo({
					url: '/pages/activity/goods_bargain/index',
				})
			},
			close: function() {
				this.$set(this, 'active', false);
				this.$set(this, 'posters', false);
				this.$set(this, 'canvasStatus', false);
			}
		},

		/**
		 * 生命周期函数--监听页面初次渲染完成
		 */
		onReady: function() {

		},
		/**
		 * 生命周期函数--监听页面显示
		 */
		onShow: function() {
			if (this.isLogin) this.getBargainDetails();
		},

		/**
		 * 生命周期函数--监听页面隐藏
		 */
		onHide: function() {
			if (this.interval !== null) clearInterval(this.interval);
		},

		/**
		 * 生命周期函数--监听页面卸载
		 */
		onUnload: function() {
			if (this.interval !== null) clearInterval(this.interval);
		},

		/**
		 * 页面相关事件处理函数--监听用户下拉动作
		 */
		onPullDownRefresh: function() {

		},

		/**
		 * 页面上拉触底事件的处理函数
		 */
		onReachBottom: function() {

		},

		//#ifdef MP
		/**
		 * 用户点击右上角分享
		 */
		onShareAppMessage: function() {
			let that = this,
				share = {
					title: '您的好友' + that.userInfo.nickname + '邀请您帮他砍' + that.bargainInfo.title + ' 快去帮忙吧！',
					path: '/pages/activity/goods_bargain_details/index?id=' + this.id + '&startBargainUid=' + this
						.startBargainUid + '&spid=' + this.uid + '&storeBargainId=' + this.storeBargainId,
					imageUrl: that.bargainInfo.image,
				};
			that.close();
			return share;
		},
		//#endif
	}
</script>

<style lang="scss">
	page {
		background-color: $theme-color !important;
	}
</style>
<style lang="scss" scoped>
	.userEvaluation {
		i {
			display: inline-block;
		}
	}

	.go {
		color: #E93323;
        text-align: center;
		font-size: 28rpx;
		margin-top: 26rpx;
		.iconfont {
			font-size: 11px
		}
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

	.canvas {
		position: fixed;
		opacity: 0;
	}

	.font_hui {
		color: #CCCCCC !important;
	}

	.bg_qian {
		background-color: #F5F5F5 !important;
	}

	.font-color-red {
		font-weight: 800;
	}

	.huiBtn {
		font-size: 30rpx;
		font-weight: bold;
		color: #fff;
		width: 630rpx;
		height: 80rpx;
		text-align: center;
		line-height: 80rpx;
		margin: 0 auto;
		background-color: #CCCCCC;
		box-shadow: 0 7rpx 0 #AAAAAA;
		border-radius: 40rpx;
	}

	.huifont {
		color: #CCCCCC;
	}

	.buyBox {
		margin-top: 40rpx;

		.on {
			width: 300rpx;
			height: 80rpx;
			border-radius: 40rpx;
			font-size: 32rpx;
			color: #fff;
			text-align: center;
			line-height: 80rpx;
		}

		.buyNow {
			background: linear-gradient(180deg, #FF8831 0%, #F11B09 100%);
			box-shadow: 0 7rpx 0 #C11100;
		}

		.buyMore {
			background: linear-gradient(180deg, #FFCA52 0%, #FE960F 100%);
			box-shadow: 0 7rpx 0 #D87A00;
		}
	}


	.mr8 {
		margin-right: 8rpx;
	}

	.contentNo {
		width: 100%;
		padding: 50rpx 15rpx;
		text-align: center;
		font-size: 26rpx;
		color: #AAAAAA;
	}

	.generate-posters {
		width: 100%;
		height: 170rpx;
		background-color: #fff;
		position: fixed;
		left: 0;
		bottom: 0;
		z-index: 388;
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

	.bargain .bargainGang {
		background-color: #fff;

		.list {
			padding: 50rpx 30rpx 0 30rpx;
		}

		.open {
			font-size: 24rpx;
			color: #999;
			margin-top: 30rpx;
		}

		.helpNo {
			text-align: center;
			font-size: 26rpx;
			color: #AAAAAA;
		}
	}

	.bargain .bargainGang .open .iconfont {
		font-size: 25rpx;
		margin: 5rpx 0 0 10rpx;
	}

	.bargain .icon-xiangzuo {
		font-size: 24rpx;
		margin-right: 6px;
	}

	.bargain .header {
		background-image: url('data:image/png;base64,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');
		background-repeat: no-repeat;
		background-size: 100% 100%;
		width: 100%;
		height: 340rpx;
		margin: 0 auto;
		padding-top: 20rpx;

		.back {
			width: 235rpx;
			height: 54rpx;
			background: linear-gradient(233deg, #FFD169 0%, #FE960F 100%);
			opacity: 1;
			border-radius: 0px 27rpx 27rpx 0px;
			line-height: 54rpx;
			text-align: center;
			color: #6E3F00;
			font-size: 28rpx;
		}

	}

	.bargain .header .pictxt {
		margin: 330rpx auto 0 auto;
		font-size: 26rpx;
		color: #fff;
	}

	.bargain .header .pictxt .pictrue {
		width: 56rpx;
		height: 56rpx;
		margin-right: 30rpx;
	}

	.bargain .header .pictxt .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		border: 2rpx solid #fff;
	}

	.bargain .header .pictxt .text {
		margin-left: 20rpx;
	}

	.bargain .header .pictxt .text text {
		margin-left: 20rpx;
	}

	.bargain .header .time {
		background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAbgAAACmCAMAAACfv2reAAAAk1BMVEUAAAD/nSL/oCj/oCj/nSH/nSH/oCj/mhr/mhr/oCj/oCj/oCj/nSH/niT/mhr/mhr/oCj/mhr/oCj/oCj/mhr/niP/oCj/oCj/mhr/////oCj/mhr/nyT/w3f/rkf/rEL/pjT/tlr/xXv/uF3//vz/vmv/wXP/vWr/79r/1aD/05r/wXL/nB//5MH/+/X/t1r/5MP28hXaAAAAGXRSTlMABvndiVtUVPjkOC8Dk+DarKyQC+QH86amYv5b+wAAA6JJREFUeNrs3Nlu4kAQheEyGOywBkImcR8Sh7Avycz7P920G8l3MYtccko63wXc969GXuiS20XJ6ygF1SQdvyaRiL5kDKrZOBFtgxjID7tVRjVZ7Q45EA9EV4z5ntVqttqfEIuqBKevjGr3dUIiiqIZ9hkp2GMWiZ4Ocv5Oqljl6IieCQ4ZqThgInpG2GWkYoeR6EnBX0olK6SiB8hICSAlhjOE4YxiOKMYziiGM4rhjGI4o5TCIWA4PQhqzzcPGE4PMA9q3m0fAcPpKdfYk3o47z1gOD3Ae+A8qUfuLby/C4bTA5zXOPekHktvu11v12uG0wOc13jpybWi7ku75X6yOR43bwHD6QHegmK13U/+tF+6kZS6bVfls8RweoDPkqvS7pZ/lXTVNpvNkTtOW7nj/Gq7avFACrG7YOlt196W4fQA5zVeeu6CWLyu81rT3lP1VWWBV5WKgEVQeVX51Bu2nNcViR7890Of93EKVO7j+iFYJJ1iv/XlEj450QZ8BHJJv9hzHXn0n1M+q2ze9c8qpz7Zo7T9Z4+vdZoHyJV6Pllbin0XMVzzrg/37JO1xHl8kfoLAHIt5zHcb8FwRjGcUQxnFMMZdU84HrNq3jdSxXBjHmzUssPo9nA8Sty8Aya3h+Ph/caFw/s3h+O4jMbtMXtWDCcJTv8yqqQ/oKYMx5FQDSpHQumGGww5hK1e32EI23BwTziOPWzYOBFRDydRZ8JBo/VJR5NOJHeGI1MYziiGM4rhjGI4oxjOKIYziuGMYjijGM4ohjOK4YxiOKMYziiGM4rhjGI4oxjOKIYziuH+s0cHJAAAAACC/r9uR6AXnBI3JW5K3JS4KXFT4qbETYmbEjclbkrclLgpcVPipsRNiZsSNyVuStyUuClxU+KmxE2JmxI3JW5K3JS4KXFT4qbETYmbEjclbkrclLgpcVPipsRNiZsSNyVuStyUuClxU+KmxE2JmxI3JW5K3JS4KXFT4qbETYmbEjclbkrclLgpcVPipsRNiZsSNyVuStyUuClxtUdHJwzDUAxFZTs2xInBxLQf2n/QvrTQGSLQ+ZAGuKIcTpTDiXI4UQ4nyuFEOZwohxPlcKIcTpTDiXI4UQ4nyuFEOZwohxPlcKIcThQD3jEJJmSRzCixHSakkyyYsRdMyCA5UWPzAZNxZJIVaY/bXU7G8Q32AhpDvvqCPd7qIzM0hI0mZsPtHDQp48RPKzQZpeEv1Vky7fFymTXh9gGY1gZJcqJI8QAAAABJRU5ErkJggg==');
		background-repeat: no-repeat;
		background-size: 100% 100%;
		width: 440rpx;
		height: 166rpx;
		margin: 0 auto;

		font-size: 22rpx;
		text-align: center;
		padding-top: 11rpx;
		box-sizing: border-box;
	}

	.bargain .header .time .red {
		color: #fc4141;
	}

	.bargain .header .people {
		text-align: center;
		color: #fff;
		font-size: 20rpx;
		position: absolute;
		width: 85%;
		/* #ifdef MP */
		height: 44px;
		line-height: 44px;
		top: 40rpx;
		/* #endif */
		/* #ifdef H5 */
		top: 58rpx;
		/* #endif */
	}

	.bargain .header .time text {
		color: #333;
	}

	.bargain {
		.wrapper {
			margin-top: -100rpx;

			.pictxt {
				width: 100%;
				height: 260rpx;
				background-color: #FFF5E6;
				padding: 20rpx;
				border-radius: 14rpx;
			}

			.surplus {
				color: #999999;
				font-size: 22rpx;
			}
		}

		.content {
			width: 100%;
			height: auto;
			background-image: url('../static/zhuangshi.png');
			background-size: 100% 100%;
			background-color: #fff;
			border-bottom-left-radius: 14rpx;
			border-bottom-right-radius: 14rpx;
			padding: 40rpx 0 70rpx 0;

			.money {
				color: #333333;
				font-size: 36rpx;

				.price {}
			}
		}
	}


	.bargain .wrapper,
		{
		// background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAArIAAAF8CAYAAAAzc1jZAAAco0lEQVR4Xu3dW4zc513H4feddWynQU1FY69zKvZuysENPqx7SFFRU4JEi+ACCVUUiQsuqYQ4VIWCchEuoAIFqRdI5QrlgkNLqQpSJA5VSWkKqKK7DkKuqIQ3RnUTe40CJRSSNDt/ZEeF1LGTmdnffuP1PLntzPf3n2fm4tMoTnrzFwECBAgQIECAAIEdKNB34DN7ZAIECBAgQIAAAQJNyPoRECBAgAABAgQI7EgBIbsjvzYPTYAAAQIECBAgIGT9BggQIECAAAECBHakgJDdkV+bhyZAgAABAgQIEBCyfgMECBAgQIAAAQI7TuD8iaX3C9kd97V5YAIECBAgQIDA/AoMrfWNlUO/2frog0J2fn8HPjkBAgQIECBAYEcJDHfdtWfj5vHvt9Z//OKDC9kd9fV5WAIECBAgQIDAfApsHN73bW3vax8eenvnNwWE7Hz+FnxqAgQIECBAgMCOETj73be/ftdr9vx57/0tL35oIbtjvkIPSoAAAQIECBCYP4GzR26/Y/euvX819PY9l396ITt/vwefmAABAgQIECCwIwS+evdtd+7ac+MjrbXlKz2wkN0RX6OHJECAAAECBAjMl8CTxw4eHC0sfKa1tnS1Ty5k5+s34dMSIECAAAECBK55gSdOLL1h1Prne2t3vtzDCtlr/qv0gAQIECBAgACB+RG4+Ae7dr9m76NX+mdiL1cQsvPzu/BJCRAgQIAAAQLXtMC5I4s39Rtu+uvW+lsneVAhO4mS1xAgQIAAAQIECGyrwNDaro2V5Ydbbz806SEhO6mU1xEgQIAAAQIECGybwMbK0u8Nvf/0NAeE7DRaXkuAAAECBAgQIFAusHFi6UND6x+edljITivm9QQIECBAgAABAmUCF1aWfmTc2p+13kfTjgrZacW8ngABAgQIECBAoERg49jSG4eF/sXW2mtnGRSys6h5DwECBAgQIECAwJYETh1uu/fduPz3Q2srsw4J2VnlvI8AAQIECBAgQGBmgfPHlx9so/aBmQdaa0J2K3reS4AAAQIECBAgMLXAuZWD9/Q++nxrfWHqN7/oDUJ2K3reS4AAAQIECBAgMJXA0FrfWFn+Yuuz/yMF3zwoZKei92ICBAgQIECAAIGtCFw4tvST44X+B1vZELIVejYIECBAgAABAgSmEji/svx3rbe3T/Wmq7zY35GtULRBgAABAgQIECAwscCFo0vfubkwvre30duH3t7cW7974je/6IVCdhY17yFAgAABAgQIECgTePL4gX297b2v99G9rbd7W+vfNcm4kJ1EyWsIECBAgAABAgRiAufvPrTYb2jfP4zakdZGR/owvHPo/XWXP4CQjX0lDhEgQIAAAQIECMwiMLQ2unD0O44Ou0b39WF039CHt7bWv13IzqLpPQQIECBAgAABAq+qwLkTB98mZF/Vr8BxAgQIECBAgACBWQX6V+6448Y7z579n1kHvI8AAQIECBAgQIDAqyHQz60sf3bh61/70X1f/renX40HcJMAAQIECBAgQIDALAL9/InloQ3DF/ozT//g/i9d+K9ZRryHAAECBAgQIECAQFrghZC9+NcwPNqfefqHxWz6K3CPAAECBAgQIEBgFoH/D9kXavbT+1fX39Nb25xlzHsIECBAgAABAgQIpAQuC9mLf2N2+MiBtfVfSD2AOwQIECBAgAABAgRmEXhJyF4cGY0337fv5JmPzTLoPQQIECBAgAABAgQSAlcM2TYMX1/4xvjNt/zTmX9OPIQbBAgQIECAAAECBKYVuHLIttb6MPzDqbX173tXa89PO+r1BAgQIECAAAECBLZb4Kohe/HwMLRfP7B2+v7tfgj7BAgQIECAAAECBKYVeNmQbW3Y3BwPP3Dbycc/N+2w1xMgQIAAAQIECBDYToFXCNlLf1/2y//5tfWjb/yX9ux2PohtAgQIECBAgAABAtMITBCyl/4ZgwcW19Z/bZphryVAgAABAgQIECCwnQKThWxr3xi15+/Zt/qva9v5MLYJECBAgAABAgQITCowache/INfq59bO/229/qvfk1q63UECBAgQIAAAQLbKDBxyF56hvHw/sWT6x/dxucxTYAAAQIECBAgQGAigelCdhj+/blnn7vrzlNnn5po3YsIECBAgAABAgQIbJPAdCH7wr9b9ncOrJ3+2W16HrMECBAgQIAAAQIEJhKYOmR7a8+P2ub33rLqP187kbAXESBAgAABAgQIbIvADCE7fGW82T504LH1P9yWJzJKgAABAgQIECBAYAKBiUK2D+2r42H8yfG4f+LWf1z/297aMMG2lxAgQIAAAQIECBDYNoGXCdnhfBvaJ/swfHzfyccfFa/b9h0YJkCAAAEC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');
		// babackground-repeat: no-repeat;
		// background-size: 100% 100%;
		width: 100%;
		background-color: #fff;
		padding: 0 24rpx 10rpx 24rpx;
		box-sizing: border-box;
	}

	.bargain .pictxt .pictrue {
		width: 220rpx;
		height: 220rpx;
		position: relative;
	}

	.bargain .pictxt .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 6rpx;
	}

	.bargain .pictxt .text {
		font-size: 30rpx;
		color: #333333;

		.line1 {
			width: 324rpx;
		}
	}

	.bargain .pictxt .text .money {
		font-weight: bold;
		font-size: 24rpx;
	}

	.bargain .pictxt .text .money .num {
		font-size: 36rpx;
	}

	.bargain .pictxt .text .successNum {
		font-size: 22rpx;
		color: #999;
	}

	.bargain .cu-progress {
		overflow: hidden;
		height: 12rpx;
		background-color: #FDEAE8;
		width: 560rpx;
		height: 20rpx;
		border-radius: 20rpx;
		margin: 20rpx auto;
	}

	.bargain .cu-progress .bg-red {
		width: 0;
		height: 100%;
		transition: width 0.6s ease;
		border-radius: 20rpx;
		background-image: linear-gradient(to right, #ffa363 0%, #e93323 100%);
	}

	.bargain .money {
		// font-size: 22rpx;
		// color: #999;
		// margin-top: 40rpx;

	}

	.bargain .bargainSuccess {
		font-size: 26rpx;
		color: #333333;
		text-align: center;
		display: flex;
		justify-content: center;
		align-items: center;
        margin-bottom: 40rpx;
		image {
			width: 48rpx;
			height: 62rpx;
			margin-right: 18rpx;
		}
	}

	.bargain .bargainSuccess .iconfont {
		font-size: 45rpx;
		color: #54c762;
		padding-right: 18rpx;
		vertical-align: -5rpx;
	}

	.bargain .bargainBnt {

		font-size: 30rpx;
		font-weight: bold;
		color: #fff;
		width: 630rpx;
		height: 80rpx;
		background-image: url('../static/btn.png');
		background-size: 100% 100%;
		// border-radius: 40rpx;
		// background: linear-gradient(180deg, #FF8831 0%, #F11B09 100%);
		text-align: center;
		line-height: 80rpx;
		margin: 0 auto;
	}

	.bargain .bargainBnt.on {
		border: 2rpx solid $theme-color;
		color: $theme-color;
		background-image: linear-gradient(to right, #fff 0%, #fff 100%);
		width: 596rpx;
		height: 76rpx;
	}

	.bargain .bargainBnt.grey {
		color: #fff;
		background-image: linear-gradient(to right, #bbbbbb 0%, #bbbbbb 100%);
	}

	.bargain .tip {
		font-size: 22rpx;
		color: #999;
		text-align: center;
		margin-top: 20rpx;
		margin-bottom: 50rpx;
	}

	.bargain .lock,
	.bargain .bargainGang .lock,
	.bargain .goodsDetails .lock {
		background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAiQAAABCCAYAAABnwc0eAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA3ZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTQyIDc5LjE2MDkyNCwgMjAxNy8wNy8xMy0wMTowNjozOSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDo4YmQzMTQ1Ny01MGY2LWFmNDMtYmY4Yi1kNWRjZTMxZDg5MTUiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6M0RCMkU3QUEzQzBCMTFFOUI2N0VEOEJBMDUwMTU2ODMiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6M0RCMkU3QTkzQzBCMTFFOUI2N0VEOEJBMDUwMTU2ODMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTggKFdpbmRvd3MpIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6NDRmMWQxNmItZTIxMC1lYzQwLWJmODYtYzE4OWRiYzNmOGYyIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjhiZDMxNDU3LTUwZjYtYWY0My1iZjhiLWQ1ZGNlMzFkODkxNSIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PoRfwVwAAAlWSURBVHja7J3NqhxFHEer+uOaGL2ECG5U1IBgEkgeQBcqLiLJ3hcIJAs34k7XZhkRFBSSjbs8QCCLEF3oA0iuBjfBr+xcRWPCzHSXVdVVM9XV1X1nrvbC6XPg78ztnuqBDh5+9THV8sfTL4qIk7ou6HpH18vu2D1dN3Vd1fVj3ODE9z8LANhO7p55KXUYTwDAf+qJLHi/o+szXXd0va/rVV1PuDrhjplzX+g6zO0HmCR4AgBG8UQRNL6h6+19vsQEmIu6XnGJZ8Z9B5iUZPAEAIziCT9C8skajUPe0nWF+w4wKfAEAIzmCRNITrmUsiSXUhzJM3G0yMWxsrBl3ptj5pzjkmsLANsPngCAUT1hAolZcJL7o77hoaz5sPm4dBc1x/yFZNPmAvcfYBLgCQAY1RMmkJz1jXddw/0wn3m6sN95lvsPMAnwBACM6gnz6eM+yZSr4ZN9KZthmOPcf4BJgCcAYFRP2Pjih082RbeZ5Rt8KQD8f8ETADCmJ8zPfu8dyuSr4YmdFyqx+8ZcZE8q+3f9txQPvinF7Lc8vsYvui3/AgDbD54AgFE9YWLMzXhoZffNSmRH9DGpT8vcvjfHEtwq6fkATIEBT+TWFXgCAE+kPZG5PJHZ932eMIHkWi5l62zTuNBvciebvDnWxrS5ylAswCToeuIp7YbMeML5QheeAMATnTyRNYMb1hculKQ8YY7u6fqydcoEERtGitX7rDO8YtrsKf4BAKZA1xNGMCL0hAsmeAIATyzzRNEKI8u/E56wMaVSyuwrf6t1AV+251M271fcFs1e9KJWqAZgCiQ9kQeeyPAEAJ6IPGFnWZwbvCPyMukJG0jmSplnTZzT9blxh23khmCtcHIXSsy5JsmcF+75FHNEAzAJOp7wjpBOML4TgycA8IT3RB6FEf8+4QkbSB7XVhbmwHu6TjcNy0Yy0lVzgTOi2eL1kf9y1xYAtpykJ/JyFUZk6Xs+eAIATzSe8F5YzraUfmq34wk/ZaMvUtsDz35w7IelaELh6NLn9tpfXNu2ALD9dDzhHRF2YDLjiWfwBACeaHsib+eKVJ6QKhbF9dfMf5Uwq+LtOb863n5Oine/444DTJ3rrztPqKUamvfSeeJb7hEAnhAuSLgsoYLXbp7oLIkX+c4qf/gn4Zjr2Vd+ugcAIliU5js0uAEAIjLnCd9ZaY1vdEdNu4Ek2xHtRBI0wjkA4EUT6aE7qgoAdFzCAKG63hgOJGWrbQs8AwBWNNFIKgDAkCeiXJLyRpFONOF8sBL9CQUAJklWivb4K6kEABKeaEWHYU+k15D4xuHurmQRAFiKZqfd48ERANDnCRm4YmDZWTHc8xFRIqEXBAAi3mmxbRuCCQD4PCHXn13pX0PiBbP6KR8AgAskO5Fj6LAAQF/HJbX8Q24SSIIwIpENAIR9lbLxgu/9KOZtACDOE37K5j8ZIRHNOhIcAwAtLxT988H0XQDAeyLZoVk7kBRrNwaAqYqmFMs9BQAANgkkYpNAstwTTUULUrAPABhP5M1rPJvL7C4AdPJEMK0rNwkkMncNfBgBAOjxBJsnAsB+nhBirbWoiUCSid7JYcliEgCIRCNcB2ZoC0YAmKYnQj+ED+NcP5D0NUA0ACBWo6dSJgIJAIAmy4LnWwXPsulxRTqQqCjRmFc6PgCwMk0kF//z34xbAwBRpvCucH6wf2drBJK4B0QKAYCOHxJuCB8vDgAg4pFUMZgruoFEqf5V8+zYCgChGPxwrBQ80wYAunnC548wP8i0KIpkoFGp+WDmhwEgDB110/NRKpjmBQCIYoPNFMEPZdS6W8fXVXQ1L6CanwEDwMoH4WtLQngCAHyeCBazqmVCSXqi6BVN61iwsBUAQFWid1c0PAEAcZ6In62X8ERihGThhmEFC9QAoL/nk9qHRLGhIgAMdFz89O5agcRcwA+rtJaQ0PMBAO+JRb8P8AQA2I7LonusNXWzbyBZpLeDrrm3ABCJhrXvALBOx2UNN3QDSTVvN26FEYZiAcA4YZ7wAQ/hBIBEngi3jd9ohKQjGv87YqZsACD0hEi7AgDAemKR6LT0d1gGAklqnIWeDwCEgSR2BJ4AgMgT4YarAzs6F/0XiNzCr24AIBVI4gf9MlACANYTs64jBvotiTUkM5F+jDiWAQDviXm3p2LmiGt6LQAQ5wmRyBLrbIyWWtQKABCLpveZV9weABDtGZfObMtaP/udMQQLAGuIhlFUABjquMxFZ55mYCR1YMomSjO4BgBCT/if8fmn/eIKAOjkieTwSDqQ3D3zUuvAiY+ebxop/7vh9oXiz9s23//MjQfYUpL/z3/4XKvTs+rx4AkAPBF6IprL9U8Il902WeeiH/9+0g6zmNWx/tXWXNy9rM8BAPK5fP+knbYxhScAoNcTKzc0vnCe+Ph+xxNhINnR9ZmuO41gomoWu97R9YWuw9xqgEnS9UQVBRM8AYAnvCe8G+JckfBEETS+oett+5fZf772G5nIcFMTE2Au6npF1zu6Ztx3gElJJvDEfLVepHaLR/AEAJ4IPVHP+55n0/GEHyH5ZNl4eQEdSqqFSzOLeAvYt3Rd4b4DTIrIE84LoScqPAGAJwJPVC5P+Eyh5r15wgSSUy6liFYgqcMLNOI5kmcil8sVspdcWwDYfhKeWKw84V8VngDAEwGqilxR2VCS8oQJJBd05f6o+VD9V925QP1XJQ5lmTha5PYzsmlzgfsPMAkSnqhWjhDNa/1wgScA8ETgicUqT1hfVDZjpDxhAslZ33hXnzQfevC11GLRoUQ1ZcRjjnnMZ54u7Hee5f4DTIIeT6imx1NrT/xZiwe3MzwBgCcCT2RBntBh5M/+PGEWtR73SaZ0wyezXzPxx1c7g99qPqvbHH9Y1fwTAGw/PZ7I8AQA7OOJ9fKEtYmZxzEpZVN0m1kwBwQAWwyeAIAxPWFa3TuUHVgWv/yLtgDw/wFPAMConjCB5GZ58N7LrZKeD8AUwBMAMKonTCC5lktZHaCxaXOVoViASYAnAGBUT5hAsqfrywNcwLTZ48GeAJMATwDAqJ6wK08qpd7XL7c2aHxbl2kjaoVqAKYAngCAMT1hA8lcKfOsiXO6PjfuGGhYuyRzXrjnU8wRDcAkwBMAMKYnbCB5XFtZmAPv6Tqt61NdP5lr63qo6wd37Ixotnh95K/o2gLAloMnAGBMT/wjwAC10O4qfVDGDQAAAABJRU5ErkJggg==');
		background-repeat: no-repeat;
		background-size: 100% 100%;
		width: 548rpx;
		height: 66rpx;
		position: absolute;
		left: 50%;
		transform: translateX(-50%);
		bottom: -43rpx;
		z-index: 5;
	}

	.bargain .title {
		font-size: 40rpx;
		font-weight: 600;
		// height: 80rpx;
		// margin-top: 30rpx;
	}

	.bargain .title .pictrue {
		width: 60rpx;
		height: 60rpx;
	}

	.bargain .title .pictrue.on,
	.bargain .title .pictrue.on {
		transform: rotate(180deg);
	}

	.bargain .title .pictrue image,
	.bargain .title .pictrue image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.bargain .title .tits {
		margin: 50rpx 15rpx 40rpx 15rpx;
	}

	.bargain .title .titleCon {
		margin: 0 10rpx -14rpx 10rpx;
		color: #fff;
		border: 16rpx solid linear-gradient(0deg, #FFD136 0%, rgba(255, 213, 72, 0.12) 100%);
		;
	}

	.bargain .title .line {
		// width: 216rpx;
		height: 16rpx;
		background: linear-gradient(0deg, #FFD136 0%, rgba(255, 213, 72, 0.12) 100%);
		opacity: 0.6;
	}

	.bargain .bargainGang .list .item {
		padding-bottom: 50rpx;
	}

	.bargain .bargainGang .list .item .pictxt {

		// width: 310rpx;
		.num {
			width: 28rpx;
			height: 36rpx;

			// image{
			// 	width: 100%;
			// 	height: 100%;
			// }
		}

		.num1 {
			background-image: url(../static/n1.png);
			background-repeat: no-repeat;
			background-size: 100% 100%;
		}

		.num2 {
			background-image: url(../static/n2.png);
			background-repeat: no-repeat;
			background-size: 100% 100%;
		}

		.num3 {
			background-image: url(../static/n3.png);
			background-repeat: no-repeat;
			background-size: 100% 100%;
		}
	}

	.bargain .bargainGang .list .item .pictxt .pictrue {
		width: 70rpx;
		height: 70rpx;
		margin-right: 14rpx;
	}

	.bargain .bargainGang .list .item .pictxt .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 50%;
	}

	.bargain .bargainGang .list .item .pictxt .text {
		width: 225rpx;
		font-size: 20rpx;
		color: #999;
	}

	.bargain .bargainGang .list .item .pictxt .text .name {
		font-size: 25rpx;
		color: #333333;
		margin-bottom: 7rpx;
	}

	.bargain .bargainGang .list .item .money {
		font-size: 25rpx;
		color: #999999;
	}

	.bargain .bargainGang .list .item .money .iconfont {
		font-size: 35rpx;
		vertical-align: middle;
		margin-right: 10rpx;
	}

	.bargain .bargainGang .load {
		font-size: 24rpx;
		text-align: center;
		line-height: 80rpx;
		height: 80rpx;
	}

	.bargain .goodsDetails {
		width: 100%;
		background-color: #fff;
		//margin: 13rpx auto 0 auto;
	}

	.bargain .goodsDetails~.goodsDetails {
		margin-bottom: 50rpx;
	}

	.bargain .goodsDetails .conter {
		// margin-top: 20rpx;
		overflow: hidden;
	}

	.bargain .goodsDetails .conter image {
		width: 100% !important;
		display: block !important;
	}

	.bargain .bargainTip {
		position: fixed;
		top: 50%;
		left: 50%;
		width: 500rpx;
		height: 469rpx;
		margin-left: -246rpx;
		z-index: 111;
		border-radius: 14rpx;
		background-color: #fff;
		transition: all 0.3s ease-in-out 0s;
		opacity: 0;
		transform: scale(0);
		padding-bottom: 60rpx;
		margin-top: -330rpx;
		background: linear-gradient(180deg, #FFFFFF 0%, #FFEEEB 100%);

		.cu-progress {
			width: 410rpx;
			margin: 0 auto;
		}

		.t1 {
			text-align: center;
			color: #666666;
			font-size: 24rpx;
			margin: 20rpx 0 54rpx 0;
		}

		.bubbleBox {
			width: 410rpx;
			margin: 16rpx auto;

			.bubble {
				position: relative;
				display: inline-block;

				text {
					display: inline-block;
					font-size: 22rpx;
					padding: 2rpx 8rpx;
					border-radius: 30rpx;
					background-color: #E93323;
					color: #fff;
				}

				text::before {
					content: '';
					width: 0;
					height: 32rpx;
					border: 14rpx solid;
					position: absolute;
					bottom: -54rpx;
					left: 14rpx;
					border-color: #E93323 transparent transparent;
				}
			}

		}

	}

	.bargain .bargainTip.on {
		opacity: 1;
		transform: scale(1);
	}

	.bargain .bargainTip .pictrue {
		width: 500rpx;
		height: 200rpx;
		margin-top: -118rpx;
	}

	.bargain .bargainTip .pictrue image {
		width: 100%;
		height: 100%;
	}

	.bargain .bargainTip .cutOff {
		font-size: 32rpx;
		color: #333;
		text-align: center;
		margin: 12rpx 0 0rpx 0;
		font-weight: 600;

		text {
			font-weight: 600;
			font-size: 44rpx;
		}
	}

	.bargain .bargainTip .cutOff.on {
		margin-top: 26rpx;
	}

	.bargain .bargainTip .help {
		font-size: 32rpx;
		font-weight: bold;
		text-align: center;
		margin-top: 40rpx;
	}

	.bargain .bargainTip .tipBnt {
		font-size: 32rpx;
		color: #fff;
		width: 360rpx;
		height: 82rpx;
		border-radius: 41rpx;
		background-image: linear-gradient(to right, #f67a38 0%, #f11b09 100%);
		text-align: center;
		line-height: 82rpx;
		margin: 0 auto;
	}

	.bargain_view {
		width: 180rpx;
		height: 48rpx;
		background: rgba(0, 0, 0, 0.5);
		opacity: 1;
		border-radius: 0 0 6rpx 6rpx;
		position: absolute;
		bottom: 0;
		font-size: 22rpx;
		color: #fff;
		text-align: center;
		line-height: 48rpx;
	}

	.iconfonts {
		font-size: 22rpx !important;
		line-height: 220rpx;
	}

	.wxParse-div {
		width: auto !important;
		height: auto !important;
	}

	.bargain .mask {
		z-index: 100;
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
</style>
