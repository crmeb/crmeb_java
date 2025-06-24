// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

const fsm = wx.getFileSystemManager ? wx.getFileSystemManager() : null;
const FILE_BASE_NAME = 'tmp_base64src'; //自定义文件名

export function base64src(base64data, dateminutes, cb) {
	const [, format, bodyData] = /data:image\/(\w+);base64,(.*)/.exec(base64data) || [];
	if (!format) {
		return (new Error('ERROR_BASE64SRC_PARSE'));
	}
	const filePath = `${wx.env.USER_DATA_PATH}/${dateminutes+FILE_BASE_NAME}.${format}`;
	const buffer = wx.base64ToArrayBuffer(bodyData);
	fsm.writeFile({
		filePath,
		data: buffer,
		encoding: 'binary',
		success() {
			cb(filePath);
		},
		fail() {
			return (new Error('ERROR_BASE64SRC_WRITE'));
		},
	});
};
//module.exports = base64src;
