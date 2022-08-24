package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserLevel;

import java.util.List;

/**
 * UserLevelService 接口实现
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
public interface UserLevelService extends IService<UserLevel> {

    /**
     * 用户等级列表
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<UserLevel> getList(PageParamRequest pageParamRequest);

    /**
     * 经验升级
     * @param user 用户
     * @return Boolean
     */
    Boolean upLevel(User user);

    /**
     * 经验降级
     * @param user 用户
     * @return Boolean
     */
    Boolean downLevel(User user);

    /**
     * 删除（通过系统等级id）
     * @param levelId 系统等级id
     * @return Boolean
     */
    Boolean deleteByLevelId(Integer levelId);

}
