<template>
  <!-- 辅助线 -->
  <view v-show="!isSortType">
    <common-wrapper :config="configData">
      <view class="lines">
        <view class="item" :style="[lineStyle]"></view>
      </view>
    </common-wrapper>
  </view>
</template>

<script>
import commonWrapper from "./commonWrapper.vue";
export default {
  components: { commonWrapper },
  name: "guide",
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
      heightConfig: 0,
      lineColor: "",
      // lineStyle: 0,
      lrEdge: 0,
      mbConfig: 0,
    };
  },
  computed: {
    lineStyle() {
      return {
        borderBottomWidth: `${this.dataConfig.heightConfig.val * 2}rpx`,
        borderBottomStyle:
          this.dataConfig.lineStyle.tabList[this.dataConfig.lineStyle.tabVal]
            .style,
        borderBottomColor: this.dataConfig.lineColor.color[0].item,
      };
    },
    configData() {
      return {
        ...this.dataConfig,
      };
    },
  },
  created() {},
  methods: {},
};
</script>

<style lang="scss">
.lines {
  .item {
    box-sizing: border-box;
    border-bottom-color: red;
    border-width: 0 0 1rpx;
    border-bottom-style: solid;
  }
}
</style>
