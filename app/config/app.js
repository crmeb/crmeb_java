// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
//移动端商城API
// 开发站
let domain = 'https://apif.java.crmeb.net'
// 演示站
// let domain = 'https://apif.java.crmeb.net'
// 深度演示站
// let domain = 'https://apif.crmeb.xbdzz.cn'

module.exports = {
	// 请求域名 格式： https://您的域名
	// #ifdef MP || APP-PLUS
		// HTTP_REQUEST_URL:'',
		HTTP_REQUEST_URL: domain,
		// H5商城地址
		// 开发站
		HTTP_H5_URL: 'http://app.beta.crmeb.xbdzz.cn',
		// 演示站
		// HTTP_H5_URL: 'http://java.crmeb.net',
		// 深度演示站
		// HTTP_H5_URL: 'crmeb.xbdzz.cn',
		// HTTP_H5_URL: 'java.9gt.net',
	// #endif
	// #ifdef H5
		HTTP_REQUEST_URL:domain,
	// #endif
	HEADER:{
		'content-type': 'application/json',
		'X-Source' : 'df07addc462f7f8f'
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
