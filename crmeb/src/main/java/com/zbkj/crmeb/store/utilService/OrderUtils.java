package com.zbkj.crmeb.store.utilService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.express.model.ShippingTemplates;
import com.zbkj.crmeb.express.model.ShippingTemplatesFree;
import com.zbkj.crmeb.express.model.ShippingTemplatesRegion;
import com.zbkj.crmeb.express.service.ShippingTemplatesFreeService;
import com.zbkj.crmeb.express.service.ShippingTemplatesRegionService;
import com.zbkj.crmeb.express.service.ShippingTemplatesService;
import com.zbkj.crmeb.front.request.OrderCreateRequest;
import com.zbkj.crmeb.front.response.ComputeOrderResponse;
import com.zbkj.crmeb.front.response.ConfirmOrderResponse;
import com.zbkj.crmeb.front.response.PriceGroupResponse;
import com.zbkj.crmeb.front.vo.OrderAgainItemVo;
import com.zbkj.crmeb.front.vo.OrderAgainVo;
import com.zbkj.crmeb.front.vo.PriceGroup;
import com.zbkj.crmeb.front.vo.PriceItem;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.request.StoreCouponUserSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreCouponUserResponse;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreOrderInfo;
import com.zbkj.crmeb.store.model.StoreOrderStatus;
import com.zbkj.crmeb.store.request.StoreOrderSearchRequest;
import com.zbkj.crmeb.store.request.StoreProductRequest;
import com.zbkj.crmeb.store.response.StoreCartResponse;
import com.zbkj.crmeb.store.response.StoreProductCartProductInfoResponse;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import com.zbkj.crmeb.system.model.SystemStore;
import com.zbkj.crmeb.system.response.SystemGroupDataOrderStatusPicResponse;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.system.service.SystemGroupService;
import com.zbkj.crmeb.system.service.SystemStoreService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserAddress;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.service.UserAddressService;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author stivepeim
 * @title: OrderUtils
 * @projectName crmeb
 * @description: 订单工具类
 * @date 2020/7/2019:29
 */
@Service
public class OrderUtils {
    @Autowired
    private SystemStoreService systemStoreService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private ShippingTemplatesService shippingTemplatesService;

    @Autowired
    private ShippingTemplatesRegionService shippingTemplatesRegionService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ShippingTemplatesFreeService shippingTemplatesFreeService;

