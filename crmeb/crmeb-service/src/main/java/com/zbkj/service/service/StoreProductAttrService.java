package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.product.StoreProductAttr;

import java.util.List;

/**
 * StoreProductAttrService 接口
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
public interface StoreProductAttrService extends IService<StoreProductAttr> {

    /**
     * 根据基本属性查询商品属性详情
     * @param storeProductAttr 商品属性
     * @return 查询商品属性集合
     */
    List<StoreProductAttr> getByEntity(StoreProductAttr storeProductAttr);

    /**
     * 根据id删除商品
     * @param productId 待删除商品id
     * @param type 类型区分是是否添加营销
     */
    void removeByProductId(Integer productId,int type);

    /**
     * 删除商品规格
     * @param productId 商品id
     * @param type 商品类型
     * @return Boolean
     */
    Boolean deleteByProductIdAndType(Integer productId, Integer type);

    /**
     * 获取商品规格列表
     * @param productId 商品id
     * @param type 商品类型
     * @return List
     */
    List<StoreProductAttr> getListByProductIdAndType(Integer productId, Integer type);
}
