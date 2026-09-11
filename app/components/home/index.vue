<template>
  <view style="touch-action: none">
    <view
      class="home"
      style="position: fixed"
      :style="{ top: top + 'px' }"
      id="right-nav"
      @touchmove.stop.prevent="setTouchMove"
    >
      <view
        class="homeCon bg-color-red"
        :class="homeActive === true ? 'on' : ''"
        v-if="homeActive"
      >
        <navigator
          :render-link="false"
          hover-class="none"
          url="/pages/index/index"
          open-type="switchTab"
          class="iconfont icon-shouye-xianxing"
        ></navigator>
        <navigator
          :render-link="false"
          hover-class="none"
          url="/pages/order_addcart/order_addcart"
          open-type="switchTab"
          class="iconfont icon-caigou-xianxing"
        ></navigator>
        <navigator
          :render-link="false"
          hover-class="none"
          url="/pages/user/index"
          open-type="switchTab"
          class="iconfont icon-yonghu1"
        ></navigator>
      </view>
      <view @click="open" class="pictrueBox">
        <view class="pictrue">
          <image
            :src="
              homeActive
                ? `${urlDomain}/crmebimage/perset/staticImg/close.gif`
                : `${urlDomain}/crmebimage/perset/staticImg/open.gif`
            "
            class="image"
          />
        </view>
      </view>
    </view>
  </view>
</template>
<script setup>
import { ref } from "vue";
import Cache from "@/utils/cache.js";
import { useAppStore } from "@/store/app.js";
import { storeToRefs } from "pinia";

const appStore = useAppStore();
const { homeActive } = storeToRefs(appStore);

const urlDomain = ref(Cache.get("imgHost"));
const top = ref("500");

function setTouchMove(e) {
  if (e.touches[0].clientY < 545 && e.touches[0].clientY > 66) {
    top.value = e.touches[0].clientY;
  }
}
function open() {
  homeActive.value ? appStore.CLOSE_HOME() : appStore.OPEN_HOME();
}
</script>

<style scoped lang="scss">
.pictrueBox {
  width: 130rpx;
  height: 120rpx;
}

/*返回主页按钮*/
.home {
  position: fixed;
  color: white;
  text-align: center;
  z-index: 9999;
  right: 15rpx;
  display: flex;
}

.home .homeCon {
  border-radius: 50rpx;
  opacity: 0;
  height: 0;
  color: $theme-color;
  width: 0;
}

.home .homeCon.on {
  opacity: 1;
  animation: bounceInRight 0.5s cubic-bezier(0.215, 0.61, 0.355, 1);
  width: 300rpx;
  height: 86rpx;
  margin-bottom: 20rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  /* background: #f44939 !important; */
  @include main_bg_color(theme);
}

.home .homeCon .iconfont {
  font-size: 48rpx;
  color: #fff;
  display: inline-block;
  margin: 0 auto;
}

.home .pictrue {
  width: 86rpx;
  height: 86rpx;
  border-radius: 50%;
  margin: 0 auto;
}

.home .pictrue .image {
  @include main_bg_color(theme);
  width: 100%;
  height: 100%;
  border-radius: 50%;
  transform: rotate(90deg);
  ms-transform: rotate(90deg);
  moz-transform: rotate(90deg);
  webkit-transform: rotate(90deg);
  o-transform: rotate(90deg);
}
</style>
