<template>
  <view class="product-info-diy" v-if="productData">
    <common-wrapper :config="configData">
      <view class="product-info-box" :class="'style-' + specStyle">
        <!-- 图片区域 -->
        <view class="image-wrap">
          <swiper
            class="swiper"
            :circular="true"
            :autoplay="!videoPlaying"
            :interval="3000"
            :duration="500"
            @change="swiperChange"
          >
            <swiper-item v-for="(item, index) in sliderImage" :key="index">
              <!-- 视频项 -->
              <view v-if="item.isVideo" class="video-item">
                <view v-show="!videoControls" class="video-wrap">
                  <video
                    id="productVideo"
                    :src="item.src"
                    object-fit="contain"
                    controls
                    style="width: 100%; height: 100%"
                    show-center-play-btn
                    show-mute-btn="true"
                    auto-pause-if-navigate
                    :custom-cache="false"
                    :enable-progress-gesture="false"
                    :poster="item.poster"
                    @play="onVideoPlay"
                    @pause="onVideoPause"
                  ></video>
                </view>
                <view class="poster" v-show="videoControls">
                  <image
                    class="image"
                    :src="item.poster"
                    mode="aspectFill"
                  ></image>
                </view>
                <view
                  class="play-btn"
                  v-show="videoControls"
                  @click.stop="playVideo"
                >
                  <image
                    class="icon"
                    src="@/static/images/stop.png"
                    mode="aspectFit"
                  ></image>
                </view>
              </view>
              <!-- 图片项 -->
              <image
                v-else
                :src="item"
                mode="aspectFill"
                class="slide-image"
              ></image>
            </swiper-item>
          </swiper>
          <!-- 镜像效果 -->
          <view class="mirror-wrap">
            <image
              :src="currentSlideImage"
              mode="aspectFill"
              class="mirror-image"
            ></image>
          </view>

          <!-- 指示器 -->
          <view
            class="indicators"
            :class="'pos-' + indicatorPosition"
            v-if="indicatorConfig.tabVal !== undefined"
          >
            <!-- 线段样式 -->
            <view v-if="indicatorConfig.tabVal === 0" class="indicator-line">
              <view
                class="line-item"
                v-for="(item, index) in sliderImage"
                :key="index"
                :class="{ active: currentSwiper === index }"
                :style="{
                  backgroundColor:
                    currentSwiper === index ? selectColor : defaultColor,
                }"
              ></view>
            </view>
            <!-- 点线样式 -->
            <view v-if="indicatorConfig.tabVal === 1" class="indicator-dot">
              <view
                class="dot-item"
                v-for="(item, index) in sliderImage"
                :key="index"
                :class="{ active: currentSwiper === index }"
                :style="{
                  backgroundColor:
                    currentSwiper === index ? selectColor : defaultColor,
                }"
              ></view>
            </view>
            <!-- 数字样式 -->
            <view v-if="indicatorConfig.tabVal === 2" class="indicator-number">
              <view class="num-box">
                <text class="current">{{ currentSwiper + 1 }}</text>
                <text>/</text>
                <text class="total">{{ sliderImage.length }}</text>
              </view>
            </view>
          </view>

          <!-- 规格样式 4: 图片内部底部 -->
          <view
            v-if="specStyle === 3 && skuList.length > 0"
            class="spec-style-4"
          >
            <scroll-view scroll-x="true" class="spec-list">
              <view
                class="spec-item"
                v-for="(item, index) in skuList"
                :key="index"
                :style="{
                  borderColor:
                    index === selectedIndex
                      ? specSelectedBorderColor
                      : 'transparent',
                  background:
                    index === selectedIndex ? specSelectedBgColor : '#777777',
                }"
                @click="changeSpec(item, index)"
              >
                <image :src="item.image" mode="aspectFill"></image>
                <view class="spec-info">
                  <view
                    class="name line2"
                    :style="{
                      color:
                        index === selectedIndex
                          ? specSelectedTextColor
                          : specUnselectedTextColor,
                    }"
                  >
                    {{ item.suk || "规格" }}
                  </view>
                </view>
              </view>
            </scroll-view>
            <view
              class="total-count"
              :style="{ color: specTextColor }"
              @click="showSpecModal"
            >
              <view>
                <view>{{ skuList.length }}款</view>
                <view>可选</view>
              </view>
              <text class="iconfont icon-jiantou"></text>
            </view>
          </view>
        </view>

        <!-- 信息区域 -->
        <view class="info-box">
          <!-- 规格样式 0 & 1: 顶部 -->
          <view
            v-if="(specStyle === 0 || specStyle === 1) && skuList.length > 0"
            class="spec-top-section"
            :class="'style-' + specStyle"
          >
            <!-- 样式 0: 小图列表 -->
            <scroll-view
              scroll-x="true"
              class="spec-list"
              v-if="specStyle === 0"
            >
              <view
                class="spec-item"
                v-for="(item, index) in skuList"
                :key="index"
                @click="changeSpec(item, index)"
              >
                <image
                  :src="item.image"
                  mode="aspectFill"
                  :style="{
                    borderColor:
                      index === selectedIndex ? specSelectedColor : '#eee',
                  }"
                ></image>
              </view>
            </scroll-view>
            <view
              v-if="specStyle === 0"
              class="total-count"
              :style="{ color: specTextColor }"
              @click="showSpecModal"
            >
              <view>
                <view>{{ skuList.length }}款</view>
                <view>可选</view>
              </view>
              <text class="iconfont icon-jiantou"></text>
            </view>
            <!-- 样式 1: 图文列表 -->
            <view class="spec-list-text-wrapper" v-if="specStyle === 1">
              <scroll-view scroll-x="true" class="spec-list-text">
                <view
                  class="spec-item"
                  v-for="(item, index) in skuList"
                  :key="index"
                  :class="{ active: index === selectedIndex }"
                  :style="{
                    background:
                      index === selectedIndex ? specSelectedBgColor : '#f5f5f5',
                    borderColor:
                      index === selectedIndex
                        ? specSelectedColor
                        : 'transparent',
                  }"
                  @click="changeSpec(item, index)"
                >
                  <image :src="item.image" mode="aspectFill"></image>
                  <text
                    class="name line1"
                    :style="{ color: specSelectedTextColor }"
                    >{{ item.suk }}</text
                  >
                </view>
                <view class="spacer"></view>
              </scroll-view>
              <view
                class="total-count"
                :style="{ color: specTextColor }"
                @click="showSpecModal"
              >
                <text>共{{ skuList.length }}款</text>
                <text class="iconfont icon-jiantou"></text>
              </view>
            </view>
          </view>

          <!-- 信息排序列表 -->
          <view
            class="info-item"
            v-for="(item, index) in sortList"
            :key="index"
          >
            <!-- 价格区域 -->
            <view
              v-if="item.name === 'price' && item.show"
              class="price-section"
            >
              <view class="price-row">
                <view
                  v-if="item.checkList.includes(0)"
                  class="main-price-wrap"
                  :style="{ color: finalPriceColor }"
                >
                  <view class="label">到手价</view>
                  <view class="symbol">¥</view>
                  <view
                    class="price"
                    :style="{
                      fontSize: priceFontSize + 'px',
                      lineHeight: priceFontSize + 'px',
                    }"
                    >{{ displayInfo.real_price }}</view
                  >
                </view>
                <view
                  v-if="item.checkList.includes(1)"
                  class="ot-price-wrap"
                  :style="{ color: sellingPriceColor }"
                >
                  <text class="label">售价</text>
                  <text class="price">¥{{ displayInfo.price }}</text>
                </view>
                <view
                  v-if="item.checkList.includes(2) && displayInfo.vip_price"
                  class="vip-price-wrap"
                >
                  <text class="badge">SVIP</text>
                  <text class="price">¥{{ displayInfo.vip_price }}</text>
                </view>
              </view>
            </view>

            <!-- 名称区域 -->
            <view v-if="item.name === 'name' && item.show" class="name-section">
              <view
                class="title"
                :style="{
                  color: titleColor,
                  fontSize: titleFontSize + 'px',
                  fontWeight: 'bold',
                }"
                >{{ productData.store_name }}</view
              >
              <!-- <view class="share" @click="emitShare">
                <text class="iconfont icon-fenxiang"></text>
              </view> -->
            </view>

            <!-- 数据区域 -->
            <view v-if="item.name === 'data' && item.show" class="data-section">
              <!-- <text
                v-if="item.checkList.includes(0)"
                :style="{ color: originalPriceColor }"
                >划线价: ¥{{ displayInfo.ot_price }}</text
              > -->
              <text
                v-if="item.checkList.includes(1)"
                :style="{ color: stockColor }"
                >库存: {{ displayInfo.stock }}</text
              >
              <text
                v-if="item.checkList.includes(2)"
                :style="{ color: salesColor }"
                >销量: {{ displayInfo.fsales }}{{ displayInfo.unit_name }}</text
              >
            </view>

            <!-- 标签区域 -->
            <view
              v-if="
                item.name === 'tags' &&
                item.show &&
                productData.label_list &&
                productData.label_list.length
              "
              class="tags-section"
            >
              <BaseTag
                :text="tag.name"
                :color="tag.font_color"
                :background="tag.bg_color"
                :borderColor="tag.border_color"
                :circle="tag.border_color ? true : false"
                :imgSrc="tag.image"
                size="middle"
                v-for="(tag, tIndex) in productData.label_list"
                :key="tIndex"
              ></BaseTag>
            </view>
          </view>

          <!-- 规格样式 2: 底部 -->
          <view
            v-if="specStyle === 2 && skuList.length > 0"
            class="spec-bottom-section"
          >
            <view class="spec-list-wrapper">
              <scroll-view scroll-x="true" class="spec-list">
                <view
                  class="spec-item"
                  v-for="(item, index) in skuList"
                  :key="index"
                  @click="changeSpec(item, index)"
                  :style="{
                    borderColor:
                      index === selectedIndex
                        ? specSelectedBorderColor
                        : 'transparent',
                  }"
                >
                  <image :src="item.image" mode="aspectFill"></image>
                  <view
                    class="name line1"
                    :style="{
                      color:
                        index === selectedIndex
                          ? specSelectedTextColor
                          : specUnselectedTextColor,
                      background:
                        index === selectedIndex
                          ? specSelectedBgColor
                          : '#f5f5f5',
                    }"
                  >
                    {{ item.suk }}
                  </view>
                </view>
                <view class="spacer"></view>
              </scroll-view>
              <view class="total-count" @click="showSpecModal">
                <view>
                  <view>{{ skuList.length }}款</view>
                  <view>可选</view>
                </view>
                <text class="iconfont icon-jiantou"></text>
              </view>
            </view>
          </view>
          <view class="presell_count" v-if="productData.presale">
            <view>
              <view>{{ '预售活动时间' }}：</view>
              <view
                v-if="
                  productData.presale_start_time && productData.presale_end_time
                "
                class="presell_time"
              >
                <view class="iconfont icon-shijian1"></view>
                {{ productData.presale_start_time }}
                <span class="area_line">~</span>
                {{ productData.presale_end_time }}
              </view>
            </view>
            <view
              >{{ '预售结束后' }} {{ productData.presale_day }}
              {{ '天内发货' }}</view
            >
          </view>
        </view>
      </view>
    </common-wrapper>
  </view>
