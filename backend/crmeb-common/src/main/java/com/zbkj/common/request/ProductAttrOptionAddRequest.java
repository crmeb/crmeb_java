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
 * <p>
 * 商品规格属性添加对象
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductAttrOptionAddRequest", description = "商品规格属性添加对象")
public class ProductAttrOptionAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID，新增时不填，修改时必填")
    private Integer id;

    @ApiModelProperty(value = "属性名", required = true)
    @NotBlank(message = "属性名不能为空")
    @Size(max = 30, message = "属性名长度不能超过30个字符")
    @JsonProperty("value")
    private String optionName;

    @ApiModelProperty(value = "图片")
    private String image;

    @ApiModelProperty(value = "排序")
    private Integer sort;
}
