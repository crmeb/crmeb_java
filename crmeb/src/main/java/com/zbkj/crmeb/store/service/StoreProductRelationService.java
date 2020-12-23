package com.zbkj.crmeb.store.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.front.request.UserCollectAllRequest;
import com.zbkj.crmeb.front.request.UserCollectRequest;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductRelation;
import com.zbkj.crmeb.store.request.StoreProductRelationSearchRequest;
import com.zbkj.crmeb.store.vo.StoreProductRelationCountVo;

import java.util.HashMap;
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

    HashMap<Integer, Integer> getLikeCountListInProductId(List<Integer> idList);

    HashMap<Integer, Integer> getCollectCountListInProductId(List<Integer> idList);

    List<StoreProductRelation> getList(Integer productId, String type);

    boolean delete(UserCollectRequest request);

    boolean all(UserCollectAllRequest request);

    List<StoreProductRelation> getLikeOrCollectByUser(Integer userId, Integer productId,boolean isLike);
}
