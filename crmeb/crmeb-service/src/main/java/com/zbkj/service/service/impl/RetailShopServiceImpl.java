package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBrokerageRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.RetailShopRequest;
import com.zbkj.common.request.RetailShopSearchRequest;
import com.zbkj.common.response.SpreadUserResponse;
import com.zbkj.common.response.UserExtractResponse;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.service.dao.UserDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RetailShopServiceImpl 接口实现 分销业务实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
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
     * @param request 分销员分页列表查询请求对象
     */
    @Override
    public PageInfo<SpreadUserResponse> getSpreadPeopleList(RetailShopSearchRequest request) {
        // id,头像，昵称，姓名，电话，推广用户数，推广订单数，推广订单额，佣金总金额，已提现金额，提现次数，未提现金额，上级推广人
        PageInfo<User> userPageInfo = userService.getAdminSpreadPeopleList(request);

        if (CollUtil.isEmpty(userPageInfo.getList())) {
            return CommonPage.copyPageInfo(userPageInfo, CollUtil.newArrayList());
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
                userResponse.setSpreadNickname(Optional.ofNullable(spreadUser.getNickname()).orElse("--"));
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
                // 冻结中佣金
                userResponse.setFreezeBrokeragePrice(BigDecimal.ZERO);
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
                // 冻结中佣金
                userResponse.setFreezeBrokeragePrice(userBrokerageRecordService.getFreezePrice(user.getUid()));
            }
            responseList.add(userResponse);
        });
        return CommonPage.copyPageInfo(userPageInfo, responseList);
    }

    /**
     * 获取分销配置信息
     * @return 返回配置信息
     */
    @Override
    public RetailShopRequest getManageInfo() {
        List<String> keys = CollUtil.newArrayList();
        keys.add(SysConfigConstants.CONFIG_KEY_BROKERAGE_FUNC_STATUS);
        keys.add(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_RATIO);
        keys.add(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_TWO);
        keys.add(SysConfigConstants.CONFIG_EXTRACT_MIN_PRICE);
        keys.add(SysConfigConstants.CONFIG_EXTRACT_BANK);
        keys.add(SysConfigConstants.CONFIG_EXTRACT_FREEZING_TIME);
        keys.add(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_QUOTA);
        keys.add(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_IS_BUBBLE);
        keys.add(SysConfigConstants.CONFIG_KEY_BROKERAGE_BINDIND);
        keys.add(SysConfigConstants.RETAIL_STORE_BROKERAGE_SHARE_NODE);
        MyRecord record = systemConfigService.getValuesByKeyList(keys);

        RetailShopRequest response = new RetailShopRequest();
        response.setBrokerageFuncStatus(Integer.parseInt(record.getStr(SysConfigConstants.CONFIG_KEY_BROKERAGE_FUNC_STATUS)));
        response.setStoreBrokerageRatio(Integer.parseInt(record.getStr(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_RATIO)));
        response.setStoreBrokerageTwo(Integer.parseInt(record.getStr(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_TWO)));
        response.setUserExtractMinPrice(new BigDecimal(record.getStr(SysConfigConstants.CONFIG_EXTRACT_MIN_PRICE)));
        response.setUserExtractBank(record.getStr(SysConfigConstants.CONFIG_EXTRACT_BANK).replace("\\n","\n"));
        response.setExtractTime(Integer.parseInt(record.getStr(SysConfigConstants.CONFIG_EXTRACT_FREEZING_TIME)));
        response.setStoreBrokerageQuota(Integer.parseInt(record.getStr(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_QUOTA)));
        response.setStoreBrokerageIsBubble(Integer.parseInt(record.getStr(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_IS_BUBBLE)));
        response.setBrokerageBindind(Integer.parseInt(record.getStr(SysConfigConstants.CONFIG_KEY_BROKERAGE_BINDIND)));
        response.setStoreBrokerageShareNode(record.getStr(SysConfigConstants.RETAIL_STORE_BROKERAGE_SHARE_NODE));
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

        systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_BROKERAGE_FUNC_STATUS, retailShopRequest.getBrokerageFuncStatus().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_RATIO, retailShopRequest.getStoreBrokerageRatio().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_TWO, retailShopRequest.getStoreBrokerageTwo().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_EXTRACT_MIN_PRICE, retailShopRequest.getUserExtractMinPrice().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_EXTRACT_BANK, retailShopRequest.getUserExtractBank());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_EXTRACT_FREEZING_TIME, retailShopRequest.getExtractTime().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_BROKERAGE_BINDIND, retailShopRequest.getBrokerageBindind().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_QUOTA, retailShopRequest.getStoreBrokerageQuota().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_IS_BUBBLE, retailShopRequest.getStoreBrokerageIsBubble().toString());
        systemConfigService.updateOrSaveValueByName(SysConfigConstants.RETAIL_STORE_BROKERAGE_SHARE_NODE, retailShopRequest.getStoreBrokerageShareNode());
        return true;
    }

}
