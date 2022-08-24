package com.zbkj.common.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 商品点赞和收藏表
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreProductRelationRequest对象", description="商品点赞和收藏表")
public class UserCollectAllRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品ID")
    @JsonProperty("id")
    @Size(min = 1, message = "请选择产品")
    private Integer[] productId;

    @ApiModelProperty(value = "产品类型|store=普通产品,product_seckill=秒杀产品(默认 普通产品 store)")
    @NotBlank(message = "请选择产品类型")
    private String category;
}
