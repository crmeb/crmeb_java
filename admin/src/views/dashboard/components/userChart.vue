<template>
  <div class="divBox">
    <el-row :gutter="24">
      <el-col :xl="16" :lg="12" :md="24" :sm="24" :xs="24" class="ivu-mb mb10 dashboard-console-visit">
        <el-card :bordered="false" dis-hover>
          <div slot="header">
            <div class="acea-row row-middle">
              <el-avatar icon="el-icon-s-operation" size="small" style="color:#1890ff;background:#e6f7ff;font-size: 13px"/>
              <span class="ivu-pl-8">用户</span>
            </div>
          </div>
          <echarts-from ref="userChart" :echartsTitle="line" :xAxis="xAxis" :seriesData="series" v-if="infoList"></echarts-from>
        </el-card>
      </el-col>
      <el-col :xl="8" :lg="12" :md="24" :sm="24" :xs="24">
        <el-card :bordered="false" dis-hover class="dashboard-console-visit">
          <div slot="header">
            <div class="acea-row row-middle">
              <el-avatar icon="el-icon-picture-outline-round" size="small" style="color:#1890ff;background:#e6f7ff;font-size: 13px" />
              <span class="ivu-pl-8">购买用户统计</span>
            </div>
          </div>
          <echarts-from ref="visitChart" :echartsTitle="circle" :legendData="legendData"
                        :seriesData="seriesUser" v-if="chartBuy"></echarts-from>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import {chartUserApi, chartBuyApi} from '@/api/dashboard';
  import echartsFrom from '@/components/echarts/index';

  export default {
    name: 'user-chart',
    components: {echartsFrom},
    data() {
      return {
        line: 'line',
        circle: 'circle',
        xAxis: [],
        infoList: {},
        series: [],
        xData: [],
        y1Data: [],
        y2Data: [],
        lists: [],
        bing_data: [],
        bing_xdata: [],

        legendData: [],
        seriesUser: [],
        chartBuy: {}
      }
    },
    methods: {
      // 统计
      getStatistics() {
        chartUserApi().then(async res => {
          this.infoList = res
          let data = []
          for (let key  in res) {
            data.push(res[key])
            this.xAxis.push(key)
          }
          this.series = [
            {
              data: data,
              name: '人数（人）',
              type: 'line',
              tooltip: true,
              smooth: true,
              symbol: 'none',
              areaStyle: {
                normal: {
                  opacity: 0.2
                }
              }
            }
          ];
          // this.bing_data = res.bing_data;
          // this.bing_xdata = res.bing_xdata;
        })
      },
      // 用户购买统计
      getRank() {
        chartBuyApi().then(async res => {
          this.chartBuy = res
          this.legendData = ["未消费用户", "消费一次用户", "留存客户", "回流客户"]
          this.seriesUser = [{
            "name": "未消费用户",
            "value": res.zero,
            "itemStyle": {
              "color": "#5cadff"
            }
          },
            {
              "name": "消费一次用户",
              "value": res.one,
              "itemStyle": {
                "color": "#b37feb"
              }
            },
            {
              "name": "留存客户",
              "value": res.history,
              "itemStyle": {
                "color": "#19be6b"
              }
            },
            {
              "name": "回流客户",
              "value": res.back,
              "itemStyle": {
                "color": "#ff9900"
              }
            }]
        })
      },
      // 监听页面宽度变化，刷新表格
      handleResize() {
        if (this.infoList && this.series.length !== 0) this.$refs.userChart.handleResize();
        if (this.infoList) this.$refs.visitChart.handleResize();
      }
    },
    mounted() {
      this.getStatistics();
      this.getRank();
    },
    beforeDestroy() {
      if (this.visitChart) {
        this.visitChart.dispose();
        this.visitChart = null;
      }
    }
  }
</script>

<style scoped lang="scss">
  .acea-row{
    /deep/.el-avatar--small {
      width: 22px;
      height: 22px;
      line-height: 22px;
    }
  }
  .ivu-pl-8{
    margin-left: 8px;
    font-size: 14px;
  }
  .dashboard-console-visit {
    /deep/.el-card__header{
      padding: 14px 20px !important;
    }
    ul {
      li {
        list-style-type: none;
        margin-top: 12px;
      }
    }
  }

  .trees-coadd {
    width: 100%;
    height: 100%;
    .scollhide {
      width: 100%;
      height: 100%;
      overflow-x: hidden;
      overflow-y: scroll;
    }
  }

  .scollhide::-webkit-scrollbar {
    display: none;
  }

  .names {
    display: inline-block;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    width: 84%;
    margin-bottom: -7px;
  }
</style>
