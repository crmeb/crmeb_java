<template>
  <!-- 辅助空白 -->
  <common-wrapper v-show="!isSortType" :config="configData">
    <view class="blankPage">
      <view class="bankCon" :style="[blankStyle]"></view>
    </view>
  </common-wrapper>
</template>

<script>
import commonWrapper from "./commonWrapper.vue";
export default {
  components: { commonWrapper },
  name: "blankPage",
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
      bgColor: "",
      heightConfig: 0,
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
              val: this.dataConfig.lrEdge ? this.dataConfig.lrEdge.val : 0,
            },
            {
              val: this.dataConfig.bottomConfig
                ? this.dataConfig.bottomConfig.val
                : 0,
            },
            {
              val: this.dataConfig.lrEdge ? this.dataConfig.lrEdge.val : 0,
            },
          ],
        },
        marginConfig: this.dataConfig.marginConfig || {
          isAll: false,
          valList: [
            {
              val: 0,
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
        componentBgConfig:
          this.dataConfig.componentBgConfig || this.dataConfig.bottomBgColor,
      };
    },
    blankStyle() {
      let borderRadius = `${this.dataConfig.fillet.val * 2}rpx`;
      if (this.dataConfig.fillet.type) {
        borderRadius = `${this.dataConfig.fillet.valList[0].val * 2}rpx ${
          this.dataConfig.fillet.valList[1].val * 2
        }rpx ${this.dataConfig.fillet.valList[3].val * 2}rpx ${
          this.dataConfig.fillet.valList[2].val * 2
        }rpx`;
      }
      return {
        height: `${this.dataConfig.heightConfig.val * 2}rpx`,
        "border-radius": borderRadius,
        background: this.dataConfig.bgColor.color[0].item,
      };
    },
  },
  created() {},
  methods: {},
};
</script>

<style lang="scss">
.blankPage {
  .bankCon {
    width: 100%;
    height: 100%;
    background-color: blue;
  }
}
</style>
