export default function modalIcon(callback) {
  const h = this.$createElement
  return new Promise((resolve, reject) => {
    this.$msgbox({
      title: '菜单图标',
      customClass: 'upload-form',
      closeOnClickModal: false,
      showClose: false,
      message: h('div', { class: 'common-form-upload' }, [
        h('iconFrom', {
          on: {
            getIcon(n) {
              callback(n)
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
