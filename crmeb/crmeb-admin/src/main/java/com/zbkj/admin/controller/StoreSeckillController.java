package com.zbkj.admin.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.StoreSeckillAddRequest;
import com.zbkj.common.request.StoreSeckillSearchRequest;
import com.zbkj.common.response.StoreSeckillResponse;
import com.zbkj.common.response.StoreProductInfoResponse;
import com.zbkj.service.service.StoreSeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商品秒杀产品表 前端控制器
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
@RequestMapping("api/admin/store/seckill")
@Api(tags = "商品 -- 秒杀 -- 商品") //配合swagger使用
public class StoreSeckillController {

    @Autowired
    private StoreSeckillService storeSeckillService;

    /**
     * 分页显示商品秒杀产品表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:seckill:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreSeckillResponse>>  getList(@Validated StoreSeckillSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<StoreSeckillResponse> storeSeckillCommonPage =
                CommonPage.restPage(storeSeckillService.getList(request, pageParamRequest));
        return CommonResult.success(storeSeckillCommonPage);
    }

    /**
     * 新增商品秒杀产品表
     * @param storeSeckillRequest 新增参数
     */
    @PreAuthorize("hasAuthority('admin:seckill:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreSeckillAddRequest storeSeckillRequest) {
        if (storeSeckillService.saveSeckill(storeSeckillRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品秒杀产品表
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:seckill:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if (storeSeckillService.deleteById(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改商品秒杀产品表
     * @param storeSeckillRequest 修改参数
     */
    @PreAuthorize("hasAuthority('admin:seckill:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated StoreSeckillAddRequest storeSeckillRequest) {
        if (storeSeckillService.updateSeckill(storeSeckillRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    @PreAuthorize("hasAuthority('admin:seckill:update:status')")
    @ApiOperation(value = "修改秒杀商品状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    public CommonResult<String> updateStatus(@RequestParam @Validated Integer id, @RequestParam @Validated boolean status) {
        if (storeSeckillService.updateSecKillStatus(id,status)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 查询商品秒杀产品表信息
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:seckill:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreProductInfoResponse> info(@RequestParam(value = "id") Integer id) {
        StoreProductInfoResponse storeSeckill = storeSeckillService.getDetailAdmin(id);
        return CommonResult.success(storeSeckill);
   }
}



