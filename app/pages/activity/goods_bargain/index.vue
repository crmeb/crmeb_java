<template>
	<view :data-theme="theme" :style="colorStyle">
		<view class="pageInfo">
			<skeleton :show="showSkeleton" :isNodes="isNodes" ref="skeleton" loading="chiaroscuro" selector="skeleton"
				></skeleton>
			<view class='bargain-list skeleton' :style="{visibility: showSkeleton ? 'hidden' : 'visible'}">
				<!-- #ifdef H5 -->
				<view class='iconfont icon-xiangzuo' @tap='goBack' :style="'top:'+ (navH/2) +'rpx'" v-if="returnShow">
				</view>
				<!-- #endif -->
				<view class='header skeleton-rect' :style="{backgroundImage:'url('+imgHost+ '/' + backBg+')'}" v-show="imgHost">
					<view class="pic">
						<view class='swipers skeleton-rect'>
							<swiper :indicator-dots="indicatorDots" :autoplay="autoplay" interval="2500" duration="500" vertical="true"
							 circular="true">
								<block v-for="(item,index) in bargainSuccessList" :key='index'>
									<swiper-item >
										<view class="acea-row row-middle">
											<image :src="item.avatar" class="mr9"></image>
											<view class='mr9 nickName'>{{$util.formatName(item.nickName)}}</view>
											<text class='mr9'>拿了</text>
											<view class='line1'>{{item.title}}</view>
										</view>
									</swiper-item>
								</block>
							</swiper>
						</view>
					</view>
					<view class="tit" v-show="bargainTotal > 0">已有{{bargainTotal}}人砍成功</view>
				</view>
				<view class='list' v-if="bargainList.length">
					<block v-for="(item,index) in bargainList" :key="index">
						<view class='item acea-row row-between-wrapper'
							@tap="openSubscribe('/pages/activity/goods_bargain_details/index?id='+ item.id +'&startBargainUid='+ uid)">
							<view class='pictrue skeleton-rect'>
								<image :src='item.image'></image>
							</view>
							<view class='text acea-row row-column-around'>
								<view class='name line2 skeleton-rect'>{{item.title}}</view>
								<view v-if="item.quota>0" class="acea-row skeleton-rect" style="margin-bottom: 14rpx;">
									<countDown :tipText="' '" :bgColor="bgColor" :dayText="':'" :hourText="':'"
										:minuteText="':'" :secondText="' '" :datatime="item.stopTime/1000" :isDay="true"
										></countDown>
									<text class="txt">后结束</text>
								</view>
								<view v-if="new Date().getTime()- item.stopTime >=0">
									<view style="font-size: 22rpx;" @tap='currentBargainUser'>已结束</view>
								</view>
								<view v-if="item.quota==0">
									<view style="font-size: 22rpx;" @tap='currentBargainUser'>已售罄</view>
								</view>
								<!-- <view class='num'><text class='iconfont icon-pintuan'></text>{{item.countPeopleAll}}人正在参与
								</view> -->
								<view class='money skeleton-rect'>最低: ￥<text :style="[{'font-size':item.minPrice.length>=7?'32rpx':'38rpx'}]" class='price'>{{item.minPrice}}</text></view>
							</view>
							<view v-if="item.quota>0" class='cutBnt bg_color'>参与砍价</view>
							<view  v-if="item.quota==0" class='cutBnt bg-color-hui'>已售罄</view>
						</view>
					</block>
					<view class='loadingicon acea-row row-center-wrapper' v-if='bargainList.length > 0' style="color: #fff;">
						<text class='loading iconfont icon-jiazai' :hidden='loading==false'></text>{{loadTitle}}
					</view>
				</view>
				<view v-else class="flex-center no_shop">
					<image :src="urlDomain + '/crmebimage/perset/staticImg/noShopper.png'" mode="aspectFit" style="width: 400rpx;"></image>
				</view>
			</view>
		</view>
	</view>
</template>
<script setup>
import { ref, watch, getCurrentInstance } from "vue";
import { onLoad, onReachBottom } from "@dcloudio/uni-app";
const app = getApp();
import { getBargainList, bargainHeaderApi } from "@/api/activity.js";
import { openBargainSubscribe } from "@/utils/SubscribeMessage.js";
import countDown from "@/pages/activity/components/countDown/index.vue";
import { toLogin } from "@/libs/login.js";
import animationType from "@/utils/animationType.js";
import util from "@/utils/util.js";
import Cache from "@/utils/cache.js";
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from "pinia";
import { useColor } from '@/composables/useColor.js';

