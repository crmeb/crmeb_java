package com.zbkj.crmeb.express.dao;

import com.zbkj.crmeb.express.model.ShippingTemplatesFree;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.crmeb.express.request.ShippingTemplatesFreeRequest;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-17
 */
public interface ShippingTemplatesFreeDao extends BaseMapper<ShippingTemplatesFree> {

    List<ShippingTemplatesFreeRequest> getListGroup(Integer tempId);
}
