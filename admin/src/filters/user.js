// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

//会员过滤器

/**
 * 等级
 */
export function levelFilter(status) {
  if(!status){
    return ''
  }
  let arrayList = JSON.parse(localStorage.getItem('levelKey'));
  let array = arrayList.filter(item => status === item.id)
  if(array.length){
    return array[0].name
  }else{
    return ''
  }
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

/**
 * 用户类型
 */
export function filterIsPromoter(status) {
  const statusMap = {
    true: '推广员',
    false: '普通用户'
  }
  return statusMap[status]
}
