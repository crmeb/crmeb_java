export function modalSure(title) {
  console.log(title)
  return new Promise((resolve, reject) => {
    this.$confirm(`确定${title || '永久删除该文件'}?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
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


/**
 * @description 短信是否登录
 */
export function isLogin() {
  return new Promise((resolve, reject) => {
    isLoginApi().then(async res => {
      resolve(res)
    }).catch(res => {
      reject(res)
    })
  })
}
