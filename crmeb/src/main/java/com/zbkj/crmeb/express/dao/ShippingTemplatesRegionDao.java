package com.zbkj.crmeb.express.dao;

import com.zbkj.crmeb.express.model.ShippingTemplatesRegion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.crmeb.express.request.ShippingTemplatesRegionRequest;

import java.util.List;

/**
 *  Mapper 接口
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
public interface ShippingTemplatesRegionDao extends BaseMapper<ShippingTemplatesRegion> {

    List<ShippingTemplatesRegionRequest> getListGroup(Integer tempId);
}
