<template>
  <div class="divBox" style="padding-bottom: 0">
    <el-row :gutter="14" class="baseInfo" v-if="checkPermi(['admin:statistics:trade:data'])">
      <el-col v-bind="grid" class="ivu-mb" v-for="(item, index) in cardLists" :key="index">
        <el-card :bordered="false" dis-hover :padding="12">
          <div class="acea-row row-between-wrapper">
            <div class="acea-row align-center">
              <span class="main_tit">{{ item.name }}</span>
            </div>
            <el-tag type="primary">{{ item.tag }}</el-tag>
          </div>
          <div class="content">
            <span class="content-number spBlock count_data">{{ item.count }}</span>
            <span class="spBlock pt10"
              >环比：
              <i class="content-is" :class="item.percent >= 0 ? 'up' : 'down'"
                >{{ item.percent >= 0 ? '+' : '-' }}{{ item.percent }}</i
              >
              <i :class="item.percent >= 0 ? 'el-icon-caret-top' : 'el-icon-caret-bottom'" />
            </span>
            <!-- <el-divider></el-divider>
            <div class="acea-row row-between-wrapper">
              <span class="content-time">昨日数据</span>
              <span class="content-time">3000元</span>
            </div> -->
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { tradeDataApi } from '@/api/statistic';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'ToDay',
  data() {
    return {
      style: { height: '250px' },
      grid: { xl: 6, lg: 6, md: 12, sm: 24, xs: 24 },
      cardLists: [],
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    checkPermi,
    getList() {
      tradeDataApi().then((res) => {
        this.cardLists = [
          {
            name: '昨日订单数量',
            icon: 'iconline-order',
            tag: '昨日',
            count: res.yesterdayOrderNum,
            percent: res.yesterdayOrderNumRatio,
          },
          {
            name: '本月订单数量',
            icon: 'iconxiaoshoue',
            count: res.monthOrderNum,
            tag: '本月',
            percent: res.monthOrderNumRatio,
          },
          {
            name: '昨日支付金额',
            icon: 'iconline-coin',
            tag: '昨日',
            count: res.yesterdayOrderSales,
            percent: res.yesterdayOrderSalesRatio,
          },
          {
            name: '本月订单金额',
            icon: 'iconshangpinzhifujine',
            count: res.monthOrderSales,
            tag: '本月',
            percent: res.monthOrderSalesRatio,
          },
        ];
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.up,
.el-icon-caret-top,
.content-is {
  color: #f5222d;
  font-size: 12px;
  opacity: 1 !important;

  &.down {
    color: #39c15b;
  }
}
.flex {
  display: flex;
}
.flex-wrap {
  flex-wrap: wrap;
}
.w-50 {
  width: 50%;
}
.mt30 {
  margin-top: 30px;
}
.down,
.el-icon-caret-bottom .content-is {
  font-size: 12px;
}

.el-icon-caret-bottom {
  color: #39c15b;
}

.br {
  border-right: 1px solid rgba(0, 0, 0, 0.1);
}

.toDay {
  width: 49%;
  display: inline-block;

  &-title {
    font-size: 14px;
  }

  &-number {
    font-size: 20px;
  }

  &-time {
    font-size: 12px;
    color: #8c8c8c;
    margin-bottom: 5px;
  }
}

.title {
  font-size: 16px;
  color: #000000;
  font-weight: 500;
}

.price {
  i {
    font-style: normal;
    font-size: 21px;
    color: #000;
  }
}
.ivu-mb {
  margin-bottom: 14px;
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
  /*opacity: 100% !important;*/
}
.main_tit {
  color: #333;
  font-size: 16px;
  font-weight: 500;
}
.content-time {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.align-center {
  align-items: center;
}
.baseInfo {
  ::v-deep .el-card__header {
    padding: 15px 20px !important;
  }
}
.count_data {
  padding: 22px 0 12px;
}
.content {
  &-number {
    font-weight: 600;
    font-family: PingFangSC-Semibold, PingFang SC;
    font-size: 30px;
  }
  &-time {
    font-size: 14px;
    /*color: #8C8C8C;*/
  }
}
</style>
