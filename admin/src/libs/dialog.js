// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

// 原实现依赖 vue-ydui（无 Vue3 版本），改用 element-plus 的 ElMessage/ElLoading
// 提供与原 $dialog 一致的接口：success / error / message / loading.open / loading.close / validateError
import { ElMessage, ElLoading, ElMessageBox, ElNotification } from '@/utils/elementPlusFeedback';

let loadingInstance = null;

const dialog = {
  // 基础 toast（原 ydui toast），用 ElMessage 实现
  toast(options = {}) {
    const { mes, timeout, callback, icon, ...rest } = options;
    const typeMap = { error: 'error', success: 'success' };
    const inst = ElMessage({
      message: mes,
      type: typeMap[icon] || 'info',
      duration: timeout || 1500,
      ...rest,
    });
    if (callback) setTimeout(callback, timeout || 1500);
    return inst;
  },
  // 原确认弹窗
  confirm(options) {
    return ElMessageBox.confirm(options.mes || '', options.title || '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    });
  },
  // 原 alert
  alert(options) {
    return ElMessageBox.alert(options.mes || '', options.title || '提示');
  },
  // 原通知
  notify(options) {
    return ElNotification(options);
  },
  // loading：原 ydui 为 loading.open(text)/loading.close()
  loading: {
    open(text = '加载中...') {
      loadingInstance = ElLoading.service({
        lock: true,
        text,
        background: 'rgba(0, 0, 0, 0.7)',
      });
    },
    close() {
      if (loadingInstance) {
        loadingInstance.close();
        loadingInstance = null;
      }
    },
  },
};

const icons = { error: '操作失败', success: '操作成功' };
Object.keys(icons).forEach((key) => {
  dialog[key] = (mes, obj = {}) => {
    return new Promise(function (resolve) {
      const message = mes || icons[key];
      ElMessage({
        message,
        type: key === 'success' ? 'success' : 'error',
        duration: 1000,
        ...obj,
      });
      setTimeout(() => resolve(), obj.timeout || 1000);
    });
  };
});

dialog.message = (mes = '操作失败', obj = {}) => {
  return new Promise(function (resolve) {
    ElMessage({
      message: mes,
      type: 'info',
      duration: 1000,
      ...obj,
    });
    setTimeout(() => resolve(), obj.timeout || 1000);
  });
};

dialog.validateError = (...args) => {
  validatorDefaultCatch(...args);
};

export function validatorDefaultCatch(err, type = 'message') {
  console.log(err);
  return dialog[type](err.errors[0].message);
}

export default dialog;
