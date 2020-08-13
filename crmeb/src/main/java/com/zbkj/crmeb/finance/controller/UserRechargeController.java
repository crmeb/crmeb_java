package com.zbkj.crmeb.finance.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.finance.request.UserRechargeRequest;
import com.zbkj.crmeb.finance.request.UserRechargeSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.finance.service.UserRechargeService;
import com.zbkj.crmeb.finance.model.UserRecharge;

import java.math.BigDecimal;
import java.util.HashMap;


/**
 * 用户充值表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/user/topUpLog")
@Api(tags = "财务 -- 充值")

public class UserRechargeController {

    @Autowired
    private UserRechargeService userRechargeService;

    /**
     * 分页显示用户充值表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-11
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserRecharge>>  getList(@Validated UserRechargeSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<UserRecharge> userRechargeCommonPage = CommonPage.restPage(userRechargeService.getList(request, pageParamRequest));
        return CommonResult.success(userRechargeCommonPage);
    }


    /**
     * 删除用户充值表
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-05-11
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        UserRecharge userRecharge = userRechargeService.getById(id);
        if(userRecharge == null){
            return CommonResult.failed("数据异常");
        }

        if(userRecharge.getPaid()){
            return CommonResult.failed("已支付的订单记录无法删除");
        }

        if(userRechargeService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 充值总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     */
    @ApiOperation(value = "提现总金额")
    @RequestMapping(value = "/balance", method = RequestMethod.POST)
    public CommonResult<HashMap<String, BigDecimal>> balance(){
        return CommonResult.success(userRechargeService.getBalanceList());
    }

}



