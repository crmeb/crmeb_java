package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.record.ShoppingProductDayRecord;
import com.zbkj.service.dao.ShoppingProductDayRecordDao;
import com.zbkj.service.service.ShoppingProductDayRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * ShoppingProductDayRecordService 接口实现
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
@Service
public class ShoppingProductDayRecordServiceImpl extends ServiceImpl<ShoppingProductDayRecordDao, ShoppingProductDayRecord> implements ShoppingProductDayRecordService {

    @Resource
    private ShoppingProductDayRecordDao dao;

    /**
     * 根据日期获取
     * @param date 日期
     * @return ShoppingProductDayRecord
     */
    @Override
    public ShoppingProductDayRecord getByDate(String date) {
        LambdaQueryWrapper<ShoppingProductDayRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShoppingProductDayRecord::getDate, date);
        ShoppingProductDayRecord dayRecord = dao.selectOne(lqw);
        if (ObjectUtil.isEmpty(dayRecord)) {
            dayRecord = new ShoppingProductDayRecord();
            dayRecord.setDate(date);
            dayRecord.setAddProductNum(0);
            dayRecord.setPageView(0);
            dayRecord.setCollectNum(0);
            dayRecord.setAddCartNum(0);
            dayRecord.setOrderProductNum(0);
            dayRecord.setOrderSuccessProductNum(0);
        }
        return dayRecord;
    }

    /**
     * 获取时间区间的数据
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return ShoppingProductDayRecord
     */
    @Override
    public ShoppingProductDayRecord getByTimeInterval(String startDate, String endDate) {
        QueryWrapper<ShoppingProductDayRecord> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(sum(add_product_num),0) as add_product_num",
                "IFNULL(sum(page_view),0) as page_view",
                "IFNULL(sum(collect_num),0) as collect_num",
                "IFNULL(sum(add_cart_num),0) as add_cart_num",
                "IFNULL(sum(order_product_num),0) as order_product_num",
                "IFNULL(sum(order_success_product_num),0) as order_success_product_num");
        wrapper.ge("date", startDate);
        wrapper.le("date", endDate);
        return dao.selectOne(wrapper);
    }

    /**
     * 根据时间范围返回趋势数据
     * @param startDate 开始日期,格式yyyy-MM-dd
     * @param endDate 结束日期,格式yyyy-MM-dd
     * @return List<ShoppingProductDayRecord>
     */
    @Override
    public List<ShoppingProductDayRecord> findTrendDataOfBetween(String startDate, String endDate) {
        QueryWrapper<ShoppingProductDayRecord> wrapper = new QueryWrapper<>();
        wrapper.select("id", "date", "page_view", "collect_num", "order_product_num", "order_success_product_num");
        wrapper.between("date", startDate, endDate);
        wrapper.orderByAsc("id");
        return dao.selectList(wrapper);
    }
}

