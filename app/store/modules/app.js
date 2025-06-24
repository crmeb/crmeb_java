// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import Auth from '../../libs/wechat';
import store from "../index";
import { tokenIsExistApi,loginConfigApi,getTheme } from '@/api/api.js';
import {
	getOrderPayConfig
} from '../../api/order.js';
import {
	getUserInfo,
	computeUser
} from "../../api/user.js";
import {
	LOGIN_STATUS,
	UID,
	PLATFORM,
	BOTTOM_NAVIGATION_ISCUSTOM
} from '../../config/cache';
import Cache from '../../utils/cache';
import {
	USER_INFO
} from '../../config/cache';
let cartArr = [{
		name: "微信支付",
		icon: "icon-weixinzhifu1",
		value: 'weixin',
		title: '微信快捷支付',
		payStatus: 1,
	},
	{
		name: "余额支付",
		icon: "icon-yuezhifu",
		value: 'yue',
		title: '可用余额:',
		payStatus: 1,
		userBalance: ''
	}
];
const state = {
	token: Cache.get(LOGIN_STATUS) || '',
	backgroundColor: "#fff",
	userInfo: Cache.get(USER_INFO) ? JSON.parse(Cache.get(USER_INFO)) : null,
	uid: Cache.get(UID) || null,
	homeActive: false,
	chatUrl: Cache.get('chatUrl') || '',
	systemPlatform: Cache.get(PLATFORM) ? Cache.get(PLATFORM) : '',
	productType: Cache.get('productType') || '',
	bottomNavigationIsCustom: Cache.get('BOTTOM_NAVIGATION_ISCUSTOM') ? Cache.get('BOTTOM_NAVIGATION_ISCUSTOM') : false, //是否使用自定义导航
};

const mutations = {
	LOGIN(state, opt) {
		state.token = opt.token;
		Cache.set(LOGIN_STATUS, opt.token);
	},
	SETUID(state, val) {
		state.uid = val;
		Cache.set(UID, val);
	},
	UPDATE_LOGIN(state, token) {
		state.token = token;
	},
	LOGOUT(state) {
		state.token = undefined;
		state.uid = undefined
		Cache.clear(LOGIN_STATUS);
		Cache.clear(UID);
		Cache.clear(USER_INFO);
	},
	BACKGROUND_COLOR(state, color) {
		state.color = color;
		document.body.style.backgroundColor = color;
	},
	UPDATE_USERINFO(state, userInfo) {
		state.userInfo = userInfo;
		Cache.set(USER_INFO, userInfo);
	},
	OPEN_HOME(state) {
		state.homeActive = true;
	},
	CLOSE_HOME(state) {
		state.homeActive = false;
	},
	SET_CHATURL(state, chatUrl) {
		state.chatUrl = chatUrl;
	},
	SYSTEM_PLATFORM(state, systemPlatform) {
		state.systemPlatform = systemPlatform;
		Cache.set(PLATFORM, systemPlatform);
	},
	//更新useInfo数据
	changInfo(state, payload) {
		state.userInfo[payload.amount1] = payload.amount2;
		Cache.set(USER_INFO, state.userInfo);
	},
	//商品类型，用于区分一般商品
	PRODUCT_TYPE(state, productType) {
		state.productType = productType;
		Cache.set('productType', productType);
	},
	/** 是否使用自定义导航 **/
	BottomNavigationIsCustom: (state, bottomNavigationIsCustom) => {
		state.bottomNavigationIsCustom = bottomNavigationIsCustom
		Cache.set(BOTTOM_NAVIGATION_ISCUSTOM, bottomNavigationIsCustom);
	},
	//清除所有本地缓存
	clearStorage(state) {
		uni.clearStorageSync();
	},
};

const actions = {
	USERINFO({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			getUserInfo().then(res => {
				commit("UPDATE_USERINFO", res.data);
				reslove(res.data);
			});
		}).catch(() => {
			commit("LOGOUT");
		});
	},
	getPayConfig({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			getOrderPayConfig().then(res => {
				let data = res.data;
				cartArr[0].payStatus = data.payWechatOpen ? 1 : 0;
				cartArr[1].payStatus = data.yuePayStatus ? 1 : 0;
				cartArr[1].userBalance = data.userBalance ? data.userBalance : 0;
				// #ifdef H5
				// if (Auth.isWeixin()) {
				// 	cartArr[2].payStatus = 0;
				// } else {
				// 	cartArr[2].payStatus = data.aliPayStatus ? 1 : 0;
				// }
				// #endif
				// #ifdef APP-PLUS
				// cartArr[2].payStatus = data.aliPayStatus ? 1 : 0;
				// #endif
				let cartArrs = cartArr.filter(e => e.payStatus === 1);
				reslove({
					userBalance: data.userBalance,
					payConfig: cartArrs
				});
			})
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	},
	/**
	 * 校验token是否有效,true为有效，false为无效
	 */
	GetTokenIsExist({
		state,
		commit
	}, force) {
		return new Promise(reslove => {
			tokenIsExistApi().then(res => {
				if (!res.data) {
					store.commit('UPDATE_LOGIN', '');
					store.commit('UPDATE_USERINFO', {});
					store.commit('SETUID', '');
				}
				reslove(res.data);
			});
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	},
	//获取diy颜色配置
	GetThemeConfig({
		state,
		commit
	}) {
		return new Promise(reslove => {
			getTheme().then(resP => {
				Cache.set('theme', `theme${Number(resP.data.value)}`);
				// #ifdef H5
				window.document.documentElement.setAttribute('data-theme', state.globalData.theme);
				// #endif
			})
		}).catch(err => {
			return util.Tips({
				title: err
			});
		});
	},
};


export default {
	state,
	mutations,
	actions
};
