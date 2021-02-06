package com.zbkj.crmeb.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.SmsConstants;
import com.constants.UserConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.utils.ValidateFormUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.combination.model.StorePink;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.express.model.Express;
import com.zbkj.crmeb.express.service.ExpressService;
import com.zbkj.crmeb.express.service.LogisticService;
import com.zbkj.crmeb.express.vo.ExpressSheetVo;
import com.zbkj.crmeb.express.vo.LogisticsResultVo;
import com.zbkj.crmeb.finance.request.FundsMonitorSearchRequest;
import com.zbkj.crmeb.front.vo.OrderAgainVo;
import com.zbkj.crmeb.pass.service.OnePassService;
import com.zbkj.crmeb.payment.service.OrderPayService;
import com.zbkj.crmeb.sms.service.SmsService;
import com.zbkj.crmeb.store.dao.StoreOrderDao;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreOrderStatus;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductAttrValue;
import com.zbkj.crmeb.store.request.*;
import com.zbkj.crmeb.store.response.*;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import com.zbkj.crmeb.system.model.SystemAdmin;
import com.zbkj.crmeb.system.model.SystemStore;
import com.zbkj.crmeb.system.request.SystemWriteOffOrderSearchRequest;
import com.zbkj.crmeb.system.response.StoreOrderItemResponse;
import com.zbkj.crmeb.system.response.SystemWriteOffOrderResponse;
import com.zbkj.crmeb.system.service.SystemAdminService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemStoreService;
import com.zbkj.crmeb.system.service.SystemStoreStaffService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.request.UserOperateFundsRequest;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import com.zbkj.crmeb.wechat.service.impl.WechatSendMessageForMinService;
import com.zbkj.crmeb.wechat.vo.WechatSendMessageForPaySuccess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StoreOrderServiceImpl 接口实现
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
public class StoreOrderServiceImpl extends ServiceImpl<StoreOrderDao, StoreOrder> implements StoreOrderService {

    @Resource
    private StoreOrderDao dao;

    @Autowired
    private SystemStoreService systemStoreService;

    @Autowired
    private SystemStoreStaffService systemStoreStaffService;

    @Autowired
    private StoreOrderInfoService StoreOrderInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserBillService userBillService;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private StoreOrderRefundService storeOrderRefundService;

    @Autowired
    private ExpressService expressService;

    @Autowired
    private TemplateMessageService templateMessageService;

    @Autowired
    private LogisticService logisticService;

    @Autowired
    private OrderUtils orderUtils;

    private Page<StoreOrder> pageInfo;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreProductAttrValueService storeProductAttrValueService;

    @Autowired
    private WechatSendMessageForMinService wechatSendMessageForMinService;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StorePinkService storePinkService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OnePassService onePassService;

    @Autowired
    private OrderPayService orderPayService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-28
    * @return List<StoreOrder>
    */
    @Override
    public StoreOrderResponse getAdminList(StoreOrderSearchRequest request, PageParamRequest pageParamRequest) {
        StoreOrderResponse storeOrderResponse = new StoreOrderResponse();

        //状态数量
        storeOrderResponse.setStatus(
                new StoreOrderCountItemResponse(
                        getCount(request, Constants.ORDER_STATUS_ALL),
                        getCount(request, Constants.ORDER_STATUS_UNPAID),
                        getCount(request, Constants.ORDER_STATUS_NOT_SHIPPED),
                        getCount(request, Constants.ORDER_STATUS_SPIKE),
                        getCount(request, Constants.ORDER_STATUS_BARGAIN),
                        getCount(request, Constants.ORDER_STATUS_COMPLETE),
                        getCount(request, Constants.ORDER_STATUS_TOBE_WRITTEN_OFF),
                        getCount(request, Constants.ORDER_STATUS_REFUNDING),
                        getCount(request, Constants.ORDER_STATUS_REFUNDED),
                        getCount(request, Constants.ORDER_STATUS_DELETED)
                )
        );

        //列表数据
        List<StoreOrderItemResponse> storeOrderItemResponseArrayList = new ArrayList<>();
        List<StoreOrder> storeOrderList = getList(request, pageParamRequest);

        if(storeOrderList != null && storeOrderList.size() > 0){
            storeOrderItemResponseArrayList = formatOrder(storeOrderList);
        }

        storeOrderResponse.setList(CommonPage.restPage(CommonPage.copyPageInfo(this.pageInfo, storeOrderItemResponseArrayList)));


        //头部数据
        pageParamRequest.setLimit(1);
        storeOrderResponse.setTop(
                new StoreOrderTopItemResponse(
                        getCount(request, request.getStatus()),
                        getAmount(request, null),
                        getAmount(request, Constants.PAY_TYPE_WE_CHAT),
                        getAmount(request, Constants.PAY_TYPE_YUE)
               )
        );
        return storeOrderResponse;
    }

    /**
     * 分页
     * @param request StoreOrderSearchRequest 请求参数
     * @param pageParamRequest PageParamRequest 分页
     * @author Mr.Zhang
     * @since 2020-06-12
     * @return List<StoreOrder>
     */
    @Override
    public List<StoreOrder> getList(StoreOrderSearchRequest request, PageParamRequest pageParamRequest) {
        this.pageInfo = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        getRequestWhere(queryWrapper, request);
        getStatusWhere(queryWrapper, request.getStatus());
        getIsDelWhere(queryWrapper, request.getIsDel());
        queryWrapper.orderByDesc("id");
        return dao.selectList(queryWrapper);
    }

    /**
     * H5 订单列表
     * @param storeOrder 查询参数
     * @param pageParamRequest 分页参数
     * @return 查询结果
     */
    @Override
    public List<StoreOrder> getUserOrderList(StoreOrder storeOrder, PageParamRequest pageParamRequest) {
        this.pageInfo = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        orderUtils.statusApiByWhere(lqw,storeOrder.getStatus());
        if(null != storeOrder.getId()){
            lqw.eq(StoreOrder::getId, storeOrder.getId());
        }
        if(null != storeOrder.getDeliveryId()){
            lqw.eq(StoreOrder::getDeliveryId, storeOrder.getDeliveryId());
        }
        if(null != storeOrder.getUnique()){
            lqw.eq(StoreOrder::getUnique, storeOrder.getUnique());
        }
        if(null != storeOrder.getIsDel()){
            lqw.eq(StoreOrder::getIsDel, storeOrder.getIsDel());
        }
        if(null != storeOrder.getUid()){
            lqw.eq(StoreOrder::getUid, storeOrder.getUid());
        }
        if(null != storeOrder.getOrderId()){
            lqw.eq(StoreOrder::getOrderId, storeOrder.getOrderId());
        }
        if(null != storeOrder.getPayTime()){
            lqw.eq(StoreOrder::getPayTime, storeOrder.getPayTime());
        }
        if(null != storeOrder.getStoreId()){
            lqw.eq(StoreOrder::getStoreId, storeOrder.getStoreId());
        }
        if(null != storeOrder.getShippingType()){
            lqw.eq(StoreOrder::getShippingType, storeOrder.getShippingType());
        }
        if(null != storeOrder.getPayType()){
            lqw.eq(StoreOrder::getPayType, storeOrder.getPayType());
        }

        lqw.orderByDesc(StoreOrder::getCreateTime);
        return dao.selectList(lqw);
    }

    /**
     * 创建订单
     * @param storeOrder 订单参数
     * @return 结果标识
     */
    @Override
    public boolean create(StoreOrder storeOrder) {
        return dao.insert(storeOrder) > 0;
    }

