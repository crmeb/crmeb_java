package com.zbkj.front.controller;

import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.combination.StoreCombination;
import com.zbkj.common.request.StorePinkRequest;
import com.zbkj.common.response.*;
import com.zbkj.service.service.StoreCombinationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 拼团商品
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Slf4j
@RestController
@RequestMapping("api/front/combination")
@Api(tags = "拼团商品")
public class CombinationController {

    @Autowired
    private StoreCombinationService storeCombinationService;

    /**
     * 拼团首页
     */
    @ApiOperation(value = "拼团首页数据")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<CombinationIndexResponse> index() {
        return CommonResult.success(storeCombinationService.getIndexInfo());
    }

    /**
     * 拼团商品列表header
     */
    @ApiOperation(value = "拼团商品列表header")
    @RequestMapping(value = "/header", method = RequestMethod.GET)
    public CommonResult<CombinationHeaderResponse> header() {
        return CommonResult.success(storeCombinationService.getHeader());
    }

    /**
     * 拼团商品列表
     */
    @ApiOperation(value = "拼团商品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreCombinationH5Response>> list(@ModelAttribute PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(storeCombinationService.getH5List(pageParamRequest)));
    }

    /**
     * 拼团商品详情
     */
    @ApiOperation(value = "拼团商品详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<CombinationDetailResponse> detail(@PathVariable(value = "id") Integer id) {
        CombinationDetailResponse h5Detail = storeCombinationService.getH5Detail(id);
        return CommonResult.success(h5Detail);
    }

    /**
     * 去拼团
     * @param pinkId 拼团团长单id
     */
    @ApiOperation(value = "去拼团")
    @RequestMapping(value = "/pink/{pinkId}", method = RequestMethod.GET)
    public CommonResult<GoPinkResponse> goPink(@PathVariable(value = "pinkId") Integer pinkId) {
        GoPinkResponse goPinkResponse = storeCombinationService.goPink(pinkId);
        return CommonResult.success(goPinkResponse);
    }

    /**
     * 更多拼团
     */
    @ApiOperation(value = "更多拼团")
    @RequestMapping(value = "/more", method = RequestMethod.GET)
    public CommonResult<PageInfo<StoreCombination>> getMore(@RequestParam Integer comId, @Validated PageParamRequest pageParamRequest) {
        PageInfo<StoreCombination> more = storeCombinationService.getMore(pageParamRequest, comId);
        return CommonResult.success(more);
    }

    /**
     * 取消拼团
     */
    @ApiOperation(value = "取消拼团")
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public CommonResult<Object> remove(@RequestBody @Validated StorePinkRequest storePinkRequest) {
        if (storeCombinationService.removePink(storePinkRequest)) {
            return CommonResult.success("取消成功");
        } else {
            return CommonResult.failed("取消失败");
        }
    }

}