    @Autowired
    private StoreCartService storeCartService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    // 组装数据给前端使用
    public OrderAgainVo tidyOrder(StoreOrder storeOrder, boolean detail, boolean isPic){
//        User currentUser = userService.getInfo();
        OrderAgainVo orderAgainVoResult = new OrderAgainVo();
        orderAgainVoResult.setStoreOrder(storeOrder);
        List<StoreOrderInfoVo> existOrderList = null;
        if(detail && storeOrder.getId() > 0){
            existOrderList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
            orderAgainVoResult.setCartInfo(existOrderList);
        }
        OrderAgainItemVo status = null;
        if(storeOrder.getId() > 0
                && storeOrder.getPayType().equals(Constants.PAY_TYPE_OFFLINE)
                && storeOrder.getStatus() >= 2){
            status = new OrderAgainItemVo(9,"线下付款","等待商家处理，请耐心等候");
        }else if(!storeOrder.getPaid()){
            status = new OrderAgainItemVo(0,"未支付","订单未支付");
            // 系统预设取消订单时间段
            List<String> configKeys = new ArrayList<>();
            configKeys.add("order_cancel_time");
            configKeys.add("order_activity_time");
            configKeys.add("order_bargain_time");
            configKeys.add("order_seckill_time");
            configKeys.add("order_pink_time");
            List<String> configValues = systemConfigService.getValuesByKes(configKeys);
            // 拼团秒杀砍价逻辑处理
            String tempTime = null;
            Date timeSpace = null;
//            if(storeOrder.getPinkId() > 0 || storeOrder.getCombinationId() > 0){
//                tempTime = configValues.get(4).length() > 0 ? configValues.get(4) : configValues.get(1);
//                timeSpace = DateUtil.addSecond(storeOrder.getCreateTime(),Integer.parseInt(tempTime) * 3600);
//            }else if(storeOrder.getSeckillId() > 0){
//                tempTime = configValues.get(3).length() > 0 ? configValues.get(3) : configValues.get(1);
//                timeSpace = DateUtil.addSecond(storeOrder.getCreateTime(), Integer.parseInt(tempTime) * 3600);
//            }else if(storeOrder.getBargainId() > 0){
//                tempTime = configValues.get(2).length() > 0 ? configValues.get(2) : configValues.get(1);
//                timeSpace = DateUtil.addSecond(storeOrder.getCreateTime(), Integer.parseInt(tempTime) * 3600);
//            }else{ // 非 营销活动购买处理
                timeSpace = DateUtil.addSecond(storeOrder.getCreateTime(),Double.valueOf(configValues.get(0).toString()).intValue() * 3600);
//            }
            status.setMsg("请在" + DateUtil.dateToStr(timeSpace, Constants.DATE_FORMAT) +"前完成支付");
        }else if(storeOrder.getRefundStatus() == 1){
            status = new OrderAgainItemVo(-1,"申请退款中","商家审核中,请耐心等待");
        }else if(storeOrder.getRefundStatus() == 2){
            status = new OrderAgainItemVo(-2,"已退款","已为您退款,感谢您的支持");
        }else if(storeOrder.getStatus() == 0){
            // todo 秒杀后面在这添加判断
            status = new OrderAgainItemVo(1,"未发货","商家未发货,请耐心等待");
        }else if(storeOrder.getStatus() == 1){ // 待收货处理
            // 待收货
            if(storeOrder.getDeliveryType().equals(Constants.ORDER_STATUS_STR_SPIKE_KEY)){ // 送货
                StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
                storeOrderStatus.setOid(storeOrder.getId());
                storeOrderStatus.setChangeType(Constants.ORDER_LOG_DELIVERY);
                List<StoreOrderStatus> sOrderStatusResults = storeOrderStatusService.getByEntity(storeOrderStatus);
                if(sOrderStatusResults.size()>0){
                    String DateStr = DateUtil.dateToStr(sOrderStatusResults.get(sOrderStatusResults.size()-1).getCreateTime(), Constants.DATE_FORMAT);
                    status = new OrderAgainItemVo(2,"待收货",DateStr+"服务商已送货");
                }
            }else if(storeOrder.getDeliveryType().equals(Constants.ORDER_LOG_EXPRESS)) {
                StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
                storeOrderStatus.setOid(storeOrder.getId());
                storeOrderStatus.setChangeType(Constants.ORDER_LOG_EXPRESS);
                List<StoreOrderStatus> sOrderStatusResults = storeOrderStatusService.getByEntity(storeOrderStatus);
                if(sOrderStatusResults.size()>0){
                    String DateStr = DateUtil.dateToStr(sOrderStatusResults.get(sOrderStatusResults.size()-1).getCreateTime(), Constants.DATE_FORMAT);
                    status = new OrderAgainItemVo(2,"待收货",DateStr+"服务商已送货");
                }
            }else {
                    StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
                    storeOrderStatus.setOid(storeOrder.getId());
                    storeOrderStatus.setChangeType(Constants.ORDER_LOG_DELIVERY_VI);
                    List<StoreOrderStatus> sOrderStatusResults = storeOrderStatusService.getByEntity(storeOrderStatus);
                    if(sOrderStatusResults.size()>0){
                        String DateStr = DateUtil.dateToStr(sOrderStatusResults.get(sOrderStatusResults.size()-1).getCreateTime(), Constants.DATE_FORMAT);
                        status = new OrderAgainItemVo(2,"待收货",DateStr+"服务商已虚拟发货");
                    }
            }
        }else if(storeOrder.getStatus() == 2){
            status = new OrderAgainItemVo(3,"待评价","已收货,快去评价一下吧");

        }else if(storeOrder.getStatus() == 3){
            status = new OrderAgainItemVo(4,"交易完成","'交易完成,感谢您的支持");
        }
        // 支付方式
        if(status != null){
            if(null == storeOrder.getStatus()){
                status.setPayType(StringUtils.isNotBlank(storeOrder.getPayType()) ? storeOrder.getPayType() : "其他方式");
            }
            if(StringUtils.isNotBlank(storeOrder.getDeliveryType())){
                status.setDeliveryType(StringUtils.isNotBlank(storeOrder.getDeliveryType()) ? storeOrder.getDeliveryType():"其他方式");
            }
        }

        orderAgainVoResult.setStatus(status);
        orderAgainVoResult.setPayTime(null != storeOrder.getPayTime() ? storeOrder.getPayTime().toString() : storeOrder.getCreateTime().toString());
        orderAgainVoResult.setAddTime(null != storeOrder.getCreateTime() ? storeOrder.getCreateTime().toString() : "");
        orderAgainVoResult.setStatusPic("");

        // 获取商品状态图片 ignore
        if(isPic){
            List<SystemGroupDataOrderStatusPicResponse> orderStatusPicList = systemGroupDataService.getListByGid(Constants.GROUP_DATA_ID_ORDER_STATUS_PIC, SystemGroupDataOrderStatusPicResponse.class);// 53 = group id 在groupData中查询数据

            for (SystemGroupDataOrderStatusPicResponse picList : orderStatusPicList) {
                if(picList.getOrderStatus() == orderAgainVoResult.getStatus().getType()){
                    orderAgainVoResult.setStatusPic(picList.getUrl());
                    break;
                }
            }
        }
        String offlinePayStatus = systemConfigService.getValueByKey("offline_pay_status");
        int offlinePayStatusFlag = StringUtils.isNotBlank(offlinePayStatus) ? Integer.parseInt(offlinePayStatus) : 2;
        orderAgainVoResult.setOfflinePayStatus(offlinePayStatusFlag);
        return orderAgainVoResult;
    }

