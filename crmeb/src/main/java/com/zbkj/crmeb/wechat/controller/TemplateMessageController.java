package com.zbkj.crmeb.wechat.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.zbkj.crmeb.wechat.model.TemplateMessage;
import com.zbkj.crmeb.wechat.request.TemplateMessageRequest;
import com.zbkj.crmeb.wechat.request.TemplateMessageSearchRequest;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import com.zbkj.crmeb.wechat.vo.TemplateMessageIndustryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 微信模板 前端控制器
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
@RequestMapping("api/admin/wechat/template")
@Api(tags = "微信 -- 消息模版") //配合swagger使用
public class TemplateMessageController {

    @Autowired
    private TemplateMessageService templateMessageService;

    /**
     * 分页显示微信模板
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<TemplateMessage>>  getList(
            @Validated TemplateMessageSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        CommonPage<TemplateMessage> templateMessageCommonPage =
                CommonPage.restPage(templateMessageService.getList(request, pageParamRequest));
        return CommonResult.success(templateMessageCommonPage);
    }

    /**
     * 新增微信模板
     * @param templateMessageRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated TemplateMessageRequest templateMessageRequest){
        TemplateMessage templateMessage = new TemplateMessage();
        BeanUtils.copyProperties(templateMessageRequest, templateMessage);
        templateMessage.setType(true);
        if(templateMessageService.save(templateMessage)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除微信模板
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable Integer id){
        if(templateMessageService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改微信模板
     * @param templateMessageRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public CommonResult<String> update(@PathVariable Integer id,
                                       @RequestBody @Validated TemplateMessageRequest templateMessageRequest){
        TemplateMessage templateMessage = templateMessageService.infoException(id);
        templateMessage.setTempId(templateMessageRequest.getTempId());
//        BeanUtils.copyProperties(templateMessageRequest, templateMessage);
        if(templateMessageService.updateById(templateMessage)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改状态
     * @param id Integer id
     * @param status Integer 状态
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update/status/{id}", method = RequestMethod.POST)
    public CommonResult<String> updateStatus(@PathVariable Integer id, @RequestParam Integer status){
//        TemplateMessage templateMessage = templateMessageService.infoException(id);
        TemplateMessage templateMessage = templateMessageService.getById(id);
        if(null == templateMessage){
            throw new CrmebException("此模板" + id + " 不存在或者已删除");
        }
        templateMessage.setStatus(status);
        if(templateMessageService.updateById(templateMessage)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询微信模板信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<TemplateMessage> info(@PathVariable Integer id){
        TemplateMessage templateMessage = templateMessageService.infoException(id);
        return CommonResult.success(templateMessage);
    }

    /**
     * 查询微信模板信息
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @ApiOperation(value = "行业信息")
    @RequestMapping(value = "/industry", method = RequestMethod.GET)
    public CommonResult<TemplateMessageIndustryVo> getIndustry(){
        return CommonResult.success(templateMessageService.getIndustry());
    }
}



