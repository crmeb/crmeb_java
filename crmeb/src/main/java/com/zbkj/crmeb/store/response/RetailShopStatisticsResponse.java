package com.zbkj.crmeb.store.response;

import lombok.Data;

/**
 * 分销头部数据Response
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
