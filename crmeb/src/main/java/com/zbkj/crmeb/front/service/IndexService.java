package com.zbkj.crmeb.front.service;

import com.common.CommonPage;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.response.IndexInfoResponse;
import com.zbkj.crmeb.front.response.IndexProductBannerResponse;
import com.zbkj.crmeb.front.response.IndexProductResponse;

import java.util.HashMap;
import java.util.List;

/**
* IndexService 接口
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
public interface IndexService{

    IndexProductBannerResponse getProductBanner(int type, PageParamRequest pageParamRequest);

    /**
     * 首页信息
     * @return IndexInfoResponse
     */
    IndexInfoResponse getIndexInfo();

    List<HashMap<String, Object>> hotKeywords();

    HashMap<String, String> getShareConfig();

    /**
     * 获取首页商品列表
     * @param type 类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】
     * @param pageParamRequest 分页参数
     * @return List
     */
    CommonPage<IndexProductResponse> findIndexProductList(Integer type, PageParamRequest pageParamRequest);
}
