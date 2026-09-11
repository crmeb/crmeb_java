import { HTTP_REQUEST_URL, HEADER, TOKENNAME } from '@/config/app';
import { LOGIN_STATUS } from '@/config/cache';
import { useAppStore } from "../store/app.js";
export function checkOverdue(data) {
	const appStore = useAppStore();
	const token = appStore.token || getCacheToken();
	let Url = HTTP_REQUEST_URL,header = { ...HEADER };
	if (token) header[TOKENNAME] = `Bearer ${token}`;
	if (!token) return;
	uni.request({
		url: Url + '/api/front/user',
		method: 'GET',
		header: header,
		success:(res) =>{
			if([410000, 410001, 410002, 401, 402].indexOf(res.data.code) !== -1){
				useAppStore().LOGOUT();
			}
		}
	})
}

function getCacheToken() {
	try {
		return uni.getStorageSync(LOGIN_STATUS) || '';
	} catch (e) {
		return '';
	}
}
