package com.zbkj.front.controller.admin;

import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.StoreOrderSearchRequest;
import com.zbkj.common.response.StoreOrderDetailResponse;
import com.zbkj.common.response.StoreOrderInfoResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.MobileAdminPermissionService;
import com.zbkj.service.service.StoreOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 移动端商家售后订单
 */
@RestController
@RequestMapping("api/front/admin/refund_order")
@Api(tags = "移动端商家管理-售后")
public class MobileAdminRefundOrderController {

    @Autowired
    private MobileAdminPermissionService mobileAdminPermissionService;

    @Autowired
    private StoreOrderService storeOrderService;

    @ApiOperation(value = "售后订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<StoreOrderDetailResponse>> getList(StoreOrderSearchRequest request,
                                                                 @RequestParam Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        request.setType(2);
        String refundTypes = getString(params, "refundTypes");
        request.setStatus(normalizeRefundStatus(refundTypes));
        request.setRefundStatusAll(StrUtil.isBlank(refundTypes) || Constants.ORDER_STATUS_ALL.equals(refundTypes));
        request.setKeywordOrderSearch(false);
        request.setPayType("");
        String keywords = getFirstString(params, "keywords", "keyword", "content");
        if (StrUtil.isNotBlank(keywords)) {
            request.setSearchType("all");
            request.setContent(keywords);
        }
        CommonPage<StoreOrderDetailResponse> page = storeOrderService.getAdminList(request);
        return CommonResult.success(page.getList());
    }

    @ApiOperation(value = "售后订单详情")
    @RequestMapping(value = "/detail/{orderId}", method = RequestMethod.GET)
    public CommonResult<StoreOrderInfoResponse> detail(@PathVariable String orderId) {
        mobileAdminPermissionService.requireEnabledStaff();
        return CommonResult.success(storeOrderService.info(orderId));
    }

    @ApiOperation(value = "售后订单备注")
    @RequestMapping(value = "/remark", method = RequestMethod.POST)
    public CommonResult<Boolean> remark(@RequestBody Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        String orderNo = getFirstString(params, "orderNo", "order_id", "orderId");
        String remark = getFirstString(params, "remark", "mark");
        if (StrUtil.isBlank(orderNo)) {
            throw new CrmebException("订单号不能为空");
        }
        if (StrUtil.isBlank(remark)) {
            throw new CrmebException("备注不能为空");
        }
        return CommonResult.success(storeOrderService.mark(orderNo, remark));
    }

    private String normalizeRefundStatus(String refundTypes) {
        if (StrUtil.isBlank(refundTypes) || Constants.ORDER_STATUS_ALL.equals(refundTypes)) {
            return Constants.ORDER_STATUS_ALL;
        }
        if ("2".equals(refundTypes)) {
            return Constants.ORDER_STATUS_REFUNDED;
        }
        return Constants.ORDER_STATUS_REFUNDING;
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
        return value == null ? "" : String.valueOf(value);
    }
}
