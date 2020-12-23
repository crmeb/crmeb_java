package com.zbkj.crmeb.store.service.impl;

import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.store.dao.StoreProductRuleDao;
import com.zbkj.crmeb.store.model.StoreProductRule;
import com.zbkj.crmeb.store.request.StoreProductRuleRequest;
import com.zbkj.crmeb.store.request.StoreProductRuleSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * StoreProductRuleServiceImpl 接口实现
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
        lambdaQueryWrapper.orderByDesc(StoreProductRule::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 新增商品规格
     * @param storeProductRuleRequest 规格参数
     * @return 新增结果
     */
    @Override
    public boolean save(StoreProductRuleRequest storeProductRuleRequest) {
        if(getListByRuleName(storeProductRuleRequest.getRuleName()).size() > 0){
            throw new CrmebException("此规格值已经存在");
        }
        StoreProductRule storeProductRule = new StoreProductRule();
        BeanUtils.copyProperties(storeProductRuleRequest, storeProductRule);
        return save(storeProductRule);
    }

    /**
     * 根据规格名称查询同名规格
     * @param ruleName 规格名称
     * @return 查询到的数据
     */
    private List<StoreProductRule> getListByRuleName(String ruleName){
        LambdaQueryWrapper<StoreProductRule> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isBlank(ruleName)){
            return new ArrayList<>();
        }
            lambdaQueryWrapper.eq(StoreProductRule::getRuleName, ruleName);
        return dao.selectList(lambdaQueryWrapper);
    }
}

