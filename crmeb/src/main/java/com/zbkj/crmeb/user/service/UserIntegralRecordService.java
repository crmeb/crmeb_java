package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.user.model.UserIntegralRecord;
import com.zbkj.crmeb.user.request.AdminIntegralSearchRequest;
import com.zbkj.crmeb.user.response.UserIntegralRecordResponse;

import java.util.List;

/**
 * 用户积分记录Service
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
public interface UserIntegralRecordService extends IService<UserIntegralRecord> {

    /**
     * 根据订单编号、uid获取记录列表
     * @param orderNo 订单编号
     * @param uid 用户uid
     * @return 记录列表
     */
    List<UserIntegralRecord> findListByOrderIdAndUid(String orderNo, Integer uid);

    /**
     * 积分解冻
     */
    void integralThaw();

    /**
     * PC后台列表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    PageInfo<UserIntegralRecordResponse> findAdminList(AdminIntegralSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据类型条件计算积分总数
     * @param uid 用户uid
     * @param type 类型：1-增加，2-扣减
     * @param date 日期
     * @param linkType 关联类型
     * @return 积分总数
     */
    Integer getSumIntegral(Integer uid, Integer type, String date, String linkType);

    /**
     * H5用户积分列表
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @return
     */
    List<UserIntegralRecord> findUserIntegralRecordList(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取用户冻结的积分
     * @param uid 用户uid
     * @return 积分数量
     */
    Integer getFrozenIntegralByUid(Integer uid);
}