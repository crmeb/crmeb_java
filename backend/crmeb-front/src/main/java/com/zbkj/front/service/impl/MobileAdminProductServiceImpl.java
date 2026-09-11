package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.express.ShippingTemplates;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.model.product.StoreProductAttrValue;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.MobileAdminProductAttrValueUpdateRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductAttrOptionAddRequest;
import com.zbkj.common.request.ShippingTemplatesSearchRequest;
import com.zbkj.common.request.StoreProductAddRequest;
import com.zbkj.common.request.StoreProductAttrAddRequest;
import com.zbkj.common.request.StoreProductAttrValueAddRequest;
import com.zbkj.common.request.StoreProductSearchRequest;
import com.zbkj.common.response.StoreProductResponse;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.vo.CategoryTreeVo;
import com.zbkj.front.service.MobileAdminPermissionService;
import com.zbkj.front.service.MobileAdminProductService;
import com.zbkj.service.service.CategoryService;
import com.zbkj.service.service.ShippingTemplatesService;
import com.zbkj.service.service.StoreProductAttrValueService;
import com.zbkj.service.service.StoreProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 移动端商家商品管理服务实现
 */
@Service
public class MobileAdminProductServiceImpl implements MobileAdminProductService {

    @Autowired
    private MobileAdminPermissionService mobileAdminPermissionService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ShippingTemplatesService shippingTemplatesService;

    @Override
    public CommonPage<StoreProductResponse> getList(Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        Integer type = parseProductType(getString(params, "type"));
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(getInteger(params, "page", 1));
        pageParamRequest.setLimit(getInteger(params, "limit", 20));

        if (type != null) {
            StoreProductSearchRequest request = new StoreProductSearchRequest();
            request.setType(type);
            request.setKeywords(getFirstString(params, "keywords", "keyword", "store_name"));
            request.setCateId(getFirstIdListString(params, "cateId", "cate_id"));
            request.setPriceOrder(getFirstString(params, "priceOrder", "price_order"));
            request.setSalesOrder(getFirstString(params, "salesOrder", "sales_order"));
            return CommonPage.restPage(storeProductService.getAdminList(request, pageParamRequest));
        }
        return getAllProductList(params, pageParamRequest);
    }

