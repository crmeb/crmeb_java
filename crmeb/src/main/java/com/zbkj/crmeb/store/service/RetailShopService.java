package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.store.request.RetailShopRequest;
import com.zbkj.crmeb.store.request.RetailShopStairUserRequest;
import com.zbkj.crmeb.store.response.RetailShopStatisticsResponse;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.response.SpreadUserResponse;
import com.zbkj.crmeb.user.response.UserResponse;

import java.util.List;

/**
 * 分销业务
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
public interface RetailShopService extends IService<User> {

    /**
     * 分销员列表
     * @param keywords 搜索参数
     * @param dateLimit 时间参数
     * @param pageRequest 分页参数
     * @return
     */
    CommonPage<SpreadUserResponse> getSpreadPeopleList(String keywords, String dateLimit, PageParamRequest pageRequest);

    /**
     * 获取分销头部数据
     * @param nickName 查询参数
     * @param dateLimit 时间参数对象
     */
    List<UserResponse> getStatisticsData(String nickName, String dateLimit);

    /**
     * 统计推广人员列表
     * @param request 查询参数
     * @return 推广人员集合列表
     */
    PageInfo<User> getStairUsers(RetailShopStairUserRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取分销配置
     * @return 分销配置信息
     */
    RetailShopRequest getManageInfo();

    /**
     * 保存或者更新分销配置信息
     * @param retailShopRequest 待保存数据
     * @return 保存结果
     */
    boolean setManageInfo(RetailShopRequest retailShopRequest);

    /**
     * 获取分销统计数据
     * @param keywords  模糊搜索参数
     * @param dateLimit 时间参数
     * @return
     */
    RetailShopStatisticsResponse getAdminStatistics(String keywords, String dateLimit);
}
