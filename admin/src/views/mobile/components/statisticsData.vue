<template>
    <div ref="container">
      <div class="public-wrapper">
        <div class="title">
          <span class="iconfont icon-xiangxishuju"></span>详细数据
        </div>
        <div class="nav acea-row row-between-wrapper">
          <div class="data">日期</div>
          <div class="browse">订单数</div>
          <div class="turnover">成交额</div>
        </div>
        <div class="conter">
          <div
            class="item acea-row row-between-wrapper"
            v-for="(item, index) in list"
            :key="index"
          >
            <div class="data">{{ item.time }}</div>
            <div class="browse">{{ item.count }}</div>
            <div class="turnover">{{ item.price }}</div>
          </div>
        </div>
      </div>
    </div>
</template>

<script>
  import { statisticsDataApi } from '@/api/order';
  export default {
    name: "statisticsData",
    props: {
      list:{
        type: Array,
        default: ()=> []
      }
    },
    components: {
      // Loading
    },
    data() {
      return {
        // list: [],
        where: {
          page: 1,
          limit: 10
        },
        loaded: false,
        loading: false
      }
    },
    created() {
      import('@/assets/js/media_750')
    }
  }
</script>

<style scoped lang="scss">
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
