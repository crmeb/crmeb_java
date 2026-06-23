<template>
  <common-wrapper :config="configData">
    <view class="hotspot">
      <image
        :src="dataConfig.picStyle.url"
        mode="widthFix"
        class="image"
        :style="[imageRadius]"
      ></image>
      <view
        v-for="item in dataConfig.picStyle.list"
        :key="item.number"
        :style="{
          top: `${item.starY}rpx`,
          left: `${item.starX}rpx`,
          width: `${item.areaWidth}rpx`,
          height: `${item.areaHeight}rpx`,
        }"
        class="area"
        @click="goPage(item.link)"
      ></view>
    </view>
  </common-wrapper>
</template>

<script>
import commonWrapper from "./commonWrapper.vue";
export default {
  components: { commonWrapper },
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
    return {};
  },
  computed: {
    imageRadius() {
      let borderRadius = `${this.dataConfig.fillet.val * 2}rpx`;
      if (this.dataConfig.fillet.type) {
        borderRadius = `${this.dataConfig.fillet.valList[0].val * 2}rpx ${
          this.dataConfig.fillet.valList[1].val * 2
        }rpx ${this.dataConfig.fillet.valList[3].val * 2}rpx ${
          this.dataConfig.fillet.valList[2].val * 2
        }rpx`;
      }
      return {
        "border-radius": borderRadius,
      };
    },
    configData() {
      return {
        ...this.dataConfig,
      };
    },
  },
  methods: {
    goPage(link) {
      this.$util.JumpPath(link);
    },
  },
};
</script>

<style lang="scss" scoped>
.hotspot {
  position: relative;

  .image {
    display: block;
    width: 100%;
  }

  .area {
    position: absolute;
  }
}
</style>