const { proxy } = getCurrentInstance();
const appStore = useAppStore();
const { isLogin, uid } = storeToRefs(appStore);

const urlDomain = ref(Cache.get("imgHost"));
const showSkeleton = ref(true);
const isNodes = ref(0);
const bgColor = ref({ bgColor: "#E93323", Color: "#fff", width: "44rpx", timeTxtwidth: "16rpx", isDay: true });
const bargainList = ref([]);
const page = ref(1);
const limit = ref(10);
const loading = ref(false);
const loadend = ref(false);
const navH = ref("");
const isAuto = ref(false);
const isShowAuth = ref(false);
const returnShow = ref(true);
const loadTitle = ref("加载更多");
const bargainSuccessList = ref([]);
const bargainTotal = ref(0);
const indicatorDots = ref(false);
const autoplay = ref(true);
const theme = ref(app.globalData.theme);
const { colorStyle } = useColor();
const imgHost = ref("");
const backBg = ref("crmebimage/perset/bargain_header/bargain_header1.jpg");
const navBgColor = ref("#e93323");

watch(isLogin, (newV) => {
  if (newV) {
    getBargainListFn();
    getBargainHeader();
  }
}, { deep: true });

onLoad((options) => {
  imgHost.value = Cache.get("imgHost");
  switch (app.globalData.theme) {
    case "theme1": backBg.value = "crmebimage/perset/bargain_header/bargain_header1.jpg"; bgColor.value.bgColor = "#e93323"; navBgColor.value = "#e93323"; break;
    case "theme2": backBg.value = "crmebimage/perset/bargain_header/bargain_header2.jpg"; bgColor.value.bgColor = "#FE5C2D"; navBgColor.value = "#FE5C2D"; break;
    case "theme3": backBg.value = "crmebimage/perset/bargain_header/bargain_header3.jpg"; bgColor.value.bgColor = "#42CA4D"; navBgColor.value = "#42CA4D"; break;
    case "theme4": backBg.value = "crmebimage/perset/bargain_header/bargain_header4.jpg"; bgColor.value.bgColor = "#1DB0FC"; navBgColor.value = "#1DB0FC"; break;
    case "theme5": backBg.value = "crmebimage/perset/bargain_header/bargain_header5.jpg"; bgColor.value.bgColor = "#FF448F"; navBgColor.value = "#FF448F"; break;
  }
  uni.setNavigationBarColor({ frontColor: "#ffffff", backgroundColor: navBgColor.value });
  setTimeout(() => { isNodes.value++; }, 500);
  var pages = getCurrentPages();
  returnShow.value = pages.length === 1 ? false : true;
  uni.setNavigationBarTitle({ title: "砍价列表" });
  navH.value = app.globalData.navHeight;
  getBargainListFn();
  getBargainHeader();
});

onReachBottom(() => { getBargainListFn(); });

function getBargainHeader() {
  bargainHeaderApi().then((res) => {
    bargainTotal.value = res.data.bargainTotal;
    bargainSuccessList.value = res.data.bargainSuccessList;
  }).catch((err) => { return util.Tips({ title: err }); });
}

function authColse(e) { isShowAuth.value = e; }
function goBack() { uni.switchTab({ url: "/pages/index/index" }); }

function openSubscribe(e) {
  let p = e;
  // #ifndef MP
  uni.navigateTo({ animationType: animationType.type, animationDuration: animationType.duration, url: p });
  // #endif
  // #ifdef MP
  uni.showLoading({ title: "正在加载" });
  openBargainSubscribe().then(() => {
    uni.hideLoading();
    uni.navigateTo({ url: p });
  }).catch(() => { uni.hideLoading(); });
  // #endif
}

function getBargainListFn() {
  if (loadend.value) return;
  if (loading.value) return;
  loading.value = true;
  loadTitle.value = "";
  getBargainList({ page: page.value, limit: limit.value })
    .then(function (res) {
      let list = res.data.list;
      let bl = util.SplitArray(list, bargainList.value);
      let isEnd = list.length < limit.value;
      loadend.value = isEnd;
      loading.value = false;
      // #ifdef H5
      setShare();
      // #endif
      loadTitle.value = isEnd ? "已全部加载" : "加载更多";
      bargainList.value = bl;
      page.value++;
      setTimeout(() => { showSkeleton.value = false; }, 1000);
    })
    .catch(() => {
      loading.value = false;
      loadTitle.value = "加载更多";
    });
}

