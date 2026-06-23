<template>
  <div class="divBox" style="padding-top: 0">
    <el-card class="box-card">
      <div class="flex justify-between mb46">
        <div class="header_title">交易概况</div>
        <div class="acea-row">
          <div class="mr-20">
            <el-radio-group
              v-model="dateLimit"
              type="button"
              class="mr20"
              size="small"
              @change="selectChange(dateLimit)"
            >
              <el-radio-button v-for="(item, i) in fromList.fromTxt" :key="i" :label="item.val">{{
                item.text
              }}</el-radio-button>
            </el-radio-group>
            <el-date-picker
              v-model="timeVal"
              size="small"
              type="daterange"
              placement="bottom-end"
              :picker-options="pickerOptions"
              value-format="yyyy-MM-dd"
              format="yyyy-MM-dd"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="onchangeTime"
              style="width: 220px; height: 34px"
              align="right"
            >
            </el-date-picker>
          </div>
          <el-button type="primary" size="small" @click="onSeach">搜索</el-button>
          <el-button type="default" size="small" icon="el-icon-upload2" @click="exports">导出</el-button>
        </div>
      </div>
      <div class="flex flex-wrap mb20" v-if="checkPermi(['admin:statistics:trade:overview'])">
        <div class="infoBox acea-row mb30" v-for="(item, index) in list" :key="index">
          <div class="iconCrl mr15" :class="item.class">
            <i :class="item.icon" class="iconfont" :style="{ color: item.color }"></i>
          </div>
          <div class="info">
            <div class="flex align-center">
              <span class="sp1" v-text="item.name"></span>
              <el-tooltip class="pl10" effect="dark" placement="top-start">
                <i class="el-icon-warning-outline" />
                <div slot="content" v-text="item.info" style="max-width: 400px"></div>
              </el-tooltip>
            </div>
            <span class="sp2" v-if="index === list.length - 1" v-text="item.list.num"></span>
            <span class="sp2" v-else v-text="item.list.num"></span>
            <span class="content-time"
              ><i :class="item.ratio >= 0 ? 'up' : 'down'">{{ item.list.percent }}</i>
              <i
                :color="item.ratio >= 0 ? '#F5222D' : '#39C15B'"
                :class="item.ratio >= 0 ? 'el-icon-caret-top' : 'el-icon-caret-bottom'"
              ></i>
            </span>
          </div>
        </div>
      </div>
      <echarts-new
        :option-data="optionData"
        :styles="style"
        height="100%"
        width="100%"
        v-if="optionData && checkPermi(['admin:statistics:trade:trend'])"
      ></echarts-new>
    </el-card>
  </div>
</template>

