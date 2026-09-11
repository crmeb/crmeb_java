<template>
  <commonWrapper
    :config="wrapperConfig"
    class="footer"
    :class="{ eject: storeInfo.id }"
    :style="bagStyle"
  >
    <view class="footer-inner acea-row row-between-wrapper">
      <div class="acea-row">
        <block v-if="!isCustomEntry">
          <block v-for="(item_id, index) in showIcons" :key="index">
            <navigator :render-link="false"
              v-if="item_id === 3"
              hover-class="none"
              class="item"
              open-type="reLaunch"
              url="/pages/index/index"
            >
              <view class="iconfont icon-shouye6"></view>
              <view class="p_center">{{ '首页' }}</view>
            </navigator>
            <view v-if="item_id === 1" @click="setCollect" class="item">
              <view
                class="iconfont icon-shoucang1"
                v-if="storeInfo.userCollect"
              ></view>
              <view class="iconfont icon-shoucang4" v-else></view>
              <view class="p_center">{{ '收藏' }}</view>
            </view>
            <view
              v-if="item_id === 2"
              class="animated item"
              :class="animated == true ? 'bounceIn' : ''"
              @click="goCart"
            >
              <view class="iconfont icon-gouwuche">
                <text class="num bg-color" v-if="parseFloat(CartCount) > 0">{{
                  CartCount || 0
                }}</text>
              </view>
              <view class="p_center">{{ '购物车' }}</view>
            </view>
            <!-- #ifdef APP-PLUS || H5 -->
            <view
              v-if="item_id === 0"
              class="item"
              @click="goCustomer"
            >
              <view class="iconfont icon-kefu"></view>
              <view class="p_center">{{ '客服' }}</view>
            </view>
            <!-- #endif -->
            <!-- #ifdef MP -->
            <view
              v-if="item_id === 0 && routineContact == 0"
              class="item"
              @click="goCustomer"
            >
              <view class="iconfont icon-kefu"></view>
              <view class="p_center">{{ '客服' }}</view>
            </view>
            <button
              v-else-if="item_id === 0 && routineContact == 1"
              class="item"
              open-type="contact"
              :send-message-title="storeInfo.store_name"
              :send-message-img="storeInfo.image"
              :send-message-path="`/pages/goods/goods_details/index?id=${storeInfo.id}`"
              show-message-card
              hover-class="none"
            >
              <view class="iconfont icon-kefu"></view>
              <view class="p_center">{{ '客服' }}</view>
            </button>
            <!-- #endif -->
            <view v-if="item_id === 4" class="item" @click="goShare">
              <view class="iconfont icon-fenxiang4"></view>
              <view class="p_center">{{ '分享' }}</view>
            </view>
          </block>
        </block>
        <block v-else>
          <view
            class="item"
            v-for="(item, index) in customMenuList"
            :key="index"
            @click="goPage(item.url)"
          >
            <view
              v-if="isCustomIcon"
              class="iconfont"
              :class="item.icon"
              :style="customIconStyle"
            ></view>
            <image
              v-else
              :src="item.img"
              class="menu-img"
              :style="customImageStyle"
              mode="aspectFit"
            ></image>
            <view class="p_center">{{ item.name }}</view>
          </view>
        </block>
      </div>
      <view v-if="noGoods" class="presale">
        <view class="acea-row">
          <form class="bnts bg-color-hui">
            <button class="bnts bg-color-hui" form-type="submit">
              {{ '暂无产品' }}
            </button>
          </form>
        </view>
      </view>
      <view class="btn-box" v-else>
        <view v-if="!storeInfo.presale">
          <view
            class="bnt acea-row"
            :class="!isCartButtonVisible ? 'virbnt' : ''"
            v-if="attr.productSelect.stock <= 0"
          >
            <form
              v-if="isCartButtonVisible"
              @submit="joinCart"
              class="joinCart bnts"
              :class="!isCartButtonVisible ? 'virbnt' : ''"
            >
              <button
                class="joinCart bnts"
                form-type="submit"
                :style="cartBtnStyle"
              >
                {{ '加入购物车' }}
              </button>
            </form>
            <form class="buy bnts bg-color-hui">
              <button
                class="buy bnts bg-color-hui"
                form-type="submit"
                :class="!isCartButtonVisible ? 'virbnt' : ''"
              >
                {{ '已售罄' }}
              </button>
            </form>
          </view>
          <view class="bnt acea-row" v-else>
            <form
              v-if="isCartButtonVisible"
              @submit="joinCart"
              class="joinCart bnts"
            >
              <button
                class="joinCart bnts"
                form-type="submit"
                :style="cartBtnStyle"
              >
                {{ '加入购物车' }}
              </button>
            </form>
            <form
              @submit="goBuy"
              class="buy bnts"
              :class="!isCartButtonVisible ? 'virbnt' : ''"
            >
              <button
                class="buy bnts"
                :class="!isCartButtonVisible ? 'virbnt' : ''"
                form-type="submit"
                :style="buyBtnStyle"
              >
                {{ '立即购买' }}
              </button>
            </form>
          </view>
        </view>
        <view class="presale" v-else>
          <view
            class="acea-row"
            v-if="presale_pay_status === 1 || presale_pay_status === 3"
          >
            <form class="bnts bg-color-hui">
              <button class="bnts bg-color-hui" form-type="submit">
                {{ presale_pay_status === 1 ? '未开始' : '已结束' }}
              </button>
            </form>
          </view>
          <view
            class="acea-row"
            v-else-if="
              attr.productSelect.quota <= 0 ||
              attr.productSelect.quota < attr.productSelect.cart_num
            "
          >
            <form class="bnts bg-color-hui">
              <button class="bnts bg-color-hui" form-type="submit">
                {{ '已售罄' }}
              </button>
            </form>
          </view>
          <view class="bnts acea-row" v-else-if="presale_pay_status === 2">
            <form @submit="goBuy" class="bnts">
              <button class="bnts" form-type="submit" :style="buyBtnStyle">
                {{ '立即购买' }}
              </button>
            </form>
          </view>
        </view>
      </view>
    </view>
  </commonWrapper>
