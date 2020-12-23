package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.model.StoreProductAttrResult;
import com.zbkj.crmeb.store.request.StoreProductAttrResultSearchRequest;

import java.util.List;

/**
 * StoreProductAttrResultService 接口
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface StoreProductAttrResultService extends IService<StoreProductAttrResult> {

    List<StoreProductAttrResult> getList(StoreProductAttrResultSearchRequest request, PageParamRequest pageParamRequest);

    StoreProductAttrResult getByProductId(int productId);

    Integer updateByProductId(StoreProductAttrResult storeProductAttrResult);

    void deleteByProductId(int productId, int type);

    /**
     * 根据商品属性值集合查询
     * @param storeProductAttrResult 查询参数
     * @return  查询结果
     */
    List<StoreProductAttrResult> getByEntity(StoreProductAttrResult storeProductAttrResult);
}
