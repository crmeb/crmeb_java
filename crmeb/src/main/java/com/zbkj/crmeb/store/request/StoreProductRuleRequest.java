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
@ApiModel(value="StoreProductRuleRequest对象", description="商品规则值(规格)表")
public class StoreProductRuleRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "规则id")
    private Integer id;

    @ApiModelProperty(value = "规格名称")
    private String ruleName;

    @ApiModelProperty(value = "规格值【JSON字符串】 [{\\\"detail\\\": [\\\"string\\\"],\\\"title\\\": \\\"string\\\"}]")
    private String ruleValue;
}
