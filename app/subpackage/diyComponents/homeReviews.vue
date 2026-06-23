<template>
  <view class="home-reviews" :style="[bgRadius]" v-if="replyList.length > 0">
    <common-wrapper :config="configData">
      <view class="reviews-box">
        <!-- Header -->
        <view class="header acea-row row-between-wrapper">
          <view class="left acea-row row-middle">
            <text class="title" :style="{ color: titleColor }">{{
              '评价'
            }}</text>
            <text
              class="count"
              :style="{ color: countColor }"
              v-if="checkList.includes(0)"
              >({{ totalCount }})</text
            >
          </view>
          <navigator
            class="right acea-row row-middle"
            v-if="checkList.includes(1)"
            :url="
              productId
                ? '/pages/goods/goods_comment_list/index?product_id=' +
                  productId
                : ''
            "
            hover-class="none"
          >
            <text class="rate" :style="{ color: rateColor }"
              >{{ replyChance }}% </text
            >{{ '好评率' }}
            <text class="iconfont icon-jiantou"></text>
          </navigator>
        </view>

        <!-- List -->
        <view class="list" v-if="!isSlide">
          <view class="item" v-for="(item, index) in showList" :key="index">
            <view class="user-info acea-row row-middle">
              <image
                :src="item.avatar"
                class="avatar"
                mode="aspectFill"
              ></image>
              <view class="info">
                <view class="name line1">{{ item.nickname }}</view>
                <view class="stars acea-row">
                  <text
                    class="iconfont icon-pingjia"
                    v-for="i in 5"
                    :key="i"
                    :style="{ color: i <= item.star ? starColor : '#eee' }"
                  ></text>
                </view>
              </view>
            </view>
            <view class="content line2">{{ item.comment }}</view>
            <view class="images acea-row" v-if="item.pics && item.pics.length">
              <view
                class="img-box"
                v-for="(img, imgIndex) in item.pics.slice(0, 3)"
                :key="imgIndex"
              >
                <image :src="img" class="img" mode="aspectFill"></image>
                <view class="more" v-if="imgIndex === 2 && item.pics.length > 3"
                  >+{{ item.pics.length - 3 }}</view
                >
              </view>
            </view>
          </view>
        </view>

        <!-- Slide List -->
        <scroll-view scroll-x="true" class="slide-wrapper" v-else>
          <view
            class="slide-item"
            v-for="(item, index) in showList"
            :key="index"
          >
            <view class="user-info acea-row row-middle">
              <image
                :src="item.avatar"
                class="avatar"
                mode="aspectFill"
              ></image>
              <view class="info">
                <view class="name line1">{{ item.nickname }}</view>
                <view class="stars acea-row">
                  <text
                    class="iconfont icon-pingjia"
                    v-for="i in 5"
                    :key="i"
                    :style="{ color: i <= item.star ? starColor : '#eee' }"
                  ></text>
                </view>
              </view>
            </view>
            <view class="content line2">{{ item.comment }}</view>
            <view class="images acea-row" v-if="item.pics && item.pics.length">
              <view
                class="img-box"
                v-for="(img, imgIndex) in item.pics.slice(0, 3)"
                :key="imgIndex"
              >
                <image :src="img" class="img" mode="aspectFill"></image>
                <view class="more" v-if="imgIndex === 2 && item.pics.length > 3"
                  >+{{ item.pics.length - 3 }}</view
                >
              </view>
            </view>
          </view>
        </scroll-view>
      </view>
    </common-wrapper>
  </view>
</template>

<script>
import commonWrapper from "./commonWrapper.vue";
import { getReplyList } from "@/api/store.js";

