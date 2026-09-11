package com.zbkj.common.response.theme;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 主题列表响应对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ThemeResponse对象", description = "主题列表响应对象")
public class ThemeResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "主题名称")
    private String title;

    @ApiModelProperty(value = "主题简介")
    private String info;

    @ApiModelProperty(value = "主题预览地址")
    private String showUrl;

    @ApiModelProperty(value = "主题类型，自建主题/广场主题")
    private String type;

    @ApiModelProperty(value = "首页图片")
    private String homeImage;

    @ApiModelProperty(value = "首页数据来源id")
    private Integer homeDataId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "首页数据更新时间")
    private Date homeDataUpdateTime;

    @ApiModelProperty(value = "分类页图片")
    private String categoryImage;

    @ApiModelProperty(value = "分类页数据来源id")
    private Integer categoryDataId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "分类页数据更新时间")
    private Date categoryDataUpdateTime;

    @ApiModelProperty(value = "商城详情页图片")
    private String detailImage;

    @ApiModelProperty(value = "商品详情页数据来源id")
    private Integer detailDataId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "商品详情页数据更新时间")
    private Date detailDataUpdateTime;

    @ApiModelProperty(value = "个人中心图片")
    private String userImage;

    @ApiModelProperty(value = "个人中心数据来源id")
    private Integer userDataId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "个人中心数据更新时间")
    private Date userDataUpdateTime;

    @ApiModelProperty(value = "主题风格数据来源id")
    private Integer themeDataId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "主题风格数据更新时间")
    private Date themeDataUpdateTime;

    @ApiModelProperty(value = "首页默认图片")
    private String homeDefaultImage;

    @ApiModelProperty(value = "分类页默认图片")
    private String categoryDefaultImage;

    @ApiModelProperty(value = "商城详情页默认图片")
    private String detailDefaultImage;

    @ApiModelProperty(value = "个人中心默认图片")
    private String userDefaultImage;

    @ApiModelProperty(value = "页面类型：theme主题，micro微页面")
    private String pageType;

    @ApiModelProperty(value = "是否使用")
    private Integer isUse;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date upTime;
}
