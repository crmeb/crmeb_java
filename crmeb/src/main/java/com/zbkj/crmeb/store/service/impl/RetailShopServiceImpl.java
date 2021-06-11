package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.model.UserExtract;
import com.zbkj.crmeb.finance.response.UserExtractResponse;
import com.zbkj.crmeb.finance.service.UserExtractService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.RetailShopRequest;
import com.zbkj.crmeb.store.response.RetailShopStatisticsResponse;
import com.zbkj.crmeb.store.service.RetailShopService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.dao.UserDao;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBrokerageRecord;
import com.zbkj.crmeb.user.response.SpreadUserResponse;
import com.zbkj.crmeb.user.service.UserBrokerageRecordService;
import com.zbkj.crmeb.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private SystemConfigService systemConfigService;

    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;

    /**
     * 获取分销列表
     * @param keywords 搜索参数
     * @param dateLimit 时间参数
     * @param pageRequest 分页参数
     * @return
     */
    @Override
    public CommonPage<SpreadUserResponse> getSpreadPeopleList(String keywords, String dateLimit, PageParamRequest pageRequest) {
        // 获取分销模式 storeBrokerageStatus 1-指定分销，2-人人分销
        String storeBrokerageStatus = systemConfigService.getValueByKeyException("store_brokerage_status");
        Page<User> pageUserPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        // id,头像，昵称，姓名，电话，推广用户数，推广订单数，推广订单额，佣金总金额，已提现金额，提现次数，未提现金额，上级推广人
        PageInfo<User> userPageInfo = userService.getAdminSpreadPeopleList(storeBrokerageStatus, keywords, dateLimit, pageRequest);

        if (CollUtil.isEmpty(userPageInfo.getList())) {
            return CommonPage.restPage(new PageInfo<>());
//            return CommonPage.restPage(userPageInfo);
        }
        List<User> userList = userPageInfo.getList();
        List<SpreadUserResponse> responseList = CollUtil.newArrayList();
        userList.forEach(user -> {
            SpreadUserResponse userResponse = new SpreadUserResponse();
            BeanUtils.copyProperties(user, userResponse);
            // 上级推广员名称
            userResponse.setSpreadNickname("无");
            if (ObjectUtil.isNotNull(user.getSpreadUid()) && user.getSpreadUid() > 0) {
                User spreadUser = userService.getById(user.getSpreadUid());
                userResponse.setSpreadNickname(Optional.ofNullable(spreadUser.getNickname()).orElse(""));
            }

            List<UserBrokerageRecord> recordList = userBrokerageRecordService.getSpreadListByUid(user.getUid());
            if (CollUtil.isEmpty(recordList)) {
                // 推广订单数
                userResponse.setSpreadOrderNum(0);
                // 推广订单额
                userResponse.setSpreadOrderTotalPrice(BigDecimal.ZERO);
                // 佣金总金额
                userResponse.setTotalBrokeragePrice(BigDecimal.ZERO);
                // 已提现金额
                userResponse.setExtractCountPrice(BigDecimal.ZERO);
                // 提现次数
                userResponse.setExtractCountNum(0);
            } else {
                // 推广订单数
                userResponse.setSpreadOrderNum(recordList.size());
                // 佣金总金额
                userResponse.setTotalBrokeragePrice(recordList.stream().map(UserBrokerageRecord::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
                // 推广订单额
                List<String> orderNoList = recordList.stream().map(UserBrokerageRecord::getLinkId).collect(Collectors.toList());
                BigDecimal spreadOrderTotalPrice = storeOrderService.getSpreadOrderTotalPriceByOrderList(orderNoList);
                userResponse.setSpreadOrderTotalPrice(spreadOrderTotalPrice);

                UserExtractResponse extractResponse = userExtractService.getUserExtractByUserId(user.getUid());
                // 已提现金额
                userResponse.setExtractCountPrice(extractResponse.getExtractCountPrice());
                // 提现次数
                userResponse.setExtractCountNum(extractResponse.getExtractCountNum());
            }
            responseList.add(userResponse);
        });
        PageInfo<SpreadUserResponse> responsePageInfo = CommonPage.copyPageInfo(pageUserPage, responseList);
        responsePageInfo.setTotal(userPageInfo.getTotal());
        responsePageInfo.setPages(userPageInfo.getPages());
        return CommonPage.restPage(responsePageInfo);
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
        response.setBrokerageBindind(systemConfigService.getValueByKey(keys.get(7)));
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
//        systemConfigService.updateOrSaveValueByName(keys.get(7), retailShopRequest.getStoreBrokeragePrice().toString());
        systemConfigService.updateOrSaveValueByName(keys.get(7), retailShopRequest.getBrokerageBindind());
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

        // 获取分销模式 storeBrokerageStatus 1-指定分销，2-人人分销
        String storeBrokerageStatus = systemConfigService.getValueByKeyException("store_brokerage_status");

        // 获取分销人数
        List<User> userDisList = userService.findDistributionList(keywords, dateLimit, storeBrokerageStatus);
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
        List<UserExtract> extractList = userExtractService.getListByUserIds(ids);
        extractList = extractList.stream().filter(e -> e.getStatus().equals(1)).collect(Collectors.toList());
        Integer withdrawCount = CollUtil.isEmpty(extractList) ? 0 : extractList.size();
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
//        keys.add("store_brokerage_price");
        keys.add("brokerage_bindind");
        return keys;
    }
}
