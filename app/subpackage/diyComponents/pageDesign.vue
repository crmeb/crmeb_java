<template>
  <view class="page-design" :class="bgClass">
    <view v-if="!errorNetwork" :style="colorStyle">
      <!-- #ifdef MP -->
      <view
        class="fixed z-10000"
        :style="[appletStyle]"
        v-if="myApplet && isHome"
      >
        <view
          class="myApplet w-324 h-62 text-center rd-12rpx lh-62rpx fs-24 bg--w111-fff text-w111-303133"
        >
          点击添加到我的小程序
          <text
            class="iconfont icon-ic_close2 text--w111-ccc ml-16"
            @click="myApplet = false"
          ></text>
        </view>
      </view>
      <!-- #endif -->
      <!-- 轮播搜索 -->
      <homeComb
        v-if="showHomeComb"
        :dataConfig="homeCombData"
        :belongIndex="belongIndex"
        @bindSortId="bindSortId"
        :isScrolled="isScrolled"
        @storeTap="storeTap"
      ></homeComb>

      <!-- 顶部搜索框 -->
      <headerSerch
        v-if="isHeaderSerch"
        :dataConfig="headerSerchCombData"
        :belongIndex="belongIndex"
        @storeTap="storeTap"
      ></headerSerch>

      <tabNav
        v-if="showCateNav"
        :dataConfig="cateNavData"
        @bindHeight="bindHeight"
        @bindSortId="bindSortId"
        :isFixed="isFixed && !cateNavData.stickyConfig.tabVal"
      ></tabNav>

      <view class="index">
        <!-- 自定义样式 -->
        <block v-for="(item, index) in styleConfig" :key="index">
          <view :id="item.id">
            <userInfor
              v-if="item.name == 'userInfor'"
              :dataConfig="item"
              @changeLogin="changeLogin"
            ></userInfor>
            <homeUserInfor
              v-else-if="item.name == 'member'"
              :dataConfig="item"
              @changeLogin="changeLogin"
            ></homeUserInfor>
            <newVip
              v-else-if="item.name == 'newVip'"
              :dataConfig="item"
            ></newVip>
            <!-- 文章列表 -->
            <articleList
              v-else-if="item.name == 'articleList'"
              :dataConfig="item"
            ></articleList>
            <bargain
              v-else-if="item.name == 'bargain'"
              :dataConfig="item"
              @changeBarg="changeBarg"
            ></bargain>
            <blankPage
              v-else-if="item.name == 'blankPage'"
              :dataConfig="item"
            ></blankPage>
            <combination
              v-else-if="item.name == 'combination'"
              :dataConfig="item"
            ></combination>
            <!-- 优惠券 -->
            <coupon
              v-else-if="item.name == 'coupon'"
              :dataConfig="item"
              @changeLogin="changeLogin"
            ></coupon>
            <!-- 客户服务 -->
            <customerService
              v-else-if="item.name == 'customerService'"
              :dataConfig="item"
            ></customerService>
            <!-- 商品列表 -->
            <goodList
              ref="goodLists"
              v-else-if="
                item.name == 'goodList' || item.name == 'goodRecommend'
              "
              :dataConfig="item"
              :list="goodList"
            ></goodList>
            <!-- <homeGoodRecommend
              v-else-if="
                item.name == 'goodList' || item.name == 'goodRecommend'
              "
              :dataConfig="item"
            ></homeGoodRecommend> -->
            <guide v-else-if="item.name == 'guide'" :dataConfig="item"></guide>
            <!-- 直播模块 -->
            <!-- #ifdef  MP-WEIXIN -->
            <liveBroadcast
              v-else-if="item.name == 'liveBroadcast'"
              :dataConfig="item"
            ></liveBroadcast>
            <!-- #endif -->
            <menus v-else-if="item.name == 'menus'" :dataConfig="item"></menus>
            <!-- 实时消息 -->
            <news v-else-if="item.name == 'news'" :dataConfig="item"></news>
            <!-- 图片库 -->
            <pictureCube
              v-else-if="item.name == 'pictureCube'"
              :dataConfig="item"
            ></pictureCube>
            <!-- 促销列表 -->
            <promotionList
              ref="promotionLists"
              v-else-if="item.name == 'promotionList'"
              :dataConfig="item"
              :productVideoStatus="productVideoStatus"
              :positionTop="positionTop"
            ></promotionList>
            <seckill
              v-else-if="item.name == 'seckill'"
              :dataConfig="item"
            ></seckill>
            <!-- 轮播图-->
            <swiperBg
              v-else-if="item.name == 'swiperBg'"
              :dataConfig="item"
            ></swiperBg>
            <swipers
              v-else-if="item.name == 'swipers'"
              :dataConfig="item"
            ></swipers>

            <!-- 标题 -->
            <titles
              v-else-if="item.name == 'titles'"
              :dataConfig="item"
            ></titles>
            <presale
              v-else-if="item.name == 'presale'"
              :dataConfig="item"
            ></presale>
            <pointsMall
              v-else-if="item.name == 'pointsMall'"
              :dataConfig="item"
            ></pointsMall>
            <!-- #ifndef APP -->
            <richText
              v-else-if="item.name == 'richText'"
              :dataConfig="item"
            ></richText>
            <videos
              v-else-if="item.name == 'videos'"
              :dataConfig="item"
            ></videos>
            <!-- #endif -->
            <signIn
              v-else-if="item.name == 'signIn'"
              :dataConfig="item"
            ></signIn>
            <hotspot
              v-else-if="item.name == 'hotspot'"
              :dataConfig="item"
            ></hotspot>
            <follow
              v-else-if="item.name == 'follow'"
              :dataConfig="item"
            ></follow>
            <!-- 商品详情 -->
            <productInfo
              v-else-if="item.name == 'productInfo'"
              :dataConfig="item"
              :productData="productData"
              :priceData="priceData"
              :skuList="skuList"
              :attrValue="attrValue"
              @changeSpec="onChangeSpec"
              @showSpecModal="onShowSpecModal"
              @share="onShare"
              @goActivity="onGoActivity"
            ></productInfo>
            <homeProductService
              v-else-if="item.name == 'productService'"
              :dataConfig="item"
              :productData="productData"
              :couponList="couponList"
              :activity="activity"
              :attr="attr"
              :attrTxt="attrTxt"
              :attrValue="attrValue"
              @showCoupon="onShowCoupon"
              @showSpecModal="onShowSpecModal"
              @openModal="onOpenModal"
              @goActivity="onGoActivity"
            ></homeProductService>
            <homeReviews
              v-else-if="item.name == 'reviews'"
              :dataConfig="item"
              :reply="reply"
              :replyCount="replyCount"
              :replyChance="replyChance"
              :productId="productId"
            ></homeReviews>
            <productDesc
              v-else-if="item.name == 'productDesc'"
              :dataConfig="item"
              :productData="productData"
            ></productDesc>
            <customComponent
              v-else-if="item.name == 'customComponent'"
              :dataConfig="item"
              @changeLogin="changeLogin"
            ></customComponent>
          </view>
        </block>

        <!-- 插槽：用于展示分类商品列表或其他底部内容 -->
        <slot name="bottom"></slot>

        <view class="pb-safe" :style="[pdHeights]" v-if="isFooter"></view>
        <pageFooter
          v-if="footerConfigData"
          :configData="footerConfigData"
          @newDataStatus="newDataStatus"
        ></pageFooter>
      </view>
    </view>

    <view v-else>
      <view class="error-network">
        <image :src="imgHost + '/crmebimage/theme-cate/error-network.gif'"></image>
        <view class="title">{{ '网络连接断开' }}</view>
        <view class="btn" @click="reconnect">{{ '重新连接' }}</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import pageFooter from "@/components/pageFooter/index.vue";
