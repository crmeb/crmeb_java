package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 确认订单请求对象
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
public class ConfirmOrderRequest {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "购物车id集合")
    @NotNull(message = "购物车编号集合 不能为空")
    private String cartIds;

    @ApiModelProperty(value = "是否立即购买")
    @NotNull(message = "是否立即购买 不能为空")
    private Boolean isNew;

    @ApiModelProperty(value = "是否再一下单")
    @NotNull(message = "是否再一下单 不能为空")
    private Boolean addAgain;

    @ApiModelProperty(value = "是否秒杀商品")
    @NotNull(message = "是否秒杀商品 不能为空")
    private Boolean secKill;

    @ApiModelProperty(value = "是否砍价商品")
    @NotNull(message = "是否砍价商品 不能为空")
    private Boolean bargain;

    @ApiModelProperty(value = "是否拼团商品")
    @NotNull(message = "是否拼团商品 不能为空")
    private Boolean combination;

    @ApiModelProperty(value = "地址id")
//    @NotNull(message = "地址编号 不能为空")
    private Integer addressId;
}
