package com.zbkj.crmeb.store.service.impl;

import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;

import com.zbkj.crmeb.store.model.StoreOrderStatus;
import com.zbkj.crmeb.store.dao.StoreOrderStatusDao;
import com.zbkj.crmeb.store.request.StoreOrderStatusSearchRequest;
import com.zbkj.crmeb.store.service.StoreOrderStatusService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * StoreOrderStatusServiceImpl 接口实现
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
public class StoreOrderStatusServiceImpl extends ServiceImpl<StoreOrderStatusDao, StoreOrderStatus> implements StoreOrderStatusService {

    @Resource
    private StoreOrderStatusDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-28
    * @return List<StoreOrderStatus>
    */
    @Override
    public List<StoreOrderStatus> getList(StoreOrderStatusSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreOrderStatus 类的多条件查询
        LambdaQueryWrapper<StoreOrderStatus> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreOrderStatus model = new StoreOrderStatus();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 保存订单退款记录
     * @param orderId Integer 订单号
     * @param amount BigDecimal 金额
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @Async
    @Override
    public void saveRefund(Integer orderId, BigDecimal amount, String message) {
        //此处更新订单状态
        String changeMessage = Constants.ORDER_LOG_MESSAGE_REFUND_PRICE.replace("{amount}", amount.toString());
        if(StringUtils.isNotBlank(message)){
            changeMessage += message;
        }
        createLog(orderId, Constants.ORDER_LOG_REFUND_PRICE, changeMessage);
    }

    /**
     * 创建记录日志
     * @param orderId Integer 订单号
     * @param type String 类型
     * @param message String 消息
     * @author Mr.Zhang
     * @since 2020-05-28
     */
    @Override
    public void createLog(Integer orderId, String type, String message) {
        try{
            StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
            storeOrderStatus.setOid(orderId);
            storeOrderStatus.setChangeType(type);
            storeOrderStatus.setChangeMessage(message);
            save(storeOrderStatus);
        }catch (Exception e){
            throw new CrmebException("日志记录失败");
        }
    }

    /**
     * 根据实体获取
     * @param storeOrderStatus 订单状态参数
     * @return 查询结果
     */
    @Override
    public List<StoreOrderStatus> getByEntity(StoreOrderStatus storeOrderStatus) {
        LambdaQueryWrapper<StoreOrderStatus> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.setEntity(storeOrderStatus);
        return dao.selectList(lambdaQueryWrapper);
    }

    public Boolean addLog(Integer orderId, String type, String message) {
        StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
        storeOrderStatus.setOid(orderId);
        storeOrderStatus.setChangeType(type);
        storeOrderStatus.setChangeMessage(message);
        return save(storeOrderStatus);
    }
}

