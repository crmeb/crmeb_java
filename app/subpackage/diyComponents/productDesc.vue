<template>
  <view class="product-intro" v-if="productData.description">
    <common-wrapper :config="dataConfig">
      <view class="title" v-if="titleShow" :style="[titleStyle]">{{
        '产品介绍'
      }}</view>
      <view class="conter">
        <!-- #ifndef APP-PLUS -->
        <parser
          :html="productData.description"
          ref="article"
          :tag-style="tagStyle"
        ></parser>
        <!-- #endif -->
        <!-- #ifdef APP-PLUS -->
        <view class="description" v-html="productData.description"></view>
        <!-- #endif -->
      </view>
    </common-wrapper>
  </view>
</template>

<script>
import commonWrapper from "./commonWrapper.vue";
import parser from "@/components/jyf-parser/jyf-parser";

export default {
  name: "productDesc",
  components: {
    parser,
    commonWrapper
  },
  props: {
    productData: {
      type: Object,
      default: () => ({}),
    },
    dataConfig: {
      type: Object,
      default: () => ({}),
    },
  },
  computed: {
    titleShow() {
      return this.dataConfig.isShow?.tabVal == 0;
    },
    titleStyle() {
      return {
        color: this.dataConfig.textColor?.color?.[0]?.item || "#333",
        fontSize: (this.dataConfig.fontSize?.val || 16) * 2 + "rpx",
        textAlign: this.dataConfig.textPosition?.val || "left",
      };
    },
  },
  data() {
    return {
      tagStyle: {
        img: "width:100%;display:block;",
        table: "width:100%",
        video: "width:100%",
      },
    };
  },
};
</script>

<style lang="scss" scoped>
.product-intro {
  .title {
    height: 90rpx;
    line-height: 90rpx;
    font-size: 30rpx;
    color: #282828;
    text-align: center;
  }

  .conter {
    font-size: 30rpx;
    color: #333;
    line-height: 50rpx;
  }
}
</style>
