import request from '@/utils/request'

export function addRole(pram) {
  const data = {
    level: pram.level,
    roleName: pram.roleName,
    status: pram.status
  }
  data.rules = pram.rules.join(',')
  return request({
    url: '/admin/system/role/save',
    method: 'POST',
    params: data
  })
}

export function delRole(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/system/role/delete',
    method: 'GET',
    params: data
  })
}

export function getInfo(pram) {
  const data = {
    ids: pram.id
  }
  return request({
    url: '/admin/system/role/info',
    method: 'get',
    params: data
  })
}

export function getRoleList(pram) {
  const data = {
    createTime: pram.createTime,
    updateTime: pram.updateTime,
    level: pram.level,
    page: pram.page,
    limit: pram.limit,
    roleName: pram.roleName,
    rules: pram.rules,
    status: pram.status
  }
  return request({
    url: '/admin/system/role/list',
    method: 'get',
    params: data
  })
}

export function updateRole(pram) {
  const data = {
    id: pram.id,
    level: pram.level,
    roleName: pram.roleName,
    rules: pram.rules.join(','),
    status: pram.status
  }
  return request({
    url: '/admin/system/role/update',
    method: 'post',
    params: data
  })
}
