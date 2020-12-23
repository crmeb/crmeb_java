package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Constant;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.store.dao.StoreProductAttrValueDao;
import com.zbkj.crmeb.store.model.StoreProductAttrValue;
import com.zbkj.crmeb.store.request.StoreProductAttrValueSearchRequest;
import com.zbkj.crmeb.store.service.StoreProductAttrValueService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreProductAttrValueServiceImpl 接口实现
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
public class StoreProductAttrValueServiceImpl extends ServiceImpl<StoreProductAttrValueDao, StoreProductAttrValue>
        implements StoreProductAttrValueService {

    @Resource
    private StoreProductAttrValueDao dao;

    @Autowired
    private SystemConfigService systemConfigService;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-27
    * @return List<StoreProductAttrValue>
    */
    @Override
    public List<StoreProductAttrValue> getList(StoreProductAttrValueSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductAttrValue 类的多条件查询
        LambdaQueryWrapper<StoreProductAttrValue> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreProductAttrValue model = new StoreProductAttrValue();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据商品id获取attrValues
     * @param productId 商品id
     * @return 包含商品id的Attrvalues
     */
    @Override
    public List<StoreProductAttrValue> getListByProductId(Integer productId) {
        LambdaQueryWrapper<StoreProductAttrValue> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreProductAttrValue::getProductId, productId);
        List<StoreProductAttrValue> ll = dao.selectList(lambdaQueryWrapper);
        return ll;
    }

    /**
     *
     * @param productId 商品id
     * @param attrId 属性id
     * @return 商品属性集合
     */
    @Override
    public List<StoreProductAttrValue> getListByProductIdAndAttrId(Integer productId, String attrId, Integer type) {
        LambdaQueryWrapper<StoreProductAttrValue> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(StoreProductAttrValue::getProductId, productId);
        lambdaQueryWrapper.eq(StoreProductAttrValue::getType, type);
        if(null != attrId){
            lambdaQueryWrapper.eq(StoreProductAttrValue::getId, attrId);
        }
        List<StoreProductAttrValue> ll = dao.selectList(lambdaQueryWrapper);
        return ll;
    }

    /**
     * 根据产品属性查询
     * @param storeProductAttrValue 商品属性参数
     * @return 查询到的属性结果
     */
    @Override
    public List<StoreProductAttrValue> getByEntity(StoreProductAttrValue storeProductAttrValue) {
        LambdaQueryWrapper<StoreProductAttrValue> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeProductAttrValue);
        return dao.selectList(lqw);
    }

    /**
     * 减库存增加销量
     * @param productId 商品id
     * @param attrValueId 商品attrValue id
     * @param num 销售数量
     * @param type 营销活动
     * @return 操作后的结果标识
     */
    @Override
    public boolean decProductAttrStock(Integer productId, Integer attrValueId, Integer num, Integer type) {
        List<StoreProductAttrValue> existAttrValues = getListByProductIdAndAttrId(productId, attrValueId+"",type);
        if(existAttrValues.size() == 0) throw new CrmebException("商品不存在");

        StoreProductAttrValue attrValue = existAttrValues.get(0);
        boolean result = false;

        LambdaUpdateWrapper<StoreProductAttrValue> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(StoreProductAttrValue::getProductId, productId);
        lambdaUpdateWrapper.eq(StoreProductAttrValue::getId, attrValueId);
        lambdaUpdateWrapper.eq(StoreProductAttrValue::getType, type);


        if(type == Constants.PRODUCT_TYPE_NORMAL){
            if(num > attrValue.getStock()){
                throw new CrmebException("库存不足");
            }
            lambdaUpdateWrapper.set(StoreProductAttrValue::getStock, attrValue.getStock()-num);
            lambdaUpdateWrapper.set(StoreProductAttrValue::getSales, attrValue.getSales()+num);
            result = update(lambdaUpdateWrapper);
        }else{
            if(num > attrValue.getQuota()){
                throw new CrmebException("库存不足");
            }
            lambdaUpdateWrapper.set(StoreProductAttrValue::getStock, attrValue.getStock()-num);
            lambdaUpdateWrapper.set(StoreProductAttrValue::getSales, attrValue.getSales()+num);
            lambdaUpdateWrapper.set(StoreProductAttrValue::getQuota, attrValue.getQuota()-num);
            result = update(lambdaUpdateWrapper);
        }

        if(result){ //判断库存警戒值
            Integer alterNumI=0;
            String alterNum = systemConfigService.getValueByKey("store_stock");
            if(StringUtils.isNotBlank(alterNum)) alterNumI = Integer.parseInt(alterNum);
            if(alterNumI >= attrValue.getStock()){
                // todo socket 发送库存警告
            }
        }
        return true;
    }

    /**
     * 根据商品id删除AttrValue
     * @param productId 商品id
     * @param type 类型区分是是否添加营销
     * @reture 删除结果
     */
    @Override
    public boolean removeByProductId(Integer productId,int type) {
        LambdaQueryWrapper<StoreProductAttrValue> lambdaQW = Wrappers.lambdaQuery();
        lambdaQW.eq(StoreProductAttrValue::getProductId, productId).eq(StoreProductAttrValue::getType,type);
        return dao.delete(lambdaQW) > 0;
    }
}

