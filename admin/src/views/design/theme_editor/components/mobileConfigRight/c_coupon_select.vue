<template>
  <div class="goods-box" v-if="defaults.couponList">
    <div class="acea-row">
      <div class="title">选择优惠券</div>
      <div class="wrapper">
        <draggable class="dragArea list-group" :list="defaults.couponList.list" group="coupons" handle=".move-icon">
          <div class="item" v-for="(item, index) in defaults.couponList.list" :key="index">
            <div class="move-icon">
              <span class="iconfont iconxingzhuangjiehe"></span>
            </div>
            <div class="coupon-item">
              <div class="name line1">{{ item.name || item.title || '优惠券' }}</div>
            </div>
            <span class="iconfont iconshanchu3" @click.stop="bindDelete(index)"></span>
          </div>
        </draggable>
        <div class="add-item" @click="openModal">
          <el-button class="btn"><span class="iconfont iconjiahao1"></span>添加</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import vuedraggable from 'vuedraggable';
export default {
  name: 'c_coupon_select',
  props: {
    configObj: {
      type: Object,
    },
  },
  components: {
    draggable: vuedraggable,
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal || {};
        this.ids = this.getCouponIds();
      },
      immediate: true,
      deep: true,
    },
  },
  data() {
    return {
      defaults: {},
      ids: [],
    };
  },
  created() {
    this.defaults = this.configObj || {};
    this.ids = this.getCouponIds();
  },
  methods: {
    openModal() {
      const couponList = (this.defaults.couponList && this.defaults.couponList.list) || [];
      this.$modalCoupon('wu', Date.now(), couponList, (couponObj) => this.getCouponId(couponObj), '', '');
    },
    //对象数组去重；
    unique(arr) {
      const res = new Map();
      return arr.filter((arr) => !res.has(arr.id) && res.set(arr.id, 1));
    },
    getCouponIds() {
      const list = (this.defaults.couponList && this.defaults.couponList.list) || [];
      return list.map((item) => item.id);
    },
    getCouponId(couponObj = []) {
      const list = this.unique(Array.isArray(couponObj) ? couponObj : []);
      this.$set(this.defaults.couponList, 'list', list);
      this.ids = this.getCouponIds();
    },
    bindDelete(index) {
      this.defaults.couponList.list.splice(index, 1);
      this.ids = this.defaults.couponList.list.map((item) => item.id);
    },
  },
};
</script>

<style scoped lang="scss">
.goods-box {
  padding: 0 15px;
  .wrapper {
    flex: 1;
  }
  .wrapper,
  .list-group {
    display: flex;
    flex-direction: column;
    width: 100%;
  }
  .title {
    color: #fff;
    font-size: 12px;
    width: 75px;
    margin-right: 5px;
    margin-top: 20px;
  }
  .add-item {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 32px;
    border-radius: 4px;
    margin: 0 0px 20px 12px;
    .btn {
      width: 100%;
    }
    .iconfont {
      font-size: 12px;
      color: #999;
      margin-right: 5px;
    }
    .tip {
      font-size: 12px;
      color: #333;
    }
  }
  .item {
    position: relative;
    display: flex;
    align-items: center;
    width: 100%;
    height: 40px;
    margin-bottom: 4px;
    background: #fff;
    border-radius: 4px;
    padding: 0 10px;
    box-sizing: border-box;
    font-size: 13px;
    &.sortable-chosen,
    &.sortable-drag,
    &.sortable-ghost {
      background: #f7f7f7 !important;
    }
    .move-icon {
      margin-right: 10px;
      .iconfont {
        cursor: move;
        color: #ddd;
        font-size: 14px;
      }
    }

    .coupon-item {
      flex: 1;
      display: flex;
      align-items: center;
      overflow: hidden;
      max-width: 210px;
      .name {
        font-size: 12px;
        color: #333;
        width: 100%;
      }
    }

    .iconshanchu3 {
      display: none;
      color: #999999;
      font-size: 16px;
      cursor: pointer;
      margin-left: 10px;
    }
    &:hover {
      .iconshanchu3 {
        display: block;
      }
    }
  }
}
</style>
