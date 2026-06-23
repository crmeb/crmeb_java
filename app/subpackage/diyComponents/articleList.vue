<template>
  <common-wrapper v-show="!isSortType" :config="configData">
    <view
      class="articleList"
      :class="{
        large: dataConfig.styleConfig.tabVal == 0,
        small: dataConfig.styleConfig.tabVal == 2,
      }"
      v-if="articleList.length"
    >
      <navigator
        :url="'/pages/news/news_details/index?id=' + item.id"
        hover-class="none"
        v-for="(item, index) in articleList"
        :key="index"
        :style="[articleItemStyle]"
        class="item"
      >
        <view v-if="dataConfig.styleConfig.tabVal != 0" class="image-wrap">
          <easy-loadimage
            :imageSrc="item.image_input[0]"
            :borderRadius="borderRadius"
            width="100%"
            height="100%"
          ></easy-loadimage>
        </view>
        <view class="text-wrap">
          <view class="title-wrap">
            <view
              class="title"
              :class="{
                line1: dataConfig.styleConfig.tabVal != 1,
                line2: dataConfig.styleConfig.tabVal == 1,
              }"
              :style="[titleStyle]"
              >{{ item.title }}</view
            >
          </view>
          <view v-if="dataConfig.styleConfig.tabVal == 0" class="image-wrap">
            <easy-loadimage
              :imageSrc="item.image_input[0]"
              :borderRadius="borderRadius"
              width="100%"
              height="100%"
            ></easy-loadimage>
          </view>
          <view class="time-wrap" :style="[numberStyle]">
            <view
              v-if="dataConfig.checkboxList.type.includes(0)"
              class="time time-wrap-item"
              :style="[timeStyle]"
              >{{ item.add_time }}</view
            >
            <view v-else class="time time-wrap-item"></view>
            <view class="like-wrap time-wrap-item">
              <view
                v-if="dataConfig.checkboxList.type.includes(1)"
                class="view acea-row row-middle"
                :class="!isLike ? 'on' : ''"
              >
                <text
                  class="iconfont icon-liulan"
                  :style="[iconEyesStyle]"
                ></text
                >{{ item.visit }}
              </view>
              <!-- <view v-if="dataConfig.checkboxList.type.includes(2)" class="like">
								<text class="iconfont icon-ic_Like" :style="[iconLikeStyle]"></text>{{ item.likes }}
							</view> -->
            </view>
          </view>
        </view>
      </navigator>
    </view>
  </common-wrapper>
</template>

<script>
import commonWrapper from "./commonWrapper.vue";
import { getThemeArticle } from "@/api/api.js";
import dayjs from "@/plugin/dayjs/dayjs.min.js";
export default {
  components: { commonWrapper },
  name: "articleList",
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
      articleList: [],
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
    isLike() {
      let like = true;
      like = this.dataConfig.checkboxList.type.includes(2);
      return like;
    },
    borderRadius() {
      let borderRadius = `${this.dataConfig.filletImg.val * 2}rpx`;
      if (this.dataConfig.filletImg.type) {
        borderRadius = `${this.dataConfig.filletImg.valList[0].val * 2}rpx ${
          this.dataConfig.filletImg.valList[1].val * 2
        }rpx ${this.dataConfig.filletImg.valList[3].val * 2}rpx ${
          this.dataConfig.filletImg.valList[2].val * 2
        }rpx`;
      }
      return borderRadius;
    },
    timeStyle() {
      return {
        color: this.dataConfig.timeColor.color[0].item,
      };
    },
    iconEyesStyle() {
      return {
        color: this.dataConfig.browseColor.color[0].item,
      };
    },
    // iconLikeStyle() {
    // 	return {
    // 		'color': this.dataConfig.likeColor.color[0].item,
    // 	};
    // },
    numberStyle() {
      return {
        color: this.dataConfig.statisticColor.color[0].item,
      };
    },
    articleItemStyle() {
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
        background: `linear-gradient(90deg, ${this.dataConfig.bgColor.color[0].item} 0%, ${this.dataConfig.bgColor.color[1].item} 100%)`,
      };
    },
    titleStyle() {
      let styleObject = {
        color: this.dataConfig.nameColor.color[0].item,
      };
      if (!this.dataConfig.nameConfig.tabVal) {
        styleObject["font-weight"] = "bold";
      }
      return styleObject;
    },
  },
  mounted() {
    this.getCidArticle();
  },
  methods: {
    getCidArticle: function () {
      let limit = this.$config.LIMIT;
      getThemeArticle({
        cid: Array.isArray(this.dataConfig.selectConfig.activeValue)
          ? this.dataConfig.selectConfig.activeValue.join(",")
          : this.dataConfig.selectConfig.activeValue || "",
        page: 1,
        limit:
          this.dataConfig.numConfig.val >= limit
            ? limit
            : this.dataConfig.numConfig.val,
      }).then((res) => {
        const data = res.data || {};
        const list = Array.isArray(data) ? data : data.list || [];
        if (this.dataConfig.styleConfig.tabVal == 2) {
          list.forEach((item) => {
            item.add_time = dayjs(item.add_time).format("MM-DD HH:mm");
          });
        }
        this.articleList = list;
      });
    },
  },
};
</script>

<style lang="scss">
.articleList {
  .item {
    display: flex;
    padding: 20rpx;
    margin: 0;
    background: #ffffff;
    margin-bottom: 20rpx;
    &:last-child {
      margin-bottom: 0;
    }
  }

  .text-wrap {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 0 0 6rpx 24rpx;
  }

  .title-wrap {
    flex: 1;
  }

  .title {
    font-size: 30rpx;
    line-height: 42rpx;
    color: #333333;
  }

  .label-wrap {
    margin-top: 16rpx;
    font-size: 0;
  }

  .label {
    display: inline-flex;
    flex-wrap: nowrap;
    height: 28rpx;
    padding: 0 6rpx;
    border: 1rpx solid #e93323;
    border-radius: 6rpx;
    font-size: 20rpx;
    color: #e93323;
  }

  .image-wrap {
    width: 240rpx;
    height: 152rpx;
  }

  .time-wrap {
    display: flex;
    font-size: 24rpx;
    line-height: 34rpx;
    color: #999999;

    .time-wrap-item:first-child {
      flex: 1;
    }
  }

  .like-wrap {
    display: flex;
  }

  .view {
    flex: 1;
    &.on {
      margin-right: 0;
    }
  }

  .iconfont {
    margin-right: 8rpx;
    font-size: 28rpx;
  }

  &.large {
    .item {
      flex-direction: column;
      padding: 0;
    }

    .text-wrap {
      padding: 32rpx 24rpx;
    }

    .image-wrap {
      width: 100%;
      height: 320rpx;
      margin-top: 24rpx;
    }

    .time-wrap {
      margin-top: 24rpx;
    }
  }

  &.small {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;

    .item {
      flex-direction: column;
      width: calc(50% - 10rpx);
      padding: 0;
      margin-bottom: 20rpx;
      box-sizing: border-box;
    }

    .text-wrap {
      padding: 20rpx 20rpx 18rpx;
      box-sizing: border-box;
    }

    .image-wrap {
      width: 100%;
      height: auto;
      aspect-ratio: 342 / 216;
    }

    .label-wrap {
      margin-top: 16rpx;
    }

    .time-wrap {
      margin-top: 16rpx;
    }

    .like-wrap {
      justify-content: space-between;
    }
  }
}
</style>
