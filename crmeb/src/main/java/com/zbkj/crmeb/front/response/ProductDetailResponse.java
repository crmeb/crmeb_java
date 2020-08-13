package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.store.response.StoreProductRecommendResponse;
import com.zbkj.crmeb.store.response.StoreProductStoreInfoResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 商品详情
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-06
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
    private List activity;

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
