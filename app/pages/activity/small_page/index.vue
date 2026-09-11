<template>
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
    :style="pageStyle"
  >
    <PageDesign
      :style="colorStyle"
      :diyData="currentDiyData"
      :isHome="true"
      :microPage="true"
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
          v-if="configData && configData.network_security"
          class="site-config"
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
  </view>
</template>

<script setup>
import { ref, computed, nextTick, getCurrentInstance } from "vue";
import { onLoad, onUnload, onShow, onPullDownRefresh, onPageScroll } from "@dcloudio/uni-app";
const app = getApp();
import { useColor } from "@/composables/useColor.js";
import { useThemePage } from "@/composables/useThemePage.js";
import PageDesign from "@/subpackage/diyComponents/pageDesign.vue";
import { getShare } from "@/api/public.js";
import { getTempIds } from "@/api/api.js";
import { SUBSCRIBE_MESSAGE } from "@/config/cache.js";
import { cacheSubscribeTemplateIds } from "@/utils/SubscribeMessage.js";
import { toLogin } from "@/libs/login.js";
import { HTTP_REQUEST_URL } from "@/config/app.js";
import Cache from "@/utils/cache.js";
import util from "@/utils/util.js";
import appUpdate from "@/components/update/app-update.vue";
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from "pinia";

const { proxy } = getCurrentInstance();
const appStore = useAppStore();
const { isLogin, uid } = storeToRefs(appStore);
const { colorStyle } = useColor();
const { themeId, themeDiyData, themeChecked, getThemeIdFromOptions, initThemePage } = useThemePage();

const pageShow = ref(false);
const currentDiyData = ref({});
const windowHeight = ref(0);
const isFixed = ref(true);
const isScrolled = ref(false);
const errorNetwork = ref(false);
const confirm_video_status = ref(false);
const belongIndex = ref(0);
const bgColor = ref("");
const bgPic = ref("");
const bgTabVal = ref("");
const site_config = ref("");
const configData = ref(Cache.get("BASIC_CONFIG"));
const shareInfo = ref({});
const imgHost = ref(HTTP_REQUEST_URL);
const isFooter = ref(false);
const pdHeight = ref(0);
const sortMpTop = ref(0);
const entryData = ref({ store_id: "", select_store_id: "" });
const microId = ref("");

const pageStyle = computed(() => ({
  backgroundColor: bgColor.value,
  backgroundImage: bgPic.value ? `url(${bgPic.value})` : "",
  minHeight: windowHeight.value + "px",
}));

onLoad((options) => {
  uni.hideTabBar();
  getOptions(options);
  initPage(options);
  nextTick(() => {
    uni.getSystemInfo({ success: (res) => { windowHeight.value = res.windowHeight; } });
  });
  // #ifdef H5
  setOpenShare();
  // #endif
  // #ifdef MP
  getTempIdsFn();
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
});

onUnload(() => { uni.$off("activeFn"); });
onShow(() => { uni.removeStorageSync("form_type_cart"); });
onPullDownRefresh(() => { initPage({ theme_id: microId.value }); uni.stopPullDownRefresh(); });
onPageScroll((e) => { uni.$emit("scroll"); isScrolled.value = e.scrollTop > 10; });

async function initPage(options = {}) {
  try {
    pageShow.value = false;
    if (microId.value && !options.micro_id) options.theme_id = microId.value;
    const data = await initThemePage("home", options);
    currentDiyData.value = data || {};
    applyThemeData(currentDiyData.value);
  } finally {
    pageShow.value = true;
  }
}

function applyThemeData(data) {
  if (!data || typeof data !== "object") return;
  bgColor.value = data.color_picker || "";
  bgPic.value = data.bg_pic || "";
  bgTabVal.value = data.bg_tab_val || "";
  if (data.title) uni.setNavigationBarTitle({ title: data.title });
  if (data.titleColor || data.titleBgColor) {
    uni.setNavigationBarColor({
      frontColor: data.titleColor || "#ffffff",
      backgroundColor: (data.titleBgColor || "#ffffff").toString().toLowerCase(),
    });
  }
}

function getOptions(options) {
  // #ifdef MP
  if (options.scene) {
    let value = util.getUrlParams(decodeURIComponent(options.scene));
    if (value.spid) app.globalData.spid = value.spid;
    if (value.micro_id) microId.value = value.micro_id;
  }
  // #endif
  if (options.spid) app.globalData.spid = options.spid;
  if (options.micro_id) microId.value = options.micro_id;
}

function reconnect() {
  initPage({ theme_id: microId.value });
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

function storeTap(id) {
  entryData.value.select_store_id = id;
  entryData.value.store_id = "";
  uni.removeStorageSync("rulesStoreId");
}

function bindSortId(item) {
  if (item.dataType && item.dataType.tabVal == 1) {
    uni.navigateTo({ url: `/pages/goods/goods_list/index?cid=${item.classPage.id}&title=${item.classPage.name}` });
  } else if (item.text && item.text.val == "首页") {
    uni.switchTab({ url: "/pages/index/index" });
  } else if (item.microPage && item.microPage.id) {
    uni.navigateTo({ url: `/pages/activity/small_page/index?micro_id=${item.microPage.id}` });
  }
}

function changeLogin() { toLogin(); }

function changeBarg(item) {
  if (!isLogin.value) { toLogin(); return; }
  uni.navigateTo({ url: `/pages/activity/goods_bargain_details/index?id=${item.id}&spid=${uid.value || 0}` });
}

function newDataStatus(val, num) {
  isFooter.value = val ? true : false;
  pdHeight.value = num;
}

function getTempIdsFn() {
  // #ifdef MP
  let messageTmplIds = wx.getStorageSync(SUBSCRIBE_MESSAGE);
  if (!messageTmplIds) {
    getTempIds().then((res) => {
      if (res.data) cacheSubscribeTemplateIds(res.data);
    }).catch(() => {});
  }
  // #endif
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

<style lang="scss" scoped>
.page {
  overflow-y: scroll;
  overflow-x: hidden;
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
