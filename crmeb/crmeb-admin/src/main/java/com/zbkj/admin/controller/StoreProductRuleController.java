package com.zbkj.admin.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.model.product.StoreProductRule;
import com.zbkj.common.request.StoreProductRuleRequest;
import com.zbkj.common.request.StoreProductRuleSearchRequest;
import com.zbkj.service.service.StoreProductRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;


/**
 * 商品规则值(规格)表 前端控制器
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
@RequestMapping("api/admin/store/product/rule")
@Api(tags = "商品 -- 规则值(规格)") //配合swagger使用
public class StoreProductRuleController {

    @Autowired
    private StoreProductRuleService storeProductRuleService;

    /**
     * 分页显示商品规则值(规格)表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @PreAuthorize("hasAuthority('admin:product:rule:list')")
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductRule>>  getList(
            @Validated StoreProductRuleSearchRequest request, @Validated PageParamRequest pageParamRequest) {
        CommonPage<StoreProductRule> storeProductRuleCommonPage =
                CommonPage.restPage(storeProductRuleService.getList(request, pageParamRequest));
        return CommonResult.success(storeProductRuleCommonPage);
    }

    /**
     * 新增商品规则值(规格)表
     * @param storeProductRuleRequest 新增参数
     */
    @PreAuthorize("hasAuthority('admin:product:rule:save')")
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreProductRuleRequest storeProductRuleRequest) {
        if (storeProductRuleService.save(storeProductRuleRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品规则值(规格)表
     * @param ids Integer
     */
    @PreAuthorize("hasAuthority('admin:product:rule:delete')")
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{ids}", method = RequestMethod.GET)
    public CommonResult<String> delete(@PathVariable String ids) {
        if (storeProductRuleService.removeByIds(CrmebUtil.stringToArray(ids))) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改商品规则值(规格)表
     * @param storeProductRuleRequest 修改参数
     */
    @PreAuthorize("hasAuthority('admin:product:rule:update')")
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated StoreProductRuleRequest storeProductRuleRequest) {
        if (storeProductRuleService.updateRule(storeProductRuleRequest)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 查询商品规则值(规格)表信息
     * @param id Integer
     */
    @PreAuthorize("hasAuthority('admin:product:rule:info')")
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<StoreProductRule> info(@PathVariable Integer id) {
        StoreProductRule storeProductRule = storeProductRuleService.getById(id);
        return CommonResult.success(storeProductRule);
   }
}



