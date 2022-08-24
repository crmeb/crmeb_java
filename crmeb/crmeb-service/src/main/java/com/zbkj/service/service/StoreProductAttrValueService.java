package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.product.StoreProductAttrValue;

import java.util.List;

/**
 * StoreProductAttrValueService 接口
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
public interface StoreProductAttrValueService extends IService<StoreProductAttrValue> {

    /**
     * 根据商品id和attrId获取列表集合
     * @param productId 商品id
     * @param attrId 属性id
     * @param type 商品类型
     * @return 商品属性集合
     */
    List<StoreProductAttrValue> getListByProductIdAndAttrId(Integer productId, String attrId, Integer type);

    /**
     * 根据商品属性查询
     * @param storeProductAttrValue 商品属性参数
     * @return 商品属性结果
     */
    List<StoreProductAttrValue> getByEntity(StoreProductAttrValue storeProductAttrValue);

    /**
     * 根据商品删除attrValue
     * @param productId 商品id
     * @param type 类型区分是是否添加营销
     * @return 删除结果
     */
    boolean removeByProductId(Integer productId,int type);

    /**
     * 根据id、类型查询
     * @param id ID
     * @param type 类型
     * @return StoreProductAttrValue
     */
    StoreProductAttrValue getByIdAndProductIdAndType(Integer id, Integer productId, Integer type);

    /**
     * 根据sku查询
     * @param productId 商品id
     * @param suk   sku
     * @param type  规格类型
     * @return StoreProductAttrValue
     */
    StoreProductAttrValue getByProductIdAndSkuAndType(Integer productId, String suk, Integer type);

    /**
     * 添加/扣减库存
     * @param id 秒杀商品id
     * @param num 数量
     * @param operationType 类型：add—添加，sub—扣减
     * @param type 活动类型 0=商品，1=秒杀，2=砍价，3=拼团
     */
    Boolean operationStock(Integer id, Integer num, String operationType, Integer type);

    /**
     * 删除商品规格属性值
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
    List<StoreProductAttrValue> getListByProductIdAndType(Integer productId, Integer type);
}
