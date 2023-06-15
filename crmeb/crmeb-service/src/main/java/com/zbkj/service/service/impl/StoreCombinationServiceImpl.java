package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.model.product.StoreProductAttr;
import com.zbkj.common.model.product.StoreProductAttrValue;
import com.zbkj.common.model.product.StoreProductDescription;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.*;
import com.zbkj.common.response.*;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.model.combination.StoreCombination;
import com.zbkj.common.model.combination.StorePink;
import com.zbkj.common.model.record.UserVisitRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.service.dao.StoreCombinationDao;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StoreCombinationService 实现类
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
public class StoreCombinationServiceImpl extends ServiceImpl<StoreCombinationDao, StoreCombination> implements StoreCombinationService {

    @Resource
    private StoreCombinationDao dao;

    @Autowired
    private StorePinkService storePinkService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private StoreProductAttrService storeProductAttrService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private StoreProductAttrResultService storeProductAttrResultService;

    @Autowired
    private StoreProductDescriptionService storeProductDescriptionService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreOrderService storeOrderService;

    private OrderService orderService;

    @Autowired
    public void setOrderService(ApplicationContext applicationContext) {
        this.orderService = applicationContext.getBean(OrderService.class);
    }

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserVisitRecordService userVisitRecordService;

    private static final Logger logger = LoggerFactory.getLogger(StoreCombinationServiceImpl.class);

