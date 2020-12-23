package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.user.model.User;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * 砍价商品详情信息Response
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
@Data
public class BargainDetailResponse {

    /**
     * 砍价商品信息
     */
    private StoreProductResponse bargain;

    /**
     * 用户砍价状态
     */
    private Integer userBargainStatus;

    /**
     * 用户信息
     */
    private User userInfo;

    /**
     * 砍价单属性AttrValueId
     */
    private Integer aloneAttrValueId;

    /**
     * 砍价支付成功订单数量
     */
    private Integer bargainSumCount;

    public BargainDetailResponse() {}

    public BargainDetailResponse(StoreProductResponse bargain, Integer userBargainStatus, User userInfo, Integer bargainSumCount) {
        this.bargain = bargain;
        this.userBargainStatus = userBargainStatus;
        this.userInfo = userInfo;
        this.bargainSumCount = bargainSumCount;
    }

}
