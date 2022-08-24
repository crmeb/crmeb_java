package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.constants.BrokerageRecordConstants;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.response.UserExtractRecordResponse;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.vo.dateLimitUtilVo;
import com.zbkj.common.model.finance.UserExtract;
import com.zbkj.common.request.UserExtractRequest;
import com.zbkj.common.request.UserExtractSearchRequest;
import com.zbkj.common.response.BalanceResponse;
import com.zbkj.common.response.UserExtractResponse;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBrokerageRecord;
import com.zbkj.service.dao.UserExtractDao;
import com.zbkj.service.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

/**
*  UserExtractServiceImpl 接口实现
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
@Service
public class UserExtractServiceImpl extends ServiceImpl<UserExtractDao, UserExtract> implements UserExtractService {

    @Resource
    private UserExtractDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-11
    * @return List<UserExtract>
    */
    @Override
    public List<UserExtract> getList(UserExtractSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 UserExtract 类的多条件查询
        LambdaQueryWrapper<UserExtract> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isBlank(request.getKeywords())) {
            lambdaQueryWrapper.and(i -> i.
                    or().like(UserExtract::getWechat, request.getKeywords()).   //微信号
                    or().like(UserExtract::getRealName, request.getKeywords()). //名称
                    or().like(UserExtract::getBankCode, request.getKeywords()). //银行卡
                    or().like(UserExtract::getBankAddress, request.getKeywords()). //开户行
                    or().like(UserExtract::getAlipayCode, request.getKeywords()). //支付宝
                    or().like(UserExtract::getFailMsg, request.getKeywords()) //失败原因
            );
        }

        //提现状态
        if (request.getStatus() != null) {
            lambdaQueryWrapper.eq(UserExtract::getStatus, request.getStatus());
        }

        //提现方式
        if (!StringUtils.isBlank(request.getExtractType())) {
            lambdaQueryWrapper.eq(UserExtract::getExtractType, request.getExtractType());
        }

        //时间范围
        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            lambdaQueryWrapper.between(UserExtract::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }

        //按创建时间降序排列
        lambdaQueryWrapper.orderByDesc(UserExtract::getCreateTime, UserExtract::getId);

        List<UserExtract> extractList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(extractList)) {
            return extractList;
        }
        List<Integer> uidList = extractList.stream().map(o -> o.getUid()).distinct().collect(Collectors.toList());
        HashMap<Integer, User> userMap = userService.getMapListInUid(uidList);
        for (UserExtract userExtract : extractList) {
            userExtract.setNickName(Optional.ofNullable(userMap.get(userExtract.getUid()).getNickname()).orElse(""));
        }
        return extractList;
    }

    /**
     * 提现总金额
     * 总佣金 = 已提现佣金 + 未提现佣金
     * 已提现佣金 = 用户成功提现的金额
     * 未提现佣金 = 用户未提现的佣金 = 可提现佣金 + 冻结佣金 = 用户佣金
     * 可提现佣金 = 包括解冻佣金、提现未通过的佣金 = 用户佣金 - 冻结期佣金
     * 待提现佣金 = 待审核状态的佣金
     * 冻结佣金 = 用户在冻结期的佣金，不包括退回佣金
     * 退回佣金 = 因退款导致的冻结佣金退回
     */
    @Override
    public BalanceResponse getBalance(String dateLimit) {
        String startTime = "";
        String endTime = "";
        if (StringUtils.isNotBlank(dateLimit)) {
            dateLimitUtilVo dateRage = DateUtil.getDateLimit(dateLimit);
            startTime = dateRage.getStartTime();
            endTime = dateRage.getEndTime();
        }

        // 已提现
        BigDecimal withdrawn = getWithdrawn(startTime, endTime);
        // 待提现(审核中)
        BigDecimal toBeWithdrawn = getWithdrawning(startTime, endTime);

        // 佣金总金额（单位时间）
        BigDecimal commissionTotal = userBrokerageRecordService.getTotalSpreadPriceBydateLimit(dateLimit);
        // 单位时间消耗的佣金
        BigDecimal subWithdarw = userBrokerageRecordService.getSubSpreadPriceByDateLimit(dateLimit);
        // 未提现
        BigDecimal unDrawn = commissionTotal.subtract(subWithdarw);
        return new BalanceResponse(withdrawn, unDrawn, commissionTotal, toBeWithdrawn);
    }


    /**
     * 提现总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return BalanceResponse
     */
    @Override
    public BigDecimal getWithdrawn(String startTime, String endTime) {
        return getSum(null, 1, startTime, endTime);
    }

    /**
     * 审核中总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return BalanceResponse
     */
    private BigDecimal getWithdrawning(String startTime, String endTime) {
        return getSum(null, 0, startTime, endTime);
    }

    /**
     * 根据状态获取总额
     * @return BigDecimal
     */
    private BigDecimal getSum(Integer userId, int status, String startTime, String endTime) {
        LambdaQueryWrapper<UserExtract> lqw = Wrappers.lambdaQuery();
        if (null != userId) {
            lqw.eq(UserExtract::getUid,userId);
        }
        lqw.eq(UserExtract::getStatus,status);
        if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
            lqw.between(UserExtract::getCreateTime, startTime, endTime);
        }
        List<UserExtract> userExtracts = dao.selectList(lqw);
        BigDecimal sum = ZERO;
        if (CollUtil.isNotEmpty(userExtracts)) {
            sum = userExtracts.stream().map(UserExtract::getExtractPrice).reduce(ZERO, BigDecimal::add);
        }
        return sum;
    }

    /**
     * 获取用户对应的提现数据
     * @param userId 用户id
     * @return 提现数据
     */
    @Override
    public UserExtractResponse getUserExtractByUserId(Integer userId) {
        QueryWrapper<UserExtract> qw = new QueryWrapper<>();
        qw.select("SUM(extract_price) as extract_price,count(id) as id, uid");
        qw.ge("status", 1);
        qw.eq("uid",userId);
        qw.groupBy("uid");
        UserExtract ux = dao.selectOne(qw);
        UserExtractResponse uexr = new UserExtractResponse();
//        uexr.setEuid(ux.getUid());
        if (null != ux) {
            uexr.setExtractCountNum(ux.getId()); // 这里的id其实是数量，借变量传递
            uexr.setExtractCountPrice(ux.getExtractPrice());
        }else{
            uexr.setExtractCountNum(0); // 这里的id其实是数量，借变量传递
            uexr.setExtractCountPrice(ZERO);
        }

        return uexr;
    }

    /**
     * 提现审核
     *
     * @param id          提现申请id
     * @param status      审核状态 -1 未通过 0 审核中 1 已提现
     * @param backMessage 驳回原因
     * @return 审核结果
     */
    @Override
    public Boolean updateStatus(Integer id, Integer status, String backMessage) {
        if (status == -1 && StringUtils.isBlank(backMessage))
            throw new CrmebException("驳回时请填写驳回原因");

        UserExtract userExtract = getById(id);
        if (ObjectUtil.isNull(userExtract)) {
            throw new CrmebException("提现申请记录不存在");
        }
        if (userExtract.getStatus() != 0) {
            throw new CrmebException("提现申请已处理过");
        }
        userExtract.setStatus(status);

        User user = userService.getById(userExtract.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("提现用户数据异常");
        }

        Boolean execute = false;

        userExtract.setUpdateTime(cn.hutool.core.date.DateUtil.date());
        // 拒绝
        if (status == -1) {//未通过时恢复用户总金额
            userExtract.setFailMsg(backMessage);
            // 添加提现申请拒绝佣金记录
            UserBrokerageRecord brokerageRecord = new UserBrokerageRecord();
            brokerageRecord.setUid(user.getUid());
            brokerageRecord.setLinkId(userExtract.getId().toString());
            brokerageRecord.setLinkType(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_WITHDRAW);
            brokerageRecord.setType(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
            brokerageRecord.setTitle(BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_WITHDRAW_FAIL);
            brokerageRecord.setPrice(userExtract.getExtractPrice());
            brokerageRecord.setBalance(user.getBrokeragePrice().add(userExtract.getExtractPrice()));
            brokerageRecord.setMark(StrUtil.format("提现申请拒绝返还佣金{}", userExtract.getExtractPrice()));
            brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
            brokerageRecord.setCreateTime(DateUtil.nowDateTime());

            execute = transactionTemplate.execute(e -> {
                // 返还佣金
                userService.operationBrokerage(userExtract.getUid(), userExtract.getExtractPrice(), user.getBrokeragePrice(), "add");
                updateById(userExtract);
                userBrokerageRecordService.save(brokerageRecord);
                return Boolean.TRUE;
            });
        }

        // 同意
        if (status == 1) {
            // 获取佣金提现记录
            UserBrokerageRecord brokerageRecord = userBrokerageRecordService.getByLinkIdAndLinkType(userExtract.getId().toString(), BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_WITHDRAW);
            if (ObjectUtil.isNull(brokerageRecord)) {
                throw new CrmebException("对应的佣金记录不存在");
            }
            execute = transactionTemplate.execute(e -> {
                updateById(userExtract);
                brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
                userBrokerageRecordService.updateById(brokerageRecord);
                return Boolean.TRUE;
            });
        }
        return execute;
    }

    /**
     * 获取提现记录列表
     * @param userId 用户uid
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<UserExtractRecordResponse> getExtractRecord(Integer userId, PageParamRequest pageParamRequest) {
        Page<UserExtract> userExtractPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        QueryWrapper<UserExtract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", userId);

        queryWrapper.groupBy("left(create_time, 7)");
        queryWrapper.orderByDesc("left(create_time, 7)");
        List<UserExtract> list = dao.selectList(queryWrapper);
        if (CollUtil.isEmpty(list)) {
            return new PageInfo<>();
        }
        ArrayList<UserExtractRecordResponse> userExtractRecordResponseList = CollectionUtil.newArrayList();
        for (UserExtract userExtract : list) {
            String date = DateUtil.dateToStr(userExtract.getCreateTime(), Constants.DATE_FORMAT_MONTH);
            userExtractRecordResponseList.add(new UserExtractRecordResponse(date, getListByMonth(userId, date)));
        }

        return CommonPage.copyPageInfo(userExtractPage, userExtractRecordResponseList);
    }

    private List<UserExtract> getListByMonth(Integer userId, String date) {
        QueryWrapper<UserExtract> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "extract_price", "status", "create_time", "update_time");
        queryWrapper.eq("uid", userId);
        queryWrapper.apply(StrUtil.format(" left(create_time, 7) = '{}'", date));
        queryWrapper.orderByDesc("create_time");
        return dao.selectList(queryWrapper);
    }

    /**
     * 获取用户提现总金额
     * @param userId 用户uid
     * @return BigDecimal
     */
    @Override
    public BigDecimal getExtractTotalMoney(Integer userId) {
        return getSum(userId, 1, null, null);
    }


    /**
     * 提现申请
     * @return Boolean
     */
    @Override
    public Boolean extractApply(UserExtractRequest request) {
        //添加判断，提现金额不能后台配置金额
        String value = systemConfigService.getValueByKeyException(Constants.CONFIG_EXTRACT_MIN_PRICE);
        BigDecimal ten = new BigDecimal(value);
        if (request.getExtractPrice().compareTo(ten) < 0) {
            throw new CrmebException(StrUtil.format("最低提现金额{}元", ten));
        }

        User user = userService.getInfo();
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("提现用户信息异常");
        }
        BigDecimal money = user.getBrokeragePrice();//可提现总金额
        if (money.compareTo(ZERO) < 1) {
            throw new CrmebException("您当前没有金额可以提现");
        }

        if (money.compareTo(request.getExtractPrice()) < 0) {
            throw new CrmebException("你当前最多可提现 " + money + "元");
        }

        UserExtract userExtract = new UserExtract();
        BeanUtils.copyProperties(request, userExtract);
        userExtract.setUid(user.getUid());
        userExtract.setBalance(money.subtract(request.getExtractPrice()));
        //存入银行名称
        if (StrUtil.isNotBlank(userExtract.getQrcodeUrl())) {
            userExtract.setQrcodeUrl(systemAttachmentService.clearPrefix(userExtract.getQrcodeUrl()));
        }

        // 添加佣金记录
        UserBrokerageRecord brokerageRecord = new UserBrokerageRecord();
        brokerageRecord.setUid(user.getUid());
        brokerageRecord.setLinkType(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_WITHDRAW);
        brokerageRecord.setType(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB);
        brokerageRecord.setTitle(BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_WITHDRAW_APPLY);
        brokerageRecord.setPrice(userExtract.getExtractPrice());
        brokerageRecord.setBalance(money.subtract(userExtract.getExtractPrice()));
        brokerageRecord.setMark(StrUtil.format("提现申请扣除佣金{}", userExtract.getExtractPrice()));
        brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_WITHDRAW);
        brokerageRecord.setCreateTime(DateUtil.nowDateTime());

        Boolean execute = transactionTemplate.execute(e -> {
            // 保存提现记录
            save(userExtract);
            // 修改用户佣金
            userService.operationBrokerage(user.getUid(), userExtract.getExtractPrice(), money, "sub");
            // 添加佣金记录
            brokerageRecord.setLinkId(userExtract.getId().toString());
            userBrokerageRecordService.save(brokerageRecord);
            return Boolean.TRUE;
        });
        // 此处可添加提现申请通知

        return execute;
    }

    /**
     * 修改提现申请
     * @param id 申请id
     * @param userExtractRequest 具体参数
     */
    @Override
    public Boolean updateExtract(Integer id, UserExtractRequest userExtractRequest) {
        UserExtract userExtract = new UserExtract();
        BeanUtils.copyProperties(userExtractRequest, userExtract);
        userExtract.setId(id);
        return updateById(userExtract);
    }

    /**
     * 提现申请待审核数量
     * @return Integer
     */
    @Override
    public Integer getNotAuditNum() {
        LambdaQueryWrapper<UserExtract> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserExtract::getStatus, 0);
        return dao.selectCount(lqw);
    }
}

