package com.zbkj.crmeb.store.request;

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
 * 商品库存
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreProductStockRequest对象", description="库存修改")
public class StoreProductStockRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品ID, 多个逗号分割，如果是修改所有的，参数值为 all", required = true)
    @NotBlank(message = "请选择商品")
    private String idList;

    @ApiModelProperty(value = "类型， 增加 add | 修改 update | 减少 diff", required = true)
    @NotBlank(message = "请选择类型")
    private String type;

    @ApiModelProperty(value = "数量", required = true)
    @Min(value = 0, message = "请填写数量")
    private Integer num;
}
