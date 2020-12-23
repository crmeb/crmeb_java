package com.zbkj.crmeb.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.utils.DateUtil;
import com.zbkj.crmeb.seckill.dao.StoreSeckillMangerDao;
import com.zbkj.crmeb.seckill.model.StoreSeckillManger;
import com.zbkj.crmeb.seckill.request.StoreSeckillMangerSearchRequest;
import com.zbkj.crmeb.seckill.response.StoreSeckillManagerResponse;
import com.zbkj.crmeb.seckill.service.StoreSeckillMangerService;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StoreSeckillMangerServiceImpl 接口实现
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
    * @author Stivepeim
    * @since 2020-09-18
    * @return List<StoreSeckillManger>
    */
    @Override
    public List<StoreSeckillManagerResponse> getList(StoreSeckillManger request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreSeckillManger 类的多条件查询
        LambdaQueryWrapper<StoreSeckillManger> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if(null != request.getId()) lambdaQueryWrapper.eq(StoreSeckillManger::getId, request.getId());
        if(null != request.getIsDel()) lambdaQueryWrapper.eq(StoreSeckillManger::getIsDel, request.getIsDel());
        if(null != request.getEndTime()) lambdaQueryWrapper.le(StoreSeckillManger::getEndTime, request.getEndTime());
        if(null != request.getStartTime()) lambdaQueryWrapper.ge(StoreSeckillManger::getStartTime, request.getStartTime());
        if(null != request.getSort()) lambdaQueryWrapper.eq(StoreSeckillManger::getSort, request.getSort());
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
//        StoreSeckillManger storeSeckillManger = new StoreSeckillManger().setId(id).setIsDel(true);
//        return dao.updateById(storeSeckillManger) > 0;
        return dao.deleteById(id) > 0;
    }

    /**
     * 检查时间段是否已经存在
     *
     * @param storeSeckillManger 查询秒杀配置
     * @return 查询结果
     */
    @Override
    public List<StoreSeckillManger> checkTimeRangeUnique(StoreSeckillManger storeSeckillManger) {
        LambdaQueryWrapper<StoreSeckillManger> lqTimeUnique = Wrappers.lambdaQuery();
        lqTimeUnique.ge(StoreSeckillManger::getStartTime, storeSeckillManger.getStartTime());
        lqTimeUnique.gt(StoreSeckillManger::getStartTime, storeSeckillManger.getEndTime());
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
    @Override
    public boolean updateByCondition(StoreSeckillManger storeSeckillManger) {
        StoreSeckillManger ssm = new StoreSeckillManger();
        ssm.setId(storeSeckillManger.getId());
        if(StringUtils.isNotBlank(storeSeckillManger.getName())) ssm.setName(storeSeckillManger.getName());
        if(StringUtils.isNotBlank(storeSeckillManger.getImg()))
            ssm.setImg(systemAttachmentService.clearPrefix(storeSeckillManger.getImg()));
        if(StringUtils.isNotBlank(storeSeckillManger.getSilderImgs()))
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
    public boolean updateStatus(int id, boolean status) {
        StoreSeckillManger ssm = new StoreSeckillManger().setId(id).setStatus(status?1:0);
        return dao.updateById(ssm) > 0;
    }

    /////////////////////////////////////////////////// 自定义方法

    // 列表用 格式化time 对前端输出一致
    private void convertTime(List<StoreSeckillManagerResponse> responses, List<StoreSeckillManger> storeSeckillMangers) {
        storeSeckillMangers.stream().map(e->{
            StoreSeckillManagerResponse r = new StoreSeckillManagerResponse();
            BeanUtils.copyProperties(e,r);
            cTime(e, r);
            responses.add(r);
            return e;
        }).collect(Collectors.toList());
    }
    // 详情用 格式化time 对前端输出一致
    private void cTime(StoreSeckillManger e, StoreSeckillManagerResponse r) {
        String pStartTime = e.getStartTime().toString();
        String pEndTime = e.getEndTime().toString();
        String startTime = pStartTime.length() == 1? "0"+pStartTime:pStartTime;
        String endTime = pEndTime.length() == 1? "0"+pEndTime:pEndTime;
        r.setTime(startTime+":00,"+endTime+":00");
    }
}

