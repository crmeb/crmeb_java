package com.zbkj.crmeb.front.service;

import com.common.CommonPage;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;
import com.zbkj.crmeb.front.request.IndexStoreProductSearchRequest;
import com.zbkj.crmeb.front.request.ProductRequest;
import com.zbkj.crmeb.front.response.ProductDetailResponse;
import com.zbkj.crmeb.front.response.ProductResponse;
import com.zbkj.crmeb.front.response.StoreProductReplayCountResponse;
import com.zbkj.crmeb.store.model.StoreProductReply;
import com.zbkj.crmeb.store.response.StoreProductReplyResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;

import java.util.HashMap;
import java.util.List;

/**
* IndexService 接口
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
public interface ProductService {
    CommonPage<ProductResponse> getIndexProduct(IndexStoreProductSearchRequest request, PageParamRequest pageParamRequest);

    List<CategoryTreeVo> getCategory();

    CommonPage<ProductResponse> getList(ProductRequest request, PageParamRequest pageParamRequest);

    ProductDetailResponse getDetail(Integer id);

    PageInfo<StoreProductReplyResponse> getReplyList(Integer id, Integer type, PageParamRequest pageParamRequest);

    StoreProductReplayCountResponse getReplyCount(Integer id);

    String getPacketPriceRange(StoreProductResponse storeProductResponse,boolean isPromoter);
}
