package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.front.request.LoginRequest;
import com.zbkj.crmeb.front.request.PasswordRequest;
import com.zbkj.crmeb.front.request.RegisterRequest;
import com.zbkj.crmeb.front.request.UserBindingRequest;
import com.zbkj.crmeb.front.response.LoginResponse;
import com.zbkj.crmeb.front.response.UserCenterResponse;
import com.zbkj.crmeb.front.response.UserCommissionResponse;
import com.zbkj.crmeb.front.response.UserSpreadPeopleItemResponse;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.RetailShopStairUserRequest;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.request.*;
import com.zbkj.crmeb.user.response.TopDetail;
import com.zbkj.crmeb.user.response.UserResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户表 服务类
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
public interface UserService extends IService<User> {
    PageInfo<UserResponse> getList(UserSearchRequest request, PageParamRequest pageParamRequest);

    boolean updateFounds(UserOperateFundsRequest request, boolean isSaveBill);

    boolean updateIntegralMoney(UserOperateIntegralMoneyRequest request);

    /**
     * 用户基本更新
     * @param user 用户参数
     * @return 更新结果
     */
    boolean updateBase(User user);

    boolean userPayCountPlus(User user);

    /**
     * 更新余额
     * @param user 用户
     * @param price 金额
     * @param type 增加add、扣减sub
     * @return 更新后的用户数据
     */
    Boolean updateNowMoney(User user, BigDecimal price, String type);

    /**
     * 会员分组
     * @param id String id
     * @param groupId Integer 分组Id
     */
    boolean group(String id, String groupId);

    /**
     * 清除会员登记
     * @param userId 用户id
     * @return 清除结果
     */
    boolean cleanLevel(Integer userId);

    LoginResponse register(RegisterRequest loginRequest, String ip) throws Exception;

    LoginResponse login(LoginRequest loginRequest) throws Exception;

    boolean password(PasswordRequest request);

    void loginOut(String token);

    User getInfo();

    User getUserPromoter();

    User getInfoException();

    User getInfoEmpty();

    Object getInfoByCondition(Integer userId,Integer type,PageParamRequest pageParamRequest);

    Integer getUserIdException();

    Integer getUserId();

    Integer getAddUserCountByDate(String date);

    Map<Object, Object> getAddUserCountGroupDate(String date);

    boolean bind(UserBindingRequest request);

    UserCenterResponse getUserCenter();

    HashMap<Integer, User> getMapListInUid(List<Integer> uidList);

    HashMap<Integer, User> getMapByList(List<User> list);

    void repeatSignNum(Integer userId);

    UserCommissionResponse getCommission();

    boolean tag(String id, String tagId);

    List<Integer> getSpreadPeopleIdList(List<Integer> userId);

    List<User> getSpreadPeopleList(List<Integer> userIds);

    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(List<Integer> userIdList, String keywords, String sortKey, String isAsc, PageParamRequest pageParamRequest);

    TopDetail getTopDetail(Integer userId);

    User registerByThird(RegisterThirdUserRequest thirdUserRequest, String type);

    String token(User user) throws Exception;

    String getValidateCodeRedisKey(String phone);

    boolean spread(Integer currentUserId, Integer spreadUserId);

    PageInfo<User> getUserListBySpreadLevel(RetailShopStairUserRequest request, PageParamRequest pageParamRequest);

    PageInfo<StoreOrder> getOrderListBySpreadLevel(RetailShopStairUserRequest request, PageParamRequest pageParamRequest);

    boolean clearSpread(Integer userId);

    User updateForPromoter(User current);

    boolean checkIsPromoter(BigDecimal price);

    List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest);

    Integer getCountByPayCount(int minPayCount, int maxPayCount);

    List<User> getUserByEntity(User user);

    void consumeAfterUpdateUserFounds(Integer uid, BigDecimal price, String type);

    void bindSpread(User user, Integer spreadPid);

    boolean upadteBrokeragePrice(User user, BigDecimal newBrokeragePrice);

    BigDecimal getUnCommissionPrice();

    /**
     * 更新推广人
     * @param request
     * @return
     */
    Boolean editSpread(UserUpdateSpreadRequest request);

    /**
     * 更新用户积分
     * @param user 用户
     * @param integral 积分
     * @param type 增加add、扣减sub
     * @return 更新后的用户对象
     */
    Boolean updateIntegral(User user, Integer integral, String type);

    /**
     * 获取分销人员列表
     * @param keywords 搜索参数
     * @param dateLimit 时间参数
     * @param storeBrokerageStatus 分销状态：1-指定分销，2-人人分销
     */
    List<User> findDistributionList(String keywords, String dateLimit, String storeBrokerageStatus);

    /**
     * 获取发展会员人数
     * @param ids       推广人id集合
     * @param dateLimit 时间参数
     * @return
     */
    Integer getDevelopDistributionPeopleNum(List<Integer> ids, String dateLimit);

    User getUserByAccount(String account);

    /**
     * 手机号注册用户
     * @param phone 手机号
     * @param spreadUid 推广人编号
     * @param clientIp ip
     * @return
     */
    User registerPhone(String phone, Integer spreadUid, String clientIp);

    /**
     * 检测能否绑定关系
     * @param user 当前用户
     * @param spreadUid 推广员Uid
     * @param type 用户类型:new-新用户，old—老用户
     * @return Boolean
     */
    Boolean checkBingSpread(User user, Integer spreadUid, String type);

    /**
     * 更新推广员推广数
     * @param uid uid
     * @return Boolean
     */
    Boolean updateSpreadCountByUid(Integer uid);

    /**
     * 添加/扣减佣金
     * @param uid 用户id
     * @param price 金额
     * @param brokeragePrice 历史金额
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationBrokerage(Integer uid, BigDecimal price, BigDecimal brokeragePrice, String type);

    /**
     * 添加/扣减余额
     * @param uid 用户id
     * @param price 金额
     * @param nowMoney 历史金额
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationNowMoney(Integer uid, BigDecimal price, BigDecimal nowMoney, String type);

    /**
     * 添加/扣减积分
     * @param uid 用户id
     * @param integral 积分
     * @param nowIntegral 历史积分
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationIntegral(Integer uid, Integer integral, Integer nowIntegral, String type);

    /**
     * PC后台分销员列表
     * @param storeBrokerageStatus 分销模式 1-指定分销，2-人人分销
     * @param keywords 搜索参数
     * @param dateLimit 时间参数
     * @param pageRequest 分页参数
     * @return
     */
    PageInfo<User> getAdminSpreadPeopleList(String storeBrokerageStatus, String keywords, String dateLimit, PageParamRequest pageRequest);

    /**
     * 推广用户数
     * @param uid 用户id
     * @param dateLimit 时间参数
     * @return Integer
     */
    Integer getSpreadNumberByUidAndDateLimit(Integer uid, String dateLimit);

    /**
     * 清除User Group id
     * @param groupId 待清除的GroupId
     */
    void clearGroupByGroupId(String groupId);

    /**
     * 清除tag
     * @param tagIds tagIds
     */
    void clearTagByTagId(String tagIds);

    /**
     * 更新用户
     * @param userRequest 用户参数
     */
    Boolean updateUser(UserRequest userRequest);
}
