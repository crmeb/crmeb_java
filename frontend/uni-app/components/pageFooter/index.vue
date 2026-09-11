<template>
  <!-- 底部导航 -->
  <view v-if="showTabBar">
    <view class="page-footer-fixed" :style="[bgColor]">
      <view class="page-footer-wrapper">
        <view
          class="page-footer"
          :class="{
            'page-footer2': newData.navStyleConfig && newData.navStyleConfig.tabVal == 1,
            'page-footer3': newData.navStyleConfig && newData.navStyleConfig.tabVal == 2,
          }"
          id="target"
          :style="[componentStyle]"
        >
          <view
            class="foot-item"
            v-for="(item, index) in menuList"
            :key="index"
            @click="goRouter(item)"
          >
            <template v-if="isActive(item)">
              <image v-if="navStyle != 1" :src="getMenuImage(item, true)"></image>
              <view v-if="navStyle != 2" class="txt active" :style="[txtActiveColor]">
                {{ item.name }}
              </view>
            </template>
            <template v-else>
              <image v-if="navStyle != 1" :src="getMenuImage(item, false)"></image>
              <view v-if="navStyle != 2" class="txt" :style="[txtColor]">
                {{ item.name }}
              </view>
            </template>
            <view v-if="isCartLink(item) && cartNum > 0" class="cart-badge">
              {{ cartNum > 99 ? "99+" : cartNum }}
            </view>
          </view>
        </view>
      </view>
    </view>
    <view :style="{ height: `${footerHeight}px` }"></view>
    <view class="safe-area-inset-bottom"></view>
  </view>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { getThemeInfo } from "@/api/api.js";
import { getCartCounts } from "@/api/order.js";
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from "pinia";

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

function looksLikeComponentCollection(value) {
  if (!value || typeof value !== "object") return false;
  const list = Array.isArray(value) ? value : Object.keys(value).map((key) => value[key]);
  return list.some((item) => item && typeof item === "object" && (item.name || item.defaultName));
}

const props = defineProps({
  isTabBar: {
    type: Boolean,
    default: true,
  },
  configData: {
    type: Object,
    default: () => null,
  },
});

const emit = defineEmits(['newDataStatus']);

const appStore = useAppStore();
const { isLogin } = storeToRefs(appStore);

const newData = ref({});
const activeRouter = ref("");
const showTabBar = ref(false);
const footerHeight = ref(0);
const cartNum = ref(0);

const menuList = computed(() => Array.isArray(newData.value.menuList) ? newData.value.menuList : []);
const navStyle = computed(() => newData.value.navStyleConfig ? newData.value.navStyleConfig.tabVal : 0);
const txtActiveColor = computed(() => {
  let styleObject = {};
  if (newData.value.toneConfig && newData.value.toneConfig.tabVal) {
    styleObject.color = getColor(newData.value.activeTxtColor);
  }
  return styleObject;
});
const txtColor = computed(() => {
  let styleObject = {};
  if (newData.value.toneConfig && newData.value.toneConfig.tabVal) {
    styleObject.color = getColor(newData.value.txtColor);
  }
  return styleObject;
});
const bgColor = computed(() => {
  let styleObject = {};
  if (!newData.value.name) return styleObject;
  if (!newData.value.navConfig || !newData.value.navConfig.tabVal) {
    styleObject.background = getColor(newData.value.bgColor);
  }
  return styleObject;
});
const componentStyle = computed(() => {
  let styleObject = {};
  if (!newData.value.name) return styleObject;
  if (newData.value.navConfig && newData.value.navConfig.tabVal) {
    styleObject.right = `${getVal(newData.value.prConfig) * 2}rpx`;
    styleObject.bottom = `${getVal(newData.value.mbConfig) * 2}rpx`;
    styleObject.left = `${getVal(newData.value.prConfig) * 2}rpx`;
    styleObject.paddingTop = `${getVal(newData.value.topConfig) * 2}rpx`;
    styleObject.paddingBottom = `${getVal(newData.value.bottomConfig) * 2}rpx`;
    styleObject.borderRadius = getRadius(newData.value.fillet);
    styleObject.background = getColor(newData.value.bgColor2);
  } else {
    styleObject.paddingTop = `${getVal(newData.value.topConfig) * 2}rpx`;
    styleObject.paddingBottom = `${getVal(newData.value.bottomConfig) * 2}rpx`;
    styleObject.background = getColor(newData.value.bgColor);
  }
  return styleObject;
});

watch(() => props.configData, (newVal) => {
  if (newVal && Object.keys(newVal).length) {
    setNavigationInfo(newVal);
  }
}, { deep: true, immediate: true });

// created
let routes = getCurrentPages();
let curRoute = routes[routes.length - 1].route;
activeRouter.value = "/" + curRoute;

// mounted
if (!props.configData || !Object.keys(props.configData).length) {
  getThemeNavigation();
}
if (isLogin.value) {
  getCartNum();
}

