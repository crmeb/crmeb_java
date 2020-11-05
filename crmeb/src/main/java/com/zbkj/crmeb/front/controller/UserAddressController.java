package com.zbkj.crmeb.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.front.request.UserAddressDelRequest;
import com.zbkj.crmeb.front.request.UserAddressRequest;
import com.zbkj.crmeb.user.model.UserAddress;
import com.zbkj.crmeb.user.service.UserAddressService;
import com.zbkj.crmeb.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 用户地址 前端控制器
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
@RestController
@RequestMapping("api/front/address")
@Api(tags = "用户 -- 地址")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private UserService userService;


    /**
     * 分页显示用户地址
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserAddress>>  getList(PageParamRequest pageParamRequest){
//        CommonPage<UserAddress> userAddressCommonPage = CommonPage.restPage(userAddressService.getList(pageParamRequest));
        UserAddress userAddress = new UserAddress();
        userAddress.setUid(userService.getUserId());
        userAddress.setIsDel(false);
        CommonPage<UserAddress> userAddressCommonPage = CommonPage.restPage(userAddressService.getListByUserAddress(userAddress,pageParamRequest));
        return CommonResult.success(userAddressCommonPage);
    }

    /**
     * 新增用户地址
     * @param request 新增参数
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "保存")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public CommonResult<UserAddress> save(@RequestBody @Validated UserAddressRequest request){
        return CommonResult.success(userAddressService.create(request));
    }

    /**
     * 删除用户地址
     * @param request UserAddressDelRequest 参数
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestBody UserAddressDelRequest request){
        if(userAddressService.delete(request.getId())){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 详情
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "获取单个地址")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<UserAddress> info(@PathVariable("id") Integer id){
        UserAddress userAddress = userAddressService.getById(id);
        return CommonResult.success(userAddress);
    }

    /**
     * 获取默认地址
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "获取默认地址")
    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public CommonResult<UserAddress> getDefault(){
        return CommonResult.success(userAddressService.getDefault());

    }

    /**
     * 设置默认地址
     * @param request UserAddressDelRequest 参数
     * @author Mr.Zhang
     * @since 2020-04-28
     */
    @ApiOperation(value = "设置默认地址")
    @RequestMapping(value = "/default/set", method = RequestMethod.POST)
    public CommonResult<UserAddress> def(@RequestBody UserAddressDelRequest request){
        if(userAddressService.def(request.getId())){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }
}



