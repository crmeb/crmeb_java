package com.zbkj.crmeb.combination.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.combination.dao.StoreCombinationDao;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.request.StoreCombinationRequest;
import com.zbkj.crmeb.combination.request.StoreCombinationSearchRequest;
import com.zbkj.crmeb.combination.request.StorePinkRequest;
import com.zbkj.crmeb.combination.response.StoreCombinationInfoResponse;
import com.zbkj.crmeb.combination.response.StoreCombinationResponse;
import com.zbkj.crmeb.combination.response.StorePinkResponse;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.front.request.OrderRefundApplyRequest;
import com.zbkj.crmeb.front.response.CombinationDetailResponse;
import com.zbkj.crmeb.front.response.GoPinkResponse;
import com.zbkj.crmeb.front.service.OrderService;
import com.zbkj.crmeb.store.model.*;
import com.zbkj.crmeb.store.request.StoreProductAttrValueRequest;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.response.StoreProductAttrValueResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.ProductUtils;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StoreCombinationService 实现类
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
public class StoreCombinationServiceImpl extends ServiceImpl<StoreCombinationDao, StoreCombination> implements StoreCombinationService {

    @Resource
    private StoreCombinationDao dao;

    @Autowired
    private StorePinkService storePinkService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private ProductUtils productUtils;

    @Autowired
    private StoreProductAttrService storeProductAttrService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private StoreProductAttrResultService storeProductAttrResultService;

    @Autowired
    private StoreProductDescriptionService storeProductDescriptionService;

    @Autowired
    private StoreProductReplyService storeProductReplyService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductRelationService storeProductRelationService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisUtil redisUtil;

    private static final Logger logger = LoggerFactory.getLogger(StoreCombinationServiceImpl.class);

