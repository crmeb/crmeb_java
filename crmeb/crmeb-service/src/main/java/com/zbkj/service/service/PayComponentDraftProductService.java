package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.ComponentProductSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.PayComponentProductAddRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.wechat.video.PayComponentDraftProduct;

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
public interface PayComponentDraftProductService extends IService<PayComponentDraftProduct> {

    /**
     * 添加商品
     * @param addRequest 商品请求参数
     * @return Boolean
     */
    Boolean add(PayComponentProductAddRequest addRequest);

    /**
     * 根据商品id获取草稿商品
     * @param proId 商品id
     * @return PayComponentDraftProduct
     */
    PayComponentDraftProduct getByProId(Integer proId);

    /**
     * 通过商品id删除草稿
     * @param proId 商品id
     */
    Boolean deleteByProId(Integer proId);

    /**
     * 管理端草稿商品列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<PayComponentDraftProduct> getAdminList(ComponentProductSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 商品详情
     * @param id 商品id
     * @return
     */
    PayComponentDraftProduct getInfo(Integer id);
}
