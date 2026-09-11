// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------

const oneDay = 3600 * 1000 * 24

function offsetDate(days) {
  const date = new Date()
  date.setTime(date.getTime() - oneDay * days)
  return date
}

export default {
  shortcuts: [
    {
      text: '昨天',
      value() {
        const end = new Date()
        const start = offsetDate(1)
        return [start, end]
      }
    },
    {
      text: '最近七天',
      value() {
        const end = new Date()
        const start = offsetDate(7)
        return [start, end]
      }
    },
    {
      text: '本月',
      value() {
        const end = new Date()
        const now = new Date()
        const start = new Date(now.getFullYear(), now.getMonth(), 1)
        return [start, end]
      }
    },
    {
      text: '最近30天',
      value() {
        const end = new Date()
        const start = offsetDate(30)
        return [start, end]
      }
    },
    {
      text: '最近一年',
      value() {
        const end = new Date()
        const start = offsetDate(365)
        return [start, end]
      }
    }
  ],
  disabledDate(time) {
    let curDate = new Date().getTime()
    let three = 365 * 24 * 3600 * 1000
    let threeMonths = curDate - three
    return time.getTime() > Date.now() || time.getTime() < threeMonths
  }
}
