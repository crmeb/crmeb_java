<template>
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
        <div class="description-term">收货人：{{orderDatalist.realName}}</div>
        <div class="description-term">联系电话：{{orderDatalist.userPhone}}</div>
        <div class="description-term">收货地址：{{orderDatalist.userAddress}}</div>
      </div>
      <el-divider></el-divider>
      <div class="title">收货信息</div>
      <div class="acea-row">
        <div class="description-term">订单编号：{{orderDatalist.orderId}}</div>
        <div class="description-term">订单状态：{{orderDatalist.status | orderStatusFilter}}</div>
        <div class="description-term">商品总数：{{orderDatalist.totalNum}}</div>
        <div class="description-term">商品总价：{{orderDatalist.totalPrice}}</div>
        <div class="description-term">交付邮费：{{orderDatalist.payPostage}}</div>
        <div class="description-term">优惠券金额：{{orderDatalist.couponPrice}}</div>
        <div class="description-term">实际支付：{{orderDatalist.payPrice}}</div>
        <div class="description-term" v-if="orderDatalist.refundPrice">退款金额：{{orderDatalist.refundPrice}}</div>
        <div class="description-term" v-if="orderDatalist.useIntegral">使用积分：{{orderDatalist.useIntegral}}</div>
        <div class="description-term" v-if="orderDatalist.backIntegral">退回积分：{{orderDatalist.backIntegral}}</div>
        <div class="description-term">创建时间：{{orderDatalist.createTime}}</div>
        <div class="description-term">支付方式：{{orderDatalist.payTypeStr}}</div>
        <div class="description-term">推广人：{{orderDatalist.spreadInfo.id + ' / ' +orderDatalist.spreadInfo.name}}</div>
        <div class="description-term" v-if="orderDatalist.shippingType === 2 && orderDatalist.statusStr.key === 'notShipped'">门店名称：{{orderDatalist.storeName}}</div>
        <div class="description-term" v-if="orderDatalist.shippingType === 2 && orderDatalist.statusStr.key === 'notShipped'">核销码：{{orderDatalist.user_phone}}</div>
        <div class="description-term">商家备注：{{orderDatalist.remark}}</div>
      </div>
      <template v-if="orderDatalist.deliveryType === 'express'">
        <el-divider></el-divider>
        <div class="title">物流信息</div>
        <div class="acea-row">
          <div class="description-term">快递公司：{{orderDatalist.deliveryName}}</div>
          <div class="description-term">快递单号：{{orderDatalist.deliveryId}}</div>
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
</template>

<script>
import { orderDetailApi } from '@/api/order'
export default {
  name: 'OrderDetail',
  props: {
    orderId: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      dialogVisible: false,
      orderDatalist: null,
      loading: false
    }
  },
  mounted() {
  },
  methods: {
    getDetail(id) {
      this.loading = true
      orderDetailApi({id: id}).then(res => {
        this.orderDatalist = res
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style scoped lang="scss">
.title{
  margin-bottom: 16px;
  color: #17233d;
  font-weight: 500;
  font-size: 14px;
}
.description{
  &-term {
    display: table-cell;
    padding-bottom: 10px;
    line-height: 20px;
    width: 50%;
    font-size: 12px;
  }
}

</style>
