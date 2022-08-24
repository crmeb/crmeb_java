package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 * 生成订单Vo对象
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
public class ShopOrderDetailAddVo {

    /** 商品详情数组 */
    @TableField(value = "product_infos")
    private List<ShopOrderProductInfoAddVo> productInfos;

    /** 支付详情数组 */
    @TableField(value = "pay_info")
    private ShopOrderPayInfoAddVo payInfo;

    /** 价格详情数组 */
    @TableField(value = "price_info")
    private ShopOrderPriceInfoVo priceInfo;
}
