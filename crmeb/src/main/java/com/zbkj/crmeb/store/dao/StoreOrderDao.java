package com.zbkj.crmeb.store.dao;

import com.zbkj.crmeb.store.model.StoreOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-28
 */
public interface StoreOrderDao extends BaseMapper<StoreOrder> {

    BigDecimal getTotalPrice(String where);

    BigDecimal getRefundPrice(String where);

    Integer getRefundTotal(String where);
}
