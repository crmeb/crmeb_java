export default function modalAttr(val, callback, keyNum) {
  const h = this.$createElement
  return new Promise((resolve, reject) => {
    this.$msgbox({
      title: '属性规格',
      customClass: 'upload-form',
      closeOnClickModal: false,
      showClose: false,
      message: h('div', { class: 'common-form-upload' }, [
        h('attrFrom', {
          props: {
            currentRow: val,
            keyNum: keyNum
          },
          on: {
            getList() {
              callback()
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
