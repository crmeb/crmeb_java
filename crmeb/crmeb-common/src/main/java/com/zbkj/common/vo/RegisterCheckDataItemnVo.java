package com.zbkj.common.vo;

import lombok.Data;

/**
 * RegisterCheckAccessInfoItemVo 获取接入状态Response
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
public class RegisterCheckDataItemnVo {
    // 审核状态, 2: 已接入, 3: 封禁中
    private Integer status;
    private RegisterCheckAccessInfoItemVo access_info;
}