    /**
    * 分页显示拼团商品表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author HZW
    * @since 2020-11-13
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
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveCombination(StoreCombinationRequest request) {
        // 过滤掉checked=false的数据
        clearNotCheckedAndValidationPrice(request);

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
        // 计算价格
        productUtils.calcPriceForAttrValuesCombination(request, storeCombination);
        // 保存数据
        boolean save = save(storeCombination);
        if (!save) throw new CrmebException("新增拼团商品失败");

        // sku处理
        if(request.getSpecType()) { // 多规格
            if (CollUtil.isNotEmpty(request.getAttr()) && request.getAttr().size() > 0) {
                request.getAttr().forEach(e -> {
                    e.setId(null);
                    e.setProductId(storeCombination.getId());
                    e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"",""),"[]"));
                    e.setType(Constants.PRODUCT_TYPE_PINGTUAN);
                });
                boolean attrSave = storeProductAttrService.saveBatch(request.getAttr());
                if (!attrSave) throw new CrmebException("新增拼团商品属性名失败");
            }
        } else { //单规格
            StoreProductAttr singleAttr = new StoreProductAttr();
            singleAttr.setProductId(storeCombination.getId()).setAttrName("规格").setAttrValues("默认").setType(Constants.PRODUCT_TYPE_PINGTUAN);
            boolean attrAddResult = storeProductAttrService.save(singleAttr);
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

            singleAttrValue.setProductId(storeCombination.getId()).setStock(storeCombination.getStock()).setSuk("默认")
                    .setSales(storeCombination.getSales()).setPrice(storeCombination.getPrice())
                    .setImage(systemAttachmentService.clearPrefix(storeCombination.getImage()))
                    .setCost(storeCombination.getCost())
                    .setType(Constants.PRODUCT_TYPE_PINGTUAN)
                    .setOtPrice(storeCombination.getOtPrice()).setBrokerage(commissionL1)
                    .setBrokerageTwo(commissionL2).setQuota(storeCombination.getQuota())
                    .setQuotaShow(storeCombination.getQuota());
            boolean saveOrUpdateResult = storeProductAttrValueService.save(singleAttrValue);
            if(!saveOrUpdateResult) throw new CrmebException("新增属性详情失败");
        }

        if (null != request.getAttrValue() && request.getAttrValue().size() > 0) {
            // 批量设置attrValues对象的商品id
            List<StoreProductAttrValueRequest> storeCombinationAttrValueRequests = request.getAttrValue();
            storeCombinationAttrValueRequests.forEach(e->{
                e.setProductId(storeCombination.getId());
            });
            List<StoreProductAttrValue> storeProductAttrValues = new ArrayList<>();
            for (StoreProductAttrValueRequest attrValuesRequest : storeCombinationAttrValueRequests) {
                StoreProductAttrValue spav = new StoreProductAttrValue();
                BeanUtils.copyProperties(attrValuesRequest, spav);
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
                spav.setType(Constants.PRODUCT_TYPE_PINGTUAN);
                storeProductAttrValues.add(spav);
            }
            // 保存属性
            if(storeProductAttrValues.size() > 0){
                boolean saveOrUpdateResult = storeProductAttrValueService.saveBatch(storeProductAttrValues);
                StoreProductAttrResult attrResult = new StoreProductAttrResult(
                        0,
                        storeCombination.getId(),
                        systemAttachmentService.clearPrefix(JSON.toJSONString(request.getAttrValue())),
                        DateUtil.getNowTime(),Constants.PRODUCT_TYPE_PINGTUAN);
                storeProductAttrResultService.save(attrResult);
                if(!saveOrUpdateResult) throw new CrmebException("新增拼团商品属性详情失败");
            }
        }
        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription(
                storeCombination.getId(),  request.getContent().length() > 0
                ? systemAttachmentService.clearPrefix(request.getContent()):"",Constants.PRODUCT_TYPE_PINGTUAN);
        storeProductDescriptionService.deleteByProductId(spd.getProductId(),Constants.PRODUCT_TYPE_PINGTUAN);
        boolean descSave = storeProductDescriptionService.save(spd);
        if (!descSave) throw new CrmebException("新增拼团商品详情失败");
        return save;
    }

    /**
     * 删除拼团商品
     * @param id
     * @return
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
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCombination(StoreCombinationRequest request) {
        // 过滤掉checked=false的数据
        System.out.println("updateCombination request = " + request);
        clearNotCheckedAndValidationPrice(request);

        StoreCombination storeCombination = new StoreCombination();
        BeanUtils.copyProperties(request, storeCombination);
        // 头图、轮播图
        storeCombination.setImage(systemAttachmentService.clearPrefix(request.getImage()));
        storeCombination.setImages(systemAttachmentService.clearPrefix(request.getImages()));
        // 活动开始结束时间
        storeCombination.setStartTime(DateUtil.dateStr2Timestamp(request.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN));
        storeCombination.setStopTime(DateUtil.dateStr2Timestamp(request.getStopTime(), Constants.DATE_TIME_TYPE_END));
        // 计算价格
        productUtils.calcPriceForAttrValuesCombination(request, storeCombination);
        // 保存数据
        boolean update = updateById(storeCombination);
        if (!update) throw new CrmebException("编辑拼团商品失败");

        // 对attr表做覆盖式更新，删除原有数据保存现有数据
        if (request.getSpecType()) { // 单规格不处理规格属性
            storeProductAttrService.removeByProductId(request.getId(),Constants.PRODUCT_TYPE_PINGTUAN);
            request.getAttr().forEach(e->{
                e.setProductId(request.getId());
                e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"",""),"[]"));
                e.setType(Constants.PRODUCT_TYPE_PINGTUAN);
            });
            boolean updateAttr = storeProductAttrService.saveBatch(request.getAttr());
            if (!updateAttr) throw new CrmebException("编辑拼团商品属性失败");
        }

        if(null != request.getAttrValue() && request.getAttrValue().size() > 0){
            storeProductAttrValueService.removeByProductId(request.getId(),Constants.PRODUCT_TYPE_PINGTUAN);
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
                spav.setType(Constants.PRODUCT_TYPE_PINGTUAN);
                spav.setQuotaShow(spav.getQuota());
                storeProductAttrValues.add(spav);
            }
            boolean saveOrUpdateResult = storeProductAttrValueService.saveBatch(storeProductAttrValues);
            if(!saveOrUpdateResult) throw new CrmebException("编辑属性详情失败");
            // attrResult整存整取，不做更新
            storeProductAttrResultService.deleteByProductId(storeCombination.getId(),Constants.PRODUCT_TYPE_PINGTUAN);
            StoreProductAttrResult attrResult = new StoreProductAttrResult(
                    0,
                    storeCombination.getId(),
                    systemAttachmentService.clearPrefix(JSON.toJSONString(request.getAttrValue())),
                    DateUtil.getNowTime(),Constants.PRODUCT_TYPE_PINGTUAN);
            boolean resultSave = storeProductAttrResultService.save(attrResult);
            if(!resultSave) throw new CrmebException("编辑属性详情结果失败");
        }
        // 处理富文本
        if (StrUtil.isNotBlank(request.getContent())) {
            StoreProductDescription spd = new StoreProductDescription(
                    storeCombination.getId(),
                    request.getContent().length() > 0
                            ? systemAttachmentService.clearPrefix(request.getContent()):"",
                    Constants.PRODUCT_TYPE_PINGTUAN);
            storeProductDescriptionService.deleteByProductId(storeCombination.getId(),Constants.PRODUCT_TYPE_PINGTUAN);
            boolean saveDesc = storeProductDescriptionService.save(spd);
            if (!saveDesc) throw new CrmebException("编辑拼团商品详情失败");
        }
        return update;
    }

    /**
     * 拼团商品详情
     * @param id    拼团商品ID
     * @return
     */
    @Override
    public StoreProductResponse getAdminDetail(Integer id) {
        StoreCombination storeCombination = dao.selectById(id);
        if (ObjectUtil.isNull(storeCombination)) throw new CrmebException("未找到对应商品信息");
        StoreProductResponse storeProductResponse = new StoreProductResponse();
        BeanUtils.copyProperties(storeCombination, storeProductResponse);
        storeProductResponse.setStartTimeStr(DateUtil.timestamp2DateStr(storeCombination.getStartTime(), Constants.DATE_FORMAT_DATE));
        storeProductResponse.setStopTimeStr(DateUtil.timestamp2DateStr(storeCombination.getStopTime(), Constants.DATE_FORMAT_DATE));


        // 查询attr
        StoreProductAttr spaPram = new StoreProductAttr();
        spaPram.setProductId(id).setType(Constants.PRODUCT_TYPE_PINGTUAN);
        List<StoreProductAttr> attrs = storeProductAttrService.getByEntity(spaPram);
        storeProductResponse.setAttr(attrs);
        storeProductResponse.setSliderImage(String.join(",",storeCombination.getImages()));

        boolean specType = false;
        if (attrs.size() > 1) {
            specType = true;
        }
        storeProductResponse.setSpecType(specType);

        // 拼装数据
        StoreProductAttrValue spavPramCombination = new StoreProductAttrValue();
        spavPramCombination.setProductId(id).setType(Constants.PRODUCT_TYPE_PINGTUAN);
        List<StoreProductAttrValue> storeProductAttrValuesCombination = storeProductAttrValueService.getByEntity(spavPramCombination);
        List<HashMap<String, Object>> attrValuesCombination = genratorSkuInfo(id, storeCombination, storeProductAttrValuesCombination, Constants.PRODUCT_TYPE_PINGTUAN, specType);

        // 查询attrValue
        StoreProductAttrValue spavPramProduct = new StoreProductAttrValue();
        spavPramProduct.setProductId(storeCombination.getProductId()).setType(Constants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> storeProductAttrValuesProduct = storeProductAttrValueService.getByEntity(spavPramProduct);
        List<HashMap<String, Object>> attrValuesProduct = genratorSkuInfo(storeCombination.getProductId(),storeCombination, storeProductAttrValuesProduct, Constants.PRODUCT_TYPE_NORMAL, specType);

        // H5 端用于生成skuList
        List<StoreProductAttrValueResponse> sPAVResponses = new ArrayList<>();

        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValuesCombination) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue,atr);
            // 单规格拼团限量数据处理
            atr.setQuota(storeProductResponse.getQuota());
            atr.setChecked(true);
            sPAVResponses.add(atr);
        }

        for (int k = 0; k < attrValuesProduct.size(); k++) {
            for (int i = 0; i < attrValuesCombination.size(); i++) {
                HashMap<String, Object> skill = attrValuesCombination.get(i);
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
                        .eq(StoreProductDescription::getProductId, id)
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_PINGTUAN));
        if(null != sd){
            storeProductResponse.setContent(null == sd.getDescription()?"":sd.getDescription());
        }
        return storeProductResponse;
    }

    /**
     * 修改拼团商品状态
     * @param id
     * @param isShow
     * @return
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
     * @return
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
            map.put("countPeople", pinkList.size());
            long countTeam = pinkList.stream().filter(i -> i.getStatus() == 2).count();
            map.put("countTeam", countTeam);
        }
        return map;
    }

    /**
     * H5拼团商品列表
     * @param pageParamRequest
     * @return
     */
    @Override
    public PageInfo<StoreCombination> getH5List(PageParamRequest pageParamRequest) {
        Page<StoreCombination> combinationPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCombination::getIsDel, false);
        lqw.eq(StoreCombination::getIsShow, true);
        long millis = System.currentTimeMillis();
        lqw.le(StoreCombination::getStartTime, millis);
        lqw.ge(StoreCombination::getStopTime, millis);
        lqw.orderByDesc(StoreCombination::getSort, StoreCombination::getId);
        List<StoreCombination> combinationList = dao.selectList(lqw);
        if (CollUtil.isEmpty(combinationList)) {
            return CommonPage.copyPageInfo(combinationPage, CollUtil.newArrayList());
        }
        return CommonPage.copyPageInfo(combinationPage, combinationList);
    }

    /**
     * H5 拼团Pink
     * @return
     */
    @Override
    public HashMap<String, Object> getForH5Pink() {
        return null;
    }

    /**
     * H5拼团商品详情
     * @param comId    拼团商品编号
     * pindAll  拼团团长ID列表
     * pink     拼团列表（团长列表）
     * pink_ok_list 拼团列表（成功拼团团长列表）
     * pink_ok_sum 拼团完成的商品总件数
     * reply 评论列表
     * replyChance 好评率
     * replyCount 评论数量
     * @return
     */
    @Override
    public CombinationDetailResponse getH5Detail(Integer comId) {
        StoreCombination storeCombination = getById(comId);
        if (ObjectUtil.isNull(storeCombination) || storeCombination.getIsDel()) {
            throw new CrmebException("对应拼团商品不存在");
        }
        StoreCombinationInfoResponse infoResponse = new StoreCombinationInfoResponse();
        BeanUtils.copyProperties(storeCombination, infoResponse);
        // 设置点赞和收藏
        User user = userService.getInfo();
        if(ObjectUtil.isNotNull(user) && ObjectUtil.isNotNull(user.getUid())){
            infoResponse.setUserLike(storeProductRelationService.getLikeOrCollectByUser(user.getUid(), storeCombination.getProductId(),true).size() > 0);
            infoResponse.setUserCollect(storeProductRelationService.getLikeOrCollectByUser(user.getUid(), storeCombination.getProductId(),false).size() > 0);
        }else{
            infoResponse.setUserLike(false);
            infoResponse.setUserCollect(false);
        }
        StoreProduct product = storeProductService.getById(storeCombination.getProductId());
        infoResponse.setProductPrice(product.getPrice());
        infoResponse.setTotal(product.getSales() + product.getFicti());
        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, comId)
                        .eq(StoreProductDescription::getType, Constants.PRODUCT_TYPE_PINGTUAN));
        if(null != sd){
            infoResponse.setContent(null == sd.getDescription() ? "" : sd.getDescription());
        }

        CombinationDetailResponse detailResponse = new CombinationDetailResponse();
        detailResponse.setStoreInfo(infoResponse);
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
                //拼团完成的商品总件数
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
            // 拼团团长ID列表
            List<Integer> pindAll = pinkingList.stream().map(StorePinkResponse::getId).collect(Collectors.toList());
            // 获取还剩几人成团
            pinkingList.forEach(i -> {
                Integer countPeople = storePinkService.getCountByKid(i.getId());
                i.setCountPeople(countPeople);
                i.setCount(i.getPeople() - countPeople);
            });

            detailResponse.setPindAll(pindAll);

            // 所有团长列表
            detailResponse.setPink(pinkingList);
        }

        // 评论部分
        Integer replyChance = 100;
        Integer replyCount = 0;
        // 获取商品所有评论列表
        List<StoreProductReply> replyList = storeProductReplyService.getAllByPidAndType(storeCombination.getProductId(), Constants.STORE_REPLY_TYPE_PINTUAN);
        if (CollUtil.isNotEmpty(replyList)) {
            replyCount = replyList.size();
            // 好评列表
            List<StoreProductReply> goodReplyList = replyList.stream().filter(i -> i.getProductScore().equals(4) || i.getProductScore().equals(5)).collect(Collectors.toList());
            if (CollUtil.isEmpty(goodReplyList)) {
                replyChance = 0;
            } else {
                replyChance = (goodReplyList.size() / replyCount) * 100;
            }
        }
        detailResponse.setReply(replyList);
        detailResponse.setReplyChance(replyChance);
        detailResponse.setReplyCount(replyCount);

        // sku部分
        detailResponse.setSpecType(false);
        StoreProductAttr spavAttr = new StoreProductAttr();
        spavAttr.setProductId(storeCombination.getId());
        spavAttr.setType(Constants.PRODUCT_TYPE_PINGTUAN);
        List<StoreProductAttr> attrList = storeProductAttrService.getByEntity(spavAttr);
