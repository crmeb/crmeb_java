package com.common;

import com.constants.Constants;
import com.utils.RedisUtil;
import com.utils.RequestUtil;
import com.utils.ThreadLocalUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 检测token是否过期
 * Created on 2019/11/23
 * @author zhangle
 */
@Component
public class CheckFrontToken {

    @Autowired
    protected RedisUtil redisUtil;

    public Boolean check(String token, HttpServletRequest request){

        try {
            boolean exists = redisUtil.exists(Constants.USER_TOKEN_REDIS_KEY_PREFIX + token);
            if(exists){
                Object value = redisUtil.get(Constants.USER_TOKEN_REDIS_KEY_PREFIX + token);

                Map<String, Object> hashedMap = new HashMap<>();
                hashedMap.put("id", value);
                ThreadLocalUtil.set(hashedMap);

                redisUtil.set(Constants.USER_TOKEN_REDIS_KEY_PREFIX +token, value, Constants.TOKEN_EXPRESS_MINUTES, TimeUnit.MINUTES);
            }else{
                //判断路由，部分路由不管用户是否登录/token过期都可以访问
                exists = checkRouter(RequestUtil.getUri(request));
            }


            return exists;
        }catch (Exception e){
            return false;
        }
    }

    //路由在此处，则返回true，无论用户是否登录都可以访问
    public boolean checkRouter(String uri) {
        String[] routerList = {
                "api/front/product/detail",
                "api/front/coupons",
                "api/front/index"
        };

        return ArrayUtils.contains(routerList, uri);
    }

    public String getTokenFormRequest(HttpServletRequest request){
        return request.getHeader(Constants.HEADER_AUTHORIZATION_KEY);
    }
}
