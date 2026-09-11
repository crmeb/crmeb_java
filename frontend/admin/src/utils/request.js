// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import axios from 'axios';
import { ElMessage as Message } from '@/utils/elementPlusFeedback';
import { useUserStore } from '@/store/modules/user';
import { getToken } from '@/utils/auth';
import SettingMer from '@/utils/settingMer';
import { isPhone } from '@/libs/wechat';

const FORBIDDEN_MESSAGE = '暂无权限';

const service = axios.create({
  baseURL: SettingMer.apiBaseURL,
  timeout: 60000, // 过期时间
});

// request interceptor
service.interceptors.request.use(
  (config) => {
    // 发送请求之前做的
    const userStore = useUserStore();
    const token = !userStore.token ? sessionStorage.getItem('token') : userStore.token;
    config.headers['X-Source'] = 'df07addc462f7f8f';
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    if (/get/i.test(config.method)) {
      config.params = config.params || {};
      config.params.temp = Date.parse(new Date()) / 1000;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// response interceptor
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    // if the custom code is not 20000, it is judged as an error.
    if (res.code === 401) {
      // to re-login
      Message.error('无效的会话，或者登录已过期，请重新登录。');
      if (window.location.pathname !== '/login') location.href = '/login';
    } else if (res.code === 403) {
      const forbiddenResponse = {
        ...res,
        message: FORBIDDEN_MESSAGE,
        msg: FORBIDDEN_MESSAGE,
      };
      if (!response.config.silent) Message.error(FORBIDDEN_MESSAGE);
      return Promise.reject(forbiddenResponse);
    }
    if (![0, 200].includes(res.code) && res.code !== 401) {
      if (isPhone()) {
        //移动端
        return Promise.reject(res || 'Error');
      }
      if (!response.config.silent) {
        Message({
          message: res.message || 'Error',
          type: 'error',
          duration: 5 * 1000,
        });
      }
      return Promise.reject(res);
    } else {
      return res.data;
    }
  },
  (error) => {
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000,
    });
    return Promise.reject(error);
  },
);

export default service;
