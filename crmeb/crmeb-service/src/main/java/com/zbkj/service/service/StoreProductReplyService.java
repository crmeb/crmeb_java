package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.StoreProductReplyAddRequest;
import com.zbkj.common.request.StoreProductReplyCommentRequest;
import com.zbkj.common.request.StoreProductReplySearchRequest;
import com.zbkj.common.response.ProductDetailReplyResponse;
import com.zbkj.common.response.ProductReplyResponse;
import com.zbkj.common.response.StoreProductReplyResponse;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.request.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.product.StoreProductReply;

/**
 * StoreProductReplyService 接口
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
public interface StoreProductReplyService extends IService<StoreProductReply> {

    /**
     * 商品评论列表
     * @param request 请求参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<StoreProductReplyResponse> getList(StoreProductReplySearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 创建订单商品评价
     * @param request 请求参数
     * @return Boolean
     */
    Boolean create(StoreProductReplyAddRequest request);

    /**
     * 添加虚拟评论
     * @param request 评论参数
     * @return 评论结果
     */
    boolean virtualCreate(StoreProductReplyAddRequest request);

    /**
     * 查询是否已经回复
     * @param unique skuId
     * @param orderId 订单id
     * @return Boolean
     */
    Boolean isReply(String unique, Integer orderId);

    /**
     * H5商品评论统计
     * @param productId 商品编号
     * @return MyRecord
     */
    MyRecord getH5Count(Integer productId);

    /**
     * H5商品详情评论信息
     * @param proId 商品编号
     * @return ProductDetailReplyResponse
     */
    ProductDetailReplyResponse getH5ProductReply(Integer proId);

    /**
     * 移动端商品评论列表
     * @param proId 商品编号
     * @param type 评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return PageInfo<ProductReplyResponse>
     */
    PageInfo<ProductReplyResponse> getH5List(Integer proId, Integer type, PageParamRequest pageParamRequest);

    /**
     * 删除评论
     * @param id 评论id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 商品评论回复
     * @param request 回复参数
     */
    Boolean comment(StoreProductReplyCommentRequest request);
}