import { HTTP_REQUEST_URL } from "@/config/app.js";
import { useColor } from "@/composables/useColor.js";
import util from "@/utils/util.js";
// diyComponents - 同目录使用相对路径
import homeComb from "./homeComb.vue";
import headerSerch from "./headerSerch.vue";
import tabNav from "./tabNav.vue";
import userInfor from "./userInfor.vue";
import homeUserInfor from "./homeUserInfor.vue";
import newVip from "./newVip.vue";
import articleList from "./articleList.vue";
import bargain from "./bargain.vue";
import blankPage from "./blankPage.vue";
import combination from "./combination.vue";
import coupon from "./coupon.vue";
import customerService from "./customerService.vue";
import goodList from "./goodList.vue";
import guide from "./guide.vue";
import liveBroadcast from "./liveBroadcast.vue";
import menus from "./menus.vue";
import news from "./news.vue";
import pictureCube from "./pictureCube.vue";
import promotionList from "./promotionList.vue";
import seckill from "./seckill.vue";
import swiperBg from "./swiperBg.vue";
import swipers from "./swipers.vue";
import titles from "./titles.vue";
import presale from "./presale.vue";
import pointsMall from "./pointsMall.vue";
import richText from "./richText.vue";
import videos from "./videos.vue";
import signIn from "./signIn.vue";
import hotspot from "./hotspot.vue";
import follow from "./follow.vue";
import productInfo from "./productInfo.vue";
import homeProductService from "./homeProductService.vue";
import homeReviews from "./homeReviews.vue";
import productDesc from "./productDesc.vue";
import customComponent from "./customComponent.vue";

