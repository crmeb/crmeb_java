package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.request.FundsMonitorSearchRequest;
import com.zbkj.crmeb.front.response.UserSpreadCommissionResponse;
import com.zbkj.crmeb.store.request.StoreOrderRefundRequest;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.response.UserBillResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
* @author Mr.Zhang
* @Description UserBillService 接口
* @since 2020-04-28
* @LastEdit stivepeim 2020-6-22
*/
public interface UserBillService extends IService<UserBill> {

    List<UserBill> getList(FundsMonitorSearchRequest request, PageParamRequest pageParamRequest);

    Integer getSumInteger(Integer pm, Integer userId, String category, String date, String type);

    BigDecimal getSumBigDecimal(Integer pm, Integer userId, String category, String date, String type);

    PageInfo<UserSpreadCommissionResponse> getListGroupByMonth(Integer userId, Integer type, PageParamRequest pageParamRequest, String category);

    boolean saveRefundBill(StoreOrderRefundRequest request, User user);

    void saveRefundBrokeragePriceBill(StoreOrderRefundRequest request, User user);

    void saveRefundIntegralBill(StoreOrderRefundRequest request, User user);

    BigDecimal getDataByUserId(Integer userId);

    BigDecimal getIntegerByOrder(Integer id, Integer userId, int pm);

    PageInfo<UserBillResponse> getListAdmin(FundsMonitorSearchRequest request, PageParamRequest pageParamRequest);

    Map<String, Integer> getCountListByMonth(FundsMonitorSearchRequest request, List<String> monthList);

    List<UserBill> getTopBrokerageListByDate(String type, PageParamRequest pageParamRequest);
}