package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 商品规则值(规格)表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_product_rule")
@ApiModel(value="StoreProductRuleItemRequest对象", description="商品规则值(规格)详情")
public class StoreProductRuleItemRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String[] detail;
}
