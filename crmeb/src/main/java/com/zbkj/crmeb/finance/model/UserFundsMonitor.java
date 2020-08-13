package com.zbkj.crmeb.finance.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户充值表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserFundsMonitor对象", description="佣金")
public class UserFundsMonitor implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "充值用户UID")
    private Integer uid;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "账户余额")
    private BigDecimal nowMoney;

    @ApiModelProperty(value = "账户佣金")
    private BigDecimal brokerage;

    @ApiModelProperty(value = "账户总佣金")
    private BigDecimal totalBrokerage;

    @ApiModelProperty(value = "提现总金额")
    private BigDecimal totalExtract;



}
