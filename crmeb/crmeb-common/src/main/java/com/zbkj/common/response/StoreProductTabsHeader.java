package com.zbkj.common.response;

import lombok.Data;

/**
 * StoreProductTabsHeader
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
public class StoreProductTabsHeader {
    private Integer count;
    private String name;
    private Integer type; // 1=出售中商品 2=仓库中商品 4=已经售罄商品 5=警戒库存 6=商品回收站

    public StoreProductTabsHeader() {
    }

    public StoreProductTabsHeader(Integer count, String name, Integer type) {
        this.count = count;
        this.name = name;
        this.type = type;
    }
}
