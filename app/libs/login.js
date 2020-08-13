import store from "../store";
import Cache from '../utils/cache';
// #ifdef H5 || APP-PLUS
import { isWeixin } from "../utils";
import auth from './wechat';
// #endif

import { LOGIN_STATUS, USER_INFO, EXPIRES_TIME, STATE_R_KEY} from './../config/cache';

function prePage(){
	let pages = getCurrentPages();
	let prePage = pages[pages.length - 1];
	return prePage.route;
}

export function toLogin(push, pathLogin) {
	store.commit("LOGOUT");
	let path = prePage();
	// #ifdef H5
	path = location.href;
	// #endif
	if(!pathLogin)
		pathLogin = '/page/users/login/index'
	Cache.set('login_back_url',path);
	// #ifdef H5 || APP-PLUS
	if (isWeixin()) {
		auth.oAuth();
	} else {
		if (path !== pathLogin) {
		 push ? uni.navigateTo({
		 	url:'/pages/users/login/index'
		 }) : uni.reLaunch({
		 	url: '/pages/users/login/index'
		 });
		}
	}
	// #endif
}


export function checkLogin()
{
	let token = Cache.get(LOGIN_STATUS);
	let expiresTime = Cache.get(EXPIRES_TIME);
	let newTime = Math.round(new Date() / 1000);
	if (expiresTime < newTime || !token){
		Cache.clear(LOGIN_STATUS);
		Cache.clear(EXPIRES_TIME);
		Cache.clear(USER_INFO);
		Cache.clear(STATE_R_KEY);
		return false;
	}else{
		store.commit('UPDATE_LOGIN',token);
		let userInfo = Cache.get(USER_INFO,true);
		if(userInfo){
			store.commit('UPDATE_USERINFO',userInfo);
		}
		return true;
	}

}
