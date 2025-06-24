package com.zbkj.common.request.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: 大粽子
 * @Date: 2023/5/16 11:26
 * @Description: 页面配置和DIY用到的页面链接搜索对象
 */
@Data
public class PageLinkSearchRequest {
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "分类id")
    private Integer cateId;

    @ApiModelProperty(value = "分组1:基础2:分销3:个人中心")
    private Boolean type;

    @ApiModelProperty(value = "页面名称")
    private String name;

    @ApiModelProperty(value = "页面链接")
    private String url;

    @ApiModelProperty(value = "参数")
    private String param;

    @ApiModelProperty(value = "事例")
    private String example;

    @ApiModelProperty(value = "状态")
    private Boolean status;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    @ApiModelProperty(value = "1是商户的链接")
    private Boolean isMer;

}
