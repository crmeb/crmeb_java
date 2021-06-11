package com.zbkj.crmeb.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.user.model.UserToken;

import java.util.List;

/**
 * UserTokenService 接口实现
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
public interface UserTokenService extends IService<UserToken> {

    UserToken getByOpenidAndType(String token, int type);

    void bind(String openId, int type, Integer uid);

    UserToken getTokenByUserId(Integer userId, int type);

    List<UserToken> getList(List<Integer> userIdList);

    UserToken getByUid(Integer uid);
}