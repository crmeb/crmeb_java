package com.zbkj.crmeb.front.controller;


import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.store.model.StoreService;
import com.zbkj.crmeb.store.model.StoreServiceLog;
import com.zbkj.crmeb.store.request.StoreServiceLogSearchRequest;
import com.zbkj.crmeb.store.service.StoreServiceLogService;
import com.zbkj.crmeb.store.service.StoreServiceService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户 -- 用户中心
 */
@Slf4j
@RestController("StoreServiceController")
@RequestMapping("api/front/user/service")
@Api(tags = "客服")
public class StoreServiceController {
    @Autowired
    private StoreServiceService storeServiceService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreServiceLogService storeServiceLogService;

    /**
     * 客服列表
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    @ApiOperation(value = "客服列表")
    @RequestMapping(value = "/lst", method = RequestMethod.GET)
    public CommonResult<List<StoreService>> getList(){
        return CommonResult.success(storeServiceService.getList(new PageParamRequest()));
    }

    /**
     * 聊天记录
     * @author Mr.Zhang
     * @since 2020-06-10
     */
    @ApiOperation(value = "聊天记录")
    @RequestMapping(value = "/record/{toUid}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "toUid", value = "聊天人编号", dataType = "intteger")
    public CommonResult<CommonPage<StoreServiceLog>> getList(@PathVariable Integer toUid, @Validated PageParamRequest pageParamRequest){
        StoreServiceLogSearchRequest request = new StoreServiceLogSearchRequest();
        request.setUid(userService.getUserIdException());
        request.setToUid(toUid);
        return CommonResult.success(CommonPage.restPage(storeServiceLogService.getList(request, pageParamRequest)));
    }
}



