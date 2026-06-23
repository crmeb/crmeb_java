<template>
  <div>
    <div class="divBox" style="padding-bottom: 0">
      <cards-data :cardLists="list" v-if="checkPermi(['admin:statistics:user:total:data'])"></cards-data>
    </div>
    <div class="divBox" style="padding-top: 0">
      <user-info key="1"></user-info>
    </div>
    <div class="divBox" style="padding-top: 0">
      <user-region :formInline="formInline" ref="userRegions" key="3"></user-region>
    </div>
  </div>
</template>

<script>
import userInfo from './components/userInfo';
import userRegion from './components/userRegion';
import cardsData from '@/components/cards/index';
import { userTotalData } from '@/api/statistic';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  components: {
    userInfo,
    // wechetInfo,
    userRegion,
    cardsData,
  },
  data() {
    return {
      list: [],
      formInline: {
        channel_type: '',
        data: '',
      },
    };
  },
  mounted() {
    this.getStatistics();
  },
  methods: {
    checkPermi,
    // 统计
    getStatistics() {
      userTotalData().then((res) => {
        this.list = [
          {
            name: '累计用户数',
            icon: 'iconleijiyonghushu',
            count: res.userNum,
            class: 'one',
            color: '#1890FF',
          },
          {
            name: '累计充值人数',
            icon: 'iconyonghu',
            count: res.rechargePeopleNum,
            class: 'two',
            color: '#A277FF',
          },
          {
            name: '累计充值金额',
            icon: 'iconchongzhijine',
            count: res.rechargeTotalAmount,
            class: 'three',
            color: '#EF9C20',
          },
          {
            name: '累计消费金额',
            icon: 'iconzhichujine1',
            count: res.consumptionAmount,
            class: 'four',
            color: '#1BBE6B',
          },
        ];
      });
    },
  },
};
</script>

<style scoped lang="scss">
.mb20 {
  margin-bottom: 20px;
}
.mb30 {
  margin-bottom: 30px;
}
.mr-20 {
  margin-right: 20px;
}
.mt-20 {
  margin-top: 20px;
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
.flex {
  display: flex;
}
.flex-wrap {
  flex-wrap: wrap;
}
.pb0 {
  padding-bottom: 0;
}
.justify-between {
  justify-content: space-between;
}
.justify-center {
  justify-content: center;
}
.align-center {
  align-items: center;
}
.all_data {
  // height: 250px;
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
.text_overflow {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
