package com.zbkj.service.service;

import com.zbkj.common.response.*;

import java.util.List;

/**
 * UserStatisticsService 接口
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
public interface UserStatisticsService {

    /**
     * 获取总数据
     * @return UserTotalResponse
     */
    UserTotalResponse getTotalDate();

    /**
     * 用户概览数据
     * @param dateLimit 时间参数
     * @return UserOverviewResponse
     */
    UserOverviewResponse getOverview(String dateLimit);

    /**
     * 获取用户性别数据
     * @return List
     */
    List<UserSexDataResponse> getSexData();

    /**
     * 获取用户渠道数据
     * @return List
     */
    List<UserChannelDataResponse> getChannelData();

    /**
     * 获取用户区域数据
     * @return List
     */
    List<UserAreaDataResponse> getAreaData();

    /**
     * 用户概览数据列表（导出使用）
     * @param dateLimit 时间参数
     * @return UserOverviewResponse
     */
    List<UserOverviewDateResponse> getOverviewList(String dateLimit);

}
