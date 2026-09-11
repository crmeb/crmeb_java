<template>
  <view v-show="!isSortType && menus.length" :data-theme="theme" :style="colorStyle">
    <common-wrapper :config="configData" :style="[boxContentStyle]">
      <view>
        <view
          class="menu-header"
          v-if="
            headerConfig.enable && (leftTopText.enable || rightTopText.enable)
          "
          :style="[headerBoxStyle]"
        >
          <text
            class="left-text"
            v-if="leftTopText.enable"
            :style="[leftTextStyle]"
          >
            {{ leftTopText.text }}
          </text>
          <view
            class="right-text"
            v-if="rightTopText.enable"
            :style="[rightTextStyle]"
            @click="goMenuPage(rightTopText.link, item)"
          >
            {{ rightTopText.text }}
            <text
              class="iconfont icon-xiangyou"
              v-if="rightTopText.text && rightTopText.link"
              :style="[rightIconStyle]"
            ></text>
          </view>
        </view>

        <!-- 列表样式 -->
        <view class="list-menu" v-if="menuStyleConfig === 2">
          <view class="list-item" v-for="(item, index) in menus" :key="index">
            <view
              v-if="
                !item.routine_contact_type ||
                item.info[1].value !== '/pages/extension/customer_list/chat'
              "
              class="list-item-content"
              @click="goMenuPage(item.info[1].value)"
            >
              <view class="list-img-box" v-if="navDisplayStyle !== 2">
                <block v-if="listStyle == 0">
                  <easy-loadimage
                    v-if="item.img"
                    :image-src="item.img"
                    width="90rpx"
                    height="90rpx"
                    :borderRadius="imgStyle"
                  ></easy-loadimage>
                  <view class="list-empty-box" v-else>
                    <image
                      src="/static/images/shandian.png"
                      mode="aspectFill"
                    ></image>
                  </view>
                </block>
                <block v-else>
                  <view class="icon-box" :style="[iconBoxStyle]">
                    <view
                      :class="['iconfont', item.icon]"
                      :style="[iconContentStyle]"
                    ></view>
                    <text v-if="item.num" class="num">{{ item.num }}</text>
                  </view>
                </block>
              </view>
              <view
                class="list-text"
                :style="[textColor]"
                v-if="navDisplayStyle !== 1"
              >
                {{ item.info[0].value }}
              </view>
            </view>
            <template v-else>
              <!-- #ifdef MP -->
              <button
                class="list-item-content"
                open-type="contact"
                v-if="item.routine_contact_type"
              >
                <view class="list-img-box" v-if="navDisplayStyle !== 2">
                  <block v-if="listStyle == 0">
                    <easy-loadimage
                      v-if="item.img"
                      :image-src="item.img"
                      width="90rpx"
                      height="90rpx"
                      :borderRadius="imgStyle"
                    ></easy-loadimage>
                    <view class="list-empty-box" v-else>
                      <image
                        src="/static/images/shandian.png"
                        mode="aspectFill"
                      ></image>
                    </view>
                  </block>
                  <block v-else>
                    <view class="icon-box" :style="[iconBoxStyle]">
                      <view
                        :class="['iconfont', item.icon]"
                        :style="[iconContentStyle]"
                      ></view>
                      <text v-if="item.num" class="num">{{ item.num }}</text>
                    </view>
                  </block>
                </view>
                <view
                  class="list-text"
                  :style="[textColor]"
                  v-if="navDisplayStyle !== 1"
                >
                  {{ item.info[0].value }}
                </view>
              </button>
              <!-- #endif -->
            </template>
            <text class="iconfont icon-xiangyou list-arrow"></text>
          </view>
        </view>

        <!-- 宫格/排列样式 -->
        <view v-else>
          <view class="swiper" v-if="isShowConfig">
            <swiper
              :interval="3000"
              :duration="500"
              :style="'height:' + navHigh + 'px;'"
              @change="bannerfun"
            >
              <block>
                <swiper-item v-for="(item, indexw) in menuList" :key="indexw">
                  <view
                    class="menu"
                    :id="'nav' + indexw"
                    :class="{
                      'grid-three': menuStyleConfig === 1 && gridStyle === 0,
                      'grid-four': menuStyleConfig === 1 && gridStyle === 1,
                    }"
                    :style="[gridContainerStyle]"
                  >
                    <template
                      v-for="(itemn, indexn) in item.list"
                      :key="indexn"
                    >
                      <view
                        v-if="
                          !itemn.routine_contact_type ||
                          itemn.info[1].value !==
                            '/pages/extension/customer_list/chat'
                        "
                        class="flex-col flex-center"
                        :class="{
                          'grid-item': menuStyleConfig === 1,
                        }"
                        @click="goMenuPage(itemn.info[1].value)"
                        :style="[gridItemContentStyle]"
                      >
                        <view
                          class="pictrue skeleton-radius"
                          v-if="navDisplayStyle !== 2"
                        >
                          <block v-if="listStyle == 0">
                            <easy-loadimage
                              :image-src="itemn.img"
                              width="90rpx"
                              height="90rpx"
                              :borderRadius="imgStyle"
                            ></easy-loadimage>
                          </block>
                          <block v-else>
                            <view class="icon-box" :style="[iconBoxStyle]">
                              <view
                                :class="['iconfont', itemn.icon]"
                                :style="[iconContentStyle]"
                              ></view>
                              <text v-if="itemn.num" class="num">{{
                                itemn.num
                              }}</text>
                            </view>
                          </block>
                        </view>
                        <view
                          class="fs-24"
                          :class="{ 'mt-10': listStyle === 0 }"
                          :style="[textColor]"
                          v-if="navDisplayStyle !== 1"
                          >{{ itemn.info[0].value }}</view
                        >
                      </view>
                      <template v-else>
                        <!-- #ifdef MP -->
                        <button
                          class="flex-col flex-center reset-btn"
                          :class="{
                            'grid-item': menuStyleConfig === 1,
                          }"
                          :key="'btn_' + indexn"
                          open-type="contact"
                          v-if="itemn.routine_contact_type"
                          :style="[gridItemContentStyle]"
                        >
                          <view
                            class="pictrue skeleton-radius"
                            v-if="navDisplayStyle !== 2"
                          >
                            <block v-if="listStyle == 0">
                              <easy-loadimage
                                :image-src="itemn.img"
                                width="90rpx"
                                height="90rpx"
                                :borderRadius="imgStyle"
                              ></easy-loadimage>
                            </block>
                            <block v-else>
                              <view class="icon-box" :style="[iconBoxStyle]">
                                <view
                                  :class="['iconfont', itemn.icon]"
                                  :style="[iconContentStyle]"
                                ></view>
                                <text v-if="itemn.num" class="num">{{
                                  itemn.num
                                }}</text>
                              </view>
                            </block>
                          </view>
                          <view
                            class="fs-24"
                            :class="{ 'mt-10': listStyle === 0 }"
                            :style="[textColor]"
                            v-if="navDisplayStyle !== 1"
                            >{{ itemn.info[0].value }}</view
                          >
                        </button>
                        <!-- #endif -->
                      </template>
                    </template>
                  </view>
                </swiper-item>
              </block>
            </swiper>
          </view>
          <view v-else>
            <view
              class="menu"
              :class="{
                'grid-three': menuStyleConfig === 1 && gridStyle === 0,
                'grid-four': menuStyleConfig === 1 && gridStyle === 1,
              }"
              :style="[gridContainerStyle]"
            >
              <template v-for="(item, index) in menus" :key="index">
                <view
                  v-if="
                    !item.routine_contact_type ||
                    item.info[1].value !== '/pages/extension/customer_list/chat'
                  "
                  class="flex-col flex-center"
                  :class="{
                    'grid-item': menuStyleConfig === 1,
                  }"
                  @click="goMenuPage(item.info[1].value)"
                  :style="[gridItemContentStyle]"
                >
                  <view
                    class="pictrue skeleton-radius"
                    v-if="navDisplayStyle !== 2"
                  >
                    <block v-if="listStyle == 0">
                      <easy-loadimage
                        :image-src="item.img"
                        width="90rpx"
                        height="90rpx"
                        :borderRadius="imgStyle"
                      ></easy-loadimage>
                    </block>
                    <block v-else>
                      <view class="icon-box" :style="[iconBoxStyle]">
                        <view
                          :class="['iconfont', item.icon]"
                          :style="[iconContentStyle]"
                        ></view>
                        <!-- 角标数量 -->
                        <text v-if="item.num" class="num">{{ item.num }}</text>
                      </view>
                    </block>
                  </view>
                  <view
                    class="fs-24"
                    :class="{ 'mt-10': listStyle === 0 }"
                    :style="[textColor]"
                    v-if="navDisplayStyle !== 1"
                    >{{ item.info[0].value }}</view
                  >
                </view>
                <template v-else>
                  <!-- #ifdef MP -->
                  <button
                    class="flex-col flex-center reset-btn"
                    :class="{
                      'grid-item': menuStyleConfig === 1,
                    }"
                    :key="'btn_' + index"
                    open-type="contact"
                    v-if="item.routine_contact_type"
                    :style="[gridItemContentStyle]"
                  >
                    <view
                      class="pictrue skeleton-radius"
                      v-if="navDisplayStyle !== 2"
                    >
                      <block v-if="listStyle == 0">
                        <easy-loadimage
                          :image-src="item.img"
                          width="90rpx"
                          height="90rpx"
                          :borderRadius="imgStyle"
                        ></easy-loadimage>
                      </block>
                      <block v-else>
                        <view class="icon-box" :style="[iconBoxStyle]">
                          <view
                            :class="['iconfont', item.icon]"
                            :style="[iconContentStyle]"
                          ></view>
                          <!-- 角标数量 -->
                          <text v-if="item.num" class="num">{{
                            item.num
                          }}</text>
                        </view>
                      </block>
                    </view>
                    <view
                      class="fs-24"
                      :class="{ 'mt-10': listStyle === 0 }"
                      :style="[textColor]"
                      v-if="navDisplayStyle !== 1"
                      >{{ item.info[0].value }}</view
                    >
                  </button>
                  <!-- #endif -->
                </template>
              </template>
            </view>
          </view>
          <view
            class="dot acea-row row-center-wrapper"
            v-if="menuList.length > 1 && isShowConfig"
          >
            <view
              class="dot-item line_dot-item"
              :style="[getDotStyle(index)]"
              v-for="(item, index) in menuList"
              :key="index"
            ></view>
          </view>
        </view>
      </view>
    </common-wrapper>
  </view>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, getCurrentInstance } from "vue";