    /**
     * 缓存订单并做计算
     * @param request
     */
    public ComputeOrderResponse computedOrder(OrderCreateRequest request, ConfirmOrderResponse cor, String orderKey){
        // 检查订单是否存在
        User current = userService.getInfo();
        if(checkOrderExist(orderKey, current.getUid())) throw new CrmebException(orderKey + "订单已存在");

        // 需要return的自定义变量
        BigDecimal couponPrice = BigDecimal.ZERO;
        BigDecimal surPlusIntegral = BigDecimal.ZERO;
        BigDecimal deductionPrice = BigDecimal.ZERO;
        BigDecimal usedIntegral = BigDecimal.ZERO;
        BigDecimal payPrice = cor.getPriceGroup().getTotalPrice();

        User currentUser = userService.getInfo();

        Integer offliePayStatus = Integer.valueOf(systemConfigService.getValueByKey("offline_pay_status"));
        List<String> exsitPayType = getPayType().stream().filter(e->{
            if(offliePayStatus == 2 && e == Constants.PAY_TYPE_OFFLINE){
                return false;
            }
            return true;
        }).distinct().collect(Collectors.toList());
        if(exsitPayType.size() == 0) throw new CrmebException("选择支付方式有误");

        // 检测订单是否重复提交
        if(checkOrderSubmitAgain(orderKey)) throw new CrmebException("请勿重复提交订单");

        if(null == currentUser) throw new CrmebException("用户不存在");

        // 判断邮费是否正确
        BigDecimal payPostage;
        if(request.getPayType().equals("offline") && systemConfigService.getValueByKey("offline_postage").equals("1")){
            payPostage = BigDecimal.ZERO;
        }else{
            UserAddress ua = new UserAddress();
            ua.setUid(currentUser.getUid());
            ua.setId(request.getAddressId());
            UserAddress currentUserAddress = userAddressService.getUserAddress(ua);
            PriceGroupResponse currentOrderPriceGroup = getOrderPriceGroup(cor.getCartInfo(), currentUserAddress);
            payPostage = currentOrderPriceGroup.getStorePostage();
        }

        // 检测优惠券
        Integer couponId = null != request.getCouponId() ? request.getCouponId() : 0;
        if(couponId > 0){
            //取优惠券的信息,看是否需要判断 商品券或者品类券
            List<Integer> primaryKey = new ArrayList<>();
            StoreCouponUser storeCouponUser = storeCouponUserService.getById(couponId);

            if (storeCouponUser.getUseType() > 1){
                //此处拿到所有的商品id
                primaryKey = cor.getCartInfo().stream().map(StoreCartResponse::getProductId).distinct().collect(Collectors.toList());
            }
            boolean couponUseResult = storeCouponUserService.use(couponId, primaryKey, payPrice);
            if(!couponUseResult) throw new CrmebException("使用优惠券失败");

            // 减去优惠券金额  后使用优惠券
            payPrice = payPrice.subtract(storeCouponUser.getMoney());
            couponPrice = storeCouponUser.getMoney();
        }


        // 发快递还是门店自提
        String storeSelfMention = systemConfigService.getValueByKey("store_self_mention");
        int shippingType = 0;

        if(Boolean.valueOf(storeSelfMention)) shippingType = 1;
        if(shippingType == 1){ // 是否包邮
            if(StringUtils.isNotBlank(cor.getOther().get("offlinePostage").toString())
                    && request.getPayType().equals(Constants.PAY_TYPE_OFFLINE)){
                payPostage = BigDecimal.ZERO;
            }
            payPrice = payPrice.add(payPostage);
        }else if(shippingType == 2){ // 门店自提没有邮费
            cor.getPriceGroup().setStorePostage(BigDecimal.ZERO);
            payPostage = BigDecimal.ZERO;
        }

        // 积分
        if(null != request.getUseIntegral() && currentUser.getIntegral().compareTo(BigDecimal.ZERO) > 0){
            deductionPrice = currentUser.getIntegral().multiply(BigDecimal.valueOf(Double.valueOf(cor.getOther().get("integralRatio").toString())));
            if(deductionPrice.compareTo(payPrice) < 0){
                payPrice = payPrice.subtract(deductionPrice);
                usedIntegral = currentUser.getIntegral();
            }else{
                deductionPrice = payPrice;
                if(payPrice.compareTo(BigDecimal.ZERO) > 0 && usedIntegral.compareTo(BigDecimal.ZERO) > 0){
                    usedIntegral = payPrice.divide(usedIntegral);
                    surPlusIntegral = currentUser.getIntegral().subtract(usedIntegral);
                }
                payPrice = BigDecimal.ZERO;
            }
        }else{
            deductionPrice = BigDecimal.ZERO;
            usedIntegral = BigDecimal.ZERO;
        }
        if(payPrice.compareTo(BigDecimal.ZERO) <= 0) payPrice = BigDecimal.ZERO;

//        HashMap<String, Object> resultMap = new HashMap<>();
        // 是否生成订单
//        if(isCreateOrder){
        // todo 秒杀在这里需要判断库存

//        StoreOrder orderCreated = createOrder(request, cor, 0, request.getStoreId());
//        resultMap.put("orderCreate", orderCreated);
//        }
        ComputeOrderResponse result = new ComputeOrderResponse();
        // 组装返回数据
        result.setTotalPrice(cor.getPriceGroup().getTotalPrice());
        result.setPayPrice(payPrice);
        result.setPayPostage(payPostage);
        result.setCouponPrice(couponPrice);
        result.setDeductionPrice(deductionPrice);
        result.setUsedIntegral(usedIntegral);
        result.setSurplusIntegral(surPlusIntegral);
        return result;
    }

