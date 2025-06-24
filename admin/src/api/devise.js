import request from '@/utils/request';

/**
 * 提现申请 列表
 * @param pram
 */
export function diyListApi(params) {
  return request({
    url: '/admin/pagediy/list',
    method: 'get',
    params,
  });
}
