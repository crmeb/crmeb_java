<template>
  <view :data-theme="theme" :style="colorStyle">
    <view class="cart_nav" :style="'height:' + navH + 'rpx;'">
      <view class="navbarCon acea-row">
        <!-- #ifndef APP-PLUS -->
        <view
          id="home"
          class="home acea-row row-center-wrapper iconfont icon-shouye4 nav_back"
          :style="{ top: homeTop + 'rpx' }"
          @tap="returns"
        >
        </view>
        <view class="nav_title" :style="{ top: homeTop + 'rpx' }">购物车</view>
        <!-- <view
          class="right_select"
          :style="{ top: homeTop + 'rpx' }"
          @tap="showNav"
        >
          <text class="iconfont icon-gengduo2"></text>
        </view> -->
        <!-- #endif -->
      </view>
    </view>
    <view
      class="dialog_nav"
      :style="'top:' + navH + 'rpx;'"
      v-show="currentPage"
    >
      <view
        class="dialog_nav_item"
        v-for="(item, index) in selectNavList"
        :key="index"
        @click="linkPage(item.url)"
      >
        <text class="iconfont" :class="item.icon"></text>
        <text class="pl-20">{{ item.name }}</text>
      </view>
    </view>
    <view
      class="shoppingCart copy-data"
      :style="'top:' + navH + 'rpx;'"
      @touchstart="touchStart"
    >
      <view class="labelNav acea-row row-around">
        <view class="item"
          ><text class="iconfont icon-xuanzhong"></text>100%正品保证</view
        >
        <view class="item"
          ><text class="iconfont icon-xuanzhong"></text>所有商品精挑细选</view
        >
        <view class="item"
          ><text class="iconfont icon-xuanzhong"></text>售后无忧</view
        >
      </view>
      <view class="borRadius14 cartBox">
        <view
          v-if="
            (cartList.valid.length === 0 && cartList.invalid.length === 0) ||
            cartList.valid.length > 0
          "
          class="nav acea-row row-between-wrapper"
        >
          <view
            >购物数量 <text class="num font_color">{{ cartCount }}</text></view
          >
          <view
            v-if="cartList.valid.length > 0 || cartList.invalid.length > 0"
            class="administrate acea-row row-center-wrapper"
            @click="manage"
            >{{ footerswitch ? "管理" : "取消" }}
          </view>
        </view>
        <view
          v-if="cartList.valid.length > 0 || cartList.invalid.length > 0"
          class="p-30"
        >
          <view class="list">
            <checkbox-group @change="checkboxChange">
              <block v-for="(item, index) in cartList.valid" :key="index">
                <view class="item acea-row row-between-wrapper">
                  <!-- #ifndef MP -->
                  <checkbox
                    :value="item.id.toString()"
                    :checked="item.checked"
                    :disabled="!item.attrStatus && footerswitch"
                    color="#ffffff"
                    backgroundColor="#ffffff"
                    activeBackgroundColor="var(--view-theme, #E93323)"
                    activeBorderColor="var(--view-theme, #E93323)"
                    iconColor="#ffffff"
                    style="transform: scale(0.9)"
                  />
                  <!-- #endif -->
                  <!-- #ifdef MP -->
                  <checkbox
                    :value="item.id"
                    :checked="item.checked"
                    :disabled="!item.attrStatus && footerswitch"
                  />
                  <!-- #endif -->
                  <view
                    @click="goGoodsDetails(item.productId)"
                    class="picTxt acea-row row-between-wrapper"
                  >
                    <view class="pictrue">
                      <image :src="item.image"></image>
                    </view>
                    <view class="text">
                      <view
                        class="line1"
                        :class="item.attrStatus ? '' : 'reColor'"
                        >{{ item.storeName }}
                      </view>
                      <view class="infor line1" v-if="item.suk"
                        >属性：{{ item.suk }}</view
                      >
                      <view class="money mt-28" v-if="item.attrStatus"
                        >￥{{ item.price }}
                      </view>
                      <view
                        class="reElection acea-row row-between-wrapper"
                        v-else
                      >
                        <view class="title">请重新选择商品规格</view>
                        <view
                          class="reBnt cart-color acea-row row-center-wrapper"
                          @click.stop="reElection(item)"
                        >
                          重选
                        </view>
                      </view>
                    </view>
                    <view
                      class="carnum acea-row row-center-wrapper"
                      v-if="item.attrStatus"
                    >
                      <view
                        class="reduce"
                        :class="item.numSub ? 'on' : ''"
                        @click.stop="subCart(index)"
                        >-</view
                      >
                      <view class="num">{{ item.cartNum }}</view>
                      <view
                        class="plus"
                        :class="item.numAdd ? 'on' : ''"
                        @click.stop="addCart(index)"
                        >+</view
                      >
                    </view>
                  </view>
                </view>
              </block>
            </checkbox-group>
          </view>
          <view
            v-if="cartList.invalid.length > 0"
            class="invalidGoods borRadius14"
            :style="
              cartList.valid.length === 0 && cartList.invalid.length > 0
                ? 'position: relative;z-index: 111;top: -120rpx;'
                : 'position: static;'
            "
          >
            <view class="goodsNav acea-row row-between-wrapper">
              <view
                v-if="cartList.invalid.length > 1 || cartList.valid.length > 0"
                @click="goodsOpen"
              >
                <text
                  class="iconfont"
                  :class="
                    goodsHidden == true ? 'icon-xiangxia' : 'icon-xiangshang'
                  "
                ></text
                >失效商品
              </view>
              <view v-else> 失效商品 </view>
              <view class="del" @click="unsetCart"
                ><text class="iconfont icon-shanchu1"></text>清空</view
              >
            </view>
            <view class="goodsList" :hidden="goodsHidden">
              <block v-for="(item, index) in cartList.invalid" :key="index">
                <view class="item acea-row row-between-wrapper">
                  <view class="invalid">失效</view>
                  <view class="picTxt acea-row row-between-wrapper">
                    <view class="pictrue">
                      <image :src="item.image"></image>
                    </view>
                    <view class="text acea-row row-column-between">
                      <view class="line1 name">{{ item.storeName }}</view>
                      <view class="infor line1" v-if="item.suk"
                        >属性：{{ item.suk }}</view
                      >
                      <view class="acea-row row-between-wrapper">
                        <view class="end">该商品已失效</view>
                      </view>
                    </view>
                  </view>
                </view>
              </block>
            </view>
          </view>
          <!-- #ifdef H5 -->
          <view style="height: 240rpx"></view>
          <!-- #endif -->
          <!-- #ifdef APP-PLUS -->
          <view style="height: 300rpx"></view>
          <!-- #endif -->
          <!-- #ifdef MP -->
          <view style="height: 300rpx"></view>
          <!-- #endif -->
          <view
            class="loadingicon acea-row row-center-wrapper"
            v-if="cartList.invalid.length && loadend"
          >
            <text
              class="loading iconfont icon-jiazai"
              :hidden="loadingInvalid == false"
            ></text
            >{{ loadTitleInvalid }}
          </view>
        </view>
        <view
          class="noCart"
          v-if="
            (cartList.valid.length == 0 &&
              cartList.invalid.length == 0 &&
              canShow) ||
            !isLogin
          "
        >
          <view class="pictrue">
            <image
              :src="urlDomain + 'crmebimage/perset/staticImg/noCart.png'"
            ></image>
          </view>
          <!-- 推荐商品 -->
          <recommend ref="recommendIndex"></recommend>
          <!-- #ifdef H5 -->
          <view style="height: 120rpx"></view>
          <!-- #endif -->
        </view>
      </view>
    </view>
    <!-- <view style="height:260rpx;"></view> -->
    <view
      class="footer acea-row row-between-wrapper"
      v-if="cartList.valid.length > 0"
      :class="bottomNavigationIsCustom ? 'bottom-custom' : ''"
    >
      <view>
        <checkbox-group @change="checkboxAllChange">
          <checkbox
            value="all"
            :checked="!!isAllSelect"
            color="#ffffff"
            backgroundColor="#ffffff"
            activeBackgroundColor="var(--view-theme, #E93323)"
            activeBorderColor="var(--view-theme, #E93323)"
            iconColor="#ffffff"
            style="transform: scale(0.9)"
          />
          <text class="checkAll">全选({{ selectValue.length }})</text>
        </checkbox-group>
      </view>
      <view class="money acea-row row-middle" v-if="footerswitch == true">
        <text class="price-color">￥{{ selectCountPrice }}</text>
        <form @submit="subOrder" report-submit="true">
          <button class="placeOrder bg_color" formType="submit">
            立即下单
          </button>
        </form>
      </view>
      <view class="button acea-row row-middle" v-else>
        <form @submit="subCollect" report-submit="true">
          <button class="btn_cart_color" formType="submit">收藏</button>
        </form>
        <form @submit="subDel" report-submit="true">
          <button class="bnt" formType="submit">删除</button>
        </form>
      </view>
    </view>
    <productWindow
      v-if="isOpenAttr"
      :attr="attr"
      :isShow="1"
      :iSplus="1"
      :iScart="1"
      @myevent="onMyEvent"
      @ChangeAttr="ChangeAttr"
      @ChangeCartNum="ChangeCartNum"
      @attrVal="attrVal"
      @iptCartNum="iptCartNum"
      @goCat="reGoCat"
      id="product-window"
    >
    </productWindow>
    <view class="uni-p-b-96"></view>
    <view class="uni-p-b-98"></view>
    <pageFooter></pageFooter>
  </view>
