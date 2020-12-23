package com.zbkj.crmeb.wechat.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.model.WechatReply;
import com.zbkj.crmeb.wechat.request.WechatReplyRequest;
import com.zbkj.crmeb.wechat.request.WechatReplySearchRequest;
import com.zbkj.crmeb.wechat.service.WechatReplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 微信关键字回复表 前端控制器
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
@RequestMapping("api/admin/wechat/keywords/reply")
@Api(tags = "微信开放平台 -- 微信关键字回复")
public class WechatReplyController {

    @Autowired
    private WechatReplyService wechatReplyService;

    /**
     * 分页显示微信关键字回复表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<WechatReply>>  getList(@Validated WechatReplySearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<WechatReply> wechatReplyCommonPage = CommonPage.restPage(wechatReplyService.getList(request, pageParamRequest));
        return CommonResult.success(wechatReplyCommonPage);
    }

    /**
     * 新增微信关键字回复表
     * @param wechatReplyRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated WechatReplyRequest wechatReplyRequest){
        WechatReply wechatReply = new WechatReply();
        BeanUtils.copyProperties(wechatReplyRequest, wechatReply);

        if(wechatReplyService.create(wechatReply)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除微信关键字回复表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(wechatReplyService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改微信关键字回复表
     * @param id integer id
     * @param wechatReplyRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated WechatReplyRequest wechatReplyRequest){
        WechatReply wechatReply = wechatReplyService.getInfoException(wechatReplyRequest.getId(), false);
        BeanUtils.copyProperties(wechatReplyRequest, wechatReply);
        if(wechatReplyService.updateVo(wechatReply)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改状态
     * @param id integer id
     * @param status boolean 状态
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "状态")
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam(value = "id") Integer id, @RequestParam(value = "status") boolean status){
        WechatReply wechatReply = wechatReplyService.getInfoException(id, false);
        wechatReply.setStatus(status);
        if(wechatReplyService.updateVo(wechatReply)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询微信关键字回复表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<WechatReply> info(@RequestParam(value = "id") Integer id){
        WechatReply wechatReply = wechatReplyService.getInfo(id);
        return CommonResult.success(wechatReply);
   }

    /**
     * 根据关键字查询数据
     * @param keywords String 关键字
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "根据关键字查询数据")
    @RequestMapping(value = "/info/keywords", method = RequestMethod.GET)
    public CommonResult<WechatReply> info(@RequestParam(value = "keywords") String keywords){
        WechatReply wechatReply = wechatReplyService.getVoByKeywords(keywords);
        return CommonResult.success(wechatReply);
    }
}