function getColor(config, fallback = "") {
  return config && config.color && config.color[0] ? config.color[0].item : fallback;
}
function getVal(config, fallback = 0) {
  return config && config.val !== undefined ? Number(config.val) || 0 : fallback;
}
function getRadius(config) {
  if (!config) return "0";
  if (config.type && Array.isArray(config.valList)) {
    return `${getVal(config.valList[0]) * 2}rpx ${getVal(config.valList[1]) * 2}rpx ${getVal(config.valList[3]) * 2}rpx ${getVal(config.valList[2]) * 2}rpx`;
  }
  return `${getVal(config) * 2}rpx`;
}
function getMenuImage(item, active) {
  if (Array.isArray(item.imgList)) {
    return item.imgList[active ? 0 : 1] || "";
  }
  return active ? item.checked || "" : item.unchecked || "";
}
function getLink(item) {
  return item && item.link ? item.link : "";
}
function isActive(item) {
  const link = getLink(item);
  return link && link.split("?")[0] === activeRouter.value;
}
function isCartLink(item) {
  return getLink(item).split("?")[0] === "/pages/order_addcart/order_addcart";
}
function setNavigationInfo(data) {
  if (!data || !data.name) return;
  newData.value = data;
  showTabBar.value = data.effectConfig ? !!data.effectConfig.tabVal : true;
  let pdHeight = getVal(data.topConfig) + getVal(data.bottomConfig);
  emit(
    "newDataStatus",
    showTabBar.value,
    pdHeight,
    getVal(data.mbConfig),
    data.navConfig ? data.navConfig.tabVal : 0
  );
  appStore.BottomNavigationIsCustom(showTabBar.value);
  if (props.isTabBar) {
    if (showTabBar.value) {
      uni.hideTabBar();
    } else {
      uni.showTabBar();
    }
  }
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
    value = parseJson(value.value, value.value || {});
  }
  return {
    ...normalized,
    value,
  };
}
function findFooterConfig(data) {
  const diyData = normalizeDiyData(data);
  const list = toComponentList(diyData.value);
  return list.find((item) => item && (item.name === "pageFoot" || item.defaultName === "pageFoot"));
}
function getThemeNavigation() {
  let data = {};
  let previewThemeId = uni.getStorageSync("previewThemeId");
  if (previewThemeId) data.theme_id = previewThemeId;
  getThemeInfo("home", data)
    .then((res) => {
      const footerConfig = findFooterConfig(res.data);
      if (footerConfig) {
        setNavigationInfo(footerConfig);
      }
    })
    .catch(() => {});
}
function getCartNum() {
  getCartCounts(true, "total")
    .then((res) => {
      cartNum.value = Number(res.data && res.data.count) || 0;
    })
    .catch(() => {});
}
function goRouter(item) {
  const link = getLink(item);
  if (!link) return;
  var pages = getCurrentPages();
  var page = pages[pages.length - 1].$page.fullPath;
  if (link === page) return;
  uni.switchTab({
    url: link,
    fail() {
      uni.redirectTo({
        url: link,
      });
    },
  });
}
</script>

<style scoped lang="scss">
.safe-area-inset-bottom {
  height: 0;
  height: constant(safe-area-inset-bottom);
  height: env(safe-area-inset-bottom);
}

.page-footer-fixed {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 999;
  width: 100%;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}

.page-footer-wrapper {
  position: relative;
}

.page-footer {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  display: flex;

  .foot-item {
    position: relative;
    display: flex;
    flex: 1;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 96rpx;
  }

  .foot-item image {
    display: block;
    height: 48rpx;
    width: 48rpx;
    margin: 0 auto;
  }

  .foot-item .txt {
    margin-top: 4rpx;
    font-size: 20rpx;
    line-height: 28rpx;
    color: #333333;

    &.active {
      color: var(--view-theme);
    }
  }
}

.page-footer2 .foot-item .txt {
  margin-top: 0;
  font-size: 32rpx;
  line-height: 44rpx;
  color: #333333;

  &.active {
    color: var(--view-theme);
  }
}

.cart-badge {
  position: absolute;
  top: 0;
  left: 50%;
  min-width: 30rpx;
  height: 30rpx;
  padding: 0 8rpx;
  border-radius: 999rpx;
  background: var(--view-theme);
  color: #fff;
  font-size: 20rpx;
  line-height: 30rpx;
  text-align: center;
  transform: translateX(16rpx);
  box-sizing: border-box;
}

.page-footer2.float .foot-item::before,
.page-footer3.float .foot-item::before {
  content: "";
  position: absolute;
  top: 50%;
  left: 0;
  width: 2rpx;
  height: 32rpx;
  background: #cccccc;
  transform: translateY(-50%);
}

.page-footer2.float .foot-item:first-child::before,
.page-footer3.float .foot-item:first-child::before {
  display: none;
}
</style>
