package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.constants.Constants;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserLevel;
import com.zbkj.service.dao.UserLevelDao;
import com.zbkj.service.service.SystemUserLevelService;
import com.zbkj.service.service.UserLevelService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserLevelServiceImpl 接口实现
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
public class UserLevelServiceImpl extends ServiceImpl<UserLevelDao, UserLevel> implements UserLevelService {

    @Resource
    private UserLevelDao dao;

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;


    /**
    * 列表
    * @param pageParamRequest 分页类参数
    * @return List<UserLevel>
    */
    @Override
    public List<UserLevel> getList(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

    /**
     * 用户升级
     * @param user 用户数据
     * @return Boolean
     */
    @Override
    public Boolean upLevel(User user) {
        //确定当前经验所达到的等级
        List<SystemUserLevel> list = systemUserLevelService.getUsableList();
        if (CollUtil.isEmpty(list)) {
            log.error("系统会员等级未配置，请配置对应数据");
            return Boolean.TRUE;
        }

        SystemUserLevel userLevelConfig = null;
        for (SystemUserLevel systemUserLevel : list) {
            if(user.getExperience() > systemUserLevel.getExperience()){
                userLevelConfig = systemUserLevel;
                continue;
            }
            break;
        }

        if(ObjectUtil.isNull(userLevelConfig)) {
            log.warn("未找到用户对应的会员等级,uid = " + user.getUid());
            return Boolean.TRUE;
        }

        // 判断用户是否还在原等级
        if (user.getLevel().equals(userLevelConfig.getId())) {
            return Boolean.TRUE;
        }

        // 判断用户等级经过向上调整
        List<SystemUserLevel> collect = list.stream().filter(e -> e.getId().equals(user.getLevel())).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(collect)) {
            if (collect.get(0).getGrade() > userLevelConfig.getGrade()) {
                return Boolean.TRUE;
            }
        }
        UserLevel newLevel = new UserLevel();
        newLevel.setStatus(true);
        newLevel.setIsDel(false);
        newLevel.setGrade(userLevelConfig.getGrade());
        newLevel.setUid(user.getUid());
        newLevel.setLevelId(userLevelConfig.getId());
        newLevel.setDiscount(userLevelConfig.getDiscount());

        Date date = DateUtil.nowDateTimeReturnDate(Constants.DATE_FORMAT);
        String mark = Constants.USER_LEVEL_UP_LOG_MARK.replace("【{$userName}】", user.getNickname()).
                replace("{$date}", DateUtil.dateToStr(date, Constants.DATE_FORMAT)).
                replace("{$levelName}", userLevelConfig.getName());
        newLevel.setMark(mark);

        //更新会员等级
        user.setLevel(userLevelConfig.getId());
        return transactionTemplate.execute(e -> {
            save(newLevel);
            userService.updateById(user);
            return Boolean.TRUE;
        });
    }

    /**
     * 经验降级
     * @param user 用户
     * @return Boolean
     */
    @Override
    public Boolean downLevel(User user) {
        //确定当前经验所达到的等级
        List<SystemUserLevel> list = systemUserLevelService.getUsableList();
        if (CollUtil.isEmpty(list)) {
            log.error("系统会员等级未配置，请配置对应数据");
            return Boolean.TRUE;
        }

        SystemUserLevel userLevelConfig = null;
        for (SystemUserLevel systemUserLevel : list) {
            if(user.getExperience() > systemUserLevel.getExperience()){
                userLevelConfig = systemUserLevel;
                continue;
            }
            break;
        }

        if(ObjectUtil.isNull(userLevelConfig)) {
            log.warn("未找到用户对应的会员等级,uid = " + user.getUid());
            return Boolean.TRUE;
        }

        // 判断用户是否还在原等级
        if (user.getLevel().equals(userLevelConfig.getId())) {
            return Boolean.TRUE;
        }

        UserLevel newLevel = new UserLevel();
        newLevel.setStatus(true);
        newLevel.setIsDel(false);
        newLevel.setGrade(userLevelConfig.getGrade());
        newLevel.setUid(user.getUid());
        newLevel.setLevelId(userLevelConfig.getId());
        newLevel.setDiscount(userLevelConfig.getDiscount());

        Date date = DateUtil.nowDateTimeReturnDate(Constants.DATE_FORMAT);
        String mark = Constants.USER_LEVEL_OPERATE_LOG_MARK.replace("【{$userName}】", user.getNickname()).
                replace("{$date}", DateUtil.dateToStr(date, Constants.DATE_FORMAT)).
                replace("{$levelName}", userLevelConfig.getName());
        newLevel.setMark(mark);

        //更新会员等级
        user.setLevel(userLevelConfig.getId());
        return transactionTemplate.execute(e -> {
            save(newLevel);
            userService.updateById(user);
            return Boolean.TRUE;
        });
    }

    /**
     * 删除（通过系统等级id）
     * @param levelId 系统等级id
     * @return Boolean
     */
    @Override
    public Boolean deleteByLevelId(Integer levelId) {
        LambdaUpdateWrapper<UserLevel> luw = Wrappers.lambdaUpdate();
        luw.set(UserLevel::getIsDel, true);
        luw.eq(UserLevel::getLevelId, levelId);
        luw.eq(UserLevel::getIsDel, false);
        return update(luw);
    }
}

