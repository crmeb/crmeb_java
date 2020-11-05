package com.zbkj.crmeb.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;

import com.zbkj.crmeb.express.model.ShippingTemplatesFree;
import com.zbkj.crmeb.express.dao.ShippingTemplatesFreeDao;
import com.zbkj.crmeb.express.request.ShippingTemplatesFreeRequest;
import com.zbkj.crmeb.express.service.ShippingTemplatesFreeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.system.service.SystemCityService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* ShippingTemplatesFreeServiceImpl 接口实现
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
public class ShippingTemplatesFreeServiceImpl extends ServiceImpl<ShippingTemplatesFreeDao, ShippingTemplatesFree> implements ShippingTemplatesFreeService {

    @Resource
    private ShippingTemplatesFreeDao dao;

    @Autowired
    private SystemCityService systemCityService;

    private List<Integer> cityIdList;


    /**
    * 列表
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-17
    * @return List<ShippingTemplatesFree>
    */
    @Override
    public List<ShippingTemplatesFree> getList(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

    /**
     * 根据模版id查询包邮信息
     * @param tempIds 模版id
     * @return 包邮信息集合
     */
    @Override
    public List<ShippingTemplatesFree> getListByTempIds(List<Integer> tempIds) {
        LambdaQueryWrapper<ShippingTemplatesFree> lqw = new LambdaQueryWrapper<>();
        lqw.in(ShippingTemplatesFree::getTempId, tempIds);
        return dao.selectList(lqw);
    }

    /**
     *  根据模版参数查询 H5 计算订单价格使用
     * @param templatesFree 模版参数
     * @return 模版集合
     */
    @Override
    public List<ShippingTemplatesFree> getListByConditionForCalcOrderPrice(ShippingTemplatesFree templatesFree) {
        LambdaQueryWrapper<ShippingTemplatesFree> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShippingTemplatesFree::getTempId, templatesFree.getTempId());
        lqw.eq(ShippingTemplatesFree::getCityId, templatesFree.getCityId());
        lqw.le(ShippingTemplatesFree::getNumber, templatesFree.getNumber());
        lqw.ge(ShippingTemplatesFree::getPrice, templatesFree.getPrice());
        return dao.selectList(lqw);
    }

    /**
     * 保存配送区域
     * @param shippingTemplatesFreeRequestList List<ShippingTemplatesFreeRequest> 运费集合
     * @param type Integer 计费方式
     * @param tempId Integer 运费模板id
     * @author Mr.Zhang
     * @since 2020-05-20
     */
    @Async
    @Override
    public void saveAll(List<ShippingTemplatesFreeRequest> shippingTemplatesFreeRequestList, Integer type, Integer tempId) {

        ArrayList<ShippingTemplatesFree> shippingTemplatesFreesList = new ArrayList<>();

        //把目前模板下的所有数据标记为无效
        updateStatus(tempId);


        for (ShippingTemplatesFreeRequest shippingTemplatesFreeRequest : shippingTemplatesFreeRequestList) {
            String uniqueKey = DigestUtils.md5Hex(shippingTemplatesFreeRequest.toString());
            if(shippingTemplatesFreeRequest.getCityId().equals("all") || shippingTemplatesFreeRequest.getCityId().equals("0")){
                cityIdList = getCityIdList();
            }else{
                cityIdList = CrmebUtil.stringToArray(shippingTemplatesFreeRequest.getCityId());
            }
            for (Integer cityId: cityIdList) {
                ShippingTemplatesFree shippingTemplatesFree = new ShippingTemplatesFree();
                shippingTemplatesFree.setCityId(cityId);
                shippingTemplatesFree.setTitle(shippingTemplatesFreeRequest.getTitle());
                shippingTemplatesFree.setUniqid(uniqueKey);
                shippingTemplatesFree.setTempId(tempId);
                shippingTemplatesFree.setType(type);
                shippingTemplatesFree.setNumber(shippingTemplatesFreeRequest.getNumber());
                shippingTemplatesFree.setPrice(shippingTemplatesFreeRequest.getPrice());
                shippingTemplatesFreesList.add(shippingTemplatesFree);
            }
        }
        //批量保存模板数据
        saveBatch(shippingTemplatesFreesList);

        //删除模板下的无效数据
        delete(tempId);
    }

    /**
     * 获取所有城市cityId
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return List<Integer>
     */
    private List<Integer> getCityIdList() {
        if(this.cityIdList == null || this.cityIdList.size() < 1){
            this.cityIdList = systemCityService.getCityIdList();
        }
        return this.cityIdList;
    }

    /**
     * 把模板下的所有数据标记为无效
     * @param tempId Integer 运费模板id
     * @author Mr.Zhang
     * @since 2020-05-20
     */
    private void updateStatus(Integer tempId) {
        LambdaQueryWrapper<ShippingTemplatesFree> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShippingTemplatesFree::getTempId, tempId);

        ShippingTemplatesFree shippingTemplatesFree = new ShippingTemplatesFree();
        shippingTemplatesFree.setStatus(true);
        update(shippingTemplatesFree, lambdaQueryWrapper);
    }

    /**
     * 删除模板下的无效数据
     * @param tempId Integer 运费模板id
     * @author Mr.Zhang
     * @since 2020-05-20
     */
    @Override
    public void delete(Integer tempId) {
        LambdaQueryWrapper<ShippingTemplatesFree> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShippingTemplatesFree::getTempId, tempId);
        lambdaQueryWrapper.eq(ShippingTemplatesFree::getStatus, true);
        dao.delete(lambdaQueryWrapper);
    }

    /**
     * 根据模板id和城市id查询数据
     * @param tempId Integer 运费模板id
     * @param cityId Integer 城市id
     * @author Mr.Zhang
     * @since 2020-05-20
     * @return ShippingTemplatesFree
     */
    private ShippingTemplatesFree getVoByTempIdAndCityId(Integer tempId, Integer cityId) {
        LambdaQueryWrapper<ShippingTemplatesFree> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShippingTemplatesFree::getCityId, cityId);
        lambdaQueryWrapper.eq(ShippingTemplatesFree::getTempId, tempId);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 分组查询
     * @param tempId Integer 运费模板id
     * @author Mr.Zhang
     * @since 2020-05-20
     * @return List<ShippingTemplatesFreeRequest>
     */
    @Override
    public List<ShippingTemplatesFreeRequest> getListGroup(Integer tempId) {
        return dao.getListGroup(tempId);
    }

}