import commonWrapper from "./commonWrapper.vue";
import { getCustomer } from "@/utils/index.js";
import util from "@/utils/util.js";
import { useColor } from '@/composables/useColor.js';
	let app = getApp();

const theme = ref(app.globalData.theme);
const { colorStyle } = useColor();
const { proxy } = getCurrentInstance();

function parseJson(value, fallback) {
  if (!value) return fallback;
  if (typeof value === "object") return value;
  try {
    return JSON.parse(value);
  } catch (e) {
    return fallback;
  }
}

function toArray(value) {
  const data = parseJson(value, value || []);
  if (Array.isArray(data)) return data;
  if (data && typeof data === "object") {
    return Object.keys(data)
      .sort((a, b) => Number(a) - Number(b))
      .map((key) => data[key]);
  }
  return [];
}

function normalizeMenuItem(item = {}) {
  const info = Array.isArray(item.info) ? item.info : [];
  const titleInfo = info[0] || {};
  const linkInfo = info[1] || {};
  return {
    ...item,
    img: item.img || item.image || item.pic || "",
    icon: item.icon || "",
    show: item.show !== false,
    info: [
      {
        ...titleInfo,
        value: titleInfo.value || item.title || item.name || item.label || "",
      },
      {
        ...linkInfo,
        value: linkInfo.value || item.link || item.url || item.path || "",
      },
    ],
  };
}

