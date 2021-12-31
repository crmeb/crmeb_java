// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'
import SettingMer from '@/utils/settingMer'
import { isPhone } from "@/libs/wechat";
const service = axios.create({
  baseURL: SettingMer.apiBaseURL,
  timeout: 60000 // 过期时间
})

// request interceptor
service.interceptors.request.use(
  config => {
    // 发送请求之前做的
    const token = !store.getters.token?sessionStorage.getItem('token'):store.getters.token;
    if (token) {
      config.headers['Authori-zation'] = token
    }
    if(/get/i.test(config.method)){
      config.params = config.params || {}
      config.params.temp= Date.parse(new Date()) / 1000
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    // if the custom code is not 20000, it is judged as an error.
    if (res.code === 401) {
      // to re-login
      Message.error('无效的会话，或者登录已过期，请重新登录。');
      location.href = '/login';
    }else if(res.code === 403){
      Message.error('没有权限访问。');
    }
    if (res.code !== 200  && res.code !== 401) {
      if (isPhone()) { //移动端
        return Promise.reject(res || 'Error')
      }
      Message({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject()
    } else {
      return res.data
    }
  },
  error => {
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
