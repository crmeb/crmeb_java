package com.zbkj.crmeb.bargain.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.utils.DateUtil;
import com.utils.vo.dateLimitUtilVo;
import com.zbkj.crmeb.bargain.dao.StoreBargainUserDao;
import com.zbkj.crmeb.bargain.model.StoreBargain;
import com.zbkj.crmeb.bargain.model.StoreBargainUser;
import com.zbkj.crmeb.bargain.request.StoreBargainUserSearchRequest;
import com.zbkj.crmeb.bargain.response.StoreBargainUserResponse;
import com.zbkj.crmeb.bargain.service.StoreBargainService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserHelpService;
import com.zbkj.crmeb.bargain.service.StoreBargainUserService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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


    /**
    * 分页展示砍价参与用户列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @since 2020-11-12
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
            bargainUserResponse.setNum(helpCount.intValue());
            return bargainUserResponse;
        }).collect(Collectors.toList());

        return CommonPage.copyPageInfo(startPage, list);
    }

    /**
     * 获取砍价用户列表
     * @param bargainId 砍价商品ID
     * @return
     */
    @Override
    public List<StoreBargainUser> getListByBargainId(Integer bargainId) {
        QueryWrapper<StoreBargainUser> qw = new QueryWrapper<>();
        qw.select("id", "status");
        qw.eq("bargain_id", bargainId).eq("is_del", false);
        return dao.selectList(qw);
    }

    /**
     * 获取砍价商品参与人数
     * @param bargainId
     * @return
     */
    @Override
    public Long getCountByBargainId(Integer bargainId) {
        QueryWrapper<StoreBargainUser> qw = new QueryWrapper<>();
        qw.select("id");
        qw.eq("bargain_id", bargainId).eq("is_del", false);
        return dao.selectCount(qw).longValue();
    }

    /**
     * 通过砍价商品ID + 用户uid 获取用户砍价商品信息
     * @param bargainId
     * @param uid
     * @return
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
     * @param bargainId
     * @param uid
     * @return
     */
    @Override
    public StoreBargainUser getByBargainIdAndUidAndPink(Integer bargainId, Integer uid) {
        LambdaQueryWrapper<StoreBargainUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargainUser::getBargainId, bargainId);
        lqw.eq(StoreBargainUser::getUid, uid);
        lqw.eq(StoreBargainUser::getIsDel, false);
        lqw.eq(StoreBargainUser::getStatus, 1);
        lqw.orderByDesc(StoreBargainUser::getId);
        List<StoreBargainUser> userList = dao.selectList(lqw);
        if (CollUtil.isEmpty(userList)) {
            return null;
        }
        return userList.get(0);
    }

    /**
     * 通过砍价商品ID + 用户uid 获取用户砍价中砍价商品信息
     * @param bargainId
     * @param uid
     * @return
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
     * 判断是否参与活动
     * @param bargainId
     * @param uid
     * @return
     */
    @Override
    public Boolean isExistByBargainIdAndUid(Integer bargainId, Integer uid) {
        LambdaQueryWrapper<StoreBargainUser> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreBargainUser::getBargainId, bargainId);
        lqw.eq(StoreBargainUser::getUid, uid);
        lqw.eq(StoreBargainUser::getIsDel, false);
        Integer count = dao.selectCount(lqw);
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * 砍价商品用户根据实体查询
     * @param bargainUser
     * @return
     */
    @Override
    public List<StoreBargainUser> getByEntity(StoreBargainUser bargainUser) {
        LambdaQueryWrapper<StoreBargainUser> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(bargainUser);
        return dao.selectList(lqw);
    }

}

