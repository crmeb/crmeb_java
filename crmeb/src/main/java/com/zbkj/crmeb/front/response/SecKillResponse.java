package com.zbkj.crmeb.front.response;

import lombok.Data;

/**
 *  秒杀Header
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
@Data
public class SecKillResponse {

    public SecKillResponse() {
    }

    public SecKillResponse(Integer id, String slide, String statusName, String time, int status, String timeSwap) {
        this.id = id;
        this.slide = slide;
        this.statusName = statusName;
        this.time = time;
        this.status = status;
        this.timeSwap = timeSwap;
    }

    private Integer id;
    private String slide;
    private String statusName; // 已结束 抢购中 即将开始
    private int status;
    private String time;
    private String timeSwap;
}
