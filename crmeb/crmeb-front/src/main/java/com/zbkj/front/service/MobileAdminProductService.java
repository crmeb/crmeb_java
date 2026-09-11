package com.zbkj.front.service;

import com.zbkj.common.model.express.ShippingTemplates;
import com.zbkj.common.model.product.StoreProductAttrValue;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.MobileAdminProductAttrValueUpdateRequest;
import com.zbkj.common.response.StoreProductResponse;
import com.zbkj.common.vo.CategoryTreeVo;

import java.util.List;
import java.util.Map;

/**
 * 移动端商家商品管理服务
 */
public interface MobileAdminProductService {

    CommonPage<StoreProductResponse> getList(Map<String, Object> params);

    Boolean setShow(Map<String, Object> params);

    List<CategoryTreeVo> getCate();

    Boolean saveCate(Map<String, Object> params);

    List<ShippingTemplates> getShippingTemp();

    Boolean create(Map<String, Object> params);

    List<StoreProductAttrValue> getAttr(Integer productId);

    Boolean updateAttr(Integer productId, MobileAdminProductAttrValueUpdateRequest request);
}
