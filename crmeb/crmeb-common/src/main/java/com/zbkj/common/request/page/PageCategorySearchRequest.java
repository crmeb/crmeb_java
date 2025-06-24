package com.zbkj.common.request.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Auther: 大粽子
 * @Date: 2023/5/16 11:23
 * @Description: 页面DIY和设计用到的分类
 */
@Data
public class PageCategorySearchRequest {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "父类id")
    private Integer pid;

    @ApiModelProperty(value = "类型:link、special、product、product_category、custom")
    private String type;

    @ApiModelProperty(value = "分类名称")
    private String name;

    @ApiModelProperty(value = "层级 个位数")
    private Boolean level;

    @ApiModelProperty(value = "是否商户 0平台，商户id")
    private Integer isMer;
}
