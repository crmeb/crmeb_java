<template>
  <div class="order-index" ref="container">
    <div class="header acea-row">
      <router-link class="item" :to="'/javaMobile/orderList/unPaid'">
        <div class="num">{{ census.unpaidCount }}</div>
        <div>待付款</div>
      </router-link>
      <router-link class="item" :to="'/javaMobile/orderList/notShipped'">
        <div class="num">{{ census.unshippedCount }}</div>
        <div>待发货</div>
      </router-link>
      <router-link class="item" :to="'/javaMobile/orderList/spike'">
        <div class="num">{{ census.receivedCount }}</div>
        <div>待收货</div>
      </router-link>
      <router-link class="item" :to="'/javaMobile/orderList/toBeWrittenOff'">
        <div class="num">{{ census.verificationCount }}</div>
        <div>待核销</div>
      </router-link>
      <router-link class="item" :to="'/javaMobile/orderList/refunding'">
        <div class="num">{{ census.refundCount }}</div>
        <div>退款</div>
      </router-link>
    </div>
    <div class="wrapper">
      <div class="title">
        <span class="iconfont icon-shujutongji"></span>数据统计
      </div>
      <div class="list acea-row">
        <router-link class="item" :to="'/javaMobile/orderStatisticsDetail/price/today'">
          <div class="num">{{ census.todayPrice }}</div>
          <div>今日成交额</div>
        </router-link>
        <router-link class="item" :to="'/javaMobile/orderStatisticsDetail/price/yesterday'">
          <div class="num">{{ census.proPrice }}</div>
          <div>昨日成交额</div>
        </router-link>
        <router-link class="item" :to="'/javaMobile/orderStatisticsDetail/price/month'">
          <div class="num">{{ census.monthPrice }}</div>
          <div>本月成交额</div>
        </router-link>
        <router-link class="item" :to="'/javaMobile/orderStatisticsDetail/order/today'">
          <div class="num">{{ census.todayCount }}</div>
          <div>今日订单数</div>
        </router-link>
        <router-link class="item" :to="'/javaMobile/orderStatisticsDetail/order/yesterday'">
          <div class="num">{{ census.proCount }}</div>
          <div>昨日订单数</div>
        </router-link>
        <router-link class="item" :to="'/javaMobile/orderStatisticsDetail/order/month'">
          <div class="num">{{ census.monthCount }}</div>
          <div>本月订单数</div>
        </router-link>
      </div>
    </div>
    <statistics-data :list="list"></statistics-data>
    <Loading :loaded="loaded" :loading="loading"></Loading>
  </div>
</template>

<script>
  import {orderStatisticsApi, statisticsDataApi } from '@/api/order';
  import statisticsData from "../components/statisticsData";
  import Loading from "../components/Loading";
  export default {
    name: "OrderIndex",
    components: {
      Loading,
      statisticsData
    },
    props: {},
    data: function() {
      return {
        census: [],
        list: [],
        where: {
          page: 1,
          limit: 10
        },
        loaded: false,
        loading: false
      };
    },
    created() {
      import('@/assets/js/media_750')
    },
    mounted: function() {
      this.getIndex();
      this.getList();
      this.$scroll(this.$refs.container, () => {
        !this.loading && this.getList();
      });
    },
    methods: {
      getIndex() {
        orderStatisticsApi().then(
          res => {
            this.census = res
          },
          err => {
            this.$dialog.message(err.message);
          }
        );
      },
      getList() {
        if (this.loading || this.loaded) return;
        this.loading = true;
        statisticsDataApi(this.where).then(
          res => {
            this.loading = false;
            this.loaded = res.length < this.where.limit;
            this.list.push.apply(this.list, res);
            this.where.page = this.where.page + 1;
          },
          error => {
            this.$dialog.message(error.message);
          },
          300
        );
      }
    }
  };
</script>
<style scoped lang="scss">
  .order-index{
    background: #f5f5f5;
    margin-top: -50px;
  }
  .order-index .header{background:url("../../../assets/imgs/orderIndex.png") no-repeat;background-size:100% 100%;width:100%;height:3.02rem;padding:0.45rem 0.3rem 0 0.3rem;}
  .order-index .header .item{flex:1;-webkit-flex:1;-o-flex:1;-ms-flex:1;text-align:center;font-size:0.24rem;color:#fff;}
  .order-index .header .item .num{font-size:0.4rem;margin-bottom:0.07rem;}
  .order-index .wrapper{width:6.9rem;background-color:#fff;border-radius:0.1rem;margin:-1.15rem auto 0 auto;padding-top:0.25rem;}
  .order-index .wrapper .title{font-size:0.3rem;color:#282828;padding:0 0.3rem;margin-bottom:0.4rem;}
  .order-index .wrapper .title .iconfont{color:#2291f8;font-size:0.4rem;margin-right:0.13rem;vertical-align:middle;}
  .order-index .wrapper .list .item{width:33.33%;text-align:center;font-size:0.24rem;color:#999;margin-bottom:0.45rem;}
  .order-index .wrapper .list .item .num{font-size:0.4rem;color:#333;}
  .public-wrapper .title{font-size:0.3rem;color:#282828;padding:0 0.3rem;margin-bottom:0.2rem;}
  .public-wrapper .title .iconfont{color:#2291f8;font-size:0.4rem;margin-right:0.13rem;vertical-align:middle;}
  .public-wrapper{margin:0.18rem auto 0 auto;width:6.9rem;background-color:#fff;border-radius:0.1rem;padding-top:0.25rem;}
  .public-wrapper .nav{padding:0 0.3rem;height:0.7rem;line-height:0.7rem;font-size:0.24rem;color:#999;}
  .public-wrapper .data{width:2.1rem;text-align:left;}
  .public-wrapper .browse{width:1.92rem;text-align:right;}
  .public-wrapper .turnover{width:2.27rem;text-align:right;}
  .public-wrapper .conter{padding:0 0.3rem;}
  .public-wrapper .conter .item{border-bottom:1px solid #f7f7f7;height:0.7rem;font-size:0.24rem;}
  .public-wrapper .conter .item .turnover{color:#d84242;}
</style>
