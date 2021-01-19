package com.zbkj.crmeb.article.request;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * 文章管理 搜索Request
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
@ApiModel(value="ArticleSearchRequest对象", description="文章管理表")
public class ArticleSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "分类id", example = "")
    private String cid;

    @ApiModelProperty(value = "搜索关键字")
    private String keywords;

    @ApiModelProperty(value = "是否有微信素材媒体id")
    private Boolean isHaveMediaId = null;

    @ApiModelProperty(value = "是否热门(小程序)", example = "")
    private Boolean isHot = null;

    @ApiModelProperty(value = "是否轮播图(小程序)" , example = "")
    private Boolean isBanner = null;

    @ApiModelProperty(value = "是否隐藏", example = "")
    private Boolean hide = null;

    @ApiModelProperty(value = "状态" , example = "")
    private Boolean status = null;

}
