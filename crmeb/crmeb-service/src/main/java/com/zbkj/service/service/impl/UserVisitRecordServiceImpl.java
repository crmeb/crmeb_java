package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.record.UserVisitRecord;
import com.zbkj.service.dao.UserVisitRecordDao;
import com.zbkj.service.service.UserVisitRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * UserVisitRecordServiceImpl 接口实现
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
@Service
public class UserVisitRecordServiceImpl extends ServiceImpl<UserVisitRecordDao, UserVisitRecord> implements UserVisitRecordService {

    @Resource
    private UserVisitRecordDao dao;

    /**
     * 通过日期获取浏览量
     * @param date 日期
     * @return Integer
     */
    @Override
    public Integer getPageviewsByDate(String date) {
        QueryWrapper<UserVisitRecord> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        wrapper.eq("date", date);
        return dao.selectCount(wrapper);
    }

    /**
     * 通过时间段获取浏览量
     * @param startDate 日期
     * @param endDate 日期
     * @return Integer
     */
    @Override
    public Integer getPageviewsByPeriod(String startDate, String endDate) {
        QueryWrapper<UserVisitRecord> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        wrapper.between("date", startDate, endDate);
        return dao.selectCount(wrapper);
    }

    /**
     * 通过日期获取活跃用户数
     * @param date 日期
     * @return Integer
     */
    @Override
    public Integer getActiveUserNumByDate(String date) {
        return dao.getActiveUserNumByDate(date);
    }

    /**
     * 通过时间段获取活跃用户数
     * @param startDate 日期
     * @param endDate 日期
     * @return Integer
     */
    @Override
    public Integer getActiveUserNumByPeriod(String startDate, String endDate) {
        return dao.getActiveUserNumByPeriod(startDate, endDate);
    }
}

