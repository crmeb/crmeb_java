package com.common;

import com.constants.Constants;
import com.utils.RedisUtil;
import com.utils.ThreadLocalUtil;
import com.zbkj.crmeb.authorization.model.TokenModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 检测token是否过期
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
public class CheckAdminToken {


    @Autowired
    protected RedisUtil redisUtil;

    public Boolean check(String token){

        try {
            boolean exists = redisUtil.exists(TokenModel.TOKEN_REDIS + token);
            if(exists){
                Object value = redisUtil.get(TokenModel.TOKEN_REDIS + token);

                Map<String, Object> hashedMap = new HashMap<>();
                hashedMap.put("id", value);
                ThreadLocalUtil.set(hashedMap);

                redisUtil.set(TokenModel.TOKEN_REDIS +token, value, Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
            }
            return exists;
        }catch (Exception e){
            return false;
        }
    }

    public String getTokenFormRequest(HttpServletRequest request){
        String pathToken =request.getParameter(Constants.HEADER_AUTHORIZATION_KEY);
        if(null != pathToken){
            return pathToken;
        }
        return request.getHeader(Constants.HEADER_AUTHORIZATION_KEY);
    }

    public static String st = "ags0o175LNCnToaXF9EaLdQ";
    public static String sk = "p&va7ylslUKwgx1vm8）L";
}