    /**
     * 注意 再次购买的逻辑 orderAgain 存redis 后confirm 接口时 从redis中获取数据再下单 调用orderCreate时 isNew=true 走直接购买逻辑
     * @param userId 用户id
     * @param productId 产品id
     * @param cartNum 商品数量
     * @param orderUnique 商品唯一标识
     * @param type 商品默认类型
     * @param isNew isNew
     * @param combinationId 拼团id
     * @param skillId 秒杀id
     * @param bargainId 砍价id
     * @return
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public List<String> addCartAgain(Integer userId, Integer productId, Integer cartNum, String orderUnique, String type, boolean isNew, Integer combinationId, Integer skillId, Integer bargainId) {
        List<String> cacheIdsResult = new ArrayList<>();
        // 检测订单是否有效
        StoreOrder storeOrderPram = new StoreOrder();
        storeOrderPram.setUnique(orderUnique);
        storeOrderPram.setUid(userId);
        storeOrderPram.setIsDel(false);
        StoreOrder existOrder = getByEntityOne(storeOrderPram);
        if(null == existOrder) throw new CrmebException("订单不存在");
        OrderAgainVo orderAgainVo = orderUtils.tidyOrder(existOrder, true, false);
//        Long cacheKey = DateUtil.getTime()+userId;
        List<StoreCartResponse> orderAgainCache = new ArrayList<>();
        for (StoreOrderInfoVo so : orderAgainVo.getCartInfo()) {
            // 判断商品是否有效
            StoreProduct storeProductPram = new StoreProduct();
            storeProductPram.setId(productId);
            storeProductPram.setIsDel(false);
            storeProductPram.setIsShow(true);
            StoreProduct existProduct = storeProductService.getByEntity(storeProductPram);
            if(null == existProduct) throw new CrmebException("该商品已下架或者删除");

            // 判断商品对应属性是否有效
            StoreProductAttrValue apAttrValuePram = new StoreProductAttrValue();
            apAttrValuePram.setProductId(productId);
            apAttrValuePram.setId(Integer.valueOf(so.getUnique()));
            apAttrValuePram.setType(0);
            List<StoreProductAttrValue> byEntity = storeProductAttrValueService.getByEntity(apAttrValuePram);
            StoreProductAttrValue existSPAttrValue = new StoreProductAttrValue();
            if(null != byEntity && byEntity.size() > 0) existSPAttrValue = byEntity.get(0);
            if(null == existSPAttrValue) throw new CrmebException("请选择有效的商品属性");
            if(existSPAttrValue.getStock() < cartNum) throw new CrmebException("该商品库存不足");
            // 添加有效商品至缓存 缓存为购物车对象
            if(isNew){
                StoreCartResponse storeCartResponse = new StoreCartResponse();
//                storeCartResponse.setId(cacheKey);
                storeCartResponse.setType(type);
                storeCartResponse.setProductId(productId);
                storeCartResponse.setProductAttrUnique(so.getUnique());
                storeCartResponse.setCartNum(cartNum);
                StoreProductCartProductInfoResponse spcpInfo = new StoreProductCartProductInfoResponse();
                BeanUtils.copyProperties(existProduct,spcpInfo);
                spcpInfo.setAttrInfo(existSPAttrValue);
                storeCartResponse.setProductInfo(spcpInfo);
                storeCartResponse.setTrueStock(storeCartResponse.getProductInfo().getAttrInfo().getStock());
                storeCartResponse.setCostPrice(storeCartResponse.getProductInfo().getAttrInfo().getCost());
//                storeCartResponse.setTruePrice(BigDecimal.ZERO);
                storeCartResponse.setTruePrice(existSPAttrValue.getPrice());
                storeCartResponse.setVipTruePrice(BigDecimal.ZERO);
                orderAgainCache.add(storeCartResponse);
            }
        }
        cacheIdsResult.add(orderUtils.setCacheOrderData(userService.getInfo(), orderAgainCache)+"");

        return cacheIdsResult;
    }

    /**
     * 订单基本查询
     * @param storeOrder 订单参数
     * @return 订单查询结果
     */
    @Override
    public List<StoreOrder> getByEntity(StoreOrder storeOrder) {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeOrder);
        return dao.selectList(lqw);
    }

    /**
     * 订单基本查询一条
     * @param storeOrder 参数
     * @return 查询结果
     */
    @Override
    public StoreOrder getByEntityOne(StoreOrder storeOrder) {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(storeOrder);
        return dao.selectOne(lqw);
    }

    /**
     * 更新订单信息
     * @param storeOrder 更新参数
     * @return 更新结果
     */
    @Override
    public boolean updateByEntity(StoreOrder storeOrder) {
        LambdaUpdateWrapper<StoreOrder> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if(null != storeOrder.getPayPrice()){
            lambdaUpdateWrapper.set(StoreOrder::getPayPrice, storeOrder.getPayPrice());
        }
        if(null != storeOrder.getPayType()){
            lambdaUpdateWrapper.set(StoreOrder::getPayType, storeOrder.getPayType());
        }
        if(null != storeOrder.getVerifyCode()){
            lambdaUpdateWrapper.set(StoreOrder::getVerifyCode, storeOrder.getVerifyCode());
        }
        if(null != storeOrder.getShippingType()){
            lambdaUpdateWrapper.set(StoreOrder::getShippingType, storeOrder.getShippingType());
        }
        if(null != storeOrder.getOrderId()){
            lambdaUpdateWrapper.set(StoreOrder::getOrderId, storeOrder.getOrderId());
        }
        if(storeOrder.getIsChannel() > -1){
            lambdaUpdateWrapper.set(StoreOrder::getIsChannel, storeOrder.getIsChannel());
        }
        if(null != storeOrder.getDeliveryType()){
            lambdaUpdateWrapper.set(StoreOrder::getDeliveryType, storeOrder.getDeliveryType());
        }
        if(null != storeOrder.getPaid()){
            lambdaUpdateWrapper.set(StoreOrder::getPaid, storeOrder.getPaid());
        }
        if(null != storeOrder.getStatus()){
            lambdaUpdateWrapper.set(StoreOrder::getStatus, storeOrder.getStatus());
        }
        if(null != storeOrder.getRefundStatus()){
            lambdaUpdateWrapper.set(StoreOrder::getRefundStatus, storeOrder.getRefundStatus());
        }
        if(null != storeOrder.getUid()){
            lambdaUpdateWrapper.set(StoreOrder::getUid, storeOrder.getUid());
        }
        if(null != storeOrder.getIsDel()){
            lambdaUpdateWrapper.set(StoreOrder::getIsDel, storeOrder.getIsDel());
        }
        if(null != storeOrder.getIsSystemDel()){
            lambdaUpdateWrapper.set(StoreOrder::getIsSystemDel, storeOrder.getIsSystemDel());
        }
        if(null != storeOrder.getStoreId()){
            lambdaUpdateWrapper.set(StoreOrder::getStoreId, storeOrder.getStoreId());
        }
        if(null != storeOrder.getPayTime()){
            lambdaUpdateWrapper.set(StoreOrder::getPayTime, storeOrder.getPayTime());
        }
        lambdaUpdateWrapper.eq(StoreOrder::getId, storeOrder.getId());
        return update(lambdaUpdateWrapper);
    }

    /**
     * 余额支付
     * @param currentUser 当前用户
     * @param formId 购买平台标识
     * @return 支付结果
     */
    @Override
    @Transactional
    public boolean yuePay(StoreOrder storeOrder, User currentUser, String formId) {
        if(currentUser.getNowMoney().compareTo(storeOrder.getPayPrice()) < 0){// 支持0元购
            throw new CrmebException("余额不足");
        }
//        BigDecimal priceSubtract = currentUser.getNowMoney().subtract(storeOrder.getPayPrice());
//        UserBill userBill = new UserBill();
//        userBill.setTitle("购买商品");
//        userBill.setUid(currentUser.getUid());
//        userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
//        userBill.setType(Constants.USER_BILL_TYPE_PAY_PRODUCT);
//        userBill.setNumber(storeOrder.getPayPrice());
//        userBill.setLinkId(storeOrder.getId()+"");
//        userBill.setBalance(currentUser.getNowMoney());
//        userBill.setMark("余额支付" + storeOrder.getPayPrice() + "元购买商品");

        UserToken userToken = userTokenService.getByUid(currentUser.getUid());

        Boolean execute = orderPayService.paySuccess(storeOrder);
//        Boolean execute = transactionTemplate.execute(e -> {
//            userService.updateNowMoney(currentUser.getUid(), priceSubtract);
//            userBillService.save(userBill);
//            paySuccess(storeOrder, currentUser, formId);
//            return Boolean.TRUE;
//        });

        // 微信小程序订阅消息 付款成功
//        String storeNameAndCarNumString = orderUtils.getStoreNameAndCarNumString(storeOrder.getId());
//        if(StringUtils.isNotBlank(storeNameAndCarNumString)){
//            WechatSendMessageForPaySuccess paySuccess = new WechatSendMessageForPaySuccess(
//                    storeOrder.getOrderId(),storeOrder.getPayPrice()+"",storeOrder.getPayTime()+"","暂无",
//                    storeOrder.getTotalPrice()+"",storeNameAndCarNumString);
//            orderUtils.sendWeiChatMiniMessageForPaySuccess(paySuccess, currentUser.getUid());
//        }
        return execute;
    }

    /**
     * 核销列表
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return List<StoreOrder>
     */
    @Override
    public SystemWriteOffOrderResponse getWriteOffList(SystemWriteOffOrderSearchRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<StoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        String where = " is_del = 0 and shipping_type = 2";
//        String where = " is_del = 0 and paid = 1";
        //时间
        if(!StringUtils.isBlank(request.getDateLimit())){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            where += " and (create_time between '" + dateLimit.getStartTime() + "' and '" + dateLimit.getEndTime() + "' )";
        }

        if(!StringUtils.isBlank(request.getKeywords())){
            where += " and (real_name like '%"+ request.getKeywords() +"%' or user_phone = '"+ request.getKeywords() +"' or order_id = '" + request.getKeywords() + "' or id = '" + request.getKeywords() + "' )";
        }

        if(request.getStoreId() != null && request.getStoreId() > 0){
            where += " and store_id = " + request.getStoreId();
        }

        SystemWriteOffOrderResponse systemWriteOffOrderResponse = new SystemWriteOffOrderResponse();
        BigDecimal totalPrice = dao.getTotalPrice(where);
        BigDecimal price = new BigDecimal(BigInteger.ZERO);
        if(totalPrice == null){
            totalPrice = price;
        }
        systemWriteOffOrderResponse.setOrderTotalPrice(totalPrice);   //订单总金额

        BigDecimal refundPrice = dao.getRefundPrice(where);
        if(refundPrice == null){
            refundPrice = price;
        }
        systemWriteOffOrderResponse.setRefundTotalPrice(refundPrice); //退款总金额
        systemWriteOffOrderResponse.setRefundTotal(dao.getRefundTotal(where));  //退款总单数


        Page<StoreOrder> storeOrderPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        lambdaQueryWrapper.apply(where);
        lambdaQueryWrapper.orderByDesc(StoreOrder::getId);
        List<StoreOrder> storeOrderList = dao.selectList(lambdaQueryWrapper);

        if(storeOrderList.size() < 1){
            systemWriteOffOrderResponse.setList(CommonPage.restPage(new PageInfo<>()));
            return systemWriteOffOrderResponse;
        }

        List<StoreOrderItemResponse> storeOrderItemResponseArrayList = formatOrder(storeOrderList);

        systemWriteOffOrderResponse.setTotal(storeOrderPage.getTotal()); //总单数
        systemWriteOffOrderResponse.setList(CommonPage.restPage(CommonPage.copyPageInfo(storeOrderPage, storeOrderItemResponseArrayList)));

        return systemWriteOffOrderResponse;
    }

    /**
     * 格式化订单信息，对外输出一致
     * @param storeOrderList List<StoreOrder> 订单列表
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return List<StoreOrderItemResponse>
     */
    private List<StoreOrderItemResponse> formatOrder(List<StoreOrder> storeOrderList) {
        List<StoreOrderItemResponse> storeOrderItemResponseArrayList  = new ArrayList<>();
        if(null == storeOrderList || storeOrderList.size() < 1){
            return storeOrderItemResponseArrayList;
        }
        //门店id
        List<Integer> storeIdList = storeOrderList.stream().map(StoreOrder::getStoreId).distinct().collect(Collectors.toList());
        //店员id / 核销员id
        List<Integer> clerkIdList = storeOrderList.stream().map(StoreOrder::getClerkId).distinct().collect(Collectors.toList());

        //订单id集合
        List<Integer> orderIdList = storeOrderList.stream().map(StoreOrder::getId).distinct().collect(Collectors.toList());

        //获取门店map
        HashMap<Integer, SystemStore> systemStoreList = systemStoreService.getMapInId(storeIdList);
        //获取店员map
//        HashMap<Integer, SystemStoreStaff> systemStoreStaffList = systemStoreStaffService.getMapInId(clerkIdList);
        HashMap<Integer, SystemAdmin> systemStoreStaffList = systemAdminService.getMapInId(clerkIdList);
        //获取订单详情map
        HashMap<Integer, List<StoreOrderInfoVo>> orderInfoList = StoreOrderInfoService.getMapInId(orderIdList);

        //根据用户获取信息
        List<Integer> userIdList = storeOrderList.stream().map(StoreOrder::getUid).distinct().collect(Collectors.toList());
        //订单用户信息
        HashMap<Integer, User> userList = userService.getMapListInUid(userIdList);

        //获取推广人id集合
        List<Integer> spreadPeopleUidList = new ArrayList<>();
        for(Map.Entry<Integer, User> entry : userList.entrySet()){
            spreadPeopleUidList.add(entry.getValue().getSpreadUid());
        }

        //推广信息
        HashMap<Integer, User> mapListInUid = new HashMap<>();
        if(userIdList.size() > 0 && spreadPeopleUidList.size() > 0) {
            //推广人信息
            mapListInUid = userService.getMapListInUid(spreadPeopleUidList);
        }

        for (StoreOrder storeOrder : storeOrderList) {
            StoreOrderItemResponse storeOrderItemResponse = new StoreOrderItemResponse();
            BeanUtils.copyProperties(storeOrder, storeOrderItemResponse);
            String storeName = "";
            if(systemStoreList.containsKey(storeOrder.getStoreId())){
                storeName = systemStoreList.get(storeOrder.getStoreId()).getName();
            }
            storeOrderItemResponse.setStoreName(storeName);

            // 添加核销人信息
            String clerkName = "";
            if(systemStoreStaffList.containsKey(storeOrder.getClerkId())){
                clerkName = systemStoreStaffList.get(storeOrder.getClerkId()).getRealName();
            }
            storeOrderItemResponse.setProductList(orderInfoList.get(storeOrder.getId()));
            storeOrderItemResponse.setTotalNum(storeOrder.getTotalNum());

            //订单状态
            storeOrderItemResponse.setStatusStr(getStatus(storeOrder));
            storeOrderItemResponse.setStatus(storeOrder.getStatus());
            //支付方式
            storeOrderItemResponse.setPayTypeStr(getPayType(storeOrder.getPayType()));

            //推广人信息
            if(!userList.isEmpty()  && null != userList.get(storeOrder.getUid()) && mapListInUid.containsKey(userList.get(storeOrder.getUid()).getSpreadUid())){
                storeOrderItemResponse.getSpreadInfo().setId(mapListInUid.get(userList.get(storeOrder.getUid()).getSpreadUid()).getUid());
                storeOrderItemResponse.getSpreadInfo().setName(mapListInUid.get(userList.get(storeOrder.getUid()).getSpreadUid()).getNickname());
            }
            storeOrderItemResponse.setRefundStatus(storeOrder.getRefundStatus());

            storeOrderItemResponse.setClerkName(clerkName);

            // 添加订单类型信息
            String orderTypeFormat = "[{}订单]{}";
            String orderType = "";
            // 核销
            if (ObjectUtil.isNotNull(storeOrder.getClerkId()) && storeOrder.getClerkId() > 0) {
                orderType = StrUtil.format(orderTypeFormat, "核销", "");
            }
            // 秒杀
            if (ObjectUtil.isNotNull(storeOrder.getSeckillId()) && storeOrder.getSeckillId() > 0) {
                orderType = StrUtil.format(orderTypeFormat, "秒杀", "");
            }
            // 砍价
            if (ObjectUtil.isNotNull(storeOrder.getBargainId()) && storeOrder.getBargainId() > 0) {
                orderType = StrUtil.format(orderTypeFormat, "砍价", "");
            }
            // 拼团
            if (ObjectUtil.isNotNull(storeOrder.getPinkId()) && storeOrder.getPinkId() > 0) {
                StorePink storePink = storePinkService.getById(storeOrder.getPinkId());
                if (ObjectUtil.isNotNull(storePink)) {
                    String pinkstatus = "";
                    if (storePink.getStatus() == 2) {
                        pinkstatus = "已完成";
                    } else if (storePink.getStatus() == 3) {
                        pinkstatus = "未完成";
                    } else {
                        pinkstatus = "正在进行中";
                    }
                    orderType = StrUtil.format(orderTypeFormat, "拼团", pinkstatus);
                }
            }
            if (StrUtil.isBlank(orderType)) {
                orderType = StrUtil.format(orderTypeFormat, "普通", "");
            }
            storeOrderItemResponse.setOrderType(orderType);
            storeOrderItemResponseArrayList.add(storeOrderItemResponse);
        }
        return storeOrderItemResponseArrayList;
    }

    /**
     * 累计消费
     * @param userId Integer 用户id
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return UserBalanceResponse
     */
    @Override
    public BigDecimal getSumBigDecimal(Integer userId, String date) {
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(pay_price) as pay_price").
                eq("paid", 1).
                eq("is_del", 0);
        if(null != userId){
            queryWrapper.eq("uid", userId);
        }
        if(null != date){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        StoreOrder storeOrder = dao.selectOne(queryWrapper);
        if(null == storeOrder || null == storeOrder.getPayPrice()){
            return BigDecimal.ZERO;
        }
        return storeOrder.getPayPrice();
    }

    /**
     * 订单列表返回map
     * @param orderIdList Integer 订单id
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return UserBalanceResponse
     */
    @Override
    public Map<Integer, StoreOrder> getMapInId(List<Integer> orderIdList) {
        Map<Integer, StoreOrder> map = new HashMap<>();
        if (null == orderIdList || orderIdList.size() < 1) {
            return map;
        }
        LambdaQueryWrapper<StoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreOrder::getId, orderIdList);
        List<StoreOrder> orderList = dao.selectList(lambdaQueryWrapper);

        if (null == orderList || orderList.size() < 1) {
            return map;
        }

        for (StoreOrder storeOrder : orderList) {
            map.put(storeOrder.getId(), storeOrder);
        }
        return map;
    }

    /** 获取订单数量
     * @param userId 用户id
     * @param date 字符串月份 moth
     * @return 对应时间段的订单数量
     */
    @Override
    public int getOrderCount(Integer userId, String date) {
        LambdaQueryWrapper<StoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreOrder::getPaid,1)
                .eq(StoreOrder::getIsDel, 0);

        if(null != userId){
            lambdaQueryWrapper.eq(StoreOrder::getUid, userId);
        }
        if(StringUtils.isNotBlank(date)){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            lambdaQueryWrapper.between(StoreOrder::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 按开始结束时间分组订单
     * @param date String 时间范围
     * @param lefTime int 截取创建时间长度
     * @author Mr.Zhang
     * @since 2020-05-16
     * @return HashMap<String, Object>
     */
    public List<StoreOrder> getOrderGroupByDate(String date, int lefTime) {
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(pay_price) as pay_price", "left(create_time, "+lefTime+") as orderId", "count(id) as id");
        if(StringUtils.isNotBlank(date)){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(date);
            queryWrapper.between("create_time", dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        queryWrapper.groupBy("orderId").orderByAsc("orderId");
        return dao.selectList(queryWrapper);
    }

    /** 退款
     * @param request StoreOrderRefundRequest 退款参数
     * @return boolean
     * 这里只处理订单状态
     * 余额支付需要把余额给用户加回去
     * 其余处理放入redis中处理
     */
    @Override
    public boolean refund(StoreOrderRefundRequest request) {
        StoreOrder storeOrder = getById(request.getOrderId());
        if(ObjectUtil.isNull(storeOrder)){throw new CrmebException("未查到订单");}
        if(!storeOrder.getPaid()){throw new CrmebException("未支付无法退款");}
        if(storeOrder.getRefundPrice().add(request.getAmount()).compareTo(storeOrder.getPayPrice()) > 0) {
            throw new CrmebException("退款金额大于支付金额，请修改退款金额");
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            if (storeOrder.getPayPrice().compareTo(BigDecimal.ZERO) != 0) {
                throw new CrmebException("退款金额不能为0，请修改退款金额");
            }
        }

        //用户
        User user = userService.getById(storeOrder.getUid());

        //退款
        if (storeOrder.getPayType().equals(Constants.PAY_TYPE_WE_CHAT) && request.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            try {
                storeOrderRefundService.refund(request, storeOrder);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CrmebException("微信申请退款失败！");
            }
        }

        //修改订单退款状态
        storeOrder.setRefundStatus(3);
        storeOrder.setRefundPrice(request.getAmount());

        Boolean execute = transactionTemplate.execute(e -> {
            updateById(storeOrder);
            if (storeOrder.getPayType().equals(Constants.PAY_TYPE_YUE)) {
                // 更新用户金额 TODO 后期要调整
                UserOperateFundsRequest userOperateFundsRequest = new UserOperateFundsRequest();
                userOperateFundsRequest.setUid(storeOrder.getUid());
                userOperateFundsRequest.setValue(request.getAmount());
                userOperateFundsRequest.setFoundsCategory(Constants.USER_BILL_CATEGORY_MONEY);
                userOperateFundsRequest.setFoundsType(Constants.ORDER_STATUS_REFUNDED);
                userOperateFundsRequest.setType(1);
                userOperateFundsRequest.setTitle(Constants.ORDER_STATUS_STR_REFUNDED);
                userService.updateFounds(userOperateFundsRequest, false); //更新余额
                //新增日志
                userBillService.saveRefundBill(request, user);
                // 退款task
                redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, storeOrder.getId());
            }
            return Boolean.TRUE;
        });
        if(!execute){
            storeOrderStatusService.saveRefund(request.getOrderId(), request.getAmount(), "失败");
            throw new CrmebException("订单更新失败");
        }

        // 发送消息通知
        HashMap<String, String> temMap = new HashMap<>();
        temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "您的订单退款申请被通过，钱款将退还至您的支付账户，请耐心等待。");
        temMap.put("keyword1", storeOrder.getOrderId());
        temMap.put("keyword2", storeOrder.getPayPrice().toString());
        temMap.put("keyword3", DateUtil.dateToStr(storeOrder.getCreateTime(), Constants.DATE_FORMAT));
        temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "感谢你的使用。");
        pushMessageRefundOrder(storeOrder, user, temMap);
//        // 小程序订阅消息 退款成功
//        String storeNameAndCarNumString = orderUtils.getStoreNameAndCarNumString(storeOrder.getId());
//        WechatSendMessageForReFundEd forReFundEd = new WechatSendMessageForReFundEd(
//                "退款成功",storeNameAndCarNumString,request.getAmount()+"",DateUtil.nowDateTimeStr(),"退款金额已到余额中",
//                storeOrder.getOrderId(),storeOrder.getId()+"",storeOrder.getCreateTime()+"",storeOrder.getRefundPrice()+"",
//                storeNameAndCarNumString,storeOrder.getRefundReason(),"CRMEB",storeOrder.getRefundReasonWapExplain(),
//                "暂无"
//        );
//        wechatSendMessageForMinService.sendReFundEdMessage(forReFundEd, userService.getUserIdException());

        return execute;
    }

    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageRefundOrder(StoreOrder storeOrder, User user, HashMap<String, String> temMap) {
        if (user.getUserType().equals(UserConstants.USER_TYPE_H5)) {
            return;
        }
        UserToken userToken;
        // 公众号
        if (user.getUserType().equals(UserConstants.USER_TYPE_WECHAT)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 发送微信模板消息
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_ORDER_REFUND, temMap, userToken.getToken());
            return;
        }
        // 小程序发送订阅消息
        String storeNameAndCarNumString = orderUtils.getStoreNameAndCarNumString(storeOrder.getId());
        if(StringUtils.isNotBlank(storeNameAndCarNumString)){
            WechatSendMessageForPaySuccess paySuccess = new WechatSendMessageForPaySuccess(
                    storeOrder.getId()+"",storeOrder.getPayPrice()+"",storeOrder.getPayTime()+"","暂无",
                    storeOrder.getTotalPrice()+"",storeNameAndCarNumString);
            orderUtils.sendWeiChatMiniMessageForPaySuccess(paySuccess, userService.getById(storeOrder).getUid());
        }
    }

    /** 订单详情
     * @param id Integer 订单id
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return StoreOrderItemResponse
     */
    @Override
    public StoreOrderInfoResponse info(Integer id) {
        StoreOrder storeOrder = getInfoException(id);
        List<StoreOrderInfoVo> orderInfos = StoreOrderInfoService.getOrderListByOrderId(id);
        StoreOrderInfoResponse storeOrderInfoResponse = new StoreOrderInfoResponse();
        BeanUtils.copyProperties(storeOrder, storeOrderInfoResponse);
        storeOrderInfoResponse.setOrderInfo(orderInfos);
        storeOrderInfoResponse.setPayTypeStr(getPayType(storeOrder.getPayType()));
        storeOrderInfoResponse.setStatusStr(getStatus(storeOrder));
        SystemStore systemStorePram = new SystemStore();
        systemStorePram.setId(storeOrder.getStoreId());
        storeOrderInfoResponse.setSystemStore(systemStoreService.getByCondition(systemStorePram));

        //用户信息
        User user = userService.getById(storeOrder.getUid());
        User spread = userService.getById(user.getSpreadUid());
        if(null != spread){
            storeOrderInfoResponse.getSpreadInfo().setId(spread.getUid());
            storeOrderInfoResponse.getSpreadInfo().setName(spread.getNickname());
        }
        storeOrderInfoResponse.setUser(user);
        return storeOrderInfoResponse;
    }

    /** 发送货物
     * @param request StoreOrderSendRequest 发货参数
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return boolean
     */
    @Override
    public boolean send(StoreOrderSendRequest request) {
        //订单信息
        StoreOrder storeOrder = getById(request.getId());
        if (ObjectUtil.isNull(storeOrder)) throw new CrmebException("订单未能查到,不能发货!");
        if (storeOrder.getIsDel()) throw new CrmebException("订单已删除,不能发货!");
        if (storeOrder.getStatus() > 0) throw new CrmebException("订单已发货请勿重复操作!");

        SystemAdmin currentAdmin = systemAdminService.getInfo();

        switch (request.getType()){
            case "1":// 发货
                express(request, storeOrder);
//                orderUtils.sendWeiChatMiniMessageForPackageExpress(storeOrder,currentAdmin.getId());
                break;
            case "2":// 送货
                delivery(request, storeOrder);
//                orderUtils.senWeiChatMiniMessageForDeliver(storeOrder,currentAdmin.getId());
                break;
            case "3":// 虚拟
                virtual(request, storeOrder);
                break;
            default:
                throw new CrmebException("类型错误");
        }

        //短信发送

        return true;
    }

    /**
     * 备注订单
     * @param id integer id
     * @param mark String 备注
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return boolean
     */
    @Override
    public boolean mark(Integer id, String mark) {
        StoreOrder storeOrder = getInfoException(id);
        storeOrder.setRemark(mark);
        return updateById(storeOrder);
    }

    /**
     * 拒绝退款
     * @param id integer id
     * @param reason String 原因
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return boolean
     */
    @Override
    public Boolean refundRefuse(Integer id, String reason) {
        StoreOrder storeOrder = getInfoException(id);
        storeOrder.setRefundReason(reason);
        storeOrder.setRefundStatus(0);

        User user = userService.getById(storeOrder.getUid());

        Boolean execute = transactionTemplate.execute(e -> {
            updateById(storeOrder);
            storeOrderStatusService.createLog(storeOrder.getId(), Constants.ORDER_LOG_REFUND_REFUSE, Constants.ORDER_LOG_MESSAGE_REFUND_REFUSE.replace("{reason}", reason));
            return Boolean.TRUE;
        });
        if (execute) {
            // 如果是拼团订单要将拼团状态改回去
            if (ObjectUtil.isNotNull(storeOrder) && storeOrder.getPinkId() > 0) {
                StorePink storePink = storePinkService.getById(storeOrder.getPinkId());
                if (storePink.getStatus().equals(3)) {
                    storePink.setStatus(1);
                    storePinkService.updateById(storePink);
                }
            }
            // 发送消息通知
            HashMap<String, String> temMap = new HashMap<>();
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "您的订单退款申请被拒绝！");
            temMap.put("keyword1", storeOrder.getOrderId());
            temMap.put("keyword2", storeOrder.getPayPrice().toString());
            temMap.put("keyword3", DateUtil.dateToStr(storeOrder.getCreateTime(), Constants.DATE_FORMAT));
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "拒绝原因："+ reason);
            pushMessageRefundOrder(storeOrder, user, temMap);
        }
        return execute;
    }

    /**
     * 查询单条
     * @param storeOrder StoreOrder 订单参数
     * @author Mr.Zhang
     * @since 2020-05-28
     * @return StoreOrder
     */
    @Override
    public StoreOrder getInfoByEntity(StoreOrder storeOrder) {
        LambdaQueryWrapper<StoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.setEntity(storeOrder);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 根据条件获取
     * @param storeOrder 订单条件
     * @return 结果
     */
    @Override
    public StoreOrder getInfoJustOrderInfo(StoreOrder storeOrder) {
        LambdaQueryWrapper<StoreOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(null != storeOrder.getUnique()){
            lambdaQueryWrapper.or().eq(StoreOrder::getOrderId, storeOrder.getUnique())
            .or().eq(StoreOrder::getUnique, storeOrder.getUnique());
        }
//        if(null != storeOrder.getUid()){
            lambdaQueryWrapper.eq(StoreOrder::getUid, storeOrder.getUid());
//        }
        if(null != storeOrder.getIsDel()){
            lambdaQueryWrapper.eq(StoreOrder::getIsDel, storeOrder.getIsDel());
        }
        return dao.selectOne(lambdaQueryWrapper);
    }

    @Override
    public LogisticsResultVo getLogisticsInfo(Integer id) {
        StoreOrder info = getById(id);
        if(StringUtils.isBlank(info.getDeliveryId())){
            //没有查询到快递信息
            throw new CrmebException("没有查询到快递信息");
        }

        return logisticService.info(info.getDeliveryId(), null, Optional.ofNullable(info.getDeliveryCode()).orElse(""), info.getUserPhone());
    }

    /**
     * 根据用户id获取订单数据
     * @param userId 用户id
     * @return
     */
    @Override
    public RetailShopOrderDataResponse getOrderDataByUserId(Integer userId) {
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(pay_price) as totalPrice, count(id) as id");
        queryWrapper.ge("paid",1);
        queryWrapper.ge("refund_status",0);
        queryWrapper.eq("uid", userId);
        queryWrapper.groupBy("uid");
        StoreOrder storeOrder = dao.selectOne(queryWrapper);
        RetailShopOrderDataResponse rop = new RetailShopOrderDataResponse();
        if(null != storeOrder){
            rop.setOrderCount(storeOrder.getId()); // id=借变量传递数据
            rop.setOrderPrice(storeOrder.getTotalPrice());
        }

        return rop;
    }

    /**
     * 根据用户id集合获取对应订单，分销中使用
     * @param ids 用户id集合
     * @return 对应用户订单集合
     */
    @Override
    public List<StoreOrder> getOrderByUserIdsForRetailShop(List<Integer> ids) {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.in(StoreOrder::getUid,ids);
        lqw.eq(StoreOrder::getPaid, 1);
        lqw.eq(StoreOrder::getRefundStatus, 0);
        lqw.eq(StoreOrder::getIsDel, false);
        return dao.selectList(lqw);
    }

    /**
     * 订单 top 查询参数
     * @param status 状态参数
     * @return 订单查询结果
     */
    @Override
    public List<StoreOrder> getTopDataUtil(int status, int userId) {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        orderUtils.statusApiByWhere(lqw,status);
        lqw.eq(StoreOrder::getIsDel, false);
        lqw.eq(StoreOrder::getUid,userId);
        return dao.selectList(lqw);
    }

    /**
     * 更改订单价格
     *
     * @param request 订单id改价对象
     * @return 更改结果
     */
    @Override
    public boolean editPrice(StoreOrderEditPriceRequest request) {
        String oldPrice;
        StoreOrder existOrder = getByOderId(request.getOrderId());
        // 订单不存在
        if(null == existOrder) {
            throw new CrmebException(Constants.RESULT_ORDER_NOTFOUND.replace("${orderCode}", request.getOrderId()));
        }
        // 订单已支付
        if(existOrder.getPaid()) {
            throw new CrmebException(Constants.RESULT_ORDER_PAYED.replace("${orderCode}", request.getOrderId()));
        }
        // 修改价格和原来价格相同
        if(existOrder.getPayPrice().compareTo(request.getPrice()) ==0) {
            throw new CrmebException(Constants.RESULT_ORDER_EDIT_PRICE_SAME.replace("${oldPrice}",existOrder.getPayPrice()+"")
                    .replace("${editPrice}",request.getPrice()+""));
        }

        oldPrice = existOrder.getPayPrice()+"";

        Boolean execute = transactionTemplate.execute(e -> {
            // 修改订单价格
            orderEditPrice(request.getOrderId(), request.getPrice());
            // 订单修改状态操作
            storeOrderStatusService.createLog(existOrder.getId(), Constants.ORDER_LOG_EDIT,
                    Constants.RESULT_ORDER_EDIT_PRICE_LOGS.replace("${orderPrice}", oldPrice)
                            .replace("${price}", request.getPrice() + ""));
            return Boolean.TRUE;
        });
        if(!execute) {
            throw new CrmebException(Constants.RESULT_ORDER_EDIT_PRICE_SUCCESS
                    .replace("${orderNo}", existOrder.getOrderId()).replace("${price}", request.getPrice()+""));
        }
        // 发送改价短信提醒
        User user = userService.getById(existOrder.getUid());
        if (StrUtil.isNotBlank(user.getPhone())) {
            // 改价短信开关是否开启
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                // 发送改价短信提醒
                smsService.sendOrderEditPriceNotice(user.getPhone(), existOrder.getOrderId(), request.getPrice());
            }
        }

        return execute;
    }

    /**
     * 改价
     * @param orderNo 订单编号
     * @param price 修改后的价格
     */
    private Boolean orderEditPrice(String orderNo, BigDecimal price) {
        LambdaUpdateWrapper<StoreOrder> luw = new LambdaUpdateWrapper<>();
        luw.set(StoreOrder::getPayPrice, price);
        luw.eq(StoreOrder::getOrderId, orderNo);
        luw.eq(StoreOrder::getPaid, false);
        return update(luw);
    }

    /**
     * 确认付款
     *
     * @param orderId 订单号
     * @return 确认付款结果
     */
    @Override
    public boolean confirmPayed(String orderId) {
        StoreOrder existOrder = getByEntityOne(new StoreOrder().setOrderId(orderId));
        if(null == existOrder) throw new CrmebException(Constants.RESULT_ORDER_NOTFOUND.replace("${orderCode}", orderId));
        return payOrderOffLine(existOrder.getId());
    }

    /**
     * 线下付款
     *
     * @param orderId 待付款订单id
     * @return 付款结果
     */
    @Override
    public boolean payOrderOffLine(Integer orderId) {
        StoreOrder existOrder = getByEntityOne(new StoreOrder().setId(orderId));
        if(null == existOrder) throw new CrmebException(
                Constants.RESULT_ORDER_NOTFOUND_IN_ID.replace("${orderId}", orderId+""));
        if(existOrder.getPaid()) throw new CrmebException(Constants.RESULT_ORDER_PAYED.replace("${orderCode}",existOrder.getOrderId()));
        existOrder.setPaid(true);
        // 订单修改状态操作
        storeOrderStatusService.createLog(existOrder.getId(),Constants.ORDER_LOG_PAY_OFFLINE,
                Constants.RESULT_ORDER_PAY_OFFLINE.replace("${orderNo}",existOrder.getOrderId())
                        .replace("${price}", existOrder.getPayPrice()+""));
        return updateById(existOrder);
    }

    /**
     * 根据时间参数统计订单销售额
     *
     * @param dateLimit 时间区间
     * @param type 类型
     * @return 统计订单信息
     */
    @Override
    public StoreOrderStatisticsResponse orderStatisticsByTime(String dateLimit,Integer type) {
        StoreOrderStatisticsResponse response = new StoreOrderStatisticsResponse();
        // 根据开始时间和结束时间获取时间差 再根据时间差获取上一个时间段 查询当前和上一个时间段的数据 进行比较且返回
        dateLimitUtilVo dateRange = DateUtil.getDateLimit(dateLimit);
        String dateStartD = dateRange.getStartTime();
        String dateEndD = dateRange.getEndTime();
        int days = DateUtil.daysBetween(
                DateUtil.strToDate(dateStartD,Constants.DATE_FORMAT_DATE),
                DateUtil.strToDate(dateEndD,Constants.DATE_FORMAT_DATE)
        );
        // 同时间区间的上一个时间起点
        String perDateStart = DateUtil.addDay(
                DateUtil.strToDate(dateStartD,Constants.DATE_FORMAT_DATE), -days, Constants.DATE_FORMAT_START);
        // 当前时间区间
        String dateStart = DateUtil.addDay(
                DateUtil.strToDate(dateStartD,Constants.DATE_FORMAT_DATE),0,Constants.DATE_FORMAT_START);
        String dateEnd = DateUtil.addDay(
                DateUtil.strToDate(dateEndD,Constants.DATE_FORMAT_DATE),0,Constants.DATE_FORMAT_END);

        // 上一个时间段查询
        List<StoreOrder> orderPerList = getOrderPayedByDateLimit(perDateStart,dateStart);

        // 当前时间段
        List<StoreOrder> orderCurrentList = getOrderPayedByDateLimit(dateStart, dateEnd);
        double increasePrice = 0;
        if(type == 1){
            double perSumPrice = orderPerList.stream().mapToDouble(e -> e.getPayPrice().doubleValue()).sum();
            double currentSumPrice = orderCurrentList.stream().mapToDouble(e -> e.getPayPrice().doubleValue()).sum();

            response.setChart(dao.getOrderStatisticsPriceDetail(new StoreDateRangeSqlPram(dateStart,dateEnd)));
            response.setTime(BigDecimal.valueOf(currentSumPrice).setScale(2,BigDecimal.ROUND_HALF_UP));
            // 当前营业额和上一个同比营业额增长区间
            increasePrice = currentSumPrice - perSumPrice;
            if(increasePrice <= 0) response.setGrowthRate(0);
            else if(perSumPrice == 0) response.setGrowthRate((int) increasePrice * 100);
            else response.setGrowthRate((int)((increasePrice * perSumPrice) * 100));
        }else if(type ==2){
            response.setChart(dao.getOrderStatisticsOrderCountDetail(new StoreDateRangeSqlPram(dateStart,dateEnd)));
            response.setTime(BigDecimal.valueOf(orderCurrentList.size()));
            increasePrice = orderCurrentList.size() - orderPerList.size();
            if(increasePrice <= 0) response.setGrowthRate(0);
            else if(orderPerList.size() == 0) response.setGrowthRate((int) increasePrice);
            else response.setGrowthRate((int)((increasePrice / orderPerList.size()) * 100));
        }
        response.setIncreaseTime(increasePrice+"");
        response.setIncreaseTimeStatus(increasePrice >= 0 ? 1:2);
        return response;
    }

    /**
     * 获取用户当天的秒杀数量
     *
     * @param storeOrder 订单查询参数
     * @return 用户当天的秒杀商品订单数量
     */
    @Override
    public List<StoreOrder> getUserCurrentDaySecKillOrders(StoreOrder storeOrder) {
        String dayStart = DateUtil.nowDateTime(Constants.DATE_FORMAT_START);
        String dayEnd = DateUtil.nowDateTime(Constants.DATE_FORMAT_END);
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getUid,storeOrder.getUid()).eq(StoreOrder::getSeckillId,storeOrder.getSeckillId())
                .between(StoreOrder::getCreateTime,dayStart,dayEnd);
        lqw.eq(StoreOrder::getIsDel, false);
        return dao.selectList(lqw);
    }

    /**
     * 获取用户当前的砍价订单数量
     * @param storeOrder    订单查询参数
     * @return
     */
    @Override
    public List<StoreOrder> getUserCurrentBargainOrders(StoreOrder storeOrder) {
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getUid,storeOrder.getUid()).eq(StoreOrder::getBargainId,storeOrder.getBargainId());
        lqw.eq(StoreOrder::getIsDel, false);
        return dao.selectList(lqw);
    }

    /**
     * 获取砍价商品订单数量（销量）
     * 用户砍价成功支付订单数量
     * @param bargainId 砍价商品编号
     * @return
     */
    @Override
    public Integer getCountByBargainId(Integer bargainId) {
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getBargainId, bargainId);
        lqw.eq(StoreOrder::getPaid, true);
        lqw.eq(StoreOrder::getRefundStatus, 0);
        return dao.selectCount(lqw);
    }

    /**
     * 获取砍价商品订单数量（销量）
     * 用户砍价成功支付订单数量
     * @param bargainId 砍价商品编号
     * @return
     */
    @Override
    public Integer getCountByBargainIdAndUid(Integer bargainId, Integer uid) {
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getBargainId, bargainId);
        lqw.eq(StoreOrder::getUid, uid);
        lqw.eq(StoreOrder::getPaid, true);
        lqw.eq(StoreOrder::getRefundStatus, 0);
        return dao.selectCount(lqw);
    }

    @Override
    public StoreOrder getByOderId(String orderId) {
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getOrderId, orderId);
        return dao.selectOne(lqw);
    }

    /**
     * 获取面单默认配置信息
     * @return
     */
    @Override
    public ExpressSheetVo getDeliveryInfo() {
        return systemConfigService.getDeliveryInfo();
    }

    @Override
    public PageInfo<StoreOrder> findListByUserIdsForRetailShop(List<Integer> userIds, RetailShopStairUserRequest request, PageParamRequest pageParamRequest) {
        Page<StoreOrder> storeOrderPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.in(StoreOrder::getUid, userIds);
        lqw.ge(StoreOrder::getPaid, 1);
        lqw.ge(StoreOrder::getRefundStatus, 0);
        if(StrUtil.isNotBlank(request.getDateLimit())){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            lqw.between(StoreOrder::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        if(StrUtil.isNotBlank(request.getNickName())){
            lqw.eq(StoreOrder::getOrderId, request.getNickName());
        }
        lqw.orderByDesc(StoreOrder::getId);
        return CommonPage.copyPageInfo(storeOrderPage, dao.selectList(lqw));
    }

    /**
     * 更新支付结果
     * @param orderNo 订单编号
     * @return Boolean
     */
    @Override
    public Boolean updatePaid(String orderNo) {
        LambdaUpdateWrapper<StoreOrder> lqw = new LambdaUpdateWrapper<>();
        lqw.set(StoreOrder::getPaid, true);
        lqw.set(StoreOrder::getPayTime, DateUtil.nowDateTime());
        lqw.eq(StoreOrder::getOrderId, orderNo);
        lqw.eq(StoreOrder::getPaid,false);
        return update(lqw);
    }

    @Override
    public Map<String, StoreOrder> getMapInOrderNo(List<String> orderNoList) {
        Map<String, StoreOrder> map = CollUtil.newHashMap();
        LambdaUpdateWrapper<StoreOrder> lqw = new LambdaUpdateWrapper<>();
        lqw.in(StoreOrder::getOrderId, orderNoList);
        List<StoreOrder> orderList = dao.selectList(lqw);
        orderList.forEach(order -> {
            map.put(order.getOrderId(), order);
        });
        return map;
    }

    /**
     * 获取推广订单总金额
     * @param orderNoList 订单编号列表
     * @return
     */
    @Override
    public BigDecimal getSpreadOrderTotalPriceByOrderList(List<String> orderNoList) {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.select(StoreOrder::getPayPrice);
        lqw.in(StoreOrder::getOrderId, orderNoList);
        List<StoreOrder> orderList = dao.selectList(lqw);
        return orderList.stream().map(StoreOrder::getPayPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取所有收货订单id集合
     * @return
     */
    @Override
    public List<StoreOrder> findIdAndUidListByReceipt() {
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.select(StoreOrder::getId, StoreOrder::getUid);
        lqw.eq(StoreOrder::getStatus, 2);
        lqw.eq(StoreOrder::getRefundStatus, 0);
        lqw.eq(StoreOrder::getIsDel, false);
        List<StoreOrder> orderList = dao.selectList(lqw);
        if (CollUtil.isEmpty(orderList)) {
            return CollUtil.newArrayList();
        }
        return orderList;
    }

    /**
     *
     * @param userId 用户uid
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public List<StoreOrder> findPaidListByUid(Integer userId, PageParamRequest pageParamRequest) {
        Page<StoreOrder> orderPage = pageInfo = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreOrder> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreOrder::getUid, userId);
        lqw.eq(StoreOrder::getPaid, true);
        lqw.eq(StoreOrder::getIsDel, false);
//        lqw.in(StoreOrder::getRefundStatus, 0, 1);
        lqw.orderByDesc(StoreOrder::getId);
        return dao.selectList(lqw);
    }

///////////////////////////////////////////////////////////////////////////////////////////////////// 以下为自定义方法

    /**
     * 根据时间参数获取有效订单
     * @return 有效订单列表
     */
    private List<StoreOrder> getOrderPayedByDateLimit(String startTime, String endTime){
        LambdaQueryWrapper<StoreOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrder::getIsDel, false).eq(StoreOrder::getPaid, true).eq(StoreOrder::getRefundStatus,0)
                .between(StoreOrder::getCreateTime, startTime, endTime);
     return dao.selectList(lqw);
    }

    /** 退款扣除积分/余额
     * @param request StoreOrderRefundRequest 退款参数
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    private void subtractBill(StoreOrderRefundRequest request, String category, String type, String foundsType) {
        try{
            FundsMonitorSearchRequest fundsMonitorSearchRequest = new FundsMonitorSearchRequest();
            fundsMonitorSearchRequest.setCategory(category);
            fundsMonitorSearchRequest.setType(type);
            fundsMonitorSearchRequest.setLinkId(request.getOrderId().toString());
            fundsMonitorSearchRequest.setPm(1);

            PageParamRequest pageParamRequest = new PageParamRequest();
            pageParamRequest.setLimit(Constants.EXPORT_MAX_LIMIT);
            List<UserBill> list = userBillService.getList(fundsMonitorSearchRequest, pageParamRequest);

            if(null == list || list.size() < 1){
                return;
            }

            for (UserBill userBill : list) {
                User user = userService.getById(userBill.getUid());
                if(null == user){
                    continue;
                }
                BigDecimal price;
                if(category.equals(Constants.USER_BILL_CATEGORY_INTEGRAL)){
                    price = new BigDecimal(user.getIntegral());
                }else{
                    price = user.getBrokeragePrice();
                }

                if(userBill.getNumber().compareTo(price) > 0){
                    userBill.setNumber(price);
                    //更新佣金
                    UserOperateFundsRequest userOperateFundsRequest = new UserOperateFundsRequest();
                    userOperateFundsRequest.setUid(user.getUid());
                    userOperateFundsRequest.setValue(request.getAmount());
                    userOperateFundsRequest.setFoundsType(foundsType);
                    userOperateFundsRequest.setType(0);
                    userService.updateFounds(userOperateFundsRequest, false); //更新佣金/积分
                    if(category.equals(Constants.USER_BILL_CATEGORY_INTEGRAL)){
                        userBillService.saveRefundIntegralBill(request, user);
                    }else{
                        userBillService.saveRefundBrokeragePriceBill(request, user);
                    }
                }
            }
        }catch (Exception e){
            throw new CrmebException("更新退款佣金/积分失败");
        }
    }

    /** 发货
     * @param id Integer id
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return Boolean
     */
    public StoreOrder getInfoException(Integer id) {
        StoreOrder info = getById(id);
        if(null == info){
            throw new CrmebException("没有找到订单信息");
        }
        return info;
    }


    /** 快递
     * @param request StoreOrderSendRequest 发货参数
     * @param storeOrder StoreOrder 订单信息
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    private void express(StoreOrderSendRequest request, StoreOrder storeOrder) {
        // 校验快递发货参数
        validateExpressSend(request);
        //快递公司信息
        Express express = expressService.getByCode(request.getExpressCode());
        if (request.getExpressRecordType().equals("1")) { // 正常发货
            deliverGoods(request, storeOrder, express);
        }
        if (request.getExpressRecordType().equals("2")) { // 电子面单
            request.setExpressName(express.getName());
            expressDump(request, storeOrder, express);
        }

        storeOrder.setDeliveryCode(express.getCode());
        storeOrder.setDeliveryName(express.getName());
        storeOrder.setStatus(1);
        storeOrder.setDeliveryType("express");

        String message = Constants.ORDER_LOG_MESSAGE_EXPRESS.replace("{deliveryName}", express.getName()).replace("{deliveryCode}", storeOrder.getDeliveryId());

        Boolean execute = transactionTemplate.execute(i -> {
            updateById(storeOrder);
            //订单记录增加
            storeOrderStatusService.createLog(request.getId(), Constants.ORDER_LOG_EXPRESS, message);
            return Boolean.TRUE;
        });

        if (!execute) throw new CrmebException("快递发货失败！");
        User user = userService.getById(storeOrder.getUid());
        if (StrUtil.isNotBlank(user.getPhone())) {
            // 发货短信提醒
            String smsSwitch = systemConfigService.getValueByKey(SmsConstants.SMS_CONFIG_DELIVER_GOODS_SWITCH);
            if (StrUtil.isNotBlank(smsSwitch) && smsSwitch.equals("1")) {
                String proName = "";
                List<StoreOrderInfoVo> voList = storeOrderInfoService.getOrderListByOrderId(storeOrder.getId());
                proName = voList.get(0).getInfo().getProductInfo().getStoreName();
                if (voList.size() > 1) {
                    proName = proName.concat("等");
                }
                smsService.sendOrderDeliverNotice(user.getPhone(), user.getNickname(), proName, storeOrder.getOrderId());
            }
        }

        // 发送消息通知
        pushMessageOrder(storeOrder, user);

//        //微信模板消息发送
//        HashMap<String, String> map = new HashMap<>();
//        map.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "订单发货提醒");
//        map.put("keyword1", storeOrder.getOrderId());
//        map.put("keyword2", storeOrder.getDeliveryName());
//        map.put("keyword3", storeOrder.getDeliveryId());
//        map.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎再次购买！");
//        templateMessageService.push(Constants.WE_CHAT_TEMP_KEY_EXPRESS, map, storeOrder.getUid(), Constants.PAY_TYPE_WE_CHAT_FROM_PUBLIC);
    }

    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageOrder(StoreOrder storeOrder, User user) {
        if (user.getUserType().equals(UserConstants.USER_TYPE_H5)) {
            return;
        }
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        // 公众号
        if (user.getUserType().equals(UserConstants.USER_TYPE_WECHAT)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 发送微信模板消息
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "订单发货提醒");
            temMap.put("keyword1", storeOrder.getOrderId());
            temMap.put("keyword2", storeOrder.getDeliveryName());
            temMap.put("keyword3", storeOrder.getDeliveryId());
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎再次购买！");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_EXPRESS, temMap, userToken.getToken());
            return;
        }
        // 小程序发送订阅消息
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }
        // 组装数据
        temMap.put("character_string1", storeOrder.getOrderId());
        temMap.put("name3", storeOrder.getDeliveryName());
        temMap.put("character_string4", storeOrder.getDeliveryId());
        temMap.put("thing7", "您的订单已发货");
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_EXPRESS, temMap, userToken.getToken());
    }

    /**
     * 电子面单
     * @param request
     * @param storeOrder
     * @param express
     */
    private void expressDump(StoreOrderSendRequest request, StoreOrder storeOrder, Express express) {
        String configExportOpen = systemConfigService.getValueByKeyException("config_export_open");
        if (!configExportOpen.equals("1")) {// 电子面单未开启
            throw new CrmebException("请先开启电子面单");
        }
        MyRecord record = new MyRecord();
        record.set("com", express.getCode());// 快递公司编码
        record.set("to_name", storeOrder.getRealName());// 收件人
        record.set("to_tel", storeOrder.getUserPhone());// 收件人电话
        record.set("to_addr", storeOrder.getUserAddress());// 收件人详细地址
        record.set("from_name", request.getToName());// 寄件人
        record.set("from_tel", request.getToTel());// 寄件人电话
        record.set("from_addr", request.getToAddr());// 寄件人详细地址
        record.set("temp_id", request.getExpressTempId());// 电子面单模板ID
        String siid = systemConfigService.getValueByKeyException("config_export_siid");
        record.set("siid", siid);// 云打印机编号
        record.set("count", storeOrder.getTotalNum());// 商品数量

        //获取购买商品名称
        List<Integer> orderIdList = new ArrayList<>();
        orderIdList.add(storeOrder.getId());
        HashMap<Integer, List<StoreOrderInfoVo>> orderInfoMap = StoreOrderInfoService.getMapInId(orderIdList);
        if(orderInfoMap.isEmpty() || !orderInfoMap.containsKey(storeOrder.getId())){
            throw new CrmebException("没有找到购买的商品信息");
        }
        List<String> productNameList = new ArrayList<>();
        for (StoreOrderInfoVo storeOrderInfoVo : orderInfoMap.get(storeOrder.getId())) {
            productNameList.add(storeOrderInfoVo.getInfo().getProductInfo().getStoreName());
        }

        record.set("cargo", String.join(",", productNameList));// 物品名称
        if (express.getPartnerId()) {
            record.set("partner_id", express.getAccount());// 电子面单月结账号(部分快递公司必选)
        }
        if (express.getPartnerKey()) {
            record.set("partner_key", express.getPassword());// 电子面单密码(部分快递公司必选)
        }
        if (express.getNet()) {
            record.set("net", express.getNetName());// 收件网点名称(部分快递公司必选)
        }

        MyRecord myRecord = onePassService.expressDump(record);
        storeOrder.setDeliveryId(myRecord.getStr("kuaidinum"));
    }

    /**
     * 正常发货
     */
    private void deliverGoods(StoreOrderSendRequest request, StoreOrder storeOrder, Express express) {
        storeOrder.setDeliveryId(request.getExpressNumber());
    }

    /**
     * 校验快递发货参数
     */
    private void validateExpressSend(StoreOrderSendRequest request) {
        if (request.getExpressRecordType().equals("1")) {
            if (StrUtil.isBlank(request.getExpressNumber())) throw new CrmebException("请填写快递单号");
            return;
        }
//        if (StrUtil.isBlank(request.getExpressName())) throw new CrmebException("请选择快递公司");
        if (StrUtil.isBlank(request.getExpressCode())) throw new CrmebException("请选择快递公司");
        if (StrUtil.isBlank(request.getExpressRecordType())) throw new CrmebException("请选择发货记录类型");
        if (StrUtil.isBlank(request.getExpressTempId())) throw new CrmebException("请选择电子面单");
        if (StrUtil.isBlank(request.getToName())) throw new CrmebException("请填写寄件人姓名");
        if (StrUtil.isBlank(request.getToTel())) throw new CrmebException("请填写寄件人电话");
        if (StrUtil.isBlank(request.getToAddr())) throw new CrmebException("请填写寄件人地址");
    }

    /** 送货上门
     * @param request StoreOrderSendRequest 发货参数
     * @param storeOrder StoreOrder 订单信息
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    private void delivery(StoreOrderSendRequest request, StoreOrder storeOrder) {
        if (StrUtil.isBlank(request.getDeliveryName())) throw new CrmebException("请输入送货人姓名");
        if (StrUtil.isBlank(request.getDeliveryTel())) throw new CrmebException("请输入送货人电话号码");
        ValidateFormUtil.isPhone(request.getDeliveryTel(), "送货人联系方式");

        //送货信息
        storeOrder.setDeliveryName(request.getDeliveryName());
        storeOrder.setDeliveryId(request.getDeliveryTel());
        storeOrder.setStatus(1);
        storeOrder.setDeliveryType("send");

        //获取购买商品名称
        List<Integer> orderIdList = new ArrayList<>();
        orderIdList.add(storeOrder.getId());
        HashMap<Integer, List<StoreOrderInfoVo>> orderInfoMap = StoreOrderInfoService.getMapInId(orderIdList);
        if(orderInfoMap.isEmpty() || !orderInfoMap.containsKey(storeOrder.getId())){
            throw new CrmebException("没有找到购买的商品信息");
        }
        List<String> productNameList = new ArrayList<>();
        for (StoreOrderInfoVo storeOrderInfoVo : orderInfoMap.get(storeOrder.getId())) {
            productNameList.add(storeOrderInfoVo.getInfo().getProductInfo().getStoreName());
        }

        String message = Constants.ORDER_LOG_MESSAGE_DELIVERY.replace("{deliveryName}", request.getDeliveryName()).replace("{deliveryCode}", request.getDeliveryTel());

        Boolean execute = transactionTemplate.execute(i -> {
            // 更新订单
            updateById(storeOrder);
            // 订单记录增加
            storeOrderStatusService.createLog(request.getId(), Constants.ORDER_LOG_DELIVERY, message);
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("订单更新送货失败");

        User user = userService.getById(storeOrder.getUid());
        // 发送消息通知
        pushMessageDeliveryOrder(storeOrder, user, request, productNameList);
    }

    /**
     * 发送消息通知
     * 根据用户类型发送
     * 公众号模板消息
     * 小程序订阅消息
     */
    private void pushMessageDeliveryOrder(StoreOrder storeOrder, User user, StoreOrderSendRequest request, List<String> productNameList) {
        if (user.getUserType().equals(UserConstants.USER_TYPE_H5)) {
            return;
        }
        UserToken userToken;
        HashMap<String, String> map = new HashMap<>();
        String proName = "";
        if (CollUtil.isNotEmpty(productNameList)) {
            proName = StringUtils.join(productNameList, "|");
        }
        // 公众号
        if (user.getUserType().equals(UserConstants.USER_TYPE_WECHAT)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            map.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "订单配送提醒");
            map.put("keyword1", storeOrder.getOrderId());
            map.put("keyword2", DateUtil.dateToStr(storeOrder.getCreateTime(), Constants.DATE_FORMAT));
            map.put("keyword3", storeOrder.getUserAddress());
            map.put("keyword4", request.getDeliveryName());
            map.put("keyword5", request.getDeliveryTel());
            map.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎再次购买！");
            // 发送微信模板消息
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_DELIVERY, map, userToken.getToken());
            return;
        }
        // 小程序发送订阅消息
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }

        if (proName.length() > 20) {
            proName = proName.substring(0, 15) + "***";
        }
        map.put("thing8", proName);
        map.put("character_string1", storeOrder.getOrderId());
        map.put("name4", request.getDeliveryName());
        map.put("phone_number10", request.getDeliveryTel());
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_DELIVERY, map, userToken.getToken());
//        String storeNameAndCarNumString = orderUtils.getStoreNameAndCarNumString(storeOrder.getId());
//        if(StringUtils.isNotBlank(storeNameAndCarNumString)){
//            WechatSendMessageForPaySuccess paySuccess = new WechatSendMessageForPaySuccess(
//                    storeOrder.getId()+"",storeOrder.getPayPrice()+"",storeOrder.getPayTime()+"","暂无",
//                    storeOrder.getTotalPrice()+"",storeNameAndCarNumString);
//            orderUtils.sendWeiChatMiniMessageForPaySuccess(paySuccess, userService.getById(storeOrder).getUid());
//        }
    }

    /** 虚拟
     * @param request StoreOrderSendRequest 发货参数
     * @param storeOrder StoreOrder 订单信息
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    private void virtual(StoreOrderSendRequest request, StoreOrder storeOrder) {
        //快递信息
        storeOrder.setDeliveryType("fictitious");
        storeOrder.setStatus(1);

        Boolean execute = transactionTemplate.execute(i -> {
            updateById(storeOrder);
            //订单记录增加
            storeOrderStatusService.createLog(request.getId(), Constants.ORDER_LOG_DELIVERY_VI, Constants.ORDER_LOG_DELIVERY_VI);
            return Boolean.TRUE;
        });
        if (!execute) throw new CrmebException("虚拟物品发货失败");
        //微信模板消息发送
    }
    /**
     * 支付成功操作
     * @param storeOrder 当前支付订单
     * @param currentUser 当前用户
     * @param formId 平台
     * @return 成功处理结果
     */
    public boolean paySuccess(StoreOrder storeOrder,User currentUser, String formId){
        // 更新订单为已支付状态
        StoreOrder storeOrderUpdate = new StoreOrder();
        storeOrderUpdate.setOrderId(storeOrder.getOrderId());
        storeOrderUpdate.setId(storeOrder.getId());
        storeOrderUpdate.setPaid(true);
        storeOrderUpdate.setPayType(storeOrder.getPayType());
        storeOrderUpdate.setPayTime(new Date());
        boolean orderUpdate2PayResult = updateById(storeOrderUpdate);
        StoreOrderStatus storeOrderStatus = new StoreOrderStatus();
        storeOrderStatus.setOid(storeOrderUpdate.getId());
        storeOrderStatus.setChangeType(Constants.ORDER_LOG_PAY_SUCCESS);
        storeOrderStatus.setChangeMessage(Constants.ORDER_LOG_MESSAGE_PAY_SUCCESS);
        storeOrderStatusService.save(storeOrderStatus);
        UserBill userBill = new UserBill();
        userBill.setTitle("购买商品");
        userBill.setUid(currentUser.getUid());
        userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
        userBill.setType(Constants.USER_BILL_TYPE_PAY_MONEY);
        userBill.setNumber(storeOrder.getPayPrice());
        userBill.setLinkId(storeOrder.getId()+"");
        userBill.setBalance(currentUser.getNowMoney());
        userBill.setMark("支付" + storeOrder.getPayPrice() + "元购买商品");
        boolean saveUserbillResult = userBillService.save(userBill);
        if(storeOrder.getUseIntegral() > 0){
            Integer useIntegral = storeOrder.getUseIntegral();
            currentUser.setIntegral(currentUser.getIntegral() - useIntegral);
            userService.updateBase(currentUser);
        }
        userService.userPayCountPlus(currentUser);
        return orderUpdate2PayResult;
    }

    /**
     * 获取总数
     * @param request 请求参数
     * @param status String 状态
     * @author Mr.Zhang
     * @since 2020-06-12
     * @return Integer
     */
    private Integer getCount(StoreOrderSearchRequest request, String status) {
        //总数只计算时间
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        getRequestTimeWhere(queryWrapper, request);
        getStatusWhere(queryWrapper, status);
        return dao.selectCount(queryWrapper);
    }

    /**
     * 获取订单金额
     * @param request 请求参数
     * @author Mr.Zhang
     * @since 2020-06-12
     * @return Integer
     */
    private BigDecimal getAmount(StoreOrderSearchRequest request, String type) {
        QueryWrapper<StoreOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("sum(pay_price) as pay_price");
        if(StringUtils.isNotBlank(type)){
            queryWrapper.eq("pay_type", type);
        }
        getRequestWhere(queryWrapper, request);
        getStatusWhere(queryWrapper, request.getStatus());
        StoreOrder storeOrder = dao.selectOne(queryWrapper);
        if(null == storeOrder){
            return BigDecimal.ZERO;
        }

        return storeOrder.getPayPrice();
    }

    /**
     * 获取request的where条件
     * @param queryWrapper QueryWrapper<StoreOrder> 表达式
     * @param request StoreOrderSearchRequest 请求参数
     * @author Mr.Zhang
     * @since 2020-06-12
     */
    private void getRequestWhere(QueryWrapper<StoreOrder> queryWrapper, StoreOrderSearchRequest request) {
        getRequestOrderIdWhere(queryWrapper, request);
        getRequestUidWhere(queryWrapper, request);
        getRequestTimeWhere(queryWrapper, request);
    }

    private void getIsDelWhere(QueryWrapper<StoreOrder> queryWrapper, Boolean isDel){
        if(null != isDel){
            queryWrapper.eq("is_del", isDel);
        }
    }

    /**
     * 获取request的where条件
     * @param queryWrapper QueryWrapper<StoreOrder> 表达式
     * @param request StoreOrderSearchRequest 请求参数
     * @author Mr.Zhang
     * @since 2020-06-12
     */
    private void getRequestOrderIdWhere(QueryWrapper<StoreOrder> queryWrapper, StoreOrderSearchRequest request) {
        if(StringUtils.isNotBlank(request.getOrderId())){
            queryWrapper.eq("order_id", request.getOrderId());
        }
    }

    /**
     * 获取request的where条件
     * @param queryWrapper QueryWrapper<StoreOrder> 表达式
     * @param request StoreOrderSearchRequest 请求参数
     * @author Mr.Zhang
     * @since 2020-06-12
     */
    private void getRequestUidWhere(QueryWrapper<StoreOrder> queryWrapper, StoreOrderSearchRequest request) {
        if(null != request.getUid() && request.getUid() > 0){
            queryWrapper.eq("uid", request.getUid());
        }
    }
    /**
     * 获取request的where条件
     * @param queryWrapper QueryWrapper<StoreOrder> 表达式
     * @param request StoreOrderSearchRequest 请求参数
     * @author Mr.Zhang
     * @since 2020-06-12
     */
    private void getRequestTimeWhere(QueryWrapper<StoreOrder> queryWrapper, StoreOrderSearchRequest request) {
        if(StringUtils.isNotBlank(request.getDateLimit())){
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            queryWrapper.between("create_time",dateLimitUtilVo.getStartTime(),dateLimitUtilVo.getEndTime());
        }
    }


    /**
     * 根据订单状态获取where条件
     * @param queryWrapper QueryWrapper<StoreOrder> 表达式
     * @param status String 类型
     * @author Mr.Zhang
     * @since 2020-06-12
     */
    private void getStatusWhere(QueryWrapper<StoreOrder> queryWrapper, String status) {
        if(null == status){
            return;
        }
        switch (status){
            case Constants.ORDER_STATUS_UNPAID: //未支付
                queryWrapper.eq("paid", 0);//支付状态
                queryWrapper.eq("status", 0); //订单状态
                queryWrapper.eq("is_del", 0);//删除状态
                break;
            case Constants.ORDER_STATUS_NOT_SHIPPED: //未发货
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 0);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("shipping_type", 1);//配送方式
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_SPIKE: //待收货
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 1);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_BARGAIN: //待评价
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 2);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_COMPLETE: //交易完成
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 3);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_TOBE_WRITTEN_OFF: //待核销
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("status", 0);
                queryWrapper.eq("refund_status", 0);
                queryWrapper.eq("shipping_type", 2);//配送方式
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_REFUNDING: //退款中
                queryWrapper.eq("paid", 1);
                queryWrapper.in("refund_status", 1,3);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_REFUNDED: //已退款
                queryWrapper.eq("paid", 1);
                queryWrapper.eq("refund_status", 2);
                queryWrapper.eq("is_del", 0);
                break;
            case Constants.ORDER_STATUS_DELETED: //已删除
                queryWrapper.eq("is_del", 1);
                break;
            default:
                break;
        }
        queryWrapper.eq("is_system_del", 0);
    }

    /**
     * 获取订单状态
     * @param storeOrder StoreOrder 订单信息
     * @author Mr.Zhang
     * @since 2020-06-12
     */
    public Map<String, String> getStatus(StoreOrder storeOrder) {
        Map<String, String> map = new HashMap<>();
        map.put("key", "");
        map.put("value", "");
        if(null == storeOrder){
            return map;
        }
        // 未支付
        if(!storeOrder.getPaid()
                && storeOrder.getStatus() == 0
                && storeOrder.getRefundStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_UNPAID);
            map.put("value", Constants.ORDER_STATUS_STR_UNPAID);
            return map;
        }
        // 未发货
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 0
                && storeOrder.getRefundStatus() == 0
                && storeOrder.getShippingType() == 1
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_NOT_SHIPPED);
            map.put("value", Constants.ORDER_STATUS_STR_NOT_SHIPPED);
            return map;
        }
        // 待收货
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 1
                && storeOrder.getRefundStatus() == 0
                && storeOrder.getShippingType() == 1
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_SPIKE);
            map.put("value", Constants.ORDER_STATUS_STR_SPIKE);
            return map;
        }
        // 待评价
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 2
                && storeOrder.getRefundStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_BARGAIN);
            map.put("value", Constants.ORDER_STATUS_STR_BARGAIN);
            return map;
        }
        // 交易完成
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 3
                && storeOrder.getRefundStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_COMPLETE);
            map.put("value", Constants.ORDER_STATUS_STR_COMPLETE);
            return map;
        }
        // 待核销
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 0
                && storeOrder.getRefundStatus() == 0
                && storeOrder.getShippingType() == 2
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_TOBE_WRITTEN_OFF);
            map.put("value", Constants.ORDER_STATUS_STR_TOBE_WRITTEN_OFF);
            return map;
        }

        //申请退款
        if(storeOrder.getPaid()
                && storeOrder.getRefundStatus() == 1
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_APPLY_REFUNDING);
            map.put("value", Constants.ORDER_STATUS_STR_APPLY_REFUNDING);
            return map;
        }

        //退款中
        if(storeOrder.getPaid()
                && storeOrder.getRefundStatus() == 3
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_REFUNDING);
            map.put("value", Constants.ORDER_STATUS_STR_REFUNDING);
            return map;
        }

        //已退款
        if(storeOrder.getPaid()
                && storeOrder.getRefundStatus() == 2
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_REFUNDED);
            map.put("value", Constants.ORDER_STATUS_STR_REFUNDED);
        }

