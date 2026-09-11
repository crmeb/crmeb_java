package com.zbkj.front.service;

import com.zbkj.common.response.MobileAdminManageStatisticsResponse;

/**
 * 移动端商家管理工作台服务
 */
public interface MobileAdminManageService {

    /**
     * 获取商家工作台统计数据。
     */
    MobileAdminManageStatisticsResponse getStatistics();
}
