package com.zbkj.crmeb.front.controller;

import com.common.CommonResult;
import com.constants.Constants;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.front.request.UserRechargeRequest;
import com.zbkj.crmeb.front.response.UserRechargePaymentResponse;
import com.zbkj.crmeb.front.response.UserRechargeResponse;
import com.zbkj.crmeb.front.service.UserCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * 用户 -- 充值
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController("UserRechargeController")
@RequestMapping("api/front/recharge")
@Api(tags = "用户 -- 充值")
public class UserRechargeController {
    @Autowired
    private UserCenterService userCenterService;

    /**
     * 充值额度选择
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @ApiOperation(value = "充值额度选择")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<UserRechargeResponse> getRechargeConfig(){
        return CommonResult.success(userCenterService.getRechargeConfig());
    }

    /**
     * 充值
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @ApiOperation(value = "小程序充值")
    @RequestMapping(value = "/routine", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> routineRecharge(HttpServletRequest httpServletRequest, @RequestBody @Validated UserRechargeRequest request){
        request.setFromType(Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM);
        UserRechargePaymentResponse responseVo = userCenterService.recharge(request);
        request.setClientIp(CrmebUtil.getClientIp(httpServletRequest));

        Map<String, Object> map = new HashMap<>();
        map.put("data", responseVo);
        map.put("type", request.getFromType());
        return CommonResult.success(map);
    }

    /**
     * 充值
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @ApiOperation(value = "公众号充值")
    @RequestMapping(value = "/wechat", method = RequestMethod.POST)
    public CommonResult<UserRechargePaymentResponse> weChatRecharge(HttpServletRequest httpServletRequest, @RequestBody @Validated UserRechargeRequest request){
        request.setClientIp(CrmebUtil.getClientIp(httpServletRequest));
        return CommonResult.success(userCenterService.recharge(request));
    }

    /**
     * 佣金转入余额
     * @author Mr.Zhang
     * @since 2020-05-18
     */
    @ApiOperation(value = "余额转入")
    @RequestMapping(value = "/transferIn", method = RequestMethod.POST)
    public CommonResult<Boolean> transferIn(@RequestParam(name = "price") BigDecimal price){
        return CommonResult.success(userCenterService.transferIn(price));
    }
}



