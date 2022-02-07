<template>
  <div class="divBox">
    <el-row :gutter="24">
      <el-col class="ivu-mb mb10 dashboard-console-visit">
        <el-card :bordered="false" dis-hover>
          <div slot="header">
            <div class="acea-row row-middle">
              <div class="header_title">用户统计</div>
            </div>
          </div>
          <echarts-from ref="userChart" :echartsTitle="line" :xAxis="xAxis" :seriesData="series" v-if="infoList"></echarts-from>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import {chartUserApi} from '@/api/dashboard';
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
      // 监听页面宽度变化，刷新表格
      handleResize() {
        if (this.infoList && this.series.length !== 0) this.$refs.userChart.handleResize();
        if (this.infoList) this.$refs.visitChart.handleResize();
      }
    },
    mounted() {
      this.getStatistics();
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
    ::v-deep.el-avatar--small {
      width: 22px;
      height: 22px;
      line-height: 22px;
    }
  }
  .header_title{
    font-size: 16px;
    font-family: PingFangSC-Semibold, PingFang SC;
    font-weight: 600;
    color: #000000;
    padding-left:8px;
    position: relative;
    &::before{
      position: absolute;
      content: '';
      width: 2px;
      height: 18px;
      background: #1890FF;
      top:0;
      left:0;
    }
  }
  .ivu-pl-8{
    margin-left: 8px;
    font-size: 14px;
  }
  .dashboard-console-visit {
    ::v-deep.el-card__header{
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
