package com.zbkj.crmeb.user.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.user.model.UserLevel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.user.request.UserLevelSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description UserLevelService 接口
* @since 2020-04-28
*/
public interface UserLevelService extends IService<UserLevel> {

    List<UserLevel> getList(UserLevelSearchRequest request, PageParamRequest pageParamRequest);

    boolean level(Integer userId, int levelId);


    /**
     * 根据用户id获取用户等级
     * @param userId 用户id
     * @return 用户等级
     */
    UserLevel getUserLevelByUserId(Integer userId);
}