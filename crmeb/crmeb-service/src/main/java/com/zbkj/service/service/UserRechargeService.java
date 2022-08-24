package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.UserRecharge;
import com.zbkj.common.request.UserRechargeSearchRequest;
import com.zbkj.common.response.UserRechargeResponse;

import java.math.BigDecimal;
import java.util.HashMap;

/**
* UserRechargeService 接口
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
public interface UserRechargeService extends IService<UserRecharge> {

    /**
     * 充值记录列表
     * @param request 请求参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserRechargeResponse> getList(UserRechargeSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 充值统计
     * @return HashMap
     */
    HashMap<String, BigDecimal> getBalanceList();

    UserRecharge getInfoByEntity(UserRecharge userRecharge);

    /**
     * 根据日期获取充值订单数量
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    Integer getRechargeOrderNumByDate(String date);

    /**
     * 根据日期获取充值订单金额
     * @param date 日期，yyyy-MM-dd格式
     * @return BigDecimal
     */
    BigDecimal getRechargeOrderAmountByDate(String date);

    /**
     * 获取总人数
     * @return Integer
     */
    Integer getTotalPeople();

    /**
     * 获取总金额
     * @return BigDecimal
     */
    BigDecimal getTotalPrice();

    /**
     * 根据时间获取充值用户数量
     * @param date 日期
     * @return Integer
     */
    Integer getRechargeUserNumByDate(String date);

    /**
     * 根据时间获取充值用户数量
     * @param startDate 日期
     * @param endDate 日期
     * @return Integer
     */
    Integer getRechargeUserNumByPeriod(String startDate, String endDate);
}
