package com.zbkj.front.controller.admin;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.express.Express;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.system.SystemStoreStaff;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.StoreOrderSearchRequest;
import com.zbkj.common.request.StoreOrderRefundRequest;
import com.zbkj.common.request.StoreOrderSendRequest;
import com.zbkj.common.request.StoreOrderTabsNumRequest;
import com.zbkj.common.request.StoreOrderUpdatePriceRequest;
import com.zbkj.common.response.StoreOrderCountItemResponse;
import com.zbkj.common.response.StoreOrderDetailResponse;
import com.zbkj.common.response.StoreOrderInfoResponse;
import com.zbkj.common.response.StoreOrderStatisticsResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.ExpressSheetVo;
import com.zbkj.common.vo.OrderInfoDetailVo;
import com.zbkj.common.vo.StoreOrderInfoOldVo;
import com.zbkj.front.service.MobileAdminPermissionService;
import com.zbkj.service.service.ExpressService;
import com.zbkj.service.service.OrderPaySuccessQueueService;
import com.zbkj.service.service.StoreOrderInfoService;
import com.zbkj.service.service.StoreOrderService;
import com.zbkj.service.service.SystemStoreStaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 移动端商家订单管理
 */
@RestController
@RequestMapping("api/front/admin/order")
@Api(tags = "移动端商家管理-订单")
public class MobileAdminOrderController {

    private static final String ORDER_STATUS_MOBILE_ALL = "mobileAll";
    private static final String ORDER_STATUS_NOT_SHIPPED_OR_WRITTEN_OFF = "notShippedOrToBeWrittenOff";

    @Autowired
    private MobileAdminPermissionService mobileAdminPermissionService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private SystemStoreStaffService systemStoreStaffService;

    @Autowired
    private ExpressService expressService;

    @Autowired
    private OrderPaySuccessQueueService orderPaySuccessQueueService;

    @ApiOperation(value = "订单统计")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> statistics() {
        mobileAdminPermissionService.requireEnabledStaff();
        Map<String, Object> data = new HashMap<>();
        String today = DateUtil.today();
        String yesterday = DateUtil.yesterday().toDateStr();
        String monthStart = DateUtil.beginOfMonth(DateUtil.date()).toDateStr();

        data.put("todayPrice", safeAmount(storeOrderService.getPayOrderAmountByDate(today)));
        data.put("proPrice", safeAmount(storeOrderService.getPayOrderAmountByDate(yesterday)));
        data.put("monthPrice", safeAmount(storeOrderService.getPayOrderAmountByPeriod(monthStart, today)));
        data.put("todayCount", storeOrderService.getPayOrderNumByDate(today));
        data.put("proCount", storeOrderService.getPayOrderNumByDate(yesterday));
        data.put("monthCount", getPayOrderCountByPeriod(monthStart, today));

        StoreOrderTabsNumRequest tabsNumRequest = new StoreOrderTabsNumRequest();
        tabsNumRequest.setType(2);
        StoreOrderCountItemResponse count = storeOrderService.getOrderStatusNum(tabsNumRequest);
        data.put("unpaid_count", count.getUnPaid());
        data.put("unshipped_count", count.getNotShipped());
        data.put("received_count", count.getSpike());
        data.put("evaluated_count", count.getBargain());
        data.put("refund_count", count.getRefunding());
        data.put("statusCount", count);
        return CommonResult.success(data);
    }

