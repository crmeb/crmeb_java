package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.express.ShippingTemplatesFree;
import com.zbkj.common.request.ShippingTemplatesFreeRequest;
import com.zbkj.common.response.ShippingTemplatesFreeResponse;

import java.util.List;

/**
 * ShippingTemplatesFreeService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface ShippingTemplatesFreeService extends IService<ShippingTemplatesFree> {

    Boolean saveAll(List<ShippingTemplatesFreeRequest> shippingTemplatesFreeRequestList, Integer type, Integer id);

    List<ShippingTemplatesFreeResponse> getListGroup(Integer tempId);

    /**
     * 删除
     *
     * @param tempId 运费模板id
     */
    Boolean deleteByTempId(Integer tempId);

    /**
     * 根据模板编号、城市ID查询
     *
     * @param tempId 模板编号
     * @param cityId 城市ID
     * @return 运费模板
     */
    ShippingTemplatesFree getByTempIdAndCityId(Integer tempId, Integer cityId);
}
