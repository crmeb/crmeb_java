package com.zbkj.crmeb.front.response;

import com.constants.Constants;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.store.response.StoreProductRecommendResponse;
import com.zbkj.crmeb.store.response.StoreProductStoreInfoResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品详情
 * +----------------------------------------------------------------------
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
@ApiModel(value="ProductDetailResponse对象", description="商品详情H5")
public class ProductDetailResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品信息")
    private StoreProductStoreInfoResponse storeInfo;

    @ApiModelProperty(value = "产品属性")
    private List<HashMap<String, Object>> productAttr;

    @ApiModelProperty(value = "商品属性详情")
    private HashMap<String,Object> productValue;

    @ApiModelProperty(value = "返佣金额区间")
    private String priceName;

    @ApiModelProperty(value = "拼团，砍价，秒杀商品集合")
    private String activity;

    @ApiModelProperty(value = "优品推荐列表")
    private List<StoreProductRecommendResponse> goodList;

    @ApiModelProperty(value = "最新评价")
    private Object reply;

    @ApiModelProperty(value = "评价数量")
    private Integer replyCount;

    @ApiModelProperty(value = "好评率")
    private Integer replyChance;

    @ApiModelProperty(value = "主图base64")
    private String base64Image;


}
