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
import CouponList from '@/components/couponList/index.vue';

export default function modalCoupon(handle, keyNum, callback, couponId = [], userIds) {
  return new Promise((resolve, reject) => {
    ElMessageBox({
      title: '优惠券列表',
      customClass: 'upload-form-temp',
      closeOnClickModal: false,
      showClose: false,
      message: h('div', { class: 'common-form-upload' }, [
        h(CouponList, {
          handle: handle,
          couponId: couponId,
          keyNum: keyNum,
          userIds: userIds,
          onGetCouponId(id) {
            callback(id);
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
