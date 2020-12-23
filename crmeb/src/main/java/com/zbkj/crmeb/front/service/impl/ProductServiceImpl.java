package com.zbkj.crmeb.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.category.vo.CategoryTreeVo;
import com.zbkj.crmeb.front.request.IndexStoreProductSearchRequest;
import com.zbkj.crmeb.front.request.ProductRequest;
import com.zbkj.crmeb.front.response.ProductActivityItemResponse;
import com.zbkj.crmeb.front.response.ProductDetailResponse;
import com.zbkj.crmeb.front.response.ProductResponse;
import com.zbkj.crmeb.front.response.StoreProductReplayCountResponse;
import com.zbkj.crmeb.front.service.ProductService;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductAttr;
import com.zbkj.crmeb.store.request.StoreProductReplySearchRequest;
import com.zbkj.crmeb.store.response.*;
import com.zbkj.crmeb.store.service.StoreProductRelationService;
import com.zbkj.crmeb.store.service.StoreProductReplyService;
import com.zbkj.crmeb.store.service.StoreProductService;
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
import java.util.stream.Collectors;

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
                    if(activityList.get(0) == Constants.PRODUCT_TYPE_SECKILL){
                        productResponse.setActivityH5(activityByProduct.get(Constants.PRODUCT_TYPE_SECKILL));
                    }
                    if(activityList.get(0) == Constants.PRODUCT_TYPE_BARGAIN){
                        productResponse.setActivityH5(activityByProduct.get(Constants.PRODUCT_TYPE_BARGAIN));
                    }
                    if(activityList.get(0) == Constants.PRODUCT_TYPE_PINGTUAN){
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
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return List<CategoryTreeVo>
     */
    @Override
    public List<CategoryTreeVo> getCategory() {
        return categoryService.getListTree(Constants.CATEGORY_TYPE_PRODUCT, 1,"");
    }

    /**
     * 商品列表
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return CommonPage<ProductResponse>
     */
    @Override
    public CommonPage<ProductResponse> getList(ProductRequest request, PageParamRequest pageParamRequest) {
        IndexStoreProductSearchRequest indexStoreProductSearchRequest = new IndexStoreProductSearchRequest();
        BeanUtils.copyProperties(request, indexStoreProductSearchRequest);
        if(request.getCid() != null){

            //查找当前类下的所有子类
            List<Integer> categoryIdList;
            List<Category> childVoListByPid = categoryService.getChildVoListByPid(request.getCid());
            categoryIdList = childVoListByPid.stream().map(Category::getId).collect(Collectors.toList());

            categoryIdList.add(request.getCid());
            indexStoreProductSearchRequest.setCateId(categoryIdList);
        }
        indexStoreProductSearchRequest.setType(1);
        indexStoreProductSearchRequest.setKeywords(request.getKeyword());
        return getIndexProduct(indexStoreProductSearchRequest, pageParamRequest);
    }

    /**
     * 商品详情
     * @author Mr.Zhang Edit by stivpeim 2020-6-30
     * @since 2020-06-03
     * @return ProductDetailResponse
     */
    @Override
    public ProductDetailResponse getDetail(Integer id) {
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        User user = null;
        try {
            StoreProductResponse productResponse = storeProductService.getByProductId(id);
            StoreProductStoreInfoResponse storeInfo = new StoreProductStoreInfoResponse();

            BeanUtils.copyProperties(productResponse,storeInfo);
            storeInfo.setActivityAllH5(productUtils.getProductAllActivity(
                    new StoreProduct().setId(productResponse.getId()).setActivity(productResponse.getActivity())));

            // 设置点赞和收藏
            user = userService.getInfo();
            if(null != user && null != user.getUid()){
                storeInfo.setUserLike(storeProductRelationService.getLikeOrCollectByUser(user.getUid(),id,true).size() > 0);
                storeInfo.setUserCollect(storeProductRelationService.getLikeOrCollectByUser(user.getUid(),id,false).size() > 0);
                productDetailResponse.setPriceName(getPacketPriceRange(productResponse, user.getIsPromoter()));
            }else{
                storeInfo.setUserLike(false);
                storeInfo.setUserCollect(false);
            }
            productDetailResponse.setStoreInfo(storeInfo);

            // 根据制式设置attr属性
            setSkuAttr(id, productDetailResponse, productResponse);
            // 根据制式设置sku属性
            HashMap<String,Object> skuMap = new HashMap<>();
            for (StoreProductAttrValueResponse attrValue : productResponse.getAttrValue()) {
                skuMap.put(attrValue.getSuk(),attrValue);
            }
            productDetailResponse.setProductValue(skuMap);
            // 优品推荐
            List<StoreProduct> storeProducts = storeProductService.getRecommendStoreProduct(18);
            List<StoreProductRecommendResponse> storeProductRecommendResponses = new ArrayList<>();
            for (StoreProduct product:storeProducts) {
                StoreProductRecommendResponse sPRecommendResponse = new StoreProductRecommendResponse();
                BeanUtils.copyProperties(product,sPRecommendResponse);
                storeProductRecommendResponses.add(sPRecommendResponse);
            }
            productDetailResponse.setGoodList(storeProductRecommendResponses);

            // 商品浏览量+1
            StoreProduct updateProduct = new StoreProduct();
            updateProduct.setId(id);
            updateProduct.setBrowse(productResponse.getBrowse() + 1);
            storeProductService.updateById(updateProduct);
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
        }

        // 记录添加(加入到redis队列中)
        HashMap<String, Object> map = CollUtil.newHashMap();
        map.put("product_id", productDetailResponse.getStoreInfo().getId());
        map.put("uid", ObjectUtil.isNotNull(user) ? user.getUid() : 0);
        map.put("type", "visit");
        map.put("add_time", System.currentTimeMillis());
        redisUtil.lPush(Constants.PRODUCT_LOG_KEY, JSONObject.toJSONString(map));

        return productDetailResponse;
    }

    /**
     * 评论列表
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return List<StoreProductReply>
     */
    @Override
    public PageInfo<StoreProductReplyResponse> getReplyList(Integer id, Integer type, PageParamRequest pageParamRequest) {
        StoreProductReplySearchRequest storeProductReplySearchRequest = new StoreProductReplySearchRequest();
        storeProductReplySearchRequest.setIsDel(false);
        storeProductReplySearchRequest.setProductId(id.toString());
        storeProductReplySearchRequest.setType(type);
        PageInfo<StoreProductReplyResponse> pageInfo = storeProductReplyService.getList(storeProductReplySearchRequest, pageParamRequest);
        pageInfo.getList().forEach(e -> {
            String nickname = e.getNickname();
            if (StrUtil.isNotBlank(nickname)) {
                if (nickname.length() == 1) {
                    nickname = nickname.concat("**");
                } else if (nickname.length() == 2) {
                    nickname = nickname.substring(0, 1) + "**";
                } else {
                    nickname = nickname.substring(0, 1) + "**" + nickname.substring(nickname.length() - 1);
                }
                e.setNickname(nickname);
            }
        });
        return pageInfo;
    }

    /**
     * 产品评价数量和好评度
     * @author Mr.Zhang
     * @since 2020-06-03
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
     * @param storeProductResponse 商品属性
     * @param isPromoter 是否推荐人
     * @return String 金额区间
     */
    @Override
    public String getPacketPriceRange(StoreProductResponse storeProductResponse, boolean isPromoter) {
        String priceName = "0";
        if(!isPromoter) return priceName;
        // 获取拥挤比例
        String brokerageRatioString = systemConfigService.getValueByKey("store_brokerage_ratio");
        BigDecimal BrokerRatio = new BigDecimal(brokerageRatioString).divide(BigDecimal.valueOf(100L));
        BigDecimal maxPrice = null;
        BigDecimal minPrice = null;
        // 获取佣金比例区间 todo 这里的对象更换为map后需要重新计算
        if(storeProductResponse.getIsSub()){ // 是否单独分拥
            maxPrice = storeProductResponse.getAttrValue().stream().map(e->e.getBrokerage()).reduce(BigDecimal.ZERO,BigDecimal::max);
            minPrice = storeProductResponse.getAttrValue().stream().map(e->e.getBrokerage()).reduce(BigDecimal.ZERO,BigDecimal::min);
        }else{
            BigDecimal _maxPrice = storeProductResponse.getAttrValue().stream().map(e->e.getPrice()).reduce(BigDecimal.ZERO,BigDecimal::max);
            BigDecimal _minPrice = storeProductResponse.getAttrValue().stream().map(e->e.getPrice()).reduce(BigDecimal.ZERO,BigDecimal::min);
            maxPrice = BrokerRatio.multiply(_maxPrice).setScale(2, RoundingMode.HALF_UP);
            minPrice = BrokerRatio.multiply(_minPrice).setScale(2, RoundingMode.HALF_UP);
        }
        if(minPrice.compareTo(BigDecimal.ZERO) == 0 && maxPrice.compareTo(BigDecimal.ZERO) == 0){
            priceName = "0";
        }else if(minPrice.compareTo(BigDecimal.ZERO) == 0 && maxPrice.compareTo(BigDecimal.ZERO) == 1){
            priceName = maxPrice.toString();
        }else if(minPrice.compareTo(BigDecimal.ZERO) == 1 && maxPrice.compareTo(BigDecimal.ZERO) == 1){
            priceName = minPrice.toString();
        }else if(minPrice.compareTo(maxPrice) == 0 && minPrice.compareTo(BigDecimal.ZERO) == 0){
            priceName = maxPrice.toString();
        }else{
            priceName = minPrice.toString() + "~" + maxPrice.toString();
        }
        return priceName;
    }

    ///////////////////////////////////////////////////////// 自定义方法
    /**
     * 设置制式结构给attr属性
     * @param id 产品id
     * @param productDetailResponse 商品详情
     * @param productResponse 商品本身
     */
    private void setSkuAttr(Integer id, ProductDetailResponse productDetailResponse, StoreProductResponse productResponse) {
        List<HashMap<String,Object>> attrMapList = new ArrayList<>();
        for (StoreProductAttr attr : productResponse.getAttr()) {
            HashMap<String, Object> attrMap = new HashMap<>();
            attrMap.put("productId",attr.getProductId());
            attrMap.put("attrName",attr.getAttrName());
            attrMap.put("type",attr.getType());
            List<String> attrValues = new ArrayList<>();
            String trimAttr = attr.getAttrValues()
                    .replace("[","")
                    .replace("]","");
            if(attr.getAttrValues().contains(",")){
                attrValues = Arrays.asList(trimAttr.split(","));
            }else{
                attrValues.add(trimAttr);
            }
            attrMap.put("attrValues",attrValues);
            // 设置带有优惠券标识的sku集合
            List<HashMap<String,Object>> attrValueMapList = new ArrayList<>();
            for (String attrValue : attrValues) {
                HashMap<String,Object> attrValueMap = new HashMap<>();
                attrValueMap.put("attr",attrValue);
//                attrValueMap.put("check",storeCouponService.getListByProductCanUse(id).size()>0);
                attrValueMapList.add(attrValueMap);
            }
            attrMap.put("attrValue",attrValueMapList);
            attrMapList.add(attrMap);
        }
        productDetailResponse.setProductAttr(attrMapList);
    }
}

