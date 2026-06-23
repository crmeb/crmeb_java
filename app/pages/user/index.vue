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
          >{{ $t("个人中心") }}</view
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
<script>
let sysHeight = uni.getWindowInfo().statusBarHeight + "px";
import {
  getMenuList,
  getUserInfo,
  setVisit,
  mpBindingPhone,
} from "@/api/user.js";
import { getThemeInfo } from "@/api/api.js";
import { wechatAuthV2, silenceAuth } from "@/api/public.js";
import { toLogin } from "@/libs/login.js";
import { mapState, mapGetters } from "vuex";
// #ifdef H5
import Auth from "@/libs/wechat";
// #endif
const app = getApp();
import dayjs from "@/plugin/dayjs/dayjs.min.js";
import Routine from "@/libs/routine";
import colors from "@/mixins/color";
import pageFooter from "@/components/pageFooter/index.vue";
import { getCustomer } from "@/utils/index.js";
import editUserModal from "@/components/eidtUserModal/index.vue";
import couponWindow from "@/components/couponWindow/index";
import waterfallsFlow from "@/components/WaterfallsFlow/WaterfallsFlow.vue";
import emptyPage from "@/components/emptyPage.vue";
import Loading from "@/components/Loading/index.vue";
import { getCrmebCopyRight } from "@/api/api.js";
import { goShopDetail } from "@/libs/order.js";
import PageDesign from "@/subpackage/diyComponents/pageDesign.vue";
import { applyTheme } from "@/utils/theme.js";

