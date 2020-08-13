package com.zbkj.crmeb.wechat.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.request.RoutineFormIdRequest;
import com.zbkj.crmeb.wechat.request.RoutineFormIdSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.wechat.service.RoutineFormIdService;
import com.zbkj.crmeb.wechat.model.RoutineFormId;


/**
 * 表单id表记录表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/wechat/routine/form")
@Api(tags = "微信开放平台 -- 表单id表记录")
public class RoutineFormIdController {

    @Autowired
    private RoutineFormIdService routineFormIdService;

    /**
     * 分页显示表单id表记录表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<RoutineFormId>>  getList(@Validated RoutineFormIdSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<RoutineFormId> routineFormIdCommonPage = CommonPage.restPage(routineFormIdService.getList(request, pageParamRequest));
        return CommonResult.success(routineFormIdCommonPage);
    }

    /**
     * 新增表单id表记录表
     * @param routineFormIdRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated RoutineFormIdRequest routineFormIdRequest){
        RoutineFormId routineFormId = new RoutineFormId();
        BeanUtils.copyProperties(routineFormIdRequest, routineFormId);

        if(routineFormIdService.save(routineFormId)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除表单id表记录表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(routineFormIdService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改表单id表记录表
     * @param id integer id
     * @param routineFormIdRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated RoutineFormIdRequest routineFormIdRequest){
        RoutineFormId routineFormId = new RoutineFormId();
        BeanUtils.copyProperties(routineFormIdRequest, routineFormId);
        routineFormId.setId(id);

        if(routineFormIdService.updateById(routineFormId)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询表单id表记录表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<RoutineFormId> info(@RequestParam(value = "id") Integer id){
        RoutineFormId routineFormId = routineFormIdService.getById(id);
        return CommonResult.success(routineFormId);
   }
}



