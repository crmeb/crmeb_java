// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import request from '@/utils/request'
/**
 * @description 附件分类 -- 所有分类
 */
export function formatLstApi(data) {
  return request.get({
    url: '/admin/product/save',
    method: 'POST',
    data
  })
}
/**
 * @description 附件分类 -- 添加分类
 */
export function attachmentCreateApi() {
  return request.get(`system/attachment/category/create/form`)
}
/**
 * @description 附件分类 -- 编辑分类
 */
export function attachmentUpdateApi(id) {
  return request.get(`system/attachment/category/update/form/${id}`)
}
/**
 * @description 附件分类 -- 删除分类
 */
export function attachmentDeleteApi(id) {
  return request.delete(`system/attachment/category/delete/${id}`)
}
/**
 * @description 图片列表
 */
export function attachmentListApi(data) {
  return request.get(`system/attachment/lst`, data)
}
/**
 * @description 图片列表 -- 删除
 */
export function picDeleteApi(id) {
  return request.delete(`system/attachment/delete`, id)
}
/**
 * @description 图片列表 -- 修改附件分类
 */
export function categoryApi(ids, attachment_category_id) {
  return request.post(`system/attachment/category`, { ids, attachment_category_id })
}
