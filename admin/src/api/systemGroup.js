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

export function groupDelete(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/system/group/delete',
    method: 'GET',
    params: data
  })
}

export function groupInfo(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/system/group/info',
    method: 'GET',
    params: data
  })
}

export function groupList(pram) {
  const data = {
    keywords: pram.keywords,
    page: pram.page,
    limit: pram.limit
  }
  return request({
    url: '/admin/system/group/list',
    method: 'GET',
    params: data
  })
}

export function groupDataList(pram) {
  const data = {
    gid:pram.gid,
    keywords: pram.keywords,
    page: pram.page,
    limit: pram.limit
  }
  return request({
    url: '/admin/system/group/data/list',
    method: 'GET',
    params: data
  })
}

export function groupSave(pram) {
  const data = {
    formId: pram.formId,
    info: pram.info,
    name: pram.name
  }
  return request({
    url: '/admin/system/group/save',
    method: 'POST',
    params: data
  })
}

export function groupEdit(pram) {
  const data = {
    formId: pram.formId,
    info: pram.info,
    name: pram.name,
    id: pram.id
  }
  return request({
    url: '/admin/system/group/update',
    method: 'POST',
    params: data
  })
}

/**
 * @description 页面设计 获取数据
 */
export function designListApi() {
  return request.get(`/admin/page/layout/index`)
}

/**
 * @description 页面设计商品Tab 获取数据
 */
 export function goodDesignList(pram) {
  const data = {
    gid: pram.gid,
  }
  return request({
    url: '/admin/system/group/data/list',
    method: 'GET',
    params: data
  })
}

/**
 * @description 页面设计 保存
 */
export function SaveDataApi(data, url) {
  return request({
    url: url,
    method: 'POST',
    data
  })
}

/**
 * @description 获取配置
 */
 export function getDataApi(data) {
  return request({
    url: '/admin/page/layout/category/config',
    method: 'GET',
    data
  })
}

/**
 * @description 保存设置
 */
 export function themeSave(params) {
  return request({
    url: `/admin/system/config/saveuniq`,
    method: 'post',
    params
  })
}

/**
 * @description 获取设置
 */
 export function getTheme(params) {
  return request({
    url: `/admin/system/config/getuniq`,
    method: 'get',
    params
  })
}