</template>

<script setup>
import { computed } from "vue";
import commonWrapper from "./commonWrapper.vue";
import { getCustomer } from "@/utils/index.js";

const DEFAULT_BOTTOM_CONFIG = {
  name: "bottomMenu",
  entryConfig: {
    tabVal: 0,
  },
  showContent: {
    type: [3, 1, 2, 0, 4],
    list: [
      { id: 3, name: "首页", icon: "icon-shouye6" },
      { id: 1, name: "收藏", icon: "icon-shoucang4" },
      { id: 2, name: "购物车", icon: "icon-gouwuche" },
      { id: 0, name: "客服", icon: "icon-kefu" },
      { id: 4, name: "分享", icon: "icon-fenxiang4" },
    ],
  },
  cartButton: {
    tabVal: 0,
  },
  menuConfig: {
    listStyle: 0,
    list: [],
  },
  toneConfig: {
    tabVal: 0,
  },
  cartColor: {
    color: [{ item: "#FAAD14" }, { item: "#FAAD14" }],
  },
  buyColor: {
    color: [{ item: "#E93323" }, { item: "#E93323" }],
  },
  componentBgConfig: {
    tabVal: 0,
    colorConfig: {
      color: [{ item: "#fff" }, { item: "#fff" }],
    },
    colorDirection: {
      tabVal: 0,
    },
    imageConfig: {
      url: "",
    },
  },
  bottomBgColor: {
    color: [{ item: "#fff" }],
  },
  menuPcFillet: {
    type: 0,
    val: 0,
    valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
  },
  iconColor: {
    color: [{ item: "#333" }],
  },
  iconSize: {
    val: 20,
  },
  iconRotate: {
    val: 0,
  },
  padding: {
    val: 0,
  },
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

function toComponentList(value) {
  const data = parseJson(value, value || {});
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (!isPlainObject(data)) return [];
  return Object.keys(data)
    .sort((a, b) => {
      const numA = Number(a);
      const numB = Number(b);
      if (!Number.isNaN(numA) && !Number.isNaN(numB)) return numA - numB;
      return String(a).localeCompare(String(b));
    })
    .map((key) => parseJson(data[key], data[key] || {}))
    .filter(Boolean);
}

function mergeConfig(defaultConfig, data) {
  if (Array.isArray(defaultConfig)) {
    return Array.isArray(data) ? data : JSON.parse(JSON.stringify(defaultConfig));
  }
  if (!defaultConfig || typeof defaultConfig !== "object") {
    return data === undefined ? defaultConfig : data;
  }
  const result = JSON.parse(JSON.stringify(defaultConfig));
  if (!data || typeof data !== "object") return result;
  Object.keys(data).forEach((key) => {
    if (
      result[key] &&
      typeof result[key] === "object" &&
      !Array.isArray(result[key]) &&
      data[key] &&
      typeof data[key] === "object" &&
      !Array.isArray(data[key])
    ) {
      result[key] = mergeConfig(result[key], data[key]);
    } else {
      result[key] = data[key];
    }
  });
  return result;
}

const props = defineProps({
  diyData: {
    type: Object,
    default: () => ({}),
  },
  storeInfo: {
    type: Object,
    default: () => ({}),
  },
  CartCount: {
    type: [Number, String],
    default: 0,
  },
  noGoods: {
    type: Boolean,
    default: false,
  },
  attr: {
    type: Object,
    default: () => ({
      productSelect: {},
    }),
  },
  presale_pay_status: {
    type: [Number, String],
    default: 1,
  },
  animated: {
    type: Boolean,
    default: false,
  },
  routineContact: {
    type: Number,
    default: 0,
  },
});

const emit = defineEmits([
  "setCollect",
  "goCart",
  "goGift",
  "share",
  "joinCart",
  "goBuy",
]);

const bottomConfig = computed(() => {
  const config = findBottomConfig(props.diyData);
  return config ? mergeConfig(DEFAULT_BOTTOM_CONFIG, config) : null;
});
const wrapperConfig = computed(() => {
  let config = bottomConfig.value;
  if (!config) return {};
  let newConfig = { ...config };
  return newConfig;
});
const toneConfig = computed(() => {
  if (!bottomConfig.value || !bottomConfig.value.toneConfig) return 0;
  return bottomConfig.value.toneConfig.tabVal;
});
const bagStyle = computed(() => {
  if (bottomConfig.value && bottomConfig.value.componentBgConfig) {
    const componentBgConfig = bottomConfig.value.componentBgConfig;
    if (componentBgConfig.tabVal === 1 && componentBgConfig.imageConfig && componentBgConfig.imageConfig.url) {
      return `background-image: url(${componentBgConfig.imageConfig.url});background-repeat: no-repeat;background-size: cover;background-position: center;`;
    }
    const color = componentBgConfig.colorConfig && componentBgConfig.colorConfig.color;
    const c1 = (color && color[0] && color[0].item) || "#fff";
    const c2 = (color && color[1] && color[1].item) || c1;
    return `background: linear-gradient(90deg, ${c1} 0%, ${c2} 100%);`;
  }
  if (bottomConfig.value && bottomConfig.value.bottomBgColor && bottomConfig.value.bottomBgColor.color) {
    return `background: ${bottomConfig.value.bottomBgColor.color[0].item};`;
  }
  return "";
});
const cartBtnStyle = computed(() => {
  if (toneConfig.value && bottomConfig.value.cartColor) {
    const color = bottomConfig.value.cartColor.color;
    const c1 = (color && color[0] && color[0].item) || "#FAAD14";
    const c2 = (color && color[1] && color[1].item) || c1;
    return `background: linear-gradient(90deg, ${c1} 0%, ${c2} 100%);`;
  }
  return ""; // Fallback to CSS default
});
const buyBtnStyle = computed(() => {
  if (toneConfig.value && bottomConfig.value.buyColor) {
    const color = bottomConfig.value.buyColor.color;
    const c1 = (color && color[0] && color[0].item) || "#E93323";
    const c2 = (color && color[1] && color[1].item) || c1;
    return `background: linear-gradient(90deg, ${c1} 0%, ${c2} 100%);`;
  }
  return ""; // Fallback to CSS default
});
const showIcons = computed(() => {
  if (!bottomConfig.value) return [3, 1, 2, 0, 4];
  return Array.isArray(bottomConfig.value.showContent.type) ? bottomConfig.value.showContent.type : [3, 1, 2, 0, 4];
});
const showCartButton = computed(() => {
  if (!bottomConfig.value) return true;
  return bottomConfig.value.cartButton.tabVal === 0;
});
const isCartButtonVisible = computed(() => {
  return props.storeInfo.cart_button !== false && showCartButton.value;
});
const entryConfig = computed(() => {
  return bottomConfig.value && bottomConfig.value.entryConfig;
});
const menuConfig = computed(() => {
  return bottomConfig.value && bottomConfig.value.menuConfig;
});
const isCustomEntry = computed(() => {
  return entryConfig.value && entryConfig.value.tabVal === 1;
});
const isCustomImage = computed(() => {
  return isCustomEntry.value && menuConfig.value && menuConfig.value.listStyle === 0;
});
const isCustomIcon = computed(() => {
  return isCustomEntry.value && menuConfig.value && menuConfig.value.listStyle === 1;
});
const customMenuList = computed(() => {
  if (!isCustomEntry.value || !menuConfig.value) return [];
  return (menuConfig.value.list || [])
    .filter((item) => item.show)
    .map((item) => ({
      name: item.info && item.info[0] ? item.info[0].value : "",
      url: item.url || (item.info && item.info[1] ? item.info[1].value : ""),
      icon: item.icon,
      img: item.img,
    }));
});
const customImageStyle = computed(() => {
  const fillet = bottomConfig.value && bottomConfig.value.menuPcFillet;
  if (!fillet) return { width: "40rpx", height: "40rpx" };
  const valList = fillet.valList || [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }];
  let radius;
  if (fillet.type) {
    radius = `${valList[0].val}px ${valList[1].val}px ${valList[3].val}px ${valList[2].val}px`;
  } else {
    radius = `${fillet.val || 0}px`;
  }
  return {
    borderRadius: radius,
    width: "40rpx",
    height: "40rpx",
    display: "block",
  };
});
const customIconStyle = computed(() => {
  const config = bottomConfig.value;
  if (!config) return {};
  const color = config.iconColor?.color?.[0]?.item || "#333";
  const size = config.iconSize?.val || 20;
  const rotate = config.iconRotate?.val || 0;
  const padding = config.padding?.val || 0;
  const shadow =
    config.shadow?.tabVal === 1 ? "0px 2px 4px rgba(0,0,0,0.2)" : "none";
  return {
    color: color,
    fontSize: `${size}px`,
    transform: `rotate(${rotate}deg)`,
    padding: `${padding}px`,
    textShadow: shadow,
    display: "inline-block",
  };
});

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
    value = parseJson(value.value, value.value || {});
  }
  return {
    ...normalized,
    value,
  };
}
function findBottomConfig(data) {
  const diyData = normalizeDiyData(data);
  const list = toComponentList(diyData.value);
  return list.find((item) => item && (item.name === "bottomMenu" || item.defaultName === "bottomMenu"));
}
function goCustomer() {
  getCustomer(`/pages/extension/customer_list/chat?productId=${props.storeInfo.id}`);
}
function goPage(url) {
  if (!url) return;
  uni.navigateTo({
    url: url,
    fail: () => {
      uni.switchTab({
        url: url,
      });
    },
  });
}
function setCollect() {
  emit("setCollect");
}
function goCart() {
  emit("goCart");
}
function goGift() {
  emit("goGift");
}
function goShare() {
  emit("share");
}
function joinCart() {
  emit("joinCart");
}
function goBuy() {
  emit("goBuy");
}
</script>

