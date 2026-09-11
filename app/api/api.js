// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import request from "@/utils/request.js";
/**
 * 公共接口 ，优惠券接口 , 行业此讯 , 手机号码注册
 * 
*/

/**
 * 获取主页数据 无需授权
 * 
*/
export function getIndexData()
{
  return request.get("index",{},{ noAuth : true});
}

/**
 * 获取登录授权login
 * 
*/
export function getLogo()
{
  return request.get('wechat/getLogo', {}, { noAuth : true});
}


/**
 * 保存form_id
 * @param string formId 
 */
export function setFormId(formId) {
  return request.post("wechat/set_form_id", { formId: formId});
}

/**
 * 领取优惠卷
 * @param int couponId
 * 
*/
export function setCouponReceive(couponId){
  return request.post('coupon/receive', { couponId: couponId});
}
/**
 * 优惠券列表
 * @param object data
*/
export function getCoupons(data){
  return request.get('coupons',data,{noAuth:true})
}

/**
 * 我的优惠券
 * @param int types 0全部  1未使用 2已使用
*/
export function getUserCoupons(data){
  return request.get('coupon/list',data)
}

/**
 * 文章分类列表
 * 
*/
export function getArticleCategoryList(){
  return request.get('article/category/list',{},{noAuth:true})
}

/**
 * 文章列表
 * @param int cid
 * 
*/
export function getArticleList(cid,data){
  return request.get('article/list/' + cid, data,{noAuth:true})
}

/**
 * 文章 热门列表
 * 
*/
export function getArticleHotList(){
  return request.get('article/hot/list',{},{noAuth:true});
}

/**
 * 文章 轮播列表
 * 
*/
export function getArticleBannerList(){
  return request.get('article/banner/list',{},{noAuth:true})
}

/**
 * 文章详情
 * @param int id 
 * 
*/
export function getArticleDetails(id){
  return request.get('article/info',id,{noAuth:true});
}

/**
 * 手机号+验证码登录接口
 * @param object data
*/
export function loginMobile(data){
  return request.post('login/mobile',data,{noAuth:true})
}

/**
 * 获取短信KEY
 * @param object phone
*/
export function verifyCode(){
  return request.get('verify_code', {},{noAuth:true})
}

/**
 * 验证码发送
 * @param object phone
*/
export function registerVerify(phone){
  return request.post('sendCode', { phone: phone },{noAuth:true},1)
}

/**
 * 手机号注册
 * @param object data
 * 
*/
export function phoneRegister(data){
  return request.post('register',data,{noAuth:true});
}

/**
 * 手机号修改密码
 * @param object data
 * 
*/
export function phoneRegisterReset(data){
  return request.post('register/reset',data,{noAuth:true})
}

/**
 * 手机号+密码登录
 * @param object data
 * 
*/
export function phoneLogin(data){
  return request.post('login',data,{noAuth:true})
}

/**
 * 切换H5登录
 * @param object data
*/
// #ifdef MP
export function switchH5Login(){
  return request.post('switch_h5', { 'from':'routine'});
}
// #endif

/*
 * h5切换公众号登录
 * */
// #ifdef H5
export function switchH5Login() {
  return request.post("switch_h5", { 'from': "wechat" });
}
// #endif

/**
 * 换绑手机号
 * 
*/
export function bindingPhone(data){
  return request.post('update/binding',data);
}

/**
 * 换绑手机号校验
 * 
*/
export function bindingVerify(data){
  return request.post('update/binding/verify',data);
}

/**
 * 退出登錄
 * 
*/
export function logout(){
  return request.get('logout');
}

/**
 * 获取订阅消息id
 */
export function getTemlIds(data)
{
  return request.get('wechat/program/my/temp/list', data , { noAuth:true});
}

const SUBSCRIBE_TEMPLATE_TYPES = ['beforePay', 'afterPay', 'createBargain', 'pink'];

/**
 * 获取城市信息
 */
export function getCity() {
  return request.get('city/list', { }, { noAuth: true });
}

/**
 * 获取小程序直播列表
 */
export function getLiveList(page,limit) {
  return request.get('wechat/live', { page, limit}, { noAuth: true });
}

/**
 * 获取小程序二维码
 */
export function getQrcode(data) {
  return request.post('qrcode/get',data,{ noAuth: true });
}

/**
 * 获取主题换色配置
 */
export function getTheme() {
  return request.get('index/color/config',{},{noAuth:true});
}
 
/**
 * 获取主题换色配置
 */
export function getAppVersion() {
  return request.get('index/get/version',{},{noAuth:true});
}

/**
 * 获取全局本地图片域名
 */
export function getImageDomain() {
  return request.get('image/domain',{},{noAuth:true});
}

/**
 * 获取主题信息
 */
export function getThemeInfo(type, data) {
  return request.get(`theme_info/${type}`, data || {}, { noAuth: true });
}

/**
 * 获取DIY签到信息，接口未提供时先按标准版地址保留
 */
