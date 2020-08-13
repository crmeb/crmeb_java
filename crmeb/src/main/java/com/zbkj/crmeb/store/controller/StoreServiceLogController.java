package com.zbkj.crmeb.store.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.request.StoreServiceLogSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.zbkj.crmeb.store.service.StoreServiceLogService;
import com.zbkj.crmeb.store.model.StoreServiceLog;


/**
 * 客服用户对话记录表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/store-service-log")
@Api(tags = "客服用户对话记录表") //配合swagger使用

public class StoreServiceLogController {

    @Autowired
    private StoreServiceLogService storeServiceLogService;

    /**
     * 分页显示客服用户对话记录表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreServiceLog>>  getList(@Validated StoreServiceLogSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreServiceLog> storeServiceLogCommonPage = CommonPage.restPage(storeServiceLogService.getList(request, pageParamRequest));
        return CommonResult.success(storeServiceLogCommonPage);
    }
}