    /**
     * 创建订单
     * @param request 订单创建参数
     * @param cor 缓存的购物车，价钱等信息集合
     * @param isChannel 购买渠道
     * @param orderId 订单id
     * @return 订单信息
     */
    public StoreOrder createOrder(OrderCreateRequest request,ConfirmOrderResponse cor, Integer isChannel,Integer orderId, String orderKey){
        UserAddress currentUserAddress = new UserAddress();
        List<Integer> cartIds = new ArrayList<>();
        Integer totalNum = 0;
        BigDecimal gainIntegral = BigDecimal.ZERO;

        // todo 开启事务

        if(request.getShippingType() == 1){ // 是否包邮
            if(request.getAddressId() <= 0) throw new CrmebException("请选择收货地址");
            UserAddress userAddress = new UserAddress();
            userAddress.setId(request.getAddressId());
            userAddress.setIsDel(false);
            if(null == userAddressService.getUserAddress(userAddress)){
                throw new CrmebException("收货地址有误");
            }
        }else{ // 到店自提
            if(StringUtils.isBlank(cor.getAddressInfo().getRealName()) || StringUtils.isBlank(cor.getAddressInfo().getRealName()))
                throw new CrmebException("请填写姓名和电话");

            currentUserAddress.setRealName(cor.getAddressInfo().getRealName());
            currentUserAddress.setPhone(cor.getAddressInfo().getRealName());
        }
        for (StoreCartResponse cartResponse : cor.getCartInfo()) {
            cartIds.add(cartResponse.getSeckillId());
            totalNum += cartResponse.getCartNum();
            // todo 秒杀拼团砍价 二期

            BigDecimal cartInfoGainIntegral =
                    cartResponse.getProductInfo().getGiveIntegral().compareTo(BigDecimal.ZERO) > 0 ?
                            cartResponse.getProductInfo().getGiveIntegral().multiply(BigDecimal.valueOf(cartResponse.getCartNum().doubleValue()))
                            :BigDecimal.ZERO;
            gainIntegral = gainIntegral.add(cartInfoGainIntegral);
        }
        // todo 检测营销产品状态
        // 发快递还是门店自提
        String storeSelfMention = systemConfigService.getValueByKey("store_self_mention");
        if(Boolean.valueOf(storeSelfMention)) request.setShippingType(1);

        StoreOrder storeOrder = new StoreOrder();
        storeOrder.setUid(cor.getUserInfo().getUid());
        storeOrder.setOrderId(CrmebUtil.getOrderNo(Constants.PAY_TYPE_WE_CHAT));

        // 后置选择收货地址信息
        if(null == cor.getAddressInfo() && request.getAddressId() > 0){
            UserAddress selectUserAddress = userAddressService.getById(request.getAddressId());
            cor.setAddressInfo(selectUserAddress);
        }

        storeOrder.setRealName(cor.getAddressInfo().getRealName());
        storeOrder.setUserPhone(cor.getAddressInfo().getPhone());
        storeOrder.setUserAddress(cor.getAddressInfo().getProvince()
                + cor.getAddressInfo().getCity()
                + cor.getAddressInfo().getDistrict()
                + cor.getAddressInfo().getDetail());
//        storeOrder.setCarId(cartIds);
        storeOrder.setTotalNum(totalNum);
        BigDecimal mapTotalPrice = cor.getPriceGroup().getTotalPrice();
        BigDecimal mapStorePostage = cor.getPriceGroup().getStorePostage();
        BigDecimal mapCouponPrice = cor.getPriceGroup().getCouponPrice();
        BigDecimal mapPayPrice = cor.getPriceGroup().getPayPrice();
        BigDecimal mapPayPostage = cor.getPriceGroup().getPayPostage();
        BigDecimal mapDeductionPrice = cor.getPriceGroup().getDeductionPrice();
        BigDecimal mapUsedIntegral = cor.getPriceGroup().getUsedIntegral();
        BigDecimal mapCostPrice = cor.getPriceGroup().getCostPrice();

        int couponId = null == cor.getUsableCoupon() ? 0: cor.getUsableCoupon().getId();
        storeOrder.setTotalPrice(mapTotalPrice);
        storeOrder.setTotalPostage(mapStorePostage);
        storeOrder.setCouponId(couponId);
        storeOrder.setCouponPrice(mapCouponPrice);
        storeOrder.setPayPrice(mapPayPrice);
        storeOrder.setPayPostage(mapPayPostage);
        storeOrder.setDeductionPrice(mapDeductionPrice);
//        storeOrder.setPaid(false);
        storeOrder.setPayType(request.getPayType());
        storeOrder.setUseIntegral(mapUsedIntegral);
        storeOrder.setGainIntegral(gainIntegral);
        storeOrder.setMark(StringEscapeUtils.escapeHtml4(request.getMark()));
        storeOrder.setCombinationId(request.getCombinationId());
        storeOrder.setPinkId(request.getPinkId());
        storeOrder.setSeckillId(request.getSeckillId());
        storeOrder.setBargainId(request.getBargainId());
        storeOrder.setCost(mapCostPrice);
        storeOrder.setIsChannel(isChannel);
        storeOrder.setCreateTime(DateUtil.nowDateTime());
        storeOrder.setUnique(orderKey);
        storeOrder.setShippingType(request.getShippingType());

        // 如果是自提
        if(request.getShippingType() == 2){
            storeOrder.setVerifyCode(CrmebUtil.randomCount(1111111111,999999999)+"");
            SystemStore systemStore = new SystemStore();
            systemStore.setId(request.getStoreId());
            systemStore.setIsShow(true);
            systemStore.setIsDel(false);
            SystemStore existSystemStore = systemStoreService.getByCondition(systemStore);
            if(null == existSystemStore) throw new CrmebException("暂无门店无法选择门店自提");
            storeOrder.setStoreId(existSystemStore.getId());
        }
        boolean createdResult = storeOrderService.create(storeOrder);
        if(!createdResult) throw new CrmebException("订单生成失败");

        // todo 秒杀产品占用库存

        // 积分抵扣
        boolean disIntegle = true;
        User currentUser = userService.getInfo();
        if(request.getUseIntegral() && currentUser.getIntegral().compareTo(BigDecimal.ZERO) > 0){
            BigDecimal deductionPrice = BigDecimal.ZERO;
            if(null != cor.getPriceGroup().getUsedIntegral()){
                deductionPrice = cor.getPriceGroup().getUsedIntegral();
                if(cor.getPriceGroup().getUsedIntegral().compareTo(BigDecimal.ZERO) > 0){
                    currentUser.setIntegral(BigDecimal.ZERO);
                    disIntegle = userService.updateBase(currentUser);
                }else{
//                currentUser.getUid(),"integral",deductionPrice,"uid"
                    if(currentUser.getIntegral().compareTo(deductionPrice) < 0){
                        currentUser.setIntegral(currentUser.getIntegral().subtract(deductionPrice));
                        disIntegle = userService.updateBase(currentUser);
                    }
                }
            }
            UserBill userBill = new UserBill();
            userBill.setTitle("积分抵扣");
            userBill.setUid(currentUser.getUid());
            userBill.setCategory("integral");
            userBill.setType("deduction");
            userBill.setNumber(deductionPrice);
            userBill.setLinkId(orderId+"");
            userBill.setBalance(currentUser.getIntegral());

            userBill.setMark("购买商品使用"+userBill.getNumber()+"积分抵扣"+deductionPrice+"元");
            boolean userBillSaveResult = userBillService.save(userBill);
            disIntegle = disIntegle && userBillSaveResult;
        }
        if(!disIntegle) throw new CrmebException("使用积分抵扣失败");

        // todo 库存操作 砍价拼团秒杀 二期


        List<StoreOrderInfo> storeOrderInfos = new ArrayList<>();
        boolean deCountResult = true;
        for (StoreCartResponse cartResponse : cor.getCartInfo()) {
            deCountResult = deCountResult && storeProductService.decProductStock(
                    cartResponse.getProductId(),
                    cartResponse.getCartNum(),
                    Integer.parseInt(cartResponse.getProductAttrUnique()), 0); // todo 限购参数
            StoreOrderInfo soInfo = new StoreOrderInfo();
            soInfo.setOrderId(storeOrder.getId());
            soInfo.setProductId(cartResponse.getProductId());
            soInfo.setInfo(JSON.toJSON(cartResponse).toString());
            soInfo.setUnique(cartResponse.getProductAttrUnique());
            storeOrderInfos.add(soInfo);
        }
        // 保存购物车商品详情
        boolean saveBatchOrderInfosResult = storeOrderInfoService.saveOrderInfos(storeOrderInfos);
        if(!saveBatchOrderInfosResult && !deCountResult){
            throw new CrmebException("订单生成失败");
        }
        // todo 删除购物车信息
        List<Long> ids = cor.getCartInfo().stream().map(StoreCartResponse::getId).distinct().collect(Collectors.toList());
        List<Integer> idsInt = ids.stream().map(e -> e.intValue()).distinct().collect(Collectors.toList());
        storeCartService.deleteCartByIds(idsInt);

        // 删除缓存订单
        cacheDeleteOrderInfo(currentUser.getUid(),storeOrder.getId());
        // 检查缺省的默认地址设置
        UserAddress defaultAddress = userAddressService.getDefault();
        if(null != defaultAddress){
            userAddressService.def(cor.getAddressInfo().getId());
        }
        storeOrderStatusService.createLog(storeOrder.getId(),
                Constants.ORDER_STATUS_CACHE_CREATE_ORDER,"订单生成");
        return storeOrder;
    }

