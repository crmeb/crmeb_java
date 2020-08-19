package com.zbkj.crmeb.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.front.request.UserRechargeRequest;
import com.zbkj.crmeb.front.request.UserSpreadPeopleRequest;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.request.RegisterThirdUserRequest;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 用户中心 服务类
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-10
 */
public interface UserCenterService extends IService<User> {
    UserCommissionResponse getCommission();

    PageInfo<UserSpreadCommissionResponse> getSpreadCommissionByType(int type, PageParamRequest pageParamRequest);

    BigDecimal getSpreadCountByType(int type);

    Boolean extractCash(UserExtractRequest request);

    UserExtractCashResponse minExtractCash();

    List<SystemUserLevel> getUserLevelList();

    UserSpreadPeopleResponse getSpreadPeopleList(UserSpreadPeopleRequest request, PageParamRequest pageParamRequest);

    List<UserBill> getUserBillList(String type, PageParamRequest pageParamRequest);

    UserRechargeResponse getRechargeConfig();

    UserBalanceResponse getUserBalance();

    UserSpreadOrderResponse getSpreadOrder(PageParamRequest pageParamRequest);

    UserRechargePaymentResponse recharge(UserRechargeRequest request);

    LoginResponse weChatAuthorizeLogin(String code, Integer spreadUid);

    String getLogo();

    LoginResponse weChatAuthorizeProgramLogin(String code, RegisterThirdUserRequest request);

    List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest);

    List<User> getTopBrokerageListByDate(String type, PageParamRequest pageParamRequest);

    List<UserSpreadBannerResponse> getSpreadBannerList(PageParamRequest pageParamRequest);

    Integer getNumberByTop(String type);

    Boolean transferIn(BigDecimal price);
}
