package com.zbkj.crmeb.sms.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.CommonResult;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.utils.ValidateFormUtil;
import com.zbkj.crmeb.sms.request.SmsApplyTempRequest;
import com.zbkj.crmeb.sms.request.SmsModifySignRequest;
import com.zbkj.crmeb.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 短信发送记录表 前端控制器
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
@RequestMapping("api/admin/sms")
@Api(tags = "短信服务")
public class SmsRecordController {

    @Autowired
    private SmsService smsService;

    /**
     * 修改签名
     */
    @ApiOperation(value = "修改签名")
    @RequestMapping(value = "/modify/sign", method = RequestMethod.POST)
    public CommonResult<JSONObject> modifySign(@RequestBody @Validated SmsModifySignRequest request){
        if (smsService.modifySign(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 短信模板
     * @return
     */
    @ApiOperation(value = "短信模板")
    @RequestMapping(value = "/temps", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> temps(@ModelAttribute PageParamRequest pageParamRequest){
        MyRecord myRecord = smsService.temps(pageParamRequest);
        return CommonResult.success(myRecord);
    }

    /**
     * 申请短信模板
     */
    @ApiOperation(value = "申请短信模板")
    @RequestMapping(value = "/temp/apply", method = RequestMethod.POST)
    public CommonResult<JSONObject> applyTempMessage(@RequestBody @Validated SmsApplyTempRequest request){
        if (smsService.applyTempMessage(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 模板申请记录
     * @return
     */
    @ApiOperation(value = "模板申请记录")
    @RequestMapping(value = "/applys", method = RequestMethod.POST)
    @ApiImplicitParam(name="type", value="type (1=验证码 2=通知 3=推广)")
    public CommonResult<Map<String, Object>> applys(@RequestParam(name = "type", required = false) Integer type, @ModelAttribute PageParamRequest pageParamRequest){
        return CommonResult.success(smsService.applys(type, pageParamRequest));
    }

    /**
     * 发送短信
     * @param phone 手机号码
     * @return 发送是否成功
     */
    @ApiOperation(value = " 发送短信")
    @RequestMapping(value = "/sendCode", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="phone", value="手机号码", required = true)
    })
    public CommonResult<Object> sendCode(@RequestParam String phone){
        ValidateFormUtil.isPhone(phone,"手机号码错误");
        boolean codeResult = smsService.pushCodeToList(phone,1, null);
        return codeResult ? CommonResult.success("短信加入发送队列成功"):CommonResult.failed("短信加入发送队列失败");
    }
}


