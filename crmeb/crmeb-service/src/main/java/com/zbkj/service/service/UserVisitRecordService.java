package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.record.UserVisitRecord;

/**
 * UserVisitRecordService 接口
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
public interface UserVisitRecordService extends IService<UserVisitRecord> {

    /**
     * 通过日期获取浏览量
     * @param date 日期
     * @return Integer
     */
    Integer getPageviewsByDate(String date);

    /**
     * 通过时间段获取浏览量
     * @param startDate 日期
     * @param endDate 日期
     * @return Integer
     */
    Integer getPageviewsByPeriod(String startDate, String endDate);

    /**
     * 通过日期获取活跃用户数
     * @param date 日期
     * @return Integer
     */
    Integer getActiveUserNumByDate(String date);

    /**
     * 通过时间段获取活跃用户数
     * @param startDate 日期
     * @param endDate 日期
     * @return Integer
     */
    Integer getActiveUserNumByPeriod(String startDate, String endDate);
}
