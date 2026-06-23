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

<script>
const app = getApp();
import colors from "@/mixins/color";
import themePage from "@/mixins/themePage.js";
import PageDesign from "@/subpackage/diyComponents/pageDesign.vue";
import { getShare } from "@/api/public.js";
import { getCrmebCopyRight, getTempIds } from "@/api/api.js";
import { SUBSCRIBE_MESSAGE } from "@/config/cache";
import { mapGetters, mapMutations } from "vuex";
import { toLogin } from "@/libs/login.js";
import { HTTP_REQUEST_URL } from "@/config/app";
import Cache from "@/utils/cache";
import appUpdate from "@/components/update/app-update.vue";

export default {
  mixins: [colors, themePage],
  components: {
    PageDesign,
    // #ifdef APP
    appUpdate,
    // #endif
  },
  computed: {
    ...mapGetters(["isLogin", "uid"]),
    pageStyle() {
      return {
        backgroundColor: this.bgColor,
        backgroundImage: this.bgPic ? `url(${this.bgPic})` : "",
        minHeight: this.windowHeight + "px",
      };
    },
  },
  data() {
    return {
      pageShow: false,
      currentDiyData: {},
      windowHeight: 0,
      isFixed: true,
      isScrolled: false,
      errorNetwork: false,
      confirm_video_status: false,
      belongIndex: 0,
      bgColor: "",
      bgPic: "",
      bgTabVal: "",
      site_config: "",
      configData: Cache.get("BASIC_CONFIG"),
      shareInfo: {},
      imgHost: HTTP_REQUEST_URL,
      themeId: "",
      isFooter: false,
      pdHeight: 0,
      sortMpTop: 0,
      entryData: {
        store_id: "",
        select_store_id: "",
      },
    };
  },
  onLoad(options) {
    uni.hideTabBar();
    this.getOptions(options);
    this.initPage(options);
    this.$nextTick(() => {
      uni.getSystemInfo({
        success: (res) => {
          this.windowHeight = res.windowHeight;
        },
      });
    });
    // #ifdef H5
    this.setOpenShare();
    // #endif
    // #ifdef MP
    this.getTempIds();
    // #endif
    getShare().then((res) => {
      this.shareInfo = res.data;
    });
    this.getCopyRight();
    this.$eventHub.$on("confirm_video_status", () => {
      if (this.confirm_video_status) return;
      this.confirm_video_status = true;
      let flag = true;
      // #ifdef H5
      flag = window.self == window.top;
      // #endif
      if (!flag) return;
      uni.showModal({
        content: "当前使用移动网络，是否继续播放视频？",
        success: (res) => {
          if (res.confirm) {
            this.SET_AUTOPLAY(true);
            this.$eventHub.$emit("product_video_observe");
          }
        },
      });
    });
  },
  onUnload() {
    uni.$off("activeFn");
  },
  onShow() {
    uni.removeStorageSync("form_type_cart");
  },
  onPullDownRefresh() {
    this.initPage({ id: this.themeId });
    uni.stopPullDownRefresh();
  },
  onPageScroll(e) {
    uni.$emit("scroll");
    this.isScrolled = e.scrollTop > 10;
  },
  methods: {
    ...mapMutations(["SET_AUTOPLAY", "SET_NEARBY"]),
    async initPage(options = {}) {
      try {
        this.pageShow = false;
        const data = await this.initThemePage("home", options);
        this.currentDiyData = data || {};
        this.applyThemeData(this.currentDiyData);
      } finally {
        this.pageShow = true;
      }
    },
    applyThemeData(data) {
      if (!data || typeof data !== "object") return;
      this.bgColor = data.color_picker || "";
      this.bgPic = data.bg_pic || "";
      this.bgTabVal = data.bg_tab_val || "";
      if (data.title) {
        uni.setNavigationBarTitle({
          title: data.title,
        });
      }
      if (data.titleColor || data.titleBgColor) {
        uni.setNavigationBarColor({
          frontColor: data.titleColor || "#ffffff",
          backgroundColor: (data.titleBgColor || "#ffffff").toString().toLowerCase(),
        });
      }
    },
    getOptions(options) {
      // #ifdef MP
      if (options.scene) {
        let value = this.$util.getUrlParams(decodeURIComponent(options.scene));
        if (value.spid) app.globalData.spid = value.spid;
      }
      // #endif
      if (options.spid) app.globalData.spid = options.spid;
    },
    getCopyRight() {
      getCrmebCopyRight()
        .then((res) => {
          let data = res.data;
          uni.setStorageSync("wechatStatus", data.wechat_status);
          if (!data.copyrightContext && !data.copyrightImage) {
            data.copyrightImage = "/static/images/support.png";
          }
          uni.setStorageSync("copyNameInfo", data.copyrightContext);
          uni.setStorageSync("copyImageInfo", data.copyrightImage);
          // #ifdef MP
          uni.setStorageSync(
            "MPSiteData",
            JSON.stringify({
              site_logo: data.site_logo,
              site_name: data.site_name,
            })
          );
          // #endif
        })
        .catch((err) => {
          return this.$util.Tips({
            title: err.msg,
          });
        });
    },
    reconnect() {
      this.initPage({ id: this.themeId });
      getShare().then((res) => {
        this.shareInfo = res.data;
      });
    },
    goICP(url) {
      // #ifdef H5
      window.open(url);
      // #endif
      // #ifdef MP
      uni.navigateTo({
        url: `/pages/annex/web_view/index?url=${url}`,
      });
      // #endif
    },
    bindHeighta(data) {
      // #ifdef APP-PLUS
      this.sortMpTop = data.top + data.height;
      // #endif
    },
    storeTap(id) {
      this.entryData.select_store_id = id;
      this.entryData.store_id = "";
      uni.removeStorageSync("rulesStoreId");
    },
    bindSortId(item) {
      if (item.dataType && item.dataType.tabVal == 1) {
        uni.navigateTo({
          url: `/pages/goods/goods_list/index?cid=${item.classPage.id}&title=${item.classPage.name}`,
        });
      } else if (item.text && item.text.val == "首页") {
        uni.switchTab({
          url: `/pages/index/index`,
        });
      } else if (item.microPage && item.microPage.id) {
        uni.navigateTo({
          url: `/pages/activity/small_page/index?id=${item.microPage.id}`,
        });
      }
    },
    changeLogin() {
      toLogin();
    },
    changeBarg(item) {
      if (!this.isLogin) {
        toLogin();
        return;
      }
      uni.navigateTo({
        url: `/pages/activity/goods_bargain_details/index?id=${item.id}&spid=${this.uid || 0}`,
      });
    },
    newDataStatus(val, num) {
      this.isFooter = val ? true : false;
      this.pdHeight = num;
    },
    getTempIds() {
      // #ifdef MP
      let messageTmplIds = wx.getStorageSync(SUBSCRIBE_MESSAGE);
      if (!messageTmplIds) {
        getTempIds().then((res) => {
          if (res.data) {
            wx.setStorageSync(SUBSCRIBE_MESSAGE, JSON.stringify(res.data));
          }
        });
      }
      // #endif
    },
    // #ifdef H5
    setOpenShare() {
      let uid = this.uid ? this.uid : 0;
      if (this.$wechat.isWeixin()) {
        getShare().then((res) => {
          let data = res.data;
          let configAppMessage = {
            desc: data.synopsis,
            title: data.title,
            link: location.href + "?spid=" + uid,
            imgUrl: data.img,
          };
          this.$wechat.wechatEvevt(
            [
              "updateAppMessageShareData",
              "updateTimelineShareData",
              "onMenuShareAppMessage",
              "onMenuShareTimeline",
            ],
            configAppMessage
          );
        });
      }
    },
    // #endif
  },
};
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
