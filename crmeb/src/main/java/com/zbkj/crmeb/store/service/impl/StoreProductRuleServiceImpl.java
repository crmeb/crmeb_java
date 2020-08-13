package com.zbkj.crmeb.store.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.store.dao.StoreProductRuleDao;
import com.zbkj.crmeb.store.model.StoreProductRule;
import com.zbkj.crmeb.store.request.StoreProductRuleSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Mr.Zhang
* @description StoreProductRuleServiceImpl 接口实现
* @date 2020-05-27
*/
@Service
public class StoreProductRuleServiceImpl extends ServiceImpl<StoreProductRuleDao, StoreProductRule> implements StoreProductRuleService {

    @Resource
    private StoreProductRuleDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-27
    * @return List<StoreProductRule>
    */
    @Override
    public List<StoreProductRule> getList(StoreProductRuleSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductRule 类的多条件查询
        LambdaQueryWrapper<StoreProductRule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(null != request.getKeywords()){
            lambdaQueryWrapper.like(StoreProductRule::getRuleName, request.getKeywords());
            lambdaQueryWrapper.or().like(StoreProductRule::getRuleValue, request.getKeywords());
        }
        return dao.selectList(lambdaQueryWrapper);
    }

}

