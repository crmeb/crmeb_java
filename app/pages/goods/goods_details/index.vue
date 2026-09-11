<template>
	<view :data-theme="theme" :style="colorStyle">
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
							:class="opacity>0.5?'on':''" :style="{ top: homeTop + 'rpx' }" v-if="returnShow" @tap="returns">
						</view>
						<!-- #endif -->
						<!-- 头部tab标题 -->
						<!-- #ifdef H5  || APP-PLUS-->
						<view class="tab_nav" v-show="opacity > 0.6">
							<view class="header flex justify-between align-center">
								<view class="item" :class="navActive === index ? 'on' : ''" v-for="(item,index) in navList" :key='index'
									@tap="tap(index)">
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
				<view class="detail_container" @touchstart="touchStart">
					<scroll-view :scroll-top="scrollTop" scroll-y='true' scroll-with-animation="true"
						:style='"height:"+height+"px;"' @scroll="scroll">
						<PageDesign
							v-if="diyDataReady"
							:diyData="diyData"
							:productData="diyProductInfo"
							:priceData="diyPriceData"
							:skuList="diySkuList"
							:reply="reply"
							:replyCount="replyCount"
							:replyChance="replyChance"
							:productId="id"
							:couponList="couponDeaultType"
							:activity="activityH5"
							:attr="attr"
							:attrTxt="attrTxt"
							:attrValue="attrValue"
							@changeSpec="onChangeSpecFromPageDesign"
							@showSpecModal="selecAttr"
							@share="listenerActionSheet"
							@showCoupon="couponTap"
							@openModal="openModal"
							@goActivity="goActivity"
						></PageDesign>
						<block v-else>
						<view id="past0">
						<productConSwiper class="skeleton-rect" :imgUrls="sliderImage" :videoline="videoLink"></productConSwiper>
						<view class="pad30">
							<view class='wrapper mb30 borRadius14'>
								<view class='share acea-row row-between row-bottom share-icon-box'>
									<view class='x-money skeleton-rect flex align-baseline'>￥
										<text class='num font-44'>{{attr.productSelect.price}}</text>
									</view>
									<view class='iconfont icon-fenxiang share-icon' @click="listenerActionSheet"></view>
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
								<view v-if="couponDeaultType.length>0 && type=='normal'" class='coupon acea-row row-between-wrapper'
									@click='couponTap'>
									<view class='hide line1 acea-row skeleton-rect'>优惠券：
										<view class='activity'>满{{couponDeaultType[0].minPrice}}减{{couponDeaultType[0].money}}
										</view>
									</view>
									<view class='iconfont icon-jiantou'></view>
								</view>
								<view class="coupon acea-row row-between-wrapper" v-if="activityH5.length">
									<view class="line1 acea-row">
										<text class="activityName skeleton-rect">活&nbsp;&nbsp;&nbsp;动：</text>
										<view v-for='(item,index) in activityH5' :key='index' @click="goActivity(item)" class="activityBox">
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
							<view class='attribute mb30 borRadius14'>
								<!-- 规格选择 -->
								<view class="p-24" @click="selecAttr">
									<view class="acea-row row-between-wrapper">
										<view class="line1 skeleton-rect">{{attrTxt}}：
											<text class='atterTxt'>{{attrValue}}</text>
										</view>
										<view class='iconfont icon-jiantou'></view>
									</view>
									<view class="acea-row row-between-wrapper" style="margin-top:7px;padding-left:55px;"
										v-if="skuImage.length > 1">
										<view class="flex">
											<image :src="item" v-for="(item,index) in skuImage.slice(0,4)" :key="index" class="attrImg">
											</image>
										</view>
										<view class="switchTxt">共{{skuArr.length}}种规格可选</view>
									</view>
								</view>
								<!-- 服务保障 -->
								<view v-if="guaranteeList.length > 0" class="acea-row row-between-wrapper p-24"
									@click="handleToGgle(true)">
									<view class="line1 skeleton-fillet">
										<text class="">保&nbsp;&nbsp;&nbsp;障：</text>
										<text class="atterTxt skeleton-fillet" v-for="(item, index) in guaranteeList"
											:key="index">{{ item.name }} ·
										</text>
									</view>
									<view class="iconfont icon-jiantou"></view>
								</view>
							</view>
							<view class='userEvaluation' id="past1">
								<view class='title acea-row row-between-wrapper'
									:style="replyCount==0?'border-bottom-left-radius:14rpx;border-bottom-right-radius:14rpx;':''">
									<text>用户评价<text>&nbsp;({{replyCount}})</text></text>
									<navigator :render-link="false" class='praise' hover-class='none'
										:url='"/pages/goods/goods_comment_list/index?productId="+id'>
										<text>好评</text>&nbsp;<text class='font_color px-12'>{{replyChance || 0}}%</text>
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
									<image :src="urlDomain+'/crmebimage/perset/staticImg/xzuo.png'"></image>
									<view class="titleTxt">优品推荐</view>
									<image :src="urlDomain+'/crmebimage/perset/staticImg/xyou.png'"></image>
								</view>
								<view class="slider-banner banner">
									<swiper indicator-dots="true" :autoplay="autoplay" :circular="circular" :interval="interval"
										:duration="duration" indicator-color="#999" :indicator-active-color="indicatorBg"
										:style="'height:'+clientHeight+'px'">
										<swiper-item v-for="(item,indexw) in good_list" :key="indexw">
											<view class="list acea-row row-middle" :id="'list'+indexw">
												<view class="item" v-for="(val,indexn) in item.list" :key="indexn" @click="goDetail(val)">
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
							<image :src="urlDomain+'/crmebimage/perset/staticImg/xzuo.png'"></image>
							<span class="sp">产品详情</span>
							<image :src="urlDomain+'/crmebimage/perset/staticImg/xyou.png'"></image>
						</view>
						<view class='conter'>
							<mp-html :content="description" ref="article" :tag-style="tagStyle"></mp-html>
						</view>
					</view>
						<view style='height:120rpx;'></view>
						</block>
					</scroll-view>
				</view>
				<productBottom
					v-if="diyDataReady"
					:diyData="diyData"
					:storeInfo="diyProductInfo"
					:CartCount="CartCount"
					:noGoods="noGoods"
					:attr="attr"
					:presale_pay_status="2"
					:animated="animated"
					:routineContact="0"
					@setCollect="setCollect"
					@goCart="goCart"
					@joinCart="joinCart"
					@goBuy="goBuy"
					@share="listenerActionSheet"
				></productBottom>
				<view v-else class='legacy-product-footer acea-row row-between-wrapper'>
				<!-- #ifdef MP -->
				<button hover-class='none' class='item skeleton-rect' @click="onClickService"
					v-if="chatConfig.telephone_service_switch === 'open'">
					<view class='iconfont icon-kefu'></view>
					<view>客服</view>
				</button>
				<template v-else>
					<button open-type="contact" hover-class='none' class='item skeleton-rect'
						v-if="chatConfig.wx_chat_independent=='open'">
						<view class='iconfont icon-kefu'></view>
						<view>客服</view>
					</button>
					<button class="item" hover-class='none' @click="wxChatService" v-else>
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
					<navigator :render-link="false" open-type='switchTab' class="animated item skeleton-rect" :class="animated==true?'bounceIn':''"
						url='/pages/order_addcart/order_addcart' hover-class="none">
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
						<form @submit="goBuy" report-submit="true"><button class="buy bnts" form-type="submit">立即购买</button>
						</form>
					</view>
				</block>
				<view class="bnt bntVideo acea-row skeleton-rect" v-if="attr.productSelect.stock <= 0 && type === 'video'">
					<form report-submit="true"><button class="bnts bg-color-hui" form-type="submit">已售罄</button>
					</form>
				</view>
				<view class="bnt bntVideo acea-row skeleton-rect" v-if="attr.productSelect.stock > 0 && type === 'video'">
					<form @submit="goBuy" report-submit="true"><button class="buy bnts" form-type="submit">立即购买</button>
					</form>
				</view>
			</view>
			<shareRedPackets :sharePacket="sharePacket" @listenerActionSheet="listenerActionSheet" @showShare="showShare">
			</shareRedPackets>
			<!-- 组件 -->
			<productWindow :attr="attr" :isShow='1' :iSplus='1' @myevent="onMyEvent" @ChangeAttr="ChangeAttr"
				@ChangeCartNum="ChangeCartNum" @attrVal="attrVal" @iptCartNum="iptCartNum" id='product-window'
				@getImg="showImg">
			</productWindow>
			<!-- 优惠券弹窗 -->
			<block v-if="couponDeaultType.length">
				<couponListWindow :coupon='coupon' :typeNum="couponDeaultType[0].useType"
					:firstType="couponDeaultType[0].useType" @ChangCouponsClone="ChangCouponsClone" @ChangCoupons="ChangCoupons"
					@ChangCouponsUseState="ChangCouponsUseState" @tabCouponType="tabCouponType">
				</couponListWindow>
			</block>
			<!-- 分享按钮 -->
			<view class="generate-posters" :class="posters ? 'on' : ''">
				<view class="generateCon acea-row row-middle">
					<!-- #ifndef MP -->
					<button class="item" hover-class="none" v-if="weixinStatus === true" @click="H5ShareBox = true">
						<view class="pictrue">
							<image :src="urlDomain+'/crmebimage/perset/staticImg/weixin.png'"></image>
						</view>
						<view class="">分享给好友</view>
					</button>
					<!-- #endif -->
					<!-- #ifdef MP -->
					<button class="item" open-type="share" hover-class="none">
						<view class="pictrue">
							<image :src="urlDomain+'/crmebimage/perset/staticImg/weixin.png'"></image>
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
							<image :src="urlDomain+'/crmebimage/perset/staticImg/changan.png'"></image>
						</view>
						<view class="">预览发图</view>
					</view>
					<!-- #endif -->
					<!-- #ifdef MP  -->
					<button class="item" hover-class="none" @click="savePosterPath">
						<view class="pictrue">
							<image :src="urlDomain+'/crmebimage/perset/staticImg/haibao.png'"></image>
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
				<image :src="urlDomain+'/crmebimage/perset/staticImg/share-info.png'" @click="H5ShareBox = false"></image>
			</view>
			<!-- 保障服务弹窗 -->
			<view class="guarantee-pop-box">
				<uni-popup ref="guaranteePopup" :safe-area="false" type="bottom" borderRadius="20px 20px 0 0">
					<view class="ensure">
						<view class="title">
							保障服务
							<view class="close-box" @click="handleToGgle(false)">
								<view class="iconfont icon-guanbi f-s-24"></view>
							</view>
						</view>
						<view class="list">
							<view class="item acea-row" v-for="(item, index) in guaranteeList" :key="index">
								<view class="pictrue">
									<text class="iconfont icon-gou1"></text>
								</view>
								<view class="text">
									<view class="name">{{ item.name }}</view>
									<view>{{ item.content }}</view>
								</view>
							</view>
						</view>
						<view class="activityBtn bnt" @click="handleToGgle(false)">
							确定
						</view>
					</view>
				</uni-popup>
			</view>
		</view>
	</view>
