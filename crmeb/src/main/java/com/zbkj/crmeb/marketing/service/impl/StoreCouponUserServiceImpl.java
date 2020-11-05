package com.zbkj.crmeb.marketing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.zbkj.crmeb.front.request.UserCouponReceiveRequest;
import com.zbkj.crmeb.marketing.dao.StoreCouponUserDao;
import com.zbkj.crmeb.marketing.model.StoreCoupon;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.request.StoreCouponUserRequest;
import com.zbkj.crmeb.marketing.request.StoreCouponUserSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreCouponUserOrder;
import com.zbkj.crmeb.marketing.response.StoreCouponUserResponse;
import com.zbkj.crmeb.marketing.service.StoreCouponService;
import com.zbkj.crmeb.marketing.service.StoreCouponUserService;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.response.StoreCartResponse;
import com.zbkj.crmeb.store.service.StoreCartService;
import com.zbkj.crmeb.store.service.StoreProductService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StoreCouponUserService 实现类
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
public class StoreCouponUserServiceImpl extends ServiceImpl<StoreCouponUserDao, StoreCouponUser> implements StoreCouponUserService {

    @Resource
    private StoreCouponUserDao dao;

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreCartService storeCartService;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-18
    * @return List<StoreCouponUser>
    */
    @Override
    public PageInfo<StoreCouponUserResponse> getList(StoreCouponUserSearchRequest request, PageParamRequest pageParamRequest) {
        Page<StoreCouponUser> storeCouponUserPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreCouponUser 类的多条件查询
        LambdaQueryWrapper<StoreCouponUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(request.getName())){
            lambdaQueryWrapper.like(StoreCouponUser::getName, request.getName());
        }

        if(request.getUid() !=null && request.getUid() > 0){
            lambdaQueryWrapper.eq(StoreCouponUser::getUid, request.getUid());
        }

        if(request.getStatus() !=null){
            lambdaQueryWrapper.eq(StoreCouponUser::getStatus, request.getStatus());
        }

        if(request.getCouponId() != null){
            lambdaQueryWrapper.eq(StoreCouponUser::getCouponId, request.getCouponId());
        }

//        if(request.getMinPrice() != null){
//            lambdaQueryWrapper.le(StoreCouponUser::getMinPrice, request.getMinPrice());
//        }


        ArrayList<StoreCouponUserResponse> storeCouponUserResponseList = new ArrayList<>();

        List<StoreCouponUser> storeCouponUserList = dao.selectList(lambdaQueryWrapper);
        if(storeCouponUserList.size() < 1){
            return new PageInfo<>();
        }

        List<Integer> uidList = storeCouponUserList.stream().map(StoreCouponUser::getUid).distinct().collect(Collectors.toList());
        HashMap<Integer, User> userList = userService.getMapListInUid(uidList);

