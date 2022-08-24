package com.zbkj.service.dao;

import com.zbkj.common.model.finance.UserRecharge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;

/**
 * 用户充值表 Mapper 接口
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
public interface UserRechargeDao extends BaseMapper<UserRecharge> {

    /**
     * 根据类型获取该类型充值总金额
     * @param type  充值类型
     * @return      该类型充值总金额
     */
    BigDecimal getSumByType(String type);

    /**
     * 获取退款总金额
     * @return 退款总金额
     */
    BigDecimal getSumByRefund();
}
