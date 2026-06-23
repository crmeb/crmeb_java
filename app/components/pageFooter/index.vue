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

<script>
import { getThemeInfo } from "@/api/api.js";
import { getCartCounts } from "@/api/order.js";
import { mapGetters } from "vuex";

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

export default {
  name: "pageFooter",
  props: {
    isTabBar: {
      type: Boolean,
      default: true,
    },
    configData: {
      type: Object,
      default: () => null,
    },
  },
  computed: {
    ...mapGetters(["isLogin"]),
    menuList() {
      return Array.isArray(this.newData.menuList) ? this.newData.menuList : [];
    },
    navStyle() {
      return this.newData.navStyleConfig ? this.newData.navStyleConfig.tabVal : 0;
    },
    txtActiveColor() {
      let styleObject = {};
      if (this.newData.toneConfig && this.newData.toneConfig.tabVal) {
        styleObject.color = this.getColor(this.newData.activeTxtColor);
      }
      return styleObject;
    },
    txtColor() {
      let styleObject = {};
      if (this.newData.toneConfig && this.newData.toneConfig.tabVal) {
        styleObject.color = this.getColor(this.newData.txtColor);
      }
      return styleObject;
    },
    bgColor() {
      let styleObject = {};
      if (!this.newData.name) return styleObject;
      if (!this.newData.navConfig || !this.newData.navConfig.tabVal) {
        styleObject.background = this.getColor(this.newData.bgColor);
      }
      return styleObject;
    },
    componentStyle() {
      let styleObject = {};
      if (!this.newData.name) return styleObject;
      if (this.newData.navConfig && this.newData.navConfig.tabVal) {
        styleObject.right = `${this.getVal(this.newData.prConfig) * 2}rpx`;
        styleObject.bottom = `${this.getVal(this.newData.mbConfig) * 2}rpx`;
        styleObject.left = `${this.getVal(this.newData.prConfig) * 2}rpx`;
        styleObject.paddingTop = `${this.getVal(this.newData.topConfig) * 2}rpx`;
        styleObject.paddingBottom = `${this.getVal(this.newData.bottomConfig) * 2}rpx`;
        styleObject.borderRadius = this.getRadius(this.newData.fillet);
        styleObject.background = this.getColor(this.newData.bgColor2);
      } else {
        styleObject.paddingTop = `${this.getVal(this.newData.topConfig) * 2}rpx`;
        styleObject.paddingBottom = `${this.getVal(this.newData.bottomConfig) * 2}rpx`;
        styleObject.background = this.getColor(this.newData.bgColor);
      }
      return styleObject;
    },
  },
  watch: {
    configData: {
      handler(newVal) {
        if (newVal && Object.keys(newVal).length) {
          this.setNavigationInfo(newVal);
        }
      },
      deep: true,
      immediate: true,
    },
  },
  data() {
    return {
      newData: {},
      activeRouter: "",
      showTabBar: false,
      footerHeight: 0,
      cartNum: 0,
    };
  },
  created() {
    let routes = getCurrentPages();
    let curRoute = routes[routes.length - 1].route;
    this.activeRouter = "/" + curRoute;
  },
  mounted() {
    if (!this.configData || !Object.keys(this.configData).length) {
      this.getThemeNavigation();
    }
    if (this.isLogin) {
      this.getCartNum();
    }
  },
  methods: {
    getColor(config, fallback = "") {
      return config && config.color && config.color[0] ? config.color[0].item : fallback;
    },
    getVal(config, fallback = 0) {
      return config && config.val !== undefined ? Number(config.val) || 0 : fallback;
    },
    getRadius(config) {
      if (!config) return "0";
      if (config.type && Array.isArray(config.valList)) {
        return `${this.getVal(config.valList[0]) * 2}rpx ${this.getVal(config.valList[1]) * 2}rpx ${this.getVal(config.valList[3]) * 2}rpx ${this.getVal(config.valList[2]) * 2}rpx`;
      }
      return `${this.getVal(config) * 2}rpx`;
    },
    getMenuImage(item, active) {
      if (Array.isArray(item.imgList)) {
        return item.imgList[active ? 0 : 1] || "";
      }
      return active ? item.checked || "" : item.unchecked || "";
    },
    getLink(item) {
      return item && item.link ? item.link : "";
    },
    isActive(item) {
      const link = this.getLink(item);
      return link && link.split("?")[0] === this.activeRouter;
    },
    isCartLink(item) {
      return this.getLink(item).split("?")[0] === "/pages/order_addcart/order_addcart";
    },
    setNavigationInfo(data) {
      if (!data || !data.name) return;
      this.newData = data;
      this.showTabBar = data.effectConfig ? !!data.effectConfig.tabVal : true;
      let pdHeight = this.getVal(data.topConfig) + this.getVal(data.bottomConfig);
      this.$emit(
        "newDataStatus",
        this.showTabBar,
        pdHeight,
        this.getVal(data.mbConfig),
        data.navConfig ? data.navConfig.tabVal : 0
      );
      this.$store.commit("BottomNavigationIsCustom", this.showTabBar);
      if (this.isTabBar) {
        if (this.showTabBar) {
          uni.hideTabBar();
        } else {
          uni.showTabBar();
        }
      }
    },
    normalizeDiyData(data) {
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
    },
    findFooterConfig(data) {
      const diyData = this.normalizeDiyData(data);
      const list = toComponentList(diyData.value);
      return list.find((item) => item && (item.name === "pageFoot" || item.defaultName === "pageFoot"));
    },
    getThemeNavigation() {
      let data = {};
      let previewThemeId = uni.getStorageSync("previewThemeId");
      if (previewThemeId) data.theme_id = previewThemeId;
      getThemeInfo("home", data)
        .then((res) => {
          const footerConfig = this.findFooterConfig(res.data);
          if (footerConfig) {
            this.setNavigationInfo(footerConfig);
          }
        })
        .catch(() => {});
    },
    getCartNum() {
      getCartCounts(true, "total")
        .then((res) => {
          this.cartNum = Number(res.data && res.data.count) || 0;
        })
        .catch(() => {});
    },
    goRouter(item) {
      const link = this.getLink(item);
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
    },
  },
};
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
