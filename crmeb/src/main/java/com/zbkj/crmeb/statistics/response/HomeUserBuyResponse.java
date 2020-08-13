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
@ApiModel(value="HomeUserBuyResponse对象", description="用户购买统计")
public class HomeUserBuyResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "未消费用户")
    private Object no;

    @ApiModelProperty(value = "消费一次用户")
    private Object once;

    @ApiModelProperty(value = "留存用户")
    private Object history;

    @ApiModelProperty(value = "回流客户")
    private Object back;
}
