package com.zbkj.crmeb.store.response;

import lombok.Data;

/**
 * @Classname RetailShopStatisticsResponse
 * @Description 分销头部数据Response
 * @Date 2020/6/23 10:01 上午
 * @Created by stivepeim
 */
@Data
public class RetailShopStatisticsResponse {
    public RetailShopStatisticsResponse() {
    }

    public RetailShopStatisticsResponse(String name, Integer count) {
        this.name = name;
        this.count = count;
    }

    private String name;
    private Integer count;
//    private String className;
}
