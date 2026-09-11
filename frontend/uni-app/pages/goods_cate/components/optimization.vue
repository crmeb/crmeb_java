<template>
  <view class="goodCate">
    <view
      class="header acea-row row-center-wrapper"
      :style="{ top: iStatusBarHeight + 'px' }"
    >
      <navigator :render-link="false"
        url="/pages/index/index"
        class="pageIndex"
        hover-class="none"
        open-type="switchTab"
      >
        <text class="iconfont icon-shouye3"></text>
      </navigator>
      <navigator :render-link="false"
        url="/pages/goods/goods_search/index"
        class="search acea-row row-center-wrapper"
        hover-class="none"
      >
        <text class="iconfont icon-xiazai5"></text>
        搜索商品</navigator
      >
    </view>
    <view class="conter">
      <view class="aside" :style="{ top: iStatusBarHeight + 'px' }">
        <view
          class="item acea-row row-center-wrapper"
          :class="index == navActive ? 'on' : ''"
          v-for="(item, index) in productList"
          :key="index"
          @click="tapNav(index, item)"
        >
          <text>{{ item.name }}</text>
        </view>
      </view>
      <view class="wrapper" :style="{ top: iStatusBarHeight + 'px' }">
        <view class="bgcolor" v-if="iSlong">
          <view
            class="longTab acea-row row-middle"
            :style="{ top: iStatusBarHeight + 'px' }"
          >
            <scroll-view
              scroll-x="true"
              style="white-space: nowrap; display: flex; height: 44rpx"
              scroll-with-animation
              :scroll-left="tabLeft"
              show-scrollbar="true"
            >
              <!-- <view class="longItem" :style='"width:"+isWidth+"px"'>全部</view> -->
              <view
                class="longItem"
                :style="'width:' + isWidth + 'px'"
                :class="index === tabClick ? 'click' : ''"
                v-for="(item, index) in categoryErList"
                :key="index"
                @click="longClick(index, item)"
                >{{ item.name }}</view
              >
            </scroll-view>
          </view>
          <view
            class="openList"
            @click="openTap"
            :style="{ top: iStatusBarHeight + 'px' }"
            ><text class="iconfont icon-xiala"></text
          ></view>
        </view>
        <view v-else>
          <view class="downTab" :style="{ top: iStatusBarHeight + 'px' }">
            <view class="title acea-row row-between-wrapper">
              <view>{{ categoryTitle }}</view>
              <view class="closeList" @click="closeTap"
                ><text class="iconfont icon-xiala"></text
              ></view>
            </view>
            <view class="children">
              <view class="acea-row row-middle">
                <view
                  class="item line1"
                  :class="index === tabClick ? 'click' : ''"
                  v-for="(item, index) in categoryErList"
                  :key="index"
                  @click="longClick(index, item)"
                  >{{ item.name }}</view
                >
              </view>
            </view>
          </view>
          <view class="mask" @click="closeTap"></view>
        </view>
        <goodList
          :tempArr="tempArr"
          :isLogin="isLogin"
          @gocartduo="goCartDuo"
          @detail="goDetail"
        ></goodList>
        <view class="loadingicon acea-row row-center-wrapper mb-2">
          <text
            class="loading iconfont icon-jiazai"
            :hidden="loading == false"
          ></text
          >{{ loadTitle }}
        </view>
      </view>
    </view>

    <view class="footer acea-row row-between-wrapper">
      <view
        class="cartIcon acea-row row-center-wrapper"
        @click="getCartLists(0)"
        v-if="cartData.cartList.length"
      >
        <image
          :src="urlDomain + 'crmebimage/perset/staticImg/cart.png'"
        ></image>
        <view class="num">{{ cartCount }}</view>
      </view>
      <view class="cartIcon acea-row row-center-wrapper noCart" v-else>
        <image
          :src="urlDomain + 'crmebimage/perset/staticImg/no_cart.png'"
        ></image>
      </view>
      <view class="money acea-row row-middle">
        <view
          >￥<text class="num">{{ totalPrice }}</text></view
        >
        <view
          class="bnt gray_bg"
          :class="{ main_bg: cartCount > 0 }"
          @click="subOrder"
          >去结算</view
        >
      </view>
    </view>
    <cartList
      :cartData="cartData"
      @closeList="closeList"
      @ChangeCartNumDan="ChangeCartList"
      @ChangeSubDel="ChangeSubDel"
      @ChangeOneDel="ChangeOneDel"
    ></cartList>
    <productWindow
      :attr="attr"
      :isShow="1"
      :iSplus="1"
      :iScart="1"
      @myevent="onMyEvent"
      @ChangeAttr="ChangeAttr"
      @ChangeCartNum="ChangeCartNumDuo"
      @attrVal="attrVal"
      @iptCartNum="iptCartNum"
      @goCat="goCatNum"
      id="product-window"
    ></productWindow>
  </view>
