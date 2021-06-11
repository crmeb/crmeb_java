package com.zbkj.crmeb.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.IntegralRecordConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.user.dao.UserIntegralRecordDao;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserIntegralRecord;
import com.zbkj.crmeb.user.request.AdminIntegralSearchRequest;
import com.zbkj.crmeb.user.response.UserIntegralRecordResponse;
import com.zbkj.crmeb.user.service.UserIntegralRecordService;
import com.zbkj.crmeb.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户积分记录Service实现类
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
public class UserIntegralRecordServiceImpl extends ServiceImpl<UserIntegralRecordDao, UserIntegralRecord> implements UserIntegralRecordService {

    private static final Logger logger = LoggerFactory.getLogger(UserIntegralRecordServiceImpl.class);

    @Resource
    private UserIntegralRecordDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 根据订单编号、uid获取记录列表
     * @param orderNo 订单编号
     * @param uid 用户uid
     * @return 记录列表
     */
    @Override
    public List<UserIntegralRecord> findListByOrderIdAndUid(String orderNo, Integer uid) {
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserIntegralRecord::getUid, uid);
        lqw.eq(UserIntegralRecord::getLinkId, orderNo);
        lqw.in(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_CREATE, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        List<UserIntegralRecord> recordList = dao.selectList(lqw);
        if (CollUtil.isEmpty(recordList)) {
            return recordList;
        }
        for (int i = 0; i < recordList.size();) {
            UserIntegralRecord record = recordList.get(i);
            if (record.getType().equals(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD)) {
                if (record.getStatus().equals(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE)) {
                    recordList.remove(i);
                    continue;
                }
            }
            i++;
        }
        return recordList;
    }

    /**
     * 积分解冻
     */
    @Override
    public void integralThaw() {
        // 查询需要解冻的积分
        List<UserIntegralRecord> thawList = findThawList();
        if (CollUtil.isEmpty(thawList)) {
            return;
        }
        for (UserIntegralRecord record : thawList) {
            // 查询对应的用户
            User user = userService.getById(record.getUid());
            if (ObjectUtil.isNull(user)) {
                continue ;
            }
            record.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            // 计算积分余额
            Integer balance = user.getIntegral() + record.getIntegral();
            record.setBalance(balance);

            // 解冻
            Boolean execute = transactionTemplate.execute(e -> {
                updateById(record);
                userService.operationIntegral(record.getUid(), record.getIntegral(), user.getIntegral(), "add");
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("积分解冻处理—解冻出错，记录id = {}", record.getId()));
            }
        }
    }

    /**
     * PC后台列表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    @Override
    public PageInfo<UserIntegralRecordResponse> findAdminList(AdminIntegralSearchRequest request, PageParamRequest pageParamRequest) {
        Page<UserIntegralRecordResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(UserIntegralRecord::getId, UserIntegralRecord::getTitle, UserIntegralRecord::getBalance, UserIntegralRecord::getIntegral,
                UserIntegralRecord::getMark, UserIntegralRecord::getUid, UserIntegralRecord::getUpdateTime);
        lqw.eq(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        if (ObjectUtil.isNotNull(request.getUid())) {
            lqw.eq(UserIntegralRecord::getUid, request.getUid());
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            List<Integer> idList = userService.findIdListLikeName(request.getKeywords());
            if (CollUtil.isNotEmpty(idList)) {
                lqw.in(UserIntegralRecord::getUid, idList);
            }
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = DateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), Constants.DATE_FORMAT);
            if(compareDateResult == -1){
                throw new CrmebException("开始时间不能大于结束时间！");
            }

            lqw.between(UserIntegralRecord::getUpdateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lqw.orderByDesc(UserIntegralRecord::getUpdateTime);
        List<UserIntegralRecord> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<UserIntegralRecordResponse> responseList = list.stream().map(i -> {
            UserIntegralRecordResponse response = new UserIntegralRecordResponse();
            BeanUtils.copyProperties(i, response);
            // 获取用户昵称
            User user = userService.getById(i.getUid());
            response.setNickName(user.getNickname());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 根据类型条件计算积分总数
     * @param uid 用户uid
     * @param type 类型：1-增加，2-扣减
     * @param date 日期
     * @param linkType 关联类型
     * @return 积分总数
     */
    @Override
    public Integer getSumIntegral(Integer uid, Integer type, String date, String linkType) {
        QueryWrapper<UserIntegralRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(integral) as integral");
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("type", type);
        if (StrUtil.isNotBlank(linkType)) {
            queryWrapper.eq("link_type", linkType);
        }
        queryWrapper.eq("status", IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        if (StrUtil.isNotBlank(date)) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            queryWrapper.between("update_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        UserIntegralRecord integralRecord = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(integralRecord) || ObjectUtil.isNull(integralRecord.getIntegral())) {
            return 0;
        }
        return integralRecord.getIntegral();
    }

    /**
     * H5用户积分列表
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    @Override
    public List<UserIntegralRecord> findUserIntegralRecordList(Integer uid, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(UserIntegralRecord::getId, UserIntegralRecord::getTitle, UserIntegralRecord::getType, UserIntegralRecord::getIntegral, UserIntegralRecord::getUpdateTime);
        lqw.eq(UserIntegralRecord::getUid, uid);
        lqw.eq(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
        lqw.orderByDesc(UserIntegralRecord::getUpdateTime);
        return dao.selectList(lqw);
    }

    /**
     * 获取用户冻结的积分
     * @param uid 用户uid
     * @return 积分数量
     */
    @Override
    public Integer getFrozenIntegralByUid(Integer uid) {
        QueryWrapper<UserIntegralRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(integral) as integral");
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("type", IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        queryWrapper.eq("link_type", IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        queryWrapper.eq("status", IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN);
        UserIntegralRecord integralRecord = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(integralRecord) || ObjectUtil.isNull(integralRecord.getIntegral())) {
            return 0;
        }
        return integralRecord.getIntegral();
    }

    /**
     * 获取需要解冻的记录列表
     * @return 记录列表
     */
    private List<UserIntegralRecord> findThawList() {
        LambdaQueryWrapper<UserIntegralRecord> lqw = Wrappers.lambdaQuery();
        lqw.le(UserIntegralRecord::getThawTime, System.currentTimeMillis());
        lqw.eq(UserIntegralRecord::getLinkType, IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        lqw.eq(UserIntegralRecord::getType, IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        lqw.eq(UserIntegralRecord::getStatus, IntegralRecordConstants.INTEGRAL_RECORD_STATUS_FROZEN);
        return dao.selectList(lqw);
    }
}

