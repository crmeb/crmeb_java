<template>
  <div class="divBox">
    <el-row :gutter="14">
      <el-col :xs="24" :sm="24" :md="24" :lg="12">
        <el-card class="box-card">
          <div class="header_title">快捷入口</div>
          <div class="nav_grid">
            <div class="nav_grid_item" v-for="(item, index) in permList" :key="index" @click="navigatorTo(item.url)">
              <div class="pic_badge" :style="{ backgroundColor: item.bgColor }">
                <span class="iconfont" :class="item.icon"></span>
              </div>
              <p>{{ item.title }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="24" :lg="12">
        <el-card class="box-card">
          <div class="header_title">待办事项</div>
          <div class="nav_grid">
            <div
              class="nav_grid_item"
              v-for="(item, index) in businessList"
              :key="index"
              @click="navigatorTo(item.path)"
            >
              <p class="num_data">{{ item.num }}</p>
              <p class="label">{{ item.title }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { businessData } from '@/api/dashboard';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

const router = useRouter();

const grid = ref({
  xl: 3,
  lg: 3,
  md: 6,
  sm: 8,
  xs: 8,
});
const nav_list = ref([
  {
    bgColor: '#1890FF',
    icon: 'iconhuiyuanguanli',
    title: '用户管理',
    url: '/user/index',
    perms: ['admin:user:list'],
  },
  {
    bgColor: '#1BBE6B',
    icon: 'iconxitongshezhi',
    title: '系统设置',
    url: '/operation/setting',
    perms: ['admin:system:config:info'],
  },
  {
    bgColor: '#EF9C20',
    icon: 'iconshangpinguanli',
    title: '商品管理',
    url: '/store/index',
    perms: ['admin:product:list'],
  },
  {
    bgColor: '#F56C6C',
    icon: 'icondingdanguanli',
    title: '订单管理',
    url: '/order/index',
    perms: ['admin:order:list'],
  },
  {
    bgColor: '#E8B600',
    icon: 'iconwenzhangguanli',
    title: '文章管理',
    url: '/content/articleManager',
    perms: ['admin:article:list'],
  },
  {
    bgColor: '#4BCAD5',
    icon: 'iconfenxiaoguanli',
    title: '分销管理',
    url: '/distribution/index',
    perms: ['admin:retail:list'],
  },
  {
    bgColor: '#1890FF',
    icon: 'iconyouhuiquan',
    title: '优惠券',
    url: '/marketing/coupon/list',
    perms: ['admin:coupon:list'],
  },
]);
const statisticData = ref([
  { title: '待发货订单', num: 0, path: '/order/index', perms: ['admin:order:list'] },
  { title: '退款中订单', num: 0, path: '/order/index', perms: ['admin:order:list'] },
  { title: '库存预警', num: 0, path: '/store/index', perms: ['admin:product:list'] },
  { title: '上架商品', num: 0, path: '/store/index', perms: ['admin:product:list'] },
  { title: '仓库商品', num: 0, path: '/store/index', perms: ['admin:product:list'] },
  { title: '提现待审核', num: 0, path: '/financial/commission/template', perms: ['admin:finance:apply:list'] },
  { title: '账户充值', num: 0, path: '/financial/record/charge', perms: ['admin:recharge:list'] },
]);

//鉴权处理
const permList = computed(() => {
  let arr = [];
  nav_list.value.forEach((item) => {
    if (checkPermi(item.perms)) {
      arr.push(item);
    }
  });
  return arr;
});
const businessList = computed(() => {
  let arr = [];
  statisticData.value.forEach((item) => {
    if (checkPermi(item.perms)) {
      arr.push(item);
    }
  });
  return arr;
});

function navigatorTo(path) {
  router.push(path);
}
function getbusinessData() {
  businessData().then((res) => {
    statisticData.value[0].num = res.notShippingOrderNum; //待发货订单
    statisticData.value[1].num = res.refundingOrderNum; //退款中订单
    statisticData.value[2].num = res.vigilanceInventoryNum; //库存预警
    statisticData.value[3].num = res.onSaleProductNum; //上架商品
    statisticData.value[4].num = res.notSaleProductNum; //仓库商品
    statisticData.value[5].num = res.notAuditNum; //提现待审核
    statisticData.value[6].num = res.totalRechargeAmount; //账户充值
  });
}

onMounted(() => {
  getbusinessData();
});
</script>
<style lang="scss" scoped>
.ivu-mb {
  margin-bottom: 14px;
}

.dashboard-console-grid {
  text-align: center;
  .ivu-card-body {
    padding: 0;
  }
  i {
    font-size: 32px;
  }
  a {
    display: block;
    color: inherit;
  }
  p {
    margin-top: 8px;
  }
}
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
.nav_grid {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 10px;
}
.nav_grid_item {
  width: 20%;
  height: 90px;
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  img {
    width: 58px;
    height: 58px;
  }
  .pic_badge {
    width: 58px;
    height: 58px;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #fff;
    border-radius: 4px;
    .iconfont {
      font-size: 30px;
    }
  }
  p {
    height: 17px;
    font-size: 14px;
    font-family: PingFangSC-Regular, PingFang SC;
    font-weight: 400;
    color: #000000;
    line-height: 17px;
    margin-top: 12px;
  }
  .num_data {
    font-size: 28px;
    font-weight: 600;
    color: #333;
    text-align: center;
    margin-bottom: 18px;
  }
  .label {
    font-size: 14px;
    color: #666666;
    text-align: center;
  }
}
</style>
