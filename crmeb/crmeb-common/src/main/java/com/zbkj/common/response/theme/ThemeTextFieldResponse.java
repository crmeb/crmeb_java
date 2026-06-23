package com.zbkj.common.response.theme;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 主题文本字段响应对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ThemeTextFieldResponse对象", description = "主题文本字段响应对象")
public class ThemeTextFieldResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户数据可绑定字段")
    private List<ThemeTextFieldItemResponse> user;

    @ApiModelProperty(value = "文章数据可绑定字段")
    private List<ThemeTextFieldItemResponse> article;

    @ApiModelProperty(value = "优惠券数据可绑定字段")
    private List<ThemeTextFieldItemResponse> coupon;

    @ApiModelProperty(value = "商品数据可绑定字段")
    private List<ThemeTextFieldItemResponse> product;
}
