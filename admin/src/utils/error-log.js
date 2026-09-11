// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { nextTick } from 'vue';
import { isString, isArray } from '@/utils/validate';
import settings from '@/settings';

// you can set in settings.js
// errorLog:'production' | ['production', 'development']
const { errorLog: needErrorLog } = settings;

function checkNeed() {
  const env = import.meta.env.MODE;
  if (isString(needErrorLog)) {
    return env === needErrorLog;
  }
  if (isArray(needErrorLog)) {
    return needErrorLog.includes(env);
  }
  return false;
}

/**
 * 安装全局错误处理（Vue3）
 * @param {import('vue').App} app
 */
export function setupErrorHandler(app) {
  if (!checkNeed()) return;
  app.config.errorHandler = function (err, vm, info) {
    // 延迟派发，避免在错误处理中再次触发响应式更新
    nextTick(() => {
      // 动态引入避免循环依赖（pinia store 内部可能依赖本模块）
      import('@/store/modules/errorLog').then((mod) => {
        const useErrorLogStore = mod.default;
        useErrorLogStore().addErrorLog({
          err,
          vm,
          info,
          url: window.location.href,
        });
      });
    });
  };
}
