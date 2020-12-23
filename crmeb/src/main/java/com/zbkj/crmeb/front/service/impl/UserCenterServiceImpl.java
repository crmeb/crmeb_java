package com.zbkj.crmeb.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.zbkj.crmeb.finance.model.UserRecharge;
import com.zbkj.crmeb.finance.request.FundsMonitorSearchRequest;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.finance.service.UserExtractService;
import com.zbkj.crmeb.finance.service.UserRechargeService;
import com.zbkj.crmeb.front.request.UserRechargeRequest;
import com.zbkj.crmeb.front.request.UserSpreadPeopleRequest;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.front.service.UserCenterService;
import com.zbkj.crmeb.payment.service.RechargePayService;
import com.zbkj.crmeb.payment.vo.wechat.CreateOrderResponseVo;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.system.request.SystemUserLevelSearchRequest;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.system.service.SystemUserLevelService;
import com.zbkj.crmeb.system.vo.SystemGroupDataRechargeConfigVo;
import com.zbkj.crmeb.user.dao.UserDao;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.request.RegisterThirdUserRequest;
import com.zbkj.crmeb.user.request.UserOperateFundsRequest;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.response.WeChatAuthorizeLoginGetOpenIdResponse;
import com.zbkj.crmeb.wechat.response.WeChatAuthorizeLoginUserInfoResponse;
import com.zbkj.crmeb.wechat.response.WeChatProgramAuthorizeLoginGetOpenIdResponse;
import com.zbkj.crmeb.wechat.service.WeChatService;
import com.zbkj.crmeb.wechat.service.impl.WechatSendMessageForMinService;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForTopped;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 用户中心 服务实现类
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
@Service
public class UserCenterServiceImpl extends ServiceImpl<UserDao, User> implements UserCenterService {

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
    private RechargePayService rechargePayService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private WechatSendMessageForMinService wechatSendMessageForMinService;



    /**
     * 推广数据接口(昨天的佣金 累计提现金额 当前佣金)
     * @author Mr.Zhang
     * @since 2020-06-08
     */
    @Override
    public UserCommissionResponse getCommission() {
        UserCommissionResponse userCommissionResponse = new UserCommissionResponse();
        //昨天的佣金
        userCommissionResponse.setLastDayCount(userBillService.getSumBigDecimal(0, userService.getUserIdException(), Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, Constants.SEARCH_DATE_YESTERDAY, null));

//        userCommissionResponse.setExtractCount(userBillService.getSumBigDecimal(0, userService.getUserIdException(), Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE, null, Constants.USER_BILL_TYPE_EXTRACT));
        //累计已提取佣金
        userCommissionResponse.setExtractCount(userExtractService.getExtractTotalMoney(userService.getUserIdException()));

        userCommissionResponse.setCommissionCount(userService.getInfo().getBrokeragePrice());
        return userCommissionResponse;
    }

    /**
     * 推广佣金明细
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return UserSpreadCommissionResponse
     */
    @Override
    public PageInfo<UserSpreadCommissionResponse> getSpreadCommissionByType(int type, PageParamRequest pageParamRequest) {
        String category = Constants.USER_BILL_CATEGORY_MONEY;
        List<String> typeList = CollUtil.newArrayList();
        switch (type){
            case 0:
                typeList.add(Constants.USER_BILL_TYPE_RECHARGE);
                typeList.add(Constants.USER_BILL_TYPE_PAY_MONEY);
                typeList.add(Constants.USER_BILL_TYPE_SYSTEM_ADD);
                typeList.add(Constants.USER_BILL_TYPE_PAY_PRODUCT_REFUND);
                typeList.add(Constants.USER_BILL_TYPE_SYSTEM_SUB);
                typeList.add(Constants.USER_BILL_TYPE_PAY_MEMBER);
                typeList.add(Constants.USER_BILL_TYPE_OFFLINE_SCAN);
                break;
            case 1:
                typeList.add(Constants.USER_BILL_TYPE_PAY_MONEY);
                typeList.add(Constants.USER_BILL_TYPE_PAY_MEMBER);
                typeList.add(Constants.USER_BILL_TYPE_OFFLINE_SCAN);
                typeList.add(Constants.USER_BILL_TYPE_USER_RECHARGE_REFUND);
                break;
            case 2:
                typeList.add(Constants.USER_BILL_TYPE_RECHARGE);
                typeList.add(Constants.USER_BILL_TYPE_SYSTEM_ADD);
                break;
            case 3:
                category = Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE;
                typeList.add(Constants.USER_BILL_TYPE_BROKERAGE);
                break;
            case 4:
                typeList.add(Constants.USER_BILL_TYPE_EXTRACT);
                break;
            default:
                break;

        }
        return userBillService.getListGroupByMonth(userService.getUserIdException(), typeList, pageParamRequest, category);
    }

