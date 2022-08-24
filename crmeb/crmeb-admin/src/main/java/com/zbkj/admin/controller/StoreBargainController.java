package com.zbkj.admin.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.StoreBargainRequest;
import com.zbkj.common.request.StoreBargainSearchRequest;
import com.zbkj.common.request.StoreBargainUserSearchRequest;
import com.zbkj.common.response.StoreBargainResponse;
import com.zbkj.common.response.StoreBargainUserHelpResponse;
import com.zbkj.common.response.StoreBargainUserResponse;
import com.zbkj.common.response.StoreProductInfoResponse;
import com.zbkj.service.service.StoreBargainService;
import com.zbkj.service.service.StoreBargainUserHelpService;
import com.zbkj.service.service.StoreBargainUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 砍价表 前端控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/admin/store/bargain")
@Api(tags = "商品 -- 砍价 -- 商品") //配合swagger使用
public class StoreBargainController {

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private StoreBargainUserService storeBargainUserService;

    @Autowired
    private StoreBargainUserHelpService storeBargainUserHelpService;

    /**
     * 分页显示砍价商品列表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:bargain:list')")
    @ApiOperation(value = "分页显示砍价商品列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreBargainResponse>>  getList(@Validated StoreBargainSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreBargainResponse> storeBargainCommonPage = CommonPage.restPage(storeBargainService.getList(request, pageParamRequest));
        return CommonResult.success(storeBargainCommonPage);
    }

    /**
     * 新增砍价商品
     * @param storeBargainRequest 新增参数
     */
    @PreAuthorize("hasAuthority('admin:bargain:save')")
    @ApiOperation(value = "新增砍价商品")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreBargainRequest storeBargainRequest){
        if(storeBargainService.saveBargain(storeBargainRequest)){
            return CommonResult.success("添加砍价商品成功");
        }else{
            return CommonResult.failed("添加砍价商品失败");
        }
    }

    /**
     * 删除砍价商品
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:bargain:delete')")
    @ApiOperation(value = "删除砍价商品")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeBargainService.deleteById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改砍价商品
     * @param storeBargainRequest 修改参数
     */
    @PreAuthorize("hasAuthority('admin:bargain:update')")
    @ApiOperation(value = "修改砍价商品")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated StoreBargainRequest storeBargainRequest){
        if (storeBargainService.updateBargain(storeBargainRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 查询砍价商品详情
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:bargain:info')")
    @ApiOperation(value = "查询砍价商品详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreProductInfoResponse> info(@RequestParam(value = "id") Integer id){
        StoreProductInfoResponse storeBargainResponse = storeBargainService.getAdminDetail(id);
        return CommonResult.success(storeBargainResponse);
   }

   /**
    *
    * 修改砍价商品状态
    * @param id 商品id
    * @param status 商品状态
    * @return {@link CommonResult<String>}
    */
   @PreAuthorize("hasAuthority('admin:bargain:update:status')")
    @ApiOperation(value = "修改砍价商品状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    public CommonResult<String> updateStatus(@RequestParam @Validated Integer id, @RequestParam @Validated boolean status){
        if(storeBargainService.updateBargainStatus(id,status)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 分页显示砍价列表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:bargain:user:list')")
    @ApiOperation(value = "分页显示砍价列表") //配合swagger使用
    @RequestMapping(value = "/bargain_list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreBargainUserResponse>> getBargainUserList(@Validated StoreBargainUserSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreBargainUserResponse> bargainUserCommonPage = CommonPage.restPage(storeBargainUserService.getList(request, pageParamRequest));
        return CommonResult.success(bargainUserCommonPage);
    }

    /**
     * 获取砍价参与详情列表
     * @param id StoreBargainUser 砍价参与用户编号
     */
    @PreAuthorize("hasAuthority('admin:bargain:user:help:list')")
    @ApiOperation(value = "获取砍价参与详情列表") //配合swagger使用
    @RequestMapping(value = "/bargain_list/{id}", method = RequestMethod.GET)
    public CommonResult<List<StoreBargainUserHelpResponse>> getBargainUserHelpDetail(@PathVariable(value = "id") Integer id){
        List<StoreBargainUserHelpResponse> list = storeBargainUserHelpService.getList(id);
        return CommonResult.success(list);
    }
}



