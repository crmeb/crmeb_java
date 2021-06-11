package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.front.request.IndexStoreProductSearchRequest;
import com.zbkj.crmeb.front.request.ProductRequest;
import com.zbkj.crmeb.marketing.model.StoreCoupon;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
import com.zbkj.crmeb.pass.service.OnePassService;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.store.dao.StoreProductDao;
import com.zbkj.crmeb.store.model.*;
import com.zbkj.crmeb.store.request.StoreProductAttrValueRequest;
import com.zbkj.crmeb.store.request.StoreProductRequest;
import com.zbkj.crmeb.store.request.StoreProductSearchRequest;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.response.StoreProductAttrValueResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.response.StoreProductTabsHeader;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.ProductUtils;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.task.order.OrderRefundTask;
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
import java.util.stream.Collectors;

/**
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

    private static final Logger logger = LoggerFactory.getLogger(OrderRefundTask.class);

    /**
     * H5端使用
     * @param request
     * @param pageParamRequest
     * @param productIdList
     * @return
     */
    @Override
    public List<StoreProduct> getList(StoreProductSearchRequest request, PageParamRequest pageParamRequest, List<Integer> productIdList) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(request.getIsBest() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsBest, request.getIsBest());
        }

        if(request.getIsHot() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsHot, request.getIsHot());
        }

        if(request.getIsNew() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsNew, request.getIsNew());
        }

        if(request.getIsBenefit() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsBest, request.getIsBenefit());
        }

        if(null != productIdList && productIdList.size() > 0){
            lambdaQueryWrapper.in(StoreProduct::getId, productIdList);
        }

        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false)
                .eq(StoreProduct::getMerId, false)
                .gt(StoreProduct::getStock, 0)
                .eq(StoreProduct::getIsShow, true)
                .orderByDesc(StoreProduct::getSort)
                .orderByDesc(StoreProduct::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-27
    * @return List<StoreProduct>
    */
    @Override
    public PageInfo<StoreProductResponse> getList(StoreProductSearchRequest request, PageParamRequest pageParamRequest) {
        Page<StoreProduct> storeProductPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProduct 类的多条件查询
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //类型搜索
        switch (request.getType()){
            case 1:
                //出售中（已上架）
                lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 2:
                //仓库中（未上架）
                lambdaQueryWrapper.eq(StoreProduct::getIsShow, false);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 3:
                //已售罄
                lambdaQueryWrapper.le(StoreProduct::getStock, 0);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 4:
                //警戒库存
                Integer stock = Integer.parseInt(systemConfigService.getValueByKey("store_stock"));
                lambdaQueryWrapper.le(StoreProduct::getStock, stock);
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                break;
            case 5:
                //回收站
                lambdaQueryWrapper.eq(StoreProduct::getIsDel, true);
                break;
            default:
                break;
        }

        //关键字搜索
        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i
                    .or().eq(StoreProduct::getId, request.getKeywords())
                    .or().like(StoreProduct::getStoreName, request.getKeywords())
                    .or().like(StoreProduct::getStoreInfo, request.getKeywords())
                    .or().like(StoreProduct::getKeyword, request.getKeywords())
                    .or().like(StoreProduct::getBarCode, request.getKeywords()));
        }
        if(StringUtils.isNotBlank(request.getCateId())){
            lambdaQueryWrapper.apply(CrmebUtil.getFindInSetSql("cate_id", request.getCateId()));
        }
        lambdaQueryWrapper.orderByDesc(StoreProduct::getSort).orderByDesc(StoreProduct::getId);
        List<StoreProduct> storeProducts = dao.selectList(lambdaQueryWrapper);
        List<StoreProductResponse> storeProductResponses = new ArrayList<>();
        for (StoreProduct product : storeProducts) {
            StoreProductResponse storeProductResponse = new StoreProductResponse();
            BeanUtils.copyProperties(product, storeProductResponse);
//            List<StoreProductAttr> attrs = attrService.getByProductId(product.getId());
            StoreProductAttr storeProductAttrPram = new StoreProductAttr();
            storeProductAttrPram.setProductId(product.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);
            List<StoreProductAttr> attrs = attrService.getByEntity(storeProductAttrPram);


            if(attrs.size() > 0){
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
            if(null != sd){
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
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据产品属性查询
     * @param storeProduct 产品参数
     * @return 产品结果
     */
    @Override
    public StoreProduct getByEntity(StoreProduct storeProduct) {
        LambdaQueryWrapper<StoreProduct> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeProduct);
        return dao.selectOne(lqw);
    }

    /**
     * 新增产品
     * @param storeProductRequest 新增产品request对象
     * @return 新增结果
     */
    @Override
    public boolean save(StoreProductRequest storeProductRequest) {
        // 多规格需要校验规格参数
        if (storeProductRequest.getSpecType()) {
            if (CollUtil.isEmpty(storeProductRequest.getAttr())) {
                throw new CrmebException("商品属性不能为空");
            }
            if (CollUtil.isEmpty(storeProductRequest.getAttrValue())) {
                throw new CrmebException("商品属性详情不能为空");
            }
            // 校验商品属性详情参数
            checkAttrValue(storeProductRequest.getAttrValue());
        }

        StoreProduct storeProduct = new StoreProduct();
        BeanUtils.copyProperties(storeProductRequest, storeProduct);
        storeProduct.setAddTime(DateUtil.getNowTime());

        // 设置Acticity活动
        productUtils.setProductActivity(storeProductRequest, storeProduct);

        //主图
        storeProduct.setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()));

        //轮播图
        storeProduct.setSliderImage(systemAttachmentService.clearPrefix(storeProduct.getSliderImage()));

        //计算价格
        productUtils.calcPriceForAttrValues(storeProductRequest, storeProduct);

        // 商品活动默认值
        storeProduct.setActivity("0, 1, 2, 3");

        //保存数据
        boolean save = save(storeProduct);
        if(storeProductRequest.getSpecType()) { // 多属性
            storeProductRequest.getAttr().forEach(e->{
                e.setProductId(storeProduct.getId());
                e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"",""),"[]"));
                e.setType(Constants.PRODUCT_TYPE_NORMAL);
            });
            boolean attrAddResult = attrService.saveOrUpdateBatch(storeProductRequest.getAttr());
            if (!attrAddResult) throw new CrmebException("新增属性名失败");
        }else{ // 单属性
            StoreProductAttr singleAttr = new StoreProductAttr();
            singleAttr.setProductId(storeProduct.getId()).setAttrName("规格").setAttrValues("默认").setType(Constants.PRODUCT_TYPE_NORMAL);
            boolean attrAddResult = attrService.save(singleAttr);
            if (!attrAddResult) throw new CrmebException("新增属性名失败");
            StoreProductAttrValue singleAttrValue = new StoreProductAttrValue();
            BigDecimal commissionL1= BigDecimal.ZERO;
            BigDecimal commissionL2= BigDecimal.ZERO;
            if(storeProductRequest.getAttrValue().size()>0){
                commissionL1 = null != storeProductRequest.getAttrValue().get(0).getBrokerage() ?
                        storeProductRequest.getAttrValue().get(0).getBrokerage():BigDecimal.ZERO;
                commissionL2 = null != storeProductRequest.getAttrValue().get(0).getBrokerageTwo() ?
                        storeProductRequest.getAttrValue().get(0).getBrokerageTwo():BigDecimal.ZERO;
            }

            singleAttrValue.setProductId(storeProduct.getId()).setStock(storeProduct.getStock()).setSuk("默认")
                    .setSales(storeProduct.getSales()).setPrice(storeProduct.getPrice())
                    .setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()))
                    .setCost(storeProduct.getCost()).setBarCode(storeProduct.getBarCode())
                    .setOtPrice(storeProduct.getOtPrice()).setBrokerage(commissionL1).setBrokerageTwo(commissionL2)
                    .setType(Constants.PRODUCT_TYPE_NORMAL);
            boolean saveOrUpdateResult = storeProductAttrValueService.save(singleAttrValue);
            if(!saveOrUpdateResult) throw new CrmebException("新增属性详情失败");
        }
        if (null != storeProductRequest.getAttrValue() && storeProductRequest.getAttrValue().size() > 0) {
            // 批量设置attrValues对象的商品id
            List<StoreProductAttrValueRequest> storeProductAttrValuesRequest = storeProductRequest.getAttrValue();
            storeProductAttrValuesRequest.forEach(e->{
                e.setProductId(storeProduct.getId());
            });
            List<StoreProductAttrValue> storeProductAttrValues = new ArrayList<>();
            for (StoreProductAttrValueRequest attrValuesRequest : storeProductAttrValuesRequest) {
                StoreProductAttrValue spav = new StoreProductAttrValue();
                BeanUtils.copyProperties(attrValuesRequest,spav);
                //设置sku字段
                if(null == attrValuesRequest.getAttrValue()){
                    break;
                }
                List<String> skuList = new ArrayList<>();
                for(Map.Entry<String,String> vo: attrValuesRequest.getAttrValue().entrySet()){
                    skuList.add(vo.getValue());
                    spav.setSuk(String.join(",",skuList));
                }
                spav.setImage(systemAttachmentService.clearPrefix(spav.getImage()));
                spav.setAttrValue(JSON.toJSONString(attrValuesRequest.getAttrValue()));
                spav.setType(Constants.PRODUCT_TYPE_NORMAL);
                storeProductAttrValues.add(spav);
            }
            // 保存属性
            if(storeProductAttrValues.size() > 0){
                boolean saveOrUpdateResult = storeProductAttrValueService.saveOrUpdateBatch(storeProductAttrValues);
                StoreProductAttrResult attrResult = new StoreProductAttrResult(
                        0,
                        storeProduct.getId(),
                        systemAttachmentService.clearPrefix(JSON.toJSONString(storeProductRequest.getAttrValue())),
                        DateUtil.getNowTime(),Constants.PRODUCT_TYPE_NORMAL);
                storeProductAttrResultService.save(attrResult);
                if(!saveOrUpdateResult) throw new CrmebException("新增属性详情失败");
            }
        }
        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription(
                storeProduct.getId(),  storeProductRequest.getContent().length() > 0
                ? systemAttachmentService.clearPrefix(storeProductRequest.getContent()):"",Constants.PRODUCT_TYPE_NORMAL);
        storeProductDescriptionService.deleteByProductId(spd.getProductId(),Constants.PRODUCT_TYPE_NORMAL);
        storeProductDescriptionService.save(spd);

        // 处理优惠券关联信息
        productUtils.shipProductCoupons(storeProductRequest, storeProduct);
        return save;
    }

    /**
     * 校验商品属性详情参数
     * @param attrValue 商品属性详情
     */
    private void checkAttrValue(List<StoreProductAttrValueRequest> attrValue) {
        for (StoreProductAttrValueRequest attr : attrValue) {
            if (CollUtil.isEmpty(attr.getAttrValue())) {
                throw new CrmebException("商品属性详情sku不能为空");
            }
            if (ObjectUtil.isNull(attr.getStock())) {
                throw new CrmebException("商品属性详情库存不能为空");
            }
            if (attr.getStock() < 0) {
                throw new CrmebException("商品属性详情库存不能小于0");
            }
            if (ObjectUtil.isNull(attr.getPrice())) {
                throw new CrmebException("商品属性详情金额不能为空");
            }
            if (attr.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CrmebException("商品属性详情金额必须大于0");
            }
            if (ObjectUtil.isNull(attr.getCost()) || attr.getCost().compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("商品属性详情成本价 不能为空或者小于0");
            }
            if (ObjectUtil.isNull(attr.getOtPrice()) || attr.getOtPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("商品属性详情原价 不能为空或者小于0");
            }
            if (ObjectUtil.isNull(attr.getWeight()) || attr.getWeight().compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("商品属性详情重量 不能为空或者小于0");
            }
            if (ObjectUtil.isNull(attr.getVolume()) || attr.getVolume().compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("商品属性详情体积 不能为空或者小于0");
            }
//            if (ObjectUtil.isNull(attr.getBrokerage()) || attr.getBrokerage().compareTo(BigDecimal.ZERO) < 0) {
//                throw new CrmebException("商品属性详情一级返佣 不能为空或者小于0");
//            }
//            if (ObjectUtil.isNull(attr.getBrokerageTwo()) || attr.getBrokerageTwo().compareTo(BigDecimal.ZERO) < 0) {
//                throw new CrmebException("商品属性详情二级返佣 不能为空或者小于0");
//            }
//            if (ObjectUtil.isNull(attr.getType())) {
//                throw new CrmebException("商品属性详情活动类型不能为空");
//            }
        }
    }

    /**
     * 更新产品
     * @param storeProductRequest 更新产品request对象
     * @return 更新结果
     */
    @Override
    public boolean update(StoreProductRequest storeProductRequest) {
        // 多规格需要校验规格参数
        if (storeProductRequest.getSpecType()) {
            if (CollUtil.isEmpty(storeProductRequest.getAttr())) {
                throw new CrmebException("商品属性不能为空");
            }
            if (CollUtil.isEmpty(storeProductRequest.getAttrValue())) {
                throw new CrmebException("商品属性详情不能为空");
            }
            // 校验商品属性详情参数
            checkAttrValue(storeProductRequest.getAttrValue());
        }

        StoreProduct tempProduct = getById(storeProductRequest.getId());
        if (ObjectUtil.isNull(tempProduct)) {
            throw new CrmebException("商品不存在");
        }
        if (tempProduct.getIsShow()) {
            throw new CrmebException("请先下架商品，再进行修改");
        }

        StoreProduct storeProduct = new StoreProduct();
        BeanUtils.copyProperties(storeProductRequest, storeProduct);
        // 设置Acticity活动
        productUtils.setProductActivity(storeProductRequest, storeProduct);

        storeProduct.setAddTime(DateUtil.getNowTime());

        //主图
        storeProduct.setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()));

        //轮播图
        storeProduct.setSliderImage(systemAttachmentService.clearPrefix(storeProduct.getSliderImage()));

        productUtils.calcPriceForAttrValues(storeProductRequest, storeProduct);
        int saveCount = dao.updateById(storeProduct);
        // 对attr表做全量更新，删除原有数据保存现有数据
        attrService.removeByProductId(storeProduct.getId(),Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> attrValueList = storeProductAttrValueService.getListByProductIdAndAttrId(
                storeProduct.getId(), null, Constants.PRODUCT_TYPE_NORMAL);
        Map<String, Integer> valueMap = CollUtil.newHashMap();
        attrValueList.forEach(e -> {
            valueMap.put(e.getSuk(), e.getId());
        });
        storeProductAttrValueService.removeByProductId(storeProduct.getId(),Constants.PRODUCT_TYPE_NORMAL);

        // 处理attr
        if(storeProductRequest.getSpecType()) {// 多规格
            storeProductRequest.getAttr().forEach(e -> {
                e.setProductId(storeProductRequest.getId());
                e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"", ""), "[]"));
                e.setType(Constants.PRODUCT_TYPE_NORMAL);
            });
            attrService.saveOrUpdateBatch(storeProductRequest.getAttr());
        }

        // 处理attrValue

        if(storeProductRequest.getSpecType()) {// 多规格
            storeProductRequest.getAttr().forEach(e->{
                e.setProductId(storeProductRequest.getId());
                e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"",""),"[]"));
                e.setType(Constants.PRODUCT_TYPE_NORMAL);
            });
            attrService.saveOrUpdateBatch(storeProductRequest.getAttr());
            if(null != storeProductRequest.getAttrValue() && storeProductRequest.getAttrValue().size() > 0){

                List<StoreProductAttrValueRequest> storeProductAttrValuesRequest = storeProductRequest.getAttrValue();
                // 批量设置attrValues对象的商品id
                storeProductAttrValuesRequest.forEach(e->e.setProductId(storeProductRequest.getId()));
                List<StoreProductAttrValue> storeProductAttrValues = new ArrayList<>();
                for (StoreProductAttrValueRequest attrValuesRequest : storeProductAttrValuesRequest) {
                    StoreProductAttrValue spav = new StoreProductAttrValue();
                    BeanUtils.copyProperties(attrValuesRequest,spav);
                    //设置sku字段
                    if(null != attrValuesRequest.getAttrValue()){
                        List<String> skuList = new ArrayList<>();
                        for(Map.Entry<String,String> vo: attrValuesRequest.getAttrValue().entrySet()){
                            skuList.add(vo.getValue());
                        }
                        spav.setSuk(String.join(",",skuList));
                    }
                    spav.setAttrValue(JSON.toJSONString(attrValuesRequest.getAttrValue()));
                    spav.setImage(systemAttachmentService.clearPrefix(spav.getImage()));
                    spav.setType(Constants.PRODUCT_TYPE_NORMAL);
                    storeProductAttrValues.add(spav);
                }
                storeProductAttrValues.forEach(e -> {
                    if (valueMap.containsKey(e.getSuk())) {
                        e.setId(valueMap.get(e.getSuk()));
                    }
                });

                boolean saveOrUpdateResult = storeProductAttrValueService.saveBatch(storeProductAttrValues);

                // attrResult整存整取，不做更新
                storeProductAttrResultService.deleteByProductId(storeProduct.getId(),Constants.PRODUCT_TYPE_NORMAL);
                StoreProductAttrResult attrResult = new StoreProductAttrResult(
                        0,
                        storeProduct.getId(),
                        systemAttachmentService.clearPrefix(JSON.toJSONString(storeProductRequest.getAttrValue())),
                        DateUtil.getNowTime(),Constants.PRODUCT_TYPE_NORMAL);
                storeProductAttrResultService.save(attrResult);
                if(!saveOrUpdateResult) throw new CrmebException("编辑属性详情失败");

            }
        }else{
            StoreProductAttr singleAttr = new StoreProductAttr();
            singleAttr.setProductId(storeProduct.getId()).setAttrName("规格").setAttrValues("默认").setType(0);

            boolean attrAddResult = attrService.save(singleAttr);
            if (!attrAddResult) throw new CrmebException("新增属性名失败");
            StoreProductAttrValue singleAttrValue = new StoreProductAttrValue();
            if(storeProductRequest.getAttrValue().size() == 0) throw new CrmebException("attrValue不能为空");
            StoreProductAttrValueRequest attrValueRequest = storeProductRequest.getAttrValue().get(0);
            BeanUtils.copyProperties(attrValueRequest,singleAttrValue);
            singleAttrValue.setProductId(storeProduct.getId());
            singleAttrValue.setSuk("默认");
            singleAttrValue.setImage(systemAttachmentService.clearPrefix(singleAttrValue.getImage()));
            singleAttrValue.setType(Constants.PRODUCT_TYPE_NORMAL);
            if (valueMap.containsKey(singleAttrValue.getSuk())) {
                singleAttrValue.setId(valueMap.get(singleAttrValue.getSuk()));
            }
            boolean saveOrUpdateResult = storeProductAttrValueService.save(singleAttrValue);
            if(!saveOrUpdateResult) throw new CrmebException("新增属性详情失败");
        }

        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription(
                storeProduct.getId(),
                storeProductRequest.getContent().length() > 0
                        ? systemAttachmentService.clearPrefix(storeProductRequest.getContent()):storeProductRequest.getContent()
                ,Constants.PRODUCT_TYPE_NORMAL);
        storeProductDescriptionService.deleteByProductId(spd.getProductId(),Constants.PRODUCT_TYPE_NORMAL);
        storeProductDescriptionService.save(spd);

        // 处理优惠券关联信息
        productUtils.shipProductCoupons(storeProductRequest, storeProduct);
        return saveCount > 0;
    }

    /**
     * 商品详情
     * @param id 商品id
     * @return 详情数据
     */
    @Override
    public StoreProductResponse getByProductId(int id) {
        StoreProduct storeProduct = dao.selectById(id);
        if(null == storeProduct) throw new CrmebException("未找到对应商品信息");
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

        if(storeProduct.getSpecType()){
            // 后端多属性用于编辑
            StoreProductAttrResult sparPram = new StoreProductAttrResult();
            sparPram.setProductId(storeProduct.getId()).setType(Constants.PRODUCT_TYPE_NORMAL);
            List<StoreProductAttrResult> attrResults = storeProductAttrResultService.getByEntity(sparPram);
            if(null == attrResults || attrResults.size() == 0){
                throw new CrmebException("未找到对应属性值");
            }
            StoreProductAttrResult attrResult = attrResults.get(0);
            //PC 端生成skuAttrInfo
            List<StoreProductAttrValueRequest> storeProductAttrValueRequests =
                    com.alibaba.fastjson.JSONObject.parseArray(attrResult.getResult(), StoreProductAttrValueRequest.class);
            if(null != storeProductAttrValueRequests){
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
//        if(null != storeProductAttrResult){
            StoreProductDescription sd = storeProductDescriptionService.getOne(
                    new LambdaQueryWrapper<StoreProductDescription>()
                            .eq(StoreProductDescription::getProductId, storeProduct.getId())
                            .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_NORMAL));
            if(null != sd){
                storeProductResponse.setContent(null == sd.getDescription()?"":sd.getDescription());
            }
//        }
        // 获取已关联的优惠券
        List<StoreProductCoupon> storeProductCoupons = storeProductCouponService.getListByProductId(storeProduct.getId());
        if(null != storeProductCoupons && storeProductCoupons.size() > 0){
            List<Integer> ids = storeProductCoupons.stream().map(StoreProductCoupon::getIssueCouponId).collect(Collectors.toList());
            List<StoreCoupon> shipCoupons = storeCouponService.getByIds(ids);
            storeProductResponse.setCoupons(shipCoupons);
            storeProductResponse.setCouponIds(ids);
        }
        return storeProductResponse;
    }

    /**
     * 产品列表
     * @param request 商品查询参数
     * @param pageParamRequest  分页参数
     * @return  商品查询结果
     */
    @Override
    public List<StoreProduct> getList(IndexStoreProductSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if(request.getIsBest() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsBest, request.getIsBest());
        }

        if(request.getIsHot() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsHot, request.getIsHot());
        }

        if(request.getIsNew() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsNew, request.getIsNew());
        }

        if(request.getIsBenefit() != null){
            lambdaQueryWrapper.eq(StoreProduct::getIsBenefit, request.getIsBenefit());
        }

        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false)
                .eq(StoreProduct::getMerId, false)
                .gt(StoreProduct::getStock, 0)
                .eq(StoreProduct::getIsShow, true);

        if(null != request.getCateId() && request.getCateId().size() > 0 ){
            lambdaQueryWrapper.apply(CrmebUtil.getFindInSetSql("cate_id", (ArrayList<Integer>) request.getCateId()));
        }

        if(StringUtils.isNotBlank(request.getKeywords())){
            if(CrmebUtil.isString2Num(request.getKeywords())){
                Integer productId = Integer.valueOf(request.getKeywords());
                lambdaQueryWrapper.like(StoreProduct::getId, productId);
            }else{
                lambdaQueryWrapper
                        .like(StoreProduct::getStoreName, request.getKeywords())
                        .or().like(StoreProduct::getStoreInfo, request.getKeywords())
                        .or().like(StoreProduct::getBarCode, request.getKeywords());
            }
        }

        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            if(request.getSalesOrder().equals(Constants.SORT_DESC)){
                lambdaQueryWrapper.last(" order by (sales + ficti) desc, sort desc, id desc");
            }else{
                lambdaQueryWrapper.last(" order by (sales + ficti) asc, sort asc, id asc");
            }
        } else {
            if(!StringUtils.isBlank(request.getPriceOrder())){
                if(request.getPriceOrder().equals(Constants.SORT_DESC)){
                    lambdaQueryWrapper.orderByDesc(StoreProduct::getPrice);
                }else{
                    lambdaQueryWrapper.orderByAsc(StoreProduct::getPrice);
                }
            }

            lambdaQueryWrapper.orderByDesc(StoreProduct::getSort);
            lambdaQueryWrapper.orderByDesc(StoreProduct::getId);
        }
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据商品tabs获取对应类型的产品数量
     * @return
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
        for (StoreProductTabsHeader h : headers){
            LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            switch (h.getType()){
                case 1:
                    //出售中（已上架）
                    lambdaQueryWrapper.eq(StoreProduct::getIsShow, true);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 2:
                    //仓库中（未上架）
                    lambdaQueryWrapper.eq(StoreProduct::getIsShow, false);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 3:
                    //已售罄
                    lambdaQueryWrapper.le(StoreProduct::getStock, 0);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 4:
                    //警戒库存
                    Integer stock = Integer.parseInt(systemConfigService.getValueByKey("store_stock"));
                    lambdaQueryWrapper.le(StoreProduct::getStock, stock);
                    lambdaQueryWrapper.eq(StoreProduct::getIsDel, false);
                    break;
                case 5:
                    //回收站
                    lambdaQueryWrapper.or().eq(StoreProduct::getIsDel, true);
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
     * 库存变动写入redis队列
     * @param request StoreProductStockRequest 参数对象
     * @author Mr.Zhang
     * @since 2020-05-06
     * @return int
     */
    @Override
    public boolean stockAddRedis(StoreProductStockRequest request) {
        String _productString = JSON.toJSONString(request);
        redisUtil.lPush(Constants.PRODUCT_STOCK_UPDATE, _productString);
        return true;
    }

    /**
     * 后台任务批量操作库存
     */
    @Override
    public void consumeProductStock() {
        String redisKey = Constants.PRODUCT_STOCK_UPDATE;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("StoreProductServiceImpl.doProductStock | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }
            try{
                StoreProductStockRequest storeProductStockRequest =
                        com.alibaba.fastjson.JSONObject.toJavaObject(com.alibaba.fastjson.JSONObject.parseObject(data.toString()), StoreProductStockRequest.class);
                boolean result = doProductStock(storeProductStockRequest);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 扣减库存添加销量
     * @param productId 产品id
     * @param num 商品数量
     * @param type 是否限购
     * @return 扣减结果
     */
    @Override
    public boolean decProductStock(Integer productId, Integer num, Integer attrValueId, Integer type) {
        // 因为attrvalue表中unique使用Id代替，更新前先查询此表是否存在
        // 不存在=但属性 存在则是多属性
        StoreProductAttrValue productsInAttrValue = storeProductAttrValueService.getById(attrValueId);
        StoreProduct storeProduct = getById(productId);
        boolean result = storeProductAttrValueService.decProductAttrStock(productId,attrValueId,num,type);
        if (!result) return result;
        LambdaUpdateWrapper<StoreProduct> lqwuper = new LambdaUpdateWrapper<>();
        lqwuper.set(StoreProduct::getStock, storeProduct.getStock()-num);
        lqwuper.set(StoreProduct::getSales, storeProduct.getSales()+num);
        lqwuper.eq(StoreProduct::getId, productId);
        lqwuper.apply(StrUtil.format(" (stock - {} >= 0) ", num));
        result = update(lqwuper);
        if(result){ //判断库存警戒值
            Integer alterNumI=0;
            String alterNum = systemConfigService.getValueByKey("store_stock");
            if(StringUtils.isNotBlank(alterNum)) alterNumI = Integer.parseInt(alterNum);
            if(alterNumI >= productsInAttrValue.getStock()){
                // todo socket 发送库存警告
            }
        }
        return result;
    }

    /**
     * 根据商品id取出二级分类
     * @param productIdStr String 商品分类
     * @return List<Integer>
     */
    @Override
    public List<Integer> getSecondaryCategoryByProductId(String productIdStr) {
        List<Integer> idList = new ArrayList<>();

        if(StringUtils.isBlank(productIdStr)){
            return idList;
        }
        List<Integer> productIdList = CrmebUtil.stringToArray(productIdStr);
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreProduct::getId, productIdList);
        List<StoreProduct> productList = dao.selectList(lambdaQueryWrapper);
        if(productIdList.size() < 1){
            return idList;
        }

        //把所有的分类id写入集合
        for (StoreProduct storeProduct : productList) {
            List<Integer> categoryIdList = CrmebUtil.stringToArray(storeProduct.getCateId());
            idList.addAll(categoryIdList);
        }

        //去重
        List<Integer> cateIdList = idList.stream().distinct().collect(Collectors.toList());
        if(cateIdList.size() < 1){
            return idList;
        }

        //取出所有的二级分类
        List<Category> categoryList = categoryService.getByIds(cateIdList);
        if(categoryList.size() < 1){
            return idList;
        }

        for (Category category: categoryList) {
            List<Integer> parentIdList = CrmebUtil.stringToArrayByRegex(category.getPath(), "/");
            if(parentIdList.size() > 2){
                Integer secondaryCategoryId = parentIdList.get(2);
                if(secondaryCategoryId > 0){
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
     * @return
     */
    @Override
    public StoreProductRequest importProductFromUrl(String url, int tag) {
        StoreProductRequest productRequest = null;
        try {
            switch (tag){
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
        }catch (Exception e){
            throw new CrmebException("确认URL和平台是否正确，以及平台费用是否足额"+e.getMessage());
        }
        return productRequest;
    }


    /**
     * 推荐商品列表
     * @param limit 最大数据量
     * @return 推荐商品列表集
     */
    @Override
    public List<StoreProduct> getRecommendStoreProduct(Integer limit) {
        if(limit <0 || limit > 20) throw new CrmebException("获取推荐商品数量不合法 limit > 0 || limit < 20");
        LambdaQueryWrapper<StoreProduct> lambdaQueryWrapper = new LambdaQueryWrapper<StoreProduct>();
        lambdaQueryWrapper.eq(StoreProduct::getIsGood,1);
        lambdaQueryWrapper.eq(StoreProduct::getIsShow,1);
        lambdaQueryWrapper.eq(StoreProduct::getIsDel,false);

        lambdaQueryWrapper.orderByDesc(StoreProduct::getSort).orderByDesc(StoreProduct::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     *
     * @param productId 商品id
     * @param type 类型：recycle——回收站 delete——彻底删除
     * @return
     */
    @Override
    public boolean deleteProduct(Integer productId, String type) {
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
            int delete = dao.delete(lambdaUpdateWrapper);
            return delete > 0;
        }
        lambdaUpdateWrapper.eq(StoreProduct::getId, productId);
        lambdaUpdateWrapper.set(StoreProduct::getIsDel, true);
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
    public boolean reStoreProduct(Integer productId) {
        LambdaUpdateWrapper<StoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(StoreProduct::getId, productId);
        lambdaUpdateWrapper.set(StoreProduct::getIsDel, false);
        return update(lambdaUpdateWrapper);
    }

    ///////////////////////////////////////////自定义方法

    /**
     * 扣减库存任务操作
     * @param storeProductStockRequest 扣减库存参数
     * @return 执行结果
     */
    @Override
    public boolean doProductStock(StoreProductStockRequest storeProductStockRequest){
        // 获取商品本身信息
        StoreProduct existProduct = getById(storeProductStockRequest.getProductId());
        List<StoreProductAttrValue> existAttr =
                storeProductAttrValueService.getListByProductIdAndAttrId(
                        storeProductStockRequest.getProductId(),
                        storeProductStockRequest.getAttrId().toString(),
                        storeProductStockRequest.getType());
        if(null == existProduct || null == existAttr){ // 未找到商品
            logger.info("库存修改任务未获取到商品信息"+JSON.toJSONString(storeProductStockRequest));
            return true;
        }

        // 回滚商品库存/销量 并更新
        boolean isPlus = storeProductStockRequest.getOperationType().equals("add");
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
        if (copyType.equals("1")) {// 一号通
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
        if (type.equals("add")) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
        }
        if (type.equals("sub")) {
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
        switch (type){
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
        PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        // id、名称、图片、价格、销量、活动
        lqw.select(StoreProduct::getId, StoreProduct::getStoreName, StoreProduct::getImage, StoreProduct::getPrice,
                StoreProduct::getActivity, StoreProduct::getSales, StoreProduct::getFicti, StoreProduct::getUnitName);

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

        if(StrUtil.isNotBlank(request.getKeyword())){
            if(CrmebUtil.isString2Num(request.getKeyword())){
                Integer productId = Integer.valueOf(request.getKeyword());
                lqw.like(StoreProduct::getId, productId);
            }else{
                lqw.like(StoreProduct::getStoreName, request.getKeyword());
            }
        }

        // 排序部分
        if (StrUtil.isNotBlank(request.getSalesOrder())) {
            if(request.getSalesOrder().equals(Constants.SORT_DESC)){
                lqw.last(" order by (sales + ficti) desc, sort desc, id desc");
            }else{
                lqw.last(" order by (sales + ficti) asc, sort asc, id asc");
            }
        } else {
            if(StrUtil.isNotBlank(request.getPriceOrder())){
                if(request.getPriceOrder().equals(Constants.SORT_DESC)){
                    lqw.orderByDesc(StoreProduct::getPrice);
                }else{
                    lqw.orderByAsc(StoreProduct::getPrice);
                }
            }

            lqw.orderByDesc(StoreProduct::getSort);
            lqw.orderByDesc(StoreProduct::getId);
        }
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
        if(ObjectUtil.isNotNull(sd)) {
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
     * 根据商品ids获取对应的列表
     * @param productIdList 商品id列表
     * @return List<StoreProduct>
     */
    @Override
    public List<StoreProduct> findH5ListByProIds(List<Integer> productIdList) {
        LambdaQueryWrapper<StoreProduct> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreProduct::getId, StoreProduct::getImage, StoreProduct::getStoreName, StoreProduct::getPrice);
        lqw.in(StoreProduct::getId, productIdList);
        return dao.selectList(lqw);
    }

}

