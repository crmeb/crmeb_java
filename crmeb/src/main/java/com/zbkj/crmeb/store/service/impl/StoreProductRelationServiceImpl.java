package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.front.request.UserCollectAllRequest;
import com.zbkj.crmeb.front.request.UserCollectRequest;
import com.zbkj.crmeb.store.dao.StoreProductRelationDao;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductRelation;
import com.zbkj.crmeb.store.request.StoreProductRelationSearchRequest;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductRelationService;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.store.vo.StoreProductRelationCountVo;
import com.zbkj.crmeb.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StoreProductRelationServiceImpl 接口实现
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
@Service
public class StoreProductRelationServiceImpl extends ServiceImpl<StoreProductRelationDao, StoreProductRelation>
        implements StoreProductRelationService {

    @Resource
    private StoreProductRelationDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductService storeProductService;

    /**
     * 列表
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @author Mr.Zhang
     * @since 2020-05-06
     * @return List<StoreProductRelation>
     */
    @Override
    public List<StoreProduct> getList(StoreProductRelationSearchRequest request, PageParamRequest pageParamRequest) {
        //带 StoreProductRelation 类的多条件查询
        LambdaQueryWrapper<StoreProductRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.setEntity(request);
        List<StoreProductRelation> storeProductRelationList = dao.selectList(lambdaQueryWrapper);
        if(storeProductRelationList == null || storeProductRelationList.size() < 1){
            return new ArrayList<>();
        }

        List<Integer> productIdList = storeProductRelationList.stream().map(StoreProductRelation::getProductId).distinct().collect(Collectors.toList());

        if(productIdList.size() < 1){
            return new ArrayList<>();
        }
        StoreProductSearchRequest storeProductSearchRequest = new StoreProductSearchRequest();
        storeProductSearchRequest.setType(1); //上架的商品
        return storeProductService.getList(storeProductSearchRequest, pageParamRequest, productIdList);
    }

    /**
     * 根据类型和产品id获取总数
     * @param productIdList List<Integer> 产品id
     * @param type String 类型
     * @author Mr.Zhang
     * @since 2020-05-06
     * @return List<StoreProductRelationCountVo>
     */
    public List<StoreProductRelationCountVo> getCountInProductId(List<Integer> productIdList, String type) {
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("productId", productIdList);
        objectObjectHashMap.put("type", type);
        return dao.getCountInProductId(objectObjectHashMap);
    }

    /**
     * 根据产品id获取点赞总数
     * @param productIdList List<Integer> 产品id
     * @author Mr.Zhang
     * @since 2020-05-06
     * @return HashMap<Integer, Integer>
     */
    public HashMap<Integer, Integer> getLikeCountListInProductId(List<Integer> productIdList) {
        List<StoreProductRelationCountVo> list = getCountInProductId(productIdList, "like");
        return getCountListInProductId(list);
    }

    /**
     * 根据产品id获取收藏总数
     * @param productIdList List<Integer> 产品id
     * @author Mr.Zhang
     * @since 2020-05-06
     * @return HashMap<Integer, Integer>
     */
    public HashMap<Integer, Integer> getCollectCountListInProductId(List<Integer> productIdList) {
        List<StoreProductRelationCountVo> list = getCountInProductId(productIdList, "collect");
        return getCountListInProductId(list);
    }

    /**
     * 添加收藏产品
     * @param request UserCollectAllRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-05-06
     * @return boolean
     */
    @Override
    public boolean all(UserCollectAllRequest request) {
        Integer[] arr = request.getProductId();
        if(arr.length < 1){
            throw new CrmebException("请选择产品");
        }

        List<Integer> list = CrmebUtil.arrayUnique(arr);

        Integer uid = userService.getUserIdException();
        deleteAll(request, uid, "collect");  //先删除所有已存在的

        ArrayList<StoreProductRelation> storeProductRelationList = new ArrayList<>();
        for (Integer productId: list) {
            StoreProductRelation storeProductRelation = new StoreProductRelation();
            storeProductRelation.setUid(uid);
            storeProductRelation.setType("collect");
            storeProductRelation.setProductId(productId);
            storeProductRelation.setCategory(request.getCategory());
            storeProductRelationList.add(storeProductRelation);
        }
        return saveBatch(storeProductRelationList);
    }


    /**
     * 取消收藏产品
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @Override
    public boolean delete(UserCollectRequest request) {
        LambdaQueryWrapper<StoreProductRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProductRelation::getProductId, request.getProductId()).
                eq(StoreProductRelation::getCategory, request.getCategory());
        dao.delete(lambdaQueryWrapper);
        return true;
    }

    /**
     * 取消收藏产品
     * @param request UserCollectAllRequest 参数
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    private void deleteAll(UserCollectAllRequest request, Integer uid, String type) {
        LambdaQueryWrapper<StoreProductRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreProductRelation::getProductId, Arrays.asList(request.getProductId()))
                .eq(StoreProductRelation::getCategory, request.getCategory())
                .eq(StoreProductRelation::getUid, uid)
                .eq(StoreProductRelation::getType, type);
        dao.delete(lambdaQueryWrapper);
    }

    /**
     * product_id => count 数据组装
     * @param list List<StoreProductRelationCountVo> 数据集合
     * @author Mr.Zhang
     * @since 2020-05-06
     * @return HashMap<Integer, Integer>
     */
    private HashMap<Integer, Integer> getCountListInProductId(List<StoreProductRelationCountVo> list){
        HashMap<Integer, Integer> map = new HashMap<>();
        for (StoreProductRelationCountVo storeProductRelationCountVo : list){
            map.put(storeProductRelationCountVo.getProductId(), storeProductRelationCountVo.getCount());
        }
        return map;
    }

    /**
     * 根据产品id和类型获取对应列表
     * @param productId 产品id
     * @param type 类型
     * @return 对应结果
     */
    @Override
    public List<StoreProductRelation> getList(Integer productId, String type) {
        LambdaQueryWrapper<StoreProductRelation> lqr = new LambdaQueryWrapper<>();
        lqr.eq(StoreProductRelation::getProductId, productId);
        lqr.eq(StoreProductRelation::getType, type);
        return dao.selectList(lqr);
    }

    /**
     * 获取用户当前是否喜欢该商品
     * @param userId 用户id
     * @param productId 商品id
     * @return 是否喜欢标识
     */
    @Override
    public List<StoreProductRelation> getLikeOrCollectByUser(Integer userId, Integer productId,boolean isLike) {
        String typeValue = isLike?"like":"collect";
        LambdaQueryWrapper<StoreProductRelation> lqr = new LambdaQueryWrapper<>();
        lqr.eq(StoreProductRelation::getProductId, productId);
        lqr.eq(StoreProductRelation::getUid, userId);
        lqr.eq(StoreProductRelation::getType,typeValue);
        return dao.selectList(lqr);
    }

}

