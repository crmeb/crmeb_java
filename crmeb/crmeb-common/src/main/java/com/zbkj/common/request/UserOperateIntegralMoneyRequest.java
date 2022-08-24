package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 资金操作
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="资金操作", description="资金操作")
public class UserOperateIntegralMoneyRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "uid")
    @NotNull
    @Min(value = 1, message = "请输入正确的uid")
    private Integer uid;

    @ApiModelProperty(value = "积分类型， 1 = 增加， 2 = 减少")
    @NotNull
    @Range(min = 1, max = 2, message = "请选择正确的类型， 【1 = 增加， 2 = 减少】")
    private Integer integralType;

    @ApiModelProperty(value = "积分")
    @Min(value = 0)
    @Max(value = 999999)
    private Integer integralValue;

    @ApiModelProperty(value = "余额类型， 1 = 增加， 2 = 减少")
    @NotNull
    @Range(min = 1, max = 2, message = "请选择正确的类型， 【1 = 增加， 2 = 减少】")
    private Integer moneyType;

    @ApiModelProperty(value = "余额")
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "999999.99")
    private BigDecimal moneyValue;

}
