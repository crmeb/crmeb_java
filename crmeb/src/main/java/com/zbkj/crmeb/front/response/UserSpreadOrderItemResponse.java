package com.zbkj.crmeb.front.response;

import com.common.CommonPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 推广订单信息
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserSpreadOrderItemResponse对象", description="推广订单信息")
public class UserSpreadOrderItemResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "推广条数")
    private Integer count = 0;

    @ApiModelProperty(value = "推广年月")
    private String time;

    @ApiModelProperty(value = "推广订单信息")
    private List<UserSpreadOrderItemChildResponse> child = new ArrayList<>();
}
