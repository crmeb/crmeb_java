package com.zbkj.crmeb.store.utilService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.PayConstants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.bargain.model.StoreBargain;
import com.zbkj.crmeb.bargain.model.StoreBargainUser;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserHelpService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserService;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
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
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.request.StoreCouponUserSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreCouponUserResponse;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.seckill.model.StoreSeckill;
import com.zbkj.crmeb.seckill.model.StoreSeckillManger;
import com.zbkj.crmeb.seckill.service.StoreSeckillMangerService;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.store.model.*;
import com.zbkj.crmeb.store.request.StoreOrderSearchRequest;
import com.zbkj.crmeb.store.response.StoreCartResponse;
import com.zbkj.crmeb.store.response.StoreProductCartProductInfoResponse;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import com.zbkj.crmeb.system.model.SystemStore;
import com.zbkj.crmeb.system.response.SystemGroupDataOrderStatusPicResponse;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.system.service.SystemStoreService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserAddress;
import com.zbkj.crmeb.user.service.UserAddressService;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.wechat.service.impl.WechatSendMessageForMinService;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForDistrbution;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForPackage;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForPaySuccess;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单工具类
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

    @Autowired
    private WechatSendMessageForMinService wechatSendMessageForMinService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private StoreSeckillService storeSeckillService;

    @Autowired
    private StoreSeckillMangerService storeSeckillMangerService;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreBargainUserService storeBargainUserService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StoreBargainUserHelpService storeBargainUserHelpService;

    @Autowired
    private StorePinkService storePinkService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 检测支付渠道
     * @param payChannel 支付渠道
     */
    public static boolean checkPayChannel(String payChannel) {
       if (!payChannel.equals(PayConstants.PAY_CHANNEL_WE_CHAT_H5) &&
               !payChannel.equals(PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM) &&
               !payChannel.equals(PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC)) {
           return false;
       }
       return true;
    }

    // 组装数据给前端使用
    public OrderAgainVo tidyOrder(StoreOrder storeOrder, boolean detail, boolean isPic){
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
            if(null != storeOrder.getDeliveryType() && storeOrder.getDeliveryType().equals(Constants.ORDER_STATUS_STR_SPIKE_KEY)){ // 送货
                StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
                storeOrderStatus.setOid(storeOrder.getId());
                storeOrderStatus.setChangeType(Constants.ORDER_LOG_DELIVERY);
                List<StoreOrderStatus> sOrderStatusResults = storeOrderStatusService.getByEntity(storeOrderStatus);
                if(sOrderStatusResults.size()>0){
                    String DateStr = DateUtil.dateToStr(sOrderStatusResults.get(sOrderStatusResults.size()-1).getCreateTime(), Constants.DATE_FORMAT);
                    status = new OrderAgainItemVo(2,"待收货",DateStr+"服务商已送货");
                }
            }else if(null != storeOrder.getDeliveryType() && storeOrder.getDeliveryType().equals(Constants.ORDER_LOG_EXPRESS)) {
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
                    } else {
                        status = new OrderAgainItemVo(2, "待收货", "退款拒绝订单已发货");
                    }
            }
        }else if(storeOrder.getStatus() == 2){
            status = new OrderAgainItemVo(3,"待评价","已收货,快去评价一下吧");

        }else if(storeOrder.getStatus() == 3){
            status = new OrderAgainItemVo(4,"交易完成","'交易完成,感谢您的支持");
        }
        // 支付方式
        if(status != null){
            if(null != storeOrder.getStatus()){
                status.setPayType(getOrderPayTypeStr(storeOrder.getPayType()));
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
    public ComputeOrderResponse computedOrder(OrderCreateRequest request, ConfirmOrderResponse cor, String orderKey, User currentUser){
        // 检查订单是否存在
        if(checkOrderExist(orderKey, currentUser.getUid())) throw new CrmebException(orderKey + "订单已存在");

        // 需要return的自定义变量
        BigDecimal couponPrice = BigDecimal.ZERO;// 优惠券金额
        Integer surPlusIntegral = 0;// 剩余积分
        BigDecimal deductionPrice;// 抵扣金额
        Integer usedIntegral = 0;// 使用积分
        BigDecimal payPrice = cor.getPriceGroup().getTotalPrice();// 支付金额

        // 线下支付状态
        Integer offliePayStatus = Integer.valueOf(systemConfigService.getValueByKey("offline_pay_status"));

        PriceGroupResponse currentOrderPriceGroup;

        // TODO 订单支付类型判断应该在外层
        List<String> existPayType = getPayType().stream().filter(e->{
            if(offliePayStatus == 2 && e.equals(Constants.PAY_TYPE_OFFLINE)){
                return false;
            }
            return true;
        }).distinct().collect(Collectors.toList());
        if(CollUtil.isEmpty(existPayType)) throw new CrmebException("选择支付方式有误");

        // 检测订单是否重复提交
        if(checkOrderSubmitAgain(orderKey)) throw new CrmebException("请勿重复提交订单");

        // 判断邮费是否正确
        BigDecimal payPostage;
        if(request.getPayType().equals("offline") && systemConfigService.getValueByKey("offline_postage").equals("1")){
            payPostage = BigDecimal.ZERO;
        }else{
            UserAddress ua = new UserAddress();
            ua.setUid(currentUser.getUid());
            ua.setId(request.getAddressId());
            UserAddress currentUserAddress = userAddressService.getUserAddress(ua);
            currentOrderPriceGroup = getOrderPriceGroup(cor.getCartInfo(), currentUserAddress);
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
            boolean couponUseResult = storeCouponUserService.canUse(couponId, primaryKey, payPrice);
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
        if(null != request.getUseIntegral() && currentUser.getIntegral() > 0){
            deductionPrice = new BigDecimal(currentUser.getIntegral()).multiply(BigDecimal.valueOf(Double.valueOf(cor.getOther().get("integralRatio").toString())));
            if(request.getUseIntegral()){
                // 积分兑换金额小于实际支付金额
                if(deductionPrice.compareTo(payPrice) < 0){
                    payPrice = payPrice.subtract(deductionPrice);
                    usedIntegral = currentUser.getIntegral().intValue();
                }else{
                    deductionPrice = payPrice;
                    if(payPrice.compareTo(BigDecimal.ZERO) > 0){
                        usedIntegral = payPrice.divide(BigDecimal.valueOf(Double.parseDouble(cor.getOther().get("integralRatio").toString()))).setScale(0, BigDecimal.ROUND_UP).intValue();
                        surPlusIntegral = currentUser.getIntegral().intValue() - usedIntegral;
                    }
                    payPrice = BigDecimal.ZERO;
                }
            }
        }else{
            deductionPrice = BigDecimal.ZERO;
            usedIntegral = 0;
        }
        if(payPrice.compareTo(BigDecimal.ZERO) <= 0) payPrice = BigDecimal.ZERO;

        ComputeOrderResponse result = new ComputeOrderResponse();
        // 组装返回数据
        result.setTotalPrice(cor.getPriceGroup().getTotalPrice());
        result.setPayPrice(payPrice.setScale(2, BigDecimal.ROUND_CEILING));
        result.setPayPostage(payPostage.setScale(2, BigDecimal.ROUND_CEILING));
        result.setCouponPrice(couponPrice.setScale(2, BigDecimal.ROUND_CEILING));
        result.setDeductionPrice(request.getUseIntegral() ? deductionPrice.setScale(2, BigDecimal.ROUND_CEILING) : BigDecimal.ZERO);
        result.setUsedIntegral(usedIntegral);
        result.setSurplusIntegral(surPlusIntegral);
        // 更新计算后的数据到订单缓存
        cor.getPriceGroup().setTotalPrice(result.getTotalPrice());
        cor.getPriceGroup().setPayPrice(result.getPayPrice());
        cor.getPriceGroup().setPayPostage(result.getPayPostage());
        cor.getPriceGroup().setCouponPrice(result.getCouponPrice());
        cor.getPriceGroup().setDeductionPrice(result.getDeductionPrice());
        cor.getPriceGroup().setUsedIntegral(result.getUsedIntegral());
        cacheRepliceOrderInfo(orderKey, currentUser.getUid(), cor);
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
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public StoreOrder createOrder(OrderCreateRequest request,ConfirmOrderResponse cor, Integer isChannel,Integer orderId, String orderKey){
//        UserAddress currentUserAddress = new UserAddress();
//        List<Integer> cartIds = new ArrayList<>();
//        Integer totalNum = 0;
//        Integer gainIntegral = 0;
//
//        if(request.getShippingType() == 1){ // 是否包邮
//            if(request.getAddressId() <= 0) throw new CrmebException("请选择收货地址");
//            UserAddress userAddress = new UserAddress();
//            userAddress.setId(request.getAddressId());
//            userAddress.setIsDel(false);
//            currentUserAddress = userAddressService.getUserAddress(userAddress);
//            if(null == currentUserAddress){
//                throw new CrmebException("收货地址有误");
//            }
//        }else if(request.getShippingType() == 2){ // 到店自提
//            if(StringUtils.isBlank(request.getRealName()) || StringUtils.isBlank(request.getPhone()))
//                throw new CrmebException("请填写姓名和电话");
//
//            currentUserAddress.setRealName(request.getRealName());
//            currentUserAddress.setPhone(request.getPhone());
//        }
//        for (StoreCartResponse cartResponse : cor.getCartInfo()) {
//            cartIds.add(cartResponse.getSeckillId());
//            totalNum += cartResponse.getCartNum();
//
//            Integer cartInfoGainIntegral =
//                    (cartResponse.getProductInfo().getGiveIntegral() != null && cartResponse.getProductInfo().getGiveIntegral() > 0 )?
//                            cartResponse.getProductInfo().getGiveIntegral() * cartResponse.getCartNum()
//                            : 0;
//            gainIntegral = gainIntegral + cartInfoGainIntegral;
//        }
//        // todo 检测营销产品状态
//        // 发快递还是门店自提
//        String storeSelfMention = systemConfigService.getValueByKey("store_self_mention");
//        if(!Boolean.valueOf(storeSelfMention)) request.setShippingType(1);
//
//        StoreOrder storeOrder = new StoreOrder();
//        storeOrder.setUid(cor.getUserInfo().getUid());
//        storeOrder.setOrderId(CrmebUtil.getOrderNo(Constants.PAY_TYPE_WE_CHAT));
//
//        // 后置选择收货地址信息
//        if(null == cor.getAddressInfo() && request.getAddressId() > 0){
//            UserAddress selectUserAddress = userAddressService.getById(request.getAddressId());
//            cor.setAddressInfo(selectUserAddress);
//        }
//
//        storeOrder.setRealName(currentUserAddress.getRealName());
//        storeOrder.setUserPhone(currentUserAddress.getPhone());
//        storeOrder.setUserAddress(cor.getAddressInfo().getProvince()
//                + cor.getAddressInfo().getCity()
//                + cor.getAddressInfo().getDistrict()
//                + cor.getAddressInfo().getDetail());
////        storeOrder.setCarId(cartIds);
//        storeOrder.setTotalNum(totalNum);
//        BigDecimal mapTotalPrice = cor.getPriceGroup().getTotalPrice();
//        BigDecimal mapStorePostage = cor.getPriceGroup().getStorePostage();
//        BigDecimal mapCouponPrice = cor.getPriceGroup().getCouponPrice();
//        BigDecimal mapPayPrice = cor.getPriceGroup().getPayPrice();
//        BigDecimal mapPayPostage = cor.getPriceGroup().getPayPostage();
//        BigDecimal mapDeductionPrice = cor.getPriceGroup().getDeductionPrice();
//        Integer mapUsedIntegral = cor.getPriceGroup().getUsedIntegral();
//        BigDecimal mapCostPrice = cor.getPriceGroup().getCostPrice();
//
//        int couponId = null == cor.getUsableCoupon() ? 0: cor.getUsableCoupon().getId();
//        storeOrder.setTotalPrice(mapTotalPrice);
//        storeOrder.setTotalPostage(mapStorePostage);
//        storeOrder.setCouponId(couponId);
//        storeOrder.setCouponPrice(mapCouponPrice);
//        storeOrder.setPayPrice(mapPayPrice);
//        storeOrder.setPayPostage(mapPayPostage);
//        storeOrder.setDeductionPrice(mapDeductionPrice);
////        storeOrder.setPaid(false);
//        storeOrder.setPayType(request.getPayType());
//        storeOrder.setUseIntegral(mapUsedIntegral);
//        storeOrder.setGainIntegral(gainIntegral);
//        storeOrder.setMark(StringEscapeUtils.escapeHtml4(request.getMark()));
//        storeOrder.setCombinationId(request.getCombinationId());
//        storeOrder.setPinkId(request.getPinkId());
//        storeOrder.setSeckillId(request.getSeckillId());
//        storeOrder.setBargainId(request.getBargainId());
//        storeOrder.setCost(mapCostPrice);
//        storeOrder.setIsChannel(isChannel);
//        storeOrder.setCreateTime(DateUtil.nowDateTime());
//        storeOrder.setUnique(orderKey);
//        storeOrder.setShippingType(request.getShippingType());
//        storeOrder.setPinkId(request.getPinkId());
//
//        // 如果是自提
//        if(request.getShippingType() == 2){
//            storeOrder.setVerifyCode(CrmebUtil.randomCount(1111111111,999999999)+"");
//            SystemStore systemStore = new SystemStore();
//            systemStore.setId(request.getStoreId());
//            systemStore.setIsShow(true);
//            systemStore.setIsDel(false);
//            SystemStore existSystemStore = systemStoreService.getByCondition(systemStore);
//            if(null == existSystemStore) throw new CrmebException("暂无门店无法选择门店自提");
//            storeOrder.setStoreId(existSystemStore.getId());
//        }
//        boolean createdResult = storeOrderService.create(storeOrder);
//        if(!createdResult) throw new CrmebException("订单生成失败");
//
//        // 积分抵扣
//        boolean disIntegle = true;
//        User currentUser = userService.getInfo();
//        if(request.getUseIntegral() && currentUser.getIntegral() > 0){
//            BigDecimal deductionPrice = BigDecimal.ZERO;
//            if(null != cor.getPriceGroup().getUsedIntegral()){
//                deductionPrice = BigDecimal.valueOf(cor.getPriceGroup().getUsedIntegral(),2);
//                if(cor.getPriceGroup().getUsedIntegral() > 0){
//                    currentUser.setIntegral(0);
//                    disIntegle = userService.updateBase(currentUser);
//                }else{
//                    if(currentUser.getIntegral() < deductionPrice.intValue()){
//                        currentUser.setIntegral(currentUser.getIntegral() - deductionPrice.intValue());
//                        disIntegle = userService.updateBase(currentUser);
//                    }
//                }
//            }
//            UserBill userBill = new UserBill();
//            userBill.setTitle("积分抵扣");
//            userBill.setUid(currentUser.getUid());
//            userBill.setCategory("integral");
//            userBill.setType("deduction");
//            userBill.setNumber(deductionPrice);
//            userBill.setLinkId(orderId+"");
//            userBill.setBalance(new BigDecimal(currentUser.getIntegral()));
//
//            userBill.setMark("购买商品使用"+userBill.getNumber()+"积分抵扣"+deductionPrice+"元");
//            boolean userBillSaveResult = userBillService.save(userBill);
//            disIntegle = disIntegle && userBillSaveResult;
//        }
//        if(!disIntegle) throw new CrmebException("使用积分抵扣失败");
//
//        Integer secKillId = null;
//        List<StoreOrderInfo> storeOrderInfos = new ArrayList<>();
//        boolean deCountResult = true;
//        for (StoreCartResponse cartResponse : cor.getCartInfo()) {
//            StoreOrderInfo soInfo = new StoreOrderInfo();
//            // 秒杀商品扣减库存
//            if(null != cartResponse.getSeckillId() && cartResponse.getSeckillId() > 0){
//                secKillId = cartResponse.getSeckillId();
//                // 秒杀商品本身库存扣减
//                deCountResult = storeSeckillService.decProductStock(
//                        cartResponse.getSeckillId(),
//                        cartResponse.getCartNum(),
//                        Integer.parseInt(cartResponse.getProductAttrUnique()), Constants.PRODUCT_TYPE_SECKILL);
//                // 商品本身扣减库存 更新商品本身库存步骤 1=根据秒杀uniuqeId 获取对应sku 2=根据商品ID获取商品sku信息后 对比相等的sku 更新其对应数量
//                // 秒杀对应商品库存扣减
//                StoreProductAttrValue currentSeckillAttrValue = storeProductAttrValueService.getById(cartResponse.getProductAttrUnique());
//                List<StoreProductAttrValue> currentSeckillAttrValues = storeProductAttrValueService.getListByProductId(cartResponse.getProductId());
//                List<StoreProductAttrValue> existAttrValues = currentSeckillAttrValues.stream().filter(e ->
//                        e.getSuk().equals(currentSeckillAttrValue.getSuk()) && e.getType().equals(Constants.PRODUCT_TYPE_NORMAL))
//                        .collect(Collectors.toList());
//                if(existAttrValues.size() != 1) throw new CrmebException("未找到扣减库存的商品");
//                deCountResult = storeProductService.decProductStock(
//                        existAttrValues.get(0).getProductId(),
//                        cartResponse.getCartNum(),
//                        existAttrValues.get(0).getId(), Constants.PRODUCT_TYPE_NORMAL);
//
//            }else if (ObjectUtil.isNotNull(cartResponse.getBargainId()) && cartResponse.getBargainId() > 0) {
//                // 砍价扣减库存
//                Integer bargainId = cartResponse.getBargainId();
//                // 砍价商品扣减库存
//                deCountResult = storeBargainService.decProductStock(
//                        cartResponse.getBargainId(),
//                        cartResponse.getCartNum(),
//                        Integer.parseInt(cartResponse.getProductAttrUnique()), Constants.PRODUCT_TYPE_BARGAIN);
//                // 商品本身扣减库存 更新商品本身库存步骤 1=根据秒杀uniuqeId 获取对应sku 2=根据商品ID获取商品sku信息后 对比相等的sku 更新其对应数量
//                // 砍价对应商品库存扣减
//                StoreProductAttrValue currentSeckillAttrValue = storeProductAttrValueService.getById(cartResponse.getProductAttrUnique());
//                List<StoreProductAttrValue> currentBargainAttrValues = storeProductAttrValueService.getListByProductId(cartResponse.getProductId());
//                List<StoreProductAttrValue> existAttrValues = currentBargainAttrValues.stream().filter(e ->
//                        e.getSuk().equals(currentSeckillAttrValue.getSuk()) && e.getType().equals(Constants.PRODUCT_TYPE_NORMAL))
//                        .collect(Collectors.toList());
//                if(existAttrValues.size() != 1) throw new CrmebException("未找到扣减库存的商品");
//                deCountResult = storeProductService.decProductStock(
//                        existAttrValues.get(0).getProductId(),
//                        cartResponse.getCartNum(),
//                        existAttrValues.get(0).getId(), Constants.PRODUCT_TYPE_NORMAL);
//
//                // 砍价商品购买成功，改变用户状态
//                StoreBargainUser storeBargainUser = storeBargainUserService.getByBargainIdAndUid(cartResponse.getBargainId(), cor.getUserInfo().getUid());
//                storeBargainUser.setStatus(3);
//                storeBargainUserService.updateById(storeBargainUser);
//            }else if (ObjectUtil.isNotNull(cartResponse.getCombinationId()) && cartResponse.getCombinationId() > 0) {
//                // 判断拼团团长是否存在
//                StorePink headPink = null;
//                if (ObjectUtil.isNotNull(cartResponse.getPinkId()) && cartResponse.getPinkId() > 0) {
//                    headPink = storePinkService.getById(cartResponse.getPinkId());
//                    if (ObjectUtil.isNull(headPink) || headPink.getIsRefund().equals(true) || headPink.getStatus() == 3) {
//                        throw new CrmebException("找不到对应的拼团团队信息");
//                    }
//                }
//                // 拼团扣减库存
//                // 拼团商品本身库存扣减
//                deCountResult = storeCombinationService.decProductStock(
//                        cartResponse.getCombinationId(),
//                        cartResponse.getCartNum(),
//                        Integer.parseInt(cartResponse.getProductAttrUnique()), Constants.PRODUCT_TYPE_PINGTUAN);
//                // 商品本身扣减库存 更新商品本身库存步骤 1=根据拼团uniuqeId 获取对应sku 2=根据商品ID获取商品sku信息后 对比相等的sku 更新其对应数量
//                // 拼团对应商品库存扣减
//                StoreProductAttrValue currentCombinationAttrValue = storeProductAttrValueService.getById(cartResponse.getProductAttrUnique());
//                List<StoreProductAttrValue> currentCombinationAttrValues = storeProductAttrValueService.getListByProductId(cartResponse.getProductId());
//                List<StoreProductAttrValue> existAttrValues = currentCombinationAttrValues.stream().filter(e ->
//                        e.getSuk().equals(currentCombinationAttrValue.getSuk()) && e.getType().equals(Constants.PRODUCT_TYPE_NORMAL))
//                        .collect(Collectors.toList());
//                if(existAttrValues.size() != 1) throw new CrmebException("未找到扣减库存的商品");
//                deCountResult = storeProductService.decProductStock(
//                        existAttrValues.get(0).getProductId(),
//                        cartResponse.getCartNum(),
//                        existAttrValues.get(0).getId(), Constants.PRODUCT_TYPE_NORMAL);
//                // 生成拼团表数据
//                StoreCombination storeCombination = storeCombinationService.getById(cartResponse.getCombinationId());
//                StorePink storePink = new StorePink();
//                storePink.setUid(cor.getUserInfo().getUid());
//                storePink.setAvatar(cor.getUserInfo().getAvatar());
//                storePink.setNickname(cor.getUserInfo().getNickname());
//                storePink.setOrderId(storeOrder.getOrderId());
//                storePink.setOrderIdKey(storeOrder.getId());
//                storePink.setTotalNum(storeOrder.getTotalNum());
//                storePink.setTotalPrice(storeOrder.getTotalPrice());
//                storePink.setCid(cartResponse.getCombinationId());
//                storePink.setPid(cartResponse.getProductId());
//                storePink.setPeople(storeCombination.getPeople());
//                storePink.setPrice(storeCombination.getPrice());
//                Integer effectiveTime = storeCombination.getEffectiveTime();// 有效小时数
//                DateTime dateTime = cn.hutool.core.date.DateUtil.date();
//                storePink.setAddTime(dateTime.getTime());
//                if (ObjectUtil.isNotNull(cartResponse.getPinkId()) && cartResponse.getPinkId() > 0) {
//                    storePink.setStopTime(headPink.getStopTime());
//                } else {
//                    DateTime hourTime = cn.hutool.core.date.DateUtil.offsetHour(dateTime, effectiveTime);
//                    long stopTime =  hourTime.getTime();
//                    if (stopTime > storeCombination.getStopTime()) {
//                        stopTime = storeCombination.getStopTime();
//                    }
//                    storePink.setStopTime(stopTime);
//                }
//                storePink.setKId(Optional.ofNullable(cartResponse.getPinkId()).orElse(0));
//                storePink.setIsTpl(false);
//                storePink.setIsRefund(false);
//                storePink.setStatus(1);
//                storePinkService.save(storePink);
//
//                // 如果是开团，需要更新订单数据
//                if (storePink.getKId() == 0) {
//                    storeOrder.setPinkId(storePink.getId());
//                    storeOrderService.updateById(storeOrder);
//                }
//            }else {
//                // 普通购买扣减库存
//                deCountResult = storeProductService.decProductStock(
//                        cartResponse.getProductId(),
//                        cartResponse.getCartNum(),
//                        Integer.parseInt(cartResponse.getProductAttrUnique()), Constants.PRODUCT_TYPE_NORMAL);
//            }
//            soInfo.setOrderId(storeOrder.getId());
//            soInfo.setProductId(cartResponse.getProductId());
//            soInfo.setInfo(JSON.toJSON(cartResponse).toString());
//            soInfo.setUnique(cartResponse.getProductAttrUnique());
//            storeOrderInfos.add(soInfo);
//
//        }
//        // 保存购物车商品详情
//        boolean saveBatchOrderInfosResult = storeOrderInfoService.saveOrderInfos(storeOrderInfos);
//        if(!saveBatchOrderInfosResult && !deCountResult){
//            throw new CrmebException("订单生成失败");
//        }
//
//        // 删除缓存订单
//        cacheDeleteOrderInfo(currentUser.getUid(), orderKey);
//        // 检查缺省的默认地址设置
//        UserAddress defaultAddress = userAddressService.getDefault();
//        if(null != defaultAddress){
//            userAddressService.def(cor.getAddressInfo().getId());
//        }
//        storeOrderStatusService.createLog(storeOrder.getId(),
//                Constants.ORDER_STATUS_CACHE_CREATE_ORDER,"订单生成");
//        return storeOrder;
        return null;
    }

    /**
     * 创建订单
     * @param request 订单创建参数
     * @param cor 缓存的购物车，价钱等信息集合
     * @param user 用户
     * @return 订单信息
     * 先扣减库存，需要使用乐观锁
     * 扣减库存成功后生成订单 + 保存其他数据
     */
    public StoreOrder createOrder_v131(OrderCreateRequest request, ConfirmOrderResponse cor, String orderKey, User user){
        // 收货信息
        UserAddress currentUserAddress = new UserAddress();
        // 购买总数量
        Integer totalNum = 0;
        // 购买赠送的积分
        Integer gainIntegral = 0;
        // 核销码
        String verifyCode = "";
        // 自提门店id
        Integer storeId = 0;

        // 校验收货信息
        if(request.getShippingType() == 1){ // 发货
            if(request.getAddressId() <= 0) throw new CrmebException("请选择收货地址");
            UserAddress userAddress = new UserAddress();
            userAddress.setId(request.getAddressId());
            userAddress.setIsDel(false);
            currentUserAddress = userAddressService.getUserAddress(userAddress);
            if(ObjectUtil.isNull(currentUserAddress)){
                throw new CrmebException("收货地址有误");
            }
        }else if(request.getShippingType() == 2){ // 到店自提
            if(StringUtils.isBlank(request.getRealName()) || StringUtils.isBlank(request.getPhone())) {
                throw new CrmebException("请填写姓名和电话");
            }
            currentUserAddress.setRealName(request.getRealName());
            currentUserAddress.setPhone(request.getPhone());
            // 自提开关是否打开
            String storeSelfMention = systemConfigService.getValueByKey("store_self_mention");
            if (storeSelfMention.equals("false")) {
                throw new CrmebException("请先联系管理员开启门店自提");
            }
            SystemStore systemStore = new SystemStore();
            systemStore.setId(request.getStoreId());
            systemStore.setIsShow(true);
            systemStore.setIsDel(false);
            SystemStore existSystemStore = systemStoreService.getByCondition(systemStore);
            if(null == existSystemStore) throw new CrmebException("暂无门店无法选择门店自提");
            storeId = existSystemStore.getId();
            verifyCode = CrmebUtil.randomCount(1111111111,999999999)+"";
            currentUserAddress.setDetail(existSystemStore.getName());
        }

        // 校验积分抵扣
        if(request.getUseIntegral() && user.getIntegral() >= 0 && cor.getPriceGroup().getUsedIntegral() >= 0) {
            if (user.getIntegral() < cor.getPriceGroup().getUsedIntegral()) {
                throw new CrmebException("用户的积分不够抵扣，请刷新页面重新计算价格");
            }
        }

        if (request.getBargainId() > 0) {
            StoreBargainUser storeBargainUser = storeBargainUserService.getByBargainIdAndUid(request.getBargainId(), user.getUid());
            if (ObjectUtil.isNull(storeBargainUser)) throw new CrmebException("砍价用户信息不存在");
            if (!storeBargainUser.getStatus().equals(1)) {
                throw new CrmebException("活动已结束或订单已支付");
            }
        }

        // 库存扣减
        List<StoreCartResponse> cartInfoList = cor.getCartInfo();
        if (CollUtil.isEmpty(cartInfoList)) throw new CrmebException("购物详情信息不存在");

        /**
         * 活动商品校验
         * skuRecord 扣减库存对象
         * ——activityId             活动商品id
         * ——activityAttrValueId    活动商品skuid
         * ——productId              普通（主）商品id
         * ——attrValueId            普通（主）商品skuid
         * ——num                    购买数量
         */
        List<MyRecord> skuRecordList = productSkuDispose(request, cartInfoList);

        List<StoreOrderInfo> storeOrderInfos = new ArrayList<>();
        // 获取购买数量+赠送积分
        // 购物车商品详情
        for (StoreCartResponse cartResponse : cartInfoList) {
            // 购买数量
            totalNum += cartResponse.getCartNum();
            // 赠送积分
            if (ObjectUtil.isNotNull(cartResponse.getProductInfo().getGiveIntegral()) && cartResponse.getProductInfo().getGiveIntegral() > 0) {
                gainIntegral += cartResponse.getProductInfo().getGiveIntegral() * cartResponse.getCartNum();
            }

            // 订单详情
            StoreOrderInfo soInfo = new StoreOrderInfo();
            soInfo.setProductId(cartResponse.getProductId());
            soInfo.setInfo(JSON.toJSON(cartResponse).toString());
            soInfo.setUnique(cartResponse.getProductAttrUnique());
            storeOrderInfos.add(soInfo);
        }

        // 下单赠送积分
        // 赠送积分比例
        if (cor.getPriceGroup().getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
            String integralStr = systemConfigService.getValueByKey(Constants.CONFIG_KEY_INTEGRAL_RATE);
            if (StrUtil.isNotBlank(integralStr)) {
                BigDecimal integralBig = new BigDecimal(integralStr);
                Integer integral = integralBig.multiply(cor.getPriceGroup().getPayPrice()).setScale(0, BigDecimal.ROUND_DOWN).intValue();
                if (integral > 0) {
                    // 添加积分
                    gainIntegral += integral;
                }
            }
        }

        String orderNo = CrmebUtil.getOrderNo("order");
        int isChannel = 3;  // 支付渠道
        if (request.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            switch (request.getPayChannel()){
                case PayConstants.PAY_CHANNEL_WE_CHAT_H5:// H5
                    isChannel = 2;
                    break;
                case PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC:// 公众号
                    isChannel = 0;
                    break;
                case PayConstants.PAY_CHANNEL_WE_CHAT_PROGRAM:// 小程序
                    isChannel = 1;
                    break;
            }
        } else {// 目前只有余额支付
//            orderNo = CrmebUtil.getOrderNo(Constants.PAY_TYPE_YUE);
        }


        StoreOrder storeOrder = new StoreOrder();
        storeOrder.setUid(cor.getUserInfo().getUid());
        storeOrder.setOrderId(orderNo);
        storeOrder.setRealName(currentUserAddress.getRealName());
        storeOrder.setUserPhone(currentUserAddress.getPhone());
        // 发货才有详细地址
        if(request.getShippingType() == 1){ // 发货
            storeOrder.setUserAddress(currentUserAddress.getProvince()
                    + currentUserAddress.getCity()
                    + currentUserAddress.getDistrict()
                    + currentUserAddress.getDetail());
        }
        // 如果是自提
        if(request.getShippingType() == 2){
            storeOrder.setVerifyCode(verifyCode);
            storeOrder.setStoreId(storeId);
            storeOrder.setUserAddress(currentUserAddress.getDetail());
        }
        storeOrder.setTotalNum(totalNum);
//        int couponId = ObjectUtil.isNull(cor.getUsableCoupon()) ? 0: cor.getUsableCoupon().getId();
        storeOrder.setCouponId(Optional.ofNullable(request.getCouponId()).orElse(0));


        // 订单总价
        BigDecimal mapTotalPrice = cor.getPriceGroup().getTotalPrice();
        // 邮费
        BigDecimal mapStorePostage = cor.getPriceGroup().getStorePostage();
        // 优惠券金额
        BigDecimal mapCouponPrice = cor.getPriceGroup().getCouponPrice();
        // 实际支付金额
        BigDecimal mapPayPrice = cor.getPriceGroup().getPayPrice();
        // 支付邮费
        BigDecimal mapPayPostage = cor.getPriceGroup().getPayPostage();
        // 抵扣金额
        BigDecimal mapDeductionPrice = cor.getPriceGroup().getDeductionPrice();
        // 使用积分
        Integer mapUsedIntegral = cor.getPriceGroup().getUsedIntegral();
        // 成本价
        BigDecimal mapCostPrice = cor.getPriceGroup().getCostPrice();

        storeOrder.setTotalPrice(mapTotalPrice);
        storeOrder.setTotalPostage(mapStorePostage);
        storeOrder.setCouponPrice(mapCouponPrice);
        storeOrder.setPayPrice(mapPayPrice);
        storeOrder.setPayPostage(mapPayPostage);
        storeOrder.setDeductionPrice(mapDeductionPrice);
        storeOrder.setPayType(request.getPayType());
        storeOrder.setUseIntegral(mapUsedIntegral);
        storeOrder.setGainIntegral(gainIntegral);
        storeOrder.setMark(StringEscapeUtils.escapeHtml4(request.getMark()));
        storeOrder.setCombinationId(request.getCombinationId());
        storeOrder.setPinkId(request.getPinkId());
        storeOrder.setSeckillId(request.getSeckillId());
        storeOrder.setBargainId(request.getBargainId());
        storeOrder.setCost(mapCostPrice);
        storeOrder.setCreateTime(DateUtil.nowDateTime());
        storeOrder.setUnique(orderKey);
        storeOrder.setShippingType(request.getShippingType());
        storeOrder.setPinkId(request.getPinkId());
        storeOrder.setIsChannel(isChannel);
        storeOrder.setPaid(false);
        // 0表示是团长开团，在支付成功后需要更换为，拼团团长pinkid
        Integer pinkId = Optional.ofNullable(cartInfoList.get(0).getPinkId()).orElse(0);
        storeOrder.setPinkId(pinkId);

        StoreCouponUser storeCouponUser = new StoreCouponUser();
        // 优惠券修改
        if (storeOrder.getCouponId() > 0) {
            storeCouponUser = storeCouponUserService.getById(storeOrder.getCouponId());
            storeCouponUser.setStatus(1);
        }
        StoreCouponUser finalStoreCouponUser = storeCouponUser;

        Boolean execute = transactionTemplate.execute(e -> {
            // 扣减库存
            // 需要根据是否活动商品，扣减不同的库存
            if (storeOrder.getSeckillId() > 0) {// 秒杀扣库存
                MyRecord skuRecord = skuRecordList.get(0);
                // 秒杀商品扣库存
                storeSeckillService.operationStock(skuRecord.getInt("activityId"), skuRecord.getInt("num"), "sub");
                // 秒杀商品规格扣库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("activityAttrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_SECKILL);
                // 普通商品口库存
                storeProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), "sub");
                // 普通商品规格扣库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_NORMAL);
            } else
            if (storeOrder.getBargainId() > 0) {// 砍价扣库存
                MyRecord skuRecord = skuRecordList.get(0);
                // 砍价商品扣库存
                storeBargainService.operationStock(skuRecord.getInt("activityId"), skuRecord.getInt("num"), "sub");
                // 砍价商品规格扣库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("activityAttrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_BARGAIN);
                // 普通商品口库存
                storeProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), "sub");
                // 普通商品规格扣库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_NORMAL);
            } else
            if (storeOrder.getCombinationId() > 0) {// 拼团扣库存
                MyRecord skuRecord = skuRecordList.get(0);
                // 秒杀商品扣库存
                storeCombinationService.operationStock(skuRecord.getInt("activityId"), skuRecord.getInt("num"), "sub");
                // 秒杀商品规格扣库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("activityAttrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_PINGTUAN);
                // 普通商品口库存
                storeProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), "sub");
                // 普通商品规格扣库存
                storeProductAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_NORMAL);
            } else { // 普通商品
                for (MyRecord skuRecord : skuRecordList) {
                    // 普通商品口库存
                    storeProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), "sub");
                    // 普通商品规格扣库存
                    storeProductAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), "sub", Constants.PRODUCT_TYPE_NORMAL);
                }
            }

            storeOrderService.create(storeOrder);
            storeOrderInfos.forEach(info -> {
                info.setOrderId(storeOrder.getId());
            });
            // 优惠券修改
            if (storeOrder.getCouponId() > 0) {
                storeCouponUserService.updateById(finalStoreCouponUser);
            }
            // 保存购物车商品详情
            storeOrderInfoService.saveOrderInfos(storeOrderInfos);
            // 生成订单日志
            storeOrderStatusService.createLog(storeOrder.getId(), Constants.ORDER_STATUS_CACHE_CREATE_ORDER, "订单生成");

            // 扣减库存