</template>

<script setup>
import { ref, reactive } from "vue";
import {
  getCategoryList,
  getProductslist,
  getAttr,
  postCartAdd,
} from "@/api/store.js";
import {
  getCartList,
  getCartCounts,
  cartDel,
  changeCartNum,
} from "@/api/order.js";
import productWindow from "@/components/productWindow/index.vue";
import goodList from "@/components/d_goodList/index.vue";
import cartList from "@/components/cartList/index.vue";
import util from "@/utils/util.js";
import * as Order from "@/libs/order.js";
import { goShopDetail } from "@/libs/order.js";
import { toLogin } from "@/libs/login.js";
import animationType from "@/utils/animationType.js";
import Cache from "@/utils/cache.js";
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from "pinia";

const appStore = useAppStore();
const { isLogin, uid } = storeToRefs(appStore);

const props = defineProps({
  showSlide: {
    type: Boolean,
    default: true,
  },
});

const urlDomain = ref(Cache.get("imgHost"));
const productList = ref([]);
const navActive = ref(0);
const categoryTitle = ref("");
const categoryErList = ref([]);
const tabLeft = ref(0);
const isWidth = ref(0); //每个导航栏占位
const tabClick = ref(0); //导航栏被点击
const iSlong = ref(true);
const tempArr = ref([]);
const loading = ref(false);
const loadend = ref(false);
const loadTitle = ref("加载更多");
const page = ref(1);
const limit = ref(999);
const cid = ref(0); //一级分类
const sid = ref(0); //二级分类
const isAuto = ref(false); //没有授权的不会自动授权
const isShowAuth = ref(false); //是否隐藏授权
const attr = reactive({
  cartAttr: false,
  productAttr: [],
  productSelect: {},
});
const productValue = ref([]);
const attrValue = ref(""); //已选属性
const storeName = ref(""); //多属性产品名称
const id = ref(0);
const cartData = reactive({
  cartList: [],
  iScart: false,
});
const cartCount = ref(0);
const totalPrice = ref(0.0);
const lengthCart = ref(0);
const iStatusBarHeight = ref(0); // 状态栏高度
// 原 Options API 中未在 data 声明但被引用的隐式属性，补充声明以兼容
const storeInfo = ref({});
const productInfo = ref({});
const isOpen = ref(false);
const cart_num = ref(1);

// created
// #ifdef APP-PLUS
iStatusBarHeight.value = uni.getSystemInfoSync().statusBarHeight;
// #endif
if (isLogin.value) {
  getCartNum();
  getCartLists(1);
}
getAllCategory();
lengthCart.value = cartData.cartList;
// 获取设备宽度
uni.getSystemInfo({
  success(e) {
    isWidth.value = e.windowWidth / 5;
  },
});

