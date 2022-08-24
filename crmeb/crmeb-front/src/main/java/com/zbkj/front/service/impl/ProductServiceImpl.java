package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.CategoryConstants;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.RedisConstatns;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.model.product.StoreProductAttr;
import com.zbkj.common.model.product.StoreProductAttrValue;
import com.zbkj.common.model.record.UserVisitRecord;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductListRequest;
import com.zbkj.common.request.ProductRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.CategoryTreeVo;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.front.service.ProductService;
import com.zbkj.service.delete.ProductUtils;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* IndexServiceImpl 接口实现
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
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

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @Autowired
    private StoreCartService cartService;

    @Autowired
    private UserVisitRecordService userVisitRecordService;

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
        if (CollUtil.isEmpty(storeProductList)) {
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
        // 获取用户
        User user = userService.getInfo();
        SystemUserLevel userLevel = null;
        if (ObjectUtil.isNotNull(user) && user.getLevel() > 0) {
            userLevel = systemUserLevelService.getByLevelId(user.getLevel());
        }

        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        // 查询商品
        StoreProduct storeProduct = storeProductService.getH5Detail(id);
        if (ObjectUtil.isNotNull(userLevel)) {
            storeProduct.setVipPrice(storeProduct.getPrice());
        }
        productDetailResponse.setProductInfo(storeProduct);

        // 获取商品规格
        List<StoreProductAttr> attrList = attrService.getListByProductIdAndType(storeProduct.getId(), Constants.PRODUCT_TYPE_NORMAL);
        // 根据制式设置attr属性
        productDetailResponse.setProductAttr(attrList);

        // 根据制式设置sku属性
        HashMap<String, Object> skuMap = CollUtil.newHashMap();
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getListByProductIdAndType(storeProduct.getId(), Constants.PRODUCT_TYPE_NORMAL);
        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValues) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue, atr);
            // 设置会员价
            if (ObjectUtil.isNotNull(userLevel)) {
                atr.setVipPrice(atr.getPrice());
            }
            skuMap.put(atr.getSuk(), atr);
        }
        productDetailResponse.setProductValue(skuMap);

        // 用户收藏、分销返佣
        if (ObjectUtil.isNotNull(user)) {
            // 查询用户是否收藏收藏
            user = userService.getInfo();
            productDetailResponse.setUserCollect(storeProductRelationService.getLikeOrCollectByUser(user.getUid(), id,false).size() > 0);
            // 判断是否开启分销
            String brokerageFuncStatus = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_BROKERAGE_FUNC_STATUS);
            if (brokerageFuncStatus.equals(Constants.COMMON_SWITCH_OPEN)) {// 分销开启
                // 判断是否开启气泡
                String isBubble = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_IS_BUBBLE);
                if (isBubble.equals(Constants.COMMON_SWITCH_OPEN)) {
                    productDetailResponse.setPriceName(getPacketPriceRange(storeProduct.getIsSub(), storeProductAttrValues, user.getIsPromoter()));
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

        // 保存用户访问记录
        if (userService.getUserId() > 0) {
            UserVisitRecord visitRecord = new UserVisitRecord();
            visitRecord.setDate(DateUtil.date().toString("yyyy-MM-dd"));
            visitRecord.setUid(userService.getUserId());
            visitRecord.setVisitType(2);
            userVisitRecordService.save(visitRecord);
        }

        return productDetailResponse;
    }

    /**
     * 获取商品SKU详情
     * @param id 商品编号
     * @return 商品详情信息
     */
    @Override
    public ProductDetailResponse getSkuDetail(Integer id) {
        // 获取用户
        User user = userService.getInfo();
        SystemUserLevel userLevel = null;
        if (ObjectUtil.isNotNull(user) && user.getLevel() > 0) {
            userLevel = systemUserLevelService.getByLevelId(user.getLevel());
        }

        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        // 查询商品
        StoreProduct storeProduct = storeProductService.getH5Detail(id);

        // 获取商品规格
        List<StoreProductAttr> attrList = attrService.getListByProductIdAndType(storeProduct.getId(), Constants.PRODUCT_TYPE_NORMAL);
        // 根据制式设置attr属性
        productDetailResponse.setProductAttr(attrList);

        // 根据制式设置sku属性
        HashMap<String, Object> skuMap = CollUtil.newHashMap();
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getListByProductIdAndType(storeProduct.getId(), Constants.PRODUCT_TYPE_NORMAL);
        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValues) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue, atr);
            // 设置会员价
            if (ObjectUtil.isNotNull(userLevel)) {
                BigDecimal vipPrice = atr.getPrice().multiply(new BigDecimal(userLevel.getDiscount())).divide(new BigDecimal(100), 2 ,BigDecimal.ROUND_HALF_UP);
                atr.setVipPrice(vipPrice);
            }
            skuMap.put(atr.getSuk(), atr);
        }
        productDetailResponse.setProductValue(skuMap);

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
        if (!isPromoter) return priceName;
        // 获取一级返佣比例
        String brokerageRatioString = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_STORE_BROKERAGE_RATIO);
        BigDecimal BrokerRatio = new BigDecimal(brokerageRatioString).divide(new BigDecimal("100"), 2, RoundingMode.DOWN);
        BigDecimal maxPrice;
        BigDecimal minPrice;
        // 获取佣金比例区间
        if (isSub) { // 是否单独分拥
            maxPrice = attrValueList.stream().map(StoreProductAttrValue::getBrokerage).reduce(BigDecimal.ZERO,BigDecimal::max);
            minPrice = attrValueList.stream().map(StoreProductAttrValue::getBrokerage).reduce(BigDecimal.ZERO,BigDecimal::min);
        } else {
            BigDecimal _maxPrice = attrValueList.stream().map(StoreProductAttrValue::getPrice).reduce(BigDecimal.ZERO,BigDecimal::max);
            BigDecimal _minPrice = attrValueList.stream().map(StoreProductAttrValue::getPrice).reduce(BigDecimal.ZERO,BigDecimal::min);
            maxPrice = BrokerRatio.multiply(_maxPrice).setScale(2, RoundingMode.HALF_UP);
            minPrice = BrokerRatio.multiply(_minPrice).setScale(2, RoundingMode.HALF_UP);
        }
        if (minPrice.compareTo(BigDecimal.ZERO) == 0 && maxPrice.compareTo(BigDecimal.ZERO) == 0) {
            priceName = "0";
        } else if (minPrice.compareTo(BigDecimal.ZERO) == 0 && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            priceName = maxPrice.toString();
        } else if (minPrice.compareTo(BigDecimal.ZERO) > 0 && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            priceName = minPrice.toString();
        } else if (minPrice.compareTo(maxPrice) == 0) {
            priceName = minPrice.toString();
        } else {
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
        if (CollUtil.isEmpty(storeProductList)) {
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
        if (CollUtil.isEmpty(storeProductList)) {
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
     * 商品列表(个别分类模型使用)
     * @param request 列表请求参数
     * @param pageParamRequest 分页参数
     * @return CommonPage
     */
    @Override
    public CommonPage<IndexProductResponse> getCategoryProductList(ProductListRequest request, PageParamRequest pageParamRequest) {
        ProductRequest searchRequest = new ProductRequest();
        BeanUtils.copyProperties(searchRequest, request);
        List<StoreProduct> storeProductList = storeProductService.findH5List(searchRequest, pageParamRequest);
        if (CollUtil.isEmpty(storeProductList)) {
            return CommonPage.restPage(new ArrayList<>());
        }
        CommonPage<StoreProduct> storeProductCommonPage = CommonPage.restPage(storeProductList);

        User user = userService.getInfo();
        List<IndexProductResponse> productResponseArrayList = new ArrayList<>();
        for (StoreProduct storeProduct : storeProductList) {
            IndexProductResponse productResponse = new IndexProductResponse();
            // 获取商品购物车数量
            if (ObjectUtil.isNotNull(user)) {
                productResponse.setCartNum(cartService.getProductNumByUidAndProductId(user.getUid(), storeProduct.getId()));
            }
            BeanUtils.copyProperties(storeProduct, productResponse);
            productResponseArrayList.add(productResponse);
        }
        CommonPage<IndexProductResponse> productResponseCommonPage = CommonPage.restPage(productResponseArrayList);
        BeanUtils.copyProperties(storeProductCommonPage, productResponseCommonPage, "list");
        return productResponseCommonPage;
    }

    /**
     * 获取商品排行榜
     * @return List
     */
    @Override
    public List<StoreProduct> getLeaderboard() {
        return storeProductService.getLeaderboard();
    }

}