</template>

<script setup>
import {
  ref,
  reactive,
  computed,
  onMounted,
  nextTick,
  getCurrentInstance,
} from "vue";
import { onLoad, onReady, onShow, onReachBottom } from "@dcloudio/uni-app";
import pageFooter from "@/components/pageFooter/index.vue";
// #ifdef APP-PLUS
let sysHeight = uni.getSystemInfoSync().statusBarHeight + "px";
// #endif
// #ifndef APP-PLUS
let sysHeight = 0;
// #endif
import {
  getCartList,
  getCartCounts,
  changeCartNum,
  cartDel,
  getResetCart,
} from "@/api/order.js";
import { collectAll, getProductDetail } from "@/api/store.js";
import { getShare } from "@/api/public.js";
import recommend from "@/components/recommend/index.vue";
import productWindow from "@/components/productWindow/index.vue";
import animationType from "@/utils/animationType.js";
import { Debounce } from "@/utils/validate.js";
import { useColor } from "@/composables/useColor.js";
import util from "@/utils/util.js";
import Cache from "@/utils/cache.js";
import * as Order from "@/libs/order.js";
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from "pinia";

const { proxy } = getCurrentInstance();
const app = getApp();
const appStore = useAppStore();
const { isLogin, bottomNavigationIsCustom } = storeToRefs(appStore);
const { colorStyle } = useColor();

