<template>
  <div>
    <div class="priceChange" :class="change === true ? 'on' : ''">
      <div class="priceTitle">
        {{
          status === 0 || status === 2 ? orderInfo.refundStatus === 1 ? "立即退款" : "一键改价" :  status === 1?"订单备注":"拒绝原因"
        }}
        <i class="el-icon-circle-close iconfonts" @click="close"></i>
        <!--<span class="iconfont icon-guanbi" @click="close"></span>-->
      </div>
      <div class="listChange" v-if="status === 0 || status === 2">
        <div
          class="item acea-row row-between-wrapper"
          v-if="orderInfo.refundStatus === 0"
        >
          <div>商品总价(¥)</div>
          <div class="money">
            {{ orderInfo.totalPrice }}<span class="iconfont icon-suozi"></span>
          </div>
        </div>
        <div
          class="item acea-row row-between-wrapper"
          v-if="orderInfo.refundStatus === 0"
        >
          <div>原始邮费(¥)</div>
          <div class="money">
            {{ orderInfo.payPostage }}<span class="iconfont icon-suozi"></span>
          </div>
        </div>
        <div
          class="item acea-row row-between-wrapper"
          v-if="orderInfo.refundStatus === 0"
        >
          <div>实际支付(¥)</div>
          <div class="money">
            <input
              type="text"
              v-model="price"
              :class="focus === true ? 'on' : ''"
              @focus="priceChange"
            />
          </div>
        </div>
        <div
          class="item acea-row row-between-wrapper"
          v-if="orderInfo.refundStatus === 1"
        >
          <div>实际支付(¥)</div>
          <div class="money">
            {{ orderInfo.payPrice }}<span class="iconfont icon-suozi"></span>
          </div>
        </div>
        <div
          class="item acea-row row-between-wrapper"
          v-if="orderInfo.refundStatus === 1"
        >
          <div>退款金额(¥)</div>
          <div class="money">
            <input
              type="text"
              v-model="refundPrice"
              :class="focus === true ? 'on' : ''"
              @focus="priceChange"
            />
          </div>
        </div>
      </div>
      <div class="listChange" v-else-if="status === 3">
        <textarea
          placeholder="请填写退款原因"
          v-model="reason" maxlength="100"
        ></textarea>
      </div>
      <div class="listChange" v-else>
        <textarea
          :placeholder="
            orderInfo.remark ? orderInfo.remark : '请填写备注信息...'
          "
          v-model="remark" maxlength="100"
        ></textarea>
      </div>
      <div class="modify" @click="save">
        {{ orderInfo.refundStatus === 0 || status === 1 || status === 3 ? "立即提交" : "确认退款" }}
      </div>
      <!--<div class="modify1" @click="refuse" v-if="orderInfo.refundStatus === 1 && status === 2">-->
        <!--拒绝退款-->
      <!--</div>-->
    </div>
    <div class="maskModel" @touchmove.prevent v-show="change === true"></div>
  </div>
