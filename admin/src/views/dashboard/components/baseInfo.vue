<template>
  <div class="divBox">
    <el-row :gutter="24" class="baseInfo">
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="12">
          <div slot="header" class="acea-row row-between-wrapper">
            <span>销售额</span>
            <el-tag type="success">昨日</el-tag>
          </div>
          <div class="content" v-if="sales">
            <span class="content-number spBlock mb15">{{ sales.count }}</span>
            <div>
              <span class="content-time mr20">日同比：<i class="content-is" :class="Number(sales.dayRate)>=0?'up':'down'">{{ sales.dayRate }}</i><i :class="Number(sales.dayRate)>=0?'el-icon-caret-top':'el-icon-caret-bottom'" /></span>
              <span class="content-time">周同比：<i class="content-is" :class="Number(sales.weekRate)>=0?'up':'down'">{{ sales.weekRate }}</i><i :class="Number(sales.weekRate)>=0?'el-icon-caret-top':'el-icon-caret-bottom'" /></span>
            </div>
            <el-divider></el-divider>
            <div class="acea-row row-between-wrapper">
              <span class="content-time">总销售额</span>
              <span>{{sales.total}} 元</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="12">
          <div slot="header" class="acea-row row-between-wrapper">
            <span>用户量访问</span>
            <el-tag type="success">昨日</el-tag>
          </div>
          <div class="content" v-if="views">
            <span class="content-number spBlock mb15">{{ views.count }}</span>
            <div>
              <span class="content-time mr20">日同比：<i class="content-is" :class="Number(views.dayRate)>=0?'up':'down'">{{ views.dayRate }}</i><i :class="Number(views.dayRate)>=0?'el-icon-caret-top':'el-icon-caret-bottom'" /></span>
              <span class="content-time">周同比：<i class="content-is" :class="Number(views.weekRate)>=0?'up':'down'">{{ views.weekRate }}</i><i :class="Number(views.weekRate)>=0?'el-icon-caret-top':'el-icon-caret-bottom'" /></span>
            </div>
            <el-divider></el-divider>
            <div class="acea-row row-between-wrapper">
              <span class="content-time">总访问量</span>
              <span>{{ views.total }} Pv</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="12">
          <div slot="header" class="acea-row row-between-wrapper">
            <span>订单量</span>
            <el-tag type="success">昨日</el-tag>
          </div>
          <div class="content" v-if="order">
            <span class="content-number spBlock mb15">{{ order.count }}</span>
            <div>
              <span class="content-time mr20">日同比：<i class="content-is" :class="Number(order.dayRate)>=0?'up':'down'">{{ order.dayRate }}</i><i :class="Number(order.dayRate)>=0?'el-icon-caret-top':'el-icon-caret-bottom'" /></span>
              <span class="content-time">周同比：<i class="content-is" :class="Number(order.weekRate)>=0?'up':'down'">{{ order.weekRate }}</i><i :class="Number(order.weekRate)>=0?'el-icon-caret-top':'el-icon-caret-bottom'" /></span>
            </div>
            <el-divider></el-divider>
            <div class="acea-row row-between-wrapper">
              <span class="content-time">总订单量</span>
              <span>{{ order.total }} 单</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="12">
          <div slot="header" class="acea-row row-between-wrapper">
            <span>新增用户</span>
            <el-tag type="success">昨日</el-tag>
          </div>
          <div class="content" v-if="user">
            <span class="content-number spBlock mb15">{{ user.count }}</span>
            <div>
              <span class="content-time mr20">日同比：<i class="content-is" :class="Number(user.dayRate)>=0?'up':'down'">{{ user.dayRate }}</i><i :class="Number(user.dayRate)>=0?'el-icon-caret-top':'el-icon-caret-bottom'" /></span>
              <span class="content-time">周同比：<i class="content-is" :class="Number(user.weekRate)>=0?'up':'down'">{{ user.weekRate }}</i><i :class="Number(user.weekRate)>=0?'el-icon-caret-top':'el-icon-caret-bottom'" /></span>
            </div>
            <el-divider></el-divider>
            <div class="acea-row row-between-wrapper">
              <span class="content-time">总用户</span>
              <span>{{ user.total }} 人</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
  import {statisticsOrderApi, statisticsSalesApi, statisticsUserApi, statisticsViewsApi} from '@/api/dashboard'
  export default {
    data() {
      return {
        infoList: [],
        grid: {
          xl: 6,
          lg: 6,
          md: 12,
          sm: 12,
          xs: 24
        },
        excessStyle: {
          color: '#f56a00',
          backgroundColor: '#fde3cf'
        },
        avatarList: [],
        sales: null,
        order: null,
        user: null,
        views: null
      }
    },
    methods: {
      // 订单量
      statisticsOrder() {
        statisticsOrderApi().then(async res => {
          this.order = res
        })
      },
      // 销售额
      statisticsSales() {
        statisticsSalesApi().then(async res => {
          this.sales = res
        })
      },
      // 新增用户
      statisticsUser() {
        statisticsUserApi().then(async res => {
          this.user = res
        })
      },
      // 用户访问量
      statisticsViews() {
        statisticsViewsApi().then(async res => {
          this.views = res
        })
      }
    },
    mounted() {
      this.statisticsOrder();
      this.statisticsSales();
      this.statisticsUser();
      this.statisticsViews();
    }
  }
</script>
<style scoped lang="scss">
  .ivu-mb{
    margin-bottom: 10px;
  }
  .up, .el-icon-caret-top {
    color: #F5222D;
    font-size: 12px;
    opacity: 1 !important;
  }

  .down, .el-icon-caret-bottom {
    color: #39C15B;
    font-size: 12px;
    /*opacity: 100% !important;*/
  }

  .baseInfo {
    /deep/ .el-card__header {
      padding: 15px 20px !important;
    }
  }

  .content {
    &-number {
      font-size: 30px;
    }
    &-time{
      font-size:14px;
      /*color: #8C8C8C;*/
    }
  }
</style>
