package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品所参与的活动类型
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
public class ProductActivityItemResponse {

    @ApiModelProperty(value = "参与活动id")
    private Integer id;

    @ApiModelProperty(value = "秒杀结束时间")
    private Integer time;

    @ApiModelProperty(value = "活动参与类型")
    private String type;
}
