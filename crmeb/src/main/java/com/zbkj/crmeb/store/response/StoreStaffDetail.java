package com.zbkj.crmeb.store.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Classname StoreStaffDetail
 * @Description 核销月详情Response
 * @Date 8/31/20 2:50 下午
 * @Created by stivepeim
 */
@Data
public class StoreStaffDetail {
    private Integer count;
    private BigDecimal price;
    private String time;
}
