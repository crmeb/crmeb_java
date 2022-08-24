package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

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
public class ShopOrderDeliveryInfoVo {

    /** 快递公司ID，通过获取快递公司列表获取 */
    @TableField(value = "delivery_id")
    private Integer deliveryId;

    /** 快递单号 */
    @TableField(value = "waybill_id")
    private Integer waybillId;
}
