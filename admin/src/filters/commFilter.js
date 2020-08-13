import * as constants from '@/utils/constants.js'
import { formatDates } from '@/utils/index';

// 公共过滤器
export function filterEmpty(val) {
  let _result = '-'
  if (!val) {
    return _result
  }
  _result = val
  return _result
}

// 时间过滤器
export function formatDate(time) {
  if (time !== 0) {
    const date = new Date(time * 1000);
    return formatDates(date, 'yyyy-MM-dd hh:mm');
  }
}

export function filterYesOrNo(value) {
  return value ? '是' : '否'
}

export function filterShowOrHide(value) {
  return value ? '显示' : '不显示'
}

export function filterShowOrHideForFormConfig(value) {
  return value === '‘0’' ? '显示' : '不显示'
}

export function filterYesOrNoIs(value) {
  return value ? '否' : '是'
}

export function filterCategroyType(value) {
  return constants.categoryType.filter(item => value === item.value)[0].name
}

export function filterConfigCategory(value) {
  return constants.configCategory.filter(item => value === item.value)[0].label
}

/**
 * @description 公众号回复类型
 */
export function keywordStatusFilter(status) {
  const statusMap = {
    'text': '文字消息',
    'image': '图片消息',
    'news': '图文消息',
    'voice': '声音消息'
  }
  return statusMap[status]
}

/**
 * @description 优惠券类型
 */
export function couponUserTypeFilter(status) {
  const statusMap = {
    1: '通用券',
    2: '商品券',
    3: '品类券'
  }
  return statusMap[status]
}

/**
 * @description 优惠券领取方式
 */
export function couponTypeFilter(status) {
  const statusMap = {
    1: '手动领取',
    2: '新人券',
    3: '赠送券'
  }
  return statusMap[status]
}

/**
 * @description 文章分类
 */
export function articleTypeFilter(status) {
  if(!status){
    return ''
  }
  let arrayList = JSON.parse(localStorage.getItem('articleClass'));
  if(arrayList.filter(item => Number(status) === Number(item.id)).length < 1){
    return ''
  }
  return arrayList.filter(item => Number(status) === Number(item.id))[0].name
}