    /**
     * 获取可用优惠券
     * @param storeCartResponse 购物车参数
     * @return 可用优惠券集合
     */
    public List<StoreCouponUserResponse> getCanUseCouponList(List<StoreCartResponse> storeCartResponse){
        StoreCouponUserSearchRequest userSearchRequest = new StoreCouponUserSearchRequest();
        userSearchRequest.setUid(userService.getUserIdException());
        userSearchRequest.setStatus(0);
        PageParamRequest pageRequest = new PageParamRequest();
        pageRequest.setPage(1);
        pageRequest.setLimit(999);
        List<StoreCouponUserResponse> allUserCouponList = storeCouponUserService.getList(userSearchRequest, pageRequest).getList();
        List<StoreCouponUserResponse> canUseUseCouponList = new ArrayList<>(); // 可用优惠券
        // 根据优惠券使用额度筛选优惠券
        if(null != allUserCouponList){
            for (StoreCartResponse cartResponse : storeCartResponse) {
                canUseUseCouponList = allUserCouponList.stream()
                        .filter(coupon -> cartResponse.getTruePrice().compareTo(coupon.getMinPrice()) <= 0)
                        .distinct().collect(Collectors.toList());
            }
        }

        return canUseUseCouponList;
    }

    /**
     * 检查订单是否重复提交
     * @param orderId 订单id
     * @return 重复提交结果
     */
    public boolean checkOrderSubmitAgain(String orderId){
        StoreOrderSearchRequest storeOrderSearchRequest = new StoreOrderSearchRequest();
        storeOrderSearchRequest.setOrderId(orderId+"");
        storeOrderSearchRequest.setUid(userService.getUserIdException());
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(999);
        pageParamRequest.setPage(1);
        return storeOrderService.getList(storeOrderSearchRequest,pageParamRequest).size() > 0;
    }
    /**
     * 封装现有支付方式
     * @return 支付方式集合
     */
    public List<String> getPayType(){
        List<String> payType = new ArrayList<>();
        payType.add(Constants.PAY_TYPE_WE_CHAT);
        payType.add(Constants.PAY_TYPE_YUE);
        payType.add(Constants.PAY_TYPE_OFFLINE);
        payType.add(Constants.PAY_TYPE_ALI_PAY);
        return payType;
    }

