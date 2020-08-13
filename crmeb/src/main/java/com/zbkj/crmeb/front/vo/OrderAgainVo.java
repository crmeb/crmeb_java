package com.zbkj.crmeb.front.vo;

import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.response.StoreCartResponse;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import lombok.Data;

import java.util.List;

/**
 * @author stivepeim
 * @title: OrderAgainVo
 * @projectName crmeb
 * @description: 再次下单
 * @date 2020/7/1517:41
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
