import Element from 'element-ui'
import '@/styles/element-variables.scss'
import uploadFromComponent from './index.vue'
import Vue from 'vue'
import Cookies from 'js-cookie'
Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})
const uploadFrom = {}
uploadFrom.install = function(Vue, options) {
  const ToastConstructor = Vue.extend(uploadFromComponent)
  // 生成一个该子类的实例
  const instance = new ToastConstructor()
  instance.$mount(document.createElement('div'))
  document.body.appendChild(instance.$el)
  Vue.prototype.$modalUpload = function(callback, isMore, modelName, boolean) {
    instance.visible = true
    instance.callback = callback
    instance.isMore = isMore
    instance.modelName = modelName
    instance.booleanVal = boolean
  }
}
export default uploadFrom
