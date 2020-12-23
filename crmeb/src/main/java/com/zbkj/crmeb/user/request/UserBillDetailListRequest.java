package com.zbkj.crmeb.user.request;

import lombok.Data;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 用户操作记录查询
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
public class UserBillDetailListRequest {
    private String nickname; // 订单号或者推荐人昵称=备注模糊查询
    private String dateLimit; // 时间参数
    private String keywords;
}
