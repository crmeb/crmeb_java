import {HTTP_REQUEST_URL,HEADER,TOKENNAME,HEADERPARAMS} from '@/config/app';
import store from "../store";
export function checkOverdue(data) {
	let Url = HTTP_REQUEST_URL,header = HEADER;
	uni.request({
		url: Url + '/api/front/user',
		method: 'GET',
		header: header,
		success:(res) =>{
			if([410000, 410001, 410002, 401].indexOf(res.data.code) !== -1){
				store.commit("LOGOUT");
			}
		}
	})
}