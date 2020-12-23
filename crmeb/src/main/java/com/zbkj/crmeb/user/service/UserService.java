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
     * @param userId 用户id
     * @param price 余额
     * @return 更新后的用户数据
     */
    boolean updateNowMoney(int userId, BigDecimal price);

    boolean group(String id, String groupId);

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
}
