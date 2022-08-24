package com.zbkj.front.service;

import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductListRequest;
import com.zbkj.common.request.ProductRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.response.*;
import com.zbkj.common.vo.CategoryTreeVo;

import java.util.List;

/**
* IndexService 接口
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
public interface ProductService {

    /**
     * 商品分类
     * @return List
     */
    List<CategoryTreeVo> getCategory();

    /**
     * 商品列表
     * @param request 请求参数
     * @param pageParamRequest 分页参数
     * @return CommonPage
     */
    CommonPage<IndexProductResponse> getList(ProductRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取商品详情
     * @param id 商品编号
     * @param type normal-正常，void-视频
     * @return 商品详情信息
     */
    ProductDetailResponse getDetail(Integer id, String type);

    /**
     * 获取商品SKU详情
     * @param id 商品编号
     * @return 商品详情信息
     */
    ProductDetailResponse getSkuDetail(Integer id);

    /**
     * 商品评论列表
     * @param proId 商品编号
     * @param type 评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return PageInfo<ProductReplyResponse>
     */
    PageInfo<ProductReplyResponse> getReplyList(Integer proId, Integer type, PageParamRequest pageParamRequest);

    /**
     * 商品评论数量
     * @param id 商品id
     * @return StoreProductReplayCountResponse
     */
    StoreProductReplayCountResponse getReplyCount(Integer id);

    /**
     * 获取热门推荐商品列表
     * @param pageRequest 分页参数
     * @return CommonPage<IndexProductResponse>
     */
    CommonPage<IndexProductResponse> getHotProductList(PageParamRequest pageRequest);

    /**
     * 商品详情评论
     * @param id 商品id
     * @return ProductDetailReplyResponse
     */
    ProductDetailReplyResponse getProductReply(Integer id);

    /**
     * 优选商品推荐
     * @return CommonPage<IndexProductResponse>
     */
    CommonPage<IndexProductResponse> getGoodProductList();

    /**
     * 商品列表(个别分类模型使用)
     * @param request 列表请求参数
     * @param pageParamRequest 分页参数
     * @return CommonPage
     */
    CommonPage<IndexProductResponse> getCategoryProductList(ProductListRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取商品排行榜
     * @return List
     */
    List<StoreProduct> getLeaderboard();
}
