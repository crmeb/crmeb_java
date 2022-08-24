package com.zbkj.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserExperienceRecord;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户中心 服务类
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
public interface UserCenterService extends IService<User> {

    /**
     * 推广数据接口(昨天的佣金 累计提现金额 当前佣金)
     * @return UserCommissionResponse
     */
    UserCommissionResponse getCommission();

    /**
     * 推广佣金/提现总和
     * @param type 类型 佣金类型3=佣金,4=提现
     * @return BigDecimal
     */
    BigDecimal getSpreadCountByType(Integer type);

    /**
     * 提现申请
     * @param request 申请参数
     * @return Boolean
     */
    Boolean extractCash(UserExtractRequest request);

    /**
     * 获取提现银行列表
     * @return List<String>
     */
    List<String> getExtractBank();

    /**
     * 会员等级列表
     */
    List<SystemUserLevel> getUserLevelList();

    /**
     * 获取推广人列表
     * @param request 查询参数
     * @param pageParamRequest 分页
     * @return List<UserSpreadPeopleItemResponse>
     */
    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(UserSpreadPeopleRequest request, PageParamRequest pageParamRequest);

    /**
     * 充值额度选择
     * @return UserRechargeResponse
     */
    UserRechargeFrontResponse getRechargeConfig();

    /**
     * 用户资金统计
     * @return UserBalanceResponse
     */
    UserBalanceResponse getUserBalance();

    /**
     * 推广订单
     * @param pageParamRequest 分页参数
     * @return UserSpreadOrderResponse
     */
    UserSpreadOrderResponse getSpreadOrder(PageParamRequest pageParamRequest);

    /**
     * 充值
     * @return UserSpreadOrderResponse;
     */
    OrderPayResultResponse recharge(UserRechargeRequest request);

    /**
     * 通过微信code登录
     */
    LoginResponse weChatAuthorizeLogin(String code, Integer spreadUid);

    /**
     * 小程序获取授权logo
     * @return String
     */
    String getLogo();

    /**
     * 微信登录小程序授权登录
     * @param code code
     * @param request 用户参数
     * @return LoginResponse
     */
    LoginResponse weChatAuthorizeProgramLogin(String code, RegisterThirdUserRequest request);

    /**
     * 推广人排行榜
     * @param type  String 时间范围(week-周，month-月)
     * @param pageParamRequest PageParamRequest 分页
     * @return List<LoginResponse>
     */
    List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest);

    /**
     * 佣金排行榜
     * @param type  String 时间范围
     * @param pageParamRequest PageParamRequest 分页
     * @return List<User>
     */
    List<User> getTopBrokerageListByDate(String type, PageParamRequest pageParamRequest);

    /**
     * 海报背景图
     * @return List
     */
    List<UserSpreadBannerResponse> getSpreadBannerList();

    /**
     * 当前用户在佣金排行第几名
     * @param type  String 时间范围
     * @return 排名
     */
    Integer getNumberByTop(String type);

    /**
     * 佣金转入余额
     * @param price 金额
     * @return Boolean
     */
    Boolean transferIn(BigDecimal price);

    /**
     * 提现记录
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
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
     * @return List<UserExperienceRecord>
     */
    List<UserExperienceRecord> getUserExperienceList(PageParamRequest pageParamRequest);

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
