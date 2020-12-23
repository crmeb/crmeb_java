<template>
    <div class="divBox">
        <el-row :gutter="24">
            <el-col san="24" class="ivu-mb">
                <el-card :bordered="false" class="dashboard-console-visit">
                    <div slot="header">
                        <div class="acea-row row-between-wrapper">
                            <div class="acea-row row-middle">
                                <el-avatar icon="el-icon-s-data" size="small" style="color:#1890ff;background:#e6f7ff;font-size: 13px" />
                                <span class="ivu-pl-8">订单</span>
                            </div>
                            <div class="checkTime">
                                <el-radio-group v-model="visitDate" class="ivu-mr-8">
                                    <el-radio label="last30" @change="handleChangeVisitType">30天</el-radio>
                                    <el-radio label="week" @change="handleChangeWeek">周</el-radio>
                                    <el-radio label="month" @change="handleChangeMonth">月</el-radio>
                                    <el-radio label="year" @change="handleChangeYear">年</el-radio>
                                </el-radio-group>
                            </div>
                        </div>
                    </div>
                    <h4>订单量趋势</h4>
                    <echarts-from ref="visitChart" :yAxisData="yAxisData" :seriesData="series" :xAxis="xAxis" v-if="info" :legendData="legendData"></echarts-from>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>
