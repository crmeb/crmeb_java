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
@ApiModel(value="StoreProductRelationSearchRequest对象", description="商品点赞和收藏表")
public class StoreProductRelationSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "类型(收藏(collect）、点赞(like))")
//    @NotBlank(message = "请设置类型！")
    private String type;

    @ApiModelProperty(value = "用户ID")
    @NotBlank(message = "用户id不能为空")
    private Integer uid;
}
