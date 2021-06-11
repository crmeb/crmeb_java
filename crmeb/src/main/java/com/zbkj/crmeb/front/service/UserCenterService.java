package com.zbkj.crmeb.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.front.request.RegisterAppWxRequest;
import com.zbkj.crmeb.front.request.UserRechargeRequest;
import com.zbkj.crmeb.front.request.UserSpreadPeopleRequest;
import com.zbkj.crmeb.front.request.WxBindingPhoneRequest;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.model.UserIntegralRecord;
import com.zbkj.crmeb.user.request.RegisterThirdUserRequest;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户中心 服务类
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public interface UserCenterService extends IService<User> {

    UserCommissionResponse getCommission();

    BigDecimal getSpreadCountByType(int type);

    Boolean extractCash(UserExtractRequest request);

    /**
     * 获取提现银行列表
     * @return List<String>
     */
    List<String> getExtractBank();

    List<SystemUserLevel> getUserLevelList();

    /**
     * 获取推广人列表
     * @param request 查询参数
     * @param pageParamRequest 分页
     * @return List<UserSpreadPeopleItemResponse>
     */
    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(UserSpreadPeopleRequest request, PageParamRequest pageParamRequest);

    UserRechargeResponse getRechargeConfig();

    UserBalanceResponse getUserBalance();

    UserSpreadOrderResponse getSpreadOrder(PageParamRequest pageParamRequest);

    OrderPayResultResponse recharge(UserRechargeRequest request);

    LoginResponse weChatAuthorizeLogin(String code, Integer spreadUid);

    String getLogo();

    LoginResponse weChatAuthorizeProgramLogin(String code, RegisterThirdUserRequest request);

    List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest);

    List<User> getTopBrokerageListByDate(String type, PageParamRequest pageParamRequest);

    List<UserSpreadBannerResponse> getSpreadBannerList(PageParamRequest pageParamRequest);

    Integer getNumberByTop(String type);

    Boolean transferIn(BigDecimal price);

    PageInfo<UserExtractRecordResponse> getExtractRecord(PageParamRequest pageParamRequest);

    /**
     * 推广佣金明细
     * @param pageParamRequest 分页参数
     */
    PageInfo<SpreadCommissionDetailResponse> getSpreadCommissionDetail(PageParamRequest pageParamRequest);

    /**
     * 用户账单记录（现金）
     * @param type 记录类型：all-全部，expenditure-支出，income-收入
     * @return CommonPage
     */
    CommonPage<UserRechargeBillRecordResponse> nowMoneyBillRecord(String type, PageParamRequest pageRequest);

    /**
     * 微信注册绑定手机号
     * @param request 请求参数
     * @return 登录信息
     */
    LoginResponse registerBindingPhone(WxBindingPhoneRequest request);

    /**
     * 用户积分记录列表
     * @param pageParamRequest 分页参数
     * @return List<UserIntegralRecord>
     */
    List<UserIntegralRecord> getUserIntegralRecordList(PageParamRequest pageParamRequest);

    /**
     * 获取用户积分信息
     * @return IntegralUserResponse
     */
    IntegralUserResponse getIntegralUser();

    /**
     * 获取用户经验记录
     * @param pageParamRequest 分页参数
     * @return List<UserBill>
     */
    List<UserBill> getUserExperienceList(PageParamRequest pageParamRequest);

    /**
     * 提现用户信息
     * @return UserExtractCashResponse
     */
    UserExtractCashResponse getExtractUser();

    /**
     * 推广人列表统计
     * @return UserSpreadPeopleResponse
     */
    UserSpreadPeopleResponse getSpreadPeopleCount();
}