    @Override
    public Boolean setShow(Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        List<Integer> ids = parseIdList(params.get("id"));
        if (CollUtil.isEmpty(ids)) {
            throw new CrmebException("请选择商品");
        }
        Boolean isShow = parseBoolean(params.get("is_show"));
        for (Integer id : ids) {
            if (Boolean.TRUE.equals(isShow)) {
                storeProductService.putOnShelf(id);
            } else {
                storeProductService.offShelf(id);
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public List<CategoryTreeVo> getCate() {
        mobileAdminPermissionService.requireEnabledStaff();
        return categoryService.getListTree(Constants.CATEGORY_TYPE_PRODUCT, 1, "");
    }

    @Override
    public Boolean saveCate(Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        List<Integer> ids = parseIdList(params.get("id"));
        String cateId = getFirstIdListString(params, "cateId", "cate_id");
        if (CollUtil.isEmpty(ids)) {
            throw new CrmebException("请选择商品");
        }
        if (StrUtil.isBlank(cateId)) {
            throw new CrmebException("请选择商品分类");
        }
        for (Integer id : ids) {
            checkProductExists(id);
            StoreProduct product = new StoreProduct();
            product.setId(id);
            product.setCateId(cateId);
            storeProductService.updateById(product);
        }
        return Boolean.TRUE;
    }

    @Override
    public List<ShippingTemplates> getShippingTemp() {
        mobileAdminPermissionService.requireEnabledStaff();
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(1);
        pageParamRequest.setLimit(100);
        return shippingTemplatesService.getList(new ShippingTemplatesSearchRequest(), pageParamRequest);
    }

    @Override
    public Boolean create(Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        return storeProductService.save(buildCreateRequest(params));
    }

    @Override
    public List<StoreProductAttrValue> getAttr(Integer productId) {
        mobileAdminPermissionService.requireEnabledStaff();
        checkProductExists(productId);
        return storeProductAttrValueService.getListByProductIdAndType(productId, Constants.PRODUCT_TYPE_NORMAL);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAttr(Integer productId, MobileAdminProductAttrValueUpdateRequest request) {
        mobileAdminPermissionService.requireEnabledStaff();
        checkProductExists(productId);
        if (request == null || CollUtil.isEmpty(request.getAttrValue())) {
            throw new CrmebException("请填写规格价格库存");
        }
        List<MobileAdminProductAttrValueUpdateRequest.AttrValueItem> requestAttrValueList = request.getAttrValue();
        List<StoreProductAttrValue> attrValueList = storeProductAttrValueService.getListByProductIdAndType(productId, Constants.PRODUCT_TYPE_NORMAL);
        boolean canMatchByOrder = CollUtil.isNotEmpty(attrValueList) && requestAttrValueList.size() == attrValueList.size();
        for (int i = 0; i < requestAttrValueList.size(); i++) {
            MobileAdminProductAttrValueUpdateRequest.AttrValueItem item = requestAttrValueList.get(i);
            if (item == null) {
                throw new CrmebException("请填写规格价格库存");
            }
            StoreProductAttrValue fallbackAttrValue = canMatchByOrder ? attrValueList.get(i) : null;
            StoreProductAttrValue attrValue = findAttrValue(productId, item, fallbackAttrValue);
            StoreProductAttrValue updateAttrValue = new StoreProductAttrValue();
            updateAttrValue.setId(attrValue.getId());
            updateAttrValue.setProductId(productId);
            updateAttrValue.setCost(item.getCost());
            updateAttrValue.setPrice(item.getPrice());
            updateAttrValue.setOtPrice(item.getOtPrice());
            updateAttrValue.setStock(item.getStock());
            storeProductAttrValueService.updateById(updateAttrValue);
        }
        refreshProductPriceAndStock(productId);
        return Boolean.TRUE;
    }

    private CommonPage<StoreProductResponse> getAllProductList(Map<String, Object> params, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<StoreProduct> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(StoreProduct::getIsRecycle, false);
        wrapper.eq(StoreProduct::getIsDel, false);
        String keywords = getFirstString(params, "keywords", "keyword", "store_name");
        if (StrUtil.isNotBlank(keywords)) {
            wrapper.and(i -> i
                    .or().eq(StoreProduct::getId, keywords)
                    .or().like(StoreProduct::getStoreName, keywords)
                    .or().like(StoreProduct::getKeyword, keywords));
        }
        wrapper.orderByDesc(StoreProduct::getSort);
        wrapper.orderByDesc(StoreProduct::getId);
        Page<StoreProduct> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<StoreProduct> productList = storeProductService.list(wrapper);
        List<StoreProductResponse> responseList = new ArrayList<>();
        for (StoreProduct product : productList) {
            StoreProductResponse response = new StoreProductResponse();
            BeanUtils.copyProperties(product, response);
            responseList.add(response);
        }
        return CommonPage.restPage(CommonPage.copyPageInfo(page, responseList));
    }

    private StoreProduct checkProductExists(Integer productId) {
        StoreProduct product = storeProductService.getById(productId);
        if (product == null || Boolean.TRUE.equals(product.getIsDel())) {
            throw new CrmebException("商品不存在");
        }
        return product;
    }

    private StoreProductAttrValue findAttrValue(Integer productId, MobileAdminProductAttrValueUpdateRequest.AttrValueItem item,
                                                StoreProductAttrValue fallbackAttrValue) {
        boolean hasIdentifier = false;
        if (item.getId() != null && item.getId() > 0) {
            hasIdentifier = true;
            StoreProductAttrValue attrValue = storeProductAttrValueService.getByIdAndProductIdAndType(item.getId(), productId, Constants.PRODUCT_TYPE_NORMAL);
            if (attrValue != null) {
                return attrValue;
            }
        }
        if (StrUtil.isNotBlank(item.getUnique())) {
            hasIdentifier = true;
            StoreProductAttrValue query = new StoreProductAttrValue();
            query.setProductId(productId);
            query.setUnique(item.getUnique());
            query.setType(Constants.PRODUCT_TYPE_NORMAL);
            query.setIsDel(false);
            List<StoreProductAttrValue> attrValueList = storeProductAttrValueService.getByEntity(query);
            if (CollUtil.isNotEmpty(attrValueList)) {
                return attrValueList.get(0);
            }
        }
        if (!hasIdentifier && fallbackAttrValue != null) {
            return fallbackAttrValue;
        }
        throw new CrmebException("商品规格不存在");
    }

    private void refreshProductPriceAndStock(Integer productId) {
        List<StoreProductAttrValue> attrValueList = storeProductAttrValueService.getListByProductIdAndType(productId, Constants.PRODUCT_TYPE_NORMAL);
        Integer stock = 0;
        BigDecimal minPrice = null;
        for (StoreProductAttrValue attrValue : attrValueList) {
            if (attrValue.getStock() != null) {
                stock += attrValue.getStock();
            }
            if (attrValue.getPrice() != null && (minPrice == null || attrValue.getPrice().compareTo(minPrice) < 0)) {
                minPrice = attrValue.getPrice();
            }
        }
        StoreProduct product = new StoreProduct();
        product.setId(productId);
        product.setStock(stock);
        if (minPrice != null) {
            product.setPrice(minPrice);
        }
        storeProductService.updateById(product);
    }

    private StoreProductAddRequest buildCreateRequest(Map<String, Object> params) {
        String image = getFirstString(params, "image");
        String storeName = getFirstString(params, "storeName", "store_name");
        String cateId = getFirstIdListString(params, "cateId", "cate_id");
        String unitName = getFirstString(params, "unitName", "unit_name");
        Map<String, Object> attr = getMap(params.get("attr"));

        if (StrUtil.isBlank(image)) {
            throw new CrmebException("请上传商品图片");
        }
        if (StrUtil.isBlank(storeName)) {
            throw new CrmebException("请输入商品名称");
        }
        if (StrUtil.isBlank(cateId)) {
            throw new CrmebException("请选择商品分类");
        }
        if (StrUtil.isBlank(unitName)) {
            throw new CrmebException("请填写商品单位");
        }

        StoreProductAddRequest request = new StoreProductAddRequest();
        request.setImage(image);
        request.setSliderImage(joinStringList(params.get("slider_image"), image));
        request.setStoreName(storeName);
        request.setKeyword(defaultIfBlank(getFirstString(params, "keyword"), storeName));
        request.setCateId(cateId);
        request.setUnitName(unitName);
        request.setSort(getInteger(params, "sort", 0));
        request.setIsHot(false);
        request.setIsBenefit(false);
        request.setIsBest(false);
        request.setIsNew(false);
        request.setIsGood(false);
        request.setGiveIntegral(0);
        request.setIsSub(false);
        request.setFicti(0);
        request.setTempId(getInteger(params, "temp_id", getInteger(params, "tempId", 0)));
        request.setSpecType(parseBoolean(params.get("spec_type")));
        request.setActivity(Collections.singletonList(Constants.PRODUCT_TYPE_NORMAL_STR));
        request.setAttr(buildSingleAttr());
        request.setAttrValue(Collections.singletonList(buildSingleAttrValue(attr, image)));
        request.setContent(defaultIfBlank(getFirstString(params, "content"), ""));
        request.setCouponIds(Collections.emptyList());
        request.setFlatPattern(image);
        request.setGuaranteeIds("");
        return request;
    }

    private List<StoreProductAttrAddRequest> buildSingleAttr() {
        ProductAttrOptionAddRequest option = new ProductAttrOptionAddRequest();
        option.setOptionName("默认");
        option.setSort(0);

        StoreProductAttrAddRequest attr = new StoreProductAttrAddRequest();
        attr.setAttrName("规格");
        attr.setAttrValues("默认");
        attr.setIsShowImage(false);
        attr.setOptionList(Collections.singletonList(option));
        return Collections.singletonList(attr);
    }

    private StoreProductAttrValueAddRequest buildSingleAttrValue(Map<String, Object> attr, String image) {
        StoreProductAttrValueAddRequest attrValue = new StoreProductAttrValueAddRequest();
        attrValue.setProductId(0);
        attrValue.setStock(getInteger(attr, "stock", 0));
        attrValue.setPrice(getBigDecimal(attr, "price"));
        attrValue.setCost(getBigDecimal(attr, "cost"));
        attrValue.setOtPrice(getBigDecimal(attr, "ot_price"));
        attrValue.setImage(defaultIfBlank(getFirstString(attr, "pic", "image"), image));
        attrValue.setWeight(getBigDecimal(attr, "weight", BigDecimal.ZERO));
        attrValue.setVolume(getBigDecimal(attr, "volume", BigDecimal.ZERO));
        attrValue.setBrokerage(BigDecimal.ZERO);
        attrValue.setBrokerageTwo(BigDecimal.ZERO);
        attrValue.setAttrValue("{\"规格\":\"默认\"}");
        attrValue.setBarCode(getFirstString(attr, "bar_code", "barCode"));
        attrValue.setIsDefault(true);
        attrValue.setIsShow(true);
        return attrValue;
    }

    private Integer parseProductType(String rawType) {
        if (StrUtil.isBlank(rawType)) {
            return null;
        }
        Integer sourceType = Integer.valueOf(rawType);
        if (sourceType.equals(4)) {
            return 3;
        }
        if (sourceType.equals(5)) {
            return 4;
        }
        return sourceType;
    }

    private List<Integer> parseIdList(Object value) {
        List<Integer> ids = new ArrayList<>();
        if (value == null) {
            return ids;
        }
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            for (Object item : list) {
                addId(ids, item);
            }
            return ids;
        }
        String rawValue = String.valueOf(value);
        if (StrUtil.isBlank(rawValue)) {
            return ids;
        }
        ids.addAll(CrmebUtil.stringToArray(rawValue));
        return ids;
    }

    private void addId(List<Integer> ids, Object value) {
        if (value == null || StrUtil.isBlank(String.valueOf(value))) {
            return;
        }
        ids.add(Integer.valueOf(String.valueOf(value)));
    }

    private Boolean parseBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String rawValue = String.valueOf(value);
        return "1".equals(rawValue) || "true".equalsIgnoreCase(rawValue);
    }

    private String getFirstString(Map<String, Object> params, String... keys) {
        for (String key : keys) {
            String value = getString(params, key);
            if (StrUtil.isNotBlank(value)) {
                return value;
            }
        }
        return "";
    }

    private String getString(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    private String getFirstIdListString(Map<String, Object> params, String... keys) {
        for (String key : keys) {
            List<Integer> ids = parseIdList(params.get(key));
            if (CollUtil.isNotEmpty(ids)) {
                List<String> stringIds = new ArrayList<>();
                for (Integer id : ids) {
                    stringIds.add(String.valueOf(id));
                }
                return String.join(",", stringIds);
            }
        }
        return "";
    }

    private Integer getInteger(Map<String, Object> params, String key, Integer defaultValue) {
        String value = getString(params, key);
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }
        return Integer.valueOf(value);
    }

    private BigDecimal getBigDecimal(Map<String, Object> params, String key) {
        return getBigDecimal(params, key, null);
    }

    private BigDecimal getBigDecimal(Map<String, Object> params, String key, BigDecimal defaultValue) {
        String value = getString(params, key);
        if (StrUtil.isBlank(value)) {
            if (defaultValue == null) {
                throw new CrmebException("请填写商品规格" + key);
            }
            return defaultValue;
        }
        return new BigDecimal(value);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Object value) {
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return Collections.emptyMap();
    }

    private String joinStringList(Object value, String defaultValue) {
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            if (CollUtil.isEmpty(list)) {
                return defaultValue;
            }
            List<String> stringList = new ArrayList<>();
            for (Object item : list) {
                if (item != null && StrUtil.isNotBlank(String.valueOf(item))) {
                    stringList.add(String.valueOf(item));
                }
            }
            if (CollUtil.isNotEmpty(stringList)) {
                return String.join(",", stringList);
            }
        }
        if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
            return String.valueOf(value);
        }
        return defaultValue;
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StrUtil.isBlank(value) ? defaultValue : value;
    }
}
