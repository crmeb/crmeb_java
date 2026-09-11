<template>
  <view
    class="new-users copy-data"
    :class="
      bgTabVal == 2
        ? 'fullsize noRepeat'
        : bgTabVal == 1
          ? 'repeat ysize'
          : 'noRepeat ysize'
    "
    :style="{
      height: pageHeight,
      backgroundColor: bgColor,
      backgroundImage: bgPic ? `url(${bgPic})` : '',
    }"
  >
    <view class="top" :style="colorStyle">
      <!-- #ifdef MP || APP-PLUS -->
      <view class="sys-head">
        <view class="sys-bar" :style="{ height: sysHeight }"></view>
        <!-- #ifdef MP -->
        <view
          class="sys-title"
          :style="member_style == 3 ? 'color:#333' : ''"
          >个人中心</view
        >
        <!-- #endif -->
        <view
          class="bg"
          :style="member_style == 3 ? 'background:#f5f5f5' : ''"
        ></view>
      </view>
      <!-- #endif -->
    </view>

    <PageDesign
      :style="colorStyle"
      :diyData="currentDiyData"
      :isHome="false"
      :isScrolled="isScrolled"
      :isFixed="isFixed"
      :belongIndex="belongIndex"
      @bindSortId="bindSortId"
      @bindHeight="bindHeighta"
      @storeTap="storeTap"
      @changeLogin="changeLogin"
      @changeBarg="changeBarg"
      @goDetail="goDetail"
    ></PageDesign>
    <image :src="copyRightPic" alt="" class="support"></image>
    <pageFooter :style="colorStyle"></pageFooter>
  </view>
</template>
<script setup>
let sysHeight = uni.getWindowInfo().statusBarHeight + "px";
import { ref, computed, getCurrentInstance } from "vue";
import { onLoad, onReady, onShow, onPullDownRefresh, onPageScroll } from "@dcloudio/uni-app";
import { getUserInfo, mpBindingPhone } from "@/api/user.js";
import { getThemeInfo, loginConfigApi } from "@/api/api.js";
import { wechatAuthV2, silenceAuth } from "@/api/public.js";
import { toLogin } from "@/libs/login.js";
// #ifdef H5
import Auth from "@/libs/wechat.js";
// #endif
const app = getApp();
import Routine from "@/libs/routine.js";
import { useColor } from "@/composables/useColor.js";
import pageFooter from "@/components/pageFooter/index.vue";
import { getCustomer } from "@/utils/index.js";
import editUserModal from "@/components/eidtUserModal/index.vue";
import couponWindow from "@/components/couponWindow/index.vue";
import waterfallsFlow from "@/components/WaterfallsFlow/WaterfallsFlow.vue";
import emptyPage from "@/components/emptyPage.vue";
import Loading from "@/components/Loading/index.vue";
import { goShopDetail } from "@/libs/order.js";
import PageDesign from "@/subpackage/diyComponents/pageDesign.vue";
import { applyTheme } from "@/utils/theme.js";
import util from "@/utils/util.js";
import Cache from "@/utils/cache.js";
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from "pinia";
import copyRightPicSrc from "@/static/images/support.png";

const { proxy } = getCurrentInstance();
const appStore = useAppStore();
const { isLogin, cartNum, uid } = storeToRefs(appStore);
const { colorStyle } = useColor();

// filters 转函数（Vue3 移除了 filters）
function coundTime(val) {
  var setTime = val * 1000;
  var nowTime = new Date();
  var rest = setTime - nowTime.getTime();
  var day = parseInt(rest / (60 * 60 * 24 * 1000));
  return day + proxy.$t("day");
}

function padTime(value) {
  return String(value).padStart(2, "0");
}

function dateFormat(value) {
  const date = new Date(Number(value) * 1000);
  if (Number.isNaN(date.getTime())) return "";
  return `${date.getFullYear()}-${padTime(date.getMonth() + 1)}-${padTime(date.getDate())}`;
}

