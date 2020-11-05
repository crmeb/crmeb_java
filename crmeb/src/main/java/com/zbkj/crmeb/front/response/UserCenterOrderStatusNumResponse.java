package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.user.model.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 个人中心
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserCenterOrderStatusNumResponse对象", description="个人中心 -- 订单状态数量")
public class UserCenterOrderStatusNumResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "未支付订单数量")
    private int noBuy;

    @ApiModelProperty(value = "未发货订单数量")
    private int noPostage;

    @ApiModelProperty(value = "未收货订单数量")
    private int noTake;

    @ApiModelProperty(value = "未评论订单数量")
    private int noReply;

    @ApiModelProperty(value = "拼团的订单数量")
    private int noPink;

    @ApiModelProperty(value = "退款的订单数量")
    private int noRefund;
}
