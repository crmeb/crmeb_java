package com.zbkj.crmeb.combination.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.combination.model.StoreCombination;
import com.zbkj.crmeb.combination.request.StoreCombinationRequest;
import com.zbkj.crmeb.combination.request.StoreCombinationSearchRequest;
import com.zbkj.crmeb.combination.request.StorePinkRequest;
import com.zbkj.crmeb.front.response.CombinationDetailResponse;
import com.zbkj.crmeb.combination.response.StoreCombinationResponse;
import com.zbkj.crmeb.front.response.GoPinkResponse;
import com.zbkj.crmeb.store.model.StoreOrder;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StorePinkService
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
public interface StoreCombinationService extends IService<StoreCombination> {

    /**
     * 分页显示拼团商品表
     * @param request   搜索条件
     * @param pageParamRequest  分页参数
     * @return
     */
    PageInfo<StoreCombinationResponse> getList(StoreCombinationSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增拼团商品
     * @param request
     * @return
     */
    Boolean saveCombination(StoreCombinationRequest request);

    /**
     * 删除拼团商品
     * @param id
     * @return
     */
    Boolean deleteById(Integer id);

    /**
     * 编辑拼团商品
     * @param request
     * @return
     */
    Boolean updateCombination(StoreCombinationRequest request);

    /**
     * 查询拼团商品详情
     * @param id
     * @return
     */
    StoreProductResponse getAdminDetail(Integer id);

    /**
     * 修改拼团商品状态
     * @param id
     * @param isShow
     */
    Boolean updateCombinationShow(Integer id, Boolean isShow);

    /**
     * admin拼团统计
     * @return
     */
    Map<String, Object> getAdminStatistics();

    /**
     * H5拼团商品列表
     * @param pageParamRequest
     * @return
     */
    PageInfo<StoreCombination> getH5List(PageParamRequest pageParamRequest);

    /**
     * H5 拼团Pink
     * @return
     */
    HashMap<String,Object> getForH5Pink();

    /**
     * H5拼团商品详情
     * @param id    拼团商品编号
     * @return
     */
    CombinationDetailResponse getH5Detail(Integer id);

    /**
     * 去拼团
     * @param pinkId 拼团团长单ID
     * @return
     */
    GoPinkResponse goPink(Integer pinkId);

    /**
     * 更多拼团信息
     * @param pageParamRequest
     * @param comId
     * @return
     */
    PageInfo<StoreCombination> getMore(PageParamRequest pageParamRequest, Integer comId);

    /**
     * 取消拼团
     * @param storePinkRequest
     */
    Boolean removePink(StorePinkRequest storePinkRequest);

    /**
     * 条件查询
     * @param storeCombination
     * @return
     */
    List<StoreCombination> getByEntity(StoreCombination storeCombination);

    /**
     * 扣减库存加销量
     * @param num           商品数量
     * @param attrValueId   拼团商品规格
     * @param productId     主商品id
     * @param user          购买用户
     * @return Boolean
     */
    Boolean decProductStock(StoreOrder storeOrder, Integer num, Integer attrValueId, Integer productId, User user);

    /**
     * 添加库存
     * @param stockRequest
     */
    Boolean stockAddRedis(StoreProductStockRequest stockRequest);

    /**
     * 后台任务批量操作库存
     */
    void consumeProductStock();

    /**
     * 拼团海报
     * @param pinkId 拼团id
     * @param from
     * @return
     */
    Boolean poster(Integer pinkId, String from);

    /**
     * 获取当前时间的拼团商品
     * @param productId
     * @return
     */
    List<StoreCombination> getCurrentBargainByProductId(Integer productId);

    /**
     * 商品是否存在拼团活动
     * @param productId 商品编号
     * @return
     */
    Boolean isExistActivity(Integer productId);

    /**
     * 查询带异常
     * @param combinationId 拼团商品id
     * @return StoreCombination
     */
    StoreCombination getByIdException(Integer combinationId);

    /**
     * 添加/扣减库存
     * @param id 秒杀商品id
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationStock(Integer id, Integer num, String type);
}