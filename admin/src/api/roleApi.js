import request from '@/utils/request'

export function getRoleById(pram) {
  const data = { ids: pram.roles }
  return request({
    url: '/admin/system/role/info',
    method: 'GET',
    params: data
  })
}

/**
 * 菜单
 * @param pram
 */
export function menuListApi() {
  return request({
    url: '/admin/system/role/menu',
    method: 'GET'
  })
}
