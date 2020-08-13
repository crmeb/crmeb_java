import Element from 'element-ui'
import '@/styles/element-variables.scss'
import articleFromComponent from './index.vue'
import Vue from 'vue'
import Cookies from 'js-cookie'
Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})
const articleFrom = {}
articleFrom.install = function(Vue, options) {
  const ToastConstructor = Vue.extend(articleFromComponent)
  // 生成一个该子类的实例
  const instance = new ToastConstructor()
  instance.$mount(document.createElement('div'))
  document.body.appendChild(instance.$el)
  Vue.prototype.$modalArticle = function(callback, handle) {
    instance.visible = true
    instance.callback = callback
    instance.handle = handle
  }
}
export default articleFrom