const props = defineProps({
  dataConfig: {
    type: Object,
    default: () => ({}),
  },
  isSortType: {
    type: [String, Number],
    default: 0,
  },
});

const navHigh = ref(0);
const menuList = ref([]);
const active = ref(0);

const rowsNum = computed(() =>
  props.dataConfig.rowsNum ? props.dataConfig.rowsNum.tabVal : 0,
);
const number = computed(() =>
  props.dataConfig.number ? props.dataConfig.number.tabVal : 1,
);
const configData = computed(() => ({ ...props.dataConfig }));
const menus = computed(() => {
  const menuConfig = props.dataConfig.menuConfig || {};
  return toArray(menuConfig.list)
    .map((item) => normalizeMenuItem(item))
    .filter((item) => item.show);
});
const isShowConfig = computed(() =>
  props.dataConfig.showConfig ? props.dataConfig.showConfig.tabVal : 0,
);
const textColor = computed(() => ({
  color: props.dataConfig.textColor
    ? props.dataConfig.textColor.color[0].item
    : "#333",
}));
const boxContentStyle = computed(() => {
  const bgColor = props.dataConfig.bgColor || {
    color: [{ item: "#fff" }, { item: "#fff" }],
  };
  return {
    background: `linear-gradient(90deg, ${bgColor.color[0].item} 0%, ${bgColor.color[1].item} 100%)`,
  };
});
const imgStyle = computed(() => {
  const filletImg = props.dataConfig.filletImg || {
    val: 0,
    type: 0,
    valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
  };
  let borderRadius = `${filletImg.val * 2}rpx`;
  if (filletImg.type) {
    borderRadius = `${filletImg.valList[0].val * 2}rpx ${
      filletImg.valList[1].val * 2
    }rpx ${filletImg.valList[3].val * 2}rpx ${filletImg.valList[2].val * 2}rpx`;
  }
  return borderRadius;
});
//分几行展示，一行展示多少个
const gridColumns = computed(() => {
  const rowGap = {
    rowGap: "24rpx",
    gridRowGap: "24rpx",
  };
  if (number.value == 0) {
    return { ...rowGap, gridTemplateColumns: "repeat(3, 1fr)" };
  } else if (number.value == 1) {
    return { ...rowGap, gridTemplateColumns: "repeat(4, 1fr)" };
  } else {
    return { ...rowGap, gridTemplateColumns: "repeat(5, 1fr)" };
  }
});
const menuStyleConfig = computed(() =>
  props.dataConfig.menuStyleConfig
    ? props.dataConfig.menuStyleConfig.tabVal
    : 0,
);
const dotColor = computed(() =>
  props.dataConfig.pointerBgColor
    ? props.dataConfig.pointerBgColor.color[0].item
    : "#DDDDDD",
);
const dotSelectColor = computed(() =>
  props.dataConfig.pointerColor
    ? props.dataConfig.pointerColor.color[0].item
    : "#E93323",
);
const headerConfig = computed(() => props.dataConfig.headerConfig || {});
const leftTopText = computed(() => props.dataConfig.leftTopText || {});
const rightTopText = computed(() => props.dataConfig.rightTopText || {});
const headerStyle = computed(() => props.dataConfig.headerStyle || {});
const navDisplayStyle = computed(() =>
  props.dataConfig.navDisplayStyle
    ? props.dataConfig.navDisplayStyle.tabVal
    : 0,
);
const gridStyle = computed(() =>
  props.dataConfig.gridStyle ? props.dataConfig.gridStyle.tabVal : 0,
);
const gridItemStyle = computed(() =>
  Object.assign(
    {
      itemPadding: 8,
      itemBgColor: "#ffffff",
      itemRadius: 0,
      itemPaddingTop: 0,
    },
    props.dataConfig.gridItemStyle,
  ),
);
const listStyle = computed(() =>
  props.dataConfig.menuConfig ? props.dataConfig.menuConfig.listStyle || 0 : 0,
);
const iconStyleConfig = computed(() => {
  let iconConfig = props.dataConfig.iconStyleConfig || {};
  return {
    color: iconConfig.color ? iconConfig.color.color[0].item : "#333",
    size: iconConfig.size ? iconConfig.size.val : 24,
    position: iconConfig.position ? iconConfig.position.tabVal : 1,
    padding: iconConfig.padding ? iconConfig.padding.val : 0,
    rotate: iconConfig.rotate ? iconConfig.rotate.val : 0,
    shadow: iconConfig.shadow ? iconConfig.shadow.tabVal : 0,
  };
});
const iconBoxStyle = computed(() => {
  const position = iconStyleConfig.value.position;
  return {
    justifyContent:
      position === 0 ? "flex-start" : position === 1 ? "center" : "flex-end",
  };
});
const headerBoxStyle = computed(() => ({
  paddingTop: headerStyle.value.topPadding * 2 + "rpx",
  paddingBottom: headerStyle.value.bottomPadding * 2 + "rpx",
  paddingLeft: headerStyle.value.leftRightPadding * 2 + "rpx",
  paddingRight: headerStyle.value.leftRightPadding * 2 + "rpx",
}));
const leftTextStyle = computed(() => ({
  color: headerStyle.value.leftColor,
  fontSize: headerStyle.value.fontSize * 2 + "rpx",
  fontWeight: headerStyle.value.leftWeight,
}));
const rightTextStyle = computed(() => ({
  color: headerStyle.value.rightColor,
  fontSize: (headerStyle.value.rightFontSize || 12) * 2 + "rpx",
  fontWeight: headerStyle.value.rightWeight,
}));
const rightIconStyle = computed(() => ({
  fontSize: (headerStyle.value.rightFontSize || 12) * 2 + "rpx",
}));
const iconContentStyle = computed(() => {
  const config = iconStyleConfig.value;
  return {
    color: config.color,
    fontSize: config.size * 2 + "rpx",
    padding: config.padding * 2 + "rpx",
    transform: "rotate(" + config.rotate + "deg)",
    textShadow: config.shadow ? "0px 2px 4px rgba(0,0,0,0.2)" : "none",
  };
});
const gridContainerStyle = computed(() => {
  if (menuStyleConfig.value === 1) {
    const columnGap = gridItemStyle.value.itemPadding * 2 + "rpx";
    const rowGap = gridItemStyle.value.itemPaddingTop * 2 + "rpx";
    return {
      columnGap,
      gridColumnGap: columnGap,
      rowGap,
      gridRowGap: rowGap,
    };
  } else {
    return gridColumns.value;
  }
});
const gridItemContentStyle = computed(() => {
  if (menuStyleConfig.value === 1) {
    return {
      backgroundColor: gridItemStyle.value.itemBgColor,
      borderRadius: gridItemStyle.value.itemRadius * 2 + "rpx",
      paddingTop: gridItemStyle.value.itemPaddingTop * 2 + "rpx",
      paddingBottom: gridItemStyle.value.itemPaddingTop * 2 + "rpx",
    };
  } else {
    return {};
  }
});

