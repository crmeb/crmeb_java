package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.store.dao.StoreProductDescriptionDao;
import com.zbkj.crmeb.store.model.StoreProductDescription;
import com.zbkj.crmeb.store.request.StoreProductDescriptionSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductDescriptionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreProductDescriptionServiceImpl 接口实现
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
public class StoreProductDescriptionServiceImpl extends ServiceImpl<StoreProductDescriptionDao, StoreProductDescription> implements StoreProductDescriptionService {

    @Resource
    private StoreProductDescriptionDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-27
    * @return List<StoreProductDescription>
    */
    @Override
    public List<StoreProductDescription> getList(StoreProductDescriptionSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductDescription 类的多条件查询
        LambdaQueryWrapper<StoreProductDescription> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreProductDescription model = new StoreProductDescription();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据商品id和type删除对应描述
     * @param productId 商品id
     * @param type      类型
     */
    @Override
    public void deleteByProductId(int productId,int type) {
        LambdaQueryWrapper<StoreProductDescription> lmq = Wrappers.lambdaQuery();
        lmq.eq(StoreProductDescription::getProductId, productId).eq(StoreProductDescription::getType,type);
        dao.delete(lmq);
    }
}

