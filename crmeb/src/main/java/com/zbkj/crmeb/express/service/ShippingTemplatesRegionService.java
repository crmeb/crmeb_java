package com.zbkj.crmeb.express.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.express.model.ShippingTemplatesRegion;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.express.request.ShippingTemplatesRegionRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description ShippingTemplatesRegionService 接口
* @since 2020-04-17
*/
public interface ShippingTemplatesRegionService extends IService<ShippingTemplatesRegion> {

    List<ShippingTemplatesRegion> getList(PageParamRequest pageParamRequest);

    /**
     * 根据ids和cityid查询
     * @param ids id集合
     * @param cityId 城市id
     * @return 运费模版集合
     */
    List<ShippingTemplatesRegion> getListInIdsAndCityId(List<Integer> ids,Integer cityId);

    void saveAll(List<ShippingTemplatesRegionRequest> shippingTemplatesRegionRequestList, Integer type, Integer id);

    List<ShippingTemplatesRegionRequest> getListGroup(Integer tempId);

    void delete(Integer tempId);
}