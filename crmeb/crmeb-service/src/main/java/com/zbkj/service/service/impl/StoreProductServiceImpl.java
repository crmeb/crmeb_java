package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.category.Category;
import com.zbkj.common.model.coupon.StoreCoupon;
import com.zbkj.common.model.product.*;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.MyRecord;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.service.dao.StoreProductDao;
import com.zbkj.service.delete.ProductUtils;
import com.zbkj.service.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
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
public class StoreProductServiceImpl extends ServiceImpl<StoreProductDao, StoreProduct>
        implements StoreProductService {

    @Resource
    private StoreProductDao dao;

    @Autowired
    private StoreProductAttrService attrService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StoreProductDescriptionService storeProductDescriptionService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private StoreProductAttrResultService storeProductAttrResultService;

    @Autowired
    private StoreProductCouponService storeProductCouponService;

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private ProductUtils productUtils;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StoreSeckillService storeSeckillService;

    @Autowired
    private OnePassService onePassService;

    @Autowired
    private StoreCartService storeCartService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final Logger logger = LoggerFactory.getLogger(StoreProductServiceImpl.class);

    /**
     * 获取产品列表Admin
     * @param request 筛选参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<StoreProductResponse> getAdminList(StoreProductSearchRequest request, PageParamRequest pageParamRequest) {
        //带 StoreProduct 类的多条件查询
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //类型搜索
        switch (request.getType()) {
            case 1:
                //出售中（已上架）
                lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);
                lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 2:
                //仓库中（未上架）
                lambdaQueryWrapper.eq(StoreProduct::getIsShow, false);
                lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 3:
                //已售罄
                lambdaQueryWrapper.le(StoreProduct::getStock, 0);
                lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 4:
                //警戒库存
                Integer stock = Integer.parseInt(systemConfigService.getValueByKey("store_stock"));
                lambdaQueryWrapper.le(StoreProduct::getStock, stock);
                lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 5:
                //回收站
                lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, true);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            default:
                break;
        }

        //关键字搜索
        if (StrUtil.isNotBlank(request.getKeywords())) {
            lambdaQueryWrapper.and(i -> i
                    .or().eq(StoreProduct::getId, request.getKeywords())
                    .or().like(StoreProduct::getStoreName, request.getKeywords())
                    .or().like(StoreProduct::getKeyword, request.getKeywords()));
        }
        lambdaQueryWrapper.apply(StringUtils.isNotBlank(request.getCateId()), "FIND_IN_SET ('" + request.getCateId() + "', cate_id)");
        lambdaQueryWrapper.orderByDesc(StoreProduct::getSort).orderByDesc(StoreProduct::getId);

        Page<StoreProduct> storeProductPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<StoreProduct> storeProducts = dao.selectList(lambdaQueryWrapper);
        List<StoreProductResponse> storeProductResponses = new ArrayList<>();
        for (StoreProduct product : storeProducts) {
            StoreProductResponse storeProductResponse = new StoreProductResponse();
            BeanUtils.copyProperties(product, storeProductResponse);
            StoreProductAttr storeProductAttrPram = new StoreProductAttr();
            storeProductAttrPram.setProductId(product.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);
            List<StoreProductAttr> attrs = attrService.getByEntity(storeProductAttrPram);

            if (attrs.size() > 0) {
                storeProductResponse.setAttr(attrs);
            }
            List<StoreProductAttrValueResponse> storeProductAttrValueResponse = new ArrayList<>();

            StoreProductAttrValue storeProductAttrValuePram = new StoreProductAttrValue();
            storeProductAttrValuePram.setProductId(product.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);
            List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getByEntity(storeProductAttrValuePram);
            storeProductAttrValues.stream().map(e->{
                StoreProductAttrValueResponse response = new StoreProductAttrValueResponse();
                BeanUtils.copyProperties(e,response);
                storeProductAttrValueResponse.add(response);
                return e;
            }).collect(Collectors.toList());
            storeProductResponse.setAttrValue(storeProductAttrValueResponse);
            // 处理富文本
            StoreProductDescription sd = storeProductDescriptionService.getOne(
                    new LambdaQueryWrapper<StoreProductDescription>()
                            .eq(StoreProductDescription::getProductId, product.getId())
                            .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_NORMAL));
            if (null != sd) {
                storeProductResponse.setContent(null == sd.getDescription()?"":sd.getDescription());
            }
            // 处理分类中文
            List<Category> cg = categoryService.getByIds(CrmebUtil.stringToArray(product.getCateId()));
            if (CollUtil.isEmpty(cg)) {
                storeProductResponse.setCateValues("");
            } else {
                storeProductResponse.setCateValues(cg.stream().map(Category::getName).collect(Collectors.joining(",")));
            }

            storeProductResponse.setCollectCount(
                    storeProductRelationService.getList(product.getId(),"collect").size());
            storeProductResponses.add(storeProductResponse);
        }
        // 多条sql查询处理分页正确
        return CommonPage.copyPageInfo(storeProductPage, storeProductResponses);
    }

    /**
     * 根据商品id集合获取
     * @param productIds id集合
     * @return
     */
    @Override
    public List<StoreProduct> getListInIds(List<Integer> productIds) {
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreProduct::getId,productIds);
        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 新增产品
     * @param request 新增产品request对象
     * @return 新增结果
     */
    @Override
    public Boolean save(StoreProductAddRequest request) {
        // 多规格需要校验规格参数
        if (!request.getSpecType()) {
            if (request.getAttrValue().size() > 1) {
                throw new CrmebException("单规格商品属性值不能大于1");
            }
        }

        StoreProduct storeProduct = new StoreProduct();
        BeanUtils.copyProperties(request, storeProduct);
        storeProduct.setId(null);
        storeProduct.setAddTime(DateUtil.getNowTime());
        storeProduct.setIsShow(false);

        // 设置Acticity活动
        storeProduct.setActivity(getProductActivityStr(request.getActivity()));

        //主图
        storeProduct.setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()));

        //轮播图
        storeProduct.setSliderImage(systemAttachmentService.clearPrefix(storeProduct.getSliderImage()));
        // 展示图
        if (StrUtil.isNotEmpty(storeProduct.getFlatPattern())) {
            storeProduct.setFlatPattern(systemAttachmentService.clearPrefix(storeProduct.getFlatPattern()));
        }

        List<StoreProductAttrValueAddRequest> attrValueAddRequestList = request.getAttrValue();
        //计算价格
        StoreProductAttrValueAddRequest minAttrValue = attrValueAddRequestList.stream().min(Comparator.comparing(StoreProductAttrValueAddRequest::getPrice)).get();
        storeProduct.setPrice(minAttrValue.getPrice());
        storeProduct.setOtPrice(minAttrValue.getOtPrice());
        storeProduct.setCost(minAttrValue.getCost());
        storeProduct.setStock(attrValueAddRequestList.stream().mapToInt(StoreProductAttrValueAddRequest::getStock).sum());

        // 默认值设置
        if (ObjectUtil.isNull(request.getSort())) {
            storeProduct.setSort(0);
        }
        if (ObjectUtil.isNull(request.getIsHot())) {
            storeProduct.setIsHot(false);
        }
        if (ObjectUtil.isNull(request.getIsBenefit())) {
            storeProduct.setIsBenefit(false);
        }
        if (ObjectUtil.isNull(request.getIsBest())) {
            storeProduct.setIsBest(false);
        }
        if (ObjectUtil.isNull(request.getIsNew())) {
            storeProduct.setIsNew(false);
        }
        if (ObjectUtil.isNull(request.getIsGood())) {
            storeProduct.setIsGood(false);
        }
        if (ObjectUtil.isNull(request.getGiveIntegral())) {
            storeProduct.setGiveIntegral(0);
        }
        if (ObjectUtil.isNull(request.getFicti())) {
            storeProduct.setFicti(0);
        }

        List<StoreProductAttrAddRequest> addRequestList = request.getAttr();
        List<StoreProductAttr> attrList = addRequestList.stream().map(e -> {
            StoreProductAttr attr = new StoreProductAttr();
            BeanUtils.copyProperties(e, attr);
            attr.setType(Constants.PRODUCT_TYPE_NORMAL);
            return attr;
        }).collect(Collectors.toList());

        List<StoreProductAttrValue> attrValueList = attrValueAddRequestList.stream().map(e -> {
            StoreProductAttrValue attrValue = new StoreProductAttrValue();
            BeanUtils.copyProperties(e, attrValue);
            attrValue.setId(null);
            attrValue.setSuk(getSku(e.getAttrValue()));
            attrValue.setQuota(0);
            attrValue.setQuotaShow(0);
            attrValue.setType(Constants.PRODUCT_TYPE_NORMAL);
            attrValue.setImage(systemAttachmentService.clearPrefix(e.getImage()));
            return attrValue;
        }).collect(Collectors.toList());

        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription();
        spd.setDescription(request.getContent().length() > 0 ? systemAttachmentService.clearPrefix(request.getContent()) : "");
        spd.setType(Constants.PRODUCT_TYPE_NORMAL);

        Boolean execute = transactionTemplate.execute(e -> {
            save(storeProduct);

            attrList.forEach(attr -> attr.setProductId(storeProduct.getId()));
            attrValueList.forEach(value -> value.setProductId(storeProduct.getId()));
            attrService.saveBatch(attrList);
            storeProductAttrValueService.saveBatch(attrValueList);

            spd.setProductId(storeProduct.getId());
            storeProductDescriptionService.deleteByProductId(storeProduct.getId(), Constants.PRODUCT_TYPE_NORMAL);
            storeProductDescriptionService.save(spd);

            if (CollUtil.isNotEmpty(request.getCouponIds())) {
                List<StoreProductCoupon> couponList = new ArrayList<>();
                for (Integer couponId : request.getCouponIds()) {
                    StoreProductCoupon spc = new StoreProductCoupon(storeProduct.getId(), couponId, DateUtil.getNowTime());
                    couponList.add(spc);
                }
                storeProductCouponService.saveBatch(couponList);
            }
            return Boolean.TRUE;
        });

        return execute;
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

    /**
     * 商品活动字符串
     * @param activityList 活动数组
     * @return 商品活动字符串
     */
    private String getProductActivityStr(List<String> activityList) {
        if (CollUtil.isEmpty(activityList)) {
            return "0, 1, 2, 3";
        }
        List<Integer> activities = new ArrayList<>();
        activityList.forEach(e->{
            switch (e) {
                case Constants.PRODUCT_TYPE_NORMAL_STR:
                    activities.add(Constants.PRODUCT_TYPE_NORMAL);
                    break;
                case Constants.PRODUCT_TYPE_SECKILL_STR:
                    activities.add(Constants.PRODUCT_TYPE_SECKILL);
                    break;
                case Constants.PRODUCT_TYPE_BARGAIN_STR:
                    activities.add(Constants.PRODUCT_TYPE_BARGAIN);
                    break;
                case Constants.PRODUCT_TYPE_PINGTUAN_STR:
                    activities.add(Constants.PRODUCT_TYPE_PINGTUAN);
                    break;
            }
        });
        return activities.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    /**
     * 更新商品信息
     * @param storeProductRequest 商品参数
     * @return 更新结果
     */
    @Override
    public Boolean update(StoreProductAddRequest storeProductRequest) {
        if (ObjectUtil.isNull(storeProductRequest.getId())) {
            throw new CrmebException("商品ID不能为空");
        }

        if (!storeProductRequest.getSpecType()) {
            if (storeProductRequest.getAttrValue().size() > 1) {
                throw new CrmebException("单规格商品属性值不能大于1");
            }
        }

        StoreProduct tempProduct = getById(storeProductRequest.getId());
        if (ObjectUtil.isNull(tempProduct)) {
            throw new CrmebException("商品不存在");
        }
        if (tempProduct.getIsRecycle() || tempProduct.getIsDel()) {
            throw new CrmebException("商品已删除");
        }
        if (tempProduct.getIsShow()) {
            throw new CrmebException("请先下架商品，再进行修改");
        }
        // 如果商品是活动商品主商品不允许修改
//        if (storeSeckillService.isExistByProductId(storeProductRequest.getId())) {
//            throw new CrmebException("商品作为秒杀商品的主商品，需要修改请先删除对应秒杀商品");
//        }
//        if (storeBargainService.isExistByProductId(storeProductRequest.getId())) {
//            throw new CrmebException("商品作为砍价商品的主商品，需要修改请先删除对应砍价商品");
//        }
//        if (storeCombinationService.isExistByProductId(storeProductRequest.getId())) {
//            throw new CrmebException("商品作为拼团商品的主商品，需要修改请先删除对应拼团商品");
//        }

        StoreProduct storeProduct = new StoreProduct();
        BeanUtils.copyProperties(storeProductRequest, storeProduct);

        // 设置Activity活动
        storeProduct.setActivity(getProductActivityStr(storeProductRequest.getActivity()));

        //主图
        storeProduct.setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()));

        //轮播图
        storeProduct.setSliderImage(systemAttachmentService.clearPrefix(storeProduct.getSliderImage()));

        List<StoreProductAttrValueAddRequest> attrValueAddRequestList = storeProductRequest.getAttrValue();
        //计算价格
        StoreProductAttrValueAddRequest minAttrValue = attrValueAddRequestList.stream().min(Comparator.comparing(StoreProductAttrValueAddRequest::getPrice)).get();
        storeProduct.setPrice(minAttrValue.getPrice());
        storeProduct.setOtPrice(minAttrValue.getOtPrice());
        storeProduct.setCost(minAttrValue.getCost());
        storeProduct.setStock(attrValueAddRequestList.stream().mapToInt(StoreProductAttrValueAddRequest::getStock).sum());

        // attr部分
        List<StoreProductAttrAddRequest> addRequestList = storeProductRequest.getAttr();
        List<StoreProductAttr> attrAddList = CollUtil.newArrayList();
        List<StoreProductAttr> attrUpdateList = CollUtil.newArrayList();
        addRequestList.forEach(e -> {
            StoreProductAttr attr = new StoreProductAttr();
            BeanUtils.copyProperties(e, attr);
            if (ObjectUtil.isNull(attr.getId())) {
                attr.setProductId(storeProduct.getId());
                attr.setType(Constants.PRODUCT_TYPE_NORMAL);
                attrAddList.add(attr);
            } else {
                attr.setIsDel(false);
                attrUpdateList.add(attr);
            }
        });

        // attrValue部分
        List<StoreProductAttrValue> attrValueAddList = CollUtil.newArrayList();
        List<StoreProductAttrValue> attrValueUpdateList = CollUtil.newArrayList();
        attrValueAddRequestList.forEach(e -> {
            StoreProductAttrValue attrValue = new StoreProductAttrValue();
            BeanUtils.copyProperties(e, attrValue);
            attrValue.setSuk(getSku(e.getAttrValue()));
            attrValue.setImage(systemAttachmentService.clearPrefix(e.getImage()));
            if (ObjectUtil.isNull(attrValue.getId()) || attrValue.getId().equals(0)) {
                attrValue.setId(null);
                attrValue.setProductId(storeProduct.getId());
                attrValue.setQuota(0);
                attrValue.setQuotaShow(0);
                attrValue.setType(Constants.PRODUCT_TYPE_NORMAL);
                attrValueAddList.add(attrValue);
            } else {
                attrValue.setIsDel(false);
                attrValueUpdateList.add(attrValue);
            }
        });

        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription();
        spd.setDescription(storeProductRequest.getContent().length() > 0 ? systemAttachmentService.clearPrefix(storeProductRequest.getContent()) : "");
        spd.setType(Constants.PRODUCT_TYPE_NORMAL);
        spd.setProductId(storeProduct.getId());

        Boolean execute = transactionTemplate.execute(e -> {
            dao.updateById(storeProduct);

            // 先删除原用attr+value
            attrService.deleteByProductIdAndType(storeProduct.getId(), Constants.PRODUCT_TYPE_NORMAL);
            storeProductAttrValueService.deleteByProductIdAndType(storeProduct.getId(), Constants.PRODUCT_TYPE_NORMAL);

            if (CollUtil.isNotEmpty(attrAddList)) {
                attrService.saveBatch(attrAddList);
            }
            if (CollUtil.isNotEmpty(attrUpdateList)) {
                attrService.saveOrUpdateBatch(attrUpdateList);
            }

            if (CollUtil.isNotEmpty(attrValueAddList)) {
                storeProductAttrValueService.saveBatch(attrValueAddList);
            }
            if (CollUtil.isNotEmpty(attrValueUpdateList)) {
                storeProductAttrValueService.saveOrUpdateBatch(attrValueUpdateList);
            }

            storeProductDescriptionService.deleteByProductId(storeProduct.getId(), Constants.PRODUCT_TYPE_NORMAL);
            storeProductDescriptionService.save(spd);

            if (CollUtil.isNotEmpty(storeProductRequest.getCouponIds())) {
                storeProductCouponService.deleteByProductId(storeProduct.getId());
                List<StoreProductCoupon> couponList = new ArrayList<>();
                for (Integer couponId : storeProductRequest.getCouponIds()) {
                    StoreProductCoupon spc = new StoreProductCoupon(storeProduct.getId(), couponId, DateUtil.getNowTime());
                    couponList.add(spc);
                }
                storeProductCouponService.saveBatch(couponList);
            } else {
                storeProductCouponService.deleteByProductId(storeProduct.getId());
            }

            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 商品详情
     * @param id 商品id
     * @return 详情数据
     */
    @Override
    public StoreProductResponse getByProductId(Integer id) {
        StoreProduct storeProduct = dao.selectById(id);
        if (null == storeProduct) throw new CrmebException("未找到对应商品信息");
        StoreProductResponse storeProductResponse = new StoreProductResponse();
        BeanUtils.copyProperties(storeProduct, storeProductResponse);
        StoreProductAttr spaPram = new StoreProductAttr();
        spaPram.setProductId(storeProduct.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);
        storeProductResponse.setAttr(attrService.getByEntity(spaPram));

        // 设置商品所参与的活动
        storeProductResponse.setActivityH5(productUtils.getProductCurrentActivity(storeProduct));
        StoreProductAttrValue spavPram = new StoreProductAttrValue();
        spavPram.setProductId(id).setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getByEntity(spavPram);
        // 根据attrValue生成前端所需的数据
        List<HashMap<String, Object>> attrValues = new ArrayList<>();

        if (storeProduct.getSpecType()) {
            // 后端多属性用于编辑
            StoreProductAttrResult sparPram = new StoreProductAttrResult();
            sparPram.setProductId(storeProduct.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);
            List<StoreProductAttrResult> attrResults = storeProductAttrResultService.getByEntity(sparPram);
            if (null == attrResults || attrResults.size() == 0) {
                throw new CrmebException("未找到对应属性值");
            }
            StoreProductAttrResult attrResult = attrResults.get(0);
            //PC 端生成skuAttrInfo
            List<StoreProductAttrValueRequest> storeProductAttrValueRequests =
                    com.alibaba.fastjson.JSONObject.parseArray(attrResult.getResult(), StoreProductAttrValueRequest.class);
            if (null != storeProductAttrValueRequests) {
                for (int i = 0; i < storeProductAttrValueRequests.size(); i++) {
//                    StoreProductAttrValueRequest storeProductAttrValueRequest = storeProductAttrValueRequests.get(i);
                    HashMap<String, Object> attrValue = new HashMap<>();
                    String currentSku = storeProductAttrValues.get(i).getSuk();
                    List<StoreProductAttrValue> hasCurrentSku =
                            storeProductAttrValues.stream().filter(e -> e.getSuk().equals(currentSku)).collect(Collectors.toList());
                    StoreProductAttrValue currentAttrValue = hasCurrentSku.get(0);
                    attrValue.put("id", hasCurrentSku.size() > 0 ? hasCurrentSku.get(0).getId():0);
                    attrValue.put("image", currentAttrValue.getImage());
                    attrValue.put("cost", currentAttrValue.getCost());
                    attrValue.put("price", currentAttrValue.getPrice());
                    attrValue.put("otPrice", currentAttrValue.getOtPrice());
                    attrValue.put("stock", currentAttrValue.getStock());
                    attrValue.put("barCode", currentAttrValue.getBarCode());
                    attrValue.put("weight", currentAttrValue.getWeight());
                    attrValue.put("volume", currentAttrValue.getVolume());
                    attrValue.put("suk", currentSku);
                    attrValue.put("attrValue", JSON.parseObject(storeProductAttrValues.get(i).getAttrValue(), Feature.OrderedField));
                    attrValue.put("brokerage", currentAttrValue.getBrokerage());
                    attrValue.put("brokerageTwo", currentAttrValue.getBrokerageTwo());
                    String[] skus = currentSku.split(",");
                    for (int k = 0; k < skus.length; k++) {
                        attrValue.put("value"+k,skus[k]);
                    }
                    attrValues.add(attrValue);
                }
            }
        }

        // H5 端用于生成skuList
        List<StoreProductAttrValueResponse> sPAVResponses = new ArrayList<>();

        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValues) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue,atr);
            sPAVResponses.add(atr);
        }
        storeProductResponse.setAttrValues(attrValues);
        storeProductResponse.setAttrValue(sPAVResponses);