// 子组件引用
const recommendIndex = ref(null);

const urlDomain = ref(Cache.get("imgHost"));
const cartCount = ref(0);
const goodsHidden = ref(false);
const footerswitch = ref(true);
const hostProduct = ref([]);
const cartList = ref({ valid: [], invalid: [] });
const isAllSelect = ref(false); //全选
const selectValue = ref([]); //选中的数据
const selectCountPrice = ref(0.0);
const isAuto = ref(false); //没有授权的不会自动授权
const isShowAuth = ref(false); //是否隐藏授权
const hotScroll = ref(false);
const hotPage = ref(1);
const hotLimit = ref(10);
const loading = ref(false);
const loadend = ref(false);
const loadTitle = ref("加载更多");
const page = ref(1);
const limit = ref(20);
const loadingInvalid = ref(false);
const loadendInvalid = ref(false);
const loadTitleInvalid = ref("加载更多");
const pageInvalid = ref(1);
const limitInvalid = ref(20);
const attr = reactive({
  cartAttr: false,
  productAttr: [],
  productSelect: {},
});
const productValue = ref([]); //系统属性
const productInfo = ref({});
const attrValue = ref(""); //已选属性
const attrTxt = ref("请选择"); //属性页面提示
const cartId = ref(0);
const product_id = ref(0);
const canShow = ref(false);
const configApi = ref({}); //分享类容配置
const theme = ref(app.globalData.theme);
const navH = ref("");
const homeTop = ref(20);
const currentPage = ref(false);
const selectNavList = ref([
  { name: "首页", icon: "icon-shouye8", url: "/pages/index/index" },
  {
    name: "搜索",
    icon: "icon-sousuo6",
    url: "/pages/goods/goods_search/index",
  },
  {
    name: "我的收藏",
    icon: "icon-shoucang3",
    url: "/pages/users/user_goods_collection/index",
  },
  { name: "个人中心", icon: "icon-gerenzhongxin1", url: "/pages/user/index" },
]);
const isOpenAttr = ref(false); // 是否允许打开规格弹窗

onLoad(() => {
  // #ifdef MP
  navH.value = getNavHeight();
  // #endif
  // #ifndef MP
  navH.value = 96;
  // #endif
  // #ifdef H5
  shareApi();
  // #endif
});

onReady(() => {
  nextTick(function () {
    // #ifdef MP
    const menuButton = uni.getMenuButtonBoundingClientRect();
    const query = uni.createSelectorQuery().in(proxy);
    query
      .select("#home")
      .boundingClientRect((data) => {
        homeTop.value = menuButton.top * 2 + menuButton.height - data.height;
      })
      .exec();
    // #endif
  });
});

onShow(() => {
  canShow.value = true;
  if (isLogin.value) {
    getIndex();
  } else {
    cartList.value = { valid: [], invalid: [] };
  }
});

onReachBottom(() => {
  if (loadend.value) {
    getInvalidList();
  }
  if (cartList.value.valid.length == 0 && cartList.value.invalid.length == 0) {
    recommendIndex.value.get_host_product();
  }
});

function getNavHeight() {
  const cachedHeight = Number(app.globalData.navHeight);
  if (cachedHeight > 0) return cachedHeight;

  const windowInfo =
    typeof uni.getWindowInfo === "function" ? uni.getWindowInfo() : {};
  const statusBarHeight = Number(windowInfo.statusBarHeight || 0);
  const windowWidth = Number(windowInfo.windowWidth || 375);

  if (statusBarHeight > 0 && windowWidth > 0) {
    return statusBarHeight * (750 / windowWidth) + 91;
  }

  return 96;
}

function goGoodsDetails(productId) {
  uni.navigateTo({
    url: "/pages/goods/goods_details/index?id=" + productId,
  });
}

//首次进入加载的接口
function getIndex() {
  hotPage.value = 1;
  hostProduct.value = [];
  hotScroll.value = false;
  loadend.value = false;
  page.value = 1;
  cartList.value.valid = [];
  getCartListFn();
  loadendInvalid.value = false;
  pageInvalid.value = 1;
  cartList.value.invalid = [];
  getInvalidList();
  footerswitch.value = true;
  hotScroll.value = false;
  hotPage.value = 1;
  hotLimit.value = 10;
  cartList.value = { valid: [], invalid: [] };
  isAllSelect.value = false;
  selectValue.value = [];
  selectCountPrice.value = 0.0;
  cartCount.value = 0;
  isShowAuth.value = false;
}

// 修改购物车
function reGoCat() {
  let productSelect = productValue.value[attrValue.value];
  if (attr.productAttr.length && productSelect === undefined)
    return util.Tips({ title: "产品库存不足，请选择其它" });

  let q = {
    id: cartId.value,
    productId: product_id.value,
    num: attr.productSelect.cart_num,
    unique:
      attr.productSelect !== undefined
        ? attr.productSelect.unique
        : productInfo.value.id,
  };
  getResetCart(q)
    .then(function (res) {
      attr.cartAttr = false;
      isOpenAttr.value = false;
      util.Tips({
        title: "添加购物车成功",
        success: () => {
          loadend.value = false;
          page.value = 1;
          cartList.value.valid = [];
          getCartListFn();
          getCartNum();
        },
      });
    })
    .catch((res) => {
      return util.Tips({ title: res });
    });
}

