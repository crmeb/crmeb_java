package com.zbkj.common.request;

import lombok.Data;

import java.util.List;

/**
 * 易联云打印参数
 * @program: crmeb
 * @author: 大粽子
 * @create: 2021-11-27 10:51
 **/
@Data
public class YlyPrintRequest {
    private String BusinessName;
    private String OrderNo;
    private String Date;
    private String Name;
    private String Phone;
    private String Address;
    private String Note;
    private List<YlyPrintRequestGoods> Goods;
    private String Amount;      // 合计
    private String Discount;    // 优惠
    private String Postal;      // 邮费
    private String Deduction;   // 折扣
    private String PayMoney;    // 实际金额
}