    /**
     * 推广佣金/提现总和
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSpreadCountByType(int type) {
        //推广佣金/提现总和
        Integer userId = userService.getUserIdException();
        if(type == 3){
            BigDecimal count = userBillService.getSumBigDecimal(null, userId, Constants.USER_BILL_CATEGORY_MONEY, null, Constants.USER_BILL_TYPE_BROKERAGE);
            BigDecimal withdraw = userBillService.getSumBigDecimal(1, userId, Constants.USER_BILL_CATEGORY_MONEY, null, Constants.USER_BILL_TYPE_BROKERAGE); //提现
            return count.subtract(withdraw);
        }

        //累计提现
        if(type == 4){
            return userExtractService.getWithdrawn(null,null);
        }

        return BigDecimal.ZERO;
    }

    /**
     * 提现申请
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return Boolean
     */
    @Override
    public Boolean extractCash(UserExtractRequest request) {
        return userExtractService.create(request, userService.getUserIdException());
    }

    /**
     * 提现银行/提现最低金额
     * @author Mr.Zhang
     * @since 2020-06-08
     * @return UserExtractCashResponse
     */
    @Override
    public UserExtractCashResponse minExtractCash() {
        String bank = systemConfigService.getValueByKeyException(Constants.CONFIG_BANK_LIST).replace("\r\n", "\n");
        String minPrice = systemConfigService.getValueByKeyException(Constants.CONFIG_EXTRACT_MIN_PRICE);

        String extractTime = systemConfigService.getValueByKey(Constants.CONFIG_EXTRACT_FREEZING_TIME);

        BigDecimal brokeragePrice = userService.getInfo().getBrokeragePrice();
        BigDecimal freeze = userExtractService.getFreeze(userService.getUserIdException());
        List<String> bankArr = new ArrayList<>();
        if(bank.indexOf("\n") > 0){
            for (String s : bank.split("\n")) {
                bankArr.add(s);
            }
        }else{
            bankArr.add(bank);
        }
        return new UserExtractCashResponse(
                bankArr,
                minPrice,
                brokeragePrice.subtract(freeze),
                freeze,
                extractTime
        );
    }

    /**
     * 会员等级列表
     * @author Mr.Zhang
     * @since 2020-06-19
     * @return List<UserLevel>
     */
    @Override
    public List<SystemUserLevel> getUserLevelList() {
        SystemUserLevelSearchRequest request = new SystemUserLevelSearchRequest();
        request.setIsShow(true);
        request.setIsDel(false);
        return systemUserLevelService.getList(request, new PageParamRequest());
    }

