<template>
  <!-- 首页 -->
  <view
    v-if="pageShow"
    class="page"
    :class="
      bgTabVal == 2
        ? 'fullsize noRepeat'
        : bgTabVal == 1
        ? 'repeat ysize'
        : 'noRepeat ysize'
    "
    :style="{
      backgroundColor: bgColor,
      backgroundImage: bgPic ? `url(${bgPic})` : '',
      minHeight: windowHeight + 'px',
    }"
  >
    <PageDesign
      :style="colorStyle"
      :diyData="currentDiyData"
      :isHome="true"
      :isScrolled="isScrolled"
      :isFixed="isFixed"
      :productVideoStatus="confirm_video_status"
      :belongIndex="belongIndex"
      :errorNetwork="errorNetwork"
      @bindSortId="bindSortId"
      @bindHeight="bindHeighta"
      @storeTap="storeTap"
      @changeLogin="changeLogin"
      @changeBarg="changeBarg"
      @newDataStatus="newDataStatus"
      @reconnect="reconnect"
    >
      <template #bottom>
        <!-- 分类商品模块 -->
        <view
          class="sort-product px-20"
          :style="{ marginTop: sortMpTop + 'px' }"
          v-if="!styleConfig.length"
        >
          <view
            class="rd-24rpx bg--w111-fff p-24 mb-24"
            v-if="sortList.children && sortList.children.length"
          >
            <scroll-view
              scroll-x="true"
              class="white-nowrap vertical-middle w-full"
              show-scrollbar="false"
            >
              <view
                class="inline-block mr-24"
                v-for="(item, index) in sortList.children"
                :key="index"
              >
                <view
                  class="flex-col flex-center"
                  @tap="changeSort(item, index)"
                >
                  <view
                    class="picture w-90 h-90 rd-50-p111-"
                    :class="{ select: curSort == index }"
                  >
                    <image
                      :src="item.pic"
                      class="w-full h-full rd-50-p111-"
                    ></image>
                  </view>
                  <text
                    class="fs-24 pt-14"
                    :class="{ 'font-num': curSort == index }"
                    >{{ item.cate_name }}</text
                  >
                </view>
              </view>
            </scroll-view>
          </view>
          <waterfallsFlow
            ref="waterfallsFlowRef"
            :wfList="goodList"
            :goDetail="'goDetail'"
            @itemTap="goDetail"
          ></waterfallsFlow>
          <Loading :loaded="loaded" :loading="loading"></Loading>
          <view v-if="goodList.length == 0 && loaded">
            <emptyPage title="暂无商品，去看点别的吧～"></emptyPage>
          </view>
        </view>
        <view class="">
          {{ site_config }}
        </view>
        <!-- #ifndef APP-PLUS -->
        <view
          v-if="configData && configData.record_No"
          class="site-config"
          @click="goICP(configData.icp_url)"
          >{{ configData.record_No }}</view
        >
        <view
          class="site-config"
          v-if="configData && configData.network_security"
          @click="goICP(configData.network_security_url)"
        >
          <image class="ban" src="/static/images/beian.png" alt="" srcset="" />
          {{ configData.network_security }}
        </view>
        <!-- #endif -->
      </template>
    </PageDesign>
    <!-- #ifdef APP -->
    <app-update ref="appUpdate" :force="true" :tabbar="false"></app-update>
    <!-- #endif -->
    <view v-if="isPreview" class="exit-preview" @click="exitPreview">
      退出预览
    </view>
  </view>
</template>

