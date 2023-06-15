package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.model.express.ShippingTemplatesRegion;
import com.zbkj.common.request.ShippingTemplatesRegionRequest;
import com.zbkj.service.dao.ShippingTemplatesRegionDao;
import com.zbkj.service.service.ShippingTemplatesRegionService;
import com.zbkj.service.service.SystemCityService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
* ShippingTemplatesRegionServiceImpl 接口实现
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
public class ShippingTemplatesRegionServiceImpl extends ServiceImpl<ShippingTemplatesRegionDao, ShippingTemplatesRegion> implements ShippingTemplatesRegionService {

    @Resource
    private ShippingTemplatesRegionDao dao;

    @Autowired
    private SystemCityService systemCityService;

    private List<Integer> cityIdList;

    @Override
    public List<ShippingTemplatesRegion> listByIds(Collection<? extends Serializable> idList) {
        LambdaQueryWrapper<ShippingTemplatesRegion> lqw = new LambdaQueryWrapper<>();
        lqw.in(ShippingTemplatesRegion::getTempId, idList);
        lqw.orderByAsc(ShippingTemplatesRegion::getCityId);
        return dao.selectList(lqw);
    }

    /**
     * 保存配送区域及运费
     * @param shippingTemplatesRegionRequestList List<ShippingTemplatesRegionRequest> 运费集合
     * @param type Integer 计费方式
     * @param tempId Integer 运费模板id
     * @author Mr.Zhang
     * @since 2020-05-20
     */
    @Async
    @Override
    public void saveAll(List<ShippingTemplatesRegionRequest> shippingTemplatesRegionRequestList, Integer type, Integer tempId) {
        ArrayList<ShippingTemplatesRegion> shippingTemplatesRegionList = new ArrayList<>();

        //把目前模板下的所有数据标记为无效
        updateStatus(tempId);

        for (ShippingTemplatesRegionRequest shippingTemplatesRegionRequest : shippingTemplatesRegionRequestList) {
            String uniqueKey = DigestUtils.md5Hex(shippingTemplatesRegionRequest.toString());

            if("all".equals(shippingTemplatesRegionRequest.getCityId()) || "0".equals(shippingTemplatesRegionRequest.getCityId())){
                cityIdList = getCityIdList();
            }else{
                cityIdList = CrmebUtil.stringToArray(shippingTemplatesRegionRequest.getCityId());
            }
            for (Integer cityId: cityIdList) {
                ShippingTemplatesRegion shippingTemplatesRegion = new ShippingTemplatesRegion();
                shippingTemplatesRegion.setCityId(cityId);
                shippingTemplatesRegion.setTitle(shippingTemplatesRegionRequest.getTitle());
                shippingTemplatesRegion.setUniqid(uniqueKey);
                shippingTemplatesRegion.setRenewal(shippingTemplatesRegionRequest.getRenewal());
                shippingTemplatesRegion.setRenewalPrice(shippingTemplatesRegionRequest.getRenewalPrice());
                shippingTemplatesRegion.setFirst(shippingTemplatesRegionRequest.getFirst());
                shippingTemplatesRegion.setFirstPrice(shippingTemplatesRegionRequest.getFirstPrice());
                shippingTemplatesRegion.setTempId(tempId);
                shippingTemplatesRegion.setType(type);
                shippingTemplatesRegion.setStatus(true);
                shippingTemplatesRegionList.add(shippingTemplatesRegion);
            }
        }
        //批量保存模板数据
        saveBatch(shippingTemplatesRegionList);

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
        LambdaQueryWrapper<ShippingTemplatesRegion> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShippingTemplatesRegion::getTempId, tempId);

        ShippingTemplatesRegion shippingTemplatesRegion = new ShippingTemplatesRegion();
        shippingTemplatesRegion.setStatus(false);
        update(shippingTemplatesRegion, lambdaQueryWrapper);
    }

    /**
     * 删除模板下的无效数据
     * @param tempId Integer 运费模板id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer tempId) {
        LambdaQueryWrapper<ShippingTemplatesRegion> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShippingTemplatesRegion::getTempId, tempId);
        lambdaQueryWrapper.eq(ShippingTemplatesRegion::getStatus, false);
        return dao.delete(lambdaQueryWrapper) > 0;
    }

    /**
     * 根据模板编号、城市ID查询
     * @param tempId 模板编号
     * @param cityId 城市ID
     * @return 运费模板
     */
    @Override
    public ShippingTemplatesRegion getByTempIdAndCityId(Integer tempId, Integer cityId) {
        LambdaQueryWrapper<ShippingTemplatesRegion> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShippingTemplatesRegion::getTempId, tempId);
        lambdaQueryWrapper.eq(ShippingTemplatesRegion::getCityId, cityId);
        lambdaQueryWrapper.eq(ShippingTemplatesRegion::getStatus, true);
        lambdaQueryWrapper.orderByDesc(ShippingTemplatesRegion::getId);
        lambdaQueryWrapper.last(" limit 1");
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 分组查询
     * @param tempId Integer 运费模板id
     * @return List<ShippingTemplatesRegionRequest>
     */
    @Override
    public List<ShippingTemplatesRegionRequest> getListGroup(Integer tempId) {
        return dao.getListGroup(tempId);
    }
}

