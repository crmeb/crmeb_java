package com.zbkj.crmeb.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.crmeb.front.response.UserSpreadPeopleItemResponse;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.vo.UserOperateFundsVo;

import java.util.List;
import java.util.Map;

/**
 * 用户表 Mapper 接口
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
public interface UserDao extends BaseMapper<User> {
    Boolean updateFounds(UserOperateFundsVo userOperateFundsVo);

    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(Map<String, Object> map);
}