<script>
import { tradeOverviewApi, tradeTrendApi } from '@/api/statistic';
import echartsNew from '@/components/echartsNew/index';
import exportExcel from '@/utils/newToExcel.js'; //表格导出方法
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  components: {
    echartsNew,
  },
  data() {
    return {
      spinShow: false,
      grid: {
        xl: 8,
        lg: 8,
        md: 8,
        sm: 24,
        xs: 24,
      },
      options: this.$timeOptions,
      name: '近30天',
      timeVal: [],
      fromList: this.$constants.timeList,
      dateLimit: '',
      dateLimitPram: {},
      list: [],
      optionData: {},
      style: { height: '400px' },
      pickerOptions: this.$timeOptions,
    };
  },
  created() {
    const end = new Date();
    const start = new Date();
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
    this.timeVal = [start, end];
  },
  mounted() {
    this.dateLimit = 'lately7';
    this.dateLimitPram = 'lately7';
    this.getStatistics();
    this.getTrend();
  },
  methods: {
    checkPermi,
    // 具体日期
    onchangeTime(e) {
      this.timeVal = e;
      this.$set(this, 'dateLimitPram', e ? this.timeVal.join(',') : '');
    },
    selectChange(limit) {
      if (limit == '') {
        //昨天的时间
        let day1 = new Date();
        let day2 = new Date();
        day1.setTime(day1.getTime() - 48 * 60 * 60 * 1000);
        day2.setTime(day2.getTime() - 24 * 60 * 60 * 1000);
        let s1 = day1.getFullYear() + '-' + (day1.getMonth() + 1) + '-' + day1.getDate();
        let s2 = day2.getFullYear() + '-' + (day2.getMonth() + 1) + '-' + day2.getDate();
        this.dateLimitPram = `${s1},${s2}`;
        this.getTrend();
        this.getStatistics();
      } else {
        this.dateLimitPram = limit;
        this.getTrend();
        this.getStatistics();
      }
    },
    onSeach() {
      this.getStatistics();
      this.getTrend();
    },
    async exports() {
      let [th, filekey, data, fileName] = [[], [], [], ''];
      let lebData = await this.getExcelData(); //表格主要数据
      if (!fileName) fileName = '交易统计表'; //文件名
      if (!filekey.length) {
        filekey = lebData.filekey;
      }
      if (!th.length) th = lebData.header; //表头
      data = data.concat(lebData.exportData);
      exportExcel(th, filekey, fileName, data);
    },
    getExcelData(excelData) {
      return new Promise((resolve, reject) => {
        tradeTrendApi({ dateLimit: this.dateLimitPram }).then((res) => {
          let header = ['日期/时间', '营业额', '商品支付金额', '充值金额', '支出金额'];
          let filekey = ['date', 'turnover', 'proPayAmount', 'rechargeAmount', 'payoutAmount'];
          let exportData = new Array();
          exportData = res.map((item) => {
            return {
              date: item.date,
              turnover: item.turnover,
              proPayAmount: item.proPayAmount,
              rechargeAmount: item.rechargeAmount,
              payoutAmount: item.payoutAmount,
            };
          });
          resolve({ header, filekey, exportData });
        });
      });
    },
    // 统计
    getStatistics() {
      tradeOverviewApi({ dateLimit: this.dateLimitPram }).then((res) => {
        let list = [
          {
            name: '营业额',
            icon: 'iconyingyee1',
            info: '商品支付金额、充值金额、购买付费会员金额、线下收银金额',
            color: '#1890FF',
            class: 'one',
            list: { num: res.turnover, percent: res.turnoverRatio },
          },
          {
            name: '商品支付金额',
            icon: 'iconshangpinzhifujine1',
            info: '选定条件下，用户购买商品的实际支付金额，包括微信支付、余额支付、支付宝支付、线下支付金额（拼团商品在成团之后计入，线下支付订单在后台确认支付后计入）',
            color: '#A277FF',
            class: 'two',
            list: { num: res.proPayAmount, percent: res.proPayAmountRatio },
          },
          {
            name: '充值金额',
            icon: 'iconchongzhijine',
            info: '选定条件下，用户成功充值的金额',
            color: '#EF9C20',
            class: 'three',
            list: { num: res.rechargeAmount, percent: res.rechargeAmountRatio },
          },
          {
            name: '支出金额',
            icon: 'iconzhichujine1',
            info: '余额支付金额、支付佣金金额、商品退款金额',
            color: '#1BBE6B',
            class: 'four',
            list: { num: res.payoutAmount, percent: res.payoutAmountRatio },
          },
          {
            name: '余额支付金额',
            icon: 'iconyuezhifujine1',
            info: '用户下单时使用余额实际支付的金额',
            color: '#4BCAD5',
            class: 'five',
            list: { num: res.balanceAmount, percent: res.balanceAmountRatio },
          },
          {
            name: '支付佣金金额',
            icon: 'iconzhifuyongjinjine1',
            info: '后台给推广员支付的推广佣金，以实际支付为准',
            color: '#EF9C20',
            class: 'six',
            list: { num: res.payoutBrokerageAmount, percent: res.payoutBrokerageAmountRatio },
          },
          {
            name: '商品退款金额',
            icon: 'iconshangpintuikuanjine',
            info: '用户成功退款的商品金额',
            color: '#1890FF',
            class: 'one',
            list: { num: res.proRefundAmount, percent: res.proRefundAmountRatio },
          },
        ];
        this.list = list.map((item) => {
          return {
            name: item.name,
            icon: item.icon,
            list: item.list,
            info: item.info,
            color: item.color,
            class: item.class,
            ratio: item.list.percent.replace('%', '') / 100,
          };
        });
      });
    },
    // 统计图
    getTrend() {
      tradeTrendApi({ dateLimit: this.dateLimitPram }).then((res) => {
        let xAxis = new Array();
        let series = [
          { name: '营业额', data: [], type: 'line', smooth: 'true', yAxisIndex: 0 },
          { name: '商品支付金额', data: [], type: 'line', smooth: 'true', yAxisIndex: 0 },
          { name: '充值金额', data: [], type: 'line', smooth: 'true', yAxisIndex: 0 },
          { name: '支出金额', data: [], type: 'line', smooth: 'true', yAxisIndex: 0 },
        ];
        xAxis = res.map((item) => {
          return item.date;
        });
        series[0].data = res.map((item) => {
          return item.turnover;
        });
        series[1].data = res.map((item) => {
          return item.proPayAmount;
        });
        series[2].data = res.map((item) => {
          return item.rechargeAmount;
        });
        series[3].data = res.map((item) => {
          return item.payoutAmount;
        });
        this.spinShow = true;
        let legend = series.map((item) => {
          return item.name;
        });
        let col = ['#B37FEB', '#FFAB2B', '#1890FF', '#00C050'];
        series.map((item, index) => {
          item.itemStyle = {
            normal: {
              color: col[index],
            },
          };
        });
        this.optionData = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross',
              label: {
                backgroundColor: '#6a7985',
              },
            },
          },
          legend: {
            x: 'center',
            data: legend,
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true,
          },
          toolbox: {
            feature: {
              saveAsImage: {},
            },
          },
          xAxis: {
            type: 'category',
            boundaryGap: true,
            // axisTick:{
            //     show:false
            // },
            // axisLine:{
            //     show:false
            // },
            // splitLine: {
            //     show: false
            // },
            axisLabel: {
              interval: 0,
              rotate: 40,
              textStyle: {
                color: '#000000',
              },
            },
            data: xAxis,
          },
          yAxis: {
            type: 'value',
            axisLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            axisLabel: {
              textStyle: {
                color: '#7F8B9C',
              },
            },
            splitLine: {
              show: true,
              lineStyle: {
                color: '#F5F7F9',
              },
            },
          },
          series: series,
        };
        this.spinShow = false;
      });
    },
  },
};
</script>

