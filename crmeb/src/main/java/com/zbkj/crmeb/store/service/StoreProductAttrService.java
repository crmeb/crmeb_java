package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.request.StoreProductAttrSearchRequest;

import java.util.List;

/**
 * StoreProductAttrService 接口
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
public interface StoreProductAttrService extends IService<StoreProductAttr> {

    List<StoreProductAttr> getList(StoreProductAttrSearchRequest request, PageParamRequest pageParamRequest);

    Boolean batchSave(List<StoreProductAttr> storeProductAttrs);

    Boolean batchUpdate(List<StoreProductAttr> storeProductAttrs);

    /**
     * 根据基本属性查询商品属性详情
     * @param storeProductAttr 商品属性
     * @return 查询商品属性集合
     */
    List<StoreProductAttr> getByEntity(StoreProductAttr storeProductAttr);

    /**
     * 根据id查询商品属性详情
     * @param productId 商品id
     * @return 查询结果
     */
    List<StoreProductAttr> getByProductId(int productId);

    /**
     * 根据id删除商品
     * @param productId 待删除商品id
     * @param type 类型区分是是否添加营销
     */
    void removeByProductId(Integer productId,int type);

}
