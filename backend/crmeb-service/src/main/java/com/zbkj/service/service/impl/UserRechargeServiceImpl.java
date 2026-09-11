package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.PayConstants;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.model.finance.UserRecharge;
import com.zbkj.common.model.user.UserBill;
import com.zbkj.common.request.UserRechargeRefundRequest;
import com.zbkj.common.request.UserRechargeSearchRequest;
import com.zbkj.common.response.UserRechargeResponse;
import com.zbkj.common.model.user.User;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.UserRechargeDao;
import com.zbkj.service.service.RechargeRefundService;
import com.zbkj.service.service.UserBillService;
import com.zbkj.service.service.UserRechargeService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* UserRechargeServiceImpl 接口实现
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
@Service
public class UserRechargeServiceImpl extends ServiceImpl<UserRechargeDao, UserRecharge> implements UserRechargeService {

    @Resource
    private UserRechargeDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private RechargeRefundService rechargeRefundService;


    /**
    * 列表
    * @param request 请求参数
    * @return List<UserRecharge>
    */
    @Override
    public PageInfo<UserRechargeResponse> getList(UserRechargeSearchRequest request) {
        //Page<UserRecharge> userRechargesList = PageHelper.startPage(request.getPage(), request.getLimit());
        //
        //DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
        ////带 UserExtract 类的多条件查询
        //LambdaQueryWrapper<UserRecharge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //if (ObjectUtil.isNotNull(request.getUid()) && request.getUid() > 0) {
        //    lambdaQueryWrapper.eq(UserRecharge::getUid, request.getUid());
        //}
        //if (StrUtil.isNotBlank(request.getKeywords())) {
        //    lambdaQueryWrapper.like(UserRecharge::getOrderId, request.getKeywords()); //订单号
        //}
        ////是否充值
        //lambdaQueryWrapper.eq(UserRecharge::getPaid, true);
        //
        ////时间范围
        //if (StrUtil.isNotBlank(dateLimit.getStartTime()) && StrUtil.isNotBlank(dateLimit.getEndTime())) {
        //    //判断时间
        //    int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), Constants.DATE_FORMAT);
        //    if(compareDateResult == -1){
        //        throw new CrmebException("开始时间不能大于结束时间！");
        //    }
        //
        //    lambdaQueryWrapper.between(UserRecharge::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        //}
        //lambdaQueryWrapper.orderByDesc(UserRecharge::getId);
        //List<UserRecharge> userRecharges = dao.selectList(lambdaQueryWrapper);
        Map<String, Object> map = CollUtil.newHashMap();
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
                map.put("startTime", dateLimit.getStartTime());
                map.put("endTime", dateLimit.getEndTime());
            }
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String orderNo = URLUtil.decode(request.getKeywords());
            map.put("orderNo", orderNo);
        }

        Page<UserRechargeResponse> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<UserRechargeResponse> userRechargesList = dao.getAdminPage(map);
        return CommonPage.copyPageInfo(page, userRechargesList);
        //if (CollUtil.isEmpty(userRecharges)) {
        //    return CommonPage.copyPageInfo(userRechargesList, CollUtil.newArrayList());
        //}
        //
        //List<Integer> userIds = userRecharges.stream().map(UserRecharge::getUid).collect(Collectors.toList());
        //HashMap<Integer, User> userHashMap = userService.getMapListInUid(userIds);
        //List<UserRechargeResponse> responseList = userRecharges.stream().map(e -> {
        //    User user = userHashMap.get(e.getUid());
        //    UserRechargeResponse r = new UserRechargeResponse();
        //    BeanUtils.copyProperties(e, r);
        //    if (null != user) {
        //        r.setAvatar(user.getAvatar());
        //        r.setNickname(user.getNickname());
        //    }
        //    return r;
        //}).collect(Collectors.toList());
        //return CommonPage.copyPageInfo(userRechargesList, responseList);
    }

    /**
     * 充值总金额
     * @return HashMap<String, BigDecimal>
     */
    @Override
    public HashMap<String, BigDecimal> getBalanceList() {
        HashMap<String, BigDecimal> map = new HashMap<>();

        BigDecimal routine = dao.getSumByType("routine");
        if(null == routine) routine = BigDecimal.ZERO;
        map.put("routine", routine); //小程序充值

//        BigDecimal weChat = dao.getSumByType("weixin");
        BigDecimal weChat = dao.getSumByType("public");
        if(null == weChat) weChat = BigDecimal.ZERO;
        map.put("weChat", weChat); //公众号充值

        BigDecimal total = dao.getSumByType("");
        if(null == total) total = BigDecimal.ZERO;
        map.put("total", total); //总金额

        BigDecimal refund = dao.getSumByRefund();
        if(null == refund) refund = BigDecimal.ZERO;
        map.put("refund", refund);

        map.put("other", total.subtract(routine).subtract(weChat)); //其他金额

        return map;
    }

    /**
     * 后台充值订单退款
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean refund(UserRechargeRefundRequest request) {
        UserRecharge userRecharge = dao.getByIdForUpdate(request.getId());
        if (ObjectUtil.isNull(userRecharge)) {
            throw new CrmebException("充值订单不存在");
        }
        if (!request.getOrderId().equals(userRecharge.getOrderId())) {
            throw new CrmebException("充值订单信息不匹配");
        }
        if (!Boolean.TRUE.equals(userRecharge.getPaid())) {
            throw new CrmebException("充值订单未支付");
        }
        if (ObjectUtil.isNotNull(userRecharge.getRefundPrice())
                && userRecharge.getRefundPrice().compareTo(BigDecimal.ZERO) > 0) {
            throw new CrmebException("充值订单已退款");
        }
        if (ObjectUtil.isNull(userRecharge.getPrice())
                || userRecharge.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CrmebException("充值订单金额异常");
        }
        if ("balance".equals(userRecharge.getRechargeType())) {
            throw new CrmebException("佣金转入余额不能退款");
        }

        BigDecimal givePrice = ObjectUtil.isNull(userRecharge.getGivePrice())
                ? BigDecimal.ZERO : userRecharge.getGivePrice();
        if (givePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new CrmebException("充值赠送金额异常");
        }
        BigDecimal deductionAmount = userRecharge.getPrice();
        if (request.getType().equals(2)) {
            deductionAmount = deductionAmount.add(givePrice);
        }

        User user = userService.getById(userRecharge.getUid());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("充值用户不存在");
        }
        if (ObjectUtil.isNull(user.getNowMoney())) {
            throw new CrmebException("用户余额数据异常");
        }
        if (user.getNowMoney().compareTo(deductionAmount) < 0) {
            throw new CrmebException("用户余额不足，无法完成充值退款");
        }

        boolean moneyUpdated = userService.operationNowMoney(
                user.getUid(), deductionAmount, user.getNowMoney(), "sub");
        if (!moneyUpdated) {
            throw new CrmebException("用户余额已发生变化，请重试");
        }

        UserBill userBill = new UserBill();
        userBill.setUid(user.getUid());
        userBill.setLinkId(userRecharge.getOrderId());
        userBill.setPm(0);
        userBill.setTitle("充值退款");
        userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
        userBill.setType(Constants.USER_BILL_TYPE_USER_RECHARGE_REFUND);
        userBill.setNumber(deductionAmount);
        userBill.setBalance(user.getNowMoney().subtract(deductionAmount));
        userBill.setMark(request.getType().equals(2)
                ? StrUtil.format("充值退款扣回本金{}元及赠送金额{}元", userRecharge.getPrice(), givePrice)
                : StrUtil.format("充值退款扣回本金{}元", userRecharge.getPrice()));
        userBill.setStatus(1);
        userBill.setCreateTime(CrmebDateUtil.nowDateTime());
        if (!userBillService.save(userBill)) {
            throw new CrmebException("充值退款流水保存失败");
        }

        userRecharge.setRefundPrice(userRecharge.getPrice());
        if (!updateById(userRecharge)) {
            throw new CrmebException("充值退款状态更新失败");
        }

        rechargeRefundService.refund(userRecharge);
        return Boolean.TRUE;
    }

    /**
     * 根据对象查询订单
     * @author Mr.Zhang
     * @since 2020-05-11
     * @return UserRecharge
     */
    @Override
    public UserRecharge getInfoByEntity(UserRecharge userRecharge) {
        LambdaQueryWrapper<UserRecharge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.setEntity(userRecharge);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 根据日期获取充值订单数量
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getRechargeOrderNumByDate(String date) {
        QueryWrapper<UserRecharge> wrapper = Wrappers.query();
        wrapper.select("id");
        wrapper.eq("paid", 1);
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(wrapper);
    }

    /**
     * 根据日期获取充值订单金额
     * @param date 日期，yyyy-MM-dd格式
     * @return BigDecimal
     */
    @Override
    public BigDecimal getRechargeOrderAmountByDate(String date) {
        QueryWrapper<UserRecharge> wrapper = Wrappers.query();
        wrapper.select("IFNULL(sum(price), 0) as price");
        wrapper.eq("paid", 1);
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectOne(wrapper).getPrice();
    }

    /**
     * 获取总人数
     * @return Integer
     */
    @Override
    public Integer getTotalPeople() {
        QueryWrapper<UserRecharge> wrapper = Wrappers.query();
        //wrapper.select("id");
        wrapper.select(" ANY_VALUE(id) AS id ");
        wrapper.eq("paid", 1);
        wrapper.groupBy("uid");
        List<UserRecharge> list = dao.selectList(wrapper);
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        return list.size();
    }

    /**
     * 获取总金额
     * @return BigDecimal
     */
    @Override
    public BigDecimal getTotalPrice() {
        QueryWrapper<UserRecharge> wrapper = Wrappers.query();
        wrapper.select("IFNULL(sum(price), 0) as price");
        wrapper.eq("paid", 1);
        return dao.selectOne(wrapper).getPrice();
    }

    /**
     * 根据时间获取充值用户数量
     * @param date 日期
     * @return Integer
     */
    @Override
    public Integer getRechargeUserNumByDate(String date) {
        QueryWrapper<UserRecharge> wrapper = Wrappers.query();
        //wrapper.select("id");
        wrapper.select(" ANY_VALUE(id) AS id ");
        wrapper.eq("paid", 1);
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        wrapper.groupBy("uid");
        List<UserRecharge> list = dao.selectList(wrapper);
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        return list.size();
    }

    /**
     * 根据时间获取充值用户数量
     * @param startDate 日期
     * @param endDate 日期
     * @return Integer
     */
    @Override
    public Integer getRechargeUserNumByPeriod(String startDate, String endDate) {
        QueryWrapper<UserRecharge> wrapper = Wrappers.query();
        //wrapper.select("id");
        wrapper.select(" ANY_VALUE(id) AS id ");
        wrapper.eq("paid", 1);
        wrapper.apply("date_format(create_time, '%Y-%m-%d') between {0} and {1}", startDate, endDate);
        wrapper.groupBy("uid");
        List<UserRecharge> list = dao.selectList(wrapper);
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        return list.size();
    }

    /**
     * 获取待上传微信发货管理订单
     */
    @Override
    public List<UserRecharge> findAwaitUploadWechatList() {
        DateTime date = cn.hutool.core.date.DateUtil.date();
        DateTime offsetMinute = cn.hutool.core.date.DateUtil.offsetMinute(date, -10);
        LambdaQueryWrapper<UserRecharge> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserRecharge::getPaid, 1);
        lqw.eq(UserRecharge::getIsWechatShipping, 0);
        lqw.eq(UserRecharge::getRechargeType, PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM);
        lqw.le(UserRecharge::getPayTime, offsetMinute);
        return dao.selectList(lqw);
    }

    /**
     * 获取订单
     * @param outTradeNo 商户系统内部的订单号
     */
    @Override
    public UserRecharge getByOutTradeNo(String outTradeNo) {
        LambdaQueryWrapper<UserRecharge> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserRecharge::getOutTradeNo, outTradeNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }
}
