<template>
  <div class="divBox" style="padding-top: 0">
    <el-row :gutter="14">
      <el-col :xs="24" :sm="24" :md="24" :lg="18" v-if="checkPermi(['admin:statistics:user:overview'])">
        <el-card class="box-card card_show">
          <div class="flex flex-wrap justify-between mb30">
            <div class="header_title">用户概览</div>
            <div>
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
                value-format="yyyy-MM-dd"
                format="yyyy-MM-dd"
                range-separator="-"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="onchangeTime"
                style="width: 220px; height: 34px; margin-right: 20px"
                align="right"
              >
              </el-date-picker>
              <el-button type="primary" class="mr15" @click="onSeach">搜索</el-button>
            </div>
          </div>
          <el-row>
            <el-col :xs="24" :sm="24" :md="18" :lg="18">
              <div class="user_chart">
                <div class="acea-row">
                  <div class="user-visitUser">
                    <div class="data_num acea-row">
                      <div class="inone">
                        <div class="user_reg_tit">注册用户数量:{{ userView.registerNum }}</div>
                        <div class="reg_time">环比增长率：{{ userView.registerNumRatio }}</div>
                      </div>
                    </div>
                  </div>
                  <div class="user-visitUser-ti column_center">
                    <span class="num_data">{{ userView.pageviews }}</span>
                    <span>访客</span>
                  </div>
                </div>
                <div class="orderUser">
                  <div class="user-orderUser acea-row">
                    <div class="data_num acea-row">
                      <div class="inone">
                        <div class="user_reg_tit">活跃用户数量:{{ userView.activeUserNum }}</div>
                        <div class="reg_time">环比增长率：{{ userView.activeUserNumRatio }}</div>
                      </div>
                    </div>
                  </div>
                  <div class="user-orderUser-ti column_center">
                    <span class="num_data">{{ userView.orderUserNum }}</span>
                    <span>下单</span>
                  </div>
                </div>
                <div class="acea-row payOrderUser">
                  <div class="user-payOrderUser acea-row">
                    <div class="data_num acea-row">
                      <div class="inone">
                        <div class="user_reg_tit">充值用户数量:{{ userView.rechargeUserNum }}</div>
                        <div class="reg_time">环比增长率：{{ userView.rechargeUserNumRatio }}</div>
                      </div>
                      <div class="inone">
                        <div class="user_reg_tit">客单价:{{ userView.customerPrice }}</div>
                      </div>
                    </div>
                  </div>
                  <div class="user-payOrderUser-ti column_center">
                    <span class="num_data"> {{ userView.orderPayUserNum }}</span>
                    <span>成交用户</span>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="24" :lg="6" v-if="checkPermi(['admin:statistics:user:channel'])">
        <el-card class="box-card">
          <div class="header_title">用户渠道</div>
          <echarts-new
            :option-data="optionData"
            :styles="style"
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
import echartsNew from '@/components/echartsNew/index';
import { userOverviewData, userChannelData } from '@/api/statistic';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  data() {
    return {
      optionData: {},
      style: { height: '370px' },
      timeVal: [],
      dateLimit: '',
      list: [],
      fromList: this.$constants.timeList,
      userView: {},
      pickerOptions: this.$timeOptions, //快捷时间选项
    };
  },
  components: {
    echartsNew,
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
    this.getUserView();
    this.getChannel();
  },
  methods: {
    checkPermi,
    onchangeTime(e) {
      this.timeVal = e;
      this.dateLimit = e ? this.timeVal.join(',') : '';
      this.dateLimitPram = e ? this.timeVal.join(',') : '';
    },
    onSeach() {
      this.getUserView();
    },
    exports() {},
    selectChange(limit) {
      if (limit == '') {
        //昨天的时间
        this.$set(this, 'dateLimitPram', 'yesterday');
        this.getUserView();
      } else {
        this.dateLimitPram = limit;
        this.getUserView();
      }
    },
    //用户概览
    getUserView() {
      userOverviewData({ dateLimit: this.dateLimitPram }).then((res) => {
        this.userView = res;
      });
    },
    //渠道
    getChannel() {
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
            //通过两层遍历返回api数据的长度，并且得到指定key的渠道name
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
          tooltip: {
            trigger: 'item',
          },
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
                    var colorList = ['#1BBE6B', '#1890FF', '#EF9C20', '#4BCAD5'];
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
  },
};
</script>

