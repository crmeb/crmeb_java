package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.record.ShoppingProductDayRecord;

import java.util.List;

/**
 * ShoppingProductDayRecordService 接口
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
public interface ShoppingProductDayRecordService extends IService<ShoppingProductDayRecord> {

    /**
     * 根据日期获取
     * @param date 日期
     * @return ShoppingProductDayRecord
     */
    ShoppingProductDayRecord getByDate(String date);

    /**
     * 获取时间区间的数据
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return ShoppingProductDayRecord
     */
    ShoppingProductDayRecord getByTimeInterval(String startDate, String endDate);

    /**
     * 根据时间范围返回趋势数据
     * @param startDate 开始日期,格式yyyy-MM-dd
     * @param endDate 结束日期,格式yyyy-MM-dd
     * @return List<ShoppingProductDayRecord>
     */
    List<ShoppingProductDayRecord> findTrendDataOfBetween(String startDate, String endDate);
}