    /**
     * 推广用户， 我自己推广了哪些用户
     * @author Mr.Zhang
     * @since 2020-05-18
     * @return UserSpreadPeopleResponse
     */
    @Override
    public UserSpreadPeopleResponse getSpreadPeopleList(UserSpreadPeopleRequest request, PageParamRequest pageParamRequest) {
        //查询当前用户名下的一级推广员
        UserSpreadPeopleResponse userSpreadPeopleResponse = new UserSpreadPeopleResponse();
        List<Integer> userIdList = new ArrayList<>();
        userIdList.add(userService.getUserIdException());
        userIdList = userService.getSpreadPeopleIdList(userIdList); //我推广的一级用户id集合

        if (CollUtil.isEmpty(userIdList)) {//如果没有一级推广人，直接返回
            userSpreadPeopleResponse.setCount(0);
            userSpreadPeopleResponse.setTotal(0);
            userSpreadPeopleResponse.setTotalLevel(0);
            return userSpreadPeopleResponse;
        }

        userSpreadPeopleResponse.setTotal(userIdList.size()); //一级推广人
        //查询二级推广人
        List<Integer> secondSpreadIdList = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(userIdList)) {
            secondSpreadIdList = userService.getSpreadPeopleIdList(userIdList);
        }
        userSpreadPeopleResponse.setTotalLevel(secondSpreadIdList.size());
        userSpreadPeopleResponse.setCount(userIdList.size() + secondSpreadIdList.size());

        if(request.getGrade() == 1){
            //二级推广人
//            userIdList.addAll(userService.getSpreadPeopleIdList(userIdList));
//            userSpreadPeopleResponse.setTotalLevel(userIdList.size()); //把二级推广人的id追加到一级推广人集合中
            userIdList.clear();
            userIdList.addAll(secondSpreadIdList);
        }

        if(userIdList.size() > 0){
            List<UserSpreadPeopleItemResponse> userSpreadPeopleItemResponseList = userService.getSpreadPeopleList(userIdList, request.getKeyword(), request.getSortKey(), request.getIsAsc(), pageParamRequest);
            userSpreadPeopleResponse.setSpreadPeopleList(userSpreadPeopleItemResponseList);
        }

