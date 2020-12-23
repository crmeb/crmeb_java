package com.zbkj.crmeb.wechat.controller;

import com.common.CommonResult;
import com.zbkj.crmeb.wechat.service.WeChatService;
import com.zbkj.crmeb.wechat.vo.ProgramCategoryVo;
import com.zbkj.crmeb.wechat.vo.ProgramTempKeywordsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 微信缓存表 前端控制器
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
@RequestMapping("api/admin/wechat/program")
@Api(tags = "微信开放平台 -- 小程序")
public class ProgramController {

    @Autowired
    private WeChatService weChatService;


    /**
     * 小程序行业信息
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @ApiOperation(value = "小程序行业信息")
    @RequestMapping(value = "/category", method = RequestMethod.GET)
    public CommonResult<List<ProgramCategoryVo>> getCategory(){
        return CommonResult.success(weChatService.getProgramCategory());
    }


    /**
     * 通过微信公共模板tid获取关键字列表
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @ApiOperation(value = "通过微信模板tid获取关键字列表")
    @RequestMapping(value = "/getWeChatKeywordsByTid", method = RequestMethod.GET)
    public CommonResult<List<ProgramTempKeywordsVo>> getWeChatKeywordsByTid(@RequestParam(value = "tid") Integer tid){
        return CommonResult.success(weChatService.getWeChatKeywordsByTid(tid));
    }
}



