package com.zbkj.crmeb.bargain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.BargainConstants;
import com.constants.Constants;
import com.constants.UserConstants;
import com.exception.CrmebException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.zbkj.crmeb.bargain.dao.StoreBargainUserHelpDao;
import com.zbkj.crmeb.bargain.model.StoreBargain;
import com.zbkj.crmeb.bargain.model.StoreBargainUser;
import com.zbkj.crmeb.bargain.model.StoreBargainUserHelp;
import com.zbkj.crmeb.bargain.response.StoreBargainUserHelpResponse;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserHelpService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserService;
import com.zbkj.crmeb.front.request.BargainFrontRequest;
import com.zbkj.crmeb.front.response.BargainCountResponse;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserService;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

/**
 * StoreBargainUserHelpService 实现类
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


    /**
     * 列表
     * @param bargainUserId
     * @return
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
     * @return
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
     * @return
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
     * 获取帮忙好友砍价数据
     * @param request
     * @return
     */
    @Override
    public BargainCountResponse getH5CountByBargainId(BargainFrontRequest request) {
        boolean isConsume = false;
        User user = userService.getInfo();
        StoreBargain storeBargain = storeBargainService.getById(request.getBargainId());
        // 获取砍价商品用户对象
        StoreBargainUser bargainUser = storeBargainUserService.getByBargainIdAndUidAndPink(request.getBargainId(), request.getBargainUserUid());
        if (ObjectUtil.isNull(bargainUser)) {
//            throw new CrmebException("用户砍价对象不存在");
            return new BargainCountResponse(ZERO, 0, storeBargain.getPrice().subtract(storeBargain.getMinPrice()), 0, 0, true, isConsume);
        }
        // 获取砍价好友信息
        LambdaQueryWrapper<StoreBargainUserHelp> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargainUserHelp::getBargainId, request.getBargainId());
        lqw.eq(StoreBargainUserHelp::getBargainUserId, bargainUser.getId());
        List<StoreBargainUserHelp> storeBargainUserHelps = dao.selectList(lqw);
        if (CollUtil.isEmpty(storeBargainUserHelps)) {
            return new BargainCountResponse(ZERO, 0, bargainUser.getBargainPrice().subtract(bargainUser.getBargainPriceMin()), 0, BargainConstants.BARGAIN_USER_STATUS_PARTICIPATE, true, isConsume);
        }

        // 已砍金额
        BigDecimal alreadyPrice = bargainUser.getPrice();
        // 砍价人数
        int count = storeBargainUserHelps.size();
        // 剩余金额
        BigDecimal price = bargainUser.getBargainPrice().subtract(bargainUser.getBargainPriceMin()).subtract(bargainUser.getPrice());
        // 砍价百分比
        int pricePercent = bargainUser.getPrice().multiply(new BigDecimal(100)).divide((bargainUser.getBargainPrice().subtract(bargainUser.getBargainPriceMin())), 0, BigDecimal.ROUND_DOWN).intValue();
        // 自己的砍价状态
        boolean userBargainStatus = false;
        List<StoreBargainUserHelp> collect = storeBargainUserHelps.stream().filter(o -> o.getUid().equals(user.getUid())).collect(Collectors.toList());
        if (CollUtil.isEmpty(collect)) {
            if (user.getUid().equals(request.getBargainUserUid())) {// 自己的砍价活动
                userBargainStatus = true;
            } else {// 其他人的砍价活动
                // 用户对本商品的砍价次数
                List<Integer> tempUserIdList = null;
                List<StoreBargainUser> tempUserList = storeBargainUserService.getListByBargainIdAndUid(storeBargain.getId(), user.getUid());
                if (CollUtil.isNotEmpty(tempUserList)) {
                    tempUserIdList = tempUserList.stream().map(StoreBargainUser::getId).collect(Collectors.toList());
                }

                Integer helpCount = getUserHelpNum(user.getUid(), storeBargain.getId(), tempUserIdList);
                if (helpCount == 0 || helpCount < storeBargain.getBargainNum()) {
                    userBargainStatus = true;
                } else {
                    isConsume = true;
                }
            }
        }

        // 活动状态
        Integer status = bargainUser.getStatus();
        return new BargainCountResponse(alreadyPrice, count, price, pricePercent, status, userBargainStatus, isConsume);
    }

    /**
     * 获取帮忙砍价好友信息
     * @param request
     * @param pageParamRequest
     * @return
     */
    @Override
    public PageInfo<StoreBargainUserHelpResponse> getHelpList(BargainFrontRequest request, PageParamRequest pageParamRequest) {
        StoreBargainUser storeBargainUser = storeBargainUserService.getByBargainIdAndUidAndPink(request.getBargainId(), request.getBargainUserUid());
        Page<StoreBargainUserHelp> helpPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        if (ObjectUtil.isNull(storeBargainUser)) {
            return CommonPage.copyPageInfo(helpPage, new ArrayList<>());
        }
        LambdaQueryWrapper<StoreBargainUserHelp> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargainUserHelp::getBargainId, request.getBargainId());
        lqw.eq(StoreBargainUserHelp::getBargainUserId, storeBargainUser.getId());
        List<StoreBargainUserHelp> storeBargainUserHelps = dao.selectList(lqw);
        if (CollUtil.isEmpty(storeBargainUserHelps)) {
            return CommonPage.copyPageInfo(helpPage, CollUtil.newArrayList());
        }
        List<StoreBargainUserHelpResponse> list = storeBargainUserHelps.stream().map(userHelp -> {
            StoreBargainUserHelpResponse helpResponse = new StoreBargainUserHelpResponse();
            BeanUtils.copyProperties(userHelp, helpResponse);
            helpResponse.setAddTime(DateUtil.timestamp2DateStr(userHelp.getAddTime(), Constants.DATE_FORMAT));
            User tempUser = userService.getById(userHelp.getUid());
            helpResponse.setNickname(tempUser.getNickname());
            helpResponse.setAvatar(tempUser.getAvatar());
            return helpResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(helpPage, list);
    }

    /**
     * 砍价
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> help(BargainFrontRequest request) {
        Map<String, Object> map = new HashMap<>();

        StoreBargain storeBargain = storeBargainService.getById(request.getBargainId());
        if (ObjectUtil.isNull(storeBargain)) throw new CrmebException("砍价商品不存在");
        if (!storeBargain.getStatus())  throw new CrmebException("砍价活动已结束");
        StoreBargainUser storeBargainUser = storeBargainUserService.getByBargainIdAndUidAndPink(request.getBargainId(), request.getBargainUserUid());
        if (ObjectUtil.isNull(storeBargainUser)) throw new CrmebException("砍价商品用户信息不存在");
        User user = userService.getInfo();

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
        } else {// 不是给自己砍价,不包含给自己看的
            // 用户对本商品的砍价次数
            List<Integer> tempUserIdList = null;
            List<StoreBargainUser> tempUserList = storeBargainUserService.getListByBargainIdAndUid(storeBargain.getId(), user.getUid());
            if (CollUtil.isNotEmpty(tempUserList)) {
                tempUserIdList = tempUserList.stream().map(StoreBargainUser::getId).collect(Collectors.toList());
            }

            Integer helpCount = getUserHelpNum(user.getUid(), storeBargain.getId(), tempUserIdList);
            if (helpCount >= storeBargain.getBargainNum()) {
                throw new CrmebException("您的帮砍次数已达上限");
            }
        }

        // 获取活动用户的总已砍次数
        Long helpCount = getHelpCountByBargainIdAndBargainUid(request.getBargainId(), storeBargainUser.getId());
        // 计算砍价金额
        BigDecimal bargainPrice = helpBargain(storeBargain, storeBargainUser, helpCount);

        LambdaUpdateWrapper<StoreBargainUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(StoreBargainUser::getPrice, storeBargainUser.getPrice().add(bargainPrice));
        updateWrapper.eq(StoreBargainUser::getId, storeBargainUser.getId());
        updateWrapper.eq(StoreBargainUser::getPrice, storeBargainUser.getPrice());
        storeBargainUser.setPrice(storeBargainUser.getPrice().add(bargainPrice));
        boolean update = storeBargainUserService.update(updateWrapper);
        if (!update) throw new CrmebException("砍价失败!");


        StoreBargainUserHelp userHelp = new StoreBargainUserHelp();
        userHelp.setBargainId(request.getBargainId());
        userHelp.setBargainUserId(storeBargainUser.getId());
        userHelp.setUid(user.getUid());
        userHelp.setPrice(bargainPrice);
        userHelp.setAddTime(System.currentTimeMillis());
        boolean save = save(userHelp);
        if (!save) throw new CrmebException("砍价失败!!!");

        // 分享人数添加
        if (!user.getUid().equals(request.getBargainUserUid())) {
            storeBargain.setShare(storeBargain.getShare() + 1);
            storeBargainService.updateById(storeBargain);
        }

        // 如果砍价完成，发送微信模板消息
        if (storeBargain.getPeopleNum().equals(helpCount.intValue() + 1)) {
            // 发送微信模板消息
//            HashMap<String, String> temMap = new HashMap<>();
//            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "好腻害！你的朋友们已经帮你砍到底价了！");
//            temMap.put("keyword1", storeBargain.getTitle());
//            temMap.put("keyword2", storeBargain.getMinPrice().toString());
//            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "感谢您的参与！");
//
//            templateMessageService.push(Constants.WE_CHAT_TEMP_KEY_BARGAIN_SUCCESS, temMap, storeBargainUser.getUid(), Constants.PAY_TYPE_WE_CHAT_FROM_PUBLIC);

            User tempUser = userService.getById(storeBargainUser.getUid());
            pushMessageOrder(storeBargain, tempUser);
        }

        map.put("bargainPrice", bargainPrice);
        return map;
    }

    /**
     * 发送消息通知
     * @param storeBargain 砍价商品
     * @param user 发起砍价用户
     */
    private void pushMessageOrder(StoreBargain storeBargain, User user) {
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
            temMap.put(Constants.WE_CHAT_TEMP_KEY_FIRST, "好腻害！你的朋友们已经帮你砍到底价了！");
            temMap.put("keyword1", storeBargain.getTitle());
            temMap.put("keyword2", storeBargain.getMinPrice().toString());
            temMap.put(Constants.WE_CHAT_TEMP_KEY_END, "请尽快支付！");
            templateMessageService.pushTemplateMessage(Constants.WE_CHAT_TEMP_KEY_BARGAIN_SUCCESS, temMap, userToken.getToken());
            return;
        }
        // 小程序发送订阅消息
        userToken = userTokenService.getTokenByUserId(user.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
        if (ObjectUtil.isNull(userToken)) {
            return ;
        }
        // 组装数据
        temMap.put("thing6",  storeBargain.getTitle());
        temMap.put("amount3", storeBargain.getMinPrice().toString() + "元");
        temMap.put("thing7", "好腻害！你的朋友们已经帮你砍到底价了！");
        templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_BARGAIN_SUCCESS, temMap, userToken.getToken());
    }

    /**
     * 获取参与砍价人员数量
     */
    @Override
    public Long getHelpPeopleCount() {
        StoreBargainUser storeBargainUser = new StoreBargainUser();
        storeBargainUser.setIsDel(false);
        List<StoreBargainUser> userList = storeBargainUserService.getByEntity(storeBargainUser);
        if (CollUtil.isEmpty(userList)) {
            return 0L;
        }
        int sum = userList.stream().mapToInt(bargainUser -> {
            LambdaQueryWrapper<StoreBargainUserHelp> lqw = new LambdaQueryWrapper<>();
            lqw.select(StoreBargainUserHelp::getId);
            lqw.eq(StoreBargainUserHelp::getBargainUserId, bargainUser.getId());
            lqw.groupBy(StoreBargainUserHelp::getUid);
            return dao.selectCount(lqw);
        }).sum();
        return (long) sum;
    }

    /**
     * 获取参与砍价人员数量
     * @param bargainId 砍价商品id
     */
    @Override
    public Long getHelpPeopleCountByBargainId(Integer bargainId) {
        List<StoreBargainUser> userList = storeBargainUserService.getListByBargainId(bargainId);
        if (CollUtil.isEmpty(userList)) {
            return 0L;
        }
        int sum = userList.stream().mapToInt(bargainUser -> {
            LambdaQueryWrapper<StoreBargainUserHelp> lqw = new LambdaQueryWrapper<>();
            lqw.select(StoreBargainUserHelp::getId);
            lqw.eq(StoreBargainUserHelp::getBargainUserId, bargainUser.getId());
            lqw.groupBy(StoreBargainUserHelp::getUid);
            return dao.selectCount(lqw);
        }).sum();
        return (long) sum;
    }

    /**
     * 获取用户还剩余的砍价金额
     * @param bargainId 砍价商品编号
     * @param bargainUserUid 砍价发起用户uid
     * @return surplusPrice 剩余的砍价金额
     */
    @Override
    public BigDecimal getSurplusPrice(Integer bargainId, Integer bargainUserUid) {
        StoreBargainUser storeBargainUser = storeBargainUserService.getByBargainIdAndUid(bargainId, bargainUserUid);
        if (ObjectUtil.isNull(storeBargainUser)) throw new CrmebException("砍价用户信息不存在");
        BigDecimal coverPrice = storeBargainUser.getBargainPrice().subtract(storeBargainUser.getBargainPriceMin());
        BigDecimal alreadyPrice = storeBargainUser.getPrice();
        return coverPrice.subtract(alreadyPrice);
    }

    /**
     * 砍价发起用户信息
     * @param bargainFrontRequest 砍价公共请求参数
     */
    @Override
    public Map<String, String> startUser(BargainFrontRequest bargainFrontRequest) {
        if (ObjectUtil.isNull(bargainFrontRequest.getBargainUserUid())) {
            throw new CrmebException("砍价发起用户id不能为空");
        }
        User user = userService.getById(bargainFrontRequest.getBargainUserUid());
        if (ObjectUtil.isNull(user)) {
            return null;
        }
        Map<String, String> map = CollUtil.newHashMap();
        map.put("avatar", Optional.ofNullable(user.getAvatar()).orElse(""));
        map.put("nickname", Optional.ofNullable(user.getNickname()).orElse(""));
        return map;
    }

    /**
     * 砍价金额计算
     * @param storeBargain  砍价商品
     * @param storeBargainUser 砍价商品用户
     * @param helpCount     帮助砍价次数
     * @return
     * 砍价时在设置好的价格区间内以大于20%小于80%之间随机一个价格后 在总金额中减去,砍价最后一位不可随机价格,直接砍价到设置区间的最低价格为准
     */
    private BigDecimal helpBargain(StoreBargain storeBargain, StoreBargainUser storeBargainUser, Long helpCount) {
        BigDecimal minPrice = storeBargainUser.getBargainPriceMin();//底价
        BigDecimal price = storeBargainUser.getBargainPrice();//砍价商品金额
        BigDecimal userPrice = storeBargainUser.getPrice();//已砍金额
        Integer peopleNum = storeBargain.getPeopleNum();//帮助砍价好友人数限定

        BigDecimal subtract = price.subtract(minPrice);// 可砍价金额（总）

        BigDecimal bargainPrice = ZERO;
        // 没有砍过
        if (helpCount == 0) {
            bargainPrice = RandomUtil.randomBigDecimal(subtract.multiply(new BigDecimal(Constants.BARGAIN_TATIO_DOWN)), subtract.multiply(new BigDecimal(Constants.BARGAIN_TATIO_UP)));
            return bargainPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        // 最后一次砍价
        if (peopleNum - helpCount.intValue() == 1) {
            return subtract.subtract(userPrice);
        }
        // 其他情况
        BigDecimal remaining = subtract.subtract(userPrice);
        bargainPrice = RandomUtil.randomBigDecimal(remaining.multiply(new BigDecimal(Constants.BARGAIN_TATIO_DOWN)), remaining.multiply(new BigDecimal(Constants.BARGAIN_TATIO_UP)));
        return bargainPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
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
     * @param uid
     * @param bargainId
     * @return
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

