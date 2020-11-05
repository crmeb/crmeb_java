package com.zbkj.crmeb.payment.service.impl;

import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.DateUtil;
import com.zbkj.crmeb.front.response.UserRechargePaymentResponse;
import com.zbkj.crmeb.marketing.request.StoreCouponUserRequest;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.payment.service.OrderPayService;
import com.zbkj.crmeb.payment.service.PayService;
import com.zbkj.crmeb.payment.vo.wechat.AttachVo;
import com.zbkj.crmeb.payment.vo.wechat.CreateOrderResponseVo;
import com.zbkj.crmeb.payment.vo.wechat.PayParamsVo;
import com.zbkj.crmeb.payment.wechat.WeChatPayService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreProductCoupon;
import com.zbkj.crmeb.store.service.StoreOrderInfoService;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.store.service.StoreOrderStatusService;
import com.zbkj.crmeb.store.service.StoreProductCouponService;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import com.zbkj.crmeb.wechat.service.WeChatService;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForPaySuccess;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;


/**
 * OrderPayService 实现类
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
@EqualsAndHashCode(callSuper = true)
@Data
@Service
public class OrderPayServiceImpl extends PayService implements OrderPayService {
    private static final Logger logger = LoggerFactory.getLogger(OrderPayServiceImpl.class);

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Lazy
    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private TemplateMessageService templateMessageService;

    @Autowired
    private UserBillService userBillService;

    @Lazy
    @Autowired
    private SmsService smsService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductCouponService storeProductCouponService;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private OrderUtils orderUtils;

    //订单类
    private StoreOrder order;

    //支付类参参数
    private PayParamsVo payParamsVo;


    /**
     * 订单支付
     * @param orderId Integer 订单号
     * @param fromType String 客户端类型
     * @author Mr.Zhang
     * @since 2020-06-22
     * @return PayResponseVo
     */
    @Override
    public CreateOrderResponseVo payOrder(Integer orderId, String fromType, String clientIp) {
        CreateOrderResponseVo responseVo = new CreateOrderResponseVo();
        StoreOrder storeOrder = storeOrderService.getById(orderId);
        setOrder(storeOrder);
        //针对order进行验证, 是否未支付验证
        beforePay();
        try{
            switch (storeOrder.getPayType()){
                case Constants.PAY_TYPE_WE_CHAT: //微信支付
                case Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM:
                    PayParamsVo payParamsVoRouter = getPayParamsVo(fromType, clientIp, storeOrder);
                    responseVo = weChatPayService.create(payParamsVoRouter);//构造下单类
                    UserRechargePaymentResponse response = weChatService.response(responseVo);
                    responseVo.setTransJsConfig(response);
                    break;
                case Constants.PAY_TYPE_ALI_PAY: //支付宝
                    throw new CrmebException("支付宝未接入支付");
                case Constants.PAY_TYPE_OFFLINE: //线下支付
                    throw new CrmebException("线下支付未开通");
                case Constants.PAY_TYPE_YUE: //余额支付 判断余额支付成功CreateOrderResponseVo.ResultCode = 1;
                    responseVo = responseVo.setResultCode(storeOrderService.yuePay(storeOrder, userService.getInfo(),"")+"");
                    break;
            }
            // 清除缓存的订单信息

        }catch (Exception e){
            e.printStackTrace();
            throw new CrmebException("订单支付失败！");
        }
        return responseVo;
    }

    /**
     *  组装微信支付参数
     * @param fromType
     * @param clientIp
     * @param storeOrder
     * @return
     */
    private PayParamsVo getPayParamsVo(String fromType, String clientIp, StoreOrder storeOrder) {
        //支付需要的参数
        return new PayParamsVo(
                storeOrder.getOrderId(),
                fromType,
                clientIp,
                getProductName(),
                storeOrder.getPayPrice(),
                storeOrder.getUid(),
                new AttachVo(Constants.SERVICE_PAY_TYPE_ORDER, storeOrder.getUid())
        );
    }


    /**
     * 支付之前
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    private void beforePay() {
        checkOrderUnPay();
    }


    /**
     * 支付成功
     * @param orderId String 订单号
     * @param userId Integer 用户id
     * @param payType String 支付类型
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    @Override
    public boolean success(String orderId, Integer userId, String payType) {
        try{
            StoreOrder storeOrder = new StoreOrder();
            storeOrder.setOrderId(orderId);
            storeOrder.setUid(userId);

            storeOrder = storeOrderService.getInfoByEntity(storeOrder);
            setOrder(storeOrder);
            checkOrderUnPay();

            afterPaySuccess();
            return true;
        }catch (Exception e){
            throw new CrmebException("订单支付回调失败，" + e.getMessage());
        }
    }

    /**
     * 支付成功之后, 需要事物处理
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    @Override
    public void afterPaySuccess() {
        //更新订单状态
        orderUpdate();

        //订单日志
        orderStatusCreate();

        //资金变动
        userBillCreate();

        //下发模板通知
        pushTempMessage();

        // 购买成功后根据配置送优惠券
        autoSendCoupons();

        // 更新用户下单数量
        updateUserPayCount();

        //增加经验、积分
        updateFounds();
    }

    /**
     * 更新用户积分经验
     */
    private void updateFounds() {
        userService.consumeAfterUpdateUserFounds(getOrder().getUid(), getOrder().getPayPrice(), Constants.USER_BILL_TYPE_PAY_ORDER);
    }

    /**
     * 更新用户下单数量
     */
    private void updateUserPayCount() {
        userService.userPayCountPlus(userService.getInfo());
    }

    // 商品购买后根据配置送券
    private void autoSendCoupons(){
        // 根据订单详情获取商品信息
        List<StoreOrderInfoVo> orders = storeOrderInfoService.getOrderListByOrderId(getOrder().getId());
        if(null == orders){
            return;
        }
        for (StoreOrderInfoVo order : orders) {
            List<StoreProductCoupon> couponsForGiveUser = storeProductCouponService.getListByProductId(order.getProductId());
            User currentUser = userService.getInfo();
            for (StoreProductCoupon storeProductCoupon : couponsForGiveUser) {
                StoreCouponUserRequest crp = new StoreCouponUserRequest();
                crp.setUid(currentUser.getUid()+"");
                crp.setCouponId(storeProductCoupon.getIssueCouponId());
                storeCouponUserService.receive(crp);
            }
        }
    }

    private void userBillCreate() {
        UserBill userBill = new UserBill();
        userBill.setPm(0);
        userBill.setUid(getOrder().getUid());
        userBill.setLinkId(getOrder().getId().toString());
        userBill.setTitle("购买商品");
        userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
        userBill.setType(Constants.USER_BILL_TYPE_PAY_MONEY);
        userBill.setNumber(getOrder().getPayPrice());
        userBill.setBalance(userService.getById(getOrder().getUid()).getNowMoney());
        userBill.setMark("支付" + getOrder().getPayPrice() + "元购买商品");
        userBillService.save(userBill);
    }

    /**
     * 订单日志
     * @author Mr.Zhang
     * @since 2020-07-01
     */
    private void orderStatusCreate() {
        storeOrderStatusService.createLog(getOrder().getId(), Constants.ORDER_LOG_PAY_SUCCESS, Constants.ORDER_LOG_MESSAGE_PAY_SUCCESS);
    }

    /**
     * 订单支付成功
     * @author Mr.Zhang
     * @since 2020-07-01
     */
    private void orderUpdate() {
        //修改订单状态
        getOrder().setPaid(true);
        getOrder().setPayTime(DateUtil.nowDateTime());
        storeOrderService.updateById(getOrder());
    }


    /**
     * 发送模板消息通知
     * @author Mr.Zhang
     * @since 2020-07-01
     */
    private void pushTempMessage() {
//        String tempKey = Constants.WE_CHAT_PUBLIC_TEMP_KEY_ORDER_PAY_SUCCESS;
//        String type = Constants.PAY_TYPE_WE_CHAT_FROM_PUBLIC;
//        if(Constants.ORDER_PAY_CHANNEL_PROGRAM == getOrder().getIsChannel()){
//            tempKey = Constants.WE_CHAT_PROGRAM_TEMP_KEY_ORDER_PAY_SUCCESS;
//            type = Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM;
//        }
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "订单支付成功");
//        map.put("orderId", getOrder().getOrderId());
//        map.put("payAmount", getOrder().getPayPrice().toString());
//        map.put(Constants.WE_CHAT_TEMP_KEY_END, "感谢购买！");