//            deductionsStock(cartInfoList, storeOrder, user);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("订单生成失败");
        }
        // 删除缓存订单
        cacheDeleteOrderInfo(user.getUid(), orderKey);
        return storeOrder;
    }

    /**
     * 商品sku处理
     * @param request
     * @param cartInfoList
     * @return List<MyRecord>
     * skuRecord 扣减库存对象
     * ——activityId             活动商品id
     * ——activityAttrValueId    活动商品skuid
     * ——productId              普通（主）商品id
     * ——attrValueId            普通（主）商品skuid
     * ——num                    购买数量
     */
    private List<MyRecord> productSkuDispose(OrderCreateRequest request, List<StoreCartResponse> cartInfoList) {
        List<MyRecord> recordList = CollUtil.newArrayList();
        Integer productId;
        Integer cartNum;
        Integer attrValueId;
        // 活动商品处理
        if (request.getSeckillId() > 0 || request.getBargainId() > 0 || request.getCombinationId() > 0) {
            StoreCartResponse storeCartResponse = cartInfoList.get(0);
            productId = storeCartResponse.getProductId();
            cartNum = storeCartResponse.getCartNum();
            attrValueId = Integer.parseInt(storeCartResponse.getProductAttrUnique());
            if (request.getSeckillId() > 0) {
                // 秒杀部分判断
                Integer seckillId = request.getSeckillId();
                StoreSeckill storeSeckill = storeSeckillService.getByIdException(seckillId);
                if (storeSeckill.getStock().equals(0) || cartNum > storeSeckill.getStock()) {
                    throw new CrmebException("秒杀商品库存不足");
                }
                StoreProductAttrValue seckillAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(attrValueId, seckillId, Constants.PRODUCT_TYPE_SECKILL);
                if (ObjectUtil.isNull(seckillAttrValue)) {
                    throw new CrmebException("秒杀商品规格不存在");
                }
                if (seckillAttrValue.getStock() <= 0 || seckillAttrValue.getQuota() <= 0 || cartNum > seckillAttrValue.getStock()) {
                    throw new CrmebException("秒杀商品规格库存不足");
                }
                // 普通商品部分判断
                StoreProduct product = storeProductService.getById(productId);
                if (ObjectUtil.isNull(product) || product.getIsDel()) {
                    throw new CrmebException("秒杀主商品不存在");
                }
                if (product.getStock().equals(0) || cartNum > product.getStock()) {
                    throw new CrmebException("秒杀主商品库存不足");
                }
                // 主商品sku
                StoreProductAttrValue productAttrValue = storeProductAttrValueService.getByProductIdAndSkuAndType(productId, seckillAttrValue.getSuk(), Constants.PRODUCT_TYPE_NORMAL);
                if (ObjectUtil.isNull(productAttrValue)) {
                    throw new CrmebException("秒杀主商品规格不存在");
                }
                if (productAttrValue.getStock() <= 0 || cartNum > productAttrValue.getStock()) {
                    throw new CrmebException("秒杀主商品规格库存不足");
                }

                MyRecord record = new MyRecord();
                record.set("activityId", seckillId);
                record.set("activityAttrValueId", attrValueId);
                record.set("productId", productId);
                record.set("attrValueId", productAttrValue.getId());
                record.set("num", cartNum);
                recordList.add(record);
            }
            if ( request.getBargainId() > 0) {
                // 砍价部分判断
                Integer bargainId = request.getBargainId();
                StoreBargain storeBargain = storeBargainService.getByIdException(bargainId);
                if (storeBargain.getStock().equals(0) || cartNum > storeBargain.getStock()) {
                    throw new CrmebException("砍价商品库存不足");
                }
                StoreProductAttrValue bargainAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(attrValueId, bargainId, Constants.PRODUCT_TYPE_BARGAIN);
                if (ObjectUtil.isNull(bargainAttrValue)) {
                    throw new CrmebException("砍价商品规格不存在");
                }
                if (bargainAttrValue.getStock() <= 0 || bargainAttrValue.getQuota() <= 0 || cartNum > bargainAttrValue.getStock()) {
                    throw new CrmebException("砍价商品规格库存不足");
                }
                // 普通商品部分判断
                StoreProduct product = storeProductService.getById(productId);
                if (ObjectUtil.isNull(product) || product.getIsDel()) {
                    throw new CrmebException("砍价主商品不存在");
                }
                if (product.getStock().equals(0) || cartNum > product.getStock()) {
                    throw new CrmebException("砍价主商品库存不足");
                }
                // 主商品sku
                StoreProductAttrValue productAttrValue = storeProductAttrValueService.getByProductIdAndSkuAndType(productId, bargainAttrValue.getSuk(), Constants.PRODUCT_TYPE_NORMAL);
                if (ObjectUtil.isNull(productAttrValue)) {
                    throw new CrmebException("砍价主商品规格不存在");
                }
                if (productAttrValue.getStock() <= 0 || cartNum > productAttrValue.getStock()) {
                    throw new CrmebException("砍价主商品规格库存不足");
                }

                MyRecord record = new MyRecord();
                record.set("activityId", bargainId);
                record.set("activityAttrValueId", attrValueId);
                record.set("productId", productId);
                record.set("attrValueId", productAttrValue.getId());
                record.set("num", cartNum);
                recordList.add(record);
            }
            if (request.getCombinationId() > 0) {
                // 拼团部分判断
                Integer combinationId = request.getCombinationId();
                StoreCombination storeCombination = storeCombinationService.getByIdException(combinationId);
                if (storeCombination.getStock().equals(0) || cartNum > storeCombination.getStock()) {
                    throw new CrmebException("拼团商品库存不足");
                }
                StoreProductAttrValue combinationAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(attrValueId, combinationId, Constants.PRODUCT_TYPE_PINGTUAN);
                if (ObjectUtil.isNull(combinationAttrValue)) {
                    throw new CrmebException("拼团商品规格不存在");
                }
                if (combinationAttrValue.getStock() <= 0 || combinationAttrValue.getQuota() <= 0 || cartNum > combinationAttrValue.getStock()) {
                    throw new CrmebException("拼团商品规格库存不足");
                }
                // 普通商品部分判断
                StoreProduct product = storeProductService.getById(productId);
                if (ObjectUtil.isNull(product) || product.getIsDel()) {
                    throw new CrmebException("拼团主商品不存在");
                }
                if (product.getStock().equals(0) || cartNum > product.getStock()) {
                    throw new CrmebException("拼团主商品库存不足");
                }
                // 主商品sku
                StoreProductAttrValue productAttrValue = storeProductAttrValueService.getByProductIdAndSkuAndType(productId, combinationAttrValue.getSuk(), Constants.PRODUCT_TYPE_NORMAL);
                if (ObjectUtil.isNull(productAttrValue)) {
                    throw new CrmebException("砍价主商品规格不存在");
                }
                if (productAttrValue.getStock() <= 0 || cartNum > productAttrValue.getStock()) {
                    throw new CrmebException("砍价主商品规格库存不足");
                }

                MyRecord record = new MyRecord();
                record.set("activityId", combinationId);
                record.set("activityAttrValueId", attrValueId);
                record.set("productId", productId);
                record.set("attrValueId", productAttrValue.getId());
                record.set("num", cartNum);
                record.set("pinkId", storeCartResponse.getPinkId());
                recordList.add(record);
            }
        } else {// 普通商品处理
            /**
             * 1.判断商品有效
             * 2.判断商品库存
             * 3.判断商品规格库存
             */
            for (StoreCartResponse cartResponse : cartInfoList) {
                productId = cartResponse.getProductId();
                cartNum = cartResponse.getCartNum();
                attrValueId = Integer.parseInt(cartResponse.getProductAttrUnique());
                StoreProduct product = storeProductService.getById(productId);
                if (ObjectUtil.isNull(product) || product.getIsDel() || !product.getIsShow()) {
                    throw new CrmebException("购买的商品不存在或已下架");
                }
                if (product.getStock().equals(0) || cartNum > product.getStock()) {
                    throw new CrmebException("购买的商品库存不足");
                }
                StoreProductAttrValue productAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(attrValueId, productId, Constants.PRODUCT_TYPE_NORMAL);
                if (ObjectUtil.isNull(productAttrValue)) {
                    throw new CrmebException("购买的商品规格不存在");
                }
                if (productAttrValue.getStock() <= 0 || cartNum > productAttrValue.getStock()) {
                    throw new CrmebException("购买的商品规格库存不足");
                }

                MyRecord record = new MyRecord();
                record.set("productId", productId);
                record.set("num", cartNum);
                record.set("attrValueId", attrValueId);
                recordList.add(record);
            }
        }
        return recordList;
    }


    /**
     * 扣减库存
     */
    private Boolean deductionsStock(List<StoreCartResponse> cartInfoList, StoreOrder storeOrder, User user) {
        // 活动商品处理，只会有一条数据
        if (cartInfoList.size() == 1) {
            StoreCartResponse cartResponse = cartInfoList.get(0);
            Integer num = cartResponse.getCartNum();
            Integer attrValueId = Integer.parseInt(cartResponse.getProductAttrUnique());
            Integer productId = cartResponse.getProductId();
            // 秒杀商品扣减库存
            if (ObjectUtil.isNotNull(cartResponse.getSeckillId()) && cartResponse.getSeckillId() > 0) {
                Integer seckillId = cartResponse.getSeckillId();
                return storeSeckillService.decProductStock(seckillId, num, attrValueId, productId);
            }
            // 砍价扣减库存
            if (ObjectUtil.isNotNull(cartResponse.getBargainId()) && cartResponse.getBargainId() > 0) {
                // 砍价扣减库存
                Integer bargainId = cartResponse.getBargainId();
                return storeBargainService.decProductStock(bargainId, num, attrValueId, productId, user.getUid());
            }
            // 拼团商品扣减库存
            if (ObjectUtil.isNotNull(cartResponse.getCombinationId()) && cartResponse.getCombinationId() > 0) {
                Integer combinationId = cartResponse.getCombinationId();
                Integer pinkId = cartResponse.getPinkId();
                return storeCombinationService.decProductStock(storeOrder, num, attrValueId, productId, user);
            }
        }
        // 处理普通商品
        Boolean execute = transactionTemplate.execute(e -> {
            for (StoreCartResponse cartResponse : cartInfoList) {
                storeProductService.decProductStock(
                        cartResponse.getProductId(),
                        cartResponse.getCartNum(),
                        Integer.parseInt(cartResponse.getProductAttrUnique()), Constants.PRODUCT_TYPE_NORMAL);
            }
            return Boolean.TRUE;
        });
        return execute;
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
            case PayConstants.PAY_TYPE_WE_CHAT:
                result = systemConfigService.getValueByKey("pay_weixin_open").equals("1");
                break;
            case PayConstants.PAY_TYPE_YUE:
                result = (systemConfigService.getValueByKey("balance_func_status").equals("1") && systemConfigService.getValueByKey("yue_pay_status").equals("1"));
                break;
            case PayConstants.PAY_TYPE_OFFLINE:
                result = systemConfigService.getValueByKey("offline_pay_status").equals("1");
                break;
        }
        return result;
    }


    /**
     * 计算价格集合
     * 计算逻辑说明，先从最小的逻辑找，1：是否指定包邮，2：按照区域计算邮费，3：默认缺省模版中的计算方式
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

        for (StoreCartResponse cartResponse : storeCartResponse) {
            totalPrice = totalPrice.add(cartResponse.getTruePrice().multiply(BigDecimal.valueOf(cartResponse.getCartNum())));
            costPrice = costPrice.add(cartResponse.getCostPrice().multiply(BigDecimal.valueOf(cartResponse.getCartNum())));
            vipPrice = vipPrice.add(cartResponse.getVipTruePrice().multiply(BigDecimal.valueOf(cartResponse.getCartNum())));
        }
        // 判断是否满额包邮 type=1按件数 2按重量 3按体积
        Integer storeFreePostageString = Integer.valueOf(systemConfigService.getValueByKey("store_free_postage"));
        if(storeFreePostageString <= 0){ // 包邮
            storeFreePostage = BigDecimal.ZERO;
        }else{
            if(null != userAddress && userAddress.getCityId() > 0){// 有用户地址的情况下
                int cityId = userAddress.getCityId();
                List<StoreProductCartProductInfoResponse> spcpInfo =
                        storeCartResponse.stream().map(StoreCartResponse::getProductInfo).collect(Collectors.toList());
                List<Integer> tempIds = spcpInfo.stream().map(StoreProductCartProductInfoResponse::getTempId).distinct().collect(Collectors.toList());
                // 查询运费模版
                List<ShippingTemplates> shippingTemplates = shippingTemplatesService.getListInIds(tempIds);

                // 包邮
                List<ShippingTemplatesFree> shippingTempFree = shippingTemplatesFreeService.getListByTempIds(tempIds);

                // 指定区域包邮费用
                List<ShippingTemplatesRegion> shippingTemplateRegions = shippingTemplatesRegionService.listByIds(tempIds);
                BigDecimal postPrice;
                if(!shippingTemplates.get(0).getAppoint() && shippingTemplateRegions.size() > 0){
                    postPrice = shippingTemplateRegions.get(0).getFirstPrice();
                }else if(shippingTemplates.get(0).getAppoint()){ // 正常配送和运费
                    //todo 首重等计算
                    List<ShippingTemplatesRegion> shippingTemplateRegionsHasCity = shippingTemplatesRegionService.getListInIdsAndCityId(tempIds, cityId);
                    postPrice = shippingTemplateRegionsHasCity.size() > 0 ?
                            shippingTemplateRegionsHasCity.get(0).getFirstPrice() : shippingTemplateRegions.get(0).getFirstPrice();
                }else{
                    postPrice = shippingTempFree.get(0).getPrice();
                }
                storePostage = postPrice;
            }else{
                storePostage = BigDecimal.ZERO;
            }
            // 判断价格是否超出满额包邮价格
//            if(storeFreePostage.compareTo(totalPrice) <= -1) storePostage = BigDecimal.ZERO;
        }
//        storePostage = BigDecimal.TEN;
//        storeFreePostage = BigDecimal.ONE;
        priceGroupResponse.setStorePostage(storePostage);
        priceGroupResponse.setStoreFreePostage(storeFreePostage);
        priceGroupResponse.setTotalPrice(totalPrice);
        priceGroupResponse.setCostPrice(costPrice);
        priceGroupResponse.setVipPrice(vipPrice);
        priceGroupResponse.setPayPrice(storePostage.add(totalPrice));
        return priceGroupResponse;
    }

    /**
     * 缓存订单信息
     * @param userId 用户id
     * @param confirmOrderResponse 确认订单对象
     * @return 缓存结果
     */
    public String cacheSetOrderInfo(Integer userId,ConfirmOrderResponse confirmOrderResponse){
        String key = DigestUtils.md5Hex(DateUtil.getNowTime().toString());
        redisUtil.set("user_order:" + userId + key, JSONObject.toJSONString(confirmOrderResponse),Constants.ORDER_CASH_CONFIRM, TimeUnit.MINUTES);
        return key;
    }

    /**
     * 获取已缓存的订单信息
     * @param userId 用户id
     * @param cacheKey 缓存key
     * @return 订单确认对象JSON
     */
    public String cacheGetOrderInfo(Integer userId, String cacheKey){
        String key = "user_order:" + userId + cacheKey;
        boolean exists = redisUtil.exists(key);
        if(!exists) return null;
        return redisUtil.get(key).toString();
    }

    /**
     * 删除缓存订单信息
     * @param userId 用户id
     * @param cacheKey 缓存key
     */
    public void cacheDeleteOrderInfo(Integer userId, String cacheKey){
        String key = "user_order:" + userId + cacheKey;
        boolean exists = redisUtil.exists(key);
        if(!exists) return;
        redisUtil.remove(key);
    }

    /**
     * 更新当前订单结算信息 切换收货地址以及积分抵扣后支付金额可能会变动
     * @param orderKey
     * @param userId
     * @param confirmOrderResponse
     */
    public void cacheRepliceOrderInfo(String orderKey,Integer userId, ConfirmOrderResponse confirmOrderResponse){
        String key = "user_order:" + userId + orderKey;
        if(redisUtil.exists(key)){
            redisUtil.remove(key);
        }
        redisUtil.set(key, JSONObject.toJSONString(confirmOrderResponse),Constants.ORDER_CASH_CONFIRM, TimeUnit.MINUTES);
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

//        String [] deleteStatusList = {
//                Constants.ORDER_STATUS_UNPAID,
//                Constants.ORDER_STATUS_REFUNDED
//        };
        String [] deleteStatusList = {
                Constants.ORDER_STATUS_COMPLETE
        };

        if(!Arrays.asList(deleteStatusList).contains(orderStatus)){
            throw new CrmebException("该订单无法删除");
        }
    }


