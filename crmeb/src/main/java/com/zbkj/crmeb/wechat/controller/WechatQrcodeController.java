package com.zbkj.crmeb.wechat.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.request.WechatQrcodeRequest;
import com.zbkj.crmeb.wechat.request.WechatQrcodeSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.wechat.service.WechatQrcodeService;
import com.zbkj.crmeb.wechat.model.WechatQrcode;


/**
 * 微信二维码管理表 前端控制器
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
@RequestMapping("api/admin/wechat/qrcode")
@Api(tags = "微信开放平台 -- 微信二维码管理")
public class WechatQrcodeController {

    @Autowired
    private WechatQrcodeService wechatQrcodeService;

    /**
     * 分页显示微信二维码管理表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<WechatQrcode>>  getList(@Validated WechatQrcodeSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<WechatQrcode> wechatQrcodeCommonPage = CommonPage.restPage(wechatQrcodeService.getList(request, pageParamRequest));
        return CommonResult.success(wechatQrcodeCommonPage);
    }

    /**
     * 新增微信二维码管理表
     * @param wechatQrcodeRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated WechatQrcodeRequest wechatQrcodeRequest){
        WechatQrcode wechatQrcode = new WechatQrcode();
        BeanUtils.copyProperties(wechatQrcodeRequest, wechatQrcode);

        if(wechatQrcodeService.save(wechatQrcode)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除微信二维码管理表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(wechatQrcodeService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改微信二维码管理表
     * @param id integer id
     * @param wechatQrcodeRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated WechatQrcodeRequest wechatQrcodeRequest){
        WechatQrcode wechatQrcode = new WechatQrcode();
        BeanUtils.copyProperties(wechatQrcodeRequest, wechatQrcode);
        wechatQrcode.setId(id);

        if(wechatQrcodeService.updateById(wechatQrcode)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询微信二维码管理表信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<WechatQrcode> info(@RequestParam(value = "id") Integer id){
        WechatQrcode wechatQrcode = wechatQrcodeService.getById(id);
        return CommonResult.success(wechatQrcode);
   }
}



