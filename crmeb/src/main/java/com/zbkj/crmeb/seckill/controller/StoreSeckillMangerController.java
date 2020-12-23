package com.zbkj.crmeb.seckill.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.zbkj.crmeb.seckill.model.StoreSeckillManger;
import com.zbkj.crmeb.seckill.request.StoreSeckillMangerRequest;
import com.zbkj.crmeb.seckill.request.StoreSeckillMangerSearchRequest;
import com.zbkj.crmeb.seckill.response.StoreSeckillManagerResponse;
import com.zbkj.crmeb.seckill.service.StoreSeckillMangerService;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 秒杀配置 前端控制器
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
@Slf4j
@RestController
@RequestMapping("api/admin/store/seckill/manger")
@Api(tags = "商品 -- 秒杀 -- 配置") //配合swagger使用

public class StoreSeckillMangerController {

    @Autowired
    private StoreSeckillMangerService storeSeckillMangerService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 分页显示
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Stivepeim
     * @since 2020-09-18
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreSeckillManagerResponse>>  getList(
            @Validated StoreSeckillMangerSearchRequest request, @Validated PageParamRequest pageParamRequest){
        StoreSeckillManger storeSeckillManger = new StoreSeckillManger();
        BeanUtils.copyProperties(request, storeSeckillManger);
        // 对request中的time做分割后赋值给mode中的start和end属性
        setTimeRangeFromRequest(request, storeSeckillManger);
        CommonPage<StoreSeckillManagerResponse> storeSeckillMangerCommonPage =
                CommonPage.restPage(storeSeckillMangerService.getList(storeSeckillManger, pageParamRequest));
        return CommonResult.success(storeSeckillMangerCommonPage);
    }

    /**
     * 新增
     * @param storeSeckillMangerRequest 新增参数
     * @author Stivepeim
     * @since 2020-09-18
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreSeckillMangerRequest storeSeckillMangerRequest){
        StoreSeckillManger storeSeckillManger = new StoreSeckillManger();
        BeanUtils.copyProperties(storeSeckillMangerRequest, storeSeckillManger);
        // 对request中的time做分割后赋值给mode中的start和end属性
        setTimeRangeFromRequest(storeSeckillMangerRequest, storeSeckillManger);
        List<StoreSeckillManger> storeSeckillMangers = storeSeckillMangerService.checkTimeRangeUnique(storeSeckillManger);
        if(storeSeckillMangers.size() > 0){
            throw new CrmebException("当前时间段的秒杀配置已存在");
        }
        storeSeckillManger.setImg(systemAttachmentService.clearPrefix(storeSeckillManger.getImg()));
        storeSeckillManger.setSilderImgs(systemAttachmentService.clearPrefix(storeSeckillManger.getSilderImgs()));
        if(storeSeckillMangerService.save(storeSeckillManger)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }


    /**
     * 删除
     * @param id Integer
     * @author Stivepeim
     * @since 2020-09-18
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeSeckillMangerService.deleteLogicById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改
     * @param id integer id
     * @param storeSeckillMangerRequest 修改参数
     * @author Stivepeim
     * @since 2020-09-18
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated StoreSeckillMangerRequest storeSeckillMangerRequest){
        StoreSeckillManger storeSeckillManger = new StoreSeckillManger();
        BeanUtils.copyProperties(storeSeckillMangerRequest, storeSeckillManger);
        storeSeckillManger.setId(id);
        // 对request中的time做分割后赋值给mode中的start和end属性
        setTimeRangeFromRequest(storeSeckillMangerRequest, storeSeckillManger);
        List<StoreSeckillManger> existTimes = storeSeckillMangerService.checkTimeRangeUnique(storeSeckillManger);
        if(existTimes.size() == 0){
            storeSeckillMangerService.updateByCondition(storeSeckillManger);
        }else{
            // 更新时排除自身更新
            StoreSeckillManger ssm = existTimes.get(0);
            if(ssm.getId() == id && ssm.getStartTime() == storeSeckillManger.getStartTime()
                    && ssm.getEndTime() == storeSeckillManger.getEndTime()){
                if(storeSeckillMangerService.updateByCondition(storeSeckillManger)){
                    return CommonResult.success();
                }else{
                    return CommonResult.failed();
                }
            }else{
                throw new CrmebException("当前时间段的秒杀配置已存在");
            }
        }
        return CommonResult.failed();
    }

    /**
     * 查询信息
     * @param id Integer
     * @author Stivepeim
     * @since 2020-09-18
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreSeckillManagerResponse> info(@RequestParam(value = "id") Integer id){
        StoreSeckillManagerResponse storeSeckillManger = storeSeckillMangerService.detail(id);
        return CommonResult.success(storeSeckillManger);
   }

    /**
     * 更新秒杀配置状态
     * @param id    配置id
     * @param status    待更新配置状态
     * @return  更新结果
     */
   @ApiOperation(value = "秒杀配置状态更新")
   @RequestMapping(value = "/update/status/{id}", method = RequestMethod.POST)
   @ApiImplicitParams({
           @ApiImplicitParam(name = "id", value = "商品id", dataType = "int", required = true),
           @ApiImplicitParam(name = "status", value = "状态", dataType = "boolean", required = true)
   })
   public CommonResult<Object> updateStatus(@PathVariable(value = "id") int id,boolean status){
        return CommonResult.success(storeSeckillMangerService.updateStatus(id,status));
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
     * 兼容时间参数 request中String格式 mode中Integer
     * @param request request参数
     * @param storeSeckillManger        秒杀配置实体
     */
    private void setTimeRangeFromRequest(@Validated @RequestBody StoreSeckillMangerSearchRequest request, StoreSeckillManger storeSeckillManger) {
        if(null == request.getTime()) return;
        if(!request.getTime().contains(",")){
            throw new CrmebException("时间参数不正确 例如:01：00,02:00");
        }
        String[] timeRage = request.getTime().split(",");
        Integer startTime = Integer.parseInt(timeRage[0].split(":")[0]);
        Integer endTime = Integer.parseInt(timeRage[1].split(":")[0]);
        storeSeckillManger.setStartTime(startTime);
        storeSeckillManger.setEndTime(endTime);
    }
}



