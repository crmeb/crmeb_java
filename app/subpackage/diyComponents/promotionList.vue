<template>
  <!-- 促销列表 -->
  <view v-show="!isSortType">
    <common-wrapper :config="configData">
      <view class="index-product-wrapper">
        <view :style="{ height: navBdH + 'px' }" class="nav-bd-box">
          <view
            class="nav-bd"
            :class="{
              'nav-bd2': dataConfig.styleConfig.tabVal == 1,
              'nav-bd3': dataConfig.styleConfig.tabVal == 2,
              'nav-bd4': dataConfig.styleConfig.tabVal == 3,
              'nav-bd5': dataConfig.styleConfig.tabVal == 4,
            }"
            :style="{
              position: sticky ? 'fixed' : 'static',
              top: positionTop + 'px',
              background: sticky ? '#FFFFFF' : 'none',
            }"
          >
            <scroll-view class="scroll-view" scroll-x="true">
              <view
                class="item"
                v-for="(item, index) in explosiveMoney"
                :key="index"
                :class="{ on: index == ProductNavindex }"
                @click="ProductNavTab(item, index)"
              >
                <view
                  v-if="dataConfig.styleConfig.tabVal == 4"
                  class="image-wrap"
                >
                  <image :src="item.image" class="image"></image>
                </view>
                <view
                  class="txt"
                  :style="[index == ProductNavindex ? textColor : {}]"
                  >{{ item.chiild[0].val }}</view
                >
                <view
                  v-if="[1, 2].includes(dataConfig.styleConfig.tabVal)"
                  class="line"
                  :style="[index == ProductNavindex ? lineColor : {}]"
                ></view>
                <view
                  class="label"
                  v-if="
                    dataConfig.styleConfig.tabVal == 0 && item.chiild[1].val
                  "
                  :style="[index == ProductNavindex ? decorateColor : {}]"
                >
                  {{ item.chiild[1].val }}
                </view>
              </view>
            </scroll-view>
          </view>
        </view>
        <goodList
          ref="goodLists"
          v-if="goodDataConfig"
          :dataConfig="goodDataConfig"
          @detail="goDetail"
        ></goodList>
      </view>
    </common-wrapper>
  </view>
</template>

<script setup>
import { ref, computed, watch, onMounted, getCurrentInstance } from "vue";
import commonWrapper from "./commonWrapper.vue";
import { getThemeProduct } from "@/api/api.js";
import goodList from "./goodList.vue";
import { useAppStore } from "@/store/app.js";
import configs from "@/config/app.js";
import util from "@/utils/util.js";

const { proxy } = getCurrentInstance();

const props = defineProps({
  dataConfig: {
    type: Object,
    default: () => {},
  },
  isSortType: {
    type: [String, Number],
    default: 0,
  },
  productVideoStatus: {
    type: Boolean,
    default: false,
  },
  positionTop: {
    type: Number,
    default: 0,
  },
});

const emit = defineEmits(["detail"]);

const goodLists = ref(null);

const tempArr = ref([]);
const iSshowH = ref(false);
const ProductNavindex = ref(0);
const explosiveMoney = ref(props.dataConfig.tabConfig.list);
const numConfig = ref(props.dataConfig.tabConfig.list[0].numConfig.val);
const mbConfig = ref(0);
const themeColor = ref("");
const titleShow = ref(0); //标题是否显示
const opriceShow = ref(0); //划线价是否显示
const priceShow = ref(0); //价格是否显示
const couponShow = ref(0); //优惠券标签是否显示
const titleConfig = ref(0); //标题位置
const fontColor = ref("");
const labelColor = ref("");
const txtColor = ref("");
const infoColor = ref("");
const goodType = ref(props.dataConfig.tabConfig.list[0].tabVal);
const loadend = ref(false);
const loading = ref(false);
const limit = ref(configs.LIMIT);
const page = ref(1);
const canPlay = ref(false);
const autoplay = ref(false);
const activeValue = ref(props.dataConfig.tabConfig.list[0]);
const goodDataConfig = ref(null);
const sticky = ref(false);
const navBdH = ref(0);
const loadTitle = ref("");

