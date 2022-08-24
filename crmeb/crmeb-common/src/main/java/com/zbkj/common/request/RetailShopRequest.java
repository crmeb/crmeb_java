package com.zbkj.common.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 分销设置参数
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
public class RetailShopRequest {

    public RetailShopRequest() {
    }

    @ApiModelProperty(value = "是否启用分销:1-启用，0-禁止")
    @NotNull(message = "是否启用分销 不能为空")
    private Integer brokerageFuncStatus;

//    @ApiModelProperty(value = "分销模式：1-指定分销，2-人人分销，3-满额分销")
//    @NotNull(message = "分销模式 不能为空")
//    private String storeBrokerageStatus;

    @ApiModelProperty(value = "分销额度：-1-关闭，0--用户购买金额大于等于设置金额时，用户自动成为分销员")
    @NotNull(message = "分销额度 不能为空")
    @Min(value = -1, message = "分销额度,不能小于-1")
    private Integer storeBrokerageQuota;

    @ApiModelProperty(value = "一级返佣比例")
    @NotNull(message = "一级返佣比例 不能为空")
    @Range(min = 0, max = 100, message = "一级返佣比例请在0-100中选择")
    private Integer storeBrokerageRatio;

    @ApiModelProperty(value = "二级返佣比例")
    @NotNull(message = "二级返佣比例 不能为空")
    @Range(min = 0, max = 100, message = "二级返佣比例在0-100中选择")
    private Integer storeBrokerageTwo;

    @ApiModelProperty(value = "分销关系绑定:0-所有用户，1-新用户")
    @NotNull(message = "分销关系绑定 不能为空")
    private Integer brokerageBindind;

    @ApiModelProperty(value = "用户提现最低金额")
    @NotNull(message = "用户提现最低金额 不能为空")
    @DecimalMin(value = "0", message = "用户提现最低金额最小为0")
    private BigDecimal userExtractMinPrice;

    @ApiModelProperty(value = "提现银行")
    @NotNull(message = "提现银行 不能为空")
    private String userExtractBank;

    @ApiModelProperty(value = "冻结时间")
    @NotNull(message = "冻结时间 不能为空")
    @Min(value = 0, message = "冻结时间最少为0天")
    private Integer extractTime;

    @ApiModelProperty(value = "是否展示分销气泡：0-展示，1-展示")
    @NotNull(message = "是否展示分销气泡 不能为空")
    @Range(min = 0, max = 1, message = "是否展示分销气泡只能选择0-1")
    private Integer storeBrokerageIsBubble;

}
