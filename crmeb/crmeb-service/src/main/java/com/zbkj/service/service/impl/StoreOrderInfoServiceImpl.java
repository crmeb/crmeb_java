package com.zbkj.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.vo.OrderInfoDetailVo;
import com.zbkj.common.vo.StoreOrderInfoOldVo;
import com.zbkj.common.vo.StoreOrderInfoVo;
import com.zbkj.common.model.order.StoreOrderInfo;
import com.zbkj.service.dao.StoreOrderInfoDao;
import com.zbkj.service.service.StoreOrderInfoService;
import com.zbkj.service.service.StoreProductReplyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * StoreOrderInfoServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class StoreOrderInfoServiceImpl extends ServiceImpl<StoreOrderInfoDao, StoreOrderInfo>
        implements StoreOrderInfoService {

    @Resource
    private StoreOrderInfoDao dao;

    @Autowired
    private StoreProductReplyService storeProductReplyService;

    /**
     * 根据id集合查询数据，返回 map
     * @param orderList List<Integer> id集合
     * @author Mr.Zhang
     * @since 2020-04-17
     * @return HashMap<Integer, StoreCart>
     */
    @Override
    public HashMap<Integer, List<StoreOrderInfoOldVo>> getMapInId(List<Integer> orderList){
        HashMap<Integer, List<StoreOrderInfoOldVo>> map = new HashMap<>();
        if(orderList.size() < 1){
            return map;
        }
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.in(StoreOrderInfo::getOrderId, orderList);
        List<StoreOrderInfo> systemStoreStaffList = dao.selectList(lambdaQueryWrapper);
        if(systemStoreStaffList.size() < 1){
            return map;
        }
        for (StoreOrderInfo storeOrderInfo : systemStoreStaffList) {
            //解析商品详情JSON
            StoreOrderInfoOldVo StoreOrderInfoVo = new StoreOrderInfoOldVo();
            BeanUtils.copyProperties(storeOrderInfo, StoreOrderInfoVo, "info");
            StoreOrderInfoVo.setInfo(JSON.parseObject(storeOrderInfo.getInfo(), OrderInfoDetailVo.class));
            if(map.containsKey(storeOrderInfo.getOrderId())){
                map.get(storeOrderInfo.getOrderId()).add(StoreOrderInfoVo);
            }else{
                List<StoreOrderInfoOldVo> storeOrderInfoVoList = new ArrayList<>();
                storeOrderInfoVoList.add(StoreOrderInfoVo);
                map.put(storeOrderInfo.getOrderId(), storeOrderInfoVoList);
            }
        }
        return map;
    }

    /**
     * 根据id集合查询数据，返回 map
     * @param orderId Integer id
     * @author Mr.Zhang
     * @since 2020-04-17
     * @return HashMap<Integer, StoreCart>
     */
    @Override
    public List<StoreOrderInfoOldVo> getOrderListByOrderId(Integer orderId){
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreOrderInfo::getOrderId, orderId);
        List<StoreOrderInfo> systemStoreStaffList = dao.selectList(lambdaQueryWrapper);
        if(systemStoreStaffList.size() < 1){
            return null;
        }

        List<StoreOrderInfoOldVo> storeOrderInfoVoList = new ArrayList<>();
        for (StoreOrderInfo storeOrderInfo : systemStoreStaffList) {
            //解析商品详情JSON
            StoreOrderInfoOldVo storeOrderInfoVo = new StoreOrderInfoOldVo();
            BeanUtils.copyProperties(storeOrderInfo, storeOrderInfoVo, "info");
            storeOrderInfoVo.setInfo(JSON.parseObject(storeOrderInfo.getInfo(), OrderInfoDetailVo.class));
            storeOrderInfoVo.getInfo().setIsReply(
                    storeProductReplyService.isReply(storeOrderInfoVo.getUnique(), storeOrderInfoVo.getOrderId()) ? 1 : 0
            );
            storeOrderInfoVoList.add(storeOrderInfoVo);
        }
        return storeOrderInfoVoList;
    }

    /**
     * 根据id集合查询数据，返回 map
     * @param orderId 订单id
     * @return HashMap<Integer, StoreCart>
     */
    @Override
    public List<StoreOrderInfoVo> getVoListByOrderId(Integer orderId){
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreOrderInfo::getOrderId, orderId);
        List<StoreOrderInfo> systemStoreStaffList = dao.selectList(lambdaQueryWrapper);
        if(systemStoreStaffList.size() < 1){
            return null;
        }

        List<StoreOrderInfoVo> storeOrderInfoVoList = new ArrayList<>();
        for (StoreOrderInfo storeOrderInfo : systemStoreStaffList) {
            //解析商品详情JSON
            StoreOrderInfoVo storeOrderInfoVo = new StoreOrderInfoVo();
            BeanUtils.copyProperties(storeOrderInfo, storeOrderInfoVo, "info");
            storeOrderInfoVo.setInfo(JSON.parseObject(storeOrderInfo.getInfo(), OrderInfoDetailVo.class));
            storeOrderInfoVoList.add(storeOrderInfoVo);
        }
        return storeOrderInfoVoList;
    }

    /**
     * 获取订单详情-订单编号
     * @param orderNo 订单编号
     * @return List
     */
    @Override
    public List<StoreOrderInfo> getListByOrderNo(String orderNo) {
        LambdaQueryWrapper<StoreOrderInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrderInfo::getOrderNo, orderNo);
        return dao.selectList(lqw);
    }

    /**
     * 根据时间、商品id获取销售件数
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     * @return Integer
     */
    @Override
    public Integer getSalesNumByDateAndProductId(String date, Integer proId) {
        return dao.getSalesNumByDateAndProductId(date, proId);
    }

    /**
     * 根据时间、商品id获取销售额
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSalesByDateAndProductId(String date, Integer proId) {
        return dao.getSalesByDateAndProductId(date, proId);
    }

    /**
     * 新增订单详情
     * @param storeOrderInfos 订单详情集合
     * @return 订单新增结果
     */
    @Override
    public boolean saveOrderInfos(List<StoreOrderInfo> storeOrderInfos) {
        return saveBatch(storeOrderInfos);
    }

    /**
     * 通过订单编号和规格号查询
     * @param uni 规格号
     * @param orderId 订单编号
     * @return StoreOrderInfo
     */
    @Override
    public StoreOrderInfo getByUniAndOrderId(String uni, Integer orderId) {
        //带 StoreOrderInfo 类的多条件查询
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreOrderInfo::getOrderId, orderId);
        lambdaQueryWrapper.eq(StoreOrderInfo::getUnique, uni);
        return dao.selectOne(lambdaQueryWrapper);
    }
}

