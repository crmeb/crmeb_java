package com.zbkj.crmeb.combination.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.zbkj.crmeb.combination.request.StoreCombinationRequest;
import com.zbkj.crmeb.combination.request.StoreCombinationSearchRequest;
import com.zbkj.crmeb.combination.request.StorePinkSearchRequest;
import com.zbkj.crmeb.combination.response.StoreCombinationResponse;
import com.zbkj.crmeb.combination.response.StorePinkAdminListResponse;
import com.zbkj.crmeb.combination.response.StorePinkDetailResponse;
import com.zbkj.crmeb.combination.service.StoreCombinationService;
import com.zbkj.crmeb.combination.service.StorePinkService;
import com.zbkj.crmeb.store.response.StoreProductResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 拼团商品表 前端控制器
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
@RequestMapping("api/admin/store/combination")
@Api(tags = "商品——拼团——商品") //配合swagger使用
public class StoreCombinationController {

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StorePinkService storePinkService;

    /**
     * 分页显示拼团商品表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author HZW
     * @since 2020-11-13
     * @return
     */
    @ApiOperation(value = "分页显示拼团商品表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreCombinationResponse>> getList(@Validated StoreCombinationSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StoreCombinationResponse> commonPage = CommonPage.restPage(storeCombinationService.getList(request, pageParamRequest));
        return CommonResult.success(commonPage);
    }

    /**
     * 新增拼团商品表
     * @param request 新增参数
     * @author HZW
     * @since 2020-11-13
     */
    @ApiOperation(value = "新增拼团商品")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreCombinationRequest request){
        if(storeCombinationService.saveCombination(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除拼团商品表
     * @param id Integer
     * @author HZW
     * @since 2020-11-13
     */
    @ApiOperation(value = "删除拼团商品")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(storeCombinationService.deleteById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改拼团商品表
     * @param id integer id
     * @param storeCombinationRequest 修改参数
     * @author HZW
     * @since 2020-11-13
     */
    @ApiOperation(value = "修改拼团商品")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated StoreCombinationRequest storeCombinationRequest){
        storeCombinationRequest.setId(id);
        if(storeCombinationService.updateCombination(storeCombinationRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询拼团商品信息
     * @param id Integer
     * @author HZW
     * @since 2020-11-13
     * @return
     */
    @ApiOperation(value = "拼团商品详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreProductResponse> info(@RequestParam(value = "id") Integer id){
        StoreProductResponse detail = storeCombinationService.getAdminDetail(id);
        return CommonResult.success(detail);
   }

    /**
     * 修改拼团商品状态
     * @return
     */
    @ApiOperation(value = "修改拼团商品状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    public CommonResult<Object> updateStatus(@RequestParam(value = "id") Integer id, @RequestParam @Validated boolean isShow){
        if(storeCombinationService.updateCombinationShow(id, isShow)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 拼团统计
     * @return
     */
    @ApiOperation(value = "拼团统计")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> statistics() {
        Map<String, Object> map = storeCombinationService.getAdminStatistics();
        return CommonResult.success(map);
    }

    /**
     * 拼团列表
     * @return
     */
    @ApiOperation(value = "拼团列表")
    @RequestMapping(value = "/combine/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StorePinkAdminListResponse>> getCombineList(@Validated StorePinkSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<StorePinkAdminListResponse> responseCommonPage = CommonPage.restPage(storePinkService.getList(request, pageParamRequest));
        return CommonResult.success(responseCommonPage);
    }

    /**
     * 拼团订单列表
     * @return
     */
    @ApiOperation(value = "拼团订单列表")
    @RequestMapping(value = "/order_pink/{id}", method = RequestMethod.GET)
    public CommonResult<List<StorePinkDetailResponse>> getPinkList(@PathVariable(value = "id") Integer id) {
        return CommonResult.success(storePinkService.getAdminList(id));
    }
}



