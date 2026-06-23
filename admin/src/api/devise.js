import request from '@/utils/request';
/**
 * 获取开屏广告
 */
export function splashGetApi() {
  return request({
    url: '/admin/page/layout/splash/ad/get',
    method: 'get',
  });
}
/**
 * 编辑开屏广告
 * @param data
 */
export function splashSaveApi(data) {
  return request({
    url: '/admin/page/layout/splash/ad/save',
    method: 'post',
    data: data,
  });
}
