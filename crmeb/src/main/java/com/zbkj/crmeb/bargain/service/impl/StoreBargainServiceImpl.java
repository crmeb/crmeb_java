package com.zbkj.crmeb.bargain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.BargainConstants;
import com.constants.Constants;
import com.constants.ProductConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.bargain.dao.StoreBargainDao;
import com.zbkj.crmeb.bargain.model.StoreBargain;
import com.zbkj.crmeb.bargain.model.StoreBargainUser;
import com.zbkj.crmeb.bargain.request.StoreBargainRequest;
import com.zbkj.crmeb.bargain.request.StoreBargainSearchRequest;
import com.zbkj.crmeb.bargain.response.StoreBargainResponse;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserHelpService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserService;
import com.zbkj.crmeb.front.request.BargainFrontRequest;
import com.zbkj.crmeb.front.response.BargainDetailResponse;
import com.zbkj.crmeb.store.model.*;
import com.zbkj.crmeb.store.request.StoreProductAttrValueRequest;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.response.StoreProductAttrValueResponse;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StoreBargainService 实现类
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
public class StoreBargainServiceImpl extends ServiceImpl<StoreBargainDao, StoreBargain> implements StoreBargainService {

    @Resource
    private StoreBargainDao dao;

    @Autowired
    private StoreBargainUserService storeBargainUserService;

    @Autowired
    private StoreBargainUserHelpService storeBargainUserHelpService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private StoreProductAttrService attrService;

    @Autowired
    private StoreProductAttrValueService attrValueService;

    @Autowired
    private StoreProductAttrResultService storeProductAttrResultService;

