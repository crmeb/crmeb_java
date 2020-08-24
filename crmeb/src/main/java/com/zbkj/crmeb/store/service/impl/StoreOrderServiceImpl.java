package com.zbkj.crmeb.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.utils.ValidateFormUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.express.model.Express;
import com.zbkj.crmeb.express.service.ExpressService;
import com.zbkj.crmeb.express.service.LogisticService;
import com.zbkj.crmeb.express.vo.LogisticsResultVo;
import com.zbkj.crmeb.finance.request.FundsMonitorSearchRequest;
import com.zbkj.crmeb.front.vo.OrderAgainVo;
import com.zbkj.crmeb.payment.service.OrderPayService;
import com.zbkj.crmeb.store.dao.StoreOrderDao;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.model.StoreOrderStatus;
import com.zbkj.crmeb.store.model.StoreProduct;
import com.zbkj.crmeb.store.model.StoreProductAttrValue;
import com.zbkj.crmeb.store.request.StoreOrderRefundRequest;
import com.zbkj.crmeb.store.request.StoreOrderSearchRequest;
import com.zbkj.crmeb.store.request.StoreOrderSendRequest;
import com.zbkj.crmeb.store.response.*;
import com.zbkj.crmeb.store.service.*;
import com.zbkj.crmeb.store.utilService.OrderUtils;
import com.zbkj.crmeb.store.vo.StoreOrderInfoVo;
import com.zbkj.crmeb.system.model.SystemStore;
import com.zbkj.crmeb.system.model.SystemStoreStaff;
import com.zbkj.crmeb.system.request.SystemWriteOffOrderSearchRequest;
import com.zbkj.crmeb.system.response.StoreOrderItemResponse;
import com.zbkj.crmeb.system.response.SystemWriteOffOrderResponse;
import com.zbkj.crmeb.system.service.SystemStoreService;
import com.zbkj.crmeb.system.service.SystemStoreStaffService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserBill;
import com.zbkj.crmeb.user.request.UserOperateFundsRequest;
import com.zbkj.crmeb.user.service.UserBillService;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import com.zbkj.crmeb.wechat.service.WeChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author Mr.Zhang edited by stivepeim
* @description StoreOrderServiceImpl 接口实现
* @date 2020-05-28
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

    @Lazy
    @Autowired
    private WeChatService weChatService;

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
    private RedisUtil redisUtil;

    @Autowired
    private OrderPayService orderPayService;



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
        statusApiByWhere(lqw,storeOrder.getStatus());
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
        // todo 营销活动二期处理

        // 检测订单是否有效
        StoreOrder storeOrderPram = new StoreOrder();
        storeOrderPram.setUnique(orderUnique);
        storeOrderPram.setUid(userId);
        storeOrderPram.setIsDel(false);
        StoreOrder existOrder = getByEntityOne(storeOrderPram);
        if(null == existOrder) throw new CrmebException("订单不存在");
        OrderAgainVo orderAgainVo = orderUtils.tidyOrder(existOrder, true, false);
        Long cacheKey = DateUtil.getTime()+userId;
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
            StoreProductAttrValue existSPAttrValue = storeProductAttrValueService.getByEntity(apAttrValuePram);
            if(null == existSPAttrValue) throw new CrmebException("请选择有效的商品属性");
            if(existSPAttrValue.getStock() < cartNum) throw new CrmebException("该商品库存不足");
            // 添加有效商品至缓存 缓存为购物车对象
            if(isNew){
                StoreCartResponse storeCartResponse = new StoreCartResponse();
                cacheIdsResult.add(cacheKey+"");
                storeCartResponse.setId(cacheKey);
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
        orderUtils.setCacheOrderAgain(cacheKey+"", orderAgainCache);

        return cacheIdsResult;
//        else{ // else 中的逻辑暂时未使用到
//         StoreCart storeCartNewPram = new StoreCart();
//         storeCartNewPram.setUid(userId);
//         storeCartNewPram.setProductId(productId);
//         storeCartNewPram.setCartNum(cartNum);
//         storeCartNewPram.setProductAttrUnique(productAttrUnique);
//         storeCartNewPram.setIsNew(isNew);
//         storeCartNewPram.setType(type);
//         storeCartNewPram.setCombinationId(combinationId);
//         storeCartNewPram.setCreateTime(DateUtil.nowDateTime());
//         storeCartNewPram.setBargainId(bargainId);
//         storeCartNewPram.setSeckillId(skillId);
//         saveCate(storeCartNewPram);
//         return storeCartNewPram;
//        }
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
        if(currentUser.getNowMoney().compareTo(storeOrder.getPayPrice()) <= 0){
            throw new CrmebException("余额不足");
        }
        BigDecimal priceSubtract = currentUser.getNowMoney().subtract(storeOrder.getPayPrice());
        boolean updateUserResult = userService.updateNowMoney(currentUser.getUid(),priceSubtract);
        UserBill userBill = new UserBill();
        userBill.setTitle("购买商品");
        userBill.setUid(currentUser.getUid());
        userBill.setCategory("nowMoney");
        userBill.setType(Constants.USER_BILL_TYPE_PAY_PRODUCT);
        userBill.setNumber(storeOrder.getPayPrice());
        userBill.setLinkId(storeOrder.getId()+"");
        userBill.setBalance(currentUser.getNowMoney());
        userBill.setMark("余额支付" + storeOrder.getPayPrice() + "元购买商品");
        boolean saveUserBillResult = userBillService.save(userBill);
        boolean paySuccessResult = paySuccess(storeOrder,currentUser,formId);
        return updateUserResult && saveUserBillResult && paySuccessResult;
//        return updateUserResult;
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
        String where = " is_del = 0";
//        String where = " is_del = 0 and paid = 1";
        //时间
        if(!StringUtils.isBlank(request.getData())){
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getData());
            where += " and (create_time between '" + dateLimit.getStartTime() + "' and '" + dateLimit.getEndTime() + "' )";
        }

        if(!StringUtils.isBlank(request.getKeywords())){
            where += " and (real_name like '% "+ request.getKeywords() +" %' or user_phone = '"+ request.getKeywords() +"' or order_id = '" + request.getKeywords() + "' or id = '" + request.getKeywords() + "' )";
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
        lambdaQueryWrapper.eq(StoreOrder::getRefundStatus, 0);
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
        //店员id
        List<Integer> clerkIdList = storeOrderList.stream().map(StoreOrder::getClerkId).distinct().collect(Collectors.toList());

        //订单id集合
        List<Integer> orderIdList = storeOrderList.stream().map(StoreOrder::getId).distinct().collect(Collectors.toList());

        //获取门店map
        HashMap<Integer, SystemStore> systemStoreList = systemStoreService.getMapInId(storeIdList);
        //获取店员map
        HashMap<Integer, SystemStoreStaff> systemStoreStaffList = systemStoreStaffService.getMapInId(clerkIdList);
        //获取订单详情map
        HashMap<Integer, List<StoreOrderInfoVo>> orderInfoList = StoreOrderInfoService.getMapInId(orderIdList);

        //根据用户获取推广人信息
        List<Integer> userIdList = storeOrderList.stream().map(StoreOrder::getUid).distinct().collect(Collectors.toList());
        //订单用户信息
        HashMap<Integer, User> userList = userService.getMapListInUid(userIdList);

        //推广信息
        HashMap<Integer, User> mapListInUid = new HashMap<>();
        if(userIdList.size() > 0){
            //推广人信息
            List<Integer> userServiceSpreadPeopleIdList = userService.getSpreadPeopleIdList(userIdList);
            if(userServiceSpreadPeopleIdList.size() > 0){
                mapListInUid = userService.getMapListInUid(userServiceSpreadPeopleIdList);
            }
        }



        for (StoreOrder storeOrder : storeOrderList) {
            StoreOrderItemResponse storeOrderItemResponse = new StoreOrderItemResponse();
            BeanUtils.copyProperties(storeOrder, storeOrderItemResponse);
            String storeName = "";
            if(systemStoreList.containsKey(storeOrder.getStoreId())){
                storeName = systemStoreList.get(storeOrder.getStoreId()).getName();
            }
            storeOrderItemResponse.setStoreName(storeName);

            String clerkName = "";
            if(systemStoreStaffList.containsKey(storeOrder.getClerkId())){
                clerkName = systemStoreStaffList.get(storeOrder.getClerkId()).getStaffName();
            }
            storeOrderItemResponse.setProductList(orderInfoList.get(storeOrder.getId()));

            //订单状态
            storeOrderItemResponse.setStatusStr(getStatus(storeOrder));
            //支付方式
            storeOrderItemResponse.setPayTypeStr(getPayType(storeOrder.getPayType()));

            //推广人信息
            if(!userList.isEmpty() && userList.containsKey(storeOrder.getUid()) && !mapListInUid.isEmpty()){
                if(userList.containsKey(storeOrder.getUid()) && mapListInUid.containsKey(userList.get(storeOrder.getUid()).getUid())){
                    storeOrderItemResponse.getSpreadInfo().setId(mapListInUid.get(userList.get(storeOrder.getUid()).getUid()).getUid());
                    storeOrderItemResponse.getSpreadInfo().setName(mapListInUid.get(userList.get(storeOrder.getUid()).getUid()).getNickname());
                }
            }

            storeOrderItemResponse.setClerkName(clerkName);
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
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public boolean refund(StoreOrderRefundRequest request) {
        try{
            StoreOrder storeOrder = getById(request.getOrderId());
            if(null == storeOrder){throw new CrmebException("未查到订单");}
            if(!storeOrder.getPaid()){throw new CrmebException("未支付无法退款");}
            if(storeOrder.getRefundPrice().add(request.getAmount()).compareTo(storeOrder.getPayPrice()) > 0){throw new CrmebException("退款金额大于支付金额，请修改退款金额");}


            //用户
            User user = userService.getById(storeOrder.getUid());

            //退款
            switch (storeOrder.getPayType()){
                case Constants.PAY_TYPE_WE_CHAT:
                    storeOrderRefundService.refund(request, storeOrder);
                    break;
                case Constants.PAY_TYPE_YUE:
                    UserOperateFundsRequest userOperateFundsRequest = new UserOperateFundsRequest();
                    userOperateFundsRequest.setUid(storeOrder.getUid());
                    userOperateFundsRequest.setValue(request.getAmount());
                    userOperateFundsRequest.setFoundsCategory(Constants.USER_BILL_CATEGORY_MONEY);
                    userOperateFundsRequest.setFoundsType(Constants.ORDER_STATUS_REFUNDED);
                    userOperateFundsRequest.setType(1);
                    userOperateFundsRequest.setTitle(Constants.ORDER_STATUS_STR_REFUNDED);
                    boolean addMoney = userService.updateFounds(userOperateFundsRequest, false); //更新余额
                    if(!addMoney){throw new CrmebException("余额退款失败");}

                    //新增日志
                    boolean addBill = userBillService.saveRefundBill(request, user);
                    if(!addBill){throw new CrmebException("余额退款失败");}

                    break;
            }

            //修改订单退款状态
            if(request.getType() == 1){
                storeOrder.setRefundStatus(2);
            }else if(request.getType() == 2){
                storeOrder.setRefundStatus(0);
            }else{
                throw new CrmebException("选择退款状态错误");
            }
            storeOrder.setRefundPrice(request.getAmount());
            boolean updateOrder = updateById(storeOrder);
            if(!updateOrder){
                storeOrderStatusService.saveRefund(request.getOrderId(), request.getAmount(), "失败");
                throw new CrmebException("订单更新失败");
            }
            //TODO 发送模板消息, 异步


            //退款成功
            storeOrderStatusService.saveRefund(request.getOrderId(), request.getAmount(), null);

            //佣金
            subtractBill(request, Constants.USER_BILL_CATEGORY_MONEY, Constants.USER_BILL_TYPE_BROKERAGE, Constants.USER_BILL_CATEGORY_BROKERAGE_PRICE);

            //积分
            subtractBill(request, Constants.USER_BILL_CATEGORY_INTEGRAL, Constants.USER_BILL_TYPE_GAIN, Constants.USER_BILL_CATEGORY_INTEGRAL);

            return true;
        }catch (Exception e){
            throw new CrmebException(e.getMessage());
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
        StoreOrderInfoResponse storeOrderInfoResponse = new StoreOrderInfoResponse();
        BeanUtils.copyProperties(storeOrder, storeOrderInfoResponse);
        storeOrderInfoResponse.setPayTypeStr(getPayType(storeOrder.getPayType()));
        storeOrderInfoResponse.setStatusStr(getStatus(storeOrder));

        //用户信息
        User user = userService.getById(storeOrder.getUid());
        User spread = userService.getById(user.getSpreadUid());
        storeOrderInfoResponse.getSpreadInfo().setId(spread.getUid());
        storeOrderInfoResponse.getSpreadInfo().setName(spread.getNickname());
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
    @Transactional(rollbackFor = {RuntimeException.class, Error.class, CrmebException.class})
    public boolean send(StoreOrderSendRequest request) {
        try{

            //订单信息
            StoreOrder storeOrder = getInfoException(request.getId());

            switch (request.getType()){
                case "1":
                    express(request, storeOrder);
                    break;
                case "2":
                    delivery(request, storeOrder);
                    break;
                case "3":
                    virtual(request, storeOrder);
                    break;
                default:
                    throw new CrmebException("类型错误");
            }

            //短信发送

            return true;
        }catch (Exception e){
            //发货失败！
            throw new CrmebException(e.getMessage());
        }
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
    public boolean refundRefuse(Integer id, String reason) {
        StoreOrder storeOrder = getInfoException(id);
        storeOrder.setRefundReason(reason);
        storeOrder.setRefundStatus(0);
        storeOrder.setStatus(1);
        updateById(storeOrder);

        storeOrderStatusService.createLog(storeOrder.getId(), Constants.ORDER_LOG_REFUND_REFUSE, Constants.ORDER_LOG_MESSAGE_REFUND_REFUSE.replace("{reason}", reason));

        //TODO 模板消息通知

        return true;
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
                    .or().eq(StoreOrder::getUnique,storeOrder.getUnique());
        }
        if(null != storeOrder.getUid()){
            lambdaQueryWrapper.eq(StoreOrder::getUid, storeOrder.getUid());
        }
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

        return logisticService.info(info.getDeliveryId(), null);
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
        lqw.ge(StoreOrder::getPaid, 1);
        lqw.ge(StoreOrder::getRefundStatus, 0);
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
        statusApiByWhere(lqw,status);
        lqw.eq(StoreOrder::getIsDel, false);
        lqw.eq(StoreOrder::getUid,userId);
        return dao.selectList(lqw);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////// 以下为自定义方法

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
                    price = user.getIntegral();
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
        try{
            //快递信息
            Express express = expressService.info(Integer.parseInt(request.getExpressId()));
            storeOrder.setDeliveryName(express.getName());
            storeOrder.setDeliveryId(request.getExpressCode());
            storeOrder.setStatus(1);
            storeOrder.setDeliveryType("express");
            updateById(storeOrder);

            //订单记录增加
            String message = Constants.ORDER_LOG_MESSAGE_EXPRESS.replace("{deliveryName}", express.getName()).replace("{deliveryCode}", request.getExpressCode());
            storeOrderStatusService.createLog(request.getId(), Constants.ORDER_LOG_EXPRESS, message);

            //微信模板消息发送
            HashMap<String, String> map = new HashMap<>();
            map.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "订单发货提醒");
            map.put("keyword1", storeOrder.getOrderId());
            map.put("keyword2", storeOrder.getDeliveryName());
            map.put("keyword3", storeOrder.getDeliveryId());
            map.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎再次购买！");

            templateMessageService.push(Constants.WE_CHAT_TEMP_KEY_EXPRESS, map, storeOrder.getUid(), Constants.PAY_TYPE_WE_CHAT_FROM_PUBLIC);

        }catch (Exception e){
            throw new CrmebException("快递发货失败！" + e.getMessage());
        }

    }

    /** 送货上门
     * @param request StoreOrderSendRequest 发货参数
     * @param storeOrder StoreOrder 订单信息
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    private void delivery(StoreOrderSendRequest request, StoreOrder storeOrder) {
        ValidateFormUtil.isPhone(request.getExpressCode(), "收货人联系方式");

        try{
            //送货信息
            storeOrder.setDeliveryName(request.getExpressId());
            storeOrder.setDeliveryId(request.getExpressCode());
            storeOrder.setStatus(1);
            storeOrder.setDeliveryType("send");
            updateById(storeOrder);

            //获取购买商品名称
            List<Integer> orderIdList = new ArrayList<>();
            orderIdList.add(storeOrder.getId());

            HashMap<Integer, List<StoreOrderInfoVo>> orderInfoMap = StoreOrderInfoService.getMapInId(orderIdList);
            if(orderInfoMap.isEmpty() || !orderInfoMap.containsKey(storeOrder.getId())){
                throw new CrmebException("没有找到购买的商品信息");
            }
            List<String> productNameList = new ArrayList<>();
            for (StoreOrderInfoVo storeOrderInfoVo : orderInfoMap.get(storeOrder.getId())) {
//                productNameList.add(storeOrderInfoVo.getInfo().getJSONObject("productInfo").getString("store_name"));
                productNameList.add(storeOrderInfoVo.getInfo().getProductInfo().getStoreName());
            }


            //订单记录增加
            String message = Constants.ORDER_LOG_MESSAGE_DELIVERY.replace("{deliveryName}", request.getExpressId()).replace("{deliveryCode}", request.getExpressCode());
            storeOrderStatusService.createLog(request.getId(), Constants.ORDER_LOG_DELIVERY, message);

            //微信模板消息发送
            HashMap<String, String> map = new HashMap<>();
            map.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "订单发货提醒");
            map.put("keyword1", StringUtils.join(productNameList, "|"));
            map.put("keyword2", DateUtil.dateToStr(storeOrder.getCreateTime(), Constants.DATE_FORMAT));
            map.put("keyword3", storeOrder.getUserAddress());
            map.put("keyword4", request.getExpressId());
            map.put("keyword5", request.getExpressCode());
            map.put(Constants.WE_CHAT_TEMP_KEY_END, "欢迎再次购买！");

            templateMessageService.push(Constants.WE_CHAT_TEMP_KEY_DELIVERY, map, storeOrder.getUid() , Constants.PAY_TYPE_WE_CHAT_FROM_PUBLIC);

        }catch (Exception e){
            throw new CrmebException("送货上门发货失败！");
        }
    }
    /** 虚拟
     * @param request StoreOrderSendRequest 发货参数
     * @param storeOrder StoreOrder 订单信息
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    private void virtual(StoreOrderSendRequest request, StoreOrder storeOrder) {
        try{
            //快递信息
            storeOrder.setDeliveryType("fictitious");
            storeOrder.setStatus(1);
            updateById(storeOrder);

            //订单记录增加
            storeOrderStatusService.createLog(request.getId(), Constants.ORDER_LOG_DELIVERY_VI, Constants.ORDER_LOG_DELIVERY_VI);

            //微信模板消息发送
        }catch (Exception e){
            e.printStackTrace();
            throw new CrmebException("虚拟物品发货失败");
        }


    }
    /**
     * 支付成功操作
     * @param storeOrder 当前支付订单
     * @param currentUser 当前用户
     * @param formId 平台
     * @return 成功处理结果
     */
    public boolean paySuccess(StoreOrder storeOrder,User currentUser, String formId){
        // todo 秒杀团购拼团
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
            BigDecimal useIntegral = BigDecimal.valueOf(storeOrder.getUseIntegral(),0);
            currentUser.setIntegral(currentUser.getIntegral().subtract(useIntegral));
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
                break;
            case Constants.ORDER_STATUS_H5_SPIKE: // 待收货
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.eq(StoreOrder::getStatus, 1);
                queryWrapper.eq(StoreOrder::getRefundStatus, 0);
                break;
            case Constants.ORDER_STATUS_H5_BARGAIN: //  待评价
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
                queryWrapper.eq(StoreOrder::getRefundStatus, 1);
                break;
            case Constants.ORDER_STATUS_H5_REFUNDED: // 已退款
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.eq(StoreOrder::getStatus, 2);
                break;
            case Constants.ORDER_STATUS_H5_REFUND: // 退款
                queryWrapper.eq(StoreOrder::getPaid, true);
                queryWrapper.in(StoreOrder::getRefundStatus, "1,2"); //大于0
                break;
        }
        queryWrapper.eq(StoreOrder::getIsDel, false);
        queryWrapper.eq(StoreOrder::getIsSystemDel, false);
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
        Integer orderStatus = null; //订单状态
        Integer paidStatus = null; //支付状态
        Integer refundStatus = null; //退款状态
        Integer deleteStatus = null; //删除状态
        Integer systemDeleteStatus = null; //系统删除
        Integer shippingType = null; //配送方式
        switch (status){
            case Constants.ORDER_STATUS_UNPAID: //未支付
                paidStatus = 0; //支付状态
                orderStatus = 0; //订单状态
                refundStatus = 0; //退款状态
                deleteStatus = 0; //删除状态
                systemDeleteStatus = 0; //系统删除
                break;
            case Constants.ORDER_STATUS_NOT_SHIPPED: //未发货
                paidStatus = 1; //支付状态
                orderStatus = 0; //订单状态
                refundStatus = 0; //退款状态
                shippingType = 1; //配送方式
                deleteStatus = 0; //删除状态
                systemDeleteStatus = 0; //系统删除
                break;
            case Constants.ORDER_STATUS_SPIKE: //待收货
                paidStatus = 1; //支付状态
                orderStatus = 1; //订单状态
                refundStatus = 0; //退款状态
                deleteStatus = 0; //删除状态
                systemDeleteStatus = 0; //系统删除
                break;
            case Constants.ORDER_STATUS_BARGAIN: //待评价
                paidStatus = 1; //支付状态
                orderStatus = 2; //订单状态
                refundStatus = 0; //退款状态
                deleteStatus = 0; //删除状态
                systemDeleteStatus = 0; //系统删除
                break;
            case Constants.ORDER_STATUS_COMPLETE: //交易完成
                paidStatus = 1; //支付状态
                orderStatus = 3; //订单状态
                refundStatus = 0; //退款状态
                deleteStatus = 0; //删除状态
                systemDeleteStatus = 0; //系统删除
                break;
            case Constants.ORDER_STATUS_TOBE_WRITTEN_OFF: //待核销
                paidStatus = 1; //支付状态
                orderStatus = 0; //订单状态
                refundStatus = 0; //退款状态
                shippingType = 2; //配送方式
                deleteStatus = 0; //删除状态
                systemDeleteStatus = 0; //系统删除
                break;
            case Constants.ORDER_STATUS_REFUNDING: //退款中
                paidStatus = 1; //支付状态
                refundStatus = 1; //退款状态
                deleteStatus = 0; //删除状态
                systemDeleteStatus = 0; //系统删除
                break;
            case Constants.ORDER_STATUS_REFUNDED: //已退款
                paidStatus = 1; //支付状态
                refundStatus = 2; //退款状态
                deleteStatus = 0; //删除状态
                systemDeleteStatus = 0; //系统删除
                break;
            case Constants.ORDER_STATUS_DELETED: //已删除
                deleteStatus = 1; //删除状态
                systemDeleteStatus = 1; //系统删除,  未付款超时自动删除
                break;
            default:
                break;
        }
        if(paidStatus != null){
            queryWrapper.eq("paid", paidStatus);
        }

        if(orderStatus != null){
            queryWrapper.eq("status", orderStatus);
        }

        if(refundStatus != null){
            queryWrapper.eq("refund_status", refundStatus);
        }

        if(shippingType != null){
            queryWrapper.eq("shipping_type", shippingType);
        }

        if(deleteStatus != null){
            if(deleteStatus == 1 && systemDeleteStatus == 1){
                queryWrapper.and(i -> i.or().eq("is_del", 1).or().eq("is_system_del", 1));
            }else{
                queryWrapper.eq("is_del", deleteStatus);
            }
        }




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

        if(!storeOrder.getPaid()
                && storeOrder.getStatus() == 0
                && storeOrder.getRefundStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_UNPAID);
            map.put("value", Constants.ORDER_STATUS_STR_UNPAID);
            return map;
        }

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

        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 1
                && storeOrder.getRefundStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_SPIKE);
            map.put("value", Constants.ORDER_STATUS_STR_SPIKE);
            return map;
        }

        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 1
                && storeOrder.getRefundStatus() == 2
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_BARGAIN);
            map.put("value", Constants.ORDER_STATUS_STR_BARGAIN);
            return map;
        }

        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 3
                && storeOrder.getRefundStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_COMPLETE);
            map.put("value", Constants.ORDER_STATUS_STR_COMPLETE);
            return map;
        }

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

        //此处前端需要判断
        if(storeOrder.getPaid()
                && storeOrder.getRefundStatus() == 1
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_REFUNDING);
            map.put("value", Constants.ORDER_STATUS_STR_REFUNDING);
            return map;
        }

        if(storeOrder.getPaid()
                && storeOrder.getRefundStatus() == 2
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_REFUNDED);
            map.put("value", Constants.ORDER_STATUS_STR_REFUNDED);
        }

        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 0
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_NOT_SHIPPED);
            map.put("value", Constants.ORDER_STATUS_STR_NOT_SHIPPED);
        }

        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 1
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_SPIKE);
            map.put("value", Constants.ORDER_STATUS_STR_SPIKE);
        }
        if(storeOrder.getPaid()
                && storeOrder.getStatus() == 2
                && !storeOrder.getIsDel()
                && !storeOrder.getIsSystemDel()){
            map.put("key", Constants.ORDER_STATUS_COMPLETE);
            map.put("value", Constants.ORDER_STATUS_STR_TAKE);
        }



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

