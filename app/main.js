// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { createSSRApp } from 'vue'
import App from './App.vue'
import { createPinia, setActivePinia } from 'pinia'
import util from '@/utils/util'
import configs from '@/config/app.js'
import Cache from '@/utils/cache'
import global from '@/libs/global.js'
import apps from '@/libs/apps.js' //校验登录是否失效
import * as Order from '@/libs/order';
import { eventBus } from '@/utils/eventBus';
import skeleton from '@/components/skeleton/index.vue'
import easyLoadimage from '@/components/base/easy-loadimage.vue'
import BaseMoney from '@/components/BaseMoney.vue'
import BaseDrawer from '@/components/tuiDrawer/tui-drawer.vue'

// #ifdef H5
import { parseQuery } from '@/utils'
import Auth from '@/libs/wechat';
import { SPREAD } from '@/config/cache';
// #endif

// 在 createSSRApp 之前创建并激活 Pinia，
// 确保 App.vue onLaunch 中 useAppStore() 可用
const pinia = createPinia()
setActivePinia(pinia)

// #ifdef H5
const cookieName = 'VCONSOLE';
let query = parseQuery(),
	urlSpread = query['spread'],
	vconsole = query[cookieName.toLowerCase()],
	md5Crmeb = 'b14d1e9baeced9bb7525ab19ee35f2d2', //CRMEB MD5 加密开启 vconsole 模式
	md5UnCrmeb = '3dca2162c4e101b7656793a1af20295c'; //UN_CREMB MD5 加密关闭 vconsole 模式

function loadVConsole() {
	const script = document.createElement('script');
	script.src = `${import.meta.env.BASE_URL}static/js/vconsole.min.js`;
	script.onload = () => {
		// eslint-disable-next-line no-new
		new window.VConsole();
	};
	script.onerror = () => {
		console.error('VConsole 加载失败');
	};
	document.head.appendChild(script);
}

if (urlSpread) {
	urlSpread = parseInt(urlSpread);
	Cache.setItem({
		name: 'spread',
		value: urlSpread,
	})
}
if (vconsole !== undefined) {
	if (vconsole === md5UnCrmeb && Cache.has(cookieName))
		Cache.clear(cookieName);
} else vconsole = Cache.get(cookieName);

if (vconsole !== undefined && vconsole === md5Crmeb) {
	Cache.set(cookieName, md5Crmeb, 3600);
	loadVConsole();
}
// #endif

export function createApp() {
	const app = createSSRApp(App)

	// Pinia 状态管理
	app.use(pinia)

	// 全局组件
	app.component('skeleton', skeleton)
	app.component('easyLoadimage', easyLoadimage)
	app.component('BaseMoney', BaseMoney)
	app.component('baseMoney', BaseMoney)
	app.component('base-drawer', BaseDrawer)
	app.component('baseDrawer', BaseDrawer)

	// 全局属性（替代 Vue.prototype.$xxx）
	app.config.globalProperties.$util = util
	app.config.globalProperties.$config = configs
	app.config.globalProperties.$Cache = Cache
	app.config.globalProperties.$Order = Order
	app.config.globalProperties.$LoginAuth = apps
	app.config.globalProperties.$GLOBAL = global
	app.config.globalProperties.$eventHub = eventBus
	app.config.globalProperties.$t = (text) => text
	// #ifdef H5
	app.config.globalProperties.$wechat = Auth
	// #endif

	return {
		app,
		Pinia: pinia
	}
}
