<template>
	<view :data-theme="theme">
		<skeleton :show="showSkeleton" :isNodes="isNodes" ref="skeleton" loading="chiaroscuro" selector="skeleton"
			bgcolor="#FFF"></skeleton>
		<view class="product-con skeleton" :style="{visibility: showSkeleton ? 'hidden' : 'visible'}">
			<view class='navbar' :class="opacity>0.6?'bgwhite':''">
				<view class='navbarH' :style='"height:"+navH+"rpx;"'>
					<view class='navbarCon acea-row' :style="{ paddingRight: navbarRight + 'px' }">
						<!-- #ifdef MP -->
						<view class="select_nav flex justify-center align-center" id="home" :style="{ top: homeTop + 'rpx' }">
							<text class="iconfont icon-fanhui2 px-20" @tap="returns"></text>
							<text class="iconfont icon-gengduo5 px-20" @tap="showNav"></text>
							<text class="nav_line"></text>
						</view>
						<!-- #endif -->
						<!-- #ifdef H5 || APP-PLUS -->
						<view id="home" class="home acea-row row-center-wrapper iconfont icon-xiangzuo h5_back"
							:class="opacity>0.5?'on':''" :style="{ top: homeTop + 'rpx' }" v-if="returnShow"
							@tap="returns">
						</view>
						<!-- #endif -->
						<!-- 头部tab标题 -->
						<!-- #ifdef H5  || APP-PLUS-->
						<view class="tab_nav" v-show="opacity > 0.6">
							<view class="header flex justify-between align-center">
								<view class="item" :class="navActive === index ? 'on' : ''" v-for="(item,index) in navList"
									:key='index' @tap="tap(index)">
									{{ item }}
								</view>
							</view>
						</view>
						<!-- #endif -->
						<!-- #ifdef H5 || APP-PLUS -->
						<view class="right_select" :style="{ top: homeTop + 'rpx' }" @tap="showNav">
							<text class="iconfont icon-gengduo2"></text>
						</view>
						<!-- #endif -->
					</view>
				</view>
			</view>
			<view class="dialog_nav" v-show="currentPage" :style="{ top: navH + 'rpx' }">
				<view class="dialog_nav_item" :class="item.after" v-for="(item,index) in selectNavList" :key="index"
					@click="linkPage(item.url)">
					<text class="iconfont" :class="item.icon"></text>
					<text class="pl-20">{{item.name}}</text>
				</view>
			</view>
			<view class="detail_container">
				<scroll-view :scroll-top="scrollTop" scroll-y='true' scroll-with-animation="true"
					:style='"height:"+height+"px;"' @scroll="scroll">
					<view id="past0">
						<productConSwiper class="skeleton-rect" :imgUrls="sliderImage" :videoline="videoLink"
							@videoPause="videoPause"></productConSwiper>
						<!-- 氛围图card -->
						<activity-style v-if="productInfo.activityStyle" :productInfo="productInfo"></activity-style>
						<view class="pad30">
							<view class='wrapper mb30 borRadius14'>
								<view class='share acea-row row-between row-bottom share-icon-box'>
									<view class='x-money skeleton-rect flex align-baseline' v-if="!productInfo.activityStyle">￥
										<text class='num font-44'>{{attr.productSelect.price}}</text>
										<view class="flex  pl-2"
											v-if="attr.productSelect.vipPrice && attr.productSelect.vipPrice > 0">
											<image :src="urlDomain+'crmebimage/perset/staticImg/vip_badge.png'" class="vip_icon"></image>
											<text
												class='vip_money skeleton-rect'>￥{{attr.productSelect.vipPrice}}</text>
										</view>
									</view>
									<view class='iconfont icon-fenxiang share-icon'  @click="listenerActionSheet"></view>
								</view>
								<view class='introduce skeleton-rect share-introduce'>{{productInfo.storeName}}</view>
								<view class='label acea-row row-between-wrapper'>
									<view class="skeleton-rect">原价:￥{{attr.productSelect.otPrice || 0}}</view>
									<view class="skeleton-rect">
										库存:{{productInfo.stock || 0}}{{productInfo.unitName || ''}}</view>
									<view class="skeleton-rect">
										销量:{{Math.floor(productInfo.sales) + Math.floor(productInfo.ficti) || 0}}{{productInfo.unitName || ''}}
									</view>
								</view>
								<view v-if="defaultCoupon.length>0 && type=='normal'"
									class='coupon acea-row row-between-wrapper' @click='couponTap'>
									<view class='hide line1 acea-row skeleton-rect'>优惠券：
										<view class='activity'>满{{defaultCoupon[0].minPrice}}减{{defaultCoupon[0].money}}
										</view>
									</view>
									<view class='iconfont icon-jiantou'></view>
								</view>
								<view class="coupon acea-row row-between-wrapper" v-if="activityH5.length">
									<view class="line1 acea-row">
										<text class="activityName skeleton-rect">活&nbsp;&nbsp;&nbsp;动：</text>
										<view v-for='(item,index) in activityH5' :key='index' @click="goActivity(item)"
											class="activityBox">
											<view v-if="item.type === '1'" class="skeleton-rect"
												:class="index==0?'activity_pin':'' || index==1?'activity_miao':'' || index==2?'activity_kan':''">
												<text class="iconfonts iconfont icon-miaosha1"></text>
												<text class="activity_title"> 参与秒杀</text>
											</view>
											<view class="skeleton-rect"
												:class="index==0?'activity_pin':'' || index==1?'activity_miao':'' || index==2?'activity_kan':''"
												v-if="item.type === '2'">
												<text class="iconfonts iconfont icon-kanjia"></text>
												<text class="activity_title"> 参与砍价</text>
											</view>
											<view class="skeleton-rect"
												:class="index==0?'activity_pin':'' || index==1?'activity_miao':'' || index==2?'activity_kan':''"
												v-if="item.type === '3'">
												<text class="iconfonts iconfont icon-pintuan"></text>
												<text class="activity_title"> 参与拼团</text>
											</view>
										</view>
									</view>
								</view>
							</view>
							<view class='attribute mb30 borRadius14' @click="selecAttr">
								<view class="acea-row row-between-wrapper">
									<view class="line1 skeleton-rect">{{attrTxt}}：
										<text class='atterTxt'>{{attrValue}}</text>
									</view>
									<view class='iconfont icon-jiantou'></view>
								</view>
								<view class="acea-row row-between-wrapper" style="margin-top:7px;padding-left:55px;"
									v-if="skuArr.length > 1">
									<view class="flex">
										<image :src="item.image" v-for="(item,index) in skuArr.slice(0,4)" :key="index"
											class="attrImg"></image>
									</view>
									<view class="switchTxt">共{{skuArr.length}}种规格可选</view>
								</view>
							</view>
							<view class='userEvaluation' id="past1">
								<view class='title acea-row row-between-wrapper'
									:style="replyCount==0?'border-bottom-left-radius:14rpx;border-bottom-right-radius:14rpx;':''">
									<view>用户评价<i>({{replyCount}})</i></view>
									<navigator class='praise' hover-class='none'
										:url='"/pages/goods/goods_comment_list/index?productId="+id'>
										<i>好评</i>&nbsp;<text class='font_color px-12'>{{replyChance || 0}}%</text>
										<text class='iconfont icon-jiantou'></text>
									</navigator>
								</view>
								<block v-if="replyCount">
									<userEvaluation :reply="reply"></userEvaluation>
								</block>
							</view>
							<!-- 优品推荐 -->
							<view class="superior borRadius14" if='good_list.length' id="past2">
								<view class="title acea-row row-center-wrapper">
									<image :src="urlDomain+'crmebimage/perset/staticImg/xzuo.png'"></image>
									<view class="titleTxt">优品推荐</view>
									<image :src="urlDomain+'crmebimage/perset/staticImg/xyou.png'"></image>
								</view>
								<view class="slider-banner banner">
									<swiper indicator-dots="true" :autoplay="autoplay" :circular="circular"
										:interval="interval" :duration="duration" indicator-color="#999"
										:indicator-active-color="indicatorBg" :style="'height:'+clientHeight+'px'">
										<swiper-item v-for="(item,indexw) in good_list" :key="indexw">
											<view class="list acea-row row-middle" :id="'list'+indexw">
												<view class="item" v-for="(val,indexn) in item.list" :key="indexn"
													@click="goDetail(val)">
													<view class="pictrue">
														<image :src="val.image"></image>
														<span class="pictrue_log pictrue_log_class"
															v-if="val.activityH5 && val.activityH5.type === '1'">秒杀</span>
														<span class="pictrue_log pictrue_log_class"
															v-if="val.activityH5 && val.activityH5.type === '2'">砍价</span>
														<span class="pictrue_log pictrue_log_class"
															v-if="val.activityH5 && val.activityH5.type === '3'">拼团</span>
													</view>
													<view class="name line1">{{val.storeName}}</view>
													<view class="money theme_price">¥{{val.price}}</view>
												</view>
											</view>
										</swiper-item>
										<!-- <view class="swiper-pagination" slot="pagination"></view> -->
									</swiper>
								</view>
							</view>
						</view>
					</view>
					<view class='product-intro' id="past3">
						<view class='title'>
							<image :src="urlDomain+'crmebimage/perset/staticImg/xzuo.png'"></image>
							<span class="sp">产品详情</span>
							<image :src="urlDomain+'crmebimage/perset/staticImg/xyou.png'"></image>
						</view>
						<view class='conter'>
							<jyf-parser :html="description" ref="article" :tag-style="tagStyle"></jyf-parser>
						</view>
					</view>
					<view style='height:120rpx;'></view>
				</scroll-view>
			</view>
			<view class='footer acea-row row-between-wrapper'>
				<!-- #ifdef MP -->
				<button hover-class='none' class='item skeleton-rect' @click="onClickService"
					v-if="chatConfig.telephone_service_switch === 'open'">
					<view class='iconfont icon-kefu'></view>
					<view>客服</view>
				</button>
				<template v-else>
					<button open-type="contact" hover-class='none' class='item skeleton-rect' v-if="chatConfig.wx_chant_independent=='open'">
						<view class='iconfont icon-kefu'></view>
						<view>客服</view>
					</button>
					<button class="item" hover-class='none' @click="wxChatService"  v-else>
						<view class='iconfont icon-kefu'></view>
						<text>联系客服</text>
					</button>
				</template>
				<!-- #endif -->
				<!-- #ifndef MP -->
				<view class="item skeleton-rect" @click="onClickService">
					<view class="iconfont icon-kefu"></view>
					<view>客服</view>
				</view>
				<!-- #endif -->
				<block v-if="type === 'normal'">
					<view @click="setCollect" class='item skeleton-rect'>
						<view class='iconfont icon-shoucang1' v-if="userCollect"></view>
						<view class='iconfont icon-shoucang' v-else></view>
						<view>收藏</view>
					</view>
					<navigator open-type='switchTab' class="animated item skeleton-rect"
						:class="animated==true?'bounceIn':''" url='/pages/order_addcart/order_addcart'
						hover-class="none">
						<view class='iconfont icon-gouwuche1'>
							<text v-if="Math.floor(CartCount)>0" class='num bg_color'>{{CartCount}}</text>
						</view>
						<view>购物车</view>
					</navigator>
					<view class="bnt acea-row skeleton-rect" v-if="attr.productSelect.stock <= 0">
						<form @submit="joinCart" report-submit="true"><button class="joinCart bnts"
								form-type="submit">加入购物车</button></form>
						<form report-submit="true"><button class="bnts bg-color-hui" form-type="submit">已售罄</button>
						</form>
					</view>
					<view class="bnt acea-row skeleton-rect" v-else>
						<form @submit="joinCart" report-submit="true"><button class="joinCart bnts"
								form-type="submit">加入购物车</button></form>
						<form @submit="goBuy" report-submit="true"><button class="buy bnts"
								form-type="submit">立即购买</button>
						</form>
					</view>
				</block>
				<view class="bnt bntVideo acea-row skeleton-rect"
					v-if="attr.productSelect.stock <= 0 && type === 'video'">
					<form report-submit="true"><button class="bnts bg-color-hui" form-type="submit">已售罄</button>
					</form>
				</view>
				<view class="bnt bntVideo acea-row skeleton-rect"
					v-if="attr.productSelect.stock > 0 && type === 'video'">
					<form @submit="goBuy" report-submit="true"><button class="buy bnts" form-type="submit">立即购买</button>
					</form>
				</view>
			</view>
			<shareRedPackets :sharePacket="sharePacket" @listenerActionSheet="listenerActionSheet"
				@showShare="showShare"></shareRedPackets>
			<!-- 组件 -->
			<productWindow :attr="attr" :isShow='1' :iSplus='1' @myevent="onMyEvent" @ChangeAttr="ChangeAttr"
				@ChangeCartNum="ChangeCartNum" @attrVal="attrVal" @iptCartNum="iptCartNum" id='product-window'
				@getImg="showImg">
			</productWindow>
			<couponListWindow :coupon='coupon' :typeNum="couponDeaultType[0].useType"
				@ChangCouponsClone="ChangCouponsClone" @ChangCoupons="ChangCoupons"
				@ChangCouponsUseState="ChangCouponsUseState" @tabCouponType="tabCouponType"></couponListWindow>
			<!-- 分享按钮 -->
			<view class="generate-posters" :class="posters ? 'on' : ''">
				<view class="generateCon acea-row row-middle">
					<!-- #ifndef MP -->
					<button class="item" hover-class="none" v-if="weixinStatus === true" @click="H5ShareBox = true">
						<view class="pictrue">
							<image :src="urlDomain+'crmebimage/perset/staticImg/weixin.png'"></image>
						</view>
						<view class="">分享给好友</view>
					</button>
					<!-- #endif -->
					<!-- #ifdef MP -->
					<button class="item" open-type="share" hover-class="none">
						<view class="pictrue">
							<image :src="urlDomain+'crmebimage/perset/staticImg/weixin.png'"></image>
						</view>
						<view class="">分享给好友</view>
					</button>
					<!-- #endif -->
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
					<!-- #ifdef H5 || MP -->
					<view class="item" @click="getpreviewImage">
						<view class="pictrue">
							<image :src="urlDomain+'crmebimage/perset/staticImg/changan.png'"></image>
						</view>
						<view class="">预览发图</view>
					</view>
					<!-- #endif -->
					<!-- #ifdef MP  -->
					<button class="item" hover-class="none" @click="savePosterPath">
						<view class="pictrue">
							<image :src="urlDomain+'crmebimage/perset/staticImg/haibao.png'"></image>
						</view>
						<view class="">保存海报</view>
					</button>
					<!-- #endif -->
				</view>
				<view class="generateClose acea-row row-center-wrapper" @click="posterImageClose">取消</view>
			</view>
			<cus-previewImg ref="cusPreviewImg" :list="skuArr" @changeSwitch="changeSwitch"
				@shareFriend="listenerActionSheet" />
			<view class="mask" v-if="posters" @click="closePosters"></view>
			<view class="mask" v-if="canvasStatus"></view>
			<view class="mask_transparent" v-if="currentPage" @touchmove="hideNav" @click="hideNav()"></view>
			<!-- 海报展示 -->
			<view class='poster-pop' v-if="canvasStatus">
				<image :src='imagePath'></image>
			</view>
			<view class="canvas" v-else>
				<canvas style="width:750px;height:1190px;" canvas-id="firstCanvas"></canvas>
				<canvas canvas-id="qrcode" :style="{width: `${qrcodeSize}px`, height: `${qrcodeSize}px`}" />
			</view>
			<!-- 发送给朋友图片 -->
			<view class="share-box" v-if="H5ShareBox">
				<image :src="urlDomain+'crmebimage/perset/staticImg/share-info.png'" @click="H5ShareBox = false"></image>
			</view>
		</view>
	</view>
