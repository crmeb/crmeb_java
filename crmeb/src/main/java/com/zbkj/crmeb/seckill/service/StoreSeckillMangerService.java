package com.zbkj.crmeb.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.zbkj.crmeb.seckill.model.StoreSeckillManger;
import com.zbkj.crmeb.seckill.request.StoreSeckillMangerRequest;
import com.zbkj.crmeb.seckill.request.StoreSeckillMangerSearchRequest;
import com.zbkj.crmeb.seckill.response.StoreSeckillManagerResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * StoreSeckillMangerService 接口
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
public interface StoreSeckillMangerService extends IService<StoreSeckillManger> {

    List<StoreSeckillManagerResponse> getList(StoreSeckillManger request, PageParamRequest pageParamRequest);

    /**
     * 删除秒杀配置 逻辑删除
     * @param id 待删除id
     * @return  删除结果
     */
    boolean deleteLogicById(int id);

    /**
     * 检查时间段是否已经存在
     * @param storeSeckillManger    查询秒杀配置
     * @return  查询结果
     */
    List<StoreSeckillManger> checkTimeRangeUnique(StoreSeckillManger storeSeckillManger);

    /**
     * 更新秒杀配置
     * @param storeSeckillManger 待更新秒杀配置
     * @return  更新结果
     */
    boolean updateByCondition(StoreSeckillManger storeSeckillManger);

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
    boolean updateStatus(int id, boolean status);

    /**
     * 更新秒杀配置
     * @param id id
     * @param storeSeckillMangerRequest 秒杀配置
     * @return 结果
     */
    boolean update(Integer id,StoreSeckillMangerRequest storeSeckillMangerRequest);
    void setTimeRangeFromRequest(@Validated @RequestBody StoreSeckillMangerRequest storeSeckillMangerRequest, StoreSeckillManger storeSeckillManger);
    void setTimeRangeFromRequest(@Validated @RequestBody StoreSeckillMangerSearchRequest request, StoreSeckillManger storeSeckillManger);

    /**
     * 获取移动端列表(正在进行和马上开始的秒杀)
     * @return List<StoreSeckillManagerResponse>
     */
    List<StoreSeckillManagerResponse> getH5List();
}
