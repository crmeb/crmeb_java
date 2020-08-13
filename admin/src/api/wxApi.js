import request from '@/utils/request'
// TODO 微信沟通难度大暂放 呵呵

export function menuCreate(data) {
  return request({
    url: '/admin/wechat/menu/public/create',
    method: 'post',
    params: data
  })
}

export function menuDelete(data) {
  return request({
    url: '/admin/wechat/menu/public/delete',
    method: 'post',
    params: data
  })
}

export function menuInfo(pram) {
  const data = {
    isAsync: pram.isAsync
  }
  return request({
    url: '/admin/wechat/menu/public/get',
    method: 'post',
    params: data
  })
}

/**
 * 微信模板消息 列表
 * @param pram
 */
export function wechatTemplateListApi(params) {
  return request({
    url: `/admin/wechat/template/list`,
    method: 'get',
    params
  })
}

/**
 * 微信模板消息 新增
 * @param pram
 */
export function wechatTemplateSaveApi(data) {
  return request({
    url: `/admin/wechat/template/save`,
    method: 'post',
    data
  })
}

/**
 * 微信模板消息 编辑
 * @param pram
 */
export function wechatTemplateUpdateApi(id, data) {
  console.log(id)
  return request({
    url: `/admin/wechat/template/update/${id}`,
    method: 'post',
    data
  })
}

/**
 * 微信模板消息 详情
 * @param pram
 */
export function wechatTemplateInfoApi(id) {
  return request({
    url: `/admin/wechat/template/info/${id}`,
    method: 'get'
  })
}

/**
 * 微信模板消息 修改状态
 * @param pram
 */
export function wechatTemplateStatusApi(id, params) {
  return request({
    url: `/admin/wechat/template/update/status/${id}`,
    method: 'post',
    params
  })
}

/**
 * 微信模板消息 删除
 * @param pram
 */
export function wechatTemplateDeleteApi(id) {
  return request({
    url: `/admin/wechat/template/delete/${id}`,
    method: 'get'
  })
}

/**
 * 关键字回复 列表
 * @param pram
 */
export function replyListApi(params) {
  return request({
    url: `/admin/wechat/keywords/reply/list`,
    method: 'get',
    params
  })
}

/**
 * 关键字回复 新增
 * @param pram
 */
export function replySaveApi(data) {
  return request({
    url: `/admin/wechat/keywords/reply/save`,
    method: 'post',
    data
  })
}

/**
 * 关键字回复 编辑
 * @param pram
 */
export function replyUpdateApi(params, data) {
  return request({
    url: `/admin/wechat/keywords/reply/update`,
    method: 'post',
    params,
    data
  })
}

/**
 * 关键字回复 详情
 * @param pram
 */
export function replyInfoApi(params) {
  return request({
    url: `/admin/wechat/keywords/reply/info`,
    method: 'get',
    params
  })
}

/**
 * 关键字回复 删除
 * @param pram
 */
export function replyDeleteApi(params) {
  return request({
    url: `/admin/wechat/keywords/reply/delete`,
    method: 'get',
    params
  })
}

/**
 * 关键字查询数据
 * @param pram
 */
export function keywordsInfoApi(params) {
  return request({
    url: `/admin/wechat/keywords/reply/info/keywords`,
    method: 'get',
    params
  })
}

/**
 * 微信菜单 获取数据
 * @param pram
 */
export function wechatMenuApi(params) {
  return request({
    url: `/admin/wechat/menu/public/get`,
    method: 'get',
    params
  })
}

/**
 * 微信菜单 新增
 * @param pram
 */
export function wechatMenuAddApi(data) {
  return request({
    url: `/admin/wechat/menu/public/create`,
    method: 'post',
    data
  })
}
