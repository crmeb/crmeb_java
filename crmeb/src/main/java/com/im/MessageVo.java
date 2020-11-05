package com.im;

import lombok.Data;

/**
 * 消息Vo对象
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
public class MessageVo {
    public MessageVo(Integer fromId, Integer toId, String message) {
        this.fromId = fromId;
        this.toId = toId;
        this.message = message;
    }

    public Integer fromId;
    public Integer toId;
    public String message;
}
