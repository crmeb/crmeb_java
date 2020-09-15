package com.zbkj.crmeb.store.request;

import com.constants.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname StoreOrderStaticsticsRequest
 * @Description 订单统计详情request
 * @Date 9/4/20 12:23 下午
 * @Created by stivepeim
 */
@Data
public class StoreOrderStaticsticsRequest {
    @ApiModelProperty(value = "页码", example= Constants.DEFAULT_PAGE + "")
    private int page = Constants.DEFAULT_PAGE;

    @ApiModelProperty(value = "每页数量", example = Constants.DEFAULT_LIMIT + "")
    private int limit = Constants.DEFAULT_LIMIT;

    @ApiModelProperty(value = "today,yesterday,lately7,lately30,month,year,/yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm:ss/")
    private String dateLimit;

    @JsonIgnore
    private String startTime;

    @JsonIgnore
    private String endTime;
}