const DISABLED_COMPONENT_NAMES = ["home_paid_vip"];

const { colorStyle, colorStatus } = useColor();

const COMPONENT_NAME_MAP = {
  home_comb: "homeComb",
  search_box: "headerSerch",
  nav_bar: "tabNav",
  home_userInfor: "userInfor",
  home_user_info: "userInfor",
  home_menu: "menus",
  picture_cube: "pictureCube",
  banner: "swiperBg",
  home_news_roll: "news",
  home_title: "titles",
  z_auxiliary_box: "blankPage",
  z_auxiliary_line: "guide",
  z_ueditor: "richText",
  home_service: "customerService",
  home_goods_list: "goodList",
  home_good_recommend: "goodRecommend",
  home_product: "promotionList",
  home_seckill: "seckill",
  home_bargain: "bargain",
  home_pink: "combination",
  home_coupon: "coupon",
  home_new_list: "articleList",
  home_video: "videos",
  home_hotspot: "hotspot",
  z_wechat_attention: "follow",
  home_member: "member",
  home_product_info: "productInfo",
  home_product_service: "productService",
  home_reviews: "reviews",
  home_product_desc: "productDesc",
  home_custom_component: "customComponent",
  home_bottom_menu: "bottomMenu",
  points_mall: "pointsMall",
  sign_in: "signIn",
  wechat_live: "liveBroadcast",
};

function parseJson(value, fallback) {
  if (!value) return fallback;
  if (typeof value === "object") return value;
  try {
    return JSON.parse(value);
  } catch (e) {
    return fallback;
  }
}

function isPlainObject(value) {
  return Object.prototype.toString.call(value) === "[object Object]";
}

function looksLikeComponentCollection(value) {
  if (!value || typeof value !== "object") return false;
  const list = Array.isArray(value) ? value : Object.keys(value).map((key) => value[key]);
  return list.some((item) => item && typeof item === "object" && (item.name || item.defaultName));
}

