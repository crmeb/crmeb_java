package com.zbkj.crmeb.express.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.express.model.ShippingTemplatesFree;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.express.request.ShippingTemplatesFreeRequest;

import java.util.List;

/**
* ShippingTemplatesFreeService 接口
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
public interface ShippingTemplatesFreeService extends IService<ShippingTemplatesFree> {

    List<ShippingTemplatesFree> getList(PageParamRequest pageParamRequest);

    /**
     * 根据模版id查询包邮信息
     * @param tempIds 模版id
     * @return 包邮信息集合
     */
    List<ShippingTemplatesFree> getListByTempIds(List<Integer> tempIds);

    /**
     * 根据条件查询
     * @param templatesFree 模版参数
     * @return 模版集合
     */
    List<ShippingTemplatesFree> getListByConditionForCalcOrderPrice(ShippingTemplatesFree templatesFree);

    void saveAll(List<ShippingTemplatesFreeRequest> shippingTemplatesFreeRequestList, Integer type, Integer id);

    List<ShippingTemplatesFreeRequest> getListGroup(Integer tempId);

    void delete(Integer id);
}
