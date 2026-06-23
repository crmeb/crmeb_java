<template>
  <view v-show="!isSortType">
    <common-wrapper :config="configData">
      <view class="video-box" id="myVideo" :style="[videoStyle]">
        <video
          class="video"
          object-fit="cover"
          :src="dataConfig.videoConfig.url"
          :poster="dataConfig.imgConfig.url"
          controls
        ></video>
      </view>
    </common-wrapper>
  </view>
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
    return {
      height: 0,
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
    // 视频高度
    videoStyle() {
      let borderRadius = `${this.dataConfig.fillet.val * 2}rpx`;
      if (this.dataConfig.fillet.type) {
        borderRadius = `${this.dataConfig.fillet.valList[0].val * 2}rpx ${
          this.dataConfig.fillet.valList[1].val * 2
        }rpx ${this.dataConfig.fillet.valList[3].val * 2}rpx ${
          this.dataConfig.fillet.valList[2].val * 2
        }rpx`;
      }
      return {
        height: this.height + "px",
        "border-radius": borderRadius,
      };
    },
  },
  mounted() {
    const query = uni.createSelectorQuery().in(this);
    query
      .select("#myVideo")
      .boundingClientRect((data) => {
        switch (this.dataConfig.scaleConfig.tabVal) {
          case 0:
            this.height = (data.width * 9) / 16;
            break;
          case 1:
            this.height = (data.width * 3) / 4;
            break;
          default:
            this.height = data.width;
            break;
        }
      })
      .exec();
  },
};
</script>

<style lang="scss" scoped>
.video-box {
  overflow: hidden;
}

.video {
  width: 100%;
  height: 100%;
}
</style>
