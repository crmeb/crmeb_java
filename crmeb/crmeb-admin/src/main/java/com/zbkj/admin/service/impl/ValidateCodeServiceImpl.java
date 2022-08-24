package com.zbkj.admin.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.ValidateCodeUtil;
import com.zbkj.admin.service.ValidateCodeService;
import com.zbkj.admin.vo.ValidateCode;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * ValidateCodeService 实现类
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
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {

    @Resource
    private RedisUtil redisAdminUtil;

    /**
     * 获取验证码信息
     */
    @Override
    public ValidateCode get() {
        ValidateCodeUtil.Validate randomCode = ValidateCodeUtil.getRandomCode();//直接调用静态方法，返回验证码对象
        if (ObjectUtil.isNull(randomCode)) {
            return null;
        }

        String value = randomCode.getValue().toLowerCase();
        String md5Key = DigestUtils.md5Hex(value);
        String redisKey = getRedisKey(md5Key);
        redisAdminUtil.set(redisKey, value, 5L, TimeUnit.MINUTES);   //5分钟过期
        String base64Str = randomCode.getBase64Str();
        return new ValidateCode(md5Key, CrmebUtil.getBase64Image(base64Str));
    }

    /**
     * 获取redis key
     * @param md5Key value的md5加密值
     */
    public String getRedisKey(String md5Key) {
        return Constants.VALIDATE_REDIS_KEY_PREFIX + md5Key;
    }

    /**
     * 验证
     */
    public Boolean check(String key, String code) {
        if (!redisAdminUtil.exists(getRedisKey(key))) {
            throw new CrmebException("验证码错误");
        }
        Object redisValue = redisAdminUtil.get(getRedisKey(key));
        if (ObjectUtil.isNull(redisValue)) {
            return false;
        }
        return redisValue.equals(code.toLowerCase());
    }
}

