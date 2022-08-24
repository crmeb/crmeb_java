package com.zbkj.common.vo;

import lombok.Data;

/**
 * 短信发送api第三方参数实体类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
public class SendSmsVo {
    private String uid;
    private String token;

    // 待发送短信手机号
    private String mobile;

    // 模版id
    private Integer template;

    // 发送参数
    private String param;

    private String content;

}
