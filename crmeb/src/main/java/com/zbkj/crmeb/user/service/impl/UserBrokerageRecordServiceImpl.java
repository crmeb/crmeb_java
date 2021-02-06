package com.zbkj.crmeb.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.BrokerageRecordConstants;
import com.constants.Constants;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.front.response.SpreadCommissionDetailResponse;
import com.zbkj.crmeb.user.dao.UserBrokerageRecordDao;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBrokerageRecord;
import com.zbkj.crmeb.user.service.UserBrokerageRecordService;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户佣金记录服务接口实现类
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
@Service
public class UserBrokerageRecordServiceImpl extends ServiceImpl<UserBrokerageRecordDao, UserBrokerageRecord> implements UserBrokerageRecordService {

    private static final Logger logger = LoggerFactory.getLogger(UserBrokerageRecordServiceImpl.class);

    @Resource
    private UserBrokerageRecordDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 根据订单编号获取记录列表
     * @param linkId 关联id
     * @param linkType 关联类型
     * @return 记录列表
     */
    @Override
    public List<UserBrokerageRecord> findListByLinkIdAndLinkType(String linkId, String linkType) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getLinkId, linkId);
        lqw.eq(UserBrokerageRecord::getLinkType, linkType);
        return dao.selectList(lqw);
    }

    /**
     * 获取记录(订单不可用此方法)
     * @param linkId 关联id
     * @param linkType 关联类型
     * @return 记录列表
     */
    @Override
    public UserBrokerageRecord getByLinkIdAndLinkType(String linkId, String linkType) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getLinkId, linkId);
        lqw.eq(UserBrokerageRecord::getLinkType, linkType);
        return dao.selectOne(lqw);
    }

    /**
     * 佣金解冻
     */
    @Override
    public void brokerageThaw() {
        // 查询需要解冻的佣金
        List<UserBrokerageRecord> thawList = findThawList();
        if (CollUtil.isEmpty(thawList)) {
            return;
        }
        for (UserBrokerageRecord record : thawList) {
            // 查询对应的用户
            User user = userService.getById(record.getUid());
            if (ObjectUtil.isNull(user)) {
                continue ;
            }
            record.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
            // 计算佣金余额
            BigDecimal balance = user.getBrokeragePrice().add(record.getPrice());
            record.setBalance(balance);

            // 分佣
            Boolean execute = transactionTemplate.execute(e -> {
                updateById(record);
                userService.operationBrokerage(record.getUid(), record.getPrice(), user.getBrokeragePrice(), "add");
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("佣金解冻处理—分佣出错，记录id = {}", record.getId()));
            }
        }

    }

    /**
     * 昨天得佣金
     * @param uid 用户uid
     * @return BigDecimal
     */
    @Override
    public BigDecimal getYesterdayIncomes(Integer uid) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserBrokerageRecord::getPrice);
        lqw.eq(UserBrokerageRecord::getUid, uid);
        dateLimitUtilVo dateLimit = DateUtil.getDateLimit(Constants.SEARCH_DATE_YESTERDAY);
        lqw.between(UserBrokerageRecord::getUpdateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        List<UserBrokerageRecord> recordList = dao.selectList(lqw);
        if (CollUtil.isEmpty(recordList)) {
            return BigDecimal.ZERO;
        }
        return recordList.stream().map(UserBrokerageRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取佣金明细列表根据uid
     * @param uid uid
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<SpreadCommissionDetailResponse> findDetailListByUid(Integer uid, PageParamRequest pageParamRequest) {
        Page<UserBrokerageRecord> recordPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        QueryWrapper<UserBrokerageRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.in("status", BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE
                , BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_WITHDRAW);
        queryWrapper.groupBy("left(update_time, 7)");
        queryWrapper.orderByDesc("left(update_time, 7)");
        List<UserBrokerageRecord> list = dao.selectList(queryWrapper);
        if (CollUtil.isEmpty(list)) {
            return new PageInfo<>();
        }

        List<SpreadCommissionDetailResponse> responseList = CollUtil.newArrayList();
        for (UserBrokerageRecord record : list) {
            String month = DateUtil.dateToStr(record.getUpdateTime(), Constants.DATE_FORMAT_MONTH);
            responseList.add(new SpreadCommissionDetailResponse(month, getListByUidAndMonth(uid, month)));
        }
        return CommonPage.copyPageInfo(recordPage, responseList);
    }

    /**
     * 获取累计推广条数
     * @param uid 用户uid
     * @return Integer
     */
    @Override
    public Integer getSpreadCountByUid(Integer uid) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserBrokerageRecord::getId);
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        return dao.selectCount(lqw);
    }

    /**
     * 获取推广记录列表
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @return
     */
    @Override
    public List<UserBrokerageRecord> findSpreadListByUid(Integer uid, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        lqw.orderByDesc(UserBrokerageRecord::getUpdateTime);
        return dao.selectList(lqw);
    }

    /**
     * 获取月份对应的推广订单数
     * @param uid 用户uid
     * @param monthList 月份列表
     * @return Map
     */
    @Override
    public Map<String, Integer> getSpreadCountByUidAndMonth(Integer uid, List<String> monthList) {
        QueryWrapper<UserBrokerageRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("count(id) as uid, update_time");
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("link_type", BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        queryWrapper.eq("status", BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        queryWrapper.apply(StrUtil.format("left(update_time, 7) in ({})", StrUtil.join(",", monthList)));
        queryWrapper.groupBy("left(update_time, 7)");
        List<UserBrokerageRecord> list = dao.selectList(queryWrapper);
        Map<String, Integer> map = CollUtil.newHashMap();
        if (CollUtil.isEmpty(list)) {
            return map;
        }
        list.forEach(record -> {
            map.put(DateUtil.dateToStr(record.getUpdateTime(), Constants.DATE_FORMAT_MONTH), record.getUid());
        });
        return map;
    }

    /**
     * 获取佣金排行榜（周、月）
     * @param type week、month
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public List<UserBrokerageRecord> getBrokerageTopByDate(String type, PageParamRequest pageParamRequest) {
        QueryWrapper<UserBrokerageRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("uid", "sum(price)AS price");
        queryWrapper.eq("link_type", BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        queryWrapper.eq("status", BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        dateLimitUtilVo dateLimit = DateUtil.getDateLimit(type);
        if(!StringUtils.isBlank(dateLimit.getStartTime())){
            queryWrapper.between("update_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        queryWrapper.groupBy("uid");
        queryWrapper.orderByDesc("price");
        return dao.selectList(queryWrapper);
    }

    /**
     * 根据Uid获取分佣记录列表
     * @param uid 用户uid
     * @return List
     */
    @Override
    public List<UserBrokerageRecord> getSpreadListByUid(Integer uid) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getType, BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        return dao.selectList(lqw);
    }

    /**
     * 佣金总金额（单位时间）
     * @param dateLimit 时间参数
     * @return BigDecimal
     */
    @Override
    public BigDecimal getTotalSpreadPriceBydateLimit(String dateLimit) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserBrokerageRecord::getPrice);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getType, BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        if (StrUtil.isNotBlank(dateLimit)) {
            dateLimitUtilVo dateLimitVo = DateUtil.getDateLimit(dateLimit);
            lqw.between(UserBrokerageRecord::getUpdateTime, dateLimitVo.getStartTime(), dateLimitVo.getEndTime());
        }
        List<UserBrokerageRecord> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.stream().map(UserBrokerageRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 单位时间消耗的佣金
     * @param dateLimit 时间参数
     * @return
     */
    @Override
    public BigDecimal getSubSpreadPriceByDateLimit(String dateLimit) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserBrokerageRecord::getPrice);
        lqw.eq(UserBrokerageRecord::getType, BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        if (StrUtil.isNotBlank(dateLimit)) {
            dateLimitUtilVo dateLimitVo = DateUtil.getDateLimit(dateLimit);
            lqw.between(UserBrokerageRecord::getUpdateTime, dateLimitVo.getStartTime(), dateLimitVo.getEndTime());
        }
        List<UserBrokerageRecord> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.stream().map(UserBrokerageRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 佣金详情列表
     * @param uid uid
     * @param pageParamRequest 分页参数
     * @return
     */
    @Override
    public PageInfo<UserBrokerageRecord> getFundsMonitorDetail(Integer uid, String dateLimit, PageParamRequest pageParamRequest) {
        Page<UserBrokerageRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserBrokerageRecord::getUid, uid);
//        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.in(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_WITHDRAW);
        if (StrUtil.isNotBlank(dateLimit)) {
            dateLimitUtilVo utilVo = DateUtil.getDateLimit(dateLimit);
            lqw.between(UserBrokerageRecord::getUpdateTime, utilVo.getStartTime(), utilVo.getEndTime());
        }
        lqw.orderByDesc(UserBrokerageRecord::getUpdateTime);
        List<UserBrokerageRecord> recordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, recordList);
    }

    /**
     * 获取冻结期佣金
     * @param uid uid
     * @return BigDecimal
     */
    @Override
    public BigDecimal getFreezePrice(Integer uid) {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserBrokerageRecord::getPrice);
        lqw.eq(UserBrokerageRecord::getUid, uid);
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN);
        List<UserBrokerageRecord> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.stream().map(UserBrokerageRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<UserBrokerageRecord> getListByUidAndMonth(Integer uid, String month) {
        QueryWrapper<UserBrokerageRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.in("status", BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE
                , BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_WITHDRAW);
        queryWrapper.eq("left(update_time, 7)", month);
        queryWrapper.orderByDesc("update_time");
        return dao.selectList(queryWrapper);
    }

    /**
     * 获取需要解冻的记录列表
     * @return 记录列表
     */
    private List<UserBrokerageRecord> findThawList() {
        LambdaQueryWrapper<UserBrokerageRecord> lqw = new LambdaQueryWrapper<>();
        lqw.le(UserBrokerageRecord::getThawTime, System.currentTimeMillis());
        lqw.eq(UserBrokerageRecord::getLinkType, BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserBrokerageRecord::getType, BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
        lqw.eq(UserBrokerageRecord::getStatus, BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_FROZEN);
        return dao.selectList(lqw);
    }
}