// data
const currentDiyData = ref({});
const imgUrls = ref([]);
const autoplay = ref(true);
const circular = ref(true);
const interval = ref(3000);
const duration = ref(500);
const isAuto = ref(false);
const isShowAuth = ref(false);
const orderStatusNum = ref({});
const userInfo = ref({});
const mpHeight = ref(0);
const showStatus = ref(1);
const activeRouter = ref("");
// #ifdef H5 || MP
const pageHeight = ref("100%");
const routineContact = ref(0);
// #endif
// #ifdef APP-PLUS
const pageHeight = ref(app.globalData.windowHeight);
// #endif
// #ifdef H5
const isWeixin = ref(Auth.isWeixin());
// #endif
const footerSee = ref(false);
const member_style = ref(0);
const is_diy = ref(uni.getStorageSync("is_diy"));
const copyRightPic = ref(copyRightPicSrc);
const belongIndex = ref(0);
const isScrolled = ref(false);
const isFixed = ref(false);
const product_video_status = ref(false);
const positionTop = ref(0);
const sortMpTop = ref(0);
const sortList = ref([]);
const curSort = ref(0);
const goodList = ref([]);
const loaded = ref(false);
const loading = ref(false);
const isCouponShow = ref(false);
const couponObj = ref({});
const site_config = ref("");
const configData = ref({});
const isFooter = ref(false);
const entryData = ref({ select_store_id: "", store_id: "" });
const sid = ref(0);
const goodPage = ref(1);
const bgColor = ref("");
const bgPic = ref("");
const bgTabVal = ref("");

// computed
const pdHeights = computed(() => ({ height: "100rpx" }));

// lifecycle
onLoad((option) => {
  uni.hideTabBar();
  applyTheme(uni.getStorageSync("previewThemeId") || "").catch(() => {});
  loginConfigApi()
    .then((res) => {
      const companyImage = (res.data || {}).companyImage;
      if (companyImage) copyRightPic.value = companyImage;
    })
    .catch(() => {});
  // #ifdef MP
  if (!isLogin.value) {
    // 小程序静默授权（已注释）
  }
  // #endif
  // #ifdef H5 || APP-PLUS
  let cacheCode = Cache.get("snsapi_userinfo_code");
  let res1 = cacheCode ? option.code != cacheCode : true;
  // #ifdef H5
  if (isWeixin.value && option.code && res1 && option.scope === "snsapi_userinfo") {
    Cache.set("snsapi_userinfo_code", option.code);
    Auth.auth(option.code).then(() => { getUserInfoFn(); }).catch(() => {});
  }
  // #endif
  // #endif
  // #ifdef APP-PLUS
  pageHeight.value = app.globalData.windowHeight;
  // #endif
  let routes = getCurrentPages();
  let curRoute = routes[routes.length - 1].route;
  activeRouter.value = "/" + curRoute;
});

onReady(() => {
  // #ifdef MP
  let info = uni.createSelectorQuery().select(".sys-head");
  info.boundingClientRect(function(data) {
    mpHeight.value = data.height;
  }).exec();
  // #endif
});

onShow(() => {
  // #ifdef APP-PLUS
  uni.getSystemInfo({
    success: function(res) { pageHeight.value = res.windowHeight + "px"; },
  });
  // #endif
  if (isLogin.value) getUserInfoFn();
  getDiyData();
});

onPullDownRefresh(() => { onLoadFun(); });
onPageScroll(() => { uni.$emit("scroll"); });

// methods
function bindSortId(item, data) {
  if (item.dataType.tabVal == 1) {
    uni.navigateTo({ url: `/pages/goods/goods_list/index?cid=${item.classPage.id}&title=${item.classPage.name}` });
  } else if (item.text.val == "首页") {
    uni.switchTab({ url: `/pages/index/index` });
  } else {
    uni.navigateTo({ url: `/pages/activity/small_page/index?micro_id=${item.microPage.id}` });
  }
}

function storeTap(id) {
  entryData.value.select_store_id = id;
  entryData.value.store_id = "";
  uni.removeStorageSync("rulesStoreId");
}

