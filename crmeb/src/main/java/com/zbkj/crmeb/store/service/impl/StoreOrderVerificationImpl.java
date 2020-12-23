package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.store.dao.StoreOrderDao;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreOrderInfo;
import com.zbkj.crmeb.store.request.StoreOrderStaticsticsRequest;
import com.zbkj.crmeb.store.response.StoreOrderVerificationConfirmResponse;
import com.zbkj.crmeb.store.response.StoreStaffDetail;
import com.zbkj.crmeb.store.response.StoreStaffTopDetail;
import com.zbkj.crmeb.store.service.StoreOrderInfoService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.StoreOrderVerification;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.model.SystemStoreStaff;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.system.service.SystemStoreService;
import com.zbkj.crmeb.system.service.SystemStoreStaffService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.wechat.service.impl.WechatSendMessageForMinService;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForVerSuccess;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.xml.ws.ServiceMode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * StoreOrderVerificationImpl 接口实现 核销订单
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
public class StoreOrderVerificationImpl implements StoreOrderVerification {

    @Resource
    private StoreOrderDao dao;

    @Autowired
    private OrderUtils orderUtils;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private WechatSendMessageForMinService wechatSendMessageForMinService;

    @Autowired
    private SystemStoreStaffService systemStoreStaffService;

    /**
     * 获取订单核销数据
     */
    @Override
    public StoreStaffTopDetail getOrderVerificationData() {
        StoreStaffTopDetail storeStaffTopDetail = new StoreStaffTopDetail();
        //订单支付没有退款 数量-
        LambdaQueryWrapper<StoreOrder> lqwOrderCount = Wrappers.lambdaQuery();
        lqwOrderCount.eq(StoreOrder::getIsDel,false).eq(StoreOrder::getPaid,true).eq(StoreOrder::getRefundStatus,0);
        storeStaffTopDetail.setOrderCount(dao.selectCount(lqwOrderCount));
        //订单支付没有退款 支付总金额
        LambdaQueryWrapper<StoreOrder> lqwSumPrice = Wrappers.lambdaQuery();
        lqwSumPrice.eq(StoreOrder::getIsDel,false).eq(StoreOrder::getPaid,true).eq(StoreOrder::getRefundStatus,0);
        List<StoreOrder> storeOrdersSumPrice = dao.selectList(lqwSumPrice);
        Double sumPrice = storeOrdersSumPrice.stream().mapToDouble(e->e.getPayPrice().doubleValue()).sum();
        storeStaffTopDetail.setSumPrice(BigDecimal.valueOf(sumPrice).setScale(2,BigDecimal.ROUND_HALF_UP));
        //订单待支付 数量
        LambdaQueryWrapper<StoreOrder> lqwUnPaidCount = Wrappers.lambdaQuery();
        orderUtils.statusApiByWhere(lqwUnPaidCount,0);
        storeStaffTopDetail.setUnpaidCount(dao.selectCount(lqwUnPaidCount));
        //订单待发货 数量
        LambdaQueryWrapper<StoreOrder> lqwUnShippedCount = Wrappers.lambdaQuery();
        orderUtils.statusApiByWhere(lqwUnShippedCount,1);
        storeStaffTopDetail.setUnshippedCount(dao.selectCount(lqwUnShippedCount));
        //订单待收货 数量
        LambdaQueryWrapper<StoreOrder> lqwReceivedCount = Wrappers.lambdaQuery();
        orderUtils.statusApiByWhere(lqwReceivedCount,2);
        storeStaffTopDetail.setReceivedCount(dao.selectCount(lqwReceivedCount));
        //订单待评价 数量 verificationCount
//        LambdaQueryWrapper<StoreOrder> lqwEvaluatedCount = Wrappers.lambdaQuery();
//        orderUtils.statusApiByWhere(lqwEvaluatedCount,3);
//        storeStaffTopDetail.setEvaluatedCount(dao.selectCount(lqwEvaluatedCount));
        // 订单待核销数量
        LambdaQueryWrapper<StoreOrder> verificationCount = Wrappers.lambdaQuery();
        orderUtils.statusApiByWhere(verificationCount,3);
        storeStaffTopDetail.setVerificationCount(dao.selectCount(verificationCount));
        //订单已完成 数量
        LambdaQueryWrapper<StoreOrder> lqwCompleteCount = Wrappers.lambdaQuery();
        orderUtils.statusApiByWhere(lqwCompleteCount,4);
        storeStaffTopDetail.setCompleteCount(dao.selectCount(lqwCompleteCount));
        //订单退款 数量
        LambdaQueryWrapper<StoreOrder> lqwRefundCount = Wrappers.lambdaQuery();
        orderUtils.statusApiByWhere(lqwRefundCount,-3);
        storeStaffTopDetail.setRefundCount(dao.selectCount(lqwRefundCount));

        // 获取今日，昨日，本月，订单金额
        String dayStart = DateUtil.nowDateTime(Constants.DATE_FORMAT_START);
        String dayEnd = DateUtil.nowDateTime(Constants.DATE_FORMAT_END);
        String yesterdayStart = DateUtil.addDay(dayStart,-1,Constants.DATE_FORMAT_START);
        String yesterdayEnd = DateUtil.addDay(dayEnd,-1,Constants.DATE_FORMAT_END);
        String monthStart = DateUtil.nowDateTime(Constants.DATE_FORMAT_MONTH_START);
        String monthEnd = DateUtil.getMonthEndDay();

        // 今日订单数量
        LambdaQueryWrapper<StoreOrder> lqwTodayCount = Wrappers.lambdaQuery();
        lqwTodayCount.eq(StoreOrder::getIsDel,false).between(StoreOrder::getPayTime,dayStart,dayEnd)
                .eq(StoreOrder::getPaid,1).eq(StoreOrder::getRefundStatus,0);
        List<StoreOrder> storeOrdersTodayCount = dao.selectList(lqwTodayCount);
        if(null == storeOrdersTodayCount) storeOrdersTodayCount = new ArrayList<>();
        storeStaffTopDetail.setTodayCount(storeOrdersTodayCount.size());

        // 今日成交额
        double todayPrice = storeOrdersTodayCount.stream().mapToDouble(e->e.getPayPrice().doubleValue()).sum();
        storeStaffTopDetail.setTodayPrice(BigDecimal.valueOf(todayPrice).setScale(2,BigDecimal.ROUND_HALF_UP));

        // 昨日订单数
        LambdaQueryWrapper<StoreOrder> lqwPro = Wrappers.lambdaQuery();
        lqwPro.eq(StoreOrder::getIsDel,false).between(StoreOrder::getCreateTime,yesterdayStart,yesterdayEnd)
                .eq(StoreOrder::getPaid, true).eq(StoreOrder::getRefundStatus,0);
        List<StoreOrder> storeOrdersPro = dao.selectList(lqwPro);
        if(null == storeOrdersPro) storeOrdersPro = new ArrayList<>();
        storeStaffTopDetail.setProCount(storeOrdersPro.size());

        //  昨日成交额
        double proPrice = storeOrdersPro.stream().mapToDouble(e->e.getPayPrice().doubleValue()).sum();
        storeStaffTopDetail.setProPrice(BigDecimal.valueOf(proPrice).setScale(2,BigDecimal.ROUND_HALF_UP));

        // 本月成交订单数量
        LambdaQueryWrapper<StoreOrder> lqwMonth = Wrappers.lambdaQuery();
        lqwMonth.eq(StoreOrder::getIsDel,false).between(StoreOrder::getPayTime,monthStart, monthEnd)
                .eq(StoreOrder::getPaid,true).eq(StoreOrder::getRefundStatus,0);
        List<StoreOrder> storeOrdersMonth = dao.selectList(lqwMonth);
        if(null == storeOrdersMonth) storeOrdersMonth = new ArrayList<>();
        storeStaffTopDetail.setMonthCount(storeOrdersMonth.size());

        // 本月成交额
        double monthTotalPrice = storeOrdersMonth.stream().mapToDouble(e -> e.getPayPrice().doubleValue()).sum();
        storeStaffTopDetail.setMonthPrice(BigDecimal.valueOf(monthTotalPrice).setScale(2,BigDecimal.ROUND_HALF_UP));

        return storeStaffTopDetail;
    }

