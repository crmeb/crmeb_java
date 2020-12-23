package com.zbkj.crmeb.authorization.manager;

import com.common.CommonResult;
import com.zbkj.crmeb.authorization.model.TokenModel;

/**
 * TokenManager
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public interface TokenManager {

    TokenModel createToken(String account, String value, String modelName) throws Exception;

    boolean checkToken(String token, String modelName);

    TokenModel getToken(String token, String modelName);

    String getCurrentClienttype(String userno);

    void deleteToken(String token, String modelName);

    Integer getUserCount();

    CommonResult<TokenModel> getOnlineUsers(Integer pageNo, Integer pageSize);

    TokenModel getRealToken(String userno);

    String getLocalInfoException(String key);

    Object getLocalInfo(String key);

    Integer getLocalUserId();
}
