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
    status: pram.status,
    phone: pram.phone
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

/**
 * 修改状态
 * @param pram
 */
export function updateStatusApi(params) {
  return request({
    url: `/admin/system/admin/updateStatus`,
    method: 'get',
    params
  })
}

/**
 * 修改后台管理员是否接收状态
 * @param pram
 */
export function updateIsSmsApi(params) {
  return request({
    url: `/admin/system/admin/update/isSms`,
    method: 'get',
    params
  })
}
