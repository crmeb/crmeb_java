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
import com.constants.SysConfigConstants;
import com.constants.SysGroupDataConstants;
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
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.seckill.model.StoreSeckill;
import com.zbkj.crmeb.seckill.model.StoreSeckillManger;
import com.zbkj.crmeb.seckill.service.StoreSeckillMangerService;
import com.zbkj.crmeb.seckill.service.StoreSeckillService;
import com.zbkj.crmeb.store.model.*;
import com.zbkj.crmeb.store.request.StoreOrderSearchRequest;
import com.zbkj.crmeb.store.response.StoreCartResponse;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.vo.StoreOrderInfoOldVo;
import com.zbkj.crmeb.system.model.SystemStore;
import com.zbkj.crmeb.system.response.SystemGroupDataOrderStatusPicResponse;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemGroupDataService;
import com.zbkj.crmeb.system.service.SystemStoreService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserAddress;
import com.zbkj.crmeb.user.service.UserAddressService;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.wechat.service.impl.WechatSendMessageForMinService;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForDistrbution;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForPackage;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForPaySuccess;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

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
               !payChannel.equals(PayConstants.PAY_CHANNEL_WE_CHAT_PUBLIC) &&
               !payChannel.equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS) &&
               !payChannel.equals(PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID)) {
           return false;
       }
       return true;
    }

    // 组装数据给前端使用
    public OrderAgainVo tidyOrder(StoreOrder storeOrder, boolean detail, boolean isPic){
        OrderAgainVo orderAgainVoResult = new OrderAgainVo();
        orderAgainVoResult.setStoreOrder(storeOrder);
        List<StoreOrderInfoOldVo> existOrderList = null;
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
            timeSpace = DateUtil.addSecond(storeOrder.getCreateTime(),Double.valueOf(configValues.get(0).toString()).intValue() * 3600);
            status.setMsg("请在" + DateUtil.dateToStr(timeSpace, Constants.DATE_FORMAT) +"前完成支付");
        }else if(storeOrder.getRefundStatus() == 1){
            status = new OrderAgainItemVo(-1,"申请退款中","商家审核中,请耐心等待");
        }else if(storeOrder.getRefundStatus() == 2){
            status = new OrderAgainItemVo(-2,"已退款","已为您退款,感谢您的支持");
        }else if(storeOrder.getRefundStatus() == 3){
            status = new OrderAgainItemVo(-3,"退款中","正在为您退款,感谢您的支持");
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
            List<SystemGroupDataOrderStatusPicResponse> orderStatusPicList = systemGroupDataService.getListByGid(SysGroupDataConstants.GROUP_DATA_ID_ORDER_STATUS_PIC, SystemGroupDataOrderStatusPicResponse.class);// 53 = group id 在groupData中查询数据

            for (SystemGroupDataOrderStatusPicResponse picList : orderStatusPicList) {
                if(picList.getOrderStatus() == orderAgainVoResult.getStatus().getType()){
                    orderAgainVoResult.setStatusPic(picList.getUrl());
                    break;
                }
            }
        }
        return orderAgainVoResult;
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
    public StoreOrder createOrder(OrderCreateRequest request, ConfirmOrderResponse cor, String orderKey, User user){
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
                case PayConstants.PAY_CHANNEL_WE_CHAT_APP_IOS:// app ios
                    isChannel = 4;
                    break;
                case PayConstants.PAY_CHANNEL_WE_CHAT_APP_ANDROID:// app android
                    isChannel = 5;
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
                    throw new CrmebException("拼团主商品规格不存在");
                }
                if (productAttrValue.getStock() <= 0 || cartNum > productAttrValue.getStock()) {
                    throw new CrmebException("拼团主商品规格库存不足");
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
                result = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_PAY_WEIXIN_OPEN).equals("1");
                break;
            case PayConstants.PAY_TYPE_YUE:
                result = (systemConfigService.getValueByKey(SysConfigConstants.CONFIG_YUE_PAY_STATUS).equals("1"));
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
        // 全场满额包邮开关
        String postageSwitchString = systemConfigService.getValueByKey("store_free_postage_switch");
        // 全场满额包邮金额
        String storeFreePostageString = systemConfigService.getValueByKey("store_free_postage");
        if (postageSwitchString.equals("true") && (storeFreePostageString.equals("0") || totalPrice.compareTo(new BigDecimal(storeFreePostageString)) >= 0)) {
            storePostage = BigDecimal.ZERO;
            storeFreePostage = BigDecimal.ZERO;
        } else{
            if (ObjectUtil.isNull(userAddress) || userAddress.getCityId() <= 0) {
                // 用户地址不存在，默认运费为0元
                storePostage = BigDecimal.ZERO;
            } else {
                // 有用户地址的情况下
                // 运费根据商品计算
                Map<Integer, MyRecord> proMap = CollUtil.newHashMap();
                storeCartResponse.stream().forEach(e -> {
                    Integer proId = e.getProductInfo().getId();
                    if (proMap.containsKey(proId)) {
                        MyRecord record = proMap.get(proId);
                        record.set("totalPrice", record.getBigDecimal("totalPrice").add(e.getTruePrice().multiply(BigDecimal.valueOf(e.getCartNum()))));
                        record.set("totalNum", record.getInt("totalNum") + e.getCartNum());
                        BigDecimal weight = e.getProductInfo().getAttrInfo().getWeight().multiply(BigDecimal.valueOf(e.getCartNum()));
                        record.set("weight", record.getBigDecimal("weight").add(weight));
                        BigDecimal volume = e.getProductInfo().getAttrInfo().getVolume().multiply(BigDecimal.valueOf(e.getCartNum()));
                        record.set("volume", record.getBigDecimal("volume").add(volume));
                    } else {
                        MyRecord record = new MyRecord();
                        record.set("totalPrice", e.getTruePrice().multiply(BigDecimal.valueOf(e.getCartNum())));
                        record.set("totalNum", e.getCartNum());
                        record.set("tempId", e.getProductInfo().getTempId());
                        record.set("proId", proId);
                        BigDecimal weight = e.getProductInfo().getAttrInfo().getWeight().multiply(BigDecimal.valueOf(e.getCartNum()));
                        record.set("weight", weight);
                        BigDecimal volume = e.getProductInfo().getAttrInfo().getVolume().multiply(BigDecimal.valueOf(e.getCartNum()));
                        record.set("volume", volume);

                        proMap.put(proId, record);
                    }
                });

                // 指定包邮（单品运费模板）> 指定区域配送（单品运费模板）
                int cityId = userAddress.getCityId();

                for (Map.Entry<Integer, MyRecord> m : proMap.entrySet()) {
                    MyRecord value = m.getValue();
                    Integer tempId = value.getInt("tempId");
                    ShippingTemplates shippingTemplate = shippingTemplatesService.getById(tempId);
                    if (shippingTemplate.getAppoint()) {// 指定包邮
                        // 判断是否在指定包邮区域内
                        // 必须满足件数 + 金额 才能包邮
                        ShippingTemplatesFree shippingTemplatesFree = shippingTemplatesFreeService.getByTempIdAndCityId(tempId, cityId);
                        if (ObjectUtil.isNotNull(shippingTemplatesFree)) { // 在包邮区域内
                            BigDecimal freeNum = shippingTemplatesFree.getNumber();
                            BigDecimal multiply = value.getBigDecimal("totalPrice");
                            if (new BigDecimal(value.getInt("totalNum")).compareTo(freeNum) >= 0 && multiply.compareTo(shippingTemplatesFree.getPrice()) >= 0) {
                                // 满足件数 + 金额 = 包邮
                                continue;
                            }
                        }
                    }
                    // 不满足指定包邮条件，走指定区域配送
                    ShippingTemplatesRegion shippingTemplatesRegion = shippingTemplatesRegionService.getByTempIdAndCityId(tempId, cityId);
                    if (ObjectUtil.isNull(shippingTemplatesRegion)) {
                        throw new CrmebException("计算运费时，未找到全国运费配置");
                    }

                    // 判断计费方式：件数、重量、体积
                    switch (shippingTemplate.getType()) {
                        case 1: // 件数
                            // 判断件数是否超过首件
                            Integer num = value.getInt("totalNum");
                            if (num <= shippingTemplatesRegion.getFirst().intValue()) {
                                storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice());
                            } else {// 超过首件的需要计算续件
                                int renewalNum = num - shippingTemplatesRegion.getFirst().intValue();
                                // 剩余件数/续件 = 需要计算的续件费用的次数
                                BigDecimal divide = new BigDecimal(renewalNum).divide(shippingTemplatesRegion.getRenewal(), 0, BigDecimal.ROUND_UP);
                                BigDecimal renewalPrice = shippingTemplatesRegion.getRenewalPrice().multiply(divide);
                                storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice()).add(renewalPrice);
                            }
                            break;
                        case 2: // 重量
                            BigDecimal weight = value.getBigDecimal("weight");
                            if (weight.compareTo(shippingTemplatesRegion.getFirst()) <= 0) {
                                storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice());
                            } else {// 超过首件的需要计算续件
                                BigDecimal renewalNum = weight.subtract(shippingTemplatesRegion.getFirst());
                                // 剩余件数/续件 = 需要计算的续件费用的次数
                                BigDecimal divide = renewalNum.divide(shippingTemplatesRegion.getRenewal(), 0, BigDecimal.ROUND_UP);
                                BigDecimal renewalPrice = shippingTemplatesRegion.getRenewalPrice().multiply(divide);
                                storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice()).add(renewalPrice);
                            }
                            break;
                        case 3: // 体积
                            BigDecimal volume = value.getBigDecimal("volume");
                            if (volume.compareTo(shippingTemplatesRegion.getFirst()) <= 0) {
                                storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice());
                            } else {// 超过首件的需要计算续件
                                BigDecimal renewalNum = volume.subtract(shippingTemplatesRegion.getFirst());
                                // 剩余件数/续件 = 需要计算的续件费用的次数
                                BigDecimal divide = renewalNum.divide(shippingTemplatesRegion.getRenewal(), 0, BigDecimal.ROUND_UP);
                                BigDecimal renewalPrice = shippingTemplatesRegion.getRenewalPrice().multiply(divide);
                                storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice()).add(renewalPrice);
                            }
                            break;
                    }
                }
            }
        }
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
//        String key = DigestUtils.md5Hex(DateUtil.getNowTime().toString()+CrmebUtil.getUuid());
        String key = DateUtil.getNowTime().toString()+CrmebUtil.getUuid();
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
    public String getOrderPayTypeStr(String payType){
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
    public void statusApiByWhere(LambdaQueryWrapper<StoreOrder> queryWrapper, Integer status){
        switch (status){
            case Constants.ORDER_STATUS_H5_UNPAID: // 未支付
                queryWrapper.eq(StoreOrder::getPaid, false);
                queryWrapper.eq(StoreOrder::getStatus, 0);
                queryWrapper.eq(StoreOrder::getRefundStatus, 0);
                queryWrapper.eq(StoreOrder::getType, 0);
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
        List<StoreOrderInfoOldVo> currentOrderInfo = storeOrderInfoService.getOrderListByOrderId(orderId);
        if(currentOrderInfo.size() > 0) {
            StringBuilder sbOrderProduct = new StringBuilder();
            for (StoreOrderInfoOldVo storeOrderInfoVo : currentOrderInfo) {
                sbOrderProduct.append(storeOrderInfoVo.getInfo().getProductName() + "*"
                        + storeOrderInfoVo.getInfo().getPayNum());
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
        // 判断日期是否过期
        String stopTimeStr = DateUtil.dateToStr(existSecKill.getStopTime(),Constants.DATE_FORMAT_DATE);
        Date stopDate = DateUtil.strToDate( stopTimeStr  + " " + seckillManger.getEndTime() +":00:00", Constants.DATE_FORMAT);
        if(DateUtil.getTwoDateDays(DateUtil.nowDateTime(),stopDate) < 0){
            throw new CrmebException("秒杀商品已过期");
        }
        // 判断是否在秒杀时段内（小时）,秒杀开始时间 <= 当前时间 <= 秒杀结束时间
        int hour = cn.hutool.core.date.DateUtil.date().getField(Calendar.HOUR_OF_DAY);// 现在的小时
        if (seckillManger.getStartTime() > hour || seckillManger.getEndTime() < hour) {
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
//        StoreOrder soPram = new StoreOrder().setUid(currentUser.getUid()).setSeckillId(storeCartPram.getSeckillId());
//        List<StoreOrder> userCurrentDaySecKillOrders = storeOrderService.getUserCurrentDaySecKillOrders(soPram);
//
//        // 判断是否有待支付订单
//        List<StoreOrder> unPayOrders = userCurrentDaySecKillOrders.stream().filter(e -> !e.getPaid()).collect(Collectors.toList());
//        if(unPayOrders.size() > 0) throw new CrmebException("您有秒杀待支付订单，请支付后再购买");
//
//        // 判断是否达到上线
//        if(null != userCurrentDaySecKillOrders && userCurrentDaySecKillOrders.size() >= existSecKill.getNum()){
//            throw new CrmebException("您已经达到当前秒杀活动上限");
//        }
//
//        // 达到商品限量 sku quota
//        List<Integer> secKillOrderIds = userCurrentDaySecKillOrders.stream().map(StoreOrder::getId).collect(Collectors.toList());
//        HashMap<Integer, List<StoreOrderInfoOldVo>> storeOrderInfos = storeOrderInfoService.getMapInId(secKillOrderIds);
//        for (int i = 0; i < userCurrentDaySecKillOrders.size(); i++) {
//            StoreOrder storeOrder = userCurrentDaySecKillOrders.get(i);
//            List<StoreOrderInfoOldVo> storeOrderInfoVos = storeOrderInfos.get(storeOrder.getId());
//            // 判断购买上线 用户当前订单中已经存在的当前秒杀商品
//            int sum = storeOrderInfoVos.stream().mapToInt(e -> e.getInfo().getPayNum()).sum();
//            if(storeCartPram.getSeckillId().equals(storeOrder.getSeckillId())
//                    && storeProductAttrValue.getQuota() < sum){
//                throw new CrmebException("您已经达到当前秒杀活动上限");
//            }
//        }

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

//        // 参与活动次数 -根据用户和秒杀信息查询当天订单判断订单数量
//        StoreOrder soPram = new StoreOrder().setUid(currentUser.getUid()).setBargainId(storeCartPram.getBargainId());
//        List<StoreOrder> userCurrentBargainOrders = storeOrderService.getUserCurrentBargainOrders(soPram);
//
//        // 判断是否有待支付订单
//        List<StoreOrder> unPayOrders = userCurrentBargainOrders.stream().filter(e -> !e.getPaid()).collect(Collectors.toList());
//        if(unPayOrders.size() > 0) throw new CrmebException("您有砍价待支付订单，请支付后再购买");
//
//        // 判断是否达到上限
//        List<StoreOrder> noRefundOrders = userCurrentBargainOrders.stream().filter(i -> i.getRefundStatus() != 2).collect(Collectors.toList());
//        if(CollUtil.isNotEmpty(userCurrentBargainOrders) && noRefundOrders.size() >= existBargain.getNum()){
//            throw new CrmebException("您已经达到当前砍价活动上限");
//        }
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