    /**
     * 判断订单是否存在
     * @param orderKey orderKey
     * @return 结果
     */
    public Boolean checkOrderExist(String orderKey, int userId){
        // 判断订单是否 利用分页api放大参数查询
//        StoreOrderSearchRequest request = new StoreOrderSearchRequest();
//        request.setUid(userService.getUserIdException());
//        request.setOrderId(orderKey);
//        request.setUid(userId);
//        request.setIsDel(false);
//        PageParamRequest paramRequest = new PageParamRequest();
//        paramRequest.setLimit(999);
//        paramRequest.setPage(1);
//        List<StoreOrder> existOrderList = storeOrderService.getList(request, new PageParamRequest());
        StoreOrder storeOrderPram = new StoreOrder();
        storeOrderPram.setUnique(orderKey);
        storeOrderPram.setUid(userId);
        List<StoreOrder> existOrderList = storeOrderService.getByEntity(storeOrderPram);
        return existOrderList.size() > 0;
    }

    /**
     * 检查支付类型
     * @param payType 支付类型标识
     * @return 是否支持
     */
    public Boolean checkPayType(String payType){
        boolean result = false;
        payType = payType.toLowerCase();
        switch (payType){
            case Constants.PAY_TYPE_WE_CHAT:
                result = systemConfigService.getValueByKey("pay_weixin_open").equals("1");
                break;
            case Constants.PAY_TYPE_YUE:
                result = (systemConfigService.getValueByKey("balance_func_status").equals("1") && systemConfigService.getValueByKey("yue_pay_status").equals("1"));
                break;
            case Constants.PAY_TYPE_OFFLINE:
                result = systemConfigService.getValueByKey("offline_pay_status").equals("1");
                break;
        }
        return result;
    }


