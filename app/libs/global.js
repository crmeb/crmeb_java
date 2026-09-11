// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
/**
 * 全局使用的变量值
 * 原通过 Vue.prototype.$GLOBAL 挂载，Vue3 由 main.js 挂到
 * app.config.globalProperties.$GLOBAL，并可直接 import 使用。
 */
import Cache from '@/utils/cache.js'

const global = {
  //图片域名
  urlDomain: Cache.get('imgHost') ? Cache.get('imgHost') : ''
}

export default global