<script setup>
const app = getApp();
import { ref, computed, nextTick, getCurrentInstance, onMounted } from "vue";
import { onLoad, onShow, onUnload, onPullDownRefresh, onReachBottom, onPageScroll } from "@dcloudio/uni-app";
import { useColor } from "@/composables/useColor.js";
import { getShare } from "@/api/public.js";
import waterfallsFlow from "@/components/WaterfallsFlow/WaterfallsFlow.vue";
import emptyPage from "@/components/emptyPage.vue";
// #ifdef MP
import { getTempIds as getTempIdsApi } from "@/api/api.js";
import { SUBSCRIBE_MESSAGE } from "@/config/cache.js";
import { cacheSubscribeTemplateIds } from "@/utils/SubscribeMessage.js";
// #endif
import { getDiy, getDiyVersion, getThemeInfo } from "@/api/api.js";
import { getCartCounts } from "@/api/order.js";
import { getCategoryList, getProductslist } from "@/api/store.js";
import { goShopDetail } from "@/libs/order.js";
import { toLogin } from "@/libs/login.js";
import { HTTP_REQUEST_URL } from "@/config/app.js";
import configs from "@/config/app.js";
import Loading from "@/components/Loading/index.vue";
import Cache from "@/utils/cache.js";
import util from "@/utils/util.js";
import appUpdate from "@/components/update/app-update.vue";
import { applyTheme } from "@/utils/theme.js";
import PageDesign from "@/subpackage/diyComponents/pageDesign.vue";
import { useAppStore } from "@/store/app.js";
import { useIndexDataStore } from "@/store/indexData.js";
import { storeToRefs } from "pinia";

const { proxy } = getCurrentInstance();
const appStore = useAppStore();
const indexDataStore = useIndexDataStore();
const { isLogin, uid, cartNum } = storeToRefs(appStore);
const { colorStyle } = useColor();

// 子组件引用
const waterfallsFlowRef = ref(null);
const appUpdateRef = ref(null);

// data
const styleConfig = ref([]);
const loading = ref(false);
const loadend = ref(false);
const loadTitle = ref("下拉加载更多");
const page = ref(1);
const limit = ref(configs.LIMIT);
const numConfig = ref(0);
const code = ref("");
const shareInfo = ref({});
const sortList = ref("");
const sortAll = ref([]);
const goodPage = ref(1);
const goodList = ref([]);
const sid = ref(0);
const curSort = ref(0);
const sortMpTop = ref(0);
const loaded = ref(false);
const domOffsetTop = ref(50);
// #ifdef APP-PLUS || MP
const isFixed = ref(true);
// #endif
// #ifdef H5
const isFixed = ref(false);
// #endif
const site_config = ref("");
const errorNetwork = ref(false);
const isHeaderSerch = ref(false);
const showHomeComb = ref(false);
const showCateNav = ref(false);
const homeCombData = ref({});
const headerSerchCombData = ref({});
const cateNavData = ref({});
const footerConfigData = ref({});
const bgColor = ref("");
const bgPic = ref("");
const bgTabVal = ref("");
const pageShow = ref(true);
const windowHeight = ref(0);
const imgHost = ref(HTTP_REQUEST_URL);
const isShowAuth = ref(false);
const isScrolled = ref(false);
const product_video_status = ref(false);
const confirm_video_status = ref(false);
const positionTop = ref(0);
const isFooter = ref(false);
const pdHeight = ref(0);
const entryData = ref({ store_id: "", latitude: "", longitude: "", select_store_id: "" });
const goodsIndex = ref([]);
const promotionIndex = ref([]);
const belongIndex = ref(0);
const isBelongStore = ref(false);
const getHeight = ref(util.getWXStatusHeight());
const myApplet = ref(true);
const configData = ref(Cache.get("BASIC_CONFIG"));
const currentDiyData = ref({});
const isPreview = ref(false);
const themeId = ref(0);

// computed
// #ifdef MP
const appletStyle = computed(() => ({
  top: getHeight.value.menuButtonInfo.bottom + 8 + "px",
  right: "10px",
}));
// #endif
const pageStyle = computed(() => ({
  backgroundColor: bgColor.value,
  backgroundImage: bgPic.value ? `url(${bgPic.value})` : "",
  minHeight: windowHeight.value + "px",
}));
const pdHeights = computed(() => {
  let H = `${pdHeight.value * 2 + 100}rpx`;
  return { height: isFooter.value ? H : "100rpx" };
});

