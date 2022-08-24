package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.SpreadCommissionDetailResponse;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.BrokerageRecordRequest;
import com.zbkj.common.request.RetailShopStairUserRequest;
import com.zbkj.common.model.user.UserBrokerageRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户佣金记录服务接口
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
public interface UserBrokerageRecordService extends IService<UserBrokerageRecord> {

    /**
     * 获取记录列表
     * @param linkId 关联id
     * @param linkType 关联类型
     * @return 记录列表
     */
    List<UserBrokerageRecord> findListByLinkIdAndLinkType(String linkId, String linkType);

    /**
     * 获取记录(订单不可用此方法)
     * @param linkId 关联id
     * @param linkType 关联类型
     * @return 记录列表
     */
    UserBrokerageRecord getByLinkIdAndLinkType(String linkId, String linkType);

    /**
     * 佣金解冻
     */
    void brokerageThaw();

    /**
     * 昨天得佣金
     * @param uid 用户uid
     */
    BigDecimal getYesterdayIncomes(Integer uid);

    /**
     * 获取佣金明细列表根据uid
     * @param uid uid
     * @param pageParamRequest 分页参数
     */
    PageInfo<SpreadCommissionDetailResponse> findDetailListByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取累计推广条数
     * @param uid 用户uid
     * @return Integer
     */
    Integer getSpreadCountByUid(Integer uid);

    /**
     * 获取推广记录列表
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<UserBrokerageRecord> findSpreadListByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取推广记录列表
     * @param request 用户uid
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserBrokerageRecord> findAdminSpreadListByUid(RetailShopStairUserRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取月份对应的推广订单数
     * @param uid 用户uid
     * @param monthList 月份列表
     * @return Map
     */
    Map<String, Integer> getSpreadCountByUidAndMonth(Integer uid, List<String> monthList);

    /**
     * 获取佣金排行榜（周、月）
     * @param type week、month
     * @return List
     */
    List<UserBrokerageRecord> getBrokerageTopByDate(String type);

    /**
     * 根据Uid和时间参数获取分佣记录列表
     * @param uid 用户uid
     * @return List
     */
    List<UserBrokerageRecord> getSpreadListByUid(Integer uid);

    /**
     * 佣金总金额（单位时间）
     * @param dateLimit 时间参数
     * @return BigDecimal
     */
    BigDecimal getTotalSpreadPriceBydateLimit(String dateLimit);

    /**
     * 单位时间消耗的佣金
     * @param dateLimit 时间参数
     * @return BigDecimal
     */
    BigDecimal getSubSpreadPriceByDateLimit(String dateLimit);

    /**
     * 获取冻结期佣金
     * @param uid uid
     * @return BigDecimal
     */
    BigDecimal getFreezePrice(Integer uid);

    /**
     * 佣金记录列表
     * @param request 筛选条件
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserBrokerageRecord> getAdminList(BrokerageRecordRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据日期获取支付佣金金额（确认到账佣金）
     * @param date 日期，yyyy-MM-dd格式
     * @return BigDecimal
     */
    BigDecimal getBrokerageAmountByDate(String date);

    /**
     * 获取累计佣金转余额金额
     * @return BigDecimal
     */
    BigDecimal getTotalYuePrice();
}
