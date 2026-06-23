<template>
  <view v-show="!isSortType && menus.length">
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
                      src="/static/images/shan.png"
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
                        src="/static/images/shan.png"
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
                    <template v-for="(itemn, indexn) in item.list">
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
                        :key="indexn"
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
              <template v-for="(item, index) in menus">
                <view
                  v-if="
                    !item.routine_contact_type ||
                    item.info[1].value !== '/pages/extension/customer_list/chat'
                  "
                  class="flex-col flex-center"
                  :class="{
                    'grid-item': menuStyleConfig === 1,
                  }"
                  :key="index"
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

<script>
import commonWrapper from "./commonWrapper.vue";
import { getCustomer } from "@/utils/index.js";
import { toLogin } from "@/libs/login.js";
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

export default {
  components: { commonWrapper },
  name: "menus",
  props: {
    dataConfig: {
      type: Object,
      default: () => ({}),
    },
    isSortType: {
      type: [String, Number],
      default: 0,
    },
  },
  data() {
    return {
      navHigh: 0,
      menuList: [],
      active: 0,
    };
  },
  computed: {
    ...mapGetters({
      isLogin: "isLogin",
    }),

    rowsNum() {
      return this.dataConfig.rowsNum ? this.dataConfig.rowsNum.tabVal : 0;
    },
    number() {
      return this.dataConfig.number ? this.dataConfig.number.tabVal : 1;
    },
    configData() {
      return {
        ...this.dataConfig,
      };
    },
    menus() {
      const menuConfig = this.dataConfig.menuConfig || {};
      return toArray(menuConfig.list)
        .map((item) => normalizeMenuItem(item))
        .filter((item) => item.show);
    },
    isShowConfig() {
      return this.dataConfig.showConfig ? this.dataConfig.showConfig.tabVal : 0;
    },
    textColor() {
      return {
        color: this.dataConfig.textColor
          ? this.dataConfig.textColor.color[0].item
          : "#333",
      };
    },
    boxContentStyle() {
      const fillet = this.dataConfig.fillet || { val: 0, type: 0, valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }] };
      const bgColor = this.dataConfig.bgColor || { color: [{ item: "#fff" }, { item: "#fff" }] };
      let borderRadius = `${fillet.val * 2}rpx`;
      if (fillet.type) {
        borderRadius = `${fillet.valList[0].val * 2}rpx ${
          fillet.valList[1].val * 2
        }rpx ${fillet.valList[3].val * 2}rpx ${
          fillet.valList[2].val * 2
        }rpx`;
      }
      return {
        background: `linear-gradient(90deg, ${bgColor.color[0].item} 0%, ${bgColor.color[1].item} 100%)`,
      };
    },
    imgStyle() {
      const filletImg = this.dataConfig.filletImg || { val: 0, type: 0, valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }] };
      let borderRadius = `${filletImg.val * 2}rpx`;
      if (filletImg.type) {
        borderRadius = `${filletImg.valList[0].val * 2}rpx ${
          filletImg.valList[1].val * 2
        }rpx ${filletImg.valList[3].val * 2}rpx ${
          filletImg.valList[2].val * 2
        }rpx`;
      }
      return borderRadius;
    },
    //分几行展示，一行展示多少个
    gridColumns() {
      if (this.number == 0) {
        return {
          gridTemplateColumns: "repeat(3, 1fr)",
        };
      } else if (this.number == 1) {
        return {
          gridTemplateColumns: "repeat(4, 1fr)",
        };
      } else {
        return {
          gridTemplateColumns: "repeat(5, 1fr)",
        };
      }
    },
    menuStyleConfig() {
      return this.dataConfig.menuStyleConfig ? this.dataConfig.menuStyleConfig.tabVal : 0;
    },
    dotColor() {
      return this.dataConfig.pointerBgColor ? this.dataConfig.pointerBgColor.color[0].item : "#DDDDDD";
    },
    dotSelectColor() {
      return this.dataConfig.pointerColor ? this.dataConfig.pointerColor.color[0].item : "#E93323";
    },
    headerConfig() {
      return this.dataConfig.headerConfig || {};
    },
    leftTopText() {
      return this.dataConfig.leftTopText || {};
    },
    rightTopText() {
      return this.dataConfig.rightTopText || {};
    },
    headerStyle() {
      return this.dataConfig.headerStyle || {};
    },
    navDisplayStyle() {
      return this.dataConfig.navDisplayStyle
        ? this.dataConfig.navDisplayStyle.tabVal
        : 0;
    },
    gridStyle() {
      return this.dataConfig.gridStyle ? this.dataConfig.gridStyle.tabVal : 0;
    },
    gridItemStyle() {
      return (
        this.dataConfig.gridItemStyle || {
          itemPadding: 8,
          itemBgColor: "#ffffff",
          itemRadius: 0,
          itemPaddingTop: 0,
        }
      );
    },
    listStyle() {
      return this.dataConfig.menuConfig ? this.dataConfig.menuConfig.listStyle || 0 : 0;
    },
    iconStyleConfig() {
      let iconConfig = this.dataConfig.iconStyleConfig || {};
      return {
        color: iconConfig.color ? iconConfig.color.color[0].item : "#333",
        size: iconConfig.size ? iconConfig.size.val : 24,
        position: iconConfig.position ? iconConfig.position.tabVal : 1,
        padding: iconConfig.padding ? iconConfig.padding.val : 0,
        rotate: iconConfig.rotate ? iconConfig.rotate.val : 0,
        shadow: iconConfig.shadow ? iconConfig.shadow.tabVal : 0,
      };
    },
    iconBoxStyle() {
      const position = this.iconStyleConfig.position;
      return {
        justifyContent:
          position === 0
            ? "flex-start"
            : position === 1
            ? "center"
            : "flex-end",
      };
    },
    headerBoxStyle() {
      return {
        paddingTop: this.headerStyle.topPadding * 2 + "rpx",
        paddingBottom: this.headerStyle.bottomPadding * 2 + "rpx",
        paddingLeft: this.headerStyle.leftRightPadding * 2 + "rpx",
        paddingRight: this.headerStyle.leftRightPadding * 2 + "rpx",
      };
    },
    leftTextStyle() {
      return {
        color: this.headerStyle.leftColor,
        fontSize: this.headerStyle.fontSize * 2 + "rpx",
        fontWeight: this.headerStyle.leftWeight,
      };
    },
    rightTextStyle() {
      return {
        color: this.headerStyle.rightColor,
        fontSize: (this.headerStyle.rightFontSize || 12) * 2 + "rpx",
        fontWeight: this.headerStyle.rightWeight,
      };
    },
    rightIconStyle() {
      return {
        fontSize: (this.headerStyle.rightFontSize || 12) * 2 + "rpx",
      };
    },
    iconContentStyle() {
      const config = this.iconStyleConfig;
      return {
        color: config.color,
        fontSize: config.size * 2 + "rpx",
        padding: config.padding * 2 + "rpx",
        transform: "rotate(" + config.rotate + "deg)",
        textShadow: config.shadow ? "0px 2px 4px rgba(0,0,0,0.2)" : "none",
      };
    },
    gridContainerStyle() {
      if (this.menuStyleConfig === 1) {
        return {
          padding: this.gridItemStyle.itemPadding * 2 + "rpx",
        };
      } else {
        return this.gridColumns;
      }
    },
    gridItemContentStyle() {
      if (this.menuStyleConfig === 1) {
        return {
          backgroundColor: this.gridItemStyle.itemBgColor,
          borderRadius: this.gridItemStyle.itemRadius * 2 + "rpx",
          paddingTop: this.gridItemStyle.itemPaddingTop * 2 + "rpx",
          paddingBottom: this.gridItemStyle.itemPaddingTop * 2 + "rpx",
        };
      } else {
        return {};
      }
    },
  },
  mounted() {
    this.getSwiperCount();
    this.$nextTick(() => {
      if (this.menuList.length && this.isShowConfig) {
        let that = this;
        // #ifdef H5
        that.menuHeight();
        // #endif
        // #ifndef H5
        setTimeout(() => {
          that.menuHeight();
        }, 150);
        // #endif
      }
    });
  },
  methods: {
    getSwiperCount() {
      /* rowsNum 显示行数  0: 1行  1: 2行 2: 3行 3 4行 */
      /* number  单行显示  0: 3个  1: 4个 2: 5个 */
      this.pageNum((this.rowsNum + 1) * (this.number + 3));
    },
    bannerfun(e) {
      this.active = e.detail.current;
    },
    menuHeight() {
      let that = this;
      const query = uni.createSelectorQuery().in(this);
      query
        .select("#nav0")
        .boundingClientRect((data) => {
          that.navHigh = data ? data.height : 0;
        })
        .exec();
    },
    pageNum(num) {
      let count = Math.ceil(this.menus.length / num);
      let goodArray = new Array();
      for (let i = 0; i < count; i++) {
        let list = this.menus.slice(i * num, i * num + num);
        if (list.length)
          goodArray.push({
            list: list,
          });
      }
      this.$set(this, "menuList", goodArray);
    },

    getDotStyle(index) {
      return {
        "background-color":
          this.active === index ? this.dotSelectColor : this.dotColor,
      };
    },
    menusTap(url) {
      this.$util.JumpPath(url);
    },
    goMenuPage(url) {
      if (!url) return;
      if (this.isLogin) {
        if (url.indexOf("http") === -1) {
          if (url== "/kefu/mobile_list") {
            return uni.navigateTo({
              url: `/pages/annex/web_view/index?url=${location.origin}${url}`,
            });
          } else if (url == "/pages/extension/customer_list/chat") {
            return getCustomer(url);
          }
          this.$util.JumpPath(url);
        } else {
          this.$util.JumpPath(url);
        }
      } else {
        toLogin();
      }
    },
  },
};
</script>

<style lang="scss">
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
  grid-row-gap: 24rpx;
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
  background-color: #ff4444;
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
