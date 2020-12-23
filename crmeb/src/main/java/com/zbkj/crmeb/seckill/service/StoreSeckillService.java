package com.zbkj.crmeb.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.crmeb.front.response.SecKillResponse;
import com.zbkj.crmeb.seckill.model.StoreSeckill;
import com.zbkj.crmeb.seckill.request.StoreSeckillAttrRequest;
import com.zbkj.crmeb.seckill.request.StoreSeckillRequest;
import com.zbkj.crmeb.seckill.request.StoreSeckillSearchRequest;
import com.zbkj.crmeb.seckill.response.StoreSeckillDetailResponse;
import com.zbkj.crmeb.seckill.response.StoreSeckillResponse;
import com.zbkj.crmeb.store.request.StoreProductRequest;
import com.zbkj.crmeb.store.request.StoreProductStockRequest;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * StoreSeckillService 接口
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
public interface StoreSeckillService extends IService<StoreSeckill> {

    PageInfo<StoreSeckillResponse> getList(StoreSeckillSearchRequest request, PageParamRequest pageParamRequest);

    /**
     *  逻辑删除
     * @param id 秒杀id
     * @return 删除结果
     */
    boolean deleteById(int id);

    /**
     *  新增秒杀商品
     * @param request 待新增秒杀商品
     * @return  新增结果
     */
    boolean saveSeckill(StoreSeckillRequest request);

    /**
     *  秒杀商品详情 H5
     * @param skillId 秒杀商品id
     * @return 详情
     */
    StoreSeckillDetailResponse getDetailH5(int skillId);

    /**
     *  秒杀商品详情 管理端
     * @param skillId 秒杀id
     * @return 详情数据
     */
    StoreProductResponse getDetailAdmin(int skillId);

    /**
     *  更新秒杀商品
     * @param request 待更新秒杀商品
     * @return  更新结果
     */
    boolean updateSeckill(StoreSeckillRequest request);

    /**
     * 更新秒杀状态
     * @param secKillId 秒杀id
     * @param status 秒杀状态
     * @return 更新结果
     */
    boolean updateSecKillStatus(int secKillId,boolean status);

    /**
     *  移动端 获取秒杀配置
     * @return 秒杀配置
     */
    HashMap<String,Object> getForH5Index();

    /**
     * 根据秒杀时间段查询已配置的秒杀商品
     * @param timeId 秒杀id
     * @param inCurrentTime 是否存在当前秒杀时间段
     * @return 秒杀中的商品
     */
    List<StoreSeckillResponse> getKillListByTimeId(String timeId,PageParamRequest pageParamRequest,boolean inCurrentTime);

    /**
     * 秒杀基础查询
     * @param storeSeckill 秒杀基本参数
     * @return 查询到的秒杀商品
     */
    List<StoreSeckill> getByEntity(StoreSeckill storeSeckill);

    /**
     * 扣减库存加销量
     * @param seckillId 产品id
     * @param num 商品数量
     * @param type 是否限购 0=不限购
     * @return 扣减结果
     */
    boolean decProductStock(Integer seckillId, Integer num, Integer attrValueId, Integer type);

    /**
     * 根据商品id查询正在秒杀的商品信息
     * @param productId 商品id
     * @return 正在参与的秒杀信息
     */
    List<StoreSeckill> getCurrentSecKillByProductId(Integer productId);

    /**
     * 添加库存
     * @param request
     * @return
     */
    boolean stockAddRedis(StoreProductStockRequest request);

    /**
     * 后台任务批量操作库存
     */
    void consumeProductStock();

    /**
     * 商品是否存在秒杀活动
     * @param productId 商品编号
     */
    Boolean isExistActivity(Integer productId);
}
