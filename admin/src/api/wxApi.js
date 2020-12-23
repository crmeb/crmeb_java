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
 * 关键字回复 修改状态
 * @param pram
 */
export function replyStatusApi(params) {
  return request({
    url: `/admin/wechat/keywords/reply/status`,
    method: 'post',
    params
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

/**
 * 小程序 公共模板列表
 */
export function publicTempListApi(params) {
  return request({
    url: `/admin/wechat/program/public/temp/list`,
    method: 'get',
    params
  })
}

/**
 * 小程序 模版所属类目
 */
export function categoryApi() {
  return request({
    url: `/admin/wechat/program/category`,
    method: 'get'
  })
}

/**
 * 小程序 通过微信模板tid获取关键字列表
 */
export function getWeChatKeywordsByTidApi(params) {
  return request({
    url: `/admin/wechat/program/getWeChatKeywordsByTid`,
    method: 'get',
    params
  })
}

/**
 * 小程序 模板详情，主要是获取左侧标题
 */
export function publicTempInfoApi(params) {
  return request({
    url: `/admin/wechat/program/public/temp/info`,
    method: 'get',
    params
  })
}

/**
 * 小程序 我的模板列表
 */
export function myTempListApi(params) {
  return request({
    url: `/admin/wechat/program/my/temp/list`,
    method: 'get',
    params
  })
}

/**
 * 小程序 我的模板详情
 */
export function myTempInfoApi(params) {
  return request({
    url: `/admin/wechat/program/my/temp/info`,
    method: 'get',
    params
  })
}

/**
 * 小程序 模板新增
 */
export function myTempSaveApi(data) {
  return request({
    url: `/admin/wechat/program/my/temp/save`,
    method: 'post',
    data
  })
}

/**
 * 小程序 模板修改
 */
export function myTempUpdateApi(params, data) {
  return request({
    url: `/admin/wechat/program/my/temp/update`,
    method: 'post',
    params,
    data
  })
}

/**
 * 小程序 我的模板修改状态
 */
export function myTempStatusApi(params) {
  return request({
    url: `/admin/wechat/program/my/temp/update/status`,
    method: 'get',
    params
  })
}


/**
 * 小程序 我的模板修改应用场景
 */
export function myTempTypeApi(params) {
  return request({
    url: `/admin/wechat/program/my/temp/update/type`,
    method: 'get',
    params
  })
}

/**
 * 获取微信sdk配置
 * @returns {*}
 */
export function getWechatConfig() {
  return request({
    url: `/admin/wechat/config`,
    method: 'get',
    params:{ url: encodeURIComponent(location.href.split('#')[0]) } // for Test
  })
}

/**
 * 微信授权登录
 * @returns {*}
 */
export function wechatAuth(code) {
  return request({
    url: `/admin/authorize/login`,
    method: 'get',
    params: { code }
  })
}

/**
 * 与微信解绑账号
 */
export function unbindApi() {
  return request({
    url: `/admin/unbind`,
    method: 'get'
  })
}

/**
 * 一键同步我的模板到小程序
 */
export function tempAsyncApi() {
  return request({
    url: `/admin/wechat/program/my/temp/async`,
    method: 'get'
  })
}
