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

export function groupDataDelete(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/system/group/data/delete',
    method: 'GET',
    params: data
  })
}

export function groupDataInfo(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/system/group/data/info',
    method: 'GET',
    params: data
  })
}

export function groupDataList(pram) {
  const data = {
    gid: pram.gid,
    keywords: pram.keywords,
    status: pram.status, // 1=开启 2=关闭
    page: pram.page,
    limit: pram.limit
  }
  return request({
    url: '/admin/system/group/data/list',
    method: 'GET',
    params: data
  })
}

export function groupDataSave(pram) {
  return request({
    url: '/admin/system/group/data/save',
    method: 'POST',
    data: pram
  })
}

export function groupDataEdit(pram, id) {
  return request({
    url: '/admin/system/group/data/update',
    method: 'POST',
    data: pram,
    params: { id: id }
  })
}
