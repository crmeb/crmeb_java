<template>
  <div>
    <el-dialog class="list-Dialog" title="优惠劵" v-model="visible" width="896px" :before-close="handleClose">
      <coupon-list
        v-if="visible"
        :handle="handle"
        :userIds="userIds"
        :couponData="coupons"
        @getCouponId="getCouponId"
        :keyNum="keyNum"
        :userType="userType"
        @closeDialog="closeDialog"
      ></coupon-list>
      <!--<upload-index v-if="visible" :isMore="isMore" @getImage="getImage" />-->
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import couponList from '../index.vue';

defineOptions({ name: 'CouponFrom' });

const visible = ref(false);
const callback = ref(function () {});
const handle = ref('');
const keyNum = ref(0);
const coupons = ref([]);
const userIds = ref('');
const userType = ref('');

function closeDialog() {
  visible.value = false;
}

function handleClose() {
  visible.value = false;
}

function getCouponId(couponObj) {
  callback.value(couponObj);
  visible.value = false;
}

function open(options = {}) {
  callback.value = typeof options.callback === 'function' ? options.callback : function () {};
  handle.value = options.handle;
  keyNum.value = options.keyNum;
  coupons.value = options.coupons ?? [];
  userIds.value = options.userIds ?? '';
  userType.value = options.userType ?? '';
  visible.value = true;
}

defineExpose({
  open,
  visible,
  callback,
  handle,
  keyNum,
  coupons,
  userIds,
  userType,
});
</script>

<style scoped>
.list-Dialog :deep(.el-dialog__body) {
  padding: 20px 24px 0 24px !important;
}
</style>
