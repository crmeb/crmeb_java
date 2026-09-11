// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import {
	HTTP_REQUEST_URL,
	HEADER,
	TOKENNAME,
	HEADERPARAMS
} from '@/config/app';
import { LOGIN_STATUS } from '@/config/cache';
import {
	toLogin,
	checkLogin
} from '../libs/login';
import { useAppStore } from "@/store/app.js";

// 在请求函数内按需获取 store 实例（Pinia 要求在 install 之后调用，
// 这里延迟到调用时获取，避免模块加载顺序问题）
function getAppStore() {
	return useAppStore();
}


/**
 * 发送请求
 */
function baseRequest(url, method, data, {
	noAuth = false,
	noVerify = false
}, params,prefix) {
	let Url = HTTP_REQUEST_URL;
	let header = { ...(params != undefined ? HEADERPARAMS : HEADER) };
	const appStore = getAppStore();
	if (!noAuth) {
		//登录过期自动登录
		if (!appStore.token && !checkLogin()) {
			toLogin();
			return Promise.reject({
				msg: '未登录'
			});
		}
	}
	const requestToken = appStore.token || getCacheToken();
	if (requestToken) header[TOKENNAME] = `Bearer ${requestToken}`;
	return new Promise((reslove, reject) => {
		uni.request({
			url: Url + `${prefix?'/api/public/':'/api/front/'}` + url,
			method: method || 'GET',
			header: header,
			data: data || {},
			timeout: 15000,
			success: (res) => {
				// 网关错误/服务端返回非 JSON（如 502 HTML）时 res.data 可能为字符串或 undefined，
				// 直接访问 res.data.code 会抛异常且 Promise 永远 pending，这里先做兜底。
				const data = res.data;
				if (res.statusCode < 200 || res.statusCode >= 300) {
					let msg = `请求异常(${res.statusCode})`;
					if (res.statusCode === 404) msg = '请求的资源不存在';
					else if (res.statusCode === 502 || res.statusCode === 504) msg = '服务器维护中，请稍后再试';
					else if (res.statusCode === 500) msg = '服务器开小差了，请稍后再试';
					return reject({ msg });
				}
				if (typeof data !== 'object' || data === null) {
					return reject({ msg: '响应数据格式异常' });
				}
				if (noVerify)
					reslove(data, res);
				else if (data.code == 200 || data.code == 0)
					reslove(data, res);
				else if ([410000, 410001, 410002, 401, 402].indexOf(data.code) !== -1) {
					if (!noAuth) toLogin();
					reject(data);
				} else if (data.code == 500) {
					reject({ msg: data.message || '系统异常' });
				} else if (data.code == 400) {
					reject({ msg: data.message || '参数校验失败' });
				} else if (data.code == 404) {
					reject({ msg: data.message || '没有找到相关数据' });
				} else if (data.code == 403) {
					reject({ msg: data.message || '没有相关权限' });
				} else
					reject({ msg: data.message || '系统错误' });
			},
			fail: (err) => {
				// err 为 uni.request 失败对象，含 errMsg 字段；透传错误信息供调用方区分超时/断网/DNS 失败
				let msg = '请求失败';
				const errMsg = (err && err.errMsg) || '';
				if (/timeout/i.test(errMsg)) msg = '请求超时，请检查网络后重试';
				else if (/fail|abort/i.test(errMsg)) msg = '网络连接失败，请检查网络设置';
				reject({ msg, errMsg });
			}
		})
	});
}

function getCacheToken() {
	try {
		return uni.getStorageSync(LOGIN_STATUS) || '';
	} catch (e) {
		return '';
	}
}

const request = {};

['options', 'get', 'post', 'put', 'head', 'delete', 'trace', 'connect'].forEach((method) => {
	request[method] = (api, data, opt, params,prefix) => baseRequest(api, method, data, opt || {}, params,prefix)
});



export default request;