<style scoped lang="scss">
.one {
  background: rgba(24, 144, 255, 0.1);
}

.two {
  background: rgba(162, 119, 255, 0.1);
}

.three {
  background: rgba(232, 182, 0, 0.1);
}

.four {
  background: rgba(27, 190, 107, 0.1);
}

.five {
  background: rgba(75, 202, 213, 0.1);
}

.six {
  background: rgba(239, 156, 32, 0.1);
}

.flex {
  display: flex;
}

.flex-wrap {
  flex-wrap: wrap;
}

.mb20 {
  margin-bottom: 20px;
}

.mb30 {
  margin-bottom: 30px;
}

.mb46 {
  margin-bottom: 46px;
}

.mr-20 {
  margin-right: 20px;
}

.pl10 {
  padding-left: 10px;
}

.justify-between {
  justify-content: space-between;
}

.align-center {
  align-items: center;
}

.up,
.el-icon-caret-top {
  color: #f5222d;
  font-size: 12px;
  opacity: 1 !important;
}

.down,
.el-icon-caret-bottom {
  color: #39c15b;
  font-size: 12px;
}

.curP {
  cursor: pointer;
}

.header_title {
  font-size: 16px;
  font-family: PingFangSC-Semibold, PingFang SC;
  font-weight: 600;
  color: #000000;
  padding-left: 8px;
  position: relative;

  &::before {
    position: absolute;
    content: '';
    width: 2px;
    height: 18px;
    background: #1890ff;
    top: 0;
    left: 0;
  }
}

.iconfont {
  font-size: 23px;
}

.iconCrl {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  text-align: center;
  line-height: 50px;
}

.lan {
  background: #1890ff;
}

.iconshangpinliulanliang {
  color: #fff;
}

.infoBox {
  width: 25%;
  height: 87px;

  @media screen and (max-width: 1300px) {
    width: 25%;
  }

  @media screen and (max-width: 1200px) {
    width: 33%;
  }

  @media screen and (max-width: 900px) {
    width: 50%;
  }
}

.info {
  .sp1 {
    color: #666;
    font-size: 14px;
    display: block;
    font-size: 14px;
  }

  .sp2 {
    font-weight: 600;
    font-family: PingFangSC-Semibold, PingFang SC;
    font-size: 28px;
    color: rgba(0, 0, 0, 0.85);
    display: inline-block;
    // padding:10px 0 10px;
    margin: 6px 0 10px;
  }

  .content-time {
    margin-left: 10px;
  }
}
</style>
