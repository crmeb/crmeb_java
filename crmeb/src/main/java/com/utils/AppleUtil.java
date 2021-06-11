package com.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import io.jsonwebtoken.*;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.security.PublicKey;

/**
 * 苹果工具类
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
public class AppleUtil {

    private static final Logger logger = LoggerFactory.getLogger(AppleUtil.class);

    /**
     * 获取苹果的公钥
     * @return
     */
    private static JSONArray getAuthKeys() {
        String url = "https://appleid.apple.com/auth/keys";
        RestTemplate restTemplate = new RestTemplate();
        JSONObject json = restTemplate.getForObject(url, JSONObject.class);
        if (ObjectUtil.isNull(json)) {
            logger.error("获取苹果的公钥失败");
        }
        JSONArray arr = json.getJSONArray("keys");
        return arr;
    }

    public static Boolean verify(String jwt) throws  Exception{
        JSONArray arr = getAuthKeys();
        if(arr == null){
            return false;
        }
        JSONObject authKey = null;

        //先取苹果第一个key进行校验
        authKey = JSONObject.parseObject(arr.getString(0));
        if(verifyExc(jwt, authKey)){
            return true;
        }else{
            //再取第二个key校验
            authKey = JSONObject.parseObject(arr.getString(1));
            return verifyExc(jwt, authKey);
        }

    }

    /**
     * 对前端传来的identityToken进行验证
     * @param jwt 对应前端传来的 identityToken
     * @param authKey 苹果的公钥 authKey
     * @return
     * @throws Exception
     */
    public static Boolean verifyExc(String jwt, JSONObject authKey) throws Exception {

        // 生成publicKey
        Jwk jwa = Jwk.fromValues(authKey);
        PublicKey publicKey = jwa.getPublicKey();
        // 分割前台传过来的identifyToken（jwt格式的token）用base64解码使用
        String aud = "";
        String sub = "";
        if (jwt.split("\\.").length > 1) {
            String claim = new String(Base64.decodeBase64(jwt.split("\\.")[1]));
            aud = JSONObject.parseObject(claim).get("aud").toString();
            sub = JSONObject.parseObject(claim).get("sub").toString();
        }
        JwtParser jwtParser = Jwts.parser().setSigningKey(publicKey);
        jwtParser.requireIssuer("https://appleid.apple.com");
        jwtParser.requireAudience(aud);
        jwtParser.requireSubject(sub);

        try {
            Jws<Claims> claim = jwtParser.parseClaimsJws(jwt);
            if (claim != null && claim.getBody().containsKey("auth_time")) {
                System.out.println(claim);
                return true;
            }
            return false;
        } catch (ExpiredJwtException e) {
            logger.error("apple identityToken expired", e);
            return false;
        } catch (Exception e) {
            logger.error("apple identityToken illegal", e);
            return false;
        }
    }

    /**
     * 对前端传来的JWT字符串identityToken的第二部分进行解码
     * 主要获取其中的aud和sub，aud大概对应ios前端的包名，sub大概对应当前用户的授权的openID
     * @param identityToken iosToken
     * @return  {"aud":"com.xkj.****","sub":"000***.8da764d3f9e34d2183e8da08a1057***.0***","c_hash":"UsKAuEoI-****","email_verified":"true","auth_time":1574673481,"iss":"https://appleid.apple.com","exp":1574674081,"iat":1574673481,"email":"****@qq.com"}
     */
    public static JSONObject parserIdentityToken(String identityToken){
        String[] arr = identityToken.split("\\.");
        String decode = new String (Base64.decodeBase64(arr[1]));
        String substring = decode.substring(0, decode.indexOf("}")+1);
        return JSON.parseObject(substring);
    }
}
