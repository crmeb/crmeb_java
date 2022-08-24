package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.request.StoreSeckillMangerRequest;
import com.zbkj.common.request.StoreSeckillMangerSearchRequest;
import com.zbkj.common.response.StoreSeckillManagerResponse;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.model.seckill.StoreSeckillManger;
import com.zbkj.service.dao.StoreSeckillMangerDao;
import com.zbkj.service.service.StoreSeckillMangerService;
import com.zbkj.service.service.SystemAttachmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * StoreSeckillMangerServiceImpl 接口实现
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
@Service
public class StoreSeckillMangerServiceImpl extends ServiceImpl<StoreSeckillMangerDao, StoreSeckillManger>
        implements StoreSeckillMangerService {

    @Resource
    private StoreSeckillMangerDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;
    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return List<StoreSeckillManger>
    */
    @Override
    public List<StoreSeckillManagerResponse> getList(StoreSeckillMangerSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreSeckillManger 类的多条件查询
        LambdaQueryWrapper<StoreSeckillManger> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if(null != request.getName()) lambdaQueryWrapper.like(StoreSeckillManger::getName, request.getName());
        if(null != request.getStatus()) lambdaQueryWrapper.eq(StoreSeckillManger::getStatus, request.getStatus());

        lambdaQueryWrapper.orderByAsc(StoreSeckillManger::getSort);

        // 处理数据time格式 适配前端
        List<StoreSeckillManagerResponse> responses = new ArrayList<>();
        List<StoreSeckillManger> storeSeckillMangers = dao.selectList(lambdaQueryWrapper);
        convertTime(responses, storeSeckillMangers);
        return responses;
    }

    /**
     * 删除秒杀配置 逻辑删除
     *
     * @param id 待删除id
     * @return 删除结果
     */
    @Override
    public boolean deleteLogicById(int id) {
        return dao.deleteById(id) > 0;
    }

    /**
     * 检查时间段是否已经存在
     *
     * @param storeSeckillManger 查询秒杀配置
     * @return 查询结果
     */
    private List<StoreSeckillManger> checkTimeRangeUnique(StoreSeckillManger storeSeckillManger) {
        LambdaQueryWrapper<StoreSeckillManger> lqTimeUnique = Wrappers.lambdaQuery();
        lqTimeUnique.ge(StoreSeckillManger::getStartTime, storeSeckillManger.getStartTime());
        lqTimeUnique.lt(StoreSeckillManger::getStartTime, storeSeckillManger.getEndTime());
        lqTimeUnique.or();
        lqTimeUnique.le(StoreSeckillManger::getStartTime, storeSeckillManger.getStartTime());
        lqTimeUnique.ge(StoreSeckillManger::getEndTime, storeSeckillManger.getEndTime());
        lqTimeUnique.or();
        lqTimeUnique.gt(StoreSeckillManger::getEndTime, storeSeckillManger.getStartTime());
        lqTimeUnique.le(StoreSeckillManger::getEndTime, storeSeckillManger.getEndTime());

        return dao.selectList(lqTimeUnique);
    }

    /**
     * 更新秒杀配置
     *
     * @param storeSeckillManger 待更新秒杀配置
     * @return 更新结果
     */
    private boolean updateByCondition(StoreSeckillManger storeSeckillManger) {
        StoreSeckillManger ssm = new StoreSeckillManger();
        ssm.setId(storeSeckillManger.getId());
        if(StrUtil.isNotBlank(storeSeckillManger.getName())) ssm.setName(storeSeckillManger.getName());
        if(StrUtil.isNotBlank(storeSeckillManger.getImg()))
            ssm.setImg(systemAttachmentService.clearPrefix(storeSeckillManger.getImg()));
        if(StrUtil.isNotBlank(storeSeckillManger.getSilderImgs()))
            ssm.setSilderImgs(systemAttachmentService.clearPrefix(storeSeckillManger.getSilderImgs()));
        if(null != storeSeckillManger.getStatus()) ssm.setStatus(storeSeckillManger.getStatus());
        if(null != storeSeckillManger.getStartTime()) ssm.setStartTime(storeSeckillManger.getStartTime());
        if(null != storeSeckillManger.getEndTime()) ssm.setEndTime(storeSeckillManger.getEndTime());
        return dao.updateById(ssm) > 0;
    }

    /**
     * 详情
     *
     * @param id 配置id
     * @return 查询到的结果
     */
    @Override
    public StoreSeckillManagerResponse detail(int id) {
        StoreSeckillManger storeSeckillManger = dao.selectById(id);
        StoreSeckillManagerResponse response = new StoreSeckillManagerResponse();
        BeanUtils.copyProperties(storeSeckillManger,response);
        cTime(storeSeckillManger, response);
        return response;
    }

    /**
     * 获取正在秒杀的时间段
     *
     * @return 正在秒杀的时间段
     */
    @Override
    public List<StoreSeckillManger> getCurrentSeckillManager() {
        int currentHour = DateUtil.getCurrentHour();
        LambdaQueryWrapper<StoreSeckillManger> lqw = Wrappers.lambdaQuery();
        lqw.le(StoreSeckillManger::getStartTime,currentHour).gt(StoreSeckillManger::getEndTime,currentHour);
        return dao.selectList(lqw);
    }

    /**
     * 更新秒杀配置状态
     *
     * @param id     id
     * @param status 待更新状态
     * @return 结果
     */
    @Override
    public Boolean updateStatus(Integer id, Boolean status) {
        StoreSeckillManger ssm = new StoreSeckillManger().setId(id).setStatus(status ? 1 : 0);
        return dao.updateById(ssm) > 0;
    }

    /**
     * 更新秒杀配置
     * @param id id
     * @param storeSeckillMangerRequest 秒杀配置
     * @return 结果
     */
    @Override
    public Boolean update(Integer id, StoreSeckillMangerRequest storeSeckillMangerRequest) {
        StoreSeckillManger storeSeckillManger = new StoreSeckillManger();
        BeanUtils.copyProperties(storeSeckillMangerRequest, storeSeckillManger);
        storeSeckillManger.setId(id);
        // 对request中的time做分割后赋值给mode中的start和end属性
        setTimeRangeFromRequest(storeSeckillMangerRequest, storeSeckillManger);
        List<StoreSeckillManger> existTimes = checkTimeRangeUnique(storeSeckillManger);
        if(existTimes.size() > 1){
            throw new CrmebException("当前时间段的秒杀配置已存在");
        }else if(existTimes.size() == 1) {
            // 判断开始时间 结束时间 是否被包涵
            LambdaQueryWrapper<StoreSeckillManger> startAndEndExcuseQuery = Wrappers.lambdaQuery();
            startAndEndExcuseQuery.ge(StoreSeckillManger::getStartTime,storeSeckillManger.getStartTime())
                    .le(StoreSeckillManger::getEndTime,storeSeckillManger.getEndTime());
            List<StoreSeckillManger> storeSeckillMangers = dao.selectList(startAndEndExcuseQuery);
            // 时间区间改大 不存在的情况
            if(CollUtil.isEmpty(storeSeckillMangers) && storeSeckillMangers.size() == 0){
                return updateByCondition(storeSeckillManger);
                // 时间区间改小 id一样且仅仅存在一条
            }else if(storeSeckillMangers.size() == 1 && storeSeckillMangers.get(0).getId().equals(id)){
                return updateByCondition(storeSeckillManger);
            }else{
                throw new CrmebException("当前时间段的秒杀配置已存在");
            }
        }else {
            return updateByCondition(storeSeckillManger);
        }
    }

    /////////////////////////////////////////////////// 自定义方法

    // 列表用 格式化time 对前端输出一致
    private void convertTime(List<StoreSeckillManagerResponse> responses, List<StoreSeckillManger> storeSeckillMangers) {
        storeSeckillMangers.forEach(e -> {
            StoreSeckillManagerResponse r = new StoreSeckillManagerResponse();
            BeanUtils.copyProperties(e, r);
            cTime(e, r);
            responses.add(r);
        });
    }
    // 详情用 格式化time 对前端输出一致
    private void  cTime(StoreSeckillManger e, StoreSeckillManagerResponse r) {
        String pStartTime = e.getStartTime().toString();
        String pEndTime = e.getEndTime().toString();
        String startTime = pStartTime.length() == 1 ? "0" + pStartTime : pStartTime;
        String endTime = pEndTime.length() == 1 ? "0" + pEndTime : pEndTime;
        r.setTime(startTime + ":00," + endTime + ":00");
    }

    /**
     * 兼容时间参数 request中String格式 mode中Integer
     * @param storeSeckillMangerRequest request参数
     * @param storeSeckillManger        秒杀配置实体
     */
    private void setTimeRangeFromRequest(@Validated @RequestBody StoreSeckillMangerRequest storeSeckillMangerRequest, StoreSeckillManger storeSeckillManger) {
        if(!storeSeckillMangerRequest.getTime().contains(",")){
            throw new CrmebException("时间参数不正确 例如:01:00,02:00");
        }
        String[] timeRage = storeSeckillMangerRequest.getTime().split(",");
        Integer startTime = Integer.parseInt(timeRage[0].split(":")[0]);
        Integer endTime = Integer.parseInt(timeRage[1].split(":")[0]);
        storeSeckillManger.setStartTime(startTime);
        storeSeckillManger.setEndTime(endTime);
    }

    /**
     * 获取移动端列表 (正在进行和马上开始的秒杀)
     * @return List<StoreSeckillManagerResponse>
     */
    @Override
    public List<StoreSeckillManagerResponse> getH5List() {
        LambdaQueryWrapper<StoreSeckillManger> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(StoreSeckillManger::getIsDel, false);
        lambdaQueryWrapper.eq(StoreSeckillManger::getStatus, 1);
        // 获取当前小时
        int currentHour = DateUtil.getCurrentHour();
        lambdaQueryWrapper.gt(StoreSeckillManger::getEndTime, currentHour);
        lambdaQueryWrapper.orderByAsc(StoreSeckillManger::getStartTime);
        List<StoreSeckillManger> storeSeckillMangers = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(storeSeckillMangers)) {
            return CollUtil.newArrayList();
        }
        // 处理数据time格式 适配前端
        List<StoreSeckillManagerResponse> responses = new ArrayList<>();
        convertTime(responses, storeSeckillMangers);
        return responses;
    }

    /**
     * 获取所有秒杀配置
     * @return List
     */
    @Override
    public List<StoreSeckillManagerResponse> getAllList() {
        LambdaQueryWrapper<StoreSeckillManger> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.orderByAsc(StoreSeckillManger::getSort);
        // 处理数据time格式 适配前端
        List<StoreSeckillManger> storeSeckillMangers = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(storeSeckillMangers)) {
            return CollUtil.newArrayList();
        }
        List<StoreSeckillManagerResponse> responses = new ArrayList<>();
        convertTime(responses, storeSeckillMangers);
        return responses;
    }

    /**
     * 添加秒杀配置
     * @param storeSeckillMangerRequest 配置参数
     */
    @Override
    public Boolean saveManger(StoreSeckillMangerRequest storeSeckillMangerRequest) {
        StoreSeckillManger storeSeckillManger = new StoreSeckillManger();
        BeanUtils.copyProperties(storeSeckillMangerRequest, storeSeckillManger);
        // 对request中的time做分割后赋值给mode中的start和end属性
        setTimeRangeFromRequest(storeSeckillMangerRequest, storeSeckillManger);
        List<StoreSeckillManger> storeSeckillMangers = checkTimeRangeUnique(storeSeckillManger);
        if (storeSeckillMangers.size() > 0) {
            throw new CrmebException("当前时间段的秒杀配置已存在");
        }
        storeSeckillManger.setImg(systemAttachmentService.clearPrefix(storeSeckillManger.getImg()));
        storeSeckillManger.setSilderImgs(systemAttachmentService.clearPrefix(storeSeckillManger.getSilderImgs()));
        return save(storeSeckillManger);
    }
}

