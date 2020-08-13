package com.zbkj.crmeb.system.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 签到记录
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SystemGroupDataSignConfigVo对象", description="签到记录")
public class SystemGroupDataRechargeConfigVo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id")
    private Integer id;

    @ApiModelProperty(value = "售价")
    private BigDecimal price;

    @ApiModelProperty(value = "赠送")
    private BigDecimal giveMoney;
}
