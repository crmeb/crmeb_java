// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { h } from 'vue';
import { ElMessageBox, ElMessage } from '@/utils/elementPlusFeedback';

export default async function modalIcon(callback) {
  const { default: IconFrom } = await import('@/components/iconFrom/index.vue');
  return new Promise((resolve, reject) => {
    let selected = false;
    ElMessageBox({
      title: '菜单图标',
      customClass: 'upload-form',
      closeOnClickModal: false,
      showClose: true,
      message: h('div', { class: 'common-form-upload' }, [
        h(IconFrom, {
          onGetIcon(n) {
            selected = true;
            callback?.(n);
            resolve(n);
            ElMessageBox.close();
          },
        }),
      ]),
      showCancelButton: false,
      showConfirmButton: false,
    })
      .then(() => {
        resolve();
      })
      .catch(() => {
        if (selected) return;
        reject();
        ElMessage({
          type: 'info',
          message: '已取消',
        });
      });
  });
}
