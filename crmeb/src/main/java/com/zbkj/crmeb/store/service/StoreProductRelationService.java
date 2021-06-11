package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.request.UserCollectAllRequest;
import com.zbkj.crmeb.front.response.UserRelationResponse;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductRelation;
import com.zbkj.crmeb.store.request.StoreProductRelationSearchRequest;

import java.util.List;

/**
 * StoreProductRelationService 接口
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
public interface StoreProductRelationService extends IService<StoreProductRelation> {

    List<StoreProduct> getList(StoreProductRelationSearchRequest request, PageParamRequest pageParamRequest);

    List<StoreProductRelation> getList(Integer productId, String type);

    /**
     * 取消收藏
     * @param requestJson 收藏idsJson
     * @return Boolean
     */
    Boolean delete(String requestJson);

    boolean all(UserCollectAllRequest request);

    List<StoreProductRelation> getLikeOrCollectByUser(Integer userId, Integer productId,boolean isLike);

    /**
     * 获取用户收藏列表
     * @param pageParamRequest 分页参数
     * @return List<UserRelationResponse>
     */
    List<UserRelationResponse> getUserList(PageParamRequest pageParamRequest);

    /**
     * 获取用户的收藏数量
     * @param uid 用户uid
     * @return 收藏数量
     */
    Integer getCollectCountByUid(Integer uid);

    /**
     * 根据商品Id取消收藏
     * @param proId 商品Id
     * @return Boolean
     */
    Boolean deleteByProId(Integer proId);
}
