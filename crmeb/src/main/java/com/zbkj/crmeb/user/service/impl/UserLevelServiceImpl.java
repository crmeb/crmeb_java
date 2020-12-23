package com.zbkj.crmeb.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;

import com.utils.DateUtil;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.system.service.SystemUserLevelService;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserLevel;
import com.zbkj.crmeb.user.dao.UserLevelDao;
import com.zbkj.crmeb.user.request.UserLevelSearchRequest;
import com.zbkj.crmeb.user.service.UserLevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * UserLevelServiceImpl 接口实现
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
public class UserLevelServiceImpl extends ServiceImpl<UserLevelDao, UserLevel> implements UserLevelService {

    @Resource
    private UserLevelDao dao;

    @Autowired
    private SystemUserLevelService systemUserLevelService;

    @Autowired
    private UserService userService;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-04-28
    * @return List<UserLevel>
    */
    @Override
    public List<UserLevel> getList(UserLevelSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

    /**
     * 修改用户等级
     * @param userId integer id
     * @param levelId integer 等级
     * @author Mr.Zhang
     * @since 2020-04-10
     * @return Boolean
     */
    @Override
    public boolean level(Integer userId, int levelId) {
        SystemUserLevel systemUserLevel = systemUserLevelService.getById(levelId);
        User user = userService.getById(userId);
        UserLevel userLevelVo = checkUserLevel(userId, levelId, systemUserLevel);

        UserLevel userLevel = new UserLevel();
        userLevel.setStatus(true);
        userLevel.setIsDel(false);
        userLevel.setGrade(systemUserLevel.getGrade());
        userLevel.setUid(userId);
        userLevel.setLevelId(levelId);
        userLevel.setDiscount(systemUserLevel.getDiscount());

        Date date = DateUtil.nowDateTimeReturnDate(Constants.DATE_FORMAT);
        String mark = Constants.USER_LEVEL_OPERATE_LOG_MARK.replace("【{$userName}】", user.getNickname()).
                replace("{$date}", DateUtil.dateToStr(date, Constants.DATE_FORMAT)).
                replace("{$levelName}", systemUserLevel.getName());
        userLevel.setMark(mark);

        if(userLevelVo == null){
            //创建新的会员等级信息
            save(userLevel);
        }else{
            //有数据，更新即可
            userLevel.setId(userLevelVo.getId());
            updateById(userLevel);
        }

        //更新会员等级
        user.setLevel(systemUserLevel.getGrade());
        userService.updateById(user);
        return true;
    }


    /**
     * 获取会员的等级信息，并且验证
     * @param userId integer id
     * @param levelId integer 等级
     * @author Mr.Zhang
     * @since 2020-04-10
     * @return Boolean
     */
    private UserLevel checkUserLevel(Integer userId, Integer levelId, SystemUserLevel systemUserLevel) {
        //查询等级是否存在
        if(systemUserLevel == null){
            throw new CrmebException("当前会员等级不存在");
        }

        if(systemUserLevel.getIsDel()){
            throw new CrmebException("当前会员等级已删除");
        }

        //当前用户会员是否过期
        return getUserLevel(userId, levelId);
    }

    /**
     * 获取会员的等级信息
     * @param userId integer id
     * @author Mr.Zhang
     * @since 2020-04-10
     * @return Boolean
     */
    private UserLevel getUserLevel(Integer userId, Integer levelId) {
        LambdaQueryWrapper<UserLevel> levelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        levelLambdaQueryWrapper.eq(UserLevel::getUid, userId);
        levelLambdaQueryWrapper.eq(UserLevel::getLevelId, levelId);
        levelLambdaQueryWrapper.eq(UserLevel::getIsDel, 0);
        return dao.selectOne(levelLambdaQueryWrapper);
    }

    /**
     * 根据用户id获取用户等级信息
     * @param userId 用户id
     * @return 用户等级
     */
    @Override
    public UserLevel getUserLevelByUserId(Integer userId) {
        LambdaQueryWrapper<UserLevel> levelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        levelLambdaQueryWrapper.eq(UserLevel::getUid, userId);
        levelLambdaQueryWrapper.eq(UserLevel::getIsDel, 0);
        return dao.selectOne(levelLambdaQueryWrapper);
    }
}

