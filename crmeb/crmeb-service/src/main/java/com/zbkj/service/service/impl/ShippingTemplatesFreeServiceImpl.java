package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.express.ShippingTemplatesFree;
import com.zbkj.common.request.ShippingTemplatesFreeRequest;
import com.zbkj.common.response.ShippingTemplatesFreeResponse;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.service.dao.ShippingTemplatesFreeDao;
import com.zbkj.service.service.ShippingTemplatesFreeService;
import com.zbkj.service.service.SystemCityService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ShippingTemplatesFreeServiceImpl 接口实现
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
public class ShippingTemplatesFreeServiceImpl extends ServiceImpl<ShippingTemplatesFreeDao, ShippingTemplatesFree> implements ShippingTemplatesFreeService {

    @Resource
    private ShippingTemplatesFreeDao dao;

    @Autowired
    private SystemCityService systemCityService;

    private List<Integer> cityIdList;

    /**
     * 保存配送区域
     *
     * @param shippingTemplatesFreeRequestList List<ShippingTemplatesFreeRequest> 运费集合
     * @param type                             Integer 计费方式
     * @param tempId                           Integer 运费模板id
     */
    @Override
    public Boolean saveAll(List<ShippingTemplatesFreeRequest> shippingTemplatesFreeRequestList, Integer type, Integer tempId) {
        ArrayList<ShippingTemplatesFree> shippingTemplatesFreesList = new ArrayList<>();
        for (ShippingTemplatesFreeRequest shippingTemplatesFreeRequest : shippingTemplatesFreeRequestList) {
            String uniqueKey = DigestUtils.md5Hex(shippingTemplatesFreeRequest.toString());
            List<String> titleArray = JSONArray.parseArray(shippingTemplatesFreeRequest.getTitle(), String.class);
            if (shippingTemplatesFreeRequest.getCityId().equals("all") || shippingTemplatesFreeRequest.getCityId().equals("0")) {
                ShippingTemplatesFree shippingTemplatesFree = new ShippingTemplatesFree();
                shippingTemplatesFree.setCityId(0);
                shippingTemplatesFree.setTitle(titleArray.get(0));
                shippingTemplatesFree.setUniqid(uniqueKey);
                shippingTemplatesFree.setTempId(tempId);
                shippingTemplatesFree.setType(type);
                shippingTemplatesFree.setStatus(true);
                shippingTemplatesFree.setNumber(shippingTemplatesFreeRequest.getNumber());
                shippingTemplatesFree.setPrice(shippingTemplatesFreeRequest.getPrice());
                shippingTemplatesFreesList.add(shippingTemplatesFree);
            } else {
                cityIdList = CrmebUtil.stringToArray(shippingTemplatesFreeRequest.getCityId());
                Map<Integer, String> cityMap = new HashMap<>();
                titleArray.forEach(e -> {
                    JSONArray parseArray = JSONArray.parseArray(e);
                    Integer cityId = parseArray.getInteger(2);
                    cityMap.put(cityId, e);
                });
                for (Integer cityId : cityIdList) {
                    ShippingTemplatesFree shippingTemplatesFree = new ShippingTemplatesFree();
                    shippingTemplatesFree.setCityId(cityId);
                    shippingTemplatesFree.setTitle(cityMap.get(cityId));
                    shippingTemplatesFree.setUniqid(uniqueKey);
                    shippingTemplatesFree.setTempId(tempId);
                    shippingTemplatesFree.setType(type);
                    shippingTemplatesFree.setNumber(shippingTemplatesFreeRequest.getNumber());
                    shippingTemplatesFree.setPrice(shippingTemplatesFreeRequest.getPrice());
                    shippingTemplatesFree.setStatus(true);
                    shippingTemplatesFreesList.add(shippingTemplatesFree);
                }
            }
        }
        //批量保存模板数据
        return saveBatch(shippingTemplatesFreesList);
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
     *
     * @param tempId Integer 运费模板id
     */
    @Override
    public Boolean deleteByTempId(Integer tempId) {
        LambdaUpdateWrapper<ShippingTemplatesFree> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(ShippingTemplatesFree::getTempId, tempId);
        return dao.delete(wrapper) > 0;
    }

    /**
     * 根据模板编号、城市ID查询
     *
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
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 分组查询
     *
     * @param tempId Integer 运费模板id
     * @return List<ShippingTemplatesFreeRequest>
     */
    @Override
    public List<ShippingTemplatesFreeResponse> getListGroup(Integer tempId) {
        LambdaQueryWrapper<ShippingTemplatesFree> lqw = Wrappers.lambdaQuery();
        lqw.select(ShippingTemplatesFree::getTitle, ShippingTemplatesFree::getNumber, ShippingTemplatesFree::getPrice,
                ShippingTemplatesFree::getUniqid);
        lqw.eq(ShippingTemplatesFree::getTempId, tempId);
        List<ShippingTemplatesFree> list = dao.selectList(lqw);
        List<ShippingTemplatesFreeResponse> responseList = CollUtil.newArrayList();
        if (CollUtil.isEmpty(list)) {
            return responseList;
        }
        Map<String, List<ShippingTemplatesFree>> listMap = list.stream().collect(Collectors.groupingBy(ShippingTemplatesFree::getUniqid, LinkedHashMap::new, Collectors.toList()));
        Set<Map.Entry<String, List<ShippingTemplatesFree>>> entrySet = listMap.entrySet();
        for (Map.Entry<String, List<ShippingTemplatesFree>> entry : entrySet) {
            String uniqid = entry.getKey();
            String title = entry.getValue().stream().map(ShippingTemplatesFree::getTitle).collect(Collectors.joining(","));
            ShippingTemplatesFree free = entry.getValue().get(0);
            ShippingTemplatesFreeResponse response = new ShippingTemplatesFreeResponse();
            response.setTitle(title);
            response.setUniqid(uniqid);
            response.setNumber(free.getNumber());
            response.setPrice(free.getPrice());
            responseList.add(response);
        }
        return responseList;
    }
}

