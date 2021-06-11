package com.zbkj.crmeb.bargain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.BargainConstants;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.CrmebUtil;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.bargain.dao.StoreBargainUserDao;
import com.zbkj.crmeb.bargain.model.StoreBargain;
import com.zbkj.crmeb.bargain.model.StoreBargainUser;
import com.zbkj.crmeb.bargain.model.StoreBargainUserHelp;
import com.zbkj.crmeb.bargain.request.StoreBargainUserSearchRequest;
import com.zbkj.crmeb.bargain.response.StoreBargainUserResponse;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserHelpService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserService;
import com.zbkj.crmeb.front.request.BargainFrontRequest;
import com.zbkj.crmeb.front.response.BargainRecordResponse;
import com.zbkj.crmeb.front.response.BargainUserInfoResponse;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.service.StoreOrderService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StoreBargainUserService 实现类
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
public class StoreBargainUserServiceImpl extends ServiceImpl<StoreBargainUserDao, StoreBargainUser> implements StoreBargainUserService {

    @Resource
    private StoreBargainUserDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreBargainUserHelpService storeBargainUserHelpService;

    @Autowired
    private StoreOrderService storeOrderService;


    /**
    * 分页展示砍价参与用户列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return List<StoreBargainUser>
    */
    @Override
    public PageInfo<StoreBargainUserResponse> getList(StoreBargainUserSearchRequest request, PageParamRequest pageParamRequest) {
        Page<StoreBargainUser> startPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreBargainUser> lqw = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(StoreBargainUser::getStatus, request.getStatus());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
            lqw.between(StoreBargainUser::getAddTime, DateUtil.dateStr2Timestamp(dateLimit.getStartTime(), Constants.DATE_TIME_TYPE_BEGIN), DateUtil.dateStr2Timestamp(dateLimit.getEndTime(), Constants.DATE_TIME_TYPE_END));
        }
        lqw.orderByDesc(StoreBargainUser::getId);
        List<StoreBargainUser> bargainUserList = dao.selectList(lqw);
        if (CollUtil.isEmpty(bargainUserList)) {
            return CommonPage.copyPageInfo(startPage, CollUtil.newArrayList());
        }
        List<StoreBargainUserResponse> list = bargainUserList.stream().map(bargainUser -> {
            StoreBargainUserResponse bargainUserResponse = new StoreBargainUserResponse();
            BeanUtils.copyProperties(bargainUser, bargainUserResponse);
            bargainUserResponse.setAddTime(DateUtil.timestamp2DateStr(bargainUser.getAddTime(), Constants.DATE_FORMAT));
            bargainUserResponse.setNowPrice(bargainUser.getBargainPrice().subtract(bargainUser.getPrice()));
            // 查询用户信息
            User user = userService.getById(bargainUser.getUid());
            bargainUserResponse.setAvatar(user.getAvatar());
            bargainUserResponse.setNickname(user.getNickname());
            // 查询砍价商品信息
            StoreBargain storeBargain = storeBargainService.getById(bargainUser.getBargainId());
            bargainUserResponse.setTitle(storeBargain.getTitle());

            bargainUserResponse.setDataTime(DateUtil.timestamp2DateStr(storeBargain.getStopTime(), Constants.DATE_FORMAT));
            bargainUserResponse.setPeopleNum(storeBargain.getPeopleNum());
            // 剩余砍价次数
            Long helpCount = storeBargainUserHelpService.getHelpCountByBargainIdAndBargainUid(storeBargain.getId(), bargainUser.getId());
            bargainUserResponse.setNum(storeBargain.getPeopleNum() - helpCount.intValue());
            return bargainUserResponse;
        }).collect(Collectors.toList());

