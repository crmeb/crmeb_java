package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.request.UserCollectAllRequest;
import com.zbkj.common.request.UserCollectRequest;
import com.zbkj.common.response.UserRelationResponse;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.model.product.StoreProductRelation;
import com.zbkj.service.dao.StoreProductRelationDao;
import com.zbkj.service.service.StoreProductRelationService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * StoreProductRelationServiceImpl 接口实现
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
@Service
public class StoreProductRelationServiceImpl extends ServiceImpl<StoreProductRelationDao, StoreProductRelation>
        implements StoreProductRelationService {

    @Resource
    private StoreProductRelationDao dao;

    @Autowired
    private UserService userService;

    /**
     * 添加收藏产品
     * @param request UserCollectAllRequest 新增参数
     * @return boolean
     */
    @Override
    public Boolean all(UserCollectAllRequest request) {
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
     */
    @Override
    public Boolean delete(String requestJson) {
        JSONObject jsonObject = JSONObject.parseObject(requestJson);
        if (StrUtil.isBlank(jsonObject.getString("ids"))) {
            throw new CrmebException("收藏id不能为空");
        }
        List<Integer> idList = CrmebUtil.stringToArray(jsonObject.getString("ids"));
        if (CollUtil.isEmpty(idList)) {
            throw new CrmebException("收藏id不能为空");
        }
        Integer userId = userService.getUserIdException();
        LambdaQueryWrapper<StoreProductRelation> lqw = Wrappers.lambdaQuery();
        lqw.in(StoreProductRelation::getId, idList);
        lqw.eq(StoreProductRelation::getUid, userId);
        int delete = dao.delete(lqw);
        return delete > 0;
    }

    /**
     * 取消收藏产品
     * @param request UserCollectAllRequest 参数
     * @param type 类型
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

    /**
     * 获取用户收藏列表
     * @param pageParamRequest 分页参数
     * @return List<UserRelationResponse>
     */
    @Override
    public List<UserRelationResponse> getUserList(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        Integer userId = userService.getUserIdException();
        return dao.getUserList(userId);
    }

    /**
     * 获取用户的收藏数量
     * @param uid 用户uid
     * @return 收藏数量
     */
    @Override
    public Integer getCollectCountByUid(Integer uid) {
        LambdaQueryWrapper<StoreProductRelation> lqr = Wrappers.lambdaQuery();
        lqr.eq(StoreProductRelation::getUid, uid);
        lqr.eq(StoreProductRelation::getType,"collect");
        return dao.selectCount(lqr);
    }

    /**
     * 根据商品Id取消收藏
     * @param proId 商品Id
     * @return Boolean
     */
    @Override
    public Boolean deleteByProId(Integer proId) {
        LambdaQueryWrapper<StoreProductRelation> lqw = Wrappers.lambdaQuery();
        lqw.in(StoreProductRelation::getProductId, proId);
        int delete = dao.delete(lqw);
        return delete > 0;
    }

    /**
     * 根据商品Id取消收藏
     * @param proId 商品Id
     * @return Boolean
     */
    @Override
    public Boolean deleteByProIdAndUid(Integer proId) {
        Integer userId = userService.getUserIdException();
        LambdaQueryWrapper<StoreProductRelation> lqw = Wrappers.lambdaQuery();
        lqw.in(StoreProductRelation::getProductId, proId);
        lqw.eq(StoreProductRelation::getUid, userId);
        int delete = dao.delete(lqw);
        return delete > 0;
    }

    /**
     * 根据日期获取收藏量
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getCountByDate(String date) {
        LambdaQueryWrapper<StoreProductRelation> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProductRelation::getId);
        lqw.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 根据日期获取收藏量
     * @param date 日期，yyyy-MM-dd格式
     * @param proId 商品id
     * @return Integer
     */
    @Override
    public Integer getCountByDateAndProId(String date, Integer proId) {
        LambdaQueryWrapper<StoreProductRelation> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProductRelation::getId);
        lqw.eq(StoreProductRelation::getProductId, proId);
        lqw.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 添加收藏
     * @param request 收藏参数
     */
    @Override
    public Boolean add(UserCollectRequest request) {
        StoreProductRelation storeProductRelation = new StoreProductRelation();
        BeanUtils.copyProperties(request, storeProductRelation);
        storeProductRelation.setUid(userService.getUserIdException());
        return save(storeProductRelation);
    }
}

