package com.zbkj.crmeb.validatecode.service;

import com.zbkj.crmeb.validatecode.model.ValidateCode;

import java.util.HashMap;

/**
 * ValidateCodeService 接口
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
public interface ValidateCodeService {

    ValidateCode get();

    boolean check(ValidateCode validateCode);
}