<template>
  <div class="deliver-goods">
    <header>
      <div class="order-num acea-row row-between-wrapper">
        <div class="num line1">订单号：{{ orderId }}</div>
        <div class="name line1">
          <span class="iconfont icon-yonghu2"></span>{{ delivery.nickname }}
        </div>
      </div>
      <div class="address">
        <div class="name">
          {{ delivery.realName
          }}<span class="phone">{{ delivery.userPhone }}</span>
        </div>
        <div>{{ delivery.userAddress }}</div>
      </div>
      <div class="line"><img src="../../../assets/imgs/line.jpg" /></div>
    </header>
    <div class="wrapper">
      <div class="item acea-row row-between-wrapper">
        <div>发货方式</div>
        <div class="mode acea-row row-middle row-right">
          <div
            class="goods"
            :class="active === index ? 'on' : ''"
            v-for="(item, index) in types"
            :key="index"
            @click="changeType(item, index)"
          >
            {{ item.title }}<span class="iconfont icon-xuanzhong2"></span>
          </div>
        </div>
      </div>
      <div class="list" v-show="active === 0">
        <div class="item acea-row row-between-wrapper">
          <div>发货方式</div>
          <select class="mode" v-model="expressCode">
            <option value="">选择快递公司</option>
            <option
              :value="item.code"
              v-for="(item, index) in express"
              :key="index"
              >{{ item.name }}</option
            >
          </select>
          <span class="iconfont icon-up"></span>
        </div>
        <div class="item acea-row row-between-wrapper">
          <div>快递单号</div>
          <input
            type="text"
            placeholder="填写快递单号"
            v-model="expressNumber"
            class="mode"
          />
        </div>
      </div>
      <div class="list" v-show="active === 1">
        <div class="item acea-row row-between-wrapper">
          <div>送货人</div>
          <input
            type="text"
            placeholder="填写送货人"
            v-model="deliveryName"
            class="mode"
          />
        </div>
        <div class="item acea-row row-between-wrapper">
          <div>送货电话</div>
          <input
            type="text"
            placeholder="填写送货电话"
            v-model="deliveryTel"
            class="mode"
          />
        </div>
      </div>
    </div>
    <div style="height:1.2rem;"></div>
    <div class="confirm" @click="saveInfo">确认提交</div>
  </div>
