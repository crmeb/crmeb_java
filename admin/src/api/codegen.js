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
 * 查询待生成代码列表
 * @param pram
 */
export function getCodegenList(pram) {
  const data = {
    page: pram.page,
    limit: pram.limit,
    tableName: pram.tableName
  }
  return request({
    url: '/codegen/list',
    method: 'GET',
    params: data
  })
}

