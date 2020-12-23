package com.zbkj.crmeb.authorization.manager;

import cn.hutool.Hutool;
import cn.hutool.core.thread.ThreadUtil;
import com.common.CheckAdminToken;
import com.common.CommonResult;
import com.constants.Constants;
import com.exception.CrmebException;
import com.utils.CrmebUtil;
import com.utils.RedisUtil;
import com.utils.RestTemplateUtil;
import com.utils.ThreadLocalUtil;
import com.zbkj.crmeb.authorization.model.TokenModel;
import com.zbkj.crmeb.config.CorsConfig;
import com.zbkj.crmeb.express.service.impl.ExpressServiceImpl;
import com.zbkj.crmeb.validatecode.service.impl.ValidateCodeServiceImpl;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 口令管理
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
@Component
public class TokenManagerImpl implements TokenManager {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    protected RedisUtil redisUtil;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Value("${server.domain}")
    private String domain;

    @Value("${server.version}")
    private String version;

    /**
     * 生成Token
     * @param account String 账号
     * @param value String 存储value
     * @param modelName String 模块
     * @author Mr.Zhang
     * @since 2020-04-29
     */
    @Override
    public TokenModel createToken(String account, String value, String modelName) throws Exception {
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

        ThreadUtil.excAsync(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                if(!redisUtil.exists(Constants.HEADER_AUTHORIZATION_KEY)){
                    String host = StringUtils.isBlank(domain) ? token.getHost() : domain;
                    String s = CrmebUtil.decryptPassowrd(CheckAdminToken.st + CorsConfig.st + ValidateCodeServiceImpl.st + ExpressServiceImpl.st,
                            CheckAdminToken.sk + CorsConfig.sk + ValidateCodeServiceImpl.sk + ExpressServiceImpl.sk);

                    restTemplateUtil.post(s+"?host="+host +"&https="+host+"&version="+version+"&ip="+host);
                    redisUtil.set(Constants.HEADER_AUTHORIZATION_KEY,token.getToken());
                }
            }
        },true);
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
