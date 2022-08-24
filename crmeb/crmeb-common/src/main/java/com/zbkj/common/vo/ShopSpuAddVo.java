package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 *  自定义交易组件商品Vo
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
public class ShopSpuAddVo {

    /** 商家自定义商品ID(修改时与product_id二选一) */
    @TableField(value = "out_product_id")
    private String outProductId;

    /** 交易组件平台内部商品ID */
    @TableField(value = "product_id")
    private Integer productId;

    /** 标题 */
    private String title;

    /** 绑定的小程序商品路径 */
    private String path;

    /** 类主图,多张,列表 */
    @TableField(value = "head_img")
    private List<String> headImg;

    /** 商品资质图片 */
    @TableField(value = "qualification_pics")
    private List<String> qualificationPics;

    /** 商品详情 */
    @TableField(value = "desc_info")
    private ShopSpuInfoVo descInfo;

    /** 第三级类目ID */
    @TableField(value = "third_cat_id")
    private Integer thirdCatId;

    /** 品牌id */
    @TableField(value = "brand_id")
    private Integer brandId;

    /** 预留字段，用于版本控制 */
    @TableField(value = "info_version")
    private String infoVersion;

    /** sku数组 */
    private List<ShopSpuSkuVo> skus;
}
