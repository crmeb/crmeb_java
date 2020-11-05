package com.zbkj.crmeb.front.vo;

import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.response.StoreCartResponse;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import lombok.Data;

import java.util.List;

/**
 * 再次下单VO对象
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
public class OrderAgainVo {
    private StoreOrder storeOrder;
    private List<StoreOrderInfoVo> cartInfo;
    private OrderAgainItemVo status;
    private String payTime;
    private String addTime;
    private String statusPic;
    private Integer offlinePayStatus;
}
