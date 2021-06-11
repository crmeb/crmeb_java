package com.zbkj.crmeb.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.front.request.CartNumRequest;
import com.zbkj.crmeb.front.request.CartResetRequest;
import com.zbkj.crmeb.front.response.CartInfoResponse;
import com.zbkj.crmeb.store.model.StoreCart;
import com.zbkj.crmeb.store.response.StoreCartResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * StoreCartService 接口
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
public interface StoreCartService extends IService<StoreCart> {

    /**
     * 根据有效标识符获取出数据
     * @param pageParamRequest 分页参数
     * @param isValid 是否失效
     * @return 购物车列表
     */
    PageInfo<CartInfoResponse> getList(PageParamRequest pageParamRequest, boolean isValid);

    /**
     * 根据用户id和购物车ids查询购物车集合
     * @param userId 用户id
     * @param cartIds 购物车id集合
     * @return 购物车列表
     */
    List<StoreCartResponse> getListByUserIdAndCartIds(Integer userId, List<String> cartIds,Boolean isNew);

    /**
     * 根据用户id和购物车id集合获取列表
     * @param userId 用户id
     * @param cartIds 购物车id集合
     * @return 购物车列表
     */
    List<StoreCart> getList(Integer userId, List<Integer> cartIds);

    /**
     * 购物车基本获取
     * @param storeCart 购物车参数
     * @return 购物车数据
     */
    List<StoreCart> getByEntity(StoreCart storeCart);

    /**
     * 获取当前购物车数量
     * @param request 请求参数
     * @return 数量
     */
    Map<String, Integer> getUserCount(CartNumRequest request);

    /**
     * 新增购物车数据
     * @param storeCart 新增购物车参数
     * @return 新增结果
     */
    String saveCate(StoreCart storeCart);

    /**
     * 设置会员价
     * @param price 原来价格
     * @param userId 会员id
     * @param isSingle 是否普通用户，true普通用户，false会员
     * @return BigDecimal
     */
    BigDecimal setVipPrice(BigDecimal price,Integer userId,boolean isSingle);

    /**
     * 删除购物车
     * @param ids 待删除id
     * @return 返回删除状态
     */
    boolean deleteCartByIds(List<Long> ids);


    /**
     * 检测商品是否有效 更新购物车商品状态
     * @param productId 商品id
     * @return 跟新结果
     */
    Boolean productStatusNotEnable(Integer productId);

    /**
     * 购物车重选提交
     * @param resetRequest 重选数据
     * @return 提交结果
     */
    boolean resetCart(CartResetRequest resetRequest);

    /**
     * 对应sku购物车生效
     * @param skuIdList skuIdList
     */
    Boolean productStatusNoEnable(List<Integer> skuIdList);

    /**
     * 删除商品对应的购物车
     * @param productId 商品id
     */
    Boolean productDelete(Integer productId);

    /**
     * 通过id和uid获取购物车信息
     * @param id 购物车id
     * @param uid 用户uid
     * @return StoreCart
     */
    StoreCart getByIdAndUid(Long id, Integer uid);
}
