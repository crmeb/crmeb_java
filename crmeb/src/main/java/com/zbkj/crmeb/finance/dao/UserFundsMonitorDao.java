package com.zbkj.crmeb.finance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.crmeb.finance.model.UserFundsMonitor;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 用户充值表 Mapper 接口
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-11
 */
public interface UserFundsMonitorDao extends BaseMapper<UserFundsMonitor> {
    List<UserFundsMonitor> getFundsMonitor(HashMap<String, Object> map);
}
