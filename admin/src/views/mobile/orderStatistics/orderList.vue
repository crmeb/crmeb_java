<template>
  <div class="pos-order-list" ref="container">
    <div class="nav acea-row row-around row-middle">
      <div
        class="item"
        :class="where.status == 'unPaid' ? 'on' : ''"
        @click="changeStatus('unPaid')"
      >
        待付款
      </div>
      <div
        class="item"
        :class="where.status == 'notShipped' ? 'on' : ''"
        @click="changeStatus('notShipped')"
      >
        待发货
      </div>
      <div
        class="item"
        :class="where.status == 'spike' ? 'on' : ''"
        @click="changeStatus('spike')"
      >
        待收货
      </div>
      <div
        class="item"
        :class="where.status == 'toBeWrittenOff' ? 'on' : ''"
        @click="changeStatus('toBeWrittenOff')"
      >
        待核销
      </div>
      <div
        class="item"
        :class="where.status == 'complete' ? 'on' : ''"
        @click="changeStatus('complete')"
      >
        已完成
      </div>
      <div
        class="item"
        :class="where.status == 'refunding' ? 'on' : ''"
        @click="changeStatus('refunding')"
      >
        退款
      </div>
    </div>
    <div class="list">
      <template v-if="list.length > 0">
        <div class="item" v-for="(item, index) in list" :key="index">
          <div class="order-num acea-row row-middle" @click="toDetail(item)">
            订单号：{{ item.orderId }}
            <span class="time">下单时间：{{ item.createTime }}</span>
          </div>
          <template if="item.productList && item.productList.length">
            <div
              class="pos-order-goods"
              v-for="(val, key) in item.productList"
              :key="key"
            >
              <div
                class="goods acea-row row-between-wrapper"
                @click="toDetail(item)"
              >
                <div class="picTxt acea-row row-between-wrapper">
                  <div class="pictrue">
                    <img :src="val.info.image" />
                  </div>
                  <div class="text ">
                    <div class="info line2">
                      {{ val.info.productName }}
                    </div>
                    <div class="attr" v-if="val.info.sku">
                      {{ val.info.sku }}
                    </div>
                  </div>
                </div>
                <div class="money">
                  <div class="x-money">￥{{ val.info.price }}</div>
                  <div class="num">x{{ val.info.payNum }}</div>
                  <div class="y-money">
                    <!--￥{{ val.info.productInfo.attrInfo.otPrice }}-->
                  </div>
                </div>
              </div>
            </div>
          </template>
          <div class="public-total">
            共{{ item.totalNum }}件商品，应支付
            <span class="money">￥{{ item.payPrice }}</span> ( 邮费 ¥{{
            item.totalPostage
            }}
            )
          </div>
          <div class="operation acea-row row-between-wrapper">
            <div class="more">
              <!--            <div class="iconfont icon-gengduo" @click="more(index)"></div>-->
              <!--            <div class="order" v-show="current === index">-->
              <!--              <div class="items">-->
              <!--                {{ where.status > 0 ? "删除" : "取消" }}订单-->
              <!--              </div>-->
              <!--              <div class="arrow"></div>-->
              <!--            </div>-->
            </div>
            <div class="acea-row row-middle">
              <div class="bnt" @click="modify(item, 0)" v-if="where.status === 'unPaid'">
                一键改价
              </div>
              <div class="bnt" @click="modify(item, 1)">订单备注</div>
              <div
                class="bnt"
                @click="modify(item, 2)"
                v-if="where.status === 'refunding' && item.refundStatus === 1"
              >
                立即退款
              </div>
              <div
                class="bnt"
                @click="modify(item, 3)"
                v-if="where.status === 'refunding' && item.refundStatus === 1"
              >
                拒绝退款
              </div>
              <!--<div-->
              <!--class="bnt cancel"-->
              <!--v-if="item.pay_type === 'offline' && item.paid === 0"-->
              <!--@click="offlinePay(item)"-->
              <!--&gt;-->
              <!--确认付款-->
              <!--</div>-->
              <router-link
                class="bnt"
                v-if="where.status === 'notShipped' && item.shippingType !== 2 && item.refundStatus !==2"
                :to="'/javaMobile/orderDelivery/' + item.orderId + '/' + item.id"
              >去发货
              </router-link>
              <router-link
                class="bnt"
                v-if="where.status === 'toBeWrittenOff' && item.shippingType === 2 && isWriteOff && item.refundStatus === 0 && item.paid == true"
                :to="'/javaMobile/orderCancellation'"
              >去核销
              </router-link>
            </div>
          </div>
        </div>
      </template>
     <template v-if="!loading && list.length === 0">
       <div style="text-align: center;">暂无数据</div>
     </template>
    </div>
    <Loading :loaded="loaded" :loading="loading"></Loading>
    <PriceChange
      v-if="orderInfo"
      :change="change"
      :orderInfo="orderInfo"
      v-on:closechange="changeclose($event)"
      :status="status"
      @getRefuse="getRefuse"
    ></PriceChange>
  </div>
