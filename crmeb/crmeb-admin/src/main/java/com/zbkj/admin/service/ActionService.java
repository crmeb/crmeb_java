package com.zbkj.admin.service;

import com.zbkj.common.model.log.SensitiveMethodLog;

/**
 * 行为service
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public interface ActionService {

    /**
     * 添加敏感记录
     * @param methodLog 记录信息
     */
    void addSensitiveLog(SensitiveMethodLog methodLog);
}
