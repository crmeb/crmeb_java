<template>
	<view :data-theme="theme">
		<skeleton :show="showSkeleton" :isNodes="isNodes" ref="skeleton" loading="chiaroscuro" selector="skeleton"
			></skeleton>
		<view class='bargain skeleton' :style="{visibility: showSkeleton ? 'hidden' : 'visible'}">
			<view class="header" :class="startBargainUid != uid ? 'on' : ''" :style="{backgroundImage:'url('+imgHost+ '/' + backBg+')'}" v-show="imgHost">
				<!-- #ifdef H5 -->
				<view class="select_nav flex justify-center align-center">
					<text class="iconfont icon-fanhui2 px-20" @tap="returns"></text>
					<text class="iconfont icon-kanjialiebiao px-20" @tap="backList"></text>
					<text class="nav_line"></text>
				</view>
				<!-- #endif -->
				<!-- #ifndef H5 -->
				<navigator url="/pages/activity/goods_bargain/index" hover-class="none">
					<view class="back">
						<text class="iconfont icon-xiangzuo"></text> 返回砍价列表
					</view>
				</navigator>
				<!-- #endif -->
			</view>
			<view class="pad30" style="padding-bottom: 30rpx;">
				<view class='wrapper'>
					<view class='pictxt acea-row row-between' @tap="goProduct">
						<view class='pictrue skeleton-rect'>
							<image :src='bargainInfo.image'></image>
						</view>
						<view class='text acea-row row-column-around'>
							<view class='line1 skeleton-rect'>{{bargainInfo.title}}</view>
							<view class="surplus skeleton-rect">最低价：￥{{bargainInfo.minPrice}}</view>
							<view class="surplus skeleton-rect">剩余：{{bargainInfo.quota}}{{bargainInfo.unitName}}</view>
							<view class='money font-color-red skeleton-rect'>
								当前: ￥
								<text class='num'>{{buyPrice}}</text>
							</view>
							<view class="acea-row skeleton-rect"
								v-if="new Date().getTime()- bargainInfo.stopTime < 0 && bargainInfo.quota>0">
								<countDown :tipText="' '" :bgColor="bgColor" :dayText="':'" :hourText="':'"
									:minuteText="':'" :secondText="' '" :datatime="datatime" :isDay="true"></countDown>
								<text style="font-size: 22rpx;margin-left: 4rpx;line-height: 36rpx;">后结束</text>
							</view>
							<view v-if="new Date().getTime()- bargainInfo.stopTime >=0" class="skeleton-rect">
								<view style="font-size: 22rpx;" @tap='currentBargainUser'>已结束</view>
							</view>
							<view v-if="bargainInfo.quota==0" class="skeleton-rect">
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
							<view style="margin-right: 40rpx;" class="skeleton-rect">已砍<text class="font-color-red"
									:class="new Date().getTime()- bargainInfo.stopTime >=0 ? 'font_hui': ''">￥{{bargainUserInfo.alreadyPrice}}</text>
							</view>
							<view class="skeleton-rect">还剩<text class="font-color-red"
									:class="new Date().getTime()- bargainInfo.stopTime >=0 ? 'font_hui': ''">￥{{bargainUserInfo.surplusPrice}}</text>
							</view>
						</view>
						<view class="cu-progress acea-row row-middle round margin-top skeleton-rect"
							:class="new Date().getTime()- bargainInfo.stopTime >=0 ? 'bg_qian': ''">
							<view class='acea-row row-middle bg-red'
								:class="new Date().getTime()- bargainInfo.stopTime >=0 ? 'bg-color-hui': ''"
								:style="'width:'+ bargainUserInfo.bargainPercent +'%;'"></view>
						</view>
						<view class='tip skeleton-rect'>
							一 已有{{bargainInfo.sales}}位好友砍价成功 一
						</view>
					</block>

					<!-- 自己砍价 -->
					<view
						v-if="startBargainUid == uid && parseFloat(bargainUserInfo.surplusPrice) > 0 && bargainStatus==1">
						<view class='bargainBnt skeleton-rect' @tap='userBargain' v-if="quota>0">
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
						<!-- #ifdef APP-PLUS -->
						<view class='bargainBnt' @click="posters = true">邀请好友帮砍价</view>
						<!-- #endif -->
					</view>

					<view
						v-if="startBargainUid != uid && bargainStatus==5 && parseFloat(bargainUserInfo.surplusPrice) > 0">
						<view class='bargainBnt' @tap='setBargainHelp'>帮好友砍一刀</view>
					</view>
					<view v-if="startBargainUid != uid && bargainStatus==4 && parseFloat(bargainUserInfo.surplusPrice) == 0" >
						<view class='bargainSuccess'>
							<image :src="urlDomain+'crmebimage/perset/activityImg/cheng.png'"></image>
							好友已砍成功
						</view>
						<view class='bargainBnt' @tap='currentBargainUser'>我也要参与</view>
					</view>
					<view v-if="startBargainUid != uid && bargainStatus==7">
						<view class='bargainSuccess'>
							<image :src="urlDomain+'crmebimage/perset/activityImg/cheng.png'"></image>
							您已帮其他好友砍过此商品
						</view>
						<view class='bargainBnt' @tap='currentBargainUser'>我也要参与</view>
					</view>
					<view v-if="startBargainUid != uid && bargainStatus==6">
						<view class='bargainSuccess'>
							<image :src="urlDomain+'crmebimage/perset/activityImg/chengh.png'"></image>
							已成功帮助好友砍价
						</view>
						<view class='bargainBnt' @tap='currentBargainUser'>我也要参与</view>
					</view>
					<view
						v-if="parseFloat(bargainUserInfo.surplusPrice) == 0 &&  startBargainUid == uid && (bargainStatus==4 || bargainStatus==8)">
						<view class='bargainSuccess'>
							<image :src="urlDomain+'crmebimage/perset/activityImg/chengh.png'"></image>
							恭喜您砍价成功，快去支付吧~
						</view>
						<view class="acea-row row-between buyBox pad30">
							<view class='buyMore on' @tap='goBargainList'>继续选购</view>
							<view v-if="bargainStatus==4" class='buyNow on' @tap='goPay'>立即支付</view>
							<view v-if="bargainStatus==8" class='buyNow on' @tap='goConfirm'>砍价记录</view>
						</view>
					</view>
					<view
						v-if="parseFloat(bargainUserInfo.surplusPrice) == 0 &&  startBargainUid == uid && bargainStatus==9">
						<view class='bargainSuccess'>
							<image :src="urlDomain+'crmebimage/perset/activityImg/chengh.png'"></image>
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
					<view v-if="bargainStatus==10" class="contentNo" style="padding: 0;">
						<view class="pb-25">
							<text class="iconfont icon-xiaolian mr8 "></text>
							您已参与
						</view>
						<view class='bargainBnt skeleton-rect'@tap='goConfirm'>
							查看记录
						</view>
					</view>
				</view>

				<!-- 砍价记录 -->
				<view class='title font-color acea-row row-center-wrapper skeleton-rect'>
					<view class='pictrue'>
						<image :src="urlDomain+'crmebimage/perset/activityImg/zuo2.png'"></image>
					</view>
					<view class="tits">
						<view class='titleCon'>砍价记录</view>
						<view class="line"></view>
					</view>
					<view class='pictrue on'>
						<image :src="urlDomain+'crmebimage/perset/activityImg/you2.png'"></image>
					</view>
				</view>
				<view class='bargainGang borRadius14 skeleton-rect'>
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
						<image :src="urlDomain+'crmebimage/perset/activityImg/zuo2.png'"></image>
					</view>
					<view class="tits">
						<view class='titleCon'>商品详情</view>
						<view class="line"></view>
					</view>
					<view class='pictrue on'>
						<image :src="urlDomain+'crmebimage/perset/activityImg/you2.png'"></image>
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
					<view class='pictrue' :style="{backgroundImage:'url('+imgHost +'/'+backHead+')'}" v-show="imgHost"></view>
					<view v-if="startBargainUid == uid">
						<view class='cutOff'>
							您已砍掉<text class='font_color'>{{bargainUserBargainPrice}}元</text>
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
						<!-- #ifdef APP-PLUS -->
						<view class='tipBnt' @click="posters = true">邀请好友帮砍价</view>
						<!-- #endif -->
					</view>
					<view v-else>
						<view class='cutOff'>
							帮好友砍掉<text class='font_color'>{{bargainUserBargainPrice}}元</text>
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
			<view class='mask' catchtouchmove="true" v-show='active==true || posters==true || canvasStatus'></view>
		</view>
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

		<!-- 发送给朋友图片 -->
		<view class="share-box" v-if="H5ShareBox">
			<image :src="urlDomain+'crmebimage/perset/staticImg/share-info.png'" @click="H5ShareBox = false"></image>
		</view>

		<!-- 海报展示 -->
		<view class='poster-pop' v-if="canvasStatus">
			<image :src='imagePath'></image>
			<!-- #ifndef H5  -->
			<view class='save-poster' @click="savePosterPath">保存到手机</view>
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<view class="keep">长按图片保存至相册</view>
			<view class="close_box">
				<text class='iconfont icon-cha2 close' @tap='close'></text>
			</view>
			<!-- #endif -->
		</view>
		<view class="canvas">
			<canvas style="width:597px;height:850px;" canvas-id="activityCanvas"></canvas>
			<canvas canvas-id="qrcode" :style="{width: `${qrcodeSize}px`, height: `${qrcodeSize}px`}"
				style="opacity: 0;" />
		</view>
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
	import {imageBase64} from "@/api/public";
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js';
	import {postCartAdd} from '../../../api/store.js';
	import util from '../../../utils/util.js';
	import {toLogin} from '@/libs/login.js';
	import {mapGetters} from "vuex";
	import { getImageDomain } from '@/api/api.js'
	import countDown from '@/components/countDown';
	import parser from "@/components/jyf-parser/jyf-parser";
	import {
		silenceBindingSpread
	} from "@/utils";
	// #ifdef APP-PLUS
	import {
		TOKENNAME,
		HTTP_H5_URL
	} from '@/config/app.js';
	// #endif
	import { BACK_URL} from '@/config/cache';
	const app = getApp();

	export default {
		components: {
			countDown,
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
				urlDomain: this.$Cache.get("imgHost"),
				showSkeleton: true, //骨架屏显示隐藏
				isNodes: 0, //控制什么时候开始抓取元素节点,只要数值改变就重新抓取
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
					img: 'width:100%;display:block;',
					table: 'width:100%',
					video: 'width:100%'
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
				posterbackgd: '../static/images/bargain_post1.png',
				PromotionCode: '', //二维码
				canvasStatus: false,
				imgTop: '', //商品图base64位
				imagePath: '', // 海报图片
				theme:app.globalData.theme,
				imgHost:'',
				backBg:'crmebimage/perset/bargain_header_bg/bargain_header_bg1.jpg',
				backHead:'crmebimage/perset/bargain_tip/bargain_tip1.png'
			}

		},
		computed: mapGetters(['isLogin', 'userInfo', 'uid']),
		/**
		 * 生命周期函数--监听页面加载
		 */
		onLoad: function(options) {
			setTimeout(() => {
				this.isNodes++;
			}, 200);
			let that = this;
			that.$set(that,'theme',that.$Cache.get('theme'));
			getImageDomain().then(res=>{
				that.$set(that,'imgHost',res.data);
			})
			switch (that.theme) {
				case 'theme1':
					that.backBg = 'crmebimage/perset/bargain_header_bg/bargain_header_bg1.jpg';
					that.backHead = 'crmebimage/perset/bargain_tip/bargain_tip1.png';
					that.posterbackgd = require('../images/bargain_post1.png') // 因为跨域不能使用网络图片，
					break;
				case 'theme2':
					that.backBg = 'crmebimage/perset/bargain_header_bg/bargain_header_bg2.jpg';
					that.backHead = 'crmebimage/perset/bargain_tip/bargain_tip2.png';
					that.posterbackgd = require('../images/bargain_post2.png') 
					break;
				case 'theme3':
					that.backBg = 'crmebimage/perset/bargain_header_bg/bargain_header_bg3.jpg';
					that.backHead = 'crmebimage/perset/bargain_tip/bargain_tip3.png';
					that.posterbackgd = require('../images/bargain_post3.png') 
					break;
				case 'theme4':
					that.backBg = 'crmebimage/perset/bargain_header_bg/bargain_header_bg4.jpg';
					that.backHead = 'crmebimage/perset/bargain_tip/bargain_tip4.png';
					that.posterbackgd = require('../images/bargain_post4.png') 
					break;
				case 'theme5':
					that.backBg = 'crmebimage/perset/bargain_header_bg/bargain_header_bg5.jpg';
					that.backHead = 'crmebimage/perset/bargain_tip/bargain_tip5.png';
					that.posterbackgd = require('../images/bargain_post5.png') 
					break;
			}
			that.$store.commit("PRODUCT_TYPE", 'normal');
			// #ifdef MP
			uni.getSystemInfo({
				success: function(res) {
					that.systemH = res.statusBarHeight
					that.navH = that.systemH + 10
				}
			})
			// #endif
			
            
            // 链接进入获取绑定关系id
            if(options.spread) app.globalData.spread = options.spread; 
	
			var pages = getCurrentPages();
			if (pages.length <= 1) {
				that.retunTop = false
			}
			
			if (options.hasOwnProperty('id') || options.scene) {
				if (options.scene) { // 仅仅小程序扫码进入
					let qrCodeValue = this.$util.getUrlParams(decodeURIComponent(options.scene));
					let mapeMpQrCodeValue = this.$util.formatMpQrCodeData(qrCodeValue);
			        app.globalData = mapeMpQrCodeValue;
					app.globalData.spread = mapeMpQrCodeValue.spread; 
					this.id = app.globalData.id
				}else{
					this.id = options.id;
				}
			}
			options.startBargainUid == 'undefined' ? that.startBargainUid = 0 : that.startBargainUid = Number(options
				.startBargainUid);
				
			if (this.isLogin) {
				if (that.startBargainUid == 0) {
					that.startBargainUid = Number(that.$store.state.app.uid)
				}
				this.storeBargainId = options.storeBargainId ? Number(options.storeBargainId) : 0;
				this.page = 1;
			} else {
				this.$Cache.set(BACK_URL,
					'/pages/activity/goods_bargain_details/index?id=' + options.id +
					'&startBargainUid=' + this.uid + '&spread=' + this.uid + '&storeBargainId=' + this.storeBargainId
				);
				toLogin();
			}
			uni.setNavigationBarTitle({
				title: '砍价详情'
			})
			if(this.isLogin && parseInt(app.globalData.spread)>0){
				silenceBindingSpread()
			}
		},
		methods: {
			//去支付
			goConfirm() {
				uni.navigateTo({
					url: `/pages/activity/bargain/index`
				})
			},
			// app分享
			// #ifdef APP-PLUS
			appShare(scene) {
				let that = this
				let routes = getCurrentPages(); // 获取当前打开过的页面路由数组
				let curRoute = routes[routes.length - 1].$page.fullPath // 获取当前页面路由，也就是最后一个打开的页面路由"
				let href = HTTP_H5_URL + '/pages/activity/goods_bargain_details/index?id=' + this.id +
					'&startBargainUid=' + this
					.uid + '&spread=' + this.uid + '&storeBargainId=' + this.storeBargainId;
				uni.share({
					provider: "weixin",
					scene: scene,
					type: 0,
					href: href,
					title: '您的好友' + that.userInfo.nickname + '邀请您帮他砍' + that.bargainInfo.title + ' 快去帮忙吧！',
					imageUrl: that.bargainInfo.image,
					success: function(res) {
						that.posters = false;
					},
					fail: function(err) {
						uni.showToast({
							title: '分享失败',
							icon: 'none',
							duration: 2000
						})
						that.posters = false;
					}
				});
			},
			// #endif
			//#ifdef H5
			setOpenShare() {
				let that = this;
				let configTimeline = {
					title: "您的好友" +
						that.userInfo.nickname +
						"邀请您砍价" +
						that.bargainInfo.title,
					desc: that.bargainInfo.title,
					link: window.location.protocol +
						"//" +
						window.location.host +
						'/pages/activity/goods_bargain_details/index?id=' + this.id + '&startBargainUid=' + this
						.uid + '&spread=' + this.uid + '&storeBargainId=' + this.storeBargainId,
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
					url: `/pages/goods/goods_details/index?id=${this.bargainInfo.productId}`
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
					.uid + '&spread=' + this.uid + '&storeBargainId=' + this.storeBargainId;
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
						.uid + '&spread=' + that.uid + '&storeBargainId=' + that.storeBargainId;
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
					setTimeout(() => {
						that.showSkeleton = false
					}, 1000)
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
			},
			// 返回
			returns() {
				uni.switchTab({
					url:'/pages/index/index'
				})
			},
			backList(){
				uni.navigateTo({
					url:'/pages/activity/goods_bargain/index'
				})
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
			//校验token是否有效,true为有效，false为无效
			this.$LoginAuth.getTokenIsExist().then(data => {
				if (data) {
					this.getBargainDetails();
				}
			});
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

		//#ifdef MP
		/**
		 * 用户点击右上角分享
		 */
		onShareAppMessage: function() {
			let that = this,
				share = {
					title: '您的好友' + that.userInfo.nickname + '邀请您帮他砍' + that.bargainInfo.title + ' 快去帮忙吧！',
					path: '/pages/activity/goods_bargain_details/index?id=' + this.id + '&startBargainUid=' + this
						.startBargainUid + '&spread=' + this.uid + '&storeBargainId=' + this.storeBargainId,
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
		@include main_bg_color(theme);
	}
</style>
<style lang="scss" scoped>
	.home_back{
		display: block;
		color: #000;
		font-size: 32px;
		text-align: center;
		width: 58rpx;
		height: 58rpx;
		line-height: 58rpx;
		background: rgba(255, 255, 255, 0.3);
		border: 1px solid rgba(0, 0, 0, 0.1);
		border-radius: 50%;
		margin-bottom: 20rpx;
		margin-left: 20rpx;
	}
	.select_nav{
		width: 170rpx !important;
		height: 60rpx !important;
		border-radius: 33rpx;
		background: rgba(0, 0, 0, 0.4);
		border: 1px solid rgba(0,0,0,0.1);
		color: #fff;
		font-size: 18px;
		line-height: 58rpx;
		position: relative;
		left: 20rpx;
	}
	.px-20{
		padding: 0 20rpx 0;
	}
	.justify-center{
		justify-content: center;
	}
	.align-center {
		align-items: center;
	}
	.nav_line{
		content: '';
		display: inline-block;
		width: 1px;
		height: 34rpx;
		background: rgba(255, 255, 255, 0.1);
		position: absolute;
		left: 0;
		right: 0;
		margin: auto;
	}
	
	
	.userEvaluation {
		i {
			display: inline-block;
		}
	}
	.bargain{
		/* #ifdef MP || APP-PLUS */
		@include main_bg_color(theme);
		/* #endif */
		height: 100vh;
		overflow: auto;
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
		.close_box{
			text-align: center;
			margin-top: 55rpx;
		}
		.close {
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
		@include price_color(theme);
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
		// box-shadow: 0 7rpx 0 #AAAAAA;
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
			@include linear-gradient(theme);
			// box-shadow: 0 7rpx 0 #C11100;
		}

		.buyMore {
			background: linear-gradient(180deg, #FFCA52 0%, #FE960F 100%);
			// box-shadow: 0 7rpx 0 #D87A00;
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
		// @include bargain-header-bg(theme);
		background-repeat: no-repeat;
		background-size: 100% 100%;
		width: 100%;
		height: 340rpx;
		margin: 0 auto;
		padding-top: 20rpx;

		.back {
			width: 235rpx;
			height: 54rpx;
			@include second-gradient(theme);
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
		/* #ifdef MP || APP-PLUS */
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
			// background-image: url('../static/zhuangshi.png');
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
		background-color: #eee;
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
		@include linear-gradient(theme);
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
		// background-image: url('../static/btn.png');
		background-size: 100% 100%;
		border-radius: 40rpx;
		@include bargain-btn(theme);
		text-align: center;
		line-height: 80rpx;
		margin: 0 auto;
	}
	.pb-25{
		padding-bottom: 25rpx;
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
		background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAiQAAABCCAYAAABnwc0eAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA3ZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTQyIDc5LjE2MDkyNCwgMjAxNy8wNy8xMy0wMTowNjozOSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDo4YmQzMTQ1Ny01MGY2LWFmNDMtYmY4Yi1kNWRjZTMxZDg5MTUiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6M0RCMkU3QUEzQzBCMTFFOUI2N0VEOEJBMDUwMTU2ODMiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6M0RCMkU3QTkzQzBCMTFFOUI2N0VEOEJBMDUwMTU2ODMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTggKFdpbmRvd3MpIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6NDRmMWQxNmItZTIxMC1lYzQwLWJmODYtYzE4OWRiYzNmOGYyIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjhiZDMxNDU3LTUwZjYtYWY0My1iZjhiLWQ1ZGNlMzFkODkxNSIv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');
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
			background-image: url('../images/n1.png');
			background-repeat: no-repeat;
			background-size: 100% 100%;
		}

		.num2 {
			background-image: url('../images/n2.png');
			background-repeat: no-repeat;
			background-size: 100% 100%;
		}

		.num3 {
			background-image: url('../images/n3.png');
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
		height: unset !important;
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
					@include main_bg_color(theme);
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
					@include bragin-border-rate(theme);
				}
			}

		}

	}
	.pictrue{
		// @include bargain-tip-header(theme);
		background-size: 100%;
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
	.font_color{
		@include price_color(theme);
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
		// background-image: linear-gradient(to right, #f67a38 0%, #f11b09 100%);
		@include linear-gradient(theme);
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