        return userSpreadPeopleResponse;
    }

    /**
     * 积分记录
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return List<UserBill>
     */
    @Override
    public List<UserBill> getUserBillList(String category, PageParamRequest pageParamRequest) {
        FundsMonitorSearchRequest request = new FundsMonitorSearchRequest();
        request.setUid(userService.getUserIdException());
        request.setCategory(category);
        return userBillService.getList(request, pageParamRequest);
    }

    /**
     * 充值额度选择
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return UserRechargeResponse
     */
    @Override
    public UserRechargeResponse getRechargeConfig() {
        UserRechargeResponse userRechargeResponse = new UserRechargeResponse();
        userRechargeResponse.setRechargeQuota(systemGroupDataService.getListByGid(Constants.GROUP_DATA_ID_RECHARGE_LIST, UserRechargeItemResponse.class));
        String rechargeAttention = systemConfigService.getValueByKey(Constants.CONFIG_RECHARGE_ATTENTION);
        List<String> rechargeAttentionList = new ArrayList<>();
        if(StringUtils.isNotBlank(rechargeAttention)){
            rechargeAttentionList = CrmebUtil.stringToArrayStrRegex(rechargeAttention, "\n");
        }
        userRechargeResponse.setRechargeAttention(rechargeAttentionList);
        return userRechargeResponse;
    }

    /**
     * 用户资金统计
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return UserBalanceResponse
     */
    @Override
    public UserBalanceResponse getUserBalance() {
        User info = userService.getInfo();
        BigDecimal recharge = userBillService.getSumBigDecimal(1, info.getUid(), Constants.USER_BILL_CATEGORY_MONEY, null, null);
        BigDecimal orderStatusSum = storeOrderService.getSumBigDecimal(info.getUid(), null);
        return new UserBalanceResponse(info.getNowMoney(), recharge, orderStatusSum);
    }

    /**
     * 推广订单
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return UserSpreadOrderResponse;
     */
    @Override
    public UserSpreadOrderResponse getSpreadOrder(PageParamRequest pageParamRequest) {
        UserSpreadOrderResponse userSpreadOrderResponse = new UserSpreadOrderResponse();
        Integer userId = userService.getUserIdException();
        List<Integer> userIdList = new ArrayList<>();
        userIdList.add(userId);
        userIdList = userService.getSpreadPeopleIdList(userIdList); //拿到一级推广id
        if(null == userIdList){
            return userSpreadOrderResponse;
        }
        //查询所有推广人的由于订单获取佣金记录，分页

        FundsMonitorSearchRequest request = new FundsMonitorSearchRequest();
//        request.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
//        request.setType(Constants.USER_BILL_TYPE_PAY_MONEY);
        request.setCategory(Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE);
        request.setType(Constants.USER_BILL_TYPE_BROKERAGE);
        request.setUserIdList(CollUtil.newArrayList(userId));
        request.setLinkId("gt");
        request.setPm(1);
        List<UserBill> list = userBillService.getList(request, pageParamRequest);
        if(null == list){
            return userSpreadOrderResponse;
        }
        CommonPage<UserBill> userBillCommonPage = CommonPage.restPage(list); //拿到分页信息
        userSpreadOrderResponse.setCount(userBillCommonPage.getTotal()); //总数

        //拿到订单id, 查询订单信息
        List<Integer> orderIdList = list.stream().map(i -> Integer.parseInt(i.getLinkId())).distinct().collect(Collectors.toList());
        Map<Integer, StoreOrder> orderList = storeOrderService.getMapInId(orderIdList);

        //用户信息
//        userIdList = list.stream().map(UserBill::getUid).distinct().collect(Collectors.toList());
        List<StoreOrder> storeOrderList = new ArrayList<>(orderList.values());
        userIdList = storeOrderList.stream().map(StoreOrder::getUid).distinct().collect(Collectors.toList());
        HashMap<Integer, User> userList = userService.getMapListInUid(userIdList);

        //按时间分组数据
        List<UserSpreadOrderItemResponse> userSpreadOrderItemResponseList = new ArrayList<>();
        for (UserBill userBill : list) {
            String date = DateUtil.dateToStr(userBill.getCreateTime(), Constants.DATE_FORMAT_MONTH);
            boolean isAdd = false;
            String orderId = "";
            Integer linkId = Integer.parseInt(userBill.getLinkId());
            if(null != orderList && orderList.containsKey(linkId)){
                orderId = orderList.get(linkId).getOrderId();
            }

            UserSpreadOrderItemChildResponse userSpreadOrderItemChildResponse = new UserSpreadOrderItemChildResponse(
                    orderId, //订单号
                    userBill.getCreateTime(),
                    (userBill.getStatus() == 1) ? userBill.getNumber() : BigDecimal.ZERO,
//                    userList.get(userBill.getUid()).getAvatar(),
//                    userList.get(userBill.getUid()).getNickname(),
                    userList.get(orderList.get(linkId).getUid()).getAvatar(),
                    userList.get(orderList.get(linkId).getUid()).getNickname(),
                    userBill.getType()
            );

            //如果在已有的数据中找到当前月份数据则追加
            for (UserSpreadOrderItemResponse userSpreadOrderItemResponse: userSpreadOrderItemResponseList) {
                if(userSpreadOrderItemResponse.getTime().equals(date)){
                    userSpreadOrderItemResponse.getChild().add(userSpreadOrderItemChildResponse);
                    isAdd = true;
                    break;
                }
            }

            //没月找到则创建一条数据
            if(!isAdd){
                //创建一个
                UserSpreadOrderItemResponse userSpreadOrderItemResponse = new UserSpreadOrderItemResponse();
                userSpreadOrderItemResponse.setTime(date);
                userSpreadOrderItemResponse.getChild().add(userSpreadOrderItemChildResponse);
                userSpreadOrderItemResponseList.add(userSpreadOrderItemResponse);
            }
        }

        List<String> monthList = userSpreadOrderItemResponseList.stream().map(s -> "'" + s.getTime() + "'").collect(Collectors.toList());

        if(monthList.size() < 1){
            return userSpreadOrderResponse;
        }

        //获取每个月分的总数
        Map<String, Integer> countMap = userBillService.getCountListByMonth(request, monthList);

        //统计每月的订单数量
        for (UserSpreadOrderItemResponse userSpreadOrderItemResponse: userSpreadOrderItemResponseList) {
            userSpreadOrderItemResponse.setCount(countMap.get(userSpreadOrderItemResponse.getTime()));
        }

        userSpreadOrderResponse.setList(userSpreadOrderItemResponseList);

        return userSpreadOrderResponse;
    }

    /**
     * 充值
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return UserSpreadOrderResponse;
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public UserRechargePaymentResponse recharge(UserRechargeRequest request) {
        request.setPayType(Constants.PAY_TYPE_WE_CHAT);

        //验证金额是否为最低金额
        String rechargeMinAmountStr = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_RECHARGE_MIN_AMOUNT);
        BigDecimal rechargeMinAmount = new BigDecimal(rechargeMinAmountStr);
        int compareResult = rechargeMinAmount.compareTo(request.getPrice());
        if(compareResult > 0){
            throw new CrmebException("充值金额不能低于" + rechargeMinAmountStr);
        }
        request.setPrice(request.getPrice());
        request.setGivePrice(BigDecimal.ZERO);

        if(request.getGroupDataId() > 0){
            SystemGroupDataRechargeConfigVo systemGroupData = systemGroupDataService.getNormalInfo(request.getGroupDataId(), SystemGroupDataRechargeConfigVo.class);
            if(null == systemGroupData){
                throw new CrmebException("您选择的充值方式已下架");
            }

            //售价和赠送
            request.setPrice(systemGroupData.getPrice());
            request.setGivePrice(systemGroupData.getGiveMoney());

        }
        User currentUser = userService.getInfoException();
        //生成系统订单
        request.setUserId(currentUser.getUid());

        UserRecharge userRecharge = userRechargeService.create(request);

        //微信支付
        try{
            CreateOrderResponseVo responseVo = rechargePayService.payOrder(userRecharge.getId(), request.getPayType(), request.getClientIp());
            if(null == responseVo){
                throw new CrmebException("下单失败！");
            }
            // 小程序订阅通知 充值成功通知
            WechatSendMessageForTopped topped = new WechatSendMessageForTopped(
                    userRecharge.getOrderId(),userRecharge.getOrderId(),userRecharge.getPrice()+"",
                    currentUser.getNowMoney()+"",userRecharge.getCreateTime()+"", userRecharge.getGivePrice()+"",
                    "暂无",userRecharge.getPrice()+"","CRMEB","微信"
            );
            wechatSendMessageForMinService.sendToppedMessage(topped, currentUser.getUid());

            return weChatService.response(responseVo);
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }
    }

    /**
     * 微信登录
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return UserSpreadOrderResponse;
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public LoginResponse weChatAuthorizeLogin(String code, Integer spreadUid) {
        try{
            System.out.println("code = " + code);
            WeChatAuthorizeLoginGetOpenIdResponse response = weChatService.authorizeLogin(code);
            System.out.println("response = " + response);
            User user = publicLogin(response.getOpenId(), response.getAccessToken());
            System.out.println("user = " + user);
            //通过用户id获取登录token信息
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(userService.token(user));
            user.setPwd(null);
            //绑定推广关系
            userService.bindSpread(user, spreadUid);
            loginResponse.setUser(user);

            return loginResponse;
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }
    }

    /**
     * 通过openId登录
     * @param openId  String 微信公众号通过code取回的openId对象
     * @param token String code换取的token
     * @author Mr.Zhang
     * @since 2020-05-25
     * @return List<LoginResponse>
     */
    private User publicLogin(String openId, String token) {
        try {
            //检测是否存在
            System.out.println("openId = " + openId);
            System.out.println("token = " + token);
            UserToken userToken = userTokenService.checkToken(openId,  Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC);
            System.out.println("userToken = " + userToken);
            if(null != userToken){
                return userService.getById(userToken.getUid());
            }

            //没有注册， 获取微信用户信息， 小程序需要前端传入用户信息参数
            WeChatAuthorizeLoginUserInfoResponse userInfo = weChatService.getUserInfo(openId, token);
            System.out.println("userInfo = " + userInfo);
            RegisterThirdUserRequest registerThirdUserRequest = new RegisterThirdUserRequest();
            BeanUtils.copyProperties(userInfo, registerThirdUserRequest);
            System.out.println("registerThirdUserRequest = " + registerThirdUserRequest);
            String unionId = userInfo.getUnionId();

            //看unionid是否已经绑定
            if(StringUtils.isNotBlank(unionId)){
                userToken = userTokenService.checkToken(userInfo.getUnionId(), Constants.THIRD_LOGIN_TOKEN_TYPE_UNION_ID);
                if(null != userToken){
                    return userService.getById(userToken.getUid());
                }
            }

            //TODO 是否需要强制注册用户，1 强制注册，2 需要返回数据给前端，让其输入手机号和验证码
            User user = userService.registerByThird(registerThirdUserRequest, Constants.USER_LOGIN_TYPE_PUBLIC);

            userTokenService.bind(openId, Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC, user.getUid());
            if(StringUtils.isNotBlank(unionId)) {
                //有就绑定
                userTokenService.bind(unionId, Constants.THIRD_LOGIN_TOKEN_TYPE_UNION_ID, user.getUid());
            }
            return user;
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }
    }

    /**
     * 获取微信授权logo
     * @author Mr.Zhang
     * @since 2020-06-29
     * @return String;
     */
    @Override
    public String getLogo() {
        String url = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SITE_LOGO);
