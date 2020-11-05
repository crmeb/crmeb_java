package com.zbkj.crmeb.express.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.express.model.ShippingTemplates;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.express.request.ShippingTemplatesRequest;
import com.zbkj.crmeb.express.request.ShippingTemplatesSearchRequest;

import java.util.List;

/**
* ShippingTemplatesService 接口
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
public interface ShippingTemplatesService extends IService<ShippingTemplates> {

    List<ShippingTemplates> getList(ShippingTemplatesSearchRequest request, PageParamRequest pageParamRequest);

    void checkExpressTemp(Integer tempId);

    boolean create(ShippingTemplatesRequest request);

    boolean update(Integer id, ShippingTemplatesRequest request);

    boolean remove(Integer id);

    /**
     * 根据模版id集合获取订单集合
     * @param ids 模版ids
     * @return 模版集合
     */
    List<ShippingTemplates> getListInIds(List<Integer> ids);
}