</template>

<script>
	import activityStyle from "./components/activityStyle.vue";
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js'
	import store from '@/store';
	import {
		HTTP_H5_URL
	} from '@/config/app.js';
	import {
		spread
	} from "@/api/user";
	import {
		getProductDetail,
		collectAdd,
		collectDel,
		postCartAdd,
		getReplyList,
		getReplyConfig,
		getProductGood,
		getReplyProduct
	} from '@/api/store.js';
	import {
		getCoupons,
		tokenIsExistApi
	} from '@/api/api.js';
	import {
		getCartCounts
	} from '@/api/order.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		mapGetters
	} from "vuex";
	import {
		imageBase64
	} from "@/api/public";
	import productConSwiper from '@/components/productConSwiper';
	import couponListWindow from '@/components/couponListWindow';
	import productWindow from '@/components/productWindow';
	import userEvaluation from '@/components/userEvaluation';
	import shareRedPackets from '@/components/shareRedPackets';
	import cusPreviewImg from '@/components/cus-previewImg/cus-previewImg.vue'
	import {
		silenceBindingSpread
	} from "@/utils";
	import parser from "@/components/jyf-parser/jyf-parser";
	import {
		computeUser
	} from "@/api/user.js";
	// #ifdef MP
	import {
		base64src
	} from '@/utils/base64src.js'
	import {
		getQrcode
	} from '@/api/api.js';
	// #endif
	let app = getApp();
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
	import {
		Debounce
	} from '@/utils/validate.js'
		import navBar from '@/components/navBar';
	export default {
		components: {
			productConSwiper,
			couponListWindow,
			productWindow,
			userEvaluation,
			shareRedPackets,
			cusPreviewImg,
			"jyf-parser": parser,
			activityStyle,
			navBar
		},
		data() {
			let that = this;
			return {
				urlDomain: this.$Cache.get("imgHost"),
				showSkeleton: true, //骨架屏显示隐藏
				isNodes: 0, //控制什么时候开始抓取元素节点,只要数值改变就重新抓取
				//属性是否打开
				coupon: {
					coupon: false,
					type: 0,
					list: [],
					count: []
				},
				attrTxt: '请选择', //属性页面提示
				attrValue: '', //已选属性
				animated: false, //购物车动画
				id: 0, //商品id
				replyCount: 0, //总评论数量
				reply: [], //评论列表
				productInfo: {}, //商品详情
				productValue: [], //系统属性
				couponList: [], //优惠券
				cart_num: 1, //购买数量
				isAuto: false, //没有授权的不会自动授权
				isShowAuth: false, //是否隐藏授权
				isOpen: false, //是否打开属性组件
				actionSheetHidden: true,
				storeImage: '', //海报产品图
				PromotionCode: '', //二维码图片
				posterbackgd: `${this.$Cache.get("imgHost")}crmebimage/perset/staticImg/posterbackgd.png`,
				sharePacket: {
					isState: true, //默认不显示
					touchstart: false
				}, //分销商详细
				circular: false,
				autoplay: false,
				interval: 3000,
				duration: 500,
				clientHeight: "",
				systemStore: {}, //门店信息
				good_list: [],
				replyChance: 0,
				CartCount: 0,
				isDown: true,
				posters: false,
				weixinStatus: false,
				attr: {
					cartAttr: false,
					productAttr: [],
					productSelect: {}
				},
				description: '',
				navActive: 0,
				H5ShareBox: false, //公众号分享图片
				activityH5: [],
				retunTop: true, //顶部返回
				navH: "",
				navList: [],
				opacity: 0,
				scrollY: 0,
				topArr: [],
				toView: '',
				height: 0,
				heightArr: [],
				lock: false,
				scrollTop: 0,
				tagStyle: {
					img: 'width:100%;display:block;',
					table: 'width:100%',
					video: 'width:100%'
				},
				sliderImage: [],
				videoLink: '',
				qrcodeSize: 600,
				canvasStatus: false, //是否显示海报
				imagePath: '', //海报路径
				imgTop: '',
				errT: '',
				homeTop: 20,
				navbarRight: 0,
				userCollect: false,
				returnShow: true, //判断顶部返回是否出现
				type: "", 
				theme: app.globalData.theme,
				indicatorBg: '',
				shareStatus: true,
				skuArr: [],
				currentPage: false,
				selectSku: {},
				selectNavList: [{
						name: '首页',
						icon: 'icon-shouye8',
						url: '/pages/index/index',
						after: 'dialog_after'
					},
					{
						name: '搜索',
						icon: 'icon-sousuo6',
						url: '/pages/goods/goods_search/index',
						after: 'dialog_after'
					},
					{
						name: '购物车',
						icon: 'icon-gouwuche7',
						url: '/pages/order_addcart/order_addcart',
						after: 'dialog_after'
					},
					{
						name: '我的收藏',
						icon: 'icon-shoucang3',
						url: '/pages/users/user_goods_collection/index',
						after: 'dialog_after'
					},
					{
						name: '个人中心',
						icon: 'icon-gerenzhongxin1',
						url: '/pages/user/index'
					},
				],
				chatConfig: {
					consumer_hotline: '',
					telephone_service_switch: 'close',
					wx_chant_independent:'open'
				}, //客服配置
				defaultCoupon: [],
				couponDeaultType: [{
					useType: 1
				}],
				//优惠券分页数据
				where: {
					page: 1,
					limit: 999,
					productId: 0,
					type: 0
				},
				couponType: 0, //优惠券类型 类型，1-通用，2-商品，3-品类
			};
		},
		computed: mapGetters(['isLogin', 'uid', 'chatUrl', 'productType']),
		watch: {
			productInfo: {
				handler: function() {
					this.$nextTick(() => {});
				},
				immediate: true
			}
		},
		onShow() {
			//校验token是否有效,true为有效，false为无效
			this.getTokenIsExist();
		},
		onLoad(options) {

			//用户从分享卡片进入的场景下获取主题色配置
			this.$set(this, 'theme', this.$Cache.get('theme'));

			//判断顶部返回是否出现
			var pages = getCurrentPages();
			this.returnShow = pages.length === 1 ? false : true;
			if (pages.length <= 1) {
				this.retunTop = false
			}

			//页面中需要计算的一些值
			let that = this;
			uni.getSystemInfo({
				success: function(res) {
					that.height = res.windowHeight
					//res.windowHeight:获取整个窗口高度为px，*2为rpx；98为头部占据的高度；
					// #ifndef APP-PLUS || H5 || MP-ALIPAY
					//that.navbarRight = res.windowWidth - uni.getMenuButtonBoundingClientRect().left;
					// #endif
				},
			});

			//获取浏览器中的参数，商品id ，normal普通商品
			if (!options.scene && !options.id) {
				this.showSkeleton = false;
				this.$util.Tips({
					title: '缺少参数无法查看商品'
				}, {
					url: '/pages/index/index'
				});
				return;
			}
			if (options.id) this.id = options.id;
			// 仅仅小程序扫码进入获取商品id，商品类型
			if (options.scene) {
				let qrCodeValue = this.$util.getUrlParams(decodeURIComponent(options.scene));
				let mapeMpQrCodeValue = this.$util.formatMpQrCodeData(qrCodeValue);
				app.globalData.spread = mapeMpQrCodeValue.spread;
				this.id = mapeMpQrCodeValue.id;
				this.type = mapeMpQrCodeValue.type ? mapeMpQrCodeValue.type : 'normal';
			}
			if (options.type === 'video') {
				// #ifdef MP
				this.navH = 160;
				// #endif
				this.type = options.type
			} else {
				this.type = 'normal'
				this.navH = app.globalData.navHeight;
			}
			// 商品类型vuex存储
			this.$store.commit("PRODUCT_TYPE", this.type);
			// 客服配置
			this.$set(this, 'chatConfig', this.$Cache.getItem('chatConfig'));
			// #ifdef H5
			computeUser();
			// #endif

			if (options.spread) this.$Cache.set('spread',options.spread);

			this.getGoodsDetails(); //商品详情
			this.getCouponType(); //获取默认的 优惠券类型
			this.getProductReplyList(); //评论列表
			this.getProductReplyCount(); //评论条数
			this.getGoods(); //优品推荐
			//主题色颜色配置
			this.indicatorBg = setThemeColor();
		},
		onReady() {
			this.isNodes++;
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
				// #ifdef APP-PLUS
				this.homeTop = 60;
				// #endif
			});
		},
		/**
		 * 用户点击右上角分享
		 */
		// #ifdef MP
		onShareAppMessage: function(res) {
			let that = this;
			that.$set(that, 'actionSheetHidden', !that.actionSheetHidden);
			return {
				title: that.productInfo.storeName || '',
				imageUrl: that.productInfo.image || '',
				path: '/pages/goods/goods_details/index?id=' + that.id + '&spread=' + that.uid,
			}
		},
		// #endif
		onReachBottom() {
			this.getCouponList(this.couponType);
		},
		// 滚动监听
		onPageScroll(e) {
			// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
			uni.$emit('scroll');
		},
		methods: {
			//独立客服跳转
			wxChatService(){
				let chatUrlArr = this.chatUrl.split('?')
				uni.navigateTo({
					url:`/pages/users/web_page/index?webUel=${chatUrlArr[0]}&title=客服&${chatUrlArr[1]}`
				})
			},
			//校验token是否有效,true为有效，false为无效
			getTokenIsExist() {
				this.$LoginAuth.getTokenIsExist().then(data => {
					if (data) {
						this.getCouponList(1); //优惠券列表 类型，1-通用，2-商品，3-品类
						this.getCartCount(true); //购物车数量
						//绑定关系
						silenceBindingSpread();
					}
				});
			},
			// #ifdef APP-PLUS
			appShare(scene) {
				let that = this
				let routes = getCurrentPages(); // 获取当前打开过的页面路由数组
				let curRoute = routes[routes.length - 1].$page.fullPath // 获取当前页面路由，也就是最后一个打开的页面路由
				uni.share({
					provider: "weixin",
					scene: scene,
					type: 0,
					href: `${HTTP_H5_URL}${curRoute}&spread=${that.uid}`,
					title: that.productInfo.storeName,
					summary: app.globalData.companyName,
					imageUrl: that.productInfo.image,
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
			onClickService() {
				if (this.chatConfig.telephone_service_switch === 'open') {
					uni.makePhoneCall({
						phoneNumber: this.chatConfig.consumer_hotline //仅为示例
					});
				} else {
					// #ifdef APP-PLUS
					uni.navigateTo({
						url: '/pages/users/web_page/index?webUel=' + this.chatUrl + '&title=客服'
					})
					// #endif
					// #ifndef APP-PLUS
					location.href = this.chatUrl;
					// #endif
				}
			},
			goActivity: function(e) {
				let item = e;
				if (item.type === "1") {
					uni.navigateTo({
						url: `/pages/activity/goods_seckill_details/index?id=${item.id}`
					});
				} else if (item.type === "2") {
					uni.navigateTo({
						url: `/pages/activity/goods_bargain_details/index?id=${item.id}&startBargainUid=${this.uid}`
					});
				} else {
					uni.navigateTo({
						url: `/pages/activity/goods_combination_details/index?id=${item.id}`
					});
				}
			},
			/**
			 * 购物车手动填写
			 * 
			 */
			iptCartNum: function(e) {
				this.$set(this.attr.productSelect, 'cart_num', e ? e : 1);
			},
			// 后退
			returns: function() {
				uni.navigateBack()
			},
			showNav() {
				this.currentPage = !this.currentPage;
			},
			tap: function(index) {
				var id = "past" + index;
				var index = index;
				var that = this;
				this.$set(this, 'toView', id);
				this.$set(this, 'navActive', index);
				this.$set(this, 'lock', true);
				this.$set(this, 'scrollTop', index > 0 ? that.topArr[index] - (app.globalData.navHeight / 2) : that
					.topArr[index]);
			},
			scroll: function(e) {
				var that = this,
					scrollY = e.detail.scrollTop;
				var opacity = scrollY / 500;
				opacity = opacity > 1 ? 1 : opacity;
				that.$set(that, 'opacity', opacity);
				that.$set(that, 'scrollY', scrollY);
				if (that.lock) {
					that.$set(that, 'lock', false)
					return;
				}
				for (var i = 0; i < that.topArr.length; i++) {
					if (scrollY < that.topArr[i] - (app.globalData.navHeight / 2) + that.heightArr[i]) {
						that.$set(that, 'navActive', i)
						break
					}
				}
				that.$set(that.sharePacket, 'touchstart', true); //滑动屏幕时让分享气泡缩回
			},
			/*
			 *去商品详情页 
			 */
			goDetail(item) {
				if (!item.activityH5) {
					uni.redirectTo({
						url: '/pages/goods/goods_details/index?id=' + item.id
					})
					return
				}
				if (item.activityH5.length == 0) {
					uni.redirectTo({
						url: '/pages/goods/goods_details/index?id=' + item.id
					})
					return
				}
				// 砍价
				if (item.activityH5 && item.activityH5.type == 2) {
					uni.redirectTo({
						url: `/pages/activity/goods_bargain_details/index?id=${item.activityH5.id}&bargain=${this.uid}`
					})
					return
				}
				// 拼团
				if (item.activityH5 && item.activityH5.type == 3) {
					uni.redirectTo({
						url: `/pages/activity/goods_combination_details/index?id=${item.activityH5.id}`
					})
					return
				}
				// 秒杀
				if (item.activityH5 && item.activityH5.type == 1) {
					debugger
					uni.redirectTo({
						url: `/pages/activity/goods_seckill_details/index?id=${item.activityH5.id}`
					})
					return
				}
			},
			ChangCouponsClone: function() {
				this.$set(this.coupon, 'coupon', false)
			},
			/**
			 * 购物车数量加和数量减
			 * 
			 */
			ChangeCartNum: function(changeValue) {
				//changeValue:是否 加|减
				//获取当前变动属性
				let productSelect = this.productValue[this.attrValue];
				//如果没有属性,赋值给商品默认库存
				if (productSelect === undefined && !this.attr.productAttr.length)
					productSelect = this.attr.productSelect;
				//无属性值即库存为0；不存在加减；
				if (productSelect === undefined) return;
				let stock = productSelect.stock || 0;
				let num = this.attr.productSelect;
				if (changeValue) {
					num.cart_num++;
					if (num.cart_num > stock) {
						this.$set(this.attr.productSelect, "cart_num", stock);
						this.$set(this, "cart_num", stock);
					}
				} else {
					num.cart_num--;
					if (num.cart_num < 1) {
						this.$set(this.attr.productSelect, "cart_num", 1);
						this.$set(this, "cart_num", 1);
					}
				}
			},
			attrVal(val) {
				this.$set(this.attr.productAttr[val.indexw], 'index', this.attr.productAttr[val.indexw].attrValues[val
					.indexn]);
			},
			/**
			 * 属性变动赋值
			 * 
			 */
			ChangeAttr: function(res) {
				let productSelect = this.productValue[res];
				this.$set(this, "selectSku", productSelect);
				if (productSelect) {
					this.$set(this.attr.productSelect, "image", productSelect.image);
					this.$set(this.attr.productSelect, "price", productSelect.price);
					this.$set(this.attr.productSelect, "stock", productSelect.stock);
					this.$set(this.attr.productSelect, "unique", productSelect.id);
					this.$set(this.attr.productSelect, "cart_num", 1);
					this.$set(this.attr.productSelect, "vipPrice", productSelect.vipPrice);
					this.$set(this.attr.productSelect, 'otPrice', productSelect.otPrice);
					this.$set(this, "attrValue", res);
					this.$set(this, "attrTxt", "已选择");
				} else {
					this.$set(this.attr.productSelect, "image", this.productInfo.image);
					this.$set(this.attr.productSelect, "price", this.productInfo.price);
					this.$set(this.attr.productSelect, "stock", 0);
					this.$set(this.attr.productSelect, "unique", this.productInfo.id);
					this.$set(this.attr.productSelect, "cart_num", 1);
					this.$set(this.attr.productSelect, "vipPrice", this.productInfo.vipPrice);
					this.$set(this.attr.productSelect, 'otPrice', this.productInfo.otPrice);
					this.$set(this, "attrValue", "");
					this.$set(this, "attrTxt", "请选择");
				}
			},
			/**
			 * 领取完毕移除当前页面领取过的优惠券展示
			 */
			ChangCoupons: function(e) {
				let coupon = e;
				let couponList = this.$util.ArrayRemove(this.couponList, 'id', coupon.id);
				this.$set(this, 'couponList', couponList);
				this.getCouponList();
			},

			setClientHeight: function() {
				let that = this;
				if (!that.good_list.length) return;
				let view = uni.createSelectorQuery().in(this).select("#list0");
				view.fields({
					size: true,
				}, data => {
					that.$set(that, 'clientHeight', data.height + 20)
				}).exec();
			},
			/**
			 * 优品推荐
			 * 
			 */
			getGoods() {
				getProductGood().then(res => {
					let good_list = res.data.list || [];
					let count = Math.ceil(good_list.length / 6);
					let goodArray = new Array();
					for (let i = 0; i < count; i++) {
						let list = good_list.slice(i * 6, i * 6 + 6);
						if (list.length) goodArray.push({
							list: list
						});
					}
					this.$set(this, 'good_list', goodArray);
					let navList = ['商品', '评价', '详情'];
					if (goodArray.length) {
						navList.splice(2, 0, '推荐')
					}
					this.$set(this, 'navList', navList);
					this.$nextTick(() => {
						if (good_list.length) {
							// #ifndef APP-PLUS
							this.setClientHeight();
							// #endif
							// #ifdef APP-PLUS
							setTimeout(() => {
								this.setClientHeight();
							}, 1000)
							// #endif
						};
					})
				});
			},
			/**
			 * 获取产品详情
			 * 
			 */
			getGoodsDetails: function() {
				let that = this;
				getProductDetail(that.id, that.type).then(res => {
					let productInfo = res.data.productInfo;
					// 字符串数组转数组；
					let arrayImg = productInfo.sliderImage;
					let sliderImage = JSON.parse(arrayImg);
					if (that.getFileType(sliderImage[0]) == 'video') {
						//判断轮播图第一张是否是视频，如果是，就赋值给videoLink，并且将其在轮播图中删除
						this.$set(this, 'videoLink', sliderImage[0]);
						sliderImage.splice(0, 1);
					}
					that.$set(that, 'sliderImage', sliderImage);
					console.log(this.sliderImage);
					that.$set(that, 'productInfo', productInfo);
					that.$set(that, 'description', productInfo.content);
					that.$set(that, 'userCollect', res.data.userCollect);
					that.$set(that.attr, 'productAttr', res.data.productAttr);
					that.$set(that, 'productValue', res.data.productValue);
					for (let key in res.data.productValue) {
						let obj = res.data.productValue[key];
						that.skuArr.push(obj)
					}
					this.$set(this, "selectSku", that.skuArr[0]);
					that.$set(that.sharePacket, 'priceName', res.data.priceName);
					//that.$set(that.sharePacket, 'isState', Math.floor(res.data.priceName) != 0 ?false : true);
					that.$set(that.sharePacket, 'isState', (res.data.priceName != "0" && res.data.priceName !==
						null) ? false : true);
					that.$set(that, 'activityH5', res.data.activityAllH5 ? res.data.activityAllH5 : []);
					uni.setNavigationBarTitle({
						title: productInfo.storeName.substring(0, 7) + "..."
					})

					let productAttr = this.attr.productAttr.map(item => {
						return {
							attrName: item.attrName,
							attrValues: item.attrValues.split(','),
							id: item.id,
							isDel: item.isDel,
							productId: item.productId,
							type: item.type
						}
					});
					this.$set(this.attr, 'productAttr', productAttr);

					// var navList = ['商品', '评价', '详情'];
					// if (goodArray.length) {
					// 	navList.splice(2, 0, '推荐')
					// }
					//that.$set(that, 'navList', navList);
					
					//无需登录即可分享
					that.getCartCount();
					//#ifdef H5
					that.make(that.uid);
					that.ShareInfo();
					this.getImageBase64(this.productInfo.image);
					// #endif
					// #ifdef MP
					that.getQrcode();
					// #endif
					setTimeout(function() {
						that.infoScroll();
					}, 500);
					// #ifdef MP
					that.imgTop = res.data.productInfo.image
					// #endif
					// #ifndef H5
					that.downloadFilestoreImage();
					// #endif
					that.DefaultSelect();
					this.showSkeleton = false
					setTimeout(() => {
						this.defaultCoupon = this.coupon.list;
					}, 1000)
				}).catch(err => {
					//状态异常返回上级页面
					that.$util.Tips({
						title: err.toString()
					}, {
						tab: 3,
						url: 1
					});
					this.showSkeleton = false
				})
			},
			//评论列表
			getProductReplyList: function() {
				getReplyProduct(this.id).then(res => {
					this.reply = res.data.productReply ? [res.data.productReply] : [];
				})
			},
			//评论条数
			getProductReplyCount: function() {
				let that = this;
				getReplyConfig(that.id).then(res => {
					that.$set(that, 'replyChance', res.data.replyChance * 100);
					that.$set(that, 'replyCount', res.data.sumCount);
				});
			},
			infoScroll: function() {
				var that = this,
					topArr = [],
					heightArr = [];
				for (var i = 0; i < that.navList.length; i++) { //productList
					//获取元素所在位置
					var query = uni.createSelectorQuery().in(this);
					var idView = "#past" + i;
					// if (!that.data.good_list.length && i == 2) {
					//   var idView = "#past" + 3;
					// }
					query.select(idView).boundingClientRect();
					query.exec(function(res) {
						var top = res[0].top;
						var height = res[0].height;
						topArr.push(top);
						heightArr.push(height);
						that.$set(that, 'topArr', topArr);
						that.$set(that, 'heightArr', heightArr);
					});
				};
			},
			/**
			 * 默认选中属性
			 * 
			 */
			DefaultSelect: function() {
				let productAttr = this.attr.productAttr;
				let value = [];
				//默认选中每种规格的第一个
				for (let key in this.productValue) {
					if (this.productValue[key].stock > 0) {
						value = this.attr.productAttr.length ? key.split(",") : [];
						break;
					}
				}
				for (let i = 0; i < value.length; i++) {
					this.$set(productAttr[i], "index", value[i]);
				}

				//sort();排序函数:数字-英文-汉字；
				let productSelect = this.productValue[value.join(",")];
				if (productSelect && productAttr.length) {
					this.$set(this.attr.productSelect, "storeName", this.productInfo.storeName);
					this.$set(this.attr.productSelect, "image", productSelect.image);
					this.$set(this.attr.productSelect, "price", productSelect.price);
					this.$set(this.attr.productSelect, "stock", productSelect.stock);
					this.$set(this.attr.productSelect, "unique", productSelect.id);
					this.$set(this.attr.productSelect, "cart_num", 1);
					this.$set(this.attr.productSelect, "vipPrice", productSelect
						.vipPrice); //attr.productSelect.otPrice
					this.$set(this.attr.productSelect, 'otPrice', productSelect.otPrice);
					this.$set(this, "attrValue", value.join(","));
					this.$set(this, "attrTxt", "已选择");
				} else if (!productSelect && productAttr.length) {
					this.$set(this.attr.productSelect, "storeName", this.productInfo.storeName);
					this.$set(this.attr.productSelect, "image", this.productInfo.image);
					this.$set(this.attr.productSelect, "price", this.productInfo.price);
					this.$set(this.attr.productSelect, "stock", 0);
					this.$set(this.attr.productSelect, "unique", this.productInfo.id);
					this.$set(this.attr.productSelect, "cart_num", 1);
					this.$set(this.attr.productSelect, "vipPrice", this.productInfo.vipPrice);
					this.$set(this.attr.productSelect, 'otPrice', this.productInfo.otPrice);
					this.$set(this, "attrValue", "");
					this.$set(this, "attrTxt", "请选择");
				} else if (!productSelect && !productAttr.length) {
					this.$set(this.attr.productSelect, "storeName", this.productInfo.storeName);
					this.$set(this.attr.productSelect, "image", this.productInfo.image);
					this.$set(this.attr.productSelect, "price", this.productInfo.price);
					this.$set(this.attr.productSelect, "stock", this.productInfo.stock);
					this.$set(this.attr.productSelect, "unique", this.productInfo.id || "");
					this.$set(this.attr.productSelect, "cart_num", 1);
					this.$set(this.attr.productSelect, "vipPrice", this.productInfo.vipPrice);
					this.$set(this.attr.productSelect, 'otPrice', this.productInfo.otPrice);
					this.$set(this, "attrValue", "");
					this.$set(this, "attrTxt", "请选择");
				}
			},
			/**
			 * 获取优惠券
			 * 
			 */
			getCouponList(type) {
				let that = this;
				if (type != undefined || type != null) {
					that.where.type = type;
				} else {
					that.where.type = "";
				}
				that.where.productId = that.id;
				if (that.loadend) return;
				if (that.loading) return;
				// if (isPage === true) that.$set(that, 'productList', []);
				that.loading = true;
				that.loadTitle = '';
				getCoupons(that.where).then(res => {
					let list = res.data.list;
					let couponList = that.$util.SplitArray(list, that.coupon.list);
					let loadend = list.length < that.where.limit;
					that.loadend = loadend;
					that.loading = false;
					that.loadTitle = loadend ? '已全部加载' : '加载更多';
					that.$set(that.coupon, 'list', couponList);
					that.$set(that.where, 'page', that.where.page + 1);
				});
			},
			async getCouponType() {
				//在onLoad只调用一次，获取默认的类型作为打开优惠券列表的参数，不会随着切换变化
				let dataList = await getCoupons({
					productId: this.id
				});
				if (dataList.length) {
					this.couponDeaultType = dataList.data;
					this.$set(this.coupon, 'type', dataList);
				}

			},
			//切换优惠券头部
			tabCouponType(type) {
				this.loadend = false;
				this.loading = false;
				this.where.page = 1;
				this.where.limit = 999;
				this.$set(this.coupon, 'list', []);
				this.$set(this.coupon, 'type', type);
				this.couponType = type; //优惠券类型
				this.getCouponList(type);
			},

			ChangCouponsUseState(index) {
				let that = this;
				that.coupon.list[index].isUse = true;
				that.$set(that.coupon, 'list', that.coupon.list);
				that.$set(that.coupon, 'coupon', false);
			},
			/** 
			 * 
			 * 
			 * 收藏商品
			 */
			setCollect: function() {
				let that = this;
				if (this.isLogin === false) {
					toLogin();
				} else {
					if (this.userCollect) {
						collectDel(this.productInfo.id).then(res => {
							that.$set(that, 'userCollect', !that.userCollect);
						})
					} else {
						collectAdd(this.productInfo.id).then(res => {
							that.$set(that, 'userCollect', !that.userCollect);
						})
					}
				}
			},
			/**
			 * 打开属性插件
			 */
			selecAttr: function() {
				this.$set(this.attr, 'cartAttr', true);
				this.$set(this, 'isOpen', true);
			},
			/**
			 * 打开优惠券插件
			 */
			couponTap: function() {
				let that = this;
				if (that.isLogin === false) {
					toLogin();
				} else {
					// this.loadend = false;
					// this.loading = false;
					// this.$set(that.coupon, 'list', []);
					// //that.getCouponList(this.couponDeaultType[0].useType); //打开弹框默认请求商品券
					that.$set(that.coupon, 'coupon', true);
				}
			},
			onMyEvent: function() {
				this.$set(this.attr, 'cartAttr', false);
				this.$set(this, 'isOpen', false);
			},
			/**
			 * 打开属性加入购物车
			 * 
			 */
			joinCart: function(e) {
				//是否登录
				if (this.isLogin === false) {
					toLogin();
				} else {
					this.goCat(1);
				}
			},
			/*
			 * 加入购物车
			 */
			goCat: function(num) {
				let that = this,
					productSelect = that.productValue[this.attrValue];
				//打开属性
				if (that.attrValue) {
					//默认选中了属性，但是没有打开过属性弹窗还是自动打开让用户查看默认选中的属性
					that.attr.cartAttr = !that.isOpen ? true : false;
				} else {
					if (that.isOpen) that.attr.cartAttr = true;
					else that.attr.cartAttr = !that.attr.cartAttr;
				}
				//只有关闭属性弹窗时进行加入购物车
				if (that.attr.cartAttr === true && that.isOpen === false)
					return (that.isOpen = true);
				//如果有属性,没有选择,提示用户选择
				if (
					that.attr.productAttr.length &&
					productSelect.stock === 0 &&
					that.isOpen === true
				)
					return that.$util.Tips({
						title: "产品库存不足，请选择其它"
					});
				if (num === 1) {
					let q = {
						productId: parseFloat(that.id),
						cartNum: parseFloat(that.attr.productSelect.cart_num),
						isNew: false,
						productAttrUnique: that.attr.productSelect !== undefined ?
							that.attr.productSelect.unique : that.productInfo.id
					};
					postCartAdd(q).then(function(res) {
							that.isOpen = false;
							that.attr.cartAttr = false;
							that.$util.Tips({
								title: "添加购物车成功",
								success: () => {
									that.getCartCount(true);
								}
							});
						})
						.catch(res => {
							that.isOpen = false;
							return that.$util.Tips({
								title: res
							});
						});
				} else {
					this.getPreOrder();
				}
			},
			/**
			 * 获取购物车数量
			 * @param boolean 是否展示购物车动画和重置属性
			 */
			getCartCount: function(isAnima) {
				let that = this;
				const isLogin = that.isLogin;
				if (isLogin) {
					getCartCounts(true, 'total').then(res => {
						that.CartCount = res.data.count;
						//加入购物车后重置属性
						if (isAnima) {
							that.animated = true;
							setTimeout(function() {
								that.animated = false;
							}, 500);
						}
					});
				}
			},
			/**
			 * 立即购买
			 */
			goBuy: Debounce(function(e) {
				if (this.isLogin === false) {
					toLogin();
				} else {
					this.goCat(0);
				}
			}),
			/**
			 * 预下单
			 */
			getPreOrder: function() {
				this.$Order.getPreOrder(this.type === 'normal' ? 'buyNow' : 'video', [{
					"attrValueId": parseFloat(this.attr.productSelect.unique),
					"productId": parseFloat(this.id),
					"productNum": parseFloat(this.attr.productSelect.cart_num)
				}]);
				this.isOpen = false;
			},
			// 授权关闭
			authColse: function(e) {
				this.isShowAuth = e
			},
			/**
			 * 分享打开
			 * 
			 */
			listenerActionSheet: function() {
				// if (this.isLogin === false) {
				// 	toLogin();
				// } else {
				// 	// #ifdef H5
				// 	if (this.$wechat.isWeixin() === true) {
				// 		this.weixinStatus = true;
				// 	}
				// 	// #endif
				// 	this.goPoster()
				// 	this.posters = true;
				// }
				// #ifdef H5
				if (this.$wechat.isWeixin() === true) {
					this.weixinStatus = true;
				}
				// #endif
				this.goPoster()
				this.posters = true;
			},
			closePosters: function() {
				this.posters = false;
				this.currentPage = false;
			},
			//隐藏海报
			posterImageClose: function() {
				this.canvasStatus = false
				this.posters = false;
			},
			//替换安全域名
			setDomain: function(url) {
				url = url ? url.toString() : '';
				//本地调试打开,生产请注销
				if (url.indexOf("https://") > -1) return url;
				else return url.replace('http://', 'https://');
			},
			//获取海报产品图（解决跨域问题，只适用于小程序）
			downloadFilestoreImage: function() {
				let that = this;
				uni.downloadFile({
					url: that.setDomain(that.productInfo.image),
					success: function(res) {
						that.storeImage = res.tempFilePath;
					},
					fail: function() {
						return that.$util.Tips({
							title: ''
						});
						that.storeImage = '';
					},
				});
			},
			// 小程序关闭分享弹窗；
			goFriend: function() {
				this.posters = false;
			},
			// 小程序二维码
			getQrcode() {
				let that = this;
				let data = {
					pid: that.uid,
					id: that.id,
					path: 'pages/goods/goods_details/index'
				}
				getQrcode(data).then(res => {
					base64src(res.data.code, Date.now(), res => {
						that.PromotionCode = res;
					});

				}).catch(err => {
					that.errT = err;
				});
			},
			// 生成二维码；
			make(uid) {
				let href = location.href.split('?')[0] + "?id=" + this.id + "&spread=" + this.uid;
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
			getImageBase64: function(images) {
				let that = this;
				imageBase64({
					url: images
				}).then(res => {
					that.imgTop = res.data.code;
				})
			},
			/**
			 * 生成海报
			 */
			goPoster: function() {
				let that = this;
				uni.showLoading({
					title: '海报生成中',
					mask: true
				});
				that.posters = false;
				let arrImagesUrl = '';
				let arrImagesUrlTop = '';
				if (!that.PromotionCode) {
					uni.hideLoading();
					that.$util.Tips({
						title: that.errT
					});
					return
				}
				setTimeout(() => {
					if (!that.imgTop) {
						uni.hideLoading();
						that.$util.Tips({
							title: '无法生成商品海报！'
						});
						return
					}
				}, 1000);
				uni.downloadFile({
					url: that.imgTop, //仅为示例，并非真实的资源
					success: (res) => {
						arrImagesUrlTop = res.tempFilePath;
						let arrImages = [that.posterbackgd, arrImagesUrlTop, that.PromotionCode];
						let storeName = that.productInfo.storeName;
						let price = that.productInfo.price;
						setTimeout(() => {
							that.$util.PosterCanvas(arrImages, storeName, price, that.productInfo
								.otPrice,
								function(tempFilePath) {
									that.imagePath = tempFilePath;
									that.canvasStatus = true;
									uni.hideLoading();
								});
						}, 500);
					}
				});
			},
			// 图片预览；
			getpreviewImage: function() {
				if (this.imagePath) {
					let photoList = [];
					photoList.push(this.imagePath)
					uni.previewImage({
						urls: photoList,
						current: this.imagePath
					});
				} else {
					this.$util.Tips({
						title: '您的海报尚未生成'
					});
				}
			},
			/*
			 * 保存到手机相册
			 */
			// #ifdef MP
			savePosterPath: function() {
				let that = this;
				uni.getSetting({
					success(res) {
						if (!res.authSetting['scope.writePhotosAlbum']) {
							uni.authorize({
								scope: 'scope.writePhotosAlbum',
								success() {
									uni.saveImageToPhotosAlbum({
										filePath: that.imagePath,
										success: function(res) {
											that.posterImageClose();
											that.$util.Tips({
												title: '保存成功',
												icon: 'success'
											});
										},
										fail: function(res) {
											that.$util.Tips({
												title: '保存失败'
											});
										}
									})
								}
							})
						} else {
							uni.saveImageToPhotosAlbum({
								filePath: that.imagePath,
								success: function(res) {
									that.posterImageClose();
									that.$util.Tips({
										title: '保存成功',
										icon: 'success'
									});
								},
								fail: function(res) {
									that.$util.Tips({
										title: '保存失败'
									});
								},
							})
						}
					}
				})
			},
			// #endif
			ShareInfo() {
				let data = this.productInfo;
				let href = location.href;
				if (this.$wechat.isWeixin()) {
					href = href.indexOf("?") === -1 ? href + "?spread=" + this.uid : href + "&spread=" + this.uid;
					let configAppMessage = {
						desc: app.globalData.companyName,
						title: data.storeName,
						link: href,
						imgUrl: data.image
					};
					this.$wechat.wechatEvevt([
						"updateAppMessageShareData",
						"updateTimelineShareData",
						"onMenuShareAppMessage",
						"onMenuShareTimeline"
					], configAppMessage).then(res => {
						// console.log(res);
					}).catch(err => {
						console.log(err);
					})
				}
			},
			showShare(status) {
				let that = this;
				that.$set(that.sharePacket, 'touchstart', status);
			},
			hideNav() {
				this.currentPage = false;
			},
			//下拉导航页面跳转
			linkPage(url) {
				if (url == '/pages/index/index' || url == '/pages/order_addcart/order_addcart' || url ==
					'/pages/user/index') {
					uni.switchTab({
						url
					})
				} else {
					uni.navigateTo({
						url
					})
				}
				this.currentPage = false
			},
			//点击sku图片打开轮播图
			showImg(index) {
				this.$refs.cusPreviewImg.open(this.selectSku.suk)
			},
			//滑动轮播图选择商品
			changeSwitch(e) {
				let productSelect = this.skuArr[e];
				this.$set(this, 'selectSku', productSelect);
				var skuList = productSelect.suk.split(',');
				skuList.forEach((i, index) => {
					this.$set(this.attr.productAttr[index], 'index', skuList[index]);
				})
				if (productSelect) {
					this.$set(this.attr.productSelect, "image", productSelect.image);
					this.$set(this.attr.productSelect, "price", productSelect.price);
					this.$set(this.attr.productSelect, "stock", productSelect.stock);
					this.$set(this.attr.productSelect, "unique", productSelect.id);
					this.$set(this.attr.productSelect, "vipPrice", productSelect.vipPrice);
					this.$set(this, "attrTxt", "已选择");
					this.$set(this, "attrValue", productSelect.suk)
				}
			},
			getFileType(fileName) {
				// 后缀获取
				let suffix = '';
				// 获取类型结果
				let result = '';
				try {
					const flieArr = fileName.split('.');
					suffix = flieArr[flieArr.length - 1];
				} catch (err) {
					suffix = '';
				}
				// fileName无后缀返回 false
				if (!suffix) {
					return false;
				}
				suffix = suffix.toLocaleLowerCase();
				// 图片格式
				const imglist = ['png', 'jpg', 'jpeg', 'bmp', 'gif'];
				// 进行图片匹配
				result = imglist.find(item => item === suffix);
				if (result) {
					return 'image';
				}
				// 匹配 视频
				const videolist = ['mp4', 'm2v', 'mkv', 'rmvb', 'wmv', 'avi', 'flv', 'mov', 'm4v'];
				result = videolist.find(item => item === suffix);
				if (result) {
					return 'video';
				}
				// 其他 文件类型
				return 'other';
			},
			videoPause() {

			}
		},
	}
</script>

<style scoped lang="scss">
	.lang{
		width: 170rpx !important;
		height: 60rpx !important;
		border-radius: 33rpx;
	}
	.circle{
		width: 58rpx !important;
		height: 58rpx !important;
		border-radius: 50%;
	}
	.product-con {
		height: 100%;
	}

	.x-money {
		font-size: 28rpx;
		font-weight: 700;
		@include price_color(theme);

	}

	.bg-color-hui {
		background: #bbb !important;
		border-radius: 0 25px 25px 0;
	}

	.select_nav {
		width: 170rpx !important;
		height: 60rpx !important;
		border-radius: 33rpx;
		background: rgba(255, 255, 255, 0.3);
		border: 1px solid rgba(0,0,0,0.07);
		color: #000;
		position: fixed;
		font-size: 18px;
		line-height: 58rpx;
		z-index: 1000;
		left: 14rpx;
	}

	.px-20 {
		padding: 0 20rpx 0;
	}

	.nav_line {
		content: '';
		display: inline-block;
		width: 1px;
		height: 34rpx;
		background: #b3b3b3;
		position: absolute;
		left: 0;
		right: 0;
		margin: auto;
	}

	.bgwhite {
		background: #fff;
	}

	.input {
		display: flex;
		align-items: center;
		/* #ifdef MP */
		width: 300rpx;
		/* #endif */
		/* #ifndef MP */
		width: 460rpx;
		/* #endif */
		height: 58rpx;
		padding: 0 0 0 30rpx;
		border: 1px solid rgba(0, 0, 0, 0.07);
		border-radius: 33rpx;
		color: #666;
		font-size: 26rpx;
		position: fixed;
		left: 0;
		right: 0;
		margin: auto;
		background: rgba(255, 255, 255, 0.3);

		.iconfont {
			margin-right: 20rpx;
			font-size: 26rpx;
			color: #666666;
		}
	}

	.container_detail {
		/* #ifdef MP */
		margin-top: 32rpx;
		/* #endif */
	}

	.tab_nav {
		width: 100%;
		height: 48px;
		padding: 0 30rpx 0;
	}

	.right_select {
		width: 58rpx;
		height: 58rpx;
		background: rgba(255, 255, 255, 0.3);
		border: 1px solid rgba(0, 0, 0, 0.1);
		border-radius: 50%;
		position: fixed;
		right: 20rpx;
		text-align: center;
		line-height: 58rpx;
	}

	.dialog_nav {
		position: absolute;
		/* #ifdef MP */
		left: 14rpx;
		/* #endif */
		/* #ifdef H5 || APP-PLUS*/
		right: 14rpx;
		/* #endif */
		width: 240rpx;
		background: #FFFFFF;
		box-shadow: 0px 0px 16rpx rgba(0, 0, 0, 0.08);
		z-index: 310;
		border-radius: 14rpx;

		&::before {
			content: '';
			width: 0;
			height: 0;
			position: absolute;
			/* #ifdef MP */
			left: 0;
			right: 0;
			margin: auto;
			/* #endif */
			/* #ifdef H5 || APP-PLUS */
			right: 8px;
			/* #endif */
			top: -9px;
			border-bottom: 10px solid #F5F5F5;
			border-left: 10px solid transparent;
			/*transparent 表示透明*/
			border-right: 10px solid transparent;
		}
	}

	.dialog_nav_item {
		width: 100%;
		height: 84rpx;
		line-height: 84rpx;
		padding: 0 20rpx 0;
		box-sizing: border-box;
		border-bottom: #eee;
		font-size: 28rpx;
		color: #333;
		position: relative;

		.iconfont {
			font-size: 32rpx;
		}
	}

	.dialog_after {
		::after {
			content: '';
			position: absolute;
			width: 172rpx;
			height: 1px;
			background-color: #EEEEEE;
			bottom: 0;
			right: 0;
		}
	}

	.pl-20 {
		padding-left: 20rpx;
	}

	.activity {
		padding: 0 20rpx;
		@include coupons_border_color(theme);
		@include main_color(theme);
		font-size: 24rpx;
		line-height: 34rpx;
		position: relative;
		margin-left: 4rpx;
	}

	.product-con .wrapper .coupon .activity:before {
		content: ' ';
		position: absolute;
		width: 7rpx;
		height: 10rpx;
		border-radius: 0 7rpx 7rpx 0;
		@include coupons_border_color(theme);
		background-color: #fff !important;
		bottom: 50%;
		left: -3rpx;
		margin-bottom: -6rpx;
		// border-left-color: #fff ;
		@include white_left_border;
	}

	.product-con .wrapper .coupon .activity:after {
		content: ' ';
		position: absolute;
		width: 7rpx;
		height: 10rpx;
		border-radius: 7rpx 0 0 7rpx;
		@include coupons_border_color(theme);
		background-color: #fff;
		right: -3rpx;
		bottom: 50%;
		margin-bottom: -6rpx;
		// border-right-color: #fff;
		@include white_right_border;
	}

	.justify-center {
		justify-content: center;
	}

	.align-center {
		align-items: center;
	}

	.align-baseline {
		align-items: baseline;
	}

	.bg_color {
		@include main_bg_color(theme);
	}

	.vip_icon {
		width: 44rpx;
		height: 28rpx;
	}

	.pl-2 {
		padding-left: 20rpx;
	}

	.vip_money {
		background: #FFE7B9;
		border-radius: 4px;
		font-size: 22rpx;
		color: #333;
		line-height: 28rpx;
		text-align: center;
		padding: 0 6rpx;
		box-sizing: border-box;
		margin-left: -4rpx;
	}

	.theme_price {
		@include price_color(theme);
	}

	.activityName {
		line-height: 44rpx;
	}

	.userEvaluation {
		i {
			display: inline-block;
		}
	}

	.bntVideo {
		width: auto !important;

		.buy {
			border-radius: 50rpx !important;
		}
	}

	.attribute {
		.line1 {
			width: 600rpx;
		}
	}

	.chat-btn {
		background-color: antiquewhite !important;
	}

	.activity_pin {
		width: auto;
		height: 44rpx;
		line-height: 44rpx;
		// background: linear-gradient(90deg, rgba(233, 51, 35, 1) 0%, rgba(250, 101, 20, 1) 100%);
		@include linear-gradient(theme);
		opacity: 1;
		border-radius: 22rpx;
		padding: 0 15rpx;
		// margin-left: 19rpx;
	}

	.activity_miao {
		width: auto;
		height: 44rpx;
		line-height: 44rpx;
		padding: 0 15rpx;
		// background: linear-gradient(90deg, rgba(250, 102, 24, 1) 0%, rgba(254, 161, 15, 1) 100%);
		@include linear-gradient(theme);
		opacity: 1;
		border-radius: 22rpx;
		margin-left: 19rpx;
	}

	.iconfonts {
		color: #fff !important;
		font-size: 28rpx;
	}

	.activity_title {
		font-size: 24rpx;
		color: #fff;
	}

	.activity_kan {
		width: auto;
		height: 44rpx;
		line-height: 44rpx;
		padding: 0 15rpx;
		@include linear-gradient(theme);
		opacity: 1;
		border-radius: 22rpx;
		margin-left: 19rpx;
	}

	.mask {
		z-index: 300 !important;
	}

	.head-bar {
		background: #fff;
	}

	.generate-posters {
		width: 100%;
		height: 318rpx;
		background-color: #fff;
		position: fixed;
		left: 0;
		bottom: 0;
		z-index: 388;
		transform: translate3d(0, 100%, 0);
		transition: all 0.3s cubic-bezier(0.25, 0.5, 0.5, 0.9);
		border-top: 1rpx solid #eee;

		.generateCon {
			height: 220rpx;
		}

		.generateClose {
			height: 98rpx;
			font-size: 28rpx;
			color: #333333;
			border-top: 1px solid #eee;
		}

		.item {
			.pictrue {
				width: 96rpx;
				height: 96rpx;
				border-radius: 50%;
				margin: 0 auto 6rpx auto;

				image {
					width: 100%;
					height: 100%;
					border-radius: 50%;
				}
			}
		}
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

	.generate-posters .item .iconfont.icon-haowuquan1 {
		color: #ff954d;
	}

	.product-con .footer {
		padding: 0 20rpx 0 30rpx;
		position: fixed;
		bottom: 0;
		width: 100%;
		box-sizing: border-box;
		background-color: #fff;
		z-index: 277;
		border-top: 1rpx solid #f0f0f0;
		height: 100rpx;
		height: calc(100rpx+ constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(100rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
	}

	.product-con .footer .item {
		font-size: 18rpx;
		color: #666;
	}

	.product-con .footer .item .iconfont {
		text-align: center;
		font-size: 40rpx;
	}

	.product-con .footer .item .iconfont.icon-shoucang1 {
		@include main_color(theme);
	}

	.product-con .footer .item .iconfont.icon-gouwuche1 {
		font-size: 40rpx;
		position: relative;
	}

	.product-con .footer .item .iconfont.icon-gouwuche1 .num {
		color: #fff;
		position: absolute;
		font-size: 18rpx;
		padding: 2rpx 8rpx 3rpx;
		border-radius: 200rpx;
		top: -10rpx;
		right: -10rpx;
	}

	.product-con .footer .bnt {
		width: 444rpx;
		height: 76rpx;
	}

	.product-con .footer .bnt .bnts {
		width: 222rpx;
		text-align: center;
		line-height: 76rpx;
		color: #fff;
		font-size: 28rpx;
	}

	.product-con .footer .bnt .joinCart {
		border-radius: 50rpx 0 0 50rpx;
		@include left_color(theme);
	}

	.product-con .footer .bnt .buy {
		border-radius: 0 50rpx 50rpx 0;
		@include main_bg_color(theme);
	}

	.product-con .store-info {
		margin-top: 20rpx;
		background-color: #fff;
	}

	.product-con .store-info .title {
		padding: 0 30rpx;
		font-size: 28rpx;
		color: #282828;
		height: 80rpx;
		line-height: 80rpx;
		border-bottom: 1px solid #f5f5f5;
	}

	.product-con .store-info .info {
		padding: 0 30rpx;
		height: 126rpx;
	}

	.product-con .store-info .info .picTxt {
		width: 615rpx;
	}

	.product-con .store-info .info .picTxt .pictrue {
		width: 76rpx;
		height: 76rpx;
	}

	.product-con .store-info .info .picTxt .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 6rpx;
	}

	.product-con .store-info .info .picTxt .text {
		width: 522rpx;
	}

	.product-con .store-info .info .picTxt .text .name {
		font-size: 30rpx;
		color: #282828;
	}

	.product-con .store-info .info .picTxt .text .address {
		font-size: 24rpx;
		color: #666;
		margin-top: 3rpx;
	}

	.product-con .store-info .info .picTxt .text .address .iconfont {
		color: #707070;
		font-size: 18rpx;
		margin-left: 10rpx;
	}

	.product-con .store-info .info .picTxt .text .address .addressTxt {
		max-width: 480rpx;
	}

	.product-con .store-info .info .iconfont {
		font-size: 40rpx;
	}

	.product-con .superior {
		background-color: #fff;
		margin-top: 30rpx;
		padding: 0 24rpx 30rpx 24rpx;
	}

	.product-con .superior .title {
		height: 98rpx;
	}

	.product-con .superior .title image {
		width: 20rpx;
		height: 20rpx;
	}

	.product-con .superior .title .titleTxt {
		margin: 0 10rpx;
		font-size: 30rpx;
		color: #333333;
	}

	.product-con .superior .slider-banner {
		width: 100%;
		margin: 0 auto;
		position: relative;
	}

	.product-con .superior .slider-banner swiper {
		height: 100%;
		width: 100%;
	}

	.product-con .superior .slider-banner swiper-item {
		height: 100%;
	}

	.product-con .superior .slider-banner .list {
		width: 100%;
	}

	.product-con .superior .slider-banner .list .item {
		width: 198rpx;
		margin: 0 22rpx 30rpx 0;
		font-size: 26rpx;
	}

	.product-con .superior .slider-banner .list .item:nth-of-type(3n) {
		margin-right: 0;
	}

	.product-con .superior .slider-banner .list .item .pictrue {
		position: relative;
		width: 100%;
		height: 198rpx;
	}

	.product-con .superior .slider-banner .list .item .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 6rpx;
	}

	.product-con .superior .slider-banner .list .item .name {
		color: #282828;
		margin-top: 12rpx;
	}

	.product-con .superior .slider-banner .swiper-pagination-bullet {
		background-color: #999;
	}

	.product-con .superior .slider-banner .swiper-pagination-bullet-active {
		background-color: $theme-color;
	}

	button {
		padding: 0;
		margin: 0;
		line-height: normal;
		background-color: #fff;
	}

	button::after {
		border: 0;
	}

	action-sheet-item {
		padding: 0;
		height: 240rpx;
		align-items: center;
		display: flex;
	}

	.contact {
		font-size: 16px;
		width: 50%;
		background-color: #fff;
		padding: 8rpx 0;
		border-radius: 0;
		margin: 0;
		line-height: 2;
	}

	.contact::after {
		border: none;
	}

	.action-sheet {
		font-size: 17px;
		line-height: 1.8;
		width: 50%;
		position: absolute;
		top: 0;
		right: 0;
		padding: 25rpx 0;
	}

	.canvas {
		position: fixed;
		z-index: -5;
		opacity: 0;
	}

	.poster-pop {
		position: fixed;
		width: 450rpx;
		height: 714rpx;
		top: 50%;
		left: 50%;
		transform: translateX(-50%);
		margin-top: -432rpx;
		z-index: 399;
	}

	.poster-pop image {
		width: 100%;
		height: 100%;
		display: block;
	}

	.poster-pop .close {
		width: 46rpx;
		height: 75rpx;
		position: fixed;
		right: 0;
		top: -73rpx;
		display: block;
	}

	.poster-pop .save-poster {
		background-color: #df2d0a;
		font-size: ：22rpx;
		color: #fff;
		text-align: center;
		height: 76rpx;
		line-height: 76rpx;
		width: 100%;
	}

	.poster-pop .keep {
		color: #fff;
		text-align: center;
		font-size: 25rpx;
		margin-top: 10rpx;
	}

	.mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.6);
	}

	.pro-wrapper .iconn {
		background-image: url('data:image/png;base64,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');
		width: 100rpx;
		height: 100rpx;
		background-repeat: no-repeat;
		background-size: 100% 100%;
		margin: 0 auto;
	}

	.pro-wrapper .iconn.iconn1 {
		background-image: url('data:image/png;base64,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');
	}

	.pictrue_log {
		width: 80upx;
		height: 40upx;
		border-radius: 10upx 0 12upx 0;
		line-height: 40upx;
		font-size: 24upx;
	}

	.pictrue_log_class {
		z-index: 3;
		background: -webkit-gradient(linear, left top, right top, from(rgba(246, 122, 56, 1)), to(rgba(241, 27, 9, 1)));
		background: linear-gradient(90deg, rgba(246, 122, 56, 1) 0%, rgba(241, 27, 9, 1) 100%);
		opacity: 1;
		position: absolute;
		top: 0;
		left: 0;
		color: #fff;
		text-align: center;

	}

	.tab_nav .header {
		width: 100%;
		height: 96rpx;
		padding: 20rpx 80rpx 0;
		font-size: 30rpx;
		color: #050505;
		background-color: #fff;
	}

	.icon-xiangzuo {
		/* #ifdef H5 */
		top: 20rpx !important;
		/* #endif */
	}

	.navbar .header .item {
		position: relative;
		margin: 0 25rpx;
	}

	.navbar .header .item.on:before {
		position: absolute;
		width: 60rpx;
		height: 5rpx;
		background-repeat: no-repeat;
		content: "";
		@include linear-gradient(theme);
		bottom: -10rpx;
		left: 50%;
		margin-left: -28rpx;
	}

	.navbar {
		position: fixed;
		// background-color: #fff;
		top: 0;
		left: 0;
		z-index: 99;
		width: 100%;
	}

	.navbar .navbarH {
		position: relative;
	}

	.navbar .navbarH .navbarCon {
		position: absolute;
		bottom: 0;
		height: 100rpx;
		width: 100%;
	}

	.h5_back {
		color: #000;
		position: fixed;
		left: 20rpx;
		font-size: 32rpx;
		text-align: center;
		width: 58rpx;
		height: 58rpx;
		background: rgba(255, 255, 255, 0.3);
		border: 1px solid rgba(0, 0, 0, 0.1);
		border-radius: 50%;
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

	.mask_transparent {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: transparent;
		z-index: 300;
	}

	.px-12 {
		padding-left: 12rpx;
		padding-right: 12rpx;
	}

	.font-44 {
		font-size: 44rpx;
	}

	.font_color {
		@include main_color(theme);
	}

	.attrImg {
		width: 66rpx;
		height: 66rpx;
		border-radius: 6rpx;
		display: block;
		margin-right: 14rpx;
	}

	.switchTxt {
		height: 60rpx;
		flex: 1;
		line-height: 60rpx;
		box-sizing: border-box;
		background: #EEEEEE;
		padding-right: 0 24rpx 0;
		border-radius: 8rpx;
		text-align: center;
	}
	.share-icon-box{
		position: relative;
		.share-icon{
			position: absolute;
			right: -15rpx;
			top: 20rpx;
		}
	}
	.share-introduce{
		padding-right: 16rpx;
	}
</style>