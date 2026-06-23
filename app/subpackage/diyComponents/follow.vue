<template>
  <!-- 关注公众号 -->
  <view>
    <common-wrapper :config="configData">
      <view :style="[followStyle]" class="follow acea-row row-between-wrapper">
        <view class="picTxt acea-row row-middle">
          <view class="pictrue">
            <easy-loadimage
              mode="widthFix"
              :image-src="dataConfig.imgConfig.url"
              width="92rpx"
              height="92rpx"
              borderRadius="46rpx"
            ></easy-loadimage>
          </view>
          <view class="name line1">
            {{ dataConfig.titleConfig.value }}
          </view>
        </view>
        <view
          :style="[buttonStyle]"
          class="notes acea-row row-center-wrapper"
          @click="followTap"
        >
          {{ '关注' }}
        </view>
        <view class="iconfont icon-iconfontguanbi"></view>
      </view>
    </common-wrapper>
    <view class="followCode" v-if="followCode">
      <view class="pictrue">
        <view class="title">{{ '关注公众号' }}</view>
        <view class="tips">{{ '活动福利，第一时间了解' }}</view>
        <view class="code-bg">
          <image class="imgs" :src="dataConfig.codeConfig.url" mode=""></image>
        </view>
        <!-- #ifdef MP || APP-PLUS -->
        <view class="btn" @tap="savePic">{{ '保存图片' }}</view>
        <!-- #endif -->
        <!-- #ifdef H5 -->
        <view class="btn" v-show="isWeixin" @tap="savePic">{{
          '长按保存图片'
        }}</view>
        <view class="btn" v-show="!isWeixin" @tap="savePic">{{
          '保存图片'
        }}</view>
        <!-- #endif -->
        <view
          class="close acea-row row-center-wrapper"
          @click="closeFollowCode"
        >
          <text class="iconfont icon-ic_close1"></text>
        </view>
      </view>
      <view class="mask"></view>
    </view>
  </view>
</template>

<script>
import commonWrapper from "./commonWrapper.vue";
import { follow } from "@/api/api.js";
import { getSubscribe } from "@/api/public";
// #ifdef H5
import Auth from "@/libs/wechat";
// #endif
export default {
  components: { commonWrapper },
  name: "follow",
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
      followCode: false,
      followUrl: "",
      bgColor: "",
      imgConfig: "",
      mbConfig: 0,
      themeColor: "",
      titleConfig: 0,
      subscribe: false,
      // #ifdef H5
      isWeixin: Auth.isWeixin(),
      //#endif
    };
  },
  computed: {
    buttonStyle() {
      return {
        "border-color": this.dataConfig.themeColor.color[0].item,
        color: this.dataConfig.themeColor.color[0].item,
      };
    },
    followStyle() {
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
        color: this.dataConfig.themeColor.color[0].item,
      };
    },
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
    followStyle() {
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
        color: this.dataConfig.themeColor.color[0].item,
      };
    },
  },
  created() {},
  mounted() {
    getSubscribe()
      .then((res) => {
        this.subscribe = res.data.subscribe || false;
      })
      .catch(() => {});
  },
  methods: {
    savePic() {
      // #ifdef H5
      var a = document.createElement("a"); // 生成一个a元素
      a.download = "wechat"; // 设置图片名称
      a.style.display = "none";
      a.href = this.dataConfig.codeConfig.url; // 将生成的URL设置为a.href属性
      document.body.appendChild(a); // 将a标签追加到文档对象中
      a.click(); // 触发a的单击事件
      a.remove(); // 一次性的，用完就删除a标签
      // #endif
      // #ifdef MP
      let _that = this;
      uni.downloadFile({
        url: _that.dataConfig.codeConfig.url, //图片地址
        success: function (response) {
          uni.getSetting({
            success(res) {
              if (!res.authSetting["scope.writePhotosAlbum"]) {
                uni.authorize({
                  scope: "scope.writePhotosAlbum",
                  success() {
                    uni.saveImageToPhotosAlbum({
                      filePath: response.tempFilePath,
                      success: function (res) {
                        _that.closeFollowCode();
                        _that.$util.Tips({
                          title: "保存成功",
                          icon: "success",
                        });
                      },
                      fail: function (res) {
                        _that.$util.Tips({
                          title: "保存失败",
                        });
                      },
                    });
                  },
                });
              } else {
                uni.saveImageToPhotosAlbum({
                  filePath: response.tempFilePath,
                  success: function (res) {
                    _that.closeFollowCode();
                    _that.$util.Tips({
                      title: "保存成功",
                      icon: "success",
                    });
                  },
                  fail: function (res) {
                    _that.$util.Tips({
                      title: "保存失败",
                    });
                  },
                });
              }
            },
          });
        },
      });
      // #endif
      //#ifdef APP-PLUS
      let thatApp = this;
      uni.downloadFile({
        url: thatApp.dataConfig.codeConfig.url, //图片地址
        success: function (response) {
          uni.saveImageToPhotosAlbum({
            filePath: response.tempFilePath,
            success: function (res) {
              thatApp.posterImageClose();
              thatApp.$util.Tips({
                title: "保存成功",
                icon: "success",
              });
            },
            fail: function (res) {
              thatApp.$util.Tips({
                title: "保存失败",
              });
            },
          });
        },
      });
      // #endif
    },
    followTap() {
      this.followCode = true;
    },
    closeFollowCode() {
      this.followCode = false;
    },
  },
};
</script>

