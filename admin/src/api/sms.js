import request from '@/utils/request'
/**
 * @description 短信发送记录 -- 列表
 */
export function smsLstApi(params) {
  return request({
    url: '/admin/sms/record/list',
    method: 'get',
    params
  })
}
/**
 * @description 短信账户 -- 登录
 */
export function configApi(params) {
  return request({
    url: '/admin/sms/login',
    method: 'get',
    params
  })
}
/**
 * @description 短信账户 -- 获取验证码
 */
export function captchaApi(phone) {
  return request({
    url: `/admin/sms/sendCodeForRegister/${phone}`,
    method: 'get'
  })
}
/**
 * @description 短信账户 -- 注册
 */
export function registerApi(params) {
  return request({
    url: '/admin/sms/register',
    method: 'post',
    params
  })
}
/**
 * @description 短信账户 -- 是否登录
 */
export function isLoginApi() {
  return request({
    url: '/admin/sms/islogin',
    method: 'get'
  })
}
/**
 * @description 短信账户 -- 退出登录
 */
export function logoutApi() {
  return request({
    url: '/admin/sms/logout',
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
    url: '/admin/sms/temp/list',
    method: 'get',
    params
  })
}
/**
 * @description 短信购买 -- 支付套餐
 */
export function smsPriceApi(params) {
  return request({
    url: '/admin/sms/pay/list',
    method: 'get',
    params
  })
}
/**
 * @description 短信购买 -- 支付码
 */
export function payCodeApi(params) {
  return request({
    url: '/admin/sms/pay/qrCode',
    method: 'get',
    params
  })
}
/**
 * @description 短信模板 -- 添加表单
 */
export function tempCreateApi(params) {
  return request({
    url: '/admin/sms/temp/apply',
    method: 'post',
    params
  })
}
/**
 * @description 短信 -- 用户信息
 */
export function smsInfoApi() {
  return request({
    url: '/admin/sms/info',
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
