package com.zbkj.crmeb.article.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文章管理 Vo
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ArticleVo对象", description="文章管理表")
public class ArticleVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "文章管理ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "分类id")
    private String cid;

    @ApiModelProperty(value = "分类")
    private String categoryName;

    @ApiModelProperty(value = "文章标题")
    private String title;

    @ApiModelProperty(value = "文章作者")
    private String author;

    @ApiModelProperty(value = "文章图片 前端用")
    private List<String> imageInput = new ArrayList<>();

    @ApiModelProperty(value = "文章图片 后端用")
    private String imageInputs;

    @ApiModelProperty(value = "文章简介")
    private String synopsis;

    @ApiModelProperty(value = "文章分享标题")
    private String shareTitle;

    @ApiModelProperty(value = "文章分享简介")
    private String shareSynopsis;

    @ApiModelProperty(value = "浏览次数")
    private String visit;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "原文链接")
    private String url;

    @ApiModelProperty(value = "微信素材媒体id")
    private String mediaId;

    @ApiModelProperty(value = "状态")
    private Boolean status;

    @ApiModelProperty(value = "是否隐藏")
    private Boolean hide;

    @ApiModelProperty(value = "管理员id")
    private Integer adminId;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "商品关联id")
    private Integer productId;

    @ApiModelProperty(value = "是否热门(小程序)")
    private Boolean isHot;

    @ApiModelProperty(value = "是否轮播图(小程序)")
    private Boolean isBanner;

    @ApiModelProperty(value = "文章内容")
    private String content;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date updateTime;
}