// lifecycle
onLoad((options) => {
  uni.hideTabBar();
  getOptions(options);
  nextTick(function() {
    uni.getSystemInfo({
      success: function(res) { windowHeight.value = res.windowHeight; },
    });
  });
  const { state, scope } = options;
  let tid = options.theme_id;
  // #ifdef MP
  if (options.scene) {
    let value = util.getUrlParams(decodeURIComponent(options.scene));
    if (value.theme_id) tid = value.theme_id;
  }
  // #endif
  if (tid) {
    themeId.value = tid;
    isPreview.value = true;
    uni.setStorageSync("previewThemeId", tid);
    applyTheme(tid);
  } else {
    let previewThemeId = uni.getStorageSync("previewThemeId");
    if (previewThemeId) {
      themeId.value = previewThemeId;
      isPreview.value = true;
      applyTheme(previewThemeId);
    } else {
      applyTheme();
    }
  }
  diyData();
  // #ifdef H5
  setOpenShare();
  // #endif
  // #ifdef MP
  getTempIds();
  // #endif
  getShare().then((res) => { shareInfo.value = res.data; });
  proxy.$eventHub.on("confirm_video_status", () => {
    if (confirm_video_status.value) return;
    confirm_video_status.value = true;
    let flag = true;
    // #ifdef H5
    flag = window.self == window.top;
    // #endif
    if (!flag) return;
    uni.showModal({
      content: "当前使用移动网络，是否继续播放视频？",
      success: (res) => {
        if (res.confirm) {
          appStore.SET_AUTOPLAY(true);
          proxy.$eventHub.emit("product_video_observe");
        }
      },
    });
  });
  // #ifdef APP-PLUS
  let onNetworkStatusChange = (res) => {
    if (res.isConnected) {
      diyData();
      uni.offNetworkStatusChange(onNetworkStatusChange);
    }
  };
  uni.onNetworkStatusChange(onNetworkStatusChange);
  // #endif
});

onUnload(() => { uni.$off("activeFn"); });

onShow(() => {
  uni.removeStorageSync("form_type_cart");
  if (isLogin.value) getCartNum();
  // #ifdef MP
  if (wx.canIUse("checkIsAddedToMyMiniProgram")) {
    checkMyApplet();
  } else {
    myApplet.value = true;
  }
  // #endif
});

onPullDownRefresh(() => { diyData(); uni.stopPullDownRefresh(); });

onReachBottom(() => { if (goodList.value.length) getGoodsList(); });

onPageScroll((e) => {
  // 通知 easy-loadimage 等组件重新判断懒加载
  uni.$emit("scroll");
  if (e.scrollTop > 20) myApplet.value = false;
  // #ifdef H5
  if (isHeaderSerch.value) {
    if (e.scrollTop > domOffsetTop.value) isFixed.value = true;
    if (e.scrollTop < domOffsetTop.value) nextTick(() => { isFixed.value = false; });
  } else {
    isFixed.value = false;
  }
  // #endif
  if (e.scrollTop > 10) isScrolled.value = true;
  else isScrolled.value = false;
});

// methods
function checkMyApplet() {
  wx.checkIsAddedToMyMiniProgram({
    success: (res) => { myApplet.value = !res.added; },
    fail: () => { myApplet.value = true; },
  });
}

function getCartNum() {
  getCartCounts(true, "total").then((res) => {
    indexDataStore.setCartNum(res.data.count + "");
    let cn = res.data.count;
    if (cn > 0) {
      uni.setTabBarBadge({ index: 3, text: cn > 99 ? "99+" : cn + "" });
    } else {
      uni.hideTabBarRedDot({ index: 3 });
    }
  }).catch((err) => {
    return util.Tips({ title: err.msg });
  });
}

function storeTap(id) {
  entryData.value.select_store_id = id;
  entryData.value.store_id = "";
  uni.removeStorageSync("rulesStoreId");
}

function getOptions(options) {
  // #ifdef MP
  if (options.scene) {
    let value = util.getUrlParams(decodeURIComponent(options.scene));
    if (value.spid) app.globalData.spid = value.spid;
  }
  // #endif
  if (options.spid) app.globalData.spid = options.spid;
}

function reconnect() {
  diyData();
  getShare().then((res) => { shareInfo.value = res.data; });
}

function goICP(url) {
  // #ifdef H5
  window.open(url);
  // #endif
  // #ifdef MP
  uni.navigateTo({ url: `/pages/annex/web_view/index?url=${url}` });
  // #endif
}

