package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.FundsMonitorRequest;
import com.zbkj.common.request.FundsMonitorSearchRequest;
import com.zbkj.common.response.MonitorResponse;
import com.zbkj.common.request.StoreOrderRefundRequest;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBill;

import java.math.BigDecimal;
import java.util.List;

/**
 * UserBillService 接口实现
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
public interface UserBillService extends IService<UserBill> {

    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<UserBill>
     */
    List<UserBill> getList(FundsMonitorSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增/消耗  总金额
     *
     * @param pm       Integer 0 = 支出 1 = 获得
     * @param userId   Integer 用户uid
     * @param category String 类型
     * @param date     String 时间范围
     * @param type     String 小类型
     * @return UserBill
     */
    BigDecimal getSumBigDecimal(Integer pm, Integer userId, String category, String date, String type);

    /**
     * 保存退款日志
     *
     * @return boolean
     */
    Boolean saveRefundBill(StoreOrderRefundRequest request, User user);

    /**
     * 资金监控
     *
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<MonitorResponse> fundMonitoring(FundsMonitorRequest request, PageParamRequest pageParamRequest);

    /**
     * 用户账单记录（现金）
     *
     * @param uid  用户uid
     * @param type 记录类型：all-全部，expenditure-支出，income-收入
     * @return PageInfo
     */
    PageInfo<UserBill> nowMoneyBillRecord(Integer uid, String type, PageParamRequest pageRequest);
}
