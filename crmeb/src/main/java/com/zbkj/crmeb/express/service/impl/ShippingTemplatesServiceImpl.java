package com.zbkj.crmeb.express.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;

import com.zbkj.crmeb.express.model.ShippingTemplates;
import com.zbkj.crmeb.express.dao.ShippingTemplatesDao;
import com.zbkj.crmeb.express.request.ShippingTemplatesFreeRequest;
import com.zbkj.crmeb.express.request.ShippingTemplatesRegionRequest;
import com.zbkj.crmeb.express.request.ShippingTemplatesRequest;
import com.zbkj.crmeb.express.request.ShippingTemplatesSearchRequest;
import com.zbkj.crmeb.express.service.ShippingTemplatesFreeService;
import com.zbkj.crmeb.express.service.ShippingTemplatesRegionService;
import com.zbkj.crmeb.express.service.ShippingTemplatesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* ShippingTemplatesServiceImpl 接口实现
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
@Service
public class ShippingTemplatesServiceImpl extends ServiceImpl<ShippingTemplatesDao, ShippingTemplates> implements ShippingTemplatesService {

    @Resource
    private ShippingTemplatesDao dao;

    @Autowired
    private ShippingTemplatesRegionService shippingTemplatesRegionService;

    @Autowired
    private ShippingTemplatesFreeService shippingTemplatesFreeService;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-17
    * @return List<ShippingTemplates>
    */
    @Override
    public List<ShippingTemplates> getList(ShippingTemplatesSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<ShippingTemplates> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.like(ShippingTemplates::getName, request.getKeywords());
        }
        lambdaQueryWrapper.orderByDesc(ShippingTemplates::getSort).orderByDesc(ShippingTemplates::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 检测运费模板是否存在
     * @param tempId Integer 模板id
     * @author Mr.Zhang
     * @since 2020-05-07
     */
    @Override
    public void checkExpressTemp(Integer tempId) {
        if(getById(tempId) == null){
            throw new CrmebException("没有相关运费模板");
        }
    }

    /**
     * 新增
     * @param request 新增参数
     * @author Mr.Zhang
     * @since 2020-04-17
     * @return bool
     */
    @Override
    public boolean create(ShippingTemplatesRequest request) {
        ShippingTemplates shippingTemplates = new ShippingTemplates();
        shippingTemplates.setName(request.getName());
        shippingTemplates.setSort(request.getSort());
        shippingTemplates.setType(request.getType());
        shippingTemplates.setAppoint(request.getAppoint());

        save(shippingTemplates);

        //区域运费
        List<ShippingTemplatesRegionRequest> shippingTemplatesRegionRequestList = request.getShippingTemplatesRegionRequestList();

        if(shippingTemplatesRegionRequestList.size() > 0){
            shippingTemplatesRegionService.saveAll(shippingTemplatesRegionRequestList, request.getType(), shippingTemplates.getId());
        }


        List<ShippingTemplatesFreeRequest> shippingTemplatesFreeRequestList = request.getShippingTemplatesFreeRequestList();
        if(null != shippingTemplatesFreeRequestList && shippingTemplatesFreeRequestList.size() > 0 && request.getAppoint()){
            shippingTemplatesFreeService.saveAll(shippingTemplatesFreeRequestList, request.getType(), shippingTemplates.getId());
        }

        return true;
    }

    /**
     * 新增
     * @param id Integer 模板id
     * @param request ShippingTemplatesRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-17
     * @return bool
     */
    @Override
    public boolean update(Integer id, ShippingTemplatesRequest request) {
        ShippingTemplates shippingTemplates = new ShippingTemplates();
        shippingTemplates.setId(id);
        shippingTemplates.setName(request.getName());
        shippingTemplates.setSort(request.getSort());
        shippingTemplates.setType(request.getType());
        shippingTemplates.setAppoint(request.getAppoint());


        updateById(shippingTemplates);

        //区域运费
        List<ShippingTemplatesRegionRequest> shippingTemplatesRegionRequestList = request.getShippingTemplatesRegionRequestList();

        if(shippingTemplatesRegionRequestList.size() < 1){
            throw new CrmebException("请设置区域配送信息！");
        }
        shippingTemplatesRegionService.saveAll(shippingTemplatesRegionRequestList, request.getType(), shippingTemplates.getId());

        List<ShippingTemplatesFreeRequest> shippingTemplatesFreeRequestList = request.getShippingTemplatesFreeRequestList();
        if(CollUtil.isNotEmpty(shippingTemplatesFreeRequestList) && request.getAppoint()){
            shippingTemplatesFreeService.saveAll(shippingTemplatesFreeRequestList, request.getType(), shippingTemplates.getId());
        }

        return true;
    }

    /**
     * 删除
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-17
     * @return boolean
     */
    @Override
    public boolean remove(Integer id) {
        shippingTemplatesRegionService.delete(id);
        shippingTemplatesFreeService.delete(id);
        return removeById(id);
    }

    /**
     * 根据id集合获取
     * @param ids 模版ids
     * @return 模版集合
     */
    @Override
    public List<ShippingTemplates> getListInIds(List<Integer> ids) {
        LambdaQueryWrapper<ShippingTemplates> lqw = new LambdaQueryWrapper<>();
        lqw.in(ShippingTemplates::getId, ids);
        return dao.selectList(lqw);
    }
}

