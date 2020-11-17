package com.zbkj.crmeb.sms.controller;

import com.alibaba.fastjson.JSONObject;
import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.SmsConstants;
import com.utils.ValidateFormUtil;
import com.zbkj.crmeb.sms.model.SmsRecord;
import com.zbkj.crmeb.sms.request.RegisterRequest;
import com.zbkj.crmeb.sms.request.SmsLoginRequest;
import com.zbkj.crmeb.sms.request.SmsRecordRequest;
import com.zbkj.crmeb.sms.service.SmsRecordService;
import com.zbkj.crmeb.sms.service.SmsService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


/**
 * 短信发送记录表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/sms")
@Api(tags = "短信服务")
public class SmsRecordController {

    @Autowired
    private SmsRecordService smsRecordService;

    @Autowired
    private SmsService smsService;

    /**
     * 注册账户时发送短信api
     * @param phone 手机号码
     * @return
     */
    @ApiModelProperty(value = "账户注册短信发送")
    @RequestMapping(value = "/sendCodeForRegister/{phone}", method = RequestMethod.GET)
    public CommonResult<Object> sendCodeForRegister(@PathVariable String phone){
        return CommonResult.success(smsService.sendCodeForRegister(phone));
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

    /**
     * 短信注册
     * @param registerRequest 注册参数
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @ApiOperation(value = "账号注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public CommonResult<JSONObject> register(@ModelAttribute RegisterRequest registerRequest){
        JSONObject result = smsService.register(registerRequest);
        return CommonResult.success(getMsgByResult(result));
    }

    /**
     * 分页显示短信发送记录表
     * @param smsRecordRequest 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @ApiOperation(value = "短信发送记录列表")
    @RequestMapping(value = "/record/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SmsRecord>>  getList(@ModelAttribute SmsRecordRequest smsRecordRequest,
                                                        @ModelAttribute PageParamRequest pageParamRequest){
        CommonPage<SmsRecord> smsRecordCommonPage =
                CommonPage.restPage(smsRecordService.getList(smsRecordRequest, pageParamRequest));
        return CommonResult.success(smsRecordCommonPage);
    }


//    /**
//     * 短信模版列表 直接查询短信api不做任何处理
//     * @param isHave 是否拥有
//     * @param pageParamRequest 分页参数
//     * @author Mr.Zhang
//     * @since 2020-04-16
//     */
//    @ApiOperation(value = "公共模板列表")
//    @RequestMapping(value = "/public/temp/list", method = RequestMethod.GET)
//    @ApiImplicitParam(name="isHave", value="是否拥有")
//    public CommonResult<JSONObject> getPublicTempList(@RequestParam(name = "isHave", required = false) String isHave,
//                                                      @ModelAttribute PageParamRequest pageParamRequest){
//        JSONObject result = smsService.getPublicTempList(isHave, pageParamRequest);
//        return CommonResult.success(getDataByResult(result));
//    }

//    /**
//     * 添加公共模板
//     * @param id id
//     * @param tempId 模板id
//     * @author Mr.Zhang
//     * @since 2020-04-17
//     */
//    @ApiOperation(value = "添加公共模板")
//    @RequestMapping(value = "/public/temp/save", method = RequestMethod.POST)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name="isHave", value="是否拥有"),
//            @ApiImplicitParam(name="tempId", value="模板ID")
//    })
//    public CommonResult<JSONObject> save(@RequestParam(name = "id") String id,
//                                         @RequestParam(name = "tempId") String tempId){
//        JSONObject result = smsService.save(id, tempId);
//        return CommonResult.success(getMsgByResult(result));
//    }

    /**
     * 获取账号信息
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @ApiOperation(value = "信息登录")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public CommonResult<JSONObject> account(SmsLoginRequest smsLoginRequest){
        JSONObject result = smsService.account(smsLoginRequest);
        return CommonResult.success(getDataByResult(result));
    }

    /**
     * 判断是否已经登录
     * @return 是否登录result
     */
    @ApiOperation(value = "是否已经登录")
    @RequestMapping(value = "/islogin", method = RequestMethod.GET)
    public CommonResult<JSONObject> isLogin(){
        return CommonResult.success(smsService.isLogin());
    }

    /**
     *
     * @return 当前登录的短信账户详情
     */
    @ApiOperation(value = "短信用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<JSONObject> getInfo(){
        return CommonResult.success(getDataByResult(smsService.info()));
    }

    /**
     * 注销当前登录
     * @return result
     */
    @ApiOperation(value = "注销")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public CommonResult<JSONObject> logOut(){
        smsService.logOut();
        return CommonResult.success();
    }

    /**
     * 获取支付码
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @ApiOperation(value = "获取支付套餐列表")
    @RequestMapping(value = "/pay/list", method = RequestMethod.GET)
    public CommonResult<JSONObject> payTempList(@ModelAttribute PageParamRequest pageParamRequest){
        JSONObject result = smsService.payTempList(pageParamRequest);
        JSONObject data = getDataByResult(result);
        return CommonResult.success(data);
    }

    /**
     * 获取支付码
     * @param payType 支付方式
     * @param mealId 套餐id
     * @param price 价格
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @ApiOperation(value = "获取支付码")
    @RequestMapping(value = "/pay/qrCode", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="payType", value="支付类型 weixin：微信支付/alipay：支付宝支付"),
            @ApiImplicitParam(name="mealId", value="套餐id"),
            @ApiImplicitParam(name="price", value="价格"),
            @ApiImplicitParam(name="num", value="套餐条数"),
    })
    public CommonResult<JSONObject> payQrCode(@RequestParam(name = "payType", defaultValue = SmsConstants.PAY_DEFAULT_PAY_TYPE) String payType,
                                              @RequestParam(name = "mealId", required = true) Integer mealId,
                                              @RequestParam(name = "price", required = true) BigDecimal price,
                                              @RequestParam(name = "num", required = false, defaultValue = "0") Integer num){
        JSONObject result = smsService.getPayQrCode(payType, mealId, price, num);
        return CommonResult.success(getDataByResult(result));
    }

    /**
     * 申请模板消息
     * @param title 主题
     * @param content 内容
     * @param type 类型
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @ApiOperation(value = "申请模板消息")
    @RequestMapping(value = "/temp/apply", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="title", value="主题"),
            @ApiImplicitParam(name="content", value="内容"),
            @ApiImplicitParam(name="type", value="type (1=验证码 2=通知 3=推广)"),
    })
    public CommonResult<JSONObject> applyTempMessage(@RequestParam(name = "title") String title,
                                                     @RequestParam(name = "content") String content,
                                                     @RequestParam(name = "type", required = false, defaultValue = "1") Integer type){
        JSONObject result = smsService.applyTempMessage(title, content, type);
        return CommonResult.success(getMsgByResult(result));
    }

    /**
     * 短信模板列表
     * @param title 主题
     * @param status 状态
     * @param pageParamRequest 分页信息
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @ApiOperation(value = "短信模板列表")
    @RequestMapping(value = "/temp/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="title", value="名称"),
            @ApiImplicitParam(name="status", value="状态"),
            @ApiImplicitParam(name="type", value="type (1=验证码 2=通知 3=推广)"),
    })
    public CommonResult<JSONObject> tempList(@RequestParam(name = "status", required = false) Integer status,
                                             @RequestParam(name = "title", required = false) String title,
                                             @RequestParam(name = "type", required = false) String type,
                                             @ModelAttribute PageParamRequest pageParamRequest){
        JSONObject result = smsService.tempList(title, status, type, pageParamRequest);
        return CommonResult.success(getDataByResult(result));
    }

    /**
     * 从JSONObject 中取出msg
     * @param result 结果数据
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    private String getMsgByResult(JSONObject result){
        return result.getString("msg");
    }

    /**
     * 从JSONObject 中取出msg
     * @param result 结果数据
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    private JSONObject getDataByResult(JSONObject result){
        return result.getJSONObject("data");
    }
}


