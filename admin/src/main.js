import Vue from 'vue'

import Cookies from 'js-cookie'

import 'normalize.css/normalize.css' // a modern alternative to CSS resets

import Element from 'element-ui'
import './styles/element-variables.scss'

import '@/styles/index.scss' // global css

import VueAwesomeSwiper from 'vue-awesome-swiper'
import 'swiper/dist/css/swiper.css'
// 懒加载
import VueLazyload from 'vue-lazyload'

import App from './App'
import store from './store'
import router from './router'
import attrFrom from './components/attrFrom'
import uploadPicture from './components/uploadPicture/uploadFrom'
import goodListFrom from './components/goodList/goodListFrom'
import couponFrom from './components/couponList/couponFrom'
import articleFrom from './components/articleList/articleFrom'
import UploadIndex from '@/components/uploadPicture/index.vue'
import UploadFile from '@/components/Upload/uploadFile.vue'
import ueditorFrom from '@/components/ueditorFrom'
import VueUeditorWrap from 'vue-ueditor-wrap'
import iconFrom from './components/iconFrom'
// 切勿更改 此组件为表单生成中使用的图片上传组件
import SelfUpload from '@/components/uploadPicture/forGenrator/index.vue'
import modalAttr from '@/libs/modal-attr'
import modalIcon from '@/libs/modal-icon'
import { modalSure } from '@/libs/public'
import { loadScriptQueue } from '@/components/FormGenerator/utils/loadScript'

import './icons' // icon
import './permission' // permission control
import './utils/error-log' // error integralLog
import * as filters from './filters' // global filters

Vue.use(VueLazyload, {
  preLoad: 1.3,
  error: require('./assets/imgs/no.png'),
  loading: require('./assets/imgs/moren.jpg'),
  attempt: 1,
  listenEvents: ['scroll', 'wheel', 'mousewheel', 'resize', 'animationend', 'transitionend', 'touchmove']
})

Vue.use(uploadPicture)
Vue.use(goodListFrom)
Vue.use(couponFrom)
Vue.use(articleFrom)
Vue.use(VueAwesomeSwiper)
Vue.component('vue-ueditor-wrap', VueUeditorWrap)
Vue.component('attrFrom', attrFrom)
Vue.component('UploadIndex', UploadIndex)
Vue.component('SelfUpload', SelfUpload)
Vue.component('iconFrom', iconFrom)
Vue.component('ueditorFrom', ueditorFrom)
Vue.component('UploadFile', UploadFile)
Vue.prototype.$modalSure = modalSure
Vue.prototype.$modalAttr = modalAttr
Vue.prototype.$modalIcon = modalIcon
// Vue.prototype.$modalCoupon = modalCoupon
/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
if (process.env.NODE_ENV === 'production') {
  const { mockXHR } = require('../mock')
  mockXHR()
}

Vue.use(Element, {
  size: Cookies.get('size') || 'mini' // set element-ui default size
})

// register global utility filters
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

Vue.config.productionTip = false

const $previewApp = document.getElementById('previewApp')
const childAttrs = {
  file: '',
  dialog: ' width="600px" class="dialog-width" v-if="visible" :visible.sync="visible" :modal-append-to-body="false" '
}

window.addEventListener('message', init, false)

function buildLinks(links) {
  let strs = ''
  links.forEach(url => {
    strs += `<link href="${url}" rel="stylesheet">`
  })
  return strs
}

function init(event) {
  if (event.data.type === 'refreshFrame') {
    const code = event.data.data
    const attrs = childAttrs[code.generateConf.type]
    let links = ''

    if (Array.isArray(code.links) && code.links.length > 0) {
      links = buildLinks(code.links)
    }

    $previewApp.innerHTML = `${links}<style>${code.css}</style><div id="app"></div>`

    if (Array.isArray(code.scripts) && code.scripts.length > 0) {
      loadScriptQueue(code.scripts, () => {
        newVue(attrs, code.js, code.html)
      })
    } else {
      newVue(attrs, code.js, code.html)
    }
  }
}

function newVue(attrs, main, html) {
  // eslint-disable-next-line no-eval
  main = eval(`(${main})`)
  main.template = `<div>${html}</div>`
  new Vue({
    components: {
      child: main
    },
    data() {
      return {
        visible: true
      }
    },
    template: `<div><child ${attrs}/></div>`
  }).$mount('#app')
}

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
