package com.zbkj.crmeb.store.response;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @Classname StoreProductAttrValueResponse
 * @Description h5端使用 attrValueItem
 * @Date 2020/7/2 9:25 上午
 * @Created by stivepeim
 */
@Data
public class StoreProductAttrValueItemResponse {

    @ApiModelProperty(value = "属性名称")
    private String attr;

    @ApiModelProperty(value = "优惠券可用状态")
    private boolean check;// 优惠券可用状态
}
