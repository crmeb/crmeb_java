<template>
  <div class="statistical-page" ref="container">
    <div class="navs">
      <div class="list">
        <div
          class="item"
          :class="time == 'today' ? 'on' : ''"
          @click="setTime('today')"
        >
          今天
        </div>
        <div
          class="item"
          :class="time == 'yesterday' ? 'on' : ''"
          @click="setTime('yesterday')"
        >
          昨天
        </div>
        <div
          class="item"
          :class="time == 'lately7' ? 'on' : ''"
          @click="setTime('lately7')"
        >
          最近7天
        </div>
        <div
          class="item"
          :class="time == 'month' ? 'on' : ''"
          @click="setTime('month')"
        >
          本月
        </div>
        <div
          class="item"
          :class="time == 'date' ? 'on' : ''"
          @click="dateTitle"
        >
          <!-- <span class="iconfont icon-xiangxia"></span>
          <span v-for="(value, index) in renderValues" :key="index">
            {{ value }}</span
          > -->
          自定义
        </div>
      </div>
    </div>
    <div class="wrapper">
      <div class="title">
        {{ title }}{{ this.where.type == 1 ? "营业额（元）" : "订单量（份）" }}
      </div>
      <div class="money">{{ time_price }}</div>
      <div class="increase acea-row row-between-wrapper">
        <div>
          {{ title }}增长率：<span
            :class="increase_time_status === 1 ? 'red' : 'green'"
            >{{ increase_time_status === 1 ? "" : "-" }}{{ growth_rate }}%
            <span
              class="iconfont"
              :class="
                increase_time_status === 1
                  ? 'icon-xiangshang1'
                  : 'icon-xiangxia2'
              "
            ></span
          ></span>
        </div>
        <div>
          {{ title }}增长：<span
            :class="increase_time_status === 1 ? 'red' : 'green'"
            >{{ Number(increase_time).toFixed(2) }}
            <span
              class="iconfont"
              :class="
                increase_time_status === 1
                  ? 'icon-xiangshang1'
                  : 'icon-xiangxia2'
              "
            ></span
          ></span>
        </div>
      </div>
    </div>
    <div class="chart">
      <div class="company">
        {{ where.type === 1 ? "单位（元）" : "单位（份）" }}
      </div>
      <ECharts :options="polar"></ECharts>
    </div>
    <!--<div class="public-wrapper">-->
      <!--<div class="title">-->
        <!--<span class="iconfont icon-xiangxishuju"></span>详细数据-->
      <!--</div>-->
      <!--<div class="nav acea-row row-between-wrapper">-->
        <!--<div class="data">日期</div>-->
        <!--<div class="browse">订单量</div>-->
        <!--<div class="turnover">成交额</div>-->
      <!--</div>-->
      <!--<div class="conter">-->
        <!--<div-->
          <!--class="item acea-row row-between-wrapper"-->
          <!--v-for="(item, index) in list"-->
          <!--:key="index"-->
        <!--&gt;-->
          <!--<div class="data">{{ item.time }}</div>-->
          <!--<div class="browse">{{ item.count }}</div>-->
          <!--<div class="turnover">{{ item.price }}</div>-->
        <!--</div>-->
      <!--</div>-->
    <!--</div>-->
    <statistics-data :list="list"></statistics-data>
    <div class="calendar-wrapper" :class="current === true ? 'on' : ''">
      <div class="calendar">
        <Calendar
          :clean="clean"
          :lunar="lunar"
          ref="calendar"
          :range="isrange"
          :multi="ismulti"
          @select="select"
          @next="next"
          @prev="prev"
          :value="value"
          :weekSwitch="weekSwitch"
          :monthRange="monthRange"
          rangeMonthFormat="yyyy-mm-dd"
          monFirst
          responsive
          :begin="[1992, 5, 20]"
          :end="[2049, 5, 20]"
        />
      </div>
    </div>
    <div
      class="maskModel"
      @touchmove.prevent
      v-show="current === true"
      @click="close"
    ></div>
    <Loading :loaded="loaded" :loading="loading"></Loading>
  </div>
