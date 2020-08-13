package com.zbkj.crmeb.authorization.manager;

import com.common.CommonResult;
import com.zbkj.crmeb.authorization.model.TokenModel;

/**
 * @author stivepeim
 * @title: TokenManager
 * @projectName crmeb
 * @Description: TODO
 * @since 2020/4/1415:27
 */
public interface TokenManager {

    TokenModel createToken(String account, String value, String modelName);

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