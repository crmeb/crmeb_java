package com.zbkj.crmeb.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.crmeb.front.response.UserSpreadPeopleItemResponse;
import com.zbkj.crmeb.user.model.User;
import com.zbkj.crmeb.user.vo.UserOperateFundsVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-10
 */

public interface UserDao extends BaseMapper<User> {
    Boolean updateFounds(UserOperateFundsVo userOperateFundsVo);

    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(Map<String, Object> map);
}