<style scoped lang="scss">
.footer {
  position: fixed;
  bottom: 0;
  width: 100%;
  box-sizing: border-box;
  z-index: 277;
  border-top: 1rpx solid #f0f0f0; // 保留默认边框作为兜底
  height: 100rpx;
  height: calc(100rpx + constant(safe-area-inset-bottom)); ///兼容 IOS<11.2/
  height: calc(100rpx + env(safe-area-inset-bottom)); ///兼容 IOS>11.2/
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  transform: translate3d(0, 100%, 0);
  transition: all 0.3s cubic-bezier(0.25, 0.5, 0.5, 0.9);

  &.eject {
    transform: translate3d(0, 0, 0);
  }

  .footer-inner {
    width: 100%;
    height: 100rpx;
    padding: 14rpx 20rpx;
    box-sizing: border-box;
    flex-wrap: nowrap;
  }

  .gift-icon {
    width: 40rpx;
    height: 40rpx;
    margin: 5rpx 0 0rpx;
  }

  .item {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    flex-shrink: 0;
    min-width: 40rpx;
    font-size: 20rpx;
    margin-right: 30rpx;
    color: #666;
    line-height: 1.2;

    .iconfont {
      text-align: center;
      font-size: 40rpx;
      line-height: 40rpx;
      margin-bottom: 4rpx;

      &.icon-shoucang1 {
        color: var(--view-theme);
      }

      &.icon-gouwuche {
        position: relative;

        .num {
          color: #fff;
          position: absolute;
          font-size: 18rpx;
          min-width: 28rpx;
          height: 28rpx;
          padding: 0 8rpx;
          box-sizing: border-box;
          border-radius: 28rpx;
          line-height: 28rpx;
          text-align: center;
          top: -10rpx;
          right: -14rpx;
        }
      }
    }

    .p_center {
      white-space: nowrap;
      font-size: 20rpx;
      line-height: 24rpx;
    }
  }

  uni-button {
    background-color: transparent;
  }
  button::after {
    border: 0;
  }
  .bnt {
    flex: 1;
    height: 72rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: nowrap;
    .bnts {
      width: 100%;
      text-align: center;
      line-height: 72rpx;
      height: 72rpx;
      color: #fff;
      font-size: 28rpx;
      border-radius: 36rpx;
      padding: 0;
      margin: 0;
      border: 0;
    }

    .joinCart {
      background-color: var(--view-bntColor);
      margin-right: 20rpx;
    }

    .buy {
      background-color: var(--view-theme);
    }
  }
}

.virbnt {
  // width: 444rpx !important;
  height: 72rpx !important;
  border-radius: 36rpx !important;
  overflow: hidden;
}

.virbnts {
  width: 444rpx !important;
  text-align: center;
  line-height: 72rpx;
  height: 72rpx;
  color: #fff;
  font-size: 28rpx;
  background-color: var(--view-bntColor);
  border-radius: 36rpx !important;
}
.btn-box {
  flex: 1;
  min-width: 0;
}
.presale .bnts {
  width: 100%;
  height: 72rpx;
  border-radius: 36rpx;
  background-color: var(--view-theme);
  text-align: center;
  line-height: 72rpx;
  color: #fff;
  font-size: 28rpx;
  padding: 0;
  margin: 0;
  border: 0;
}
</style>
