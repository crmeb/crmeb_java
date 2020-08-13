//会员过滤器

/**
 * 等级
 */
export function levelFilter(status) {
  if(!status){
    return ''
  }
  let arrayList = JSON.parse(localStorage.getItem('levelKey'));
  return arrayList.filter(item => status === item.id)[0].name
}

/**
 * 用户类型
 */
export function typeFilter(status) {
  const statusMap = {
    'wechat': '微信用户',
    'routine': '小程序用户',
    'h5': 'H5用户'
  }
  return statusMap[status]
}
