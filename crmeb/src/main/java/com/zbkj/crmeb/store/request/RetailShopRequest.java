package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author stivepeim
 * @title: RetailShopRequest
 * @projectName crmeb
 * @description: 分销设置参数
 * @date 2020/7/2110:29
 */
@Data
public class RetailShopRequest {

    public RetailShopRequest() {
    }

    public RetailShopRequest(String brokerageFuncStatus, String storeBrokerageStatus, String storeBrokerageRatio, String storeBrokerageTwo, String userExtractMinPrice, String userExtractBank, String extractTime, String storeBrokeragePrice) {
        this.brokerageFuncStatus = brokerageFuncStatus;
        this.storeBrokerageStatus = storeBrokerageStatus;
        this.storeBrokerageRatio = storeBrokerageRatio;
        this.storeBrokerageTwo = storeBrokerageTwo;
        this.userExtractMinPrice = userExtractMinPrice;
        this.userExtractBank = userExtractBank;
        this.extractTime = extractTime;
        this.storeBrokeragePrice = storeBrokeragePrice;
    }
    public RetailShopRequest(List<String> keys){
            this.brokerageFuncStatus = keys.get(0);
            this.storeBrokerageStatus = keys.get(1);
            this.storeBrokerageRatio = keys.get(2);
            this.storeBrokerageTwo = keys.get(3);
            this.userExtractMinPrice = keys.get(4);
            this.userExtractBank = keys.get(5);
            this.extractTime = keys.get(6);
            this.storeBrokeragePrice = keys.get(7);
    }

    @ApiModelProperty(value = "是否启用分销")
    @NotNull(message = "是否启用分销 不能为空")
    private String brokerageFuncStatus;

    @ApiModelProperty(value = "分销模式")
    @NotNull(message = "分销模式 不能为空")
    private String storeBrokerageStatus;

    @ApiModelProperty(value = "一级返佣比例")
    @NotNull(message = "一级返佣比例 不能为空")
    private String storeBrokerageRatio;

    @ApiModelProperty(value = "二级返佣比例")
    @NotNull(message = "二级返佣比例 不能为空")
    private String storeBrokerageTwo;

    @ApiModelProperty(value = "分销关系绑定")
    @NotNull(message = "分销关系绑定 不能为空")
    private String brokerageBindind;

    @ApiModelProperty(value = "用户提现最低金额")
    @NotNull(message = "用户提现最低金额 不能为空")
    private String userExtractMinPrice;

    @ApiModelProperty(value = "提现银行")
    @NotNull(message = "提现银行 不能为空")
    private String userExtractBank;

    @ApiModelProperty(value = "冻结时间")
    @NotNull(message = "冻结时间 不能为空")
    private String extractTime;

    @ApiModelProperty(value = "人人分销满足金额")
    @NotNull(message = "人人分销满足金额 不能为空")
    private String storeBrokeragePrice;
}