function bindHeighta(data) {
  // #ifdef APP-PLUS
  sortMpTop.value = data.top + data.height;
  // #endif
}

function changeSort(item, index) {
  if (curSort.value == index) return;
  curSort.value = index;
  sid.value = item.id;
  goodList.value = [];
  goodPage.value = 1;
  loaded.value = false;
}

function goDetail(item) {
  goShopDetail(item, uid.value).then(() => {
    uni.navigateTo({ url: `/pages/goods/goods_details/index?id=${item.id}` });
  });
}

function couponClose() {
  isCouponShow.value = false;
}

function goICP(url) {
  // #ifdef H5
  window.open(url);
  // #endif
  // #ifdef MP
  uni.navigateTo({ url: `/pages/annex/web_view/index?url=${url}` });
  // #endif
}

function changeBarg(item) {
  if (!isLogin.value) toLogin();
  else uni.navigateTo({ url: `/pages/activity/goods_bargain_details/index?id=${item.id}&spid=${appStore.uid}` });
}

function changeLogin() {
  if (!isLogin.value) toLogin(true);
}

function objToArr(data) {
  if (!data || typeof data !== "object") return [];
  return Object.keys(data).sort().map((key) => data[key]);
}

function getDiyData() {
  let previewThemeId = uni.getStorageSync("previewThemeId");
  let data = {};
  if (previewThemeId) data.theme_id = previewThemeId;
  getThemeInfo("user", data).then((res) => {
    currentDiyData.value = res.data;
    if (currentDiyData.value.is_bg_color) bgColor.value = currentDiyData.value.color_picker || "";
    if (currentDiyData.value.is_bg_pic) {
      bgPic.value = currentDiyData.value.bg_pic || "";
      bgTabVal.value = currentDiyData.value.bg_tab_val || "";
    }
  });
}

function getWechatuserinfo() {
  //#ifdef H5
  Auth.isWeixin() && Auth.toAuth("snsapi_userinfo", "/pages/user/index");
  //#endif
}

function openAuto() { toLogin(); }

function onLoadFun() {
  getUserInfoFn();
  getDiyData();
}

function Setting() {
  uni.openSetting({ success: function() {} });
}

function authColse(e) {
  isShowAuth.value = e;
}

function bindPhone() {
  uni.navigateTo({ url: "/pages/users/user_phone/index" });
}

function getphonenumber(e) {
  if (e.detail.errMsg == "getPhoneNumber:ok") {
    Routine.getCode().then((code) => {
      let data = { code, iv: e.detail.iv, encryptedData: e.detail.encryptedData };
      mpBindingPhone(data).then((res) => {
        getUserInfoFn();
        util.Tips({ title: res.msg, icon: "success" });
      }).catch((err) => {
        return util.Tips({ title: err });
      });
    }).catch(() => { uni.hideLoading(); });
  }
}

/**
 * 获取个人用户信息
 */
function getUserInfoFn() {
  getUserInfo().then((res) => {
    userInfo.value = res.data;
    appStore.UPDATE_USERINFO(res.data);
    appStore.SETUID(res.data.uid);
    uni.stopPullDownRefresh();
  });
}

function getUserProfile() { toLogin(); }
</script>

<style lang="scss">
.new-users {
  padding-bottom: 100rpx;
  .sys-head {
    position: relative;
    width: 100%;
    background: #fff;
    z-index: 50;

    .sys-bar {
      width: 100%;
    }

    .sys-title {
      width: 100%;
      height: 43px;
      line-height: 43px;
      text-align: center;
      font-size: 32rpx;
      color: #000;
      font-weight: 500;
      position: relative;
      z-index: 10;
    }

    .bg {
      position: absolute;
      left: 0;
      top: 0;
      width: 100%;
      height: 100%;
      z-index: 5;
    }
  }
  .support {
    width: 219rpx;
    height: 74rpx;
    margin: 54rpx auto;
    display: block;
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
</style>
