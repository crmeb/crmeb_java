package com.zbkj.crmeb.store.service.impl;

import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.zbkj.crmeb.store.model.StoreCategory;
import com.zbkj.crmeb.store.dao.StoreCategoryDao;
import com.zbkj.crmeb.store.request.StoreCategorySearchRequest;
import com.zbkj.crmeb.store.response.StoreCategoryTreeList;
import com.zbkj.crmeb.store.service.StoreCategoryService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
* @author Mr.Zhang
* @description StoreCategoryServiceImpl 接口实现
* @date 2020-05-28
*/
@Service
public class StoreCategoryServiceImpl extends ServiceImpl<StoreCategoryDao, StoreCategory> implements StoreCategoryService {

    @Resource
    private StoreCategoryDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-28
    * @return List<StoreCategory>
    */
    @Override
    public List<StoreCategory> getList(StoreCategorySearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreCategory 类的多条件查询
        LambdaQueryWrapper<StoreCategory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreCategory model = new StoreCategory();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

//    @Override
//    public List<StoreCategoryTreeList> getTreeList(Integer pid, Integer isShow){
//        List<StoreCategoryTreeList> treeList = new ArrayList<>();
//        LambdaQueryWrapper<StoreCategory> lqw = new LambdaQueryWrapper<>();
//        lqw.eq(StoreCategory::getId, pid);
//        if(isShow >= 0){
//            lqw.eq(StoreCategory::getIsShow, isShow);
//        }
//        lqw.orderByDesc(StoreCategory::getSort);
//        List<StoreCategory> allList = dao.selectList(lqw);
//        Map<Integer, StoreCategory> allListMap = allList.stream()
//                .distinct().collect(Collectors.toMap(StoreCategory::getId,(p) -> p));
//        System.out.println("allListMap:"+allListMap);
//        return treeList;
//    }
}