        return CommonPage.copyPageInfo(startPage, list);
    }

    /**
     * 获取砍价用户列表
     * @param bargainId 砍价商品ID
     * @return List<StoreBargainUser>
     */
    @Override
    public List<StoreBargainUser> getListByBargainId(Integer bargainId) {
        QueryWrapper<StoreBargainUser> qw = new QueryWrapper<>();
        qw.select("id", "status");
        qw.eq("bargain_id", bargainId).eq("is_del", false);
        return dao.selectList(qw);
    }

    /**
     * 通过砍价商品ID + 用户uid 获取用户砍价商品信息
     * @param bargainId 砍价商品编号
     * @param uid       参与用户uid
     * @return StoreBargainUser
     */
    @Override
    public StoreBargainUser getByBargainIdAndUid(Integer bargainId, Integer uid) {
        LambdaQueryWrapper<StoreBargainUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargainUser::getBargainId, bargainId);
        lqw.eq(StoreBargainUser::getUid, uid);
        lqw.eq(StoreBargainUser::getIsDel, false);
        lqw.orderByDesc(StoreBargainUser::getId);
        List<StoreBargainUser> userList = dao.selectList(lqw);
        if (CollUtil.isEmpty(userList)) {
            return null;
        }
        return userList.get(0);
    }

    /**
     * 通过砍价商品ID + 用户uid 获取用户砍价中砍价商品信息
     * @param bargainId 砍价商品编号
     * @param uid       参与用户uid
     * @return StoreBargainUser
     */
    @Override
    public StoreBargainUser getByBargainIdAndUidAndPink(Integer bargainId, Integer uid) {
        LambdaQueryWrapper<StoreBargainUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargainUser::getBargainId, bargainId);
        lqw.eq(StoreBargainUser::getUid, uid);
        lqw.eq(StoreBargainUser::getIsDel, false);
        lqw.eq(StoreBargainUser::getStatus, BargainConstants.BARGAIN_USER_STATUS_PARTICIPATE);
        lqw.orderByDesc(StoreBargainUser::getId);
        List<StoreBargainUser> userList = dao.selectList(lqw);
        if (CollUtil.isEmpty(userList)) {
            return null;
        }
        return userList.get(0);
    }

    /**
     * 通过砍价商品ID + 用户uid 获取用户砍价中砍价商品信息
     * @param bargainId 砍价商品编号
     * @param uid       参与用户uid
     * @return StoreBargainUser
     */
    @Override
    public List<StoreBargainUser> getListByBargainIdAndUid(Integer bargainId, Integer uid) {
        LambdaQueryWrapper<StoreBargainUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargainUser::getBargainId, bargainId);
        lqw.eq(StoreBargainUser::getUid, uid);
        lqw.eq(StoreBargainUser::getIsDel, false);
        return dao.selectList(lqw);
    }

    /**
     * 砍价商品用户根据实体查询
     * @param bargainUser 砍价活动
     * @return List<StoreBargainUser>
     */
    @Override
    public List<StoreBargainUser> getByEntity(StoreBargainUser bargainUser) {
        LambdaQueryWrapper<StoreBargainUser> lqw = Wrappers.lambdaQuery();
        lqw.setEntity(bargainUser);
        return dao.selectList(lqw);
    }

    /**
     * 获取砍价成功列表Header
     */
    @Override
    public List<StoreBargainUser> getHeaderList() {
        LambdaQueryWrapper<StoreBargainUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreBargainUser::getStatus, 3);
        lqw.eq(StoreBargainUser::getIsDel, false);
        lqw.groupBy(StoreBargainUser::getUid);
        lqw.orderByDesc(StoreBargainUser::getId);
        lqw.last(" limit 10");
        return dao.selectList(lqw);
    }

    /**
     * 获取用户砍价信息
     * @param bargainFrontRequest 请求参数
     * @return BargainUserInfoResponse
     */
    @Override
    public BargainUserInfoResponse getBargainUserInfo(BargainFrontRequest bargainFrontRequest) {
        if (ObjectUtil.isNull(bargainFrontRequest.getBargainUserId()) || bargainFrontRequest.getBargainUserId()<= 0) { // 获取自己的砍价信息
            return oneselfBargainActivity(bargainFrontRequest);
        }
        return otherBargainActivity(bargainFrontRequest);
    }

    /**
     * 其他途径进入砍价活动
     * @param bargainFrontRequest 请求参数
     * @return BargainUserInfoResponse
     */
    private BargainUserInfoResponse otherBargainActivity(BargainFrontRequest bargainFrontRequest) {
        User user = userService.getInfoException();
        // 获取砍价商品信息
        StoreBargain storeBargain = storeBargainService.getById(bargainFrontRequest.getBargainId());
        if (ObjectUtil.isNull(storeBargain) || storeBargain.getIsDel()) {
            throw new CrmebException("未找到对应砍价商品信息");
        }
        if (!storeBargain.getStatus()) {
            throw new CrmebException("砍价商品已下架");
        }
        if (storeBargain.getStock() <= 0 || storeBargain.getQuota() <= 0) {
            throw new CrmebException("砍价商品已售罄");
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (storeBargain.getStopTime() < currentTimeMillis) {
            throw new CrmebException("活动已结束");
        }
        StoreBargainUser bargainUser = getById(bargainFrontRequest.getBargainUserId());
        if (ObjectUtil.isNull(bargainUser)) {
            throw new CrmebException("用户砍价活动未找到");
        }
        if (bargainUser.getIsDel()) {
            throw new CrmebException("用户砍价活动已取消");
        }
        if (bargainUser.getStatus().equals(2)) {
            throw new CrmebException("砍价活动已过期");
        }
        // 判断是否是自己的砍价活动
        BargainUserInfoResponse infoResponse = new BargainUserInfoResponse();
        int bargainStatus;// 砍价状态
        int percent;// 砍价百分比
        if (bargainUser.getUid().equals(user.getUid())) {// 自己的砍价活动
            if (bargainUser.getStatus().equals(3)) {// 砍价已完成
                // 判断是否生成订单
                StoreOrder bargainOrder = storeOrderService.getByBargainOrder(bargainUser.getBargainId(), bargainUser.getId());
                if (ObjectUtil.isNotNull(bargainOrder)) {// 有订单
                    // 判断是否支付
                    if (!bargainOrder.getPaid()) {// 未支付
                        bargainStatus = 8;// 砍价已生成订单未支付
                        BigDecimal alreadyPrice = bargainUser.getPrice();// 已砍金额
                        BigDecimal surplusPrice = BigDecimal.ZERO;// 剩余金额
                        percent = 100;
                        infoResponse.setBargainStatus(bargainStatus);
                        infoResponse.setAlreadyPrice(alreadyPrice);
                        infoResponse.setSurplusPrice(surplusPrice);
                        infoResponse.setBargainPercent(percent);
                        // 获取好友助力列表
                        infoResponse.setUserHelpList(getHelpList(bargainUser.getId()));
                        infoResponse.setStoreBargainUserId(bargainUser.getId());
                        return infoResponse;
                    }
                    // 已支付
                    bargainStatus = 9;// 砍价订单已支付
                    BigDecimal alreadyPrice = bargainUser.getPrice();// 已砍金额
                    BigDecimal surplusPrice = BigDecimal.ZERO;// 剩余金额
                    percent = 100;
                    infoResponse.setBargainStatus(bargainStatus);
                    infoResponse.setAlreadyPrice(alreadyPrice);
                    infoResponse.setSurplusPrice(surplusPrice);
                    infoResponse.setBargainPercent(percent);
                    // 获取好友助力列表
                    infoResponse.setUserHelpList(getHelpList(bargainUser.getId()));
                    infoResponse.setStoreBargainUserId(bargainUser.getId());
                    return infoResponse;
                }
                // 无订单
                bargainStatus = 4;// 砍价已完成
                BigDecimal alreadyPrice = bargainUser.getPrice();// 已砍金额
                BigDecimal surplusPrice = BigDecimal.ZERO;// 剩余金额
                percent = 100;
                infoResponse.setBargainStatus(bargainStatus);
                infoResponse.setAlreadyPrice(alreadyPrice);
                infoResponse.setSurplusPrice(surplusPrice);
                infoResponse.setBargainPercent(percent);
                // 获取好友助力列表
                infoResponse.setUserHelpList(getHelpList(bargainUser.getId()));
                infoResponse.setStoreBargainUserId(bargainUser.getId());
                return infoResponse;
            }
            bargainStatus = 3;// 砍价中
            BigDecimal alreadyPrice = bargainUser.getPrice();// 已砍金额
            BigDecimal surplusPrice = bargainUser.getBargainPrice().subtract(storeBargain.getMinPrice()).subtract(alreadyPrice);// 剩余金额
            percent =  CrmebUtil.percentInstanceIntVal(alreadyPrice, alreadyPrice.add(surplusPrice));
            infoResponse.setBargainStatus(bargainStatus);
            infoResponse.setAlreadyPrice(alreadyPrice);
            infoResponse.setSurplusPrice(surplusPrice);
            infoResponse.setBargainPercent(percent);
            // 获取好友助力列表
            infoResponse.setUserHelpList(getHelpList(bargainUser.getId()));
            infoResponse.setStoreBargainUserId(bargainUser.getId());
            return infoResponse;
        }
        // 其他人的砍价活动
        if (bargainUser.getStatus().equals(3)) {
            bargainStatus = 4;// 砍价已完成
            BigDecimal alreadyPrice = bargainUser.getPrice();// 已砍金额
            BigDecimal surplusPrice = BigDecimal.ZERO;// 剩余金额
            percent = 100;
            infoResponse.setBargainStatus(bargainStatus);
            infoResponse.setAlreadyPrice(alreadyPrice);
            infoResponse.setSurplusPrice(surplusPrice);
            infoResponse.setBargainPercent(percent);
            // 获取好友助力列表
            infoResponse.setUserHelpList(getHelpList(bargainUser.getId()));
            infoResponse.setStoreBargainUserId(bargainUser.getId());
            User tempUser = userService.getById(bargainUser.getUid());
            infoResponse.setStoreBargainUserName(tempUser.getNickname());
            infoResponse.setStoreBargainUserAvatar(tempUser.getAvatar());
            return infoResponse;
        }
        // 是否帮ta砍过
        Boolean isHelp = storeBargainUserHelpService.getIsHelp(bargainUser.getId(), user.getUid());
        if (isHelp) { // 帮砍过
            bargainStatus = 6;// 已帮砍
            BigDecimal alreadyPrice = bargainUser.getPrice();// 已砍金额
            BigDecimal surplusPrice = bargainUser.getBargainPrice().subtract(storeBargain.getMinPrice()).subtract(alreadyPrice);// 剩余金额
            percent = CrmebUtil.percentInstanceIntVal(alreadyPrice, alreadyPrice.add(surplusPrice));
            infoResponse.setBargainStatus(bargainStatus);
            infoResponse.setAlreadyPrice(alreadyPrice);
            infoResponse.setSurplusPrice(surplusPrice);
            infoResponse.setBargainPercent(percent);
            // 获取好友助力列表
            infoResponse.setUserHelpList(getHelpList(bargainUser.getId()));
            infoResponse.setStoreBargainUserId(bargainUser.getId());
            User tempUser = userService.getById(bargainUser.getUid());
            infoResponse.setStoreBargainUserName(tempUser.getNickname());
            infoResponse.setStoreBargainUserAvatar(tempUser.getAvatar());
            return infoResponse;
        }
        // 获取该砍价商品用户的帮砍次数
        Integer helpNum = getHelpNumByBargainIdAndUid(bargainFrontRequest.getBargainId(), user.getUid());
        if (storeBargain.getBargainNum() <= helpNum) {
            bargainStatus = 7;// 帮砍次数已满
            BigDecimal alreadyPrice = bargainUser.getPrice();// 已砍金额
            BigDecimal surplusPrice = bargainUser.getBargainPrice().subtract(storeBargain.getMinPrice()).subtract(alreadyPrice);// 剩余金额
            percent = CrmebUtil.percentInstanceIntVal(alreadyPrice, alreadyPrice.add(surplusPrice));
            infoResponse.setBargainStatus(bargainStatus);
            infoResponse.setAlreadyPrice(alreadyPrice);
            infoResponse.setSurplusPrice(surplusPrice);
            infoResponse.setBargainPercent(percent);
            // 获取好友助力列表
            infoResponse.setUserHelpList(getHelpList(bargainUser.getId()));
            infoResponse.setStoreBargainUserId(bargainUser.getId());
            User tempUser = userService.getById(bargainUser.getUid());
            infoResponse.setStoreBargainUserName(tempUser.getNickname());
            infoResponse.setStoreBargainUserAvatar(tempUser.getAvatar());
            return infoResponse;
        }
        // 没有帮砍过
        bargainStatus = 5;// 可以帮砍
        BigDecimal alreadyPrice = bargainUser.getPrice();// 已砍金额
        BigDecimal surplusPrice = bargainUser.getBargainPrice().subtract(storeBargain.getMinPrice()).subtract(alreadyPrice);// 剩余金额
        percent = CrmebUtil.percentInstanceIntVal(alreadyPrice, alreadyPrice.add(surplusPrice));
        infoResponse.setBargainStatus(bargainStatus);
        infoResponse.setAlreadyPrice(alreadyPrice);
        infoResponse.setSurplusPrice(surplusPrice);
        infoResponse.setBargainPercent(percent);
        // 获取好友助力列表
        infoResponse.setUserHelpList(getHelpList(bargainUser.getId()));
        infoResponse.setStoreBargainUserId(bargainUser.getId());
        User tempUser = userService.getById(bargainUser.getUid());
        infoResponse.setStoreBargainUserName(tempUser.getNickname());
        infoResponse.setStoreBargainUserAvatar(tempUser.getAvatar());
        return infoResponse;
    }

    /**
     * 获取好友助力列表
     * @param bargainUserId 砍价活动id
     * @return List<StoreBargainUserHelp>
     */
    private List<StoreBargainUserHelp> getHelpList(Integer bargainUserId) {
        List<StoreBargainUserHelp> helpList = storeBargainUserHelpService.getHelpListByBargainUserId(bargainUserId);
        helpList.forEach(e -> {
            User helpUser = userService.getById(e.getUid());
            e.setNickname(helpUser.getNickname());
            e.setAvatar(helpUser.getAvatar());
            e.setAddTimeStr(cn.hutool.core.date.DateUtil.date(e.getAddTime()).toString());
        });
        return helpList;
    }

    /**
     * 自己的砍价活动
     * @param bargainFrontRequest 请求参数
     * @return BargainUserInfoResponse
     */
    private BargainUserInfoResponse oneselfBargainActivity(BargainFrontRequest bargainFrontRequest) {
        User user = userService.getInfoException();
        // 获取砍价商品信息
        StoreBargain storeBargain = storeBargainService.getById(bargainFrontRequest.getBargainId());
        if (ObjectUtil.isNull(storeBargain) || storeBargain.getIsDel()) {
            throw new CrmebException("未找到对应砍价商品信息");
        }
        if (!storeBargain.getStatus()) {
            throw new CrmebException("砍价商品已下架");
        }
        if (storeBargain.getStock() <= 0 || storeBargain.getQuota() <= 0) {
            throw new CrmebException("砍价商品已售罄");
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (storeBargain.getStopTime() < currentTimeMillis) {
            throw new CrmebException("活动已结束");
        }

        BargainUserInfoResponse infoResponse = new BargainUserInfoResponse();
        // 查看是否有正在砍价的信息
        StoreBargainUser storeBargainUser = getLastByIdAndUid(bargainFrontRequest.getBargainId(), user.getUid());
        int percent = 0;// 砍价百分比
        int bargainStatus = 1;// 砍价状态：1-可以参与砍价
        if (ObjectUtil.isNull(storeBargainUser)) {// 没有参与该商品的砍价活动
            BigDecimal alreadyPrice = BigDecimal.ZERO;// 已砍金额
            BigDecimal surplusPrice = storeBargain.getPrice().subtract(storeBargain.getMinPrice());// 剩余金额
            infoResponse.setBargainStatus(bargainStatus);
            infoResponse.setAlreadyPrice(alreadyPrice);
            infoResponse.setSurplusPrice(surplusPrice);
            infoResponse.setBargainPercent(percent);
            return infoResponse;
        }
        if (storeBargainUser.getStatus().equals(2)) {// 活动结束，砍价未完成
            throw new CrmebException("未在活动期内完成砍价");
        }
        // 参与过该商品的砍价
        if (storeBargainUser.getIsDel()) { // 已取消
            // 获取用户参与过的次数
            Integer bargainCount = getCountByBargainIdAndUid(bargainFrontRequest.getBargainId(), user.getUid());
            if (storeBargain.getNum() >= bargainCount) {
                bargainStatus = 2;// 参与次数已满
                BigDecimal alreadyPrice = BigDecimal.ZERO;// 已砍金额
                BigDecimal surplusPrice = storeBargain.getPrice().subtract(storeBargain.getMinPrice());// 剩余金额
                infoResponse.setBargainStatus(bargainStatus);
                infoResponse.setAlreadyPrice(alreadyPrice);
                infoResponse.setSurplusPrice(surplusPrice);
                return infoResponse;
            }
            // 还可以参与
            BigDecimal alreadyPrice = BigDecimal.ZERO;// 已砍金额
            BigDecimal surplusPrice = storeBargain.getPrice().subtract(storeBargain.getMinPrice());// 剩余金额
            infoResponse.setBargainStatus(bargainStatus);
            infoResponse.setAlreadyPrice(alreadyPrice);
            infoResponse.setSurplusPrice(surplusPrice);
            infoResponse.setBargainPercent(percent);
            return infoResponse;
        }
        if (storeBargainUser.getStatus().equals(3)) {// 砍价已完成
            // 判断是否生成订单
            StoreOrder bargainOrder = storeOrderService.getByBargainOrder(storeBargainUser.getBargainId(), storeBargainUser.getId());
            if (ObjectUtil.isNotNull(bargainOrder)) {// 有订单
                // 判断是否支付
                if (!bargainOrder.getPaid()) {// 未支付
                    bargainStatus = 8;// 砍价已生成订单未支付
                    BigDecimal alreadyPrice = storeBargainUser.getPrice();// 已砍金额
                    BigDecimal surplusPrice = BigDecimal.ZERO;// 剩余金额
                    percent = 100;
                    infoResponse.setBargainStatus(bargainStatus);
                    infoResponse.setAlreadyPrice(alreadyPrice);
                    infoResponse.setSurplusPrice(surplusPrice);
                    infoResponse.setBargainPercent(percent);
                    // 获取好友助力列表
                    infoResponse.setUserHelpList(getHelpList(storeBargainUser.getId()));
                    infoResponse.setStoreBargainUserId(storeBargainUser.getId());
                    return infoResponse;
                }
                // 已支付，看起新的活动
                // 获取用户参与过的次数
                Integer bargainCount = getCountByBargainIdAndUid(bargainFrontRequest.getBargainId(), user.getUid());
                if (storeBargain.getNum() <= bargainCount) {
                    bargainStatus = 2;// 参与次数已满
                    BigDecimal alreadyPrice = BigDecimal.ZERO;// 已砍金额
                    BigDecimal surplusPrice = storeBargain.getPrice().subtract(storeBargain.getMinPrice());// 剩余金额
                    infoResponse.setBargainStatus(bargainStatus);
                    infoResponse.setAlreadyPrice(alreadyPrice);
                    infoResponse.setSurplusPrice(surplusPrice);
                    return infoResponse;
                }
                // 还可以参与
                BigDecimal alreadyPrice = BigDecimal.ZERO;// 已砍金额
                BigDecimal surplusPrice = storeBargain.getPrice().subtract(storeBargain.getMinPrice());// 剩余金额
                infoResponse.setBargainStatus(bargainStatus);
                infoResponse.setAlreadyPrice(alreadyPrice);
                infoResponse.setSurplusPrice(surplusPrice);
                infoResponse.setBargainPercent(percent);
                return infoResponse;
            }
            // 没有订单
            bargainStatus = 4;// 砍价已完成
            BigDecimal alreadyPrice = storeBargainUser.getPrice();// 已砍金额
            BigDecimal surplusPrice = BigDecimal.ZERO;// 剩余金额
            percent = 100;
            infoResponse.setBargainStatus(bargainStatus);
            infoResponse.setAlreadyPrice(alreadyPrice);
            infoResponse.setSurplusPrice(surplusPrice);
            infoResponse.setBargainPercent(percent);
            // 获取好友助力列表
            infoResponse.setUserHelpList(getHelpList(storeBargainUser.getId()));
            infoResponse.setStoreBargainUserId(storeBargainUser.getId());
            return infoResponse;
        }
        // 有尚未砍完的活动
        bargainStatus = 3;// 砍价中
        BigDecimal alreadyPrice = storeBargainUser.getPrice();// 已砍金额
        BigDecimal surplusPrice = storeBargainUser.getBargainPrice().subtract(storeBargain.getMinPrice()).subtract(alreadyPrice);// 剩余金额
        percent =  CrmebUtil.percentInstanceIntVal(alreadyPrice, alreadyPrice.add(surplusPrice));
        infoResponse.setBargainStatus(bargainStatus);
        infoResponse.setAlreadyPrice(alreadyPrice);
        infoResponse.setSurplusPrice(surplusPrice);
        infoResponse.setBargainPercent(percent);
        // 获取好友助力列表
        infoResponse.setUserHelpList(getHelpList(storeBargainUser.getId()));
        infoResponse.setStoreBargainUserId(storeBargainUser.getId());
        return infoResponse;
    }

    /**
     * 获取最后一条砍价信息
     * @return StoreBargainUser
     */
    private StoreBargainUser getLastByIdAndUid(Integer id, Integer uid) {
        LambdaQueryWrapper<StoreBargainUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreBargainUser::getBargainId, id);
        lqw.eq(StoreBargainUser::getUid, uid);
        lqw.orderByDesc(StoreBargainUser::getId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 砍价记录
     * @return PageInfo<BargainRecordResponse>
     */
    @Override
    public PageInfo<BargainRecordResponse> getRecordList(PageParamRequest pageParamRequest) {
        Integer userId = userService.getUserIdException();
        Page<Object> startPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<StoreBargainUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreBargainUser::getUid, userId);
        lqw.orderByDesc(StoreBargainUser::getId);
        List<StoreBargainUser> bargainUserList = dao.selectList(lqw);
        if (CollUtil.isEmpty(bargainUserList)) {
            return new PageInfo<>();
        }
        List<Integer> bargainIdList = bargainUserList.stream().map(StoreBargainUser::getBargainId).distinct().collect(Collectors.toList());
        HashMap<Integer, StoreBargain> bargainMap = storeBargainService.getMapInId(bargainIdList);
        List<BargainRecordResponse> responseList = bargainUserList.stream().map(e -> {
            BargainRecordResponse recordResponse = new BargainRecordResponse();
            StoreBargain storeBargain = bargainMap.get(e.getBargainId());
            BeanUtils.copyProperties(storeBargain, recordResponse);
            recordResponse.setBargainUserId(e.getId());
            recordResponse.setStatus(e.getStatus());
            recordResponse.setIsDel(e.getIsDel());
            recordResponse.setIsOrder(false);
            recordResponse.setIsPay(false);
            if (!e.getIsDel() && e.getStatus().equals(3)) {
                // 查询是否有订单
                StoreOrder bargainOrder = storeOrderService.getByBargainOrder(e.getBargainId(), e.getId());
                if (ObjectUtil.isNotNull(bargainOrder)) {
                    recordResponse.setIsOrder(true);
                    if (bargainOrder.getIsDel()) {
                        recordResponse.setIsDel(true);
                    } else if (bargainOrder.getPaid()) {
                        recordResponse.setIsPay(true);
                    } else {
                        recordResponse.setOrderNo(bargainOrder.getOrderId());
                    }
                }
            }
            // 剩余金额
            BigDecimal surplusPrice;
            if (e.getStatus().equals(3)) {
                surplusPrice = e.getBargainPriceMin();
            } else {
                surplusPrice = e.getBargainPriceMin().add(e.getBargainPrice()).subtract(e.getPrice());
            }
            recordResponse.setSurplusPrice(surplusPrice);
            return recordResponse;
        }).collect(Collectors.toList());

        return CommonPage.copyPageInfo(startPage, responseList);
    }

    private Integer getHelpNumByBargainIdAndUid(Integer bargainId, Integer uid) {
        // 查看是否有正在砍价的信息
        LambdaQueryWrapper<StoreBargainUser> lqw = Wrappers.lambdaQuery();
        lqw.select(StoreBargainUser::getId);
        lqw.eq(StoreBargainUser::getBargainId, bargainId);
        lqw.eq(StoreBargainUser::getUid, uid);
        List<StoreBargainUser> bargainUserList = dao.selectList(lqw);
        if (CollUtil.isEmpty(bargainUserList)) {
            return 0;
        }
        List<Integer> bargainUserIdList = bargainUserList.stream().map(StoreBargainUser::getId).collect(Collectors.toList());
        return storeBargainUserHelpService.getHelpCountByBargainIdAndUidInBUserId(bargainId, uid, bargainUserIdList);
    }

    /**
     * 获取用户参与砍价活动的次数（成功）
     * @param bargainId 砍价商品id
     * @param uid 用户uid
     * @return 用户参与砍价活动的次数（成功）
     */
    private Integer getCountByBargainIdAndUid(Integer bargainId, Integer uid) {
        LambdaQueryWrapper<StoreBargainUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreBargainUser::getBargainId, bargainId);
        lqw.eq(StoreBargainUser::getUid, uid);
        lqw.eq(StoreBargainUser::getIsDel, false);
        lqw.eq(StoreBargainUser::getStatus, 3);
        return dao.selectCount(lqw);
    }

}