</template>
<script>
  import {required, num} from "@/utils/validate";
  import {validatorDefaultCatch} from "@/libs/dialog";
  import { orderMarkApi, updatePriceApi, orderRefundApi } from '@/api/order';
  export default {
    name: "PriceChange",
    components: {},
    props: {
      change: Boolean,
      orderInfo: {
        type: Object,
        default: null
      },
      status: {
        type: Number,
        default: 0
      }
    },
    data: function () {
      return {
        focus: false,
        price: 0,
        refundPrice: 0,
        remark: "",
        reason: ''
      };
    },
    watch: {
      orderInfo: function () {
        this.price = this.orderInfo.payPrice;
        this.refundPrice = this.orderInfo.payPrice;
        this.remark = this.orderInfo.remark;
      }
    },
    created() {
      import('@/assets/js/media_750')
    },
    methods: {
      priceChange: function () {
        this.focus = true;
      },
      close: function () {
        this.price = this.orderInfo.payPrice;
        this.$emit("closechange", false);
      },
      save() {
        if(this.status === 3) {
          this.refuse();
        }else{
          this.savePrice({
            price: this.price,
            refundPrice: this.refundPrice,
            type: 1,
            remark: this.remark,
            orderId: this.orderInfo.orderId
          })
        }
      },
      async savePrice(opt) {
        let that = this,
          data = {},
          price = opt.price,
          refundPrice = opt.refundPrice,
          refundStatus = that.orderInfo.refundStatus,
          remark = opt.remark;
        if (that.status == 0 && refundStatus === 0) {
          try {
            await this.$validator({
              price: [
                required(required.message("金额")),
                num(num.message("金额"))
              ]
            }).validate({price});
          } catch (e) {
            return validatorDefaultCatch(e);
          }
          data.payPrice = price;
          data.orderNo = opt.orderId;
          updatePriceApi(data).then(() => {
            // that.change = false;
            this.$emit("closechange", false);
            that.$dialog.success("改价成功");
            // that.$emit('init');
            // that.init();
          }).catch((error) => {
            that.$dialog.error(error.message);
          });
        } else if (that.status == 2 && refundStatus === 1) {
          try {
            await this.$validator({
              refundPrice: [
                required(required.message("金额")),
                num(num.message("金额"))
              ]
            }).validate({refundPrice});
          } catch (e) {
            return validatorDefaultCatch(e);
          }
          data.amount = refundPrice;
          data.type = opt.type;
          data.orderNo = opt.orderId;
          orderRefundApi(data).then(
            res => {
              this.$emit("closechange", false);
              // that.change = false;
              that.$dialog.success('退款成功');
              // that.init();
              // that.$emit('init');
            },
            err => {
              this.$emit("closechange", false);
              that.$dialog.error(err.message);
            }
          );
        } else {
          try {
            await this.$validator({
              remark: [required(required.message("备注"))]
            }).validate({remark});
          } catch (e) {
            return validatorDefaultCatch(e);
          }
          data.mark = remark;
          data.orderNo = opt.orderId;
          orderMarkApi(data).then(
            res => {
              this.$emit("closechange", false);
              // that.change = false;
              that.$dialog.success('提交成功');
              // that.$emit('init');
              // that.init();
            },
            err => {
              this.$emit("closechange", false);
              // that.change = false;
              that.$dialog.error(err.message);
            }
          );
        }
      },
      async refuse() {
        let reason= this.reason;
        try {
          await this.$validator({
            reason: [required(required.message("备注"))]
          }).validate({reason});
        } catch (e) {
          return validatorDefaultCatch(e);
        }
        this.$emit("getRefuse", this.orderInfo.orderId, reason);
      }
    }
  };
</script>
<style scoped>
  @import '../../../styles/reset.css';
  .priceChange{position:fixed;width:5.8rem;height:6.7rem;background-color:#fff;border-radius:0.1rem;top:50%;left:50%;margin-left:-2.9rem;margin-top:-3.35rem;z-index:99;transition:all 0.3s ease-in-out 0s;-webkit-transition:all 0.3s ease-in-out 0s;-o-transition:all 0.3s ease-in-out 0s;-moz-transition:all 0.3s ease-in-out 0s;-webkit-transform:scale(0);-o-transform:scale(0);-moz-transform:scale(0);-ms-transform:scale(0);
    transform: scale(0);opacity:0;}
  .priceChange.on{opacity:1;transform: scale(1);-webkit-transform:scale(1);-o-transform:scale(1);-moz-transform:scale(1);-ms-transform:scale(1);}
  .priceChange .priceTitle{background:url("../../../assets/imgs/pricetitle.jpg") no-repeat;background-size:100% 100%;width:100%;height:1.6rem;border-radius:0.1rem 0.1rem 0 0;text-align:center;font-size:0.4rem;color:#fff;line-height:1.6rem;position:relative;}
  .priceChange .priceTitle .iconfonts{position:absolute;font-size:0.4rem;right:0.26rem;top:0.23rem;width:0.4rem;height:0.4rem;line-height:0.4rem;}
  .priceChange .listChange{padding:0 0.4rem;}
  .priceChange .listChange .item{height:1.03rem;border-bottom:1px solid #e3e3e3;font-size:0.32rem;color:#333;}
  .priceChange .listChange .item .money{color:#666;width:3rem;text-align:right;}
  .priceChange .listChange .item .money .iconfont{font-size:0.32rem;margin-left:0.2rem;}
  .priceChange .listChange .item .money input{width:100%;height:100%;text-align:right;color:#ccc;}
  .priceChange .listChange .item .money input.on{color:#666;}
  .priceChange .modify{font-size:0.32rem;color:#fff;width:4.9rem;height:0.9rem;text-align:center;line-height:0.9rem;border-radius:0.45rem;background-color:#2291f8;margin:0.53rem auto 0 auto;}
  .priceChange .modify1{font-size:0.32rem;color:#312b2b;width:4.9rem;height:0.9rem;text-align:center;line-height:0.9rem;border-radius:0.45rem;background-color:#eee;margin:0.3rem auto 0 auto;}
  .priceChange .listChange textarea {
    border: 1px solid #eee;
    width: 100%;
    height: 2rem;
    margin-top: 0.5rem;
    border-radius: 0.1rem;
    color: #333;
    padding: 0.2rem;
    font-size: 0.3rem;
  }
</style>
