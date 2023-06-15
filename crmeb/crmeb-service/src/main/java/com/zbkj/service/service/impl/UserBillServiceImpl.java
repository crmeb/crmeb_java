package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.response.UserBillResponse;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.vo.dateLimitUtilVo;
import com.zbkj.common.request.FundsMonitorRequest;
import com.zbkj.common.request.FundsMonitorSearchRequest;
import com.zbkj.common.response.MonitorResponse;
import com.zbkj.common.request.StoreOrderRefundRequest;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBill;
import com.zbkj.service.dao.UserBillDao;
import com.zbkj.service.service.UserBillService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UserBillServiceImpl 接口实现
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
public class UserBillServiceImpl extends ServiceImpl<UserBillDao, UserBill> implements UserBillService {

    @Resource
    private UserBillDao dao;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return List<UserBill>
    */
    @Override
    public List<UserBill> getList(FundsMonitorSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        QueryWrapper<UserBill> queryWrapper = new QueryWrapper<>();
        getMonthSql(request, queryWrapper);

        //排序
        if (request.getSort() == null) {
            queryWrapper.orderByDesc("create_time");
        }else{
            if ("asc".equals(request.getSort())) {
                queryWrapper.orderByAsc("number");
            }else{
                queryWrapper.orderByDesc("number");
            }
        }

        // 查询类型
        if (StringUtils.isNotBlank(request.getCategory())) {
            queryWrapper.eq("category", request.getCategory());
        }
        if (ObjectUtil.isNotNull(request.getPm())) {
            queryWrapper.eq("pm", request.getPm());
        }

        return dao.selectList(queryWrapper);
    }

    private void getMonthSql(FundsMonitorSearchRequest request, QueryWrapper<UserBill> queryWrapper) {
        queryWrapper.gt("status", 0); // -1无效
        if (!StringUtils.isBlank(request.getKeywords())) {
            queryWrapper.and(i -> i.
                    or().eq("id", request.getKeywords()).   //用户账单id
                    or().eq("uid", request.getKeywords()). //用户uid
                    or().eq("link_id", request.getKeywords()). //关联id
                    or().like("title", request.getKeywords()) //账单标题
            );
        }

        //时间范围
        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = DateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), Constants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException("开始时间不能大于结束时间！");
            }

            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());

            //资金范围
            if (request.getMax() != null && request.getMin() != null) {
                //判断时间
                if (request.getMax().compareTo(request.getMin()) < 0) {
                    throw new CrmebException("最大金额不能小于最小金额！");
                }
                queryWrapper.between("number", request.getMin(), request.getMax());
            }
        }


        //关联id
        if (StringUtils.isNotBlank(request.getLinkId())) {
            if ("gt".equals(request.getLinkId())) {
                queryWrapper.ne("link_id", 0);
            }else{
                queryWrapper.eq("link_id", request.getLinkId());
            }
        }

        //用户id集合
        if (null != request.getUserIdList() && request.getUserIdList().size() > 0) {
            queryWrapper.in("uid", request.getUserIdList());
        } else if (ObjectUtil.isNotNull(request.getUid())) {
            queryWrapper.eq("uid", request.getUid());
        }



        if (StringUtils.isNotBlank(request.getCategory())) {
            queryWrapper.eq("category", request.getCategory());
        }

        if (StringUtils.isNotBlank(request.getType())) {
            queryWrapper.eq("type", request.getType());
        }
    }

    /**
     * 新增/消耗  总金额
     * @param pm Integer 0 = 支出 1 = 获得
     * @param userId Integer 用户uid
     * @param category String 类型
     * @param date String 时间范围
     * @param type String 小类型
     * @return UserBill
     */
    @Override
    public BigDecimal getSumBigDecimal(Integer pm, Integer userId, String category, String date, String type) {
        QueryWrapper<UserBill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", category).
                eq("status", 1);
        if (ObjectUtil.isNotNull(userId)) {
            queryWrapper.eq("uid", userId);
        }
        if (null != pm) {
            queryWrapper.eq("pm", pm);
        }
        if (null != type) {
            queryWrapper.eq("type", type);
        }
        if (null != date) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        List<UserBill> userBills = dao.selectList(queryWrapper);
        if (CollUtil.isEmpty(userBills)) {
            return BigDecimal.ZERO;
        }
        return userBills.stream().map(UserBill::getNumber).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, BigDecimal.ROUND_DOWN);
    }

    /**
     * 保存退款日志
     * @return boolean
     */
    @Override
    public Boolean saveRefundBill(StoreOrderRefundRequest request, User user) {
        UserBill userBill = new UserBill();
        userBill.setTitle("商品退款");
        userBill.setUid(user.getUid());
        userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
        userBill.setType(Constants.USER_BILL_TYPE_PAY_PRODUCT_REFUND);
        userBill.setNumber(request.getAmount());
        userBill.setLinkId(request.getOrderId().toString());
        userBill.setBalance(user.getNowMoney().add(request.getAmount()));
        userBill.setMark("订单退款到余额" + request.getAmount() + "元");
        userBill.setPm(1);
        return save(userBill);
    }

    /**
     * 资金监控
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MonitorResponse> fundMonitoring(FundsMonitorRequest request, PageParamRequest pageParamRequest) {
        Page<UserBill> billPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Map<String, Object> map = new HashMap<>();
        if (StrUtil.isNotBlank(request.getKeywords())) {
            map.put("keywords", StrUtil.format("%{}%", request.getKeywords()));
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            map.put("startTime", dateLimit.getStartTime());
            map.put("endTime", dateLimit.getEndTime());
        }
        // 明细类型筛选
        if (StrUtil.isNotBlank(request.getTitle())) {
            switch (request.getTitle()) {
                case "recharge" :
                    map.put("title", "充值支付");
                    break;
                case "admin" :
                    map.put("title", "后台操作");
                    break;
                case "productRefund" :
                    map.put("title", "商品退款");
                    break;
                case "payProduct" :
                    map.put("title", "购买商品");
                    break;
            }

        }
        List<UserBillResponse> userBillResponses = dao.fundMonitoring(map);
        if (CollUtil.isEmpty(userBillResponses)) {
            return CommonPage.copyPageInfo(billPage, CollUtil.newArrayList());
        }
        List<MonitorResponse> responseList = userBillResponses.stream().map(e -> {
            MonitorResponse monitorResponse = new MonitorResponse();
            BeanUtils.copyProperties(e, monitorResponse);
            return monitorResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(billPage, responseList);
    }

    /**
     * 用户账单记录（现金）
     * @param uid 用户uid
     * @param type 记录类型：all-全部，expenditure-支出，income-收入
     * @return PageInfo
     */
    @Override
    public PageInfo<UserBill> nowMoneyBillRecord(Integer uid, String type, PageParamRequest pageRequest) {
        Page<UserBill> billPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<UserBill> lqw = Wrappers.lambdaQuery();
        lqw.select(UserBill::getTitle, UserBill::getNumber, UserBill::getBalance, UserBill::getMark, UserBill::getCreateTime, UserBill::getPm);
        lqw.eq(UserBill::getUid, uid);
        lqw.eq(UserBill::getCategory, Constants.USER_BILL_CATEGORY_MONEY);
        switch (type) {
            case "all":
                break;
            case "expenditure":
                lqw.eq(UserBill::getPm, 0);
                break;
            case "income":
                lqw.eq(UserBill::getPm, 1);
                break;
        }
        lqw.eq(UserBill::getStatus, 1);
        lqw.orderByDesc(UserBill::getId);
        List<UserBill> billList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(billPage, billList);
    }

}