</template>
<script>
  import PriceChange from "../components/PriceChange";
  import Loading from "../components/Loading";
  import { orderRefuseApi, orderListApi, statisticsDataApi, orderMarkApi, editPriceApi, orderRefundApi } from '@/api/order';
  import { required, num } from "@/utils/validate";
  import { validatorDefaultCatch } from "@/libs/dialog";
  import { isWriteOff } from "@/utils";
  export default {
    name: "AdminOrderList",
    components: {
      PriceChange,
      Loading
    },
    props: {},
    data: function() {
      return {
        isWriteOff: isWriteOff(),
        current: "",
        change: false,
        types: 0,
        where: {
          page: 1,
          limit: 10,
          status: 'unPaid'
        },
        list: [],
        loaded: false,
        loading: false,
        orderInfo: {},
        status: null
      };
    },
    watch: {
      "$route.params.types": function(newVal) {
        let that = this;
        if (newVal != undefined) {
          that.where.status = newVal;
          that.init();
        }
      },
      types: function() {
        this.getIndex();
      }
    },
    created() {
      import('@/assets/js/media_750')
    },
    mounted() {
      this.where.status = this.$route.params.types;
      this.current = "";
      this.getIndex();
      this.$scroll(this.$refs.container, () => {
        !this.loading && this.getIndex();
      });
    },
    methods: {
      more: function(index) {
        if (this.current === index) this.current = "";
        else this.current = index;
      },
      modify: function(item, status) {
        this.change = true;
        this.orderInfo = item;
        this.status = status;
      },
      changeclose: function(msg) {
        this.change = msg;
        this.init()
      },
      // 拒绝退款
      getRefuse(id, reason) {
        orderRefuseApi({ orderNo: id, reason: reason}).then(() =>{
          this.change = false;
          this.$dialog.success("已拒绝退款");
          this.init();
        }).catch((error) => {
          this.$dialog.error(error.message);
        });
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
                required(required.message("金额"))
              ]
            }).validate({ price });
          } catch (e) {
            return validatorDefaultCatch(e);
          }
          data.price = price;
          data.orderNo  = opt.orderId;
          editPriceApi(data).then(() =>{
            that.change = false;
            that.$dialog.success("改价成功");
            that.init();
          }).catch((error) => {
            that.$dialog.error(error.message);
          });
        } else if (that.status == 0 && refundStatus === 1) {
          try {
            await this.$validator({
              refundPrice: [
                required(required.message("金额")),
                num(num.message("金额"))
              ]
            }).validate({ refundPrice });
          } catch (e) {
            return validatorDefaultCatch(e);
          }
          data.amount = refundPrice;
          data.type = opt.type;
          data.orderNo  = opt.orderId;
          orderRefundApi(data).then(
            res => {
              that.change = false;
              that.$dialog.success('退款成功');
              that.init();
            },
            err => {
              that.change = false;
              that.$dialog.error(err.message);
            }
          );
        } else {
          try {
            await this.$validator({
              remark: [required(required.message("备注"))]
            }).validate({ remark });
          } catch (e) {
            return validatorDefaultCatch(e);
          }
          data.mark = remark;
          data.orderNo = opt.orderId;
          orderMarkApi(data).then(
            res => {
              that.change = false;
              that.$dialog.success('提交成功');
              that.init();
            },
            err => {
              that.change = false;
              that.$dialog.error(err.message);
            }
          );
        }
      },
      init: function() {
        this.list = [];
        this.where.page = 1;
        this.loaded = false;
        this.loading = false;
        this.getIndex();
        this.current = "";
      },
      getIndex() {
        if (this.loading || this.loaded) return;
        this.loading = true;
        orderListApi(this.where).then(
          res => {
            this.loading = false;
            this.loaded = res.list.length < this.where.limit;
            this.list.push.apply(this.list, res.list);
            this.where.page = this.where.page + 1;
          },
          err => {
            this.$dialog.error(err.message);
          }
        );
      },
      changeStatus(val) {
        if (this.where.status != val) {
          this.where.status = val;
          this.init();
        }
      },
      toDetail(item) {
        this.$router.push({ path: "/javaMobile/orderDetail/" + item.orderId });
      },
      offlinePay(item) {
        // setOfflinePay({ order_id: item.order_id }).then(
        //   res => {
        //     this.$dialog.success(res.message);
        //     this.init();
        //   },
        //   error => {
        //     this.$dialog.error(error.message);
        //   }
        // );
      }
    }
  };
