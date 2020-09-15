/*
/ 全局静态变量定义
切勿随意修改数组次序，很多地方已下标方式使用
 */

export const page = {
  limit: [20, 40, 60, 80, 100],
  page: 1,
  layout: 'total, sizes, prev, pager, next, jumper'
}

/**
 * 无限极分类type
 * @type {{product: number, attachment: number, menu: number, article: number, operator: number}}
 */
export const categoryType = [ // 1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类， 6 配置分类， 7 秒杀配置
  { name: '产品分类', value: 1, shortName: '产品' },
  { name: '附件分类', value: 2, shortName: '附件' },
  { name: '文章分类', value: 3, shortName: '文章' },
  { name: '设置分类', value: 4, shortName: '设置' },
  { name: '菜单分类', value: 5, shortName: '菜单' },
  { name: '配置分类', value: 6, shortName: '配置' },
  { name: '秒杀配置', value: 7, shortName: '秒杀' }
]

export const roleListStatus = [
  { label: '全部', value: '' },
  { label: '显示', value: 1 },
  { label: '不显示', value: 0 }
]

export const showHiddenStatus = [
  { label: '显示', value: '‘1’' },
  { label: '不显示', value: '‘0’' }
]

export const switchStatus = [
  { label: '开启', value: 1 },
  { label: '关闭', value: 0 }
]

export const deletedOrNormal = [
  { label: '正常', value: 0 },
  { label: '已删除', value: 1 }
]

/**
 * 暂时弃用
 * @type {*[]}
 */
export const configCategory = [
  { label: '系统', value: '0' },
  { label: '应用', value: '1' },
  { label: '支付', value: '2' },
  { label: '其他', value: '3' }
]

/**
 * 表单配置集合集中配置
 * @type {{id: number, dis: string}[]}
 */
export const formConfigIds = [
  { id: 84, dis: '微信公众号表单配置' },
  { id: 86, dis: '秒杀配置' }
]

/**
 * 时间选择器
 */
export const fromList = {
  title: '选择时间',
  custom: true,
  fromTxt: [
    { text: '全部', val: '' },
    { text: '今天', val: 'today' },
    { text: '昨天', val: 'yesterday' },
    { text: '最近7天', val: 'lately7' },
    { text: '最近30天', val: 'lately30' },
    { text: '本月', val: 'month' },
    { text: '本年', val: 'year' }
  ]
}
