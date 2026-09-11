<template>
  <!-- 富文本 -->
  <view v-show="!isSortType">
    <common-wrapper :config="configData">
      <view class="richText" v-if="description" :style="[richTextStyle]">
        <!-- #ifndef APP-PLUS -->
        <mp-html
          :content="description"
          ref="article"
          :tag-style="tagStyle"
        ></mp-html>
        <!-- #endif -->
        <!-- #ifdef APP-PLUS -->
        <!-- eslint-disable-next-line vue/no-v-text-v-html-on-component -->
        <view v-html="description"></view>
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
  dataConfig: {
    type: Object,
    default: () => ({}),
  },
  isSortType: {
    type: [String, Number],
    default: 0,
  },
});

const tagStyle = ref({
  img: "width:100%;display:block;",
  table: "width:100%",
  video: "width:100%",
});
const bgColor = ref("");
const lrConfig = ref(0);
const udConfig = ref(0);

const configData = computed(() => {
  return {
    ...props.dataConfig,
    paddingConfig: props.dataConfig.paddingConfig || {
      isAll: false,
      valList: [
        {
          val: props.dataConfig.topConfig ? props.dataConfig.topConfig.val : 0,
        },
        {
          val: props.dataConfig.prConfig ? props.dataConfig.prConfig.val : 0,
        },
        {
          val: props.dataConfig.bottomConfig
            ? props.dataConfig.bottomConfig.val
            : 0,
        },
        {
          val: props.dataConfig.prConfig ? props.dataConfig.prConfig.val : 0,
        },
      ],
    },
    marginConfig: props.dataConfig.marginConfig || {
      isAll: false,
      valList: [
        {
          val: props.dataConfig.mbConfig ? props.dataConfig.mbConfig.val : 0,
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
});

const description = computed(() => {
  let desc = props.dataConfig.richText.val;
  if (desc) {
    desc = desc.replace(
      /<img/gi,
      '<img style="max-width:100%;height:auto;float:left;display:block" '
    );
    desc = desc.replace(
      /<video/gi,
      '<video style="width:100%;height:auto;display:block" '
    );
  }
  return desc;
});

const richTextStyle = computed(() => {
  let borderRadius = `${props.dataConfig.fillet.val * 2}rpx`;
  if (props.dataConfig.fillet.type) {
    borderRadius = `${props.dataConfig.fillet.valList[0].val * 2}rpx ${
      props.dataConfig.fillet.valList[1].val * 2
    }rpx ${props.dataConfig.fillet.valList[3].val * 2}rpx ${
      props.dataConfig.fillet.valList[2].val * 2
    }rpx`;
  }
  return {
    "border-radius": borderRadius,
    background: props.dataConfig.bgColor.color[0].item,
  };
});
</script>

<style lang="scss">
.richText {
  padding: 30rpx;
  background-color: #fff;

  // margin: 0 20rpx;
  // border-radius: 24rpx;
  &::after {
    content: "";
    display: table;
    clear: both;
  }
}

::v-deep uni-video {
  width: 100% !important;
}

::v-deep video {
  width: 100% !important;
}
</style>
