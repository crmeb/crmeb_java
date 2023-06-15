package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.coupon.StoreCoupon;
import com.zbkj.common.model.coupon.StoreCouponUser;
import com.zbkj.common.model.finance.UserRecharge;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.*;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.token.FrontTokenComponent;
import com.zbkj.common.token.WeChatOauthToken;
import com.zbkj.common.utils.*;
import com.zbkj.common.vo.*;
import com.zbkj.front.service.LoginService;
import com.zbkj.front.service.UserCenterService;
import com.zbkj.service.dao.UserDao;
import com.zbkj.service.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户中心 服务实现类
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
public class UserCenterServiceImpl extends ServiceImpl<UserDao, User> implements UserCenterService {

    private final Logger logger = LoggerFactory.getLogger(UserCenterServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private UserExtractService userExtractService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserRechargeService userRechargeService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private WechatNewService wechatNewService;

    @Autowired
    private UserExperienceRecordService experienceRecordService;

    @Autowired
    private FrontTokenComponent tokenComponent;


    /**
     * 推广数据接口(昨天的佣金 累计提现金额 当前佣金)
     */
    @Override
    public UserCommissionResponse getCommission() {
        User user = userService.getInfoException();
        // 昨天得佣金
        BigDecimal yesterdayIncomes = userBrokerageRecordService.getYesterdayIncomes(user.getUid());
        //累计已提取佣金
        BigDecimal totalMoney = userExtractService.getExtractTotalMoney(user.getUid());

        UserCommissionResponse userCommissionResponse = new UserCommissionResponse();
        userCommissionResponse.setLastDayCount(yesterdayIncomes);
        userCommissionResponse.setExtractCount(totalMoney);
        userCommissionResponse.setCommissionCount(user.getBrokeragePrice());
        return userCommissionResponse;
    }

    /**
     * 推广佣金/提现总和
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSpreadCountByType(Integer type) {
        //推广佣金/提现总和
        Integer userId = userService.getUserIdException();
        if (type == 3) {
            BigDecimal count = userBillService.getSumBigDecimal(null, userId, Constants.USER_BILL_CATEGORY_MONEY, null, Constants.USER_BILL_TYPE_BROKERAGE);
            BigDecimal withdraw = userBillService.getSumBigDecimal(1, userId, Constants.USER_BILL_CATEGORY_MONEY, null, Constants.USER_BILL_TYPE_BROKERAGE); //提现
            return count.subtract(withdraw);
        }

        //累计提现
        if (type == 4) {
            return userExtractService.getWithdrawn(null,null);
        }

        return BigDecimal.ZERO;
    }

    /**
     * 提现申请
     * @return Boolean
     */
    @Override
    public Boolean extractCash(UserExtractRequest request) {
        switch (request.getExtractType()) {
            case "weixin":
                if (StringUtils.isBlank(request.getWechat())) {
                    throw new  CrmebException("请填写微信号！");
                }
                request.setAlipayCode(null);
                request.setBankCode(null);
                request.setBankName(null);
                break;
            case "alipay":
                if (StringUtils.isBlank(request.getAlipayCode())) {
                    throw new  CrmebException("请填写支付宝账号！");
                }
                request.setWechat(null);
                request.setBankCode(null);
                request.setBankName(null);
                break;
            case "bank":
                if (StringUtils.isBlank(request.getBankName())) {
                    throw new  CrmebException("请填写银行名称！");
                }
                if (StringUtils.isBlank(request.getBankCode())) {
                    throw new  CrmebException("请填写银行卡号！");
                }
                request.setWechat(null);
                request.setAlipayCode(null);
                break;
            default:
                throw new  CrmebException("请选择支付方式");
        }
        return userExtractService.extractApply(request);
    }

    /**
     * 提现银行/提现最低金额
     * @return UserExtractCashResponse
     */
    @Override
    public List<String> getExtractBank() {
        // 获取提现银行
        String bank = systemConfigService.getValueByKeyException(Constants.CONFIG_BANK_LIST).replace("\r\n", "\n");
        List<String> bankArr = new ArrayList<>();
        if (bank.indexOf("\n") > 0) {
            bankArr.addAll(Arrays.asList(bank.split("\n")));
        }else{
            bankArr.add(bank);
        }
        return bankArr;
    }

    /**
     * 会员等级列表
     * @return List<UserLevel>
     */
    @Override
    public List<SystemUserLevel> getUserLevelList() {
        return systemUserLevelService.getH5LevelList();
    }

    /**
     * 推广用户， 我自己推广了哪些用户
     * @return List<UserSpreadPeopleItemResponse>
     */
    @Override
    public List<UserSpreadPeopleItemResponse> getSpreadPeopleList(UserSpreadPeopleRequest request, PageParamRequest pageParamRequest) {
        //查询当前用户名下的一级推广员
        Integer userId = userService.getUserIdException();
        List<Integer> userIdList = new ArrayList<>();
        userIdList.add(userId);
        userIdList = userService.getSpreadPeopleIdList(userIdList); //我推广的一级用户id集合

        if (CollUtil.isEmpty(userIdList)) {//如果没有一级推广人，直接返回
            return new ArrayList<>();
        }
        if (request.getGrade().equals(1)) {// 二级推广人
            //查询二级推广人
            List<Integer> secondSpreadIdList = userService.getSpreadPeopleIdList(userIdList);
            //二级推广人
            userIdList.clear();
            userIdList.addAll(secondSpreadIdList);
        }
        List<UserSpreadPeopleItemResponse> spreadPeopleList = userService.getSpreadPeopleList(userIdList, request.getKeyword(), request.getSortKey(), request.getIsAsc(), pageParamRequest);
        spreadPeopleList.forEach(e -> {
            OrderBrokerageData brokerageData = storeOrderService.getBrokerageData(e.getUid(), userId);
            e.setOrderCount(brokerageData.getNum());
            e.setNumberCount(brokerageData.getPrice());
        });
        return spreadPeopleList;
    }

    /**
     * 充值额度选择
     * @return UserRechargeResponse
     */
    @Override
    public UserRechargeFrontResponse getRechargeConfig() {
        UserRechargeFrontResponse userRechargeResponse = new UserRechargeFrontResponse();
        userRechargeResponse.setRechargeQuota(systemGroupDataService.getListByGid(SysGroupDataConstants.GROUP_DATA_ID_RECHARGE_LIST, UserRechargeItemResponse.class));
        String rechargeAttention = systemConfigService.getValueByKey(Constants.CONFIG_RECHARGE_ATTENTION);
        List<String> rechargeAttentionList = new ArrayList<>();
        if (StringUtils.isNotBlank(rechargeAttention)) {
            rechargeAttentionList = CrmebUtil.stringToArrayStrRegex(rechargeAttention, "\n");
        }
        userRechargeResponse.setRechargeAttention(rechargeAttentionList);
        return userRechargeResponse;
    }

    /**
     * 用户资金统计
     * @return UserBalanceResponse
     */
    @Override
    public UserBalanceResponse getUserBalance() {
        User info = userService.getInfo();
        BigDecimal recharge = userBillService.getSumBigDecimal(1, info.getUid(), Constants.USER_BILL_CATEGORY_MONEY, null, null);
        BigDecimal orderStatusSum = userBillService.getSumBigDecimal(0, info.getUid(), Constants.USER_BILL_CATEGORY_MONEY, null, null);
//        BigDecimal orderStatusSum = storeOrderService.getSumBigDecimal(info.getUid(), null);
        return new UserBalanceResponse(info.getNowMoney(), recharge, orderStatusSum);
    }

    /**
     * 推广订单
     * @return UserSpreadOrderResponse;
     */
    @Override
    public UserSpreadOrderResponse getSpreadOrder(PageParamRequest pageParamRequest) {
        User user = userService.getInfo();
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户数据异常");
        }
        UserSpreadOrderResponse spreadOrderResponse = new UserSpreadOrderResponse();
        // 获取累计推广条数
        Integer spreadCount = userBrokerageRecordService.getSpreadCountByUid(user.getUid());
        spreadOrderResponse.setCount(spreadCount.longValue());
        if (spreadCount.equals(0)) {
            return spreadOrderResponse;
        }

        // 获取推广订单记录，分页
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.findSpreadListByUid(user.getUid(), pageParamRequest);
        // 获取对应的订单信息
        List<String> orderNoList = recordList.stream().map(UserBrokerageRecord::getLinkId).collect(Collectors.toList());
        Map<String, StoreOrder> orderMap = storeOrderService.getMapInOrderNo(orderNoList);
        // 获取对应的用户信息
        List<StoreOrder> storeOrderList = new ArrayList<>(orderMap.values());
        List<Integer> uidList = storeOrderList.stream().map(StoreOrder::getUid).distinct().collect(Collectors.toList());
        HashMap<Integer, User> userMap = userService.getMapListInUid(uidList);

        List<UserSpreadOrderItemResponse> userSpreadOrderItemResponseList = new ArrayList<>();
        List<String> monthList = CollUtil.newArrayList();
        recordList.forEach(record -> {
            UserSpreadOrderItemChildResponse userSpreadOrderItemChildResponse = new UserSpreadOrderItemChildResponse();
            userSpreadOrderItemChildResponse.setOrderId(record.getLinkId());
            userSpreadOrderItemChildResponse.setTime(record.getUpdateTime());
            userSpreadOrderItemChildResponse.setNumber(record.getPrice());
            Integer orderUid = orderMap.get(record.getLinkId()).getUid();
            userSpreadOrderItemChildResponse.setAvatar(userMap.get(orderUid).getAvatar());
            userSpreadOrderItemChildResponse.setNickname(userMap.get(orderUid).getNickname());
            userSpreadOrderItemChildResponse.setType("返佣");

            String month = DateUtil.dateToStr(record.getUpdateTime(), Constants.DATE_FORMAT_MONTH);
            if (monthList.contains(month)) {
                //如果在已有的数据中找到当前月份数据则追加
                for (UserSpreadOrderItemResponse userSpreadOrderItemResponse : userSpreadOrderItemResponseList) {
                    if (userSpreadOrderItemResponse.getTime().equals(month)) {
                        userSpreadOrderItemResponse.getChild().add(userSpreadOrderItemChildResponse);
                        break;
                    }
                }
            } else {// 不包含此月份
                //创建一个
                UserSpreadOrderItemResponse userSpreadOrderItemResponse = new UserSpreadOrderItemResponse();
                userSpreadOrderItemResponse.setTime(month);
                userSpreadOrderItemResponse.getChild().add(userSpreadOrderItemChildResponse);
                userSpreadOrderItemResponseList.add(userSpreadOrderItemResponse);
                monthList.add(month);
            }
        });

        // 获取月份总订单数
        Map<String, Integer> countMap = userBrokerageRecordService.getSpreadCountByUidAndMonth(user.getUid(), monthList);
        for (UserSpreadOrderItemResponse userSpreadOrderItemResponse: userSpreadOrderItemResponseList) {
            userSpreadOrderItemResponse.setCount(countMap.get(userSpreadOrderItemResponse.getTime()));
        }

        spreadOrderResponse.setList(userSpreadOrderItemResponseList);
        return spreadOrderResponse;
    }