<script>
  import { chartOrder30Api, chartOrderWeekApi, chartOrderMonthApi, chartOrderYearApi } from '@/api/dashboard';
  import echartsFrom from '@/components/echarts/index';

  export default {
    components: {echartsFrom},
    data() {
      return {
        infoList: null,
        visitDate: 'last30',
        series: [],
        xAxis: [],
        info: {},
        legendData: [],
        yAxisData: []
      }
    },
    mounted(){
      this.yAxisData = [
        {
          type: 'value',
          name: '金额',
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            textStyle: {
              color: '#7F8B9C'
            }
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: '#F5F7F9'
            }
          }
        },
        {
          type: 'value',
          name: '数量',
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            textStyle: {
              color: '#7F8B9C'
            }
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: '#F5F7F9'
            }
          }
          // axisLabel: {
          //     formatter: '{value} °C'
          // }
        }
      ]
    },
    methods: {
      // 时间改变
      handleChangeVisitType() {
        this.xAxis = []
        this.legendData = []
        chartOrder30Api().then(async res => {
          this.info = res
          let pices = []
          let qualitys = []
          for (let key  in res.price) {
            pices.push(Number(res.price[key]))
            this.xAxis.push(key)
          }
          for (let key  in res.quality) {
            qualitys.push(Number(res.quality[key]))
          }
          this.legendData = ['订单金额','订单数']
          this.series = [{
            name:"订单金额",
            type:"bar",
            itemStyle:{
              "normal":{
                "color":{
                  "x":0,
                  "y":0,
                  "x2":0,
                  "y2":1,
                  "colorStops":[
                    {
                      "offset":0,
                      "color":"#69cdff"
                    },
                    {
                      "offset":0.5,
                      "color":"#3eb3f7"
                    },
                    {
                      "offset":1,
                      "color":"#1495eb"
                    }
                  ]
                }
              }
            },
            data: pices
          },
            {
              name:"订单数",
              type:"line",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#6fdeab"
                      },
                      {
                        "offset":0.5,
                        "color":"#44d693"
                      },
                      {
                        "offset":1,
                        "color":"#2cc981"
                      }
                    ]
                  }
                }
              },
              yAxisIndex: 1,
              data: qualitys
            }]
        })
      },
      handleChangeWeek() {
        this.xAxis = []
        this.legendData = []
        chartOrderWeekApi().then(async res => {
          this.info = res
          this.legendData = ["上周金额", "本周金额", "上周订单数", "本周订单数"]
          let prePrice = []
          let price = []
          let qualitys = []
          let preQuality = []
          for (let key  in res.prePrice) {
            prePrice.push(Number(res.prePrice[key]))
            this.xAxis.push(key)
          }
          for (let key  in res.price) {
            price.push(Number(res.price[key]))
          }
          for (let key  in res.preQuality) {
            preQuality.push(Number(res.preQuality[key]))
          }
          for (let key  in res.quality) {
            qualitys.push(Number(res.quality[key]))
          }
          this.series = [
            {
              name :"上周金额",
              type:"bar",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#69cdff"
                      },
                      {
                        "offset":0.5,
                        "color":"#3eb3f7"
                      },
                      {
                        "offset":1,
                        "color":"#1495eb"
                      }
                    ]
                  }
                }
              },
              data: prePrice
            },
            {
              name:"本周金额",
              type:"bar",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#69cdff"
                      },
                      {
                        "offset":0.5,
                        "color":"#3eb3f7"
                      },
                      {
                        "offset":1,
                        "color":"#1495eb"
                      }
                    ]
                  }
                }
              },
              data: price
            },
            {
              name:"上周订单数",
              type:"line",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#6fdeab"
                      },
                      {
                        "offset":0.5,
                        "color":"#44d693"
                      },
                      {
                        "offset":1,
                        "color":"#2cc981"
                      }
                    ]
                  }
                }
              },
              yAxisIndex: 1,
              data: preQuality
            },
            {
              name:"本周订单数",
              type:"line",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#6fdeab"
                      },
                      {
                        "offset":0.5,
                        "color":"#44d693"
                      },
                      {
                        "offset":1,
                        "color":"#2cc981"
                      }
                    ]
                  }
                }
              },
              yAxisIndex: 1,
              data: qualitys
            }
          ]
        })
      },
      handleChangeMonth() {
        this.xAxis = []
        this.legendData = []
        chartOrderMonthApi().then(async res => {
          this.info = res
          this.legendData = ["上月金额", "本月金额", "上月订单数", "本月订单数"]
          let prePrice = []
          let price = []
          let qualitys = []
          let preQuality = []
          for (let key  in res.prePrice) {
            prePrice.push(Number(res.prePrice[key]))
            this.xAxis.push(key)
          }
          for (let key  in res.price) {
            price.push(Number(res.price[key]))
          }
          for (let key  in res.preQuality) {
            preQuality.push(Number(res.preQuality[key]))
          }
          for (let key  in res.quality) {
            qualitys.push(Number(res.quality[key]))
          }
          this.series = [
            {
              name:"上月金额",
              type:"bar",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#69cdff"
                      },
                      {
                        "offset":0.5,
                        "color":"#3eb3f7"
                      },
                      {
                        "offset":1,
                        "color":"#1495eb"
                      }
                    ]
                  }
                }
              },
              data: prePrice
            },
            {
              name:"本月金额",
              type:"bar",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#69cdff"
                      },
                      {
                        "offset":0.5,
                        "color":"#3eb3f7"
                      },
                      {
                        "offset":1,
                        "color":"#1495eb"
                      }
                    ]
                  }
                }
              },
              data: price
            },
            {
              name:"上月订单数",
              type:"line",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#6fdeab"
                      },
                      {
                        "offset":0.5,
                        "color":"#44d693"
                      },
                      {
                        "offset":1,
                        "color":"#2cc981"
                      }
                    ]
                  }
                }
              },
              yAxisIndex: 1,
              data: preQuality
            },
            {
              name:"本月订单数",
              type:"line",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#6fdeab"
                      },
                      {
                        "offset":0.5,
                        "color":"#44d693"
                      },
                      {
                        "offset":1,
                        "color":"#2cc981"
                      }
                    ]
                  }
                }
              },
              yAxisIndex: 1,
              data: qualitys
            }
          ]
        })
      },
      handleChangeYear() {
        this.xAxis = []
        this.legendData = []
        chartOrderYearApi().then(async res => {
          this.info = res
          this.legendData =  ["去年金额", "今年金额", "去年订单数", "今年订单数"]
          let prePrice = []
          let price = []
          let qualitys = []
          let preQuality = []
          for (let key  in res.prePrice) {
            prePrice.push(Number(res.prePrice[key]))
            this.xAxis.push(key)
          }
          for (let key  in res.price) {
            price.push(Number(res.price[key]))
          }
          for (let key  in res.preQuality) {
            preQuality.push(Number(res.preQuality[key]))
          }
          for (let key  in res.quality) {
            qualitys.push(Number(res.quality[key]))
          }
          this.series = [
            {
              name:"去年金额",
              type:"bar",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#69cdff"
                      },
                      {
                        "offset":0.5,
                        "color":"#3eb3f7"
                      },
                      {
                        "offset":1,
                        "color":"#1495eb"
                      }
                    ]
                  }
                }
              },
              data: prePrice
            },
            {
              name:"今年金额",
              type:"bar",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#69cdff"
                      },
                      {
                        "offset":0.5,
                        "color":"#3eb3f7"
                      },
                      {
                        "offset":1,
                        "color":"#1495eb"
                      }
                    ]
                  }
                }
              },
              data: price
            },
            {
              name:"去年订单数",
              type:"line",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#6fdeab"
                      },
                      {
                        "offset":0.5,
                        "color":"#44d693"
                      },
                      {
                        "offset":1,
                        "color":"#2cc981"
                      }
                    ]
                  }
                }
              },
              yAxisIndex: 1,
              data: preQuality
            },
            {
              name:"今年订单数",
              type:"line",
              itemStyle:{
                "normal":{
                  "color":{
                    "x":0,
                    "y":0,
                    "x2":0,
                    "y2":1,
                    "colorStops":[
                      {
                        "offset":0,
                        "color":"#6fdeab"
                      },
                      {
                        "offset":0.5,
                        "color":"#44d693"
                      },
                      {
                        "offset":1,
                        "color":"#2cc981"
                      }
                    ]
                  }
                }
              },
              yAxisIndex: 1,
              data: qualitys
            }
          ]
        })
      },
      // 监听页面宽度变化，刷新表格
      handleResize() {
        if (this.infoList) this.$refs.visitChart.handleResize();
      }
    },
    created() {
      this.handleChangeVisitType();
    }
  }
</script>
<style lang="scss" scoped>
  .acea-row{
    /deep/.el-avatar--small {
      width: 22px;
      height: 22px;
      line-height: 22px;
    }
  }
  .checkTime{
    /deep/.el-radio__input{
      display: none;
    }
  }
  .ivu-pl-8{
    margin-left: 8px;
    font-size: 14px;
  }
  .divBox {
    padding: 0 20px !important;
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
  .ivu-mb{
    margin-bottom: 10px;
  }
</style>