//        if (null != storeProductAttrResult) {
            StoreProductDescription sd = storeProductDescriptionService.getOne(
                    new LambdaQueryWrapper<StoreProductDescription>()
                            .eq(StoreProductDescription::getProductId, storeProduct.getId())
                            .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_NORMAL));
            if (null != sd) {
                storeProductResponse.setContent(null == sd.getDescription()?"":sd.getDescription());
            }
//        }
        // 获取已关联的优惠券
        List<StoreProductCoupon> storeProductCoupons = storeProductCouponService.getListByProductId(storeProduct.getId());
        if (null != storeProductCoupons && storeProductCoupons.size() > 0) {
            List<Integer> ids = storeProductCoupons.stream().map(StoreProductCoupon::getIssueCouponId).collect(Collectors.toList());
            List<StoreCoupon> shipCoupons = storeCouponService.getByIds(ids);
            storeProductResponse.setCoupons(shipCoupons);
            storeProductResponse.setCouponIds(ids);
        }
        return storeProductResponse;
    }

    /**
     * 商品详情（管理端）
     * @param id 商品id
     * @return StoreProductInfoResponse
     */
    @Override
    public StoreProductInfoResponse getInfo(Integer id) {
        StoreProduct storeProduct = dao.selectById(id);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new CrmebException("未找到对应商品信息");
        }

        StoreProductInfoResponse storeProductResponse = new StoreProductInfoResponse();
        BeanUtils.copyProperties(storeProduct, storeProductResponse);

        // 设置商品所参与的活动
        List<String> activityList = getProductActivityList(storeProduct.getActivity());
        storeProductResponse.setActivity(activityList);

        List<StoreProductAttr> attrList = attrService.getListByProductIdAndType(storeProduct.getId(), Constants.PRODUCT_TYPE_NORMAL);
        storeProductResponse.setAttr(attrList);

        List<StoreProductAttrValue> attrValueList = storeProductAttrValueService.getListByProductIdAndType(storeProduct.getId(), Constants.PRODUCT_TYPE_NORMAL);
        List<AttrValueResponse> valueResponseList = attrValueList.stream().map(e -> {
            AttrValueResponse valueResponse = new AttrValueResponse();
            BeanUtils.copyProperties(e, valueResponse);
            return valueResponse;
        }).collect(Collectors.toList());
        storeProductResponse.setAttrValue(valueResponseList);

        StoreProductDescription sd = storeProductDescriptionService.getByProductIdAndType(storeProduct.getId(), Constants.PRODUCT_TYPE_NORMAL);
        if (ObjectUtil.isNotNull(sd)) {
            storeProductResponse.setContent(ObjectUtil.isNull(sd.getDescription()) ? "" : sd.getDescription());
        }

        // 获取已关联的优惠券
        List<StoreProductCoupon> storeProductCoupons = storeProductCouponService.getListByProductId(storeProduct.getId());
        if (CollUtil.isNotEmpty(storeProductCoupons)) {
            List<Integer> ids = storeProductCoupons.stream().map(StoreProductCoupon::getIssueCouponId).collect(Collectors.toList());
            storeProductResponse.setCouponIds(ids);
        }
        return storeProductResponse;
    }

    /**
     * 商品活动字符列表
     * @param activityStr 商品活动字符串
     * @return 商品活动字符列表
     */
    private List<String> getProductActivityList(String activityStr) {
        List<String> activityList = CollUtil.newArrayList();
        if ("0, 1, 2, 3".equals(activityStr)) {
            activityList.add(Constants.PRODUCT_TYPE_NORMAL_STR);
            activityList.add(Constants.PRODUCT_TYPE_SECKILL_STR);
            activityList.add(Constants.PRODUCT_TYPE_BARGAIN_STR);
            activityList.add(Constants.PRODUCT_TYPE_PINGTUAN_STR);
            return activityList;
        }
        String[] split = activityStr.split(",");
        for (String s : split) {
            Integer integer = Integer.valueOf(s);
            if (integer.equals(Constants.PRODUCT_TYPE_NORMAL)) {
                activityList.add(Constants.PRODUCT_TYPE_NORMAL_STR);
            }
            if (integer.equals(Constants.PRODUCT_TYPE_SECKILL)) {
                activityList.add(Constants.PRODUCT_TYPE_SECKILL_STR);
            }
            if (integer.equals(Constants.PRODUCT_TYPE_BARGAIN)) {
                activityList.add(Constants.PRODUCT_TYPE_BARGAIN_STR);
            }
            if (integer.equals(Constants.PRODUCT_TYPE_PINGTUAN)) {
                activityList.add(Constants.PRODUCT_TYPE_PINGTUAN_STR);
            }
        }
        return activityList;
    }

    /**
     * 根据商品tabs获取对应类型的产品数量
     * @return List
     */
    @Override
    public List<StoreProductTabsHeader> getTabsHeader() {
        List<StoreProductTabsHeader> headers = new ArrayList<>();
        StoreProductTabsHeader header1 = new StoreProductTabsHeader(0,"出售中商品",1);
        StoreProductTabsHeader header2 = new StoreProductTabsHeader(0,"仓库中商品",2);
        StoreProductTabsHeader header3 = new StoreProductTabsHeader(0,"已经售馨商品",3);
        StoreProductTabsHeader header4 = new StoreProductTabsHeader(0,"警戒库存",4);
        StoreProductTabsHeader header5 = new StoreProductTabsHeader(0,"商品回收站",5);
        headers.add(header1);
        headers.add(header2);
        headers.add(header3);
        headers.add(header4);
        headers.add(header5);
        for (StoreProductTabsHeader h : headers) {
            LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            switch (h.getType()) {
                case 1:
                    //出售中（已上架）
                    lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);
                    lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 2:
                    //仓库中（未上架）
                    lambdaQueryWrapper.eq(StoreProduct::getIsShow, false);
                    lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 3:
                    //已售罄
                    lambdaQueryWrapper.le(StoreProduct::getStock, 0);
                    lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 4:
                    //警戒库存
                    Integer stock = Integer.parseInt(systemConfigService.getValueByKey("store_stock"));
                    lambdaQueryWrapper.le(StoreProduct::getStock, stock);
                    lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 5:
                    //回收站
                    lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, true);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                default:
                    break;
            }
            List<StoreProduct> storeProducts = dao.selectList(lambdaQueryWrapper);
            h.setCount(storeProducts.size());
        }

        return headers;
    }

    /**
     * 后台任务批量操作库存
     */
    @Override
    public void consumeProductStock() {
        String redisKey = Constants.PRODUCT_STOCK_UPDATE;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("StoreProductServiceImpl.doProductStock | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (null == data) {
                continue;
            }
            try {
                StoreProductStockRequest storeProductStockRequest =
                        com.alibaba.fastjson.JSONObject.toJavaObject(com.alibaba.fastjson.JSONObject.parseObject(data.toString()), StoreProductStockRequest.class);
                boolean result = doProductStock(storeProductStockRequest);
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 根据商品id取出二级分类
     * @param productIdStr String 商品分类
     * @return List<Integer>
     */
    @Override
    public List<Integer> getSecondaryCategoryByProductId(String productIdStr) {
        List<Integer> idList = new ArrayList<>();

        if (StringUtils.isBlank(productIdStr)) {
            return idList;
        }
        List<Integer> productIdList = CrmebUtil.stringToArray(productIdStr);
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreProduct::getId, productIdList);
        List<StoreProduct> productList = dao.selectList(lambdaQueryWrapper);
        if (productIdList.size() < 1) {
            return idList;
        }

        //把所有的分类id写入集合
        for (StoreProduct storeProduct : productList) {
            List<Integer> categoryIdList = CrmebUtil.stringToArray(storeProduct.getCateId());
            idList.addAll(categoryIdList);
        }

        //去重
        List<Integer> cateIdList = idList.stream().distinct().collect(Collectors.toList());
        if (cateIdList.size() < 1) {
            return idList;
        }

        //取出所有的二级分类
        List<Category> categoryList = categoryService.getByIds(cateIdList);
        if (categoryList.size() < 1) {
            return idList;
        }

        for (Category category: categoryList) {
            List<Integer> parentIdList = CrmebUtil.stringToArrayByRegex(category.getPath(), "/");
            if (parentIdList.size() > 2) {
                Integer secondaryCategoryId = parentIdList.get(2);
                if (secondaryCategoryId > 0) {
                    idList.add(secondaryCategoryId);
                }
            }
        }
        return idList;
    }

    /**
     * 根据其他平台url导入产品信息
     * @param url 待导入平台url
     * @param tag 1=淘宝，2=京东，3=苏宁，4=拼多多， 5=天猫
     * @return StoreProductRequest
     */
    @Override
    public StoreProductRequest importProductFromUrl(String url, int tag) {
        StoreProductRequest productRequest = null;
        try {
            switch (tag) {
                case 1:
                    productRequest = productUtils.getTaobaoProductInfo(url,tag);
                    break;
                case 2:
                    productRequest = productUtils.getJDProductInfo(url,tag);
                    break;
                case 3:
                    productRequest = productUtils.getSuningProductInfo(url,tag);
                    break;
                case 4:
                    productRequest = productUtils.getPddProductInfo(url,tag);
                    break;
                case 5:
                    productRequest = productUtils.getTmallProductInfo(url,tag);
                    break;
            }
        } catch (Exception e) {
            throw new CrmebException("确认URL和平台是否正确，以及平台费用是否足额"+e.getMessage());
        }
        return productRequest;
    }

    /**
     *
     * @param productId 商品id
     * @param type 类型：recycle——回收站 delete——彻底删除
     * @return Boolean
     */
    @Override
    public Boolean deleteProduct(Integer productId, String type) {
        StoreProduct product = getById(productId);
        if (ObjectUtil.isNull(product)) {
            throw new CrmebException("商品不存在");
        }
        if (StrUtil.isNotBlank(type) && "recycle".equals(type) && product.getIsDel()) {
            throw new CrmebException("商品已存在回收站");
        }

        LambdaUpdateWrapper<StoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if (StrUtil.isNotBlank(type) && "delete".equals(type)) {
            // 判断商品活动状态(秒杀、砍价、拼团)
            isExistActivity(productId);

            lambdaUpdateWrapper.eq(StoreProduct::getId, productId);
            lambdaUpdateWrapper.set(StoreProduct::getIsDel, true);
            return update(lambdaUpdateWrapper);
        }
        lambdaUpdateWrapper.eq(StoreProduct::getId, productId);
        lambdaUpdateWrapper.set(StoreProduct::getIsRecycle, true);
        return update(lambdaUpdateWrapper);
    }

    /**
     * 判断商品活动状态(秒杀、砍价、拼团)
     * @param productId
     */
    private void isExistActivity(Integer productId) {
        Boolean existActivity = false;
        // 秒杀活动判断
        existActivity = storeSeckillService.isExistActivity(productId);
        if (existActivity) {
            throw new CrmebException("有商品关联的秒杀商品活动开启中，不能删除");
        }
        // 砍价活动判断
        existActivity = storeBargainService.isExistActivity(productId);
        if (existActivity) {
            throw new CrmebException("有商品关联的砍价商品活动开启中，不能删除");
        }
        // 拼团活动判断
        existActivity = storeCombinationService.isExistActivity(productId);
        if (existActivity) {
            throw new CrmebException("有商品关联的拼团商品活动开启中，不能删除");
        }
    }

    /**
     * 恢复已删除的商品
     * @param productId 商品id
     * @return 恢复结果
     */
    @Override
    public Boolean reStoreProduct(Integer productId) {
        LambdaUpdateWrapper<StoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(StoreProduct::getId, productId);
        lambdaUpdateWrapper.set(StoreProduct::getIsRecycle, false);
        return update(lambdaUpdateWrapper);
    }

    ///////////////////////////////////////////自定义方法

    /**
     * 扣减库存任务操作
     * @param storeProductStockRequest 扣减库存参数
     * @return 执行结果
     */
    @Override
    public boolean doProductStock(StoreProductStockRequest storeProductStockRequest) {
        // 获取商品本身信息
        StoreProduct existProduct = getById(storeProductStockRequest.getProductId());
        List<StoreProductAttrValue> existAttr =
                storeProductAttrValueService.getListByProductIdAndAttrId(
                        storeProductStockRequest.getProductId(),
                        storeProductStockRequest.getAttrId().toString(),
                        storeProductStockRequest.getType());
        if (null == existProduct || null == existAttr) { // 未找到商品
            logger.info("库存修改任务未获取到商品信息"+JSON.toJSONString(storeProductStockRequest));
            return true;
        }

        // 回滚商品库存/销量 并更新
        boolean isPlus = "add".equals(storeProductStockRequest.getOperationType());
        int productStock = isPlus ? existProduct.getStock() + storeProductStockRequest.getNum() : existProduct.getStock() - storeProductStockRequest.getNum();
        existProduct.setStock(productStock);
        existProduct.setSales(existProduct.getSales() - storeProductStockRequest.getNum());
        updateById(existProduct);

        // 回滚sku库存
        for (StoreProductAttrValue attrValue : existAttr) {
            int productAttrStock = isPlus ? attrValue.getStock() + storeProductStockRequest.getNum() : attrValue.getStock() - storeProductStockRequest.getNum();
            attrValue.setStock(productAttrStock);
            attrValue.setSales(attrValue.getSales()-storeProductStockRequest.getNum());
            storeProductAttrValueService.updateById(attrValue);
        }
        return true;
    }

    /**
     * 获取复制商品配置
     * @return copyType 复制类型：1：一号通
     *         copyNum 复制条数(一号通类型下有值)
     */
    @Override
    public MyRecord copyConfig() {
        String copyType = systemConfigService.getValueByKey("system_product_copy_type");
        if (StrUtil.isBlank(copyType)) {
            throw new CrmebException("请先进行采集商品配置");
        }
        int copyNum = 0;
        if ("1".equals(copyType)) {// 一号通
            JSONObject info = onePassService.info();
            copyNum = Optional.ofNullable(info.getJSONObject("copy").getInteger("num")).orElse(0);
        }
        MyRecord record = new MyRecord();
        record.set("copyType", copyType);
        record.set("copyNum", copyNum);
        return record;
    }

    /**
     * 复制平台商品
     * @param url 商品链接
     * @return MyRecord
     */
    @Override
    public MyRecord copyProduct(String url) {
        JSONObject jsonObject = onePassService.copyGoods(url);
        StoreProductRequest storeProductRequest = ProductUtils.onePassCopyTransition(jsonObject);
        MyRecord record = new MyRecord();
        return record.set("info", storeProductRequest);
    }

    /**
     * 添加/扣减库存
     * @param id 商品id
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String type) {
        UpdateWrapper<StoreProduct> updateWrapper = new UpdateWrapper<>();
        if ("add".equals(type)) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
        }
        if ("sub".equals(type)) {
            updateWrapper.setSql(StrUtil.format("stock = stock - {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales + {}", num));
            // 扣减时加乐观锁保证库存不为负
            updateWrapper.last(StrUtil.format(" and (stock - {} >= 0)", num));
        }
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新普通商品库存失败,商品id = " + id);
        }
        return update;
    }

    /**
     * 下架
     * @param id 商品id
     */
    @Override
    public Boolean offShelf(Integer id) {
        StoreProduct storeProduct = getById(id);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new CrmebException("商品不存在");
        }
        if (!storeProduct.getIsShow()) {
            return true;
        }

        storeProduct.setIsShow(false);
        Boolean execute = transactionTemplate.execute(e -> {
            dao.updateById(storeProduct);
            storeCartService.productStatusNotEnable(id);
            // 商品下架时，清除用户收藏
            storeProductRelationService.deleteByProId(storeProduct.getId());
            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 上架
     * @param id 商品id
     * @return Boolean
     */
    @Override
    public Boolean putOnShelf(Integer id) {
        StoreProduct storeProduct = getById(id);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new CrmebException("商品不存在");
        }
        if (storeProduct.getIsShow()) {
            return true;
        }

        // 获取商品skuid
        StoreProductAttrValue tempSku = new StoreProductAttrValue();
        tempSku.setProductId(id);
        tempSku.setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> skuList = storeProductAttrValueService.getByEntity(tempSku);
        List<Integer> skuIdList = skuList.stream().map(StoreProductAttrValue::getId).collect(Collectors.toList());

        storeProduct.setIsShow(true);
        Boolean execute = transactionTemplate.execute(e -> {
            dao.updateById(storeProduct);
            storeCartService.productStatusNoEnable(skuIdList);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 首页商品列表
     * @param type 类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】
     * @param pageParamRequest 分页参数
     * @return CommonPage
     */
    @Override
    public List<StoreProduct> getIndexProduct(Integer type, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(StoreProduct::getId, StoreProduct::getImage, StoreProduct::getStoreName,
                StoreProduct::getPrice, StoreProduct::getOtPrice, StoreProduct::getActivity);
        switch (type) {
            case Constants.INDEX_RECOMMEND_BANNER: //精品推荐
                lambdaQueryWrapper.eq(StoreProduct::getIsBest, true);
                break;
            case Constants.INDEX_HOT_BANNER: //热门榜单
                lambdaQueryWrapper.eq(StoreProduct::getIsHot, true);
                break;
            case Constants.INDEX_NEW_BANNER: //首发新品
                lambdaQueryWrapper.eq(StoreProduct::getIsNew, true);
                break;
            case Constants.INDEX_BENEFIT_BANNER: //促销单品
                lambdaQueryWrapper.eq(StoreProduct::getIsBenefit, true);
                break;
            case Constants.INDEX_GOOD_BANNER: // 优选推荐
                lambdaQueryWrapper.eq(StoreProduct::getIsGood, true);
                break;
        }

        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
        lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
        lambdaQueryWrapper.gt(StoreProduct::getStock, 0);
        lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);

        lambdaQueryWrapper.orderByDesc(StoreProduct::getSort);
        lambdaQueryWrapper.orderByDesc(StoreProduct::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 获取商品移动端列表
     * @param request 筛选参数
     * @param pageRequest 分页参数
     * @return List
     */
    @Override
    public List<StoreProduct> findH5List(ProductRequest request, PageParamRequest pageRequest) {

        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        // id、名称、图片、价格、销量、活动
        lqw.select(StoreProduct::getId, StoreProduct::getStoreName, StoreProduct::getImage, StoreProduct::getPrice,
                StoreProduct::getActivity, StoreProduct::getSales, StoreProduct::getFicti, StoreProduct::getUnitName,
                StoreProduct::getFlatPattern, StoreProduct::getStock);

        lqw.eq(StoreProduct::getIsRecycle, false);
        lqw.eq(StoreProduct::getIsDel, false);
        lqw.eq(StoreProduct::getMerId, false);
        lqw.gt(StoreProduct::getStock, 0);
        lqw.eq(StoreProduct::getIsShow, true);

        if (ObjectUtil.isNotNull(request.getCid()) && request.getCid() > 0) {
            //查找当前类下的所有子类
            List<Category> childVoListByPid = categoryService.getChildVoListByPid(request.getCid());
            List<Integer> categoryIdList = childVoListByPid.stream().map(Category::getId).collect(Collectors.toList());
            categoryIdList.add(request.getCid());
            lqw.apply(CrmebUtil.getFindInSetSql("cate_id", (ArrayList<Integer>) categoryIdList));
        }

        if (StrUtil.isNotBlank(request.getKeyword())) {
            if (CrmebUtil.isString2Num(request.getKeyword())) {
                Integer productId = Integer.valueOf(request.getKeyword());
                lqw.like(StoreProduct::getId, productId);
            } else {
                lqw.like(StoreProduct::getStoreName, request.getKeyword());
            }
        }

        // 排序部分
        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            if (request.getSalesOrder().equals(Constants.SORT_DESC)) {
                lqw.last(" order by (sales + ficti) desc, sort desc, id desc");
            } else {
                lqw.last(" order by (sales + ficti) asc, sort asc, id asc");
            }
        } else {
            if (StrUtil.isNotBlank(request.getPriceOrder())) {
                if (request.getPriceOrder().equals(Constants.SORT_DESC)) {
                    lqw.orderByDesc(StoreProduct::getPrice);
                } else {
                    lqw.orderByAsc(StoreProduct::getPrice);
                }
            }

            lqw.orderByDesc(StoreProduct::getSort);
            lqw.orderByDesc(StoreProduct::getId);
        }
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        return dao.selectList(lqw);
    }

    /**
     * 获取移动端商品详情
     * @param id 商品id
     * @return StoreProduct
     */
    @Override
    public StoreProduct getH5Detail(Integer id) {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId, StoreProduct::getImage, StoreProduct::getStoreName, StoreProduct::getSliderImage,
                StoreProduct::getOtPrice, StoreProduct::getStock, StoreProduct::getSales, StoreProduct::getPrice, StoreProduct::getActivity,
                StoreProduct::getFicti, StoreProduct::getIsSub, StoreProduct::getStoreInfo, StoreProduct::getBrowse, StoreProduct::getUnitName);
        lqw.eq(StoreProduct::getId, id);
        lqw.eq(StoreProduct::getIsRecycle, false);
        lqw.eq(StoreProduct::getIsDel, false);
        lqw.eq(StoreProduct::getIsShow, true);
        StoreProduct storeProduct = dao.selectOne(lqw);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new CrmebException(StrUtil.format("未找到编号为{}的商品", id));
        }

        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, storeProduct.getId())
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_NORMAL));
        if (ObjectUtil.isNotNull(sd)) {
            storeProduct.setContent(StrUtil.isBlank(sd.getDescription()) ? "" : sd.getDescription());
        }
        return storeProduct;
    }

    /**
     * 获取购物车商品信息
     * @param productId 商品编号
     * @return StoreProduct
     */
    @Override
    public StoreProduct getCartByProId(Integer productId) {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId, StoreProduct::getImage, StoreProduct::getStoreName);
        lqw.eq(StoreProduct::getId, productId);
        return dao.selectOne(lqw);
    }

    /**
     * 根据日期获取新增商品数量
     * @param date 日期，yyyy-MM-dd格式
     * @return Integer
     */
    @Override
    public Integer getNewProductByDate(String date) {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId);
        lqw.eq(StoreProduct::getIsDel, 0);
        lqw.apply("date_format(add_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 获取所有未删除的商品
     * @return List<StoreProduct>
     */
    @Override
    public List<StoreProduct> findAllProductByNotDelte() {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId);
        lqw.eq(StoreProduct::getIsDel, 0);
        return dao.selectList(lqw);
    }

    /**
     * 模糊搜索商品名称
     * @param productName 商品名称
     * @return List
     */
    @Override
    public List<StoreProduct> likeProductName(String productName) {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId);
        lqw.like(StoreProduct::getStoreName, productName);
        lqw.eq(StoreProduct::getIsDel, 0);
        return dao.selectList(lqw);
    }

    /**
     * 警戒库存数量
     * @return Integer
     */
    @Override
    public Integer getVigilanceInventoryNum() {
        Integer stock = Integer.parseInt(systemConfigService.getValueByKey("store_stock"));
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.le(StoreProduct::getStock, stock);
        lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 销售中（上架）商品数量
     * @return Integer
     */
    @Override
    public Integer getOnSaleNum() {
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);
        lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 未销售（仓库）商品数量
     * @return Integer
     */
    @Override
    public Integer getNotSaleNum() {
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(StoreProduct::getIsShow, false);
        lambdaQueryWrapper.eq(StoreProduct::getIsRecycle, false);
        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 获取商品排行榜
     * 1.   3个商品以内不返回数据
     * 2.   TOP10
     * @return List
     */
    @Override
    public List<StoreProduct> getLeaderboard() {
        QueryWrapper<StoreProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_show", true);
        queryWrapper.eq("is_recycle", false);
        queryWrapper.eq("is_del", false);
        queryWrapper.last("limit 10");
        Integer count = dao.selectCount(queryWrapper);
        if (count < 4) {
            return CollUtil.newArrayList();
        }
        queryWrapper.select("id", "store_name", "image", "price", "ot_price", "(sales + ficti) as sales");
        queryWrapper.orderByDesc("sales");
        return dao.selectList(queryWrapper);
    }

}

