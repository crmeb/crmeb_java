<template>
  <view class="home-paid-vip" v-if="isShowPaidVip">
    <commonWrapper :config="dataConfig">
      <view class="vip-container" :style="[containerStyle]">
        <view class="left-content">
          <image
            :src="imgUrl"
            class="vip-icon"
            v-if="imgUrl"
            mode="heightFix"
          ></image>
          <view class="text-content" :style="{ color: tipsTextColor }">
            {{ '开通SVIP会员预计省' }}
            <text :style="{ color: moneyTextColor }">{{ diff }}</text>
            {{ '元' }}
          </view>
        </view>
        <view class="right-content">
          <navigator
            url="/pages/annex/vip_paid/index"
            hover-class="none"
            class="open-btn"
            :style="[btnStyle]"
          >
            {{ rightBntText }}
            <text class="iconfont icon-jiantou"></text>
          </navigator>
        </view>
      </view>
    </commonWrapper>
  </view>
</template>

<script>
import commonWrapper from "./commonWrapper.vue";
import { mapGetters } from "vuex";

export default {
  name: "homePaidVip",
  components: {
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
     isShowPaidVip: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    ...mapGetters(["isLogin", "userInfo"]),
    diff() {
      let price =
        Number(this.priceData.price) || Number(this.productData.price) || 0;
      let vip_price =
        Number(this.priceData.vip_price) ||
        Number(this.productData.vip_price) ||
        0;
      let val = (price - vip_price).toFixed(2);
      return val > 0 ? val : "0.00";
    },
    containerStyle() {
      return {};
    },
    imgUrl() {
      return this.dataConfig.imgConfig ? this.dataConfig.imgConfig.url : "";
    },
    rightBntText() {
      return this.dataConfig.rightBntConfig
        ? this.dataConfig.rightBntConfig.value
        : '立即开通';
    },
    isCustomTone() {
      return (
        this.dataConfig.toneConfig && this.dataConfig.toneConfig.tabVal === 1
      );
    },
    tipsTextColor() {
      return this.isCustomTone && this.dataConfig.tipsColor
        ? this.dataConfig.tipsColor.color[0].item
        : "#333333";
    },
    moneyTextColor() {
      return this.isCustomTone && this.dataConfig.moneyColor
        ? this.dataConfig.moneyColor.color[0].item
        : "#F62C2C";
    },
    btnStyle() {
      return {
        color:
          this.isCustomTone && this.dataConfig.btnColor
            ? this.dataConfig.btnColor.color[0].item
            : "#FF6B00",
        fontSize:
          (this.dataConfig.btnConfig ? this.dataConfig.btnConfig.val : 13) * 2 +
          "rpx",
      };
    },
  },
};
</script>

<style lang="scss" scoped>
.home-paid-vip {
  .vip-container {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .left-content {
      display: flex;
      align-items: center;

      .vip-icon {
        width: 36rpx; // Default size from admin component info
        height: 36rpx;
        margin-right: 16rpx;
      }

      .text-content {
        font-size: 26rpx;
        font-weight: 400;
      }
    }

    .right-content {
      .open-btn {
        display: flex;
        align-items: center;
        font-weight: 500;

        .iconfont {
          font-size: 24rpx;
          margin-left: 4rpx;
        }
      }
    }
  }
}
</style>
