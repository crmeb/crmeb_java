<template>
  <div>
    <el-row :gutter="14">
      <el-col :xs="24" :sm="24" :md="24" :lg="18" v-if="checkPermi(['admin:statistics:user:area'])">
        <el-card :bordered="false" dis-hover class="ivu-mt">
          <div class="acea-row row-between-wrapper">
            <div class="header_title mb20">用户地域分布</div>
          </div>
          <el-row>
            <el-col :xs="24" :sm="24" :md="24" :lg="10">
              <div class="echarts">
                <div :style="{ height: '400px', width: '100%' }" ref="myEchart"></div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="24" :md="24" :lg="14">
              <div class="tables">
                <el-table :data="resdataList" :default-sort="{ prop: 'area', order: 'descending' }" max-height="400">
                  <el-table-column
                    v-for="(item, key) in columns1"
                    :key="key"
                    :label="item.title"
                    :prop="item.key"
                    sortable
                  >
                  </el-table-column>
                </el-table>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="24" :lg="6" v-if="checkPermi(['admin:statistics:user:sex'])">
        <el-card :bordered="false" dis-hover class="ivu-mt">
          <div class="acea-row row-between-wrapper">
            <div class="header_title mb20">用户性别比例</div>
          </div>
          <echarts-new
            :option-data="optionSex"
            :styles="style1"
            height="100%"
            width="100%"
            v-if="optionData"
          ></echarts-new>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import echarts from 'echarts';
