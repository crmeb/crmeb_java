
var josn=require('../../domain.json')  
let domain = josn.domain

module.exports = {
	// 请求域名 格式： https://您的域名
	// #ifdef MP
		// HTTP_REQUEST_URL:'',
		HTTP_REQUEST_URL:domain,
	// #endif
	
	// #ifdef H5
		//H5接口是浏览器地址
		// HTTP_REQUEST_URL: window.location.protocol+"//"+window.location.host,
		// http://api.java.crmeb.net:20001
		HTTP_REQUEST_URL:domain,
	// #endif
	
	HEADER:{
		'content-type': 'application/json'
	},
	HEADERPARAMS:{
		'content-type': 'application/x-www-form-urlencoded'
	},
	// 回话密钥名称 请勿修改此配置
	TOKENNAME: 'Authori-zation',
	// 缓存时间 0 永久
	EXPIRE:0,
};