const configData = computed(() => {
  return {
    ...props.dataConfig,
    paddingConfig: props.dataConfig.paddingConfig || {
      isAll: false,
      valList: [
        {
          val: props.dataConfig.topConfig
            ? props.dataConfig.topConfig.val
            : 0,
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

const decorateColor = computed(() => {
  if (props.dataConfig.toneConfig.tabVal) {
    let color = props.dataConfig.decorateColor.color;
    let background = `linear-gradient(90deg, ${color[0].item} 0%, ${color[1].item} 100%)`;
    if (props.dataConfig.styleConfig.tabVal == 2) {
      color = props.dataConfig.decorateColor2.color;
      background = color[0].item;
    }
    return {
      background: background,
    };
  } else {
    return {
      background: `linear-gradient(90deg, var(--view-gradient) 0%, var(--view-theme) 100%)`,
    };
  }
});

const textColor = computed(() => {
  if (props.dataConfig.toneConfig.tabVal) {
    let color = props.dataConfig.textColor.color[0].item;
    let bgColor = props.dataConfig.decorateColor.color;
    let background = "";
    if ([0, 1, 2].includes(props.dataConfig.styleConfig.tabVal)) {
      color = props.dataConfig.textColor.color[0].item;
    } else if ([3, 4].includes(props.dataConfig.styleConfig.tabVal)) {
      color = "#ffffff";
      background = `linear-gradient(90deg, ${bgColor[0].item} 0%, ${bgColor[1].item} 100%)`;
    } else if (props.dataConfig.styleConfig.tabVal == 1) {
      color = "#282828";
    }
    return {
      color: color,
      background: background,
    };
  } else {
    if (props.dataConfig.styleConfig.tabVal == 1) {
      return {
        color: "#282828",
      };
    } else if ([3, 4].includes(props.dataConfig.styleConfig.tabVal)) {
      return {
        background:
          "linear-gradient(90deg, var(--view-gradient) 0, var(--view-theme) 100%)",
        color: "#ffffff",
      };
    } else {
      return {
        color: "var(--view-theme)",
      };
    }
  }
});

const lineColor = computed(() => {
  let diy = props.dataConfig.toneConfig.tabVal;
  let type = props.dataConfig.styleConfig.tabVal;
  let color = props.dataConfig.decorateColor.color;
  let bgColor = `linear-gradient(90deg, ${color[0].item} 0%, ${color[1].item} 100%)`;
  if (type == 1) {
    return {
      background: diy
        ? bgColor
        : "linear-gradient(90deg, var(--view-gradient) 0, var(--view-theme) 100%)",
    };
  } else if (type == 2) {
    return {
      "border-bottom-color": diy
        ? props.dataConfig.textColor2.color[0].item
        : "var(--view-theme)",
    };
  }
});

function buildGoodDataConfig() {
  let type = goodType.value == 0 ? 3 : goodType.value;
  return {
    styleConfig: {
      tabVal: 1,
    },
    goodsList: activeValue.value.goodsList,
    brandList: activeValue.value.brandConfig,
    classList: {
      classVal: activeValue.value.selectConfig.activeValue,
    },
    goodsLabel: activeValue.value.goodsLabel,
    typeConfig: {
      activeValue: type,
    },
    goodsSort: {
      tabVal: activeValue.value.goodsSort,
    },
    numberConfig: {
      val: numConfig.value,
    },
    bntStyleConfig: props.dataConfig.bntStyleConfig,
    cartConfig: props.dataConfig.cartConfig,
    bntConfig: props.dataConfig.bntConfig,
    filletImg: {
      type: 0,
      val: 8,
    },
    checkboxInfo: {
      type: [0, 1, 2, 3, 4, 5],
    },
    toneConfig: {
      tabVal: 0,
    },
    toneCartConfig: props.dataConfig.toneCartConfig,
    bntBgColor: props.dataConfig.bntBgColor,
    goodsName: {
      tabVal: 1,
    },
    goodsNameColor: {
      color: [
        {
          item: "#333333",
        },
      ],
    },
    goodsPriceColor: {
      color: [
        {
          item: props.dataConfig.toneCartConfig.tabVal
            ? props.dataConfig.goodsPriceColor.color[0].item
            : "var(--view-theme)",
        },
      ],
    },
    topConfig: {
      val: 0,
    },
    prConfig: {
      val: 0,
    },
    bottomConfig: {
      val: 0,
    },
    mbConfig: {
      val: 0,
    },
    bottomBgColor: {
      color: [
        {
          item: "",
        },
      ],
    },
    fillet: props.dataConfig.fillet,
    name: "promotionList",
  };
}

watch(
  activeValue,
  () => {
    goodDataConfig.value = buildGoodDataConfig();
  },
  { immediate: true }
);

watch(
  goodType,
  () => {
    //value !== undefined && getGroomList();
  },
  { immediate: true }
);

watch(tempArr, () => {
  // #ifndef APP-PLUS
  proxy.$nextTick(() => {
    if (props.productVideoStatus) {
      uni.getNetworkType({
        success: (res) => {
          if (["wifi", "unknown"].includes(res.networkType)) {
            // 监听
            observeVideo();
          }
          if (["2g", "3g", "4g", "5g"].includes(res.networkType)) {
            if (useAppStore().autoplay) {
              // 监听
              observeVideo();
            } else {
              proxy.$eventHub.emit("confirm_video_status");
            }
          }
        },
      });
    }
  });
  // #endif
});

// created
// #ifndef APP-PLUS
proxy.$eventHub.on("product_video_observe", () => {
  observeVideo();
});
// #endif
// getGroomList();
goodDataConfig.value = buildGoodDataConfig();

onMounted(() => {
  let view = uni.createSelectorQuery().in(proxy).select(".nav-bd");
  let views = uni.createSelectorQuery().in(proxy).select(".nav-bd-box");
  view
    .boundingClientRect((data) => {
      navBdH.value = data ? data.height : 0;
    })
    .exec();
  if (!props.dataConfig.slideConfig.tabVal) {
    uni.$on("onPageScroll", () => {
      views
        .boundingClientRect((data) => {
          sticky.value = data ? data.top <= props.positionTop : false;
        })
        .exec();
    });
  }
});

function observeVideo() {
  autoplay.value = true;
  // let observer = uni.createIntersectionObserver(this, { observeAll: true });
  // observer.relativeToViewport().observe('.video', res => {
  // 	if (res.intersectionRatio) {
  // 		uni.createVideoContext(res.id, this).play();
  // 	} else{
  // 		uni.createVideoContext(res.id, this).pause();
  // 	}
  // });
}

// 促销列表的点击事件；
function changeTab(item) {
  goodType.value = item.tabVal;
  activeValue.value = item;
  // tempArr.value = [];
  // page.value = 1;
  // loadend.value = false;
  // let onloadH = true;
  // getGroomList(onloadH);
}

// 精品推荐
function getGroomList(onloadH) {
  let type = goodType.value == 0 ? 3 : goodType.value;
  if (loadend.value) return false;
  if (loading.value) return false;
  if (onloadH) {
    iSshowH.value = true;
  }
  let datas = {
    limit: numConfig.value,
    order: activeValue.value.goodsSort || 0,
    sort: activeValue.value.goodsSort || 0,
  };
  if (type == 1) {
    datas.ids = activeValue.value.goodsList.ids.join();
  } else if (type == 3) {
    datas.cate_ids = activeValue.value.selectConfig.activeValue.join();
  }
  getThemeProduct(datas)
    .then(({ data }) => {
      iSshowH.value = false;
      let maxPage = Math.ceil(numConfig.value / limit.value);
      let list = Array.isArray(data) ? data : data && data.list ? data.list : [],
        isLoadend = list.length < limit.value || page.value >= maxPage;
      let newTempArr = util.SplitArray(list, tempArr.value);
      tempArr.value = newTempArr.slice(0, numConfig.value);
      loadend.value = isLoadend;
      loadTitle.value = isLoadend ? "没有更多内容啦~" : "加载更多";
      page.value = page.value + 1;
      loading.value = false;
    })
    .catch((res) => {
      loading.value = false;
      loadTitle.value = "加载更多";
    });
}

// 首发新品切换
function ProductNavTab(item, index) {
  ProductNavindex.value = index;
  changeTab(item);
}

function goDetail(item) {
  emit("detail", item);
}
</script>

<style lang="scss">
// 这里可以自行配置
$border-radius: 10px;

.index-product-wrapper {
  &.on {
    min-height: 1500rpx;
  }

  .nav-bd {
    position: relative;
    top: 0;
    right: 0;
    left: 0;
    z-index: 99;

    &.nav-bd2 {
      .item {
        position: relative;
        padding: 18rpx 0;
        margin-right: 56rpx;

        &:first-child {
          // margin-left: 24rpx;
        }

        &:last-child {
          margin-right: 24rpx;
        }

        &.on {
          .txt {
            font-weight: 500;
            font-size: 32rpx;
            color: #333333;
          }

          // .line {
          //	background: linear-gradient(90deg, var(--view-gradient) 0, var(--view-theme) 100%);
          // }
        }

        .txt {
          position: relative;
          z-index: 1;
          font-size: 28rpx;
          line-height: 44rpx;
        }

        .line {
          position: absolute;
          bottom: 22rpx;
          left: 0;
          width: 100%;
          height: 8rpx;
          border-radius: 4rpx;
        }
      }
    }

    &.nav-bd3 {
      .item {
        position: relative;
        padding: 18rpx 0;
        margin-right: 56rpx;

        &:first-child {
          // margin-left: 24rpx;
        }

        &:last-child {
          margin-right: 24rpx;
        }

        &.on {
          .txt {
            font-weight: 500;
            font-size: 32rpx;
            color: #e93323;
          }

          // .line {
          // 	border-bottom-color: var(--view-theme);
          // }
        }

        .txt {
          font-size: 28rpx;
          line-height: 44rpx;
        }

        .line {
          position: absolute;
          bottom: 8rpx;
          left: 50%;
          width: 64rpx;
          height: 64rpx;
          border: 4rpx solid transparent;
          border-radius: 50%;
          transform: translateX(-50%);
        }
      }
    }

    &.nav-bd4 {
      .item {
        padding: 16rpx 0;
        margin-right: 16rpx;

        &:first-child {
          // margin-left: 20rpx;
        }

        &:last-child {
          margin-right: 20rpx;
        }

        &.on {
          .txt {
            background: linear-gradient(90deg, #ff7931 0%, #e93323 100%);
            font-size: 26rpx;
            color: #ffffff;
          }
        }

        .txt {
          height: 48rpx;
          padding: 0 20rpx;
          border-radius: 24rpx;
          font-size: 28rpx;
          line-height: 48rpx;
        }
      }
    }

    &.nav-bd5 {
      padding-left: 20rpx;
      .item {
        padding: 20rpx 0;
        margin-right: 16rpx;

        &:first-child {
          // margin-left: 24rpx;
        }

        &:last-child {
          margin-right: 24rpx;
        }

        &.on {
          .image-wrap {
            border-color: var(--view-theme);
            background: #ffffff;
          }

          .txt {
            // background: linear-gradient(90deg, #FF7931 0%, #E93323 100%);
            color: #ffffff;
          }
        }

        .image-wrap {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 104rpx;
          height: 104rpx;
          border: 3rpx solid transparent;
          border-radius: 50%;
          margin: 0 auto;
        }

        .image {
          width: 92rpx;
          height: 92rpx;
          border: 1rpx solid #eeeeee;
          border-radius: 50%;
        }

        .txt {
          height: 40rpx;
          padding: 0 12rpx;
          border-radius: 20rpx;
          margin-top: 8rpx;
          font-size: 24rpx;
          line-height: 40rpx;
          color: #333333;
        }
      }
    }

    .scroll-view {
      white-space: nowrap;
    }

    .item {
      display: inline-block;
      padding: 10rpx 0 14rpx;
      margin-right: 66rpx;
      text-align: center;

      &:first-child {
        // margin-left: 36rpx;
      }

      &:last-child {
        margin-right: 36rpx;
      }

      &.on {
        .txt {
          font-weight: 500;
          color: #e93323;
        }

        .label {
          background: linear-gradient(90deg, #ff7931 0%, #e93323 100%);
          font-size: 22rpx;
          color: #ffffff;
        }
      }

      .txt {
        font-size: 30rpx;
        line-height: 42rpx;
        color: #282828;
      }

      .label {
        display: inline-block;
        height: 38rpx;
        padding: 0 12rpx;
        border-radius: 19rpx;
        margin-top: 6rpx;
        font-size: 24rpx;
        line-height: 38rpx;
        color: #999999;
      }
    }
  }
}
</style>
