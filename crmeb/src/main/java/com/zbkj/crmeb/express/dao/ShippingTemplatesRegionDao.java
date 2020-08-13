package com.zbkj.crmeb.express.dao;

import com.zbkj.crmeb.express.model.ShippingTemplatesRegion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.crmeb.express.request.ShippingTemplatesRegionRequest;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-17
 */
public interface ShippingTemplatesRegionDao extends BaseMapper<ShippingTemplatesRegion> {

    List<ShippingTemplatesRegionRequest> getListGroup(Integer tempId);
}
