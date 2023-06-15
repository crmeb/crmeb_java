package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.UserCouponReceiveRequest;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.CouponConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.vo.OrderInfoDetailVo;
import com.zbkj.common.vo.OrderInfoVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.model.coupon.StoreCoupon;
import com.zbkj.common.model.coupon.StoreCouponUser;
import com.zbkj.common.request.StoreCouponUserRequest;
import com.zbkj.common.request.StoreCouponUserSearchRequest;
import com.zbkj.common.response.StoreCouponUserOrder;
import com.zbkj.common.response.StoreCouponUserResponse;
import com.zbkj.common.model.user.User;
import com.zbkj.service.dao.StoreCouponUserDao;
import com.zbkj.service.service.StoreCouponService;
import com.zbkj.service.service.StoreCouponUserService;
import com.zbkj.service.service.StoreProductService;
import com.zbkj.service.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StoreCouponUserService 实现类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
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
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RedisUtil redisUtil;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return List<StoreCouponUser>
    */
    @Override
    public PageInfo<StoreCouponUserResponse> getList(StoreCouponUserSearchRequest request, PageParamRequest pageParamRequest) {
        Page<StoreCouponUser> storeCouponUserPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreCouponUser 类的多条件查询
        LambdaQueryWrapper<StoreCouponUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isBlank(request.getName())) {
            lambdaQueryWrapper.like(StoreCouponUser::getName, request.getName());
        }

        if (request.getUid() !=null && request.getUid() > 0) {
            lambdaQueryWrapper.eq(StoreCouponUser::getUid, request.getUid());
        }

        if (request.getStatus() !=null) {
            lambdaQueryWrapper.eq(StoreCouponUser::getStatus, request.getStatus());
        }

        if (request.getCouponId() != null) {
            lambdaQueryWrapper.eq(StoreCouponUser::getCouponId, request.getCouponId());
        }
        lambdaQueryWrapper.orderByDesc(StoreCouponUser::getId);
        List<StoreCouponUser> storeCouponUserList = dao.selectList(lambdaQueryWrapper);
        if (storeCouponUserList.size() < 1) {
            return new PageInfo<>();
        }
        ArrayList<StoreCouponUserResponse> storeCouponUserResponseList = new ArrayList<>();

        List<Integer> uidList = storeCouponUserList.stream().map(StoreCouponUser::getUid).distinct().collect(Collectors.toList());
        HashMap<Integer, User> userList = userService.getMapListInUid(uidList);

        for (StoreCouponUser storeCouponUser : storeCouponUserList) {
            StoreCouponUserResponse storeCouponUserResponse = new StoreCouponUserResponse();
            BeanUtils.copyProperties(storeCouponUser, storeCouponUserResponse);
            if (userList.containsKey(storeCouponUser.getUid())) {
                storeCouponUserResponse.setNickname(userList.get(storeCouponUser.getUid()).getNickname());
                storeCouponUserResponse.setAvatar(userList.get(storeCouponUser.getUid()).getAvatar());
            }
            storeCouponUserResponseList.add(storeCouponUserResponse);
        }
        return CommonPage.copyPageInfo(storeCouponUserPage, storeCouponUserResponseList);
    }

    /**
     * 领券/批量领券
     * @param request 新增参数
     * @return boolean
     */
    @Override
    public Boolean receive(StoreCouponUserRequest request) {
        //获取优惠券信息
        StoreCoupon storeCoupon = storeCouponService.getInfoException(request.getCouponId());

        List<Integer> uidList = CrmebUtil.stringToArray(request.getUid());
        if (uidList.size() < 1) {
            throw new CrmebException("请选择用户！");
        }

        //看是否有剩余数量
        if (storeCoupon.getIsLimited()) {
            //看剩余的是否够给当前用户
            if (storeCoupon.getLastTotal() < uidList.size()) {
                throw new CrmebException("当前剩余数量不够领取！");
            }
        }

        //过滤掉已经领取过的用户
        filterReceiveUserInUid(storeCoupon.getId(), uidList);
        if (uidList.size() < 1) {
            //都已经领取过了
            throw new CrmebException("当前用户已经领取过此优惠券了！");
        }

        //是否有固定的使用时间
        if (!storeCoupon.getIsFixedTime()) {
            String endTime = DateUtil.addDay(DateUtil.nowDate(Constants.DATE_FORMAT), storeCoupon.getDay(), Constants.DATE_FORMAT);
            storeCoupon.setUseEndTime(DateUtil.strToDate(endTime, Constants.DATE_FORMAT));
            storeCoupon.setUseStartTime(DateUtil.nowDateTimeReturnDate(Constants.DATE_FORMAT));
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
            storeCouponUser.setType(CouponConstants.STORE_COUPON_USER_TYPE_SEND);
            if (storeCoupon.getUseType() > 1) {
                storeCouponUser.setPrimaryKey(storeCoupon.getPrimaryKey());
            }
            storeCouponUserList.add(storeCouponUser);
        }

        storeCoupon.setLastTotal(storeCoupon.getLastTotal() - uidList.size());

        Boolean execute = transactionTemplate.execute(e -> {
            saveBatch(storeCouponUserList);
            storeCouponService.updateById(storeCoupon);
            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 过滤已经领取过此优惠券的用户id
     * @param couponId Integer 优惠券id
     * @param uidList List<Integer> 用户id集合
     */
    private void filterReceiveUserInUid(Integer couponId, List<Integer> uidList) {
        LambdaQueryWrapper<StoreCouponUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCouponUser::getCouponId, couponId);
        lambdaQueryWrapper.in(StoreCouponUser::getUid, uidList);
        List<StoreCouponUser> storeCouponUserList = dao.selectList(lambdaQueryWrapper);
        if (storeCouponUserList != null) {
            List<Integer> receiveUidList = storeCouponUserList.stream().map(StoreCouponUser::getUid).distinct().collect(Collectors.toList());
            uidList.removeAll(receiveUidList);
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
        List<StoreCouponUser> list = findListByUid(userId);

        if (list.size() < 1) {
            return null;
        }

        HashMap<Integer, StoreCouponUser> map = new HashMap<>();
        for (StoreCouponUser info : list) {
            map.put(info.getCouponId(), info);
        }
        return map;
    }

    private List<StoreCouponUser> findListByUid(Integer uid) {
        LambdaQueryWrapper<StoreCouponUser> lwq = new LambdaQueryWrapper<>();
        lwq.eq(StoreCouponUser::getUid, uid);
        return dao.selectList(lwq);
    }

    /**
     * 根据购物车id获取可用优惠券
     * @param preOrderNo 预下单订单号
     * @return 优惠券集合
     */
    @Override
    public List<StoreCouponUserOrder> getListByPreOrderNo(String preOrderNo) {
        // 通过缓存获取预下单对象
        String key = "user_order:" + preOrderNo;
        boolean exists = redisUtil.exists(key);
        if (!exists) {
            throw new CrmebException("预下单订单不存在");
        }
        String orderVoString = redisUtil.get(key).toString();
        OrderInfoVo orderInfoVo = JSONObject.parseObject(orderVoString, OrderInfoVo.class);
        //产品id集合
        List<Integer> productIds = orderInfoVo.getOrderDetailList().stream().map(OrderInfoDetailVo::getProductId).distinct().collect(Collectors.toList());

        //计算购物车价格
        BigDecimal maxPrice = orderInfoVo.getProTotalFee();

        LambdaQueryWrapper<StoreCouponUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Date date = DateUtil.nowDateTime();
        lambdaQueryWrapper.eq(StoreCouponUser::getStatus, 0)
                .le(StoreCouponUser::getMinPrice, maxPrice)
                .lt(StoreCouponUser::getStartTime, date)
                .gt(StoreCouponUser::getEndTime, date);
        lambdaQueryWrapper.eq(StoreCouponUser::getUid, userService.getUserIdException());
        getPrimaryKeySql(lambdaQueryWrapper, StringUtils.join(productIds, ","));


        ArrayList<StoreCouponUserOrder> storeCouponUserOrderArrayList = new ArrayList<>();
        List<StoreCouponUser> storeCouponUserList = dao.selectList(lambdaQueryWrapper);
        if (storeCouponUserList.size() < 1) {
            return storeCouponUserOrderArrayList;
        }
        //前端组件统一 转化数据
        for (StoreCouponUser storeCouponUser : storeCouponUserList) {
            StoreCouponUserOrder storeCouponUserOrder = new StoreCouponUserOrder();
            BeanUtils.copyProperties(storeCouponUser, storeCouponUserOrder);
            storeCouponUserOrderArrayList.add(storeCouponUserOrder);
        }
        return storeCouponUserOrderArrayList;
    }

    /**
     * 移动端列表
     * @param type 类型，usable-可用，unusable-不可用
     * @param userId 用户id
     * @param pageParamRequest 分页类参数
     * @return List<StoreCouponUser>
     */
    private List<StoreCouponUser> getH5List(String type, Integer userId, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreCouponUser 类的多条件查询
        LambdaQueryWrapper<StoreCouponUser> lqw = new LambdaQueryWrapper<>();

        lqw.eq(StoreCouponUser::getUid, userId);
        if ("usable".equals(type)) {
            lqw.eq(StoreCouponUser::getStatus, CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
            lqw.orderByDesc(StoreCouponUser::getId);
        }
        if ("unusable".equals(type)) {
            lqw.gt(StoreCouponUser::getStatus, CouponConstants.STORE_COUPON_USER_STATUS_USABLE);
            lqw.last(StrUtil.format(" order by case `status` when {} then {} when {} then {} when {} then {} end", 0, 1, 1, 2, 2, 3));
        }

        List<StoreCouponUser> storeCouponUserList = dao.selectList(lqw);
        if (CollUtil.isEmpty(storeCouponUserList)) {
            return CollUtil.newArrayList();
        }
        return storeCouponUserList;
    }

    /**
     * 优惠券过期定时任务
     */
    @Override
    public void overdueTask() {
        // 查询所有状态——可用的优惠券
        LambdaQueryWrapper<StoreCouponUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreCouponUser::getStatus, 0);
        List<StoreCouponUser> couponList = dao.selectList(lqw);
        if (CollUtil.isEmpty(couponList)) {
            return;
        }
        // 判断优惠券是否过期
        List<StoreCouponUser> updateList = CollUtil.newArrayList();
        String nowDateStr = DateUtil.nowDate(Constants.DATE_FORMAT);
        couponList.forEach(coupon -> {
            if (ObjectUtil.isNotNull(coupon.getEndTime())) {
                String endDateStr = DateUtil.dateToStr(coupon.getEndTime(), Constants.DATE_FORMAT);
                if (DateUtil.compareDate(nowDateStr, endDateStr, Constants.DATE_FORMAT) >= 0) {
                    coupon.setStatus(2);
                    updateList.add(coupon);
                }
            }
        });

        if (CollUtil.isEmpty(updateList)) {
            return;
        }
        boolean update = updateBatchById(updateList);
        if (!update) throw new CrmebException("批量更新优惠券过期动作失败");
    }

    /**
     * 用户领取优惠券
     */
    @Override
    public Boolean receiveCoupon(UserCouponReceiveRequest request) {
        // 获取优惠券信息
        StoreCoupon storeCoupon = storeCouponService.getInfoException(request.getCouponId());

        Integer userId = userService.getUserIdException();

        //看是否有剩余数量,是否够给当前用户
        if (storeCoupon.getIsLimited() && storeCoupon.getLastTotal() < 1) {
            throw new CrmebException("当前剩余数量不够领取！");
        }
        //过滤掉已经领取过的用户
        List<Integer> uidList = CollUtil.newArrayList();
        uidList.add(userId);
        filterReceiveUserInUid(storeCoupon.getId(), uidList);
        if (uidList.size() < 1) {
            //都已经领取过了
            throw new CrmebException("当前用户已经领取过此优惠券了！");
        }
        //是否有固定的使用时间
        if (!storeCoupon.getIsFixedTime()) {
            String endTime = DateUtil.addDay(DateUtil.nowDate(Constants.DATE_FORMAT), storeCoupon.getDay(), Constants.DATE_FORMAT);
            storeCoupon.setUseEndTime(DateUtil.strToDate(endTime, Constants.DATE_FORMAT));
            storeCoupon.setUseStartTime(DateUtil.nowDateTimeReturnDate(Constants.DATE_FORMAT));
        }
        StoreCouponUser storeCouponUser = new StoreCouponUser();
        storeCouponUser.setCouponId(storeCoupon.getId());
        storeCouponUser.setUid(userId);
        storeCouponUser.setName(storeCoupon.getName());
        storeCouponUser.setMoney(storeCoupon.getMoney());
        storeCouponUser.setMinPrice(storeCoupon.getMinPrice());
        storeCouponUser.setStartTime(storeCoupon.getUseStartTime());
        storeCouponUser.setEndTime(storeCoupon.getUseEndTime());
        storeCouponUser.setUseType(storeCoupon.getUseType());
        storeCouponUser.setType(CouponConstants.STORE_COUPON_USER_TYPE_GET);
        if (storeCoupon.getUseType() > 1) {
            storeCouponUser.setPrimaryKey(storeCoupon.getPrimaryKey());
        }
        Boolean execute = transactionTemplate.execute(e -> {
            save(storeCouponUser);
            storeCouponService.deduction(storeCoupon.getId(), 1, storeCoupon.getIsLimited());
            return Boolean.TRUE;
        });

        return execute;
    }

    /**
     * 支付成功赠送处理
     * @param couponId 优惠券编号
     * @param uid  用户uid
     * @return MyRecord
     */
    @Override
    public MyRecord paySuccessGiveAway(Integer couponId, Integer uid) {
        MyRecord record = new MyRecord();
        record.set("status", "fail");
        // 获取优惠券信息
        StoreCoupon storeCoupon = storeCouponService.getById(couponId);
        if (ObjectUtil.isNull(storeCoupon) || storeCoupon.getIsDel() || !storeCoupon.getStatus()) {
            record.set("errMsg", "优惠券信息不存在或者已失效！");
            return record;
        }

        // 判断是否达到可领取时间
        if (ObjectUtil.isNotNull(storeCoupon.getReceiveStartTime())) {
            //非永久可领取
            String date = DateUtil.nowDateTimeStr();
            int result = DateUtil.compareDate(date, DateUtil.dateToStr(storeCoupon.getReceiveStartTime(), Constants.DATE_FORMAT), Constants.DATE_FORMAT);
            if (result == -1) {
                // 未开始
                record.set("errMsg", "还未达到优惠券领取时间！");
                return record;
            }
        }

        //看是否过期
        if (storeCoupon.getReceiveEndTime() != null) {
            //非永久可领取
            String date = DateUtil.nowDateTimeStr();
            int result = DateUtil.compareDate(date, DateUtil.dateToStr(storeCoupon.getReceiveEndTime(), Constants.DATE_FORMAT), Constants.DATE_FORMAT);
            if (result == 1) {
                //过期了
                record.set("errMsg", "已超过优惠券领取最后期限！");
                return record;
            }
        }

        //看是否有剩余数量
        if (storeCoupon.getIsLimited() && storeCoupon.getLastTotal() < 1) {
            record.set("errMsg", "此优惠券已经被领完了！");
            return record;
        }

        //过滤掉已经领取过的用户
        List<Integer> uidList = CollUtil.newArrayList();
        uidList.add(uid);
        filterReceiveUserInUid(storeCoupon.getId(), uidList);
        if (uidList.size() < 1) {
            //都已经领取过了
            record.set("errMsg", "当前用户已经领取过此优惠券了！");
            return record;
        }

        //是否有固定的使用时间
        if (!storeCoupon.getIsFixedTime()) {
            String endTime = DateUtil.addDay(DateUtil.nowDate(Constants.DATE_FORMAT), storeCoupon.getDay(), Constants.DATE_FORMAT);
            storeCoupon.setUseEndTime(DateUtil.strToDate(endTime, Constants.DATE_FORMAT));
            storeCoupon.setUseStartTime(DateUtil.nowDateTimeReturnDate(Constants.DATE_FORMAT));
        }

        StoreCouponUser storeCouponUser = new StoreCouponUser();
        storeCouponUser.setCouponId(storeCoupon.getId());
        storeCouponUser.setUid(uid);
        storeCouponUser.setName(storeCoupon.getName());
        storeCouponUser.setMoney(storeCoupon.getMoney());
        storeCouponUser.setMinPrice(storeCoupon.getMinPrice());
        storeCouponUser.setStartTime(storeCoupon.getUseStartTime());
        storeCouponUser.setEndTime(storeCoupon.getUseEndTime());
        storeCouponUser.setUseType(storeCoupon.getUseType());
        storeCouponUser.setType(CouponConstants.STORE_COUPON_USER_TYPE_BUY);
        if (storeCoupon.getUseType() > 1) {
            storeCouponUser.setPrimaryKey(storeCoupon.getPrimaryKey());
        }
        record.set("status", "ok");
        record.set("storeCouponUser", storeCouponUser);
        record.set("isLimited", storeCoupon.getIsLimited());
        return record;
    }

    /**
     * 根据uid获取列表
     * @param uid uid
     * @param pageParamRequest 分页参数
     * @return 优惠券列表
     */
    @Override
    public List<StoreCouponUser> findListByUid(Integer uid, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreCouponUser 类的多条件查询
        LambdaQueryWrapper<StoreCouponUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(StoreCouponUser::getUid, uid);
        lambdaQueryWrapper.orderByDesc(StoreCouponUser::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 获取可用优惠券数量
     * @param uid 用户uid
     * @return Integer
     */
    @Override
    public Integer getUseCount(Integer uid) {
        LambdaQueryWrapper<StoreCouponUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreCouponUser::getUid, uid);
        lambdaQueryWrapper.eq(StoreCouponUser::getStatus, 0);
        List<StoreCouponUser> storeCouponUserList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(storeCouponUserList)) {
            return 0;
        }
        Date date = DateUtil.nowDateTime();
        for (int i = 0; i < storeCouponUserList.size();) {
            StoreCouponUser couponUser = storeCouponUserList.get(i);
            //判断是否在使用时间内
            if (ObjectUtil.isNotNull(couponUser.getStartTime()) && ObjectUtil.isNotNull(couponUser.getEndTime())) {
                if (date.compareTo(couponUser.getEndTime()) >= 0) {
                    storeCouponUserList.remove(i);
                    continue;
                }
            }
            i++;
        }
        return CollUtil.isEmpty(storeCouponUserList) ? 0 : storeCouponUserList.size();
    }

    /**
     * 我的优惠券列表
     * @param type 类型，usable-可用，unusable-不可用
     * @param pageParamRequest 分页参数
     * @return CommonPage<StoreCouponUserResponse>
     */
    @Override
    public CommonPage<StoreCouponUserResponse> getMyCouponList(String type, PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();

        List<StoreCouponUser> couponUserList = getH5List(type, userId, pageParamRequest);

        if (CollUtil.isEmpty(couponUserList)) {
            return null;
        }
        Date date = DateUtil.nowDateTime();
        List<StoreCouponUserResponse> responseList = CollUtil.newArrayList();
        for (StoreCouponUser storeCouponUser :couponUserList) {
            StoreCouponUserResponse storeCouponUserResponse = new StoreCouponUserResponse();
            BeanUtils.copyProperties(storeCouponUser, storeCouponUserResponse);
            String validStr = "usable";// 可用
            if (storeCouponUser.getStatus().equals(CouponConstants.STORE_COUPON_USER_STATUS_USED)) {
                validStr = "unusable";// 已用
            }
            if (storeCouponUser.getStatus().equals(CouponConstants.STORE_COUPON_USER_STATUS_LAPSED)) {
                validStr = "overdue";// 过期
            }

            //判断是否在使用时间内
            if (null != storeCouponUserResponse.getStartTime() && null != storeCouponUserResponse.getEndTime()) {
                if (storeCouponUserResponse.getStartTime().compareTo(date) > 0) {
                    validStr = "notStart";// 未开始
                }
                if (date.compareTo(storeCouponUserResponse.getEndTime()) >= 0) {
                    validStr = "overdue";// 过期
                }
            }
            storeCouponUserResponse.setValidStr(validStr);

            // 更改使用时间格式，去掉时分秒
            storeCouponUserResponse.setUseStartTimeStr(DateUtil.dateToStr(storeCouponUserResponse.getStartTime(), Constants.DATE_FORMAT_DATE));
            storeCouponUserResponse.setUseEndTimeStr(DateUtil.dateToStr(storeCouponUserResponse.getEndTime(), Constants.DATE_FORMAT_DATE));
            responseList.add(storeCouponUserResponse);
        }
        return CommonPage.restPage(responseList);
    }

    private void getPrimaryKeySql(LambdaQueryWrapper<StoreCouponUser> lambdaQueryWrapper, String productIdStr) {
        if (StringUtils.isBlank(productIdStr)) {
            return;
        }

        List<Integer> categoryIdList = storeProductService.getSecondaryCategoryByProductId(productIdStr);
        String categoryIdStr = categoryIdList.stream().map(Object::toString).collect(Collectors.joining(","));
        lambdaQueryWrapper.and(i -> i.and(
                //通用券  商品券  品类券
                t -> t.eq(StoreCouponUser::getUseType, 1)
                        .or(p -> p.eq(StoreCouponUser::getUseType , 2).apply(StrUtil.format(" primary_key in ({})", productIdStr)))
                        .or(c -> c.eq(StoreCouponUser::getUseType , 3).apply(StrUtil.format(" primary_key in ({})", categoryIdStr)))

        ));
    }
}

