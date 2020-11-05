package com.zbkj.crmeb.front.response;

import lombok.Data;

/**
 * 支付订单 Response item
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
public class OrderPayItemResponse {
    private String key;
    private Object orderId;

    public OrderPayItemResponse() {
    }

    public OrderPayItemResponse(String key, Object orderId) {
        this.key = key;
        this.orderId = orderId;
    }
}