    @ApiOperation(value = "订单每日统计列表")
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public CommonResult<List<Map<String, Object>>> data(@RequestParam Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        Integer page = getInteger(params, "page", 1);
        Integer limit = getInteger(params, "limit", 10);
        int startOffset = Math.max(page - 1, 0) * limit;
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = startOffset; i < startOffset + limit; i++) {
            String date = DateUtil.offsetDay(DateUtil.date(), -i).toDateStr();
            Map<String, Object> item = new HashMap<>();
            item.put("time", date);
            item.put("count", storeOrderService.getPayOrderNumByDate(date));
            item.put("price", safeAmount(storeOrderService.getPayOrderAmountByDate(date)));
            list.add(item);
        }
        return CommonResult.success(list);
    }

    @ApiOperation(value = "订单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreOrderDetailResponse>> getList(StoreOrderSearchRequest request,
                                                                       @RequestParam Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        normalizeOrderSearchRequest(request, params);
        return CommonResult.success(storeOrderService.getAdminList(request));
    }

    @ApiOperation(value = "订单详情")
    @RequestMapping(value = "/detail/{orderId}", method = RequestMethod.GET)
    public CommonResult<StoreOrderInfoResponse> detail(@PathVariable String orderId) {
        mobileAdminPermissionService.requireEnabledStaff();
        return CommonResult.success(storeOrderService.info(orderId));
    }

    @ApiOperation(value = "订单改价")
    @RequestMapping(value = "/price", method = RequestMethod.POST)
    public CommonResult<StoreOrderInfoResponse> price(@RequestBody Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        String orderNo = getFirstString(params, "orderNo", "order_id", "orderId");
        String price = getFirstString(params, "payPrice", "price");
        if (StrUtil.isBlank(orderNo) || StrUtil.isBlank(price)) {
            throw new CrmebException("订单号和金额不能为空");
        }
        StoreOrderUpdatePriceRequest request = new StoreOrderUpdatePriceRequest();
        request.setOrderNo(orderNo);
        request.setPayPrice(new BigDecimal(price));
        storeOrderService.updatePrice(request);
        return CommonResult.success(storeOrderService.info(orderNo));
    }

    @ApiOperation(value = "订单备注")
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

    @ApiOperation(value = "订单退款")
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public CommonResult<Boolean> refund(@RequestBody Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        StoreOrderRefundRequest request = buildRefundRequest(params);
        return CommonResult.success(storeOrderService.refund(request));
    }

    @ApiOperation(value = "拒绝退款")
    @RequestMapping(value = "/refund/refuse", method = RequestMethod.POST)
    public CommonResult<Boolean> refundRefuse(@RequestBody Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        String orderNo = getFirstString(params, "orderNo", "order_id", "orderId");
        String reason = getFirstString(params, "reason", "remark");
        if (StrUtil.isBlank(orderNo)) {
            throw new CrmebException("订单号不能为空");
        }
        if (StrUtil.isBlank(reason)) {
            throw new CrmebException("拒绝原因不能为空");
        }
        return CommonResult.success(storeOrderService.refundRefuse(orderNo, reason));
    }

    @ApiOperation(value = "线下付款订单确认付款")
    @RequestMapping(value = "/offline", method = RequestMethod.POST)
    public CommonResult<Boolean> offline(@RequestBody Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        StoreOrder storeOrder = findOrder(getFirstString(params, "orderNo", "order_id", "orderId", "id"));
        if (!Constants.PAY_TYPE_OFFLINE.equals(storeOrder.getPayType())) {
            throw new CrmebException("当前订单不是线下付款订单");
        }
        if (Boolean.TRUE.equals(storeOrder.getPaid())) {
            return CommonResult.success(Boolean.TRUE);
        }
        Boolean result = storeOrderService.updatePaid(storeOrder.getOrderId());
        if (Boolean.TRUE.equals(result)) {
            orderPaySuccessQueueService.enqueue(storeOrder.getOrderId());
        }
        return CommonResult.success(result);
    }

    @ApiOperation(value = "同意退货")
    @RequestMapping(value = "/agreeExpress", method = RequestMethod.POST)
    public CommonResult<Boolean> agreeExpress(@RequestBody Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        Integer id = getInteger(params, "id", 0);
        if (id <= 0) {
            throw new CrmebException("订单ID不能为空");
        }
        StoreOrder storeOrder = storeOrderService.getById(id);
        if (storeOrder == null) {
            throw new CrmebException("订单不存在");
        }
        StoreOrder updateOrder = new StoreOrder();
        updateOrder.setId(id);
        updateOrder.setRefundStatus(3);
        updateOrder.setUpdateTime(DateUtil.date());
        return CommonResult.success(storeOrderService.updateById(updateOrder));
    }

    @ApiOperation(value = "订单发货信息")
    @RequestMapping(value = "/delivery/gain/{orderId}", method = RequestMethod.GET)
    public CommonResult<StoreOrderInfoResponse> deliveryGain(@PathVariable String orderId) {
        mobileAdminPermissionService.requireEnabledStaff();
        return CommonResult.success(storeOrderService.info(orderId));
    }

    @ApiOperation(value = "获取订单可拆分商品列表")
    @RequestMapping(value = "/split_cart_info/{id}", method = RequestMethod.GET)
    public CommonResult<List<Map<String, Object>>> splitCartInfo(@PathVariable Integer id) {
        mobileAdminPermissionService.requireEnabledStaff();
        return CommonResult.success(buildSplitCartInfo(id));
    }

    @ApiOperation(value = "拆单发货")
    @RequestMapping(value = "/split_delivery/{id}", method = RequestMethod.PUT)
    public CommonResult<Object> splitDelivery(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        return CommonResult.failed("当前项目暂未配置拆单发货能力，请使用整单发货");
    }

    @ApiOperation(value = "订单发货保存")
    @RequestMapping(value = "/delivery/keep/{id}", method = RequestMethod.POST)
    public CommonResult<String> deliveryKeep(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        mobileAdminPermissionService.requireEnabledStaff();
        StoreOrderSendRequest request = buildSendRequest(id, params);
        return CommonResult.success(storeOrderService.send(request));
    }

    @ApiOperation(value = "电子面单模板")
    @RequestMapping(value = "/export_temp", method = RequestMethod.GET)
    public CommonResult<Object> exportTemp(@RequestParam(value = "com", required = false, defaultValue = "") String com) {
        mobileAdminPermissionService.requireEnabledStaff();
        if (StrUtil.isBlank(com)) {
            return CommonResult.success(emptyExportTemp());
        }
        try {
            return CommonResult.success(expressService.template(com));
        } catch (Exception e) {
            return CommonResult.success(emptyExportTemp());
        }
    }

    @ApiOperation(value = "快递公司列表")
    @RequestMapping(value = "/logistics", method = RequestMethod.GET)
    public CommonResult<List<Express>> logistics(@RequestParam(value = "type", required = false, defaultValue = "normal") String type) {
        mobileAdminPermissionService.requireEnabledStaff();
        String expressType = "elec".equals(type) ? "elec" : "normal";
        return CommonResult.success(expressService.findAll(expressType));
    }

    @ApiOperation(value = "配送员列表")
    @RequestMapping(value = "/delivery", method = RequestMethod.GET)
    public CommonResult<List<Map<String, Object>>> delivery() {
        mobileAdminPermissionService.requireEnabledStaff();
        QueryWrapper<SystemStoreStaff> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByDesc("id");
        List<SystemStoreStaff> staffList = systemStoreStaffService.list(wrapper);
        List<Map<String, Object>> list = new ArrayList<>();
        for (SystemStoreStaff staff : staffList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", staff.getId());
            item.put("uid", staff.getUid());
            item.put("wx_name", staff.getStaffName());
            item.put("staff_name", staff.getStaffName());
            item.put("phone", staff.getPhone());
            item.put("avatar", staff.getAvatar());
            list.add(item);
        }
        return CommonResult.success(list);
    }

    @ApiOperation(value = "订单统计详情")
    @RequestMapping(value = "/time", method = RequestMethod.GET)
    public CommonResult<StoreOrderStatisticsResponse> time(@RequestParam(value = "dateLimit", defaultValue = "today") String dateLimit,
                                                           @RequestParam(value = "type", defaultValue = "1") String type) {
        mobileAdminPermissionService.requireEnabledStaff();
        return CommonResult.success(storeOrderService.orderStatisticsByTime(dateLimit, parseStatisticsType(type)));
    }

    @ApiOperation(value = "订单发货默认配置")
    @RequestMapping(value = "/delivery_info", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> deliveryInfo() {
        mobileAdminPermissionService.requireEnabledStaff();
        ExpressSheetVo sheet = storeOrderService.getDeliveryInfo();
        Map<String, Object> data = new HashMap<>();
        data.put("to_name", sheet.getExportToName());
        data.put("to_tel", sheet.getExportToTel());
        data.put("to_add", sheet.getExportToAddress());
        data.put("export_id", sheet.getExportId());
        data.put("export_com", sheet.getExportCom());
        data.put("export_temp_id", sheet.getExportTempId());
        data.put("export_siid", sheet.getExportSiid());
        data.put("export_open", sheet.getExportOpen());
        return CommonResult.success(data);
    }

    private void normalizeOrderSearchRequest(StoreOrderSearchRequest request, Map<String, Object> params) {
        request.setType(getInteger(params, "type", 2));
        String status = getFirstString(params, "status", "types");
        request.setStatus(normalizeOrderStatus(status));
        String orderNo = getFirstString(params, "orderNo", "order_id", "orderId");
        if (StrUtil.isNotBlank(orderNo)) {
            request.setOrderNo(orderNo);
        }
        String keywords = getFirstString(params, "content", "keyword", "keywords");
        if (StrUtil.isNotBlank(keywords)) {
            request.setSearchType("all");
            request.setContent(keywords);
            request.setKeywordOrderSearch(true);
        }
        String dateLimit = getFirstString(params, "dateLimit", "date", "data");
        if (StrUtil.isNotBlank(dateLimit)) {
            request.setDateLimit(dateLimit);
        }
        String deliveryId = getFirstString(params, "deliveryId", "delivery_id");
        if (StrUtil.isNotBlank(deliveryId)) {
            request.setDeliveryId(deliveryId);
        }
        String payType = normalizePayType(getFirstString(params, "payType", "pay_type"));
        if (StrUtil.isNotBlank(payType)) {
            request.setPayType(payType);
        }
    }

    private String normalizeOrderStatus(String status) {
        if (StrUtil.isBlank(status) || "-1".equals(status) || Constants.ORDER_STATUS_ALL.equals(status)) {
            return ORDER_STATUS_MOBILE_ALL;
        }
        if ("0".equals(status)) {
            return Constants.ORDER_STATUS_UNPAID;
        }
        if ("1".equals(status)) {
            return ORDER_STATUS_NOT_SHIPPED_OR_WRITTEN_OFF;
        }
        if ("2".equals(status)) {
            return Constants.ORDER_STATUS_SPIKE;
        }
        if ("3".equals(status)) {
            return Constants.ORDER_STATUS_BARGAIN;
        }
        if ("4".equals(status)) {
            return Constants.ORDER_STATUS_COMPLETE;
        }
        if ("8".equals(status)) {
            return Constants.ORDER_STATUS_TOBE_WRITTEN_OFF;
        }
        if ("-3".equals(status)) {
            return Constants.ORDER_STATUS_REFUNDING;
        }
        return status;
    }

    private String normalizePayType(String payType) {
        if (StrUtil.isBlank(payType)) {
            return "";
        }
        switch (payType) {
            case "1":
                return Constants.PAY_TYPE_WE_CHAT;
            case "2":
                return Constants.PAY_TYPE_YUE;
            case "3":
                return Constants.PAY_TYPE_OFFLINE;
            case "4":
                return Constants.PAY_TYPE_ALI_PAY;
            default:
                return payType;
        }
    }

    private StoreOrderSendRequest buildSendRequest(Integer id, Map<String, Object> params) {
        StoreOrder order = storeOrderService.getById(id);
        if (order == null) {
            throw new CrmebException("订单不存在");
        }
        StoreOrderSendRequest request = new StoreOrderSendRequest();
        request.setId(id);
        request.setOrderNo(order.getOrderId());
        request.setDeliveryType(normalizeDeliveryType(getFirstString(params, "deliveryType", "delivery_type", "type")));
        request.setExpressName(getFirstString(params, "expressName", "delivery_name"));
        request.setExpressCode(getFirstString(params, "expressCode", "delivery_code"));
        request.setExpressNumber(getFirstString(params, "expressNumber", "delivery_id"));
        request.setExpressRecordType(defaultIfBlank(getFirstString(params, "expressRecordType", "express_record_type"), "1"));
        request.setExpressTempId(getFirstString(params, "expressTempId", "express_temp_id"));
        request.setToName(getFirstString(params, "toName", "to_name"));
        request.setToTel(getFirstString(params, "toTel", "to_tel"));
        request.setToAddr(getFirstString(params, "toAddr", "to_addr"));
        request.setDeliveryName(getFirstString(params, "deliveryName", "sh_delivery_name", "delivery_name"));
        request.setDeliveryTel(getFirstString(params, "deliveryTel", "sh_delivery_id", "delivery_id", "phone"));
        return request;
    }

    private StoreOrder findOrder(String orderId) {
        if (StrUtil.isBlank(orderId)) {
            throw new CrmebException("订单号不能为空");
        }
        StoreOrder storeOrder = storeOrderService.getByOderId(orderId);
        if (storeOrder == null && orderId.matches("\\d+")) {
            storeOrder = storeOrderService.getById(Integer.valueOf(orderId));
        }
        if (storeOrder == null) {
            throw new CrmebException("订单不存在");
        }
        return storeOrder;
    }

    private List<Map<String, Object>> buildSplitCartInfo(Integer orderId) {
        List<StoreOrderInfoOldVo> orderInfoList = storeOrderInfoService.getOrderListByOrderId(orderId);
        if (orderInfoList == null) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (StoreOrderInfoOldVo orderInfo : orderInfoList) {
            OrderInfoDetailVo info = orderInfo.getInfo();
            Integer payNum = info.getPayNum() == null ? 0 : info.getPayNum();
            Map<String, Object> item = new HashMap<>();
            item.put("id", orderInfo.getId());
            item.put("cart_id", orderInfo.getId());
            item.put("cart_num", payNum);
            item.put("surplus_num", payNum);
            item.put("cart_info", buildCartInfo(orderInfo, info, payNum));
            list.add(item);
        }
        return list;
    }

    private Map<String, Object> buildCartInfo(StoreOrderInfoOldVo orderInfo, OrderInfoDetailVo info, Integer payNum) {
        BigDecimal price = info.getPrice() == null ? BigDecimal.ZERO : info.getPrice();
        Map<String, Object> attrInfo = new HashMap<>();
        attrInfo.put("suk", defaultIfBlank(info.getSku(), "默认"));
        attrInfo.put("price", price);
        attrInfo.put("image", info.getImage());

        Map<String, Object> productInfo = new HashMap<>();
        productInfo.put("id", info.getProductId());
        productInfo.put("image", info.getImage());
        productInfo.put("store_name", info.getProductName());
        productInfo.put("price", price);
        productInfo.put("ot_price", price);
        productInfo.put("attrInfo", attrInfo);

        Map<String, Object> cartInfo = new HashMap<>();
        cartInfo.put("id", orderInfo.getId());
        cartInfo.put("cart_num", payNum);
        cartInfo.put("sum_true_price", price.multiply(new BigDecimal(payNum)));
        cartInfo.put("truePrice", price.multiply(new BigDecimal(payNum)));
        cartInfo.put("productInfo", productInfo);
        return cartInfo;
    }

    private Map<String, Object> emptyExportTemp() {
        Map<String, Object> data = new HashMap<>();
        data.put("data", Collections.emptyList());
        return data;
    }

    private StoreOrderRefundRequest buildRefundRequest(Map<String, Object> params) {
        String orderNo = getFirstString(params, "orderNo", "order_id", "orderId");
        String amount = getFirstString(params, "amount", "price", "refund_price");
        if (StrUtil.isBlank(orderNo)) {
            throw new CrmebException("订单号不能为空");
        }
        if (StrUtil.isBlank(amount)) {
            throw new CrmebException("退款金额不能为空");
        }
        StoreOrderRefundRequest request = new StoreOrderRefundRequest();
        request.setOrderNo(orderNo);
        request.setAmount(new BigDecimal(amount));
        Integer orderId = getInteger(params, "id", 0);
        if (orderId > 0) {
            request.setOrderId(orderId);
        }
        return request;
    }

    private String normalizeDeliveryType(String deliveryType) {
        if ("1".equals(deliveryType) || "express".equals(deliveryType)) {
            return "express";
        }
        if ("2".equals(deliveryType) || "send".equals(deliveryType)) {
            return "send";
        }
        if ("3".equals(deliveryType) || "fictitious".equals(deliveryType)) {
            return "fictitious";
        }
        return deliveryType;
    }

    private Integer parseStatisticsType(String type) {
        if ("price".equals(type)) {
            return 1;
        }
        if ("order".equals(type)) {
            return 2;
        }
        return Integer.valueOf(type);
    }

    private Integer getPayOrderCountByPeriod(String startDate, String endDate) {
        QueryWrapper<StoreOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("paid", 1);
        wrapper.apply("date_format(create_time, '%Y-%m-%d') between {0} and {1}", startDate, endDate);
        return storeOrderService.count(wrapper);
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String getFirstString(Map<String, Object> params, String... keys) {
        for (String key : keys) {
            Object value = params.get(key);
            if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
                return String.valueOf(value);
            }
        }
        return "";
    }

    private Integer getInteger(Map<String, Object> params, String key, Integer defaultValue) {
        Object value = params.get(key);
        if (value == null || StrUtil.isBlank(String.valueOf(value))) {
            return defaultValue;
        }
        return Integer.valueOf(String.valueOf(value));
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StrUtil.isBlank(value) ? defaultValue : value;
    }
}
