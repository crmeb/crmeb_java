package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 商品属性表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_product_attr")
@ApiModel(value="StoreProductAttrRequest对象", description="规格")
public class StoreProductAttrRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "规格名称")
    private String attrName;

    @ApiModelProperty(value = "属性值")
    private List<StoreProductAttrItemRequest> attrValues;
}
