import request from '@/utils/request'

export function getFormConfigInfo(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/system/form/temp/info',
    method: 'GET',
    params: data
  })
}

export function getFormConfigList(pram) {
  const data = {
    keywords: pram.keywords,
    page: pram.page,
    limit: pram.limit
  }
  return request({
    url: '/admin/system/form/temp/list',
    method: 'GET',
    params: data
  })
}

export function getFormConfigSave(pram) {
  const data = {
    content: pram.content,
    info: pram.info,
    name: pram.name
  }
  return request({
    url: '/admin/system/form/temp/save',
    method: 'POST',
    data: data
  })
}

export function getFormConfigEdit(pram) {
  const params = { id: pram.id }
  const data = {
    content: pram.content,
    info: pram.info,
    name: pram.name
  }
  return request({
    url: '/admin/system/form/temp/update',
    method: 'POST',
    params: params,
    data: data
  })
}
