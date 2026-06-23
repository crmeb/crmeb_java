package com.zbkj.admin.controller;

import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.RetailShopRequest;
import com.zbkj.common.request.RetailShopSearchRequest;
import com.zbkj.common.request.RetailShopStairUserRequest;
import com.zbkj.common.response.SpreadOrderResponse;
import com.zbkj.common.response.SpreadUserResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.RetailShopService;
import com.zbkj.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 分销模块
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/store/retail")
@Api(tags = "分销")
public class RetailShopController {

    @Autowired
    private RetailShopService retailShopService;

    @Autowired
    private UserService userService;

    /**
     * 分销员列表
     * @param request 分销员分页列表查询请求对象
     */
    @PreAuthorize("hasAuthority('admin:retail:list')")
    @ApiOperation(value = "分销员列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<SpreadUserResponse>> getList(@ModelAttribute RetailShopSearchRequest request) {
        return CommonResult.success(CommonPage.restPage(retailShopService.getSpreadPeopleList(request)));
    }

    /**
     * 根据用户参数获取推广人列表
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return 查询结果推广人列表
     */
    @PreAuthorize("hasAuthority('admin:retail:spread:list')")
    @ApiOperation(value = "根据条件获取推广人列表")
    @RequestMapping(value = "/spread/userlist", method = RequestMethod.POST)
    public CommonResult<CommonPage<User>> getUserListBySpreadLevel(@RequestBody @Validated RetailShopStairUserRequest request,
                                                                   @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userService.getUserListBySpreadLevel(request, pageParamRequest)));
    }


    /**
     * 根据参数获取推广订单列表
     * @param request          查询参数
     * @param pageParamRequest 分页参数
     * @return 查询结果推广人订单列表
     */
    @PreAuthorize("hasAuthority('admin:retail:spread:order:list')")
    @ApiOperation(value = "根据条件获取推广人订单")
    @RequestMapping(value = "/spread/orderlist", method = RequestMethod.POST)
    public CommonResult<CommonPage<SpreadOrderResponse>> getOrdersBySpreadLevel(@RequestBody @Validated RetailShopStairUserRequest request,
                                                                                @ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(userService.getOrderListBySpreadLevel(request, pageParamRequest)));
    }

    /**
     * 清除上级推广人
     * @param id 当前被清理的用户id
     * @return 推广关系清理后的结果
     */
    @PreAuthorize("hasAuthority('admin:retail:spread:clean')")
    @ApiOperation(value = "清除上级推广人")
    @RequestMapping(value = "/spread/clean/{id}", method = RequestMethod.GET)
    public CommonResult<Object> clearSpread(@PathVariable Integer id) {
        return CommonResult.success(userService.clearSpread(id));
    }

    /**
     * 分销设置获取
     * @return 保存分销设置
     */
    @PreAuthorize("hasAuthority('admin:retail:spread:manage:get')")
    @ApiOperation(value = "分销配置信息获取")
    @RequestMapping(value = "/spread/manage/get", method = RequestMethod.GET)
    public CommonResult<Object> getSpreadInfo() {
        return CommonResult.success(retailShopService.getManageInfo());
    }


    @PreAuthorize("hasAuthority('admin:retail:spread:manage:set')")
    @ApiOperation(value = "分销管理信息保存")
    @RequestMapping(value = "/spread/manage/set", method = RequestMethod.POST)
    public CommonResult<Object> setSpreadInfo(@RequestBody @Validated RetailShopRequest retailShopRequest) {
        if (retailShopService.setManageInfo(retailShopRequest)) {
            return CommonResult.success("保存成功");
        }
        return CommonResult.failed("保存失败");
    }
}