function onMyEvent() {
  attr.cartAttr = false;
  isOpenAttr.value = false;
}

function reElection(item) {
  getGoodsDetails(item);
}

/**
 * 获取产品详情
 */
function getGoodsDetails(item) {
  uni.showLoading({ title: "加载中", mask: true });
  cartId.value = item.id;
  product_id.value = item.productId;
  getProductDetail(item.productId)
    .then((res) => {
      uni.hideLoading();
      attr.cartAttr = true;
      isOpenAttr.value = true;
      productInfo.value = res.data.productInfo;
      productValue.value = res.data.productValue;
      let pa = res.data.productAttr.map((item) => ({
        attrName: item.attrName,
        attrValues: item.attrValues.split(","),
        id: item.id,
        isDel: item.isDel,
        productId: item.productId,
        type: item.type,
        optionList: item.optionList || [],
        isShowImage: item.isShowImage,
      }));
      attr.productAttr = pa;
      DefaultSelect();
    })
    .catch((err) => {
      uni.hideLoading();
    });
}

/**
 * 属性变动赋值
 */
function ChangeAttr(res) {
  let productSelect = productValue.value[res];
  if (productSelect && productSelect.stock > 0) {
    attr.productSelect.image = productSelect.image;
    attr.productSelect.price = productSelect.price;
    attr.productSelect.stock = productSelect.stock;
    attr.productSelect.unique = productSelect.id;
    attr.productSelect.isShow = productSelect.isShow;
    if (!attr.productSelect.isShow) {
      attr.productSelect.stock = 0;
      util.Tips({ title: "请重新选择其它规格" });
    }
    attr.productSelect.cart_num = 1;
    attrValue.value = res;
    attrTxt.value = "已选择";
  } else {
    attr.productSelect.image = productInfo.value.image;
    attr.productSelect.price = productInfo.value.price;
    attr.productSelect.stock = 0;
    attr.productSelect.unique = productInfo.value.id;
    attr.productSelect.cart_num = 0;
    attrValue.value = "";
    attrTxt.value = "请选择";
  }
}

/**
 * 默认选中属性
 */
function DefaultSelect() {
  let productAttr = attr.productAttr;
  let value = [];
  const sortedArray = Object.entries(productValue.value)
    .sort(([, a], [, b]) => a.id - b.id)
    .map(([key, val]) => ({ key, ...val }));
  for (let i = 0; i < sortedArray.length; i++) {
    const attrItem = sortedArray[i];
    if (attrItem.stock > 0 && attrItem.isShow) {
      if (value.length == 0)
        value = productAttr.length ? attrItem.key.split(",") : [];
      if (attrItem.isDefault) {
        value = productAttr.length ? attrItem.key.split(",") : [];
        break;
      }
    }
  }
  for (let i = 0; i < productAttr.length; i++) productAttr[i].index = value[i];
  let productSelect = productValue.value[value.join(",")];
  if (productSelect && productAttr.length) {
    attr.productSelect.storeName = productInfo.value.storeName;
    attr.productSelect.image = productSelect.image;
    attr.productSelect.price = productSelect.price;
    attr.productSelect.stock = productSelect.stock;
    attr.productSelect.unique = productSelect.id;
    attr.productSelect.cart_num = 1;
    attrValue.value = value.join(",");
    attrTxt.value = "已选择";
  } else if (!productSelect && productAttr.length) {
    attr.productSelect.storeName = productInfo.value.storeName;
    attr.productSelect.image = productInfo.value.image;
    attr.productSelect.price = productInfo.value.price;
    attr.productSelect.stock = 0;
    attr.productSelect.unique = productInfo.value.id;
    attr.productSelect.cart_num = 0;
    attrValue.value = "";
    attrTxt.value = "请选择";
  } else if (!productSelect && !productAttr.length) {
    attr.productSelect.storeName = productInfo.value.storeName;
    attr.productSelect.image = productInfo.value.image;
    attr.productSelect.price = productInfo.value.price;
    attr.productSelect.stock = productInfo.value.stock;
    attr.productSelect.unique = productInfo.value.id || "";
    attr.productSelect.cart_num = 1;
    attrValue.value = "";
    attrTxt.value = "请选择";
  }
}

function attrVal(val) {
  attr.productAttr[val.indexw].index =
    attr.productAttr[val.indexw].attrValues[val.indexn];
}

/**
 * 购物车数量加和数量减
 */
function ChangeCartNum(changeValue) {
  let productSelect = productValue.value[attrValue.value];
  if (productSelect === undefined && !attr.productAttr.length)
    productSelect = attr.productSelect;
  if (productSelect === undefined) return;
  let stock = productSelect.stock || 0;
  let num = attr.productSelect;
  if (changeValue) {
    num.cart_num++;
    if (num.cart_num > stock) {
      attr.productSelect.cart_num = stock ? stock : 1;
    }
  } else {
    num.cart_num--;
    if (num.cart_num < 1) {
      attr.productSelect.cart_num = 1;
    }
  }
}

