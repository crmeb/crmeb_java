package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

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
@ApiModel(value="StoreProductAttrItemRequest对象", description="属性值")
public class StoreProductAttrItemRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "属性名称", example = "颜色")
    @NotBlank(message = "请设置属性名称")
    private String attrName;

    @ApiModelProperty(value = "属性值， 多个逗号分割", example = "黑色，白色")
    @NotBlank(message = "请设置属性名值")
    private String attrValues;
}
