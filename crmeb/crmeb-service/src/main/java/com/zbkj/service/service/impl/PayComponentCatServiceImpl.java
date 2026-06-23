package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.model.wechat.video.PayComponentCat;
import com.zbkj.common.vo.*;
import com.zbkj.service.dao.PayComponentCatDao;
import com.zbkj.service.service.PayComponentCatService;
import com.zbkj.service.service.WechatVideoSpuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Service
public class PayComponentCatServiceImpl extends ServiceImpl<PayComponentCatDao, PayComponentCat> implements PayComponentCatService {

    private final Logger logger = LoggerFactory.getLogger(PayComponentCatServiceImpl.class);

    @Resource
    private PayComponentCatDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private WechatVideoSpuService wechatVideoSpuService;
    @Autowired
    private RedisUtil redisUtil;

    private final String PAY_COMPONENT_CAT_LIST = "pay_component_cat_list";

    /**
     * 获取类目
     * @return List<FirstCatVo>
     */
    @Override
    public List<CatItem> getList() {
        if (!redisUtil.exists(PAY_COMPONENT_CAT_LIST)) {
            autoUpdate();
        }
        String catJsonStr = (String) redisUtil.get(PAY_COMPONENT_CAT_LIST);
        List<CatItem> firstCatVoList = JSONArray.parseArray(catJsonStr, CatItem.class);
        return firstCatVoList;
    }

