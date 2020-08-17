import request from '@/utils/request'

export function getMenu() {
  return request({
    url: '/admin/system/role/testMenu',
    method: 'GET'
  })
}

export function adminDel(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/system/admin/delete',
    method: 'GET',
    params: data
  })
}

export function adminInfo(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/system/admin/info',
    method: 'GET',
    params: data
  })
}

export function adminList(params) {
  return request({
    url: '/admin/system/admin/list',
    method: 'GET',
    params
  })
}

export function adminAdd(pram) {
  const data = {
    account: pram.account,
    level: pram.level,
    pwd: pram.pwd,
    realName: pram.realName,
    roles: pram.roles.join(','),
    status: pram.status
  }
  return request({
    url: '/admin/system/admin/save',
    method: 'POST',
    params: data
  })
}

export function adminUpdate(pram) {
  // const data = {
  //   account: pram.account,
  //   level: pram.level,
  //   pwd: pram.pwd,
  //   realName: pram.realName,
  //   roles: pram.roles.join(','),
  //   status: pram.status,
  //   id: pram.id,
  //   isDel: pram.isDel
  // }
  return request({
    url: '/admin/system/admin/update',
    method: 'POST',
    params: pram
  })
}