//        templateMessageService.push(tempKey, map, getOrder().getUid(), type);
        // 小程序发送订阅消息
        String storeNameAndCarNumString = orderUtils.getStoreNameAndCarNumString(getOrder().getId());
        if(StringUtils.isNotBlank(storeNameAndCarNumString)){
            WechatSendMessageForPaySuccess paySuccess = new WechatSendMessageForPaySuccess(
                    getOrder().getId()+"",getOrder().getPayPrice()+"",getOrder().getPayTime()+"","暂无",
                    getOrder().getTotalPrice()+"",storeNameAndCarNumString);
            orderUtils.sendWeiChatMiniMessageForPaySuccess(paySuccess, userService.getById(getOrder()).getUid());
        }
    }


    /**
     * 检测是否未支付
     * @author Mr.Zhang
     * @since 2020-06-22
     */
    private void checkOrderUnPay() {
        if(null == getOrder()){
            throw new CrmebException("没有找到订单信息");
        }

        if(getOrder().getPaid()){
            throw new CrmebException("当前操作被禁止，订单已经被处理");
        }
    }

    /**
     * 获取订单详情数据
     * @author Mr.Zhang
     * @since 2020-06-22
     * @return List<StoreOrderInfoVo>
     */
    private List<StoreOrderInfoVo> getStoreOrderInfoList(){
        //商品信息
        return storeOrderInfoService.getOrderListByOrderId(getOrder().getId());
    }

    /**
     * 获取订单产品名称
     * @author Mr.Zhang
     * @since 2020-06-22
     * @return String
     */
    private String getProductName(){

        List<StoreOrderInfoVo> orderList = getStoreOrderInfoList();
        if(orderList.size() < 1){
            throw new CrmebException("在订单里没有找到商品数据");
        }
//        return orderList.get(0).getInfo().getJSONObject("productInfo").getString("store_name");
        return orderList.get(0).getInfo().getProductInfo().getStoreName();
    }
}
