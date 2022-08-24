package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 同步订单支付结果Vo对象
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
public class ShopOrderPayVo {

    /** 订单ID */
    @TableField(value = "order_id")
    private Integer orderId;

    /** 商家自定义订单ID，与 order_id 二选一 */
    @TableField(value = "out_order_id")
    private String outOrderId;

    /** 用户的openid */
    private String openid;

    /** 类型，默认1:支付成功,2:支付失败,3:用户取消,4:超时未支付;5:商家取消;10:其他原因取消 */
    @TableField(value = "action_type")
    private Integer actionType;

    /** 其他具体原因 */
    @TableField(value = "action_remark")
    private String actionRemark;

    /** 支付订单号，action_type=1时必填 */
    @TableField(value = "transaction_id")
    private String transactionId;

    /** 支付完成时间，action_type=1时必填 */
    @TableField(value = "pay_time")
    private String payTime;
}
