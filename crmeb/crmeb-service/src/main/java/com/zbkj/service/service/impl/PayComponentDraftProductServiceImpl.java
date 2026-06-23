package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.wechat.video.*;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.ComponentProductSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.PayComponentProductAddRequest;
import com.zbkj.common.request.StoreProductAttrValueAddRequest;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.vo.*;
import com.zbkj.service.dao.PayComponentDraftProductDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
public class PayComponentDraftProductServiceImpl extends ServiceImpl<PayComponentDraftProductDao, PayComponentDraftProduct> implements PayComponentDraftProductService {

    @Resource
    private PayComponentDraftProductDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private PayComponentProductService productService;

    @Autowired
    private WechatVideoSpuService wechatVideoSpuService;
    @Autowired
    private PayComponentProductSkuService skuService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private PayComponentCatService catService;
    @Autowired
    private WechatVideoBeforeService wechatVideoBeforeService;

    /**
     * 添加商品
     * @param addRequest 商品请求参数
     * @return Boolean
     */
    @Override
    public Boolean add(PayComponentProductAddRequest addRequest) {
        // 第一步，正式商品库创建商品，sku，先设置为删除状态
        PayComponentProduct product = new PayComponentProduct();
        BeanUtils.copyProperties(addRequest, product);
        if (ObjectUtil.isNull(addRequest.getBrandId()) || addRequest.getBrandId() == 0) {
            product.setBrandId(2100000000);
        }

        product.setHeadImg(addRequest.getHeadImg());
        if (StrUtil.isNotBlank(product.getQualificationPics()) && !product.getQualificationPics().equals("[]")) {
            product.setQualificationPics(systemAttachmentService.clearPrefix(product.getQualificationPics()));
        }
        product.setIsDel(true);
        PayComponentProductInfo productInfo = new PayComponentProductInfo();
        if (StrUtil.isNotBlank(addRequest.getDescInfo())) {
            productInfo.setDesc(systemAttachmentService.clearPrefix(addRequest.getDescInfo()));
        }
        // 规格部分
        List<PayComponentProductSku> skuList = CollUtil.newArrayList();
        if (!addRequest.getSpecType()) { // 单规格
            PayComponentProductSkuAttr skuAttr = new PayComponentProductSkuAttr();
            skuAttr.setAttrKey("请选择规格");
            skuAttr.setAttrValue("默认");
            skuAttr.setIsDel(false);
            List<PayComponentProductSkuAttr> attrList = CollUtil.newArrayList();
            attrList.add(skuAttr);
            StoreProductAttrValueAddRequest attrValueRequest = addRequest.getAttrValue().get(0);
            PayComponentProductSku sku = new PayComponentProductSku();
            sku.setThumbImg(attrValueRequest.getImage());
            sku.setSalePrice(attrValueRequest.getPrice().multiply(new BigDecimal("100")).longValue());
            sku.setMarketPrice(attrValueRequest.getOtPrice().multiply(new BigDecimal("100")).longValue());
            sku.setStockNum(attrValueRequest.getStock());
            sku.setAttrList(attrList);
            sku.setIsDel(true);
            sku.setSku("默认");
            skuList.add(sku);
        } else { // 多规格
            List<StoreProductAttrValueAddRequest> valueRequestList = addRequest.getAttrValue();
            valueRequestList.forEach(attrValueRequest -> {
                PayComponentProductSku sku = new PayComponentProductSku();
                sku.setThumbImg(attrValueRequest.getImage());
                sku.setSalePrice(attrValueRequest.getPrice().multiply(new BigDecimal("100")).longValue());
                sku.setMarketPrice(attrValueRequest.getOtPrice().multiply(new BigDecimal("100")).longValue());
                sku.setStockNum(attrValueRequest.getStock());
                sku.setIsDel(true);

                List<String> skuAttrList = new ArrayList<>();
                String attrValueJson = attrValueRequest.getAttrValue();
                JSONObject jsonObject = JSONObject.parseObject(attrValueJson);
                List<PayComponentProductSkuAttr> attrList = CollUtil.newArrayList();
                for (Map.Entry<String, Object> vo : jsonObject.entrySet()){
                    skuAttrList.add(vo.getValue().toString());
                    PayComponentProductSkuAttr skuAttr = new PayComponentProductSkuAttr();
                    skuAttr.setAttrKey(vo.getKey());
                    skuAttr.setAttrValue(vo.getValue().toString());
                    skuAttr.setIsDel(false);
                    attrList.add(skuAttr);
                }
                sku.setSku(String.join(",", skuAttrList));
                sku.setAttrList(attrList);
                skuList.add(sku);
            });
        }
        Boolean execute = transactionTemplate.execute(e -> {
            productService.save(product);
            product.setPath(StrUtil.format("/pages/goods_details/index?id={}&type={}", product.getId(), "video"));
            product.setUpdateTime(DateUtil.date().toString());
            productService.updateById(product);
            for (PayComponentProductSku sku : skuList) {
                sku.setProductId(product.getId());
                skuService.save(sku);
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("第一步，本地保存商品时错误");
        }

//        product.setHeadImg(systemAttachmentService.prefixImage(product.getHeadImg()));
//        if (StrUtil.isNotBlank(product.getQualificationPics())) {
//            product.setQualificationPics(systemAttachmentService.prefixImage(product.getQualificationPics()));
//        }
        // 第二步，组装商品，发给组件
        ShopSpuAddVo shopSpuAddVo = assembleShopSpuVo(product, skuList, productInfo);
        ShopSpuAddResponseVo spuAddResponseVo;
        try {
            spuAddResponseVo = wechatVideoSpuService.shopSpuAdd(shopSpuAddVo);
        } catch (Exception e) {// 抛出异常，说明微信调用错误，删除之前保存的商品、sku等信息
            productService.removeById(product);
            List<Integer> skuIds = skuList.stream().map(PayComponentProductSku::getId).collect(Collectors.toList());
            skuService.removeByIds(skuIds);
            throw new CrmebException(e.getMessage());
        }

        // 第三步，保存草稿商品，保存需要给前端展示的数据表
        // 轮播图
//        product.setHeadImg(systemAttachmentService.clearPrefix(product.getHeadImg()));
        // 商品资质图
//        if (StrUtil.isNotBlank(product.getQualificationPics())) {
//            product.setQualificationPics(systemAttachmentService.clearPrefix(product.getQualificationPics()));
//        }
        Integer componentProductId = spuAddResponseVo.getProductId();
        PayComponentDraftProduct draftProduct = new PayComponentDraftProduct();
        BeanUtils.copyProperties(product, draftProduct);
        draftProduct.setId(null);
        draftProduct.setProductId(product.getId());
        draftProduct.setComponentProductId(componentProductId);
        draftProduct.setCreateTime(spuAddResponseVo.getCreateTime());
        draftProduct.setUpdateTime(spuAddResponseVo.getUpdateTime());
        draftProduct.setStatus(0);
        draftProduct.setEditStatus(2);

        // sku
        Map<String, String> skuMap = CollUtil.newHashMap();
        spuAddResponseVo.getSkus().forEach(e -> {
            skuMap.put(e.getOutSkuId(), e.getSkuId());
        });
        skuList.forEach(sku -> {
            sku.setComponentProductId(componentProductId);
            sku.setSkuId(skuMap.get(sku.getId().toString()));
            sku.setIsDel(false);
        });

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
        draftProduct.setSku(JSONArray.toJSONString(skuList));
        draftProduct.setAttr(JSONArray.toJSONString(addRequest.getAttr()));
        draftProduct.setAttrValue(JSONArray.toJSONString(addRequest.getAttrValue()));
        draftProduct.setIsDel(false);
        draftProduct.setAddTime(DateUtil.date());
        draftProduct.setSales(0);
        draftProduct.setSpecType(addRequest.getSpecType());
        draftProduct.setDescInfo(addRequest.getDescInfo());
        int stock = skuList.stream().mapToInt(PayComponentProductSku::getStockNum).sum();
        draftProduct.setStock(stock);

        boolean save = save(draftProduct);
        if (!save) {
            throw new CrmebException("第三步，本地保存草稿商品时错误");
        }
        return save;
    }

    /**
     * 根据商品id获取草稿商品
     * @param proId 商品id
     * @return PayComponentDraftProduct
     */
    @Override
    public PayComponentDraftProduct getByProId(Integer proId) {
        LambdaQueryWrapper<PayComponentDraftProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentDraftProduct::getProductId, proId);
        return dao.selectOne(lqw);
    }

    /**
     * 通过商品id删除草稿
     * @param proId 商品id
     */
    @Override
    public Boolean deleteByProId(Integer proId) {
        LambdaUpdateWrapper<PayComponentDraftProduct> luw = Wrappers.lambdaUpdate();
        luw.set(PayComponentDraftProduct::getIsDel, true);
        luw.eq(PayComponentDraftProduct::getProductId, proId);
        return update(luw);
    }

    /**
     * 管理端草稿商品列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<PayComponentDraftProduct> getAdminList(ComponentProductSearchRequest request, PageParamRequest pageParamRequest) {
        Page<PayComponentDraftProduct> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PayComponentDraftProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentDraftProduct::getIsDel, false);
        if (ObjectUtil.isNotNull(request.getProId())) {
            lqw.eq(PayComponentDraftProduct::getProductId, request.getProId());
        }
        if (StrUtil.isNotBlank(request.getSearch())) {
            lqw.like(PayComponentDraftProduct::getTitle, request.getSearch());
        }
        lqw.eq(PayComponentDraftProduct::getIsDel, false);
        lqw.orderByDesc(PayComponentDraftProduct::getId);
        List<PayComponentDraftProduct> productList = dao.selectList(lqw);
        productList.forEach(e -> {
            PayComponentCat componentCat = catService.getByThirdCatId(e.getThirdCatId());
            if (ObjectUtil.isNull(componentCat)) {
                e.setThirdCatName("");
            } else {
                e.setThirdCatName(Optional.ofNullable(componentCat.getThirdCatName()).orElse(""));
            }
        });
        return CommonPage.copyPageInfo(page, productList);
    }

    /**
     * 商品详情
     * @param id 商品id
     * @return PayComponentDraftProduct
     */
    @Override
    public PayComponentDraftProduct getInfo(Integer id) {
        PayComponentDraftProduct draftProduct = getById(id);
        if (ObjectUtil.isNull(draftProduct) || draftProduct.getIsDel()) {
            throw new CrmebException("商品不存在");
        }
//        List<JSONObject> jsonAttrList = JSONArray.parseArray(draftProduct.getAttrValue(), JSONObject.class);
//        jsonAttrList.forEach(e -> {
//            String sku = e.getString("suk");
//            String[] skus = sku.split(",");
//            for (int i = 0; i < skus.length; i++) {
//                e.put("value" + i, skus[i]);
//            }
//        });
//        draftProduct.setAttrValue(JSONArray.toJSONString(jsonAttrList));

        // 查询对应类目
        PayComponentCat componentCat = catService.getByThirdCatId(draftProduct.getThirdCatId());
        draftProduct.setCatInfo(componentCat);
        return draftProduct;
    }

    /**
     * 组装spu添加对象
     * @return ShopSpuAddVo
     */
    private ShopSpuAddVo assembleShopSpuVo(PayComponentProduct product, List<PayComponentProductSku> skuList, PayComponentProductInfo productInfo) {
        ShopSpuAddVo shopSpuAddVo = new ShopSpuAddVo();
        shopSpuAddVo.setOutProductId(product.getId().toString());
        shopSpuAddVo.setTitle(product.getTitle());
        shopSpuAddVo.setPath(product.getPath());
        List<String> stringList = JSONArray.parseArray(product.getHeadImg(), String.class);
        shopSpuAddVo.setHeadImg(stringList);
        if (StrUtil.isNotBlank(product.getQualificationPics()) && !product.getQualificationPics().equals("[]")) {
            shopSpuAddVo.setQualificationPics(CrmebUtil.stringToArrayStr(product.getQualificationPics()));
        }
        if (StrUtil.isNotBlank(productInfo.getDesc())) {
            ShopSpuInfoVo shopSpuInfoVo = new ShopSpuInfoVo();
            shopSpuInfoVo.setDesc(productInfo.getDesc());
            shopSpuAddVo.setDescInfo(shopSpuInfoVo);
        }
        shopSpuAddVo.setThirdCatId(product.getThirdCatId());
        shopSpuAddVo.setBrandId(product.getBrandId());
        List<ShopSpuSkuVo> skuVoList = skuList.stream().map(sku -> {
            ShopSpuSkuVo spuSkuVo = new ShopSpuSkuVo();
            BeanUtils.copyProperties(sku, spuSkuVo);
            spuSkuVo.setOutProductId(product.getId().toString());
            spuSkuVo.setOutSkuId(sku.getId().toString());

            List<ShopSpuSkuAttrVo> attrVoList = sku.getAttrList().stream().map(attr -> {
                ShopSpuSkuAttrVo shopSpuSkuAttrVo = new ShopSpuSkuAttrVo();
                BeanUtils.copyProperties(attr, shopSpuSkuAttrVo);
                return shopSpuSkuAttrVo;
            }).collect(Collectors.toList());
            spuSkuVo.setSkuAttrs(attrVoList);
            return spuSkuVo;
        }).collect(Collectors.toList());
        shopSpuAddVo.setSkus(skuVoList);
        return shopSpuAddVo;
    }
}

