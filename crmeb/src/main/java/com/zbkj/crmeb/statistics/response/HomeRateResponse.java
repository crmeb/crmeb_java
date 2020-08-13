package com.zbkj.crmeb.statistics.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 主页增统计
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="HomeRateResponse对象", description="主页用户新增统计")
public class HomeRateResponse implements Serializable {

    private static final long serialVersionUID=1L;

    public HomeRateResponse() {
    }

    public HomeRateResponse(Object count, Object dayRate, Object weekRate, Object total) {
        this.count = count;
        this.dayRate = dayRate;
        this.weekRate = weekRate;
        this.total = total;
    }

    @ApiModelProperty(value = "昨日新增量")
    private Object count;

    @ApiModelProperty(value = "日同比率")
    private Object dayRate;

    @ApiModelProperty(value = "周同比率")
    private Object weekRate;

    @ApiModelProperty(value = "总数")
    private Object total;


}