/**
 * 购物车手动填写（规格弹窗）
 */
function iptCartNumAttr(e) {
  attr.productSelect.cart_num = e;
}

function subDel(event) {
  let sv = selectValue.value;
  if (sv.length > 0)
    cartDel(sv).then((res) => {
      loadend.value = false;
      page.value = 1;
      cartList.value.valid = [];
      getCartListFn();
      getCartNum();
    });
  else return util.Tips({ title: "请选择产品" });
}

function getSelectValueProductId() {
  let validList = cartList.value.valid;
  let sv = selectValue.value;
  let productId = [];
  if (sv.length > 0) {
    for (let index in validList) {
      if (inArray(validList[index].id, sv)) {
        productId.push(validList[index].productId);
      }
    }
  }
  return productId;
}

function subCollect(event) {
  let sv = selectValue.value;
  if (sv.length > 0) {
    collectAll(getSelectValueProductId())
      .then((res) => {
        return util.Tips({ title: "收藏成功", icon: "success" });
      })
      .catch((err) => {
        return util.Tips({ title: err });
      });
  } else {
    return util.Tips({ title: "请选择产品" });
  }
}

// 立即下单
const subOrder = Debounce(function (event) {
  let sv = selectValue.value;
  if (sv.length > 0) {
    getPreOrder();
  } else {
    return util.Tips({ title: "请选择产品" });
  }
});

/**
 * 预下单
 */
function getPreOrder() {
  let shoppingCartId = selectValue.value.map((item) => ({
    shoppingCartId: Number(item),
  }));
  Order.getPreOrder("shoppingCart", shoppingCartId);
}

function checkboxAllChange(event) {
  let value = event.detail.value;
  if (value.length > 0) setAllSelectValue(1);
  else setAllSelectValue(0);
}

function setAllSelectValue(status) {
  let sv = [];
  let valid = cartList.value.valid;
  if (valid.length > 0) {
    let newValid = valid.map((item) => {
      if (status) {
        if (footerswitch.value) {
          if (item.attrStatus) {
            item.checked = true;
            sv.push(item.id);
          } else item.checked = false;
        } else {
          item.checked = true;
          sv.push(item.id);
        }
        isAllSelect.value = true;
      } else {
        item.checked = false;
        isAllSelect.value = false;
      }
      return item;
    });
    cartList.value.valid = newValid;
    selectValue.value = sv;
    switchSelect();
  }
}

function checkboxChange(event) {
  let value = event.detail.value;
  let valid = cartList.value.valid;
  let arr1 = [];
  let arr2 = [];
  let arr3 = [];
  let newValid = valid.map((item) => {
    if (inArray(item.id, value)) {
      if (footerswitch.value) {
        if (item.attrStatus) {
          item.checked = true;
          arr1.push(item);
        } else item.checked = false;
      } else {
        item.checked = true;
        arr1.push(item);
      }
    } else {
      item.checked = false;
      arr2.push(item);
    }
    return item;
  });
  if (footerswitch.value) {
    arr3 = arr2.filter((item) => !item.attrStatus);
  }
  cartList.value.valid = newValid;
  isAllSelect.value = newValid.length === arr1.length + arr3.length;
  selectValue.value = value;
  switchSelect();
}

function inArray(search, array) {
  for (let i in array) {
    if (array[i] == search) return true;
  }
  return false;
}

function switchSelect() {
  let validList = cartList.value.valid;
  let sv = selectValue.value;
  let total = 0.0;
  if (sv.length < 1) {
    selectCountPrice.value = total;
  } else {
    for (let index in validList) {
      if (inArray(validList[index].id, sv)) {
        total = util.$h.Add(
          total,
          util.$h.Mul(validList[index].cartNum, validList[index].price),
        );
      }
    }
    selectCountPrice.value = total;
  }
}

/**
 * 购物车手动填写（列表页）
 */
function iptCartNum(index) {
  let item = cartList.value.valid[index];
  if (item.cartNum) {
    setCartNum(item.id, item.cartNum);
  }
  switchSelect();
}

function blurInput(index) {
  let item = cartList.value.valid[index];
  if (!item.cartNum) {
    item.cartNum = 1;
    cartList.value.valid = [...cartList.value.valid];
  }
}

const subCart = Debounce(function (index) {
  let status = false;
  let item = cartList.value.valid[index];
  item.cartNum = Number(item.cartNum) - 1;
  if (item.cartNum < 1) status = true;
  if (item.cartNum <= 1) {
    item.cartNum = 1;
    item.numSub = true;
  } else {
    item.numSub = false;
    item.numAdd = false;
  }
  if (false == status) {
    setCartNum(item.id, item.cartNum, function (data) {
      cartList.value.valid[index] = item;
      switchSelect();
      getCartNum();
    });
  }
});

const addCart = Debounce(function (index) {
  let item = cartList.value.valid[index];
  item.cartNum = Number(item.cartNum) + 1;
  if (item.cartNum < item.stock) {
    item.numAdd = false;
    item.numSub = false;
    setCartNum(item.id, item.cartNum, function (data) {
      cartList.value.valid[index] = item;
      switchSelect();
      getCartNum();
    });
  } else if (item.cartNum === item.stock) {
    item.numAdd = true;
    item.numSub = false;
    setCartNum(item.id, item.cartNum, function (data) {
      cartList.value.valid[index] = item;
      switchSelect();
      getCartNum();
    });
  } else {
    item.cartNum = item.stock;
    item.numAdd = true;
    item.numSub = false;
  }
});

