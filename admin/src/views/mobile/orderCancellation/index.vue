<template>
  <div>
    <div class="OrderCancellation">
      <div class="header"></div>
      <div class="whiteBg">
        <div class="input">
          <input type="number" placeholder="请输入核销码" v-model="verify_code" />
        </div>
        <div class="bnt" @click="storeCancellation">立即核销</div>
      </div>
      <div class="scan" v-if="isWeixin">
        <img src="../../../assets/imgs/scan.gif" @click="openQRCode" />
      </div>
    </div>
    <WriteOff
      v-if="orderInfo"
      :iShidden="iShidden"
      :orderInfo="orderInfo"
      @cancel="cancel"
      @confirm="confirm"
    ></WriteOff>
  </div>
</template>
<script>
  import WriteOff from "../components/WriteOff";
  import { wechatEvevt } from "@/libs/wechat";
  // import { orderVerific } from "@api/order";
  import { writeUpdateApi, writeConfirmApi } from '@/api/order'
  const NAME = "OrderCancellation";

  export default {
    name: NAME,
    components: {
      WriteOff
    },
    props: {},
    data: function() {
      return {
        isWeixin: this.$wechat.isWeixin(),
        iShidden: true,
        orderInfo: null,
        verify_code: ""
      };
    },
    created() {
      import('@/assets/js/media_750')
    },
    methods: {
      cancel: function(res) {
        this.iShidden = res;
      },
      confirm: function() {
        writeUpdateApi(this.verify_code)
          .then(res => {
            this.iShidden = true;
            this.verify_code = "";
            this.$dialog.success(res.msg);
          })
          .catch(res => {
            this.$dialog.error(res.msg);
          });
      },
      storeCancellation: function() {
        let ref = /[0-9]{10}/;
        if (!this.verify_code) return this.$dialog.error("请输入核销码");
        if (!ref.test(this.verify_code))
        return this.$dialog.error("请输入正确的核销码");
        this.$dialog.loading.open("查询中");
        writeConfirmApi(this.verify_code)
          .then(res => {
            this.$dialog.loading.close();
            this.orderInfo = res;
            this.iShidden = false;
          })
          .catch(res => {
            this.$dialog.loading.close();
            this.verify_code = "";
            return this.$dialog.error(res.message);
          });
      },
      openQRCode: function() {
        let that = this;
        wechatEvevt("scanQRCode", {
          needResult: 1,
          scanType: ["qrCode", "barCode"]
        })
          .then(res => {
            if (res.resultStr) {
              that.verify_code = res.resultStr;
              that.storeCancellation();
            } else that.$dialog.error("没有扫描到什么！");
          })
          .catch(res => {
            if (res.is_ready) {
              res.wx.scanQRCode({
                needResult: 1,
                scanType: ["qrCode", "barCode"],
                success: function(res) {
                  that.verify_code = res.resultStr;
                  that.storeCancellation();
                },
                fail: function(res) {
                  if (res.errMsg == "scanQRCode:permission denied") {
                    that.$dialog.error("没有权限");
                  }
                }
              });
            }
          });
      }
    }
  };
</script>
<style scoped lang="scss">
  .OrderCancellation .header {
    background: url("../../../assets/imgs/writeOffBg.jpg") no-repeat;
    width: 100%;
    height: 3rem;
    background-size: 100% 100%;
  }
  .OrderCancellation .whiteBg {
    width: 5.9rem;
    background-color: #fff;
    margin: -0.93rem auto 0 auto;
    padding-top: 0.8rem;
    border-radius: 0.06rem 0.06rem 0 0;
  }
  .OrderCancellation .whiteBg .input {
    width: 5.8rem;
    margin: 0 auto;
    border-bottom: 0.01rem solid #eee;
  }
  .OrderCancellation .whiteBg .input input {
    padding: 0.25rem;
    font-size: 0.6rem;
    color: #282828;
    width: 100%;
    text-align: center;
    border: none;
  }
  .OrderCancellation .whiteBg .bnt {
    font-size: 0.32rem;
    color: #fff;
    width: 5.8rem;
    height: 0.86rem;
    border-radius: 0.43rem;
    background-image: linear-gradient(to right, #f67a38 0%, #f11b09 100%);
    background-image: -webkit-linear-gradient(to right, #f67a38 0%, #f11b09 100%);
    background-image: -moz-linear-gradient(to right, #f67a38 0%, #f11b09 100%);
    text-align: center;
    line-height: 0.86rem;
    margin: 0.55rem auto 0 auto;
  }
  .OrderCancellation .scan {
    width: 3rem;
    height: 3rem;
    margin: 1.1rem auto 0 auto;
    background-color: #f5f5f5;
  }
  .OrderCancellation .scan img {
    width: 100%;
    height: 100%;
    display: block;
  }
</style>