    /**
     * 核销月详情
     * @return 月详情
     */
    @Override
    public List<StoreStaffDetail> getOrderVerificationDetail(StoreOrderStaticsticsRequest request) {
        request.setPage((request.getPage() - 1) * request.getLimit());
        dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
        request.setStartTime(dateLimit.getStartTime());
        request.setEndTime(dateLimit.getEndTime());
        return dao.getOrderVerificationDetail(request);
    }


    /**
     * 根据核销码核销订单
     *
     * @param vCode 核销码
     * @return 核销结果
     */
    @Override
    public boolean verificationOrderByCode(String vCode) {
        StoreOrderVerificationConfirmResponse existOrder = getVerificationOrderByCode(vCode);
        if(existOrder.getCombinationId() >0 && existOrder.getPinkId() > 0){
            // todo 营销业务待处理
        }
        // 判断当前用户是否有权限核销
        SystemAdmin currentAdmin = systemAdminService.getInfo();
//        List<Integer> currentIds = new ArrayList<>();
//        currentIds.add(currentAdmin.getId());
//        List<SystemStoreStaff> currentStaffs = systemStoreStaffService.getByAdminUserIds(currentIds);
//        if(currentStaffs.size() == 0) throw new CrmebException(Constants.RESULT_VERIFICATION_NOTAUTH);
        // 添加核销人员后执行核销操作
        StoreOrder storeOrder = new StoreOrder();
        BeanUtils.copyProperties(existOrder,storeOrder);
        storeOrder.setStatus(Constants.ORDER_STATUS_INT_BARGAIN);
//        storeOrder.setClerkId(currentStaffs.get(0).getId());
//        storeOrder.setStoreId(currentStaffs.get(0).getStoreId());
        boolean saveStatus = dao.updateById(storeOrder) > 0;

        // 小程序订阅消息发送
        if(saveStatus){
            WechatSendMessageForVerSuccess ver = new WechatSendMessageForVerSuccess(
                    "CRMEB",orderUtils.getStoreNameAndCarNumString(storeOrder.getId()),storeOrder.getOrderId(),
                    DateUtil.nowDateTimeStr(),storeOrder.getPayPrice()+"","暂无","CRMEB"
            );
            wechatSendMessageForMinService.sendVerSuccessMessage(ver,currentAdmin.getId());
        }
        return saveStatus;
    }

    /**
     * 根据核销码查询待核销订单
     *
     * @param vCode 核销码
     * @return 待核销订单详情
     */
    @Override
    public StoreOrderVerificationConfirmResponse getVerificationOrderByCode(String vCode) {
        StoreOrderVerificationConfirmResponse response = new StoreOrderVerificationConfirmResponse();
        StoreOrder storeOrderPram = new StoreOrder().setVerifyCode(vCode).setPaid(true).setRefundStatus(0);
        StoreOrder existOrder = storeOrderService.getByEntityOne(storeOrderPram);
        if(null == existOrder) throw new CrmebException(Constants.RESULT_VERIFICATION_ORDER_NOT_FUND.replace("${vCode}",vCode));
        if(existOrder.getStatus() > 0) throw new CrmebException(Constants.RESULT_VERIFICATION_ORDER_VED.replace("${vCode}",vCode));
        BeanUtils.copyProperties(existOrder, response);
        response.setStoreOrderInfoVos(storeOrderInfoService.getOrderListByOrderId(existOrder.getId()));
        return response;
    }
}