    /**
     * 充值
     * @return UserSpreadOrderResponse;
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public OrderPayResultResponse recharge(UserRechargeRequest request) {
        request.setPayType(Constants.PAY_TYPE_WE_CHAT);

        //验证金额是否为最低金额
        String rechargeMinAmountStr = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_RECHARGE_MIN_AMOUNT);
        BigDecimal rechargeMinAmount = new BigDecimal(rechargeMinAmountStr);
        int compareResult = rechargeMinAmount.compareTo(request.getPrice());
        if (compareResult > 0) {
            throw new CrmebException("充值金额不能低于" + rechargeMinAmountStr);
        }

        request.setGivePrice(BigDecimal.ZERO);

        if (request.getGroupDataId() > 0) {
            SystemGroupDataRechargeConfigVo systemGroupData = systemGroupDataService.getNormalInfo(request.getGroupDataId(), SystemGroupDataRechargeConfigVo.class);
            if (ObjectUtil.isNull(systemGroupData)) {
                throw new CrmebException("您选择的充值方式已下架");
            }
            //售价和赠送
            request.setPrice(systemGroupData.getPrice());
            request.setGivePrice(systemGroupData.getGiveMoney());
        }
        User currentUser = userService.getInfoException();
        //生成系统订单
        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setUid(currentUser.getUid());
        userRecharge.setOrderId(CrmebUtil.getOrderNo("recharge"));
        userRecharge.setPrice(request.getPrice());
        userRecharge.setGivePrice(request.getGivePrice());
        userRecharge.setRechargeType(request.getFromType());
        boolean save = userRechargeService.save(userRecharge);
        if (!save) {
            throw new CrmebException("生成充值订单失败!");
        }

        OrderPayResultResponse response = new OrderPayResultResponse();
        MyRecord record = new MyRecord();
        Map<String, String> unifiedorder = weChatPayService.unifiedRecharge(userRecharge, request.getClientIp());
        record.set("status", true);
        response.setStatus(true);
        WxPayJsResultVo vo = new WxPayJsResultVo();
        vo.setAppId(unifiedorder.get("appId"));
        vo.setNonceStr(unifiedorder.get("nonceStr"));
        vo.setPackages(unifiedorder.get("package"));
        vo.setSignType(unifiedorder.get("signType"));
        vo.setTimeStamp(unifiedorder.get("timeStamp"));
        vo.setPaySign(unifiedorder.get("paySign"));
        if (userRecharge.getRechargeType().equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5)) {
            vo.setMwebUrl(unifiedorder.get("mweb_url"));
            response.setPayType(PayConstants.PAY_CHANNEL_WE_CHAT_H5);
        }
        response.setJsConfig(vo);
        response.setOrderNo(userRecharge.getOrderId());
        return response;
    }

    /**
     * 微信登录
     * @return LoginResponse;
     */
    @Override
    public LoginResponse weChatAuthorizeLogin(String code, Integer spreadUid) {
        // 通过code获取获取公众号授权信息
        WeChatOauthToken oauthToken = wechatNewService.getOauth2AccessToken(code);
        //检测是否存在
        UserToken userToken = userTokenService.getByOpenidAndType(oauthToken.getOpenId(),  Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC);
        LoginResponse loginResponse = new LoginResponse();
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }

            // 记录最后一次登录时间
            user.setLastLoginTime(DateUtil.nowDateTime());
            Boolean execute = transactionTemplate.execute(e -> {
                // 分销绑定
                if (userService.checkBingSpread(user, spreadUid, "old")) {
                    user.setSpreadUid(spreadUid);
                    user.setSpreadTime(DateUtil.nowDateTime());
                    // 处理新旧推广人数据
                    userService.updateSpreadCountByUid(spreadUid, "add");
                }
                userService.updateById(user);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("公众号登录绑定分销关系失败，uid={},spreadUid={}", user.getUid(), spreadUid));
            }
            try {
                String token = tokenComponent.createToken(user);
                loginResponse.setToken(token);
            } catch (Exception e) {
                logger.error(StrUtil.format("公众号登录生成token失败，uid={}", user.getUid()));
                e.printStackTrace();
            }
            loginResponse.setType("login");
            loginResponse.setUid(user.getUid());
            loginResponse.setNikeName(user.getNickname());
            loginResponse.setPhone(user.getPhone());
            return loginResponse;
        }
        // 没有用户，走创建用户流程
        // 从微信获取用户信息，存入Redis中，将key返回给前端，前端在下一步绑定手机号的时候下发
        WeChatAuthorizeLoginUserInfoVo userInfo = wechatNewService.getSnsUserInfo(oauthToken.getAccessToken(), oauthToken.getOpenId());
        RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
        BeanUtils.copyProperties(userInfo, registerThirdUserRequest);
        registerThirdUserRequest.setSpreadPid(spreadUid);
        registerThirdUserRequest.setType(Constants.USER_LOGIN_TYPE_PUBLIC);
        registerThirdUserRequest.setOpenId(oauthToken.getOpenId());
        String key = SecureUtil.md5(oauthToken.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(registerThirdUserRequest), (long) (60 * 2), TimeUnit.MINUTES);

        loginResponse.setType("register");
        loginResponse.setKey(key);
        return loginResponse;
    }

    /**
     * 获取微信授权logo
     * @return String;
     */
    @Override
    public String getLogo() {
        return systemConfigService.getValueByKey(Constants.CONFIG_KEY_MOBILE_LOGIN_LOGO);
    }

    /**
     * 小程序登录
     * @param code String 前端临时授权code
     * @param request RegisterThirdUserRequest 用户信息
     * @return LoginResponse
     */
    @Override
    public LoginResponse weChatAuthorizeProgramLogin(String code, RegisterThirdUserRequest request) {
        WeChatMiniAuthorizeVo response = wechatNewService.miniAuthCode(code);
        System.out.println("小程序登陆成功 = " + JSON.toJSONString(response));

        //检测是否存在
        UserToken userToken = userTokenService.getByOpenidAndType(response.getOpenId(), Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM);
        LoginResponse loginResponse = new LoginResponse();
        if (ObjectUtil.isNotNull(userToken)) {// 已存在，正常登录
            User user = userService.getById(userToken.getUid());
            if (!user.getStatus()) {
                throw new CrmebException("当前账户已禁用，请联系管理员！");
            }
            // 记录最后一次登录时间
            user.setLastLoginTime(DateUtil.nowDateTime());
            Boolean execute = transactionTemplate.execute(e -> {
                // 分销绑定
                if (userService.checkBingSpread(user, request.getSpreadPid(), "old")) {
                    user.setSpreadUid(request.getSpreadPid());
                    user.setSpreadTime(DateUtil.nowDateTime());
                    // 处理新旧推广人数据
                    userService.updateSpreadCountByUid(request.getSpreadPid(), "add");
                }
                userService.updateById(user);
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("小程序登录绑定分销关系失败，uid={},spreadUid={}", user.getUid(), request.getSpreadPid()));
            }

            try {
                String token = tokenComponent.createToken(user);
                loginResponse.setToken(token);
            } catch (Exception e) {
                logger.error(StrUtil.format("小程序登录生成token失败，uid={}", user.getUid()));
                e.printStackTrace();
            }
            loginResponse.setType("login");
            loginResponse.setUid(user.getUid());
            loginResponse.setNikeName(user.getNickname());
            loginResponse.setPhone(user.getPhone());
            return loginResponse;
        }

        if (StrUtil.isBlank(request.getNickName()) && StrUtil.isBlank(request.getAvatar()) && StrUtil.isBlank(request.getHeadimgurl())) {
            // 返回后，前端去走注册起始页
            loginResponse.setType("start");
            return loginResponse;
        }

        request.setType(Constants.USER_LOGIN_TYPE_PROGRAM);
        request.setOpenId(response.getOpenId());
        String key = SecureUtil.md5(response.getOpenId());
        redisUtil.set(key, JSONObject.toJSONString(request), (long) (60 * 2), TimeUnit.MINUTES);
        loginResponse.setType("register");
        loginResponse.setKey(key);
        return loginResponse;
    }

    /**
     * 推广人排行榜
     * @param type  String 时间范围(week-周，month-月)
     * @param pageParamRequest PageParamRequest 分页
     * @return List<LoginResponse>
     */
    @Override
    public List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest) {
        return userService.getTopSpreadPeopleListByDate(type, pageParamRequest);
    }

    /**
     * 佣金排行榜
     * @param type  String 时间范围
     * @param pageParamRequest PageParamRequest 分页
     * @return List<User>
     */
    @Override
    public List<User> getTopBrokerageListByDate(String type, PageParamRequest pageParamRequest) {
        // 获取佣金排行榜（周、月）
        List<UserBrokerageRecord> recordList = userBrokerageRecordService.getBrokerageTopByDate(type);
        if (CollUtil.isEmpty(recordList)) {
            return null;
        }
        // 解决0元排行问题
        for (int i = 0; i < recordList.size();) {
            UserBrokerageRecord userBrokerageRecord = recordList.get(i);
            if (userBrokerageRecord.getPrice().compareTo(BigDecimal.ZERO) < 1) {
                recordList.remove(i);
                continue;
            }
            i++;
        }
        if (CollUtil.isEmpty(recordList)) {
            return null;
        }

        List<Integer> uidList = recordList.stream().map(UserBrokerageRecord::getUid).collect(Collectors.toList());
        //查询用户
        HashMap<Integer, User> userVoList = userService.getMapListInUid(uidList);

        //解决排序问题
        List<User> userList = CollUtil.newArrayList();
        for (UserBrokerageRecord record: recordList) {
            User user = new User();
            User userVo = userVoList.get(record.getUid());

            user.setUid(record.getUid());
            user.setAvatar(userVo.getAvatar());
            user.setBrokeragePrice(record.getPrice());
            if (StrUtil.isBlank(userVo.getNickname())) {
                user.setNickname(userVo.getPhone().substring(0, 2) + "****" + userVo.getPhone().substring(7));
            }else{
                user.setNickname(userVo.getNickname());
            }
            userList.add(user);
        }
        return userList;
    }

    /**
     * 推广海报图
     * @return List<SystemGroupData>
     */
    @Override
    public List<UserSpreadBannerResponse> getSpreadBannerList() {
        return systemGroupDataService.getListByGid(Constants.GROUP_DATA_ID_SPREAD_BANNER_LIST, UserSpreadBannerResponse.class);
    }

    /**
     * 当前用户在佣金排行第几名
     * @param type  String 时间范围
     * @return 优惠券集合
     */
    @Override
    public Integer getNumberByTop(String type) {
        int number = 0;
        Integer userId = userService.getUserIdException();
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(100);

        List<UserBrokerageRecord> recordList = userBrokerageRecordService.getBrokerageTopByDate(type);
        if (CollUtil.isEmpty(recordList)) {
            return number;
        }

        for (int i = 0; i < recordList.size(); i++) {
            if (recordList.get(i).getUid().equals(userId)) {
                number = i + 1;
                break ;
            }
        }
        return number;
    }

    /**
     * 佣金转入余额
     * @return Boolean
     */
    @Override
    public Boolean transferIn(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CrmebException("转入金额不能为0");
        }
        //当前可提现佣金
        User user = userService.getInfo();
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户数据异常");
        }
        BigDecimal subtract = user.getBrokeragePrice();
        if (subtract.compareTo(price) < 0) {
            throw new CrmebException("您当前可充值余额为 " + subtract + "元");
        }
        // userBill现金增加记录
        UserBill userBill = new UserBill();
        userBill.setUid(user.getUid());
        userBill.setLinkId("0");
        userBill.setPm(1);
        userBill.setTitle("佣金转余额");
        userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
        userBill.setType(Constants.USER_BILL_TYPE_TRANSFER_IN);
        userBill.setNumber(price);
        userBill.setBalance(user.getNowMoney().add(price));
        userBill.setMark(StrUtil.format("佣金转余额,增加{}", price));
        userBill.setStatus(1);
        userBill.setCreateTime(DateUtil.nowDateTime());

        // userBrokerage转出记录
        UserBrokerageRecord brokerageRecord = new UserBrokerageRecord();
        brokerageRecord.setUid(user.getUid());
        brokerageRecord.setLinkId("0");
        brokerageRecord.setLinkType(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_YUE);
        brokerageRecord.setType(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_SUB);
        brokerageRecord.setTitle(BrokerageRecordConstants.BROKERAGE_RECORD_TITLE_BROKERAGE_YUE);
        brokerageRecord.setPrice(price);
        brokerageRecord.setBalance(user.getNowMoney().add(price));
        brokerageRecord.setMark(StrUtil.format("佣金转余额，减少{}", price));
        brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        brokerageRecord.setCreateTime(DateUtil.nowDateTime());

        Boolean execute = transactionTemplate.execute(e -> {
            // 扣佣金
            userService.operationBrokerage(user.getUid(), price, user.getBrokeragePrice(), "sub");
            // 加余额
            userService.operationNowMoney(user.getUid(), price, user.getNowMoney(), "add");
            userBillService.save(userBill);
            userBrokerageRecordService.save(brokerageRecord);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 提现记录
     */
    @Override
    public PageInfo<UserExtractRecordResponse> getExtractRecord(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        return userExtractService.getExtractRecord(userId, pageParamRequest);
    }

    /**
     * 推广佣金明细
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<SpreadCommissionDetailResponse> getSpreadCommissionDetail(PageParamRequest pageParamRequest) {
        User user = userService.getInfoException();
        return userBrokerageRecordService.findDetailListByUid(user.getUid(), pageParamRequest);
    }

    /**
     * 用户账单记录（现金）
     * @param type 记录类型：all-全部，expenditure-支出，income-收入
     * @return CommonPage
     */
    @Override
    public CommonPage<UserRechargeBillRecordResponse> nowMoneyBillRecord(String type, PageParamRequest pageRequest) {
        User user = userService.getInfo();
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户数据异常");
        }
        PageInfo<UserBill> billPageInfo = userBillService.nowMoneyBillRecord(user.getUid(), type, pageRequest);
        List<UserBill> list = billPageInfo.getList();

        // 获取年-月
        Map<String, List<UserBill>> map = CollUtil.newHashMap();
        list.forEach(i -> {
            String month = StrUtil.subPre(DateUtil.dateToStr(i.getCreateTime(), Constants.DATE_FORMAT), 7);
            if (map.containsKey(month)) {
                map.get(month).add(i);
            } else {
                List<UserBill> billList = CollUtil.newArrayList();
                billList.add(i);
                map.put(month, billList);
            }
        });
        List<UserRechargeBillRecordResponse> responseList = CollUtil.newArrayList();
        map.forEach((key, value) -> {
            UserRechargeBillRecordResponse response = new UserRechargeBillRecordResponse();
            response.setDate(key);
            response.setList(value);
            responseList.add(response);
        });

        PageInfo<UserRechargeBillRecordResponse> pageInfo = CommonPage.copyPageInfo(billPageInfo, responseList);
        return CommonPage.restPage(pageInfo);
    }

    /**
     * 微信注册绑定手机号
     * @param request 请求参数
     * @return 登录信息
     */
    @Override
    public LoginResponse registerBindingPhone(WxBindingPhoneRequest request) {
        checkBindingPhone(request);

        // 进入创建用户绑定手机号流程
        Object o = redisUtil.get(request.getKey());
        if (ObjectUtil.isNull(o)) {
            throw new CrmebException("用户缓存已过期，请清除缓存重新登录");
        }
        RegisterThirdUserRequest registerThirdUserRequest = JSONObject.parseObject(o.toString(), RegisterThirdUserRequest.class);

        boolean isNew = true;

        User user = userService.getByPhone(request.getPhone());
        if (ObjectUtil.isNull(user)) {
            user = userService.registerByThird(registerThirdUserRequest);
            user.setPhone(request.getPhone());
            user.setAccount(request.getPhone());
            user.setSpreadUid(0);
            user.setPwd(CommonUtil.createPwd(request.getPhone()));
        } else {// 已有账户，关联到之前得账户即可
            // 查询是否用对应得token
            int type = 0;
            switch (request.getType()) {
                case "public":
                    type = Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC;
                    break;
                case "routine":
                    type = Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM;
                    break;
            }

            UserToken userToken = userTokenService.getTokenByUserId(user.getUid(), type);
            if (ObjectUtil.isNotNull(userToken)) {
                throw new CrmebException("该手机号已被注册");
            }
            isNew = false;
        }

        User finalUser = user;
        boolean finalIsNew = isNew;
        Boolean execute = transactionTemplate.execute(e -> {
            if (finalIsNew) {// 新用户
                // 分销绑定
                if (userService.checkBingSpread(finalUser, registerThirdUserRequest.getSpreadPid(), "new")) {
                    finalUser.setSpreadUid(registerThirdUserRequest.getSpreadPid());
                    finalUser.setSpreadTime(DateUtil.nowDateTime());
                    userService.updateSpreadCountByUid(registerThirdUserRequest.getSpreadPid(), "add");
                }
                userService.save(finalUser);
                // 赠送新人券
                giveNewPeopleCoupon(finalUser.getUid());
            }
            switch (request.getType()) {
                case "public":
                    userTokenService.bind(registerThirdUserRequest.getOpenId(), Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC, finalUser.getUid());
                    break;
                case "routine":
                    userTokenService.bind(registerThirdUserRequest.getOpenId(), Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM, finalUser.getUid());
                    break;
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error("微信用户注册生成失败，nickName = " + registerThirdUserRequest.getNickName());
        } else if (!isNew) {// 老用户绑定推广人
            if (ObjectUtil.isNotNull(registerThirdUserRequest.getSpreadPid()) && registerThirdUserRequest.getSpreadPid() > 0) {
                loginService.bindSpread(finalUser, registerThirdUserRequest.getSpreadPid());
            }
        }
        LoginResponse loginResponse = new LoginResponse();
        try {
            String token = tokenComponent.createToken(finalUser);
            loginResponse.setToken(token);
        } catch (Exception e) {
            logger.error(StrUtil.format("绑定手机号，自动登录生成token失败，uid={}", finalUser.getUid()));
            e.printStackTrace();
        }
        loginResponse.setType("login");
        loginResponse.setUid(user.getUid());
        loginResponse.setNikeName(user.getNickname());
        loginResponse.setPhone(user.getPhone());
        return loginResponse;
    }

    /**
     * 用户积分记录列表
     * @param pageParamRequest 分页参数
     * @return List<UserIntegralRecord>
     */
    @Override
    public List<UserIntegralRecord> getUserIntegralRecordList(PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        return userIntegralRecordService.findUserIntegralRecordList(uid, pageParamRequest);
    }

    /**
     * 获取用户积分信息
     * @return IntegralUserResponse
     */
    @Override
    public IntegralUserResponse getIntegralUser() {
        User user = userService.getInfoException();
        IntegralUserResponse userSignInfoResponse = new IntegralUserResponse();

        //签到
        Integer sumIntegral = userIntegralRecordService.getSumIntegral(user.getUid(), IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD, "", null);
        Integer deductionIntegral = userIntegralRecordService.getSumIntegral(user.getUid(), IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB, "", IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_ORDER);
        userSignInfoResponse.setSumIntegral(sumIntegral);
        userSignInfoResponse.setDeductionIntegral(deductionIntegral);
        // 冻结积分
        Integer frozenIntegral = userIntegralRecordService.getFrozenIntegralByUid(user.getUid());
        userSignInfoResponse.setFrozenIntegral(frozenIntegral);
        userSignInfoResponse.setIntegral(user.getIntegral());
        return userSignInfoResponse;
    }

    /**
     * 获取用户经验记录
     * @param pageParamRequest 分页参数
     * @return List<UserExperienceRecord>
     */
    @Override
    public List<UserExperienceRecord> getUserExperienceList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        return experienceRecordService.getH5List(userId, pageParamRequest);
    }

    /**
     * 提现用户信息
     * @return UserExtractCashResponse
     */
    @Override
    public UserExtractCashResponse getExtractUser() {
        User user = userService.getInfoException();
        // 提现最低金额
        String minPrice = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_EXTRACT_MIN_PRICE);
        // 冻结天数
        String extractTime = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_EXTRACT_FREEZING_TIME);
        // 可提现佣金
        BigDecimal brokeragePrice = user.getBrokeragePrice();
        // 冻结佣金
        BigDecimal freeze = userBrokerageRecordService.getFreezePrice(user.getUid());
        return new UserExtractCashResponse(minPrice, brokeragePrice, freeze, extractTime);
    }

    /**
     * 推广人列表统计
     * @return UserSpreadPeopleResponse
     */
    @Override
    public UserSpreadPeopleResponse getSpreadPeopleCount() {
        //查询当前用户名下的一级推广员
        UserSpreadPeopleResponse userSpreadPeopleResponse = new UserSpreadPeopleResponse();
        List<Integer> userIdList = new ArrayList<>();
        Integer userId = userService.getUserIdException();
        userIdList.add(userId);
        userIdList = userService.getSpreadPeopleIdList(userIdList); //我推广的一级用户id集合

        if (CollUtil.isEmpty(userIdList)) {//如果没有一级推广人，直接返回
            userSpreadPeopleResponse.setCount(0);
            userSpreadPeopleResponse.setTotal(0);
            userSpreadPeopleResponse.setTotalLevel(0);
            return userSpreadPeopleResponse;
        }

        userSpreadPeopleResponse.setTotal(userIdList.size()); //一级推广人
        //查询二级推广人
        List<Integer> secondSpreadIdList = userService.getSpreadPeopleIdList(userIdList);
        if (CollUtil.isEmpty(secondSpreadIdList)) {
            userSpreadPeopleResponse.setTotalLevel(0);
            userSpreadPeopleResponse.setCount(userSpreadPeopleResponse.getTotal());
            return userSpreadPeopleResponse;
        }
        userSpreadPeopleResponse.setTotalLevel(secondSpreadIdList.size());
        userSpreadPeopleResponse.setCount(userIdList.size() + secondSpreadIdList.size());
        return userSpreadPeopleResponse;
    }

    /**
     * 绑定手机号数据校验
     */
    private void checkBindingPhone(WxBindingPhoneRequest request) {
        if (!"public".equals(request.getType()) && !"routine".equals(request.getType()) && !"iosWx".equals(request.getType()) && !"androidWx".equals(request.getType())) {
            throw new CrmebException("未知的用户类型");
        }
        if ("public".equals(request.getType())) {
            if (StrUtil.isBlank(request.getCaptcha())) {
                throw new CrmebException("验证码不能为空");
            }
            boolean matchPhone = ReUtil.isMatch(RegularConstants.PHONE_TWO, request.getPhone());
            if (!matchPhone) {
                throw new CrmebException("手机号格式错误，请输入正确得手机号");
            }
            // 公众号用户校验验证码
            boolean match = ReUtil.isMatch(RegularConstants.VALIDATE_CODE_NUM_SIX, request.getCaptcha());
            if (!match) {
                throw new CrmebException("验证码格式错误，验证码必须为6位数字");
            }
            checkValidateCode(request.getPhone(), request.getCaptcha());
        } else {
            // 参数校验
            if (StrUtil.isBlank(request.getCode())) {
                throw new CrmebException("小程序获取手机号code不能为空");
            }
            if (StrUtil.isBlank(request.getEncryptedData())) {
                throw new CrmebException("小程序获取手机号加密数据不能为空");
            }
            if (StrUtil.isBlank(request.getIv())) {
                throw new CrmebException("小程序获取手机号加密算法的初始向量不能为空");
            }
            // 获取appid
            String programAppId = systemConfigService.getValueByKey(WeChatConstants.WECHAT_MINI_APPID);
            if (StringUtils.isBlank(programAppId)) {
                throw new CrmebException("微信小程序appId未设置");
            }

            WeChatMiniAuthorizeVo response = wechatNewService.miniAuthCode(request.getCode());
//            WeChatMiniAuthorizeVo response = weChatService.programAuthorizeLogin(request.getCode());
            System.out.println("小程序登陆成功 = " + JSON.toJSONString(response));
            String decrypt = WxUtil.decrypt(programAppId, request.getEncryptedData(), response.getSessionKey(), request.getIv());
            if (StrUtil.isBlank(decrypt)) {
                throw new CrmebException("微信小程序获取手机号解密失败");
            }
            JSONObject jsonObject = JSONObject.parseObject(decrypt);
            if (StrUtil.isBlank(jsonObject.getString("phoneNumber"))) {
                throw new CrmebException("微信小程序获取手机号没有有效的手机号");
            }
            request.setPhone(jsonObject.getString("phoneNumber"));
        }
    }

    /**
     * 赠送新人券
     * @param uid 用户uid
     */
    private void giveNewPeopleCoupon(Integer uid) {
        // 查询是否有新人注册赠送优惠券
        List<StoreCouponUser> couponUserList = CollUtil.newArrayList();
        List<StoreCoupon> couponList = storeCouponService.findRegisterList();
        if (CollUtil.isNotEmpty(couponList)) {
            couponList.forEach(storeCoupon -> {
                //是否有固定的使用时间
                if (!storeCoupon.getIsFixedTime()) {
                    String endTime = DateUtil.addDay(DateUtil.nowDate(Constants.DATE_FORMAT), storeCoupon.getDay(), Constants.DATE_FORMAT);
                    storeCoupon.setUseEndTime(DateUtil.strToDate(endTime, Constants.DATE_FORMAT));
                    storeCoupon.setUseStartTime(DateUtil.nowDateTimeReturnDate(Constants.DATE_FORMAT));
                }

                StoreCouponUser storeCouponUser = new StoreCouponUser();
                storeCouponUser.setCouponId(storeCoupon.getId());
                storeCouponUser.setName(storeCoupon.getName());
                storeCouponUser.setMoney(storeCoupon.getMoney());
                storeCouponUser.setMinPrice(storeCoupon.getMinPrice());
                storeCouponUser.setUseType(storeCoupon.getUseType());
                if (storeCoupon.getIsFixedTime()) {// 使用固定时间
                    storeCouponUser.setStartTime(storeCoupon.getUseStartTime());
                    storeCouponUser.setEndTime(storeCoupon.getUseEndTime());
                } else {// 没有固定使用时间
                    Date nowDate = DateUtil.nowDateTime();
                    storeCouponUser.setStartTime(nowDate);
                    DateTime dateTime = cn.hutool.core.date.DateUtil.offsetDay(nowDate, storeCoupon.getDay());
                    storeCouponUser.setEndTime(dateTime);
                }
                storeCouponUser.setType("register");
                if (storeCoupon.getUseType() > 1) {
                    storeCouponUser.setPrimaryKey(storeCoupon.getPrimaryKey());
                }
                storeCouponUser.setType(CouponConstants.STORE_COUPON_USER_TYPE_REGISTER);
                couponUserList.add(storeCouponUser);
            });
        }

        // 赠送客户优惠券
        if (CollUtil.isNotEmpty(couponUserList)) {
            couponUserList.forEach(couponUser -> couponUser.setUid(uid));
            storeCouponUserService.saveBatch(couponUserList);
            couponList.forEach(coupon -> storeCouponService.deduction(coupon.getId(), 1, coupon.getIsLimited()));
        }
    }

    /**
     * 检测手机验证码
     * @param phone 手机号
     * @param code 验证码
     */
    private void checkValidateCode(String phone, String code) {
        Object validateCode = redisUtil.get(SmsConstants.SMS_VALIDATE_PHONE + phone);
        if (validateCode == null) {
            throw new CrmebException("验证码已过期");
        }
        if (!validateCode.toString().equals(code)) {
            throw new CrmebException("验证码错误");
        }
        //删除验证码
        redisUtil.delete(SmsConstants.SMS_VALIDATE_PHONE + phone);
    }
}
