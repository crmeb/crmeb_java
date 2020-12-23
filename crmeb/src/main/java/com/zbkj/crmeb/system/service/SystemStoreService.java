package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.front.request.StoreNearRequest;
import com.zbkj.crmeb.front.response.StoreNearResponse;
import com.zbkj.crmeb.system.model.SystemStore;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.request.SystemStoreRequest;

import java.util.HashMap;
import java.util.List;

/**
 * SystemStoreService 接口
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
public interface SystemStoreService extends IService<SystemStore> {
    List<SystemStore> getList(String keywords, int status, PageParamRequest pageParamRequest);

    /**
     * 根据基本参数获取
     * @param systemStore 基本参数
     * @return 门店自提结果
     */
    SystemStore getByCondition(SystemStore systemStore);

    boolean updateStatus(Integer id, boolean status);
    boolean delete(Integer id);

    HashMap<String, Integer> getCount();

    HashMap<Integer, SystemStore> getMapInId(List<Integer> storeIdList);

    StoreNearResponse getNearList(StoreNearRequest request, PageParamRequest pageParamRequest);

    boolean create(SystemStoreRequest request);

    boolean update(Integer id, SystemStoreRequest request);

    /**
     * 彻底删除
     * @param id 提货点编号
     */
    Boolean completeLyDelete(Integer id);

    /**
     * 提货点恢复
     * @param id 提货点编号
     */
    Boolean recovery(Integer id);
}