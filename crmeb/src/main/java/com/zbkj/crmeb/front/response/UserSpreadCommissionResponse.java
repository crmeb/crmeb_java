package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.user.model.UserBill;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 推广佣金明细
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-06-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserSpreadCommissionResponse对象", description="推广佣金明细")
public class UserSpreadCommissionResponse implements Serializable {

    private static final long serialVersionUID=1L;

    public UserSpreadCommissionResponse() {}
    public UserSpreadCommissionResponse(String date, List<UserBill> list) {
        this.date = date;
        this.list = list;
    }

    @ApiModelProperty(value = "月份")
    private String date;

    @ApiModelProperty(value = "数据")
    private List<UserBill> list;

}
