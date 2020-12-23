package com.zbkj.crmeb.marketing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.marketing.model.StoreCoupon;
import com.zbkj.crmeb.marketing.request.StoreCouponRequest;
import com.zbkj.crmeb.marketing.request.StoreCouponSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreCouponFrontResponse;
import com.zbkj.crmeb.marketing.response.StoreCouponInfoResponse;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * StoreCouponService 接口
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
public interface StoreCouponService extends IService<StoreCoupon> {

    List<StoreCoupon> getList(StoreCouponSearchRequest request, PageParamRequest pageParamRequest);

    boolean create(StoreCouponRequest request);

    StoreCoupon getInfoException(Integer id);

    List<StoreCoupon> getReceiveListInId(List<Integer> couponId);

    void checkException(StoreCoupon storeCoupon);

//    List<StoreCoupon> getListByProductCanUse(Integer productId);

    StoreCouponInfoResponse info(Integer id);

    List<StoreCouponFrontResponse> getListByUser(Integer productId, PageParamRequest pageParamRequest, Integer userId);

    /**
     * 根据优惠券id获取
     * @param ids 优惠券id集合
     * @return
     */
    List<StoreCoupon> getByIds(List<Integer> ids);
}
