// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { useAppStore } from "../store/app.js";
import Cache from '../utils/cache';
import { Debounce } from '@/utils/validate.js'
// #ifdef H5 || APP-PLUS
import { isWeixin } from "../utils";
// #endif


import { LOGIN_STATUS, USER_INFO, EXPIRES_TIME, STATE_R_KEY, BACK_URL} from './../config/cache';
import { TOKEN_EXPIRE_SECONDS } from '@/config/app.js';

function prePage(){
	let pages = getCurrentPages();
	let prePage = pages[pages.length - 1];
	return prePage && prePage.$page ? prePage.$page.fullPath : '';
}

export const toLogin = Debounce(_toLogin,800)

export function _toLogin(push, pathLogin) {
	// 公众号登录方式(单选),1微信授权，2手机号登录/
	let publicLoginType = getApp().globalData.publicLoginType;
	const forceLogin = push === true;

	const appStore = useAppStore();
	appStore.LOGOUT();
	let path = prePage();
	// #ifdef H5
	path = location.href;
	path = location.pathname + location.search;
	// #endif
	if(!pathLogin){
		pathLogin = '/page/users/login/index'
		Cache.set(BACK_URL,path);
	}
	// 在 Cache.set 之后读取，确保拿到的是当前页路径而非刷新前残留的旧值
	let login_back_url = Cache.get(BACK_URL);

	// #ifdef H5
	if (isWeixin() && publicLoginType ==1) {
		if (location.pathname.indexOf('/pages/users/wechat_login/index') === -1) {
			uni.navigateTo({
				url: '/pages/users/wechat_login/index'
			})
		}
	} else {
		uni.navigateTo({
				url: '/pages/users/login/index'
			})
	}
	// #endif

	// #ifndef H5
	if (forceLogin || ['pages/user/index','/pages/user/index'].indexOf(login_back_url) == -1) {
		// #ifdef MP
		uni.navigateTo({
			 url: '/pages/users/wechat_login/index'
		})
		// #endif
		// #ifdef APP-PLUS
			uni.showModal({
			    title: '登录提示',
			    content: '登录以后可体验商城完整功能',
				cancelColor: '#000000',
				confirmColor: '#526BB1',
			    success: function (res) {
			        if (res.confirm) {
			           uni.navigateTo({
			           	url: '/pages/users/login/index'
			           })
			        } else if (res.cancel) {
			            // console.log('用户点击取消');
			        }
			    }
			});
		// #endif
	}
	// #endif
}


export function checkLogin()
{
	let token = Cache.get(LOGIN_STATUS);
	let expiresTime = Number(uni.getStorageSync(EXPIRES_TIME) || 0);
	let newTime = Math.round(new Date() / 1000);
	if (!token || (expiresTime && expiresTime < newTime)){
		Cache.clear(LOGIN_STATUS);
		Cache.clear(EXPIRES_TIME);
		Cache.clear(USER_INFO);
		Cache.clear(STATE_R_KEY);
		return false;
	}else{
		if (!expiresTime) {
			Cache.set(EXPIRES_TIME, newTime + TOKEN_EXPIRE_SECONDS);
		}
		const appStore = useAppStore();
		appStore.UPDATE_LOGIN(token);
		let userInfo = Cache.get(USER_INFO,true);
		if(userInfo){
			appStore.UPDATE_USERINFO(userInfo);
		}
		return true;
	}

}
