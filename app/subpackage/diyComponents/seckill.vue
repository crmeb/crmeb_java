<template>
  <!-- 秒杀列表 -->
  <common-wrapper v-show="!isSortType" :config="configData">
    <view class="seckill" v-if="spikeList.length > 0">
      <view>
        <!-- 秒杀头部 -->
        <view
          class="w-full h-96 px-24 flex-between-center bg-cover"
          :style="[headerStyle]"
        >
          <view class="flex-y-center">
            <text
              class="fs-32 lh-44rpx fw-500"
              :style="[titleStyle]"
              v-if="titleConfig"
              >{{ titleTxtConfig }}</text
            >
            <image :src="titleImg" class="w-140 h-32" v-else></image>
            <text
              class="fs-26 text--w111-999 lh-36rpx pl-20"
              :style="[tipsColor]"
              >{{ '距离结束' }}</text
            >
            <countDown
              :is-day="false"
              tip-text=" "
              day-text=" "
              hour-text=":"
              minute-text=":"
              second-text=" "
              :datatime="datatime"
              :bgColor="numberBgColor"
              :colors="numberColor"
              :dotColor="dotColor"
            ></countDown>
          </view>
          <view
            class="flex-y-center fs-24 text--w111-999"
            :style="[headerBntColor]"
            @tap="goPage('/pages/activity/goods_seckill/index')"
          >
            <text>{{ rightBntTxt }}</text>
            <text
              class="iconfont icon-ic_rightarrow fs-24"
              :style="[headerBntColor]"
            ></text>
          </view>
        </view>
        <!-- 单列 -->
        <view
          class="pt-32 pr-20 pb-32 pl-20 bg--w111-fff"
          :style="[boxContentStyle]"
          v-if="goodStyleConfig == 0"
        >
          <view
            class="w-full flex justify-between item"
            v-for="(item, index) in spikeList"
            :key="index"
            @tap="goDetails(item)"
          >
            <easy-loadimage
              :image-src="item.image"
              :border-src="item.activity_image"
              width="240rpx"
              height="240rpx"
              :borderRadius="imgStyle"
            ></easy-loadimage>
            <view class="flex-1 flex-col justify-between pl-20 h-240">
              <view>
                <view
                  class="w-full fs-28 h-80 lh-40rpx line2"
                  :style="[productStyle]"
                  v-if="checkboxInfo.includes(0)"
                  >{{ item.title }}</view
                >
                <view
                  class="w-full flex-y-center mt-20"
                  v-if="checkboxInfo.includes(1)"
                >
                  <view class="progress relative">
                    <view
                      class="active"
                      :style="{
                        width: item.percent + '%',
                        background: progressBgColor,
                      }"
                    >
                      <!-- <image src="@/static/images/lightning.png" class="lightning"></image> -->
                    </view>
                    <image
                      src="@/static/images/lightning.png"
                      class="lightning"
                      :style="{
                        left: item.percent + '%',
                        transform:
                          item.percent == 100
                            ? 'translateX(-100%)'
                            : 'translateX(0)',
                      }"
                    ></image>
                  </view>
                  <text
                    class="fs-22 lh-30rpx pl-12"
                    :style="{ color: priceColor }"
                    >已抢{{ item.percent + "%" }}</text
                  >
                </view>
              </view>
              <view class="flex justify-between items-end">
                <view class="flex-col">
                  <baseMoney
                    :money="item.price"
                    symbolSize="24"
                    integerSize="36"
                    decimalSize="36"
                    weight
                    :color="priceColor"
                    preFix="秒杀价"
                    preFixSize="24"
                    :textColor="priceColor"
                    v-if="checkboxInfo.includes(2)"
                  ></baseMoney>
                  <text
                    class="text-line fs-28 text--w111-999 pt-10 mb-10 Regular"
                    v-if="checkboxInfo.includes(3)"
                    :style="[otPriceColor]"
                    >{{ '¥'
                    }}{{ formatMoney(item.product_price) }}</text
                  >
                </view>
                <view
                  class="w-144 h-56 rd-30rpx flex-center fs-24 text--w111-fff"
                  v-if="!showBtn"
                  :style="[btnBgColor]"
                  >{{ '去抢购' }}</view
                >
              </view>
            </view>
          </view>
        </view>
        <!-- 两列 -->
        <view
          class="grid-column-2 grid-gap-22rpx pt-32 pr-20 pb-32 pl-20"
          :style="[boxContentStyle]"
          v-if="goodStyleConfig == 1"
        >
          <view
            v-for="(item, index) in spikeList"
            :key="index"
            @tap="goDetails(item)"
          >
            <easy-loadimage
              :image-src="item.image"
              :border-src="item.activity_image"
              width="100%"
              height="324rpx"
              :borderRadius="imgStyle"
            ></easy-loadimage>
            <view
              class="w-full line2 mt-16 fs-28 lh-40rpx"
              :style="[productStyle]"
              v-if="checkboxInfo.includes(0)"
              >{{ item.title }}</view
            >
            <view class="flex justify-between items-end mt-10">
              <view class="flex-col">
                <baseMoney
                  :money="item.price"
                  symbolSize="24"
                  integerSize="36"
                  decimalSize="36"
                  weight
                  :color="priceColor"
                  v-if="checkboxInfo.includes(2)"
                ></baseMoney>
                <text
                  class="text-line fs-28 text--w111-999 pt-14 Regular"
                  v-if="checkboxInfo.includes(3)"
                  :style="[otPriceColor]"
                  >{{ '¥'
                  }}{{ formatMoney(item.product_price) }}</text
                >
              </view>
              <view
                class="w-144 h-56 rd-30rpx flex-center fs-24 text--w111-fff bg--w111-E93323"
                v-if="!showBtn"
                :style="[btnBgColor]"
                >{{ '去抢购' }}</view
              >
            </view>
          </view>
        </view>
        <!-- 三列 -->
        <view
          class="grid-column-3 grid-gap-18rpx pt-32 pr-20 pb-32 pl-20"
          :style="[boxContentStyle]"
          v-if="goodStyleConfig == 2"
        >
          <view
            v-for="(item, index) in spikeList"
            :key="index"
            @tap="goDetails(item)"
          >
            <easy-loadimage
              :image-src="item.image"
              :border-src="item.activity_image"
              width="100%"
              height="212rpx"
              :borderRadius="imgStyle"
            ></easy-loadimage>
            <view
              class="w-full line1 mt-16 fs-26"
              :style="[productStyle]"
              v-if="checkboxInfo.includes(0)"
              >{{ item.title }}</view
            >
            <view
              class="price-box flex-center relative"
              :style="[tagBag]"
              v-if="checkboxInfo.includes(2)"
            >
              <baseMoney
                :money="item.price"
                symbolSize="24"
                integerSize="30"
                decimalSize="30"
                weight
                :color="bargainPriceColor"
              ></baseMoney>
              <image
                src="@/static/images/shandian.png"
                class="shandian"
              ></image>
            </view>
            <view
              class="text-line fs-24 text--w111-999 Regular lh-32rpx mt-10"
              :style="[otPriceColor]"
              v-if="checkboxInfo.includes(3)"
              >{{ '¥'
              }}{{ formatMoney(item.product_price) }}</view
            >
          </view>
        </view>
        <!-- 滑动 -->
        <scroll-view
          scroll-x="true"
          show-scrollbar="false"
          class="white-nowrap vertical-middle w-full p-32"
          :style="[boxContentStyle]"
          v-if="goodStyleConfig == 3"
        >
          <view
            class="inline-block"
            :class="{ 'ml-20': index }"
            v-for="(item, index) in spikeList"
            :key="index"
            @tap="goDetails(item)"
          >
            <easy-loadimage
              :image-src="item.image"
              :border-src="item.activity_image"
              width="224rpx"
              height="224rpx"
              :borderRadius="imgStyle"
            ></easy-loadimage>
            <view
              class="w-222 line1 mt-16 fs-26 lh-26rpx"
              :style="[productStyle]"
              v-if="checkboxInfo.includes(0)"
              >{{ item.title }}</view
            >
            <view class="flex justify-between items-end mt-16">
              <view>
                <baseMoney
                  :money="item.price"
                  symbolSize="24"
                  integerSize="36"
                  decimalSize="36"
                  weight
                  :color="priceColor"
                  v-if="checkboxInfo.includes(2)"
                ></baseMoney>
                <view
                  class="text-line fs-24 text--w111-999 Regular lh-32rpx"
                  :style="[otPriceColor]"
                  v-if="checkboxInfo.includes(3)"
                  >{{ '¥'
                  }}{{ formatMoney(item.product_price) }}</view
                >
              </view>
              <view
                class="qiang flex-center fs-24 text--w111-fff"
                v-if="!showBtn"
                :style="[btnBgColor]"
                >{{ '抢' }}</view
              >
            </view>
          </view>
        </scroll-view>
      </view>
    </view>
  </common-wrapper>