//        detailResponse.setProductAttr(attrList);
        setSkuAttr(attrList, detailResponse);
        if (CollUtil.isNotEmpty(attrList) && attrList.size() > 1) {
            detailResponse.setSpecType(true);
        }

        // 单属性时讲attrValueId 赋值给外层方便前端使用
        if(!detailResponse.getSpecType()){
            detailResponse.setAloneAttrValueId(attrList.get(0).getId());
        }

        StoreProductAttrValue spavValue = new StoreProductAttrValue();
        spavValue.setProductId(storeCombination.getId());
        spavValue.setType(Constants.PRODUCT_TYPE_PINGTUAN);
        List<StoreProductAttrValue> valueList = storeProductAttrValueService.getByEntity(spavValue);
//        List<HashMap<String, Object>> attrValuesCombination = genratorSkuInfo(storeCombination.getId(), storeCombination, valueList, Constants.PRODUCT_TYPE_PINGTUAN);

        // H5 端用于生成skuList
        List<StoreProductAttrValueResponse> sPAVResponses = new ArrayList<>();

        for (StoreProductAttrValue storeProductAttrValue : valueList) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue, atr);
            sPAVResponses.add(atr);
        }
        HashMap<String,Object> skuMap = new HashMap<>();
        for (StoreProductAttrValueResponse attrValue : sPAVResponses) {
            skuMap.put(attrValue.getSuk(), attrValue);
        }
        detailResponse.setProductValue(skuMap);

        return detailResponse;
    }

    /**
     * 去拼团
     * @param pinkId 拼团团长单ID
     * @return
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
        User user = userService.getInfo();

        GoPinkResponse goPinkResponse = new GoPinkResponse();
        List<StorePink> pinkList = storePinkService.getListByCidAndKid(teamPink.getCid(), teamPink.getId());
        //拼团剩余人数
        int count = teamPink.getPeople() - 1 - Optional.ofNullable(pinkList.size()).orElse(0);

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
        List<StorePinkResponse> pinkResponseList = pinkList.stream().map(i -> {
            StorePinkResponse storePinkResponse = new StorePinkResponse();
            BeanUtils.copyProperties(i, storePinkResponse);
            User userOne = userService.getById(i.getUid());
            storePinkResponse.setNickname(userOne.getNickname());
            storePinkResponse.setAvatar(userOne.getAvatar());
            return storePinkResponse;
        }).collect(Collectors.toList());
        // 团长
        StorePinkResponse storePinkResponse = new StorePinkResponse();
        BeanUtils.copyProperties(teamPink, storePinkResponse);
        if (teamPink.getUid().equals(user.getUid())) {
            storePinkResponse.setNickname(user.getNickname());
            storePinkResponse.setAvatar(user.getAvatar());
        } else {
            User teamUser = userService.getById(teamPink.getUid());
            storePinkResponse.setNickname(teamUser.getNickname());
            storePinkResponse.setAvatar(teamUser.getAvatar());
        }

        goPinkResponse.setCount(count);
        goPinkResponse.setCurrentPinkOrder(teamPink.getOrderId());
        goPinkResponse.setIsOk(isOk);
        goPinkResponse.setPinkBool(pinkBool);
        goPinkResponse.setUserBool(userBool);
        goPinkResponse.setPinkAll(pinkResponseList);
        goPinkResponse.setPinkT(storePinkResponse);
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
        if(!detailResponse.getSpecType()){
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
        HashMap<String,Object> skuMap = new HashMap<>();
        for (StoreProductAttrValueResponse attrValue : sPAVResponses) {
            skuMap.put(attrValue.getSuk(), attrValue);
        }

        detailResponse.setProductValue(skuMap);
        goPinkResponse.setStoreCombination(detailResponse);

        return goPinkResponse;
    }

    /**
     * 更多拼团信息
     * @param pageParamRequest
     * @return
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
     * @param storePinkRequest
     * @return
     * 此处只是转为申请退款订单
     * 自己是团长，取消后，顺位第一人变为团长
     * 自己不是团长，直接取消
     */
    @Override
    public Boolean removePink(StorePinkRequest storePinkRequest) {
        StorePink userPink = storePinkService.getById(storePinkRequest.getId());
        if (ObjectUtil.isNull(userPink)) throw new CrmebException("未查到拼团信息，无法取消");
        if (userPink.getIsRefund()) throw new CrmebException("拼团已取消");
        // 获取是否拼团成功
        if (userPink.getStatus() == 2) {
            throw new CrmebException("拼团已完成，无法取消");
        }
        Integer count = storePinkService.getCountByKid(userPink.getKId());
        if (count.equals(userPink.getPeople())) {
            // 拼团完成操作
            storePinkService.pinkSuccess(userPink.getId());
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
                pinkList.forEach(i -> {
                    i.setKId(newHeadPink.getId());
                });
                pinkList.add(newHeadPink);
                storePinkService.updateBatchById(pinkList);
            }
        }

        return true;
    }

    /**
     * 条件查询
     * @param storeCombination
     * @return
     */
    @Override
    public List<StoreCombination> getByEntity(StoreCombination storeCombination) {
        LambdaQueryWrapper<StoreCombination> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeCombination);
        return dao.selectList(lqw);
    }

    /**
     * 扣减库存加销量
     * @param combinationId 产品id
     * @param num 商品数量
     * @param attrValueId
     * @param type 是否限购 0=不限购
     * @return
     */
    @Override
    public Boolean decProductStock(Integer combinationId, Integer num, int attrValueId, int type) {
        // 因为attrvalue表中unique使用Id代替，更新前先查询此表是否存在

        // 秒杀SKU 扣减库存加销量
        StoreProductAttrValue spavPram = new StoreProductAttrValue();
        spavPram.setProductId(combinationId).setType(type).setId(attrValueId);
        List<StoreProductAttrValue> existAttrValues = storeProductAttrValueService.getByEntity(spavPram);
        if(CollUtil.isEmpty(existAttrValues)) throw new CrmebException("未找到相关商品属性信息");

        StoreProductAttrValue productsInAttrValue = existAttrValues.get(0);
        StoreCombination storeCombination = getById(combinationId);
        boolean result = false;
        if(ObjectUtil.isNotNull(productsInAttrValue)){
            boolean resultDecProductStock = storeProductAttrValueService.decProductAttrStock(combinationId, attrValueId, num, type);
            if(!resultDecProductStock) throw new CrmebException("扣减拼团sku库存失败");
        }

        // 拼团商品表扣减库存加销量
        LambdaUpdateWrapper<StoreCombination> lqwuper = new LambdaUpdateWrapper<>();
        lqwuper.eq(StoreCombination::getId, combinationId);
        lqwuper.set(StoreCombination::getStock, storeCombination.getStock()-num);
        lqwuper.set(StoreCombination::getSales, storeCombination.getSales()+num);
        lqwuper.set(StoreCombination::getQuota, storeCombination.getQuota()-num);
        result = update(lqwuper);
        return result;
    }

    /**
     * 添加库存
     * @param request
     * @return
     */
    @Override
    public Boolean stockAddRedis(StoreProductStockRequest request) {
        String _productString = JSON.toJSONString(request);
        redisUtil.lPush(Constants.PRODUCT_COMBINATION_STOCK_UPDATE, _productString);
        return true;
    }

    @Override
    public void consumeProductStock() {
        String redisKey = Constants.PRODUCT_COMBINATION_STOCK_UPDATE;
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
     * 拼团海报
     * @param pinkId 拼团id
     * @param from
     * @return
     * TODO 目前由前端生成
     */
    @Override
    public Boolean poster(Integer pinkId, String from) {
        return null;
    }

    /**
     * 获取当前时间的拼团商品
     * @param productId
     * @return
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
     * @param productId 商品编号
     * @return
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
     * 拼团操作库存
     * @param storeProductStockRequest
     * @return
     */
    private boolean doProductStock(StoreProductStockRequest storeProductStockRequest) {
        // 砍价商品信息回滚
        StoreCombination existCombination = getById(storeProductStockRequest.getCombinationId());
        List<StoreProductAttrValue> existAttr =
                storeProductAttrValueService.getListByProductIdAndAttrId(
                        storeProductStockRequest.getCombinationId(),
                        storeProductStockRequest.getAttrId().toString(),
                        storeProductStockRequest.getType());
        if(ObjectUtil.isNull(existCombination) || ObjectUtil.isNull(existAttr)){ // 未找到商品
            logger.info("库存修改任务未获取到商品信息"+JSON.toJSONString(storeProductStockRequest));
            return true;
        }

        // 回滚商品库存/销量 并更新
        boolean isPlus = storeProductStockRequest.getOperationType().equals("add");
        int productStock = isPlus ? existCombination.getStock() + storeProductStockRequest.getNum() : existCombination.getStock() - storeProductStockRequest.getNum();
        existCombination.setStock(productStock);
        existCombination.setSales(existCombination.getSales() - storeProductStockRequest.getNum());
        existCombination.setQuota(existCombination.getQuota() + storeProductStockRequest.getNum());
        updateById(existCombination);

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
     * 根据配置生成sku配置信息
     * @param id    商品id
     * @param storeCombination  拼团商品信息
     * @param storeProductAttrValues     属性信息
     * @param productType   拼团和正常数据
     * @return
     */
    private List<HashMap<String, Object>> genratorSkuInfo(Integer id, StoreCombination storeCombination, List<StoreProductAttrValue> storeProductAttrValues, int productType, boolean specType) {
        List<HashMap<String, Object>> attrValues = new ArrayList<>();
        if (specType) {

            StoreProductAttrResult sparPram = new StoreProductAttrResult();
            sparPram.setProductId(id).setType(productType);
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

    /**
     * 过滤没被选中的数据，并校验参数
     * @param request
     */
    private void clearNotCheckedAndValidationPrice(StoreCombinationRequest request) {
        if(request.getSpecType()){
            request.setAttrValue(request.getAttrValue().stream().filter(StoreProductAttrValueRequest::getChecked).collect(Collectors.toList()));
        }
        if (CollUtil.isEmpty(request.getAttrValue())) {
            throw new CrmebException("请选择 规格");
        }
        for (StoreProductAttrValueRequest attr : request.getAttrValue()) {
            if (ObjectUtil.isNull(attr.getPrice()) || attr.getPrice().compareTo(BigDecimal.ZERO) <= 0){
                throw new CrmebException("请正确输入 拼团价格");
            }
            if (ObjectUtil.isNull(attr.getQuota()) || attr.getQuota() <= 0) {
                throw new CrmebException("请正确输入 限量");
            }
            if (attr.getQuota() > attr.getStock()) {
                throw new CrmebException("限量 不能大于库存");
            }

        }
    }

    /**
     * 设置制式结构给attr属性
     * @param attrList
     * @param detailResponse
     */
    private void setSkuAttr(List<StoreProductAttr> attrList, CombinationDetailResponse detailResponse) {
        List<HashMap<String,Object>> attrMapList = new ArrayList<>();
        for (StoreProductAttr attr : attrList) {
            HashMap<String, Object> attrMap = new HashMap<>();
            attrMap.put("productId",attr.getProductId());
            attrMap.put("attrName",attr.getAttrName());
//            attrMap.put("type",attr.getType());
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

            List<HashMap<String,Object>> attrValueMapList = new ArrayList<>();
            for (String attrValue : attrValues) {
                HashMap<String,Object> attrValueMap = new HashMap<>();
                attrValueMap.put("attr",attrValue);
                attrValueMapList.add(attrValueMap);
            }
            attrMap.put("attrValue",attrValueMapList);
            attrMapList.add(attrMap);
        }
        detailResponse.setProductAttr(attrMapList);
    }

    /**
     * 获取制式结构给attr属性
     * @param attrList
     * @return
     */
    private List<HashMap<String, Object>> getSkuAttrList(List<StoreProductAttr> attrList) {
        List<HashMap<String,Object>> attrMapList = new ArrayList<>();
        if (CollUtil.isEmpty(attrList)) {
            return attrMapList;
        }
        for (StoreProductAttr attr : attrList) {
            HashMap<String, Object> attrMap = new HashMap<>();
            attrMap.put("productId",attr.getProductId());
            attrMap.put("attrName",attr.getAttrName());
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

            List<HashMap<String,Object>> attrValueMapList = new ArrayList<>();
            for (String attrValue : attrValues) {
                HashMap<String,Object> attrValueMap = new HashMap<>();
                attrValueMap.put("attr",attrValue);
                attrValueMapList.add(attrValueMap);
            }
            attrMap.put("attrValue",attrValueMapList);
            attrMapList.add(attrMap);
        }
        return attrMapList;
    }
}

