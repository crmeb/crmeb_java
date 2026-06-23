package com.zbkj.common.request.theme;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 主题新增/修改请求
 */
@Data
@ApiModel(value = "ThemeRequest对象", description = "主题新增/修改请求")
public class ThemeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "版本号", required = true)
    @NotBlank(message = "请填写版本号")
    @Length(max = 255, message = "版本号长度不能超过255个字符")
    private String version;

    @ApiModelProperty(value = "主题名称", required = true)
    @NotBlank(message = "请填写主题名称")
    @Length(max = 255, message = "主题名称长度不能超过255个字符")
    private String title;

    @ApiModelProperty(value = "主题简介")
    @Length(max = 255, message = "主题简介长度不能超过255个字符")
    private String info;

    @ApiModelProperty(value = "主题类型，0:自建主题，1:广场主题")
    private Integer type;

    @ApiModelProperty(value = "首页数据")
    private String homeData;

    @ApiModelProperty(value = "首页图片")
    @Length(max = 255, message = "首页图片长度不能超过255个字符")
    private String homeImage;

    @ApiModelProperty(value = "首页数据来源id")
    private Integer homeDataId;

    @ApiModelProperty(value = "首页数据更新时间")
    private Integer homeDataUpdateTime;

    @ApiModelProperty(value = "分类页数据")
    private String categoryData;

    @ApiModelProperty(value = "分类页图片")
    @Length(max = 255, message = "分类页图片长度不能超过255个字符")
    private String categoryImage;

    @ApiModelProperty(value = "分类页数据来源id")
    private Integer categoryDataId;

    @ApiModelProperty(value = "分类页数据更新时间")
    private Integer categoryDataUpdateTime;

    @ApiModelProperty(value = "商品详情页数据")
    private String detailData;

    @ApiModelProperty(value = "商城详情页图片")
    @Length(max = 255, message = "商城详情页图片长度不能超过255个字符")
    private String detailImage;

    @ApiModelProperty(value = "商品详情页数据来源id")
    private Integer detailDataId;

    @ApiModelProperty(value = "商品详情页数据更新时间")
    private Integer detailDataUpdateTime;

    @ApiModelProperty(value = "个人中心数据")
    private String userData;

    @ApiModelProperty(value = "个人中心图片")
    @Length(max = 255, message = "个人中心图片长度不能超过255个字符")
    private String userImage;

    @ApiModelProperty(value = "个人中心数据来源id")
    private Integer userDataId;

    @ApiModelProperty(value = "个人中心数据更新时间")
    private Integer userDataUpdateTime;

    @ApiModelProperty(value = "主题风格数据")
    private String themeData;

    @ApiModelProperty(value = "主题风格数据来源id")
    private Integer themeDataId;

    @ApiModelProperty(value = "主题风格数据更新时间")
    private Integer themeDataUpdateTime;

    @ApiModelProperty(value = "首页默认数据")
    private String homeDefaultData;

    @ApiModelProperty(value = "首页默认图片")
    @Length(max = 255, message = "首页默认图片长度不能超过255个字符")
    private String homeDefaultImage;

    @ApiModelProperty(value = "分类页默认数据")
    private String categoryDefaultData;

    @ApiModelProperty(value = "分类页默认图片")
    @Length(max = 255, message = "分类页默认图片长度不能超过255个字符")
    private String categoryDefaultImage;

    @ApiModelProperty(value = "商品详情页默认数据")
    private String detailDefaultData;

    @ApiModelProperty(value = "商城详情页默认图片")
    @Length(max = 255, message = "商城详情页默认图片长度不能超过255个字符")
    private String detailDefaultImage;

    @ApiModelProperty(value = "个人中心默认数据")
    private String userDefaultData;

    @ApiModelProperty(value = "个人中心默认图片")
    @Length(max = 255, message = "个人中心默认图片长度不能超过255个字符")
    private String userDefaultImage;

    @ApiModelProperty(value = "主题风格默认数据")
    private String themeDefaultData;

    @ApiModelProperty(value = "页面类型：theme主题，micro微页面")
    @Length(max = 255, message = "页面类型长度不能超过255个字符")
    private String pageType;

    @ApiModelProperty(value = "是否使用")
    private Integer isUse;
}
