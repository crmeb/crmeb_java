package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.wechat.video.*;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.model.product.StoreProductAttr;
import com.zbkj.common.model.product.StoreProductAttrValue;
import com.zbkj.common.request.StoreProductAttrValueRequest;
import com.zbkj.common.model.wechat.WechatCallback;
import com.zbkj.common.vo.ShopSpuAuditVo;
import com.zbkj.common.vo.ShopSpuCommonVo;
import com.zbkj.common.vo.ShopSpuVo;
import com.zbkj.service.dao.WechatCallbackDao;
import com.zbkj.service.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 微信小程序回调Service
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class WechatCallbackServiceImpl extends ServiceImpl<WechatCallbackDao, WechatCallback> implements WechatCallbackService {

    private static final Logger logger = LoggerFactory.getLogger(WechatCallbackServiceImpl.class);

    @Resource
    private WechatCallbackDao dao;

    @Autowired
    private PayComponentProductService componentProductService;
    @Autowired
    private PayComponentDraftProductService componentDraftProductService;
    @Autowired
    private WechatVideoSpuService wechatVideoSpuService;
    @Autowired
    private PayComponentProductAuditInfoService componentProductAuditInfoService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private PayComponentProductInfoService componentProductInfoService;
    @Autowired
    private PayComponentProductSkuService componentProductSkuService;
    @Autowired
    private PayComponentProductSkuAttrService componentProductSkuAttrService;
    @Autowired
    private StoreProductAttrService productAttrService;
    @Autowired
    private StoreProductAttrValueService productAttrValueService;
    @Autowired
    private PayComponentShopBrandService componentShopBrandService;

    /**
     * 微信回调
     * @param request request
     * @return String
     */
    @Override
    public String callback(String request) {
        System.out.println("微信小程序回调：" + request);
        JSONObject jsonObject = JSONObject.parseObject(request);
        System.out.println("微信小程序回调jsonObject：" + jsonObject);
        WechatCallback wechatCallback = new WechatCallback();
        wechatCallback.setToUserName(jsonObject.getString("ToUserName"));
        wechatCallback.setFromUserName(jsonObject.getString("FromUserName"));
        wechatCallback.setCreateTime(jsonObject.getLong("CreateTime"));
        wechatCallback.setMsgType(jsonObject.getString("MsgType"));
        wechatCallback.setEvent(jsonObject.getString("Event"));
        wechatCallback.setAddTime(DateUtil.date());
        wechatCallback.setContent(request);
        save(wechatCallback);
        switch (wechatCallback.getEvent()) {
            case WeChatConstants.WECAHT_CALLBACK_EVENT_SPU_AUDIT:// 商品审核
                spuAuditEvent(jsonObject);
                break;
            case WeChatConstants.WECAHT_CALLBACK_EVENT_BRAND_AUDIT:// 品牌审核
                brandAuditEvent(jsonObject);
                break;
        }
        return "success";
    }

    /**
     * 品牌审核时间
     * @param jsonObject jsonObject
     */
    private void brandAuditEvent(JSONObject jsonObject) {
        String auditId = jsonObject.getString("audit_id");
        // 查找品牌记录
        PayComponentShopBrand shopBrand = componentShopBrandService.getByAuditId(auditId);
        if (ObjectUtil.isNull(shopBrand)) {
            logger.error(StrUtil.format("品牌审核回调未找到品牌id，审核id:{}", auditId));
            return ;
        }
        Integer status = jsonObject.getInteger("status");
        if (status.equals(1)) { // 审核通过
            shopBrand.setBrandId(jsonObject.getInteger("brand_id"));
        }
        if (status.equals(9)) {// 审核拒绝
            shopBrand.setRejectReason(jsonObject.getString("reject_reason"));
            if (ObjectUtil.isNotNull(jsonObject.getInteger("brand_id"))) {
                shopBrand.setBrandId(jsonObject.getInteger("brand_id"));
            }
        }
        shopBrand.setStatus(status);
        boolean update = componentShopBrandService.updateById(shopBrand);
        if (!update) {
            logger.error(StrUtil.format("品牌审核回调保存数据出错，审核id:{}", auditId));
        }
    }

    /**
     * 商品审核事件
     * 1.先判断是否审核通过
     * 2.如果审核未通过，修改草稿商品表状态、记录失败原因
     * 3.如果审核通过，修改商品表，修改草稿商品表状态，修改sku表，记录审核表
     * @param jsonObject jsonObject
     */
    private void spuAuditEvent(JSONObject jsonObject) {
//        JSONObject spuAudit = jsonObject.getJSONObject("OpenProductSpuAudit");
        String outProductId = jsonObject.getString("out_product_id");
        System.out.println("outProductId = " + outProductId);
        if (jsonObject.getInteger("status").equals(3)) {// 审核未通过
            logger.warn(StrUtil.format("商品审核未通过，商品id:{}，失败原因:{}", outProductId, jsonObject.getString("reject_reason")));
            // 查找对应的草稿商品
            PayComponentDraftProduct draftProduct = componentDraftProductService.getByProId(Integer.valueOf(outProductId));
            if (ObjectUtil.isNull(draftProduct) || draftProduct.getIsDel()) {
                logger.error(StrUtil.format("商品审核未通过，未找到草稿商品，商品id:{}，失败原因:{}", outProductId, jsonObject.getString("reject_reason")));
                return ;
            }
            ShopSpuCommonVo shopSpuCommonVo = new ShopSpuCommonVo();
            shopSpuCommonVo.setOutProductId(outProductId);
            shopSpuCommonVo.setNeedEditSpu(1);
            ShopSpuVo shopSpuVo = wechatVideoSpuService.shopSpuGet(shopSpuCommonVo);
            draftProduct.setEditStatus(shopSpuVo.getEditStatus());
            draftProduct.setStatus(shopSpuVo.getStatus());
            draftProduct.setUpdateTime(shopSpuVo.getUpdateTime());
            componentDraftProductService.updateById(draftProduct);

            // 保存审核数据
            ShopSpuAuditVo shopSpuAuditVo = shopSpuVo.getAuditInfo();
            PayComponentProductAuditInfo auditInfo = componentProductAuditInfoService.getByProductIdAndAuditId(Integer.valueOf(outProductId), shopSpuAuditVo.getAuditId());
            if (ObjectUtil.isNull(auditInfo)) {
                auditInfo = new PayComponentProductAuditInfo();
                auditInfo.setProductId(Integer.valueOf(outProductId));
                auditInfo.setComponentProductId(draftProduct.getComponentProductId());
                auditInfo.setAuditId(shopSpuAuditVo.getAuditId());
                auditInfo.setRejectReason(Optional.ofNullable(shopSpuAuditVo.getRejectReason()).orElse(""));
                auditInfo.setAuditTime(shopSpuAuditVo.getAuditTime());
                auditInfo.setSubmitTime(shopSpuAuditVo.getSubmitTime());
                componentProductAuditInfoService.save(auditInfo);
            }
            logger.error(StrUtil.format("商品审核未通过，记录已保存，商品id:{}，失败原因:{}", outProductId, jsonObject.getString("reject_reason")));
            return ;
        }
        // 审核通过
        // 查找对应的草稿商品
        PayComponentDraftProduct draftProduct = componentDraftProductService.getByProId(Integer.valueOf(outProductId));
        if (ObjectUtil.isNull(draftProduct) || draftProduct.getIsDel()) {
            logger.error(StrUtil.format("商品审核回调未找到草稿商品，商品id:{}", outProductId));
            return ;
        }
        //查找对应的商品
        PayComponentProduct product = componentProductService.getById(Integer.valueOf(outProductId));
        if (ObjectUtil.isNull(product)) {
            logger.error(StrUtil.format("商品审核回调未找到商品，商品id:{}", outProductId));
            return ;
        }
        // 判断是商品新增还是商品修改
        if (product.getIsDel() && ObjectUtil.isNull(product.getComponentProductId())) {
            // 商品新增
            // 获取组件线上商品数据
            ShopSpuCommonVo shopSpuCommonVo = new ShopSpuCommonVo();
            shopSpuCommonVo.setOutProductId(outProductId);
            shopSpuCommonVo.setNeedEditSpu(0);
            ShopSpuVo shopSpuVo = wechatVideoSpuService.shopSpuGet(shopSpuCommonVo);

            draftProduct.setStatus(shopSpuVo.getStatus());
            draftProduct.setEditStatus(shopSpuVo.getEditStatus());
            draftProduct.setUpdateTime(shopSpuVo.getUpdateTime());

            product.setComponentProductId(draftProduct.getComponentProductId());
            product.setStatus(shopSpuVo.getStatus());
            product.setEditStatus(shopSpuVo.getEditStatus());
            product.setCreateTime(draftProduct.getCreateTime());
            product.setUpdateTime(draftProduct.getUpdateTime());
            product.setStock(draftProduct.getStock());
            product.setIsDel(false);

            PayComponentProductInfo productInfo = new PayComponentProductInfo();
            if (StrUtil.isNotBlank(draftProduct.getDescInfo())) {
                productInfo.setProductId(product.getId());
                productInfo.setComponentProductId(product.getComponentProductId());
                productInfo.setDesc(draftProduct.getDescInfo());
                productInfo.setIsDel(false);
            }

            List<PayComponentProductSku> skuList = JSONArray.parseArray(draftProduct.getSku(), PayComponentProductSku.class);
            List<PayComponentProductSkuAttr> attrsList = CollUtil.newArrayList();
            skuList.forEach(e -> {
                List<PayComponentProductSkuAttr> skuAttrs = e.getAttrList();
                skuAttrs.forEach(attr -> {
                    attr.setSkuId(e.getId());
                    attr.setComponentSkuId(e.getSkuId());
                    attr.setIsDel(false);
                });
                attrsList.addAll(skuAttrs);
            });

            List<StoreProductAttr> attrList = JSONArray.parseArray(draftProduct.getAttr(), StoreProductAttr.class);
            List<StoreProductAttrValueRequest> attrValueList = JSONArray.parseArray(draftProduct.getAttrValue(), StoreProductAttrValueRequest.class);
            // 组装历史表数据，原有attr、attrValue表保存
            MyRecord tableData = assembleHistoryTableData(product, attrList, attrValueList);
            List<StoreProductAttr> productAttrList = tableData.get("productAttrList");
            List<StoreProductAttrValue> productAttrValueList = tableData.get("productAttrValueList");

            // 保存审核数据
            ShopSpuAuditVo shopSpuAuditVo = shopSpuVo.getAuditInfo();
            PayComponentProductAuditInfo auditInfo = componentProductAuditInfoService.getByProductIdAndAuditId(Integer.valueOf(outProductId), shopSpuAuditVo.getAuditId());
            if (ObjectUtil.isNull(auditInfo)) {
                auditInfo = new PayComponentProductAuditInfo();
                auditInfo.setProductId(Integer.valueOf(outProductId));
                auditInfo.setComponentProductId(draftProduct.getComponentProductId());
                auditInfo.setAuditId(shopSpuAuditVo.getAuditId());
                auditInfo.setRejectReason(Optional.ofNullable(shopSpuAuditVo.getRejectReason()).orElse(""));
                auditInfo.setAuditTime(shopSpuAuditVo.getAuditTime());
                auditInfo.setSubmitTime(shopSpuAuditVo.getSubmitTime());
            }
            PayComponentProductAuditInfo finalAuditInfo = auditInfo;
            Boolean execute = transactionTemplate.execute(e -> {
                draftProduct.setUpdateTime(DateUtil.date().toString());
                componentDraftProductService.updateById(draftProduct);
                product.setUpdateTime(DateUtil.date().toString());
                componentProductService.updateById(product);
                if (StrUtil.isNotBlank(draftProduct.getDescInfo())) {
                    componentProductInfoService.save(productInfo);
                }

                // 历史表部分
                productAttrService.saveBatch(productAttrList);
                productAttrValueService.saveBatch(productAttrValueList);

                skuList.forEach(s -> {
                    for (StoreProductAttrValue value : productAttrValueList) {
                        if (value.getSuk().equals(s.getSku())) {
                            s.setAttrValueId(value.getId());
                            break;
                        }
                    }
                });
                componentProductSkuService.updateBatchById(skuList);
                componentProductSkuAttrService.saveBatch(attrsList);

                if (ObjectUtil.isNull(finalAuditInfo.getId())) {
                    componentProductAuditInfoService.save(finalAuditInfo);
                }
                return Boolean.TRUE;
            });
            if (!execute) {
                logger.error(StrUtil.format("商品审核回调新增商品保存数据出错，商品id:{}", outProductId));
            }

            return ;
        }
        if (product.getIsDel()) {
            logger.error(StrUtil.format("商品审核回调正式商品不存在，商品id:{}", outProductId));
            return ;
        }
        // 更新商品
        // 获取组件线上商品数据
        ShopSpuCommonVo shopSpuCommonVo = new ShopSpuCommonVo();
        shopSpuCommonVo.setOutProductId(outProductId);
        shopSpuCommonVo.setNeedEditSpu(0);
        ShopSpuVo shopSpuVo = wechatVideoSpuService.shopSpuGet(shopSpuCommonVo);

        draftProduct.setStatus(shopSpuVo.getStatus());
        draftProduct.setEditStatus(shopSpuVo.getEditStatus());
        draftProduct.setUpdateTime(shopSpuVo.getUpdateTime());
        // 同步草稿箱数据
        BeanUtils.copyProperties(draftProduct, product, "id", "sales", "isDel");
        product.setHeadImg(systemAttachmentService.prefixImage(product.getHeadImg()));
        if (StrUtil.isNotBlank(product.getQualificationPics())) {
            product.setQualificationPics(systemAttachmentService.prefixImage(product.getQualificationPics()));
        }

        PayComponentProductInfo productInfo = new PayComponentProductInfo();
        if (StrUtil.isNotBlank(draftProduct.getDescInfo())) {
            productInfo.setProductId(product.getId());
            productInfo.setComponentProductId(product.getComponentProductId());
            productInfo.setDesc(draftProduct.getDescInfo());
            productInfo.setIsDel(false);
        }
        // 处理sku部分
        List<PayComponentProductSku> skuList = JSONArray.parseArray(draftProduct.getSku(), PayComponentProductSku.class);
        List<PayComponentProductSkuAttr> attrsList = CollUtil.newArrayList();
        skuList.forEach(e -> {
            List<PayComponentProductSkuAttr> skuAttrs = e.getAttrList();
            skuAttrs.forEach(attr -> {
                attr.setSkuId(e.getId());
                attr.setComponentSkuId(e.getSkuId());
                attr.setIsDel(false);
            });
            attrsList.addAll(skuAttrs);
        });

        List<StoreProductAttr> attrList = JSONArray.parseArray(draftProduct.getAttr(), StoreProductAttr.class);
        List<StoreProductAttrValueRequest> attrValueList = JSONArray.parseArray(draftProduct.getAttrValue(), StoreProductAttrValueRequest.class);
        // 组装历史表数据，原有attr、attrValue表保存
        MyRecord tableData = assembleHistoryTableData(product, attrList, attrValueList);
        List<StoreProductAttr> productAttrList = tableData.get("productAttrList");
        List<StoreProductAttrValue> productAttrValueList = tableData.get("productAttrValueList");

        // 保存审核数据
        ShopSpuAuditVo shopSpuAuditVo = shopSpuVo.getAuditInfo();
        PayComponentProductAuditInfo auditInfo = componentProductAuditInfoService.getByProductIdAndAuditId(Integer.valueOf(outProductId), shopSpuAuditVo.getAuditId());
        if (ObjectUtil.isNull(auditInfo)) {
            auditInfo = new PayComponentProductAuditInfo();
            auditInfo.setProductId(Integer.valueOf(outProductId));
            auditInfo.setComponentProductId(draftProduct.getComponentProductId());
            auditInfo.setAuditId(shopSpuAuditVo.getAuditId());
            auditInfo.setRejectReason(Optional.ofNullable(shopSpuAuditVo.getRejectReason()).orElse(""));
            auditInfo.setAuditTime(shopSpuAuditVo.getAuditTime());
            auditInfo.setSubmitTime(shopSpuAuditVo.getSubmitTime());
        }
        PayComponentProductAuditInfo finalAuditInfo = auditInfo;
        Boolean execute = transactionTemplate.execute(e -> {
            draftProduct.setUpdateTime(DateUtil.date().toString());
            componentDraftProductService.updateById(draftProduct);
            product.setUpdateTime(DateUtil.date().toString());
            componentProductService.updateById(product);
            // 删除历史info数据
            componentProductInfoService.deleteByProId(product.getId());
            if (StrUtil.isNotBlank(draftProduct.getDescInfo())) {
                componentProductInfoService.save(productInfo);
            }

            // 历史表部分
            productAttrService.removeByProductId(product.getId(), Constants.PRODUCT_TYPE_COMPONENT);
            productAttrService.saveBatch(productAttrList);
            productAttrValueService.removeByProductId(product.getId(), Constants.PRODUCT_TYPE_COMPONENT);
            productAttrValueService.saveBatch(productAttrValueList);

            // 先删除现有数据
            componentProductSkuService.deleteByProId(product.getId());
            skuList.forEach(s -> {
                for (StoreProductAttrValue value : productAttrValueList) {
                    if (value.getSuk().equals(s.getSku())) {
                        s.setAttrValueId(value.getId());
                        break;
                    }
                }
            });
            componentProductSkuService.updateBatchById(skuList);

            componentProductSkuAttrService.saveBatch(attrsList);

            if (ObjectUtil.isNull(finalAuditInfo.getId())) {
                componentProductAuditInfoService.save(finalAuditInfo);
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            logger.error(StrUtil.format("商品审核回调新增商品保存数据出错，商品id:{}", outProductId));
        }

    }

    /**
     * 组装历史表数据，原有attr、attrValue表保存
     * @param product 组件商品
     * @param attrList 规格数组
     * @param attrValueList 规格属性数组
     * @return MyRecord
     */
    private MyRecord assembleHistoryTableData(PayComponentProduct product, List<StoreProductAttr> attrList, List<StoreProductAttrValueRequest> attrValueList) {
        List<StoreProductAttr> productAttrList = CollUtil.newArrayList();
        List<StoreProductAttrValue> productAttrValueList = CollUtil.newArrayList();
        // 原有attr、attrValue表保存
        if (product.getSpecType() ) { // 多属性
            if (CollUtil.isNotEmpty(attrList)) {
                attrList.forEach(e->{
                    e.setProductId(product.getId());
                    e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"",""),"[]"));
                    e.setType(Constants.PRODUCT_TYPE_COMPONENT);
                });
                productAttrList.addAll(attrList);
            }
            if (CollUtil.isNotEmpty(attrValueList)) {
                // 批量设置attrValues对象的商品id
                for (StoreProductAttrValueRequest attrValuesRequest : attrValueList) {
                    attrValuesRequest.setProductId(product.getId());
                    StoreProductAttrValue productAttrValue = new StoreProductAttrValue();
                    BeanUtils.copyProperties(attrValuesRequest,productAttrValue);
                    //设置sku字段
                    if(StrUtil.isBlank(attrValuesRequest.getAttrValue())) {
                        break;
                    }
                    productAttrValue.setSuk(getSku(attrValuesRequest.getAttrValue()));
                    productAttrValue.setImage(systemAttachmentService.clearPrefix(productAttrValue.getImage()));
                    productAttrValue.setAttrValue(JSON.toJSONString(attrValuesRequest.getAttrValue()));
                    productAttrValue.setType(Constants.PRODUCT_TYPE_COMPONENT);
                    productAttrValueList.add(productAttrValue);
                }
            }
        } else { // 单属性
            StoreProductAttr singleAttr = new StoreProductAttr();
            singleAttr.setProductId(product.getId()).setAttrName("规格").setAttrValues("默认").setType(Constants.PRODUCT_TYPE_COMPONENT);
            productAttrList.add(singleAttr);

            StoreProductAttrValue singleAttrValue = new StoreProductAttrValue();
            // 一级、二级返佣
            StoreProductAttrValueRequest attrValueRequest = attrValueList.get(0);
            BeanUtils.copyProperties(attrValueRequest, singleAttrValue);
            if (ObjectUtil.isNull(singleAttrValue.getBrokerage())) {
                singleAttrValue.setBrokerage(BigDecimal.ZERO);
            }
            if (ObjectUtil.isNull(singleAttrValue.getBrokerageTwo())) {
                singleAttrValue.setBrokerageTwo(BigDecimal.ZERO);
            }
            singleAttrValue.setProductId(product.getId());
            singleAttrValue.setSuk("默认");
            singleAttrValue.setType(Constants.PRODUCT_TYPE_COMPONENT);
            singleAttrValue.setImage(systemAttachmentService.clearPrefix(singleAttrValue.getImage()));
            productAttrValueList.add(singleAttrValue);
        }
        MyRecord record = new MyRecord();
        record.set("productAttrList", productAttrList);
        record.set("productAttrValueList", productAttrValueList);
        return record;
    }

    /**
     * 商品sku
     * @param attrValue json字符串
     * @return sku
     */
    private String getSku(String attrValue) {
        LinkedHashMap<String, String> linkedHashMap = JSONObject.parseObject(attrValue, LinkedHashMap.class, Feature.OrderedField);
        Iterator<Map.Entry<String, String>> iterator = linkedHashMap.entrySet().iterator();
        List<String> strings = CollUtil.newArrayList();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            strings.add(next.getValue());
        }
//        List<String> strings = jsonObject.values().stream().map(o -> (String) o).collect(Collectors.toList());
        return String.join(",", strings);
    }
}

