package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.wechat.video.PayComponentProductSku;

import java.util.List;

/**
 *
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public interface PayComponentProductSkuService extends IService<PayComponentProductSku> {

    /**
     * 通过商品id删除数据
     * @param proId 商品id
     * @return Boolean
     */
    Boolean deleteByProId(Integer proId);

    /**
     * 通过商品id获取列表
     * @param proId 商品id
     * @return List
     */
    List<PayComponentProductSku> getListByProId(Integer proId);

    /**
     * 通过商品id，规格属性id获取对象
     * @param proId 商品id
     * @param attrValueId 规格属性id
     * @return PayComponentProductSku
     */
    PayComponentProductSku getByProIdAndAttrValueId(Integer proId, Integer attrValueId);

    /**
     * 添加/扣减库存
     * @param skuId skuId
     * @param num 数量
     * @param operationType 类型：add—添加，sub—扣减
     * @return Boolean
     */
    Boolean operationStock(Integer skuId, Integer num, String operationType, Integer version);
}
