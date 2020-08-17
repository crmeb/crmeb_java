package com.zbkj.crmeb.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 资金操作
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-10
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
    private int integralType;

    @ApiModelProperty(value = "积分")
    private BigDecimal integralValue;

    @ApiModelProperty(value = "余额类型， 1 = 增加， 2 = 减少")
    @NotNull
    @Range(min = 1, max = 2, message = "请选择正确的类型， 【1 = 增加， 2 = 减少】")
    private int moneyType;

    @ApiModelProperty(value = "余额")
    private BigDecimal moneyValue;

}
