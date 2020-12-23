package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.model.SystemCity;
import com.zbkj.crmeb.system.request.SystemCityRequest;
import com.zbkj.crmeb.system.request.SystemCitySearchRequest;

import java.util.List;

/**
 * SystemCityService 接口
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
public interface SystemCityService extends IService<SystemCity> {

    Object getList(SystemCitySearchRequest request);

    boolean updateStatus(Integer id, Boolean status);

    boolean update(Integer id, SystemCityRequest request);

    Object getListTree();

    String getStringNameInId(String cityIdList);

    List<Integer> getCityIdList();

    SystemCity getCityByCityId(Integer cityId);
}