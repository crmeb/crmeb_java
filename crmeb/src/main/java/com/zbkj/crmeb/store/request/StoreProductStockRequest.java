package com.zbkj.crmeb.store.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 商品库存
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
@ApiModel(value="StoreProductStockRequest对象", description="库存修改")
public class StoreProductStockRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品ID", required = true)
    @NotEmpty(message = "请选择商品")
    private Integer productId;

    private Integer seckillId;

    private Integer bargainId;

    private Integer combinationId;

    @ApiModelProperty(value = "商品属性ID集合", required = true)
    @NotEmpty(message = "请选择商品属性id集合")
    private Integer attrId;

    @ApiModelProperty(value = "类型， 增加 add | 减少 diff", required = true)
    @NotBlank(message = "请选择类型")
    private String operationType;

    @ApiModelProperty(value = "数量", required = true)
    @Min(value = 0, message = "请填写数量")
    private Integer num;

    @ApiModelProperty(value = "商品类型 0=普通 1=秒杀", required = false)
    private Integer type;

    @ApiModelProperty(value = "商品SKU信息")
    private String suk;
}
