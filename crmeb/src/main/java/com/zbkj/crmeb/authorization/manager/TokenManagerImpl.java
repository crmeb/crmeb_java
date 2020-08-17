package com.zbkj.crmeb.authorization.manager;

import com.common.CommonResult;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.RedisUtil;
import com.utils.ThreadLocalUtil;
import com.zbkj.crmeb.authorization.model.TokenModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author stivepeim
 * @title: TokenManagerImpl
 * @projectName crmeb
 * @Description: 口令管理
 * @since 2020/4/1415:29
 */
@Component
public class TokenManagerImpl implements TokenManager {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    protected RedisUtil redisUtil;

    /**
     * 生成Token
     * @param account String 账号
     * @param value String 存储value
     * @param modelName String 模块
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    @Override
    public TokenModel createToken(String account, String value, String modelName) {
        String _token = UUID.randomUUID().toString().replace("-", "");
        TokenModel token = new TokenModel(account, _token);
        token.setUserNo(account);
        String clientType = request.getParameter("clienttype");
        token.setClienttype(clientType == null ? "Web" : clientType);
        token.setHost(request.getRemoteHost());
        token.setLastAccessedTime(System.currentTimeMillis());

        redisUtil.set(modelName + _token, value,
                Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);

        Map<String, Object> hashedMap = new HashMap<>();
        hashedMap.put(modelName, value);
        ThreadLocalUtil.set(hashedMap);
        return token;
    }

    /**
     * 获取本地存储的实际
     * @param key String 模块
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    @Override
    public String getLocalInfoException(String key) {
        Object value = ThreadLocalUtil.get(key);
        if(value == null){
            throw new CrmebException("登录信息已过期，请重新登录！");
        }
        return value.toString();
    }

    /**
     * 获取本地存储的实际
     * @param key String 模块
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    @Override
    public Object getLocalInfo(String key) {
        if(StringUtils.isNotBlank(key)){
            return ThreadLocalUtil.get(key);
        }
        return null;
    }

    /**
     * 获取用户id
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    @Override
    public Integer getLocalUserId() {
        return Integer.parseInt(getLocalInfoException("id"));
    }

    /**
     * 检测Token
     * @param token String token
     * @param modelName String 模块
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    @Override
    public boolean checkToken(String token, String modelName) {
        return redisUtil.exists(modelName + token);
    }

    /**
     * 检测Token
     * @param token String token
     * @param modelName String 模块
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    @Override
    public TokenModel getToken(String token, String modelName) {
         Object o = redisUtil.get(modelName + token);
            TokenModel tokenModel = new TokenModel();
//            tokenModel.setUserNo(o.toString());
            tokenModel.setUserId(Integer.parseInt(o.toString()));
        return tokenModel;
    }


    @Override
    public String getCurrentClienttype(String userno) {
        return null;
    }

    /**
     * 删除Token
     * @param token String token
     * @param modelName String 模块
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    @Override
    public void deleteToken(String token, String modelName) {
        redisUtil.remove(modelName +token);
    }

    @Override
    public Integer getUserCount() {
        return null;
    }

    @Override
    public CommonResult<TokenModel> getOnlineUsers(Integer pageNo, Integer pageSize) {
        return null;
    }

    @Override
    public TokenModel getRealToken(String userno) {
        return null;
    }

}
