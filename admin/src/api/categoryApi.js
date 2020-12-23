import request from '@/utils/request'

/**
 * 新增分类
 * @param pram
 */
export function addCategroy(pram) {
  const data = {
    extra: pram.extra,
    name: pram.name,
    pid: pram.pid,
    sort: pram.sort,
    status: pram.status,
    type: pram.type,
    url: pram.url
  }
  return request({
    url: '/admin/category/save',
    method: 'POST',
    params: data
  })
}

/**
 * 分类详情
 * @param pram
 */
export function infoCategroy(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/category/info',
    method: 'GET',
    params: data
  })
}

/**
 * 删除分类
 * @param pram
 */
export function deleteCategroy(pram) {
  const data = {
    id: pram.id
  }
  return request({
    url: '/admin/category/delete',
    method: 'GET',
    params: data
  })
}

/**
 * 分类列表
 * @param pram
 */
export function listCategroy(pram) {
  const data = {
    limit: pram.limit,
    name: pram.name,
    page: pram.page,
    pid: pram.pid,
    status: pram.status,
    type: pram.type

  }
  return request({
    url: '/admin/category/list',
    method: 'GET',
    params: data
  })
}

/**
 * 分类数据tree数据
 * @param pram
 */
export function treeCategroy(pram) {
  const data = {
    type: pram.type,
    status: pram.status,
    name: pram.name
  }
  return request({
    url: '/admin/category/list/tree',
    method: 'GET',
    params: data
  })
}

/**
 * 更新分类
 * @param pram
 */
export function updateCategroy(pram) {
  const data = {
    extra: pram.extra,
    name: pram.name,
    pid: pram.pid,
    sort: pram.sort,
    status: pram.status,
    type: pram.type,
    url: pram.url,
    id: pram.id
  }
  return request({
    url: '/admin/category/update',
    method: 'POST',
    params: data
  })
}

/**
 * 根据id集合查询对应分类列表
 * @param pram
 */
export function categroyByIds(pram) {
  const data = {
    ids: pram.ids
  }
  return request({
    url: '/admin/category/list/ids',
    method: 'GET',
    params: data
  })
}

/**
 * 修改 显示关闭状态
 * @param pram
 */
export function categroyUpdateStatus(id) {
  return request({
    url: `/admin/category/updateStatus/${id}`,
    method: 'GET'
  })
}
