// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
//移动端商城API
let domain = 'http://your Front API domain'

module.exports = {
	// 请求域名 格式： https://您的域名
	HTTP_REQUEST_URL:domain,
	
	// #ifdef MP
		HTTP_REQUEST_URL: domain,
	// #endif
	HTTP_ADMIN_URL:'http://your h5 domain', //PC后台的API请求地址，上传图片用,影响h5上传头像
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
	//分页最多显示条数
	LIMIT: 10
};
