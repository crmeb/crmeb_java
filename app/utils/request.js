import {
	HTTP_REQUEST_URL,
	HEADER,
	TOKENNAME,
	HEADERPARAMS
} from '@/config/app';
import {
	toLogin,
	checkLogin
} from '../libs/login';
import store from '../store';


/**
 * 发送请求
 */
function baseRequest(url, method, data, {
	noAuth = false,
	noVerify = false
}, params) {
	let Url = HTTP_REQUEST_URL,header = HEADER
	if (params != undefined) {
		header = HEADERPARAMS;
	}
	if (!noAuth) {
		//登录过期自动登录
		if (!store.state.app.token && !checkLogin()) {
			toLogin();
			return Promise.reject({
				msg: '未登录'
			});
		}
	}
	if (store.state.app.token) header[TOKENNAME] = store.state.app.token;
	return new Promise((reslove, reject) => {
		uni.request({
			url: Url + '/api/front/' + url,
			method: method || 'GET',
			header: header,
			data: data || {},
			success: (res) => {
				if (noVerify)
					reslove(res.data, res);
				else if (res.data.code == 200)
					reslove(res.data, res);
				else if ([410000, 410001, 410002, 401].indexOf(res.data.code) !== -1) {
					toLogin();
					reject(res.data);
				} else
					reject(res.data.message || '系统错误');
			},
			fail: (msg) => {
				reject('请求失败');
			}
		})
	});
}

const request = {};

['options', 'get', 'post', 'put', 'head', 'delete', 'trace', 'connect'].forEach((method) => {
	request[method] = (api, data, opt, params) => baseRequest(api, method, data, opt || {}, params)
});



export default request;
