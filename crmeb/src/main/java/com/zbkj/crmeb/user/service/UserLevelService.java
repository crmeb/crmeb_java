package com.zbkj.crmeb.user.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.model.UserLevel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.user.request.UserLevelSearchRequest;

import java.util.List;

/**
 * UserLevelService 接口实现
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
public interface UserLevelService extends IService<UserLevel> {

    List<UserLevel> getList(UserLevelSearchRequest request, PageParamRequest pageParamRequest);

    boolean level(Integer userId, int levelId);


    /**
     * 根据用户id获取用户等级
     * @param userId 用户id
     * @return 用户等级
     */
    UserLevel getUserLevelByUserId(Integer userId);

    /**
     * 经验升级
     * @param user
     * @return
     */
    Boolean upLevel(User user);
}