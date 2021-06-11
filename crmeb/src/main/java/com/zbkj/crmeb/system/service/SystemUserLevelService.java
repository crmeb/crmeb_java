package com.zbkj.crmeb.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemUserLevel;
import com.zbkj.crmeb.system.request.SystemUserLevelRequest;
import com.zbkj.crmeb.system.request.SystemUserLevelSearchRequest;

import java.util.List;

/**
 * SystemUserLevelService 接口
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
public interface SystemUserLevelService extends IService<SystemUserLevel> {

    List<SystemUserLevel> getList(SystemUserLevelSearchRequest request, PageParamRequest pageParamRequest);

    boolean create(SystemUserLevelRequest request);

    boolean update(Integer id, SystemUserLevelRequest request);

    SystemUserLevel getByLevelId(Integer levelId);

    /**
     * 获取系统等级列表（移动端）
     */
    List<SystemUserLevel> getH5LevelList();
}
