// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { createApp, h, defineComponent, defineAsyncComponent } from 'vue';
import Cookies from 'js-cookie';

import '@/styles/index.scss';
import 'normalize.css/normalize.css'; // a modern alternative to CSS resets
// Element Plus 组件样式改为按需注入：由 vite.config.js 中 ElementPlusResolver({ importStyle: 'css' }) 自动处理，
// 服务式 API（ElMessage/ElMessageBox/ElNotification/ElLoading）样式在 elementPlusFeedback.js 显式引入。
import { provideGlobalConfig } from 'element-plus/es/components/config-provider/src/hooks/use-global-config.mjs';
import zhCn from 'element-plus/es/locale/lang/zh-cn';

import './theme/element-variables.scss';
import '@/theme/index.scss'; // global css
import '@/assets/fonts/font.css'; // font css
import '@/assets/iconfont/iconfont';
import '@/assets/iconfont/iconfont.css';
import '@/assets/iconfont/iconfont-weapp-icon.css';

// v-viewer（图片预览）改为延迟加载：使用 v-viewer 指令的组件均为懒加载业务页（uploadPictures/goodsList/design），
// 非首屏必需。改为 mount 后动态 import + 注册，避免 v-viewer 进入入口 chunk。
import 'viewerjs/dist/viewer.css';

import { parseTime, resetForm, addDateRange, handleTree } from '@/utils/parsing';

import App from './App.vue';
import pinia from './store';
import router from './router';

import base from '@/components/base/index'; // 公共组件自动注册
import uploadPicture from './components/uploadPicture/uploadFrom/index.js';
import goodListFrom from './components/goodList/goodListFrom/index.js';
import couponFrom from './components/couponList/couponFrom/index.js';
import articleFrom from './components/articleList/articleFrom/index.js';
import CompatElSwitch from '@/components/ElementPlusCompat/Switch.vue';
import Pagination from '@/components/Pagination/index.vue';
import { ElLoading } from '@/utils/elementPlusFeedback';

import dialog from '@/libs/dialog';
import scroll from '@/libs/loading';
import schema from 'async-validator';
import Debounce from './libs/debounce.js'; //防抖自定义指令
import util from '@/utils/utils';
import modalParserFrom from '@/libs/modal-parserFrom';
import modalAttr from '@/libs/modal-attr';
import modalIcon from '@/libs/modal-icon';
import modalPrompt from '@/libs/modal-prompt';
import { modalSure } from '@/libs/public';
import { HandlePrice } from '@/utils/public';
import timeOptions from '@/libs/timeOptions';
import { loadScriptQueue } from '@/components/FormGenerator/utils/loadScript';
import { setupIcons } from './icons'; // icon
import './permission'; // permission control
import { setupErrorHandler } from './utils/error-log'; // error integralLog
import * as filters from './filters'; // global filters（Vue3 已无 Vue.filter，改挂 $filters）
import { parseQuery } from '@/utils';
import * as Auth from '@/libs/wechat';
import * as constants from '@/utils/constants.js';
import * as selfUtil from '@/utils/ZBKJIutil.js';
import SettingMer from '@/utils/settingMer';
import plugins from './plugins';
import directive from './directive'; //directive
import bus from '@/utils/bus'; // mitt 事件总线，替代 Vue.prototype.bus = new Vue()
// el-icon-xxx 已通过 CSS mask 方案全局兼容（src/theme/el-icons.scss），无需 JS 插件

const app = createApp(App);

// element-plus 全局尺寸统一使用 default，旧版尺寸配置统一归一化。
const rawSize = Cookies.get('size') || 'default';
const elSizeMap = { medium: 'default', mini: 'default', small: 'default', large: 'default', '': 'default' };
const elSize = elSizeMap[rawSize] || rawSize;
app.use(ElLoading);
provideGlobalConfig({ size: elSize, locale: zhCn }, app, true);

// SVG 图标组件
setupIcons(app);

// 事件总线挂到 globalProperties（兼容 this.bus 用法）
app.config.globalProperties.bus = bus;

// 全局公共组件自动注册
app.use(base);
// 单例弹窗插件
app.use(uploadPicture);
app.use(goodListFrom);
app.use(couponFrom);
app.use(articleFrom);
// 图片预览（v-viewer）改为延迟注册，见文件末尾 mount 后动态加载
app.use(plugins);
app.use(directive);

