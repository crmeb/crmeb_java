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

export function configCheckUnique(pram) {
  const data = {
    name: pram.name,
  };
  return request({
    url: '/admin/system/config/check',
    method: 'GET',
    params: data,
  });
}

export function configDelete(pram) {
  const data = {
    id: pram.id,
  };
  return request({
    url: '/admin/system/config/delete',
    method: 'GET',
    params: data,
  });
}

export function configInfo(pram) {
  const data = {
    formId: pram.id,
  };
  return request({
    url: '/admin/system/config/info',
    method: 'GET',
    params: data,
  });
}

export function configList(pram) {
  const data = {
    page: pram.page,
    limit: pram.limit,
  };
  return request({
    url: '/admin/system/config/list',
    method: 'GET',
    params: data,
  });
}

export function configSave(pram) {
  const data = {
    systemConfigRequest: {
      desc: pram.desc,
      groupId: pram.groupId,
      info: pram.info,
      name: pram.name,
      pid: pram.pid,
      status: pram.status,
      type: pram.type,
      value: pram.value, // value 存储表单配置数据，其他的参数来自于父级数据 justForAPI
    },
  };
  return request({
    url: '/admin/system/config/save',
    method: 'POST',
    params: data,
  });
}

export function configSaveForm(pram) {
  return request({
    url: '/admin/system/config/save/form',
    method: 'POST',
    data: pram,
  });
}

export function configUpdate(pram) {
  const data = {
    id: pram.id,
    systemConfigRequest: pram.systemConfigRequest,
  };
  return request({
    url: '/admin/system/config/update',
    method: 'POST',
    params: data,
  });
}

export function configSaveUniq(pram) {
  const data = {
    key: pram.key,
    value: pram.value,
  };
  return request({
    url: '/admin/system/config/saveuniq',
    method: 'POST',
    params: data,
  });
}

/**
 * @description 获取管理端logo
 */
export function getSiteLogoApi() {
  return request({
    url: '/admin/system/config/get/site/logo',
    method: 'GET',
  });
}

/**
 * @description 获取文件存储类型
 */
export function getUploadTypeApi() {
  return request({
    url: '/admin/system/config/get/upload/type',
    method: 'GET',
  });
}

/**
 * @description 获取小程序下载地址
 */
export function getMiniDownloadUrlApi() {
  return request({
    url: '/admin/system/config/get/mini/download/url',
    method: 'GET',
  });
}

/**
 * @description 获取腾讯地图key
 */
export function getTxMapKeyApi() {
  return request({
    url: '/admin/system/config/get/tx/map/key',
    method: 'GET',
  });
}

/**
 * @description 获取移动端首页列表样式
 */
export function getHomeStyleApi() {
  return request({
    url: '/admin/system/config/get/home/page/list/style',
    method: 'GET',
  });
}

/**
 * @description 获取移动端首页列表样式
 */
export function getAuthHostApi() {
  return request({
    url: '/admin/system/config/get/auth/host',
    method: 'GET',
  });
}

/**
 * @description 清除config缓存
 */
export function clearCacheApi() {
  return request({
    url: '/admin/system/config/clear/cache',
    method: 'post',
  });
}

/**
 * @description 获取主题色
 */
export function changeColorApi() {
  return request({
    url: '/admin/system/config/get/change/color',
    method: 'get',
  });
}

/**
 * @description 保存主题色
 */
export function saveColorApi(data) {
  return request({
    url: '/admin/system/config/save/change/color',
    method: 'post',
    data,
  });
}

/**
 * @description 保存移动端首页列表样式
 */
export function savehomeStyleApi(data) {
  return request({
    url: '/admin/system/config/save/home/page/list/style',
    method: 'post',
    data,
  });
}

/**
 * @description 一号通 应用保存
 */
export function passAppSaveApi(data) {
  return request({
    url: '/admin/pass/appsave',
    method: 'post',
    data,
  });
}

/**
 * @description 一号通 应用详情获取
 */
export function passAppInfoApi() {
  return request({
    url: '/admin/pass/appget',
    method: 'get',
  });
}
/**
 * @description 获取移动端域名
 */
export function frontDomainApi(data) {
  return request({
    url: '/public/jsconfig/get/front/domain',
    method: 'GET',
  });
}
/**
 * @description 获取图片域名
 */
export function mediaDomainApi(data) {
  return request({
    url: '/public/jsconfig/get/admin/mediadomain',
    method: 'GET',
  });
}
/**
 * @description 获取系统状态信息
 */
export function systemStateInfoApi() {
  return request({
    url: '/admin/system/status/info',
    method: 'get',
  });
}
