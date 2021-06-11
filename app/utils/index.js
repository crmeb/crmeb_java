import { spread } from "@/api/user";
import Cache from "@/utils/cache";

/**
 * 静默授权绑定上下级，使用在已经登录后扫描了别人的推广二维码
 * @param {Object} puid
 */
export function silenceBindingSpread() {


	//#ifdef H5
	let puid = Cache.get('spread');
	//#endif

	//#ifdef MP
	let puid = getApp().globalData.spid;
	//#endif

	puid = parseInt(puid);
	if (Number.isNaN(puid)) {
		puid = 0;
	}
	if (puid) {
		spread(puid).then(res => {}).catch(res => {});
		//#ifdef H5
		Cache.clear('spread');
		//#endif

		//#ifdef MP
		getApp().globalData.spid = 0;
		getApp().globalData.code = 0;
		//#endif
	} else {
		Cache.set('spread', 0);
	}
}

export function isWeixin() {
	return navigator.userAgent.toLowerCase().indexOf("micromessenger") !== -1;
}

export function parseQuery() {
	const res = {};

	const query = (location.href.split("?")[1] || "")
		.trim()
		.replace(/^(\?|#|&)/, "");

	if (!query) {
		return res;
	}

	query.split("&").forEach(param => {
		const parts = param.replace(/\+/g, " ").split("=");
		const key = decodeURIComponent(parts.shift());
		const val = parts.length > 0 ? decodeURIComponent(parts.join("=")) : null;

		if (res[key] === undefined) {
			res[key] = val;
		} else if (Array.isArray(res[key])) {
			res[key].push(val);
		} else {
			res[key] = [res[key], val];
		}
	});

	return res;
}

// #ifdef H5
const VUE_APP_WS_URL = process.env.VUE_APP_WS_URL || `ws://${location.hostname}:20001`;
export {
	VUE_APP_WS_URL
}
// #endif



export default parseQuery;
