// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import request from '@/utils/request';

/**
 * @description 协议管理-- 详情
 */
export function agreementInfoApi(data) {
  return request.get(`admin/agreement/${data}`);
}

/**
 * @description 协议管理-- 保存
 */
export function agreementSaveApi(save, data) {
  // return request.post(`admin/platform/agreement/${save}`, data);
  return request({
    url: `admin/agreement/${save}`,
    method: 'POST',
    data,
  });
}
