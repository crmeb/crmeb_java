package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 *  自定义交易组件商品SkuVo
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
public class ShopSpuSkuVo {

    /** 商家自定义商品ID */
    @TableField(value = "out_product_id")
    private String outProductId;

    /** 商家自定义skuID */
    @TableField(value = "out_sku_id")
    private String outSkuId;

    /** sku小图 */
    @TableField(value = "thumb_img")
    private String thumbImg;

    /** 售卖价格,以分为单位 */
    @TableField(value = "sale_price")
    private Long salePrice;

    /** 市场价格,以分为单位 */
    @TableField(value = "market_price")
    private Long marketPrice;

    /** 库存 */
    @TableField(value = "stock_num")
    private Integer stockNum;

    /** 条形码 */
    private String barcode;

    /** 商品编码 */
    @TableField(value = "sku_code")
    private String skuCode;

    /** 销售属性（自定义） */
    @TableField(value = "sku_attrs")
    private List<ShopSpuSkuAttrVo> skuAttrs;
}
