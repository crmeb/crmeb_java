package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.express.ShippingTemplatesRegion;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ShippingTemplatesRegionRequest;

import java.util.List;

/**
* ShippingTemplatesRegionService 接口
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
public interface ShippingTemplatesRegionService extends IService<ShippingTemplatesRegion> {

    void saveAll(List<ShippingTemplatesRegionRequest> shippingTemplatesRegionRequestList, Integer type, Integer id);

    List<ShippingTemplatesRegionRequest> getListGroup(Integer tempId);

    /**
     * 删除
     * @param tempId 运费模板id
     * @return Boolean
     */
    Boolean delete(Integer tempId);

    /**
     * 根据模板编号、城市ID查询
     * @param tempId 模板编号
     * @param cityId 城市ID
     * @return 运费模板
     */
    ShippingTemplatesRegion getByTempIdAndCityId(Integer tempId, Integer cityId);
}
