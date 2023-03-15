package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.model.express.ShippingTemplatesFree;
import com.zbkj.common.request.ShippingTemplatesFreeRequest;
import com.zbkj.service.dao.ShippingTemplatesFreeDao;
import com.zbkj.service.service.ShippingTemplatesFreeService;
import com.zbkj.service.service.SystemCityService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* ShippingTemplatesFreeServiceImpl 接口实现
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
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
            if("all".equals(shippingTemplatesFreeRequest.getCityId()) || "0".equals(shippingTemplatesFreeRequest.getCityId())){
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
                shippingTemplatesFree.setStatus(true);
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
        shippingTemplatesFree.setStatus(false);
        update(shippingTemplatesFree, lambdaQueryWrapper);
    }

    /**
     * 删除模板下的无效数据
     * @param tempId Integer 运费模板id
     */
    @Override
    public Boolean delete(Integer tempId) {
        LambdaQueryWrapper<ShippingTemplatesFree> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShippingTemplatesFree::getTempId, tempId);
        lambdaQueryWrapper.eq(ShippingTemplatesFree::getStatus, false);
        return dao.delete(lambdaQueryWrapper) > 0;
    }

    /**
     * 根据模板编号、城市ID查询
     * @param tempId 模板编号
     * @param cityId 城市ID
     * @return 运费模板
     */
    @Override
    public ShippingTemplatesFree getByTempIdAndCityId(Integer tempId, Integer cityId) {
        LambdaQueryWrapper<ShippingTemplatesFree> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShippingTemplatesFree::getTempId, tempId);
        lqw.eq(ShippingTemplatesFree::getCityId, cityId);
        lqw.eq(ShippingTemplatesFree::getStatus, true);
        lqw.orderByDesc(ShippingTemplatesFree::getId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 分组查询
     * @param tempId Integer 运费模板id
     * @return List<ShippingTemplatesFreeRequest>
     */
    @Override
    public List<ShippingTemplatesFreeRequest> getListGroup(Integer tempId) {
        return dao.getListGroup(tempId);
    }
}