//购物车数量变化
function setCartNum(cId, cNum, successCallback) {
  changeCartNum(cId, cNum).then((res) => {
    successCallback && successCallback(res.data);
  });
}

function getCartNum() {
  getCartCounts(true, "total").then((res) => {
    cartCount.value = res.data.count;
  });
}

function getCartData(data) {
  return new Promise((resolve, reject) => {
    getCartList(data)
      .then((res) => {
        resolve(res.data);
      })
      .catch(function (err) {
        loading.value = false;
        canShow.value = true;
        util.Tips({ title: err });
      });
  });
}

async function getCartListFn() {
  uni.showLoading({ title: "加载中", mask: true });
  let data = { page: page.value, limit: limit.value, isValid: true };
  getCartCounts(true, "total").then(async (c) => {
    cartCount.value = c.data.count;
    for (let i = 0; i < Math.ceil(cartCount.value / limit.value); i++) {
      let cl = await getCartData(data);
      let valid = cl.list;
      let validList = util.SplitArray(valid, cartList.value.valid);
      let sv = [];
      if (validList.length > 0) {
        for (let index in validList) {
          validList[index].numSub = validList[index].cartNum == 1;
          let stock = validList[index].stock ? validList[index].stock : 0;
          validList[index].numAdd =
            validList[index].cartNum == stock ||
            validList[index].cartNum == validList[index].stock;
          if (validList[index].attrStatus) {
            validList[index].checked = true;
            sv.push(validList[index].id);
          } else {
            validList[index].checked = false;
          }
        }
      }
      cartList.value.valid = validList;
      data.page += 1;
      selectValue.value = sv;
      let newArr = validList.filter((item) => item.attrStatus);
      isAllSelect.value = newArr.length == sv.length && newArr.length;
      switchSelect();
    }
    loading.value = false;
    canShow.value = true;
    uni.hideLoading();
  });
}

function getInvalidList() {
  if (loadendInvalid.value) return false;
  if (loadingInvalid.value) return false;
  let data = {
    page: pageInvalid.value,
    limit: limitInvalid.value,
    isValid: false,
  };
  getCartList(data)
    .then((res) => {
      let invalid = res.data.list;
      let isEnd = invalid.length < limitInvalid.value;
      let invalidList = util.SplitArray(invalid, cartList.value.invalid);
      cartList.value.invalid = invalidList;
      loadendInvalid.value = isEnd;
      loadTitleInvalid.value = isEnd ? "我也是有底线的~" : "加载更多";
      pageInvalid.value = pageInvalid.value + 1;
      loadingInvalid.value = false;
    })
    .catch((res) => {
      loadingInvalid.value = false;
      loadTitleInvalid.value = "加载更多";
    });
}

function goodsOpen() {
  goodsHidden.value = !goodsHidden.value;
}

function manage() {
  footerswitch.value = !footerswitch.value;
  let arr1 = [];
  let arr2 = [];
  let newValid = cartList.value.valid.map((item) => {
    if (footerswitch.value) {
      if (item.attrStatus) {
        if (item.checked) arr1.push(item.id);
      } else {
        item.checked = false;
        arr2.push(item);
      }
    } else {
      if (item.checked) arr1.push(item.id);
    }
    return item;
  });
  cartList.value.valid = newValid;
  if (footerswitch.value) {
    isAllSelect.value = newValid.length === arr1.length + arr2.length;
  } else {
    isAllSelect.value = newValid.length === arr1.length;
  }
  selectValue.value = arr1;
  switchSelect();
}

function unsetCart() {
  let ids = [];
  for (let i = 0, len = cartList.value.invalid.length; i < len; i++) {
    ids.push(cartList.value.invalid[i].id);
  }
  cartDel(ids)
    .then((res) => {
      util.Tips({ title: "清除成功" });
      cartList.value.invalid = [];
      getHostProduct();
    })
    .catch((res) => {});
}

function shareApi() {
  getShare().then((res) => {
    configApi.value = res.data;
    // #ifdef H5
    setOpenShare(res.data);
    // #endif
  });
}

// 微信分享；
function setOpenShare(data) {
  // #ifdef H5
  if (proxy.$wechat.isWeixin()) {
    let configAppMessage = {
      desc: data.synopsis,
      title: data.title,
      link: location.href,
      imgUrl: data.img,
    };
    proxy.$wechat.wechatEvevt(
      ["updateAppMessageShareData", "updateTimelineShareData"],
      configAppMessage,
    );
  }
  // #endif
}

function returns() {
  uni.switchTab({ url: "/pages/index/index" });
}

function showNav() {
  currentPage.value = !currentPage.value;
}

function linkPage(url) {
  if (url == "/pages/index/index" || url == "/pages/user/index") {
    uni.switchTab({ url });
  } else {
    uni.navigateTo({
      animationType: animationType.type,
      animationDuration: animationType.duration,
      url,
    });
  }
  currentPage.value = false;
}

function touchStart() {
  currentPage.value = false;
}
</script>

<style scoped lang="scss">
.invalidClas {
  position: relative;
  z-index: 111;
  top: -120rpx;
}