</template>
<script>
import statisticsData from "../components/statisticsData";
import ECharts from "vue-echarts";
import "echarts/lib/chart/line";
import "echarts/lib/component/polar";
import Calendar from "mpvue-calendar";
import "mpvue-calendar/src/browser-style.css";
import { statisticsDataApi, orderTimeApi } from '@/api/order';
import { parseTime } from '@/utils';
import Loading from "../components/Loading";
const year = new Date().getFullYear();
const month = new Date().getMonth() + 1;
const day = new Date().getDate();
export default {
  name: "Statistics",
  components: {
    ECharts,
    Calendar,
    Loading,
    statisticsData
  },
  props: {},
  data: function() {
    return {
      polar: {
        tooltip: {
          trigger: "axis"
        },
        legend: {
          data: [""]
        },
        toolbox: {
          show: false,
          feature: {
            mark: { show: true },
            dataView: { show: true, readOnly: false },
            magicType: { show: true, type: ["line"] },
            restore: { show: true },
            saveAsImage: { show: true }
          }
        },
        calculable: true,
        xAxis: [
          {
            type: "category",
            boundaryGap: false,
            data: ["周一", "周二", "周三", "周四", "周五", "周六", "周日"],
            splitLine: {
              show: false
            },
            axisLine: {
              lineStyle: {
                color: "#999",
                width: 1 //这里是为了突出显示加上的
              }
            }
          }
        ],
        yAxis: [
          {
            type: "value",
            splitLine: {
              show: true,
              lineStyle: {
                color: ["#f5f5f5"],
                width: 1,
                type: "solid"
              }
            },
            axisLine: {
              lineStyle: {
                color: "#999",
                width: 1 //这里是为了突出显示加上的
              }
            }
          }
        ],
        series: [
          {
            name: "邮件营销",
            type: "line",
            stack: "总量",
            itemStyle: {
              normal: {
                color: "#2291f8", //折点颜色
                lineStyle: {
                  color: "#2291f8" //折线颜色
                }
              }
            },
            data: [120, 132.5, 101, 134, 90, 150, 30]
          }
        ],
        grid: {
          x: 30,
          x2: 10,
          y: 20,
          y2: 110,
          left: 40
        },
        animationDuration: 2000
      },
      value: [[year, month, day - 1], [year, month, day]],
      isrange: true,
      weekSwitch: false,
      ismulti: false,
      monFirst: true,
      clean: true, //简洁模式
      lunar: false, //显示农历
      renderValues: [],
      monthRange: [],
      current: false,
      where: {
        dateLimit : '',
        type: ''
      },
      types: "", //类型|order=订单数|price=营业额
      time: "", //时间|today=今天|yesterday=昨天|month=本月
      title: "", //时间|today=今天|yesterday=昨天|month=本月
      growth_rate: "", //增长率
      increase_time: "", //增长率
      increase_time_status: "", //增长率
      time_price: "", //增长率
      loaded: false,
      loading: false,
      filter: {
        page: 1,
        limit: 10,
        dateLimit: ""
      },
      list: []
    };
  },
  watch: {
    "$route.params": function(newVal) {
      var that = this;
      if (newVal != undefined) {
        that.setType(newVal.type);
        that.setTime(newVal.time);
        that.getIndex();
      }
    }
  },
  mounted: function() {
    this.handelRenderValues();
    this.setTime(this.$route.params.time);
    this.setType(this.$route.params.type);
    this.getIndex();
    this.getInfo();
    this.$scroll(this.$refs.container, () => {
      !this.loading && this.getInfo();
    });
  },
  computed: {
    monthRangeText() {
      return this.monthRange.length ? "固定" : "指定范围";
    }
  },
  methods: {
    getIndex: function() {
      var that = this;
      orderTimeApi(that.where).then(
        res => {
          var _info = res.chart,
            day = [],
            num = [];
          _info.forEach(function(item) {
            day.push(item.time);
            num.push(item.num);
          });
          that.polar.xAxis[0].data = day;
          that.polar.series[0].data = num;
          that.growth_rate = res.growthRate;
          that.increase_time = res.increaseTime;
          that.increase_time_status = res.increaseTimeStatus;
          that.time_price = res.time;
        },
        error => {
          that.$dialog.error(error.msg);
        }
      );
    },
    setTime: function(time) {
      this.time = time;
      this.where.dateLimit = time
      this.filter.dateLimit = time
      this.list = [];
      this.filter.page = 1;
      this.loaded = false;
      this.loading = false;
      this.getIndex();
      this.getInfo();
    },
    setType: function(type) {
      switch (type) {
        case "price":
          this.where.type = 1;
          break;
        case "order":
          this.where.type = 2;
          break;
      }
    },
    handelRenderValues(data) {
      if (this.ismulti) {
        this.renderValues = this.value.map(v => v.join("-"));
      } else if (this.isrange) {
        const values = [];
        data || this.value;
        this.value.forEach((v, i) => {
          values.push(v.join("-"));
          // if (!i) {
          //   values.push("~");
          // }
        });
        this.renderValues = values;
      } else {
        this.renderValues = [this.value.join("-")];
      }
      this.where.dateLimit = this.renderValues.join(',')
      // this.where.dateLimit = parseTime(this.renderValues[0], '{y}-{m}-{d}')+','+parseTime(this.renderValues[1], '{y}-{m}-{d}')
      this.filter.dateLimit = this.where.dateLimit
    },
    prev(y, m, w) {
      console.log(y, m, w);
    },
    next(year, month, week) {
      console.log(year, month, week);
    },
    selectYear(year) {
    },
    setToday() {
      this.$refs.calendar.setToday();
    },
    dateInfo() {
      const info = this.$refs.calendar.dateInfo(2018, 8, 23);
    },
    renderer() {
      if (this.monthRange.length) {
        this.monthRange = ["2018-08", "2018-08"];
      }
      this.$refs.calendar.renderer(2018, 8); //渲染2018年8月份
    },
    select(val, val2) {
      if (this.isrange) {
        this.handelRenderValues([val, val2]);
      } else if (this.ismulti) {
        this.handelRenderValues(val);
      } else {
        this.handelRenderValues([val]);
      }
      this.list = [];
      this.filter.page = 1;
      this.loaded = false;
      this.loading = false;
      this.time = "date";
      this.title = "";
      // this.getIndex();
      // this.getInfo();
    },
    dateTitle: function() {
      this.current = true;
    },
    close: function() {
      this.current = false;
      this.getIndex();
      this.getInfo();
    },
    getInfo: function() {
      var that = this;
      if (that.loading || that.loaded) return;
      that.loading = true;
      statisticsDataApi(that.filter).then(
        res => {
          that.loading = false;
          that.loaded = res.length < that.filter.limit;
          that.list.push.apply(that.list, res);
          that.filter.page = that.filter.page + 1;
        },
        error => {
          that.$dialog.message(error.msg);
        }
      );
    }
  }
};
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
  .statistical-page {margin-top:-50px;}
  .statistical-page .navs{width:100%;height:0.96rem;background-color:#fff;overflow:hidden;line-height:0.96rem;position:fixed;top:0;left:0;z-index:9;}
  .statistical-page .navs .list{overflow-y:hidden;overflow-x:auto;white-space: nowrap;-webkit-overflow-scrolling: touch;
    width: 100%;}
  .statistical-page .navs .item{font-size:0.32rem;color:#282828;margin-left:0.6rem;display: inline-block;}
  .statistical-page .navs .item.on{color:#2291f8;}
  .statistical-page .navs .item .iconfont{font-size:0.25rem;margin-left:0.13rem;}
  .statistical-page .wrapper{width:7.4rem;background-color:#fff;border-radius:0.1rem;margin:0.19rem auto 0 auto;padding:0.5rem 0.6rem;}
  .statistical-page .wrapper .title{font-size:0.3rem;color:#999;text-align:center;}
  .statistical-page .wrapper .money{font-size:0.72rem;color:#fba02a;text-align:center;margin-top:0.1rem;}
  .statistical-page .wrapper .increase{font-size:0.28rem;color:#999;margin-top:0.2rem;}
  .statistical-page .wrapper .increase .red{color:#ff6969;}
  .statistical-page .wrapper .increase .green{color:#1abb1d;}
  .statistical-page .wrapper .increase .iconfont{font-size:0.23rem;margin-left:0.15rem;}
  .statistical-page .chart{width:6.9rem;height:4.8rem;background-color:#fff;border-radius:0.1rem;margin:0.23rem auto 0 auto;padding: 0.25rem 0.22rem 0 0.22rem;}
  .statistical-page .chart .company{font-size:0.26rem;color:#999;}
  .statistical-page .mc-body{padding-bottom:0;}
  .statistical-page .mc-body tr{background-color: #edf8fe;border-top: 1px solid #fff;width:100%;}
.echarts {
  width: 100%;
  height: 5.5rem;
}
.calendar-wrapper {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 777;
  transform: translate3d(0, 100%, 0);
  transition: all 0.3s cubic-bezier(0.25, 0.5, 0.5, 0.9);
}
.calendar-wrapper.on {
  transform: translate3d(0, 0, 0);
}
.statistical-page .wrapper .increase {
  font-size: 0.26rem;
}
.statistical-page .wrapper .increase .iconfont {
  margin-left: 0;
}
</style>
