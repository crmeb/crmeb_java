package com.zbkj.crmeb.store.response;

import com.common.CommonPage;
import com.zbkj.crmeb.system.response.StoreOrderItemResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreOrderCountResponse对象", description="订单数量")
public class StoreOrderResponse implements Serializable {

    @ApiModelProperty(value = "top")
    private StoreOrderTopItemResponse top;

    @ApiModelProperty(value = "状态")
    private StoreOrderCountItemResponse status;

    @ApiModelProperty(value = "列表")
    private CommonPage<StoreOrderItemResponse> list;


}
