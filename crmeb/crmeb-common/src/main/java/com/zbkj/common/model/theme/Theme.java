package com.zbkj.common.model.theme;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 主题表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_theme")
@ApiModel(value = "Theme对象", description = "主题表")
public class Theme implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "自增ID")
    private Integer id;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "主题名称")
    private String title;

    @ApiModelProperty(value = "主题简介")
    private String info;

    @ApiModelProperty(value = "主题类型，0:自建主题，1:广场主题")
    private Integer type;

    @ApiModelProperty(value = "首页数据")
    private String homeData;

    @ApiModelProperty(value = "首页图片")
    private String homeImage;

    @ApiModelProperty(value = "首页数据来源id")
    private Integer homeDataId;

    @ApiModelProperty(value = "首页数据更新时间")
    private Integer homeDataUpdateTime;

    @ApiModelProperty(value = "分类页数据")
    private String categoryData;

    @ApiModelProperty(value = "分类页图片")
    private String categoryImage;

    @ApiModelProperty(value = "分类页数据来源id")
    private Integer categoryDataId;

    @ApiModelProperty(value = "分类页数据更新时间")
    private Integer categoryDataUpdateTime;

    @ApiModelProperty(value = "商品详情页数据")
    private String detailData;

    @ApiModelProperty(value = "商城详情页图片")
    private String detailImage;

    @ApiModelProperty(value = "商品详情页数据来源id")
    private Integer detailDataId;

    @ApiModelProperty(value = "商品详情页数据更新时间")
    private Integer detailDataUpdateTime;

    @ApiModelProperty(value = "个人中心数据")
    private String userData;

    @ApiModelProperty(value = "个人中心图片")
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
    private String homeDefaultImage;

    @ApiModelProperty(value = "分类页默认数据")
    private String categoryDefaultData;

    @ApiModelProperty(value = "分类页默认图片")
    private String categoryDefaultImage;

    @ApiModelProperty(value = "商品详情页默认数据")
    private String detailDefaultData;

    @ApiModelProperty(value = "商城详情页默认图片")
    private String detailDefaultImage;

    @ApiModelProperty(value = "个人中心默认数据")
    private String userDefaultData;

    @ApiModelProperty(value = "个人中心默认图片")
    private String userDefaultImage;

    @ApiModelProperty(value = "主题风格默认数据")
    private String themeDefaultData;

    @ApiModelProperty(value = "页面类型：theme主题，micro微页面")
    private String pageType;

    @ApiModelProperty(value = "是否使用")
    private Integer isUse;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @ApiModelProperty(value = "添加时间")
    private Integer addTime;

    @ApiModelProperty(value = "更新时间")
    private Integer upTime;
}
