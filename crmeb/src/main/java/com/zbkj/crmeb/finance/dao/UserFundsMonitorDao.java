package com.zbkj.crmeb.finance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.crmeb.finance.model.UserFundsMonitor;

import java.util.HashMap;
import java.util.List;

/**
 * 用户充值表 Mapper 接口
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
public interface UserFundsMonitorDao extends BaseMapper<UserFundsMonitor> {

    /**
     * 佣金列表
     * @author Mr.Zhang
     * @since 2020-04-28
     * @return List<User>
     */
    List<UserFundsMonitor> getFundsMonitor(HashMap<String, Object> map);
}
