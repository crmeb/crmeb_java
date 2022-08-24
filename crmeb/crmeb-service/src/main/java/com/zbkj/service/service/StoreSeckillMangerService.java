package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.model.seckill.StoreSeckillManger;
import com.zbkj.common.request.StoreSeckillMangerRequest;
import com.zbkj.common.request.StoreSeckillMangerSearchRequest;
import com.zbkj.common.response.StoreSeckillManagerResponse;

import java.util.List;

/**
 * StoreSeckillMangerService 接口
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
public interface StoreSeckillMangerService extends IService<StoreSeckillManger> {

    /**
     * 秒杀配置列表
     * @param request 查询参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<StoreSeckillManagerResponse> getList(StoreSeckillMangerSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 删除秒杀配置 逻辑删除
     * @param id 待删除id
     * @return  删除结果
     */
    boolean deleteLogicById(int id);

    /**
     * 详情
     * @param id 配置id
     * @return  查询到的结果
     */
    StoreSeckillManagerResponse detail(int id);

    /**
     * 获取正在秒杀的时间段
     * @return 正在秒杀的时间段
     */
    List<StoreSeckillManger> getCurrentSeckillManager();

    /**
     * 更新秒杀配置状态
     * @param id id
     * @param status 待更新状态
     * @return 结果
     */
    Boolean updateStatus(Integer id, Boolean status);

    /**
     * 更新秒杀配置
     * @param id id
     * @param storeSeckillMangerRequest 秒杀配置
     * @return 结果
     */
    Boolean update(Integer id, StoreSeckillMangerRequest storeSeckillMangerRequest);

    /**
     * 获取移动端列表(正在进行和马上开始的秒杀)
     * @return List<StoreSeckillManagerResponse>
     */
    List<StoreSeckillManagerResponse> getH5List();

    /**
     * 获取所有秒杀配置
     * @return List
     */
    List<StoreSeckillManagerResponse> getAllList();

    /**
     * 添加秒杀配置
     * @param storeSeckillMangerRequest 配置参数
     */
    Boolean saveManger(StoreSeckillMangerRequest storeSeckillMangerRequest);
}
