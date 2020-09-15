import request from "@/utils/request.js";
import wechat from "@/libs/wechat.js";

/**
 * 获取微信公众号js配置
 * @returns {*}
 */
export function getWechatConfig() {
  return request.get("wechat/config",{ url: encodeURIComponent(wechat.signLink()) },{ noAuth: true });
}

// export function getWechatConfig() {
//   return request.get(
//     "wechat/config",
//     { url: encodeURIComponent(location.href.split('#')[0]) },
//     { noAuth: true }
//   );
// }

/**
 * 获取微信sdk配置
 * @returns {*}
 */
export function wechatAuth(code, spread, login_type) {
  return request.get(
    "wechat/authorize/login",
    { code, spread_spid:spread, login_type },
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