.invalidClasNO {
  position: static;
  margin-top: 15px;
}

.cartBox {
  // background-color: #fff;
}

.cart_nav {
  position: fixed;
  @include main_bg_color(theme);
  top: 0;
  left: 0;
  z-index: 99;
  width: 100%;
}

.navbarCon {
  position: absolute;
  bottom: 0;
  height: 100rpx;
  width: 100%;
}

.nav_back {
  color: #fff;
  position: fixed;
  left: 20rpx;
  font-size: 32rpx;
  text-align: center;
  line-height: 58rpx;
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

.nav_title {
  width: 200rpx;
  height: 58rpx;
  line-height: 58rpx;
  color: #fff;
  font-size: 36rpx;
  position: fixed;
  text-align: center;
  left: 0;
  right: 0;
  margin: auto;
}

.right_select {
  position: fixed;
  right: 20rpx;
  color: #fff;
  text-align: center;
  line-height: 58rpx;
}

.dialog_nav {
  position: fixed;
  right: 14rpx;
  width: 240rpx;
  background: #ffffff;
  box-shadow: 0px 0px 16rpx rgba(0, 0, 0, 0.08);
  z-index: 999;
  border-radius: 14rpx;

  &::before {
    content: "";
    width: 0;
    height: 0;
    position: absolute;
    right: 8px;
    top: -9px;
    border-bottom: 10px solid #fff;
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
    background-color: e4df0;
  }

  &::after {
    content: "";
    position: absolute;
    width: 86px;
    height: 1px;
    background-color: #eeeeee;
    bottom: 0;
    right: 0;
  }
}

.pl-20 {
  padding-left: 20rpx;
}

.shoppingCart {
  /* #ifdef H5 */
  // padding-bottom: 0;
  // padding-bottom: constant(safe-area-inset-bottom);
  // padding-bottom: env(safe-area-inset-bottom);
  /* #endif */
  position: absolute;
  width: 100%;
}

.shoppingCart .labelNav {
  height: 178rpx;
  padding: 30rpx 30rpx 0;
  font-size: 22rpx;
  color: #fff;
  width: 100%;
  box-sizing: border-box;
  @include main_bg_color(theme);
  z-index: 5;
}

.shoppingCart .labelNav .item .iconfont {
  font-size: 25rpx;
  margin-right: 10rpx;
}

.shoppingCart .nav {
  width: 92%;
  height: 90rpx;
  background-color: #fff;
  padding: 0 24rpx;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  font-size: 28rpx;
  color: #282828;
  margin: -90rpx auto 0;
  z-index: 6;
  border-top-left-radius: 14rpx;
  border-top-right-radius: 14rpx;
}

.shoppingCart .nav .num {
  margin-left: 12rpx;
}

.shoppingCart .nav .administrate {
  font-size: 28rpx;
  color: #333333;
}

.shoppingCart .noCart {
  // margin-top: 171rpx;
  background-color: #fff;
  padding-top: 0.1rpx;
}

.shoppingCart .noCart .pictrue {
  width: 414rpx;
  height: 336rpx;
  margin: 78rpx auto 56rpx auto;
}

.shoppingCart .noCart .pictrue image {
  width: 100%;
  height: 100%;
}

.shoppingCart .list {
  width: 100%;
  // margin-top: 178rpx;
  /* #ifdef MP */
  // margin-bottom:120rpx;
  /* #endif */
  /* #ifndef MP */
  // margin-bottom:240rpx;
  /* #endif */
  overflow: hidden;
  border-bottom-left-radius: 14rpx;
  border-bottom-right-radius: 14rpx;
}

.shoppingCart .list .item {
  padding: 24rpx;
  background-color: #fff;
}

.shoppingCart .list .item .picTxt {
  width: 582rpx;
  position: relative;
}

.shoppingCart .list .item .picTxt .pictrue {
  width: 160rpx;
  height: 160rpx;
}

.shoppingCart .list .item .picTxt .pictrue image {
  width: 100%;
  height: 100%;
  border-radius: 6rpx;
}

.shoppingCart .list .item .picTxt .text {
  width: 396rpx;
  font-size: 28rpx;
  color: #282828;
}

.shoppingCart .list .item .picTxt .text .reColor {
  color: #999;
}

.shoppingCart .list .item .picTxt .text .reElection {
  margin-top: 20rpx;
}

.shoppingCart .list .item .picTxt .text .reElection .title {
  font-size: 24rpx;
}

.shoppingCart .list .item .picTxt .text .reElection .reBnt {
  width: 120rpx;
  height: 46rpx;
  border-radius: 23rpx;
  font-size: 26rpx;
}

.shoppingCart .list .item .picTxt .text .infor {
  font-size: 24rpx;
  color: #999999;
  margin-top: 16rpx;
}

.money {
  font-size: 32rpx;
  font-weight: 600;
  @include price_color(theme);

  .price-color {
    @include price_color(theme);
  }
}

.mt-28 {
  margin-top: 28rpx;
}

.bg_color {
  @include main_bg_color(theme);
}

.font_color {
  @include main_color(theme);
}

.shoppingCart .list .item .picTxt .carnum {
  height: 47rpx;
  position: absolute;
  bottom: 7rpx;
  right: 0;
}

.shoppingCart .list .item .picTxt .carnum view {
  border: 1rpx solid #a4a4a4;
  width: 66rpx;
  text-align: center;
  height: 100%;
  line-height: 44rpx;
  font-size: 28rpx;
  color: #a4a4a4;
}

.shoppingCart .list .item .picTxt .carnum .reduce {
  border-right: 0;
  border-radius: 3rpx 0 0 3rpx;
  border-radius: 22rpx 0rpx 0rpx 22rpx;
  font-size: 34rpx;
  line-height: 40rpx;
}

.on {
  border-color: #e3e3e3 !important;
  color: #dedede !important;
}

.shoppingCart .list .item .picTxt .carnum .plus {
  border-left: 0;
  border-radius: 0 3rpx 3rpx 0;
  border-radius: 0rpx 22rpx 22rpx 0rpx;
  font-size: 34rpx;
  line-height: 40rpx;
}

.shoppingCart .list .item .picTxt .carnum .num {
  color: #282828;
}

.shoppingCart .invalidGoods {
  background-color: #fff;
  margin-top: 30rpx;
}

.shoppingCart .invalidGoods .goodsNav {
  width: 100%;
  height: 90rpx;
  padding: 0 24rpx;
  box-sizing: border-box;
  font-size: 28rpx;
  color: #333333;
}

.shoppingCart .invalidGoods .goodsNav .iconfont {
  color: #424242;
  font-size: 28rpx;
  margin-right: 17rpx;
}

.shoppingCart .invalidGoods .goodsNav .del {
  font-size: 26rpx;
  color: #333;
}

.shoppingCart .invalidGoods .goodsNav .del .icon-shanchu1 {
  color: #333;
  font-size: 33rpx;
  vertical-align: -2rpx;
  margin-right: 8rpx;
}

.shoppingCart .invalidGoods .goodsList .item {
  padding: 24rpx;
}

.shoppingCart .invalidGoods .goodsList .picTxt {
  width: 576rpx;
}

.shoppingCart .invalidGoods .goodsList .item .invalid {
  font-size: 22rpx;
  color: #cccccc;
  height: 36rpx;
  border-radius: 3rpx;
  text-align: center;
  line-height: 36rpx;
}

.shoppingCart .invalidGoods .goodsList .item .pictrue {
  width: 160rpx;
  height: 160rpx;
}

.shoppingCart .invalidGoods .goodsList .item .pictrue image {
  width: 100%;
  height: 100%;
  border-radius: 6rpx;
}

.shoppingCart .invalidGoods .goodsList .item .text {
  width: 396rpx;
  font-size: 28rpx;
  color: #999;
  height: 140rpx;
}

.shoppingCart .invalidGoods .goodsList .item .text .name {
  width: 100%;
}

.shoppingCart .invalidGoods .goodsList .item .text .infor {
  font-size: 24rpx;
}

.shoppingCart .invalidGoods .goodsList .item .text .end {
  font-size: 26rpx;
  color: #bbb;
}

.footer {
  z-index: 999;
  width: 100%;
  height: 100rpx;
  background-color: #fff;
  position: fixed;
  padding: 0 24rpx;
  box-sizing: border-box;
  border-top: 1rpx solid #eee;
  bottom: var(--window-bottom);
}

.footer .checkAll {
  font-size: 28rpx;
  color: #282828;
  margin-left: 14rpx;
}

.footer .money {
  font-size: 30rpx;

  .font-color {
    font-weight: 600;
  }
}

.footer .placeOrder {
  color: #fff;
  font-size: 30rpx;
  width: 226rpx;
  height: 70rpx;
  border-radius: 50rpx;
  text-align: center;
  line-height: 70rpx;
  margin-left: 22rpx;
}

.footer .button .bnt {
  font-size: 28rpx;
  color: #999;
  border-radius: 50rpx;
  border: 1px solid #999;
  width: 160rpx;
  height: 60rpx;
  text-align: center;
  line-height: 60rpx;
}

.btn_cart_color {
  font-size: 14px;
  border-radius: 25px;
  width: 80px;
  height: 30px;
  text-align: center;
  line-height: 30px;
  @include coupons_border_color(theme);
  @include main_color(theme);
}

.footer .button form ~ form {
  margin-left: 17rpx;
}

.uni-p-b-96 {
  height: 96rpx;
}

/* #ifdef H5 */
::v-deep .uni-checkbox-input {
  border-radius: 50%;
  width: 38rpx;
  height: 38rpx;
  margin-right: 0 !important;
}

::v-deep .uni-checkbox-input.uni-checkbox-input-checked {
  @include main_bg_color(theme);
  border: none !important;
  color: #fff !important;
}

::v-deep .uni-checkbox-input.uni-checkbox-input-checked::before {
  font-size: 35rpx;
}
/* #endif */

::v-deep checkbox .uni-checkbox-input.uni-checkbox-input-checked {
  @include main_bg_color(theme);
  border: none !important;
  color: #fff !important;
}

::v-deep checkbox .wx-checkbox-input.wx-checkbox-input-checked {
  @include main_bg_color(theme);
  border: none !important;
  color: #fff !important;
  margin-right: 0 !important;
}

.bottom-custom {
  bottom: calc(98rpx + constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
  bottom: calc(98rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
}
.p-30 {
  padding: 0 30rpx;
}
</style>
