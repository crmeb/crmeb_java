<template>
  <div>
    <el-dialog class="list-Dialog" title="优惠劵" :visible.sync="visible" width="896px" :before-close="handleClose">
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

<script>
import couponList from '../index.vue';
export default {
  name: 'CouponFrom',
  components: { couponList },
  data() {
    return {
      visible: false,
      callback: function () {},
      handle: '',
      keyNum: 0,
      coupons: [],
      userIds: '',
      userType: '',
    };
  },
  watch: {
    // show() {
    //   this.visible = this.show
    // }
  },
  methods: {
    closeDialog() {
      this.visible = false;
    },
    handleClose() {
      this.visible = false;
    },
    getCouponId(couponObj) {
      this.callback(couponObj);
      this.visible = false;
    },
  },
};
</script>

<style scoped>
.list-Dialog ::v-deep .el-dialog__body {
  padding: 20px 24px 0 24px !important;
}
</style>
