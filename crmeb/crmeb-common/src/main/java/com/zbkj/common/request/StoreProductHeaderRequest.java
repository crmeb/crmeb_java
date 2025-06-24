package com.zbkj.common.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商品管理头部统计-搜索条件
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoreProductHeaderRequest {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "分类ID， 多个逗号分隔")
    private String cateId;

    @ApiModelProperty(value = "关键字搜索， 支持(商品名称, 关键字, 商品条码)")
    private String keywords;
}
