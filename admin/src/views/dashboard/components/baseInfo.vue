<template>
  <div class="divBox" style="padding-bottom:0;">
    <el-row :gutter="20" class="baseInfo">
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="12">
          <div  class="acea-row row-between-wrapper">
            <div class="acea-row align-center">
              <!-- <div class="main_badge">
                <span class="iconfont iconxiaoshoue"></span>
              </div> -->
              <span class="main_tit">销售额</span>
            </div>
            <el-tag type="primary">今日</el-tag>
          </div>
          <div class="content" v-if="viewData">
            <span class="content-number spBlock my15">{{ viewData.sales }}</span>
            <el-divider></el-divider>
            <div class="acea-row row-between-wrapper">
              <span class="content-time">昨日数据</span>
              <span class="content-time">{{viewData.yesterdaySales}} 元</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="12">
          <div  class="acea-row row-between-wrapper">
            <div class="acea-row align-center">
              <!-- <div class="main_badge">
                <span class="iconfont iconyonghu"></span>
              </div> -->
              <span class="main_tit">用户访问量</span>
            </div>
            <el-tag type="primary">今日</el-tag>
          </div>
          <div class="content" v-if="viewData">
            <span class="content-number spBlock my15">{{ viewData.pageviews }}</span>
            <el-divider></el-divider>
            <div class="acea-row row-between-wrapper">
              <span class="content-time">昨日数据</span>
              <span class="content-time">{{ viewData.yesterdayPageviews }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="12">
          <div  class="acea-row row-between-wrapper">
            <div class="acea-row align-center">
              <!-- <div class="main_badge">
                <span class="iconfont icondingdan"></span>
              </div> -->
              <span class="main_tit">订单量</span>
            </div>
            <el-tag type="primary">今日</el-tag>
          </div>
          <div class="content" v-if="viewData">
            <span class="content-number spBlock my15">{{ viewData.orderNum }}</span>
            <el-divider></el-divider>
            <div class="acea-row row-between-wrapper">
              <span class="content-time">昨日数据</span>
              <span class="content-time">{{ viewData.yesterdayOrderNum }}单</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col v-bind="grid" class="ivu-mb">
        <el-card :bordered="false" dis-hover :padding="12">
          <div  class="acea-row row-between-wrapper">
            <div class="acea-row align-center">
              <!-- <div class="main_badge">
                <span class="iconfont iconxinzengyonghu"></span>
              </div> -->
              <span class="main_tit">新增用户</span>
            </div>
            <el-tag type="primary">今日</el-tag>
          </div>
          <div class="content" v-if="viewData">
            <span class="content-number spBlock my15">{{ viewData.newUserNum }}</span>
            <el-divider></el-divider>
            <div class="acea-row row-between-wrapper">
              <span class="content-time">昨日数据</span>
              <span class="content-time">{{ viewData.yesterdayNewUserNum }} 人</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script>
  import {viewModelApi} from '@/api/dashboard'
  export default {
    data() {
      return {
        grid: { xl: 6, lg: 6, md: 12, sm: 12, xs: 24},
        viewData:{}
      }
    },
    methods: {
      statisticsOrder() {
        viewModelApi().then(async res => {
          this.viewData = res;
        })
      },
    },
    mounted() {
      this.statisticsOrder();
    }
  }
</script>
<style scoped lang="scss">
  .ivu-mb{
    margin-bottom: 20px;
  }
  .up, .el-icon-caret-top {
    color: #F5222D;
    font-size: 12px;
    opacity: 1 !important;
  }

  .down, .el-icon-caret-bottom {
    color: #39C15B;
    font-size: 12px;
    /*opacity: 100% !important;*/
  }
  .main_tit{
    color: #333;
    font-size: 14px;
    font-weight: 500;
  }
  .content-time{
    font-size: 14px;
    color:#333;
    font-weight: 500;
  }
  .main_badge{
    width: 30px;
    height: 30px;
    border-radius: 5px;
    margin-right: 10px;
    background: #2C90FF;
    color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .my15{
    margin: 15px 0 15px;
  }
  .align-center{
    align-items: center;
  }
  .baseInfo {
    ::v-deep .el-card__header {
      padding: 15px 20px !important;
    }
  }

  .content {
    &-number {
      font-size: 30px;
      font-weight: 600;
      font-family: PingFangSC-Semibold, PingFang SC;
      color: #333;
    }
    &-time{
      font-size:14px;
      color: #333333;
      font-weight: 400;
    }
  }
</style>