package com.zbkj.crmeb.marketing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.front.request.UserCouponReceiveRequest;
import com.zbkj.crmeb.marketing.model.StoreCouponUser;
import com.zbkj.crmeb.marketing.request.StoreCouponUserRequest;
import com.zbkj.crmeb.marketing.request.StoreCouponUserSearchRequest;
import com.zbkj.crmeb.marketing.response.StoreCouponUserOrder;
import com.zbkj.crmeb.marketing.response.StoreCouponUserResponse;
import com.zbkj.crmeb.store.model.StoreOrder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * StoreCouponUserService 接口
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
public interface StoreCouponUserService extends IService<StoreCouponUser> {

    PageInfo<StoreCouponUserResponse> getList(StoreCouponUserSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 基本条件查询
     * @param storeCouponUser 基本参数
     * @return 查询的优惠券结果
     */
    List<StoreCouponUser> getList(StoreCouponUser storeCouponUser);

    boolean receive(StoreCouponUserRequest storeCouponUserRequest);

    boolean use(Integer id, List<Integer> productIdList, BigDecimal price);

    /**
     * 检测优惠券是否可用，计算订单价格时使用
     * @param id            优惠券id
     * @param productIdList 商品id集合
     * @param price 价格
     * @return  可用状态
     */
    boolean canUse(Integer id, List<Integer> productIdList, BigDecimal price);

    boolean receiveAll(UserCouponReceiveRequest request, Integer userId, String type);

    boolean rollbackByCancelOrder(StoreOrder storeOrder);

    HashMap<Integer, StoreCouponUser> getMapByUserId(Integer userId);

    /**
     * 根据购物车id获取可用优惠券
     * @param cartIds 购物车id
     * @return 可用优惠券集合
     */
    List<StoreCouponUserOrder> getListByCartIds(List<Integer> cartIds);

    List<StoreCouponUserResponse> getListFront(Integer userId, PageParamRequest pageParamRequest);
}
