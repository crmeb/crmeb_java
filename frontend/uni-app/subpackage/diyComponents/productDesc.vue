<template>
  <view class="product-intro" v-if="productData.description">
    <common-wrapper :config="dataConfig">
      <view class="title" v-if="titleShow" :style="[titleStyle]">{{
        '产品介绍'
      }}</view>
      <view class="conter">
        <!-- #ifndef APP-PLUS -->
        <mp-html
          :content="productData.description"
          ref="article"
          :tag-style="tagStyle"
        />
        <!-- #endif -->
        <!-- #ifdef APP-PLUS -->
        <view class="description" v-html="productData.description"></view>
        <!-- #endif -->
      </view>
    </common-wrapper>
  </view>
</template>

<script setup>
import { ref, computed } from "vue";
import commonWrapper from "./commonWrapper.vue";
import mpHtml from "@/uni_modules/mp-html/components/mp-html/mp-html.vue";

const props = defineProps({
  productData: {
    type: Object,
    default: () => ({}),
  },
  dataConfig: {
    type: Object,
    default: () => ({}),
  },
});

const tagStyle = ref({
  img: "width:100%;display:block;",
  table: "width:100%",
  video: "width:100%",
});

const titleShow = computed(() => {
  return props.dataConfig.isShow?.tabVal == 0;
});

const titleStyle = computed(() => {
  return {
    color: props.dataConfig.textColor?.color?.[0]?.item || "#333",
    fontSize: (props.dataConfig.fontSize?.val || 16) * 2 + "rpx",
    textAlign: props.dataConfig.textPosition?.val || "left",
  };
});
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