</template>

<script setup>
	import { ref, reactive, computed, watch, nextTick, getCurrentInstance } from 'vue';
	import { onLoad, onShow, onReachBottom, onReady, onPageScroll, onShareAppMessage } from '@dcloudio/uni-app';
	import tuiSkeleton from "@/components/base/tui-skeleton.vue";
	import uQRCode from '@/js_sdk/Sansnn-uQRCode/uqrcode.js'
	import { useAppStore } from "@/store/app.js";
	import { storeToRefs } from 'pinia';
	import {
		HTTP_H5_URL
	} from '@/config/app.js';
	import {
		spread
	} from "@/api/user.js";
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
			getThemeInfo
		} from '@/api/api.js';
	import {
		getCartCounts
	} from '@/api/order.js';
	import {
		toLogin
	} from '@/libs/login.js';
	import {
		imageBase64
	} from "@/api/public.js";
	import productConSwiper from '@/components/productConSwiper/index.vue';
	import couponListWindow from '@/components/couponListWindow/index.vue';
	import productWindow from '@/components/productWindow/index.vue';
	import userEvaluation from '@/components/userEvaluation/index.vue';
	import shareRedPackets from '@/components/shareRedPackets/index.vue';
	import CusPreviewImg from '@/components/cus-previewImg/cus-previewImg.vue'
	import {
		silenceBindingSpread
	} from "@/utils";
	import mpHtml from '@/uni_modules/mp-html/components/mp-html/mp-html.vue';
	import {
		computeUser
	} from "@/api/user.js";
	// #ifdef MP
	import {
		base64src
	} from '@/utils/base64src.js'
	import {
		getQrcode as fetchQrcode
	} from '@/api/api.js';
	// #endif
	let app = getApp();
	import {
		setThemeColor
	} from '@/utils/setTheme.js'
	import {
		Debounce
	} from '@/utils/validate.js'
	import { useColor } from '@/composables/useColor.js';
	import navBar from '@/components/navBar.vue';
		import PageDesign from '@/subpackage/diyComponents/pageDesign.vue';
		import productBottom from '@/subpackage/diyComponents/productBottom.vue';
	import util from '@/utils/util.js';
	import Cache from '@/utils/cache.js';
	import * as Order from '@/libs/order.js';
	// #ifdef H5
	import Auth from '@/libs/wechat.js';
	// #endif

	const { proxy } = getCurrentInstance();
	const { colorStyle } = useColor();
	const store = useAppStore();
	const { isLogin, uid, chatUrl, productType } = storeToRefs(store);

	// Template refs
	const cusPreviewImg = ref(null);
	const guaranteePopup = ref(null);

	// --- Data ---
	const urlDomain = ref(Cache.get("imgHost"));
	const showSkeleton = ref(true); //骨架屏显示隐藏
	const isNodes = ref(0); //控制什么时候开始抓取元素节点,只要数值改变就重新抓取
	//属性是否打开
	const coupon = reactive({
		coupon: false,
		type: 0,
		list: [],
		count: []
	});
	const attrTxt = ref('请选择'); //属性页面提示
	const attrValue = ref(''); //已选属性
	const animated = ref(false); //购物车动画
	const id = ref(0); //商品id
	const replyCount = ref(0); //总评论数量
	const reply = ref([]); //评论列表
	const productInfo = ref({}); //商品详情
	const productValue = ref([]); //系统属性
	const guaranteeList = ref([]); // 保障服务列表
	const couponList = ref([]); //优惠券
		const cart_num = ref(1); //购买数量
		const diyData = ref({});
		const noGoods = ref(false);
		const isAuto = ref(false); //没有授权的不会自动授权
	const isShowAuth = ref(false); //是否隐藏授权
	const isOpen = ref(false); //是否打开属性组件
	const actionSheetHidden = ref(true);
	const storeImage = ref(''); //海报产品图
	const PromotionCode = ref(''); //二维码图片
	const posterbackgd = ref(`${Cache.get("imgHost")}/crmebimage/perset/staticImg/posterbackgd.png`);
	const sharePacket = reactive({
		isState: true, //默认不显示
		touchstart: false
	}); //分销商详细
	const circular = ref(false);
	const autoplay = ref(false);
	const interval = ref(3000);
	const duration = ref(500);
	const clientHeight = ref("");
	const systemStore = ref({}); //门店信息
	const good_list = ref([]);
	const replyChance = ref(0);
	const CartCount = ref(0);
	const isDown = ref(true);
	const posters = ref(false);
	const weixinStatus = ref(false);
	const attr = reactive({
		cartAttr: false,
		productAttr: [],
		productSelect: {}
	});
	const description = ref('');
	const navActive = ref(0);
	const H5ShareBox = ref(false); //公众号分享图片
	const activityH5 = ref([]);
	const retunTop = ref(true); //顶部返回
	const navH = ref("");
	const navList = ref([]);
	const opacity = ref(0);
	const scrollY = ref(0);
	const topArr = ref([]);
	const toView = ref('');
	const height = ref(0);
	const heightArr = ref([]);
	const lock = ref(false);
	const scrollTop = ref(0);
	const tagStyle = ref({
		img: 'width:100%;display:block;',
		table: 'width:100%',
		video: 'width:100%'
	});
	const sliderImage = ref([]);
	const videoLink = ref('');
	const qrcodeSize = ref(600);
	const canvasStatus = ref(false); //是否显示海报
	const imagePath = ref(''); //海报路径
	const imgTop = ref('');
	const errT = ref('');
	const homeTop = ref(20);
	const navbarRight = ref(0);
	const userCollect = ref(false);
	const returnShow = ref(true); //判断顶部返回是否出现
	const type = ref(""); //视频号普通商品类型
	const theme = ref(app.globalData.theme);
	const indicatorBg = ref('');
	const shareStatus = ref(true);
	const skuArr = ref([]);
	const currentPage = ref(false);
	const selectSku = ref({});
	const selectNavList = ref([{
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
	]);
	const chatConfig = reactive({
		consumer_hotline: '',
		telephone_service_switch: 'close',
		wx_chat_independent: 'open'
	}); //客服配置
	const defaultCoupon = ref([]);
	// couponDeaultType: [{
	// 	useType: 1
	// }],
	const couponDeaultType = ref([]);
	//优惠券分页数据
	const where = reactive({
		page: 1,
		limit: 999,
		productId: 0,
		type: 0
	});
	const couponType = ref(0); //优惠券类型 类型，1-通用，2-商品，3-品类
	const skuImage = ref([]); //规格小图
	const loadend = ref(false);
	const loading = ref(false);
	const loadTitle = ref('');

	// --- Computed ---
	const diyDataReady = computed(() => {
		return diyData.value && Object.keys(diyData.value).length > 0;
	});
	const diyProtectionList = computed(() => {
		return (guaranteeList.value || []).map((item) => ({
			...item,
			title: item.title || item.name,
		}));
	});
	const diyProductInfo = computed(() => {
		const pInfo = productInfo.value || {};
		const productSelect = attr.productSelect || {};
		const sales = Math.floor(Number(pInfo.sales) || 0) + Math.floor(Number(pInfo.ficti) || 0);
		const sliderImages = sliderImage.value.length ? sliderImage.value : parseSliderImage(pInfo.sliderImage);
		return {
			...pInfo,
			store_name: pInfo.storeName || pInfo.store_name || '',
			store_info: pInfo.storeInfo || pInfo.store_info || '',
			slider_image: sliderImages.length ? sliderImages : (pInfo.image ? [pInfo.image] : []),
			video_link: videoLink.value || pInfo.videoLink || pInfo.video_link || '',
			description: description.value || pInfo.content || pInfo.description || '',
			unit_name: pInfo.unitName || pInfo.unit_name || '',
			ot_price: productSelect.otPrice || pInfo.otPrice || pInfo.ot_price || 0,
			real_price: productSelect.price || pInfo.price || 0,
			price: productSelect.price || pInfo.price || 0,
			fsales: sales,
			userCollect: userCollect.value,
			protection_list: diyProtectionList.value,
			label_list: pInfo.labelList || pInfo.label_list || [],
			cart_button: type.value === 'normal',
			presale: false,
		};
	});
	const diyPriceData = computed(() => {
		const pInfo = productInfo.value || {};
		const productSelect = attr.productSelect || {};
		return {
			price: productSelect.price || pInfo.price || 0,
			real_price: productSelect.price || pInfo.price || 0,
			ot_price: productSelect.otPrice || pInfo.otPrice || 0,
			stock: productSelect.stock || pInfo.stock || 0,
			unit_name: pInfo.unitName || '',
		};
	});
	const diySkuList = computed(() => {
		return (skuArr.value || []).map((item) => ({
			...item,
			unique: item.unique || item.id,
			ot_price: item.otPrice || item.ot_price || 0,
			real_price: item.price || item.real_price || 0,
		}));
	});

	// --- Watch ---
	watch(productInfo, () => {
		nextTick(() => {});
	}, { immediate: true });

	// --- Methods ---
	const getDiyData = () => {
		let previewThemeId = uni.getStorageSync('previewThemeId');
		let data = {};
		if (previewThemeId) data.theme_id = previewThemeId;
		getThemeInfo('detail', data).then((res) => {
			diyData.value = res.data || {};
		}).catch(() => {
			diyData.value = {};
		});
	};

	const parseSliderImage = (value) => {
		if (!value) return [];
		if (Array.isArray(value)) return value;
		try {
			const list = JSON.parse(value);
			return Array.isArray(list) ? list : [];
		} catch (e) {
			return typeof value === 'string' ? value.split(',').filter(Boolean) : [];
		}
	};

	const onChangeSpecFromPageDesign = (item) => {
		if (item && item.suk) {
			let values = item.suk.split(',');
			if (attr.productAttr && attr.productAttr.length === values.length) {
				values.forEach((value, index) => {
					attr.productAttr[index].index = value;
				});
			}
			ChangeAttr(item.suk);
		} else {
			selecAttr();
		}
	};

	const openModal = (type) => {
		if (type === 'protection') {
			handleToGgle(true);
		}
	};

	const goCart = () => {
		uni.switchTab({
			url: '/pages/order_addcart/order_addcart'
		});
	};
      //滚动
      const touchStart = () => {
        currentPage.value = false;
      };
	//独立客服跳转
	const wxChatService = () => {
		let chatUrlArr = chatUrl.value.split('?')
		uni.navigateTo({
			url: `/pages/users/web_page/index?webUel=${chatUrlArr[0]}&title=客服&${chatUrlArr[1]}`
		})
	};
	const loadUserRelatedData = () => {
		if (isLogin.value) {
			getCouponList(0); //优惠券列表 类型，1-通用，2-商品，3-品类
			getCartCount(true); //购物车数量
			//绑定关系
			silenceBindingSpread();
		}
	};
	// #ifdef APP-PLUS
	const appShare = (scene) => {
		let routes = getCurrentPages(); // 获取当前打开过的页面路由数组
		let curRoute = routes[routes.length - 1].$page.fullPath // 获取当前页面路由，也就是最后一个打开的页面路由
		uni.share({
			provider: "weixin",
			scene: scene,
			type: 0,
			href: `${HTTP_H5_URL}${curRoute}&spread=${uid.value}`,
			title: productInfo.value.storeName,
			summary: app.globalData.companyName,
			imageUrl: productInfo.value.image,
			success: function(res) {
				posters.value = false;
			},
			fail: function(err) {
				uni.showToast({
					title: '分享失败',
					icon: 'none',
					duration: 2000
				})
				posters.value = false;
			}
		});
	};
	// #endif
	const onClickService = () => {
		if (chatConfig.telephone_service_switch === 'open') {
			uni.makePhoneCall({
				phoneNumber: chatConfig.consumer_hotline //仅为示例
			});
		} else {
			// #ifdef APP-PLUS
			uni.navigateTo({
				url: '/pages/users/web_page/index?webUel=' + chatUrl.value + '&title=客服'
			})
			// #endif
			// #ifndef APP-PLUS
			location.href = chatUrl.value;
			// #endif
		}
	};
	const goActivity = (e) => {
		let item = e;
		if (item.type === "1") {
			uni.navigateTo({
				url: `/pages/activity/goods_seckill_details/index?id=${item.id}`
			});
		} else if (item.type === "2") {
			uni.navigateTo({
				url: `/pages/activity/goods_bargain_details/index?id=${item.id}&startBargainUid=${uid.value}`
			});
		} else {
			uni.navigateTo({
				url: `/pages/activity/goods_combination_details/index?id=${item.id}`
			});
		}
	};
	/**
	 * 购物车手动填写
	 *
	 */
	const iptCartNum = (e) => {
		attr.productSelect.cart_num = e ? e : 1;
	};
	// 后退
	const returns = () => {
		uni.navigateBack()
	};
	const showNav = () => {
		currentPage.value = !currentPage.value;
	};
	const tap = (index) => {
		var id = "past" + index;
		var index = index;
		toView.value = id;
		navActive.value = index;
		lock.value = true;
		scrollTop.value = index > 0 ? topArr.value[index] - (app.globalData.navHeight / 2) : topArr.value[index];
	};
	const scroll = (e) => {
		var scrollYVal = e.detail.scrollTop;
		var opacityVal = scrollYVal / 500;
		opacityVal = opacityVal > 1 ? 1 : opacityVal;
		opacity.value = opacityVal;
		scrollY.value = scrollYVal;
		if (lock.value) {
			lock.value = false;
			return;
		}
		for (var i = 0; i < topArr.value.length; i++) {
			if (scrollYVal < topArr.value[i] - (app.globalData.navHeight / 2) + heightArr.value[i]) {
				navActive.value = i;
				break
			}
		}
		sharePacket.touchstart = true; //滑动屏幕时让分享气泡缩回
	};
	/*
	 *去商品详情页
	 */
	const goDetail = (item) => {
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
				url: `/pages/activity/goods_bargain_details/index?id=${item.activityH5.id}&startBargainUid=${uid.value}`
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
			uni.redirectTo({
				url: `/pages/activity/goods_seckill_details/index?id=${item.activityH5.id}`
			})
			return
		}
	};
	// 关闭优惠券弹窗，重置优惠券列表
	const ChangCouponsClone = () => {
		coupon.coupon = false;
		initCouponList();
		coupon.type = 0;
		couponType.value = 0; //优惠券类型
		getCouponList(0);
	};
	/**
	 * 购物车数量加和数量减
	 *
	 */
	const ChangeCartNum = (changeValue) => {
		//changeValue:是否 加|减
		//获取当前变动属性
		let productSelect = productValue.value[attrValue.value];
		//如果没有属性,赋值给商品默认库存
		if (productSelect === undefined && !attr.productAttr.length)
			productSelect = attr.productSelect;
		//无属性值即库存为0；不存在加减；
		if (productSelect === undefined) return;
		let stock = productSelect.stock || 0;
		let num = attr.productSelect;
		if (changeValue) {
			num.cart_num++;
			if (num.cart_num > stock) {
				attr.productSelect.cart_num = stock;
				cart_num.value = stock;
			}
		} else {
			num.cart_num--;
			if (num.cart_num < 1) {
				attr.productSelect.cart_num = 1;
				cart_num.value = 1;
			}
		}
	};
	const attrVal = (val) => {
		attr.productAttr[val.indexw].index = attr.productAttr[val.indexw].attrValues[val.indexn];
	};
	/**
	 * 属性变动赋值
	 *
	 */
	const ChangeAttr = (res) => {
		let productSelect = productValue.value[res];
		selectSku.value = productSelect;
		if (productSelect) {
			attr.productSelect.image = productSelect.image;
			attr.productSelect.price = productSelect.price;
			attr.productSelect.stock = productSelect.stock;
			attr.productSelect.unique = productSelect.id;
			attr.productSelect.cart_num = 1;
			attr.productSelect.otPrice = productSelect.otPrice;
			attr.productSelect.isShow = productSelect.isShow;
			// 后台传入的规格不展示时视为库存为0
			if (!attr.productSelect.isShow) {
				attr.productSelect.stock = 0;
				util.Tips({
					title: "请重新选择其它规格"
				});
			}
			attrValue.value = res;
			attrTxt.value = "已选择";
		} else {
			attr.productSelect.image = productInfo.value.image;
			attr.productSelect.price = productInfo.value.price;
			attr.productSelect.stock = 0;
			attr.productSelect.unique = productInfo.value.id;
			attr.productSelect.cart_num = 1;
			attr.productSelect.otPrice = productInfo.value.otPrice;
			attr.productSelect.isShow = false;
			attrValue.value = "";
			attrTxt.value = "请选择";
		}
	};
	/**
	 * 领取完毕移除当前页面领取过的优惠券展示
	 */
	const ChangCoupons = (e) => {
		let couponItem = e;
		let newCouponList = util.ArrayRemove(couponList.value, 'id', couponItem.id);
		couponList.value = newCouponList;
		getCouponList();
	};
	const setClientHeight = () => {
		if (!good_list.value.length) return;
		let view = uni.createSelectorQuery().select("#list0");
			view.fields({
				size: true,
			}, data => {
				clientHeight.value = data ? data.height + 20 : 0;
			}).exec();
		};
	/**
	 * 优品推荐
	 *
	 */
	const getGoods = () => {
		getProductGood().then(res => {
			let goodList = res.data.list || [];
			let count = Math.ceil(goodList.length / 6);
			let goodArray = new Array();
			for (let i = 0; i < count; i++) {
				let list = goodList.slice(i * 6, i * 6 + 6);
				if (list.length) goodArray.push({
					list: list
				});
			}
			good_list.value = goodArray;
			let newNavList = ['商品', '评价', '详情'];
			if (goodArray.length) {
				newNavList.splice(2, 0, '推荐')
			}
			navList.value = newNavList;
			nextTick(() => {
				if (goodList.length) {
					// #ifndef APP-PLUS
					setClientHeight();
					// #endif
					// #ifdef APP-PLUS
					setTimeout(() => {
						setClientHeight();
					}, 1000)
					// #endif
				};
			})
		});
	};
	/**
	 * 获取产品详情
	 *
	 */
	const getGoodsDetails = () => {
		getProductDetail(id.value, type.value).then(res => {
			let pInfo = res.data.productInfo;
			// 字符串数组转数组；
			let sImage = parseSliderImage(pInfo.sliderImage);
			if (getFileType(sImage[0]) == 'video') {
				//判断轮播图第一张是否是视频，如果是，就赋值给videoLink，并且将其在轮播图中删除
				videoLink.value = sImage[0];
				sImage.splice(0, 1);
			}
			sliderImage.value = sImage;
			productInfo.value = pInfo;
			description.value = pInfo.content;
			userCollect.value = res.data.userCollect;
			attr.productAttr = res.data.productAttr;
			productValue.value = res.data.productValue;
			guaranteeList.value = res.data.guaranteeList || [];
			skuArr.value = [];
			for (let key in res.data.productValue) {
				let obj = res.data.productValue[key];
				skuArr.value.push(obj)
			}
			selectSku.value = skuArr.value[0];
			sharePacket.priceName = res.data.priceName;
			//sharePacket.isState = Math.floor(res.data.priceName) != 0 ?false : true;
			sharePacket.isState = (res.data.priceName != "0" && res.data.priceName !==
				null) ? false : true;
			activityH5.value = res.data.activityAllH5 ? res.data.activityAllH5 : [];
			uni.setNavigationBarTitle({
				title: pInfo.storeName.substring(0, 7) + "..."
			})

			let productAttr = attr.productAttr.map(item => {
				return {
					attrName: item.attrName,
					attrValues: item.attrValues.split(','),
					id: item.id,
					isDel: item.isDel,
					productId: item.productId,
					type: item.type,
					optionList: item.optionList || [],
					isShowImage: item.isShowImage
				}
			});
			attr.productAttr = productAttr;
			// 获取suk小图
			getSkuImage()
			// var navList = ['商品', '评价', '详情'];
			// if (goodArray.length) {
			// 	navList.splice(2, 0, '推荐')
			// }
			//navList.value = navList;

			//无需登录即可分享
			getCartCount();
			//#ifdef H5
			make();
			ShareInfo();
			getImageBase64(productInfo.value.image);
			// #endif
			// #ifdef MP
			getQrcode();
			// #endif
			setTimeout(function() {
				infoScroll();
			}, 500);
			// #ifdef MP
			imgTop.value = res.data.productInfo.image
			// #endif
			// #ifndef H5
			downloadFilestoreImage();
			// #endif
			DefaultSelect();
			showSkeleton.value = false
			// setTimeout(() => {
			// 	defaultCoupon.value = coupon.list;
			// }, 1000)
		}).catch(err => {
			//状态异常返回上级页面
			util.Tips({
				title: err.toString()
			}, {
				tab: 3,
				url: 1
			});
			showSkeleton.value = false
		})
	};
	//评论列表
	const getProductReplyList = () => {
		getReplyProduct(id.value).then(res => {
			reply.value = res.data.productReply ? [res.data.productReply] : [];
		})
	};
	//评论条数
	const getProductReplyCount = () => {
		getReplyConfig(id.value).then(res => {
			replyChance.value = res.data.replyChance * 100;
			replyCount.value = res.data.sumCount;
		});
	};
	const infoScroll = () => {
		var topArrLocal = [],
			heightArrLocal = [];
		for (var i = 0; i < navList.value.length; i++) { //productList
			//获取元素所在位置
			var query = uni.createSelectorQuery();
			var idView = "#past" + i;
			// if (!good_list.value.length && i == 2) {
			//   var idView = "#past" + 3;
			// }
			query.select(idView).boundingClientRect();
			query.exec(function(res) {
				var top = res && res[0] ? res[0].top : 0;
				var height = res && res[0] ? res[0].height : 0;
				topArrLocal.push(top);
				heightArrLocal.push(height);
				topArr.value = topArrLocal;
				heightArr.value = heightArrLocal;
			});
		};
	};
	/**
	 * 默认选中属性
	 *
	 */
	const DefaultSelect = () => {
		let productAttr = attr.productAttr;
		let value = [];
		// 按 id 升序排序
		const sortedArray = Object.entries(productValue.value)
			.sort(([, a], [, b]) => a.id - b.id)
			.map(([key, val]) => ({
				key,
				...val
			}));
		// 默认规格设置
		for (let i = 0; i < sortedArray.length; i++) {
			const attrItem = sortedArray[i]
			if (attrItem.stock > 0 && attrItem.isShow) {
				if (value.length == 0) {
					value = attr.productAttr.length ? attrItem.key.split(",") : [];
				}
				if (attrItem.isDefault) {
					value = attr.productAttr.length ? attrItem.key.split(",") : [];
					break
				}
			}
		}
		for (let i = 0; i < value.length; i++) {
			productAttr[i].index = value[i];
		}

		//sort();排序函数:数字-英文-汉字；
		let productSelect = productValue.value[value.join(",")];
		if (productSelect && productAttr.length) {
			attr.productSelect.storeName = productInfo.value.storeName;
			attr.productSelect.image = productSelect.image;
			attr.productSelect.price = productSelect.price;
			attr.productSelect.stock = productSelect.stock;
			attr.productSelect.unique = productSelect.id;
			attr.productSelect.cart_num = 1;
			attr.productSelect.otPrice = productSelect.otPrice;
			attrValue.value = value.join(",");
			attrTxt.value = "已选择";
		} else if (!productSelect && productAttr.length) {
			attr.productSelect.storeName = productInfo.value.storeName;
			attr.productSelect.image = productInfo.value.image;
			attr.productSelect.price = productInfo.value.price;
			attr.productSelect.stock = 0;
			attr.productSelect.unique = productInfo.value.id;
			attr.productSelect.cart_num = 1;
			attr.productSelect.otPrice = productInfo.value.otPrice;
			attrValue.value = "";
			attrTxt.value = "请选择";
		} else if (!productSelect && !productAttr.length) {
			attr.productSelect.storeName = productInfo.value.storeName;
			attr.productSelect.image = productInfo.value.image;
			attr.productSelect.price = productInfo.value.price;
			attr.productSelect.stock = productInfo.value.stock;
			attr.productSelect.unique = productInfo.value.id || "";
			attr.productSelect.cart_num = 1;
			attr.productSelect.otPrice = productInfo.value.otPrice;
			attrValue.value = "";
			attrTxt.value = "请选择";
		}
	};
	/**
	 * 获取优惠券
	 *
	 */
	const getCouponList = (type) => {
		if (type != undefined || type != null) {
			where.type = type;
		} else {
			where.type = "";
		}
		where.productId = id.value;
		if (loadend.value) return;
		if (loading.value) return;
		// if (isPage === true) couponList.value = [];
		loading.value = true;
		loadTitle.value = '';
		getCoupons(where).then(res => {
			let list = res.data.list;
			let newCouponList = util.SplitArray(list, coupon.list);
			let isLoadEnd = list.length < where.limit;
			loadend.value = isLoadEnd;
			loading.value = false;
			loadTitle.value = isLoadEnd ? '已全部加载' : '加载更多';
			coupon.list = newCouponList;
			where.page = where.page + 1;
		});
	};
	const getCouponType = async () => {
		//在onLoad只调用一次，获取默认的类型作为打开优惠券列表的参数，不会随着切换变化
		let dataList = await getCoupons({
			productId: id.value
		});
		// if (dataList.length) {
		// 	couponDeaultType.value = dataList.data;
		// 	coupon.type = dataList;
		// }
		if (dataList.data.list.length) {
			couponDeaultType.value = dataList.data.list;
			coupon.type = dataList.data;
		}
	};
	// 重置优惠券列表
	const initCouponList = () => {
		loadend.value = false;
		loading.value = false;
		where.page = 1;
		where.limit = 999;
		coupon.list = [];
	};
	//切换优惠券头部
	const tabCouponType = (type) => {
		initCouponList();
		coupon.type = type;
		couponType.value = type; //优惠券类型
		getCouponList(type);
	};
	const ChangCouponsUseState = (index) => {
		coupon.list[index].isUse = true;
		coupon.coupon = false;
	};
	/**
	 *
	 *
	 * 收藏商品
	 */
	const setCollect = () => {
		if (isLogin.value === false) {
			toLogin();
		} else {
			if (userCollect.value) {
				collectDel(productInfo.value.id).then(res => {
					userCollect.value = !userCollect.value;
				})
			} else {
				collectAdd(productInfo.value.id).then(res => {
					userCollect.value = !userCollect.value;
				})
			}
		}
	};
	/**
	 * 打开属性插件
	 */
	const selecAttr = () => {
		attr.cartAttr = true;
		isOpen.value = true;
	};
	/**
	 * 打开优惠券插件
	 */
	const couponTap = () => {
		if (!couponDeaultType.value.length) return;
		// if (isLogin.value === false) {
		// 	toLogin();
		// } else {
		// loadend.value = false;
		// loading.value = false;
		// coupon.list = [];
		// //getCouponList(couponDeaultType.value[0].useType); //打开弹框默认请求商品券
		initCouponList();
		getCouponList(couponDeaultType.value[0].useType);
		coupon.coupon = true;
		// }
	};
	const onMyEvent = () => {
		attr.cartAttr = false;
		isOpen.value = false;
	};
	/**
	 * 打开属性加入购物车
	 *
	 */
	const joinCart = (e) => {
		//是否登录
		if (isLogin.value === false) {
			toLogin();
		} else {
			goCat(1);
		}
	};
	/*
	 * 加入购物车
	 */
	const goCat = (num) => {
		let productSelectVal = productValue.value[attrValue.value];
		//打开属性
		if (attrValue.value) {
			//默认选中了属性，但是没有打开过属性弹窗还是自动打开让用户查看默认选中的属性
			attr.cartAttr = !isOpen.value ? true : false;
		} else {
			if (isOpen.value) attr.cartAttr = true;
			else attr.cartAttr = !attr.cartAttr;
		}
		//只有关闭属性弹窗时进行加入购物车
		if (attr.cartAttr === true && isOpen.value === false)
			return (isOpen.value = true);
		//如果有属性,没有选择,提示用户选择
		if (
			attr.productAttr.length &&
			productSelectVal.stock === 0 &&
			isOpen.value === true
		) {
			return util.Tips({
				title: "产品库存不足，请选择其它"
			});
		} else if (attr.productSelect.stock == 0 && isOpen.value === true) {
			return util.Tips({
				title: "请选择其它规格"
			});
		}
		if (num === 1) {
			let q = {
				productId: parseFloat(id.value),
				cartNum: parseFloat(attr.productSelect.cart_num),
				isNew: false,
				productAttrUnique: attr.productSelect !== undefined ?
					attr.productSelect.unique : productInfo.value.id
			};
			postCartAdd(q).then(function(res) {
					isOpen.value = false;
					attr.cartAttr = false;
					util.Tips({
						title: "添加购物车成功",
						success: () => {
							getCartCount(true);
						}
					});
				})
				.catch(res => {
					isOpen.value = false;
					return util.Tips({
						title: res
					});
				});
		} else {
			getPreOrder();
		}
	};
	/**
	 * 获取购物车数量
	 * @param boolean 是否展示购物车动画和重置属性
	 */
	const getCartCount = (isAnima) => {
		const isLoggedIn = isLogin.value;
		if (isLoggedIn) {
			getCartCounts(true, 'total').then(res => {
				CartCount.value = res.data.count;
				//加入购物车后重置属性
				if (isAnima) {
					animated.value = true;
					setTimeout(function() {
						animated.value = false;
					}, 500);
				}
			});
		}
	};
	/**
	 * 立即购买
	 */
	const goBuy = Debounce(function(e) {
		if (isLogin.value === false) {
			toLogin();
		} else {
			goCat(0);
		}
	});
	/**
	 * 预下单
	 */
	const getPreOrder = () => {
		Order.getPreOrder(type.value === 'normal' ? 'buyNow' : 'video', [{
			"attrValueId": parseFloat(attr.productSelect.unique),
			"productId": parseFloat(id.value),
			"productNum": parseFloat(attr.productSelect.cart_num)
		}]);
		isOpen.value = false;
	};
	// 授权关闭
	const authColse = (e) => {
		isShowAuth.value = e;
	};
	/**
	 * 分享打开
	 *
	 */
	const listenerActionSheet = () => {
		// if (isLogin.value === false) {
		// 	toLogin();
		// } else {
		// 	// #ifdef H5
		// 	if (proxy.$wechat.isWeixin() === true) {
		// 		weixinStatus.value = true;
		// 	}
		// 	// #endif
		// 	goPoster()
		// 	posters.value = true;
		// }
		// #ifdef H5
		if (proxy.$wechat.isWeixin() === true) {
			weixinStatus.value = true;
		}
		// #endif
		goPoster()
		posters.value = true;
	};
	const closePosters = () => {
		posters.value = false;
		currentPage.value = false;
	};
	//隐藏海报
	const posterImageClose = () => {
		canvasStatus.value = false;
		posters.value = false;
	};
	//替换安全域名
	const setDomain = (url) => {
		url = url ? url.toString() : '';
		//本地调试打开,生产请注销
		if (url.indexOf("https://") > -1) return url;
		else return url.replace('http://', 'https://');
	};
	//获取海报产品图（解决跨域问题，只适用于小程序）
	const downloadFilestoreImage = () => {
		uni.downloadFile({
			url: setDomain(productInfo.value.image),
			success: function(res) {
				storeImage.value = res.tempFilePath;
			},
			fail: function() {
				return util.Tips({
					title: ''
				});
				storeImage.value = '';
			},
		});
	};
	// 小程序关闭分享弹窗；
	const goFriend = () => {
		posters.value = false;
	};
	// 小程序二维码
	// #ifdef MP
	const getQrcode = () => {
		let data = {
			pid: uid.value,
			id: id.value,
			path: 'pages/goods/goods_details/index'
		}
		fetchQrcode(data).then(res => {
			base64src(res.data.code, Date.now(), res => {
				PromotionCode.value = res;
			});

		}).catch(err => {
			errT.value = err;
		});
	};
	// #endif
	// 生成二维码；
	const make = () => {
		let href = location.href.split('?')[0] + "?id=" + id.value + "&spread=" + uid.value;
		uQRCode.make({
			canvasId: 'qrcode',
			text: href,
			size: qrcodeSize.value,
			margin: 10,
			success: res => {
				PromotionCode.value = res;
			},
			complete: () => {},
			fail: res => {
				util.Tips({
					title: '海报二维码生成失败！'
				});
			}
		})
	};
	const getImageBase64 = (images) => {
		imageBase64({
			url: images
		}).then(res => {
			imgTop.value = res.data.code;
		})
	};
	/**
	 * 生成海报
	 */
	const goPoster = () => {
		uni.showLoading({
			title: '海报生成中',
			mask: true
		});
		posters.value = false;
		let arrImagesUrl = '';
		let arrImagesUrlTop = '';
		if (!PromotionCode.value) {
			uni.hideLoading();
			util.Tips({
				title: errT.value
			});
			return
		}
		setTimeout(() => {
			if (!imgTop.value) {
				uni.hideLoading();
				util.Tips({
					title: '无法生成商品海报！'
				});
				return
			}
		}, 1000);
		uni.downloadFile({
			url: imgTop.value, //仅为示例，并非真实的资源
			success: (res) => {
				arrImagesUrlTop = res.tempFilePath;
				let arrImages = [posterbackgd.value, arrImagesUrlTop, PromotionCode.value];
				let storeName = productInfo.value.storeName;
				let price = productInfo.value.price;
				setTimeout(() => {
					util.PosterCanvas(arrImages, storeName, price, productInfo.value
						.otPrice,
						function(tempFilePath) {
							imagePath.value = tempFilePath;
							canvasStatus.value = true;
							uni.hideLoading();
						});
				}, 500);
			}
		});
	};
	// 图片预览；
	const getpreviewImage = () => {
		if (imagePath.value) {
			let photoList = [];
			photoList.push(imagePath.value)
			uni.previewImage({
				urls: photoList,
				current: imagePath.value
			});
		} else {
			util.Tips({
				title: '您的海报尚未生成'
			});
		}
	};
	/*
	 * 保存到手机相册
	 */
	// #ifdef MP
	const savePosterPath = () => {
		uni.getSetting({
			success(res) {
				if (!res.authSetting['scope.writePhotosAlbum']) {
					uni.authorize({
						scope: 'scope.writePhotosAlbum',
						success() {
							uni.saveImageToPhotosAlbum({
								filePath: imagePath.value,
								success: function(res) {
									posterImageClose();
									util.Tips({
										title: '保存成功',
										icon: 'success'
									});
								},
								fail: function(res) {
									util.Tips({
										title: '保存失败'
									});
								}
							})
						}
					})
				} else {
					uni.saveImageToPhotosAlbum({
						filePath: imagePath.value,
						success: function(res) {
							posterImageClose();
							util.Tips({
								title: '保存成功',
								icon: 'success'
							});
						},
						fail: function(res) {
							util.Tips({
								title: '保存失败'
							});
						},
					})
				}
			}
		})
	};
	// #endif
	const ShareInfo = () => {
		let data = productInfo.value;
		let href = location.href;
		// #ifdef H5
		if (proxy.$wechat.isWeixin()) {
			href = href.indexOf("?") === -1 ? href + "?spread=" + uid.value : href + "&spread=" + uid.value;
			let configAppMessage = {
				desc: app.globalData.companyName,
				title: data.storeName,
				link: href,
				imgUrl: data.image
			};
			proxy.$wechat.wechatEvevt([
				"updateAppMessageShareData",
				"updateTimelineShareData",
				"onMenuShareAppMessage",
				"onMenuShareTimeline"
			], configAppMessage).then(res => {
			}).catch(() => {})
		}
		// #endif
	};
	const showShare = (status) => {
		sharePacket.touchstart = status;
	};
	const hideNav = () => {
		currentPage.value = false;
	};
	//下拉导航页面跳转
	const linkPage = (url) => {
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
		currentPage.value = false;
	};
	//点击sku图片打开轮播图
	const showImg = (index) => {
		cusPreviewImg.value.open(selectSku.value.suk)
	};
	//滑动轮播图选择商品
	const changeSwitch = (e) => {
		let productSelect = skuArr.value[e];
		selectSku.value = productSelect;
		var skuList = productSelect.suk.split(',');
		skuList.forEach((i, index) => {
			attr.productAttr[index].index = skuList[index];
		})
		if (productSelect) {
			attr.productSelect.image = productSelect.image;
			attr.productSelect.price = productSelect.price;
			attr.productSelect.stock = productSelect.stock;
			// 后台传入的规格不展示时视为库存为0
			if (!productSelect.isShow) {
				attr.productSelect.stock = 0;
			}
			attr.productSelect.unique = productSelect.id;
			attrTxt.value = "已选择";
			attrValue.value = productSelect.suk;
		}
	};
	const getFileType = (fileName) => {
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
	};
	// 保障协议弹出框
	const handleToGgle = (type) => {
		if (type) {
			guaranteePopup.value.open()
		} else {
			guaranteePopup.value.close()
		}
	};
	// 获取suk小图
	const getSkuImage = () => {
		let sku = []
		let skuTable = []
		attr.productAttr.map((item) => {
			item.optionList.map(items => {
				if (items.image) sku.push(items.image)
			});
		})
		const uniqueData = sku.filter((item, index, self) =>
			index === self.findIndex((t) => t === item))

		if (uniqueData.length > 0) {
			skuImage.value = uniqueData
		} else {
			for (let key in productValue.value) {
				let obj = productValue.value[key];
				skuTable.push(obj.image);
			}
			skuImage.value = skuTable.filter((item, index, self) =>
				index === self.findIndex((t) => t === item))
		}
	};

	// --- Lifecycle Hooks ---
	onShow(() => {
		loadUserRelatedData();
	});

	onLoad((options) => {

		//用户从分享卡片进入的场景下获取主题色配置
		theme.value = Cache.get('theme');

		//判断顶部返回是否出现
		var pages = getCurrentPages();
		returnShow.value = pages.length === 1 ? false : true;
		if (pages.length <= 1) {
			retunTop.value = false
		}

		//页面中需要计算的一些值
		uni.getSystemInfo({
			success: function(res) {
				height.value = res.windowHeight
				//res.windowHeight:获取整个窗口高度为px，*2为rpx；98为头部占据的高度；
				// #ifndef APP-PLUS || H5 || MP-ALIPAY
				//navbarRight.value = res.windowWidth - uni.getMenuButtonBoundingClientRect().left;
				// #endif
			},
		});

		//获取浏览器中的参数，商品id video视频号商品，normal普通商品
		if (!options.scene && !options.id) {
			showSkeleton.value = false;
			util.Tips({
				title: '缺少参数无法查看商品'
			}, {
				url: '/pages/index/index'
			});
			return;
		}
		if (options.id) id.value = options.id;
		// 仅仅小程序扫码进入获取商品id，商品类型
		if (options.scene) {
			let qrCodeValue = util.getUrlParams(decodeURIComponent(options.scene));
			let mapeMpQrCodeValue = util.formatMpQrCodeData(qrCodeValue);
			app.globalData.spread = mapeMpQrCodeValue.spread;
			id.value = mapeMpQrCodeValue.id;
			type.value = mapeMpQrCodeValue.type ? mapeMpQrCodeValue.type : 'normal';
		}
		if (options.type === 'video') {
			// #ifdef MP
			navH.value = 160;
			// #endif
			type.value = options.type
		} else {
			type.value = 'normal'
			navH.value = app.globalData.navHeight;
		}
		// 商品类型vuex存储
		useAppStore().PRODUCT_TYPE(type.value);
		// 客服配置
		Object.assign(chatConfig, Cache.getItem('chatConfig') || {});
		// #ifdef H5
		computeUser();
		// #endif

		if (options.spread) {
			Cache.set('spread', options.spread);
			app.globalData.spread = options.spread;
		}

		getDiyData(); //商品详情主题配置
		getGoodsDetails(); //商品详情
		getCouponType(); //获取默认的 优惠券类型
		getProductReplyList(); //评论列表
		getProductReplyCount(); //评论条数
		getGoods(); //优品推荐
		//主题色颜色配置
		indicatorBg.value = setThemeColor();
	});

	onReady(() => {
		isNodes.value++;
		nextTick(() => {
			// #ifdef MP
			const menuButton = uni.getMenuButtonBoundingClientRect();
			const query = uni.createSelectorQuery();
				query
					.select('#home')
					.boundingClientRect(data => {
						homeTop.value = menuButton.top * 2 + menuButton.height - (data ? data.height : 0);
					})
					.exec();
			// #endif
			// #ifdef APP-PLUS
			homeTop.value = 60;
			// #endif
		});
	});

	/**
	 * 用户点击右上角分享
	 */
	// #ifdef MP
	onShareAppMessage((res) => {
		actionSheetHidden.value = !actionSheetHidden.value;
		return {
			title: productInfo.value.storeName || '',
			imageUrl: productInfo.value.image || '',
			path: '/pages/goods/goods_details/index?id=' + id.value + '&spread=' + uid.value,
		}
	});
	// #endif

	onReachBottom(() => {
		getCouponList(couponType.value);
	});

	// 滚动监听
	onPageScroll((e) => {
		// 传入scrollTop值并触发所有easy-loadimage组件下的滚动监听事件
		uni.$emit('scroll');
	});