export default {
  components: { commonWrapper },
  name: "homeReviews",
  props: {
    dataConfig: {
      type: Object,
      default: () => ({}),
    },
    productId: {
      type: [Number, String],
      default: 0,
    },
    reply: {
      type: Array,
      default: () => [],
    },
    replyCount: {
      type: [Number, String],
      default: 0,
    },
    replyChance: {
      type: [Number, String],
      default: 0,
    },
  },
  data() {
    return {
      replyList: [],
    };
  },
  watch: {
    productId: {
      handler(nVal) {
        if (nVal) {
          this.getReplyListFun();
        }
      },
      immediate: true,
    },
    dataConfig: {
      handler(nVal) {
        if (nVal && this.productId) {
          this.getReplyListFun();
        }
      },
      deep: true,
    },
  },
  computed: {
    configData() {
      return this.dataConfig;
    },
    bgRadius() {
      if (!this.dataConfig.fillet) return {};
      let borderRadius = `${this.dataConfig.fillet.val * 2}rpx`;
      if (this.dataConfig.fillet.type) {
        borderRadius = `${this.dataConfig.fillet.valList[0].val * 2}rpx ${
          this.dataConfig.fillet.valList[1].val * 2
        }rpx ${this.dataConfig.fillet.valList[3].val * 2}rpx ${
          this.dataConfig.fillet.valList[2].val * 2
        }rpx`;
      }
      return {
        borderRadius: borderRadius,
        overflow: "hidden",
      };
    },
    checkList() {
      return this.dataConfig.checkBoxConfig
        ? this.dataConfig.checkBoxConfig.type
        : [];
    },
    isSlide() {
      return this.dataConfig.layoutConfig
        ? this.dataConfig.layoutConfig.tabVal === 1
        : false;
    },
    showList() {
      return this.replyList;
    },
    titleColor() {
      return this.dataConfig.titleColor && this.dataConfig.titleColor.color[0]
        ? this.dataConfig.titleColor.color[0].item
        : "#333333";
    },
    countColor() {
      return this.dataConfig.countColor && this.dataConfig.countColor.color[0]
        ? this.dataConfig.countColor.color[0].item
        : "#999999";
    },
    rateColor() {
      if (
        this.dataConfig.toneConfig &&
        this.dataConfig.toneConfig.tabVal === 1
      ) {
        return this.dataConfig.rateColor && this.dataConfig.rateColor.color[0]
          ? this.dataConfig.rateColor.color[0].item
          : "#E93323";
      }
      return "var(--view-theme)";
    },
    starColor() {
      if (
        this.dataConfig.toneConfig &&
        this.dataConfig.toneConfig.tabVal === 1
      ) {
        return this.dataConfig.starColor && this.dataConfig.starColor.color[0]
          ? this.dataConfig.starColor.color[0].item
          : "#E93323";
      }
      return "var(--view-theme)";
    },
    totalCount() {
      return this.replyCount || "0";
    },
  },
  methods: {
    getReplyListFun() {
      let limit = this.dataConfig.numConfig ? this.dataConfig.numConfig.val : 2;
      getReplyList(this.productId, {
        page: 1,
        limit: limit,
        type: 0,
      }).then((res) => {
        this.replyList = res.data || [];
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.header {
  margin-bottom: 24rpx;
  .title {
    font-size: 30rpx;
    font-weight: 600;
  }
  .icon-jiantou {
    color: #999;
  }
  .count {
    font-size: 24rpx;
    margin-left: 10rpx;
  }
  .right {
    font-size: 24rpx;
  }
  .iconfont {
    font-size: 24rpx;
    margin-left: 4rpx;
  }
}
.list {
  .item {
    background-color: #f9f9f9;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-bottom: 20rpx;
  }
}
.slide-wrapper {
  white-space: nowrap;
  width: 100%;
  .slide-item {
    display: inline-block;
    background-color: #f9f9f9;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-right: 20rpx;
    width: 600rpx;
    vertical-align: top;
    &:last-child {
      margin-right: 0;
    }
  }
}
.user-info {
  margin-bottom: 16rpx;
  .avatar {
    width: 64rpx;
    height: 64rpx;
    border-radius: 50%;
  }
  .info {
    margin-left: 16rpx;
    .name {
      font-size: 26rpx;
      color: #333;
      line-height: 36rpx;
      margin-bottom: 4rpx;
      max-width: 300rpx;
    }
    .stars {
      .iconfont {
        font-size: 20rpx;
        margin-right: 4rpx;
      }
    }
  }
}
.content {
  font-size: 26rpx;
  color: #333;
  line-height: 40rpx;
  margin-bottom: 16rpx;
  white-space: normal;
}
.images {
  .img-box {
    position: relative;
    margin-right: 12rpx;
    width: 120rpx;
    height: 120rpx;
    border-radius: 8rpx;
    overflow: hidden;
    .img {
      width: 100%;
      height: 100%;
    }
    .more {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 28rpx;
    }
  }
}
</style>