onMounted(() => {
  getSwiperCount();
  nextTick(() => {
    if (menuList.value.length && isShowConfig.value) {
      // #ifdef H5
      menuHeight();
      // #endif
      // #ifndef H5
      setTimeout(() => {
        menuHeight();
      }, 150);
      // #endif
    }
  });
});

function getSwiperCount() {
  /* rowsNum 显示行数  0: 1行  1: 2行 2: 3行 3 4行 */
  /* number  单行显示  0: 3个  1: 4个 2: 5个 */
  const columns =
    menuStyleConfig.value === 1 ? gridStyle.value + 3 : number.value + 3;
  pageNum((rowsNum.value + 1) * columns);
}
function bannerfun(e) {
  active.value = e.detail.current;
}
function menuHeight() {
  const query = uni.createSelectorQuery().in(proxy);
  query
    .select("#nav0")
    .boundingClientRect((data) => {
      navHigh.value = data ? data.height : 0;
    })
    .exec();
}
function pageNum(num) {
  let count = Math.ceil(menus.value.length / num);
  let goodArray = new Array();
  for (let i = 0; i < count; i++) {
    let list = menus.value.slice(i * num, i * num + num);
    if (list.length)
      goodArray.push({
        list: list,
      });
  }
  menuList.value = goodArray;
}

