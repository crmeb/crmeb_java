import Element from 'element-ui'
import '@/styles/element-variables.scss'
import uploadFromComponent from './index.vue'
import Vue from 'vue'
import Cookies from 'js-cookie'
Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})
const goodListFrom = {}
goodListFrom.install = function(Vue, options) {
  const ToastConstructor = Vue.extend(uploadFromComponent)
  // 生成一个该子类的实例
  const instance = new ToastConstructor()
  instance.$mount(document.createElement('div'))
  document.body.appendChild(instance.$el)
  Vue.prototype.$modalGoodList = function(callback, handleNum, row) {
    instance.visible = true
    instance.callback = callback
    instance.handleNum = handleNum
    instance.checked = row
  }
}
export default goodListFrom