function bindHeighta(data) {
  // #ifdef APP-PLUS
  sortMpTop.value = data.top + data.height;
  // #endif
}

function bindHeight(data) {
  uni.hideLoading();
  domOffsetTop.value = data.top;
}

function goGoodsDetail(item) {
  goShopDetail(item, uid.value).then(() => {
    uni.navigateTo({ url: `/pages/goods/goods_details/index?id=${item.id}` });
  });
}

function changeSort(item, index) {
  if (curSort.value == index) return;
  curSort.value = index;
  sid.value = item.id;
  goodList.value = [];
  goodPage.value = 1;
  loaded.value = false;
  getGoodsList();
}

function bindSortId(item, data) {
  if (item.dataType.tabVal == 1) {
    uni.navigateTo({ url: `/pages/goods/goods_list/index?cid=${item.classPage.id}&title=${item.classPage.name}` });
  } else if (item.text.val == "首页") {
    uni.switchTab({ url: `/pages/index/index` });
  } else {
    uni.navigateTo({ url: `/pages/activity/small_page/index?micro_id=${item.microPage.id}` });
  }
}

function getProductList(data) {
  curSort.value = 0;
  loaded.value = false;
  if (sortAll.value.length > 0) {
    sortAll.value.forEach((el) => {
      if (el.id == data) {
        sortList.value = el;
        sid.value = el.children.length ? el.children[0].id : "";
      }
    });
    goodList.value = [];
    goodPage.value = 1;
    nextTick(() => { if (sortList.value != "") getGoodsList(); });
  } else {
    getCategoryList().then((res) => {
      sortAll.value = res.data;
      res.data.forEach((el) => {
        if (el.id == data) {
          sortList.value = el;
          sid.value = el.children.length ? el.children[0].id : "";
        }
      });
      goodList.value = [];
      goodPage.value = 1;
      nextTick(() => { if (sortList.value != "") getGoodsList(); });
    });
  }
}

function getGoodsList() {
  if (loading.value || loaded.value) return;
  loading.value = true;
  getProductslist({
    sid: sid.value, keyword: "", priceOrder: "", salesOrder: "",
    news: 0, page: goodPage.value, limit: 10, cid: sortList.value.id,
  }).then((res) => {
    loading.value = false;
    loaded.value = res.data.length < 10;
    goodPage.value++;
    goodList.value = goodList.value.concat(res.data);
  });
}

function onLoadFun() { isShowAuth.value = false; }

// #ifdef H5
function getQueryString(name) {
  var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
  var reg_rewrite = new RegExp("(^|/)" + name + "/([^/]*)(/|$)", "i");
  var r = window.location.search.substr(1).match(reg);
  var q = window.location.pathname.substr(1).match(reg_rewrite);
  if (r != null) return unescape(r[2]);
  else if (q != null) return unescape(q[2]);
  else return null;
}
// #endif

// #ifdef MP
function getTempIds() {
  let messageTmplIds = wx.getStorageSync(SUBSCRIBE_MESSAGE);
  if (!messageTmplIds) {
    getTempIdsApi().then((res) => {
      if (res.data) cacheSubscribeTemplateIds(res.data);
    }).catch(() => {});
  }
}
// #endif

function objToArr(data) {
  if (!data || typeof data !== "object") return [];
  let obj = Object.keys(data).sort();
  return obj.map((key) => data[key]);
}

