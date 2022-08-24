package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.user.UserToken;

/**
 * UserTokenService 接口实现
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
public interface UserTokenService extends IService<UserToken> {

    /**
     * 获取UserToken
     * @param token 微信为openid
     * @param type 类型
     * @return UserToken
     */
    UserToken getByOpenidAndType(String token, int type);

    void bind(String openId, int type, Integer uid);

    UserToken getTokenByUserId(Integer userId, int type);

}
