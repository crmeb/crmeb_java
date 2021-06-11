import request from "@/utils/request.js";
/**
 * 
 * 所有活动接口 包括：拼团，砍价，秒杀
 * 
*/

/**
 * 拼团列表
 * 
*/
export function getCombinationList(data) {
  return request.get('combination/list', data,{noAuth:true});
}

/**
 * 拼团详情
 * 
*/
export function getCombinationDetail(id) {
  return request.get('combination/detail/'+id);
}

/**
 * 拼团 开团
 */
export function getCombinationPink(id) {
  return request.get("combination/pink/" + id);
}

/**
 * 拼团 取消开团
 */
export function postCombinationRemove(data) {
  return request.post("combination/remove",data);
}

/**
 * 拼团 更多拼团
 */
export function getCombinationMore(data) {
  return request.get("combination/more",data,{noAuth:true});
}

/**
 * 砍价列表
 */
export function getBargainList(data) {
  return request.get("bargain/list", data,{noAuth:true});
}

/**
 * 
 * 砍价列表(已参与)
 * @param object data
*/
export function getBargainUserList(data){
  return request.get('bargain/record',data);
}


/**
 * 
 * 取消砍价
 * @param int bargainId
*/
export function bargainUserCancel(bargainId){
  return request.post('bargain/user/cancel', { bargainId: bargainId})
}

/**
 * 砍价产品详情
 */
export function getBargainDetail(id) {
  return request.get("bargain/detail/" + id);
}

/**
 * 用户砍价信息,注意自己的活动和别人的活动的区别
 */
export function getBargainUser(data) {
  return request.get("bargain/user", data);
}


/**
 * 砍价开启
 */
export function postBargainStart(bargainId) {
  return request.post("bargain/start", { bargainId: bargainId});
}

/**
 * 砍价 帮助好友砍价
 */
export function postBargainHelp(data) {
  return request.post("bargain/help", data);
}

/**
 * 砍价 砍掉金额
 */
export function postBargainHelpPrice(data) {
  return request.post("bargain/help/price", data);
}

/**
 * 砍价 砍价帮 
 */
export function postBargainHelpList(params,data) {
  return request.get("bargain/help/list?limit="+params.limit+"&page="+params.page, data,{});
}

/**
 * 秒杀产品时间区间
 * 
*/
export function getSeckillIndexTime(){
  return request.get('seckill/index',{},{noAuth:true});
}

/**
 * 秒杀产品列表
 * @param int time
 * @param object data
*/
export function getSeckillList(time,data){
  return request.get('seckill/list/'+time,data,{noAuth:true});
}

/**
 * 秒杀产品详情
 * @param int id
*/
export function getSeckillDetail(id){
  return request.get('seckill/detail/'+id);
}

/**
 * 砍价海报
 * @param object data
 * 
*/
export function getBargainPoster(data){
  return request.post('bargain/poster',data)
}

/**
 * 拼团海报
 * @param object data
 * 
*/
export function getCombinationPoster(data){
  return request.post('combination/poster',data)
}

/**
 * 砍价取消
 */
export function getBargainUserCancel(data) {
  return request.post("/bargain/user/cancel", data);
}

/**
 * 获取秒杀小程序二维码
 */
export function seckillCode(id,data) {
  return request.get("seckill/code/"+id,data);
}

/**
 * 获取拼团小程序二维码
 */
export function scombinationCode(id) {
  return request.get("combination/code/"+id);
}

/**
 * 秒杀产品时间区间
 * @param int id
*/
export function getSeckillHeaderApi(){
  return request.get('seckill/header',{},{noAuth:true});
}

/**
 * 首页秒杀产品列表
*/
export function getSeckillIndexApi(){
  return request.get('seckill/index',{},{noAuth:true});
}


/**
 * 首页拼团产品列表
*/
export function getCombinationIndexApi(){
  return request.get('combination/index',{},{noAuth:true});
}

/**
 * 首页砍价产品列表
*/
export function getBargainIndexApi(){
  return request.get('bargain/index',{},{noAuth:true});
}

/**
 * 首页砍价产品列表
*/
export function bargainHeaderApi(){
  return request.get('bargain/header');
}

/**
 * 拼图列表头部
*/
export function combinationHeaderApi(){
  return request.get('combination/header');
}