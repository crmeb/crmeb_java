// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useErrorLogStore = defineStore('errorLog', () => {
  const logs = ref([]);

  function addErrorLog(log) {
    logs.value.push(log);
  }

  function clearErrorLog() {
    logs.value.splice(0);
  }

  return {
    logs,
    addErrorLog,
    clearErrorLog,
  };
});