// 生成订单；
function subOrder() {
  let list = cartData.cartList;
  if (list.length) {
    let shoppingCartId = list.map((item) => {
      return {
        shoppingCartId: Number(item.id),
      };
    });
    Order.getPreOrder("shoppingCart", shoppingCartId);
    cartData.iScart = false;
  } else {
    return util.Tips({
      title: "请选择产品",
    });
  }
}
// 计算总价；
function getTotalPrice() {
  let list = cartData.cartList,
    total = 0.0;
  list.forEach((item) => {
    if (item.attrStatus) {
      total = util.$h.Add(
        total,
        util.$h.Mul(item.cartNum, item.price),
      );
    }
  });
  totalPrice.value = total;
}
function ChangeSubDel(event) {
  let list = cartData.cartList,
    ids = [];
  list.forEach((item) => {
    ids.push(item.id);
  });
  cartDel(ids.join(",")).then((res) => {
    cartData.cartList = [];
    cartData.iScart = false;
    totalPrice.value = 0.0;
    page.value = 1;
    loadend.value = false;
    tempArr.value = [];
    productslist();
    getCartNum();
  });
}
function ChangeOneDel(id2, index) {
  let list = cartData.cartList;
  cartDel(id2.toString()).then((res) => {
    list.splice(index, 1);
    if (!list.length) {
      cartData.iScart = false;
      page.value = 1;
      loadend.value = false;
      tempArr.value = [];
      productslist();
    }
    getCartNum();
  });
}
function getCartLists(iSshow) {
  let data = {
    page: 1,
    limit: limit.value,
    isValid: true,
  };
  getCartList(data).then((res) => {
    cartData.cartList = res.data.list;
    if (res.data.list.length) {
      cartData.iScart = iSshow ? false : !cartData.iScart;
    } else {
      cartData.iScart = false;
    }
    getTotalPrice();
  });
}
function closeList(e) {
  cartData.iScart = e;
  page.value = 1;
  loadend.value = false;
  tempArr.value = [];
  productslist();
}
function getCartNum() {
  getCartCounts(true, "sum").then((res) => {
    cartCount.value = res.data.count;
  });
}

function onMyEvent() {
  attr.cartAttr = false;
}
/**
 * 默认选中属性
 */
function DefaultSelect() {
  let productAttr = attr.productAttr;
  let value = [];
  // 按 id 升序排序
  const sortedArray = Object.entries(productValue.value)
    .sort(([, a], [, b]) => a.id - b.id)
    .map(([key, val]) => ({
      key,
      ...val,
    }));
  // 默认规格设置
  for (let i = 0; i < sortedArray.length; i++) {
    const attrItem = sortedArray[i];
    if (attrItem.stock > 0 && attrItem.isShow) {
      if (value.length == 0) {
        value = attr.productAttr.length ? attrItem.key.split(",") : [];
      }
      if (attrItem.isDefault) {
        value = attr.productAttr.length ? attrItem.key.split(",") : [];
        break;
      }
    }
  }
  for (let i = 0; i < productAttr.length; i++) {
    productAttr[i].index = value[i];
  }
  //sort();排序函数:数字-英文-汉字；
  let productSelect = productValue.value[value.join(",")];
  if (productSelect && productAttr.length) {
    attr.productSelect.storeName = storeName.value;
    attr.productSelect.image = productSelect.image;
    attr.productSelect.price = productSelect.price;
    attr.productSelect.stock = productSelect.stock;
    attr.productSelect.unique = productSelect.id;
    attr.productSelect.cart_num = 1;
    attrValue.value = value.join(",");
  } else if (!productSelect && productAttr.length) {
    attr.productSelect.storeName = storeName.value;
    attr.productSelect.image = storeInfo.value.image;
    attr.productSelect.price = storeInfo.value.price;
    attr.productSelect.stock = 0;
    attr.productSelect.unique = "";
    attr.productSelect.cart_num = 0;
    attrValue.value = "";
  } else if (!productSelect && !productAttr.length) {
    attr.productSelect.storeName = storeName.value;
    attr.productSelect.image = storeInfo.value.image;
    attr.productSelect.price = storeInfo.value.price;
    attr.productSelect.stock = storeInfo.value.stock;
    attr.productSelect.unique = storeInfo.value.unique || "";
    attr.productSelect.cart_num = 1;
    attrValue.value = "";
  }
}
/**
 * 属性变动赋值
 */
function ChangeAttr(res) {
  let productSelect = productValue.value[res];
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
        title: "请重新选择其它规格",
      });
    }
    attrValue.value = res;
  } else {
    attr.productSelect.price = "暂无报价";
    attr.productSelect.stock = 0;
    attr.productSelect.unique = 0;
    attr.productSelect.cart_num = 0;
    attrValue.value = "";
  }
}
function attrVal(val) {
  attr.productAttr[val.indexw].index =
    attr.productAttr[val.indexw].attrValues[val.indexn];
}
/**
 * 购物车手动填写
 */
