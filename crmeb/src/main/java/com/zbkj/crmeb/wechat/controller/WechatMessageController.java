package com.zbkj.crmeb.wechat.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.request.WechatMessageRequest;
import com.zbkj.crmeb.wechat.request.WechatMessageSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.wechat.service.WechatMessageService;
import com.zbkj.crmeb.wechat.model.WechatMessage;


/**
 * 用户行为记录表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/wechat/user/log")
@Api(tags = "微信开放平台 -- 用户行为记录")
public class WechatMessageController {

    @Autowired
    private WechatMessageService wechatMessageService;

    /**
     * 分页显示用户行为记录表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<WechatMessage>>  getList(@Validated WechatMessageSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<WechatMessage> wechatMessageCommonPage = CommonPage.restPage(wechatMessageService.getList(request, pageParamRequest));
        return CommonResult.success(wechatMessageCommonPage);
    }

    /**
     * 新增用户行为记录表
     * @param wechatMessageRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated WechatMessageRequest wechatMessageRequest){
        WechatMessage wechatMessage = new WechatMessage();
        BeanUtils.copyProperties(wechatMessageRequest, wechatMessage);

        if(wechatMessageService.save(wechatMessage)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除用户行为记录表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(wechatMessageService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改用户行为记录表
     * @param id integer id
     * @param wechatMessageRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated WechatMessageRequest wechatMessageRequest){
        WechatMessage wechatMessage = new WechatMessage();
        BeanUtils.copyProperties(wechatMessageRequest, wechatMessage);
        wechatMessage.setId(id);

        if(wechatMessageService.updateById(wechatMessage)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询用户行为记录表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<WechatMessage> info(@RequestParam(value = "id") Integer id){
        WechatMessage wechatMessage = wechatMessageService.getById(id);
        return CommonResult.success(wechatMessage);
   }
}