</template>

<script>
import commonWrapper from "./commonWrapper.vue";
import BaseTag from "@/components/BaseTag.vue";

export default {
  name: "productInfo",
  components: {
    BaseTag,
    commonWrapper,
  },
  props: {
    dataConfig: {
      type: Object,
      default: () => ({}),
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
    attrValue: {
      type: String,
      default: "",
    },
    colorStyle: {
      type: Object,
      default: () => ({ theme: "#E93323" }),
    },
  },
  data() {
    return {
      currentSwiper: 0,
      selectedIndex: 0,
      hasUserSelected: false,
      videoControls: true,
      videoPlaying: false,
    };
  },
  computed: {
    displayInfo() {
      let price = this.priceData.price || "0.00";
      let real_price = this.priceData.real_price || "0.00";
      let ot_price = this.priceData.ot_price || "0.00";
      let vip_price = this.priceData.vip_price;
      let stock = this.productData.stock || 0;
      let fsales = this.productData.fsales || 0;
      let unit_name = this.productData.unit_name || "";

      if (this.skuList.length > 0 && this.selectedIndex < this.skuList.length) {
        let sku = this.skuList[this.selectedIndex];
        if (sku) {
          if (sku.price) price = sku.price;
          if (sku.real_price) real_price = sku.real_price;
          if (sku.ot_price) ot_price = sku.ot_price;
          if (sku.vip_price) vip_price = sku.vip_price;
          if (sku.stock || sku.stock === 0) stock = sku.stock;
        }
      }
      return {
        price,
        ot_price,
        vip_price,
        stock,
        fsales,
        unit_name,
        real_price,
      };
    },
    sliderImage() {
      const images = Array.isArray(this.productData.slider_image)
        ? this.productData.slider_image
        : [];
      const videoLink = this.productData.video_link;
      if (videoLink) {
        // 有视频时，第一项为视频对象，后面是图片（跳过第一张图片作为封面）
        return [
          {
            isVideo: true,
            src: videoLink,
            poster: images[0] || "",
          },
          ...images.slice(1),
        ];
      }
      return images;
    },
    currentSlideImage() {
      const currentItem = this.sliderImage[this.currentSwiper];
      if (currentItem) {
        // 如果是视频，返回 poster；否则返回图片
        return currentItem.isVideo ? currentItem.poster : currentItem;
      }
      return "";
    },
    configData() {
      return {
        ...this.dataConfig,
        paddingConfig: this.dataConfig.paddingConfig || {
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
        marginConfig: this.dataConfig.marginConfig || {
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
      };
    },
    specStyle() {
      return this.dataConfig.specStyle ? this.dataConfig.specStyle.tabVal : 0;
    },
    specSettings() {
      return this.dataConfig.specSettings || {};
    },
    isCustomSpecTone() {
      return (
        this.specSettings.colorTone && this.specSettings.colorTone.tabVal === 1
      );
    },
    specSelectedColor() {
      if (this.isCustomSpecTone) {
        return this.specSettings.selectedBorderColor &&
          this.specSettings.selectedBorderColor.color[0].item
          ? this.specSettings.selectedBorderColor.color[0].item
          : "#E93323";
      }
      return "var(--view-theme)";
    },
    specTextColor() {
      if (this.isCustomSpecTone) {
        return this.specSettings.textColor &&
          this.specSettings.textColor.color[0].item
          ? this.specSettings.textColor.color[0].item
          : "#666";
      }
      return "var(--view-theme)";
    },
    specSelectedBorderColor() {
      if (this.isCustomSpecTone) {
        return this.specSettings.selectedBorderColor &&
          this.specSettings.selectedBorderColor.color[0].item
          ? this.specSettings.selectedBorderColor.color[0].item
          : this.specSelectedColor;
      }
      return this.specSelectedColor;
    },
    specSelectedTextColor() {
      if (this.isCustomSpecTone) {
        return this.specSettings.selectedTextColor &&
          this.specSettings.selectedTextColor.color[0].item
          ? this.specSettings.selectedTextColor.color[0].item
          : this.specTextColor;
      }
      return this.specTextColor;
    },
    specSelectedBgColor() {
      if (this.isCustomSpecTone) {
        return this.specSettings.selectedBgColor &&
          this.specSettings.selectedBgColor.color[0].item
          ? this.specSettings.selectedBgColor.color[0].item
          : this.hexToRgba(this.colorStyle.theme || "#E93323", 0.1);
      }
      return this.specStyle === 3
        ? "#777777"
        : this.hexToRgba(this.colorStyle.theme || "#E93323", 0.1);
    },
    specUnselectedTextColor() {
      if (this.isCustomSpecTone) {
        return this.specSettings.unselectedTextColor &&
          this.specSettings.unselectedTextColor.color[0].item
          ? this.specSettings.unselectedTextColor.color[0].item
          : this.specStyle === 3
          ? "#ffffff"
          : "#333333";
      }
      return this.specStyle === 3 ? "#ffffff" : "#333333";
    },
    titleColor() {
      const config = this.dataConfig.titleConfig;
      if (!config) return "#333333";
      if (config.tabVal === 0) {
        return "var(--view-theme)";
      }
      return config.color && config.color.color[0].item
        ? config.color.color[0].item
        : "#333333";
    },
    titleFontSize() {
      return this.dataConfig.titleConfig && this.dataConfig.titleConfig.fontSize
        ? this.dataConfig.titleConfig.fontSize.val
        : 16;
    },
    sortList() {
      return this.dataConfig.sortList
        ? this.dataConfig.sortList.list
        : [
            { name: "price", show: true, checkList: [0, 1, 2] },
            { name: "name", show: true },
            { name: "data", show: true, checkList: [0, 1, 2] },
            { name: "tags", show: true },
          ];
    },
    indicatorConfig() {
      return this.dataConfig.indicatorConfig || {};
    },
    selectColor() {
      return this.indicatorConfig.selectColor
        ? this.indicatorConfig.selectColor.color[0].item
        : "#E93323";
    },
    defaultColor() {
      return this.indicatorConfig.defaultColor
        ? this.indicatorConfig.defaultColor.color[0].item
        : "#CCCCCC";
    },
    indicatorPosition() {
      if (this.indicatorConfig.tabVal === 0) return "center";
      const pos = this.indicatorConfig.positionVal;
      return pos === 0 ? "left" : pos === 2 ? "right" : "center";
    },
    priceSettings() {
      return this.dataConfig.priceSettings || {};
    },
    dataSettings() {
      return this.dataConfig.dataSettings || {};
    },
    isCustomPriceTone() {
      return (
        this.priceSettings.colorTone &&
        this.priceSettings.colorTone.tabVal === 1
      );
    },
    finalPriceColor() {
      if (this.isCustomPriceTone) {
        return this.priceSettings.finalPriceColor &&
          this.priceSettings.finalPriceColor.color[0].item
          ? this.priceSettings.finalPriceColor.color[0].item
          : "#E93323";
      }
      return "var(--view-theme)" || "#E93323";
    },
    sellingPriceColor() {
      if (this.isCustomPriceTone) {
        return this.priceSettings.sellingPriceColor &&
          this.priceSettings.sellingPriceColor.color[0].item
          ? this.priceSettings.sellingPriceColor.color[0].item
          : "#333333";
      }
      return "#333333";
    },
    priceFontSize() {
      return this.priceSettings.priceFontSize
        ? this.priceSettings.priceFontSize.val
        : 24;
    },
    originalPriceColor() {
      return this.dataSettings.originalPriceColor &&
        this.dataSettings.originalPriceColor.color[0].item
        ? this.dataSettings.originalPriceColor.color[0].item
        : "#999999";
    },
    stockColor() {
      return this.dataSettings.stockColor &&
        this.dataSettings.stockColor.color[0].item
        ? this.dataSettings.stockColor.color[0].item
        : "#999999";
    },
    salesColor() {
      return this.dataSettings.salesColor &&
        this.dataSettings.salesColor.color[0].item
        ? this.dataSettings.salesColor.color[0].item
        : "#999999";
    },
  },
  methods: {
    swiperChange(e) {
      this.currentSwiper = e.detail.current;
      // 切换到非视频页时暂停视频
      if (this.currentSwiper !== 0 || !this.productData.video_link) {
        this.videoControls = true;
        this.videoPlaying = false;
        // 暂停视频
        const videoContext = uni.createVideoContext("productVideo", this);
        videoContext && videoContext.pause();
      }
    },
    playVideo() {
      this.videoControls = false;
      this.videoPlaying = true;
      this.$nextTick(() => {
        const videoContext = uni.createVideoContext("productVideo", this);
        videoContext.play();
      });
    },
    onVideoPlay() {
      this.videoPlaying = true;
    },
    onVideoPause() {
      this.videoPlaying = false;
    },
    changeSpec(item, index) {
      if (index !== undefined) {
        this.selectedIndex = index;
        this.hasUserSelected = true;
      }
      this.$emit("changeSpec", item);
    },
    syncSelectedIndex() {
      if (!this.skuList.length) return;
      const attrIndex = this.skuList.findIndex(
        (item) => item.suk === this.attrValue
      );
      if (attrIndex > -1) {
        this.selectedIndex = attrIndex;
        this.hasUserSelected = false;
        return;
      }
      if (
        this.hasUserSelected &&
        this.selectedIndex > -1 &&
        this.selectedIndex < this.skuList.length
      ) {
        return;
      }
      const stockIndex = this.skuList.findIndex((item) => Number(item.stock) > 0);
      this.selectedIndex = stockIndex > -1 ? stockIndex : 0;
    },
    showSpecModal() {
      this.$emit("showSpecModal");
    },
    emitShare() {
      this.$emit("share");
    },
    hexToRgba(hex, opacity) {
      if (!hex) return "";
      let c;
      if (/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)) {
        c = hex.substring(1).split("");
        if (c.length == 3) {
          c = [c[0], c[0], c[1], c[1], c[2], c[2]];
        }
        c = "0x" + c.join("");
        return (
          "rgba(" +
          [(c >> 16) & 255, (c >> 8) & 255, c & 255].join(",") +
          "," +
          opacity +
          ")"
        );
      }
      return hex;
    },
  },
  watch: {
    skuList: {
      handler() {
        this.syncSelectedIndex();
      },
      deep: true,
      immediate: true,
    },
    attrValue() {
      this.syncSelectedIndex();
    },
  },
};
</script>

<style scoped lang="scss">
.product-info-box {
  &.style-3 {
    .image-wrap .indicators {
      bottom: 170rpx;
    }
  }

  .image-wrap {
    position: relative;
    width: 100%;
    height: 782rpx;

    .swiper {
      width: 100%;
      height: 750rpx;

      .slide-image {
        width: 100%;
        height: 100%;
      }

      .video-item {
        position: relative;
        width: 100%;
        height: 100%;

        .video-wrap {
          width: 100%;
          height: 100%;
        }

        .poster {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          z-index: 9;

          .image {
            width: 100%;
            height: 100%;
          }
        }

        .play-btn {
          position: absolute;
          top: 50%;
          left: 50%;
          width: 136rpx;
          height: 136rpx;
          margin-top: -68rpx;
          margin-left: -68rpx;
          z-index: 10;

          .icon {
            width: 100%;
            height: 100%;
          }
        }
      }
    }
    .mirror-wrap {
      position: absolute;
      bottom: 0;
      left: 0;
      width: 100%;
      height: 32rpx;
      overflow: hidden;
      z-index: 1;

      .mirror-image {
        width: 100%;
        height: 750rpx;
        transform: scaleY(-1);
        filter: blur(2rpx);
      }
    }

    .indicators {
      position: absolute;
      bottom: 60rpx;
      width: 100%;
      display: flex;
      padding: 0 20rpx;
      box-sizing: border-box;
      z-index: 10;

      &.pos-left {
        justify-content: flex-start;
      }
      &.pos-center {
        justify-content: center;
      }
      &.pos-right {
        justify-content: flex-end;
      }

      .indicator-line {
        display: flex;
        align-items: center;
        width: 100%;
        .line-item {
          width: 20%;
          height: 4rpx;
          margin: 0 6rpx;
          border-radius: 4rpx;
          opacity: 0.5;
          &.active {
            opacity: 1;
          }
        }
      }

      .indicator-dot {
        display: flex;
        align-items: center;
        .dot-item {
          width: 12rpx;
          height: 12rpx;
          border-radius: 50%;
          margin: 0 6rpx;
          transition: all 0.3s;
          opacity: 0.5;
          &.active {
            width: 24rpx;
            border-radius: 6rpx;
            opacity: 1;
          }
        }
      }

      .indicator-number {
        .num-box {
          background: rgba(0, 0, 0, 0.3);
          color: #fff;
          font-size: 24rpx;
          padding: 4rpx 16rpx;
          border-radius: 20rpx;
          .current {
            font-size: 28rpx;
          }
        }
      }
    }

    .spec-style-4 {
      position: absolute;
      bottom: 10rpx;
      width: 100%;
      background: rgba(153, 153, 153, 0.6);
      padding: 20rpx 0 30rpx 20rpx;
      box-sizing: border-box;
      z-index: 10;
      display: flex;
      justify-content: space-between;
      .spec-list {
        white-space: nowrap;
        width: 100%;
        display: flex;
        align-items: center;
        padding-right: 160rpx;
        .spec-item {
          display: inline-flex;
          align-items: center;
          margin-right: 20rpx;
          border: 2rpx solid transparent;
          border-radius: 16rpx;
          padding: 4rpx;
          background: #777777;

          image {
            width: 80rpx;
            height: 80rpx;
            border-radius: 8rpx;
            margin-right: 16rpx;
          }

          .spec-info {
            padding-right: 10rpx;
          }

          .name {
            font-size: 24rpx;
            color: #fff;
            max-width: 120rpx;
          }
        }

      }
      .total-count {
        display: inline-flex;
        justify-content: center;
        align-items: center;
        font-size: 20rpx;
        margin-left: 10rpx;
        vertical-align: top;
        height: 88rpx;
        color: #fff;
        background: #777777;
        position: absolute;
        right: 0;
        z-index: 10;
        padding: 0 20rpx;
        border-radius: 8rpx 0 0 8rpx;

        .iconfont {
          font-size: 20rpx;
        }
      }
    }
  }

  .info-box {
    position: relative;
    padding: 32rpx 32rpx 8rpx 32rpx;
    border-radius: 32rpx 32rpx 0 0;
    background: #fff;
    margin-top: -32rpx;
    z-index: 11;

    .spec-top-section {
      margin-bottom: 30rpx;
      position: relative;
      .spec-list {
        white-space: nowrap;
        width: 100%;

        .spec-item {
          display: inline-block;
          margin-right: 16rpx;

          image {
            width: 80rpx;
            height: 80rpx;
            border-radius: 8rpx;
            border: 2rpx solid #eee;
          }
        }
      }
      .total-count {
        position: absolute;
        right: 0;
        top: 0;
        display: inline-flex;
        justify-content: center;
        align-items: center;
        font-size: 20rpx;
        vertical-align: top;
        height: 80rpx;
        margin-left: 10rpx;
        position: absolute;
        right: 0;
        background-color: #fff;
        z-index: 10;
        padding-left: 10rpx;

        .iconfont {
          font-size: 20rpx;
        }
      }

      .spec-list-text-wrapper {
        position: relative;

        .spec-list-text {
          white-space: nowrap;
          width: 100%;

          .spec-item {
            display: inline-flex;
            align-items: center;
            padding: 8rpx 16rpx;
            border-radius: 12rpx;
            margin-right: 16rpx;
            background: #f5f5f5;
            border: 2rpx solid transparent;
            image {
              width: 40rpx;
              height: 40rpx;
              border-radius: 50%;
              margin-right: 8rpx;
            }

            .name {
              font-size: 24rpx;
              color: #333;
            }
          }

          .spacer {
            display: inline-block;
            width: 100rpx;
            height: 1rpx;
          }
        }

        .total-count {
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24rpx;
          position: absolute;
          right: 0;
          top: 0;
          bottom: 0;
          min-width: 100rpx;
          height: 60rpx;
          background-color: #fff;
          z-index: 10;
          padding-left: 10rpx;
          box-shadow: -10rpx 0 10rpx -5rpx rgba(0, 0, 0, 0.05);

          .iconfont {
            font-size: 20rpx;
          }
        }
      }
    }

    .info-item {
      margin-bottom: 16rpx;

      .price-section {
        .price-row {
          display: flex;
          align-items: center;
          flex-wrap: wrap;

          .main-price-wrap {
            display: flex;
            align-items: baseline;
            margin-right: 20rpx;
            font-weight: bold;

            .label {
              font-size: 24rpx;
              margin-right: 4rpx;
            }
            .symbol {
              font-size: 28rpx;
              line-height: 28rpx;
            }
          }

          .ot-price-wrap {
            display: flex;
            align-items: center;
            text-decoration: line-through;
            font-size: 24rpx;
            color: #999;
            margin-right: 20rpx;
          }

          .vip-price-wrap {
            display: flex;
            align-items: center;
            height: 32rpx;

            .badge {
              background: #333;
              color: #f8dcae;
              font-size: 20rpx;
              padding: 0 8rpx;
              border-radius: 18rpx 0 0 18rpx;
              height: 100%;
              display: flex;
              align-items: center;
              font-weight: bold;
            }

            .price {
              background: #fff0d8;
              color: #333;
              font-size: 22rpx;
              padding: 0 10rpx 0 4rpx;
              border-radius: 0 18rpx 18rpx 0;
              height: 100%;
              display: flex;
              align-items: center;
              font-weight: bold;
            }
          }
        }
      }

      .name-section {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-top: 20rpx;

        .title {
          flex: 1;
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
          line-height: 1.4;
        }

        .share {
          display: flex;
          flex-direction: column;
          align-items: center;
          margin-left: 20rpx;
          color: #666;
          font-size: 20rpx;

          .iconfont {
            font-size: 36rpx;
            margin-bottom: 4rpx;
          }
        }
      }

      .data-section {
        display: flex;
        justify-content: space-between;
        font-size: 24rpx;
        color: #999;
        margin-top: 20rpx;
      }

      .tags-section {
        display: flex;
        flex-wrap: wrap;
        margin-top: 20rpx;
      }
    }

    .spec-bottom-section {
      margin-top: 30rpx;

      .spec-list-wrapper {
        position: relative;
      }

      .spec-list {
        white-space: nowrap;
        width: 100%;

        .spec-item {
          display: inline-block;
          margin-right: 16rpx;
          text-align: center;
          border: 2rpx solid transparent;
          border-radius: 8rpx;
          overflow: hidden;
          image {
            width: 116rpx;
            height: 116rpx;
            border-radius: 8rpx;
          }

          .name {
            font-size: 24rpx;
            color: #333;
            background: #f5f5f5;
            padding: 6rpx 14rpx;
            font-size: 22rpx;
            max-width: 116rpx;
          }
        }

        .spacer {
          display: inline-block;
          width: 80rpx;
          height: 1rpx;
        }
      }

      .total-count {
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 20rpx;
        position: absolute;
        right: 0;
        top: 0;
        bottom: 0;
        width: 80rpx;
        background-color: #fff;
        z-index: 10;
        padding-left: 10rpx;

        .iconfont {
          font-size: 20rpx;
        }
      }
    }
  }
}

.line1 {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.presell_count {
  font-size: 24rpx;
  color: #999999;
  padding: 10rpx 0 20rpx 0;

  .presell_time {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    margin: 4rpx 0;

    .area_line {
      display: inline-block;
      margin: 0 6rpx;
    }
  }

  .icon-shijian1 {
    margin-right: 4rpx;
    flex-shrink: 0;
  }
}
</style>
