package com.zbkj.crmeb.system.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.system.model.SystemAttachment;
import com.zbkj.crmeb.system.request.SystemAttachmentMoveRequest;
import com.zbkj.crmeb.system.request.SystemAttachmentRequest;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


/**
 * 附件管理表 前端控制器
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
@RequestMapping("api/admin/system/attachment")
@Api(tags = "附件管理") //配合swagger使用

public class SystemAttachmentController {

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 分页显示附件管理表
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-06-02
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SystemAttachment>>  getList(
            @RequestParam @Validated Integer pid,
            @Validated PageParamRequest pageParamRequest){
        CommonPage<SystemAttachment> systemAttachmentCommonPage =
                CommonPage.restPage(systemAttachmentService.getList(pid, pageParamRequest));
        return CommonResult.success(systemAttachmentCommonPage);
    }

    /**
     * 新增附件管理表
     * @param systemAttachmentRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-06-02
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated SystemAttachmentRequest systemAttachmentRequest){
        SystemAttachment systemAttachment = new SystemAttachment();
        BeanUtils.copyProperties(systemAttachmentRequest, systemAttachment);

        if(systemAttachmentService.save(systemAttachment)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除附件管理表
     * @param ids String
     * @author Mr.Zhang
     * @since 2020-06-02
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{ids}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable String ids){
        if(systemAttachmentService.removeByIds(CrmebUtil.stringToArray(ids))){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改附件管理表
     * @param id integer id
     * @param systemAttachmentRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-06-02
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id,
                                       @RequestBody @Validated SystemAttachmentRequest systemAttachmentRequest){
        SystemAttachment systemAttachment = new SystemAttachment();
        BeanUtils.copyProperties(systemAttachmentRequest, systemAttachment);
        systemAttachment.setAttId(id);

        if(systemAttachmentService.updateById(systemAttachment)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询附件管理表信息
     * @param move SystemAttachmentMoveRequest
     * @author Mr.Zhang
     * @since 2020-06-02
     */
    @ApiOperation(value = "更改图片目录")
    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public CommonResult<String> updateAttrId(@RequestBody @Validated SystemAttachmentMoveRequest move){
        LambdaUpdateWrapper<SystemAttachment> lup = new LambdaUpdateWrapper<>();
        lup.in(SystemAttachment::getAttId, CrmebUtil.stringToArray(move.getAttrId()));
        lup.set(SystemAttachment::getPid, move.getPid());
        if(systemAttachmentService.update(lup)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询附件管理表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-06-02
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<SystemAttachment> info(@PathVariable Integer id){
        SystemAttachment systemAttachment = systemAttachmentService.getById(id);
        return CommonResult.success(systemAttachment);
   }
}



