package com.zbkj.crmeb.store.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * @Classname StoreOrderStatisticsResponse
 * @Description TODO
 * @Date 9/2/20 5:58 下午
 * @Created by stivepeim
 */
@Data
public class StoreOrderStatisticsResponse {
    @ApiModelProperty(value = "订单数图标数据")
    private List<StoreOrderStatisticsChartItemResponse> chart; // 订单数图标数据

    @ApiModelProperty(value = "时间区间增长率")
    private Integer growthRate; // 时间区间增长率

    @ApiModelProperty(value = "同比")
    private String increaseTime;

    @ApiModelProperty(value = "同比上个时间区间增长营业额 1=增长，2=减少")
    private Integer increaseTimeStatus; // 同比上个时间区间增长营业额 1=增长，2=减少

    @ApiModelProperty(value = "时间区间订单数")
    private BigDecimal time;
}
