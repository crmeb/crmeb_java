package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.BargainConstants;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.NotifyConstants;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemNotification;
import com.zbkj.common.request.BargainFrontRequest;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.model.bargain.StoreBargain;
import com.zbkj.common.model.bargain.StoreBargainUser;
import com.zbkj.common.model.bargain.StoreBargainUserHelp;
import com.zbkj.common.response.StoreBargainUserHelpResponse;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.service.dao.StoreBargainUserHelpDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * StoreBargainUserHelpService 实现类
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
public class StoreBargainUserHelpServiceImpl extends ServiceImpl<StoreBargainUserHelpDao, StoreBargainUserHelp> implements StoreBargainUserHelpService {

    @Resource
    private StoreBargainUserHelpDao dao;

    @Autowired
    private StoreBargainUserService storeBargainUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private TemplateMessageService templateMessageService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private SystemNotificationService systemNotificationService;


    /**
     * 列表
     * @param bargainUserId 砍价活动id
     * @return List<StoreBargainUserHelpResponse>
     */
    @Override
    public List<StoreBargainUserHelpResponse> getList(Integer bargainUserId) {
        LambdaQueryWrapper<StoreBargainUserHelp> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargainUserHelp::getBargainUserId, bargainUserId);
        lqw.orderByDesc(StoreBargainUserHelp::getId);
        List<StoreBargainUserHelp> helpList = dao.selectList(lqw);
        if (CollUtil.isEmpty(helpList)) {
            return CollUtil.newArrayList();
        }
        List<StoreBargainUserHelpResponse> list = helpList.stream().map(help -> {
            StoreBargainUserHelpResponse helpResponse = new StoreBargainUserHelpResponse();
            BeanUtils.copyProperties(help, helpResponse);
            helpResponse.setAddTime(DateUtil.timestamp2DateStr(help.getAddTime(), Constants.DATE_FORMAT));
            User user = userService.getById(help.getUid());
            helpResponse.setAvatar(user.getAvatar());
            helpResponse.setNickname(user.getNickname());
            return helpResponse;
        }).collect(Collectors.toList());
        return list;
    }

    /**
     * 获取帮助砍价人数
     * @param bargainId 砍价商品ID
     * @return Long
     */
    @Override
    public Long getHelpCountByBargainId(Integer bargainId) {
        LambdaQueryWrapper<StoreBargainUserHelp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(StoreBargainUserHelp::getId);
        lambdaQueryWrapper.eq(StoreBargainUserHelp::getBargainId, bargainId);
        return dao.selectCount(lambdaQueryWrapper).longValue();
    }

    /**
     * 获取帮助砍价人数
     * @param bargainId 砍价商品ID
     * @param bargainUserId 砍价商品发起用户表id
     * @return Long
     */
    @Override
    public Long getHelpCountByBargainIdAndBargainUid(Integer bargainId, Integer bargainUserId) {
        LambdaQueryWrapper<StoreBargainUserHelp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(StoreBargainUserHelp::getId);
        lambdaQueryWrapper.eq(StoreBargainUserHelp::getBargainId, bargainId);
        lambdaQueryWrapper.eq(StoreBargainUserHelp::getBargainUserId, bargainUserId);
        return dao.selectCount(lambdaQueryWrapper).longValue();
    }

    /**
     * 砍价
     * @param request 砍价请求参数
     * @return 砍价金额
     */
    @Override
    public Map<String, Object> help(BargainFrontRequest request) {
        if (ObjectUtil.isNull(request.getBargainUserId())) {
            throw new CrmebException("砍价活动id不能为空");
        }

        Map<String, Object> map = new HashMap<>();
        User user = userService.getInfoException();
        StoreBargain storeBargain = storeBargainService.getById(request.getBargainId());
        if (ObjectUtil.isNull(storeBargain) || storeBargain.getIsDel()) {
            throw new CrmebException("对应的砍价商品不存在");
        }
        if (!storeBargain.getStatus()) {
            throw new CrmebException("砍价商品已下架");
        }
        if (storeBargain.getQuota() <= 0 || storeBargain.getStock() <= 0) {
            throw new CrmebException("砍价商品已售罄");
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis < storeBargain.getStartTime()) {
            throw new CrmebException("砍价活动未开始");
        }
        if (currentTimeMillis > storeBargain.getStopTime()) {
            throw new CrmebException("砍价活动已结束");
        }
        StoreBargainUser storeBargainUser = storeBargainUserService.getById(request.getBargainUserId());
        if (ObjectUtil.isNull(storeBargainUser)) {
            throw new CrmebException("砍价商品用户信息不存在");
        }

        // 判断是否砍价成功
        if (storeBargainUser.getStatus().equals(BargainConstants.BARGAIN_USER_STATUS_SUCCESS) || storeBargainUser.getBargainPriceMin().compareTo(storeBargainUser.getBargainPrice().subtract(storeBargainUser.getPrice())) >= 0) {
            throw new CrmebException("商品已完成砍价");
        }

        // 给自己砍价
        if (user.getUid().equals(storeBargainUser.getUid())) {
            StoreBargainUserHelp myHelp = getByUidAndBargainIdAndBargainUserId(user.getUid(), storeBargain.getId(), storeBargainUser.getId());
            if (ObjectUtil.isNotNull(myHelp)) {
                throw new CrmebException("您已经砍过了");
            }
        } else {// 不是给自己砍价,不包含给自己砍的
            // 用户对本商品的砍价次数
            List<StoreBargainUser> tempUserList = storeBargainUserService.getListByBargainIdAndUid(storeBargain.getId(), user.getUid());
            if (CollUtil.isNotEmpty(tempUserList)) {
                List<Integer> tempUserIdList = tempUserList.stream().map(StoreBargainUser::getId).collect(Collectors.toList());
                Integer helpCount = getUserHelpNum(user.getUid(), storeBargain.getId(), tempUserIdList);
                if (helpCount >= storeBargain.getBargainNum()) {
                    throw new CrmebException("您的帮砍次数已达上限");
                }
            }
        }

        // 获取活动用户的总已砍次数
        Long helpCount = getHelpCountByBargainIdAndBargainUid(request.getBargainId(), storeBargainUser.getId());
        // 计算砍价金额
        BigDecimal bargainPrice = helpBargain(storeBargain, storeBargainUser, helpCount);
        boolean isOut = false;
        if (storeBargain.getPeopleNum().equals(helpCount.intValue() + 1)) {
            isOut = true;
        }

        boolean finalIsOut = isOut;
        Boolean execute = transactionTemplate.execute(e -> {
            LambdaUpdateWrapper<StoreBargainUser> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(StoreBargainUser::getPrice, storeBargainUser.getPrice().add(bargainPrice));
            if (finalIsOut) {
                updateWrapper.set(StoreBargainUser::getStatus, 3);
            }
            updateWrapper.eq(StoreBargainUser::getId, storeBargainUser.getId());
            updateWrapper.eq(StoreBargainUser::getPrice, storeBargainUser.getPrice());
            storeBargainUserService.update(updateWrapper);
            storeBargainUser.setPrice(storeBargainUser.getPrice().add(bargainPrice));

            StoreBargainUserHelp userHelp = new StoreBargainUserHelp();
            userHelp.setBargainId(request.getBargainId());
            userHelp.setBargainUserId(storeBargainUser.getId());
            userHelp.setUid(user.getUid());
            userHelp.setPrice(bargainPrice);
            userHelp.setAddTime(System.currentTimeMillis());
            save(userHelp);

            // 分享人数添加
            if (!user.getUid().equals(request.getBargainUserUid())) {
                storeBargain.setShare(storeBargain.getShare() + 1);
                storeBargainService.updateById(storeBargain);
            }

            return Boolean.TRUE;
        });

        if (!execute) {
            throw new CrmebException("砍价失败!");
        }

        try {
            // 如果砍价完成，发送微信模板消息
            if (isOut) {
                // 发送微信模板消息
                SystemNotification notification = systemNotificationService.getByMark(NotifyConstants.BARGAINING_SUCCESS_MARK);
                if (notification.getIsWechat().equals(1) || notification.getIsRoutine().equals(1)) {
                    User tempUser = userService.getById(storeBargainUser.getUid());
                    pushMessageOrder(storeBargain, tempUser, notification);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("砍价成功发送微信消息失败：" + e.getMessage());
        }
        map.put("bargainPrice", bargainPrice);
        return map;
    }

    /**
     * 发送消息通知
     * @param storeBargain 砍价商品
     * @param user 发起砍价用户
     */
    private void pushMessageOrder(StoreBargain storeBargain, User user, SystemNotification notification) {
        UserToken userToken;
        HashMap<String, String> temMap = new HashMap<>();
        // 公众号
        if (notification.getIsWechat().equals(1)) {
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_WECHAT);
            if (ObjectUtil.isNotNull(userToken)) {
                // 发送微信模板消息
                temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "好腻害！你的朋友们已经帮你砍到底价了！");
                temMap.put("keyword1", storeBargain.getTitle());
                temMap.put("keyword2", storeBargain.getMinPrice().toString());
                temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "请尽快支付！");
                templateMessageService.pushTemplateMessage(notification.getWechatId(), temMap, userToken.getToken());
                return;
            }
        }
        if (notification.getIsRoutine().equals(1)) {
            // 小程序发送订阅消息
            userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
            if (ObjectUtil.isNull(userToken)) {
                return ;
            }
            // 组装数据
//        temMap.put("thing6",  storeBargain.getTitle());
//        temMap.put("amount3", storeBargain.getMinPrice().toString() + "元");
//        temMap.put("thing7", "好腻害！你的朋友们已经帮你砍到底价了！");
            temMap.put("thing1",  storeBargain.getTitle());
            temMap.put("amount2", storeBargain.getMinPrice().toString() + "元");
            temMap.put("thing3", "好腻害！你的朋友们已经帮你砍到底价了！");
            templateMessageService.pushMiniTemplateMessage(notification.getRoutineId(), temMap, userToken.getToken());
        }
    }

    /**
     * 获取参与砍价总人数（次）
     * @return Integer
     */
    @Override
    public Integer getCount() {
        LambdaQueryWrapper<StoreBargainUserHelp> lqw = new LambdaQueryWrapper<>();
        lqw.select(StoreBargainUserHelp::getId);
        return dao.selectCount(lqw);
    }

    /**
     * 获取好友助力列表
     * @param bargainUserId 砍价用户表id
     * @return List<StoreBargainUserHelp>
     */
    @Override
    public List<StoreBargainUserHelp> getHelpListByBargainUserId(Integer bargainUserId) {
        LambdaQueryWrapper<StoreBargainUserHelp> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargainUserHelp::getBargainUserId, bargainUserId);
        return dao.selectList(lqw);
    }

    /**
     * 是否帮砍过
     * @param bargainUserId 用户砍价活动id
     * @param uid 用户uid
     * @return Boolean
     */
    @Override
    public Boolean getIsHelp(Integer bargainUserId, Integer uid) {
        LambdaQueryWrapper<StoreBargainUserHelp> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargainUserHelp::getBargainUserId, bargainUserId);
        lqw.eq(StoreBargainUserHelp::getUid, uid);
        StoreBargainUserHelp userHelp = dao.selectOne(lqw);
        if (ObjectUtil.isNull(userHelp)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 获取该砍价商品用户的帮砍次数
     * @param bargainId 砍价商品id
     * @param uid 用户uid
     * @param bargainUserIdList 用户参与砍价活动id数组
     * @return Integer
     */
    @Override
    public Integer getHelpCountByBargainIdAndUidInBUserId(Integer bargainId, Integer uid, List<Integer> bargainUserIdList) {
        LambdaQueryWrapper<StoreBargainUserHelp> lqw = new LambdaQueryWrapper<>();
        lqw.select(StoreBargainUserHelp::getId);
        lqw.eq(StoreBargainUserHelp::getBargainId, bargainId);
        lqw.eq(StoreBargainUserHelp::getUid, uid);
        lqw.notIn(StoreBargainUserHelp::getBargainUserId, bargainUserIdList);
        return dao.selectCount(lqw);
    }

    /**
     * 砍价金额计算
     * @param storeBargain  砍价商品
     * @param storeBargainUser 砍价商品用户
     * @param helpCount     帮助砍价次数
     * @return
     * 砍价时在设置好的价格区间内以大于20%小于80%之间随机一个价格后 在总金额中减去,砍价最后一位不可随机价格,直接砍价到设置区间的最低价格为准
     *
     * 剩余砍价金额 - 剩余砍价次数 * 0.01 = 可砍价金额
     * 可砍价金额 > 0.01 以大于20%小于80%之间随机一个价格
     *
     */
    private BigDecimal helpBargain(StoreBargain storeBargain, StoreBargainUser storeBargainUser, Long helpCount) {
        BigDecimal minPrice = storeBargainUser.getBargainPriceMin();//底价
        BigDecimal price = storeBargainUser.getBargainPrice();//砍价商品金额
        BigDecimal userPrice = storeBargainUser.getPrice();//已砍金额
        Integer peopleNum = storeBargain.getPeopleNum();//帮助砍价好友人数限定

        BigDecimal subtract = price.subtract(minPrice);// 可砍价金额（总）

        BigDecimal bargainPrice;
        double retainPrice;// 需要保留的金额
        // 没有砍过
        if (helpCount == 0) {
            // 可砍价金额
            retainPrice = (peopleNum - 1) * 0.01;
            BigDecimal canBargainPrice = subtract.subtract(new BigDecimal(retainPrice));
            if (canBargainPrice.compareTo(new BigDecimal("0.01")) > 0) {// 超过0.01
                bargainPrice = RandomUtil.randomBigDecimal(subtract.multiply(new BigDecimal(Constants.BARGAIN_TATIO_DOWN)), subtract.multiply(new BigDecimal(Constants.BARGAIN_TATIO_UP)));
                if (bargainPrice.compareTo(new BigDecimal("0.01")) < 0) {
                    bargainPrice = new BigDecimal("0.01");
                }
                return bargainPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            return new BigDecimal("0.01");
        }
        // 最后一次砍价
        if (peopleNum - helpCount.intValue() == 1) {
            return subtract.subtract(userPrice);
        }
        // 其他情况
        retainPrice = (peopleNum - helpCount.intValue()) * 0.01;
        BigDecimal remaining = subtract.subtract(userPrice).subtract(new BigDecimal(retainPrice));
        if (remaining.compareTo(new BigDecimal("0.01")) > 0) {// 超过0.01
            bargainPrice = RandomUtil.randomBigDecimal(remaining.multiply(new BigDecimal(Constants.BARGAIN_TATIO_DOWN)), remaining.multiply(new BigDecimal(Constants.BARGAIN_TATIO_UP)));
            if (bargainPrice.compareTo(new BigDecimal("0.01")) < 0) {
                bargainPrice = new BigDecimal("0.01");
            }
            return bargainPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return new BigDecimal("0.01");
    }

    /**
     * 获取帮助砍价用户的帮砍次数
     */
    private StoreBargainUserHelp getByUidAndBargainIdAndBargainUserId(Integer uid, Integer bargainId, Integer bargainUserId) {
        LambdaQueryWrapper<StoreBargainUserHelp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreBargainUserHelp::getUid, uid);
        lambdaQueryWrapper.eq(StoreBargainUserHelp::getBargainId, bargainId);
        lambdaQueryWrapper.eq(StoreBargainUserHelp::getBargainUserId, bargainUserId);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 获取用户帮砍次数
     * 用户对商品砍了几次(不包含自己)
     * @param uid 用户uid
     * @param bargainId 砍价商品id
     * @return Integer
     */
    private Integer getUserHelpNum(Integer uid, Integer bargainId, List<Integer> tempUserIdList) {
        LambdaQueryWrapper<StoreBargainUserHelp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(StoreBargainUserHelp::getId);
        lambdaQueryWrapper.eq(StoreBargainUserHelp::getUid, uid);
        lambdaQueryWrapper.eq(StoreBargainUserHelp::getBargainId, bargainId);
        if (CollUtil.isNotEmpty(tempUserIdList)) {
            lambdaQueryWrapper.notIn(StoreBargainUserHelp::getBargainUserId, tempUserIdList);
        }
        return dao.selectCount(lambdaQueryWrapper);
    }
}