<style lang="scss">
.follow {
  padding: 14rpx 32rpx 14rpx 30rpx;
  background: #ffffff;

  .picTxt {
    flex: 1;
    min-width: 0;

    .name {
      flex: 1;
      font-size: 30rpx;
      color: #333333;
      margin-left: 20rpx;
    }
  }

  .notes {
    position: relative;
    font-weight: 500;
    font-size: 24rpx;
    color: var(--view-theme);
    width: 112rpx;
    height: 56rpx;
    border: 1px solid var(--view-theme);
    border-radius: 28rpx;
    overflow: hidden;
  }

  .iconfont {
    margin-left: 16rpx;
    font-size: 32rpx;
    color: #333333;
  }
}

.followCode {
  .pictrue {
    width: 548rpx;
    height: 758rpx;
    border-radius: 48rpx;
    background: #ffffff;
    left: 50%;
    top: 50%;
    position: fixed;
    z-index: 10000;
    transform: translate(-50%, -50%);

    .title {
      padding: 48rpx 0 46rpx;
      border-top-left-radius: 48rpx;
      border-top-right-radius: 48rpx;
      border-bottom-right-radius: 274rpx 40rpx;
      border-bottom-left-radius: 274rpx 40rpx;
      background: linear-gradient(90deg, #ff7931 0%, var(--view-theme) 100%);
      text-align: center;
      font-weight: 500;
      font-size: 40rpx;
      line-height: 56rpx;
      color: #ffffff;
    }

    .tips {
      margin-top: 48rpx;
      text-align: center;
      font-size: 28rpx;
      line-height: 40rpx;
      color: #3d3d3d;
    }

    .code-bg {
      width: 312rpx;
      height: 312rpx;
      margin: 24rpx auto 0;
    }

    .imgs {
      width: 100%;
      height: 100%;
    }

    .btn {
      width: 420rpx;
      height: 80rpx;
      border-radius: 40rpx;
      margin: 40rpx auto 60rpx;
      background: linear-gradient(90deg, #ff7931 0%, var(--view-theme) 100%);
      text-align: center;
      font-weight: 500;
      font-size: 28rpx;
      line-height: 80rpx;
      color: #ffffff;
    }

    .close {
      position: absolute;
      bottom: -108rpx;
      left: 50%;
      width: 60rpx;
      height: 60rpx;
      border-radius: 50%;
      transform: translateX(-50%);

      .iconfont {
        font-size: 40rpx;
        color: #cccccc;
      }
    }
  }

  .mask {
    z-index: 9999;
  }
}
.official-account {
  position: absolute;
  z-index: 99999;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  opacity: 0;
}
</style>
