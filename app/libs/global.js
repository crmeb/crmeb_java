/**
 * 全局使用的变量值
 */
import Cache from '@/utils/cache'
import Vue from 'vue'
const global = {
  //图片域名
  urlDomain: Cache.get('imgHost')?Cache.get('imgHost'):''
}
Vue.prototype.$GLOBAL = global