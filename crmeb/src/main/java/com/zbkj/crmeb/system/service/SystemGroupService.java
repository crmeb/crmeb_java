package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.model.SystemGroupData;
import com.zbkj.crmeb.system.request.SystemGroupSearchRequest;

import java.util.List;

/**
 * SystemGroupService 接口
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
public interface SystemGroupService extends IService<SystemGroup> {

    List<SystemGroup> getList(SystemGroupSearchRequest request, PageParamRequest pageParamRequest);
}
