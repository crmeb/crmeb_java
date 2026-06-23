<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="flex justify-between mb46">
        <div class="header_title">商品概况</div>
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
          <el-button type="default" size="small" icon="el-icon-upload2" @click="exports"> 导出</el-button>
        </div>
      </div>
      <div class="flex flex-wrap mb20" v-if="checkPermi(['admin:statistics:product:data'])">
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
        v-if="optionData && checkPermi(['admin:statistics:product:trend'])"
      ></echarts-new>
    </el-card>
  </div>
</template>

<script>
import echartsNew from '@/components/echartsNew/index';
import exportExcel from '@/utils/newToExcel.js';
import { productDataApi, productTrendApi } from '@/api/statistic';
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
      pickerOptions: this.$timeOptions, //快捷时间选项
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
      this.dateLimit = e ? this.timeVal.join(',') : '';
      this.dateLimitPram = e ? this.timeVal.join(',') : '';
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
      if (!fileName) fileName = '商品统计表'; //文件名
      if (!filekey.length) {
        filekey = lebData.filekey;
      }
      if (!th.length) th = lebData.header; //表头
      data = data.concat(lebData.exportData);
      exportExcel(th, filekey, fileName, data);
    },
    getExcelData(excelData) {
      return new Promise((resolve, reject) => {
        productTrendApi({ dateLimit: this.dateLimitPram }).then((res) => {
          let header = ['日期/时间', '浏览量', '新增商品数量', '收藏量', '加购件数', '下单商品数', '交易成功商品数']; //表头
          let filekey = [
            'date',
            'pageView',
            'addProductNum',
            'collectNum',
            'addCartNum',
            'orderProductNum',
            'orderSuccessProductNum',
          ]; //表头每一项对应的key
          let exportData = new Array();
          exportData = res.map((item) => {
            return {
              date: item.date,
              pageView: item.pageView,
              addProductNum: item.addProductNum,
              collectNum: item.collectNum,
              addCartNum: item.addCartNum,
              orderProductNum: item.orderProductNum,
              orderSuccessProductNum: item.orderSuccessProductNum,
            };
          });
          resolve({ header, filekey, exportData });
        });
      });
    },
    // 统计
    getStatistics() {
      productDataApi({ dateLimit: this.dateLimitPram }).then((res) => {
        var list = [
          {
            name: '新增量',
            icon: 'iconxinzengliang',
            color: '#1890FF',
            class: 'one',
            info: '在选定条件下，新增加的商品之和',
            list: { num: res.newProductNum, percent: res.newProductNumRatio },
          },
          {
            name: '浏览量',
            icon: 'iconliulanliang',
            color: '#A277FF',
            class: 'two',
            info: '在选定条件下，所有商品详情页被访问的次数，一个人在统计时间内访问多次记为多次',
            list: { num: res.pageView, percent: res.pageViewRatio },
          },
          {
            name: '收藏量',
            icon: 'iconshoucangliang',
            color: '#EF9C20',
            class: 'three',
            info: '在选定条件下，被收藏的商品数量之和',
            list: { num: res.collectNum, percent: res.collectNumRatio },
          },
          {
            name: '加购量',
            icon: 'iconjiagouliang',
            color: '#1BBE6B',
            class: 'four',
            info: '在选定条件下，添加商品进入购物车的商品件数',
            list: { num: res.addCartNum, percent: res.addCartNumRatio },
          },
          {
            name: '交易总件数',
            icon: 'iconjiaoyijianshu',
            color: '#4BCAD5',
            class: 'five',
            info: '在选定条件下，成功下单的商品件数之和',
            list: {
              num: res.orderProductNum,
              percent: res.orderProductNumRatio,
            },
          },
          {
            name: '交易成功数',
            icon: 'iconjiaoyichenggongshu',
            color: '#EF9C20',
            class: 'six',
            info: '在选定条件下，交易成功的商品件数之和',
            list: {
              num: res.orderSuccessProductNum,
              percent: res.orderSuccessProductNumRatio,
            },
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
    getTrend() {
      productTrendApi({ dateLimit: this.dateLimitPram }).then((res) => {
        let xAxis = new Array();
        let series = [
          {
            name: '商品浏览量',
            data: [],
            type: 'line',
            smooth: 'true',
            yAxisIndex: 0,
          },
          {
            name: '收藏数量',
            data: [],
            type: 'line',
            smooth: 'true',
            yAxisIndex: 0,
          },
          {
            name: '交易总件数',
            data: [],
            type: 'line',
            smooth: 'true',
            yAxisIndex: 0,
          },
        ];
        xAxis = res.map((item) => {
          return item.date;
        });
        series[0].data = res.map((item) => {
          return item.pageView;
        });
        series[1].data = res.map((item) => {
          return item.collectNum;
        });
        series[2].data = res.map((item) => {
          return item.orderSuccessProductNum;
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
            axisLabel: {
              interval: 0,
              rotate: 40,
              textStyle: {
                color: '#000000',
              },
            },
            data: xAxis,
          },
          yAxis: [
            {
              type: 'value',
              name: '数量',
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
          ],
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
.mb46 {
  margin-bottom: 46px;
}
.mb30 {
  margin-bottom: 30px;
}
.mr-20 {
  margin-right: 20px;
}
.pl10 {
  padding-left: 10px;
}
.mr-10 {
  margin-right: 10px;
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
  font-size: 13px;
  opacity: 1 !important;
}

.down,
.el-icon-caret-bottom {
  color: #39c15b;
  font-size: 13px;
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
