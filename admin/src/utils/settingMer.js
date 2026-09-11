// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

// 请求接口地址 如果没有配置自动获取当前网址路径
const VITE_APP_API_URL = import.meta.env.VITE_APP_BASE_API || `${location.origin}`;
const VITE_APP_WS_URL =
  import.meta.env.VITE_APP_WS_URL || (location.protocol === 'https' ? 'wss' : 'ws') + ':' + location.hostname;
const SettingMer = {
  // 服务器地址
  httpUrl: VITE_APP_API_URL,
  // 接口请求地址
  apiBaseURL: VITE_APP_API_URL + '/api/',
  // socket连接
  wsSocketUrl: VITE_APP_WS_URL,
};

export default SettingMer;
