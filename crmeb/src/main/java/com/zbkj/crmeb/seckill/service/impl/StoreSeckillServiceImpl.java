package com.zbkj.crmeb.seckill.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.front.response.*;
import com.zbkj.crmeb.seckill.dao.StoreSeckillDao;
import com.zbkj.crmeb.seckill.model.StoreSeckill;
import com.zbkj.crmeb.seckill.model.StoreSeckillManger;
import com.zbkj.crmeb.seckill.request.StoreSeckillRequest;
import com.zbkj.crmeb.seckill.request.StoreSeckillSearchRequest;
import com.zbkj.crmeb.seckill.response.StoreSeckillDetailResponse;
import com.zbkj.crmeb.seckill.response.StoreSeckillManagerResponse;
import com.zbkj.crmeb.seckill.response.StoreSeckillResponse;
import com.zbkj.crmeb.seckill.service.StoreSeckillMangerService;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.store.model.*;
import com.zbkj.crmeb.store.request.StoreProductAttrValueRequest;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.response.StoreProductAttrValueResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.ProductUtils;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.task.order.OrderRefundTask;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StoreSeckillService 实现类
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
public class StoreSeckillServiceImpl extends ServiceImpl<StoreSeckillDao, StoreSeckill>
        implements StoreSeckillService {

    @Resource
    private StoreSeckillDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private StoreProductDescriptionService storeProductDescriptionService;

    @Autowired
    private StoreSeckillMangerService storeSeckillMangerService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreProductAttrService attrService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private StoreProductAttrResultService storeProductAttrResultService;

    @Autowired
    private ProductUtils productUtils;

    @Autowired
    private RedisUtil redisUtil;

    private static final Logger logger = LoggerFactory.getLogger(OrderRefundTask.class);

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return List<StoreSeckill>
    */
    @Override
    public PageInfo<StoreSeckillResponse> getList(StoreSeckillSearchRequest request, PageParamRequest pageParamRequest) {
        //带 StoreSeckill 类的多条件查询
        Page<StoreSeckill> storeSeckillProductPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<StoreSeckill> lambdaQueryWrapper = Wrappers.lambdaQuery();
        StoreSeckill model = new StoreSeckill();
        BeanUtils.copyProperties(request, model);
        if(null != request.getStatus()){
            lambdaQueryWrapper.eq(StoreSeckill::getStatus,request.getStatus());
        }
        if(StringUtils.isNotBlank(request.getKeywords())){
            lambdaQueryWrapper.like(StoreSeckill::getTitle,request.getKeywords())
                    .or().like(StoreSeckill::getId,request.getKeywords());
        }
        if(null != request.getTimeId()){
            lambdaQueryWrapper.eq(StoreSeckill::getTimeId,request.getTimeId());
        }
        lambdaQueryWrapper.eq(StoreSeckill::getIsDel,false);
        lambdaQueryWrapper.orderByDesc(StoreSeckill::getSort).orderByDesc(StoreSeckill::getId);
        List<StoreSeckill> storeProducts = dao.selectList(lambdaQueryWrapper);
        List<StoreSeckillResponse> storeProductResponses = new ArrayList<>();

        // 当前正在秒杀的timeId 正确的数据这里应该只会获得一条数据
        List<StoreSeckillManger> currentSeckillManager = storeSeckillMangerService.getCurrentSeckillManager();
        Integer currentSkillTimeId = 0;
//        String currentSkillTime = null;
        if(null != currentSeckillManager && currentSeckillManager.size() > 0){
            currentSkillTimeId = currentSeckillManager.get(0).getId();
        }

        // 查询所有秒杀配置后根据关系添加到秒杀商品列表中
        List<StoreSeckillManagerResponse> storeSeckillMangerServiceList =
                storeSeckillMangerService.getList(new StoreSeckillManger(), new PageParamRequest());
        for (StoreSeckill product : storeProducts) {
            StoreSeckillResponse storeProductResponse = new StoreSeckillResponse();
            BeanUtils.copyProperties(product, storeProductResponse);
            storeProductResponse.setImages(CrmebUtil.stringToArrayStr(product.getImages()));

            StoreProductAttr storeProductAttrPram = new StoreProductAttr();
            storeProductAttrPram.setProductId(product.getId()).setType(Constants.PRODUCT_TYPE_SECKILL);
            List<StoreProductAttr> attrs = attrService.getByEntity(storeProductAttrPram);

            if(attrs.size() > 0){
                storeProductResponse.setAttr(attrs);
            }
            // 处理富文本
            StoreProductDescription sd = storeProductDescriptionService.getOne(
                    new LambdaQueryWrapper<StoreProductDescription>()
                            .eq(StoreProductDescription::getProductId, product.getId())
                                .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_SECKILL));
            if(null != sd){
                storeProductResponse.setContent(null == sd.getDescription()?"":sd.getDescription());
            }
            // 添加秒杀配置关系
            List<StoreSeckillManagerResponse> hasTimeIds = storeSeckillMangerServiceList.stream()
                    .filter(e -> e.getId().equals(storeProductResponse.getTimeId())).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(hasTimeIds)) {
                storeProductResponse.setStoreSeckillManagerResponse(hasTimeIds.get(0));
                storeProductResponse.setCurrentTimeId(currentSkillTimeId);
                storeProductResponse.setCurrentTime(hasTimeIds.get(0).getTime());
            }
            storeProductResponses.add(storeProductResponse);
        }
        // 多条sql查询处理分页正确
        return CommonPage.copyPageInfo(storeSeckillProductPage, storeProductResponses);
    }

    /**
     * 逻辑删除
     *
     * @param id 秒杀id
     * @return 删除结果
     */
    @Override
    public Boolean deleteById(Integer id) {
        StoreSeckill skill = new StoreSeckill().setId(id).setIsDel(true);
        return dao.updateById(skill) > 0;
    }

    /**
     * 新增秒杀商品
     *
     * @param request 待新增秒杀商品
     * @return 新增结果
     */
    @Override
    public Boolean saveSeckill(StoreSeckillRequest request) {
        // 过滤掉checked=false的数据
        clearNotCheckedAndValidationPrice(request);

        // 当前商品是否已经存在秒杀中
        checkProductInSeamTime(request);
        StoreSeckill storeSeckill = new StoreSeckill();
        BeanUtils.copyProperties(request, storeSeckill);
        // 主图
        storeSeckill.setImage(systemAttachmentService.clearPrefix(storeSeckill.getImage()));
        // 轮播图
        storeSeckill.setImages(systemAttachmentService.clearPrefix(storeSeckill.getImages()));
        // 设置秒杀开始时间和结束时间
        storeSeckill.setStartTime(DateUtil.strToDate(request.getStartTime(), Constants.DATE_FORMAT_DATE));
        storeSeckill.setStopTime(DateUtil.strToDate(request.getStopTime(), Constants.DATE_FORMAT_DATE));

        //计算价格
        productUtils.calcPriceForAttrValuesSeckill(request, storeSeckill);
        //保存数据
        boolean save = save(storeSeckill);
        if(request.getSpecType()) { // 多属性
            if(null != request.getAttr() && request.getAttr().size() > 0){
                request.getAttr().forEach(e->{
                    e.setProductId(storeSeckill.getId());
                    e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"",""),"[]"));
                    e.setType(Constants.PRODUCT_TYPE_SECKILL);
                });
                boolean attrAddResult = attrService.saveBatch(request.getAttr());
                if (!attrAddResult) throw new CrmebException("新增属性名失败");
            }
        }else{ // 单属性
            StoreProductAttr singleAttr = new StoreProductAttr();
            singleAttr.setProductId(storeSeckill.getId()).setAttrName("规格").setAttrValues("默认").setType(Constants.PRODUCT_TYPE_SECKILL);
            boolean attrAddResult = attrService.save(singleAttr);
            if (!attrAddResult) throw new CrmebException("新增属性名失败");
            StoreProductAttrValue singleAttrValue = new StoreProductAttrValue();
            BigDecimal commissionL1= BigDecimal.ZERO;
            BigDecimal commissionL2= BigDecimal.ZERO;
            if(request.getAttrValue().size()>0){
                commissionL1 = null != request.getAttrValue().get(0).getBrokerage() ?
                        request.getAttrValue().get(0).getBrokerage():BigDecimal.ZERO;
                commissionL2 = null != request.getAttrValue().get(0).getBrokerageTwo() ?
                        request.getAttrValue().get(0).getBrokerageTwo():BigDecimal.ZERO;
            }

            singleAttrValue.setProductId(storeSeckill.getId()).setStock(storeSeckill.getStock()).setSuk("默认")
                    .setSales(storeSeckill.getSales()).setPrice(storeSeckill.getPrice())
                    .setImage(systemAttachmentService.clearPrefix(storeSeckill.getImage()))
                    .setCost(storeSeckill.getCost())
//                    .setBarCode(storeSeckill.getBarCode())
                    .setType(Constants.PRODUCT_TYPE_SECKILL)
                    .setOtPrice(storeSeckill.getOtPrice()).setBrokerage(commissionL1)
                    .setBrokerageTwo(commissionL2).setQuota(storeSeckill.getQuota())
                    .setQuotaShow(storeSeckill.getQuota());
            boolean saveOrUpdateResult = storeProductAttrValueService.save(singleAttrValue);
            if(!saveOrUpdateResult) throw new CrmebException("新增属性详情失败");
        }
        if (null != request.getAttrValue() && request.getAttrValue().size() > 0) {
            // 批量设置attrValues对象的商品id
            List<StoreProductAttrValueRequest> storeSeckillAttrValueRequests = request.getAttrValue();
            storeSeckillAttrValueRequests.forEach(e->{
                e.setProductId(storeSeckill.getId());
            });
            List<StoreProductAttrValue> storeProductAttrValues = new ArrayList<>();
            for (StoreProductAttrValueRequest attrValuesRequest : storeSeckillAttrValueRequests) {
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
                spav.setQuotaShow(spav.getQuota());
                spav.setType(Constants.PRODUCT_TYPE_SECKILL);
                storeProductAttrValues.add(spav);
            }
            // 保存属性
            if(storeProductAttrValues.size() > 0){
                boolean saveOrUpdateResult = storeProductAttrValueService.saveBatch(storeProductAttrValues);
                StoreProductAttrResult attrResult = new StoreProductAttrResult(
                        0,
                        storeSeckill.getId(),
                        systemAttachmentService.clearPrefix(JSON.toJSONString(request.getAttrValue())),
                        DateUtil.getNowTime(),Constants.PRODUCT_TYPE_SECKILL);
                storeProductAttrResultService.save(attrResult);
                if(!saveOrUpdateResult) throw new CrmebException("新增属性详情失败");
            }
        }
        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription(
                storeSeckill.getId(),  request.getContent().length() > 0
                ? systemAttachmentService.clearPrefix(request.getContent()):"",Constants.PRODUCT_TYPE_SECKILL);
        storeProductDescriptionService.deleteByProductId(spd.getProductId(),Constants.PRODUCT_TYPE_SECKILL);
        storeProductDescriptionService.save(spd);
        return save;
    }

    /**
     * 更新秒杀商品
     *
     * @param request 待更新秒杀商品
     * @return 更新结果
     */
    @Override
    public Boolean updateSeckill(StoreSeckillRequest request) {
        // 过滤掉checked=false的数据
        clearNotCheckedAndValidationPrice(request);

        // 当前商品是否已经存在秒杀中
        checkProductInSeamTime(request);

        StoreSeckill storeProduct = new StoreSeckill();
        BeanUtils.copyProperties(request, storeProduct);
        storeProduct.setStartTime(DateUtil.strToDate(request.getStartTime(),Constants.DATE_FORMAT_DATE));
        storeProduct.setStopTime(DateUtil.strToDate(request.getStopTime(),Constants.DATE_FORMAT_DATE));

        //主图
        storeProduct.setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()));

        //轮播图
        storeProduct.setImages(systemAttachmentService.clearPrefix(storeProduct.getImages()));

        productUtils.calcPriceForAttrValuesSeckill(request, storeProduct);
        int saveCount = dao.updateById(storeProduct);
        // 对attr表做全量更新，删除原有数据保存现有数据
        attrService.removeByProductId(request.getId(),Constants.PRODUCT_TYPE_SECKILL);
        storeProductAttrValueService.removeByProductId(request.getId(),Constants.PRODUCT_TYPE_SECKILL);
        request.getAttr().forEach(e->{
            e.setProductId(request.getId());
            e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"",""),"[]"));
            e.setType(Constants.PRODUCT_TYPE_SECKILL);
        });
        attrService.saveBatch(request.getAttr());
        if(null != request.getAttrValue() && request.getAttrValue().size() > 0){
            List<StoreProductAttrValueRequest> storeProductAttrValuesRequest = request.getAttrValue();
            // 批量设置attrValues对象的商品id
            storeProductAttrValuesRequest.forEach(e->e.setProductId(request.getId()));
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
                String attrValue = null;
                if(null != attrValuesRequest.getAttrValue() && attrValuesRequest.getAttrValue().size() > 0){
                    attrValue = JSON.toJSONString(attrValuesRequest.getAttrValue());
                }
                spav.setAttrValue(attrValue);
                spav.setImage(systemAttachmentService.clearPrefix(spav.getImage()));
                spav.setType(Constants.PRODUCT_TYPE_SECKILL);
                spav.setQuotaShow(spav.getQuota());
                storeProductAttrValues.add(spav);
            }
            boolean saveOrUpdateResult = storeProductAttrValueService.saveBatch(storeProductAttrValues);
            // attrResult整存整取，不做更新
            storeProductAttrResultService.deleteByProductId(storeProduct.getId(),Constants.PRODUCT_TYPE_SECKILL);
            StoreProductAttrResult attrResult = new StoreProductAttrResult(
                    0,
                    storeProduct.getId(),
                    systemAttachmentService.clearPrefix(JSON.toJSONString(request.getAttrValue())),
                    DateUtil.getNowTime(),Constants.PRODUCT_TYPE_SECKILL);
            storeProductAttrResultService.save(attrResult);
            if(!saveOrUpdateResult) throw new CrmebException("编辑属性详情失败");

        }

        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription(
                storeProduct.getId(),
                request.getContent().length() > 0
                        ? systemAttachmentService.clearPrefix(request.getContent()):"",
                Constants.PRODUCT_TYPE_SECKILL);
        storeProductDescriptionService.deleteByProductId(storeProduct.getId(),Constants.PRODUCT_TYPE_SECKILL);
        storeProductDescriptionService.save(spd);

        return saveCount > 0;
    }

    /**
     * 更新秒杀状态
     *
     * @param secKillId 秒杀id
     * @param status    秒杀状态
     * @return 更新结果
     */
    @Override
    public Boolean updateSecKillStatus(int secKillId, boolean status) {
        StoreSeckill seckill = getById(secKillId);
        if (ObjectUtil.isNull(seckill) || seckill.getIsDel()) {
            throw new CrmebException("秒杀商品不存在");
        }
        if (status) {
            // 判断商品是否存在
            StoreProduct product = storeProductService.getById(seckill.getProductId());
            if (ObjectUtil.isNull(product)) {
                throw new CrmebException("关联的商品已删除，无法开启活动");
            }
        }

        StoreSeckill storeSeckill = new StoreSeckill().setId(secKillId).setStatus(status?1:0);
        return dao.updateById(storeSeckill) > 0;
    }

    /**
     * 秒杀商品详情
     *
     * @param skillId 秒杀商品id
     * @return 详情
     */
    @Override
    public StoreSeckillDetailResponse getDetailH5(Integer skillId) {
        StoreSeckillDetailResponse productDetailResponse = new StoreSeckillDetailResponse();

        // 获取秒杀商品信息
        StoreSeckill storeSeckill = dao.selectById(skillId);
        if (ObjectUtil.isNull(storeSeckill) || storeSeckill.getIsDel()) {
            throw new CrmebException("未找到对应秒杀商品信息");
        }
        if (storeSeckill.getStatus().equals(0)) {
            throw new CrmebException("秒杀商品已下架");
        }

        SecKillDetailH5Response detailH5Response = new SecKillDetailH5Response();
        BeanUtils.copyProperties(storeSeckill, detailH5Response);
        detailH5Response.setStoreName(storeSeckill.getTitle());
        detailH5Response.setSliderImage(storeSeckill.getImages());
        detailH5Response.setStoreInfo(storeSeckill.getInfo());
        Integer seckillStatus = getSeckillStatus(storeSeckill);
        detailH5Response.setSeckillStatus(seckillStatus);
        // 详情
        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, skillId)
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_SECKILL));
        if (ObjectUtil.isNotNull(sd)) {
            detailH5Response.setContent(ObjectUtil.isNull(sd.getDescription()) ? "" : sd.getDescription());
        }
        // 获取主商品信息
        StoreProduct storeProduct = storeProductService.getById(storeSeckill.getProductId());
        // 秒杀销量 = 原商品销量（包含虚拟销量）
        detailH5Response.setSales(storeProduct.getSales());
        detailH5Response.setFicti(storeProduct.getFicti());
        productDetailResponse.setStoreSeckill(detailH5Response);

        // 获取秒杀商品规格
        StoreProductAttr spaPram = new StoreProductAttr();
        spaPram.setProductId(skillId).setType(Constants.PRODUCT_TYPE_SECKILL);
        List<StoreProductAttr> attrList = attrService.getByEntity(spaPram);
        // 根据制式设置attr属性
        List<ProductAttrResponse> skuAttr = getSkuAttr(attrList);
        productDetailResponse.setProductAttr(skuAttr);

        // 根据制式设置sku属性
        HashMap<String, Object> skuMap = CollUtil.newHashMap();
        // 获取主商品sku
        StoreProductAttrValue spavPram = new StoreProductAttrValue();
        spavPram.setProductId(storeSeckill.getProductId()).setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getByEntity(spavPram);
        // 获取秒杀商品sku
        StoreProductAttrValue spavPram1 = new StoreProductAttrValue();
        spavPram1.setProductId(storeSeckill.getId()).setType(Constants.PRODUCT_TYPE_SECKILL);
        List<StoreProductAttrValue> seckillAttrValues = storeProductAttrValueService.getByEntity(spavPram1);

        for (int i = 0; i < storeProductAttrValues.size(); i++) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            StoreProductAttrValue productAttrValue = storeProductAttrValues.get(i);
            List<StoreProductAttrValue> valueList = seckillAttrValues.stream().filter(e -> {
                return productAttrValue.getSuk().equals(e.getSuk());
            }).collect(Collectors.toList());
            if (CollUtil.isEmpty(valueList)) {
                BeanUtils.copyProperties(productAttrValue, atr);
            } else {
                BeanUtils.copyProperties(valueList.get(0), atr);
            }
            skuMap.put(atr.getSuk(), atr);
        }
        productDetailResponse.setProductValue(skuMap);

        // 设置点赞和收藏
        User user = userService.getInfo();
        if(ObjectUtil.isNotNull(user)){
            productDetailResponse.setUserCollect(storeProductRelationService.getLikeOrCollectByUser(user.getUid(), detailH5Response.getProductId(),false).size() > 0);
        }else{
            productDetailResponse.setUserCollect(false);
        }

        return productDetailResponse;
    }

    /**
     * 获取秒杀规格（公共转换）
     * @param attrList 秒杀规格列表
     * @return List<ProductAttrResponse>
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

    /**
     * 获取秒杀状态
     * @param storeSeckill 秒杀商品
     * @return 秒杀状态
     */
    private Integer getSeckillStatus(StoreSeckill storeSeckill) {
        if(storeSeckill.getStatus() == 1 && DateUtil.nowDateTime().compareTo(storeSeckill.getStartTime()) < 0){
            // 即将开始
            return 1;
        }
        else if(storeSeckill.getStatus() == 0) {
            // 关闭
            return 0;
        }
        else if(storeSeckill.getStatus() == 1 && DateUtil.nowDateTime().compareTo(storeSeckill.getStartTime())>0
                && DateUtil.nowDateTime().compareTo(storeSeckill.getStopTime()) < 0) {
            // 进行中
            return 2;
        }
        else if(storeSeckill.getStatus() == 1 && DateUtil.nowDateTime().compareTo(storeSeckill.getStopTime()) >= 0){
            // 已结束
            return -1;
        }
        return -2;
    }

    /**
     * 秒杀商品详情 管理端
     *
     * @param skillId 秒杀id
     * @return 详情数据
     */
    @Override
    public StoreProductResponse getDetailAdmin(int skillId) {
        StoreSeckill storeProduct = dao.selectById(skillId);
        if(null == storeProduct) throw new CrmebException("未找到对应商品信息");
        StoreProductResponse storeProductResponse = new StoreProductResponse();
        BeanUtils.copyProperties(storeProduct, storeProductResponse);

        // 查询attr
        StoreProductAttr spaPram = new StoreProductAttr();
        spaPram.setProductId(skillId).setType(Constants.PRODUCT_TYPE_SECKILL);
        storeProductResponse.setAttr(attrService.getByEntity(spaPram));
        storeProductResponse.setSliderImage(String.join(",",storeProduct.getImages()));

        // 注意：数据瓶装步骤：分别查询秒杀和商品本山信息组装sku信息之后，再对比sku属性是否相等来赋值是否秒杀sku信息
        StoreProductAttrValue spavPramSkill = new StoreProductAttrValue();
        spavPramSkill.setProductId(skillId).setType(Constants.PRODUCT_TYPE_SECKILL);
        List<StoreProductAttrValue> storeProductAttrValuesSkill = storeProductAttrValueService.getByEntity(spavPramSkill);
        List<HashMap<String, Object>> attrValuesSkill = genratorSkuInfo(skillId,storeProduct, storeProductAttrValuesSkill, Constants.PRODUCT_TYPE_SECKILL);

        // 查询attrValue
        StoreProductAttrValue spavPramProduct = new StoreProductAttrValue();
        spavPramProduct.setProductId(storeProduct.getProductId()).setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> storeProductAttrValuesProduct = storeProductAttrValueService.getByEntity(spavPramProduct);
        List<HashMap<String, Object>> attrValuesProduct = genratorSkuInfo(storeProduct.getProductId(),storeProduct, storeProductAttrValuesProduct, Constants.PRODUCT_TYPE_NORMAL);

        // H5 端用于生成skuList
        List<StoreProductAttrValueResponse> sPAVResponses = new ArrayList<>();

        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValuesSkill) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue,atr);
            // 单规格秒杀限量数据处理
            atr.setQuota(storeProductResponse.getQuota());
            sPAVResponses.add(atr);
        }

        for (int k = 0; k < attrValuesProduct.size(); k++) {
            for (int i = 0; i < attrValuesSkill.size(); i++) {
                HashMap<String, Object> skill = attrValuesSkill.get(i);
                HashMap<String, Object> product = attrValuesProduct.get(k);
                product.put("checked", false);
                product.put("quota", product.get("stock"));
                product.put("price", product.get("price"));
                if(skill.get("suk").equals(product.get("suk"))){
                    product.put("checked", true);
                    product.put("quota", skill.get("quota"));
                    product.put("price",skill.get("price"));
                    break;
                }
            }
        }

        storeProductResponse.setAttrValues(attrValuesProduct);
        storeProductResponse.setAttrValue(sPAVResponses);
        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, skillId)
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_SECKILL));
        if(null != sd){
            storeProductResponse.setContent(null == sd.getDescription()?"":sd.getDescription());
        }
        return storeProductResponse;
    }

    /**
     * 移动端 获取秒杀配置
     * 获取当前时间段 + 下边的6个商品
     * @return 秒杀配置
     */
    @Override
    public List<SecKillResponse> getForH5Index() {
        List<SecKillResponse> response = new ArrayList<>();
        int currentHour = DateUtil.getCurrentHour();
        // 获取所有的秒杀配置
        List<StoreSeckillManagerResponse> skillManagerList = storeSeckillMangerService.getH5List();
        // 根据当前时间过滤 仅处理正在进行和马上开始的秒杀
        skillManagerList.forEach(e->{
            // 根据当前秒杀配置id查询是否有商品正在参与次时间段
            Integer proNum = getCountByTimeId(e.getId());
            if (proNum > 0) {
                int secKillEndSecondTimestamp = DateUtil.getSecondTimestamp(DateUtil.nowDateTime("yyyy-MM-dd " + e.getEndTime() + ":00:00"));
                SecKillResponse r = new SecKillResponse(e.getId(),e.getSilderImgs(),e.getStatusName(),
                        e.getTime(),e.getKillStatus(),secKillEndSecondTimestamp+"");
                if(e.getStartTime() <= currentHour && currentHour < e.getEndTime()){
                    r.setIsCheck(true);
                }
                response.add(r);
            }
        });

        return response;
    }

    /**
     * 获取秒杀时段商品数量
     * @param timeId 秒杀时段id
     * @return Integer
     */
    private Integer getCountByTimeId(Integer timeId) {
        LambdaQueryWrapper<StoreSeckill> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreSeckill::getStatus,1);
        lqw.eq(StoreSeckill::getIsDel,false);
        lqw.eq(StoreSeckill::getIsShow,true);
        lqw.eq(StoreSeckill::getTimeId,timeId);
        String currentDate = DateUtil.nowDate(Constants.DATE_FORMAT_DATE);
        lqw.le(StoreSeckill::getStartTime, currentDate);
        lqw.ge(StoreSeckill::getStopTime, currentDate);
        lqw.orderByDesc(StoreSeckill::getId);
        return dao.selectCount(lqw);
    }

    /**
     * 根据秒杀时间段查询已配置的秒杀商品
     *
     * @param timeId 秒杀id
     * @return 秒杀中的商品
     */
    @Override
    public List<StoreSecKillH5Response> getKillListByTimeId(String timeId, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        String currentDate = DateUtil.nowDate(Constants.DATE_FORMAT_DATE);
        LambdaQueryWrapper<StoreSeckill> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreSeckill::getStatus,1);
        lqw.eq(StoreSeckill::getIsDel,false);
        lqw.eq(StoreSeckill::getIsShow,true);
        lqw.eq(StoreSeckill::getTimeId,timeId);
        lqw.le(StoreSeckill::getStartTime, currentDate);
        lqw.ge(StoreSeckill::getStopTime,currentDate);
        lqw.orderByDesc(StoreSeckill::getId);
        List<StoreSeckill> storeSeckills = dao.selectList(lqw);
        if (CollUtil.isEmpty(storeSeckills)) {
            return CollUtil.newArrayList();
        }
        List<StoreSecKillH5Response> responses = new ArrayList<>();
        storeSeckills.forEach(e->{
            StoreSecKillH5Response response = new StoreSecKillH5Response();
            BeanUtils.copyProperties(e, response);
            response.setPercent(CrmebUtil.percentInstanceIntVal(e.getQuotaShow() - e.getQuota(), e.getQuotaShow()));
            responses.add(response);
        });
        return responses;
    }

    /**
     * 秒杀基础查询
     *
     * @param storeSeckill 秒杀基本参数
     * @return 查询到的秒杀商品
     */
    @Override
    public List<StoreSeckill> getByEntity(StoreSeckill storeSeckill) {
        LambdaQueryWrapper<StoreSeckill> lqw = Wrappers.lambdaQuery();
        lqw.setEntity(storeSeckill);
        return dao.selectList(lqw);
    }

    /**
     * 根据商品id查询正在秒杀的商品信息
     *
     * @param productId 商品id
     * @return 正在参与的秒杀信息
     */
    @Override
    public List<StoreSeckill> getCurrentSecKillByProductId(Integer productId) {
        List<StoreSeckill> result = new ArrayList<>();
        // 获取当前时间段的秒杀
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(20);
        List<StoreSeckillManagerResponse> storeSeckillManagerResponses =
                storeSeckillMangerService.getList(new StoreSeckillManger(), pageParamRequest);
        List<StoreSeckillManagerResponse> currentSsmr =
                storeSeckillManagerResponses.stream().filter(e -> e.getKillStatus() == 2).collect(Collectors.toList());
        if(currentSsmr.size() == 0){
            return result;
        }
        List<Integer> skillManagerIds = currentSsmr.stream().map(StoreSeckillManagerResponse::getId).collect(Collectors.toList());
        // 获取正在秒杀的商品信息
        LambdaQueryWrapper<StoreSeckill> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreSeckill::getProductId,productId);
        lqw.eq(StoreSeckill::getIsDel,false);
        lqw.in(StoreSeckill::getTimeId, skillManagerIds);
        result = dao.selectList(lqw);
        return result;
    }


    /**
     * 库存变动写入redis队列
     * @param request StoreProductStockRequest 参数对象
     * @return Boolean
     */
    @Override
    public Boolean stockAddRedis(StoreProductStockRequest request) {
        String _productString = JSON.toJSONString(request);
        redisUtil.lPush(Constants.PRODUCT_SECKILL_STOCK_UPDATE, _productString);
        return true;
    }

    /**
     * 后台任务批量操作库存
     */
    @Override
    public void consumeProductStock() {
        String redisKey = Constants.PRODUCT_SECKILL_STOCK_UPDATE;
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
     * 商品是否存在秒杀活动
     * @param productId 商品编号
     * @return Boolean
     */
    @Override
    public Boolean isExistActivity(Integer productId) {
        LambdaQueryWrapper<StoreSeckill> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreSeckill::getProductId, productId);
        lqw.eq(StoreSeckill::getIsDel, false);
        List<StoreSeckill> seckillList = dao.selectList(lqw);
        if (CollUtil.isEmpty(seckillList)) {
            return false;
        }
        // 判断关联的商品是否处于活动开启状态
        List<StoreSeckill> list = seckillList.stream().filter(i -> i.getStatus().equals(1)).collect(Collectors.toList());
        return CollUtil.isNotEmpty(list);
    }

    /**
     * 查询带异常
     * @param id 秒杀商品id
     * @return StoreSeckill
     */
    @Override
    public StoreSeckill getByIdException(Integer id) {
        LambdaQueryWrapper<StoreSeckill> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreSeckill::getId, id);
        lqw.eq(StoreSeckill::getIsDel, false);
        lqw.eq(StoreSeckill::getIsShow, true);
        StoreSeckill storeSeckill = dao.selectOne(lqw);
        if (ObjectUtil.isNull(storeSeckill)) throw new CrmebException("秒杀商品不存在或以删除");
        return storeSeckill;
    }

    /**
     * 添加(退货)/扣减库存
     * @param id 秒杀商品id
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     * @return Boolean
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String type) {
        UpdateWrapper<StoreSeckill> updateWrapper = new UpdateWrapper<>();
        if (type.equals("add")) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
            updateWrapper.setSql(StrUtil.format("quota = quota + {}", num));
        }
        if (type.equals("sub")) {
            updateWrapper.setSql(StrUtil.format("stock = stock - {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales + {}", num));
            updateWrapper.setSql(StrUtil.format("quota = quota - {}", num));
            // 扣减时加乐观锁保证库存不为负
            updateWrapper.last(StrUtil.format(" and (quota - {} >= 0)", num));
        }
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新商品库存失败！商品id = " + id);
        }
        return update;
    }

    /**
     * 获取秒杀首页信息
     * 当前时段秒杀信息 + 当前时段秒杀商品6条
     * @return SeckillIndexResponse
     */
    @Override
    public SeckillIndexResponse getIndexInfo() {
        StoreSeckillManger storeSeckillManger = new StoreSeckillManger();
        storeSeckillManger.setIsDel(false);
        // 根据当前时间过滤 仅处理正在进行的秒杀
        List<StoreSeckillManger> currentSeckillManagerList = storeSeckillMangerService.getCurrentSeckillManager();
        if (CollUtil.isEmpty(currentSeckillManagerList)) {
            return null;
        }
        StoreSeckillManger seckillManger = currentSeckillManagerList.get(0);

        // 查询当前时段秒杀商品
        String currentDate = DateUtil.nowDate(Constants.DATE_FORMAT_DATE);
        LambdaQueryWrapper<StoreSeckill> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreSeckill::getId, StoreSeckill::getProductId, StoreSeckill::getImage, StoreSeckill::getTitle, StoreSeckill::getPrice, StoreSeckill::getOtPrice);
        lqw.eq(StoreSeckill::getStatus,1);
        lqw.eq(StoreSeckill::getIsDel,false);
        lqw.eq(StoreSeckill::getIsShow,true);
        lqw.eq(StoreSeckill::getTimeId, seckillManger.getId());
        lqw.le(StoreSeckill::getStartTime, currentDate);
        lqw.ge(StoreSeckill::getStopTime,currentDate);
        lqw.orderByDesc(StoreSeckill::getId);
        lqw.last(" limit 6");
        List<StoreSeckill> seckillList = dao.selectList(lqw);
        if (CollUtil.isEmpty(seckillList)) {
            // 如果没有秒杀商品也不展示
            return null;
        }

        SeckillIndexResponse response = new SeckillIndexResponse();
        // 处理秒杀时段信息
        StoreSeckillManagerResponse managerResponse = new StoreSeckillManagerResponse();
        BeanUtils.copyProperties(seckillManger, managerResponse);
        String pStartTime = seckillManger.getStartTime().toString();
        String pEndTime = seckillManger.getEndTime().toString();
        String startTime = pStartTime.length() == 1? "0"+pStartTime:pStartTime;
        String endTime = pEndTime.length() == 1? "0"+pEndTime:pEndTime;
        managerResponse.setTime(startTime+":00,"+endTime+":00");
        int secKillEndSecondTimestamp = DateUtil.getSecondTimestamp(DateUtil.nowDateTime("yyyy-MM-dd " + seckillManger.getEndTime() + ":00:00"));
        SecKillResponse secKillResponse = new SecKillResponse(seckillManger.getId(),seckillManger.getSilderImgs(),managerResponse.getStatusName(),
                managerResponse.getTime(),managerResponse.getKillStatus(),secKillEndSecondTimestamp+"");
        response.setSecKillResponse(secKillResponse);
        response.setProductList(seckillList);
        return response;
    }
    ///////////////////////////////////////////////////////////////////  自定义方法

    // 秒杀操作库存
    private boolean doProductStock(StoreProductStockRequest storeProductStockRequest){
        // 秒杀商品信息回滚
        StoreSeckill existProduct = getById(storeProductStockRequest.getSeckillId());
        List<StoreProductAttrValue> existAttr =
                storeProductAttrValueService.getListByProductIdAndAttrId(
                        storeProductStockRequest.getSeckillId(),
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
        existProduct.setQuota(existProduct.getQuota() + storeProductStockRequest.getNum());
        updateById(existProduct);

        // 回滚sku库存
        for (StoreProductAttrValue attrValue : existAttr) {
            int productAttrStock = isPlus ? attrValue.getStock() + storeProductStockRequest.getNum() : attrValue.getStock() - storeProductStockRequest.getNum();
            attrValue.setStock(productAttrStock);
            attrValue.setSales(attrValue.getSales()-storeProductStockRequest.getNum());
            attrValue.setQuota(attrValue.getQuota() + storeProductStockRequest.getNum());
            storeProductAttrValueService.updateById(attrValue);
        }

        // 商品本身库存回滚
        // StoreProductStockRequest 创建次对象调用商品扣减库存实现扣减上本本身库存
        StoreProductResponse existProductLinkedSeckill = storeProductService.getByProductId(storeProductStockRequest.getProductId());
        for (StoreProductAttrValueResponse attrValueResponse : existProductLinkedSeckill.getAttrValue()) {
            if(attrValueResponse.getSuk().equals(storeProductStockRequest.getSuk())){
                StoreProductStockRequest r = new StoreProductStockRequest()
                        .setAttrId(attrValueResponse.getId())
                        .setNum(storeProductStockRequest.getNum())
                        .setOperationType("add")
                        .setProductId(storeProductStockRequest.getProductId())
                        .setType(Constants.PRODUCT_TYPE_NORMAL)
                        .setSuk(storeProductStockRequest.getSuk());
                storeProductService.doProductStock(r);
            }
        }

        return true;
    }

    /**
     * 检查是否有商品存在同一时段的秒杀
     * @param request   待操作数据
     */
    private void checkProductInSeamTime(StoreSeckillRequest request){
        // 查询当前时间段和选择的秒杀时间段是否存在此商品
        LambdaQueryWrapper<StoreSeckill> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreSeckill::getTimeId,request.getTimeId())
                .eq(StoreSeckill::getProductId,request.getProductId())
                .eq(StoreSeckill::getIsDel,false)
                .eq(StoreSeckill::getTimeId, request.getTimeId());
        List<StoreSeckill> seckills = dao.selectList(lqw);
        if(null != seckills && seckills.size() == 1 && null != request.getId() && request.getId().equals(seckills.get(0).getId())){ // 排除更新秒杀操作
            return;
        }
        if(null != seckills && seckills.size() >= 1) throw new CrmebException("当前商品已经存在"+seckills.get(0).getTitle()+"的秒杀中");
    }

    /**
     * 根据配置生成sku配置信息
     * @param productId     商品id
     * @param storeProduct  秒杀商品信息
     * @param storeProductAttrValues    属性信息
     * @param productType   秒杀和正常数据
     * @return  sku信息
     */
    private  List<HashMap<String, Object>> genratorSkuInfo(int productId,StoreSeckill storeProduct,
                                                           List<StoreProductAttrValue> storeProductAttrValues,
                                                           int productType) {
        List<HashMap<String, Object>> attrValues = new ArrayList<>();
        if(storeProduct.getSpecType()){

            StoreProductAttrResult sparPram = new StoreProductAttrResult();
            sparPram.setProductId(productId).setType(productType);
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
                    attrValue.put("attrValue", JSON.parse(storeProductAttrValues.get(i).getAttrValue(), Feature.OrderedField));
                    attrValue.put("brokerage", currentAttrValue.getBrokerage());
                    attrValue.put("brokerageTwo", currentAttrValue.getBrokerageTwo());
                    attrValue.put("quota", currentAttrValue.getQuota());
                    String[] skus = currentSku.split(",");
                    for (int k = 0; k < skus.length; k++) {
                        attrValue.put("value"+k,skus[k]);
                    }
                    attrValues.add(attrValue);
                }

            }
        }
        return attrValues;
    }

    // 过滤AttrValue数据中checked=false的数据
    private void clearNotCheckedAndValidationPrice(StoreSeckillRequest request){
        if(request.getSpecType()){
            request.setAttrValue(request.getAttrValue().stream().filter(StoreProductAttrValueRequest::getChecked).collect(Collectors.toList()));
        }
        for (StoreProductAttrValueRequest attr : request.getAttrValue()) {
            if((null == attr.getPrice() || attr.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            || (null == attr.getQuota() || attr.getQuota() <= 0)){
                throw new CrmebException("请正确输入 秒杀价格和限量");
            }
        }
    }
}

