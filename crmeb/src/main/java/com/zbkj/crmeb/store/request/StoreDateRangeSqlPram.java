package com.zbkj.crmeb.store.request;

import lombok.Data;

/**
 * @Classname StoreDateRangeSqlPram
 * @Description 订单时间参数，开始时间结束时间
 * @Date 9/4/20 10:09 上午
 * @Created by stivepeim
 */
@Data
public class StoreDateRangeSqlPram {

    public StoreDateRangeSqlPram(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private String startTime;
    private String endTime;
}
