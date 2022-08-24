package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

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
public class ShopOrderPayInfoVo {

    /** 支付方式（目前只有"微信支付"） */
    @TableField(value = "pay_method")
    private String payMethod;

    /** 预支付ID */
    @TableField(value = "prepay_id")
    private String prepayId;

    /** 预付款时间（拿到prepay_id的时间） */
    @TableField(value = "prepay_time")
    private Integer prepayTime;

    /** 支付ID */
    @TableField(value = "transaction_id")
    private String transactionId;

    /** 付款时间（拿到transaction_id的时间） */
    @TableField(value = "pay_time")
    private Integer payTime;
}