//        if(StringUtils.isNotBlank(url) && !url.contains("http")){
//            url = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SITE_URL) + url;
//            url = url.replace("\\", "/");
//        }
        return url;
    }

    /**
     * 小程序登录
     * @param code String 前端临时授权code
     * @param request RegisterThirdUserRequest 用户信息
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return UserSpreadOrderResponse;
     */
    @Override
    public LoginResponse weChatAuthorizeProgramLogin(String code, RegisterThirdUserRequest request) {
        try{
            WeChatProgramAuthorizeLoginGetOpenIdResponse response = weChatService.programAuthorizeLogin(code);
            System.out.println("小程序登陆成功 = " + JSON.toJSONString(response));
            User user = programLogin(response.getOpenId(), response.getUnionId(), request);
            //通过用户id获取登录token信息
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(userService.token(user));
            user.setPwd(null);
            //绑定推广关系
            userService.bindSpread(user, request.getSpreadPid());
            loginResponse.setUser(user);

            return loginResponse;
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }
    }

    /**
     * 推广人排行榜
     * @param type  String 时间范围
     * @param pageParamRequest PageParamRequest 分页
     * @author Mr.Zhang
     * @since 2020-05-25
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
     * @author Mr.Zhang
     * @since 2020-05-25
     * @return List<LoginResponse>
     */
    @Override
    public List<User> getTopBrokerageListByDate(String type, PageParamRequest pageParamRequest) {
        List<UserBill> userBillVoList = userBillService.getTopBrokerageListByDate(type, pageParamRequest);
        if(userBillVoList.size() < 1){
            return null;
        }

        List<Integer> uidList = userBillVoList.stream().map(UserBill::getUid).collect(Collectors.toList());
        if(uidList.size() < 1){
            return null;
        }

        ArrayList<User> userList = new ArrayList<>();
        //查询用户
        HashMap<Integer, User> userVoList = userService.getMapListInUid(uidList);

        //解决排序问题
        for (UserBill userBillVo: userBillVoList) {
            User user = new User();
            User userVo = userVoList.get(userBillVo.getUid());

            user.setUid(userBillVo.getUid());
            user.setAvatar(userVo.getAvatar());
            user.setBrokeragePrice(userBillVo.getNumber());
            if(StringUtils.isBlank(userVo.getNickname())){
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
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return List<SystemGroupData>
     */
    @Override
    public List<UserSpreadBannerResponse> getSpreadBannerList(PageParamRequest pageParamRequest) {
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

        List<UserBill> userBillVoList = userBillService.getTopBrokerageListByDate(type, pageParamRequest);
        if(userBillVoList.size() < 1){
            return number;
        }

        List<Integer> uidList = userBillVoList.stream().map(UserBill::getUid).collect(Collectors.toList());
        if(uidList.size() < 1){
            return number;
        }

        int i = 1;
        for (UserBill userBill : userBillVoList) {
            if(userBill.getUid().equals(userId)){
                number = i;
                break;
            }
            i++;
        }

        return number;
    }

    /**
     * 佣金转入余额
     * @author Mr.Zhang
     * @since 2020-05-18
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public Boolean transferIn(BigDecimal price) {
        try{
            //当前可提现佣金
            User user = userService.getInfo();
            BigDecimal freeze = userExtractService.getFreeze(user.getUid()); //冻结佣金
            BigDecimal subtract = user.getBrokeragePrice().subtract(freeze);

            if(subtract.compareTo(price) < 0){
                throw new CrmebException("您当前可充值余额为 " + subtract + "元");
            }

            UserOperateFundsRequest request = new UserOperateFundsRequest();
            request.setFoundsCategory(Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE);
            request.setType(0);
            request.setFoundsType(Constants.USER_BILL_TYPE_TRANSFER_IN);
            request.setUid(user.getUid());
            request.setValue(price);
            request.setTitle("佣金转余额");
            userService.updateFounds(request, true); //更新佣金


            UserOperateFundsRequest money = new UserOperateFundsRequest();
            money.setFoundsCategory(Constants.USER_BILL_CATEGORY_MONEY);
            money.setType(1);
            money.setFoundsType(Constants.USER_BILL_TYPE_TRANSFER_IN);
            money.setUid(user.getUid());
            money.setValue(price);
            money.setTitle("佣金转余额");
            userService.updateFounds(money, true); //更新佣金
            return true;
        }catch (Exception e){
            throw new CrmebException("操作失败：" + e.getMessage());
        }

    }

    /**
     * 小程序登录
     * @param openId  String 微信公众号通过code取回的openId对象
     * @param unionId String unionId
     * @author Mr.Zhang
     * @since 2020-05-25
     * @return List<LoginResponse>
     */
    private User programLogin(String openId, String unionId, RegisterThirdUserRequest request) {
        try {
            //检测是否存在
            UserToken userToken = userTokenService.checkToken(openId, Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM);
            if(null != userToken){
                return userService.getById(userToken.getUid());
            }

            if(StringUtils.isNotBlank(unionId)) {
                userToken = userTokenService.checkToken(unionId, Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM);
                if (null != userToken) {
                    return userService.getById(userToken.getUid());
                }
            }

            //TODO 是否需要强制注册用户，1 强制注册，2 需要返回数据给前端，让其输入手机号和验证码
            User user = userService.registerByThird(request, Constants.USER_LOGIN_TYPE_PROGRAM);

            userTokenService.bind(openId, Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM, user.getUid());
            if(StringUtils.isNotBlank(unionId)) {
                //有就绑定
                userTokenService.bind(unionId, Constants.THIRD_LOGIN_TOKEN_TYPE_UNION_ID, user.getUid());
            }
            return user;
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }
    }

    /**
     * 提现记录
     * @author HZE
     * @since 2020-10-27
     * @return
     */
    @Override
    public PageInfo<UserExtractRecordResponse> getExtractRecord(PageParamRequest pageParamRequest) {
        return userExtractService.getExtractRecord(userService.getUserIdException(), pageParamRequest);
    }

    /**
     * 提现总金额
     * @author HZE
     * @since 2020-10-27
     * @return
     */
    @Override
    public BigDecimal getExtractTotalMoney(){
        return userExtractService.getExtractTotalMoney(userService.getUserIdException());
    }
}