// #ifdef H5
function setShare() {
  proxy.$wechat.isWeixin() &&
    proxy.$wechat.wechatEvevt(
      ["updateAppMessageShareData", "updateTimelineShareData", "onMenuShareAppMessage", "onMenuShareTimeline"],
      {
        desc: bargainList.value[0].title,
        title: bargainList.value[0].title,
        link: location.href,
        imgUrl: bargainList.value[0].image,
      }
    ).then(() => {}).catch(() => {});
}
// #endif
</script>

<style lang="scss">
	page {
		@include main_bg_color(theme);
		min-height: 100%;
	}
</style>
<style lang="scss" scoped>
	.pageInfo{
		/* #ifdef MP || APP-PLUS */
		@include main_bg_color(theme);
		/* #endif */
	}
     .mr9{
		 margin-right: 9rpx;
	 }
	.swipers {
		height: 100%;
		width: 76%;
		margin: auto;
		overflow: hidden;
		font-size: 22rpx;
		color: #fff;
        image{
			width: 24rpx;
			height: 24rpx;
			border-radius: 50%;
			overflow: hidden;
		}
		swiper {
			height: 100%;
			width: 100%;
			overflow: hidden;
		}
		.line1{
			width: 195rpx;
		}
	}

	.bargain-list .icon-xiangzuo {
		font-size: 40rpx;
		color: #fff;
		position: fixed;
		left: 30rpx;
		z-index: 99;
		transform: translateY(-20%);
	}

	.bargain-list .header {
		// @include bargain-list-header(theme);
		babackground-repeat: no-repeat;
		background-size: 100% 100%;
		width: 750rpx;
		height: 420rpx;
        .acea-row{
        	height: 50rpx;
			line-height: 50rpx;
			left: 50rpx;
			.nickName{
				width: 65rpx;
				overflow: hidden;
				white-space: nowrap;
			}
        }
		.pic {
			width: 478rpx;
			height: 50rpx;
			margin: 0 auto;
		}

		.tit {
			color: #FFFFFF;
			font-size: 24rpx;
			font-weight: 400;
			text-align: center;
			margin-top: 304rpx;
		}
	}

	.bargain-list .list {
		padding: 0 30rpx;
	}

	.bargain-list .list .item {
		position: relative;
		height: 250rpx;
		background-color: #fff;
		border-radius: 14rpx;
		margin-bottom: 20rpx;
		padding: 30rpx 25rpx;
	}

	.bargain-list .list .item .pictrue {
		width: 190rpx;
		height: 190rpx;
	}

	.bargain-list .list .item .pictrue image {
		width: 100%;
		height: 100%;
		border-radius: 14rpx;
	}

	.bargain-list .list .item .text {
		width: 432rpx;
		font-size: 28rpx;
		color: #333333;
		
		.txt{
			font-size: 22rpx;
			margin-left: 4rpx;
			color: #666666;
			line-height: 36rpx;
		}
	}

	.bargain-list .list .item .text .name {
		width: 100%;
		height: 68rpx;
		line-height: 36rpx;
		font-size: 28rpx;
		margin-bottom: 26rpx;
	}

	.bargain-list .list .item .text .num {
		font-size: 26rpx;
		color: #999;
	}

	.bargain-list .list .item .text .num .iconfont {
		font-size: 35rpx;
		margin-right: 7rpx;
	}

	.bargain-list .list .item .text .money {
		font-size: 24rpx;
		font-weight: bold;
		@include price_color(theme);
	}

	.bargain-list .list .item .text .money .price {
		font-size: 38rpx;
	}

	.bargain-list .list .item .cutBnt {
		position: absolute;
		width: 162rpx;
		height: 52rpx;
		border-radius: 50rpx;
		font-size: 24rpx;
		color: #fff;
		text-align: center;
		line-height: 52rpx;
		right: 24rpx;
		bottom: 30rpx;
	}
	.bg_color{
		@include linear-gradient(theme);
	}
	.bg-color-hui{
		background-color: #CDCBCC;
	}
	.bargain-list .list .item .cutBnt .iconfont {
		margin-right: 8rpx;
		font-size: 30rpx;
	}

	.bargain-list .list .load {
		font-size: 24rpx;
		height: 85rpx;
		text-align: center;
		line-height: 85rpx;
	}
	.flex-center{
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.no_shop{
		margin: 0 30rpx 0;
		height: 700rpx;
		border-radius: 10rpx;
		background-color: #fff;
	}
	.bargain-list{
		min-height: 100vh;
	}
</style>