</template>
<script>
// import { getAdminOrderDelivery, setAdminOrderDelivery } from "../../api/admin";
import { orderSendApi, orderDetailApi } from '@/api/order';
import { expressList } from '@/api/logistics';
import { required, num } from "@/utils/validate";
import { validatorDefaultCatch } from "@/libs/dialog";
export default {
  name: "GoodsDeliver",
  components: {},
  props: {},
  data: function() {
    return {
      types: [
        {
          type: "1",
          title: "发货"
        },
        {
          type: "2",
          title: "送货"
        },
        {
          type: "3",
          title: "无需发货"
        }
      ],
      active: 0,
      orderId: "",
      delivery: {},
      express: [],
      type: "1",
      deliveryName: "",
      expressCode: "",
      expressNumber: '',
      deliveryTel: ""
    };
  },
  watch: {
    "$route.params.oid": function(newVal) {
      let that = this;
      if (newVal != undefined) {
        that.orderId = newVal;
        that.getIndex();
      }
    }
  },
  created() {
    import('@/assets/js/media_750')
  },
  mounted: function() {
    this.orderId = this.$route.params.oid;
    this.getIndex();
    this.getLogistics();
  },
  methods: {
    changeType(item, index) {
      this.active = index;
      this.type = item.type;
      this.deliveryName = "";
      this.deliveryTel = "";
      this.expressCode = "";
      this.expressNumber = "";
    },
    getIndex() {
      orderDetailApi({ orderNo: this.orderId }).then(res => {
        this.delivery = res
      }).catch((error)=>{
        this.$dialog.error(error.message);
      })
    },
    getLogistics() {
      expressList({ page: 1, limit: 999, isShow:1 }).then(async res => {
        this.express = res.list
      })
    },
    async saveInfo() {
      // type: '1',
      //   expressRecordType: '1',
      //   expressId: '',
      //   expressCode: '',
      //   deliveryName: '',
      //   deliveryTel: '',
      //   // expressName: '',
      //   expressNumber: '',
      //   expressTempId: '',
      //   toAddr: '',
      //   toName: '',
      //   toTel: '',
      //   orderNo: ''


      let that = this,
        deliveryName = that.deliveryName,
        deliveryTel = that.deliveryTel,
        save = {};
      save.type = that.type;
      save.orderNo = that.orderId;

      switch (that.type) {
        case "1":
          // try {
          //   await this.$validator({
          //     expressId: [required(required.message("快递公司"))],
          //     expressCode: [required(required.message("快递单号"))]
          //   }).validate({ expressId, expressCode });
          // } catch (e) {
          //   return validatorDefaultCatch(e);
          // }
          if( !that.expressCode ) return that.$dialog.error('请输入快递公司');
          if( !that.expressNumber ) return that.$dialog.error('请输入快递单号');
          save.expressNumber = that.expressNumber;
          save.expressRecordType = 1;
          save.expressCode = that.expressCode;
          that.setInfo(save);
          break;
        case "2":
          try {
            await this.$validator({
              deliveryName: [required(required.message("发货人姓名"))],
              deliveryTel: [required(required.message("发货人电话"))]
            }).validate({ deliveryName, deliveryTel });
          } catch (e) {
            return validatorDefaultCatch(e);
          }
          save.deliveryName = deliveryName;
          save.deliveryTel = deliveryTel;
          that.setInfo(save);
          break;
        case "3":
          that.setInfo(save);
          break;
      }
    },
    setInfo: function(item) {
      let that = this;
      orderSendApi(item).then(
        res => {
          that.$dialog.success('发送货成功');
          that.$router.go(-1);
        },
        error => {
          that.$dialog.error(error.message);
        }
      );
    }
  }
};
</script>
<style scoped lang="scss">
  /*input{*/
    /*line-height: normal; box-sizing:border-box;*/
    /*-webkit-text-size-adjust: none; -webkit-tap-highlight-color: rgba(0, 0, 0, 0); font-family: 'PingFang SC', 'STHeitiSC-Light', 'Helvetica-Light', arial, sans-serif, 'Droid Sans Fallback'; color:#333;*/
    /*outline:none; border:none; -webkit-appearance:none;border-radius: 0; background:none;*/
  /*}*/
  /*input,input[type="search"],button,select,option,textarea,a{ outline:none; border:0; -webkit-appearance:none;border-radius: 0; background:none;-webkit-box-sizing:border-box;box-sizing:border-box;}*/
  /*button, input, select, textarea { font-size:100%; }*/
  .deliver-goods header{width:100%;background-color:#fff;margin-top:0.1rem;}
  .deliver-goods header .order-num{padding:0 0.3rem;border-bottom:1px solid #f5f5f5;height:0.67rem;}
  .deliver-goods header .order-num .num{width:4.3rem;font-size:0.26rem;color:#282828;position:relative;}
  .deliver-goods header .order-num .num:after{position:absolute;content:'';width:1px;height:0.3rem;background-color:#ddd;top:50%;margin-top:-0.15rem;right:0;}
  .deliver-goods header .order-num .name{width:2.6rem;font-size:0.26rem;color:#282828;text-align: center;}
  .deliver-goods header .order-num .name .iconfont{font-size:0.35rem;color:#477ef3;vertical-align:middle;margin-right:0.1rem;}
  .deliver-goods header .address{font-size:0.26rem;color:#868686;background-color:#fff;padding:0.3rem;}
  .deliver-goods header .address .name{font-size:0.3rem;color:#282828;margin-bottom:0.1rem;}
  .deliver-goods header .address .name .phone{margin-left:0.4rem;}
  .deliver-goods header .line{width:100%;height:0.03rem;}
  .deliver-goods header .line img{width:100%;height:100%;display:block;}
  .deliver-goods .wrapper{width:100%;background-color:#fff;}
  .deliver-goods .wrapper .item{border-bottom:1px solid #f0f0f0;padding:0 0.3rem;height:0.96rem;font-size:0.32rem;color:#282828;position:relative;}
  .deliver-goods .wrapper .item .mode{width:4.6rem;height:100%;text-align:right;}
  .deliver-goods .wrapper .item .mode .iconfont{font-size:0.3rem;margin-left:0.13rem;}
  .deliver-goods .wrapper .item .mode .goods~.goods{margin-left:0.3rem;}
  .deliver-goods .wrapper .item .mode .goods{color:#bbb;}
  .deliver-goods .wrapper .item .mode .goods.on{color:#477ef3;}
  .deliver-goods .wrapper .item .icon-up{position:absolute;font-size:0.35rem;color:#2c2c2c;right:0.3rem;}
  .deliver-goods .wrapper .item select{direction: rtl;padding-right:0.6rem;position: relative;z-index: 2;}
  .deliver-goods .wrapper .item input::placeholder{color:#bbb;}
  .deliver-goods .confirm{font-size:0.32rem;color:#fff;width:100%;height:1rem;background-color:#477ef3;text-align:center;line-height:1rem;position:fixed;bottom:0;}
</style>