    /**
     * 分页显示拼团商品表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<StoreCombination>
     */
    @Override
    public PageInfo<StoreCombinationResponse> getList(StoreCombinationSearchRequest request, PageParamRequest pageParamRequest) {
        Page<StoreCombination> combinationPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreCombination> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCombination::getIsDel, false);
        if (StrUtil.isNotEmpty(request.getKeywords())) {
            lambdaQueryWrapper.and(i -> i.like(StoreCombination::getProductId, request.getKeywords())
                    .or().like(StoreCombination::getId, request.getKeywords())
                    .or().like(StoreCombination::getTitle, request.getKeywords()));
        }
        if (ObjectUtil.isNotNull(request.getIsShow())) {
            lambdaQueryWrapper.eq(StoreCombination::getIsShow, request.getIsShow() == 1);
        }
        lambdaQueryWrapper.orderByDesc(StoreCombination::getSort, StoreCombination::getId);
        List<StoreCombination> storeCombinationList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(storeCombinationList)) {
            return CommonPage.copyPageInfo(combinationPage, CollUtil.newArrayList());
        }

        List<StoreCombinationResponse> responseList = storeCombinationList.stream().map(combination -> {
            //原价 拼团人数 参与人数 成团数量 限量剩余
            StoreCombinationResponse combinationResponse = new StoreCombinationResponse();
            BeanUtils.copyProperties(combination, combinationResponse);
            combinationResponse.setRemainingQuota(combination.getQuota());
            List<StorePink> pinkList = storePinkService.getListByCid(combination.getId());
            combinationResponse.setCountPeople(0);
            combinationResponse.setCountPeopleAll(0);
            combinationResponse.setCountPeoplePink(0);
            if (CollUtil.isNotEmpty(pinkList)) {
                List<StorePink> teamList = pinkList.stream().filter(i -> i.getKId() == 0).collect(Collectors.toList());
                List<StorePink> successTeam = teamList.stream().filter(i -> i.getStatus() == 2).collect(Collectors.toList());
                combinationResponse.setCountPeople(teamList.size());
                combinationResponse.setCountPeopleAll(pinkList.size());
                combinationResponse.setCountPeoplePink(successTeam.size());
            }
            combinationResponse.setStopTimeStr(DateUtil.timestamp2DateStr(combination.getStopTime(), Constants.DATE_FORMAT_DATE));
            return combinationResponse;
        }).collect(Collectors.toList());

        return CommonPage.copyPageInfo(combinationPage, responseList);
    }

    /**
     * 新增拼团商品
     *
     * @param request 新增请求参数
     * @return Boolean
     */
    @Override
    public Boolean saveCombination(StoreCombinationRequest request) {
        if (!request.getSpecType()) {
            if (request.getAttrValue().size() > 1) {
                throw new CrmebException("单规格商品属性值不能大于1");
            }
        }
        // 过滤掉checked=false的数据
//        clearNotCheckedAndValidationPrice(request);

        StoreCombination storeCombination = new StoreCombination();
        BeanUtils.copyProperties(request, storeCombination);
        // 校验结束时间
        Long stopTime = DateUtil.dateStr2Timestamp(request.getStopTime(), Constants.DATE_TIME_TYPE_END);
        if (stopTime <= System.currentTimeMillis()) {
            throw new CrmebException("活动结束时间不能小于当前时间");
        }

        storeCombination.setId(null);
        // 头图、轮播图
        storeCombination.setImage(systemAttachmentService.clearPrefix(request.getImage()));
        storeCombination.setImages(systemAttachmentService.clearPrefix(request.getImages()));
        // 活动开始结束时间
        storeCombination.setStartTime(DateUtil.dateStr2Timestamp(request.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN));
        storeCombination.setStopTime(stopTime);
        storeCombination.setAddTime(System.currentTimeMillis());
        storeCombination.setSales(0);

        List<StoreProductAttrValueAddRequest> attrValueAddRequestList = request.getAttrValue();
        // 计算价格
        StoreProductAttrValueAddRequest minAttrValue = attrValueAddRequestList.stream().min(Comparator.comparing(StoreProductAttrValueAddRequest::getPrice)).get();
        storeCombination.setPrice(minAttrValue.getPrice());
        storeCombination.setOtPrice(minAttrValue.getOtPrice());
        storeCombination.setCost(minAttrValue.getCost());
        storeCombination.setStock(attrValueAddRequestList.stream().mapToInt(StoreProductAttrValueAddRequest::getStock).sum());
        int quotaTotal = attrValueAddRequestList.stream().mapToInt(StoreProductAttrValueAddRequest::getQuota).sum();
        storeCombination.setQuota(quotaTotal);
        storeCombination.setQuotaShow(quotaTotal);

        List<StoreProductAttrAddRequest> addRequestList = request.getAttr();
        List<StoreProductAttr> attrList = addRequestList.stream().map(e -> {
            StoreProductAttr attr = new StoreProductAttr();
            BeanUtils.copyProperties(e, attr);
            attr.setType(Constants.PRODUCT_TYPE_PINGTUAN);
            return attr;
        }).collect(Collectors.toList());

        List<StoreProductAttrValue> attrValueList = attrValueAddRequestList.stream().map(e -> {
            StoreProductAttrValue attrValue = new StoreProductAttrValue();
            BeanUtils.copyProperties(e, attrValue);
            attrValue.setSuk(e.getSuk());
            attrValue.setQuota(e.getQuota());
            attrValue.setQuotaShow(e.getQuota());
            attrValue.setType(Constants.PRODUCT_TYPE_PINGTUAN);
            attrValue.setImage(systemAttachmentService.clearPrefix(e.getImage()));
            return attrValue;
        }).collect(Collectors.toList());

        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription();
        spd.setDescription(request.getContent().length() > 0 ? systemAttachmentService.clearPrefix(request.getContent()) : "");
        spd.setType(Constants.PRODUCT_TYPE_PINGTUAN);

        Boolean execute = transactionTemplate.execute(e -> {
            save(storeCombination);

            attrList.forEach(attr -> attr.setProductId(storeCombination.getId()));
            attrValueList.forEach(value -> value.setProductId(storeCombination.getId()));
            storeProductAttrService.saveBatch(attrList);
            storeProductAttrValueService.saveBatch(attrValueList);

            spd.setProductId(storeCombination.getId());
            storeProductDescriptionService.deleteByProductId(storeCombination.getId(), Constants.PRODUCT_TYPE_PINGTUAN);
            storeProductDescriptionService.save(spd);

            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 删除拼团商品
     */
    @Override
    public Boolean deleteById(Integer id) {
        StoreCombination combination = getById(id);
        long timeMillis = System.currentTimeMillis();
        if (combination.getIsShow().equals(true) && combination.getStartTime() <= timeMillis && timeMillis <= combination.getStopTime()) {
            throw new CrmebException("活动开启中，商品不支持删除");
        }

        StoreCombination storeCombination = new StoreCombination();
        storeCombination.setId(id).setIsDel(true);
        return updateById(storeCombination);
    }

    /**
     * 编辑拼团商品
     *
     * @param request 编辑请求参数
     * @return Boolean
     */
    @Override
    public Boolean updateCombination(StoreCombinationRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException("拼团商品id不能为空");
        }
        StoreCombination existCombination = getById(request.getId());
        if (ObjectUtil.isNull(existCombination) || existCombination.getIsDel()) {
            throw new CrmebException("拼团商品不存在");
        }
        long timeMillis = System.currentTimeMillis();
        if (existCombination.getIsShow().equals(true) && existCombination.getStartTime() <= timeMillis && timeMillis <= existCombination.getStopTime()) {
            throw new CrmebException("活动开启中，商品不支持修改");
        }

        StoreCombination storeCombination = new StoreCombination();
        BeanUtils.copyProperties(request, storeCombination);
        // 头图、轮播图
        storeCombination.setImage(systemAttachmentService.clearPrefix(request.getImage()));
        storeCombination.setImages(systemAttachmentService.clearPrefix(request.getImages()));
        // 活动开始结束时间
        storeCombination.setStartTime(DateUtil.dateStr2Timestamp(request.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN));
        storeCombination.setStopTime(DateUtil.dateStr2Timestamp(request.getStopTime(), Constants.DATE_TIME_TYPE_END));

        List<StoreProductAttrValueAddRequest> attrValueAddRequestList = request.getAttrValue();
        // 计算价格
        StoreProductAttrValueAddRequest minAttrValue = attrValueAddRequestList.stream().min(Comparator.comparing(StoreProductAttrValueAddRequest::getPrice)).get();
        storeCombination.setPrice(minAttrValue.getPrice());
        storeCombination.setOtPrice(minAttrValue.getOtPrice());
        storeCombination.setCost(minAttrValue.getCost());
        storeCombination.setStock(attrValueAddRequestList.stream().mapToInt(StoreProductAttrValueAddRequest::getStock).sum());
        int quotaTotal = attrValueAddRequestList.stream().mapToInt(StoreProductAttrValueAddRequest::getQuota).sum();
        storeCombination.setQuota(quotaTotal);
        storeCombination.setQuotaShow(quotaTotal);

        // attr部分
        List<StoreProductAttrAddRequest> addRequestList = request.getAttr();
        List<StoreProductAttr> attrAddList = CollUtil.newArrayList();
        List<StoreProductAttr> attrUpdateList = CollUtil.newArrayList();
        addRequestList.forEach(e -> {
            StoreProductAttr attr = new StoreProductAttr();
            BeanUtils.copyProperties(e, attr);
            if (ObjectUtil.isNull(attr.getId())) {
                attr.setProductId(storeCombination.getId());
                attr.setType(Constants.PRODUCT_TYPE_PINGTUAN);
                attrAddList.add(attr);
            } else {
                attr.setProductId(storeCombination.getId());
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
            attrValue.setSuk(e.getSuk());
            attrValue.setImage(systemAttachmentService.clearPrefix(e.getImage()));
            attrValue.setQuota(e.getQuota());
            attrValue.setQuotaShow(e.getQuota());
            if (ObjectUtil.isNull(attrValue.getId())) {
                attrValue.setProductId(storeCombination.getId());
                attrValue.setType(Constants.PRODUCT_TYPE_PINGTUAN);
                attrValueAddList.add(attrValue);
            } else {
                attrValue.setProductId(storeCombination.getId());
                attrValue.setIsDel(false);
                attrValueUpdateList.add(attrValue);
            }
        });

        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription();
        spd.setDescription(request.getContent().length() > 0 ? systemAttachmentService.clearPrefix(request.getContent()) : "");
        spd.setType(Constants.PRODUCT_TYPE_PINGTUAN);
        spd.setProductId(request.getId());

        Boolean execute = transactionTemplate.execute(e -> {
            dao.updateById(storeCombination);

            // 先删除原用attr+value
            storeProductAttrService.deleteByProductIdAndType(storeCombination.getId(), Constants.PRODUCT_TYPE_PINGTUAN);
            storeProductAttrValueService.deleteByProductIdAndType(storeCombination.getId(), Constants.PRODUCT_TYPE_PINGTUAN);

            if (CollUtil.isNotEmpty(attrAddList)) {
                storeProductAttrService.saveBatch(attrAddList);
            }
            if (CollUtil.isNotEmpty(attrUpdateList)) {
                storeProductAttrService.saveOrUpdateBatch(attrUpdateList);
            }

            if (CollUtil.isNotEmpty(attrValueAddList)) {
                storeProductAttrValueService.saveBatch(attrValueAddList);
            }
            if (CollUtil.isNotEmpty(attrValueUpdateList)) {
                storeProductAttrValueService.saveOrUpdateBatch(attrValueUpdateList);
            }

            storeProductDescriptionService.deleteByProductId(storeCombination.getId(), Constants.PRODUCT_TYPE_PINGTUAN);
            storeProductDescriptionService.save(spd);

            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 拼团商品详情
     *
     * @param id 拼团商品ID
     * @return StoreProductInfoResponse
     */
    @Override
    public StoreProductInfoResponse getAdminDetail(Integer id) {
        StoreCombination storeCombination = dao.selectById(id);
        if (ObjectUtil.isNull(storeCombination) || storeCombination.getIsDel()) {
            throw new CrmebException("未找到对应商品信息");
        }
        StoreProductInfoResponse storeProductResponse = new StoreProductInfoResponse();
        BeanUtils.copyProperties(storeCombination, storeProductResponse);
//        storeProductResponse.setStartTimeStr(DateUtil.timestamp2DateStr(storeCombination.getStartTime(), Constants.DATE_FORMAT_DATE));
//        storeProductResponse.setStopTimeStr(DateUtil.timestamp2DateStr(storeCombination.getStopTime(), Constants.DATE_FORMAT_DATE));

        // 查询attr
        List<StoreProductAttr> attrs = storeProductAttrService.getListByProductIdAndType(id, Constants.PRODUCT_TYPE_PINGTUAN);
        storeProductResponse.setAttr(attrs);
        storeProductResponse.setSliderImage(String.join(",", storeCombination.getImages()));

        boolean specType = false;
        if (attrs.size() > 1) {
            specType = true;
        }
        storeProductResponse.setSpecType(specType);

        List<StoreProductAttrValue> comAttrValueList = storeProductAttrValueService.getListByProductIdAndType(id, ProductConstants.PRODUCT_TYPE_PINGTUAN);
        // 查询主商品sku
        List<StoreProductAttrValue> attrValueList = storeProductAttrValueService.getListByProductIdAndType(storeCombination.getProductId(), Constants.PRODUCT_TYPE_NORMAL);

        List<AttrValueResponse> valueResponseList = attrValueList.stream().map(e -> {
            AttrValueResponse valueResponse = new AttrValueResponse();
            Integer tempId = 0;
            for (StoreProductAttrValue value : comAttrValueList) {
                if (value.getSuk().equals(e.getSuk())) {
                    tempId = value.getId();
                    BeanUtils.copyProperties(value, valueResponse);
                    break;
                }
            }
            if (tempId.equals(0)) {
                BeanUtils.copyProperties(e, valueResponse);
                valueResponse.setId(null);
            } else {
                valueResponse.setId(tempId);
            }
            return valueResponse;
        }).collect(Collectors.toList());
        storeProductResponse.setAttrValue(valueResponseList);

        StoreProductDescription sd = storeProductDescriptionService.getByProductIdAndType(id, Constants.PRODUCT_TYPE_PINGTUAN);
        if (ObjectUtil.isNotNull(sd)) {
            storeProductResponse.setContent(ObjectUtil.isNull(sd.getDescription()) ? "" : sd.getDescription());
        }
        return storeProductResponse;
    }

    /**
     * 修改拼团商品状态
     */
    @Override
    public Boolean updateCombinationShow(Integer id, Boolean isShow) {
        StoreCombination temp = getById(id);
        if (ObjectUtil.isNull(temp) || temp.getIsDel()) {
            throw new CrmebException("拼团商品不存在");
        }
        if (isShow) {
            // 判断商品是否存在
            StoreProduct product = storeProductService.getById(temp.getProductId());
            if (ObjectUtil.isNull(product)) {
                throw new CrmebException("关联的商品已删除，无法开启活动");
            }
        }

        StoreCombination storeCombination = new StoreCombination();
        storeCombination.setId(id).setIsShow(isShow);
        return updateById(storeCombination);
    }

    /**
     * admin拼团统计
     */
    @Override
    public Map<String, Object> getAdminStatistics() {
        StorePink spavPink = new StorePink();
        spavPink.setKId(0);
        List<StorePink> pinkList = storePinkService.getByEntity(spavPink);
        Map<String, Object> map = CollUtil.newHashMap();
        map.put("countPeople", 0);
        map.put("countTeam", 0);
        if (CollUtil.isNotEmpty(pinkList)) {
            map.put("countPeople", storePinkService.count());
            long countTeam = pinkList.stream().filter(i -> i.getStatus() == 2).count();
            map.put("countTeam", countTeam);
        }
        return map;
    }

    /**
     * H5拼团商品列表
     */
    @Override
    public List<StoreCombinationH5Response> getH5List(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreCombination> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreCombination::getId ,StoreCombination::getProductId ,StoreCombination::getImage ,StoreCombination::getTitle
                ,StoreCombination::getPeople ,StoreCombination::getOtPrice ,StoreCombination::getPrice ,StoreCombination::getStock);
        lqw.eq(StoreCombination::getIsDel, false);
        lqw.eq(StoreCombination::getIsShow, true);
        long millis = System.currentTimeMillis();
        lqw.le(StoreCombination::getStartTime, millis);
        lqw.ge(StoreCombination::getStopTime, millis);
        lqw.orderByDesc(StoreCombination::getSort, StoreCombination::getId);
        List<StoreCombination> combinationList = dao.selectList(lqw);
        if (CollUtil.isEmpty(combinationList)) {
            return CollUtil.newArrayList();
        }
        List<StoreCombinationH5Response> responseList = combinationList.stream().map(e -> {
            StoreCombinationH5Response response = new StoreCombinationH5Response();
            BeanUtils.copyProperties(e, response);
            return response;
        }).collect(Collectors.toList());
        return responseList;
    }

    /**
     * H5拼团商品详情
     *
     * @param comId 拼团商品编号
     * @return CombinationDetailResponse
     */
    @Override
    public CombinationDetailResponse getH5Detail(Integer comId) {
        CombinationDetailResponse detailResponse = new CombinationDetailResponse();
        StoreCombination storeCombination = getById(comId);
        if (ObjectUtil.isNull(storeCombination) || storeCombination.getIsDel()) {
            throw new CrmebException("对应拼团商品不存在");
        }
        if (!storeCombination.getIsShow()) {
            throw new CrmebException("拼团商品已下架");
        }
        CombinationDetailH5Response infoResponse = new CombinationDetailH5Response();
        BeanUtils.copyProperties(storeCombination, infoResponse);
        infoResponse.setStoreName(storeCombination.getTitle());
        infoResponse.setSliderImage(storeCombination.getImages());
        infoResponse.setStoreInfo(storeCombination.getInfo());
        // 详情
        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, comId)
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_PINGTUAN));
        if (ObjectUtil.isNotNull(sd)) {
            infoResponse.setContent(ObjectUtil.isNull(sd.getDescription()) ? "" : sd.getDescription());
        }

        // 获取主商品信息
        StoreProduct storeProduct = storeProductService.getById(storeCombination.getProductId());
        // 主商品状态
        if (storeProduct.getIsDel()) {
            detailResponse.setMasterStatus("delete");
        } else if (!storeProduct.getIsShow()) {
            detailResponse.setMasterStatus("soldOut");
        } else if (storeProduct.getStock() <= 0) {
            detailResponse.setMasterStatus("sellOut");
        } else {
            detailResponse.setMasterStatus("normal");
        }

        // 拼团销量 = 原商品销量（包含虚拟销量）
        infoResponse.setSales(storeProduct.getSales());
        infoResponse.setFicti(storeProduct.getFicti());
        detailResponse.setStoreCombination(infoResponse);

        // 获取拼团商品规格
        List<StoreProductAttr> attrList = storeProductAttrService.getListByProductIdAndType(comId, Constants.PRODUCT_TYPE_PINGTUAN);
        // 根据制式设置attr属性
//        List<ProductAttrResponse> skuAttr = getSkuAttr(attrList);
        detailResponse.setProductAttr(attrList);

        // 根据制式设置sku属性
        HashMap<String, Object> skuMap = CollUtil.newHashMap();
        // 获取主商品sku
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getListByProductIdAndType(storeCombination.getProductId(), Constants.PRODUCT_TYPE_NORMAL);
        // 获取拼团商品sku
        List<StoreProductAttrValue> combinationAttrValues = storeProductAttrValueService.getListByProductIdAndType(storeCombination.getId(), Constants.PRODUCT_TYPE_PINGTUAN);

        for (StoreProductAttrValue productAttrValue : storeProductAttrValues) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            List<StoreProductAttrValue> valueList = combinationAttrValues.stream().filter(e -> productAttrValue.getSuk().equals(e.getSuk())).collect(Collectors.toList());
            if (CollUtil.isEmpty(valueList)) {
                BeanUtils.copyProperties(productAttrValue, atr);
            } else {
                BeanUtils.copyProperties(valueList.get(0), atr);
            }
            if (ObjectUtil.isNull(atr.getQuota())) {
                atr.setQuota(0);
            }
            skuMap.put(atr.getSuk(), atr);
        }
        detailResponse.setProductValue(skuMap);

        // 设置点赞和收藏
        User user = userService.getInfo();
        if (ObjectUtil.isNotNull(user) && ObjectUtil.isNotNull(user.getUid())) {
            detailResponse.setUserCollect(storeProductRelationService.getLikeOrCollectByUser(user.getUid(), storeCombination.getProductId(), false).size() > 0);
        } else {
            detailResponse.setUserCollect(false);
        }

        detailResponse.setPinkOkSum(0);
        // 拼团团长列表
        List<StorePink> headList = storePinkService.getListByCidAndKid(storeCombination.getId(), 0);
        if (CollUtil.isNotEmpty(headList)) {
            List<StorePinkResponse> headPinkList = headList.stream().map(i -> {
                StorePinkResponse pinkResponse = new StorePinkResponse();
                BeanUtils.copyProperties(i, pinkResponse);
                User teamUser = userService.getById(i.getUid());
                pinkResponse.setAvatar(Optional.ofNullable(teamUser.getAvatar()).orElse(""));
                pinkResponse.setNickname(teamUser.getNickname());
                return pinkResponse;
            }).collect(Collectors.toList());

            // 拼团成功部分
            List<StorePinkResponse> okList = headPinkList.stream().filter(i -> i.getStatus().equals(2)).collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(okList)) {
                // 拼团完成的商品总件数
                List<StorePink> pinkOkList = CollUtil.newArrayList();
                okList.forEach(e -> {
                    List<StorePink> list = storePinkService.getListByCidAndKid(e.getCid(), e.getId());
                    if (CollUtil.isNotEmpty(list)) {
                        pinkOkList.addAll(list);
                    }
                });
                List<StorePinkResponse> responseList = pinkOkList.stream().map(i -> {
                    StorePinkResponse pinkResponse = new StorePinkResponse();
                    BeanUtils.copyProperties(i, pinkResponse);
                    User teamUser = userService.getById(i.getUid());
                    pinkResponse.setAvatar(Optional.ofNullable(teamUser.getAvatar()).orElse(""));
                    pinkResponse.setNickname(teamUser.getNickname());
                    return pinkResponse;
                }).collect(Collectors.toList());
                okList.addAll(responseList);
                int pinkOkSum = okList.stream().mapToInt(StorePinkResponse::getTotalNum).sum();
                detailResponse.setPinkOkSum(pinkOkSum);
                //拼团完成的用户
                detailResponse.setPinkOkList(okList);
            }

            // 可拼团团长部分
            List<StorePinkResponse> pinkingList = headPinkList.stream().filter(i -> {
                boolean filter = false;
                if (i.getStatus().equals(1) && i.getStopTime() > System.currentTimeMillis()) {
                    filter = true;
                }
                return filter;
            }).collect(Collectors.toList());
            // 获取还剩几人成团
            pinkingList.forEach(i -> {
                Integer countPeople = storePinkService.getCountByKid(i.getId());
                i.setCountPeople(countPeople);
                i.setCount(i.getPeople() - countPeople);
            });

            // 所有团长列表
            detailResponse.setPinkList(pinkingList);
        }

        // 保存用户访问记录
        UserVisitRecord visitRecord = new UserVisitRecord();
        visitRecord.setDate(cn.hutool.core.date.DateUtil.date().toString("yyyy-MM-dd"));
        visitRecord.setUid(userService.getUserId());
        visitRecord.setVisitType(3);
        userVisitRecordService.save(visitRecord);
        return detailResponse;
    }

    /**
     * 去拼团
     *
     * @param pinkId 拼团团长单ID
     * @return GoPinkResponse
     */
    @Override
    public GoPinkResponse goPink(Integer pinkId) {
        int isOk = 0;//判断拼团是否完成
        int userBool = 0;//判断当前用户是否在团内  0未在 1在
        int pinkBool = 0;//判断拼团是否成功  0未成功 1成功

        StorePink teamPink = storePinkService.getById(pinkId);
        if (ObjectUtil.isNull(teamPink) || teamPink.getIsRefund()) throw new CrmebException("对应的拼团不存在");
        StoreCombination storeCombination = getById(teamPink.getCid());
        if (ObjectUtil.isNull(storeCombination) || storeCombination.getIsDel()) throw new CrmebException("对应拼团商品不存在");

        // 判断拼团活动时效
        if (!storeCombination.getIsShow()) {
            throw new CrmebException("拼团活动已结束");
        }
        if (System.currentTimeMillis() > storeCombination.getStopTime()) {
            throw new CrmebException("拼团活动已结束");
        }

        User user = userService.getInfo();

        GoPinkResponse goPinkResponse = new GoPinkResponse();
        List<StorePink> pinkList;
        if (teamPink.getKId().equals(0)) {
            pinkList = storePinkService.getListByCidAndKid(teamPink.getCid(), teamPink.getId());
            pinkList.add(teamPink);
        } else {
            pinkList = storePinkService.getListByCidAndKid(teamPink.getCid(), teamPink.getKId());
            pinkList.add(storePinkService.getById(teamPink.getKId()));
        }
        //拼团剩余人数
        int count = teamPink.getPeople() - ( CollUtil.isEmpty(pinkList) ? 0 : pinkList.size() );

        if (teamPink.getStatus() == 2) {//已完成
            isOk = 1;
            pinkBool = 1;
        }
        if (teamPink.getStatus() == 1) {//进行中
            if (count < 1) {// 拼团已完成
                isOk = 1;
                pinkBool = 1;
                // 拼团完成处理
                storePinkService.pinkSuccess(teamPink.getId());
            }
        }

        List<Integer> uidList = pinkList.stream().map(StorePink::getUid).collect(Collectors.toList());
        uidList.add(teamPink.getUid());
        if (uidList.contains(user.getUid())) {
            userBool = 1;
        }

        // 处理用户头像昵称
        List<StorePinkResponse> pinkResponseList = CollUtil.newArrayList();
        // 团长
        StorePinkResponse storePinkTResponse = new StorePinkResponse();
        for (StorePink pink : pinkList) {
            if (pink.getKId().equals(0)) {
                BeanUtils.copyProperties(pink, storePinkTResponse);
                if (pink.getUid().equals(user.getUid())) {
                    storePinkTResponse.setNickname(user.getNickname());
                    storePinkTResponse.setAvatar(user.getAvatar());
                } else {
                    User teamUser = userService.getById(pink.getUid());
                    storePinkTResponse.setNickname(teamUser.getNickname());
                    storePinkTResponse.setAvatar(teamUser.getAvatar());
                }
                continue;
            }
            StorePinkResponse storePinkResponse = new StorePinkResponse();
            BeanUtils.copyProperties(pink, storePinkResponse);
            User userOne = userService.getById(pink.getUid());
            storePinkResponse.setNickname(userOne.getNickname());
            storePinkResponse.setAvatar(userOne.getAvatar());
            pinkResponseList.add(storePinkResponse);
        }

        goPinkResponse.setCount(count);
        goPinkResponse.setIsOk(isOk);
        goPinkResponse.setPinkBool(pinkBool);
        goPinkResponse.setUserBool(userBool);
        if (userBool == 1) {
            pinkList.forEach(e -> {
                if (e.getUid().equals(user.getUid())) {
                    goPinkResponse.setCurrentPinkOrder(e.getOrderId());
                }
            });
        }
        goPinkResponse.setPinkAll(pinkResponseList);
        goPinkResponse.setPinkT(storePinkTResponse);
        goPinkResponse.setUserInfo(user);

        // storeCombination部分
        StoreCombinationResponse detailResponse = new StoreCombinationResponse();
        BeanUtils.copyProperties(storeCombination, detailResponse);
        detailResponse.setSpecType(false);
        // sku部分
        StoreProductAttr spavAttr = new StoreProductAttr();
        spavAttr.setProductId(storeCombination.getId());
        spavAttr.setType(Constants.PRODUCT_TYPE_PINGTUAN);
        List<StoreProductAttr> attrList = storeProductAttrService.getByEntity(spavAttr);
        List<HashMap<String, Object>> skuAttrList = getSkuAttrList(attrList);
        detailResponse.setProductAttr(skuAttrList);
        if (CollUtil.isNotEmpty(attrList) && attrList.size() > 1) {
            detailResponse.setSpecType(true);
        }
        // 单属性时讲attrValueId 赋值给外层方便前端使用
        if (!detailResponse.getSpecType()) {
            detailResponse.setAloneAttrValueId(attrList.get(0).getId());
        }


        StoreProductAttrValue spavValue = new StoreProductAttrValue();
        spavValue.setProductId(storeCombination.getId());
        spavValue.setType(Constants.PRODUCT_TYPE_PINGTUAN);
        List<StoreProductAttrValue> valueList = storeProductAttrValueService.getByEntity(spavValue);
        // H5 端用于生成skuList
        List<StoreProductAttrValueResponse> sPAVResponses = new ArrayList<>();
        for (StoreProductAttrValue storeProductAttrValue : valueList) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue, atr);
            sPAVResponses.add(atr);
        }
        HashMap<String, Object> skuMap = new HashMap<>();
        for (StoreProductAttrValueResponse attrValue : sPAVResponses) {
            skuMap.put(attrValue.getSuk(), attrValue);
        }

        detailResponse.setProductValue(skuMap);
        goPinkResponse.setStoreCombination(detailResponse);
        return goPinkResponse;
    }

    /**
     * 更多拼团信息
     */
    @Override
    public PageInfo<StoreCombination> getMore(PageParamRequest pageParamRequest, Integer comId) {
        Page<StoreCombination> combinationPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(comId)) {
            lqw.ne(StoreCombination::getId, comId);
        }
        lqw.eq(StoreCombination::getIsDel, false);
        lqw.eq(StoreCombination::getIsShow, true);
        long millis = System.currentTimeMillis();
        lqw.le(StoreCombination::getStartTime, millis);
        lqw.ge(StoreCombination::getStopTime, millis);
        lqw.orderByDesc(StoreCombination::getSort, StoreCombination::getId);
        List<StoreCombination> storeCombinations = dao.selectList(lqw);
        return CommonPage.copyPageInfo(combinationPage, storeCombinations);
    }

    /**
     * 取消拼团
     *
     * @return 此处只是转为申请退款订单
     * 自己是团长，取消后，顺位第一人变为团长
     * 自己不是团长，直接取消
     */
    @Override
    public Boolean removePink(StorePinkRequest storePinkRequest) {
        StorePink userPink = storePinkService.getById(storePinkRequest.getId());
        if (ObjectUtil.isNull(userPink)) throw new CrmebException("未查到拼团信息，无法取消");
        if (userPink.getIsRefund()) throw new CrmebException("拼团订单已退款");
        // 获取是否拼团成功
        if (userPink.getStatus() == 2) {
            throw new CrmebException("拼团已完成，无法取消");
        }
        Integer kid = userPink.getKId() > 0 ? userPink.getKId() : userPink.getId();
        Integer count = storePinkService.getCountByKid(kid);
        if (count.equals(userPink.getPeople())) {
            // 拼团完成操作
            storePinkService.pinkSuccess(kid);
            throw new CrmebException("拼团已完成，无法取消");
        }
        if (userPink.getStatus() == 3) {
            throw new CrmebException("拼团已申请取消");
        }

        StoreOrder order = storeOrderService.getByOderId(userPink.getOrderId());
        if (ObjectUtil.isNull(order) || order.getIsDel()) throw new CrmebException("拼团订单不存在");
        if (order.getStatus() == -1 && order.getRefundStatus() != 0) {
            throw new CrmebException("拼团订单已进入退款流程");
        }

        // 订单申请退款
        OrderRefundApplyRequest refundRequest = new OrderRefundApplyRequest();
        refundRequest.setId(order.getId());
        refundRequest.setUni(order.getOrderId());
        refundRequest.setText("拼团订单取消，申请退款");
        refundRequest.setExplain("用户取消拼团订单，申请退款");
        boolean apply = orderService.refundApply(refundRequest);
        if (!apply) throw new CrmebException("订单申请退款失败");

        // 拼团改为未完成
        userPink.setStatus(3).setStopTime(System.currentTimeMillis());
        storePinkService.updateById(userPink);
        if (userPink.getKId() == 0) {// 是团长
            List<StorePink> pinkList = storePinkService.getListByCidAndKid(userPink.getCid(), userPink.getId());
            if (CollUtil.isNotEmpty(pinkList)) {
                // 团员更换团长
                StorePink newHeadPink = pinkList.get(pinkList.size() - 1);
                newHeadPink.setKId(0);
                pinkList.remove(pinkList.size() - 1);
                pinkList.forEach(i -> i.setKId(newHeadPink.getId()));
                pinkList.add(newHeadPink);
                storePinkService.updateBatchById(pinkList);
            }
        }

        return true;
    }

    @Override
    public void consumeProductStock() {
        String redisKey = Constants.PRODUCT_COMBINATION_STOCK_UPDATE;
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
     * 获取当前时间的拼团商品
     *
     * @param productId 商品编号
     */
    @Override
    public List<StoreCombination> getCurrentBargainByProductId(Integer productId) {
        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCombination::getProductId, productId);
        lqw.eq(StoreCombination::getIsShow, true);
        long millis = System.currentTimeMillis();
        lqw.le(StoreCombination::getStartTime, millis);
        lqw.ge(StoreCombination::getStopTime, millis);
        lqw.orderByDesc(StoreCombination::getSort, StoreCombination::getId);
        return dao.selectList(lqw);
    }

    /**
     * 商品是否存在拼团活动
     *
     * @param productId 商品编号
     */
    @Override
    public Boolean isExistActivity(Integer productId) {
        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCombination::getProductId, productId);
        List<StoreCombination> combinationList = dao.selectList(lqw);
        if (CollUtil.isEmpty(combinationList)) {
            return false;
        }
        // 判断关联的商品是否处于活动开启状态
        List<StoreCombination> list = combinationList.stream().filter(i -> i.getIsShow().equals(true)).collect(Collectors.toList());
        return CollUtil.isNotEmpty(list);
    }

    /**
     * 查询带异常
     *
     * @param combinationId 拼团商品id
     * @return StoreCombination
     */
    @Override
    public StoreCombination getByIdException(Integer combinationId) {
        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCombination::getId, combinationId);
        lqw.eq(StoreCombination::getIsDel, false);
        lqw.eq(StoreCombination::getIsShow, true);
        StoreCombination storeCombination = dao.selectOne(lqw);
        if (ObjectUtil.isNull(storeCombination)) throw new CrmebException("拼团商品不存在或未开启");
        return storeCombination;
    }

    /**
     * 添加/扣减库存
     *
     * @param id   秒杀商品id
     * @param num  数量
     * @param type 类型：add—添加，sub—扣减
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String type) {
        UpdateWrapper<StoreCombination> updateWrapper = new UpdateWrapper<>();
        if ("add".equals(type)) {
            updateWrapper.setSql(StrUtil.format("stock = stock + {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales - {}", num));
            updateWrapper.setSql(StrUtil.format("quota = quota + {}", num));
        }
        if ("sub".equals(type)) {
            updateWrapper.setSql(StrUtil.format("stock = stock - {}", num));
            updateWrapper.setSql(StrUtil.format("sales = sales + {}", num));
            updateWrapper.setSql(StrUtil.format("quota = quota - {}", num));
            // 扣减时加乐观锁保证库存不为负
            updateWrapper.last(StrUtil.format(" and (quota - {} >= 0)", num));
        }
        updateWrapper.eq("id", id);
        boolean update = update(updateWrapper);
        if (!update) {
            throw new CrmebException("更新拼团商品库存失败,商品id = " + id);
        }
        return update;
    }

    /**
     * 拼团首页数据
     * 拼团数据 + 拼团商品6个
     * 3个用户头像（最多）
     * 拼团参与总人数
     * @return CombinationIndexResponse
     */
    @Override
    public CombinationIndexResponse getIndexInfo() {
        // 获取最近的3单拼团订单
        List<StorePink> tempPinkList = storePinkService.findSizePink(3);
        List<String> avatarList = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(tempPinkList)) {
            // 获取这三个用户头像
            avatarList = tempPinkList.stream().map(StorePink::getAvatar).collect(Collectors.toList());
        }
        // 获取拼团参与总人数
        Integer totalPeople = storePinkService.getTotalPeople();

        // 获取6个拼团商品
        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCombination::getIsDel, false);
        lqw.eq(StoreCombination::getIsShow, true);
        lqw.ge(StoreCombination::getStock, 0);
        long millis = System.currentTimeMillis();
        lqw.le(StoreCombination::getStartTime, millis);
        lqw.ge(StoreCombination::getStopTime, millis);
        lqw.orderByDesc(StoreCombination::getSort, StoreCombination::getId);
        lqw.last(" limit 6");
        List<StoreCombination> combinationList = dao.selectList(lqw);
        if (CollUtil.isEmpty(combinationList)) {
            return null;
        }
        combinationList.forEach(e -> {
            int percentIntVal = CrmebUtil.percentInstanceIntVal(e.getQuota(), e.getQuotaShow());
            e.setQuotaPercent(percentIntVal);
        });

        CombinationIndexResponse response = new CombinationIndexResponse();
        response.setAvatarList(avatarList);
        response.setTotalPeople(totalPeople);
        response.setProductList(combinationList);
        return response;
    }

    /**
     * 拼团列表header
     * @return CombinationHeaderResponse
     */
    @Override
    public CombinationHeaderResponse getHeader() {
        // 获取最近的3单拼团订单
        List<StorePink> tempPinkList = storePinkService.findSizePink(7);
        List<String> avatarList = CollUtil.newArrayList();
        if (CollUtil.isNotEmpty(tempPinkList)) {
            // 获取这三个用户头像
            avatarList = tempPinkList.stream().map(StorePink::getAvatar).collect(Collectors.toList());
        }
        // 获取拼团参与总人数
        Integer totalPeople = storePinkService.getTotalPeople();

        // 获取拼团列表banner
        List<HashMap<String, Object>> bannerList = systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_COMBINATION_LIST_BANNNER);

        CombinationHeaderResponse response = new CombinationHeaderResponse();
        response.setAvatarList(avatarList);
        response.setTotalPeople(totalPeople);
        response.setBannerList(bannerList);
        return response;
    }

    /**
     * 拼团操作库存
     */
    private boolean doProductStock(StoreProductStockRequest storeProductStockRequest) {
        // 砍价商品信息回滚
        StoreCombination existCombination = getById(storeProductStockRequest.getCombinationId());
        List<StoreProductAttrValue> existAttr =
                storeProductAttrValueService.getListByProductIdAndAttrId(
                        storeProductStockRequest.getCombinationId(),
                        storeProductStockRequest.getAttrId().toString(),
                        storeProductStockRequest.getType());
        if (ObjectUtil.isNull(existCombination) || ObjectUtil.isNull(existAttr)) { // 未找到商品
            logger.info("库存修改任务未获取到商品信息" + JSON.toJSONString(storeProductStockRequest));
            return true;
        }

        // 回滚商品库存/销量 并更新
        boolean isPlus = "add".equals(storeProductStockRequest.getOperationType());
        int productStock = isPlus ? existCombination.getStock() + storeProductStockRequest.getNum() : existCombination.getStock() - storeProductStockRequest.getNum();
        existCombination.setStock(productStock);
        existCombination.setSales(existCombination.getSales() - storeProductStockRequest.getNum());
        existCombination.setQuota(existCombination.getQuota() + storeProductStockRequest.getNum());
        updateById(existCombination);

        // 回滚sku库存
        for (StoreProductAttrValue attrValue : existAttr) {
            int productAttrStock = isPlus ? attrValue.getStock() + storeProductStockRequest.getNum() : attrValue.getStock() - storeProductStockRequest.getNum();
            attrValue.setStock(productAttrStock);
            attrValue.setSales(attrValue.getSales() - storeProductStockRequest.getNum());
            attrValue.setQuota(attrValue.getQuota() + storeProductStockRequest.getNum());
            storeProductAttrValueService.updateById(attrValue);
        }

        // 商品本身库存回滚
        // StoreProductStockRequest 创建次对象调用商品扣减库存实现扣减上本本身库存
        StoreProductResponse existProductLinkedSeckill = storeProductService.getByProductId(storeProductStockRequest.getProductId());
        for (StoreProductAttrValueResponse attrValueResponse : existProductLinkedSeckill.getAttrValue()) {
            if (attrValueResponse.getSuk().equals(storeProductStockRequest.getSuk())) {
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
     * 获取制式结构给attr属性
     */
    private List<HashMap<String, Object>> getSkuAttrList(List<StoreProductAttr> attrList) {
        List<HashMap<String, Object>> attrMapList = new ArrayList<>();
        if (CollUtil.isEmpty(attrList)) {
            return attrMapList;
        }
        for (StoreProductAttr attr : attrList) {
            HashMap<String, Object> attrMap = new HashMap<>();
            attrMap.put("productId", attr.getProductId());
            attrMap.put("attrName", attr.getAttrName());
            List<String> attrValues = new ArrayList<>();
            String trimAttr = attr.getAttrValues()
                    .replace("[", "")
                    .replace("]", "");
            if (attr.getAttrValues().contains(",")) {
                attrValues = Arrays.asList(trimAttr.split(","));
            } else {
                attrValues.add(trimAttr);
            }
            attrMap.put("attrValues", attrValues);

            List<HashMap<String, Object>> attrValueMapList = new ArrayList<>();
            for (String attrValue : attrValues) {
                HashMap<String, Object> attrValueMap = new HashMap<>();
                attrValueMap.put("attr", attrValue);
                attrValueMapList.add(attrValueMap);
            }
            attrMap.put("attrValue", attrValueMapList);
            attrMapList.add(attrMap);
        }
        return attrMapList;
    }
}