</script>


<style scoped lang="scss">
	.lang {
		width: 170rpx !important;
		height: 60rpx !important;
		border-radius: 33rpx;
	}

	.circle {
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
		border: 1px solid rgba(0, 0, 0, 0.07);
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

	.product-con .attribute {
		padding: 0;

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

	.product-con .legacy-product-footer {
		position: fixed;
		bottom: 0;
		width: 100%;
		box-sizing: border-box;
		background-color: #fff;
		z-index: 277;
		border-top: 1rpx solid #f0f0f0;
		height: 100rpx;
		height: calc(100rpx + constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		height: calc(100rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
		flex-wrap: nowrap;
	}

	.product-con .legacy-product-footer .item {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		font-size: 18rpx;
		color: #666;
		margin-right: 30rpx;
		flex-shrink: 0;
		line-height: 24rpx;
	}

	.product-con .legacy-product-footer .item .iconfont {
		text-align: center;
		font-size: 34rpx;
		line-height: 40rpx;
	}

	.product-con .legacy-product-footer .item .iconfont.icon-shoucang1 {
		@include main_color(theme);
	}

	.product-con .legacy-product-footer .item .iconfont.icon-gouwuche1 {
		font-size: 40rpx;
		position: relative;
	}

	.product-con .legacy-product-footer .item .iconfont.icon-gouwuche1 .num {
		color: #fff;
		position: absolute;
		font-size: 18rpx;
		width: 28rpx;
		height: 28rpx;
		padding: 0;
		border-radius: 50%;
		line-height: 28rpx;
		text-align: center;
		box-sizing: border-box;
		top: -12rpx;
		right: -16rpx;
	}

	.product-con .legacy-product-footer .bnt {
		flex: 1;
		min-width: 0;
		height: 76rpx;
		flex-wrap: nowrap;
	}

	.product-con .legacy-product-footer .bnt .bnts {
		width: 100%;
		text-align: center;
		line-height: 76rpx;
		color: #fff;
		font-size: 28rpx;
		border-radius: 50rpx;
	}

	.product-con .legacy-product-footer .bnt form {
		flex: 1;
		min-width: 0;
	}

	.product-con .legacy-product-footer .bnt .joinCart {
		margin-right: 20rpx;
		@include left_color(theme);
	}

	.product-con .legacy-product-footer .bnt .buy {
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
		color: j7addc;
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

	.share-icon-box {
		position: relative;

		.share-icon {
			position: absolute;
			right: -15rpx;
			top: 20rpx;
		}
	}

	.share-introduce {
		padding-right: 16rpx;
	}

	.ensure {
		width: 100%;
		background-color: #fff;
		border-top-left-radius: 40rpx;
		border-top-right-radius: 40rpx;
		padding-bottom: 22rpx;
		padding-top: 38rpx;
		padding-bottom: calc(22rpx + constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
		padding-bottom: calc(22rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/

		.title {
			font-size: 32rpx;
			color: #282828;
			text-align: center;
			margin: 0 0 36rpx 0;
			position: relative;

			.close-box {
				position: absolute;
				right: 30rpx;
				top: 8rpx;
			}
		}

		.list {
			max-height: 60vh;
			margin: 0 30rpx 20rpx;
			overflow: scroll;

			.item {
				margin-bottom: 40rpx;
				flex-wrap: nowrap;

				.pictrue {
					width: 36rpx;
					height: 36rpx;
					border-radius: 50%;
					margin-right: 30rpx;

					::v-deep image,
					.easy-loadimage,
					image,
					uni-image {
						width: 100%;
						height: 100%;
						border-radius: 50%;
					}
				}

				.text {
					// width: 618rpx;
					color: #999999;
					font-size: 28rpx;

					.name {
						color: #333333;
						font-weight: bold;
						margin-bottom: 20rpx;
					}
				}
			}
		}

		.bnt,
		.activityBtn {
			width: 690rpx;
			height: 86rpx;
			text-align: center;
			line-height: 86rpx;
			border-radius: 43rpx;
			font-size: 30rpx;
			color: #fff;
			margin: 0 auto;
		}

		.icon-gou1 {
			@include main_color(theme);
		}

		.bnt {
			@include main_bg_color(theme);
		}
	}

	.guarantee-pop-box {
		position: fixed;
		bottom: 0;
		z-index: 999;
	}

	.ensure.on {
		transform: translate3d(0, 0, 0);
	}
</style>
