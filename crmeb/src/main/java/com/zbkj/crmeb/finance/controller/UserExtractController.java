package com.zbkj.crmeb.finance.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.finance.request.UserExtractRequest;
import com.zbkj.crmeb.finance.request.UserExtractSearchRequest;
import com.zbkj.crmeb.finance.response.BalanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.finance.service.UserExtractService;
import com.zbkj.crmeb.finance.model.UserExtract;


/**
 * 用户提现表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/finance/apply")
@Api(tags = "财务 -- 提现申请")

public class UserExtractController {

    @Autowired
    private UserExtractService userExtractService;

    /**
     * 分页显示用户提现表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-05-11
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserExtract>>  getList(@Validated UserExtractSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<UserExtract> userExtractCommonPage = CommonPage.restPage(userExtractService.getList(request, pageParamRequest));
        return CommonResult.success(userExtractCommonPage);
    }

    /**
     * 修改用户提现表
     * @param id integer id
     * @param userExtractRequest 修改参数
     * @author Mr.Zhang
     * @since 2020-05-11
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @Validated UserExtractRequest userExtractRequest){
        UserExtract userExtract = new UserExtract();
        BeanUtils.copyProperties(userExtractRequest, userExtract);
        userExtract.setId(id);

        if(userExtractService.updateById(userExtract)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 提现总金额
     * @author Mr.Zhang
     * @since 2020-05-11
     */
    @ApiOperation(value = "提现总金额")
    @RequestMapping(value = "/balance", method = RequestMethod.POST)
    public CommonResult<BalanceResponse> balance(){
        return CommonResult.success(userExtractService.getBalance(null));
    }

}



