// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2021 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import req from './req'

/**
 * 查询授权
 * @param pram
 */
 export function authCertQuery(prams) {
    const data = {
        domain_name:prams.host,
        label:22,
        version:'2.0'
    }
    return req({
      url: document.location.protocol + '//authorize.crmeb.net/api/auth_cert_query',
      // url: 'https://authorize.crmeb.net/api/auth_cert_query',
      method: 'POST',
      data
    })
}

/**
 * 授权表单提交
 */
 export function authCertSubmit(data) {
    return req({
      url: document.location.protocol + '//authorize.crmeb.net/api/auth_apply',
      // url: 'https://authorize.crmeb.net/api/auth_apply',
      method: 'POST',
      data
    })
}