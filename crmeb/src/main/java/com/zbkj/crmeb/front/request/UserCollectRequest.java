package com.zbkj.crmeb.front.request;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 商品点赞和收藏表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_product_relation")
@ApiModel(value="UserCollectRequest对象", description="商品点赞和收藏表")
public class UserCollectRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品ID")
    @JsonProperty("id")
    @Min(value = 1, message = "请选择产品")
    private Integer productId;

    @ApiModelProperty(value = "产品类型|store=普通产品,product_seckill=秒杀产品(默认 普通产品 store)")
    @JsonProperty("category")
    @NotBlank(message = "请选择产品类型")
    private String category;
}
