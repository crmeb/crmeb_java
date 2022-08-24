package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.record.UserVisitRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 用户访问记录表 Mapper 接口
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
public interface UserVisitRecordDao extends BaseMapper<UserVisitRecord> {

    /**
     * 获取活跃用户数
     * @param date 日期
     * @return Integer
     */
    Integer getActiveUserNumByDate(@Param("date") String date);

    /**
     * 通过时间段获取活跃用户数
     * @param startDate 日期
     * @param endDate 日期
     * @return Integer
     */
    Integer getActiveUserNumByPeriod(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