<style scoped lang="scss">
.mb20 {
  margin-bottom: 20px;
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

.flex {
  display: flex;
}

.flex-wrap {
  flex-wrap: wrap;
}

.mb20 {
  margin-bottom: 20px;
}

.mt20 {
  margin-top: 20px;
}

.mb30 {
  margin-bottom: 30px;
}

.mr-20 {
  margin-right: 20px;
}

.justify-between {
  justify-content: space-between;
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

.header {
  &-title {
    font-size: 16px;
    color: rgba(0, 0, 0, 0.85);
  }

  &-time {
    font-size: 12px;
    color: #000000;
    opacity: 0.45;
  }
}

.iconfont {
  font-size: 16px;
  color: #fff;
}

.iconCrl {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  text-align: center;
  line-height: 32px;
  opacity: 0.7;
}

.lan {
  background: #1890ff;
}

.iconshangpinliulanliang {
  color: #fff;
}

.infoBox {
  width: 20%;
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

.data_num {
  height: 100px;
}

.info {
  .sp1 {
    color: #666;
    font-size: 14px;
    display: block;
  }

  .sp2 {
    font-weight: 400;
    font-size: 30px;
    color: rgba(0, 0, 0, 0.85);
    display: block;
    padding: 10px 0 10px;
  }

  .sp3 {
    font-size: 12px;
    font-weight: 400;
    color: rgba(0, 0, 0, 0.45);
    display: block;
  }
}

.user_chart {
  min-width: 900px;
}

.user-visitUser {
  width: 75%;
  height: 100px;
  background: #f2f6ff;
  // padding: 18px 0 18px 17px;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;

  @media screen and (max-width: 1200px) {
    width: 55%;
  }

  @media screen and (max-width: 900px) {
    width: 35%;
  }
}

.user-visitUser-ti {
  width: 310px;
  height: 100px;
  background: #1890ff;
  transform: perspective(5em) rotateX(-11deg);
  margin-left: -104px;
  margin-top: 8px;
  text-align: center;
  color: #fff;
  font-size: 14px;
}

.column_center {
  display: flex;
  flex-direction: column;
  // justify-content: center;
  align-items: center;
  padding-top: 17px;
}

.orderUser {
  position: relative;
  top: -6px;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  white-space: normal;
}

.payOrderUser {
  position: relative;
  top: -16px;
}

.user-orderUser {
  width: 75%;
  height: 98px;
  background: #f1fffa;
  box-sizing: border-box;

  @media screen and (max-width: 1200px) {
    width: 55%;
  }

  @media screen and (max-width: 900px) {
    width: 35%;
  }
}

.user-orderUser-ti {
  width: 226px;
  height: 98px;
  background: #4bcad5;
  transform: perspective(7em) rotateX(-20deg);
  margin-left: -62px;
  margin-top: 7px;
  text-align: center;
  color: #fff;
  font-size: 14px;
}

.user-orderUser-change,
.user-orderUser-changeduan {
  height: 100px;
  border-bottom: 1px solid #d8d8d8;
  border-top: 1px solid #d8d8d8;
  margin-left: -19px;
}

.user-orderUser-change {
  width: 128px;
}

.user-payOrderUser {
  width: 75%;
  height: 95px;
  background: #f3f4f8;
  box-sizing: border-box;
  margin-top: 3px;

  @media screen and (max-width: 1200px) {
    width: 55%;
  }

  @media screen and (max-width: 900px) {
    width: 35%;
  }
}

.user-payOrderUser-ti {
  width: 145px;
  height: 92px;
  background: #5e7092;
  transform: perspective(3em) rotateX(-13deg);
  margin-left: -22px;
  margin-top: 15px;
  text-align: center;
  color: #fff;
  font-size: 14px;
}

.num_data {
  font-size: 24px;
  color: #fff;
  font-weight: 600;
  line-height: 33px;
}

.sp1 {
  margin-left: 10px;
  overflow: auto;
}

.sp2 {
  margin-top: 77px;
  margin-left: 10px;
  overflow: auto;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.inone {
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin-left: 61px;
}

.m20 {
  margin: 20px;
}

.user_reg_tit {
  font-size: 16px;
  color: #333;
  font-weight: bold;
}

.reg_time {
  font-size: 14px;
  padding: 10px 0 10px;
  color: #333;
}

.mb30 {
  margin-bottom: 30px;
}

.card_show {
  @media screen and (max-width: 700px) {
    display: none;
  }
}
</style>