function iptCartNum(e) {
  attr.productSelect.cart_num = e;
}
function onLoadFun() {}
// 产品列表
function productslist() {
  if (loadend.value) return; //如果返回列表长度小于请求分页长度，就让他为true,就不继续请求了
  if (loading.value) return;
  loading.value = true;
  loadTitle.value = "";
  getProductslist({
    page: page.value,
    limit: limit.value,
    type: 1,
    cid: sid.value,
  })
    .then((res) => {
      let list = res.data.list,
        isEnd = list.length < limit.value; //返回列表长度小于请求分页长度为true,反之为false
      tempArr.value = util.SplitArray(list, tempArr.value);
      loading.value = false;
      loadend.value = isEnd;
      loadTitle.value = isEnd ? "我也是有底线的~" : "加载更多";
      page.value = page.value + 1;
    })
    .catch((err) => {
      ((loading.value = false), (loadTitle.value = "加载更多"));
    });
}
// 改变多属性购物车
function ChangeCartNumDuo(changeValue) {
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
}
// 已经加入购物车时的购物加减；
function ChangeCartList(changeValue, index) {
  let list = cartData.cartList;
  let num = list[index];
  let stock = list[index].stock;
  ChangeCartNum(changeValue, num, stock, 0, num.productId, index, 1);
  if (!list.length) {
    cartData.iScart = false;
    page.value = 1;
    loadend.value = false;
    tempArr.value = [];
    productslist();
  }
}
// 购物车加减计算函数
function ChangeCartNum(changeValue, index) {
  if (changeValue) {
    if (index.cartNum >= index.stock) {
      index.cartNum = index.stock;
    } else {
      index.cartNum++;
      changeCartNum(index.id, index.cartNum).then((res) => {
        getCartNum(true);
        getTotalPrice();
      });
    }
  } else {
    index.cartNum--;
    changeCartNum(index.id, index.cartNum).then((res) => {
      getCartNum(true);
      getTotalPrice();
    });
    if (index.cartNum == 0) {
      cartDel(index.id).then((res) => {
        getCartLists(1);
        getTotalPrice();
        productslist();
        getCartNum();
      });
    }
  }
}
// 多规格加入购物车；
function goCatNum() {
  goCat(1);
}
/*
 * 加入购物车
 */
function goCat(num) {
  let productSelect = productValue.value[attrValue.value];
  //打开属性
  if (attrValue.value) {
    //默认选中了属性，但是没有打开过属性弹窗还是自动打开让用户查看默认选中的属性
    attr.cartAttr = !isOpen.value ? true : false;
  } else {
    if (isOpen.value) attr.cartAttr = true;
    else attr.cartAttr = !attr.cartAttr;
  }
  //只有关闭属性弹窗时进行加入购物车
  //如果有属性,没有选择,提示用户选择
  if (
    attr.productAttr.length &&
    productSelect.stock === 0 &&
    isOpen.value === true
  )
    return util.Tips({
      title: "产品库存不足，请选择其它",
    });
  if (num === 1) {
    let q = {
      productId: parseFloat(id.value),
      cartNum: parseFloat(attr.productSelect.cart_num),
      isNew: false,
      productAttrUnique:
        attr.productSelect !== undefined
          ? attr.productSelect.unique
          : productInfo.value.id,
    };
    postCartAdd(q)
      .then(function (res) {
        isOpen.value = false;
        attr.cartAttr = false;
        util.Tips({
          title: "添加购物车成功",
          success: () => {
            getCartNum(true);
            setTimeout(() => {
              getCartLists(1);
            }, 200);
          },
        });
      })
      .catch((res) => {
        isOpen.value = false;
        return util.Tips({
          title: res,
        });
      });
  } else {
    getPreOrder();
  }
}
function goCartDuo(item) {
  if (!isLogin.value) {
    getIsLogin();
  } else {
    uni.showLoading({
      title: "加载中",
    });
    storeName.value = item.storeName;
    getAttrs(item.id, item.storeName);
    id.value = item.id;
  }
}
function getIsLogin() {
  toLogin();
}
// 商品详情接口；
function getAttrs(idParam) {
  getAttr(idParam).then((res) => {
    uni.hideLoading();
    attr.productAttr = res.data.productAttr;
    productValue.value = res.data.productValue;
    let productAttr = attr.productAttr.map((item) => {
      return {
        attrName: item.attrName,
        attrValues: item.attrValues.split(","),
        id: item.id,
        isDel: item.isDel,
        productId: item.productId,
        type: item.type,
        optionList: item.optionList || [],
        isShowImage: item.isShowImage,
      };
    });
    attr.productAttr = productAttr;
    attr.cartAttr = true;
    DefaultSelect();
  });
}
// 去详情页
function goDetail(item) {
  if (!isLogin.value) {
    toLogin();
  } else {
    goShopDetail(item, uid.value).then((res) => {
      uni.navigateTo({
        animationType: animationType.type,
        animationDuration: animationType.duration,
        url: `/pages/goods/goods_details/index?id=${item.id}`,
      });
    });
  }
}

