package com.zbkj.crmeb.system.request;


import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 城市表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_city")
@ApiModel(value="SystemCitySearchRequest对象", description="城市表列表搜索条件")
public class SystemCitySearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "父级id", required = true, example= "0")
    @NotNull(message = "父级id不能为空")  //不可为空
    @DecimalMin(value = "0", message = "父级id必须大于等于0") //数字最小值为0
    private Integer parentId;

}
