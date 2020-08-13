package com.zbkj.crmeb.finance.response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Classname UserExtractResponse
 * @Description 提现数据用于分销
 * @Date 2020/6/22 2:40 下午
 * @Created by stivepeim
 */
@Data
public class UserExtractResponse {
    // 提现数据总额
    @ApiModelProperty(value = "体现数据总额")
    private BigDecimal extractCountPrice;
    // 提现次数
    @ApiModelProperty(value = "提现次数")
    private Integer extractCountNum;
    // 提现用户id
//    private Integer euid;
}
