package com.zbkj.crmeb.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.finance.model.UserFundsMonitor;
import com.zbkj.crmeb.finance.request.FundsMonitorUserSearchRequest;
import com.zbkj.crmeb.user.model.UserBrokerageRecord;

import java.util.List;

/**
*  UserRechargeService 接口
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
public interface UserFundsMonitorService extends IService<UserFundsMonitor> {

    /**
     * 佣金列表
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return List<User>
     */
    List<UserFundsMonitor> getFundsMonitor(FundsMonitorUserSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 佣金详细记录
     * @param uid 用户uid
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return
     */
    PageInfo<UserBrokerageRecord> getFundsMonitorDetail(Integer uid, String dateLimit, PageParamRequest pageParamRequest);
}
