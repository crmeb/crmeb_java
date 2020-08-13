package com.zbkj.crmeb.article.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 文章管理表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ArticleRequest对象", description="文章管理表")
public class ArticleRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "分类id", required = true)
    @NotBlank(message = "请选择分类")
    private String cid;

    @ApiModelProperty(value = "文章标题", required = true)
    @NotBlank(message = "请填写文章标题")
    private String title;

    @ApiModelProperty(value = "文章作者", required = true)
    @NotBlank(message = "请填写文章标题")
    private String author;

    @ApiModelProperty(value = "文章图片", required = true)
    @NotBlank(message = "请上传文章图片")
    private String imageInput;

    @ApiModelProperty(value = "文章简介", required = true)
    @NotBlank(message = "请填写文章简介")
    private String synopsis;

    @ApiModelProperty(value = "文章分享标题", required = true)
    @NotBlank(message = "请填写文章分享标题")
    private String shareTitle;

    @ApiModelProperty(value = "文章分享简介", required = true)
    @NotBlank(message = "请填写文章分享简介")
    private String shareSynopsis;

    @ApiModelProperty(value = "排序", example = "0", required = true)
    private Integer sort;

    @ApiModelProperty(value = "原文链接")
    private String url;

    @ApiModelProperty(value = "是否热门(小程序)", example = "false")
    private Boolean isHot;

    @ApiModelProperty(value = "是否轮播图(小程序)" , example = "true")
    private Boolean isBanner;

    @ApiModelProperty(value = "文章内容", required = true)
    @NotBlank(message = "请填写文章内容")
    private String content;
}
