package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemGroupData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.request.SystemFormCheckRequest;
import com.zbkj.crmeb.system.request.SystemGroupDataRequest;
import com.zbkj.crmeb.system.request.SystemGroupDataSearchRequest;

import java.util.HashMap;
import java.util.List;

/**
 * SystemGroupDataService 接口
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
public interface SystemGroupDataService extends IService<SystemGroupData> {

    List<SystemGroupData> getList(SystemGroupDataSearchRequest request, PageParamRequest pageParamRequest);

    boolean create(SystemGroupDataRequest systemGroupDataRequest);

    boolean update(Integer id, SystemGroupDataRequest request);

    <T> List<T> getListByGid(Integer gid, Class<T> cls);

    List<HashMap<String, Object>> getListMapByGid(Integer gid);

    <T> T getNormalInfo(Integer groupDataId, Class<T> cls);
}