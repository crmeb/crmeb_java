<template>
  <div class="goods-box" v-if="defaults.couponList">
    <div class="acea-row">
      <div class="title">选择优惠券</div>
      <div class="wrapper">
        <draggable
          class="dragArea list-group"
          :list="defaults.couponList.list"
          :item-key="getDraggableItemKey"
          group="coupons"
          handle=".move-icon"
        >
          <template #item="{ element: item, index }">
            <div class="item">
              <div class="move-icon">
                <span class="iconfont iconxingzhuangjiehe"></span>
              </div>
              <div class="coupon-item">
                <div class="name line1">{{ item.name || item.title || '优惠券' }}</div>
              </div>
              <span class="iconfont iconshanchu3" @click.stop="bindDelete(index)"></span>
            </div>
          </template>
        </draggable>
        <div class="add-item" @click="openModal">
          <el-button class="btn"><span class="iconfont iconjiahao1"></span>添加</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, getCurrentInstance } from 'vue';
import draggable from 'vuedraggable';
import { getDraggableItemKey } from '@/utils/draggableKey';

defineOptions({ name: 'c_coupon_select' });

const props = defineProps({
  configObj: {
    type: Object,
  },
});

const { proxy } = getCurrentInstance();

const defaults = ref({});
const ids = ref([]);

defaults.value = props.configObj || {};
ids.value = getCouponIds();

watch(
  () => props.configObj,
  (nVal, oVal) => {
    defaults.value = nVal || {};
    ids.value = getCouponIds();
  },
  { immediate: true, deep: true },
);

function openModal() {
  const couponList = (defaults.value.couponList && defaults.value.couponList.list) || [];
  proxy.$modalCoupon('wu', Date.now(), couponList, (couponObj) => getCouponId(couponObj), '', '');
}
//对象数组去重；
function unique(arr) {
  const res = new Map();
  return arr.filter((arr) => !res.has(arr.id) && res.set(arr.id, 1));
}
function getCouponIds() {
  const list = (defaults.value.couponList && defaults.value.couponList.list) || [];
  return list.map((item) => item.id);
}
function getCouponId(couponObj = []) {
  const list = unique(Array.isArray(couponObj) ? couponObj : []);
  defaults.value.couponList.list = list;
  ids.value = getCouponIds();
}
function bindDelete(index) {
  defaults.value.couponList.list.splice(index, 1);
  ids.value = defaults.value.couponList.list.map((item) => item.id);
}
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
