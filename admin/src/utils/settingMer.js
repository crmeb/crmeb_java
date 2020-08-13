// 请求接口地址 如果没有配置自动获取当前网址路径
const VUE_APP_API_URL = process.env.VUE_APP_BASE_API || `${location.origin}`
const VUE_APP_WS_URL = process.env.VUE_APP_WS_URL || (location.protocol === 'https' ? 'wss' : 'ws') + ':' + location.hostname
const SettingMer = {
  // 服务器地址
  httpUrl: VUE_APP_API_URL,
  // 接口请求地址
  apiBaseURL: VUE_APP_API_URL + '/api/',
  // socket连接
  wsSocketUrl: VUE_APP_WS_URL
}

export default SettingMer