function getDotStyle(index) {
  return {
    "background-color":
      active.value === index ? dotSelectColor.value : dotColor.value,
  };
}
function menusTap(url) {
  util.JumpPath(url);
}
function goMenuPage(url) {
  if (!url) return;
  // 不在此处统一拦截登录，交由目标页 onLoad 自行 checkLogin
  if (url.indexOf("http") === -1) {
    if (url == "/kefu/mobile_list") {
      return uni.navigateTo({
        url: `/pages/annex/web_view/index?url=${location.origin}${url}`,
      });
    } else if (url == "/pages/extension/customer_list/chat") {
      return getCustomer(url);
    }
    util.JumpPath(url);
  } else {
    util.JumpPath(url);
  }
}
</script>

<style lang="scss" scoped>
.menu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .right-text {
    display: flex;
    align-items: center;
  }
}

.dot {
  width: 100%;
  padding-bottom: 20rpx;

  .instruct {
    width: 50rpx;
    height: 36rpx;
    line-height: 36rpx;
    background-color: rgba(0, 0, 0, 0.8);
    color: #fff;
    border-radius: 16rpx;
    font-size: 24rpx;
    text-align: center;
  }

  .dot-item {
    width: 10rpx;
    height: 10rpx;
    background: rgba(0, 0, 0, 0.4);
    border-radius: 50%;
    margin: 0 4px;

    &.line_dot-item {
      width: 20rpx;
      height: 6rpx;
      border-radius: 3rpx;
    }
  }
}
.nav {
  .item {
    margin-right: 42rpx;
    width: 90rpx;
    text-align: center;
    font-size: 24rpx;
    display: inline-block;
  }
}
.menu {
  display: grid;
  grid-template-rows: auto;
  width: 100%;
  padding: 0 0 20rpx 0;
}
.swiper {
  z-index: 20;
  position: relative;
  overflow: hidden;
  swiper,
  .swiper-item {
    width: 100%;
    display: block;
  }
}

.list-menu {
  .list-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 0 24rpx;
    padding: 24rpx 0;
    border-bottom: 1rpx solid #eee;
    &:first-child {
      margin-top: 0;
    }
    &:last-child {
      border-bottom: none;
      margin-bottom: 0;
    }

    .list-item-content {
      display: flex;
      align-items: center;

      .list-img-box {
        margin-right: 20rpx;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .list-text {
        font-size: 28rpx;
      }
    }

    .list-arrow {
      color: #999;
      font-size: 24rpx;
    }
  }
}
.icon-box {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  width: 90rpx;
  height: 90rpx;
}
.num {
  @include main_bg_color(theme);
  position: absolute;
  top: 10rpx;
  right: 8rpx;
  height: 28rpx;
  min-width: 20rpx;
  padding: 4rpx 8rpx;
  line-height: 20rpx;
  text-align: center;
  font-size: 20rpx;
  color: #fff;
  border-radius: 50%;
}

.grid-three {
  grid-template-columns: repeat(3, 1fr) !important;
}

.grid-four {
  grid-template-columns: repeat(4, 1fr) !important;
}

.grid-item {
  width: 100%;
}
</style>
