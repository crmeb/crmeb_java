package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.express.ShippingTemplatesRegion;
import com.zbkj.common.request.ShippingTemplatesRegionRequest;
import com.zbkj.common.response.ShippingTemplatesRegionResponse;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.service.dao.ShippingTemplatesRegionDao;
import com.zbkj.service.service.ShippingTemplatesRegionService;
import com.zbkj.service.service.SystemCityService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ShippingTemplatesRegionServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
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
     *
     * @param shippingTemplatesRegionRequestList List<ShippingTemplatesRegionRequest> 运费集合
     * @param type                               Integer 计费方式
     * @param tempId                             Integer 运费模板id
     */
    @Override
    public Boolean saveAll(List<ShippingTemplatesRegionRequest> shippingTemplatesRegionRequestList, Integer type, Integer tempId) {
        ArrayList<ShippingTemplatesRegion> shippingTemplatesRegionList = new ArrayList<>();
        for (ShippingTemplatesRegionRequest shippingTemplatesRegionRequest : shippingTemplatesRegionRequestList) {
            String uniqueKey = DigestUtils.md5Hex(shippingTemplatesRegionRequest.toString());
            List<String> titleArray = JSONArray.parseArray(shippingTemplatesRegionRequest.getTitle(), String.class);
            if (shippingTemplatesRegionRequest.getCityId().equals("all") || shippingTemplatesRegionRequest.getCityId().equals("0")) {
                ShippingTemplatesRegion shippingTemplatesRegion = new ShippingTemplatesRegion();
                shippingTemplatesRegion.setCityId(0);
                shippingTemplatesRegion.setTitle(titleArray.get(0));
                shippingTemplatesRegion.setUniqid(uniqueKey);
                shippingTemplatesRegion.setRenewal(shippingTemplatesRegionRequest.getRenewal());
                shippingTemplatesRegion.setRenewalPrice(shippingTemplatesRegionRequest.getRenewalPrice());
                shippingTemplatesRegion.setFirst(shippingTemplatesRegionRequest.getFirst());
                shippingTemplatesRegion.setFirstPrice(shippingTemplatesRegionRequest.getFirstPrice());
                shippingTemplatesRegion.setTempId(tempId);
                shippingTemplatesRegion.setType(type);
                shippingTemplatesRegion.setStatus(true);
                shippingTemplatesRegionList.add(shippingTemplatesRegion);
            } else {
                List<Integer> cityIdList = CrmebUtil.stringToArray(shippingTemplatesRegionRequest.getCityId());
                Map<Integer, String> cityMap = new HashMap<>();
                titleArray.forEach(e -> {
                    JSONArray parseArray = JSONArray.parseArray(e);
                    Integer cityId = parseArray.getInteger(2);
                    cityMap.put(cityId, e);
                });
                for (Integer cityId : cityIdList) {
                    ShippingTemplatesRegion shippingTemplatesRegion = new ShippingTemplatesRegion();
                    shippingTemplatesRegion.setCityId(cityId);
                    shippingTemplatesRegion.setTitle(cityMap.get(cityId));
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
        }
        //批量保存模板数据
        return saveBatch(shippingTemplatesRegionList);
    }

    @Override
    public Boolean deleteByTempId(Integer tempId) {
        LambdaUpdateWrapper<ShippingTemplatesRegion> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(ShippingTemplatesRegion::getTempId, tempId);
        return dao.delete(wrapper) > 0;
    }

    /**
     * 获取所有城市cityId
     *
     * @return List<Integer>
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private List<Integer> getCityIdList() {
        if (this.cityIdList == null || this.cityIdList.size() < 1) {
            this.cityIdList = systemCityService.getCityIdList();
        }
        return this.cityIdList;
    }

    /**
     * 把模板下的所有数据标记为无效
     *
     * @param tempId Integer 运费模板id
     */
    private Boolean updateStatus(Integer tempId) {
        LambdaUpdateWrapper<ShippingTemplatesRegion> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ShippingTemplatesRegion::getStatus, false);
        wrapper.eq(ShippingTemplatesRegion::getTempId, tempId);
        return update(wrapper);
    }

    /**
     * 删除模板下的无效数据
     *
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
     *
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
     * 获取运费模板区域列表
     *
     * @param tempId 模板标号
     * @return List
     */
    @Override
    public List<ShippingTemplatesRegion> findListById(Integer tempId) {
        LambdaQueryWrapper<ShippingTemplatesRegion> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShippingTemplatesRegion::getTempId, tempId);
        lqw.eq(ShippingTemplatesRegion::getStatus, true);
        return dao.selectList(lqw);
    }

    /**
     * 分组查询
     *
     * @param tempId Integer 运费模板id
     * @return List<ShippingTemplatesRegionRequest>
     */
    @Override
    public List<ShippingTemplatesRegionResponse> getListGroup(Integer tempId) {
        LambdaQueryWrapper<ShippingTemplatesRegion> lqw = Wrappers.lambdaQuery();
        lqw.select(ShippingTemplatesRegion::getTitle, ShippingTemplatesRegion::getFirst, ShippingTemplatesRegion::getFirstPrice,
                ShippingTemplatesRegion::getRenewal, ShippingTemplatesRegion::getRenewalPrice, ShippingTemplatesRegion::getUniqid);
        lqw.eq(ShippingTemplatesRegion::getTempId, tempId);
        List<ShippingTemplatesRegion> list = dao.selectList(lqw);
        List<ShippingTemplatesRegionResponse> responseList = CollUtil.newArrayList();
        if (CollUtil.isEmpty(list)) {
            return responseList;
        }
        Map<String, List<ShippingTemplatesRegion>> listMap = list.stream().collect(Collectors.groupingBy(ShippingTemplatesRegion::getUniqid, LinkedHashMap::new, Collectors.toList()));
        Set<Map.Entry<String, List<ShippingTemplatesRegion>>> entrySet = listMap.entrySet();
        for (Map.Entry<String, List<ShippingTemplatesRegion>> entry : entrySet) {
            String uniqid = entry.getKey();
            String title = entry.getValue().stream().map(ShippingTemplatesRegion::getTitle).collect(Collectors.joining(","));
            ShippingTemplatesRegion region = entry.getValue().get(0);
            ShippingTemplatesRegionResponse response = new ShippingTemplatesRegionResponse();
            response.setTitle(title);
            response.setUniqid(uniqid);
            response.setFirst(region.getFirst());
            response.setFirstPrice(region.getFirstPrice());
            response.setRenewal(region.getRenewal());
            response.setRenewalPrice(region.getRenewalPrice());
            responseList.add(response);
        }
        return responseList;
    }
}

