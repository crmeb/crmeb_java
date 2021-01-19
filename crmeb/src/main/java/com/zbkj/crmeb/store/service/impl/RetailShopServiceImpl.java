package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.service.UserExtractService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.RetailShopRequest;
import com.zbkj.crmeb.store.request.RetailShopStairUserRequest;
import com.zbkj.crmeb.store.response.RetailShopStatisticsResponse;
import com.zbkj.crmeb.store.response.RetailShopUserResponse;
import com.zbkj.crmeb.store.service.RetailShopService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.dao.UserDao;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.request.UserSearchRequest;
import com.zbkj.crmeb.user.response.UserResponse;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RetailShopServiceImpl 接口实现 分销业务实现
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
public class RetailShopServiceImpl extends ServiceImpl<UserDao, User> implements RetailShopService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserExtractService userExtractService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取分销列表
     * @param keywords
     * @param dateLimit
     * @param pageRequest
     * @return
     */
    @Override
    public CommonPage<RetailShopUserResponse> getList(String keywords, String dateLimit, PageParamRequest pageRequest) {
        Page<User> pageUserPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());

//        User currentUser = userService.getUserByEntity();
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setStatus(true);
        userSearchRequest.setIsPromoter(true);
        userSearchRequest.setKeywords(keywords);
        userSearchRequest.setDateLimit(dateLimit);
        PageInfo<UserResponse> userResponses = userService.getList(userSearchRequest,pageRequest);
        List<RetailShopUserResponse> retailShopUserResponses = new ArrayList<>();
        for (UserResponse rp:userResponses.getList()) {
            RetailShopUserResponse nrp = new RetailShopUserResponse(
                    rp.getUid(),rp.getAccount(),rp.getPwd(),rp.getRealName(),rp.getBirthday(),rp.getCardId(),rp.getMark(),
                    rp.getPartnerId(),rp.getGroupId(),rp.getGroupName(),rp.getTagName(),rp.getNickname(),rp.getAvatar(),rp.getPhone(),
                    rp.getAddIp(),rp.getLastIp(),rp.getNowMoney(),rp.getBrokeragePrice(),rp.getIntegral(),rp.getExperience(),rp.getSignNum(),
                    rp.getStatus(),rp.getLevel(),rp.getSpreadUid(),rp.getSpreadTime(),rp.getSpreadNickname(),rp.getUserType(),rp.getIsPromoter(),
                    rp.getPayCount(),rp.getSpreadCount(),rp.getAddres(),rp.getAdminid(),rp.getLoginType(),rp.getUpdateTime(),rp.getCreateTime(),
                    rp.getLastLoginTime(),rp.getCleanTime(),null,null);
            retailShopUserResponses.add(nrp);
        }

        for (RetailShopUserResponse rShopUser:retailShopUserResponses) {
            // 推广用户数量
//            List<Integer> userIds = new ArrayList<>();
            List<Integer> userIds = userResponses.getList().stream().map(e -> {
                return e.getUid();
            }).collect(Collectors.toList());
//            userIds.add(currentUser.getUid());
//            rShopUser.setSpreadPeopleCount(userService.getSpreadPeopleIdList(userIds).size());
            rShopUser.setSpreadPeopleCount(userService.getSpreadPeopleIdList(userIds).size());
            // 获取提现数据
            rShopUser.setUserExtractResponse(userExtractService.getUserExtractByUserId(rShopUser.getUid()));
            // 获取订单数据
            rShopUser.setRetailShopOrderDataResponse(storeOrderService.getOrderDataByUserId(rShopUser.getUid()));
            // 佣金数据
            rShopUser.setBrokerageMoney(userBillService.getDataByUserId(rShopUser.getUid()));
        }
        PageInfo<RetailShopUserResponse> responsePageInfo = CommonPage.copyPageInfo(pageUserPage, retailShopUserResponses);
        responsePageInfo.setTotal(userResponses.getTotal());
        responsePageInfo.setPages(userResponses.getPages());
        return CommonPage.restPage(responsePageInfo);
    }

    /**
     * 获取分销头部数据
     * @param nickName 查询参数
     * @param dateLimit 时间参数对象
     */
    @Override
    public List<UserResponse> getStatisticsData(String nickName, String dateLimit) {
        UserSearchRequest request = new UserSearchRequest();
        request.setIsPromoter(true);
        request.setDateLimit(dateLimit);
        request.setKeywords(nickName);

        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(1);

        PageInfo<UserResponse> list = userService.getList(request, pageParamRequest);
        return list.getList();
    }

    /**
     * 统计推广人员列表
     * @param request 查询参数
     * @return 推广人员集合列表
     */
    @Override
    public PageInfo<User> getStairUsers(RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
        return userService.getUserListBySpreadLevel(request, pageParamRequest);
    }

    /**
     * 获取分销配置信息
     * @return 返回配置信息
     */
    @Override
    public RetailShopRequest getManageInfo() {
        List<String> keys = initKeys();
        RetailShopRequest response = new RetailShopRequest();
        response.setBrokerageFuncStatus(systemConfigService.getValueByKey(keys.get(0)));
        response.setStoreBrokerageStatus(systemConfigService.getValueByKey(keys.get(1)));
        response.setStoreBrokerageRatio(Integer.parseInt(systemConfigService.getValueByKey(keys.get(2))));
        response.setStoreBrokerageTwo(Integer.parseInt(systemConfigService.getValueByKey(keys.get(3))));
        response.setUserExtractMinPrice(new BigDecimal(systemConfigService.getValueByKey(keys.get(4))));
        response.setUserExtractBank(systemConfigService.getValueByKey(keys.get(5)).replace("\\n","\n"));
        response.setExtractTime(Integer.parseInt(systemConfigService.getValueByKey(keys.get(6))));
        response.setStoreBrokeragePrice(new BigDecimal(systemConfigService.getValueByKey(keys.get(7))));
        response.setBrokerageBindind(systemConfigService.getValueByKey(keys.get(8)));
        return response;
    }

    /**
     * 更新分销配置信息
     * @param retailShopRequest 待保存数据
     * @return 更新结果
     */
    @Override
    public boolean setManageInfo(RetailShopRequest retailShopRequest) {
        // 返佣比例之和+起来不能超过100%
        int ration = retailShopRequest.getStoreBrokerageTwo() + retailShopRequest.getStoreBrokerageRatio();
        if (ration > 100 || ration < 0) throw new CrmebException("返佣比例加起来不能超过100%");

        List<String> keys = initKeys();
        systemConfigService.updateOrSaveValueByName(keys.get(0), retailShopRequest.getBrokerageFuncStatus());
        systemConfigService.updateOrSaveValueByName(keys.get(1), retailShopRequest.getStoreBrokerageStatus());
        systemConfigService.updateOrSaveValueByName(keys.get(2), retailShopRequest.getStoreBrokerageRatio().toString());
        systemConfigService.updateOrSaveValueByName(keys.get(3), retailShopRequest.getStoreBrokerageTwo().toString());
        systemConfigService.updateOrSaveValueByName(keys.get(4), retailShopRequest.getUserExtractMinPrice().toString());
        systemConfigService.updateOrSaveValueByName(keys.get(5), retailShopRequest.getUserExtractBank());
        systemConfigService.updateOrSaveValueByName(keys.get(6), retailShopRequest.getExtractTime().toString());
        systemConfigService.updateOrSaveValueByName(keys.get(7), retailShopRequest.getStoreBrokeragePrice().toString());
        systemConfigService.updateOrSaveValueByName(keys.get(8), retailShopRequest.getBrokerageBindind());
        return true;
    }

    /**
     * 获取分销统计数据
     * @param keywords  模糊搜索参数
     * @param dateLimit 时间参数
     * @return RetailShopStatisticsResponse
     */
    @Override
    public RetailShopStatisticsResponse getAdminStatistics(String keywords, String dateLimit) {

        // 获取分销人数
        List<User> userDisList = userService.findDistributionList(keywords, dateLimit);
        if (CollUtil.isEmpty(userDisList)) {
            return new RetailShopStatisticsResponse(0, 0, 0, BigDecimal.ZERO, 0, BigDecimal.ZERO);
        }
        // 分销人员人数
        Integer distributionNum = userDisList.size();

        // 发展会员人数
        List<Integer> ids = userDisList.stream().map(User::getUid).collect(Collectors.toList());
        Integer developNum = userService.getDevelopDistributionPeopleNum(ids, dateLimit);

        List<StoreOrder> storeOrders = storeOrderService.getOrderByUserIdsForRetailShop(ids);
        // 订单总数
        Integer orderNum = storeOrders.size();
        // 订单金额
        BigDecimal orderPriceCount = storeOrders.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 提现次数
        Integer withdrawCount = userExtractService.getListByUserIds(ids).size();
        // 未提现金额
        BigDecimal noWithdrawPrice = userDisList.stream().map(User::getBrokeragePrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RetailShopStatisticsResponse(distributionNum, developNum, orderNum, orderPriceCount, withdrawCount, noWithdrawPrice);
    }

    ///////////////////////////////////////////////////// 自定义

    public List<String> initKeys(){
        List<String> keys = new ArrayList<>();
        keys.add("brokerage_func_status");
        keys.add("store_brokerage_status");
        keys.add("store_brokerage_ratio");
        keys.add("store_brokerage_two");
        keys.add("user_extract_min_price");
        keys.add("user_extract_bank");
        keys.add("extract_time");
        keys.add("store_brokerage_price");
        keys.add("brokerage_bindind");
        return keys;
    }
}
