package com.zbkj.crmeb.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.CategoryConstants;
import com.constants.Constants;
import com.constants.SysConfigConstants;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;
import com.zbkj.crmeb.front.request.IndexStoreProductSearchRequest;
import com.zbkj.crmeb.front.request.ProductRequest;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.front.service.ProductService;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.model.StoreProductAttrValue;
import com.zbkj.crmeb.store.response.StoreProductAttrValueResponse;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.ProductUtils;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
* IndexServiceImpl 接口实现
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StoreProductReplyService storeProductReplyService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private ProductUtils productUtils;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StoreProductAttrService attrService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    /**
     * 首页产品的轮播图和产品信息
     * @param request IndexStoreProductSearchRequest 参数
     * @author Mr.Zhang
     * @since 2020-06-02
     * @return CommonPage<ProductResponse>
     */
    @Override
    public CommonPage<ProductResponse> getIndexProduct(IndexStoreProductSearchRequest request, PageParamRequest pageParamRequest){
        List<StoreProduct> storeProductList = storeProductService.getList(request, pageParamRequest);
        CommonPage<StoreProduct> storeProductCommonPage = CommonPage.restPage(storeProductList);

        if(storeProductList.size() < 1){
            return CommonPage.restPage(new ArrayList<>());
        }
        List<ProductResponse> productResponseArrayList = new ArrayList<>();
        for (StoreProduct storeProduct : storeProductList) {
            ProductResponse productResponse = new ProductResponse();
            // 根据参与活动添加对应商品活动标示
            if(StringUtils.isNotBlank(storeProduct.getActivity())){
                HashMap<Integer,ProductActivityItemResponse> activityByProduct =
                        productUtils.getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
                List<Integer> activityList = CrmebUtil.stringToArrayInt(storeProduct.getActivity());
                if (CollUtil.isNotEmpty(activityByProduct) && activityList.size() > 0) {
                    if(activityList.get(0).equals(Constants.PRODUCT_TYPE_SECKILL)){
                        productResponse.setActivityH5(activityByProduct.get(Constants.PRODUCT_TYPE_SECKILL));
                    }
                    if(activityList.get(0).equals(Constants.PRODUCT_TYPE_BARGAIN)){
                        productResponse.setActivityH5(activityByProduct.get(Constants.PRODUCT_TYPE_BARGAIN));
                    }
                    if(activityList.get(0).equals(Constants.PRODUCT_TYPE_PINGTUAN)){
                        productResponse.setActivityH5(activityByProduct.get(Constants.PRODUCT_TYPE_PINGTUAN));
                    }
                }
            }

            BeanUtils.copyProperties(storeProduct, productResponse);

            productResponse.setCateId(CrmebUtil.stringToArray(storeProduct.getCateId()));
            productResponseArrayList.add(productResponse);
        }
        CommonPage<ProductResponse> productResponseCommonPage = CommonPage.restPage(productResponseArrayList);
        BeanUtils.copyProperties(storeProductCommonPage, productResponseCommonPage, "list");

        return productResponseCommonPage;
    }

    /**
     * 获取分类
     * @return List<CategoryTreeVo>
     */
    @Override
    public List<CategoryTreeVo> getCategory() {
        List<CategoryTreeVo> listTree = categoryService.getListTree(CategoryConstants.CATEGORY_TYPE_PRODUCT, 1, "");
        for (int i = 0; i < listTree.size();) {
            CategoryTreeVo categoryTreeVo = listTree.get(i);
            if (!categoryTreeVo.getPid().equals(0)) {
                listTree.remove(i);
                continue;
            }
            i++;
        }
        return listTree;
    }

    /**
     * 商品列表
     * @return CommonPage<IndexProductResponse>
     */
    @Override
    public CommonPage<IndexProductResponse> getList(ProductRequest request, PageParamRequest pageRequest) {
        List<StoreProduct> storeProductList = storeProductService.findH5List(request, pageRequest);
        if(CollUtil.isEmpty(storeProductList)){
            return CommonPage.restPage(new ArrayList<>());
        }
        CommonPage<StoreProduct> storeProductCommonPage = CommonPage.restPage(storeProductList);

        List<IndexProductResponse> productResponseArrayList = new ArrayList<>();
        for (StoreProduct storeProduct : storeProductList) {
            IndexProductResponse productResponse = new IndexProductResponse();
            List<Integer> activityList = CrmebUtil.stringToArrayInt(storeProduct.getActivity());
            // 活动类型默认：直接跳过
            if (activityList.get(0).equals(Constants.PRODUCT_TYPE_NORMAL)) {
                BeanUtils.copyProperties(storeProduct, productResponse);
                productResponseArrayList.add(productResponse);
                continue;
            }
            // 根据参与活动添加对应商品活动标示
            HashMap<Integer, ProductActivityItemResponse> activityByProduct =
                    productUtils.getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
            if (CollUtil.isNotEmpty(activityByProduct)) {
                for (Integer activity : activityList) {
                    if (activity.equals(Constants.PRODUCT_TYPE_NORMAL)) {
                        break;
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_SECKILL)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_SECKILL);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_BARGAIN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_BARGAIN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_PINGTUAN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_PINGTUAN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                }
            }
            BeanUtils.copyProperties(storeProduct, productResponse);
            productResponseArrayList.add(productResponse);
        }
        CommonPage<IndexProductResponse> productResponseCommonPage = CommonPage.restPage(productResponseArrayList);
        BeanUtils.copyProperties(storeProductCommonPage, productResponseCommonPage, "list");
        return productResponseCommonPage;
    }

    /**
     * 获取商品详情
     * @param id 商品编号
     * @param type normal-正常，video-视频
     * @return 商品详情信息
     */
    @Override
    public ProductDetailResponse getDetail(Integer id, String type) {

        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        // 查询商品
        StoreProduct storeProduct = storeProductService.getH5Detail(id);
        productDetailResponse.setProductInfo(storeProduct);

        // 获取商品规格
        StoreProductAttr spaPram = new StoreProductAttr();
        spaPram.setProductId(storeProduct.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttr> attrList = attrService.getByEntity(spaPram);
        // 根据制式设置attr属性
        List<ProductAttrResponse> skuAttr = getSkuAttr(attrList);
        productDetailResponse.setProductAttr(skuAttr);

        // 根据制式设置sku属性
        HashMap<String, Object> skuMap = CollUtil.newHashMap();
        StoreProductAttrValue spavPram = new StoreProductAttrValue();
        spavPram.setProductId(id).setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getByEntity(spavPram);
        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValues) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue, atr);
            skuMap.put(atr.getSuk(), atr);
        }
        productDetailResponse.setProductValue(skuMap);

        // 用户收藏、分销返佣
        User user = userService.getInfo();
        if (ObjectUtil.isNotNull(user)) {
            // 查询用户是否收藏收藏
            user = userService.getInfo();
            productDetailResponse.setUserCollect(storeProductRelationService.getLikeOrCollectByUser(user.getUid(), id,false).size() > 0);
            // 判断是否开启分销
            String brokerageFuncStatus = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_BROKERAGE_FUNC_STATUS);
            String storeBrokerageStatus = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_STATUS);

            if (brokerageFuncStatus.equals(Constants.COMMON_SWITCH_OPEN)) {// 分销开启
                if (storeBrokerageStatus.equals(SysConfigConstants.STORE_BROKERAGE_STATUS_APPOINT)) {// 指定分销
                    productDetailResponse.setPriceName(getPacketPriceRange(storeProduct.getIsSub(), storeProductAttrValues, user.getIsPromoter()));
                } else {// 人人分销
                    productDetailResponse.setPriceName(getPacketPriceRange(storeProduct.getIsSub(),storeProductAttrValues, true));
                }
            }
        } else {
            productDetailResponse.setUserCollect(false);
        }
        // 商品活动
        List<ProductActivityItemResponse> activityAllH5 = productUtils.getProductAllActivity(storeProduct);
        productDetailResponse.setActivityAllH5(activityAllH5);

        // 商品浏览量+1
        StoreProduct updateProduct = new StoreProduct();
        updateProduct.setId(id);
        updateProduct.setBrowse(storeProduct.getBrowse() + 1);
        storeProductService.updateById(updateProduct);

        // 记录添加(加入到redis队列中)
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("product_id", productDetailResponse.getProductInfo().getId());
        map.put("uid", ObjectUtil.isNotNull(user) ? user.getUid() : 0);
        map.put("type", "visit");
        map.put("add_time", System.currentTimeMillis());
        redisUtil.lPush(Constants.PRODUCT_LOG_KEY, JSONObject.toJSONString(map));

        return productDetailResponse;
    }

    /**
     * 商品评论列表
     * @param proId 商品编号
     * @param type 评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return PageInfo<ProductReplyResponse>
     */
    @Override
    public PageInfo<ProductReplyResponse> getReplyList(Integer proId, Integer type, PageParamRequest pageParamRequest) {
        return storeProductReplyService.getH5List(proId, type, pageParamRequest);
    }

    /**
     * 产品评价数量和好评度
     * @return StoreProductReplayCountResponse
     */
    @Override
    public StoreProductReplayCountResponse getReplyCount(Integer id) {
        MyRecord myRecord = storeProductReplyService.getH5Count(id);
        Long sumCount = myRecord.getLong("sumCount");
        Long goodCount = myRecord.getLong("goodCount");
        Long inCount = myRecord.getLong("mediumCount");
        Long poorCount = myRecord.getLong("poorCount");
        String replyChance = myRecord.getStr("replyChance");
        Integer replyStar = myRecord.getInt("replyStar");
        return new StoreProductReplayCountResponse(sumCount, goodCount, inCount, poorCount, replyChance, replyStar);
    }

    /**
     * 获取商品佣金区间
     * @param isSub 是否单独计算分佣
     * @param attrValueList 商品属性列表
     * @param isPromoter 是否推荐人
     * @return String 金额区间
     */
    private String getPacketPriceRange(Boolean isSub, List<StoreProductAttrValue> attrValueList, Boolean isPromoter) {
        String priceName = "0";
        if(!isPromoter) return priceName;
        // 获取一级返佣比例
        String brokerageRatioString = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_RATIO);
        BigDecimal BrokerRatio = new BigDecimal(brokerageRatioString).divide(new BigDecimal("100"), 2, RoundingMode.DOWN);
        BigDecimal maxPrice;
        BigDecimal minPrice;
        // 获取佣金比例区间
        if(isSub){ // 是否单独分拥
            maxPrice = attrValueList.stream().map(StoreProductAttrValue::getBrokerage).reduce(BigDecimal.ZERO,BigDecimal::max);
            minPrice = attrValueList.stream().map(StoreProductAttrValue::getBrokerage).reduce(BigDecimal.ZERO,BigDecimal::min);
        }else{
            BigDecimal _maxPrice = attrValueList.stream().map(StoreProductAttrValue::getPrice).reduce(BigDecimal.ZERO,BigDecimal::max);
            BigDecimal _minPrice = attrValueList.stream().map(StoreProductAttrValue::getPrice).reduce(BigDecimal.ZERO,BigDecimal::min);
            maxPrice = BrokerRatio.multiply(_maxPrice).setScale(2, RoundingMode.HALF_UP);
            minPrice = BrokerRatio.multiply(_minPrice).setScale(2, RoundingMode.HALF_UP);
        }
        if(minPrice.compareTo(BigDecimal.ZERO) == 0 && maxPrice.compareTo(BigDecimal.ZERO) == 0){
            priceName = "0";
        }else if(minPrice.compareTo(BigDecimal.ZERO) == 0 && maxPrice.compareTo(BigDecimal.ZERO) > 0){
            priceName = maxPrice.toString();
        }else if(minPrice.compareTo(BigDecimal.ZERO) > 0 && maxPrice.compareTo(BigDecimal.ZERO) > 0){
            priceName = minPrice.toString();
        }else if(minPrice.compareTo(maxPrice) == 0){
            priceName = minPrice.toString();
        }else{
            priceName = minPrice.toString() + "~" + maxPrice.toString();
        }
        return priceName;
    }

    /**
     * 获取热门推荐商品列表
     * @param pageRequest 分页参数
     * @return CommonPage<IndexProductResponse>
     */
    @Override
    public CommonPage<IndexProductResponse> getHotProductList(PageParamRequest pageRequest) {
        List<StoreProduct> storeProductList = storeProductService.getIndexProduct(Constants.INDEX_HOT_BANNER, pageRequest);
        if(CollUtil.isEmpty(storeProductList)){
            return CommonPage.restPage(new ArrayList<>());
        }
        CommonPage<StoreProduct> storeProductCommonPage = CommonPage.restPage(storeProductList);

        List<IndexProductResponse> productResponseArrayList = new ArrayList<>();
        for (StoreProduct storeProduct : storeProductList) {
            IndexProductResponse productResponse = new IndexProductResponse();
            List<Integer> activityList = CrmebUtil.stringToArrayInt(storeProduct.getActivity());
            // 活动类型默认：直接跳过
            if (activityList.get(0).equals(Constants.PRODUCT_TYPE_NORMAL)) {
                BeanUtils.copyProperties(storeProduct, productResponse);
                productResponseArrayList.add(productResponse);
                continue;
            }
            // 根据参与活动添加对应商品活动标示
            HashMap<Integer, ProductActivityItemResponse> activityByProduct =
                    productUtils.getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
            if (CollUtil.isNotEmpty(activityByProduct)) {
                for (Integer activity : activityList) {
                    if (activity.equals(Constants.PRODUCT_TYPE_NORMAL)) {
                        break;
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_SECKILL)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_SECKILL);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_BARGAIN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_BARGAIN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_PINGTUAN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_PINGTUAN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                }
            }
            BeanUtils.copyProperties(storeProduct, productResponse);
            productResponseArrayList.add(productResponse);
        }
        CommonPage<IndexProductResponse> productResponseCommonPage = CommonPage.restPage(productResponseArrayList);
        BeanUtils.copyProperties(storeProductCommonPage, productResponseCommonPage, "list");
        return productResponseCommonPage;
    }

    /**
     * 商品详情评论
     * @param id 商品id
     * @return ProductDetailReplyResponse
     * 评论只有一条，图文
     * 评价总数
     * 好评率
     */
    @Override
    public ProductDetailReplyResponse getProductReply(Integer id) {
        return storeProductReplyService.getH5ProductReply(id);
    }

    /**
     * 优选商品推荐
     * @return CommonPage<IndexProductResponse>
     */
    @Override
    public CommonPage<IndexProductResponse> getGoodProductList() {
        PageParamRequest pageRequest = new PageParamRequest();
        pageRequest.setLimit(9);
        List<StoreProduct> storeProductList = storeProductService.getIndexProduct(Constants.INDEX_RECOMMEND_BANNER, pageRequest);
        if(CollUtil.isEmpty(storeProductList)){
            return CommonPage.restPage(new ArrayList<>());
        }
        CommonPage<StoreProduct> storeProductCommonPage = CommonPage.restPage(storeProductList);

        List<IndexProductResponse> productResponseArrayList = new ArrayList<>();
        for (StoreProduct storeProduct : storeProductList) {
            IndexProductResponse productResponse = new IndexProductResponse();
            List<Integer> activityList = CrmebUtil.stringToArrayInt(storeProduct.getActivity());
            // 活动类型默认：直接跳过
            if (activityList.get(0).equals(Constants.PRODUCT_TYPE_NORMAL)) {
                BeanUtils.copyProperties(storeProduct, productResponse);
                productResponseArrayList.add(productResponse);
                continue;
            }
            // 根据参与活动添加对应商品活动标示
            HashMap<Integer, ProductActivityItemResponse> activityByProduct =
                    productUtils.getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
            if (CollUtil.isNotEmpty(activityByProduct)) {
                for (Integer activity : activityList) {
                    if (activity.equals(Constants.PRODUCT_TYPE_NORMAL)) {
                        break;
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_SECKILL)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_SECKILL);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_BARGAIN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_BARGAIN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_PINGTUAN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_PINGTUAN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                }
            }
            BeanUtils.copyProperties(storeProduct, productResponse);
            productResponseArrayList.add(productResponse);
        }
        CommonPage<IndexProductResponse> productResponseCommonPage = CommonPage.restPage(productResponseArrayList);
        BeanUtils.copyProperties(storeProductCommonPage, productResponseCommonPage, "list");
        return productResponseCommonPage;
    }

    ///////////////////////////////////////////////////////// 自定义方法

    /**
     * 设置制式结构给attr属性
     * @param attrList attr列表
     * @return List<MyRecord>
     */
    private List<ProductAttrResponse> getSkuAttr(List<StoreProductAttr> attrList) {
        List<ProductAttrResponse> attrResponseList = new ArrayList<>();
        for (StoreProductAttr attr : attrList) {
            ProductAttrResponse attrResponse = new ProductAttrResponse();
            attrResponse.setProductId(attr.getProductId());
            attrResponse.setAttrName(attr.getAttrName());
            attrResponse.setType(attr.getType());
            List<String> attrValues = new ArrayList<>();
            String trimAttr = attr.getAttrValues()
                    .replace("[","")
                    .replace("]","");
            if(attr.getAttrValues().contains(",")){
                attrValues = Arrays.asList(trimAttr.split(","));
            }else{
                attrValues.add(trimAttr);
            }
            attrResponse.setAttrValues(attrValues);
            attrResponseList.add(attrResponse);
        }
        return attrResponseList;
    }
}

