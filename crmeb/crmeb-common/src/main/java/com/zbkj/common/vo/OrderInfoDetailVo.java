package com.zbkj.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单详情Vo对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
public class OrderInfoDetailVo {

    /** 商品id */
    private Integer productId;

    /** 商品名称 */
    private String productName;

    /** 规格属性id */
    private Integer attrValueId;

    /** 商品图片 */
    private String image;

    /** sku */
    private String sku;

    /** 单价 */
    private BigDecimal price;

    /** 购买数量 */
    private Integer payNum;

    /** 重量 */
    private BigDecimal weight;

    /** 体积 */
    private BigDecimal volume;

    /** 运费模板ID */
    private Integer tempId;

    /** 获得积分 */
    private Integer giveIntegral;

    /** 是否评价 */
    private Integer isReply;

    /** 是否单独分佣 */
    private Boolean isSub;

    /** 会员价 */
    private BigDecimal vipPrice;

    /** 商品类型:0-普通，1-秒杀，2-砍价，3-拼团，4-视频号 */
    private Integer productType;
}
