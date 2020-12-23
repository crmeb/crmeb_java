package com.zbkj.crmeb.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constants.Constants;
import com.utils.RedisUtil;
import com.zbkj.crmeb.system.dao.SystemCityDao;
import com.zbkj.crmeb.system.model.SystemCity;
import com.zbkj.crmeb.system.service.SystemCityAsyncService;
import com.zbkj.crmeb.system.vo.SystemCityTreeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SystemCityAsyncServiceImpl 接口实现
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
public class SystemCityAsyncServiceImpl extends ServiceImpl<SystemCityDao, SystemCity> implements SystemCityAsyncService {

    @Resource
    private SystemCityDao dao;

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 设置属性列表进入redis
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @Override
    public void setListTree() {
        //循环数据，把数据对象变成带list结构的vo
        List<SystemCityTreeVo> treeList = new ArrayList<>();

        LambdaQueryWrapper<SystemCity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(SystemCity::getCityId, SystemCity::getParentId, SystemCity::getName);
        lambdaQueryWrapper.eq(SystemCity::getIsShow, true);
        List<SystemCity> allTree = dao.selectList(lambdaQueryWrapper);

        if(allTree == null){
            return;
        }

        for (SystemCity systemCity: allTree) {
            SystemCityTreeVo systemCityTreeVo = new SystemCityTreeVo();
            BeanUtils.copyProperties(systemCity, systemCityTreeVo);
            treeList.add(systemCityTreeVo);
        }


        //返回
        Map<Integer, SystemCityTreeVo> map = new HashMap<>();
        //cityId 为 key 存储到map 中
        for (SystemCityTreeVo systemCityTreeVo1 : treeList) {
            map.put(systemCityTreeVo1.getCityId(), systemCityTreeVo1);
        }


        List<SystemCityTreeVo> list = new ArrayList<>();
        for (SystemCityTreeVo tree : treeList) {
            //子集ID返回对象，有则添加。
            SystemCityTreeVo tree1 = map.get(tree.getParentId());
            if(tree1 != null){
                tree1.getChild().add(tree);
            }else {
                list.add(tree);
            }
        }
        redisUtil.set(Constants.CITY_LIST_TREE, list);
    }

    /**
     * 数据整体刷入redis
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    private void asyncList(Integer pid){
        LambdaQueryWrapper<SystemCity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemCity::getParentId, pid);
        lambdaQueryWrapper.in(SystemCity::getIsShow, true);
        List<SystemCity> systemCityList = dao.selectList(lambdaQueryWrapper);
        if(systemCityList != null && systemCityList.size() > 0){
            redisUtil.hmSet(Constants.CITY_LIST, pid.toString(), systemCityList);
        }
    }

    /**
     * 数据整体刷入redis
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @Async
    public void async(Integer pid){
        asyncList(pid);
        setListTree();
    }
}

