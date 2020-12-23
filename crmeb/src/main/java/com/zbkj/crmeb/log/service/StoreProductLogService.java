package com.zbkj.crmeb.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.log.model.StoreProductLog;

/**
 * StoreProductLogService 接口
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
public interface StoreProductLogService extends IService<StoreProductLog> {

//    List<StoreProductLog> getList(StoreProductLogSearchRequest request, PageParamRequest pageParamRequest);

    Integer getCountByTimeAndType(String time, String type);

    void addLogTask();
}