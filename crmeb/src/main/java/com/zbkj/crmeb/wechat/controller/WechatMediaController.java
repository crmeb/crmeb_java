package com.zbkj.crmeb.wechat.controller;

import com.common.CommonResult;
import com.zbkj.crmeb.wechat.service.WechatMediaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * 微信回复表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/wechat/media")
@Api(tags = "微信开放平台 -- 素材")
public class WechatMediaController {

    @Autowired
    private WechatMediaService wechatMediaService;

    /**
     * 上传
     * @author Mr.Zhang
     * @since 2020-04-18
     */
    @ApiOperation(value = "上传")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public CommonResult<Map<String, String>> upload(
            @RequestParam("media") @ApiParam(name = "media", value = "待上传素材图片文件", required = true) MultipartFile file,
            @RequestParam("type") @ApiParam(name = "type", value = "媒体文件类型，分别有图片（image）、语音（voice", required = true, allowableValues = "range[image,voice]") String type
    ) throws Exception {
        return CommonResult.success(wechatMediaService.upload(file, type));
    }
}