// 显式注册全局组件
app.component('attrFrom', defineAsyncComponent(() => import('./components/attrFrom/index.vue')));
app.component('UploadIndex', defineAsyncComponent(() => import('@/components/uploadPicture/index.vue')));
app.component('SelfUpload', defineAsyncComponent(() => import('@/components/uploadPicture/forGenrator/index.vue')));
app.component('WangEditor', defineAsyncComponent(() => import('@/components/wangEditor/index.vue')));
app.component('tinymce', defineAsyncComponent(() => import('@/components/Tinymce/index.vue')));
app.component('ElSwitch', CompatElSwitch);
app.component('iconFrom', defineAsyncComponent(() => import('./components/iconFrom/index.vue')));
app.component('uploadFile', defineAsyncComponent(() => import('@/components/Upload/uploadFile.vue')));
app.component('timeSelect', defineAsyncComponent(() => import('@/components/TimeSelect/index.vue')));
app.component(
  'common_wrapper',
  defineAsyncComponent(() => import('@/views/design/theme_editor/components/mobilePage/common_wrapper.vue')),
);
app.component('Pagination', Pagination);

// Vue3 已移除全局过滤器，改挂 $filters；模板中 {{ x | filter }} 需改为 {{ $filters.filter(x) }}
app.config.globalProperties.$filters = filters;

// 各类全局工具方法（保留 Vue2 时代的 this.$xxx 用法以减少迁移面）
app.config.globalProperties.$modalParserFrom = modalParserFrom;
app.config.globalProperties.$modalSure = modalSure;
app.config.globalProperties.$HandlePrice = HandlePrice;
app.config.globalProperties.$modalAttr = modalAttr;
app.config.globalProperties.$modalIcon = modalIcon;
app.config.globalProperties.$modalPrompt = modalPrompt;
app.config.globalProperties.$dialog = dialog;
app.config.globalProperties.$scroll = scroll;
app.config.globalProperties.$wechat = Auth;
app.config.globalProperties.$util = util;
app.config.globalProperties.$constants = constants;
app.config.globalProperties.$selfUtil = selfUtil;
app.config.globalProperties.$timeOptions = timeOptions;
app.config.globalProperties.$validator = function (rule) {
  return new schema(rule);
};
app.config.globalProperties.handleTree = handleTree;
app.config.globalProperties.parseTime = parseTime;
app.config.globalProperties.resetForm = resetForm;

// pinia / router
app.use(pinia);
app.use(router);

// 全局错误处理
setupErrorHandler(app);

// vconsole（调试控制台，按 query 或 cookie 触发）
let cookieName = 'VCONSOLE';
let query = parseQuery();
let urlSpread = query['spread'];
let vconsole = query[cookieName.toLowerCase()];
let md5Crmeb = 'b14d1e9baeced9bb7525ab19ee35f2d2'; //CRMEB MD5 加密开启vconsole模式
let md5UnCrmeb = '3dca2162c4e101b7656793a1af20295c'; //UN_CREMB MD5 加密关闭vconsole模式

if (vconsole !== undefined) {
  if (vconsole === md5UnCrmeb && Cookies.has(cookieName)) Cookies.remove(cookieName);
} else vconsole = Cookies.get(cookieName);

if (vconsole !== undefined && vconsole === md5Crmeb) {
  Cookies.set(cookieName, md5Crmeb, 3600);
  import('vconsole').then((Module) => {
    new Module.default();
  });
}

// 自定义实现 String 类型的 replaceAll 方法（旧浏览器兼容，保留）
if (!String.prototype.replaceAll) {
  String.prototype.replaceAll = function (s1, s2) {
    return this.replace(new RegExp(s1, 'gm'), s2);
  };
}

// 第三方统计脚本
var _hmt = _hmt || [];
function loadAnalyticsScript() {
  var hm = document.createElement('script');
  hm.src = 'https://cdn.oss.9gt.net/js/es.js?version=JAVA-KY-v3.1';
  var s = document.getElementsByTagName('script')[0];
  s.parentNode.insertBefore(hm, s);
}
if (window.requestIdleCallback) {
  window.requestIdleCallback(loadAnalyticsScript);
} else {
  window.setTimeout(loadAnalyticsScript, 1500);
}