function openTap() {
  iSlong.value = false;
}
function closeTap() {
  iSlong.value = true;
}
function getAllCategory() {
  getCategoryList().then((res) => {
    res.data.forEach((item) => {
      if (item.child) {
        item.child.unshift({
          id: item.id,
          name: "全部",
        });
      } else {
        item.child = [
          {
            id: item.id,
            name: "全部",
          },
        ];
      }
    });
    let data = res.data;
    categoryTitle.value = data[0].name;
    sid.value = data[0].id;
    productList.value = data;
    let pid = uni.getStorageSync("categoryId");
    if (pid) {
      let indexNow = productList.value.findIndex((item) => item.id == pid);
      let item = productList.value.find((item) => item.id == pid);
      tapNav(indexNow, item);
      uni.removeStorageSync("categoryId");
    }
    categoryErList.value = res.data[0].child ? res.data[0].child : [];
    page.value = 1;
    loadend.value = false;
    tempArr.value = [];
    productslist();
  });
}
function tapNav(index, item) {
  let list = productList.value[index];
  navActive.value = index;
  categoryTitle.value = list.name;
  categoryErList.value = item.child ? item.child : [];
  tabClick.value = 0;
  tabLeft.value = 0;
  sid.value = item.id;
  page.value = 1;
  loadend.value = false;
  tempArr.value = [];
  productslist();
}
function navSwitch(index, item) {
  if (productList.value.length > 3) {
    tabLeft.value = (index - 1) * (isWidth.value + 6); //设置下划线位置
  }
  tabClick.value = index; //设置导航点击了哪一个
  iSlong.value = true;
  sid.value = item.id;
  page.value = 1;
  loadend.value = false;
  tempArr.value = [];
  productslist();
}
// 导航栏点击
function longClick(index, item) {
  if (productList.value.length > 3) {
    tabLeft.value = (index - 1) * (isWidth.value + 6); //设置下划线位置
  }
  tabClick.value = index; //设置导航点击了哪一个
  iSlong.value = true;
  sid.value = item.id;
  page.value = 1;
  loadend.value = false;
  tempArr.value = [];
  productslist();
}

defineExpose({ getCartNum, getCartLists, productslist });
</script>

<style lang="scss">
page {
  background-color: #fff;
}

::-webkit-scrollbar {
  width: 0;
  height: 0;
  color: transparent;
  display: none;
}