    /**
     * 计算价格集合
     * @param storeCartResponse 购物车参数
     * @param userAddress 用户收货地址
     * @return 计算后的价格集合
     */
    public PriceGroupResponse getOrderPriceGroup(List<StoreCartResponse> storeCartResponse, UserAddress userAddress){
        PriceGroupResponse priceGroupResponse = new PriceGroupResponse();
        BigDecimal storePostage = BigDecimal.ZERO, // 运费
                storeFreePostage = BigDecimal.ZERO, // 满额包邮
                totalPrice = BigDecimal.ZERO, // 订单总金额
                costPrice = BigDecimal.ZERO, // 订单成本价
                vipPrice = BigDecimal.ZERO; // 订单会员优惠价

        String storeFreePostageString = systemConfigService.getValueByKey("store_free_postage");
        for (StoreCartResponse cartResponse : storeCartResponse) {
            totalPrice = totalPrice.add(cartResponse.getTruePrice().multiply(BigDecimal.valueOf(cartResponse.getCartNum())));
            costPrice = costPrice.add(cartResponse.getCostPrice().multiply(BigDecimal.valueOf(cartResponse.getCartNum())));
            vipPrice = vipPrice.add(cartResponse.getVipTruePrice().multiply(BigDecimal.valueOf(cartResponse.getCartNum())));
        }
        if(storeFreePostageString.equals("0")){ // 包邮
            storeFreePostage = BigDecimal.ZERO;
        }else{
            if(null != userAddress && userAddress.getCityId() > 0){
                int cityId = userAddress.getCityId() > 0 ? userAddress.getCityId():0;
                List<StoreProductCartProductInfoResponse> spcpInfo =
                        storeCartResponse.stream().map(StoreCartResponse::getProductInfo).distinct().collect(Collectors.toList());
                List<Integer> tempIds = spcpInfo.stream().map(StoreProductCartProductInfoResponse::getTempId).distinct().collect(Collectors.toList());
                List<ShippingTemplates> shippingTemplates = shippingTemplatesService.getListInIds(tempIds);
                List<ShippingTemplatesRegion> shippingTemplateRegions = shippingTemplatesRegionService.getListInIdsAndCityId(tempIds, cityId);
                List<Integer> shippingTemplateRids = shippingTemplateRegions.stream().map(ShippingTemplatesRegion::getTempId).distinct().collect(Collectors.toList());

//                LinkedHashMap<Integer,HashMap<String,Object>> templateNumMap = new LinkedHashMap<>();
                List<PriceGroup> priceGroups = new ArrayList<>();
                PriceGroup priceGroup = new PriceGroup();
                for (StoreCartResponse cartResponse : storeCartResponse) {
                    Integer tempId = cartResponse.getProductInfo().getTempId() > 0 ? cartResponse.getProductInfo().getTempId() : 0;
                    List<ShippingTemplates> findHas = shippingTemplates.stream().filter(e -> e.getId() == tempId).distinct().collect(Collectors.toList());
                    Integer type = findHas.size() > 0 ? findHas.get(0).getType() : shippingTemplates.get(0).getType();
                    BigDecimal num;

                    switch (type){
                        case 1:
                            num = BigDecimal.valueOf(cartResponse.getCartNum());
                            break;
                        case 2:
                            num = cartResponse.getProductInfo().getAttrInfo().getWeight().multiply(BigDecimal.valueOf(cartResponse.getCartNum()));
                            break;
                        default:
                            num = cartResponse.getProductInfo().getAttrInfo().getVolume().multiply(BigDecimal.valueOf(cartResponse.getCartNum()));
                            break;
                    }
                    List<ShippingTemplatesRegion> hasTempRegions = shippingTemplateRegions.stream().filter(e -> e.getTempId() == tempId).distinct().collect(Collectors.toList());
                    ShippingTemplatesRegion region = hasTempRegions.size() > 0 ? hasTempRegions.get(0) : shippingTemplateRegions.get(0);
                    Integer tempTempId = cartResponse.getProductInfo().getTempId();

                    if(priceGroups.size() == 0){
                        PriceItem priceItem = new PriceItem(
                                num,cartResponse.getTruePrice().multiply(BigDecimal.valueOf(cartResponse.getCartNum())),
                                region.getFirst(),region.getFirstPrice(),region.getRenewal(),region.getRenewalPrice(),
                                cartResponse.getProductInfo().getTempId(),userAddress.getCityId()
                        );
                        priceGroup.setTempId(tempTempId);
                        priceGroup.setPriceItem(priceItem);
                    }else{
                        BigDecimal priceValue = cartResponse.getTruePrice().multiply(BigDecimal.valueOf(cartResponse.getCartNum()));

                        priceGroup.getPriceItem().setNum(priceGroup.getPriceItem().getNum().add(BigDecimal.valueOf(cartResponse.getCartNum())));
                        priceGroup.getPriceItem().setPrice(priceGroup.getPriceItem().getPrice().add(priceValue));
                    }
                    priceGroups.add(priceGroup);
                }
                // 首重排序
                priceGroups.sort((p1, p2) -> p1.getPriceItem().getPrice().compareTo(p2.getPriceItem().getFirst()));

                Integer type = 0;
                storePostage = BigDecimal.ZERO;
                for (int i = 0; i < priceGroups.size(); i++) {
                    PriceGroup priceg = priceGroups.get(i);
                    ShippingTemplatesFree shippingTemplatesFree = new ShippingTemplatesFree();
                    shippingTemplatesFree.setTempId(priceg.getTempId());
                    shippingTemplatesFree.setCityId(priceg.getPriceItem().getCityId());
                    shippingTemplatesFree.setNumber(priceg.getPriceItem().getNum().intValue());
                    shippingTemplatesFree.setPrice(priceg.getPriceItem().getPrice());
                    List<ShippingTemplatesFree> listByConditionForCalcOrderPrice = shippingTemplatesFreeService.getListByConditionForCalcOrderPrice(shippingTemplatesFree);
                    if(listByConditionForCalcOrderPrice.size() == 0) priceGroups.remove(i);
                }
                for (PriceGroup priceg : priceGroups) {
                    if(type == 0){
                        BigDecimal number = priceg.getPriceItem().getNum();
                        BigDecimal first = priceg.getPriceItem().getFirst();
                        BigDecimal firstPrice = priceg.getPriceItem().getFirstPrice();
                        if(first.compareTo(number) >= 1){
                            storePostage.add(firstPrice);
                        }else{
                            BigDecimal renewal = priceg.getPriceItem().getRenewal();
                            if(renewal.compareTo(BigDecimal.ZERO) > 0){
                                BigDecimal fistPrice = priceg.getPriceItem().getNum().subtract(priceg.getPriceItem().getFirst());
                                Integer renwal = priceg.getPriceItem().getRenewal().compareTo(BigDecimal.ZERO) > -1 ? 0:2;
                                BigDecimal divPrice = BigDecimal.valueOf(renwal).divide(fistPrice);
                                BigDecimal mulPrice = divPrice.multiply(priceg.getPriceItem().getRenewalPrice());
                                BigDecimal addPrice = storePostage.add(priceg.getPriceItem().getFirstPrice());
                                storePostage = mulPrice.add(addPrice);
                            }
                        }
                        type = 1;
                    }else{
                        if(priceg.getPriceItem().getRenewal().compareTo(BigDecimal.ZERO) > 0){
                            Integer renwal = priceg.getPriceItem().getRenewal().compareTo(BigDecimal.ZERO) > -1 ? 0:2;
                            BigDecimal divPrice = priceg.getPriceItem().getNum().divide(BigDecimal.valueOf(renwal));
                            BigDecimal mulPrice = divPrice.multiply(BigDecimal.valueOf(2));
                            storePostage = storePostage.add(mulPrice);
                        }
                    }
                }
            }else{
                storePostage = BigDecimal.ZERO;
            }
            if(storeFreePostage.compareTo(totalPrice) <= -1) storePostage = BigDecimal.ZERO;
        }
        priceGroupResponse.setStorePostage(storePostage);
        priceGroupResponse.setStoreFreePostage(storeFreePostage);
        priceGroupResponse.setTotalPrice(totalPrice);
        priceGroupResponse.setCostPrice(costPrice);
        priceGroupResponse.setVipPrice(vipPrice);
        priceGroupResponse.setPayPrice(storeFreePostage.add(totalPrice));
        return priceGroupResponse;
    }

