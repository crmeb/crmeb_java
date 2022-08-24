package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.product.StoreProductRelation;
import com.zbkj.common.response.UserRelationResponse;

import java.util.List;

/**
 * 商品点赞和收藏表 Mapper 接口
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
public interface StoreProductRelationDao extends BaseMapper<StoreProductRelation> {

    /**
     * 用户收藏列表
     * @param uid 用户uid
     */
    List<UserRelationResponse> getUserList(Integer uid);
}
