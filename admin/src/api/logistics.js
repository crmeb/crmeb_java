import request from '@/utils/request'

// 城市列表
export function cityList(data) {
  return request({
    url: '/admin/system/city/list',
    method: 'get',
    params: { ...data }
  })
}

// 城市列表
export function cityListTree() {
  return request({
    url: '/admin/system/city/list/tree',
    method: 'get'
  })
}

// 城市修改状态
export function updateStatus(data) {
  return request({
    url: '/admin/system/city/update/status',
    method: 'post',
    params: { ...data }
  })
}

// 城市修改
export function cityUpdate(data) {
  return request({
    url: '/admin/system/city/update',
    method: 'post',
    params: { ...data }
  })
}

// 城市详情
export function cityInfo(data) {
  return request({
    url: '/admin/system/city/info',
    method: 'get',
    params: { ...data }
  })
}

// 物流公司列表
export function expressList(data) {
  return request({
    url: '/admin/express/list',
    method: 'get',
    params: { ...data }
  })
}

// 同步物流公司
export function expressSyncApi() {
  return request({
    url: '/admin/express/sync/express',
    method: 'post'
  })
}

// 物流公司修改状态
export function expressUpdateShow(data) {
  return request({
    url: '/admin/express/update/show',
    method: 'post',
    data
  })
}

// 物流公司 编辑
export function expressUpdate(data) {
  return request({
    url: '/admin/express/update',
    method: 'post',
    data
  })
}

// 新增物流公司
export function expressSave(data) {
  return request({
    url: '/admin/express/save',
    method: 'post',
    params: { ...data }
  })
}

// 删除物流公司
export function expressDelete(data) {
  return request({
    url: '/admin/express/delete',
    method: 'GET',
    params: { ...data }
  })
}

// 物流运费模板列表
export function shippingTemplatesList(data) {
  return request({
    url: '/admin/express/shipping/templates/list',
    method: 'get',
    params: { ...data }
  })
}

// 物流运费模板详情
export function templateDetailApi(data) {
  return request({
    url: '/admin/express/shipping/templates/info',
    method: 'get',
    params: { ...data }
  })
}

// 物流运费模板包邮
export function shippingFree(data) {
  return request({
    url: '/admin/express/shipping/free/list',
    method: 'get',
    params: { ...data }
  })
}

// 物流运费模板不包邮
export function shippingRegion(data) {
  return request({
    url: 'admin/express/shipping/region/list',
    method: 'get',
    params: { ...data }
  })
}

// 物流运费模板新增
export function shippingSave(data) {
  return request({
    url: 'admin/express/shipping/templates/save',
    method: 'post',
    data: data
  })
}

// 物流运费模板更新
export function shippingUpdate(data, id) {
  return request({
    url: 'admin/express/shipping/templates/update',
    method: 'post',
    data: data,
    params: { ...id }
  })
}

// 物流运费模板删除
export function shippingDetete(data) {
  return request({
    url: 'admin/express/shipping/templates/delete',
    method: 'get',
    params: data
  })
}

// 物流公司详情
export function expressInfo(data) {
  return request({
    url: 'admin/express/info',
    method: 'get',
    params: { ...data }
  })
}