</template>

<script>
import commonWrapper from "./commonWrapper.vue";
import countDown from "@/components/countDown";
import { getThemeSeckill } from "@/api/api.js";
export default {
  options: {
    styleIsolation: "shared",
  },
  name: "seckill",
  components: {
    countDown,
    commonWrapper,
  },
  props: {
    dataConfig: {
      type: Object,
      default: () => {},
    },
    isSortType: {
      type: [String, Number],
      default: 0,
    },
  },
  data() {
    return {
      datatime: "",
      spikeList: [],
      seckillTimeIndex: 0,
      seckillTime: [],
    };
  },
  computed: {
    configData() {
      return {
        ...this.dataConfig,
        paddingConfig: this.dataConfig.paddingConfig || {
          isAll: false,
          valList: [
            {
              val: this.dataConfig.topConfig
                ? this.dataConfig.topConfig.val
                : 0,
            },
            {
              val: this.dataConfig.prConfig ? this.dataConfig.prConfig.val : 0,
            },
            {
              val: this.dataConfig.bottomConfig
                ? this.dataConfig.bottomConfig.val
                : 0,
            },
            {
              val: this.dataConfig.prConfig ? this.dataConfig.prConfig.val : 0,
            },
          ],
        },
        marginConfig: this.dataConfig.marginConfig || {
          isAll: false,
          valList: [
            {
              val: this.dataConfig.mbConfig ? this.dataConfig.mbConfig.val : 0,
            },
            {
              val: 0,
            },
            {
              val: 0,
            },
            {
              val: 0,
            },
          ],
        },
      };
    },
    titleStyle() {
      let titleText = this.dataConfig.titleText;
      return {
        fontStyle: !titleText.tabVal
          ? "normal"
          : titleText.tabList[titleText.tabVal].style,
        fontWeight: !titleText.tabVal ? "bold" : "normal",
        color: this.dataConfig.titleColor.color[0].item,
        fontSize: this.dataConfig.titleNumber.val * 2 + "rpx",
      };
    },
    // boxStyle() {
    //   return {
    //     padding: `${this.dataConfig.topConfig.val * 2}rpx ${
    //       this.dataConfig.prConfig.val * 2
    //     }rpx ${this.dataConfig.bottomConfig.val * 2}rpx`,
    //     marginTop: `${this.dataConfig.mbConfig.val * 2}rpx`,
    //     background: this.dataConfig.bottomBgColor.color[0].item,
    //   };
    // },
    tagBag() {
      return {
        color: this.dataConfig.toneConfig.tabVal
          ? this.dataConfig.goodsBntTxtColor.color[0].item
          : "#fff",
        background: this.dataConfig.toneConfig.tabVal
          ? `linear-gradient(270deg,${this.dataConfig.goodsBntColor.color[1].item} 0%,${this.dataConfig.goodsBntColor.color[0].item} 100%)`
          : "var(--view-theme)",
      };
    },
    bargainPriceColor() {
      return this.dataConfig.toneConfig.tabVal
        ? this.dataConfig.seckillPriceColor2.color[0].item
        : "#fff";
    },
    boxContentStyle() {
      let br = `${this.dataConfig.fillet.val * 2}rpx`;
      let borderRadius = `0 0 ${br} ${br}`;
      if (this.dataConfig.fillet.type) {
        borderRadius = `0 0 ${this.dataConfig.fillet.valList[3].val * 2}rpx ${
          this.dataConfig.fillet.valList[2].val * 2
        }rpx`;
      }
      return {
        borderRadius,
        background: `linear-gradient(90deg, ${this.dataConfig.moduleColor.color[0].item} 0%, ${this.dataConfig.moduleColor.color[1].item} 100%)`,
      };
    },
    /*商品模板*/
    goodStyleConfig() {
      return this.dataConfig.goodStyleConfig.tabVal;
    },
    styleConfig() {
      return this.dataConfig.styleConfig.tabVal;
    },
    headerStyle() {
      let br = `${this.dataConfig.fillet.val * 2}rpx`,
        borderRadius = "",
        imgBgUrl = this.dataConfig.imgBgConfig.url;
      if (this.dataConfig.fillet.type) {
        borderRadius = `${this.dataConfig.fillet.valList[0].val * 2}rpx ${
          this.dataConfig.fillet.valList[1].val * 2
        }rpx 0 0`;
      } else {
        borderRadius = `${br} ${br} 0 0`;
      }
      return {
        backgroundImage: this.styleConfig
          ? "url(" + imgBgUrl + ")"
          : `linear-gradient(90deg,${this.dataConfig.headerBgColor.color[0].item} 0%,${this.dataConfig.headerBgColor.color[1].item} 100%)`,
        borderRadius,
      };
    },
    /*标题是文本还是图片*/
    titleConfig() {
      return this.dataConfig.titleConfig.tabVal;
    },
    /*标题文本*/
    titleTxtConfig() {
      return this.dataConfig.titleTxtConfig.value;
    },
    /*标题图片*/
    titleImg() {
      return this.styleConfig ? this.titleUrl : this.titleColorUrl;
    },
    titleColorUrl() {
      return this.dataConfig.imgColorConfig.url;
    },
    titleUrl() {
      return this.dataConfig.imgConfig.url;
    },
    /*标题提示文字*/
    tipsColor() {
      return {
        color: this.styleConfig
          ? this.dataConfig.tipsColor.color[0].item
          : this.dataConfig.tipsColor2.color[0].item,
      };
    },
    /*头部按钮文本*/
    rightBntTxt() {
      return this.dataConfig.rightBntConfig.value;
    },
    /*头部按钮样式*/
    headerBntColor() {
      return {
        color: this.styleConfig
          ? this.dataConfig.headerBntColor.color[0].item
          : this.dataConfig.headerBntColor2.color[0].item,
        fontSize: `${this.dataConfig.bntNumber.val * 2}rpx`,
      };
    },
    /*商品图片圆角样式*/
    imgStyle() {
      let borderRadius = `${this.dataConfig.filletImg.val * 2}rpx`;
      if (this.dataConfig.filletImg.type) {
        borderRadius = `${this.dataConfig.filletImg.valList[0].val * 2}rpx ${
          this.dataConfig.filletImg.valList[1].val * 2
        }rpx ${this.dataConfig.filletImg.valList[3].val * 2}rpx ${
          this.dataConfig.filletImg.valList[2].val * 2
        }rpx`;
      }
      return borderRadius;
    },
    /*商品名称样式*/
    productStyle() {
      return {
        color: this.dataConfig.goodsNameColor.color[0].item,
        fontWeight: this.dataConfig.goodsName.tabVal ? "normal" : "bold",
      };
    },
    /* 展示信息 */
    checkboxInfo() {
      return this.dataConfig.checkboxInfo.type;
    },
    /* 价格颜色 */
    priceColor() {
      return this.dataConfig.toneConfig.tabVal
        ? this.dataConfig.progressTxtColor.color[0].item
        : "var(--view-theme)";
    },
    /* 划线价颜色 */
    otPriceColor() {
      return {
        color: this.dataConfig.goodsPriceColor.color[0].item,
      };
    },
    showBtn() {
      return this.dataConfig.seckillConfig.tabVal;
    },
    /* 按钮颜色 */
    btnBgColor() {
      return {
        background: this.dataConfig.toneConfig.tabVal
          ? `linear-gradient(90deg,${this.dataConfig.goodsBntColor.color[1].item} 0%,${this.dataConfig.goodsBntColor.color[0].item} 100%)`
          : "linear-gradient(90deg, var(--view-gradient) 0%, var(--view-theme) 100%)",
        color: this.dataConfig.goodsBntTxtColor.color[0].item,
      };
    },
    progressBgColor() {
      return this.dataConfig.toneConfig.tabVal
        ? `linear-gradient(90deg,${this.dataConfig.progressColor.color[0].item} 0%,${this.dataConfig.progressColor.color[1].item} 100%)`
        : "linear-gradient(45deg, var(--view-gradient) 0%, var(--view-theme) 100%)";
    },
    /*倒计时背景色*/
    numberBgColor() {
      return this.styleConfig
        ? `linear-gradient(90deg, ${this.dataConfig.numberBgColor.color[0].item} 0%, ${this.dataConfig.numberBgColor.color[1].item} 100%)`
        : `linear-gradient(90deg, ${this.dataConfig.numberBgColor2.color[0].item} 0%, ${this.dataConfig.numberBgColor2.color[1].item} 100%)`;
    },
    numberColor() {
      return this.styleConfig
        ? this.dataConfig.numberColor.color[0].item
        : this.dataConfig.numberColor2.color[0].item;
    },
    dotColor() {
      return this.styleConfig
        ? this.dataConfig.numberBgColor.color[0].item
        : this.dataConfig.numberBgColor2.color[0].item;
    },
    /*商品数量*/
    numberConfig() {
      return this.dataConfig.numberConfig.val;
    },
  },
  mounted() {
    this.getSeckillIndexTime();
  },
  methods: {
    goPage(url) {
      uni.navigateTo({
        url,
      });
    },
    goDetails(item) {
      let url = "/pages/activity/goods_seckill_details/index?id=" + item.id + "&type=1";
      const timeItem = this.timeList && this.timeList[this.active];
      if (timeItem && timeItem.id) {
        url += "&time_id=" + timeItem.id;
      }
      uni.navigateTo({
        url,
      });
    },
    formatMoney(value) {
      const number = Number(value);
      return Number.isFinite(number) ? number.toFixed(2) : "0.00";
    },
    normalizeProduct(item) {
      const productPrice =
        item.product_price !== undefined && item.product_price !== null
          ? item.product_price
          : item.ot_price !== undefined && item.ot_price !== null
          ? item.ot_price
          : item.otPrice !== undefined && item.otPrice !== null
          ? item.otPrice
          : item.price;
      return {
        ...item,
        title: item.title || item.store_name || item.storeName || "",
        product_price: productPrice === undefined || productPrice === null ? 0 : productPrice,
        percent: Number(item.percent || item.sales_percent || item.salesPercent || 0),
      };
    },
    getSeckillIndexTime() {
      let limit = this.$config.LIMIT;
      let params = {
        limit: this.numberConfig >= limit ? limit : this.numberConfig,
      };
      this.datatime = Math.floor(Date.now() / 1000) + 24 * 60 * 60;
      getThemeSeckill(params).then(({ data }) => {
        const list = Array.isArray(data) ? data : data && data.list ? data.list : [];
        this.spikeList = list.map((item) => this.normalizeProduct(item));
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.Regular {
  font-family: "Regular";
}
::v-deep .styleAll {
  padding: 0 8rpx;
  border-radius: 4rpx;
  font-size: 24rpx;
  line-height: 40rpx;
}
.bg-cover {
  background-repeat: no-repeat;
  background-size: 100% 100%;
}
.item ~ .item {
  margin-top: 32rpx;
}
.progress {
  width: 240rpx;
  height: 24rpx;
  border-radius: 12rpx;
  background-color: rgba(253, 240, 237, 1);
  overflow: hidden;
  .active {
    height: 24rpx;
    border-radius: 12rpx;
    background: linear-gradient(45deg, #ff7931 0%, #e93323 100%);
  }
  .lightning {
    position: absolute;
    top: -2rpx;
    width: 28rpx;
    height: 28rpx;
  }
}
.price-box {
  width: 150rpx;
  height: 44rpx;
  background: linear-gradient(
    45deg,
    var(--view-gradient) 0%,
    var(--view-theme) 100%
  );
  border-radius: 0px 8rpx 8rpx 0px;
  margin: 20rpx 0 0 10rpx;
  .shandian {
    position: absolute;
    width: 24rpx;
    height: 44rpx;
    top: 0;
    left: -10rpx;
  }
}
.qiang {
  width: 66rpx;
  height: 40rpx;
  background: linear-gradient(45deg, #ff7931 0%, #e93323 100%);
  border-radius: 0px 22rpx 22rpx 0px;
  position: relative;
  &:before {
    content: "";
    position: absolute;
    top: 0;
    left: -10rpx;
    width: 24rpx;
    height: 40rpx;
    background-image: url("@/static/images/shandian.png");
    background-size: cover;
  }
}
scroll-view {
  box-sizing: border-box;
}
</style>
