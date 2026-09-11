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

export default async function modalAttr(val, callback, keyNum) {
  const { default: AttrFrom } = await import('@/components/attrFrom/index.vue');
  return new Promise((resolve, reject) => {
    ElMessageBox({
      title: '属性规格',
      customClass: 'upload-form',
      closeOnClickModal: false,
      showClose: true,
      message: h('div', { class: 'common-form-upload' }, [
        h(AttrFrom, {
          currentRow: val,
          keyNum: keyNum,
          onGetList() {
            callback();
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
        reject();
        ElMessage({
          type: 'info',
          message: '已取消',
        });
      });
  });
}
