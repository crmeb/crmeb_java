package com.zbkj.crmeb.store.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.crmeb.front.response.UserRelationResponse;
import com.zbkj.crmeb.store.model.StoreProductRelation;
import com.zbkj.crmeb.store.vo.StoreProductRelationCountVo;

import java.util.HashMap;
import java.util.List;

/**
 * 商品点赞和收藏表 Mapper 接口
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
public interface StoreProductRelationDao extends BaseMapper<StoreProductRelation> {

    List<StoreProductRelationCountVo> getCountInProductId(HashMap<String, Object> objectObjectHashMap);

    List<UserRelationResponse> getUserList(Integer uid);
}
