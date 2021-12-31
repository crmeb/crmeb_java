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
/**
 * @description 短信发送记录 -- 列表
 */
export function smsLstApi(params) {
  return request({
    url: '/admin/pass/user/record',
    method: 'get',
    params
  })
}
/**
 * @description 短信账户 -- 登录
 */
export function configApi(data) {
  return request({
    url: '/admin/pass/login',
    method: 'post',
    data
  })
}
/**
 * @description 短信账户 -- 获取验证码
 */
export function captchaApi(params) {
  return request({
    url: `/admin/pass/sendUserCode`,
    method: 'get',
    params
  })
}
/**
 * @description 短信账户 -- 注册
 */
export function registerApi(data) {
  return request({
    url: '/admin/pass/register',
    method: 'post',
    data
  })
}
/**
 * @description 短信账户 -- 是否登录
 */
export function isLoginApi() {
  return request({
    url: '/admin/pass/isLogin',
    method: 'get'
  })
}
/**
 * @description 短信账户 -- 退出登录
 */
export function logoutApi() {
  return request({
    url: '/admin/pass/logout',
    method: 'get'
  })
}
/**
 * @description 短信账户 -- 剩余条数
 */
export function smsNumberApi() {
  return request({
    url: '/admin/sms/logout',
    method: 'get'
  })
}
/**
 * @description 短信模板 -- 列表
 */
export function smsTempLstApi(params) {
  return request({
    url: '/admin/sms/temps',
    method: 'get',
    params
  })
}
/**
 * @description 短信购买 -- 支付套餐
 */
export function smsPriceApi(params) {
  return request({
    url: '/admin/pass/meal/list',
    method: 'get',
    params
  })
}
/**
 * @description 短信购买 -- 支付码
 */
export function payCodeApi(data) {
  return request({
    url: '/admin/pass/meal/code',
    method: 'post',
    data
  })
}
/**
 * @description 短信模板 -- 添加表单
 */
export function tempCreateApi(data) {
  return request({
    url: '/admin/sms/temp/apply',
    method: 'post',
    data
  })
}
/**
 * @description 短信 -- 用户信息
 */
export function smsInfoApi() {
  return request({
    url: '/admin/pass/info',
    method: 'get'
  })
}

/**
 * @description 短信 -- 短信提醒开关保存
 */
export function smsSaveApi(params) {
  return request({
    url: '/admin/sms/config/save',
    method: 'post',
    params
  })
}

/**
 * @description 短信 -- 修改密码
 */
export function updatePasswordApi(data) {
  return request({
    url: '/admin/pass/update/password',
    method: 'post',
    data
  })
}

/**
 * @description 短信 -- 修改手机号
 */
export function updateHoneApi(data) {
  return request({
    url: '/admin/pass/update/phone',
    method: 'post',
    data
  })
}

/**
 * @description 一号通 -- 服务开通
 */
export function serviceOpenApi(data) {
  return request({
    url: '/admin/pass/service/open',
    method: 'post',
    data
  })
}

/**
 * @description 一号通 -- 电子面单模板
 */
export function exportTempApi(params) {
  return request({
    url: '/admin/express/template',
    method: 'get',
    params
  })
}

/**
 * @description 全部物流公司
 */
export function expressAllApi(params) {
  return request({
    url: 'admin/express/all',
    method: 'get',
    params
  })
}

/**
 * @description 修改签名
 */
export function smsSignApi(data) {
  return request({
    url: 'admin/sms/modify/sign',
    method: 'post',
    data
  })
}

/**
 * @description 修改手机号验证账号密码
 */
export function phoneValidatorApi(data) {
  return request({
    url: 'admin/pass/update/phone/validator',
    method: 'post',
    data
  })
}
