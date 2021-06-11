import request from "@/utils/request.js";
import wechat from "@/libs/wechat.js";
import {
	toLogin,
	checkLogin
} from '../libs/login';
/**
 * 获取微信公众号js配置
 * @returns {*}
 */
export function getWechatConfig() {
  return request.get("wechat/config",{ url: encodeURIComponent(wechat.signLink()) },{ noAuth: true });
}

/**
 * 获取微信sdk配置
 * @returns {*}
 */
export function wechatAuth(code, spread) {
	var reg=/^[0-9]+.?[0-9]*$/; //判断字符串是否为数字 ，判断正整数用/^[1-9]+[0-9]*]*$/
	spread = reg.test(spread) ? spread : 0;
  return request.get(
    "wechat/authorize/login?code=" + code + "&spread_spid=" + spread, {},
    { noAuth: true }
  );
}

/**
 * 获取登录授权login
 * 
*/
export function getLogo()
{
	// wechat/get_logo
  return request.get('wechat/getLogo', {}, { noAuth : true});
}

/**
 * 小程序用户登录
 * @param data object 小程序用户登录信息
 */
export function login(code,data) {
  return request.post("wechat/authorize/program/login?code="+code, data, { noAuth : true });
}
/**
 * 分享
 * @returns {*}
 */
export function getShare() {
  return request.get("share", {}, { noAuth: true });
}

/**
 * 获取关注海报
 * @returns {*}
 */
export function follow() {
  return request.get("wechat/follow", {}, { noAuth: true });
}

/**
 * 获取图片base64
 * @retins {*}
 * */
export function imageBase64(image) {
  return request.post("qrcode/base64",image,{ noAuth: true },1);
}

/**
 * 自动复制口令功能
 * @returns {*}
 */
export function copyWords() {
  return request.get("copy_words", {}, { noAuth: true });
}

/**
 * 首页 获取客服地址
 * @returns {*}
 */
export function kefuConfig() {
  return request.get("config", {}, { noAuth: true });
}

/**
 * 微信（公众号，小程序）绑定手机号
 * @param {Object} data
 */
export function getUserPhone(data){
	return request.post('wechat/register/binding/phone',data,{noAuth : true});
}

/**
 * APP微信登录
 * @param {Object} data
 */
export function appAuth(data) {
	return request.post("wechat/authorize/app/login", data, { noAuth : true });
}

/**
 * 苹果登录
 * @param {Object} data
 */
export function appleLogin(data) {
	return request.post("ios/login", data, { noAuth : true });
}


/**
 * 苹果绑定手机号
 * @param {Object} data
 */
export function iosBinding(data) {
	return request.post("ios/binding/phone", data, { noAuth : true });
}