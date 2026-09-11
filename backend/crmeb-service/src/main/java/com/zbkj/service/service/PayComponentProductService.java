package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.ComponentProductSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.PayComponentProductAddRequest;
import com.zbkj.common.response.PayComponentProductResponse;
import com.zbkj.common.response.ProductDetailResponse;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.wechat.video.PayComponentProduct;

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
public interface PayComponentProductService extends IService<PayComponentProduct> {

    /**
     * 添加商品
     * @param addRequest 商品请求参数
     * @return Boolean
     */
//    Boolean add(PayComponentProductAddRequest addRequest);

    /**
     * 删除商品
     * @param proId 商品id
     * @return Boolean
     */
    Boolean delete(Integer proId);

    /**
     * 更新商品
     * @param addRequest 商品请求参数
     * @return Boolean
     */
    Boolean update(PayComponentProductAddRequest addRequest);

    /**
     * 上架商品
     * @param proId 商品id
     * @return Boolean
     */
    Boolean listing(Integer proId);

    /**
     * 下架商品
     * @param proId 商品id
     * @return Boolean
     */
    Boolean delisting(Integer proId);

    /**
     * 获取H5商品详情（为兼容原格式，组装原来的数据格式）
     * @param id 商品id
     * @return ProductDetailResponse
     */
    ProductDetailResponse getH5Detail(Integer id);

    /**
     * 获取管理端商品列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<PayComponentProduct> getAdminList(ComponentProductSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 添加/扣减库存
     * @param productId 商品id
     * @param num 数量
     * @param operationType 类型：add—添加，sub—扣减
     * @return Boolean
     */
    Boolean operationStock(Integer productId, Integer num, String operationType);

    /**
     * 获取商品详情
     * @param id 商品id
     * @return PayComponentProductResponse
     */
    PayComponentProductResponse getInfo(Integer id);
}