import '../../../../../node_modules/echarts/map/js/china.js'; // 引入中国地图数据
import { userAreaData, userChannelData, userSexData } from '@/api/statistic';
import echartsNew from '@/components/echartsNew/index';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'userRegion',
  components: {
    echartsNew,
  },
  props: {
    formInline: {
      type: Object,
      default: function () {
        return {
          channel_type: '',
          data: '',
        };
      },
    },
  },
  data() {
    return {
      chart: null,
      resdata: [],
      resdataList: [],
      columns1: [
        {
          title: 'TOP省份',
          key: 'area',
        },
        {
          title: '支付金额',
          key: 'payAmount',
          sortable: true,
        },
        {
          title: '成交用户数量',
          key: 'payUserNum',
          sortable: true,
        },
        {
          title: '累计用户数量',
          key: 'userNum',
          sortable: true,
        },
      ],
      style: { height: '400px' },
      style1: { height: '400px' },
      optionData: {},
      optionSex: {},
    };
  },
  mounted() {
    this.getTrend();
    this.getSource();
    this.getSex();
  },
  beforeDestroy() {
    if (!this.chart) {
      return;
    }
    this.chart.dispose();
    this.chart = null;
  },
  methods: {
    checkPermi,
    chinaConfigure() {
      let myChart = echarts.init(this.$refs.myEchart); //这里是为了获得容器所在位置
      window.onresize = myChart.resize;
      myChart.setOption({
        // 进行相关配置
        backgroundColor: '#fff',
        tooltip: {
          trigger: 'item',
          formatter: function (params) {
            return params.data
              ? `地区:${params.name}</br>支付金额: ${params.data.value}</br>成交用户数量: ${params.data.newNum}</br>累计用户数量: ${params.data.payPrice}`
              : `地区:${params.name}</br>支付金额: 0</br>成交用户数量: 0</br>累计用户数量: 0`;
          },
        }, // 鼠标移到图里面的浮动提示框
        dataRange: {
          show: false,
          min: 0,
          max: 1000,
          text: ['High', 'Low'],
          realtime: true,
          calculable: true,
          color: ['orangered', 'yellow', 'lightskyblue'],
        },
        geo: {
          // 这个是重点配置区
          map: 'china', // 表示中国地图
          roam: false,
          label: {
            normal: {
              show: false, // 是否显示对应地名
              textStyle: {
                color: 'rgba(0,0,0,0.4)',
              },
            },
          },
          itemStyle: {
            normal: {
              borderColor: 'rgba(0, 0, 0, 0.2)',
            },
            emphasis: {
              areaColor: null,
              shadowOffsetX: 0,
              shadowOffsetY: 0,
              shadowBlur: 10,
              borderWidth: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)',
            },
          },
        },
        series: [
          {
            type: 'scatter',
            zoom: 1.2,
            aspectScale: 1.75, //长宽比
            coordinateSystem: 'geo', // 对应上方配置
          },
          {
            type: 'map',
            geoIndex: 0,
            data: this.resdata,
          },
        ],
      });
    },
    // 统计图
    getTrend() {
      userAreaData().then((res) => {
        this.resdataList = res;
        this.resdata = res.map((item) => {
          let jsonData = {};
          jsonData.name = item.area;
          jsonData.value = item.payAmount;
          jsonData.newNum = item.payUserNum;
          jsonData.payPrice = item.userNum;
          return jsonData;
        });
        this.chinaConfigure();
      });
    },
    //来源
    getSource() {
      userChannelData().then((res) => {
        let channelData = new Array();
        channelData = [
          { name: 'H5', value: 0, channel: 'h5' },
          { name: 'APP', value: 0, channel: 'ios' },
          { name: '小程序', value: 0, channel: 'routine' },
          { name: '公众号', value: 0, channel: 'wechat' },
        ];
        let channelArr = new Array();
        channelData.forEach((item) => {
          res.forEach((item1) => {
            if (item.channel == item1.channel) {
              channelArr.push({
                name: item.name,
                value: item1.num ? item1.num : 0,
                channel: item1.channel,
              });
            }
          });
        });
        this.optionData = {
          tooltip: { trigger: 'item' },
          legend: {
            orient: 'vertical',
            left: 'right',
          },
          series: [
            {
              name: '访问来源',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              label: {
                show: false,
                position: 'center',
              },
              itemStyle: {
                emphasis: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)',
                },
                normal: {
                  color: function (params) {
                    //自定义颜色
                    var colorList = ['#F8C348', '#1991FF', '#40C9D3', '#F94748', '#854FF8'];
                    return colorList[params.dataIndex];
                  },
                },
              },
              labelLine: {
                show: false,
              },
              data: channelArr,
            },
          ],
        };
      });
    },
    getSex() {
      userSexData().then((res) => {
        let sexData = new Array();
        sexData = [
          { name: '未知', value: '', sex: '0' },
          { name: '男', value: '', sex: '1' },
          { name: '女', value: '', sex: '2' },
          { name: '保密', value: '', sex: '3' },
        ];
        let sexArr = [];
        sexData.forEach((item) => {
          res.forEach((item1) => {
            if (item.sex == item1.sex) {
              sexArr.push({
                name: item.name,
                value: item1.num,
                sex: item1.sex,
              });
            }
          });
        });
        this.optionSex = {
          tooltip: {
            trigger: 'item',
          },
          legend: {
            orient: 'vertical',
            left: 'right',
          },
          series: [
            {
              name: '用户性别',
              type: 'pie',
              radius: '70%',
              itemStyle: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)',
                },
                normal: {
                  color: function (params) {
                    //自定义颜色
                    var colorList = ['#1890FF', '#EF9C20', '#4BCAD5', '#854FF8', '#F94748'];
                    return colorList[params.dataIndex];
                  },
                },
              },
              labelLine: {
                show: true,
              },
              data: sexArr,
            },
          ],
        };
      });
    },
  },
};
</script>

<style scoped lang="scss">
.echarts {
  width: 100%;
}
.tables {
  width: 100%;
  ::v-deepivu-table-overflowy {
    &::-webkit-scrollbar {
      width: 0;
    }
    &::-webkit-scrollbar-track {
      background-color: transparent;
    }
    &::-webkit-scrollbar-thumb {
      background: #e8eaec;
    }
  }
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
</style>
