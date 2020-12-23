package com.zbkj.crmeb.system.dao;

import com.zbkj.crmeb.front.request.StoreNearRequest;
import com.zbkj.crmeb.system.model.SystemStore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.crmeb.system.vo.SystemStoreNearVo;

import java.util.List;

/**
 * 门店自提 Mapper 接口
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
public interface SystemStoreDao extends BaseMapper<SystemStore> {

    List<SystemStoreNearVo> getNearList(StoreNearRequest request);
}

