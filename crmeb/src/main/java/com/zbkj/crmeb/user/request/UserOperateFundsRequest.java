package com.zbkj.crmeb.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 资金操作
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="资金操作", description="资金操作")
public class UserOperateFundsRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "uid")
    @NotNull
    @Min(value = 1, message = "请输入正确的uid")
    private Integer uid;

    @ApiModelProperty(value = "操作模块， orderPaySuccess")
    @NotBlank
    private String foundsType;

    @ApiModelProperty(value = "资金类型， integral = 积分， now_money = 余额, exp = 经验,  brokerage_price = 佣金")
    @NotBlank
    private String foundsCategory;

    @ApiModelProperty(value = "类型， 1 = 增加， 0 = 减少")
    @NotNull
    @Range(min = 0, max = 1, message = "请选择正确的类型， 【1 = 增加， 0 = 减少】")
    private int type;

    @ApiModelProperty(value = "金额")
    @NotNull
    @DecimalMin(value = "0.01", message = "请输入正确的金额")
    private BigDecimal value;

    @ApiModelProperty(value = "操作动作 订单支付等")
    @NotNull
    private String title;

    @ApiModelProperty(value = "关联三方id")
    private String linkId = "0";
}