        for (StoreCouponUser storeCouponUser : storeCouponUserList) {
            StoreCouponUserResponse storeCouponUserResponse = new StoreCouponUserResponse();
            BeanUtils.copyProperties(storeCouponUser, storeCouponUserResponse);
            if(userList.containsKey(storeCouponUser.getUid())){
                storeCouponUserResponse.setNickname(userList.get(storeCouponUser.getUid()).getNickname());
                storeCouponUserResponse.setAvatar(userList.get(storeCouponUser.getUid()).getAvatar());
            }
            storeCouponUserResponseList.add(storeCouponUserResponse);
        }
        return CommonPage.copyPageInfo(storeCouponUserPage, storeCouponUserResponseList);
    }

    /**
     * 基本条件查询
     * @param storeCouponUser 基本参数
     * @return 基本条件查询结果
     */
    @Override
    public List<StoreCouponUser> getList(StoreCouponUser storeCouponUser) {
        LambdaQueryWrapper<StoreCouponUser> lwq = new LambdaQueryWrapper<>();
        lwq.setEntity(storeCouponUser);
        return dao.selectList(lwq);
    }

    /**
     * 领券/批量领券
     * @param request 新增参数
     * @author Mr.Zhang
     * @since 2020-05-18
     * @return boolean
     */
    @Override
    public boolean receive(StoreCouponUserRequest request) {
        //获取优惠券信息
        StoreCoupon storeCoupon = storeCouponService.getInfoException(request.getCouponId());

        List<Integer> uidList = CrmebUtil.stringToArray(request.getUid());
        if(uidList.size() < 1){
            throw new CrmebException("请选择用户！");
        }

        //看是否有剩余数量
        if(storeCoupon.getIsLimited()){
            //看剩余的是否够给当前用户
            if(storeCoupon.getLastTotal() < uidList.size()){
                throw new CrmebException("当前剩余数量不够领取！");
            }
        }

        //过滤掉已经领取过的用户
        filterReceiveUserInUid(storeCoupon.getId(), uidList);
        if(uidList.size() < 1){
            //都已经领取过了
            throw new CrmebException("当前用户已经领取过此优惠券了！");

        }

        //是否有固定的使用时间
        if(!storeCoupon.getIsFixedTime()){
            String endTime = DateUtil.addDay(DateUtil.nowDate(Constants.DATE_FORMAT_START), storeCoupon.getDay() + 1, Constants.DATE_FORMAT_START);
            storeCoupon.setUseEndTime(DateUtil.strToDate(endTime, Constants.DATE_FORMAT_START));
            storeCoupon.setUseStartTime(DateUtil.nowDateTimeReturnDate(Constants.DATE_FORMAT_DATE));
        }

        ArrayList<StoreCouponUser> storeCouponUserList = new ArrayList<>();

        for (Integer uid : uidList) {
            StoreCouponUser storeCouponUser = new StoreCouponUser();
            storeCouponUser.setCouponId(storeCoupon.getId());
            storeCouponUser.setUid(uid);
            storeCouponUser.setName(storeCoupon.getName());
            storeCouponUser.setMoney(storeCoupon.getMoney());
            storeCouponUser.setMinPrice(storeCoupon.getMinPrice());
            storeCouponUser.setStartTime(storeCoupon.getUseStartTime());
            storeCouponUser.setEndTime(storeCoupon.getUseEndTime());
            storeCouponUser.setUseType(storeCoupon.getUseType());
            storeCouponUserList.add(storeCouponUser);
        }

        return saveBatch(storeCouponUserList);
    }

    /**
     * 过滤已经领取过此优惠券的用户id
     * @param couponId Integer 优惠券id
     * @param uidList List<Integer> 用户id集合
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    private void filterReceiveUserInUid(Integer couponId, List<Integer> uidList) {
        LambdaQueryWrapper<StoreCouponUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(StoreCouponUser::getCouponId, couponId)
                .in(StoreCouponUser::getUid, uidList)
                .eq(StoreCouponUser::getStatus,0);
        List<StoreCouponUser> storeCouponUserList = dao.selectList(lambdaQueryWrapper);
        if(storeCouponUserList != null){
            List<Integer> receiveUidList = storeCouponUserList.stream().map(StoreCouponUser::getUid).distinct().collect(Collectors.toList());
            uidList.removeAll(receiveUidList);
        }
    }

    /**
     * 过滤已经领取过此优惠券的 优惠券id
     * @param couponIdList List<Integer> 优惠券id
     * @param uid List<Integer> 用户id集合
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    private void filterReceiveUserInCouponId(List<Integer> couponIdList, Integer uid) {
        LambdaQueryWrapper<StoreCouponUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(StoreCouponUser::getCouponId, couponIdList).eq(StoreCouponUser::getUid, uid);
        List<StoreCouponUser> storeCouponUserList = dao.selectList(lambdaQueryWrapper);
        if(storeCouponUserList != null){
            List<Integer> receiveUidList = storeCouponUserList.stream().map(StoreCouponUser::getUid).distinct().collect(Collectors.toList());
            couponIdList.removeAll(receiveUidList);
        }
    }

    /**
     * 使用
     * @param id Integer 领取记录id
     * @author Mr.Zhang
     * @since 2020-05-18
     * @return boolean
     */
    @Override
    public boolean use(Integer id, List<Integer> productIdList, BigDecimal price) {
        StoreCouponUser storeCouponUser = getById(id);
        if(storeCouponUser == null || !storeCouponUser.getUid().equals(userService.getUserIdException())){
            throw new CrmebException("领取记录不存在！");
        }

        if(storeCouponUser.getStatus() == 1){
            throw new CrmebException("此优惠券已使用！");
        }

        if(storeCouponUser.getStatus() == 2){
            throw new CrmebException("此优惠券已失效！");
        }

        //判断是否在使用时间内
        Date date = DateUtil.nowDateTime();
        if(storeCouponUser.getStartTime().compareTo(date) > 0){
            throw new CrmebException("此优惠券还未到达使用时间范围之内！");
        }

        if(date.compareTo(storeCouponUser.getEndTime()) > 0){
            throw new CrmebException("此优惠券已经失效了");
        }

        if(storeCouponUser.getMinPrice().compareTo(price) > 0){
            throw new CrmebException("总金额小于优惠券最小使用金额");
        }

        //检测优惠券信息
        if(storeCouponUser.getUseType() > 1){
            if(productIdList.size() < 1){
                throw new CrmebException("没有找到商品");
            }

            //拿出需要使用优惠券的商品分类集合
            List<Integer> categoryIdList = storeProductService.getSecondaryCategoryByProductId(StringUtils.join(productIdList, ","));

            //设置优惠券所提供的集合
            List<Integer> primaryKeyIdList = CrmebUtil.stringToArray(storeCouponUser.getPrimaryKey());

            //取两个集合的交集，如果是false则证明没有相同的值
            if(storeCouponUser.getUseType() == 2 && !primaryKeyIdList.retainAll(productIdList)){
                throw new CrmebException("此优惠券为商品券，请购买相关商品之后再使用！");
            }

            if(storeCouponUser.getUseType() == 3 && !primaryKeyIdList.retainAll(categoryIdList)){
                throw new CrmebException("此优惠券为分类券，请购买相关分类下的商品之后再使用！");
            }
        }

        storeCouponUser.setStatus(1);
        storeCouponUser.setUseTime(DateUtil.nowDateTime());
        return updateById(storeCouponUser);
    }

    /**
     * 检测优惠券是否可用，计算订单价格时使用
     *
     * @param id            优惠券id
     * @param productIdList 商品id集合
     * @param price         价格
     * @return 可用状态
     */
    @Override
    public boolean canUse(Integer id, List<Integer> productIdList, BigDecimal price) {
        StoreCouponUser storeCouponUser = getById(id);
        if(storeCouponUser == null || !storeCouponUser.getUid().equals(userService.getUserIdException())){
            throw new CrmebException("领取记录不存在！");
        }

        if(storeCouponUser.getStatus() == 1){
            throw new CrmebException("此优惠券已使用！");
        }

        if(storeCouponUser.getStatus() == 2){
            throw new CrmebException("此优惠券已失效！");
        }

        //判断是否在使用时间内
        Date date = DateUtil.nowDateTime();
        if(storeCouponUser.getStartTime().compareTo(date) > 0){
            throw new CrmebException("此优惠券还未到达使用时间范围之内！");
        }

        if(date.compareTo(storeCouponUser.getEndTime()) > 0){
            throw new CrmebException("此优惠券已经失效了");
        }

        if(storeCouponUser.getMinPrice().compareTo(price) > 0){
            throw new CrmebException("总金额小于优惠券最小使用金额");
        }

        //检测优惠券信息
        if(storeCouponUser.getUseType() > 1){
            if(productIdList.size() < 1){
                throw new CrmebException("没有找到商品");
            }

            //拿出需要使用优惠券的商品分类集合
            List<Integer> categoryIdList = storeProductService.getSecondaryCategoryByProductId(StringUtils.join(productIdList, ","));

            //设置优惠券所提供的集合
            List<Integer> primaryKeyIdList = CrmebUtil.stringToArray(storeCouponUser.getPrimaryKey());

            //取两个集合的交集，如果是false则证明没有相同的值
            if(storeCouponUser.getUseType() == 2 && !primaryKeyIdList.retainAll(productIdList)){
                throw new CrmebException("此优惠券为商品券，请购买相关商品之后再使用！");
            }

            if(storeCouponUser.getUseType() == 3 && !primaryKeyIdList.retainAll(categoryIdList)){
                throw new CrmebException("此优惠券为分类券，请购买相关分类下的商品之后再使用！");
            }
        }
        return true;
    }

    /**
     * 用户批量领取优惠券
     * @param request UserCouponReceiveRequest 领取参数
     * @param userId Integer 用户id
     * @param type String 获取方式
     * @author Mr.Zhang
     * @since 2020-05-18
     * @return boolean
     */
    @Override
    public boolean receiveAll(UserCouponReceiveRequest request, Integer userId, String type) {

        //获取优惠券信息
        List<Integer> couponIdList = Arrays.asList(request.getCouponId());
        //过滤掉已经领取过的用户
        filterReceiveUserInCouponId(couponIdList, userId);
        if(couponIdList.size() < 1){
            //都已经领取过了
            throw new CrmebException("当前用户已经领取过此优惠券了！");
        }


        List<StoreCoupon> storeCouponList = storeCouponService.getReceiveListInId(couponIdList);
        if(storeCouponList.size() < 1){
            //没有查到说明，当前没有可以供领取的优惠券
            throw new CrmebException("没有可以发放的优惠券！");
        }


        ArrayList<StoreCouponUser> storeCouponUserList = new ArrayList<>();
        for (StoreCoupon storeCoupon : storeCouponList) {
            storeCouponService.checkException(storeCoupon);
            //如果是按领取时间计算天数
            if(storeCoupon.getIsFixedTime()){
                String endTime = DateUtil.addDay(DateUtil.nowDate(Constants.DATE_FORMAT_DATE), storeCoupon.getDay() + 1, Constants.DATE_FORMAT);
                storeCoupon.setUseEndTime(DateUtil.strToDate(endTime, Constants.DATE_FORMAT));
                storeCoupon.setUseStartTime(DateUtil.nowDateTimeReturnDate(Constants.DATE_FORMAT_DATE));
            }

            StoreCouponUser storeCouponUser = new StoreCouponUser();
            storeCouponUser.setCouponId(storeCoupon.getId());
            storeCouponUser.setUid(userId);
            storeCouponUser.setName(storeCoupon.getName());
            storeCouponUser.setMoney(storeCoupon.getMoney());
            storeCouponUser.setMinPrice(storeCoupon.getMinPrice());
            storeCouponUser.setType(type);
            storeCouponUser.setStartTime(storeCoupon.getUseStartTime());
            storeCouponUser.setEndTime(storeCoupon.getUseEndTime());
            storeCouponUser.setUseType(storeCoupon.getUseType());
            storeCouponUserList.add(storeCouponUser);
        }

        return saveBatch(storeCouponUserList);
    }

    /**
     * 下单之后取消订单回滚优惠券使用
     * @param storeOrder StoreOrder 订单数据
     * @author Mr.Zhang
     * @since 2020-05-18
     * @return boolean
     */
    @Override
    public boolean rollbackByCancelOrder(StoreOrder storeOrder) {
        try{
            LambdaUpdateWrapper<StoreCouponUser> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(StoreCouponUser::getUid, storeOrder.getUid())
                    .eq(StoreCouponUser::getId, storeOrder.getCouponId()) //此处为 coupon_user表的自增id
                    .set(StoreCouponUser::getStatus, 0);
            return update(lambdaUpdateWrapper);
        }catch (Exception e){
            throw new CrmebException("回滚优惠券信息失败");
        }
    }

    /**
     * 用户已领取的优惠券
     * @param userId Integer 用户id
     * @author Mr.Zhang
     * @since 2020-05-18
     * @return boolean
     */
    @Override
    public HashMap<Integer, StoreCouponUser>  getMapByUserId(Integer userId) {
        StoreCouponUser storeCouponUser = new StoreCouponUser();
        storeCouponUser.setUid(userId);
        List<StoreCouponUser> list = getList(storeCouponUser);

        if(list.size() < 1){
            return null;
        }

        HashMap<Integer, StoreCouponUser> map = new HashMap<>();
        for (StoreCouponUser info : list) {
            map.put(info.getCouponId(), info);
        }
        return map;
    }

    /**
     * 根据购物车id获取可用优惠券
     * @param cartIds 购物车id
     * @return 优惠券集合
     */
    @Override
    public List<StoreCouponUserOrder> getListByCartIds(List<Integer> cartIds) {

        List<String> carIdsStr = cartIds.stream().map(e -> e.toString()).collect(Collectors.toList());
        //购物车产品集合
        List<StoreCartResponse> storeCartResponseList =
                storeCartService.getListByUserIdAndCartIds(userService.getUserIdException(), carIdsStr,1);

        //产品id集合
        List<Integer> productIds = storeCartResponseList.stream().map(StoreCartResponse::getProductId).distinct().collect(Collectors.toList());

        //计算购物车价格
        BigDecimal maxPrice = storeCartResponseList.stream().map(StoreCartResponse::getTruePrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        LambdaQueryWrapper<StoreCouponUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Date date = DateUtil.nowDateTime();
        lambdaQueryWrapper.eq(StoreCouponUser::getStatus, 0)
                .le(StoreCouponUser::getMinPrice, maxPrice)
                .lt(StoreCouponUser::getStartTime, date)
                .gt(StoreCouponUser::getEndTime, date);
        getPrimaryKeySql(lambdaQueryWrapper, StringUtils.join(productIds, ","));

        //前端组件统一 转化数据
        ArrayList<StoreCouponUserOrder> storeCouponUserOrderArrayList = new ArrayList<>();
        List<StoreCouponUser> storeCouponUserList = dao.selectList(lambdaQueryWrapper);
        if(storeCouponUserList.size() < 1){
            return storeCouponUserOrderArrayList;
        }

        for (StoreCouponUser storeCouponUser : storeCouponUserList) {
            StoreCouponUserOrder storeCouponUserOrder = new StoreCouponUserOrder();
            BeanUtils.copyProperties(storeCouponUser, storeCouponUserOrder);
            storeCouponUserOrderArrayList.add(storeCouponUserOrder);
        }
        return storeCouponUserOrderArrayList;
    }

    /**
     * H5 优惠券列表
     * @param userId            用户id
     * @param pageParamRequest  分页参数
     * @return  优惠券列表
     */
    @Override
    public List<StoreCouponUserResponse> getListFront(Integer userId, PageParamRequest pageParamRequest) {
        StoreCouponUserSearchRequest request = new StoreCouponUserSearchRequest();
        request.setUid(userId);
        PageInfo<StoreCouponUserResponse> list = getList(request, pageParamRequest);

        if(null == list.getList() || list.getList().size() < 1){
            return null;
        }
        Date date = DateUtil.nowDateTime();
        for (StoreCouponUserResponse storeCouponUserResponse : list.getList()) {
            boolean type = true;
            if(storeCouponUserResponse.getStatus() > 0){
                type = false;
            }

            //判断是否在使用时间内
            if(null != storeCouponUserResponse.getStartTime() && null != storeCouponUserResponse.getEndTime()){
                if(storeCouponUserResponse.getStartTime().compareTo(date) > 0){
                    type = false;
                }

                if(date.compareTo(storeCouponUserResponse.getEndTime()) >= 0){
                    type = false;
                }
            }

            storeCouponUserResponse.setIsValid(type);
        }

        return list.getList();
    }

    private void getPrimaryKeySql(LambdaQueryWrapper<StoreCouponUser> lambdaQueryWrapper, String productIdStr){
        if(StringUtils.isBlank(productIdStr)){
            return;
        }

        List<Integer> categoryIdList = storeProductService.getSecondaryCategoryByProductId(productIdStr);
        lambdaQueryWrapper.and(i -> i.and(
                //通用券  商品券  品类券
                t -> t.eq(StoreCouponUser::getUseType, 1)
                        .or(p -> p.eq(StoreCouponUser::getUseType , 2).apply(CrmebUtil.getFindInSetSql("primary_key", productIdStr)))
                        .or(c -> c.eq(StoreCouponUser::getUseType , 3).apply(CrmebUtil.getFindInSetSql("primary_key", (ArrayList<Integer>) categoryIdList)))
        ));
    }
}

