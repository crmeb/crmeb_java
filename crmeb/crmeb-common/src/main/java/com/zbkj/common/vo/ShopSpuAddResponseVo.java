package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 *  自定义交易组件商品添加响应Vo
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
public class ShopSpuAddResponseVo {

    /** 交易组件平台内部商品ID */
    @TableField(value = "product_id")
    private Integer productId;

    /** 商家自定义商品ID */
    @TableField(value = "out_product_id")
    private String outProductId;

    /** 创建时间 */
    @TableField(value = "create_time")
    private String createTime;

    /** 更新时间 */
    @TableField(value = "update_time")
    private String updateTime;

    /** sku数组 */
    private List<ShopSpuAddSkuResponseVo> skus;
}
