package com.zbkj.common.request;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/** 易联云打印商品详情
 * @program: crmeb
 * @author: 大粽子
 * @create: 2021-11-27 10:56
 **/
@Data
public class YlyPrintRequestGoods {
    private String GoodsName;
    private String UnitPrice;
    private String Num;
    private String Money;

    public YlyPrintRequestGoods(String goodsName, String unitPrice, String num, String money) {
        // 打印商品名称只打印前10位
        String LastGoodsName = goodsName;
        if(StringUtils.isNotBlank(goodsName) && goodsName.length() > 10){
            LastGoodsName = goodsName.substring(0,11);
        }
        GoodsName = LastGoodsName;
        UnitPrice = unitPrice;
        Num = num;
        Money = money;
    }
}