    /**
     * 根据第三级id获取类目
     * @param thirdCatId 第三级id
     * @return PayComponentCat
     */
    @Override
    public PayComponentCat getByThirdCatId(Integer thirdCatId) {
        LambdaQueryWrapper<PayComponentCat> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentCat::getThirdCatId, thirdCatId);
        return dao.selectOne(lqw);
    }

    /**
     * 自动更新自定义交易组件类目
     * 1.获取最新的类目信息
     * 2.更新数据库
     * 3.更新缓存
     */
    @Override
    public void autoUpdate() {
        List<ShopCatDetailVo> catDetailVoList = wechatVideoSpuService.getShopCat();
        if (CollUtil.isEmpty(catDetailVoList)) {
            logger.info("微信未返回类目信息");
            return ;
        }
        List<PayComponentCat> catList = catDetailVoList.stream().map(e -> {
            PayComponentCat payComponentCat = new PayComponentCat();
            BeanUtils.copyProperties(e, payComponentCat);
            return payComponentCat;
        }).collect(Collectors.toList());

        Boolean execute = transactionTemplate.execute(e -> {
            // 清空原有数据
            dao.deleteAll();
            // 插入现在的数据
            saveBatch(catList);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("自动更新自定义交易组件类目,操作数据库时出错");
        }
        // 清空Redis缓存，重新添加
        Boolean updateCatRedis = updateCatRedis(catList);
        if (!updateCatRedis) {
            throw new CrmebException("自动更新自定义交易组件类目,操作Redis时出错");
        }
        logger.info(StrUtil.format("自动更新自定义交易组件类目成功，时间{}", DateUtil.now()));
    }

    /**
     * 更新类目缓存
     * @param catList 类目列表
     */
    private Boolean updateCatRedis(List<PayComponentCat> catList) {
        // 组装缓存数据
//        List<FirstCatVo> catVoList = assembleRedisData(catList);
        List<CatItem> catVoList = assembleRedisDataForFront(catList);

        if (redisUtil.exists(PAY_COMPONENT_CAT_LIST)) {
            redisUtil.delete(PAY_COMPONENT_CAT_LIST);
        }
        return redisUtil.set(PAY_COMPONENT_CAT_LIST, JSONArray.toJSONString(catVoList));
    }

    /**
     * 组装redis数据
     * @param catList 类目列表
     * @return List<FirstCatVo>
     */
    private List<FirstCatVo> assembleRedisData(List<PayComponentCat> catList) {
        // 第一级
        HashMap<Integer, String> firstMap = CollUtil.newHashMap();
        catList.forEach(e -> {
            if (!firstMap.containsKey(e.getFirstCatId())) {
                firstMap.put(e.getFirstCatId(), e.getFirstCatName());
            }
        });
        List<FirstCatVo> voList = CollUtil.newArrayList();
        firstMap.forEach((k, v) -> {
            FirstCatVo firstCatVo = new FirstCatVo();
            firstCatVo.setFirstCatId(k);
            firstCatVo.setFirstCatName(v);
            voList.add(firstCatVo);
        });
        // 第二级
        voList.forEach(e -> {
            HashMap<Integer, String> secondMap = CollUtil.newHashMap();
            catList.stream().filter(a -> a.getFirstCatId().equals(e.getFirstCatId())).forEach(cat -> {
                if (!secondMap.containsKey(cat.getSecondCatId())) {
                    secondMap.put(cat.getSecondCatId(), cat.getSecondCatName());
                }
            });
            List<SecondCatVo> secondCatVoList = CollUtil.newArrayList();
            secondMap.forEach((k, v) -> {
                SecondCatVo secondCatVo = new SecondCatVo();
                secondCatVo.setSecondCatId(k);
                secondCatVo.setSecondCatName(v);
                secondCatVoList.add(secondCatVo);
            });
            // 第三级
            secondCatVoList.forEach(b -> {
                List<ThirdCatVo> thirdCatVoList = catList.stream().filter(i -> i.getSecondCatId().equals(b.getSecondCatId())).map(o -> {
                    ThirdCatVo thirdCatVo = new ThirdCatVo();
                    thirdCatVo.setThirdCatId(o.getThirdCatId());
                    thirdCatVo.setThirdCatName(o.getThirdCatName());
                    thirdCatVo.setQualification(o.getQualification());
                    thirdCatVo.setQualificationType(o.getQualificationType());
                    thirdCatVo.setProductQualification(o.getProductQualification());
                    thirdCatVo.setProductQualificationType(o.getProductQualificationType());
                    return thirdCatVo;
                }).collect(Collectors.toList());
                b.setThirdCatList(thirdCatVoList);
            });
            e.setSecondCatList(secondCatVoList);
        });
        return voList;
    }

    /**
     * 组装redis数据 数据对应前端组件
     * @param catList 类目列表
     * @return List<FirstCatVo>
     */
    private List<CatItem> assembleRedisDataForFront(List<PayComponentCat> catList) {
        // 第一级
        HashMap<Integer, String> firstMap = CollUtil.newHashMap();
        catList.forEach(e -> {
            if (!firstMap.containsKey(e.getFirstCatId())) {
                firstMap.put(e.getFirstCatId(), e.getFirstCatName());
            }
        });
        List<CatItem> catItem1s = CollUtil.newArrayList();
        firstMap.forEach((k, v) -> {
            catItem1s.add(new CatItem(k,v,new ArrayList<>()));
        });
        // 第二级
        catItem1s.forEach(e -> {
            HashMap<Integer, String> secondMap = CollUtil.newHashMap();
            catList.stream().filter(a -> a.getFirstCatId().equals(e.getValue())).forEach(cat -> {
                if (!secondMap.containsKey(cat.getSecondCatId())) {
                    secondMap.put(cat.getSecondCatId(), cat.getSecondCatName());
                }
            });
            List<CatItem> catItem2s = CollUtil.newArrayList();
            secondMap.forEach((k, v) -> {
                CatItem item = new CatItem();
                item.setLabel(v);
                item.setValue(k);
                catItem2s.add(item);
            });
            // 第三级
            catItem2s.forEach(b -> {
                List<CatItem> catItem3s = CollUtil.newArrayList();
                List<ThirdCatVo> thirdCatVoList = catList.stream().filter(i -> i.getSecondCatId().equals(b.getValue())).map(o -> {
                    ThirdCatVo thirdCatVo = new ThirdCatVo();
                    CatItem item = new CatItem();
                    item.setLabel(o.getThirdCatName());
                    item.setValue(o.getThirdCatId());
                    item.setQualification(o.getQualification());
                    item.setQualificationType(o.getQualificationType());
                    item.setProductQualification(o.getProductQualification());
                    item.setProductQualificationType(o.getProductQualificationType());
                    catItem3s.add(item);
                    return thirdCatVo;
                }).collect(Collectors.toList());
                b.setChildren(catItem3s);
            });
            e.setChildren(catItem2s);
        });
        return catItem1s;
    }
}

