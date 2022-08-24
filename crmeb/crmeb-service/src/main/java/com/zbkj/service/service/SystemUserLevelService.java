package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.system.SystemUserLevel;
import com.zbkj.common.request.SystemUserLevelRequest;
import com.zbkj.common.request.SystemUserLevelUpdateShowRequest;

import java.util.List;

/**
 * SystemUserLevelService 接口
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
public interface SystemUserLevelService extends IService<SystemUserLevel> {

    /**
     * 获取等级列表
     */
    List<SystemUserLevel> getList();

    /**
     * 系统等级新增
     * @param request request
     * @return Boolean
     */
    Boolean create(SystemUserLevelRequest request);

    /**
     * 系统等级更新
     * @param id    等级id
     * @param request   等级数据
     * @return Boolean
     */
    Boolean update(Integer id, SystemUserLevelRequest request);

    SystemUserLevel getByLevelId(Integer levelId);

    /**
     * 获取系统等级列表（移动端）
     */
    List<SystemUserLevel> getH5LevelList();

    /**
     * 删除系统等级
     * @param id 等级id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 使用/禁用
     * @param request request
     */
    Boolean updateShow(SystemUserLevelUpdateShowRequest request);

    /**
     * 获取可用等级列表
     * @return List
     */
    List<SystemUserLevel> getUsableList();
}
