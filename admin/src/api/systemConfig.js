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

export function configCheckUnique(pram) {
  const data = {
    name: pram.name
  }
  return request({
    url: '/admin/system/config/check',
    method: 'GET',
    params: data
  })
}

export function configDelete(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/system/config/delete',
    method: 'GET',
    params: data
  })
}

export function configInfo(pram) {
  const data = {
    formId: pram.id
  }
  return request({
    url: '/admin/system/config/info',
    method: 'GET',
    params: data
  })
}

export function configList(pram) {
  const data = {
    page: pram.page,
    limit: pram.limit
  }
  return request({
    url: '/admin/system/config/list',
    method: 'GET',
    params: data
  })
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
      value: pram.value // value 存储表单配置数据，其他的参数来自于父级数据 justForAPI
    }
  }
  return request({
    url: '/admin/system/config/save',
    method: 'POST',
    params: data
  })
}

export function configSaveForm(pram) {
  return request({
    url: '/admin/system/config/save/form',
    method: 'POST',
    data: pram
  })
}

export function configUpdate(pram) {
  const data = {
    id: pram.id,
    systemConfigRequest: pram.systemConfigRequest
  }
  return request({
    url: '/admin/system/config/update',
    method: 'POST',
    params: data
  })
}

export function configSaveUniq(pram) {
  const data = {
    key: pram.key,
    value: pram.value
  }
  return request({
    url: '/admin/system/config/saveuniq',
    method: 'POST',
    params: data
  })
}

export function configGetUniq(pram) {
  const data = {
    key: pram.key
  }
  return request({
    url: '/admin/system/config/getuniq',
    method: 'GET',
    params: data
  })
}


