/**
 * 此处可直接引用自己项目封装好的 axios 配合后端联调
 */

import request from '../utils/axios'; // 组件内部封装的axios
 //import request from "@/api/axios.js"       //调用项目封装的axios


/**
 * 滑块验证
 * @param {Object} data
 */
export function ajcaptchaCheck(data) {
	return request.post("safety/check", data, {
		noAuth: true
	});
}

/**
 * 滑块信息
 * @param {Object} data
 */ 
export function getAjcaptcha(data) {
	return request.post("safety/get", data,  {
		noAuth: true
	});
}
