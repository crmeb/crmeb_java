package com.zbkj.crmeb.store.service.impl;

import com.alibaba.fastjson.JSON;
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
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.utils.UrlUtil;
import com.zbkj.crmeb.category.model.Category;
import com.zbkj.crmeb.category.service.CategoryService;
import com.zbkj.crmeb.front.request.IndexStoreProductSearchRequest;
import com.zbkj.crmeb.marketing.model.StoreCoupon;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
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
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.task.order.OrderRefundByUser;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author Mr.Zhang
* @description StoreProductServiceImpl 接口实现
* @date 2020-05-27
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
    private StoreProductCateService storeProductCateService;

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

    private static final Logger logger = LoggerFactory.getLogger(OrderRefundByUser.class);

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
            List<StoreProductAttr> attrs = attrService.getByProductId(product.getId());
            if(attrs.size() > 0){
                storeProductResponse.setAttr(attrs);
            }
//            StoreProductAttrResult spResult = attrResultService.getByProductId(product.getId());
//            if(null != spResult){
//                if(StringUtils.isNotBlank(spResult.getResult())){
            List<StoreProductAttrValueResponse> storeProductAttrValueResponse = new ArrayList<>();
            List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getListByProductId(product.getId());
            storeProductAttrValues.stream().map(e->{
                StoreProductAttrValueResponse response = new StoreProductAttrValueResponse();
                BeanUtils.copyProperties(e,response);
                storeProductAttrValueResponse.add(response);
                return e;
            }).collect(Collectors.toList());
            storeProductResponse.setAttrValue(storeProductAttrValueResponse);
//                }
                // 处理富文本
                StoreProductDescription sd = storeProductDescriptionService.getOne(
                        new LambdaQueryWrapper<StoreProductDescription>()
                                .eq(StoreProductDescription::getProductId, product.getId()));
//                                .eq(StoreProductDescription::getType, spResult.getType()));
                if(null != sd){
                    storeProductResponse.setContent(null == sd.getDescription()?"":sd.getDescription());
                }
//            }
            // 处理分类中文
            List<Category> cg = categoryService.getByIds(CrmebUtil.stringToArray(product.getCateId()));
            StringBuilder sb = new StringBuilder();
            for (Category category : cg) {
                sb.append(sb.length() == 0 ? category.getName(): category.getName()+",");
            }
            storeProductResponse.setCateValues(sb.toString());

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
        StoreProduct storeProduct = new StoreProduct();
        BeanUtils.copyProperties(storeProductRequest, storeProduct);
        storeProduct.setAddTime(DateUtil.getNowTime());

        //主图
        storeProduct.setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()));

        //轮播图
        storeProduct.setSliderImage(systemAttachmentService.clearPrefix(storeProduct.getSliderImage()));

        // 获取 attrValue 字符串 解析后对应attrValue表中的数据
