package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserExperienceRecord;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.model.user.UserSign;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.ExperienceRecordConstants;
import com.zbkj.common.constants.IntegralRecordConstants;
import com.zbkj.common.constants.SysGroupDataConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.response.UserSignInfoResponse;
import com.zbkj.common.vo.UserSignMonthVo;
import com.zbkj.common.vo.UserSignVo;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.vo.SystemGroupDataSignConfigVo;
import com.zbkj.service.dao.UserSignDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * UserSignServiceImpl 接口实现
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
public class UserSignServiceImpl extends ServiceImpl<UserSignDao, UserSign> implements UserSignService {

    @Resource
    private UserSignDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private UserExperienceRecordService userExperienceRecordService;

    /**
     * 用户积分列表
     *
     * @param pageParamRequest 分页类参数
     * @return List<UserSignVo>
     */
    @Override
    public List<UserSignVo> getList(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserSign> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSign::getType, 1);
        lambdaQueryWrapper.eq(UserSign::getUid, userService.getUserIdException());
        lambdaQueryWrapper.orderByDesc(UserSign::getId);
        List<UserSign> userSignList = dao.selectList(lambdaQueryWrapper);

        ArrayList<UserSignVo> userSignVoList = new ArrayList<>();
        if (userSignList.size() < 1) {
            return userSignVoList;
        }
        for (UserSign userSign : userSignList) {
            userSignVoList.add(new UserSignVo(userSign.getTitle(), userSign.getNumber(), userSign.getCreateDay()));
        }
        return userSignVoList;
    }

    /**
     * 签到
     */
    @Override
    public SystemGroupDataSignConfigVo sign() {
        User user = userService.getInfoException();

        // 获取最后一次签到记录
        UserSign lastUserSign = getLastDayByUid(user.getUid());
        if (ObjectUtil.isNull(lastUserSign)) {
            // 没有签到过,重置签到次数
            user.setSignNum(0);
        } else {
            // 判断是否重复签到
            String lastDate = DateUtil.dateToStr(lastUserSign.getCreateDay(), Constants.DATE_FORMAT_DATE);
            String nowDate = DateUtil.nowDate(Constants.DATE_FORMAT_DATE);
            //对比今天数据
            if (DateUtil.compareDate(lastDate, nowDate, Constants.DATE_FORMAT_DATE) == 0) {
                throw new CrmebException("今日已签到。不可重复签到");
            }
            String nextDate = DateUtil.addDay(lastUserSign.getCreateDay(), 1, Constants.DATE_FORMAT_DATE);
            int compareDate = DateUtil.compareDate(nextDate, nowDate, Constants.DATE_FORMAT_DATE);
            if (compareDate != 0) {
                //不相等，所以不是连续签到,重置签到次数
                user.setSignNum(0);
            }
        }

        //获取签到数据
        List<SystemGroupDataSignConfigVo> config = getSignConfig();
        if (CollUtil.isEmpty(config)) {
            throw new CrmebException("签到配置不存在，请在管理端配置签到数据");
        }

        //如果已经签到一个周期，那么再次签到的时候直接从第一天重新开始
        if (user.getSignNum().equals(config.size())) {
            user.setSignNum(0);
        }

        user.setSignNum(user.getSignNum() + 1);
        SystemGroupDataSignConfigVo configVo = null;
        for (SystemGroupDataSignConfigVo systemSignConfigVo : config) {
            if (user.getSignNum().equals(systemSignConfigVo.getDay())) {
                configVo = systemSignConfigVo;
                break ;
            }
        }
        if (ObjectUtil.isNull(configVo)) {
            throw new CrmebException("请先配置签到天数！");
        }

        //保存签到数据
        UserSign userSign = new UserSign();
        userSign.setUid(user.getUid());
        userSign.setTitle(Constants.SIGN_TYPE_INTEGRAL_TITLE);
        userSign.setNumber(configVo.getIntegral());
        userSign.setType(Constants.SIGN_TYPE_INTEGRAL);
        userSign.setBalance(user.getIntegral() + configVo.getIntegral());
        userSign.setCreateDay(DateUtil.strToDate(DateUtil.nowDate(Constants.DATE_FORMAT_DATE), Constants.DATE_FORMAT_DATE));

        // 生成用户积分记录
        UserIntegralRecord integralRecord = new UserIntegralRecord();
        integralRecord.setUid(user.getUid());
        integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SIGN);
        integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
        integralRecord.setTitle(IntegralRecordConstants.BROKERAGE_RECORD_TITLE_SIGN);
        integralRecord.setIntegral(configVo.getIntegral());
        integralRecord.setBalance(user.getIntegral() + configVo.getIntegral());
        integralRecord.setMark(StrUtil.format("签到积分奖励增加了{}积分", configVo.getIntegral()));
        integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);

        //更新用户经验信息
        UserExperienceRecord experienceRecord = new UserExperienceRecord();
        experienceRecord.setUid(user.getUid());
        experienceRecord.setLinkType(ExperienceRecordConstants.EXPERIENCE_RECORD_LINK_TYPE_SIGN);
        experienceRecord.setType(ExperienceRecordConstants.EXPERIENCE_RECORD_TYPE_ADD);
        experienceRecord.setTitle(ExperienceRecordConstants.EXPERIENCE_RECORD_TITLE_SIGN);
        experienceRecord.setExperience(configVo.getExperience());
        experienceRecord.setBalance(user.getExperience() + configVo.getExperience());
        experienceRecord.setMark(StrUtil.format("签到经验奖励增加了{}经验", configVo.getExperience()));
        experienceRecord.setStatus(ExperienceRecordConstants.EXPERIENCE_RECORD_STATUS_CREATE);

        // 更新用户积分
        user.setIntegral(user.getIntegral() + configVo.getIntegral());
        // 更新用户经验
        user.setExperience(user.getExperience() + configVo.getExperience());

        Boolean execute = transactionTemplate.execute(e -> {
            //保存签到数据
            save(userSign);
            // 更新用户积分记录
            userIntegralRecordService.save(integralRecord);
            //更新用户经验信息
            userExperienceRecordService.save(experienceRecord);
            //更新用户 签到天数、积分、经验
            userService.updateById(user);
            // 用户升级
            userLevelService.upLevel(user);
            return Boolean.TRUE;
        });

        if (!execute) {
            throw new CrmebException("修改用户签到信息失败!");
        }

        return configVo;
    }


    /**
     * 今日记录详情
     */
    @Override
    public HashMap<String, Object> get() {
        HashMap<String, Object> map = new HashMap<>();
        //当前积分
        User info = userService.getInfo();
        map.put("integral", info.getIntegral());
        //总计签到天数
        map.put("count", signCount(info.getUid()));
        //连续签到数据

        //今日是否已经签到
        map.put("today", false);
        return map;
    }

    /**
     * 签到配置
     *
     * @return List<SystemGroupDataSignConfigVo>
     */
    @Override
    public List<SystemGroupDataSignConfigVo> getSignConfig() {
        //获取配置数据
        return systemGroupDataService.getListByGid(SysGroupDataConstants.GROUP_DATA_ID_SIGN, SystemGroupDataSignConfigVo.class);
    }

    /**
     * 列表年月
     *
     * @param pageParamRequest 分页类参数
     * @return List<UserSignVo>
     */
    @Override
    public List<UserSignMonthVo> getListGroupMonth(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserSign> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSign::getType, 1);
        lambdaQueryWrapper.eq(UserSign::getUid, userService.getUserIdException());
        lambdaQueryWrapper.orderByDesc(UserSign::getCreateDay);
        List<UserSign> userSignList = dao.selectList(lambdaQueryWrapper);

        ArrayList<UserSignMonthVo> signMonthVoArrayList = new ArrayList<>();
        if (userSignList.size() < 1) {
            return signMonthVoArrayList;
        }

        for (UserSign userSign : userSignList) {
            String date = DateUtil.dateToStr(userSign.getCreateDay(), Constants.DATE_FORMAT_MONTH);
            UserSignVo userSignVo = new UserSignVo(userSign.getTitle(), userSign.getNumber(), userSign.getCreateDay());
            boolean findResult = false;
            if (signMonthVoArrayList.size() > 0) {
                //有数据之后则 判断是否已存在，存在则更新
                for (UserSignMonthVo userSignMonthVo : signMonthVoArrayList) {
                    if (userSignMonthVo.getMonth().equals(date)) {
                        userSignMonthVo.getList().add(userSignVo);
                        findResult = true;
                        break;
                    }
                }
            }

            //不存在则创建
            if (!findResult) {
                //如果没有找到则需要单独添加
                ArrayList<UserSignVo> userSignVoArrayList = new ArrayList<>();
                userSignVoArrayList.add(userSignVo);
                signMonthVoArrayList.add(new UserSignMonthVo(date, userSignVoArrayList));
            }
        }
        return signMonthVoArrayList;
    }

    /**
     * 获取用户签到信息
     * @return UserSignInfoResponse
     */
    @Override
    public UserSignInfoResponse getUserSignInfo() {
        User user = userService.getInfoException();
        UserSignInfoResponse userSignInfoResponse = new UserSignInfoResponse();
        BeanUtils.copyProperties(user, userSignInfoResponse);

        // 签到总次数
        userSignInfoResponse.setSumSignDay(getCount(user.getUid()));
        // 今天是否签到
        Boolean isCheckNowDaySign = checkDaySign(user.getUid());
        userSignInfoResponse.setIsDaySign(isCheckNowDaySign);
        // 昨天是否签到
        Boolean isYesterdaySign = checkYesterdaySign(user.getUid());
        userSignInfoResponse.setIsYesterdaySign(isYesterdaySign);
        if (!isYesterdaySign) {
            // 今天是否签到
            if (!isCheckNowDaySign) {
                userSignInfoResponse.setSignNum(0);
            }
        }

        // 连续签到天数：当前用户已经签到完一个周期，那么重置
        if (userSignInfoResponse.getSignNum() > 0 &&  userSignInfoResponse.getSignNum().equals(getSignConfig().size())) {
            userSignInfoResponse.setSignNum(0);
            userService.repeatSignNum(user.getUid());
        }

        userSignInfoResponse.setIntegral(user.getIntegral());
        return userSignInfoResponse;
    }

    /**
     * 检测今天是否签到
     *
     * @param userId Integer 用户id
     * @return UserSignInfoResponse
     * @author Mr.Zhang
     * @since 2020-05-29
     */
    private Boolean checkDaySign(Integer userId) {
        List<UserSign> userSignList = getInfoByDay(userId, DateUtil.nowDate(Constants.DATE_FORMAT_DATE));
        return userSignList.size() != 0;
    }

    /**
     * 检测昨天天是否签到
     *
     * @param userId Integer 用户id
     * @return UserSignInfoResponse
     * @author Mr.Zhang
     * @since 2020-05-29
     */
    private Boolean checkYesterdaySign(Integer userId) {
        String day = DateUtil.nowDate(Constants.DATE_FORMAT_DATE);
        String yesterday = DateUtil.addDay(day, -1, Constants.DATE_FORMAT_DATE);
        List<UserSign> userSignList = getInfoByDay(userId, yesterday);
        return userSignList.size() != 0;
    }

    /**
     * 根据日期查询数据
     *
     * @param userId Integer 用户id
     * @param date   Date 日期
     * @return UserSignInfoResponse
     * @author Mr.Zhang
     * @since 2020-05-29
     */
    private List<UserSign> getInfoByDay(Integer userId, String date) {
        LambdaQueryWrapper<UserSign> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSign::getUid, userId).eq(UserSign::getType, 1).eq(UserSign::getCreateDay, date);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 累计签到次数
     *
     * @param userId Integer 用户id
     * @return UserSignInfoResponse
     * @author Mr.Zhang
     * @since 2020-05-29
     */
    private Integer getCount(Integer userId) {
        LambdaQueryWrapper<UserSign> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSign::getUid, userId).eq(UserSign::getType, 1);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 获取签到的最后一条记录
     * @param uid 用户id
     * @return UserSign
     */
    private UserSign getLastDayByUid(Integer uid) {
        LambdaQueryWrapper<UserSign> lqw = Wrappers.lambdaQuery();
        lqw.select(UserSign::getCreateDay);
        lqw.eq(UserSign::getUid, uid);
        lqw.orderByDesc(UserSign::getCreateDay);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 累计签到天数
     *
     * @param userId Integer 用户id
     * @return Integer
     * @author Mr.Zhang
     * @since 2020-04-30
     */
    private Integer signCount(Integer userId) {
        LambdaQueryWrapper<UserSign> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSign::getUid, userId);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 条件获取列表
     *
     * @param sign sign
     * @param pageParamRequest 分页参数
     * @return List<UserSign>
     */
    @Override
    public List<UserSign> getListByCondition(UserSign sign, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserSign> lqw = new LambdaQueryWrapper<>();
        lqw.setEntity(sign);
        lqw.orderByDesc(UserSign::getCreateTime);
        return dao.selectList(lqw);
    }
}