const props = defineProps({
  // DIY配置数据
  diyData: {
    type: Object,
    default: () => ({}),
  },
  // 是否为首页（用于控制小程序添加到我的小程序提示等）
  isHome: {
    type: Boolean,
    default: false,
  },
  // 页面滚动状态
  isScrolled: {
    type: Boolean,
    default: false,
  },
  // 是否固定（用于吸顶）
  isFixed: {
    type: Boolean,
    default: false,
  },
  productData: {
    type: Object,
    default: () => ({}),
  },
  priceData: {
    type: Object,
    default: () => ({}),
  },
  skuList: {
    type: Array,
    default: () => [],
  },
  reply: {
    type: Array,
    default: () => [],
  },
  replyCount: {
    type: Number,
    default: 0,
  },
  replyChance: {
    type: [String, Number],
    default: 0,
  },
  productId: {
    type: [Number, String],
    default: 0,
  },
  goodList: {
    type: Array,
    default: () => [],
  },
  // 视频播放状态
  productVideoStatus: {
    type: Boolean,
    default: false,
  },
  // 进店规则归属门店排序位置
  belongIndex: {
    type: Number,
    default: 0,
  },
  // 网络错误状态
  errorNetwork: {
    type: Boolean,
    default: false,
  },
  couponList: {
    type: Array,
    default: () => [],
  },
  activity: {
    type: Array,
    default: () => [],
  },
  attr: {
    type: Object,
    default: () => ({}),
  },
  attrTxt: {
    type: String,
    default: "",
  },
  attrValue: {
    type: String,
    default: "",
  },
  // 微页面
  microPage: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits([
  "reconnect",
  "changeSpec",
  "showSpecModal",
  "showCoupon",
  "openModal",
  "goActivity",
  "share",
  "bindSortId",
  "bindHeight",
  "storeTap",
  "changeLogin",
  "changeBarg",
  "newDataStatus",
]);

const styleConfig = ref([]);
const homeCombData = ref({});
const headerSerchCombData = ref({});
const cateNavData = ref({});
const footerConfigData = ref(null);
const showHomeComb = ref(false);
const isHeaderSerch = ref(false);
const showCateNav = ref(false);
const bgColor = ref("");
const bgPic = ref("");
const bgTabVal = ref("");
const positionTop = ref(0);
const isFooter = ref(false);
const pdHeight = ref(0);
const myApplet = ref(true);
const getHeight = ref(util.getWXStatusHeight());
const imgHost = ref(HTTP_REQUEST_URL);

// #ifdef MP
const appletStyle = computed(() => {
  return {
    top: getHeight.value.menuButtonInfo.bottom + 8 + "px",
    right: "10px",
  };
});
// #endif
const pageStyle = computed(() => {
  return {
    backgroundColor: bgColor.value,
    backgroundImage: bgPic.value ? `url(${bgPic.value})` : "",
    minHeight: "100vh", // 确保背景铺满
  };
});
const bgClass = computed(() => {
  if (bgTabVal.value == 2) return "fullsize noRepeat";
  if (bgTabVal.value == 1) return "repeat ysize";
  return "noRepeat ysize";
});
const pdHeights = computed(() => {
  let H = `${pdHeight.value * 2 + 100}rpx`;
  return {
    height: isFooter.value ? H : "100rpx",
  };
});

function reconnect() {
  emit("reconnect");
}
function onChangeSpec(item) {
  emit("changeSpec", item);
}
function onShowSpecModal() {
  emit("showSpecModal");
}
function onShowCoupon() {
  emit("showCoupon");
}
function onOpenModal(type) {
  emit("openModal", type);
}
function onGoActivity(item) {
  emit("goActivity", item);
}
function onShare() {
  emit("share");
}
function normalizeDiyData(data) {
  let normalized = parseJson(data, data || {});
  if (!normalized || typeof normalized !== "object") return {};
  if (normalized.value === undefined && looksLikeComponentCollection(normalized)) {
    return {
      value: normalized,
    };
  }
  let value = parseJson(normalized.value, normalized.value || {});
  if (value && typeof value === "object" && value.value !== undefined) {
    const innerValue = parseJson(value.value, value.value || {});
    normalized = {
      ...normalized,
      ...value,
      value: innerValue,
    };
  } else {
    normalized = {
      ...normalized,
      value,
    };
  }
  return normalized;
}
function normalizeComponentItem(item, key) {
  const data = parseJson(item, item || {});
  if (!data || typeof data !== "object") return null;
  const name = COMPONENT_NAME_MAP[data.name] || data.defaultName || data.name;
  if (!name) return null;
  if (DISABLED_COMPONENT_NAMES.includes(name)) return null;
  return {
    ...data,
    name,
    timestamp: data.timestamp || data.num || key || 0,
    id: data.id || `id${data.timestamp || data.num || key || ""}`,
  };
}
// 对象转数组
function objToArr(data) {
  const value = parseJson(data, data || {});
  if (!value) return [];
  if (Array.isArray(value)) {
    return value
      .map((item, index) => normalizeComponentItem(item, index))
      .filter(Boolean);
  }
  if (!isPlainObject(value)) return [];
  return Object.keys(value)
    .sort((a, b) => {
      const numA = Number(a);
      const numB = Number(b);
      if (!Number.isNaN(numA) && !Number.isNaN(numB)) return numA - numB;
      return String(a).localeCompare(String(b));
    })
    .map((key) => normalizeComponentItem(value[key], key))
    .filter(Boolean);
}
function setDiyData(data) {
  const diyData = normalizeDiyData(data);
  if (!diyData) return;
  if (diyData.is_bg_color) {
    bgColor.value = diyData.color_picker;
  }
  if (diyData.is_bg_pic) {
    bgPic.value = diyData.bg_pic;
    bgTabVal.value = diyData.bg_tab_val;
  }

  let temp = [];
  // 重置状态
  showHomeComb.value = false;
  isHeaderSerch.value = false;
  showCateNav.value = false;
  footerConfigData.value = null;
  if (diyData.value) {
    let lastArr = objToArr(diyData.value);
    lastArr.forEach((item) => {
      if (item.name == "pageFoot" && !props.microPage) {
        footerConfigData.value = item;
      }
      if (item.name === "homeComb" && !item.isHide) {
        showHomeComb.value = true;
        homeCombData.value = item;
        if (item.searchConfig && item.searchConfig.tabVal) {
          positionTop.value = uni.getWindowInfo().statusBarHeight + 43;
        }
      }
      if (item.name == "headerSerch" && !item.isHide) {
        isHeaderSerch.value = true;
        headerSerchCombData.value = item;
      }
      if (item.name == "tabNav" && !item.isHide) {
        showCateNav.value = true;
        cateNavData.value = item;
      }
      if (!item.isHide) {
        temp.push(item);
      }
    });

    // 排序
    temp.sort((a, b) => a.timestamp - b.timestamp);
    styleConfig.value = temp;
  }
}
function bindSortId(item, data) {
  emit("bindSortId", item, data);
}
function bindHeight(data) {
  emit("bindHeight", data);
}
function storeTap(id) {
  emit("storeTap", id);
}
function changeLogin() {
  emit("changeLogin");
}
function changeBarg(item) {
  emit("changeBarg", item);
}
function newDataStatus(val, num) {
  isFooter.value = val ? true : false;
  pdHeight.value = num;
  emit("newDataStatus", { val, num });
}

watch(
  () => props.diyData,
  (val) => {
    if (val && Object.keys(val).length > 0) {
      setDiyData(val);
    } else {
      // 重置数据
      styleConfig.value = [];
      homeCombData.value = {};
      headerSerchCombData.value = {};
      cateNavData.value = {};
      footerConfigData.value = null;
      showHomeComb.value = false;
      isHeaderSerch.value = false;
      showCateNav.value = false;
      bgColor.value = "";
      bgPic.value = "";
      bgTabVal.value = "";
    }
  },
  { deep: true, immediate: true },
);
</script>

<style lang="scss" scoped>
.page-design {
  overflow-y: scroll;
  overflow-x: hidden;
  min-height: 100vh;
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
</style>
