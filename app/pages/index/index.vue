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
            ref="waterfallsFlow"
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

<script>
const app = getApp();
import colors from "@/mixins/color";
import { getCrmebCopyRight } from "@/api/api.js";
import { getShare } from "@/api/public.js";
import waterfallsFlow from "@/components/WaterfallsFlow/WaterfallsFlow.vue";
import emptyPage from "@/components/emptyPage.vue";
// #ifdef MP
import { getTempIds } from "@/api/api.js";
import { SUBSCRIBE_MESSAGE } from "@/config/cache";
// #endif
import { mapGetters, mapMutations } from "vuex";
import { getDiy, getDiyVersion, getThemeInfo } from "@/api/api.js";
import { getCartCounts } from "@/api/order.js";
import { getCategoryList, getProductslist } from "@/api/store.js";
import { goShopDetail } from "@/libs/order.js";
import { toLogin } from "@/libs/login.js";
import { HTTP_REQUEST_URL } from "@/config/app";
import Loading from "@/components/Loading/index.vue";
import Cache from "@/utils/cache";
import appUpdate from "@/components/update/app-update.vue";
import { applyTheme } from "@/utils/theme.js";
import PageDesign from "@/subpackage/diyComponents/pageDesign.vue";

export default {
  computed: {
    // #ifdef MP
    appletStyle() {
      return {
        top: this.getHeight.menuButtonInfo.bottom + 8 + "px",
        right: "10px",
      };
    },
    // #endif
    pageStyle() {
      return {
        backgroundColor: this.bgColor,
        backgroundImage: this.bgPic ? `url(${this.bgPic})` : "",
        minHeight: this.windowHeight + "px",
      };
    },
    pdHeights() {
      let H = `${this.pdHeight * 2 + 100}rpx`;
      return {
        height: this.isFooter ? H : "100rpx",
      };
    },
    ...mapGetters(["isLogin", "uid", "cartNum"]),
  },
  mixins: [colors],
  components: {
    PageDesign,
    Loading,
    waterfallsFlow,
    emptyPage,
    // #ifdef APP
    appUpdate,
    // #endif
  },
  data() {
    return {
      styleConfig: [],
      loading: false,
      loadend: false,
      loadTitle: "下拉加载更多", //提示语
      page: 1,
      limit: this.$config.LIMIT,
      numConfig: 0,
      code: "",
      shareInfo: {},
      sortList: "",
      sortAll: [],
      goodPage: 1,
      goodList: [],
      sid: 0,
      curSort: 0,
      sortMpTop: 0,
      loaded: false,
      loading: false,
      domOffsetTop: 50,
      // #ifdef APP-PLUS || MP
      isFixed: true,
      // #endif
      // #ifdef H5
      isFixed: false,
      // #endif
      site_config: "",
      errorNetwork: false, // 是否断网
      isHeaderSerch: false,
      showHomeComb: false,
      showCateNav: false,
      homeCombData: {},
      headerSerchCombData: {},
      cateNavData: {},
      footerConfigData: {},
      bgColor: "",
      bgPic: "",
      bgTabVal: "",
      pageShow: true,
      windowHeight: 0,
      imgHost: HTTP_REQUEST_URL,
      isShowAuth: false,
      isScrolled: false,
      product_video_status: false,
      confirm_video_status: false,
      positionTop: 0,
      isFooter: false,
      pdHeight: 0, //自定义底部导航上下边距和
      entryData: {
        store_id: "",
        latitude: "",
        longitude: "",
        select_store_id: "",
      },
      goodsIndex: [],
      promotionIndex: [],
      belongIndex: 0, // 进店规则归属门店排序位置；
      isBelongStore: false, //判断是否为归属门店；
      getHeight: this.$util.getWXStatusHeight(),
      myApplet: true,
      configData: Cache.get("BASIC_CONFIG"),
      currentDiyData: {},
      isPreview: false,
      themeId: 0,
    };
  },
  onLoad(options) {
    let that = this;
    uni.hideTabBar();
    that.getOptions(options);
    this.$nextTick(function () {
      uni.getSystemInfo({
        success: function (res) {
          that.windowHeight = res.windowHeight;
        },
      });
    });
    const { state, scope } = options;
    let themeId = options.theme_id;
    // #ifdef MP
    if (options.scene) {
      let value = this.$util.getUrlParams(decodeURIComponent(options.scene));
      if (value.theme_id) themeId = value.theme_id;
    }
    // #endif

    if (themeId) {
      this.themeId = themeId;
      this.isPreview = true;
      uni.setStorageSync("previewThemeId", themeId);
      applyTheme(themeId);
    } else {
      let previewThemeId = uni.getStorageSync("previewThemeId");
      if (previewThemeId) {
        this.themeId = previewThemeId;
        this.isPreview = true;
        applyTheme(previewThemeId);
      } else {
        applyTheme();
      }
    }
    this.diyData();
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
      if (this.confirm_video_status) {
        return;
      }
      this.confirm_video_status = true;
      let flag = true;
      // #ifdef H5
      flag = window.self == window.top;
      // #endif
      if (!flag) {
        return;
      }
      uni.showModal({
        content: "当前使用移动网络，是否继续播放视频？",
        success: (res) => {
          if (res.confirm) {
            // 监听
            this.SET_AUTOPLAY(true);
            this.$eventHub.$emit("product_video_observe");
          }
        },
      });
    });

    // #ifdef APP-PLUS
    let onNetworkStatusChange = (res) => {
      if (res.isConnected) {
        this.diyData();
        uni.offNetworkStatusChange(onNetworkStatusChange);
      }
    };
    uni.onNetworkStatusChange(onNetworkStatusChange);
    // #endif
  },
  onUnload() {
    // 清除监听
    uni.$off("activeFn");
  },
  onShow() {
    uni.removeStorageSync("form_type_cart");
    if (this.isLogin) {
      this.getCartNum();
    }
    // #ifdef MP
    if (wx.canIUse("checkIsAddedToMyMiniProgram")) {
      this.checkMyApplet();
    } else {
      this.myApplet = true;
    }
    // #endif
  },
  onPullDownRefresh() {
    this.diyData();
    uni.stopPullDownRefresh();
  },
  methods: {
    ...mapMutations(["SET_AUTOPLAY", "SET_NEARBY"]),
    checkMyApplet() {
      wx.checkIsAddedToMyMiniProgram({
        success: (res) => {
          if (res.added) {
            this.myApplet = false;
          } else {
            this.myApplet = true;
          }
        },
        fail: () => {
          this.myApplet = true;
        },
      });
    },
    getCartNum: function () {
      getCartCounts()
        .then((res) => {
          this.$store.commit("indexData/setCartNum", res.data.count + "");
          let cartNum = res.data.count;
          if (cartNum > 0) {
            uni.setTabBarBadge({
              index: 3,
              text: cartNum > 99 ? "99+" : cartNum + "",
            });
          } else {
            uni.hideTabBarRedDot({
              index: 3,
            });
          }
        })
        .catch((err) => {
          return this.$util.Tips({
            title: err.msg,
          });
        });
    },
    storeTap(id) {
      this.entryData.select_store_id = id;
      this.entryData.store_id = "";
      uni.removeStorageSync("rulesStoreId");
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
    getOptions(options) {
      let that = this;
      // #ifdef MP
      if (options.scene) {
        let value = that.$util.getUrlParams(decodeURIComponent(options.scene));
        //记录推广人uid
        if (value.spid) app.globalData.spid = value.spid;
      }
      // #endif
      if (options.spid) app.globalData.spid = options.spid;
    },
    // 重新链接
    reconnect() {
      this.diyData();
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
    bindHeight(data) {
      uni.hideLoading();
      this.domOffsetTop = data.top;
    },
    // 去商品详情
    goGoodsDetail(item) {
      goShopDetail(item, this.uid).then((res) => {
        uni.navigateTo({
          url: `/pages/goods/goods_details/index?id=${item.id}`,
        });
      });
    },
    // 分类点击
    changeSort(item, index) {
      if (this.curSort == index) return;
      this.curSort = index;
      this.sid = item.id;
      this.goodList = [];
      this.goodPage = 1;
      this.loaded = false;
      this.getGoodsList();
    },
    /**
			 * @param data {
				classPage: 0 分类id
				microPage: 0 微页面id
				type: 1   0 微页面 1 商品分类
			 }*/
    bindSortId(item, data) {
      if (item.dataType.tabVal == 1) {
        uni.navigateTo({
          url: `/pages/goods/goods_list/index?cid=${item.classPage.id}&title=${item.classPage.name}`,
        });
      } else if (item.text.val == "首页") {
        uni.switchTab({
          url: `/pages/index/index`,
        });
      } else {
        uni.navigateTo({
          url: `/pages/activity/small_page/index?id=${item.microPage.id}`,
        });
      }
    },
    getProductList(data) {
      let tempObj = "";
      this.curSort = 0;
      this.loaded = false;
      if (this.sortAll.length > 0) {
        this.sortAll.forEach((el, index) => {
          if (el.id == data) {
            this.$set(this, "sortList", el);
            this.sid = el.children.length ? el.children[0].id : "";
          }
        });
        this.goodList = [];
        this.goodPage = 1;
        this.$nextTick(() => {
          if (this.sortList != "") this.getGoodsList();
        });
      } else {
        getCategoryList().then((res) => {
          this.sortAll = res.data;
          res.data.forEach((el, index) => {
            if (el.id == data) {
              this.sortList = el;
              this.sid = el.children.length ? el.children[0].id : "";
            }
          });
          this.goodList = [];
          this.goodPage = 1;

          this.$nextTick(() => {
            if (this.sortList != "") this.getGoodsList();
          });
        });
      }
    },
    // 商品列表
    getGoodsList() {
      if (this.loading || this.loaded) return;
      this.loading = true;
      getProductslist({
        sid: this.sid,
        keyword: "",
        priceOrder: "",
        salesOrder: "",
        news: 0,
        page: this.goodPage,
        limit: 10,
        cid: this.sortList.id,
      }).then((res) => {
        this.loading = false;
        this.loaded = res.data.length < 10;
        this.goodPage++;
        this.goodList = this.goodList.concat(res.data);
      });
    },
    onLoadFun() {
      this.isShowAuth = false;
    },
    // #ifdef H5
    // 获取url后面的参数
    getQueryString(name) {
      var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
      var reg_rewrite = new RegExp("(^|/)" + name + "/([^/]*)(/|$)", "i");
      var r = window.location.search.substr(1).match(reg);
      var q = window.location.pathname.substr(1).match(reg_rewrite);
      if (r != null) {
        return unescape(r[2]);
      } else if (q != null) {
        return unescape(q[2]);
      } else {
        return null;
      }
    },
    // #endif

    // #ifdef MP
    getTempIds() {
      let messageTmplIds = wx.getStorageSync(SUBSCRIBE_MESSAGE);
      if (!messageTmplIds) {
        getTempIds().then((res) => {
          if (res.data)
            wx.setStorageSync(SUBSCRIBE_MESSAGE, JSON.stringify(res.data));
        });
      }
    },
    // #endif
    // 对象转数组
    objToArr(data) {
      if (!data || typeof data !== "object") return [];
      let obj = Object.keys(data).sort();
      let m = obj.map((key) => data[key]);
      return m;
    },
    setDiyData(data) {
      if (!data) return;
      this.currentDiyData = data;
      this.errorNetwork = false;
      if (data.is_bg_color) {
        this.bgColor = data.color_picker || "";
      }
      if (data.is_bg_pic) {
        this.bgPic = data.bg_pic || "";
        this.bgTabVal = data.bg_tab_val || "";
      }
      this.pageShow = 1;
      if (data.title) {
        uni.setNavigationBarTitle({
          title: data.title,
        });
      }
      let temp = [];
      let goodsIndex = [];
      let promotionIndex = [];
      let lastArr = this.objToArr(data.value);
      lastArr.forEach((item, index, arr) => {
        if (!item) return;
        if (item.name == "pageFoot") {
          this.footerConfigData = item;
        }
        if (item.name === "homeComb" && !item.isHide) {
          this.showHomeComb = true;
          this.homeCombData = item;
          if (item.searchConfig && item.searchConfig.tabVal) {
            this.positionTop = uni.getWindowInfo().statusBarHeight + 43;
          }
        }
        if (item.name == "headerSerch" && !item.isHide) {
          this.isHeaderSerch = true;
          this.headerSerchCombData = item;
        }
        if (item.name == "tabNav" && !item.isHide) {
          this.showCateNav = true;
          this.cateNavData = item;
        }
        if (item.name == "goodList" && !item.isHide) {
          goodsIndex.push(index);
        }
        if (item.name == "promotionList" && !item.isHide) {
          promotionIndex.push(index);
        }
        if (!item.isHide) {
          temp.push(item);
        }
      });

      function sortNumber(a, b) {
        return (a.timestamp || 0) - (b.timestamp || 0);
      }
      temp.sort(sortNumber);
      this.styleConfig = temp;
      this.goodsIndex = goodsIndex;
      this.promotionIndex = promotionIndex;
    },
    exitPreview() {
      uni.removeStorageSync("previewThemeId");
      this.themeId = 0;
      this.currentDiyData = {};

      this.isPreview = false;
      applyTheme();
      this.diyData();
      // 去掉 theme_id 刷新当前页面
      // uni.reLaunch({ url: "/pages/index/index" });
    },
    getDiyData() {
      let data = {};
      if (this.themeId) data.theme_id = this.themeId;
      getThemeInfo("home", data)
        .then((res) => {
          uni.setStorageSync("diyData", JSON.stringify(res.data));
          this.setDiyData(res.data);
        })
        .catch((error) => {
          // #ifdef APP-PLUS
          if (error.status) {
            uni.hideLoading();
            if (this.errorNetwork) {
              uni.showToast({
                title: "请开启网络连接",
                icon: "none",
                duration: 2000,
              });
            }
            this.errorNetwork = true;
          }
          // #endif
        });
    },
    diyData() {
      // let diyData = uni.getStorageSync('diyData');
      // if (diyData) {
      // 	getDiyVersion(0).then((res) => {
      // 		let diyVersion = uni.getStorageSync('diyVersion');
      // 		if (res.data.version + '0' === diyVersion) {
      // 			this.setDiyData(JSON.parse(diyData));
      // 		} else {
      // 			uni.setStorageSync('diyVersion', res.data.version + '0');
      // 			this.getDiyData();
      // 		}
      // 	});
      // } else {
      // }
      this.getDiyData();
    },

    changeLogin() {
      this.getIsLogin();
    },
    getIsLogin() {
      toLogin();
    },
    changeBarg(item) {
      if (!this.isLogin) {
        this.getIsLogin();
      } else {
        uni.navigateTo({
          url: `/pages/activity/goods_bargain_details/index?id=${item.id}&spid=${this.$store.state.app.uid}`,
        });
      }
    },
    goDetail(item) {
      goShopDetail(item, this.$store.state.app.uid).then((res) => {
        uni.navigateTo({
          url: `/pages/goods/goods_details/index?id=${item.id}`,
        });
      });
    },
    newDataStatus(val, num) {
      this.isFooter = val ? true : false;
      this.pdHeight = num;
    },
    // #ifdef H5
    // 微信分享；
    setOpenShare: function () {
      let that = this;
      let uid = this.uid ? this.uid : 0;
      if (that.$wechat.isWeixin()) {
        getShare().then((res) => {
          let data = res.data;
          let configAppMessage = {
            desc: data.synopsis,
            title: data.title,
            link: location.href + "?spid=" + uid,
            imgUrl: data.img,
          };
          that.$wechat.wechatEvevt(
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
  onReachBottom() {
    if (this.goodList.length) {
      this.getGoodsList();
    }
  },
  onPageScroll(e) {
    if (e.scrollTop > 20) {
      this.myApplet = false;
    }
    // #ifdef H5
    if (this.isHeaderSerch) {
      if (e.scrollTop > this.domOffsetTop) {
        this.isFixed = true;
      }
      if (e.scrollTop < this.domOffsetTop) {
        this.$nextTick(() => {
          this.isFixed = false;
        });
      }
    } else {
      this.isFixed = false;
    }
    // #endif
    if (e.scrollTop > 10) {
      this.isScrolled = true;
    } else {
      this.isScrolled = false;
    }
    uni.$emit("scroll");
    uni.$emit("onPageScroll", e.scrollTop);
  },
  //#ifdef MP
  onShareAppMessage() {
    let uid = this.uid ? this.uid : 0;
    if (this.shareInfo.img) {
      return {
        title: this.shareInfo.title,
        path: "/pages/index/index?spid=" + uid,
        imageUrl: this.shareInfo.img,
        desc: this.shareInfo.synopsis,
      };
    } else {
      return {
        title: this.shareInfo.title,
        path: "/pages/index/index?spid=" + uid,
        // imageUrl: this.shareInfo.img,
        // desc: this.shareInfo.synopsis
      };
    }
  },
  //分享到朋友圈
  onShareTimeline: function () {
    return {
      title: this.shareInfo.title,
      path: "/pages/index/index",
      imageUrl: this.shareInfo.img,
      desc: this.shareInfo.synopsis,
    };
  },
  //#endif
};
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
