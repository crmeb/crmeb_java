package com.zbkj.crmeb.front.controller;


import com.common.CommonPage;
import com.common.CommonResult;
import com.zbkj.crmeb.express.model.Express;
import com.zbkj.crmeb.express.service.ExpressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 物流公司 前端控制器
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
@RestController("ExpressFrontController")
@RequestMapping("api/front")
@Api(tags = "物流公司")
public class ExpressController {
    @Autowired
    private ExpressService expressService;

    /**
     * 物流公司列表
     * @author Mr.Zhang
     * @since 2020-06-01
     */
    @ApiOperation(value = "列表")
    @RequestMapping(value = "/logistics", method = RequestMethod.GET)
    public CommonResult<CommonPage<Express>> register(){
        return CommonResult.success(CommonPage.restPage(expressService.findAll("normal")));
    }
}