.goodCate {
  background-color: $crmeb-bg-color;

  .mask {
    // z-index: 99;
  }

  ::v-deep .attrProduct {
    .mask {
      z-index: 100;
    }
  }

  .header {
    position: fixed;
    height: 128rpx;
    background-color: #fff;
    top: 0;
    left: 0;
    width: 100%;
    z-index: 99;
    border-bottom: 1px solid #f0f0f0;

    .pageIndex {
      width: 68rpx;
      height: 68rpx;
      border-radius: 50%;
      @include main_bg_color(theme);
      text-align: center;
      line-height: 68rpx;

      .iconfont {
        color: #fff;
        font-size: 30rpx;
      }
    }

    .search {
      width: 600rpx;
      /* #ifdef MP || APP-PLUS */
      width: 550rpx;
      /* #endif */
      height: 68rpx;
      border-radius: 36rpx;
      background-color: #f8f8f8;
      font-size: 26rpx;
      color: #adadad;
      margin-left: 22rpx;

      .iconfont {
        font-size: 30rpx;
        margin: 4rpx 16rpx 0 0;
      }
    }
  }

  .conter {
    padding-top: 64px;
    box-sizing: border-box;

    .aside {
      position: fixed;
      width: 23%;
      left: 0;
      bottom: 0;
      top: 0;
      background-color: $crmeb-bg-color-grey;
      overflow-y: auto;
      overflow-x: hidden;
      margin-top: 128rpx;
      z-index: 97;
      padding-bottom: 140rpx;

      .item {
        height: 100rpx;
        width: 100%;
        font-size: 26rpx;
        color: $crmeb-font-color;

        &.on {
          background-color: $crmeb-bg-color;
          width: 100%;
          text-align: center;
          @include main_color(theme);
          font-weight: 500;
          position: relative;

          &::after {
            content: "";
            position: absolute;
            width: 6rpx;
            height: 46rpx;
            @include main_bg_color(theme);
            border-radius: 0 4rpx 4rpx 0;
            left: 0;
          }
        }
      }
    }
  }

  .wrapper {
    position: relative;
    margin-top: 100rpx;
    padding-top: 10rpx;
    width: 77%;
    float: right;
    background-color: $crmeb-bg-color;
    padding-bottom: 130rpx;
  }

  .hide_slide {
    margin-top: 104rpx;
    width: 100%;
    float: right;
    background-color: $crmeb-bg-color;
    padding-bottom: 130rpx;
  }

  .bgcolor {
    width: 100%;
    background-color: $crmeb-bg-color;
  }

  .goodsList {
    margin-top: 0 !important;
  }

  .longTab {
    width: 65%;
    position: fixed;
    top: 0;
    margin-top: 128rpx;
    height: 100rpx;
    z-index: 99;
    background-color: $crmeb-bg-color;
  }

  .hongTab {
    width: 100%;
    position: fixed;
    top: 0;
    margin-top: 128rpx;
    height: 100rpx;
    z-index: 99;
    background-color: $crmeb-bg-color;
  }

  .longItem {
    height: 44rpx;
    display: inline-block;
    line-height: 44rpx;
    text-align: center;
    font-size: 26rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    color: $crmeb-font-color;
    background-color: $crmeb-bg-color-grey;
    border-radius: 22rpx;
    margin-left: 12rpx;

    &.click {
      font-weight: bold;
      @include cate-two-btn(theme);
      @include main_color(theme);
    }
  }

  .underlineBox {
    height: 3px;
    width: 20%;
    display: flex;
    align-content: center;
    justify-content: center;
    transition: 0.5s;

    .underline {
      width: 33rpx;
      height: 4rpx;
      background-color: $crmeb-bg-color;
    }
  }

  .openList {
    width: 12%;
    height: 100rpx;
    background-color: $crmeb-bg-color;
    line-height: 100rpx;
    padding-left: 30rpx;
    margin-top: 128rpx;
    position: fixed;
    right: 0;
    // top: 128rpx;
    z-index: 99;

    .iconfont {
      font-size: 22rpx;
      color: $crmeb-font-color-subtitle;
    }
  }

  .downTab {
    width: 100%;
    position: fixed;
    top: 0;
    margin-top: 128rpx;
    z-index: 99;
    background-color: #fff;
    right: 0;
    box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.06);
    border-radius: 0 0 24rpx 24rpx;
    overflow: hidden;
    box-sizing: border-box;
  }
  ::v-deep .mask {
    z-index: 98;
  }

  .hownTab {
    width: 100%;
    position: fixed;
    top: 0;
    margin-top: 128rpx;
    z-index: 99;
    background-color: $crmeb-bg-color;
    right: 0;
  }

  .title {
    font-size: 26rpx;
    color: $crmeb-font-color-assist;
    padding: 28rpx 28rpx 20rpx;
    box-sizing: border-box;

    .closeList {
      width: 64rpx;
      height: 64rpx;
      line-height: 64rpx;
      padding-left: 0;
      transform: rotate(180deg);
      text-align: center;
      border-radius: 50%;
      background-color: $crmeb-bg-color-grey;

      .iconfont {
        font-size: 22rpx;
        color: $crmeb-font-color-subtitle;
      }
    }
  }

  .children {
    max-height: 520rpx;
    overflow-x: hidden;
    overflow-y: auto;
    padding: 0 24rpx 24rpx;
    box-sizing: border-box;

    .acea-row {
      flex-wrap: wrap;
    }

    .item {
      height: 64rpx;
      background-color: $crmeb-bg-color-grey;
      border-radius: 32rpx;
      line-height: 64rpx;
      padding: 0 28rpx;
      margin: 0 18rpx 18rpx 0;
      min-width: 150rpx;
      text-align: center;
      box-sizing: border-box;
      flex: 0 0 auto;

      &.click {
        font-weight: bold;
        @include cate-two-btn(theme);
        @include main_color(theme);
      }
    }
  }

  .list_prod {
    padding: 0 30rpx;

    .item {
      width: 100%;
      box-sizing: border-box;
      margin-bottom: 20rpx;

      .pic {
        width: 690rpx;
        height: 284rpx;
        margin: auto;
        border-radius: 16rpx;
        position: relative;

        image {
          width: 100%;
          height: 100%;
          border-radius: 16rpx;
        }
      }

      .pictxt {
        width: 100%;

        .text {
          font-size: 30rpx;
          font-family: PingFang SC;
          font-weight: bold;
          color: #282828;
          margin: 20rpx 0;
        }

        .bottom {
          .money {
            font-size: 42rpx;
            font-weight: bold;
            margin-right: 18rpx;
            @include price_color(theme);

            .sign {
              font-size: 26rpx;
            }

            .item_sales {
              font-size: 24rpx;
              font-family: PingFang SC;
              font-weight: 400;
              padding-left: 17rpx;
              color: #8e8e8e;
            }
          }

          .cart {
            height: 56rpx;

            .pictrue {
              color: #e93323;
              font-size: 46rpx;
              width: 50rpx;
              height: 50rpx;
              text-align: center;
              line-height: 50rpx;

              &.icon-jiahao {
                background: linear-gradient(140deg, #fa6514 0%, #e93323 100%);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
              }
            }

            .num {
              font-size: 30rpx;
              color: #282828;
              font-weight: bold;
              width: 80rpx;
              text-align: center;
            }
          }

          .bnt {
            padding: 0 30rpx;
            height: 56rpx;
            line-height: 56rpx;
            @include main_bg_color(theme);
            border-radius: 42rpx;
            font-size: 26rpx;
            color: #fff;
            position: relative;

            .num {
              @include main_color(theme);
              @include coupons_border_color(theme);
              background: #fff;
              min-width: 12rpx;
              border-radius: 15px;
              position: absolute;
              right: -14rpx;
              top: -15rpx;
              font-size: 22rpx;
              padding: 0 10rpx;
              height: 34rpx;
              line-height: 34rpx;
            }
          }

          .end {
            padding: 0 30rpx;
            height: 56rpx;
            line-height: 56rpx;
            border-radius: 42rpx;
            font-size: 26rpx;
            color: #fff;
            position: relative;
            background: rgba(203, 203, 203, 1);
          }
        }
      }
    }
  }

  .footer {
    position: fixed;
    left: 0;
    bottom: env(safe-area-inset-bottom);
    width: 100%;
    background-color: #fff;
    box-shadow: 0px -3rpx 16rpx rgba(36, 12, 12, 0.05);
    z-index: 101;
    padding: 0 30rpx;
    box-sizing: border-box;
    height: 100rpx;

    &:after {
      content: "";
      height: env(safe-area-inset-bottom); // 这里是重点
      position: absolute;
      top: 100%;
      left: 0;
      right: 0;
      background-color: #fff;
    }

    .cartIcon {
      width: 96rpx;
      height: 96rpx;
      @include main_bg_color(theme);
      border-radius: 50%;
      position: relative;
      margin-top: -36rpx;

      &.noCart {
        background: #cbcbcb !important;
      }

      image {
        width: 49rpx;
        height: 46rpx;
        display: block;
      }

      .num {
        min-width: 12rpx;
        color: #fff;
        border-radius: 15px;
        position: absolute;
        right: -6rpx;
        top: -10rpx;
        font-size: 22rpx;
        padding: 0 10rpx;
        height: 34rpx;
        line-height: 34rpx;
        @include main_color(theme);
        @include coupons_border_color(theme);
        background-color: #fff;
      }
    }

    .money {
      @include price_color(theme);
      font-size: 28rpx;
      font-weight: bold;

      .num {
        font-size: 42rpx;
      }

      .bnt {
        width: 222rpx;
        height: 76rpx;
        border-radius: 46rpx;
        line-height: 76rpx;
        text-align: center;
        color: #fff;
        margin-left: 24rpx;
      }

      .main_bg {
        @include main_bg_color(theme);
      }

      .gray_bg {
        background-color: #b3b3b4;
      }
    }
  }
}
</style>
