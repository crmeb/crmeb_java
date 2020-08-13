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
* @author Mr.Zhang
* @description StoreProductRelationService 接口
* @date 2020-05-27
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
