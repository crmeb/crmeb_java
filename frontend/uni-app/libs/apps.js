// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { appAuth } from '../api/public';
class Apps{
	/**
	 * 授权登录获取token
	 * @param {Object} code
	 */
	authApp(code) {
		return new Promise((resolve, reject) => {
			appAuth(code,{'spread_spid': 0})
				.then(({
					data
				}) => {
					resolve(data);
					Cache.set(WX_AUTH, code);
					Cache.clear(STATE_KEY);
					loginType && Cache.clear(LOGINTYPE);
					
				})
				.catch(reject);
		});
	}
}
export default new Apps();
