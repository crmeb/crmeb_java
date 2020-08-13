export default function modalCoupon(handle, keyNum, callback, couponId=[], userIds) {
  const h = this.$createElement
  return new Promise((resolve, reject) => {
    this.$msgbox({
      title: '优惠券列表',
      customClass: 'upload-form-temp',
      closeOnClickModal: false,
      showClose: false,
      message: h('div', { class: 'common-form-upload' }, [
        h('couponList', {
          props: {
            handle: handle,
            couponId: couponId,
            keyNum: keyNum,
            userIds: userIds
          },
          on: {
            getCouponId(id) {
              callback(id)
            }
          }
        })
      ]),
      showCancelButton: false,
      showConfirmButton: false
    }).then(() => {
      resolve()
    }).catch(() => {
      reject()
      this.$message({
        type: 'info',
        message: '已取消'
      })
    })
  })
}
