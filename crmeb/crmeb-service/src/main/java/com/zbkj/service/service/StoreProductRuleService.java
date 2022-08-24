package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.model.product.StoreProductRule;
import com.zbkj.common.request.StoreProductRuleRequest;
import com.zbkj.common.request.StoreProductRuleSearchRequest;

import java.util.List;

/**
 * StoreProductRuleService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface StoreProductRuleService extends IService<StoreProductRule> {

    /**
     * 商品规格列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<StoreProductRule> getList(StoreProductRuleSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增商品规格
     * @param storeProductRuleRequest 规格参数
     * @return 新增结果
     */
    boolean save(StoreProductRuleRequest storeProductRuleRequest);

    /**
     * 修改规格
     * @param storeProductRuleRequest 规格参数
     * @return Boolean
     */
    Boolean updateRule(StoreProductRuleRequest storeProductRuleRequest);
}
