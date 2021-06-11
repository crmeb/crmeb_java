<template>
  <div>
    <el-dialog
      title="优惠劵"
      :visible.sync="visible"
      width="896px"
      :before-close="handleClose"
    >
      <coupon-list v-if="visible" :handle="handle" :userIds="userIds" :couponData="coupons" @getCouponId="getCouponId" :keyNum="keyNum" :userType="userType"></coupon-list>
      <!--<upload-index v-if="visible" :isMore="isMore" @getImage="getImage" />-->
    </el-dialog>
  </div>
</template>

<script>
  import couponList from '../index.vue'
  export default {
    name: 'CouponFrom',
    components:{ couponList },
    data() {
      return {
        visible: false,
        callback: function() {},
        handle: '',
        keyNum: 0,
        coupons: [],
        userIds: '',
        userType: ''
      }
    },
    watch: {
      // show() {
      //   this.visible = this.show
      // }
    },
    methods: {
      handleClose() {
        this.visible = false
      },
      getCouponId(couponObj) {
        this.callback(couponObj)
        this.visible = false
      }
    }
  }
</script>

<style scoped>

</style>
