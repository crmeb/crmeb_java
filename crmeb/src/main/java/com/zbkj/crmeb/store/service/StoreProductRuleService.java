package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.model.StoreProductRule;
import com.zbkj.crmeb.store.request.StoreProductRuleRequest;
import com.zbkj.crmeb.store.request.StoreProductRuleSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @description StoreProductRuleService 接口
* @date 2020-05-27
*/
public interface StoreProductRuleService extends IService<StoreProductRule> {

    List<StoreProductRule> getList(StoreProductRuleSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增商品规格
     * @param storeProductRuleRequest 规格参数
     * @return 新增结果
     */
    boolean save(StoreProductRuleRequest storeProductRuleRequest);
}
