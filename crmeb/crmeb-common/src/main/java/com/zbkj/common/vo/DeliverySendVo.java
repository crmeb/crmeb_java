package com.zbkj.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 * 订单发货Vo对象
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
public class DeliverySendVo {

    /** 订单ID */
    @TableField(value = "order_id")
    private Integer orderId;

    /** 商家自定义订单ID，与 order_id 二选一 */
    @TableField(value = "out_order_id")
    private String outOrderId;

    /** 用户的openid */
    private String openid;

    /** 发货完成标志位, 0: 未发完, 1:已发完 */
    @TableField(value = "finish_all_delivery")
    private Integer finishSllDelivery;

    /** 快递信息,delivery_type=1时必填 */
    @TableField(value = "delivery_list")
    private List<DeliveryInfoVo> deliveryList;
}