//        //未发货
//        if(storeOrder.getPaid()
//                && storeOrder.getStatus() == 0
//                && !storeOrder.getIsDel()
//                && !storeOrder.getIsSystemDel()){
//            map.put("key", Constants.ORDER_STATUS_NOT_SHIPPED);
//            map.put("value", Constants.ORDER_STATUS_STR_NOT_SHIPPED);
//        }

//        //待收货
//        if(storeOrder.getPaid()
//                && storeOrder.getStatus() == 1
//                && !storeOrder.getIsDel()
//                && !storeOrder.getIsSystemDel()){
//            map.put("key", Constants.ORDER_STATUS_SPIKE);
//            map.put("value", Constants.ORDER_STATUS_STR_SPIKE);
//        }

//        //用户已收货
//        if(storeOrder.getPaid()
//                && storeOrder.getStatus() == 2
//                && !storeOrder.getIsDel()
//                && !storeOrder.getIsSystemDel()){
//            map.put("key", Constants.ORDER_STATUS_COMPLETE);
//            map.put("value", Constants.ORDER_STATUS_STR_TAKE);
//        }


        //已删除
        if(storeOrder.getIsDel() || storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_DELETED);
            map.put("value", Constants.ORDER_STATUS_STR_DELETED);
        }

        return map;
    }
    /**
     * 获取支付文字
     * @param payType String 支付方式
     * @author Mr.Zhang
     * @since 2020-06-12
     */
    private String getPayType(String payType) {
        switch (payType){
            case Constants.PAY_TYPE_WE_CHAT:
                return Constants.PAY_TYPE_STR_WE_CHAT;
            case Constants.PAY_TYPE_YUE:
                return Constants.PAY_TYPE_STR_YUE;
            case Constants.PAY_TYPE_OFFLINE:
                return Constants.PAY_TYPE_STR_OFFLINE;
            case Constants.PAY_TYPE_ALI_PAY:
                return Constants.PAY_TYPE_STR_ALI_PAY;
            default:
                return Constants.PAY_TYPE_STR_OTHER;
        }
    }

}

