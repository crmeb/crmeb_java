package com.zbkj.crmeb.front.vo;

import lombok.Data;

/**
 * 再次下单ItemVO对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
public class OrderAgainItemVo {

    public OrderAgainItemVo() {
    }

    public OrderAgainItemVo(Integer type, String title, String msg) {
        this.type = type;
        this.title = title;
        this.msg = msg;
    }

    private Integer type;
    private String title;
    private String msg;
    private String payType;
    private String deliveryType;

}