//        List<StoreProductAttrValueRequest> storeProductAttrValuesRequest = getStoreProductAttrValueRequests(storeProductRequest);

        //计算价格
        calcPriceForAttrValues(storeProductRequest, storeProduct);

        //保存数据
        boolean save = save(storeProduct);
        if(storeProductRequest.getSpecType()) { // 多属性
            storeProductRequest.getAttr().forEach(e->{
                e.setProductId(storeProduct.getId());
                e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"",""),"[]"));
            });
            boolean attrAddResult = attrService.saveOrUpdateBatch(storeProductRequest.getAttr());
            if (!attrAddResult) throw new CrmebException("新增属性名失败");
        }else{ // 单属性
            StoreProductAttr singleAttr = new StoreProductAttr();
            singleAttr.setProductId(storeProduct.getId()).setAttrName("规格").setAttrValues("默认").setType(0);
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
                    .setOtPrice(storeProduct.getOtPrice()).setBrokerage(commissionL1).setBrokerageTwo(commissionL2);
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
//                    HashMap<String, Object> attrValues = setAttrValueByRequest(storeProductRequest);
                }
                spav.setImage(systemAttachmentService.clearPrefix(spav.getImage()));
                spav.setAttrValue(JSON.toJSONString(attrValuesRequest.getAttrValue()));
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
                ? systemAttachmentService.clearPrefix(storeProductRequest.getContent()):storeProductRequest.getContent());
        storeProductDescriptionService.deleteByProductId(spd.getProductId());
        storeProductDescriptionService.save(spd);

        // 处理优惠券关联信息
        shipProductCoupons(storeProductRequest, storeProduct);
        return save;
    }


    /**
     * 更新产品
     * @param storeProductRequest 更新产品request对象
     * @return 更新结果
     */
    @Override
    public boolean update(StoreProductRequest storeProductRequest) {
        StoreProduct storeProduct = new StoreProduct();
        BeanUtils.copyProperties(storeProductRequest, storeProduct);
        storeProduct.setAddTime(DateUtil.getNowTime());

        //主图
        storeProduct.setImage(systemAttachmentService.clearPrefix(storeProduct.getImage()));

        //轮播图
        storeProduct.setSliderImage(systemAttachmentService.clearPrefix(storeProduct.getSliderImage()));

//        List<StoreProductAttrValueRequest> storeProductAttrValuesRequest = getStoreProductAttrValueRequests(storeProductRequest);

        calcPriceForAttrValues(storeProductRequest, storeProduct);
        int saveCount = dao.updateById(storeProduct);
        // 对attr表做全量更新，删除原有数据保存现有数据
        attrService.removeByProductId(storeProduct.getId());
        storeProductAttrValueService.removeByProductId(storeProduct.getId());
        if(storeProductRequest.getSpecType()) { // todo 确认单属性商品的attr参数
            storeProductRequest.getAttr().forEach(e->{
                e.setProductId(storeProductRequest.getId());
                e.setAttrValues(StringUtils.strip(e.getAttrValues().replace("\"",""),"[]"));
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
//                    HashMap<String, Object> attrValues = setAttrValueByRequest(storeProductRequest);
                spav.setAttrValue(JSON.toJSONString(attrValuesRequest.getAttrValue()));
//                    spav.setAttrValue(JSON.toJSONString(attrValues));
                    storeProductAttrValues.add(spav);
                }
                boolean saveOrUpdateResult = storeProductAttrValueService.saveOrUpdateBatch(storeProductAttrValues);
                // attrResult整存整取，不做更新
                storeProductAttrResultService.deleteByProductId(storeProduct.getId());
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
            boolean saveOrUpdateResult = storeProductAttrValueService.save(singleAttrValue);
            if(!saveOrUpdateResult) throw new CrmebException("新增属性详情失败");
        }

        // 处理分类辅助表
        if(null != storeProductRequest.getCateIds()){
            for (int i = 0; i < storeProductRequest.getCateIds().size(); i++) {
                Integer cateid = storeProductRequest.getCateIds().get(i);
                StoreProductCate storeProductCate =
                        new StoreProductCate(storeProduct.getId(),cateid, DateUtil.getNowTime());
                LambdaUpdateWrapper<StoreProductCate> luw = new LambdaUpdateWrapper<>();
                luw.set(StoreProductCate::getProductId, storeProductCate.getProductId());
                luw.set(StoreProductCate::getCateId, storeProductCate.getCateId());
                luw.set(StoreProductCate::getAddTime, storeProductCate.getAddTime());
                boolean updateResult = storeProductCateService.update(luw);
                if(!updateResult) throw new CrmebException("编辑产品分类辅助失败");
            }
        }

        // 处理富文本
        StoreProductDescription spd = new StoreProductDescription(
                storeProduct.getId(),
                storeProductRequest.getContent().length() > 0
                        ? systemAttachmentService.clearPrefix(storeProductRequest.getContent()):storeProductRequest.getContent());
        storeProductDescriptionService.deleteByProductId(spd.getProductId());
        storeProductDescriptionService.save(spd);

        // 处理优惠券关联信息
        shipProductCoupons(storeProductRequest, storeProduct);
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
//        if(storeProduct.getSpecType()){
            storeProductResponse.setAttr(attrService.getByProductId(storeProduct.getId()));
//        }else{
//            storeProductResponse.setAttr(new ArrayList<>());
//        }
        List<StoreProductAttrValue> storeProductAttrValues = storeProductAttrValueService.getListByProductId(storeProduct.getId());
        // 根据attrValue生成前端所需的数据
        List<HashMap<String, Object>> attrValues = new ArrayList<>();

        if(storeProduct.getSpecType()){
            // 后端多属性用于编辑
            StoreProductAttrResult attrResult = storeProductAttrResultService.getByProductId(storeProduct.getId());
            //PC 端生成skuAttrInfo
            List<StoreProductAttrValueRequest> storeProductAttrValueRequests =
                    com.alibaba.fastjson.JSONObject.parseArray(attrResult.getResult(), StoreProductAttrValueRequest.class);
            if(null != storeProductAttrValueRequests){
                for (int i = 0; i < storeProductAttrValueRequests.size(); i++) {
                    StoreProductAttrValueRequest storeProductAttrValueRequest = storeProductAttrValueRequests.get(i);
                    HashMap<String, Object> attrValue = new HashMap<>();
                    attrValue.put("image", storeProductAttrValueRequest.getImage());
                    attrValue.put("cost", storeProductAttrValueRequest.getCost());
                    attrValue.put("price", storeProductAttrValueRequest.getPrice());
                    attrValue.put("otPrice", storeProductAttrValueRequest.getOtPrice());
                    attrValue.put("stock", storeProductAttrValueRequest.getStock());
                    attrValue.put("barCode", storeProductAttrValueRequest.getBarCode());
                    attrValue.put("weight", storeProductAttrValueRequest.getWeight());
                    attrValue.put("volume", storeProductAttrValueRequest.getVolume());
                    attrValue.put("suk", storeProductAttrValues.get(i).getSuk());
//                com.alibaba.fastjson.JSONObject jsonAttr =  JSON.parseObject(storeProductAttrValues.get(i).getAttrValue());
//                com.alibaba.fastjson.JSONArray jsonAttrs = jsonAttr.getJSONArray("attrValue");
//                com.alibaba.fastjson.JSONObject attrO = JSON.parseObject(jsonAttrs.get(i).toString());
//                attrValue.put("attrValue", attrO.getJSONObject("attrValue"));
                    attrValue.put("attrValue", JSON.parse(storeProductAttrValues.get(i).getAttrValue()));
                    attrValue.put("brokerage", storeProductAttrValueRequest.getBrokerage());
                    attrValue.put("brokerage_two", storeProductAttrValueRequest.getBrokerageTwo());
                    Iterator<Map.Entry<String, String>> iterator = storeProductAttrValueRequest.getAttrValue().entrySet().iterator();
                    int j = 0;
                    while (iterator.hasNext()){
                        Map.Entry<String, String> entry = iterator.next();
                        attrValue.put("value"+j, entry.getValue());
                        j += 1;
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
                            .eq(StoreProductDescription::getProductId, storeProduct.getId()));
//                            .eq(StoreProductDescription::getType, storeProductAttrResult.getType()));
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
     * @param request
     * @param pageParamRequest
     * @return
     */
    @Override
    public List<StoreProduct> getList(IndexStoreProductSearchRequest request, PageParamRequest pageParamRequest) {
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
            lambdaQueryWrapper.eq(StoreProduct::getIsBenefit, request.getIsBenefit());
        }

        lambdaQueryWrapper.eq(StoreProduct::getIsDel, false)
                .eq(StoreProduct::getMerId, false)
                .gt(StoreProduct::getStock, 0)
                .eq(StoreProduct::getIsShow, true)
                .orderByDesc(StoreProduct::getSort);
        if(!StringUtils.isBlank(request.getPriceOrder())){
            if(request.getPriceOrder().equals(Constants.SORT_DESC)){
                lambdaQueryWrapper.orderByDesc(StoreProduct::getPrice);
            }else{
                lambdaQueryWrapper.orderByAsc(StoreProduct::getPrice);
            }
        }

        if(!StringUtils.isBlank(request.getSalesOrder())){
            if(request.getSalesOrder().equals(Constants.SORT_DESC)){
                lambdaQueryWrapper.orderByDesc(StoreProduct::getSales);
            }else{
                lambdaQueryWrapper.orderByAsc(StoreProduct::getSales);
            }
        }
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

        lambdaQueryWrapper.orderByDesc(StoreProduct::getId);
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
        redisUtil.lPush(Constants.PRODUCT_STOCK_UPDATE, JSON.toJSONString(request));
        return true;
    }

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
     * @param type 是否限购 0=不限购
     * @return 扣减结果
     */
    @Override
    public boolean decProductStock(Integer productId, Integer num, Integer attrValueId, Integer type) {
        // 因为attrvalue表中unique使用Id代替，更新前先查询此表是否存在
        // 不存在=但属性 存在则是多属性
        StoreProductAttrValue productsInAttrValue =
                storeProductAttrValueService.getById(attrValueId);
        StoreProduct storeProduct = getById(productId);
        boolean result = false;
        if(null != productsInAttrValue){
            result = storeProductAttrValueService.decProductAttrStock(productId,attrValueId,num,type);
        }
        LambdaUpdateWrapper<StoreProduct> lqwuper = new LambdaUpdateWrapper<>();
        lqwuper.eq(StoreProduct::getId, productId);
        lqwuper.set(StoreProduct::getStock, storeProduct.getStock()-num);
        lqwuper.set(StoreProduct::getSales, storeProduct.getSales()+num);
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
                    productRequest = getTaobaoProductInfo(url,tag);
                    break;
                case 2:
                    productRequest = getJDProductInfo(url,tag);
                    break;
                case 3:
                    productRequest = getSuningProductInfo(url,tag);
                    break;
                case 4:
                    productRequest = getPddProductInfo(url,tag);
                    break;
                case 5:
                    productRequest = getTmallProductInfo(url,tag);
                    break;
            }
        }catch (Exception e){
            throw new CrmebException("确认URL和平台是否正确，以及平台费用是否足额"+e.getMessage());
        }
        return productRequest;
    }

    private String baseUrl;

    String rightUrl;


    /**
     * 设置优惠券信息
     * @param storeProductRequest 商品request信息
     * @param storeProduct 商品信息
     */
    private void shipProductCoupons(StoreProductRequest storeProductRequest, StoreProduct storeProduct) {
        if(null != storeProductRequest.getCouponIds() && storeProductRequest.getCouponIds().size() > 0){
            storeProductCouponService.deleteByProductId(storeProduct.getId());
            List<StoreProductCoupon> spcs = new ArrayList<>();
            for (Integer couponId : storeProductRequest.getCouponIds()) {
                StoreProductCoupon spc = new StoreProductCoupon(storeProduct.getId(), couponId, DateUtil.getNowTime());
                spcs.add(spc);
            }
            storeProductCouponService.saveCoupons(spcs);
        }
    }

    /**
     * 解析淘宝产品数据
     * @param url
     * @param tag
     * @throws IOException
     * @throws JSONException
     */
    public StoreProductRequest getTaobaoProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url, tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
//        JSONObject tbJsonData = new JSONObject(JSONExample.tbJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");
        JSONObject item = data.getJSONObject("item");
        if (null == item) throw new CrmebException("复制商品失败--返回数据格式错误--未找到item");

        StoreProductRequest productRequest = new StoreProductRequest();
        StoreProduct product = new StoreProduct();
        product.setStoreName(item.getString("title"));
        product.setStoreInfo(item.getString("title"));
        product.setSliderImage(item.getString("images"));
        product.setImage(item.getString("images").split(",")[0]
                .replace("[", "").replace("\"", ""));
        product.setKeyword(item.getString("title"));
        BeanUtils.copyProperties(product, productRequest);
        productRequest.setContent(item.getString("desc"));

        JSONArray props = item.getJSONArray("props");
        if (null == props) throw new CrmebException("复制商品失败--返回数据格式错误--未找到props");
        if (props.length() > 0) {
            List<StoreProductAttr> spaAttes = new ArrayList<>();
            for (int i = 0; i < props.length(); i++) {
                JSONObject pItem = props.getJSONObject(i);
                StoreProductAttr spattr = new StoreProductAttr();
                spattr.setAttrName(pItem.getString("name"));
                JSONArray values = pItem.getJSONArray("values");
                List<String> attrValues = new ArrayList<>();
                for (int j = 0; j < values.length(); j++) {
                    JSONObject value = values.getJSONObject(j);
                    attrValues.add(value.getString("name"));
                }
                spattr.setAttrValues(attrValues.toString());
                spaAttes.add(spattr);
            }
            productRequest.setAttr(spaAttes);
        }
        return productRequest;
    }

    /**
     * 解析京东产品数据
     * @param url
     * @param tag
     * @return
     * @throws JSONException
     */
    public StoreProductRequest getJDProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url,tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
//        JSONObject tbJsonData = new JSONObject(JSONExample.jdJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");
        JSONObject item = data.getJSONObject("item");
        if (null == item) throw new CrmebException("复制商品失败--返回数据格式错误--未找到item");

        StoreProductRequest productRequest = new StoreProductRequest();
        StoreProduct product = new StoreProduct();
        product.setStoreName(item.getString("name"));
        product.setStoreInfo(item.getString("name"));
        product.setSliderImage(item.getString("images"));
        product.setImage(item.getString("images").split(",")[0]
                .replace("[", "").replace("\"", ""));
        product.setPrice(BigDecimal.valueOf(item.getDouble("price")));
        BeanUtils.copyProperties(product, productRequest);
        productRequest.setContent(item.getString("desc"));

        JSONObject props = item.getJSONObject("skuProps");
        if (null == props) throw new CrmebException("复制商品失败--返回数据格式错误--未找到props");
        List<StoreProductAttr> spaAttes = new ArrayList<>();
        JSONObject saleJson = item.getJSONObject("saleProp");
        int attrValueIsNullCount = 0;
        Iterator<String> saleProps = saleJson.keys();
        while (saleProps.hasNext()) {
            StoreProductAttr spattr = new StoreProductAttr();
            String stepkey = saleProps.next();
            String stepValue = props.getString(stepkey);
            String stepValueValidLength = stepValue.replace("[","").replace("]","").replace("\"","");
            if(stepValueValidLength.length() > 0){
                com.alibaba.fastjson.JSONArray stepValues = JSON.parseArray(stepValue);
                int c = stepValues.get(0).toString().length();
                attrValueIsNullCount += c == 0 ? 1 : 0;
                spattr.setAttrName(saleJson.getString(stepkey));
                spattr.setAttrValues(props.getString(stepkey));
                spaAttes.add(spattr);
                productRequest.setAttr(spaAttes);
            }else{
                attrValueIsNullCount += 1;
            }
        }
        // 判断是否单属性
        productRequest.setSpecType(spaAttes.size() != attrValueIsNullCount);
        return productRequest;
    }

    /**
     * 解析天猫产品数据
     * @param url
     * @param tag
     * @return
     * @throws JSONException
     */
    public StoreProductRequest getTmallProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url, tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
