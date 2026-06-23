package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.ProductRankingRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.record.ProductDayRecord;

/**
 * ProductDayRecordService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface ProductDayRecordService extends IService<ProductDayRecord> {

    /**
     * 获取商品排行榜
     * @param request 查询参数
     * @return PageInfo
     */
    PageInfo<ProductDayRecord> getRanking(ProductRankingRequest request);
}
