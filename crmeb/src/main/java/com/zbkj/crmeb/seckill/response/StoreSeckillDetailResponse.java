package com.zbkj.crmeb.seckill.response;

import com.zbkj.crmeb.front.response.ProductAttrResponse;
import com.zbkj.crmeb.front.response.SecKillDetailH5Response;
import com.zbkj.crmeb.store.response.StoreProductRecommendResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 商品秒杀详情响应对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreSeckillDetailResponse对象", description="商品秒杀详情响应对象")
public class StoreSeckillDetailResponse implements Serializable {

    private static final long serialVersionUID = -4101548587444327191L;

//    @ApiModelProperty(value = "商品信息")
//    private StoreSeckillStoreInfoResponse storeInfo;

    @ApiModelProperty(value = "产品属性")
    private List<ProductAttrResponse> productAttr;

    @ApiModelProperty(value = "商品属性详情")
    private HashMap<String,Object> productValue;

    @ApiModelProperty(value = "返佣金额区间")
    private String priceName;

//    @ApiModelProperty(value = "拼团，砍价，秒杀商品集合")
//    private List activity;

//    @ApiModelProperty(value = "优品推荐列表")
//    private List<StoreProductRecommendResponse> goodList;

//    @ApiModelProperty(value = "最新评价")
//    private Object reply;
//
//    @ApiModelProperty(value = "评价数量")
//    private Integer replyCount;
//
//    @ApiModelProperty(value = "好评率")
//    private Integer replyChance;

//    @ApiModelProperty(value = "主图base64")
//    private String base64Image;

    // 以下属性仅仅前端使用

//    @ApiModelProperty(value = "秒杀状态，前端用")
//    private Integer killStatus;
//
//    @ApiModelProperty(value = "秒杀状态String 未开始/进行中/活动已结束")
//    private String statusName;

    ////////////////////// 秒杀用到

//    @ApiModelProperty(value = "秒杀单属性AttrValueId")
//    private Integer aloneAttrValueId;

//    @ApiModelProperty(value = "限购数量 - 销量")
//    private Integer quota;
//
//    @ApiModelProperty(value = "限购总数")
//    private Integer quotaShow;

    @ApiModelProperty(value = "收藏标识")
    private Boolean userCollect;

    @ApiModelProperty(value = "秒杀商品信息")
    private SecKillDetailH5Response storeSeckill;
}