//        JSONObject tbJsonData = new JSONObject(JSONExample.tmallJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");
        JSONObject item = data.getJSONObject("item");
        if (null == item) throw new CrmebException("复制商品失败--返回数据格式错误--未找到item");

        StoreProductRequest productRequest = new StoreProductRequest();
        StoreProduct product = new StoreProduct();
        product.setStoreName(item.getString("title"));
        product.setStoreInfo(item.getString("subTitle"));
        product.setSliderImage(item.getString("images"));
        product.setImage(item.getString("images").split(",")[0]
                .replace("[", "").replace("\"", ""));
        product.setKeyword(item.getString("title"));
        BeanUtils.copyProperties(product, productRequest);
        productRequest.setContent(item.getString("desc"));

        JSONArray props = item.getJSONArray("props");
        if (null == props) throw new CrmebException("复制商品失败--返回数据格式错误--未找到props");
        if (props.length() > 0) {
            List<StoreProductAttr> spaAttes = new ArrayList<>();
            for (int i = 0; i < props.length(); i++) {
                JSONObject pItem = props.getJSONObject(i);
                StoreProductAttr spattr = new StoreProductAttr();
                spattr.setAttrName(pItem.getString("name"));
                JSONArray values = pItem.getJSONArray("values");
                List<String> attrValues = new ArrayList<>();
                for (int j = 0; j < values.length(); j++) {
                    JSONObject value = values.getJSONObject(j);
                    attrValues.add(value.getString("name"));
                }
                spattr.setAttrValues(attrValues.toString());
                spaAttes.add(spattr);
            }
            productRequest.setAttr(spaAttes);
        }
        return productRequest;
    }

    /**
     * 解析拼多多产品数据
     * @param url
     * @param tag
     * @return
     * @throws JSONException
     */
    public StoreProductRequest getPddProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url, tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
