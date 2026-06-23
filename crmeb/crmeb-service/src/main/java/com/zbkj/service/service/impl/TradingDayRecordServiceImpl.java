package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.record.TradingDayRecord;
import com.zbkj.service.dao.TradingDayRecordDao;
import com.zbkj.service.service.TradingDayRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
public class TradingDayRecordServiceImpl extends ServiceImpl<TradingDayRecordDao, TradingDayRecord> implements TradingDayRecordService {

    @Resource
    private TradingDayRecordDao dao;

    /**
     * 根据日期获取记录
     * @param date 日期，yyyy-MM-dd
     * @return TradingDayRecord
     */
    @Override
    public TradingDayRecord getByDate(String date) {
        LambdaQueryWrapper<TradingDayRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(TradingDayRecord::getDate, date);
        TradingDayRecord record = dao.selectOne(lqw);
        if (ObjectUtil.isEmpty(record)) {
            record = new TradingDayRecord();
            record.setDate(date);
            record.setProductOrderNum(0);
            record.setProductOrderPayNum(0);
            record.setProductOrderPayFee(BigDecimal.ZERO);
            record.setProductOrderRefundNum(0);
            record.setProductOrderRefundFee(BigDecimal.ZERO);
            record.setRechargeOrderNum(0);
            record.setRechargeOrderFee(BigDecimal.ZERO);
            record.setBalancePayFee(BigDecimal.ZERO);
            record.setBrokerageFee(BigDecimal.ZERO);
        }
        return record;
    }

    /**
     * 获取时间段内的数据
     * @param startDate 日期，yyyy-MM-dd
     * @param endDate 日期，yyyy-MM-dd
     * @return TradingDayRecord
     */
    @Override
    public TradingDayRecord getByTimeInterval(String startDate, String endDate) {
        QueryWrapper<TradingDayRecord> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(sum(product_order_num),0) as product_order_num",
                "IFNULL(sum(product_order_pay_num),0) as product_order_pay_num",
                "IFNULL(sum(product_order_pay_fee),0) as product_order_pay_fee",
                "IFNULL(sum(product_order_refund_num),0) as product_order_refund_num",
                "IFNULL(sum(product_order_refund_fee),0) as product_order_refund_fee",
                "IFNULL(sum(recharge_order_num),0) as recharge_order_num",
                "IFNULL(sum(recharge_order_fee),0) as recharge_order_fee",
                "IFNULL(sum(balance_pay_fee),0) as balance_pay_fee",
                "IFNULL(sum(brokerage_fee),0) as brokerage_fee");
        wrapper.between("date", startDate, endDate);
        TradingDayRecord record = dao.selectOne(wrapper);
        if (ObjectUtil.isEmpty(record)) {
            record = new TradingDayRecord();
            record.setProductOrderNum(0);
            record.setProductOrderPayNum(0);
            record.setProductOrderPayFee(BigDecimal.ZERO);
            record.setProductOrderRefundNum(0);
            record.setProductOrderRefundFee(BigDecimal.ZERO);
            record.setRechargeOrderNum(0);
            record.setRechargeOrderFee(BigDecimal.ZERO);
            record.setBalancePayFee(BigDecimal.ZERO);
            record.setBrokerageFee(BigDecimal.ZERO);
        }
        return record;
    }

    /**
     * 根据时间范围返回趋势数据
     * @param startDate 开始日期,格式yyyy-MM-dd
     * @param endDate 结束日期,格式yyyy-MM-dd
     * @return List<TradingDayRecord>
     */
    @Override
    public List<TradingDayRecord> findTrendDataOfBetween(String startDate, String endDate) {
        QueryWrapper<TradingDayRecord> wrapper = new QueryWrapper<>();
        wrapper.select("id", "date", "product_order_pay_fee", "product_order_refund_fee", "recharge_order_fee",
                "balance_pay_fee", "brokerage_fee");
        wrapper.between("date", startDate, endDate);
        wrapper.orderByAsc("id");
        return dao.selectList(wrapper);
    }
}

