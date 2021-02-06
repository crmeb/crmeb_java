package com.zbkj.crmeb.authorization.model;

import com.utils.CrmebUtil;
import com.zbkj.crmeb.system.model.SystemAdmin;
import lombok.Data;

import java.io.Serializable;
import java.util.Locale;

/**
 * token实体类
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
@Data
public class TokenModel implements Serializable {
    private static final long serialVersionUID = 4903514237492573024L;
    // 加密后的token key
    public static final String TOKEN_KEY = "TOKEN";
    // Redis 存储的key
    public static final String TOKEN_REDIS = "TOKEN:ADMIN:";
    // 用户号
    private String userNo;
    private Integer userId;
    private String token;
    // 最后访问时间
    private long lastAccessedTime = System.currentTimeMillis();
    // 过期时间
    private long expirationTime;
    // 客户端类型
    private String clienttype;
    // 客户端语言
    private Locale locale;
    // 客户端ip
    private String host;
    // 当前登录用户信息
    private SystemAdmin systemAdmin;

    public String getAuthorization() throws Exception {
        return CrmebUtil.encryptPassword(userNo+"_"+ token, TOKEN_KEY);
    }

    public TokenModel(String userno, String token){
        this.userNo = userno;
        this.token = token;
    }

    public TokenModel() {
    }
}
