package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.category.model.Category;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 用户地址表
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
@ApiModel(value="IndexInfoItemResponse对象", description="用户登录返回数据")
public class IndexInfoItemResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "快速选择简介")
    private String fastInfo;

    @ApiModelProperty(value = "精品推荐简介")
    private String bastInfo;

    @ApiModelProperty(value = "首发新品简介")
    private String firstInfo;

    @ApiModelProperty(value = "促销单品简介")
    private String salesInfo;

    @ApiModelProperty(value = "快速选择分类个数")
    private String fastNumber;

    @ApiModelProperty(value = "精品推荐个数")
    private String bastNumber;

    @ApiModelProperty(value = "首发新品个数")
    private String firstNumber;

    @ApiModelProperty(value = "首页促销单品")
    private String promotionNumber;

    @ApiModelProperty(value = "分类")
    private List<Category> fastList;

    @ApiModelProperty(value = "精品推荐")
    private List<ProductResponse> bastList;

    @ApiModelProperty(value = "首发新品")
    private List<ProductResponse> firstList;

    @ApiModelProperty(value = "首页精品推荐图片")
    private List<HashMap<String, Object>> bastBanner;

}