// ============================================================
// FormGenerator 预览：运行时根据传入的 js/html 字符串构造子应用
// （Vue3 版本：用 createApp + 运行时模板编译）
// 注意：依赖 vue 的运行时编译器，已在 vite.config.js 中将 vue alias 到 esm-bundler 完整版。
// ============================================================
const $previewApp = document.getElementById('previewApp');
const childAttrs = {
  file: '',
  // Vue3 中 .sync 改为 v-model:visible；:modal-append-to-body 在 element-plus 中无此 prop
  dialog: ' width="600px" class="dialog-width" v-if="visible" v-model:visible="visible" ',
};

window.addEventListener('message', init, false);

function buildLinks(links) {
  let strs = '';
  links.forEach((url) => {
    strs += `<link href="${url}" rel="stylesheet">`;
  });
  return strs;
}

function init(event) {
  if (event.data.type === 'refreshFrame') {
    const code = event.data.data;
    const attrs = childAttrs[code.generateConf.type];
    let links = '';

    if (Array.isArray(code.links) && code.links.length > 0) {
      links = buildLinks(code.links);
    }

    $previewApp.innerHTML = `${links}<style>${code.css}</style><div id="app"></div>`;

    if (Array.isArray(code.scripts) && code.scripts.length > 0) {
      loadScriptQueue(code.scripts, () => {
        newVue(attrs, code.js, code.html);
      });
    } else {
      newVue(attrs, code.js, code.html);
    }
  }
}

async function newVue(attrs, main, html) {
  // eslint-disable-next-line no-eval
  main = eval(`(${main})`);
  // Vue3：用 defineComponent 包装，并通过 template 字符串 + 运行时编译渲染
  const Child = defineComponent({
    ...main,
    template: `<div>${html}</div>`,
  });
  const PreviewApp = defineComponent({
    components: { child: Child },
    data() {
      return {
        visible: true,
      };
    },
    render() {
      return h('div', [h(Child, { visible: this.visible })]);
    },
  });
  // 子应用独立实例，复用主应用的插件上下文
  const { elementComponentMap } = await import('@/components/FormGenerator/components/render/elementComponents');
  const subApp = createApp(PreviewApp);
  subApp.use(ElLoading);
  provideGlobalConfig({ size: 'default', locale: zhCn }, subApp, true);
  Object.keys(elementComponentMap).forEach((name) => {
    subApp.component(name, elementComponentMap[name]);
  });
  subApp.component('ElSwitch', CompatElSwitch);
  subApp.use(pinia);
  subApp.use(router);
  subApp.mount('#app');
}

// ============================================================
// 防抖自定义指令 v-debounceClick
// 传参：v-debounceClick="() =>{handleFun(arg)}"
// 不传参:v-debounceClick="handleFun"
// delayTime:延迟的时间,只执行最后一次
// ============================================================
app.directive('debounceClick', {
  // Vue3 钩子：bind -> beforeMount, inserted -> mounted
  beforeMount(el, binding, vnode, oldvnode) {},
  mounted(el, binding) {
    let delayTime = el.getAttribute('delay-time') || 500;
    el.onclick = Debounce(function () {
      binding.value();
    }, delayTime);
  },
});

// 暴露 app context 供单例弹窗插件使用（createVNode + render 时挂载 appContext）
window.__APP_CONTEXT__ = app._context;

app.mount('#app');

// v-viewer 延迟注册：mount 后动态加载并注册指令。
// 使用 v-viewer 指令的组件均为懒加载业务页，首屏不渲染，故此时注册不会错过任何已挂载组件。
// 用 requestIdleCallback 在浏览器空闲时加载，进一步降低对首屏交互的影响。
function setupViewer() {
  import('v-viewer').then((Module) => {
    const Viewer = Module.default;
    app.use(Viewer, {
      defaultOptions: {
        zIndex: 9999,
      },
    });
  });
}
if (window.requestIdleCallback) {
  window.requestIdleCallback(setupViewer, { timeout: 3000 });
} else {
  window.setTimeout(setupViewer, 1500);
}

export { SettingMer };
