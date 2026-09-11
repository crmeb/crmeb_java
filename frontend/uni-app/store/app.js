// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
// Pinia 版本的 app store，替换原 Vuex store/modules/app.js
// 等价映射：
//   state.*       -> ref()
//   getters.*     -> computed / storeToRefs
//   mutations.*   -> 直接修改 ref 的函数（保持原 mutation 名，组件调用方式由 commit 改为直接调用）
//   actions.*     -> 异步函数

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
// #ifdef H5
import Auth from '@/libs/wechat';
// #endif
import { loginConfigApi, getTheme } from '@/api/api.js';
import { getOrderPayConfig } from '@/api/order.js';
import { getUserInfo } from '@/api/user.js';
import {
	LOGIN_STATUS,
	UID,
	PLATFORM,
	BOTTOM_NAVIGATION_ISCUSTOM,
	USER_INFO,
	EXPIRES_TIME,
	STATE_R_KEY,
	WX_AUTH
} from '@/config/cache';
import { TOKEN_EXPIRE_SECONDS } from '@/config/app.js';
import Cache from '@/utils/cache';

export const useAppStore = defineStore('app', () => {
	// ---------- state ----------
	const token = ref(Cache.get(LOGIN_STATUS) || '');
	const backgroundColor = ref('#fff');
	const userInfo = ref(Cache.get(USER_INFO) ? JSON.parse(Cache.get(USER_INFO)) : null);
	const uid = ref(Cache.get(UID) || null);
	const homeActive = ref(false);
	const chatUrl = ref(Cache.get('chatUrl') || '');
	const systemPlatform = ref(Cache.get(PLATFORM) ? Cache.get(PLATFORM) : '');
	const productType = ref(Cache.get('productType') || '');
	const bottomNavigationIsCustom = ref(
		Cache.get(BOTTOM_NAVIGATION_ISCUSTOM) ? Cache.get(BOTTOM_NAVIGATION_ISCUSTOM) : false
	);

	// 支付方式配置（原模块内 cartArr，仅 getPayConfig 使用）
	const cartArr = ref([
		{ name: '微信支付', icon: 'icon-weixinzhifu1', value: 'weixin', title: '微信快捷支付', payStatus: 1 },
		{ name: '余额支付', icon: 'icon-yuezhifu', value: 'yue', title: '可用余额:', payStatus: 1, userBalance: '' },
		// #ifndef MP
		{ name: '支付宝支付', icon: 'icon-zhifubao', value: 'alipay', title: '支付宝快捷支付', payStatus: 1 }
		// #endif
	]);

	// ---------- getters ----------
	const isLogin = computed(() => !!token.value);
	const globalData = computed(() => ({})); // 兼容原 getters.globalData，实际 globalData 由 App.vue 管理

	function getLoginExpireTime(opt = {}) {
		const now = Cache.time();
		const ttl = Number(opt.time);
		if (Number.isFinite(ttl) && ttl > 0) return now + ttl;

		const absoluteExpire = Number(opt.expires_time || opt.expiresTime || opt.expireTime);
		if (Number.isFinite(absoluteExpire) && absoluteExpire > now) return absoluteExpire;

		return now + TOKEN_EXPIRE_SECONDS;
	}

	// ---------- mutations ----------
	function LOGIN(opt) {
		if (!opt || !opt.token) {
			UPDATE_LOGIN('');
			return;
		}
		token.value = opt.token;
		Cache.set(LOGIN_STATUS, opt.token);
		Cache.set(EXPIRES_TIME, getLoginExpireTime(opt));
	}
	function SETUID(val) {
		uid.value = val;
		Cache.set(UID, val);
	}
	function UPDATE_LOGIN(newToken) {
		token.value = newToken;
		if (newToken) {
			Cache.set(LOGIN_STATUS, newToken);
			if (!Number(uni.getStorageSync(EXPIRES_TIME) || 0)) {
				Cache.set(EXPIRES_TIME, Cache.time() + TOKEN_EXPIRE_SECONDS);
			}
		} else {
			Cache.clear(LOGIN_STATUS);
			Cache.clear(EXPIRES_TIME);
		}
	}
	function LOGOUT() {
		token.value = undefined;
		uid.value = undefined;
		userInfo.value = null;
		Cache.clear(LOGIN_STATUS);
		Cache.clear(UID);
		Cache.clear(USER_INFO);
		// 补全登录态相关残留缓存，与 libs/login.js checkLogin 过期清理保持一致
		Cache.clear(EXPIRES_TIME);
		Cache.clear(STATE_R_KEY);
		Cache.clear(WX_AUTH);
		Cache.clear('snsapiKey');
	}
	function BACKGROUND_COLOR(color) {
		backgroundColor.value = color;
		// #ifdef H5
		document.body.style.backgroundColor = color;
		// #endif
	}
	function UPDATE_USERINFO(info) {
		userInfo.value = info;
		Cache.set(USER_INFO, info);
	}
	function OPEN_HOME() {
		homeActive.value = true;
	}
	function CLOSE_HOME() {
		homeActive.value = false;
	}
	function SET_CHATURL(url) {
		chatUrl.value = url;
	}
	function SYSTEM_PLATFORM(platform) {
		systemPlatform.value = platform;
		Cache.set(PLATFORM, platform);
	}
	// 更新 userInfo 某个字段
	function changInfo(payload) {
		userInfo.value[payload.amount1] = payload.amount2;
		Cache.set(USER_INFO, userInfo.value);
	}
	// 商品类型，用于区分视频号商品与一般商品
	function PRODUCT_TYPE(type) {
		productType.value = type;
		Cache.set('productType', type);
	}
	// 是否使用自定义导航
	function BottomNavigationIsCustom(val) {
		bottomNavigationIsCustom.value = val;
		Cache.set(BOTTOM_NAVIGATION_ISCUSTOM, val);
	}
	// 清除所有本地缓存
	function clearStorage() {
		uni.clearStorageSync();
	}

	// ---------- actions ----------
	function USERINFO(force) {
		return getUserInfo().then((res) => {
			UPDATE_USERINFO(res.data);
			return res.data;
		});
	}

	function getPayConfig(force) {
		return new Promise((resolve) => {
			getOrderPayConfig().then((res) => {
				const data = res.data;
				cartArr.value[0].payStatus = data.payWechatOpen ? 1 : 0;
				cartArr.value[1].payStatus = data.yuePayStatus ? 1 : 0;
				cartArr.value[1].userBalance = data.userBalance ? data.userBalance : 0;
				// #ifdef H5
				if (Auth.isWeixin()) {
					cartArr.value[2].payStatus = 0;
				} else {
					cartArr.value[2].payStatus = data.aliPayStatus ? 1 : 0;
				}
				// #endif
				// #ifdef APP-PLUS
				cartArr.value[2].payStatus = data.aliPayStatus ? 1 : 0;
				// #endif
				const cartArrs = cartArr.value.filter((e) => e.payStatus === 1);
				resolve({
					userBalance: data.userBalance,
					payConfig: cartArrs
				});
			});
		}).catch((err) => {
			return Promise.reject(err);
		});
	}

	/**
	 * 获取 diy 颜色配置
	 */
	function GetThemeConfig() {
		return new Promise((resolve) => {
			getTheme().then((resP) => {
				Cache.set('theme', `theme${Number(resP.data.value)}`);
				// #ifdef H5
				window.document.documentElement.setAttribute('data-theme', '');
				// #endif
				resolve();
			});
		}).catch((err) => {
			return Promise.reject(err);
		});
	}

	return {
		// state
		token,
		backgroundColor,
		userInfo,
		uid,
		homeActive,
		chatUrl,
		systemPlatform,
		productType,
		bottomNavigationIsCustom,
		// getters
		isLogin,
		globalData,
		// mutations
		LOGIN,
		SETUID,
		UPDATE_LOGIN,
		LOGOUT,
		BACKGROUND_COLOR,
		UPDATE_USERINFO,
		OPEN_HOME,
		CLOSE_HOME,
		SET_CHATURL,
		SYSTEM_PLATFORM,
		changInfo,
		PRODUCT_TYPE,
		BottomNavigationIsCustom,
		clearStorage,
		// actions
		USERINFO,
		getPayConfig,
		GetThemeConfig,
	};
});