//    /**
//     * 缓存再次下单数据 todo 再次下单缓存key待统一优化
//     * @param cacheKey key
//     * @param orderAgainCache 再次下单数据
//     */
//    public void setCacheOrderAgain(String cacheKey, List<StoreCartResponse> orderAgainCache){
//        String d = JSONObject.toJSONString(orderAgainCache);
//        redisUtil.set(cacheKey+"", d, Constants.ORDER_CASH_CONFIRM, TimeUnit.MINUTES);
//    }

//    /**
//     * 获取再次下单缓存数据 todo 再次下单缓存key待统一优化
//     * @param cacheKey key
//     */
//    public List<StoreCartResponse> getCacheOrderAgain(String cacheKey){
//        if(!redisUtil.exists(cacheKey)) return null;
//        return JSONObject.parseArray(redisUtil.get(cacheKey).toString(),StoreCartResponse.class);
//    }

    /**
     * 订单数据缓存进redis订单通用
     * @param user 订单缓存key
     * @param list 待缓存的订单数据
     */
    public String setCacheOrderData(User user,Object list){
        Long cacheKey = DateUtil.getTime()+user.getUid();
        String key = Constants.ORDER_CACHE_PER + cacheKey;
        redisUtil.set(key,JSONObject.toJSONString(list), Constants.ORDER_CASH_CONFIRM, TimeUnit.MINUTES);
        return key;
    }

    /**
     * 根据缓存key获取已缓存订单数据
     * @param cacheKey  缓存key
     * @return  查询到的缓存订单数据
     */
    public String getCacheOrderData(String cacheKey){
        if(!redisUtil.exists(cacheKey)) return null;
        return redisUtil.get(cacheKey).toString();
    }

    /**
     * 翻译支付方式给前端
     * @param payType
     * @return
     */
    private String getOrderPayTypeStr(String payType){
        String payTypeStr = null;
        switch (payType){
            case Constants.PAY_TYPE_WE_CHAT:
                payTypeStr = "微信支付";
                break;
            case Constants.PAY_TYPE_YUE:
                payTypeStr = "余额支付";
                break;
            case Constants.PAY_TYPE_OFFLINE:
                payTypeStr = "线下支付";
                break;
            case Constants.PAY_TYPE_ALI_PAY:
                payTypeStr = "支付宝支付";
                break;
            default:
                payTypeStr = "其他支付方式";
                break;
        }
        return payTypeStr;
    }

    /**
     * h5 订单查询 where status 封装
     * @param queryWrapper 查询条件
     * @param status 状态
     */
    public void statusApiByWhere(LambdaQueryWrapper<StoreOrder> queryWrapper, int status){
        switch (status){
            case Constants.ORDER_STATUS_H5_UNPAID: // 未支付
                queryWrapper.eq(StoreOrder::getPaid, false);
                queryWrapper.eq(StoreOrder::getStatus, 0);
                queryWrapper.eq(StoreOrder::getRefundStatus, 0);
                break;
            case Constants.ORDER_STATUS_H5_NOT_SHIPPED: // 待发货
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.eq(StoreOrder::getStatus, 0);
                queryWrapper.eq(StoreOrder::getRefundStatus, 0);
//                queryWrapper.eq(StoreOrder::getShippingType, 1);
                break;
            case Constants.ORDER_STATUS_H5_SPIKE: // 待收货
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.eq(StoreOrder::getStatus, 1);
                queryWrapper.eq(StoreOrder::getRefundStatus, 0);
                break;
            case Constants.ORDER_STATUS_H5_JUDGE: //  已支付 已收货 待评价
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.eq(StoreOrder::getStatus, 2);
                queryWrapper.eq(StoreOrder::getRefundStatus, 0);
                break;
            case Constants.ORDER_STATUS_H5_COMPLETE: // 已完成
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.eq(StoreOrder::getStatus, 3);
                queryWrapper.eq(StoreOrder::getRefundStatus, 0);
                break;
            case Constants.ORDER_STATUS_H5_REFUNDING: // 退款中
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.in(StoreOrder::getRefundStatus, 1, 3);
                break;
            case Constants.ORDER_STATUS_H5_REFUNDED: // 已退款
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.eq(StoreOrder::getRefundStatus, 2);
                break;
            case Constants.ORDER_STATUS_H5_REFUND: // 包含已退款和退款中
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.in(StoreOrder::getRefundStatus, 1,2,3);
                break;
        }
        queryWrapper.eq(StoreOrder::getIsDel, false);
        queryWrapper.eq(StoreOrder::getIsSystemDel, false);
    }

    /**
     * 根据订单id获取订单中商品和名称和购买数量字符串
     * @param orderId   订单id
     * @return          商品名称*购买数量
     */
    public String getStoreNameAndCarNumString(int orderId){
        List<StoreOrderInfoVo> currentOrderInfo = storeOrderInfoService.getOrderListByOrderId(orderId);
        if(currentOrderInfo.size() > 0) {
            StringBuilder sbOrderProduct = new StringBuilder();
            for (StoreOrderInfoVo storeOrderInfoVo : currentOrderInfo) {
                sbOrderProduct.append(storeOrderInfoVo.getInfo().getProductInfo().getStoreName() + "*"
                        + storeOrderInfoVo.getInfo().getCartNum());
            }
            return sbOrderProduct.toString();
        }
        return null;
    }

    /**
     * 根据订单对象获取订单配送中文
     * @param order 订单对象
     * @return      配送方式中文字符串
     */
    public String getPayTypeStrByOrder(StoreOrder order){
        return order.getShippingType() == 1 ? "快递":"自提";
    }

    /** 这里的方法完全是为了订单中调用而封装 订单支付成功有余额支付和微信支付成功回调中
     * 微信小程序发送付款成功订阅消息
     * @param paySuccess    付款成功订阅消息
     * @param userId        用户Id
     */
    public void sendWeiChatMiniMessageForPaySuccess(WechatSendMessageForPaySuccess paySuccess, int userId){
        wechatSendMessageForMinService.sendPaySuccessMessage(paySuccess,userId);
    }

    /**
     * 微信小程序发送发货快递通知订阅消息
     * @param storeOrder 发送快递
     * @param userId    接收消息的用户id
     */
    public void sendWeiChatMiniMessageForPackageExpress(StoreOrder storeOrder, int userId){
        WechatSendMessageForPackage toPackage = new WechatSendMessageForPackage(
                getOrderPayTypeStr(storeOrder.getPayType()),storeOrder.getOrderId(),storeOrder.getUserAddress(),
                storeOrder.getUserPhone(),storeOrder.getRealName()
        );
        wechatSendMessageForMinService.sendRePackageMessage(toPackage,userId);
    }

    /**
     * 微信小程序发送 配送通知订阅消息
     * @param storeOrder    配送订单信息
     * @param userId        接收消息的用户id
     */
    public void senWeiChatMiniMessageForDeliver(StoreOrder storeOrder, int userId){
        WechatSendMessageForDistrbution distrbution = new WechatSendMessageForDistrbution(
                storeOrder.getId()+"",storeOrder.getDeliveryName(),storeOrder.getDeliveryId()+"",
                "商家已经开始配送", "暂无",getStoreNameAndCarNumString(storeOrder.getId()),"暂无"
        );
        wechatSendMessageForMinService.sendDistrbutionMessage(distrbution,userId);
    }

    /**
     * 下单前秒杀验证
     * @param storeCartPram 秒杀参数
     * @param currentUser   当前购买人
     */
    public StoreSeckill validSecKill(StoreCart storeCartPram, User currentUser) {
        // 判断秒杀商品是否有效
        StoreSeckill storeProductPram = new StoreSeckill();
        storeProductPram.setId(storeCartPram.getSeckillId());
        storeProductPram.setIsDel(false);
        storeProductPram.setIsShow(true);
        List<StoreSeckill> existSecKills = storeSeckillService.getByEntity(storeProductPram);
        if(null == existSecKills) throw new CrmebException("该商品已下架或者删除");

        // 判断秒杀时间段
        StoreSeckill existSecKill = existSecKills.get(0);
        StoreSeckillManger seckillManger = storeSeckillMangerService.getById(existSecKill.getTimeId());
        String stopTimeStr = DateUtil.dateToStr(existSecKill.getStopTime(),Constants.DATE_FORMAT_DATE);
        Date stopDate = DateUtil.strToDate( stopTimeStr  + " " + seckillManger.getEndTime() +":00:00", Constants.DATE_FORMAT);
        if(DateUtil.getTwoDateDays(DateUtil.nowDateTime(),stopDate) < 0){
            throw new CrmebException("秒杀商品已过期");
        }

        // 判断秒杀商品库存和秒杀限量
        StoreProductAttrValue spavPram = new StoreProductAttrValue()
                .setId(Integer.valueOf(storeCartPram.getProductAttrUnique()))
                .setType(Constants.PRODUCT_TYPE_SECKILL);
        List<StoreProductAttrValue> currentSecKillAttrValues = storeProductAttrValueService.getByEntity(spavPram);
        if(null == currentSecKillAttrValues || currentSecKillAttrValues.size() == 0){
            throw new CrmebException("未找到该商品信息");
        }
        StoreProductAttrValue storeProductAttrValue = currentSecKillAttrValues.get(0); // 仅仅会获取一条数据
        // 当天参与活动次数 -根据用户和秒杀信息查询当天订单判断订单数量
        StoreOrder soPram = new StoreOrder().setUid(currentUser.getUid()).setSeckillId(storeCartPram.getSeckillId());
        List<StoreOrder> userCurrentDaySecKillOrders = storeOrderService.getUserCurrentDaySecKillOrders(soPram);

        // 判断是否有待支付订单
        List<StoreOrder> unPayOrders = userCurrentDaySecKillOrders.stream().filter(e -> !e.getPaid()).collect(Collectors.toList());
        if(unPayOrders.size() > 0) throw new CrmebException("您有秒杀待支付订单，请支付后再购买");

        // 判断是否达到上线
        if(null != userCurrentDaySecKillOrders && userCurrentDaySecKillOrders.size() >= existSecKill.getNum()){
            throw new CrmebException("您已经达到当前秒杀活动上限");
        }

        // 达到商品限量 sku quota
        List<Integer> secKillOrderIds = userCurrentDaySecKillOrders.stream().map(StoreOrder::getId).collect(Collectors.toList());
        HashMap<Integer, List<StoreOrderInfoVo>> storeOrderInfos = storeOrderInfoService.getMapInId(secKillOrderIds);
        for (int i = 0; i < userCurrentDaySecKillOrders.size(); i++) {
            StoreOrder storeOrder = userCurrentDaySecKillOrders.get(i);
            List<StoreOrderInfoVo> storeOrderInfoVos = storeOrderInfos.get(storeOrder.getId());
            // 判断购买上线 用户当前订单中已经存在的当前秒杀商品
            int sum = storeOrderInfoVos.stream().mapToInt(e -> e.getInfo().getCartNum()).sum();
            if(storeCartPram.getSeckillId().equals(storeOrder.getSeckillId())
                    && storeProductAttrValue.getQuota() < sum){
                throw new CrmebException("您已经达到当前秒杀活动上限");
            }
        }

        return existSecKill;
    }

    /**
     * 下单前砍价验证
     * @param storeCartPram 砍价参数
     * @param currentUser   当前购买人
     * @return
     */
    public MyRecord validBargain(StoreCart storeCartPram, User currentUser) {
        // 判断砍价商品是否有效
        StoreBargain existBargain = storeBargainService.getByIdException(storeCartPram.getBargainId());
        // 判断购买数量
        if (storeCartPram.getCartNum() > existBargain.getQuota()) {
            throw new CrmebException("砍价商品库存不足");
        }
        if (existBargain.getQuota() <= 0 || existBargain.getStock() <= 0) {// 销量等于限量
            throw new CrmebException("当前拼团商品已售罄");
        }

        // 判断砍价活动时间段
        long timeMillis = System.currentTimeMillis();
        if (timeMillis < existBargain.getStartTime()) {
            throw new CrmebException("砍价商品活动未开始");
        }
        if (timeMillis > existBargain.getStopTime()) {
            throw new CrmebException("砍价商品已过期");
        }

        // 判断用户是否砍价完成
        BigDecimal surplusPrice = storeBargainUserHelpService.getSurplusPrice(storeCartPram.getBargainId(), currentUser.getUid());
        if (surplusPrice.compareTo(BigDecimal.ZERO) > 0) {
            throw new CrmebException("请先完成砍价");
        }

        // 判断砍价商品库存和砍价限量
        StoreProductAttrValue storeProductAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(Integer.valueOf(storeCartPram.getProductAttrUnique()), storeCartPram.getBargainId(), Constants.PRODUCT_TYPE_BARGAIN);
        if(ObjectUtil.isNull(storeProductAttrValue)){
            throw new CrmebException("未找到该商品规格信息");
        }
        if (storeProductAttrValue.getQuota() <= 0 || storeProductAttrValue.getStock() <= 0){// sku销量等于限量
            throw new CrmebException("当前砍价商品已售罄");
        }

        // 参与活动次数 -根据用户和秒杀信息查询当天订单判断订单数量
        StoreOrder soPram = new StoreOrder().setUid(currentUser.getUid()).setBargainId(storeCartPram.getBargainId());
        List<StoreOrder> userCurrentBargainOrders = storeOrderService.getUserCurrentBargainOrders(soPram);

        // 判断是否有待支付订单
        List<StoreOrder> unPayOrders = userCurrentBargainOrders.stream().filter(e -> !e.getPaid()).collect(Collectors.toList());
        if(unPayOrders.size() > 0) throw new CrmebException("您有砍价待支付订单，请支付后再购买");

        // 判断是否达到上限
        List<StoreOrder> noRefundOrders = userCurrentBargainOrders.stream().filter(i -> i.getRefundStatus() != 2).collect(Collectors.toList());
        if(CollUtil.isNotEmpty(userCurrentBargainOrders) && noRefundOrders.size() >= existBargain.getNum()){
            throw new CrmebException("您已经达到当前砍价活动上限");
        }
        MyRecord record = new MyRecord();
        record.set("product", existBargain);
        record.set("attrInfo", storeProductAttrValue);
        return record;
    }

    /**
     * 下单前拼团验证
     * @param storeCartPram 拼团参数
     * @param currentUser   当前购买人
     * @return
     */
    public MyRecord validCombination(StoreCart storeCartPram, User currentUser) {
        // 判断拼团商品是否有效
        StoreCombination existCombination = storeCombinationService.getByIdException(storeCartPram.getCombinationId());

        // 判断拼团时间段
        long timeMillis = System.currentTimeMillis();
        if (timeMillis < existCombination.getStartTime()) {
            throw new CrmebException("拼团商品活动未开始");
        }
        if (timeMillis >= existCombination.getStopTime()) {
            throw new CrmebException("拼团商品已过期");
        }
        // 判断购买数量
        if (storeCartPram.getCartNum() > existCombination.getOnceNum()) {
            throw new CrmebException("购买数量超过单次拼团购买上限");
        }
        if (storeCartPram.getCartNum() > existCombination.getQuota()) {
            throw new CrmebException("拼团商品库存不足");
        }
        if (existCombination.getQuota() <= 0 || existCombination.getStock() <= 0) {// 销量等于限量
            throw new CrmebException("当前拼团商品已售罄");
        }

        // 如果时参团，判断团队是否已满员
        if (ObjectUtil.isNotNull(storeCartPram.getPinkId()) && storeCartPram.getPinkId() > 0) {
            Integer countPeople = storePinkService.getCountByKid(storeCartPram.getPinkId());
            if (countPeople >= existCombination.getPeople()) {
                throw new CrmebException("当前拼团人数已满");
            }
        }

        // 判断拼团商品规格库存和拼团限量
        // 判断商品对应属性是否有效
        StoreProductAttrValue storeProductAttrValue = storeProductAttrValueService.getByIdAndProductIdAndType(Integer.valueOf(storeCartPram.getProductAttrUnique()), storeCartPram.getCombinationId(), Constants.PRODUCT_TYPE_PINGTUAN);
        if(ObjectUtil.isNull(storeProductAttrValue)){
            throw new CrmebException("未找到该商品信息");
        }
        if (storeProductAttrValue.getQuota() <= 0 || storeProductAttrValue.getStock() <= 0){// sku销量等于限量
            throw new CrmebException("当前拼团商品已售罄");
        }
        if (storeCartPram.getCartNum() > storeProductAttrValue.getQuota()) {
            throw new CrmebException("数量超过拼团商品库存上限");
        }

        // 用户参与活动的次数
        StoreOrder soPram = new StoreOrder().setUid(currentUser.getUid()).setCombinationId(storeCartPram.getCombinationId());
        List<StoreOrder> userCombinationOrders = storeOrderService.getByEntity(soPram);

        if (CollUtil.isNotEmpty(userCombinationOrders)) {
            // 判断是否有待支付订单
            List<StoreOrder> unPayOrders = userCombinationOrders.stream().filter(e -> !e.getPaid()).collect(Collectors.toList());
            if(unPayOrders.size() > 0) throw new CrmebException("您有拼团待支付订单，请支付后再购买");

            List<StoreOrder> noRefundOrders = userCombinationOrders.stream().filter(i -> i.getRefundStatus() != 2).collect(Collectors.toList());
            int payNum = userCombinationOrders.stream().mapToInt(order -> {
                if (order.getRefundStatus() != 2) {
                    return order.getTotalNum();
                }
                return 0;
            }).sum();
            if (CollUtil.isNotEmpty(noRefundOrders) && existCombination.getNum() <= payNum){
                throw new CrmebException("您已达到该商品拼团活动上限");
            }
            if ((payNum + storeCartPram.getCartNum()) > existCombination.getNum()) {
                throw new CrmebException("超过该商品拼团活动您的购买上限");
            }
        }

        MyRecord record = new MyRecord();
        record.set("product", existCombination);
        record.set("attrInfo", storeProductAttrValue);
        return record;
    }
}
