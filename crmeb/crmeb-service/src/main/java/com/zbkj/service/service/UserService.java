package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户表 服务类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface UserService extends IService<User> {

    /**
     * 用户分页列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserResponse> getList(UserSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 操作积分、余额
     */
    Boolean updateIntegralMoney(UserOperateIntegralMoneyRequest request);

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
    Boolean group(String id, String groupId);

    /**
     * 修改密码
     * @param request 修改密码参数
     * @return Boolean
     */
    Boolean password(PasswordRequest request);

    User getInfo();

    User getInfoException();

    /**
     * 根据参数类型查询会员对应的信息
     * @param userId Integer 会员id
     * @param type int 类型 0=消费记录，1=积分明细，2=签到记录，3=持有优惠券，4=余额变动，5=好友关系
     * @param pageParamRequest PageParamRequest 分页
     */
    Object getInfoByCondition(Integer userId,Integer type,PageParamRequest pageParamRequest);

    Integer getUserIdException();

    Integer getUserId();

    Map<Object, Object> getAddUserCountGroupDate(String date);

    /**
     * 换绑手机号校验
     */
    Boolean updatePhoneVerify(UserBindingPhoneUpdateRequest request);

    /**
     * 换绑手机号
     */
    Boolean updatePhone(UserBindingPhoneUpdateRequest request);

    /**
     * 个人中心-用户信息
     * @return UserCenterResponse
     */
    UserCenterResponse getUserCenter();

    /**
     * 根据用户id获取用户列表 map模式
     * @param uidList uidList
     * @return HashMap
     */
    HashMap<Integer, User> getMapListInUid(List<Integer> uidList);

    void repeatSignNum(Integer userId);

    /**
     * 会员标签
     *
     * @param id         String id
     * @param tagId Integer 标签Id
     */
    Boolean tag(String id, String tagId);

    List<Integer> getSpreadPeopleIdList(List<Integer> userId);

    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(List<Integer> userIdList, String keywords, String sortKey, String isAsc, PageParamRequest pageParamRequest);

    /**
     * 会员详情页Top数据
     * @param userId 用户uid
     * @return TopDetail
     */
    TopDetail getTopDetail(Integer userId);

    User registerByThird(RegisterThirdUserRequest thirdUserRequest);

    String getValidateCodeRedisKey(String phone);

    PageInfo<User> getUserListBySpreadLevel(RetailShopStairUserRequest request, PageParamRequest pageParamRequest);

    PageInfo<SpreadOrderResponse> getOrderListBySpreadLevel(RetailShopStairUserRequest request, PageParamRequest pageParamRequest);

    boolean clearSpread(Integer userId);

    /**
     * 推广人排行榜
     * @param type  String 时间范围(week-周，month-月)
     * @param pageParamRequest PageParamRequest 分页
     * @return List
     */
    List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest);

    Integer getCountByPayCount(int minPayCount, int maxPayCount);

    /**
     * 绑定推广关系（登录状态）
     * @param spreadUid 推广人id
     */
    void bindSpread(Integer spreadUid);

    /**
     * 更新推广人
     * @param request 请求参数
     * @return Boolean
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
     * 手机号注册用户
     * @param phone 手机号
     * @param spreadUid 推广人编号
     * @return User
     */
    User registerPhone(String phone, Integer spreadUid);

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
     * @param type add or sub
     * @return Boolean
     */
    Boolean updateSpreadCountByUid(Integer uid, String type);

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
     * @param keywords 搜索参数
     * @param dateLimit 时间参数
     * @param pageRequest 分页参数
     * @return
     */
    PageInfo<User> getAdminSpreadPeopleList(String keywords, String dateLimit, PageParamRequest pageRequest);

    /**
     * 清除User Group id
     * @param groupId 待清除的GroupId
     */
    void clearGroupByGroupId(String groupId);

    /**
     * 更新用户
     * @param userRequest 用户参数
     */
    Boolean updateUser(UserUpdateRequest userRequest);

    /**
     * 根据手机号查询用户
     * @param phone 用户手机号
     * @return 用户信息
     */
    User getByPhone(String phone);

    /**
     * 后台修改用户手机号
     * @param id 用户uid
     * @param phone 手机号
     * @return Boolean
     */
    Boolean updateUserPhone(Integer id, String phone);

    /**
     * 根据昵称匹配用户，返回id集合
     * @param nikeName 需要匹配得昵称
     * @return List
     */
    List<Integer> findIdListLikeName(String nikeName);

    /**
     * 清除对应的用户等级
     * @param levelId 等级id
     */
    Boolean removeLevelByLevelId(Integer levelId);

    /**
     * 更新用户会员等级
     * @param request request
     * @return Boolean
     */
    Boolean updateUserLevel(UpdateUserLevelRequest request);

    /**
     * 获取用户总人数
     */
    Integer getTotalNum();

    /**
     * 根据日期获取注册用户数量
     * @param date 日期
     * @return Integer
     */
    Integer getRegisterNumByDate(String date);

    /**
     * 根据日期段获取注册用户数量
     * @param startDate 日期
     * @param endDate 日期
     * @return UserOverviewResponse
     */
    Integer getRegisterNumByPeriod(String startDate, String endDate);

    /**
     * 获取用户性别数据
     * @return List
     */
    List<User> getSexData();

    /**
     * 获取用户渠道数据
     * @return List
     */
    List<User> getChannelData();

    /**
     * 获取所有用户的id跟地址
     * @return List
     */
    List<User> findIdAndAddresList();

    /**
     * 修改个人资料
     * @param request 修改信息
     */
    Boolean editUser(UserEditRequest request);

    /**
     * 获取用户详情
     * @param id 用户uid
     */
    User getInfoByUid(Integer id);
}
