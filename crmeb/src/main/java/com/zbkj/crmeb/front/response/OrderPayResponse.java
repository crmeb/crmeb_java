package com.zbkj.crmeb.front.response;

import lombok.Data;

/**
 * 支付订单 Response
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
public class OrderPayResponse {

    private String status;

    private OrderPayItemResponse result; // 非线上支付对象

    private Object jsConfig; // 线上支付对象 todo 后面抽象对象

    private String message;

    public OrderPayResponse() {
    }

    public OrderPayResponse(String status, OrderPayItemResponse result) {
        this.status = status;
        this.result = result;
    }
}