    @Autowired
    private StoreProductDescriptionService storeProductDescriptionService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final Logger logger = LoggerFactory.getLogger(StoreBargainServiceImpl.class);

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author HZW
    * @since 2020-11-06
    * @return List<StoreBargain>
    */
    @Override
    public PageInfo<StoreBargainResponse> getList(StoreBargainSearchRequest request, PageParamRequest pageParamRequest) {
        Page<StoreBargain> storeBargainPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreBargain> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreBargain::getIsDel, false);
        if (StrUtil.isNotEmpty(request.getKeywords())) {
            lambdaQueryWrapper.and(i -> i.like(StoreBargain::getId, request.getKeywords())
                    .or().like(StoreBargain::getStoreName, request.getKeywords())
                    .or().like(StoreBargain::getTitle, request.getKeywords()));
        }
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lambdaQueryWrapper.eq(StoreBargain::getStatus, request.getStatus());
        }
        lambdaQueryWrapper.orderByDesc(StoreBargain::getSort, StoreBargain::getId);
        List<StoreBargain> storeBargainList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(storeBargainList)) {
            return CommonPage.copyPageInfo(storeBargainPage, CollUtil.newArrayList());
        }
        // 1.砍价参与人数，2.帮忙砍价人数，3.砍价成功人数
        List<StoreBargainResponse> storeProductResponses = CollUtil.newArrayList();
        for (StoreBargain storeBargain : storeBargainList) {
            StoreBargainResponse storeBargainResponse = new StoreBargainResponse();
            BeanUtils.copyProperties(storeBargain, storeBargainResponse);
            storeBargainResponse.setStartTime(DateUtil.timestamp2DateStr(storeBargain.getStartTime(), Constants.DATE_FORMAT_DATE));
            storeBargainResponse.setStopTime(DateUtil.timestamp2DateStr(storeBargain.getStopTime(), Constants.DATE_FORMAT_DATE));
            storeBargainResponse.setAddTime(DateUtil.timestamp2DateStr(storeBargain.getAddTime(), Constants.DATE_FORMAT));
            List<StoreBargainUser> bargainUserList = storeBargainUserService.getListByBargainId(storeBargain.getId());
            if (CollUtil.isEmpty(bargainUserList)) {
                storeBargainResponse.setCountPeopleAll(0L);
                storeBargainResponse.setCountPeopleHelp(0L);
                storeBargainResponse.setCountPeopleSuccess(0L);
                //限量剩余
                storeBargainResponse.setSurplusQuota(storeBargain.getQuota());
                storeProductResponses.add(storeBargainResponse);
                continue ;
            }
            //砍价参与人数
            Integer countPeopleAll = bargainUserList.size();
            //砍价成功人数
            Long countPeopleSuccess = bargainUserList.stream()
                    .filter(o -> o.getStatus().equals(BargainConstants.BARGAIN_USER_STATUS_SUCCESS)).count();
            //帮忙砍价人数
            Long countPeopleHelp = storeBargainUserHelpService.getHelpCountByBargainId(storeBargain.getId());
            storeBargainResponse.setCountPeopleAll(countPeopleAll.longValue());
            storeBargainResponse.setCountPeopleHelp(countPeopleHelp);
            storeBargainResponse.setCountPeopleSuccess(countPeopleSuccess);
            //限量剩余
            storeBargainResponse.setSurplusQuota(storeBargain.getQuota());
            storeProductResponses.add(storeBargainResponse);
        }
        return CommonPage.copyPageInfo(storeBargainPage, storeProductResponses);
    }

    /**
     * 删除砍价商品
     * @param id
     * @return
     */
    @Override
    public boolean deleteById(Integer id) {
        StoreBargain existBargain = getById(id);
        long timeMillis = System.currentTimeMillis();
        if (existBargain.getStatus().equals(true) && existBargain.getStartTime() <= timeMillis && timeMillis <= existBargain.getStopTime()) {
            throw new CrmebException("活动开启中，商品不支持删除");
        }

        StoreBargain storeBargain = new StoreBargain();
        storeBargain.setId(id).setIsDel(true);
        return dao.updateById(storeBargain) > 0;
    }

    /**
     * 新增砍价商品
     * @param request   砍价商品result
     * @return 新增结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBargain(StoreBargainRequest request) {
        // 参数校验
        if (null == request.getAttrValue() || request.getAttrValue().size() < 1) {
            throw new CrmebException("请选择砍价商品的规格属性");
        }
        StoreProductAttrValueRequest attrValueRequest = request.getAttrValue().get(0);
        if (ObjectUtil.isNull(attrValueRequest.getQuota()) || attrValueRequest.getQuota() <= 0) {
            throw new CrmebException("活动限购数量必须大于0");
        }

        StoreBargain bargain = new StoreBargain();
        BeanUtils.copyProperties(request, bargain);
        bargain.setId(null);
        // 头图、轮播图
        bargain.setImage(systemAttachmentService.clearPrefix(request.getImage()));
        bargain.setImages(systemAttachmentService.clearPrefix(request.getImages()));
        // 活动开始结束时间
        bargain.setStartTime(DateUtil.dateStr2Timestamp(request.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN));
        bargain.setStopTime(DateUtil.dateStr2Timestamp(request.getStopTime(), Constants.DATE_TIME_TYPE_END));
        bargain.setAddTime(System.currentTimeMillis());
        bargain.setStoreName(request.getProName());
        // 砍价商品价格
        bargain.setPrice(attrValueRequest.getPrice());
        bargain.setMinPrice(attrValueRequest.getMinPrice());
        bargain.setCost(attrValueRequest.getCost());
        bargain.setStock(attrValueRequest.getStock());
        bargain.setQuota(attrValueRequest.getQuota());
        bargain.setIsDel(false);
        bargain.setQuotaShow(bargain.getQuota());
        bargain.setSales(0);
        boolean save = save(bargain);
        if (!save) throw new CrmebException("新增砍价商品失败");

        // 处理商品属性，给砍价商品一份单规格默认属性
        StoreProductAttr singleAttr = new StoreProductAttr();
        singleAttr.setProductId(bargain.getId()).setAttrName(ProductConstants.SINGLE_ATTR_NAME)
                .setAttrValues(ProductConstants.SINGLE_ATTR_VALUE).setType(ProductConstants.PRODUCT_TYPE_BARGAIN);
        boolean attrAddResult = attrService.save(singleAttr);
        if (!attrAddResult) throw new CrmebException("新增属性名失败");

        // 处理商品属性值，砍价商品只有一条属性值
        StoreProductAttrValue singleAttrValue = new StoreProductAttrValue();
        BeanUtils.copyProperties(attrValueRequest, singleAttrValue);
        singleAttrValue.setProductId(bargain.getId()).setType(ProductConstants.PRODUCT_TYPE_BARGAIN);
        singleAttrValue.setImage(systemAttachmentService.clearPrefix(singleAttrValue.getImage()));
        boolean saveAttrValue = attrValueService.save(singleAttrValue);
        if(!saveAttrValue) throw new CrmebException("新增属性详情失败");

        // 处理商品属性值result
        StoreProductAttrResult attrResult = new StoreProductAttrResult(
                0,
                bargain.getId(),
                systemAttachmentService.clearPrefix(JSON.toJSONString(request.getAttrValue())),
                DateUtil.getNowTime(),ProductConstants.PRODUCT_TYPE_BARGAIN);
        boolean saveResult = storeProductAttrResultService.save(attrResult);
        if(!saveResult) throw new CrmebException("新增商品属性详情详情失败");

        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription(
                bargain.getId(),  request.getContent().length() > 0
                ? systemAttachmentService.clearPrefix(request.getContent()) : "" , ProductConstants.PRODUCT_TYPE_BARGAIN);
        storeProductDescriptionService.deleteByProductId(spd.getProductId(), ProductConstants.PRODUCT_TYPE_BARGAIN);
        boolean saveDesc = storeProductDescriptionService.save(spd);
        if (!saveDesc) throw new CrmebException("新增商品富文本失败");

        return save;
    }

    /**
     * 编辑砍价商品
     * @param request
     * @return
     */
    @Override
    public boolean updateBarhain(StoreBargainRequest request) {
        StoreBargain existBargain = getById(request.getId());
        long timeMillis = System.currentTimeMillis();
        if (existBargain.getStatus().equals(true) && existBargain.getStartTime() <= timeMillis && timeMillis <= existBargain.getStopTime()) {
            throw new CrmebException("活动开启中，商品不支持修改");
        }

        if (null == request.getAttrValue() || request.getAttrValue().size() < 1) {
            throw new CrmebException("请选择砍价商品的规格属性");
        }
        StoreProductAttrValueRequest attrValueRequest = request.getAttrValue().get(0);

        StoreBargain bargain = new StoreBargain();
        BeanUtils.copyProperties(request, bargain);
        // 头图、轮播图
        bargain.setImage(systemAttachmentService.clearPrefix(request.getImage()));
        bargain.setImages(systemAttachmentService.clearPrefix(request.getImages()));
        // 活动开始结束时间
        bargain.setStartTime(DateUtil.dateStr2Timestamp(request.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN));
        bargain.setStopTime(DateUtil.dateStr2Timestamp(request.getStopTime(), Constants.DATE_TIME_TYPE_END));
        bargain.setStoreName(request.getProName());
        // 砍价商品价格
        bargain.setPrice(attrValueRequest.getPrice());
        bargain.setMinPrice(attrValueRequest.getMinPrice());
        bargain.setCost(attrValueRequest.getCost());
        bargain.setStock(attrValueRequest.getStock());
        bargain.setQuota(attrValueRequest.getQuota());
        bargain.setQuotaShow(attrValueRequest.getQuota());
        int saveCount = dao.updateById(bargain);
        if (saveCount <= 0) {
            throw new CrmebException("编辑砍价商品失败");
        }

        // 砍价商品属性attr不用变化，attrValue删除原有的替换为现有的
        attrValueService.removeByProductId(request.getId(), ProductConstants.PRODUCT_TYPE_BARGAIN);
        StoreProductAttrValue singleAttrValue = new StoreProductAttrValue();
        BeanUtils.copyProperties(attrValueRequest, singleAttrValue);
        singleAttrValue.setProductId(bargain.getId()).setType(ProductConstants.PRODUCT_TYPE_BARGAIN);
        singleAttrValue.setImage(systemAttachmentService.clearPrefix(singleAttrValue.getImage()));
        boolean saveAttrValue = attrValueService.save(singleAttrValue);
        if(!saveAttrValue) throw new CrmebException("编辑属性详情失败");

        // attrResult整存整取，不做更新
        storeProductAttrResultService.deleteByProductId(bargain.getId(),ProductConstants.PRODUCT_TYPE_BARGAIN);
        StoreProductAttrResult attrResult = new StoreProductAttrResult(
                0,
                bargain.getId(),
                systemAttachmentService.clearPrefix(JSON.toJSONString(request.getAttrValue())),
                DateUtil.getNowTime(),ProductConstants.PRODUCT_TYPE_BARGAIN);
        storeProductAttrResultService.save(attrResult);

        return saveCount > 0;
    }

    /**
     * 更新砍价商品状态
     * @param id 砍价商品id
     * @param status 砍价商品状态
     * @return Boolean
     */
    @Override
    public boolean updateBargainStatus(Integer id, boolean status) {
        StoreBargain temp = getById(id);
        if (ObjectUtil.isNull(temp) || temp.getIsDel()) {
            throw new CrmebException("砍价商品不存在");
        }
        if (status) {
            // 判断商品是否存在
            StoreProduct product = storeProductService.getById(temp.getProductId());
            if (ObjectUtil.isNull(product)) {
                throw new CrmebException("关联的商品已删除，无法开启活动");
            }
        }

        StoreBargain storeBargain = new StoreBargain();
        storeBargain.setId(id).setStatus(status);
        return dao.updateById(storeBargain) > 0;
    }

    /**
     * 获取砍价商品详情
     * @param bargainId 砍价商品id
     * @return
     */
    @Override
    public StoreProductResponse getAdminDetail(Integer bargainId) {
        StoreBargain storeBargain = dao.selectById(bargainId);
        if (ObjectUtil.isNull(storeBargain)) throw new CrmebException("未找到对应砍价商品信息");
        StoreProductResponse storeProductResponse = new StoreProductResponse();
        BeanUtils.copyProperties(storeBargain, storeProductResponse);
        storeProductResponse.setStartTime(new Date(storeBargain.getStartTime()));
        storeProductResponse.setStopTime(new Date(storeBargain.getStopTime()));
        storeProductResponse.setStatus(storeBargain.getStatus().equals(true) ? 1 : 0);

        // 查询attr
        StoreProductAttr spaPram = new StoreProductAttr();
        spaPram.setProductId(storeBargain.getProductId() ).setType(ProductConstants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttr> attrs = attrService.getByEntity(spaPram);
        storeProductResponse.setAttr(attrs);
        storeProductResponse.setSliderImage(String.join(",",storeBargain.getImages()));

        boolean specType = false;
        if (attrs.size() > 1) {
            specType = true;
        }
        storeProductResponse.setSpecType(specType);

        // 注意：数据瓶装步骤：分别查询砍价和商品本身信息组装sku信息之后，再对比sku属性是否相等来赋值是否砍价sku信息
        StoreProductAttrValue spavPramBargain = new StoreProductAttrValue();
        spavPramBargain.setProductId(bargainId).setType(ProductConstants.PRODUCT_TYPE_BARGAIN);
        List<StoreProductAttrValue> storeProductAttrValuesBargain = attrValueService.getByEntity(spavPramBargain);
        List<HashMap<String, Object>> attrValuesBargain = genratorSkuInfo(bargainId, specType, storeBargain, storeProductAttrValuesBargain, ProductConstants.PRODUCT_TYPE_BARGAIN);

        // 查询attrValue
        StoreProductAttrValue spavPramProduct = new StoreProductAttrValue();
        spavPramProduct.setProductId(storeBargain.getProductId()).setType(ProductConstants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttrValue> storeProductAttrValuesProduct = attrValueService.getByEntity(spavPramProduct);
        List<HashMap<String, Object>> attrValuesProduct = genratorSkuInfo(storeBargain.getProductId(), specType, storeBargain, storeProductAttrValuesProduct, ProductConstants.PRODUCT_TYPE_NORMAL);

        // H5 端用于生成skuList
        List<StoreProductAttrValueResponse> sPAVResponses = new ArrayList<>();

        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValuesBargain) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue, atr);
            // 单规格秒杀限量数据处理
            atr.setQuota(storeProductResponse.getQuota());
            atr.setMinPrice(storeBargain.getMinPrice());
            atr.setChecked(true);
            sPAVResponses.add(atr);
        }

        for (int k = 0; k < attrValuesProduct.size(); k++) {
            for (int i = 0; i < attrValuesBargain.size(); i++) {
                HashMap<String, Object> bargainMap = attrValuesBargain.get(i);
                HashMap<String, Object> productMap = attrValuesProduct.get(k);
                productMap.put("checked", false);
                productMap.put("quota", productMap.get("stock"));
                productMap.put("price", productMap.get("price"));
                if(bargainMap.get("suk").equals(productMap.get("suk"))){
                    productMap.put("checked", true);
                    productMap.put("quota", bargainMap.get("quota"));
                    productMap.put("price",bargainMap.get("price"));
                    break;
                }
            }
        }

        storeProductResponse.setAttrValues(attrValuesProduct);
        storeProductResponse.setAttrValue(sPAVResponses);

        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, bargainId)
                        .eq(StoreProductDescription::getType, ProductConstants.PRODUCT_TYPE_BARGAIN));
        if(null != sd){
            storeProductResponse.setContent(StrUtil.isBlank(sd.getDescription()) ? "" : sd.getDescription());
        }
        if (StrUtil.isNotBlank(storeProductResponse.getRule())) {
            storeProductResponse.setRule(systemAttachmentService.clearPrefix(storeBargain.getRule()));
        }
        return storeProductResponse;
    }

    /**
     * h5 获取砍价商品列表
     * @param pageParamRequest
     * @return
     */
    @Override
    public PageInfo<StoreBargainResponse> getH5List(PageParamRequest pageParamRequest) {
        Page<StoreBargain> storeBargainPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper();
        lqw.eq(StoreBargain::getStatus, true);
        lqw.eq(StoreBargain::getIsDel, false);
        long timeMillis = System.currentTimeMillis();
        lqw.le(StoreBargain::getStartTime, timeMillis);
        lqw.ge(StoreBargain::getStopTime, timeMillis);
        lqw.orderByDesc(StoreBargain::getSort, StoreBargain::getId);
        List<StoreBargain> storeBargains = dao.selectList(lqw);
        List<StoreBargainResponse> bargainResponseList = storeBargains.stream().map(bargain -> {
            StoreBargainResponse storeBargainResponse = new StoreBargainResponse();
            Long countByBargainId = storeBargainUserService.getCountByBargainId(bargain.getId());
            BeanUtils.copyProperties(bargain, storeBargainResponse);
            storeBargainResponse.setCountPeopleAll(countByBargainId);
            return storeBargainResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(storeBargainPage, bargainResponseList);
    }

    /**
     * 获取查看、分享、参与人数
     * 这里目前是获取总的数据，以后还是要改成获取商品的数据
     * @return
     */
    @Override
    public Map<String, Object> getH5Share(Integer bargainId) {
        QueryWrapper<StoreBargain> qw = new QueryWrapper<>();
        qw.select("ifnull(sum(look), 0) as lookCount", "ifnull(sum(share), 0) as shareCount");
        qw.eq("is_del", false);
        Map<String, Object> map = getMap(qw);
        Integer count = storeBargainUserHelpService.count();
        map.put("userCount", count.longValue());
        return map;
    }

    /**
     * H5 砍价商品详情
     * @param id 砍价商品id
     */
    @Override
    public BargainDetailResponse getH5Detail(Integer id) {
        StoreBargain storeBargain = dao.selectById(id);
        if (ObjectUtil.isNull(storeBargain)) throw new CrmebException("未找到对应砍价商品信息");
        StoreProductResponse storeProductResponse = new StoreProductResponse();
        BeanUtils.copyProperties(storeBargain, storeProductResponse);
        storeProductResponse.setRule(systemAttachmentService.clearPrefix(storeProductResponse.getRule()));
        storeProductResponse.setEndTime(storeBargain.getStopTime());

        StoreProductAttr spaPram = new StoreProductAttr();
        spaPram.setProductId(id).setType(ProductConstants.PRODUCT_TYPE_BARGAIN);
        storeProductResponse.setAttr(attrService.getByEntity(spaPram));

        StoreProductAttrValue spavPramBargain = new StoreProductAttrValue();
        spavPramBargain.setProductId(id).setType(ProductConstants.PRODUCT_TYPE_BARGAIN);
        List<StoreProductAttrValue> storeProductAttrValuesBargain = storeProductAttrValueService.getByEntity(spavPramBargain);

        // 查询attr
        StoreProductAttr spaPramNormal = new StoreProductAttr();
        spaPramNormal.setProductId(storeBargain.getProductId() ).setType(ProductConstants.PRODUCT_TYPE_NORMAL);
        List<StoreProductAttr> attrs = attrService.getByEntity(spaPramNormal);
        boolean specType = false;
        if (attrs.size() > 1) {
            specType = true;
        }
        List<HashMap<String, Object>> attrValuesBargain = genratorSkuInfo(id, specType, storeBargain, storeProductAttrValuesBargain, ProductConstants.PRODUCT_TYPE_BARGAIN);

        // H5 端用于生成skuList
        List<StoreProductAttrValueResponse> sPAVResponses = new ArrayList<>();

        for (StoreProductAttrValue storeProductAttrValue : storeProductAttrValuesBargain) {
            StoreProductAttrValueResponse atr = new StoreProductAttrValueResponse();
            BeanUtils.copyProperties(storeProductAttrValue,atr);
            atr.setQuota(storeProductAttrValue.getQuota());
            atr.setMinPrice(storeBargain.getMinPrice());
            sPAVResponses.add(atr);
        }
        storeProductResponse.setAttrValues(attrValuesBargain);
        storeProductResponse.setAttrValue(sPAVResponses);
        StoreProductDescription sd = storeProductDescriptionService.getOne(
                new LambdaQueryWrapper<StoreProductDescription>()
                        .eq(StoreProductDescription::getProductId, id)
                        .eq(StoreProductDescription::getType, ProductConstants.PRODUCT_TYPE_BARGAIN));
        if(null != sd){
            storeProductResponse.setContent(null == sd.getDescription()?"":sd.getDescription());
        }

        User user = userService.getInfo();
        // 用户发起的砍价活动次数
        int userBargainStatus = isCanPink(storeBargain, user.getUid());

        // 砍价支付成功订单数量
        Integer bargainSumCount = storeOrderService.getCountByBargainIdAndUid(storeBargain.getId(), user.getUid());

        BargainDetailResponse bargainDetailResponse = new BargainDetailResponse(storeProductResponse, userBargainStatus, user, bargainSumCount);
        bargainDetailResponse.setAloneAttrValueId(storeProductResponse.getAttrValue().get(0).getId());

        // 查看人数+1
        storeBargain.setLook(storeBargain.getLook() + 1);
        updateById(storeBargain);
        return bargainDetailResponse;
    }

    /**
     * 用户砍价活动次数
     * @param storeBargain 砍价商品
     * @param uid 用户uid
     * @return
     * 1.用户有没有砍价中的活动
     * 2.没有的，用户的砍价次数有没有到
     */
    private int isCanPink(StoreBargain storeBargain, Integer uid) {
        int userBargainStatus = 0; // 能
        List<StoreBargainUser> list = storeBargainUserService.getListByBargainIdAndUid(storeBargain.getId(), uid);
        if (CollUtil.isNotEmpty(list)) {
            userBargainStatus = list.size();
        }
        return userBargainStatus;
    }

    /**
     * 获取当前时间段的砍价商品
     * @param productId 砍价商品id
     * @return
     */
    @Override
    public List<StoreBargain> getCurrentBargainByProductId(Integer productId) {
        long timeMillis = System.currentTimeMillis();
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargain::getProductId, productId);
        lqw.eq(StoreBargain::getIsDel, false);
        lqw.le(StoreBargain::getStartTime, timeMillis);
        lqw.ge(StoreBargain::getStopTime, timeMillis);
        lqw.orderByDesc(StoreBargain::getSort, StoreBargain::getId);
        return dao.selectList(lqw);
    }

    /**
     * 参与砍价商品活动
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean start(BargainFrontRequest request) {
        StoreBargain storeBargain = dao.selectById(request.getBargainId());
        if (ObjectUtil.isNull(storeBargain)) throw new CrmebException("对应的砍价商品不存在");
        if (!storeBargain.getStatus())  throw new CrmebException("砍价活动已结束");
        User user = userService.getInfo();

        // 判断是否有正在砍价商品
        StoreBargainUser spavBargainUser = new StoreBargainUser();
        spavBargainUser.setIsDel(false).setBargainId(request.getBargainId()).setUid(user.getUid());
        List<StoreBargainUser> historyList = storeBargainUserService.getByEntity(spavBargainUser);
        if (CollUtil.isNotEmpty(historyList)) {
            List<StoreBargainUser> collect = historyList.stream().filter(i -> i.getStatus().equals(BargainConstants.BARGAIN_USER_STATUS_PARTICIPATE)).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(collect)) throw new CrmebException("请先完成当前砍价活动");
            // 判断是否达到参与砍价活动上限
            if (historyList.size() >= storeBargain.getNum()) {
                throw new CrmebException("您已达到当前砍价活动上限");
            }
        }

        StoreBargainUser storeBargainUser = new StoreBargainUser();
        storeBargainUser.setUid(user.getUid());
        storeBargainUser.setBargainId(request.getBargainId());
        storeBargainUser.setBargainPriceMin(storeBargain.getMinPrice());
        storeBargainUser.setBargainPrice(storeBargain.getPrice());
        storeBargainUser.setPrice(BigDecimal.ZERO);
        storeBargainUser.setAddTime(System.currentTimeMillis());
        storeBargainUser.setStatus(BargainConstants.BARGAIN_USER_STATUS_PARTICIPATE);
        return storeBargainUserService.save(storeBargainUser);
    }

    /**
     * 砍价商品根据实体查询
     * @param storeBargainParam 请求参数
     * @return list
     */
    @Override
    public List<StoreBargain> getByEntity(StoreBargain storeBargainParam) {
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeBargainParam);
        return dao.selectList(lqw);
    }

    /**
     * 扣减砍价商品库存
     * @param bargainId     砍价产品id
     * @param num           购买商品数量
     * @param attrValueId   砍价产品规格
     * @param productId     主商品id
     * @param uid           用户uid
     * @return Boolean
     */
    @Override
    public Boolean decProductStock(Integer bargainId, Integer num, Integer attrValueId, Integer productId, Integer uid) {
        // 因为attrvalue表中unique使用Id代替，更新前先查询此表是否存在
        StoreProductAttrValue spavParm = new StoreProductAttrValue();
        spavParm.setProductId(bargainId);
        spavParm.setType(ProductConstants.PRODUCT_TYPE_BARGAIN);
        spavParm.setId(attrValueId);
        List<StoreProductAttrValue> attrvalues = storeProductAttrValueService.getByEntity(spavParm);
        if (CollUtil.isEmpty(attrvalues)) throw new CrmebException("未找到相关商品属性");
        StoreProductAttrValue bargaunAttrValue = attrvalues.get(0);
        // 对应的主商品sku
        List<StoreProductAttrValue> currentProAttrValues = storeProductAttrValueService.getListByProductId(productId);
        List<StoreProductAttrValue> existAttrValues = currentProAttrValues.stream().filter(e ->
                e.getSuk().equals(bargaunAttrValue.getSuk()) && e.getType().equals(ProductConstants.PRODUCT_TYPE_NORMAL))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(existAttrValues)) throw new CrmebException("未找到扣减库存的商品");
        // 砍价SKU 库存减，销量、限购总数减
        StoreBargain storeBargain = getById(bargainId);
        LambdaUpdateWrapper<StoreBargain> luw = new LambdaUpdateWrapper<>();
        luw.set(StoreBargain::getStock, storeBargain.getStock() - num);
        luw.set(StoreBargain::getSales, storeBargain.getSales() + num);
        luw.set(StoreBargain::getQuota, storeBargain.getQuota() - num);
        luw.eq(StoreBargain::getId, bargainId);
        luw.apply(StrUtil.format(" (stock - {} >= 0) ", num));
        // 砍价商品购买成功，改变用户状态
        StoreBargainUser storeBargainUser = storeBargainUserService.getByBargainIdAndUid(bargainId, uid);
        if (ObjectUtil.isNull(storeBargainUser)) throw new CrmebException("砍价用户信息不存在");
        storeBargainUser.setStatus(3);
        Boolean execute = transactionTemplate.execute(e -> {
            storeProductAttrValueService.decProductAttrStock(bargainId, attrValueId, num, ProductConstants.PRODUCT_TYPE_BARGAIN);
            storeProductService.decProductStock(productId, num, existAttrValues.get(0).getId(), ProductConstants.PRODUCT_TYPE_NORMAL);
            update(luw);
            storeBargainUserService.updateById(storeBargainUser);
            return Boolean.TRUE;
        });
        return execute;
    }

    /**
     * 添加库存
     * @param stockRequest  StoreProductStockRequest 参数对象
     * @return
     */
    @Override
    public Boolean stockAddRedis(StoreProductStockRequest stockRequest) {
        String _productString = JSON.toJSONString(stockRequest);
        redisUtil.lPush(Constants.PRODUCT_BARGAIN_STOCK_UPDATE, _productString);
        return true;
    }

    /**
     * 后台任务批量操作库存
     */
    @Override
    public void consumeProductStock() {
        String redisKey = Constants.PRODUCT_BARGAIN_STOCK_UPDATE;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("StoreBargainServiceImpl.consumeProductStock | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(data)) {
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
     * 砍价活动结束后处理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stopAfterChange() {
        // 查询活动状态为开启，结束时间小于当前时间的数据
        List<StoreBargain> storeBargainList = getByStatusAndGtStopTime();
        logger.info("StoreBargainServiceImpl.stopAfterChange | size:" + storeBargainList.size());
        if (CollUtil.isEmpty(storeBargainList)) {
            return;
        }
        List<StoreBargainUser> bargainUserList = CollUtil.newArrayList();
        for (StoreBargain bargain : storeBargainList) {
            // 查询用户参与活动表中状态为进行中的数据
            StoreBargainUser spavBargainUser = new StoreBargainUser();
            spavBargainUser.setBargainId(bargain.getId());
            spavBargainUser.setStatus(BargainConstants.BARGAIN_USER_STATUS_PARTICIPATE);
            spavBargainUser.setIsDel(false);
            List<StoreBargainUser> bargainUsers = storeBargainUserService.getByEntity(spavBargainUser);
            if (CollUtil.isEmpty(bargainUsers)) {
                continue ;
            }
            for (int i = 0; i < bargainUsers.size(); i++) {
                bargainUsers.get(i).setStatus(BargainConstants.BARGAIN_USER_STATUS_FAIL);
            }
            bargainUserList.addAll(bargainUsers);
        }
        boolean b = storeBargainUserService.updateBatchById(bargainUserList, 100);
        if (!b) {
            logger.error("砍价活动结束后更新用户状态定时任务——————失败");
            throw new CrmebException("砍价活动结束后更新用户状态失败");
        }
    }

    /**
     * 商品是否存在砍价活动
     * @param productId 商品编号
     * @return
     */
    @Override
    public Boolean isExistActivity(Integer productId) {
        // 查询商品关联的砍价商品
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargain::getProductId, productId);
        List<StoreBargain> bargainList = dao.selectList(lqw);
        if (CollUtil.isEmpty(bargainList)) {
            return false;
        }
        // 判断关联的商品是否处于活动开启状态
        List<StoreBargain> list = bargainList.stream().filter(i -> i.getStatus().equals(true)).collect(Collectors.toList());
        return CollUtil.isNotEmpty(list);
    }

    /**
     * 查询带异常
     * @param id 砍价商品id
     * @return StoreBargain
     */
    @Override
    public StoreBargain getByIdException(Integer id) {
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargain::getId, id);
        lqw.eq(StoreBargain::getIsDel, false);
        lqw.eq(StoreBargain::getStatus, true);
        StoreBargain storeBargain = dao.selectOne(lqw);
        if (ObjectUtil.isNull(storeBargain)) throw new CrmebException("砍价商品不存在或未开启");
        return storeBargain;
    }

    /**
     * 添加/扣减库存
     * @param id 秒杀商品id
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    @Override
    public Boolean operationStock(Integer id, Integer num, String type) {
        UpdateWrapper<StoreBargain> updateWrapper = new UpdateWrapper<>();
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
            updateWrapper.last(StrUtil.format(" and (stock - {} >= 0)", num));
        }
        updateWrapper.eq("id", id);
        return update(updateWrapper);
    }

    /**
     * 查询活动状态为开启，结束时间小于当前时间的数据
     * @return
     */
    private List<StoreBargain> getByStatusAndGtStopTime() {
        LambdaQueryWrapper<StoreBargain> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargain::getStatus, true);
        lqw.lt(StoreBargain::getStopTime, System.currentTimeMillis());
        return dao.selectList(lqw);
    }

    /**
     * 根据配置生成sku配置信息
     * @param productId     商品id
     * @param specType  是否是多规格
     * @param storeProductAttrValues    属性信息
     * @param productType   秒杀和正常数据
     * @return  sku信息
     */
    private  List<HashMap<String, Object>> genratorSkuInfo(int productId, boolean specType, StoreBargain storeBargain,
                                                           List<StoreProductAttrValue> storeProductAttrValues,
                                                           int productType) {
        List<HashMap<String, Object>> attrValues = new ArrayList<>();
        if (specType) {
            StoreProductAttrResult sparPram = new StoreProductAttrResult();
            sparPram.setProductId(productId).setType(productType);
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
                    HashMap<String, Object> attrValue = new HashMap<>();
                    String currentSku = storeProductAttrValues.get(i).getSuk();
                    List<StoreProductAttrValue> hasCurrentSku =
                            storeProductAttrValues.stream().filter(e -> e.getSuk().equals(currentSku)).collect(Collectors.toList());
                    StoreProductAttrValue currentAttrValue = hasCurrentSku.get(0);
                    attrValue.put("id", hasCurrentSku.size() > 0 ? hasCurrentSku.get(0).getId() : 0);
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
                    attrValue.put("minPrice", storeBargain.getMinPrice());
                    String[] skus = currentSku.split(",");
                    for (int k = 0; k < skus.length; k++) {
                        attrValue.put("value" + k, skus[k]);
                    }
                    attrValues.add(attrValue);
                }

            }
        }
        return attrValues;
    }

    // 砍价操作库存
    private boolean doProductStock(StoreProductStockRequest storeProductStockRequest){
        // 砍价商品信息回滚
        StoreBargain existProduct = getById(storeProductStockRequest.getBargainId());
        List<StoreProductAttrValue> existAttr =
                storeProductAttrValueService.getListByProductIdAndAttrId(
                        storeProductStockRequest.getBargainId(),
                        storeProductStockRequest.getAttrId().toString(),
                        storeProductStockRequest.getType());
        if(ObjectUtil.isNull(existProduct) || ObjectUtil.isNull(existAttr)){ // 未找到商品
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

}