</script>
<style scoped lang="scss">
  .pos-order-goods{padding:0 0.3rem;background-color: #fff;}
  .pos-order-goods .goods{height:1.85rem;}
  .pos-order-goods .goods~.goods{border-top:1px dashed #e5e5e5;}
  .pos-order-goods .goods .picTxt{width:5.15rem;}
  .pos-order-goods .goods .picTxt .pictrue{width:1.3rem;height:1.3rem;}
  .pos-order-goods .goods .picTxt .pictrue img{width:100%;height:100%;border-radius:0.06rem;}
  .pos-order-goods .goods .picTxt .text{width:3.65rem;height:1.3rem;}
  .pos-order-goods .goods .picTxt .text .info{font-size:0.28rem;color:#282828;}
  .pos-order-goods .goods .picTxt .text .attr{font-size:0.2rem;color:#999;height: 0.8rem;
    line-height: 0.8rem;}
  .pos-order-goods .goods .money{width:1.64rem;text-align:right;font-size:0.28rem;height: 1.3rem;}
  .pos-order-goods .goods .money .x-money{color:#282828;}
  .pos-order-goods .goods .money .num{color:#ff9600;margin:0.05rem 0;}
  .pos-order-goods .goods .money .y-money{color:#999;text-decoration:line-through;}
  .pos-order-list{background: #f5f5f5;}
  .pos-order-list .nav{width:100%;height:0.96rem;background-color:#fff;font-size:0.3rem;color:#282828;position:fixed;top:0;left:0;z-index: 66;}
  .pos-order-list .nav .item.on{color:#2291f8;}
  .pos-order-list .list{margin-top:0.2rem;}
  .pos-order-list .list .item{background-color:#fff;width:100%;}
  .pos-order-list .list .item~.item{margin-top:0.24rem;}
  .pos-order-list .list .item .order-num{height:1.24rem;border-bottom:1px solid #eee;font-size:0.3rem;font-weight:bold;color:#282828;padding:0 0.3rem;}
  .pos-order-list .list .item .order-num .time{font-size:0.26rem;font-weight:normal;color:#999;margin-top: -0.4rem;}
  .pos-order-list .list .item .operation{padding:0.2rem 0.3rem;margin-top: 0.03rem;}
  .pos-order-list .list .item .operation .more{position:relative;}
  .pos-order-list .list .item .operation .icon-gengduo{font-size:0.5rem;color:#aaa;}

  .pos-order-list .list .item .operation .order .arrow{width: 0;height: 0;border-left: 0.11rem solid transparent;border-right: 0.11rem solid transparent;border-top: 0.2rem solid #e5e5e5;position:absolute;left: 0.15rem;bottom:-0.18rem;}
  .pos-order-list .list .item .operation .order .arrow:before{content:'';width: 0;height: 0;border-left: 0.07rem solid transparent;border-right: 0.07rem solid transparent;border-top: 0.2rem solid #fff;position:absolute;left:-0.07rem;bottom:0;}
  .pos-order-list .list .item .operation .order{width:2rem;background-color:#fff;border:1px solid #eee;border-radius:0.1rem;position:absolute;top:-1rem;z-index:9;}
  .pos-order-list .list .item .operation .order .items{height:0.77rem;line-height:0.77rem;text-align:center;}
  .pos-order-list .list .item .operation .order .items~.items{border-top:1px solid #f5f5f5;}

  .pos-order-list .list .item .operation .bnt{font-size:0.28rem;color:#5c5c5c;width:1.7rem;height:0.6rem;border-radius:0.3rem;border:1px solid #bbb;text-align:center;line-height:0.6rem;}
  .pos-order-list .list .item .operation .bnt~.bnt{margin-left:0.14rem;}
  .public-total{font-size:0.28rem;color:#282828;border-top:1px solid #eee;height:0.92rem;line-height:0.92rem;text-align:right;padding:0 0.3rem;background-color: #fff;}
  .public-total .money{color:#ff4c3c;}
</style>
