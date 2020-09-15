//小程序 微信过滤器
import Cookies from 'js-cookie'
/**
 * @description 小程序所属类目
 */
export function wxCategoryFilter(status) {
  if(!status){
    return ''
  }
  if(!Cookies.get('WxCategory')) {
    return
  }
  let arrayList = JSON.parse(Cookies.get('WxCategory'));
  if(arrayList.filter(item => Number(status) === Number(item.id)).length < 1){
    return ''
  }
  return arrayList.filter(item => Number(status) === Number(item.id))[0].name
}

/**
 * @description 小程序模板类型
 */
export function wxTypeFilter(status) {
  const statusMap = {
    2: '一次性订阅',
    3: '长期订阅'
  }
  return statusMap[status]
}