    /**
     * 缓存订单信息
     * @param userId 用户id
     * @param confirmOrderResponse 确认订单对象
     * @return 缓存结果
     */
    public String cacheOrderInfo(Integer userId,ConfirmOrderResponse confirmOrderResponse){
        String key = DigestUtils.md5Hex(DateUtil.getNowTime().toString());
        redisUtil.set("user_order_" + userId + key, JSONObject.toJSONString(confirmOrderResponse),Constants.ORDER_CASH_CONFIRM, TimeUnit.MINUTES);
        return key;
    }

    /**
     * 获取已缓存的订单信息
     * @param userId 用户id
     * @param cacheKey 缓存key
     * @return 订单确认对象JSON
     */
    public String cacheGetOrderInfo(Integer userId, String cacheKey){
        String key = "user_order_" + userId + cacheKey;
        boolean exists = redisUtil.exists(key);
        if(!exists) return null;
        return redisUtil.get(key).toString();
    }

    /**
     * 删除缓存订单信息
     * @param userId 用户id
     * @param cacheKey 缓存key
     */
    public void cacheDeleteOrderInfo(Integer userId, Integer cacheKey){
        String key = "user_order_" + userId + cacheKey;
        boolean exists = redisUtil.exists(key);
        if(!exists) return;
        redisUtil.remove(key);
    }

    /**
     * 根据订单号查询订单信息
     * @param id 订单id
     * @return 计算后的价格集合
     */
    public StoreOrder getInfoById(Integer id) {
        StoreOrder storeOrder = storeOrderService.getById(id);
        Integer userId = userService.getUserIdException();

        if(null == storeOrder || !userId.equals(storeOrder.getUid())){
            //订单号错误
            throw new CrmebException("没有找到相关订单信息!");
        }

        return storeOrder;
    }

    /**
     * 检测订单是否可以删除
     * @param orderStatus 订单状态
     * @return 计算后的价格集合
     */
    public void checkDeleteStatus(String orderStatus) {
        //退款状态，持可以添加

        String [] deleteStatusList = {
                Constants.ORDER_STATUS_UNPAID,
                Constants.ORDER_STATUS_REFUNDED
        };

        if(!Arrays.asList(deleteStatusList).contains(orderStatus)){
            throw new CrmebException("该订单无法删除");
        }
    }


    /**
     * 缓存再次下单数据
     * @param cacheKey key
     * @param orderAgainCache 再次下单数据
     */
    public void setCacheOrderAgain(String cacheKey, List<StoreCartResponse> orderAgainCache){
        String d = JSONObject.toJSONString(orderAgainCache);
        redisUtil.set(cacheKey+"", d, Constants.ORDER_CASH_CONFIRM, TimeUnit.MINUTES);
    }

    /**
     * 获取再次下单缓存数据
     * @param cacheKey key
     */
    public List<StoreCartResponse> getCacheOrderAgain(String cacheKey){
        if(!redisUtil.exists(cacheKey)) return null;
        return JSONObject.parseArray(redisUtil.get(cacheKey).toString(),StoreCartResponse.class);
    }

}