function setDiyData(data) {
  if (!data) return;
  currentDiyData.value = data;
  errorNetwork.value = false;
  if (data.is_bg_color) bgColor.value = data.color_picker || "";
  if (data.is_bg_pic) { bgPic.value = data.bg_pic || ""; bgTabVal.value = data.bg_tab_val || ""; }
  pageShow.value = 1;
  if (data.title) uni.setNavigationBarTitle({ title: data.title });
  let temp = [];
  let gi = [];
  let pi = [];
  let lastArr = objToArr(data.value);
  lastArr.forEach((item, index) => {
    if (!item) return;
    if (item.name == "pageFoot") footerConfigData.value = item;
    if (item.name === "homeComb" && !item.isHide) {
      showHomeComb.value = true;
      homeCombData.value = item;
      if (item.searchConfig && item.searchConfig.tabVal) positionTop.value = uni.getWindowInfo().statusBarHeight + 43;
    }
    if (item.name == "headerSerch" && !item.isHide) { isHeaderSerch.value = true; headerSerchCombData.value = item; }
    if (item.name == "tabNav" && !item.isHide) { showCateNav.value = true; cateNavData.value = item; }
    if (item.name == "goodList" && !item.isHide) gi.push(index);
    if (item.name == "promotionList" && !item.isHide) pi.push(index);
    if (!item.isHide) temp.push(item);
  });
  temp.sort((a, b) => (a.timestamp || 0) - (b.timestamp || 0));
  styleConfig.value = temp;
  goodsIndex.value = gi;
  promotionIndex.value = pi;
}

function exitPreview() {
  uni.removeStorageSync("previewThemeId");
  themeId.value = 0;
  currentDiyData.value = {};
  isPreview.value = false;
  applyTheme();
  diyData();
}

function getDiyData() {
  let data = {};
  if (themeId.value) data.theme_id = themeId.value;
  getThemeInfo("home", data).then((res) => {
    uni.setStorageSync("diyData", JSON.stringify(res.data));
    setDiyData(res.data);
  }).catch((error) => {
    // #ifdef APP-PLUS
    if (error.status) {
      uni.hideLoading();
      if (errorNetwork.value) uni.showToast({ title: "请开启网络连接", icon: "none", duration: 2000 });
      errorNetwork.value = true;
    }
    // #endif
  });
}

function diyData() { getDiyData(); }

function changeLogin() { getIsLogin(); }
function getIsLogin() { toLogin(); }

function changeBarg(item) {
  if (!isLogin.value) getIsLogin();
  else uni.navigateTo({ url: `/pages/activity/goods_bargain_details/index?id=${item.id}&spid=${uid.value}` });
}

function goDetail(item) {
  goShopDetail(item, uid.value).then(() => {
    uni.navigateTo({ url: `/pages/goods/goods_details/index?id=${item.id}` });
  });
}

function newDataStatus(val, num) {
  isFooter.value = val ? true : false;
  pdHeight.value = num;
}

// #ifdef H5
function setOpenShare() {
  let u = uid.value ? uid.value : 0;
  if (proxy.$wechat.isWeixin()) {
    getShare().then((res) => {
      let data = res.data;
      let configAppMessage = {
        desc: data.synopsis, title: data.title,
        link: location.href + "?spid=" + u, imgUrl: data.img,
      };
      proxy.$wechat.wechatEvevt(
        ["updateAppMessageShareData", "updateTimelineShareData", "onMenuShareAppMessage", "onMenuShareTimeline"],
        configAppMessage
      );
    });
  }
}
// #endif
</script>

<style lang="scss">
.page {
  // padding-bottom: 50px;
  overflow-y: scroll;
  overflow-x: hidden;
}
.myApplet {
  position: relative;
  &::after {
    position: absolute;
    right: 55px;
    top: -5px;
    content: "";
    width: 0;
    height: 0;
    border-left: 7px solid transparent;
    border-right: 7px solid transparent;
    border-bottom: 7px solid #fff;
  }
}
.pictrue_log_class {
  background-color: var(--view-theme);
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

  image {
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

.sort-scroll {
  background-color: #fff;
}

.sort-product {
  margin-top: 20rpx;
}

.site-config {
  margin: 40rpx 0;
  font-size: 24rpx;
  text-align: center;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  .ban {
    width: 22rpx;
    height: 24rpx;
    margin-right: 10rpx;
  }
  &.fixed {
    position: fixed;
    bottom: 69px;
    left: 0;
    width: 100%;
  }
}
.exit-preview {
  position: fixed;
  bottom: 200rpx;
  right: 30rpx;
  z-index: 999;
  background-color: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 10rpx 24rpx;
  border-radius: 30rpx;
  font-size: 24rpx;
}
.select {
  border: 1px solid var(--view-theme);
}
</style>
