package com.zbkj.crmeb.system.dao;

import com.zbkj.crmeb.front.request.StoreNearRequest;
import com.zbkj.crmeb.system.model.SystemStore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.crmeb.system.vo.SystemStoreNearVo;

import java.util.List;

/**
 * <p>
 * 门店自提 Mapper 接口
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-13
 */
public interface SystemStoreDao extends BaseMapper<SystemStore> {

    List<SystemStoreNearVo> getNearList(StoreNearRequest request);
}

