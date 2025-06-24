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
 *  列表
 */
export function pagediyListApi(params) {
  return request({
    url: '/admin/pagediy/list',
    method: 'get',
    params,
  });
}
/**
 *  新增
 */
export function pagediySaveApi(data) {
  return request({
    url: '/admin/pagediy/save',
    method: 'post',
    data,
  });
}
/**
 *  更新
 */
export function pagediyUpdateApi(data) {
  return request({
    url: '/admin/pagediy/update',
    method: 'post',
    data,
  });
}
/**
 *详情
 */
export function pagediyInfoApi(id) {
  return request({
    url: `/admin/pagediy/info/${id}`,
    method: 'get',
  });
}
/**
 *删除
 */
export function pagediyDeleteApi(params) {
  return request({
    url: `/admin/pagediy/delete`,
    method: 'get',
    params,
  });
}
/**
 *设为首页
 */
export function pagediySetdefaultApi(id) {
  return request({
    url: `/admin/pagediy/setdefault/${id}`,
    method: 'get',
  });
}
/**
 *获取首页
 */
export function pagediyGetSetHome(id) {
  return request({
    url: `/admin/pagediy/getdefault`,
    method: 'get',
  });
}
/**
 * 获取小程序二维码
 * @returns {*}
 */
export function wechatQrcodeApi(data) {
  return request({
    url: `/public/wechat/mini/get/qrcode`,
    method: 'post',
    data,
  });
}