export function getSign() {
  return request.get('v2/diy/sign', {}, { noAuth: true });
}

/**
 * 获取主题商品列表
 */
function toThemeProductParams(data = {}) {
  const params = { ...data };
  if (params.cate_id !== undefined && params.cate_ids === undefined) {
    params.cate_ids = params.cate_id;
  }
  if (params.priceOrder && params.order === undefined) {
    params.order = 2;
    params.sort = params.priceOrder === 'asc' ? 1 : 2;
  }
  if (params.salesOrder && params.order === undefined) {
    params.order = 1;
    params.sort = params.salesOrder === 'asc' ? 1 : 2;
  }
  delete params.cate_id;
  delete params.priceOrder;
  delete params.salesOrder;
  delete params.store_label_id;
  delete params.brand_id;
  return params;
}

export function getThemeProduct(data) {
  return request.get('theme/product', toThemeProductParams(data), { noAuth: true });
}

/**
 * 获取主题文章列表
 */
export function getThemeArticle(data = {}) {
  const params = {};
  if (data.cid !== undefined) params.cid = data.cid;
  if (data.page !== undefined) params.page = data.page;
  if (data.limit !== undefined) params.limit = data.limit;
  return request.get('theme/article', params, { noAuth: true });
}

/**
 * 获取主题优惠券列表
 */
export function getThemeCoupon(data = {}) {
  const params = {};
  if (data.limit !== undefined) params.limit = data.limit;
  return request.get('theme/coupon', params, { noAuth: true });
}

function toThemeLimitParams(data = {}) {
  const params = {};
  if (data.limit !== undefined) params.limit = data.limit;
  return params;
}

/**
 * 获取主题秒杀商品列表
 */
export function getThemeSeckill(data = {}) {
  return request.get('theme/seckill', toThemeLimitParams(data), { noAuth: true });
}

/**
 * 获取主题砍价商品列表
 */
export function getThemeBargain(data = {}) {
  return request.get('theme/bargain', toThemeLimitParams(data), { noAuth: true });
}

/**
 * 获取主题拼团商品列表
 */
export function getThemeCombination(data = {}) {
  return request.get('theme/combination', toThemeLimitParams(data), { noAuth: true });
}

/**
 * 获取主题用户信息，接口未提供时先按标准版地址保留
 */
export function getThemeUser() {
  return request.get('theme/user', {}, { noAuth: false });
}

/**
 * 新人礼列表，接口未提供时先按标准版地址保留
 */
export function newcomerList(data) {
  return request.get('newcomer/list', data, { noAuth: true });
}

/**
 * 商品分类版本号
 */
export function getCategoryVersion() {
  return request.get('category_version', {}, { noAuth: true });
}

/**
 * 获取订阅消息 id，当前后端按 type 分组提供模板。
 */
export function getTempIds() {
  return Promise.all(
    SUBSCRIBE_TEMPLATE_TYPES.map(type =>
      getTemlIds({ type })
        .then(res => ({
          type,
          data: Array.isArray(res.data) ? res.data.map(item => item.tempId).filter(Boolean) : []
        }))
        .catch(() => ({ type, data: [] }))
    )
  ).then(list => ({
    code: 200,
    data: list.reduce((data, item) => {
      data[item.type] = item.data;
      return data;
    }, {})
  }));
}

/**
 * 获取首页 DIY，标准版首页兼容接口
 */
export function getDiy(id) {
  return request.get(`v2/diy/get_diy/default${id ? "?id=" + id : ""}`, {}, { noAuth: true });
}

/**
 * 获取 DIY 版本
 */
export function getDiyVersion(name) {
  return request.get(`v2/diy/get_version/${name}`, {}, { noAuth: true });
}

/**
 * 商品排行榜
*/
export function productRank(){
  return request.get('product/leaderboard',{},{noAuth:true});
}

/**
 * 获取登录配置
*/
export function loginConfigApi(){
  return request.get(`login/config`,{},{noAuth:true});
}

/**
 * 获取底部导航信息
*/
export function getBottomNavigationApi(){
  return request.get(`get/bottom/navigation`,{},{noAuth:true});
}
/**
 * 协议详情
*/
export function agreementInfo(info){
  return request.get(`agreement/${info}`,{},{noAuth:true});
}
/**
 * 首页装修
*/
export function pagediyInfoApi(id){
  return request.get(`pagediy/info/${id}`,{},{noAuth:true});
}
/**
 * 首页 第二级商品分类
 *
*/
export function getCategoryTwo(id)
{
  return request.get(`categorybypid/${id}`,{},{ noAuth : true});
}
/**
 * 获取备案设置
 *
*/
export function getConfigCopyright(id)
{
  return request.get(`config/get/copyright`,{},{ noAuth : true},{},true);
}

/*
 * 获取开屏广告
 */
export function getOpenAdvApi()
{
  return request.get(`splash/ad/info`,{},{ noAuth : true});
}
