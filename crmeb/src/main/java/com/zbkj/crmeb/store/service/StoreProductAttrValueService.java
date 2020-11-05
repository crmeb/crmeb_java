package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.store.model.StoreProductAttrValue;
import com.zbkj.crmeb.store.request.StoreProductAttrValueSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang edit by stivepeim 7-6
* @description StoreProductAttrValueService 接口
* @date 2020-05-27
*/
public interface StoreProductAttrValueService extends IService<StoreProductAttrValue> {

    /**
     * 商品属性分页李贝奥
     * @param request 条件参数
     * @param pageParamRequest 分页参数
     * @return 商品属性集合
     */
    List<StoreProductAttrValue> getList(StoreProductAttrValueSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据商品id获取
     * @param productId 商品id
     * @return 商品属性集合
     */
    List<StoreProductAttrValue> getListByProductId(Integer productId);

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
     * 加销量，减库存
     * @param productId 商品id
     * @param attrValueId 商品attrValue id
     * @param num 销售数量
     * @param type 是否限购
     * @return
     */
    boolean decProductAttrStock(Integer productId, Integer attrValueId, Integer num, Integer type);

    /**
     * 根据商品删除attrValue
     * @param productId 商品id
     * @param type 类型区分是是否添加营销
     * @return 删除结果
     */
    boolean removeByProductId(Integer productId,int type);
}