//        JSONObject tbJsonData = new JSONObject(JSONExample.pddJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");
        JSONObject item = data.getJSONObject("item");
        if (null == item) throw new CrmebException("复制商品失败--返回数据格式错误--未找到item");

        StoreProductRequest productRequest = new StoreProductRequest();
        StoreProduct product = new StoreProduct();
        product.setStoreName(item.getString("goodsName"));
        product.setStoreInfo(item.getString("goodsDesc"));
        product.setSliderImage(item.getString("thumbUrl"));
        product.setImage(item.getString("banner"));
        product.setVideoLink(item.getJSONArray("video").getJSONObject(0).getString("videoUrl"));
        product.setPrice(BigDecimal.valueOf(item.getDouble("maxNormalPrice")));
        product.setOtPrice(BigDecimal.valueOf(item.getDouble("marketPrice")));
        BeanUtils.copyProperties(product, productRequest);

        JSONArray props = item.getJSONArray("skus");
        if (null == props) throw new CrmebException("复制商品失败--返回数据格式错误--未找到props");
        if (props.length() > 0) {
            List<StoreProductAttr> spaAttes = new ArrayList<>();
            HashMap<String,List<String>> tempAttr = new HashMap<>();
            for (int i = 0; i < props.length(); i++) {
                JSONObject pItem = props.getJSONObject(i);
                JSONArray specArray = pItem.getJSONArray("specs");
                for (int j = 0; j < specArray.length(); j++) {
                    JSONObject specItem = specArray.getJSONObject(j);
                    String keyTemp = specItem.getString("spec_key");
                    String valueTemp = specItem.getString("spec_value");
                    if(tempAttr.containsKey(keyTemp)){
                        if(!tempAttr.get(keyTemp).contains(valueTemp)){
                            tempAttr.get(keyTemp).add(valueTemp);
                        }
                    }else{
                        List<String> tempList = new ArrayList<>();
                        tempList.add(valueTemp);
                        tempAttr.put(keyTemp, tempList);
                    }
                }

            }
            Iterator iterator = tempAttr.keySet().iterator();
            while (iterator.hasNext()){
                String key = (String)iterator.next();
                StoreProductAttr spattr = new StoreProductAttr();
                spattr.setAttrName(key);
                spattr.setAttrValues(tempAttr.get(key).toString());
                spaAttes.add(spattr);
            }
            productRequest.setAttr(spaAttes);
        }
        return productRequest;
    }

    /** TODO 苏宁返回的数据不一致，暂放
     * 解析苏宁产品数据
     * @param url
     * @param tag
     * @return
     * @throws JSONException
     */
    public StoreProductRequest getSuningProductInfo(String url, int tag) throws JSONException, IOException {
        setConfig(url,tag);
        JSONObject tbJsonData = getRequestFromUrl(baseUrl + rightUrl);
        System.out.println("tbJsonData:"+tbJsonData);
//        JSONObject tbJsonData = new JSONObject(JSONExample.snJson); // just Test
        JSONObject data = tbJsonData.getJSONObject("data");
        if (null == data) throw new CrmebException("复制商品失败--返回数据格式错误--未找到data");

        StoreProductRequest productRequest = new StoreProductRequest();
        StoreProduct product = new StoreProduct();
        product.setStoreName(data.getString("title"));
        product.setStoreInfo(data.getString("title"));
        product.setSliderImage(data.getString("images"));
        product.setImage(data.getString("images").split(",")[0]
                .replace("[", "").replace("\"", ""));
        Long priceS = data.getLong("price");
        product.setPrice(BigDecimal.valueOf(priceS));
        BeanUtils.copyProperties(product, productRequest);
        productRequest.setContent(data.getString("desc"));
        if(null == data.optJSONArray("passSubList")){
            return productRequest;
        }
        JSONArray props = data.getJSONArray("passSubList");
        if (null == props){
            return productRequest;
        }
        if (props.length() > 0) {
            List<StoreProductAttr> spaAttes = new ArrayList<>();
            for (int i = 0; i < props.length(); i++) {
                JSONObject pItem = props.getJSONObject(i);
                Iterator it = pItem.keys();
                while (it.hasNext()){
                    String key = (String)it.next();
                    JSONArray skuItems = pItem.getJSONArray(key);
                    List<String> attrValues = new ArrayList<>();
                    StoreProductAttr spattr = new StoreProductAttr();
                    for (int j = 0; j < skuItems.length(); j++) {
                        JSONObject skuItem = skuItems.getJSONObject(j);
                        if(null != skuItem.optString("characterValueDisplayName"))
                        attrValues.add(skuItem.getString("characterValueDisplayName"));
                    }
                    spattr.setAttrName(key);
                    spattr.setAttrValues(attrValues.toString());
                    spaAttes.add(spattr);
                }
                productRequest.setAttr(spaAttes);
            }
        }
        return productRequest;
    }

    /**
     * 设置配置数据
     * @param tag
     */
    private void setConfig(String url, int tag){
        String rightEndUrl = "&itemid=";
        switch (tag){ // 导入平台1=淘宝，2=京东，3=苏宁，4=拼多多， 5=天猫
            case 1:
                baseUrl = systemConfigService.getValueByKey("inportProductTB");
                rightEndUrl += UrlUtil.getParamsByKey(url, "id");
                break;
            case 2:
                baseUrl = systemConfigService.getValueByKey("inportProductJD");
                rightEndUrl += url.substring(url.lastIndexOf("/")+1).replace(".html","");
                break;
            case 3:
                baseUrl = systemConfigService.getValueByKey("inportProductSN");
                int start = url.indexOf(".com/") + 5;
                int end = url.indexOf(".html");
                String sp = url.substring(start,end);
                String[] shopProduct = sp.split("/");
                rightEndUrl += shopProduct[1]+"&shopid="+shopProduct[0];
                break;
            case 4:
                rightEndUrl += UrlUtil.getParamsByKey(url, "goods_id");
                baseUrl = systemConfigService.getValueByKey("inportProductPDD");
                break;
            case 5:
                rightEndUrl += UrlUtil.getParamsByKey(url, "id");
                baseUrl = systemConfigService.getValueByKey("inportProductTM");
                break;
        }
        String token = systemConfigService.getValueByKey("importProductToken");
        if(StringUtils.isBlank(token)){
            throw new CrmebException("请配置复制产品平台的Token -- www.99api.com");
        }
        if(StringUtils.isBlank(baseUrl)){
            throw new CrmebException("请配置复制产品平台的Url-- www.99api.com");
        }
        rightUrl = "?apikey="+systemConfigService.getValueByKey("importProductToken")+rightEndUrl;

    }

    /**
     * 99api产品复制工具方法
     * @param rd
     * @return
     * @throws IOException
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * 根据url访问99api后返回对应的平台的产品json数据 带有body参数，暂时无用
     * @param url
     * @param body
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject postRequestFromUrl(String url, String body) throws IOException, JSONException {
        URL realUrl = new URL(url);
        URLConnection conn = realUrl.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(body);
        out.flush();
        InputStream instream = conn.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(instream, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            instream.close();
        }
    }

    /**
     * 根据url访问99api后返回对应的平台的产品json数据
     * @param url
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject getRequestFromUrl(String url) throws IOException, JSONException {
        URL realUrl = new URL(url);
        URLConnection conn = realUrl.openConnection();
        InputStream instream = conn.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(instream, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            instream.close();
        }
    }

    /**
     * 计算产品属性之中最大和最小的价格，新增和编辑使用
     * @param storeProductRequest 分析的参数
     * @param storeProduct 当前操作的产品
     */
    private void calcPriceForAttrValues(StoreProductRequest storeProductRequest, StoreProduct storeProduct) {
        // 设置商品成本价和市场价，成本价=sku配置的最低价，商品价格=sku配置的最高价格
//        BigDecimal costPrice = storeProductRequest.getAttrValue()
//                    .stream().map(e->e.getCost()).reduce(BigDecimal.ZERO,BigDecimal::add);
        int costPrice = storeProductRequest.getAttrValue()
                .stream().mapToInt(e->e.getCost().intValue()).max().getAsInt();
//        BigDecimal sellPrice = storeProductRequest.getAttrValue()
//                    .stream().map(e->e.getOtPrice()).reduce(BigDecimal.ZERO,BigDecimal::add);
        int sellPrice = storeProductRequest.getAttrValue()
                .stream().mapToInt(e->e.getOtPrice().intValue()).min().getAsInt();

        int stock = storeProductRequest.getAttrValue()
                    .stream().mapToInt(e->e.getStock()).sum();
        storeProduct.setPrice(BigDecimal.valueOf(costPrice));
        storeProduct.setOtPrice(BigDecimal.valueOf(sellPrice));
        storeProduct.setStock(stock);
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
        lambdaQueryWrapper.eq(StoreProduct::getIsGood,false);
        lambdaQueryWrapper.orderByDesc(StoreProduct::getSort).orderByDesc(StoreProduct::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     *
     * @param productId 商品id
     * @return
     */
    @Override
    public boolean deleteProduct(Integer productId) {
        LambdaUpdateWrapper<StoreProduct> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(StoreProduct::getId, productId);
        lambdaUpdateWrapper.set(StoreProduct::getIsDel, true);
        return update(lambdaUpdateWrapper);
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

    // 操作库存
    private boolean doProductStock(StoreProductStockRequest storeProductStockRequest){
        StoreProduct existProduct = getById(storeProductStockRequest.getProductId());
        List<StoreProductAttrValue> existAttr =
                storeProductAttrValueService.getListByProductIdAndAttrId(storeProductStockRequest.getProductId(), storeProductStockRequest.getAttrId().toString());
        if(null == existProduct || null == existAttr){ // 为找到商品
            logger.info("库存修改任务未获取到商品信息"+JSON.toJSONString(storeProductStockRequest));
            return true;
        }
        boolean isPlus = storeProductStockRequest.getType().equals("add");
        int productStock = isPlus ? existProduct.getStock() + storeProductStockRequest.getNum() : existProduct.getStock() - storeProductStockRequest.getNum();
        existProduct.setStock(productStock);
        updateById(existProduct);
        for (StoreProductAttrValue attrValue : existAttr) {
            int productAttrStock = isPlus ? attrValue.getStock() + storeProductStockRequest.getNum() : attrValue.getStock() - storeProductStockRequest.getNum();
            attrValue.setStock(productAttrStock);
            storeProductAttrValueService.updateById(attrValue);
        }
        return true;
    }


    /**
     * 解析json字符串 返回对应对象的数据
     * @param storeProductRequest
     * @return
     */
//    private List<StoreProductAttrValueRequest> getStoreProductAttrValueRequests(StoreProductRequest storeProductRequest) {
//        List<StoreProductAttrValueRequest> storeProductAttrValuesRequest = new ArrayList<>();
//        com.alibaba.fastjson.JSONArray attrJSONArray = com.alibaba.fastjson.JSONArray.parseArray(storeProductRequest.getAttrValue());
//        for (int i = 0; i < attrJSONArray.size(); i++) {
//            com.alibaba.fastjson.JSONObject jsonObject = attrJSONArray.getJSONObject(i);
//            StoreProductAttrValueRequest attrValueRequest =
//                    com.alibaba.fastjson.JSONObject.parseObject(String.valueOf(jsonObject), StoreProductAttrValueRequest.class);
//            storeProductAttrValuesRequest.add(attrValueRequest);
//        }
//        return storeProductAttrValuesRequest;
//    }

    /**
     * 产品保存和更新时设置attr和attrValues属性
     * @param storeProductRequest 产品属性
     * @return 设置后的数据对象
     */
    private HashMap<String, Object> setAttrValueByRequest(StoreProductRequest storeProductRequest) {
        HashMap<String, Object> attrValues = new HashMap<>();
        attrValues.put("attr", storeProductRequest.getAttr());
        attrValues.put("attrValue", storeProductRequest.getAttrValue());
        return attrValues;
    }
    //    public static void main(String[] args) throws IOException, JSONException {
//        // 请求示例 url 默认请求参数已经做URL编码
//        // 淘宝API
//        String tbUrl = "https://api03.6bqb.com/taobao/detail?apikey=A5E94A9B7EBEBE9BB305680C0EE23885&itemid=16793826526";
//        // 京东
//        String jdUrl = "https://api03.6bqb.com/jd/detail?apikey=A5E94A9B7EBEBE9BB305680C0EE23885&itemid=10000017776";
//        // 苏宁
//        String snUrl = "https://api03.6bqb.com/suning/detail?apikey=A5E94A9B7EBEBE9BB305680C0EE23885&itemid=10750373914&shopid=0070088010";
//        // 拼多多
//        String pddUrl = "https://api03.6bqb.com/pdd/detail?apikey=A5E94A9B7EBEBE9BB305680C0EE23885&itemid=5914165983";
//        CopyProduct cp = new CopyProduct();
////        JSONObject tbJson = cp.getRequestFromUrl(tbUrl);
////        System.out.println("淘宝产品"+tbJson.toString());
////        JSONObject jdJson = cp.getRequestFromUrl(jdUrl);
////        System.out.println("京东产品"+jdJson.toString());
////        JSONObject snJson = cp.getRequestFromUrl(snUrl);
////        System.out.println("苏宁产品"+snJson.toString());
//        JSONObject pddJson = cp.getRequestFromUrl(pddUrl);
//
//        System.out.println("拼多多产品"+pddJson.toString());
//
//        cp.getTaobaoProductInfo("",0);
//    }
}

