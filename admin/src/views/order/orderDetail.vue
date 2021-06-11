<template>
  <div>
    <el-dialog
      title="订单信息"
      :visible.sync="dialogVisible"
      width="700px"
      v-if="orderDatalist"
    >
      <div class="description" v-loading="loading">
        <div class="title">用户信息</div>
        <div class="acea-row">
          <div class="description-term">用户昵称：{{orderDatalist.realName}}</div>
          <div class="description-term">绑定电话：{{orderDatalist.phone ? orderDatalist.phone : '无'}}</div>
        </div>
        <el-divider></el-divider>
        <div class="title">{{orderDatalist.statusStr.key === 'toBeWrittenOff'?'提货信息': '收货信息'}}</div>
        <div class="acea-row">
          <div class="description-term">{{orderDatalist.statusStr.key === 'toBeWrittenOff'?'提货人': '收货人'}}：{{orderDatalist.realName}}</div>
          <div class="description-term">{{orderDatalist.statusStr.key === 'toBeWrittenOff'?'提货电话': '收货电话'}}：{{orderDatalist.userPhone}}</div>
          <div class="description-term" v-if="orderDatalist.statusStr.key !== 'toBeWrittenOff'">{{orderDatalist.statusStr.key === 'toBeWrittenOff'?'提货地址': '收货地址'}}：{{orderDatalist.userAddress}}</div>
        </div>
        <el-divider></el-divider>
        <div class="title">订单信息</div>
        <div class="acea-row">
          <div class="description-term">订单编号：{{orderDatalist.orderId}}</div>
          <div class="description-term" style="color: red">订单状态：{{orderDatalist.statusStr.value}}</div>
          <div class="description-term">商品总数：{{orderDatalist.totalNum}}</div>
          <div class="description-term">商品总价：{{orderDatalist.proTotalPrice}}</div>
          <div class="description-term">支付邮费：{{orderDatalist.payPostage}}</div>
          <div class="description-term">优惠券金额：{{orderDatalist.couponPrice}}</div>
          <div class="description-term">实际支付：{{orderDatalist.payPrice}}</div>
          <div class="description-term fontColor3" v-if="orderDatalist.refundPrice">退款金额：{{orderDatalist.refundPrice}}</div>
          <div class="description-term" v-if="orderDatalist.useIntegral">使用积分：{{orderDatalist.useIntegral}}</div>
          <div class="description-term" v-if="orderDatalist.backIntegral">退回积分：{{orderDatalist.backIntegral}}</div>
          <div class="description-term">创建时间：{{orderDatalist.createTime}}</div>
          <div class="description-term">支付方式：{{orderDatalist.payTypeStr}}</div>
          <div class="description-term">推广人：{{orderDatalist.spreadName | filterEmpty}}</div>
          <div class="description-term" v-if="orderDatalist.shippingType === 2 && orderDatalist.statusStr.key === 'notShipped'">门店名称：{{orderDatalist.storeName}}</div>
          <div class="description-term" v-if="orderDatalist.shippingType === 2 && orderDatalist.statusStr.key === 'notShipped'">核销码：{{orderDatalist.user_phone}}</div>
          <div class="description-term">商家备注：{{orderDatalist.remark}}</div>
          <template v-if="orderDatalist.statusStr.key === 'toBeWrittenOff' && orderDatalist.systemStore">
            <div class="description-term">提货码：{{orderDatalist.verifyCode}}</div>
            <div class="description-term">门店名称：{{orderDatalist.systemStore.name}}</div>
            <div class="description-term">门店电话：{{orderDatalist.systemStore.phone}}</div>
            <div class="description-term">门店地址：{{orderDatalist.systemStore.address + orderDatalist.systemStore.detailedAddress}}</div>
          </template>

        </div>
        <template v-if="orderDatalist.deliveryType === 'express'">
          <el-divider></el-divider>
          <div class="title">物流信息</div>
          <div class="acea-row">
            <div class="description-term">快递公司：{{orderDatalist.deliveryName}}</div>
            <div class="description-term">快递单号：{{orderDatalist.deliveryId}}<el-button type="primary" size="mini" @click="openLogistics" style="margin-left: 5px">物流查询</el-button></div>
          </div>
        </template>
        <template v-if="orderDatalist.deliveryType === 'send'">
          <el-divider></el-divider>
          <div class="title">配送信息</div>
          <div class="acea-row">
            <div class="description-term">送货人姓名：{{orderDatalist.deliveryName}}</div>
            <div class="description-term">送货人电话：{{orderDatalist.deliveryId}}</div>
          </div>
        </template>
        <template v-if="orderDatalist.mark">
          <el-divider></el-divider>
          <div class="title">用户备注</div>
          <div class="acea-row">
            <div class="description-term">{{orderDatalist.mark}}</div>
          </div>
        </template>
      </div>
    </el-dialog>
    <el-dialog
      v-if="orderDatalist"
      title="提示"
      :visible.sync="modal2"
      width="30%">
      <div class="logistics acea-row row-top">
        <div class="logistics_img"><img src="@/assets/imgs/expressi.jpg"></div>
        <div class="logistics_cent">
          <span class="mb10">物流公司：{{orderDatalist.deliveryName}}</span>
          <span>物流单号：{{orderDatalist.deliveryId}}</span>
        </div>
      </div>
      <div class="acea-row row-column-around trees-coadd">
        <div class="scollhide">
          <el-timeline :reverse="reverse">
            <el-timeline-item v-for="(item,i) in result" :key="i">
              <p class="time" v-text="item.time"></p>
              <p class="content" v-text="item.status"></p>
            </el-timeline-item>
          </el-timeline >
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="modal2 = false">关闭</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<script>0
import { orderDetailApi, getLogisticsInfoApi } from '@/api/order'
export default {
  name: 'OrderDetail',
  props: {
    orderId: {
      type: String,
      default: 0
    }
  },
  data() {
    return {
      reverse: true,
      dialogVisible: false,
      orderDatalist: null,
      loading: false,
      modal2: false,
      result: []
    }
  },
  mounted() {
  },
  methods: {
    openLogistics () {
      this.getOrderData()
      this.modal2 = true;
    },
    // 获取订单物流信息
    getOrderData () {
      getLogisticsInfoApi({orderNo:this.orderId}).then(async res => {
        this.result = res.list;
      })
    },
    getDetail(id) {
      this.loading = true
      orderDetailApi({orderNo: id}).then(res => {
        this.orderDatalist = res
        this.loading = false
      }).catch(() => {
        this.orderDatalist = null
        this.loading = false
      })
    }
  }
}
</script>

<style scoped lang="scss">
  .logistics {
    align-items: center;
    padding: 10px 0px;
    .logistics_img {
      width: 45px;
      height: 45px;
      margin-right: 12px;
      img {
        width: 100%;
        height: 100%;
      }
    }
    .logistics_cent {
      span {
        display: block;
        font-size: 12px;
      }
    }
  }

  .trees-coadd {
    width: 100%;
    height: 400px;
    border-radius: 4px;
    overflow: hidden;
    .scollhide {
      width: 100%;
      height: 100%;
      overflow: auto;
      margin-left: 18px;
      padding: 10px 0 10px 0;
      box-sizing: border-box;
      .content {
        font-size: 12px;
      }

      .time {
        font-size: 12px;
        color: #2d8cf0;
      }

    }
  }

  .title {
    margin-bottom: 10px;
    color: #17233d;
    font-weight: 500;
    font-size: 14px;
  }

  .description {
    &-term {
      display: table-cell;
      padding-bottom: 5px;
      line-height: 20px;
      width: 50%;
      font-size: 12px;
    }
    /deep/ .el-divider--horizontal {
      margin: 12px 0 !important;
    }
  }

</style>
