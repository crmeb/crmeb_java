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
public class ShopOrderAddVo {

    /** 创建时间 */
    @TableField(value = "create_time")
    private String createTime;

    /** 商家自定义订单ID */
    @TableField(value = "out_order_id")
    private String outOrderId;

    /** 用户的openid */
    private String openid;

    /** 商家小程序该订单的页面path，用于微信侧订单中心跳转 */
    private String path;

    /** 下单时小程序的场景值，可通getLaunchOptionsSync或onLaunch/onShow拿到 */
    private Integer scene;

    /** 订单详情 */
    @TableField(value = "order_detail")
    private ShopOrderDetailAddVo orderDetail;

    /** 交付详情 */
    @TableField(value = "delivery_detail")
    private ShopOrderDeliveryDetailAddVo deliveryDetail;

    /** 地址详情 */
    @TableField(value = "address_info")
    private ShopOrderAddressInfoAddVo addressInfo;

    /** 用户id */
    private Integer outUserId;
}
