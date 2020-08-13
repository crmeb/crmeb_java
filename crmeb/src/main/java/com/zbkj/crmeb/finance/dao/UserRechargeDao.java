package com.zbkj.crmeb.finance.dao;

import com.zbkj.crmeb.finance.model.UserRecharge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;

/**
 * <p>
 * 用户充值表 Mapper 接口
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-11
 */
public interface UserRechargeDao extends BaseMapper<UserRecharge> {

    BigDecimal getSumByType(String type);
}