export default {
  components: {
    pageFooter,
    editUserModal,
    PageDesign,
    couponWindow,
    waterfallsFlow,
    emptyPage,
    Loading,
  },
  // computed: mapGetters(['isLogin','cartNum']),
  computed: {
    pdHeights() {
      return { height: "100rpx" };
    },
    ...mapGetters({
      cartNum: "cartNum",
      isLogin: "isLogin",
    }),
  },
  filters: {
    coundTime(val) {
      var setTime = val * 1000;
      var nowTime = new Date();
      var rest = setTime - nowTime.getTime();
      var day = parseInt(rest / (60 * 60 * 24 * 1000));
      // var hour = parseInt(rest/(60*60*1000)%24) //小时
      return day + this.$t("day");
    },
    dateFormat: function (value) {
      return dayjs(value * 1000).format("YYYY-MM-DD");
    },
  },
  mixins: [colors],
  data() {
    return {
      currentDiyData: {},
      storeMenu: [], // 商家管理
      orderMenu: [
        {
          img: "icon-daifukuan",
          title: "待付款",
          url: "/pages/goods/order_list/index?status=0",
        },
        {
          img: "icon-daifahuo",
          title: "待发货",
          url: "/pages/goods/order_list/index?status=1",
        },
        {
          img: "icon-daishouhuo",
          title: "待收货",
          url: "/pages/goods/order_list/index?status=2",
        },
        {
          img: "icon-daipingjia",
          title: "待评价",
          url: "/pages/goods/order_list/index?status=3",
        },
        {
          img: "icon-a-shouhoutuikuan",
          title: "售后/退款",
          url: "/pages/users/user_return_list/index",
        },
      ],
      imgUrls: [],
      autoplay: true,
      circular: true,
      interval: 3000,
      duration: 500,
      isAuto: false, //没有授权的不会自动授权
      isShowAuth: false, //是否隐藏授权
      orderStatusNum: {},
      userInfo: {},
      MyMenus: [],
      sysHeight: sysHeight,
      mpHeight: 0,
      showStatus: 1,
      activeRouter: "",
      // #ifdef H5 || MP
      pageHeight: "100%",
      routineContact: 0,
      // #endif
      // #ifdef APP-PLUS
      pageHeight: app.globalData.windowHeight,
      // #endif
      // #ifdef H5
      isWeixin: Auth.isWeixin(),
      //#endif
      footerSee: false,
      my_menus_status: 0,
      business_status: 0,
      member_style: 0,
      my_banner_status: 0,
      is_diy: uni.getStorageSync("is_diy"),
      copyRightPic: require("static/images/support.png"), //版权图片
      belongIndex: 0,
      isScrolled: false,
      isFixed: false,
      product_video_status: false,
      positionTop: 0,
      sortMpTop: 0,
      sortList: [],
      curSort: 0,
      goodList: [],
      loaded: false,
      loading: false,
      isCouponShow: false,
      couponObj: {},
      site_config: "",
      configData: {},
      isFooter: false,
      entryData: { select_store_id: "", store_id: "" },
      sid: 0,
      goodPage: 1,
      bgColor: "",
      bgPic: "",
      bgTabVal: "",
    };
  },
  onLoad(option) {
    uni.hideTabBar();
    applyTheme(uni.getStorageSync("previewThemeId") || "").catch(() => {});
    let that = this;
    // #ifdef MP
    // 小程序静默授权
    if (!this.$store.getters.isLogin) {
      // Routine.getCode()
      // 	.then(code => {
      // 		Routine.silenceAuth(code).then(res => {
      // 			this.onLoadFun();
      // 		})
      // 	})
      // 	.catch(res => {
      // 		uni.hideLoading();
      // 	});
    }
    // #endif

    // #ifdef H5 || APP-PLUS
    // if (that.isLogin == false) {
    // 	toLogin();
    // }
    //获取用户信息回来后授权
    let cacheCode = this.$Cache.get("snsapi_userinfo_code");
    let res1 = cacheCode ? option.code != cacheCode : true;
    if (
      this.isWeixin &&
      option.code &&
      res1 &&
      option.scope === "snsapi_userinfo"
    ) {
      this.$Cache.set("snsapi_userinfo_code", option.code);
      Auth.auth(option.code)
        .then((res) => {
          this.getUserInfo();
        })
        .catch((err) => {});
    }
    // #endif
    // #ifdef APP-PLUS
    that.$set(that, "pageHeight", app.globalData.windowHeight);
    // #endif

    let routes = getCurrentPages(); // 获取当前打开过的页面路由数组
    let curRoute = routes[routes.length - 1].route; //获取当前页面路由
    this.activeRouter = "/" + curRoute;
    this.getCopyRight();
  },
  onReady() {
    let self = this;
    // #ifdef MP
    let info = uni.createSelectorQuery().select(".sys-head");
    info
      .boundingClientRect(function (data) {
        //data - 各种参数
        self.mpHeight = data.height;
      })
      .exec();
    // #endif
  },
  onShow: function () {
    let that = this;
    // #ifdef APP-PLUS
    uni.getSystemInfo({
      success: function (res) {
        that.pageHeight = res.windowHeight + "px";
      },
    });
    // #endif
    if (that.isLogin) {
      this.getUserInfo();
      this.setVisit();
    }
    // this.getMyMenus();
    this.getDiyData();
    this.getCopyRight();
  },
  onPullDownRefresh() {
    this.onLoadFun();
  },
  onPageScroll() {
    uni.$emit("scroll");
  },
  methods: {
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
    storeTap(id) {
      this.entryData.select_store_id = id;
      this.entryData.store_id = "";
      uni.removeStorageSync("rulesStoreId");
    },
    bindHeighta(data) {
      // #ifdef APP-PLUS
      this.sortMpTop = data.top + data.height;
      // #endif
    },
    changeSort(item, index) {
      if (this.curSort == index) return;
      this.curSort = index;
      this.sid = item.id;
      this.goodList = [];
      this.goodPage = 1;
      this.loaded = false;
      if (this.getGoodsList) this.getGoodsList();
    },
    goDetail(item) {
      goShopDetail(item, this.$store.state.app.uid).then((res) => {
        uni.navigateTo({
          url: `/pages/goods/goods_details/index?id=${item.id}`,
        });
      });
    },
    couponClose() {
      this.isCouponShow = false;
      if (!uni.getStorageSync("oldUser") && this.getNewCoupon) {
        this.getNewCoupon();
      }
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
    changeBarg(item) {
      if (!this.isLogin) {
        toLogin();
      } else {
        uni.navigateTo({
          url: `/pages/activity/goods_bargain_details/index?id=${item.id}&spid=${this.$store.state.app.uid}`,
        });
      }
    },
    changeLogin() {
      if (!this.isLogin) {
        toLogin();
      }
    },
    objToArr(data) {
      if (!data || typeof data !== "object") return [];
      let obj = Object.keys(data).sort();
      let m = obj.map((key) => data[key]);
      return m;
    },
    getDiyData() {
      let previewThemeId = uni.getStorageSync("previewThemeId");
      let data = {};
      if (previewThemeId) data.theme_id = previewThemeId;
      getThemeInfo("user", data).then((res) => {
        this.currentDiyData = res.data;
        if (this.currentDiyData.is_bg_color) {
          this.bgColor = this.currentDiyData.color_picker || "";
        }
        if (this.currentDiyData.is_bg_pic) {
          this.bgPic = this.currentDiyData.bg_pic || "";
          this.bgTabVal = this.currentDiyData.bg_tab_val || "";
        }
      });
    },
    getWechatuserinfo() {
      //#ifdef H5
      Auth.isWeixin() && Auth.toAuth("snsapi_userinfo", "/pages/user/index");
      //#endif
    },
    // 记录会员访问
    setVisit() {
      setVisit({
        url: "/pages/user/index",
      }).then((res) => {});
    },
    // 打开授权
    openAuto() {
      toLogin();
    },
    // 授权回调
    onLoadFun() {
      this.getUserInfo();
    //   this.getMyMenus();
      this.getDiyData();
      this.setVisit();
    },
    Setting: function () {
      uni.openSetting({
        success: function (res) {},
      });
    },
    // 授权关闭
    authColse: function (e) {
      this.isShowAuth = e;
    },
    // 绑定手机
    bindPhone() {
      uni.navigateTo({
        url: "/pages/users/user_phone/index",
      });
    },
    getphonenumber(e) {
      if (e.detail.errMsg == "getPhoneNumber:ok") {
        Routine.getCode()
          .then((code) => {
            let data = {
              code,
              iv: e.detail.iv,
              encryptedData: e.detail.encryptedData,
            };
            mpBindingPhone(data)
              .then((res) => {
                this.getUserInfo();
                this.$util.Tips({
                  title: res.msg,
                  icon: "success",
                });
              })
              .catch((err) => {
                return this.$util.Tips({
                  title: err,
                });
              });
          })
          .catch((error) => {
            uni.hideLoading();
          });
      }
    },
    /**
     * 获取个人用户信息
     */
    getUserInfo: function () {
      let that = this;
      getUserInfo().then((res) => {
        that.userInfo = res.data;
        this.$store.commit("UPDATE_USERINFO", res.data);
        that.$store.commit("SETUID", res.data.uid);
        that.orderMenu.forEach((item, index) => {
          switch (item.title) {
            case "待付款":
              item.num = res.data.orderStatusNum.unpaid_count;
              break;
            case "待发货":
              item.num = res.data.orderStatusNum.unshipped_count;
              break;
            case "待收货":
              item.num = res.data.orderStatusNum.received_count;
              break;
            case "待评价":
              item.num = res.data.orderStatusNum.evaluated_count;
              break;
            case "售后/退款":
              item.num = res.data.orderStatusNum.refunding_count;
              break;
          }
        });
        uni.stopPullDownRefresh();
      });
    },
    //小程序授权api替换 getUserInfo
    getUserProfile() {
      toLogin();
    },
    /**
     *
     * 获取个人中心图标
     */
    switchTab(order) {
      this.orderMenu.forEach((item, index) => {
        switch (item.title) {
          case "待付款":
            item.img = order.dfk;
            break;
          case "待发货":
            item.img = order.dfh;
            break;
          case "待收货":
            item.img = order.dsh;
            break;
          case "待评价":
            item.img = order.dpj;
            break;
          case "售后/退款":
            item.img = order.sh;
            break;
        }
      });
    },
    getMyMenus: function () {
      let that = this;
      // if (this.MyMenus.length) return;
      getMenuList().then((res) => {
        this.member_style = Number(res.data.diy_data.value);
        this.my_banner_status = res.data.diy_data.my_banner_status;
        this.my_menus_status = res.data.diy_data.my_menus_status;
        this.business_status = res.data.diy_data.business_status;
        let storeMenu = [];
        let myMenu = [];
        res.data.routine_my_menus.forEach((el, index, arr) => {
          if (
            el.url == "/pages/admin/order/index" ||
            el.url == "/pages/admin/order_cancellation/index" ||
            el.url == "/pages/admin/manage/index" ||
            el.name == "客服接待"
          ) {
            storeMenu.push(el);
          } else {
            myMenu.push(el);
          }
        });

        let order01 = {
          dfk: "icon-daifukuan",
          dfh: "icon-daifahuo",
          dsh: "icon-daishouhuo",
          dpj: "icon-daipingjia",
          sh: "icon-a-shouhoutuikuan",
        };
        let order02 = {
          dfk: "icon-daifukuan-lan",
          dfh: "icon-daifahuo-lan",
          dsh: "icon-daishouhuo-lan",
          dpj: "icon-daipingjia-lan",
          sh: "icon-shouhoutuikuan-lan",
        };
        let order03 = {
          dfk: "icon-daifukuan-ju",
          dfh: "icon-daifahuo-ju",
          dsh: "icon-daishouhuo-ju",
          dpj: "icon-daipingjia-ju",
          sh: "icon-shouhoutuikuan-ju",
        };
        let order04 = {
          dfk: "icon-daifukuan-fen",
          dfh: "icon-daifahuo-fen",
          dsh: "icon-daishouhuo-fen",
          dpj: "icon-daipingjia-fen",
          sh: "icon-shouhoutuikuan-fen",
        };
        let order05 = {
          dfk: "icon-daifukuan-lv",
          dfh: "icon-daifahuo-lv",
          dsh: "icon-daishouhuo-lv",
          dpj: "icon-daipingjia-lv",
          sh: "icon-shouhoutuikuan-lv",
        };
        if (this.member_style == 1) {
          this.switchTab(order01);
        } else if (this.member_style == 2) {
          this.switchTab(order02);
        } else if (this.member_style == 3) {
          this.switchTab(order03);
        } else if (this.member_style == 4) {
          this.switchTab(order04);
        } else if (this.member_style == 5) {
          this.switchTab(order05);
        }
        this.storeMenu = storeMenu;
        this.MyMenus = myMenu;
      });
    },
    getCopyRight() {
      getCrmebCopyRight().then((res) => {
        if (res.data.copyrightImage)
          this.copyRightPic = res.data.copyrightImage;
      });
    },
  },
};
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
