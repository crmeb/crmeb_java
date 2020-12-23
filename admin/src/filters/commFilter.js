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


/**
 * @description 支付状态
 */
export function payStatusFilter(status) {
  const statusMap = {
    false: '未支付',
    true: '已支付'
  }
  return statusMap[status]
}

/**
 * @description 提现方式
 */
export function extractTypeFilter(status) {
  const statusMap = {
    'bank': '银行卡',
    'alipay': '支付宝',
    'weixin': '微信'
  }
  return statusMap[status]
}

/**
 * @description 充值类型
 */
export function rechargeTypeFilter(status) {
  const statusMap = {
    'public': '微信公众号',
    'weixinh5': '微信H5支付',
    'routine': '小程序'
  }
  return statusMap[status]
}

/**
 * @description 财务审核状态
 */
export function extractStatusFilter(status) {
  const statusMap = {
    '-1': '已拒绝',
    '0': '审核中',
    '1': '已提现'
  }
  return statusMap[status]
}

/**
 * @description 砍价状态
 */
export function bargainStatusFilter(status) {
  const statusMap = {
    '1': '进行中',
    '2': '未完成',
    '3': '已成功'
  }
  return statusMap[status]
}

/**
 * @description 砍价状态
 */
export function bargainColorFilter(status) {
  const statusMap = {
    '1': '',
    '2': 'danger',
    '3': 'success'
  }
  return statusMap[status]
}

/**
 * @description 拼团状态
 */
export function groupStatusFilter(status) {
  const statusMap = {
    '1': '进行中',
    '2': '已成功',
    '3': '未完成'
  }
  return statusMap[status]
}

/**
 * @description 拼团状态
 */
export function groupColorFilter(status) {
  const statusMap = {
    '1': '',
    '2': 'success',
    '3': 'danger'
  }
  return statusMap[status]
}

/**
 * @description 一号通tab值
 */
export function onePassTypeFilter(status) {
  const statusMap = {
    'sms': '短信',
    'copy': '商品采集',
    'expr_query': '物流查询',
    'expr_dump': '电子面单打印'
  }
  return statusMap[status]
}
