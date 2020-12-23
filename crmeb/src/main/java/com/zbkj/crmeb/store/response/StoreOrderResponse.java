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
 * 商品表
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
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
